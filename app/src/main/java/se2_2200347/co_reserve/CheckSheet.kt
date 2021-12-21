package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_check_sheet.*

class CheckSheet : AppCompatActivity() {

    private val mySnap = MySnap.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_sheet)

        title = getText(R.string.check_t)

        check2.setOnClickListener {
            check2.isChecked = getChecked()
        }

        check_back.setOnClickListener {
            finish()
        }

        check_submit.setOnClickListener {
            val checks = listOf(
                check1.isChecked,
                    getChecked(),
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

    /**
     * 扉のロックがかかっているか確認するメソッド。check2と連動
     * param snapshot 扉のロック状況のスナップ
     * param roomNumber 利用している部屋の番号
     * param locked 利用している部屋のロック状況(1 or else)
     */
    private fun getChecked() : Boolean {
        val snapshot = mySnap.lockSnapshot
        val roomNumber = intent.getIntExtra("ROOM", -1)
        val locked = snapshot.child("$roomNumber/lock").value.toString().toInt()
        if (locked == 1) {
            return true
        }
        Toast.makeText(baseContext, "扉がロックされていません。", Toast.LENGTH_SHORT).show()
        return false
    }
}