package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_release.*

private val database = FirebaseDatabase.getInstance()
private val lockRef = database.getReference("lock")

class Release : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_release)

        title = getText(R.string.release_tit)

        val roomNumber = intent.getIntExtra("ROOM", -1)
        //予約終了時間をfirebaseにアップする(ESP側で判定する用)
        val bookEnd = intent.getIntExtra("END", -9)
        lockRef.child("$roomNumber/end").setValue("$bookEnd")

        val key = intent.getStringExtra("KEY")

        //部屋番号、終了時間、予約情報のキー値を内部ストレージに保存
        val es = getSharedPreferences("ES", MODE_PRIVATE)
        val ed = es.edit()
        ed.putInt("SWITCH", roomNumber)
        ed.putInt("END", bookEnd)
        ed.putString("KEY", key)
        ed.apply()

        val text = "ROOM${roomNumber}のロックを解除しました。\n終了時間にご注意ください。"
        lockRef.child("$roomNumber/lock").setValue("0")
        release_tv.text = text

        release_submit.setOnClickListener {
            val intent = Intent(this, LockUnlock::class.java)
            startActivity(intent)
            finish()
        }
    }
}