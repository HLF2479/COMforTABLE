package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_reserve_confirmation.*

class Reserve_confirmation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve_confirmation)

        val id = getSharedPreferences("STU_DATA", MODE_PRIVATE).getString("NUM", "")
        val date = intent.getStringExtra("DATE")
        val startTime = intent.getStringExtra("BOOK_START")
        val endTime = intent.getStringExtra("BOOK_END")
        val roomNumber = intent.getIntExtra("ROOM_N", 0)
        val count = intent.getIntExtra("COUNT", 0)
        val updateFlag = intent.getBooleanExtra("UPDATE", false)
        val resDate = (date + startTime + endTime).toLong()
        var viewText = "${Divide(resDate).div16()}\nroom${roomNumber}で予約します。\nよろしいですか？"

        var en = re_con_accept.isEnabled
        if (roomNumber < 1 || roomNumber > 5) {
            viewText = "部屋番号が異常です。"
            en = false
        } else if (!updateFlag && count >= 3) {
            viewText = "予約件数がいっぱいです。\n既に予約している分が完了してから再度お試しください。"
            en = false
        }
        conf_text.text = viewText

        re_con_accept.setOnClickListener {
            if (id != null && date != null && startTime != null && endTime != null) {
                val firebase = FirebaseReserve()
                if (!updateFlag) {
                    firebase.reg(startTime.toInt(), endTime.toInt(), date, roomNumber.toString(), id)
                    firebase.setCount(id, count + 1)
                } else if (updateFlag) {
                    val oldDate = intent.getStringExtra("OLD_DATE")!!
                    val oldStart = intent.getStringExtra("OLD_START")!!
                    val oldRoom = intent.getStringExtra("OLD_ROOM")!!
                    firebase.reg(startTime.toInt(), endTime.toInt(), date, roomNumber.toString(), id, oldDate, oldStart, oldRoom)
                }
                val intent = Intent(this, Reserve_Commit::class.java)
                startActivity(intent)
            }
        }
        re_con_cancel.setOnClickListener {
            finish()
        }
    }
}