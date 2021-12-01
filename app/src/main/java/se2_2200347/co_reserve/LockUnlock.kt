package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_lock_unlock.*
import com.google.firebase.database.FirebaseDatabase

class LockUnlock : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()

    private var c = 0
    private val handler = Handler()
    private val flags = arrayOf(true, false)
    val btn_lock = object : Runnable {
        override fun run() {
            c = 1 - c
            unlock_btn.isEnabled = flags[c]
            lock_btn.isEnabled = flags[c]
            if (c == 1) {
                handler.postDelayed(this, 3000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_unlock)

        val sp = getSharedPreferences("ES", MODE_PRIVATE)
        val ro_number = sp.getInt("SWITCH", -1)
        val lockRef = database.getReference("lock").child(ro_number.toString())

        unlock_btn.setOnClickListener {
            Toast.makeText(this, "ロックを解除しました。", Toast.LENGTH_SHORT).show()
            handler.post(btn_lock)
            lockRef.setValue("0")
        }
        lock_btn.setOnClickListener {
            Toast.makeText(this, "扉をロックしました。", Toast.LENGTH_SHORT).show()
            handler.post(btn_lock)
            lockRef.setValue("1")
        }
        quit_btn.setOnClickListener {
            val intent = Intent(this, CheckSheet::class.java)
            intent.putExtra("ROOM", ro_number)
            startActivity(intent)
        }

    }
}