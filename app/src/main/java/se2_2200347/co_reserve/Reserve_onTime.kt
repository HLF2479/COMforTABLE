package se2_2200347.co_reserve

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    //選択中の日付を格納する変数
    private var year = ""       //年
    private var month = ""      //月
    private var dayOfWeek = ""  //日
    private var day = 0         //曜日

    //曜日表示用配列
    private val days = listOf("", "日", "月", "火", "水", "木", "金", "土")

    //表示中の部屋番号を格納する変数
    private var idx = "0"

    //ListView出力用予約データ入力配列
    private var list1 = arrayListOf<String>()   //room1の予約情報
    private var list2 = arrayListOf<String>()   //room2の予約情報
    private var list3 = arrayListOf<String>()   //room3の予約情報
    private var list4 = arrayListOf<String>()   //room4の予約情報
    private var list5 = arrayListOf<String>()   //room5の予約情報
    private var lists = arrayListOf(list1, list2, list3, list4, list5)

    //更新時専用変数群(以下Updates)
    private var updateFlag = false  //「更新」かどうかを判定するフラグ(true -> 「更新」、false -> 「予約」)
    private var oldDate = ""        //更新前日付
    private var oldStart = ""       //更新前予約開始時間
    private var oldRoom = ""        //更新前予約部屋番号

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserve_on_time)

        //遷移元から日付情報と更新フラグを取得
        year = intent.getStringExtra("YEAR")!!
        val f_year = year?.substring(2,4)   //年は下２文字だけ表示させる
        month = intent.getStringExtra("MONTH")!!
        dayOfWeek = intent.getStringExtra("DATE")!!
        day = intent.getIntExtra("DAY", 0)
        updateFlag = intent.getBooleanExtra("UPDATE", false)

        //取得した日付情報をテキストにして出力
        rot_date.text = "${f_year}年${month}月${dayOfWeek}日（${days[day]}）"

        //「更新」の場合に、Updatesに情報を格納する
        if (updateFlag) {
            oldDate = year + month + dayOfWeek
            oldStart = intent.getStringExtra("BOOK_S")!!
            oldRoom = intent.getStringExtra("ROOM")!!
        }

        bookRef.orderByChild("book_start").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mySnap.bookSnapshot = snapshot
                judgeUpdate()
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
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        re_btn_confirmation.setOnClickListener {
            val startHour = re_start_hour.selectedItem.toString()   //選択中予約開始時間(時)
            val startMinute = re_start_min.selectedItem.toString()  //選択中予約開始時間(分)
            val endHour = re_end_hour.selectedItem.toString()       //選択中予約終了時間(時)
            val endMinute = re_end_min.selectedItem.toString()      //選択中予約終了時間(分)
            val roomNumber = re_spi_room.selectedItemPosition + 1   //選択中予約部屋番号

            //遷移先で読み込む用に日付、予約開始時間、予約終了時間をそれぞれ纏める
            val resDate = year + month?.let { it1 -> Divide(-1).format02(it1.toLong()) } + dayOfWeek?.let { it2 -> Divide(-1).format02(it2.toLong()) }
            val startTime = startHour + startMinute
            val endTime = endHour + endMinute

            if (startTime.toInt() < endTime.toInt()) {
                //予約開始時間が予約終了時間より前なら、次の画面に遷移する
                val intent = Intent(this, Reserve_confirmation::class.java)
                intent.apply {
                    putExtra("DATE", resDate)
                    putExtra("BOOK_START", startTime)
                    putExtra("BOOK_END", endTime)
                    putExtra("ROOM_N", roomNumber)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if(requestCode == 9999) {
                //カレンダーから新しく日付情報を取得
                year = data?.getStringExtra("YEAR")!!
                val f_year = year.substring(2,4)   //年は下２文字だけ表示させる
                month = data.getStringExtra("MONTH")!!
                dayOfWeek = data.getStringExtra("DATE")!!
                day = data.getIntExtra("DAY", 0)

                //取得した日付情報をテキストにして出力
                rot_date.text = "" + f_year + "年" + month + "月" + dayOfWeek + "日（" + days[day] + "）"
            }
            judgeUpdate()
        }
    }

    /**
     * 現在の処理が「更新」か「予約」かを判定して、対応するメソッドを呼び出すメソッド
     */
    private fun judgeUpdate() {
        //「更新」なら更新用の、「予約」なら予約用の配列処理をする
        when (updateFlag) {
            true -> setForUpdate()
            false -> setReserves()
        }
    }

    /**
     * 「booking」データベースから取得した情報をもとにListViewに表示させる配列を作成する
     * param dataSnapShot DataSnapshot 予約全件を格納しているスナップ
     * param sel String 年月日を纏めた文字列
     */
    private fun setReserves() {
        val dataSnapshot = mySnap.bookSnapshot
        val sel = year + month + dayOfWeek

        clearList()

        //対象の日付と予約情報の日付が一致している場合に配列に予約情報を格納する
        for (i in dataSnapshot.children) {
            if ("${i.child("date").value}" == sel) { addData(i) }
        }

        setNull()
        setList()
    }

    /**
     * 「booking」データベースから取得した情報をもとにListViewに表示させる配列を作成する
     * param dataSnapShot DataSnapshot 予約全件を格納しているスナップ
     * param sel String 選択中の日付を纏めた文字列
     */
    private fun setForUpdate() {
        val dataSnapshot = mySnap.bookSnapshot
        val sel = year + month + dayOfWeek

        clearList()

        for (i in dataSnapshot.children) {
            val date = i.child("date").value.toString()         //日付
            val start = i.child("book_start").value.toString()  //開始時間
            val room = i.child("room").value.toString()         //部屋番号
            //対象の日付と予約情報の日付が一致していて、かつ更新しようとしている情報と一致していない場合に配列に予約情報を格納する
            if (date == sel
                    && !(date == oldDate && start == oldStart && room == oldRoom)
            ) { addData(i) }
        }

        setNull()
        setList()
    }

    /**
     * listsの中身を初期化するメソッド
     */
    private fun clearList() {
        for (i in lists.indices) {
            lists[i].clear()
        }
    }

    /**
     * スナップから予約情報を取得して配列に格納するメソッド
     * param num Int 部屋番号
     * param startTime Long 予約開始時間
     * param endTime Long 予約終了時間
     * param sToE Long 開始時間と終了時間を纏めた変数
     * param text String 表示用文字列
     */
    private fun addData(i : DataSnapshot) {
        val num = "${i.child("room").value}".toInt() - 1
        val startTime = i.child("book_start").value.toString().toLong()
        val endTime = i.child("book_end").value.toString().toLong()
        val sToE = startTime * 10000 + endTime
        val text = Divide(sToE).div8()
        lists[num].add(text)
    }

    /**
     * 配列の中身がなかった場合に専用メッセージを格納するメソッド
     */
    private fun setNull() {
        for (i in lists.indices) {
            if (lists[i].size == 0) {
                lists[i].add("${getText(R.string.onTime_noReserve)}")
            }
        }
    }

    /**
     * 格納中の部屋番号をもとにListViewに予約一覧を表示する
     * param num Int 部屋番号
     * param adapter ArrayAdapter 配列表示用クラス
     */
    private fun setList() {
        val num = idx.toInt()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lists[num])
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        re_list_reserved.adapter = adapter
    }
}