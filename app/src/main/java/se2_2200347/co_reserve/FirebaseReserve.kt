package se2_2200347.co_reserve

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.concurrent.fixedRateTimer
import kotlin.coroutines.coroutineContext

//firebaseの接続用のリファランスの宣言
private val testbase = FirebaseDatabase.getInstance().getReference()

//スナップショットをいろいろなところで用いるために事前に宣言
private lateinit var snaped : DataSnapshot
private lateinit var postListener : ValueEventListener

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
    fun makecheck(res : reserve){
        testbase.child("booking").push().setValue(res)
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

    //新規予約を登録するために用いるメソッド
    fun reg(st : Int , ed : Int , date : String , room : String , ID : String){
        //st : 新規開始時間   ed : 新規終了時間     rst : 既存開始時間    red : 既存終了時間
        var rst = 0
        var red = 0
        //データ追加可能かどうかの判定を行うフラグ
        var tag = true
        val stl = arrayListOf<Int>()
        val edl = arrayListOf<Int>()
        val datel = arrayListOf<String>()
        val rooml = arrayListOf<String>()

        //めんどくさくなったので配列にぶち込みました
        for(i in snaped.children){
            //日時、開始終了が一致する場合、配列に登録しない
            if(i.child("book_start").value.toString() != rst.toString()
                    &&  i.child("book_end").value.toString() != red.toString()) {
                stl.add(i.child("book_start").value.toString().toInt())
                edl.add(i.child("book_end").value.toString().toInt())
                datel.add(i.child("date").value.toString())
                rooml.add(i.child("room").value.toString())
            }
        }

        val regs = reserve(date , st.toString() , ed.toString() , room , ID)

        //スナップショット内のデータを参照し、範囲内に予約がある場合にはエラーを返す
        for (i in 0 .. stl.size - 1) {
            //部屋番号と日付の日付が同じ予約のみを判定する

            if (stl[i] < st && st < edl[i] || //既存の予約の開始時間が希望予約時間の間にある場合
                    stl[i] < ed && ed < edl[i] || //既存の予約終了時間が予約希望時間の間にある場合
                    st <= stl[i] && edl[i] <= ed) {  //既存の予約時間の間に予約希望時間が含まれている場合
                tag = false
                break
            }

        }
//予約にかぶりが発生しなかった場合に登録処理を行う
        if (tag){makecheck(regs)}


    }

    //予約の変更を行うために行うメソッド
    fun reg(st : Int , ed : Int , date : String , room : String , ID : String , olddate : String? , oldroom : String?){
        //st : 新規開始時間   ed : 新規終了時間     rst : 既存開始時間    red : 既存終了時間
        //初回宣言時は変更する予定の予約を入力する
        var rst = 0
        var red = 0
        //データ追加可能かどうかの判定を行うフラグ
        var tag = true
        val stl = arrayListOf<Int>(0)
        val edl = arrayListOf<Int>(0)
        val datel = arrayListOf<String>("")
        val rooml = arrayListOf<String>("")
        var key = ""

        //めんどくさくなったので配列にぶち込みました
        for(i in snaped.children){
            //日時、開始終了が一致する場合、配列に登録しない
            if(i.child("book_start").value.toString() != rst.toString()
                    &&  i.child("book_end").value.toString() != red.toString()
                    && i.child("date").value.toString() != olddate
                    && i.child("room").value.toString() != oldroom) {
                stl.add(i.child("book_start").value.toString().toInt())
                edl.add(i.child("book_end").value.toString().toInt())
                datel.add(i.child("date").value.toString())
                rooml.add(i.child("room").value.toString())
            }else{
                //全ての日時を配列に登録する
                stl[0] = i.child("book_start").value.toString().toInt()
                edl[0] = i.child("book_end").value.toString().toInt()
                datel[0] = i.child("date").value.toString()
                rooml[0] = i.child("room").value.toString()
                key = i.child("room").key.toString()
            }
        }

        val regs = reserve(date , st.toString() , ed.toString() , room , ID)

        //スナップショット内のデータを参照し、範囲内に予約がある場合にはエラーを返す
        for (i in 0 .. stl.size - 1) {
            //部屋番号と日付の日付が同じ予約のみを判定する

            if (stl[i] < st && st < edl[i] || //既存の予約の開始時間が希望予約時間の間にある場合
                    stl[i] < ed && ed < edl[i] || //既存の予約終了時間が予約希望時間の間にある場合
                    st <= stl[i] && edl[i] <= ed) {  //既存の予約時間の間に予約希望時間が含まれている場合
                tag = false
                break
            }

        }
//予約にかぶりが発生しなかった場合に登録処理を行う
        if (tag){
            makecheck(regs)
            //変更前の予約を削除する
            testbase.child("booking").child(key).removeValue()

        }

    }

    fun delete(st : Int , date : String , room : String , ID : String){
        //削除するデータのキー値を格納する変数
        var key = ""
        //キーを取得するために要素が合致するものを検索する
        for(i in snaped.children){
            if(i.child("book_start").value.toString().toInt() == st
                    && i.child("room").value.toString() == room
                    && i.child("ID").value.toString() == ID
                    && i.child("date").value.toString() == date){

                key = i.key.toString()
            }
        }
        testbase.child("booking").child(key).removeValue()


    }

}