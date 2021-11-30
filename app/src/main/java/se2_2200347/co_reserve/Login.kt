package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.NumberFormatException

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sp = getSharedPreferences("STU_DATA", MODE_PRIVATE)
        val editor = sp.edit()

        login_btn.setOnClickListener {

            val number = login_num.text.toString().trim()
            val pass = login_pass.text.toString().trim()
            val ref = database.getReference("users/$number/id_pass")

            try {
                ref.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val getPass = snapshot.value.toString()
                        if (pass == getPass) {
                            editor.putString("NUM", number)
                            editor.apply()
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else {
                            Toast.makeText(baseContext, "ID、またはパスワードが違います。", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(baseContext, "読み込み失敗", Toast.LENGTH_SHORT).show()
                    }
                })
            } catch (e: NumberFormatException) {
                Toast.makeText(baseContext, "ID,パスワードを入力してください(半角英数字)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}