package se2_2200347.co_reserve

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

private val database = FirebaseDatabase.getInstance()
private val bookRef = database.getReference("booking")
private val userRef = database.getReference("users")

class ListAdapter(context: Context, userList: ArrayList<String>, DateList: ArrayList<Long>, ID: String, count: Int): BaseAdapter(){

    private val context = context
    private val userList = userList
    private val dateList = DateList
    private val userNumber = ID
    private var count = count

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_reserve, null)
        val textView = view.findViewById<TextView>(R.id.reserve_tv)

        val reserve = userList[position]
        textView.text = reserve

        val updater = view.findViewById<TextView>(R.id.update_tv)
        val remover = view.findViewById<TextView>(R.id.remove_tv)

        val date = dateList[position] / 1000000000
        val bookStart = dateList[position] / 100000 % 10000
        val bookEnd = dateList[position] / 10 % 10000
        val room = dateList[position] % 10


        updater.setOnClickListener {
            val year = date / 10000
            val month = date / 100 % 100
            val dayOfMonth = date % 100
            val intent = Intent(context, Reserve_onTime::class.java)
            intent.putExtra("YEAR", year.toString())
            intent.putExtra("MONTH", month.toString())
            intent.putExtra("DATE", dayOfMonth.toString())
            var date = LocalDate.parse("$year-$month-$dayOfMonth")
            val day = date.dayOfWeek
            when (day) {
                DayOfWeek.SUNDAY -> intent.putExtra("DAY", 1)
                DayOfWeek.MONDAY -> intent.putExtra("DAY", 2)
                DayOfWeek.TUESDAY -> intent.putExtra("DAY", 3)
                DayOfWeek.WEDNESDAY -> intent.putExtra("DAY", 4)
                DayOfWeek.THURSDAY -> intent.putExtra("DAY", 5)
                DayOfWeek.FRIDAY -> intent.putExtra("DAY", 6)
                DayOfWeek.SATURDAY -> intent.putExtra("DAY", 7)
            }
            context.startActivity(intent)
            Toast.makeText(context, "${position+1}件目の更新が押されました", Toast.LENGTH_SHORT).show()
        }
        remover.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("予約取消")
                    .setMessage("${reserve}の予約を取り消します。\nよろしいですか？")
                    .setPositiveButton("取消", DialogInterface.OnClickListener { dialog, which ->
                        bookRef.orderByChild("date").equalTo("$date").addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var res = ""
                                for (i in snapshot.children) {
                                    val keyStart = i.child("book_start").value.toString().toLong()
                                    val keyRoom = i.child("room").value.toString().toLong()
                                    if (keyStart == bookStart && keyRoom == room) {
                                        res = i.key.toString()
                                        bookRef.child(res).removeValue()
                                        userRef.child(userNumber).child("counter").setValue("${count - 1}")
                                    }
                                }
                                Toast.makeText(context, "予約を取り消しました", Toast.LENGTH_SHORT).show()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, "通信に失敗しました。", Toast.LENGTH_SHORT).show()
                            }
                        })
                    })
                    .setNegativeButton("戻る", DialogInterface.OnClickListener { dialog, which ->  })
            builder.show()
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