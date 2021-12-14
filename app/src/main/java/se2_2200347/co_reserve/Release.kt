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

        val sp = getSharedPreferences("ES", MODE_PRIVATE)
        val ed = sp.edit()

        //部屋番号を内部ストレージに保存
        val roomNumber = intent.getIntExtra("ROOM", -1)
        ed.putInt("SWITCH", roomNumber).apply()

        //予約終了時間をfirebaseにアップする(ESP側で判定する用)
        val bookEnd = intent.getIntExtra("END", -9)
        lockRef.child("$roomNumber/end").setValue("$bookEnd")

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