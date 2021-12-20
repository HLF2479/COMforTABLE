package se2_2200347.co_reserve

import android.content.ContentValues
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//firebaseの接続用のリファランスの宣言
private val database = FirebaseDatabase.getInstance()
private val testbase = database.getReference()
private val bookRef = database.getReference("booking")
private val userRef = database.getReference("users")

//スナップショットをいろいろなところで用いるために事前に宣言
private lateinit var snaped : DataSnapshot
private lateinit var postListener : ValueEventListener

private val mySnap = MySnap.getInstance()

class FirebaseReserve {

    /**  テーブルの細かいデータをまとめて入れるデータクラスfile
    date : String 日付
    st : String 予約開始時間
    ed : String 予約終了時間
    room : String 部屋番号
    ID : String学籍番号
     */
    data class reserve(val date : String? = null , val st : String? = null , val ed : String? = null ,
                                val room : String? = null , val ID : String? =null ){
        //Null default values create a no-argument default constructor , which is needed
        //for deserialization from a DataSnapshot
    }

    /**
     * データクラスreserveで設定したデータをbookingテーブル配下に挿入するメソッド
     * 子要素 a の配下に生成した一意のkey名をvalueに取得する
     */
    private fun makecheck(res : reserve){
        testbase.child("booking").push().setValue(res)
    }
    private fun makeCheck(data: Map<String, String>) {
        bookRef.push().setValue(data)
    }
    private fun makeUpdate(data: Map<String, String>, key: String) {
        bookRef.child(key).setValue(data)
    }


    //ページを表示時にデータベースの監視を開始するメソッド
    fun download() {
        postListener = object : ValueEventListener {

            //データが更新されるたびに細かく呼び出されるところ？
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //データが更新されるたびにスナップショットを更新保存する
                snaped = dataSnapshot
            }

            //データ更新されたときのリスナーがエラー吐いた場合に行われる処理
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        //常時監視する領域の指定（Rootに近くなるほど頻繁に更新されるので重くなる)
        //似たような箇所を複数監視されると重たいので、リセットしてから宣言のし直し
        testbase.child("booking").orderByChild("book_start").removeEventListener(postListener)
        testbase.child("booking").orderByChild("book_start").addValueEventListener(postListener)
    }

    //データベースの監視を終了するメソッド
    fun watchend(){
        testbase.child("booking").orderByChild("book_start").removeEventListener(postListener)
    }

    /**
     * 新規予約を登録するために用いるメソッド
     * param st 新規開始時間
     * param ed 新規終了時間
     * param date 新規予約日付
     * param room 新規予約部屋番号
     * param ID 学籍番号
     * param newCount 新規予約件数
     * param rst 既存開始時間
     * param red 既存終了時間
     */
    fun reg (st : Int, ed : Int, date : String, room : String, ID : String, newCount: Int) : Boolean {
        var rst = 0
        var red = 0
        //データ追加可能かどうかの判定を行うフラグ
        var tag = true
        //既存予約の情報格納用配列
        var stl = arrayListOf<Int>()
        var edl = arrayListOf<Int>()
        var datel = arrayListOf<String>()
        var rooml = arrayListOf<String>()

        val snapshot = mySnap.bookSnapshot
        for (i in snapshot.children) {
            //日付が一致する予約情報を配列に登録
            if(i.child("date").value.toString() == date
                    && i.child("book_start").value.toString() != rst.toString()
                    && i.child("book_end").value.toString() != red.toString()) {
                stl.add(i.child("book_start").value.toString().toInt())
                edl.add(i.child("book_end").value.toString().toInt())
                datel.add(i.child("date").value.toString())
                rooml.add(i.child("room").value.toString())
            }
        }

        //送信用データ配列
        val regs = mapOf(
                "date" to date,
                "book_start" to st.toString(),
                "book_end" to ed.toString(),
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
//        bookRef.orderByChild("book_start").addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                //めんどくさくなったので配列にぶち込みました
//                for(i in snapshot.children){
//                    //日時、開始終了が一致する場合、配列に登録しない
//                      cut
//                }
//
////                val regs = reserve(date , st.toString() , ed.toString() , room , ID)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("ERROR", "error")
//            }
//        })
        //cut
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
    fun reg (st : Int, ed : Int, date : String, room : String, ID : String, oldDate : String?, oldStart : String?, oldRoom : String?) : Boolean {
        //データ追加可能かどうかの判定を行うフラグ
        var tag = true
        //比較対象になる予約情報を格納する配列
        val stl = arrayListOf<Int>()
        val edl = arrayListOf<Int>()
        val datel = arrayListOf<String>()
        val rooml = arrayListOf<String>()
        //更新時に削除する予約のキー値を格納する変数
        var key = ""

        val snapshot = mySnap.bookSnapshot
        //めんどくさくなったので配列にぶち込みました
        for (i in snapshot.children) {
            val iDate = i.child("date").value.toString()
            val iStart = i.child("book_start").value.toString()
            val iRoom = i.child("room").value.toString()
            if (iDate == oldDate && iStart == oldStart && iRoom == oldRoom) {
                //日時、開始終了が旧予約と一致する場合、配列に登録せずにキー値だけ変数に保存
                key = i.key.toString()
            } else if (iDate == date) {
                //日付が一致する予約情報を配列に登録
                stl.add(i.child("book_start").value.toString().toInt())
                edl.add(i.child("book_end").value.toString().toInt())
                datel.add(i.child("date").value.toString())
                rooml.add(i.child("room").value.toString())
            }
        }

//        val regs = reserve(date , st.toString() , ed.toString() , room , ID)
        val regs = mapOf (
                "date" to date,
                "book_start" to st.toString(),
                "book_end" to ed.toString(),
                "room" to room,
                "user_id" to ID
        )

        //スナップショット内のデータを参照し、範囲内に予約がある場合にはエラーを返す
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
    fun delete(date : Long, st : Long, room : Long, ID: String, count : Int){
        val snapshot = mySnap.myBooking

        var key : String    //削除したい予約情報のキー値を格納する変数
        for (i in snapshot.children) {
            val keyDate = i.child("date").value.toString().toLong()         //日付
            val keyStart = i.child("book_start").value.toString().toLong()  //開始時間
            val keyRoom = i.child("room").value.toString().toLong()         //部屋番号
            if (keyDate == date && keyStart == st && keyRoom == room) {    //上記の変数３つに当てはまるか判定
                key = i.key.toString()          //予約番号キーを取得
                val newCount = count - 1        //新しい予約件数
                bookRef.child(key).removeValue()//取得したキーの予約情報を削除
                setCount(ID, newCount)          //新しい予約件数をfirebaseに登録
            }
        }
    }

    /**
     * 現在時間が予約情報と一致しているか判定するメソッド
     * param room 部屋番号
     * param endKey 予約終了時間
     */
    fun getEnabled(room: Int, endKey: Int) : Boolean {
        val snapshot = mySnap.lockSnapshot
        val endTime = snapshot.child("$room/end").value.toString().toInt()
        if (endTime == endKey) return true
        return false
    }

    fun setCount(ID: String, COUNT: Int) {
        userRef.child(ID).child("counter").setValue(COUNT)
    }
}