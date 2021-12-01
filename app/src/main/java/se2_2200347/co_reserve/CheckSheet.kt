package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_check_sheet.*

class CheckSheet : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_sheet)

        val roomNumber = intent.getIntExtra("ROOM", -1)
        val lockRef = database.getReference("lock/$roomNumber")

        check1.setOnClickListener {
            lockRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val locked = snapshot.value.toString().toInt()
                    if (locked == 0) {
                        check1.isChecked = false
                        Toast.makeText(baseContext, "扉がロックされていません。", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(baseContext, "通信に失敗しました。", Toast.LENGTH_SHORT).show()
                }
            })
        }

        check_back.setOnClickListener {
            finish()
        }

        check_submit.setOnClickListener {
            val checks = listOf(
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
                finish()
            } else {
                Toast.makeText(baseContext, "チェックボックスが全て埋まっていません。", Toast.LENGTH_SHORT).show()
            }
        }
    }
}