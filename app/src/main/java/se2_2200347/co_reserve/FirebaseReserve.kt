package se2_2200347.co_reserve

import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//firebaseの接続用のリファランスの宣言
private val testbase = FirebaseDatabase.getInstance().getReference()

//スナップショットをいろいろなところで用いるために事前に宣言
private lateinit var snaped : DataSnapshot

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


    //データが更新されるたびに起動するメソッド系列（更新）
    private fun downloaded(date : String , room : String ) {
        //ここの部分でデータの取得を行っている

        //既存の予約の開始時間と終了時間を入れておくための変数を宣言
        var stkey = ""
        var edkey = ""

        //予約変更する前の開始時間と終了時間をいったんこちらで宣言
        var oldst = "".toString().toInt()
        var olded = "".toString().toInt()

        val postListener = object : ValueEventListener {

            //データが更新されるたびに細かく呼び出されるところ？
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                // val post = dataSnapshot.child("username").getValue()
                //データが更新されるたびにスナップショットを更新保存する
                snaped = dataSnapshot

                var n = 0
                var rst = 0
                var red = 0
                for (e in snaped.children) {
                    if (n % 2 == 0) {
                        rst = e.value.toString().toInt()
                        stkey = e.key.toString()
                    } else {
                        red = e.value.toString().toInt()
                        edkey = e.key.toString()
                    }
                    if (rst == oldst && red == olded && n % 2 == 1) {
                        break
                    }
                    n++
                }

            }

            //データ更新されたときのリスナーがエラー吐いた場合に行われる処理
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
    }

}