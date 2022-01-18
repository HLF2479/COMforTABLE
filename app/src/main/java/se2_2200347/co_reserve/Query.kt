package se2_2200347.co_reserve

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_query.*

class Query : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query)

        title = getText(R.string.query_tit)

        submit.setOnClickListener {
            val subject = subject_et.text.toString()
            val message = body_et.text.toString()
            val builder = AlertDialog.Builder(this)
            if (subject != "" && message != "") {
                builder.setTitle(R.string.query_sub_tit)
                    .setMessage("件名：$subject\n内容：$message\nで送信します。よろしいですか？")
                    .setPositiveButton(R.string.query_send) { dialog, which ->
                        Toast.makeText(baseContext, R.string.query_send_t, Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, which ->  }
            } else {
                builder.setTitle(R.string.query_error_tit)
                    .setMessage(R.string.query_error_mes)
                    .setPositiveButton(R.string.ok) { dialog, which -> }
            }
            builder.show()
        }
    }
}