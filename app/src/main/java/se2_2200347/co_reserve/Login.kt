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

        title = getText(R.string.login_tit)

        val sp = getSharedPreferences("STU_DATA", MODE_PRIVATE)
        val editor = sp.edit()

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) { snapData = snapshot }   //ユーザー情報をクラス内変数に保持
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, R.string.error_firebase, Toast.LENGTH_SHORT).show()
            }
        })

        login_btn.setOnClickListener {

            val number = login_num.text.toString().trim()   //ID
            val pass = login_pass.text.toString().trim()    //パスワード

            try {
                val getPass = snapData.child("$number").child("id_pass").value.toString()   //firebaseからIDの一致するユーザのパスワードを取得
                //パスワードが一致した場合、一意キーを生成してホーム画面に遷移する
                if (pass == getPass) {
                    val key = userRef.child("$number/key").push().key.toString()    //一意キーを生成
                    userRef.child("$number/key").setValue(key)                      //生成した一意キーをfirebaseに送信

                    //内部ストレージに生成した一意キーとIDを保存
                    editor.putString("KEY", key)
                    editor.putString("NUM", number)
                    if ( "${snapData.child("$number").child("mode").value}" == "admin" )
                        editor.putBoolean("ADMIN", true)
                    editor.apply()

                    //裏のActivityを閉じてホーム画面に遷移
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                } else {
                    Toast.makeText(baseContext, R.string.login_error_t, Toast.LENGTH_SHORT).show()
                }
            } catch (e: NumberFormatException) {
                Toast.makeText(baseContext, R.string.login_error_all_t, Toast.LENGTH_SHORT).show()
            }
        }
    }
}