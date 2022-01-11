package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_reserve_confirmation.*

private val mySnap = MySnap.getInstance()

class Reserve_confirmation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve_confirmation)

        title = getText(R.string.reserve_t3)

        val id = getSharedPreferences("STU_DATA", MODE_PRIVATE).getString("NUM", "")

        //遷移元から日付と予約時間、更新フラグを取得する
        val date = intent.getStringExtra("DATE")
        val startTime = intent.getStringExtra("BOOK_START")
        val endTime = intent.getStringExtra("BOOK_END")
        val roomNumber = intent.getIntExtra("ROOM_N", 0)
        val updateFlag = intent.getBooleanExtra("UPDATE", false)

        //表示用文字列作成のために日付と予約時間を纏める
        val resDate = (date + startTime + endTime).toLong()
        //「更新」の時は、文字列を変化させる
        var textR = getText(R.string.home_reserve)
        if (updateFlag) {
            textR = getText(R.string.update)
            title = getText(R.string.reserve_t3u)
        }
        //纏めた情報から文字列を作成して出力する
        var viewText = "${Divide(resDate).div16()}\nroom${roomNumber}で${textR}します。\nよろしいですか？"

        //スナップからユーザーの予約件数を取得する
        val count = mySnap.userSnapshot.child("counter").value.toString().toInt()

        var en = true   //異常事態を判定するフラグ
        //各異常に対して専用のテキストを出力する
        if (roomNumber < 1 || roomNumber > 5) {
            //部屋番号が異常な範囲の時
            viewText = "${getText(R.string.confirm_roomError)}"
            en = false
        } else if (count > 3 || (!updateFlag && count == 3)) {
            //予約件数が３件を超えている時、または、「更新」ではない状態で予約件数が３件ある時
            viewText = "${getText(R.string.confirm_resError)}"
            en = false
        }
        if (!en) {
            title = getText(R.string.reserve_t3e)
        }
        conf_text.text = viewText

        re_con_accept.setOnClickListener {
            if (id != null && date != null && startTime != null && endTime != null && en) {
                val firebase = FirebaseReserve()
                var flag = false    //予約が成功したか判定するフラグ
                if (!updateFlag) {
                    flag = firebase.reg(startTime.toInt(), endTime.toInt(), date, roomNumber.toString(), id, count + 1)
                } else if (updateFlag) {
                    val oldDate = intent.getStringExtra("OLD_DATE")!!   //更新前予約日
                    val oldStart = intent.getStringExtra("OLD_START")!! //更新前予約開始時間
                    val oldRoom = intent.getStringExtra("OLD_ROOM")!!   //更新前予約部屋番号
                    flag = firebase.upd(startTime.toInt(), endTime.toInt(), date, roomNumber.toString(), id, oldDate, oldStart, oldRoom)
                }
                val intent = Intent(this, Reserve_Commit::class.java)
                intent.putExtra("OK_FLAG", flag)
                intent.putExtra("UPDATE", updateFlag)
                startActivity(intent)
                finish()
            }
        }
        re_con_cancel.setOnClickListener {
            finish()
        }
    }
}