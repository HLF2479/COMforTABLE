package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_reserve_confirmation.*

class Reserve_confirmation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve_confirmation)
        val s_hour = intent.getStringExtra("START_H")
        val s_min = intent.getStringExtra("START_M")
        val e_hour = intent.getStringExtra("END_H")
        val e_min = intent.getStringExtra("END_M")
        val room = intent.getIntExtra("ROOM_N", 0)
        val Viewtext = "" + s_hour + "：" + s_min + "～" + e_hour + "：" + e_min + "\n" + room + "番の部屋で予約します。\nよろしいですか？"
        conf_text.setText(Viewtext)

        re_con_accept.setOnClickListener {
            val intent = Intent(this, Reserve_Commit::class.java)
            startActivity(intent)
        }
        re_con_cancel.setOnClickListener {
            finish()
        }
    }
}