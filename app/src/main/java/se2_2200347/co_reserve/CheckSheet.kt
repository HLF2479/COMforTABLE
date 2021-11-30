package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_check_sheet.*

class CheckSheet : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_sheet)

        check_back.setOnClickListener {
            finish()
        }

        check_submit.setOnClickListener {
            val checks = arrayOf(
                check1.isChecked,
                check2.isChecked,
                check3.isChecked,
                check4.isChecked,
            )
            var flag = true
            for (i in checks.indices) {
                if (!checks[i]) {
                    flag = false
                }
            }
            if (flag) {
                val intent = Intent(this, Quit::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(baseContext, "チェックボックスが全て埋まっていません。", Toast.LENGTH_SHORT).show()
            }
        }
    }
}