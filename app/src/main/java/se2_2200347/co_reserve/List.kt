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
private val bookref = database.getReference("booking")

class List : AppCompatActivity() {

    private var array = arrayListOf<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val sp = getSharedPreferences("STU_DATA", Context.MODE_PRIVATE)
        val number = sp.getString("NUM", "")
        bookref.orderByChild("user_id").equalTo("$number").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //ref内の値を抜き取ってリストに表示
                array.clear()
                for (i in snapshot.children) {
                    var data = i.child("date").value.toString().toLong()
                    var res = data * 10000
                    data = i.child("book_start").value.toString().toLong()
                    res = (res + data) * 10000
                    data = i.child("book_end").value.toString().toLong()
                    res = (res + data) * 10
                    data = i.child("room").value.toString().toLong()
                    res += data
                    array.add(res)
                }
                array.sort()
                var result = arrayListOf<String>()
                for (element in array) {
                    result.add(Divide(element).div17())
                }
//                    val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, result)
                val adapter = ListAdapter(this@List, result, array)
                ListView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "読み込み失敗", Toast.LENGTH_SHORT).show()
            }
        })
    }
}