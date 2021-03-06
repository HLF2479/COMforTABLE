package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_query_confirm.*

class QueryConfirm : AppCompatActivity() {

    private val dialog = ShowDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query_confirm)

        title = getText(R.string.query_sub_tit)

        //送信した時に表示するダイアログ
        val dismiss = Runnable {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        val posDis = Runnable {
            dialog.getPD(
                "${getText(R.string.query_send_tit)}", "${getText(R.string.query_send_t)}",
                "${getText(R.string.ok)}", {},
                dismiss
            )
        }

        val subject = intent.getStringExtra("SUBJECT")!!
        val message = intent.getStringExtra("MESSAGE")!!
        query_con_sub_tv.text = subject
        query_con_body_tv.text = message
        query_con_btn.setOnClickListener {
            val service = MailService(subject, message)
            service.send()
            Handler().post(posDis)
        }
    }
}