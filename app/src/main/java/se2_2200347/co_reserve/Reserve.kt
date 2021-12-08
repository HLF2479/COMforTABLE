package se2_2200347.co_reserve

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_reserve.*
import java.util.*

class Reserve : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve)

        var calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 0)
        // 過去の選択可能日を当日に変更
        cal_reserve.minDate = calendar.timeInMillis

        calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 13)

        // 未来の選択可能日を２週間後に変更
        cal_reserve.maxDate = calendar.timeInMillis

        cal_reserve.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val date = "$year/$month/$dayOfMonth"
            calendar.set(year, month, dayOfMonth)
            val day = calendar.get(Calendar.DAY_OF_WEEK)
            if (day == Calendar.SUNDAY) {
                val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.Theme_AppCompat_Light_Dialog))
                builder.setTitle("エラー")
                builder.setMessage("この日は日曜日なので選択できません")
                builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which -> })
                builder.show()
            } else {
                var intent = Intent(this, Reserve_onTime::class.java)
                intent.putExtra("YEAR", year.toString())
                intent.putExtra("MONTH", (month + 1).toString())
                intent.putExtra("DATE", dayOfMonth.toString())
                intent.putExtra("DAY", day)
                startActivity(intent)
            }
        }
    }
}