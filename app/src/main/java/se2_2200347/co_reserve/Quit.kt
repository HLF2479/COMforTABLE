package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_quit.*

private val firebase = FirebaseReserve()

class Quit : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quit)

        title = getText(R.string.quit_tit)

        val es = getSharedPreferences("ES", MODE_PRIVATE)

        //内部ストレージに保存してあるキー値を取得して、ログtableに出力する
        val key = es.getString("KEY", "")!!
        firebase.sendLog(key)

        //"end"を初期値に戻すために一時的に加えた処理(最終的にESP側で処理する予定)
        val room = es.getInt("SWITCH", -1)
        firebase.setNine(room)

        //入室処理に関わる内部ストレージ情報をリセット
        es.edit().clear().apply()

        quit_btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}