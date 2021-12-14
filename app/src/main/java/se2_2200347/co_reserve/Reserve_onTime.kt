package se2_2200347.co_reserve

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_reserve_on_time.*

private val database = FirebaseDatabase.getInstance()
private val bookRef = database.getReference("booking")

private val mySnap = MySnap.getInstance()

class Reserve_onTime : AppCompatActivity() {

    //年月日(曜日)を格納する変数
    private var year = ""
    private var month = ""
    private var dayOfWeek = ""
    private var day = 0

    //表示中の部屋番号を格納する変数
    private var idx = "0"

    //ListView出力用予約データ入力配列
    private var list1 = arrayListOf<String>()
    private var list2 = arrayListOf<String>()
    private var list3 = arrayListOf<String>()
    private var list4 = arrayListOf<String>()
    private var list5 = arrayListOf<String>()
    private var list6 = arrayListOf<String>()
    private var list7 = arrayListOf<String>()
    private var lists = arrayListOf(list1, list2, list3, list4, list5, list6, list7)

    //曜日表示用配列
    private val days = listOf("", "日", "月", "火", "水", "木", "金", "土")
    //更新時専用変数群(以下Updates)
    private var updateFlag = false
    private var oldDate = ""
    private var oldStart = ""
    private var oldRoom = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve_on_time)

        year = intent.getStringExtra("YEAR")!!
        val f_year = year?.substring(2,4)   //年は下２文字だけ表示させる
        month = intent.getStringExtra("MONTH")!!
        dayOfWeek = intent.getStringExtra("DATE")!!
        day = intent.getIntExtra("DAY", 0)
        var count = intent.getIntExtra("COUNT", 0)
        updateFlag = intent.getBooleanExtra("UPDATE", false)
        rot_date.text = "${f_year}年${month}月${dayOfWeek}日（${ days[day]}）"

        //「更新」の場合に、Updatesに情報を格納する
        if (updateFlag) {
            oldDate = year + month + dayOfWeek
            oldStart = intent.getStringExtra("BOOK_S")!!
            oldRoom = intent.getStringExtra("ROOM")!!
        }

        var flag = false
        bookRef.orderByChild("book_start").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mySnap.bookSnapshot = snapshot
                if (!updateFlag) {
                    setReserves()
                } else if(updateFlag) {
                    setForUpdate()
                }
                if (!flag) {
                    flag = true
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        rot_date_calendar.setOnClickListener {
            val intent = Intent(this, Reserve::class.java)
            intent.putExtra("FOR_RESULT", true)
            startActivityForResult(intent, 9999)
        }

        re_spi_room.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                idx = "${parent?.getItemIdAtPosition(position)}"
                setList()
                flag = true
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        re_btn_confirmation.setOnClickListener {
            val startHour = re_start_hour.selectedItem.toString()
            val startMinute = re_start_min.selectedItem.toString()
            val endHour = re_end_hour.selectedItem.toString()
            val endMinute = re_end_min.selectedItem.toString()
            val roomNumber = re_spi_room.selectedItemPosition + 1

            val resDate = year + month?.let { it1 -> Divide(-1).format02(it1.toLong()) } + dayOfWeek?.let { it2 -> Divide(-1).format02(it2.toLong()) }
            val startTime = startHour + startMinute
            val endTime = endHour + endMinute

            if (startTime.toInt() < endTime.toInt()) {
                val intent = Intent(this, Reserve_confirmation::class.java)
                intent.apply {
                    putExtra("DATE", resDate)
                    putExtra("BOOK_START", startTime)
                    putExtra("BOOK_END", endTime)
                    putExtra("ROOM_N", roomNumber)
                    putExtra("COUNT", count)
                    putExtra("UPDATE", updateFlag)
                    putExtra("OLD_DATE", oldDate)
                    putExtra("OLD_START", oldStart)
                    putExtra("OLD_ROOM", oldRoom)
                }
                startActivity(intent)
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("予約時間エラー")
                builder.setMessage("登録しようとしている時間は異常なため、予約できません。")
                builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which -> })
                builder.show()
            }
        }
    }

    /**
     * 格納中の部屋番号をもとにListViewに予約一覧を表示する
     * param num Int
     * param adapter ArrayAdapter
     */
    private fun setList() {
        val num = idx.toInt()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lists[num])
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        re_list_reserved.adapter = adapter
    }

    /**
     * 「booking」データベースから取得した情報をもとにListViewに表示させる配列を作成する
     * param dataSnapShot DataSnapshot
     * param sel String 年月日を纏めた文字列
     */
    private fun setReserves() {
        val dataSnapshot = mySnap.bookSnapshot
        val sel = year + month + dayOfWeek

        for (i in lists.indices) {
            lists[i].clear()
        }

        for (i in dataSnapshot.children) {
            if ("${i.child("date").value}" == sel) {
                val num = "${i.child("room").value}".toInt() - 1
                val startTime = i.child("book_start").value.toString().toLong()
                val endTime = i.child("book_end").value.toString().toLong()
                val sToE = startTime * 10000 + endTime
                val text = Divide(sToE).div8()
                lists[num].add(text)
            }
        }
        for (i in lists.indices) {
            if (lists[i].size == 0) {
                lists[i].add("予約はありません。")
            }
        }

        setList()
    }

    /**
     * 「booking」データベースから取得した情報をもとにListViewに表示させる配列を作成する
     * param dataSnapShot DataSnapshot
     * param sel String 年月日を纏めた文字列
     */
    private fun setForUpdate() {
        val dataSnapshot = mySnap.bookSnapshot
        val sel = year + month + dayOfWeek

        for (i in lists.indices) {
            lists[i].clear()
        }

        for (i in dataSnapshot.children) {
            val date = i.child("date").value.toString()
            val start = i.child("book_start").value.toString()
            val room = i.child("room").value.toString()
            if (date == sel
                    && !(date == oldDate && start == oldStart && room == oldRoom)
            ) {
                val num = "${i.child("room").value}".toInt() - 1
                val startTime = i.child("book_start").value.toString().toLong()
                val endTime = i.child("book_end").value.toString().toLong()
                val sToE = startTime * 10000 + endTime
                val text = Divide(sToE).div8()
                lists[num].add(text)
            }
        }
        for (i in lists.indices) {
            if (lists[i].size == 0) {
                lists[i].add("予約はありません。")
            }
        }

        setList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if(requestCode == 9999) {
                year = data?.getStringExtra("YEAR")!!
                val f_year = year.substring(2,4)   //年は下２文字だけ表示させる
                month = data.getStringExtra("MONTH")!!
                dayOfWeek = data.getStringExtra("DATE")!!
                day = data.getIntExtra("DAY", 0)
                rot_date.text = "" + f_year + "年" + month + "月" + dayOfWeek + "日（" + days[day] + "）"
            }
            if (!updateFlag) {
                setReserves()
            } else if (updateFlag) {
                setForUpdate()
            }
        }
    }
}