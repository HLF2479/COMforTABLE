package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_quit.*

private val mySnap = MySnap.getInstance()
private val firebase = FirebaseReserve()

class Quit : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quit)

        title = getText(R.string.quit_t)

        val es = getSharedPreferences("ES", MODE_PRIVATE)

        //内部ストレージに保存してあるキー値を取得して、ログtableに出力する
        val key = es.getString("KEY", "")!!
        firebase.sendLog(key)

        //ユーザーtableの予約件数を１つ減らす
        val snapshot = mySnap.userSnapshot
        val id = snapshot.key.toString()    //ユーザーIDの取得
        val count = snapshot.child("counter").value.toString().toInt() - 1   //予約件数の取得
        firebase.setCount(id, count)

        //"end"を初期値に戻すために一時的に加えた処理(最終的にESP側で処理する予定)
        val room = es.getInt("SWITCH", -1)
        firebase.setNine(room)

        //各種内部ストレージ情報をリセット
        val ed = es.edit()
        ed.putInt("SWITCH", -1)
        ed.putInt("END", -1)
        ed.putString("KEY", "")
        ed.apply()

        quit_btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}