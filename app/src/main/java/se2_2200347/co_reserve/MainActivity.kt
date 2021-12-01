package se2_2200347.co_reserve

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val userRef = database.getReference("users")
    private val bookRef = database.getReference("booking")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sp = getSharedPreferences("STU_DATA", Context.MODE_PRIVATE)
        val number = sp.getString("NUM", "")

        if (number == "") {
            TitleToOneWay()
        } else {
            st_num.text = number
            userRef.child("$number/name").addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    st_name.text = snapshot.value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(baseContext, "名前の読み込みに失敗しました。", Toast.LENGTH_SHORT).show()
                }
            })

            bookRef.orderByChild("user_id").equalTo("$number")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var reff = arrayListOf<Long>()
                            try {
                                for (i in snapshot.children) {
                                    val date = i.child("date").value.toString().toLong()
                                    var convD = date * 10000
                                    val time = i.child("book_start").value.toString().toLong()
                                    convD += time
                                    reff.add(convD)
                                }
                                reff.sort()
                                val resText = Divide(reff[0]).div12()
                                time_txt.text = resText[0]
                                time_txt.textSize = 84F
                                date_txt.text = resText[1]
                            } catch (e: Exception) {
                                time_txt.text = "予約がありません"
                                time_txt.textSize = 40F
                                date_txt.text = ""
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(baseContext, "予約日の読み込みに失敗しました", Toast.LENGTH_SHORT).show()
                        }
                    })
        }

        btn_reserve.setOnClickListener {
            val intent = Intent(this, Reserve::class.java)
            startActivity(intent)
        }
        btn_list.setOnClickListener {
            val intent = Intent(this, List::class.java)
            startActivity(intent)
        }
        btn_entry.setOnClickListener {
            val sp = getSharedPreferences("ES", MODE_PRIVATE)
            val flag = sp.getInt("SWITCH", -1)
            if (flag != -1) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("エラー")
                        .setMessage("既に入室中です。")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which -> })
                builder.show()
            } else {
                val intent = Intent(this, Reader::class.java)
                startActivity(intent)
            }
        }
        btn_lock.setOnClickListener {
            val sp = getSharedPreferences("ES", MODE_PRIVATE)
            val flag = sp.getInt("SWITCH", -1)
            if (flag == -1) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("エラー")
                        .setMessage("部屋に入室していません。\nロックボタンは、入室の処理が完了すると使用可能になります。")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which -> })
                builder.show()
            } else {
                val intent = Intent(this, LockUnlock::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        return when (item.itemId) {
            R.id.logout -> {
                val sp = getSharedPreferences("STU_DATA", Context.MODE_PRIVATE)
                val editor = sp.edit()
                editor.clear().apply()
                TitleToOneWay()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun TitleToOneWay() {
        val intent = Intent(this, Title::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}