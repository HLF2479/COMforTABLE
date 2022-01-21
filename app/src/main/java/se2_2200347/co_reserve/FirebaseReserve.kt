package se2_2200347.co_reserve

import com.google.firebase.database.FirebaseDatabase

//firebaseの接続用のリファランスの宣言
private val database = FirebaseDatabase.getInstance()
private val bookRef = database.getReference("booking")
private val userRef = database.getReference("users")
private val lockRef = database.getReference("lock")
private val logRef = database.getReference("log")

private val mySnap = MySnap.getInstance()

class FirebaseReserve {

    /**
     * 設定したデータをbookingテーブル配下に挿入するメソッド
     * 子要素 a の配下に生成した一意のkey名をvalueに取得する
     */
    private fun makeCheck(data: Map<String, String>) {
        bookRef.push().setValue(data)
    }

    /**
     * 設定したデータをbookingテーブル配下に挿入するメソッド
     * key名は既にあるものを参照する
     */
    private fun makeUpdate(data: Map<String, String>, key: String) {
        bookRef.child(key).setValue(data)
    }

    /**
     * 利用終了したデータををlogテーブル配下に挿入するメソッド
     * 子要素 a の配下に生成した一意のkey名をvalueに取得する
     */
    private fun makeLog(data: Map<String, String>) {
        logRef.push().setValue(data)
    }

    /**
     * 新規予約を登録するために用いるメソッド
     * param st 新規開始時間
     * param ed 新規終了時間
     * param date 新規予約日付
     * param room 新規予約部屋番号
     * param ID 学籍番号
     * param newCount 新規予約件数
     */
    fun reg (st : Int, ed : Int, date : String, room : String, ID : String, newCount: Int) : Boolean {
        //データ追加可能かどうかの判定を行うフラグ
        var tag = true
        //既存予約の情報格納用配列
        var stl = arrayListOf<Int>()        //開始時間
        var edl = arrayListOf<Int>()        //終了時間
        var datel = arrayListOf<String>()   //日付
        var rooml = arrayListOf<String>()   //部屋番号

        val snapshot = mySnap.bookSnapshot
        //比較用の予約情報を配列に格納
        for (i in snapshot.children) {
            val iDate = i.child("date").value.toString()    //日付
            val iRoom = i.child("room").value.toString()    //部屋番号
            //日付と部屋番号が一致する予約情報を配列に登録
            if(iDate == date && iRoom == room) {
                stl.add(i.child("book_start").value.toString().toInt()) //開始時間
                edl.add(i.child("book_end").value.toString().toInt())   //終了時間
                datel.add(iDate)
                rooml.add(iRoom)
            }
        }

        //送信用データ配列
        val regs = mapOf(
                "date" to date,
                "book_start" to "%04d".format(st),
                "book_end" to "%04d".format(ed),
                "room" to room,
                "user_id" to ID
        )

        //スナップショット内のデータを参照し、範囲内に予約がある場合にはエラーを返す
        for (i in stl.indices) {
            //部屋番号と日付の日付が同じ予約のみを判定する
            if (stl[i] < st && st < edl[i] || //既存の予約の開始時間が希望予約時間の間にある場合
                    stl[i] < ed && ed < edl[i] || //既存の予約終了時間が予約希望時間の間にある場合
                    st <= stl[i] && edl[i] <= ed) {  //既存の予約時間の間に予約希望時間が含まれている場合
                tag = false
                break
            }
        }

        //予約にかぶりが発生しなかった場合に登録処理を行う
        if (tag) {
            makeCheck(regs)
            setCount(ID, newCount)
            return true
        }
        return false
    }

    /**
     * 予約の変更処理を行うメソッド
     * param st 新規開始時間
     * param ed 新規終了時間
     * param date 新規登録日付
     * param room 新規部屋番号
     * param ID ユーザー番号
     * param oldDate 旧予約日付
     * param oldStart 旧予約開始時間
     * param oldRoom 旧部屋番号
     */
    fun upd (st : Int, ed : Int, date : String, room : String, ID : String, oldDate : String?, oldStart : String?, oldRoom : String?) : Boolean {
        //比較対象になる予約情報を格納する配列
        val stl = arrayListOf<Int>()        //開始時間
        val edl = arrayListOf<Int>()        //終了時間
        val datel = arrayListOf<String>()   //日付
        val rooml = arrayListOf<String>()   //部屋番号

        var key = ""    //更新時に削除する予約のキー値を格納する変数

        val snapshot = mySnap.bookSnapshot
        //比較する予約情報を配列に格納
        for (i in snapshot.children) {
            val iDate = i.child("date").value.toString()        //日付
            var iStart = i.child("book_start").value.toString() //開始時間
            if (iStart.length == 3) iStart = "0$iStart"               //9時台の時に"0"を追加
            val iRoom = i.child("room").value.toString()        //部屋番号
            if (iDate == oldDate && iStart == oldStart && iRoom == oldRoom) {
                //日時、開始終了が旧予約と一致する場合、配列に登録せずにキー値だけ変数に保存
                key = i.key.toString()
            } else if (iDate == date && iRoom == room) {
                //日付と部屋番号が一致する予約情報を配列に登録
                stl.add(iStart.toInt())
                edl.add(i.child("book_end").value.toString().toInt())   //終了時間
                datel.add(iDate)
                rooml.add(iRoom)
            }
        }

        //送信用データ配列
        val regs = mapOf (
                "date" to date,
                "book_start" to "%04d".format(st),
                "book_end" to "%04d".format(ed),
                "room" to room,
                "user_id" to ID
        )

        //スナップショット内のデータを参照し、範囲内に予約がある場合にはエラーを返す
        var tag = true  //データ追加可能かどうかの判定を行うフラグ
        for (i in 0 until stl.size) {
            //部屋番号と日付の日付が同じ予約のみを判定する
            if (stl[i] < st && st < edl[i] || //既存の予約の開始時間が希望予約時間の間にある場合
                    stl[i] < ed && ed < edl[i] || //既存の予約終了時間が予約希望時間の間にある場合
                    st <= stl[i] && edl[i] <= ed) {  //既存の予約時間の間に予約希望時間が含まれている場合
                tag = false
                break
            }
        }

        //予約にかぶりが発生しなかった場合に更新処理を行う
        if (tag) {
            makeUpdate(regs, key)
            return true
        }
        return false
    }

    /**
     * 予約情報を取り消すメソッド
     * param date 日付
     * param st 予約開始時間
     */
    fun cancel(date : Long, st : Long, room : Long){
        val snapshot = mySnap.myBooking

        var key : String    //削除したい予約情報のキー値を格納する変数
        for (i in snapshot.children) {
            val keyDate = i.child("date").value.toString().toLong()         //日付
            val keyStart = i.child("book_start").value.toString().toLong()  //開始時間
            val keyRoom = i.child("room").value.toString().toLong()         //部屋番号
            if (keyDate == date && keyStart == st && keyRoom == room) {    //上記の変数３つに当てはまるか判定
                key = i.key.toString()          //予約番号キーを取得
                delete(key)
            }
        }
    }

    /**
     * 予約情報を削除し、対応するユーザーの予約件数を１減らすメソッド
     * param k 予約情報のキー値
     * param id String ユーザーID
     * param count Int 削除後にユーザーがもつ予約件数
     */
    fun delete(k: String) {
        bookRef.child(k).removeValue()
        val snapshot = mySnap.userSnapshot
        val id = snapshot.key.toString()
        val count = "${snapshot.child("counter").value}".toInt() - 1
        setCount(id, count)
    }

    /**
     * 予約情報をログtableに移動させるメソッド
     * param key 対象となる予約情報のキー値
     */
    fun sendLog(key: String) {
        val snapshot = mySnap.myBooking.child(key)
        //送信用データ配列
        val reg = mapOf(
                "date" to snapshot.child("date").value.toString(),
                "book_start" to snapshot.child("book_start").value.toString(),
                "book_end" to snapshot.child("book_end").value.toString(),
                "room" to snapshot.child("room").value.toString(),
                "user_id" to snapshot.child("user_id").value.toString()
        )
        makeLog(reg)
        delete(key)
    }

    /**
     * 現在時間が予約情報と一致しているか判定するメソッド
     * param room 部屋番号
     * param endKey 予約終了時間
     */
    fun getEnabled(room: Int, endKey: Int) : Boolean {
        val snapshot = mySnap.lockSnapshot
        val endTime = snapshot.child("$room/end").value.toString().toInt()  //部屋のロックに保存されている終了時間
        if (endTime == endKey) return true
        return false
    }

    /**
     * 予約したユーザーの予約件数を変化させるメソッド
     * param ID ユーザー番号
     * param COUNT 予約件数
     */
    fun setCount(ID: String, COUNT: Int) {
        userRef.child(ID).child("counter").setValue(COUNT)
    }

    /**
     * 一時利用。"end"の情報を-9に戻すメソッド
     * param room 部屋番号
     */
    fun setNine(room: Int) {
        lockRef.child("$room").child("end").setValue("-9")
    }
}