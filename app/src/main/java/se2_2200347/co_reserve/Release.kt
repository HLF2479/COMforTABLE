package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_release.*

class Release : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_release)

        val sp = getSharedPreferences("ES", MODE_PRIVATE)
        val ed = sp.edit()

        val ro_number = intent.getIntExtra("ROOM", -1)
        ed.putInt("SWITCH", ro_number).apply()

        release_submit.setOnClickListener {
            val intent = Intent(this, LockUnlock::class.java)
            startActivity(intent)
            finish()
        }
    }
}