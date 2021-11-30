package se2_2200347.co_reserve

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.activity_list.*

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private val database = FirebaseDatabase.getInstance()
private val userref = database.getReference("users")
private val bookref = database.getReference("booking")
private var maxId = -1

class List : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        list_count.setOnClickListener {
            userref.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    maxId = try {
                        snapshot.childrenCount.toInt()
                    } catch (e : Exception) {
                        0
                    }
                    Toast.makeText(baseContext, "IDの最大数は${maxId}です。", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(baseContext, "読み込み失敗", Toast.LENGTH_SHORT).show()
                }
            })
        }

        list_get.setOnClickListener {
            val sp = getSharedPreferences("STU_DATA", Context.MODE_PRIVATE)
            val number = sp.getString("NUM", "")
            bookref.orderByChild("user_id").equalTo("$number").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //ref内の値を抜き取ってリストに表示
                    var array = mutableMapOf<String, Long>()
                    var keies = arrayListOf<String>()
                    for (i in snapshot.children) {
                        var data = i.child("date").value.toString().toLong()
                        var res = data * 10000
                        data = i.child("book_start").value.toString().toLong()
                        res = (res + data) * 10000
                        data = i.child("book_end").value.toString().toLong()
                        res = (res + data) * 10
                        data = i.child("room").value.toString().toLong()
                        res += data
                        array[i.key.toString()] = res
                    }
                    array.sort()
                    var result = arrayListOf<String>()
                    for (element in array) {
                        result.add(Divide(element).div17())
                    }
                    Toast.makeText(baseContext, "${snapshot.childrenCount}件予約しています", Toast.LENGTH_SHORT).show()  //学籍番号を表示(後で消す)
//                    val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, result)
                    val adapter = ListAdapter(this@List, result)
                    ListView.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(baseContext, "読み込み失敗", Toast.LENGTH_SHORT).show()
                }
            })
        }

        list_send.setOnClickListener {
            val User = bookref.child("${(2200121..2209876).shuffled().first()}")
            val users = listOf(
                User.child("name"),
                User.child("id_pass"),
                User.child("counter")
            )
            val datas = listOf(
                "ECC${(1..9999).shuffled().first()}",
                (121212..989898).shuffled().first(),
                (0..3).shuffled().first()
            )
            for (i in users.indices) {
                users[i].setValue(datas[i])
            }
        }
    }
}