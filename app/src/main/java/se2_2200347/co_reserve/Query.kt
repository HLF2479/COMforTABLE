package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_query.*

class Query : AppCompatActivity() {

    private val dialog = ShowDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query)

        title = getText(R.string.query_tit)

        submit.setOnClickListener {
            val subject = subject_et.text.toString()    //件名取得
            val message = body_et.text.toString()       //本文取得
            //件名と本文が入力されている場合にのみ次の画面に遷移する
            if (subject != "" && message != "") {
                val intent = Intent(this, QueryConfirm::class.java)
                intent.putExtra("SUBJECT", subject)
                intent.putExtra("MESSAGE", message)
                startActivity(intent)
            } else {
                dialog.getP(
                    "${getText(R.string.query_error_tit)}", "${getText(R.string.query_error_mes)}",
                    "${getText(R.string.ok)}") {}
            }
        }
    }
}