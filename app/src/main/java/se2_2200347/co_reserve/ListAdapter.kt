package se2_2200347.co_reserve

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.collections.ArrayList

/**
 * 予約一覧を表示させるためのAdapterを設定するクラスファイル
 * 参照元：https://qiita.com/DreamHanks/items/6159ec4c296a1a280db9
 */

class ListAdapter(context: Context, userList: ArrayList<String>, DateList: ArrayList<Long>, Admin: Boolean): BaseAdapter(){

    private val context = context
    private val userList = userList
    private val dateList = DateList
    private val admin = Admin
    private val dialog = ShowDialog(context)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_reserve, null)
        val textView = view.findViewById<TextView>(R.id.reserve_tv)

        val reserve = userList[position]
        textView.text = reserve

        val updater = view.findViewById<TextView>(R.id.update_tv)   //「更新」のID
        val remover = view.findViewById<TextView>(R.id.remove_tv)   //「取消」のID

        if (admin) {
            updater.isEnabled = false
            remover.isEnabled = false
        }

        val date = dateList[position] / 1000000000          //年月日情報
        val bookStart = dateList[position] / 100000 % 10000 //予約開始時間
        val bookEnd = dateList[position] / 10 % 10000       //予約終了時間
        val room = dateList[position] % 10                  //部屋番号

        updater.setOnClickListener {
            //positionから年月日情報を取り出し、曜日を加えて予約アクティビティに遷移する
            val year = date / 10000         //年
            val month = date / 100 % 100    //月
            val dayOfMonth = date % 100     //日
            var dayOfWeek : Int             //曜日(数値)

            //選択されている日付から曜日情報を取得して、数値化する
//            val localDate = LocalDate.parse("$year-$month-$dayOfMonth")
            val localDate = LocalDate.of(year.toInt(), month.toInt(), dayOfMonth.toInt())
            val day = localDate.dayOfWeek
            dayOfWeek = when (day) {
                DayOfWeek.SUNDAY -> 1
                DayOfWeek.MONDAY -> 2
                DayOfWeek.TUESDAY -> 3
                DayOfWeek.WEDNESDAY -> 4
                DayOfWeek.THURSDAY -> 5
                DayOfWeek.FRIDAY -> 6
                DayOfWeek.SATURDAY -> 7
            }

            val intent = Intent(context, Reserve_onTime::class.java)
            intent.apply {
                putExtra("YEAR", year.toString())
                putExtra("MONTH", month.toString())
                putExtra("DATE", dayOfMonth.toString())
                putExtra("DAY", dayOfWeek)
                putExtra("BOOK_S", "%04d".format(bookStart))
                putExtra("BOOK_E", "%04d".format(bookEnd))
                putExtra("ROOM", "$room")
                putExtra("UPDATE", true)
            }
            context.startActivity(intent)
        }

        remover.setOnClickListener {
            val pos = Runnable {
                //各ユーザーの予約情報の内、対応したものを削除する
                FirebaseReserve().cancel(date, bookStart, room)
                Toast.makeText(context, R.string.la_cancel_mes, Toast.LENGTH_SHORT).show()
            }
            dialog.getPN(
                "${context.getText(R.string.la_cancel)}", "${reserve}の予約を取り消します。\nよろしいですか？",
                "${context.getText(R.string.yes)}", pos,
                "${context.getText(R.string.no)}", {})
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return userList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return userList.size
    }
}