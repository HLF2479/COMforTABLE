package se2_2200347.co_reserve

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_reserve__commit.*

class Reserve_Commit : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve__commit)

        title = getText(R.string.reserve_t4e)

        val ok = intent.getBooleanExtra("OK_FLAG", false)
        val updateFlag = intent.getBooleanExtra("UPDATE", false)
        if (ok && !updateFlag) {
            title = getText(R.string.reserve_t4s)
            commit_tv.text = getText(R.string.commit_ok)
        } else if (ok && updateFlag) {
            title = getText(R.string.reserve_t4u)
            commit_tv.text = getText(R.string.update_ok)
        }

        re_com_btn.setOnClickListener {
            if (ok) {
                val intent = Intent(application, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                finish()
            }
        }
    }
}