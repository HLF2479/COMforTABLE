package se2_2200347.co_reserve

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.activity_list.*

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_reserve.view.*

private val database = FirebaseDatabase.getInstance()
private val bookRef = database.getReference("booking")

class List : AppCompatActivity() {

    //ListViewに表示する予約情報を格納する変数
    private var array = arrayListOf<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        title = getText(R.string.list_t)

        val count = intent.getIntExtra("COUNT", 0)
        val number = getSharedPreferences("STU_DATA", MODE_PRIVATE).getString("NUM", "")
        val myBook = bookRef.orderByChild("user_id").equalTo("$number")

        myBook.addValueEventListener(object : ValueEventListener {
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
//                val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, result)
                val adapter = number?.let { ListAdapter(this@List, result, array, it, count) }
                ListView.adapter = adapter
            }

            override fun onCancelled(e: DatabaseError) {
                Log.e("ERROR", e.toString())
            }
        })
    }
}