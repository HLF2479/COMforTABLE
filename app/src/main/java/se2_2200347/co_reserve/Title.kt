package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_title.*

class Title : AppCompatActivity() {

    private val handler = Handler()
    private var count = 0
    private val scrOn = object : Runnable {
        override fun run() {
            title_bg.setImageResource(R.drawable.n_title1)
            if (count == 0) {
                count = 1
                handler.postDelayed(this, 700)
            } else if (count == 1) {
                count = 0
                val intent = Intent(applicationContext, Login::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        tit_btn.setOnClickListener {
            handler.post(scrOn)
        }

    }

    override fun onResume() {
        super.onResume()
        title_bg.setImageResource(R.drawable.n_title2)
    }
}