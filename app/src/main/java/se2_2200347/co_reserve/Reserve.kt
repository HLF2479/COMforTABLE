package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_reserve.*
import java.util.*

class Reserve : AppCompatActivity() {

    private val dialog = ShowDialog(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve)

        title = getText(R.string.reserve_tit)

        val frFlag = intent.getBooleanExtra("FOR_RESULT", false) //onTimeクラスから遷移してきたかを表すフラグ

        // 過去の選択可能日を当日に変更
        var calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 0)
        cal_reserve.minDate = calendar.timeInMillis

        // 未来の選択可能日を２週間後に変更
        calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 13)
        cal_reserve.maxDate = calendar.timeInMillis

        cal_reserve.setOnDateChangeListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            val day = calendar.get(Calendar.DAY_OF_WEEK)
            if (day == Calendar.SUNDAY) {
                //選択した日付が日曜日なら、予約できない旨を出力して処理を終える
                dialog.getP(
                    "${getText(R.string.error)}", "${getText(R.string.reserve_error_sun)}",
                    "${getText(R.string.ok)}") {}
            } else if (frFlag){
                //onTimeクラスから遷移してきた場合、選択した日付情報をもってonTimeクラスに戻る
                val intent = Intent().apply {
                    putExtra("YEAR", year.toString())
                    putExtra("MONTH", (month + 1).toString())
                    putExtra("DATE", dayOfMonth.toString())
                    putExtra("DAY", day)
                }
                setResult(RESULT_OK, intent)
                finish()
            } else if(!frFlag){
                //「予約」から遷移してきた場合、選択した日付情報をもってonTimeクラスに遷移する
                var intent = Intent(this, Reserve_onTime::class.java)
                intent.apply {
                    putExtra("YEAR", year.toString())
                    putExtra("MONTH", (month + 1).toString())
                    putExtra("DATE", dayOfMonth.toString())
                    putExtra("DAY", day)
                }
                startActivity(intent)
            }
        }
    }
}