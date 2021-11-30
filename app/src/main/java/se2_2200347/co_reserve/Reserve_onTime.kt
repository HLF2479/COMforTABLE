package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_reserve_on_time.*

private val daies = listOf<String>("", "日", "月", "火", "水", "木", "金", "土")

//１～７番部屋の予約時間仮データ(s:開始時間、e:終了時間)
private val list1_s = arrayListOf<String>("10:00", "13:30", "16:00", "18:00", )
private val list1_e = arrayListOf<String>("12:00", "15:00", "17:30", "20:00", )
private val list2_s = arrayListOf<String>("09:00", "11:00", "12:50", "16:00", "17:45", )
private val list2_e = arrayListOf<String>("10:30", "11:30", "15:30", "17:15", "19:00", )
private val list3_s = arrayListOf<String>("11:00", "14:00", "18:00", )
private val list3_e = arrayListOf<String>("12:30", "16:15", "19:00", )
private val list4_s = arrayListOf<String>("10:15", "12:45",)
private val list4_e = arrayListOf<String>("11:30", "15:15",)
private val list5_s = arrayListOf<String>("09:15", "10:30", "11:30", "13:00", "14:15", "15:30", "17:30",)
private val list5_e = arrayListOf<String>("10:00", "11:00", "12:15", "13:45", "15:00", "17:00", "18:45",)
private val list6_s = arrayListOf<String>("10:15", "18:15",)
private val list6_e = arrayListOf<String>("14:30", "19:00",)
private val list7_s = arrayListOf<String>("11:45",)
private val list7_e = arrayListOf<String>("17:30",)

//ListView出力用予約データ入力配列
private var list1 = arrayListOf<String>()
private var list2 = arrayListOf<String>()
private var list3 = arrayListOf<String>()
private var list4 = arrayListOf<String>()
private var list5 = arrayListOf<String>()
private var list6 = arrayListOf<String>()
private var list7 = arrayListOf<String>()
private var lists_s = arrayListOf(list1_s, list2_s, list3_s, list4_s, list5_s, list6_s, list7_s,)
private var lists_e = arrayListOf(list1_e, list2_e, list3_e, list4_e, list5_e, list6_e, list7_e,)
private var lists = arrayListOf(list1, list2, list3, list4, list5, list6, list7)

class Reserve_onTime : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve_on_time)

        for (i in 0 until lists.size) {
            for (k in 0 until lists_s[i].size) {
                lists[i].add(lists_s[i][k] + " ~ " + lists_e[i][k])
            }
        }

        setList("0")

        val year = intent.getStringExtra("YEAR")
        val f_year = year?.substring(2,4)   //年は下２文字だけ表示させる
        val month = intent.getStringExtra("MONTH")
        val date = intent.getStringExtra("DATE")
        val day = intent.getIntExtra("DAY", 0)
        re_date.text = "" + f_year + "年" + month + "月" + date + "日（" + daies[day] + "）"

        re_spi_room.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val idx = parent?.getItemIdAtPosition(position)
                setList(idx.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        re_btn_confirmation.setOnClickListener {
            val start_hour = re_start_hour.selectedItem.toString()
            val start_minute = re_start_min.selectedItem.toString()
            val end_hour = re_end_hour.selectedItem.toString()
            val end_minunte = re_end_min.selectedItem.toString()
            val room_num = re_spi_room.selectedItemPosition + 1
            val intent = Intent(this, Reserve_confirmation::class.java)
            intent.putExtra("START_H", start_hour)
            intent.putExtra("START_M", start_minute)
            intent.putExtra("END_H", end_hour)
            intent.putExtra("END_M", end_minunte)
            intent.putExtra("ROOM_N", room_num)
            startActivity(intent)
        }
    }

    fun setList(idx: String) {
        val num = idx.toInt()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lists[num])
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        re_list_reseved.adapter = adapter
    }
}