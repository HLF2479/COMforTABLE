package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_attention.*

class Attention : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attention)

        val ro_number = intent.getIntExtra("ROOM", -1)
//        val text = "" + ro_number + "番の部屋を認識しました。"
//        attention_txt.setText(text)
        attention_submit.setOnClickListener {
            val intent = Intent(this, Release::class.java)
            intent.putExtra("ROOM", ro_number)
            startActivity(intent)
            finish()
        }
    }
}