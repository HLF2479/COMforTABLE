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
    private val userRef = database.getReference("users")
    private lateinit var snapData : DataSnapshot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sp = getSharedPreferences("STU_DATA", MODE_PRIVATE)
        val editor = sp.edit()

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapData = snapshot
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "通信に失敗しました。", Toast.LENGTH_SHORT).show()
            }
        })

        login_btn.setOnClickListener {

            val number = login_num.text.toString().trim()
            val pass = login_pass.text.toString().trim()

            try {
                val getPass = snapData.child("$number").child("id_pass").value.toString()
                if (pass == getPass) {
                    val key = userRef.child("$number/key").push().key.toString()
                    userRef.child("$number/key").setValue(key)
                    editor.putString("KEY", key)
                    editor.putString("NUM", number)
                    editor.apply()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    Toast.makeText(baseContext, "ID、またはパスワードが違います。", Toast.LENGTH_SHORT).show()
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(baseContext, "ID,パスワードを入力してください(半角英数字)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}