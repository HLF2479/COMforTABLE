package se2_2200347.co_reserve

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_attention.*
import java.time.LocalDate
import java.time.LocalTime

private val mySnap = MySnap.getInstance()

class Attention : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attention)

        title = getText(R.string.enter_te)

        val snapshot = mySnap.myBooking
        val roomNumber = intent.getIntExtra("ROOM", -1)

        val localD = LocalDate.now()    //現在日付
        val localT = LocalTime.now()    //現在時刻

        var key = ""
        var resStart : Int  //予約情報の予約開始時間
        var resEnd = -9     //予約情報の予約首領時間
        var resRoom : Int   //予約情報の予約部屋番号

        var flagD = false   //予約日付の一致判定フラグ
        var flagT = false   //予約時刻の一致判定フラグ
        var okFlag = false  //予約情報の完全一致を判定するフラグ

        for (i in snapshot.children) {
            //各予約情報の内、日付を獲得してlocalDateに変換する
            val resDate = i.child("date").value.toString().toInt()
            val resYear = resDate / 10000
            val resMonth = resDate / 100 % 100
            val resMonthDay = resDate % 100
            val compDate = LocalDate.of(resYear, resMonth, resMonthDay)
            Log.d("LOCAL_DATE", compDate.toString())

            //日付が一致した場合に一致判定フラグを是にして時刻判定する
            if (localD == compDate) {
                flagD = true    //日付の一致判定フラグを是にする

                //予約情報から開始時間と終了時間を取得してlocalTimeに変換する
                resStart = i.child("book_start").value.toString().toInt()
                resEnd = i.child("book_end").value.toString().toInt()
                Log.d("LOCAL_TIME", "$resStart, $resEnd")
                val hourS = resStart / 100
                val minS = resStart % 100
                val compS = LocalTime.of(hourS, minS, 0)
                Log.d("LOCAL_START", "$compS")
                val hourE = resEnd / 100
                val minE = resEnd % 100
                val compE = LocalTime.of(hourE, minE, 0)
                Log.d("LOCAL_END", "$compE")

                resRoom = i.child("room").value.toString().toInt()
                Log.d("LOCAL_ROOM", "room$resRoom")

                //部屋番号の一致、開始時間 ＜ 現在時間 ＜ 終了時間 の成立が両立されていれば、時刻の一致判定フラグを是にしてキー値を取得する
                if (compS < localT && localT < compE && resRoom == roomNumber) {
                    flagT = true
                    key = i.key.toString()
                }
            }
        }

        //エラー文をセットし、日付、時刻の一致判定フラグが是なら次に進む文章をセットしなおす
        var text = getText(R.string.attention_error)
        if (flagD && flagT) {
            title = getText(R.string.att_title)
            text = getText(R.string.attention_ok)
            attention_submit.text = getText(R.string.next)
            okFlag = true   //完全一致フラグを是にする
        }
        attention_txt.text = text

        attention_submit.setOnClickListener {
            if (okFlag) {
                val intent = Intent(this, Release::class.java)
                intent.putExtra("ROOM", roomNumber)
                intent.putExtra("END", resEnd)
                intent.putExtra("KEY", key)
                startActivity(intent)
                finish()
            } else {
                finish()
            }
        }
    }
}