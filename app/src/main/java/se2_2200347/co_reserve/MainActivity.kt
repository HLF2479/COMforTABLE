package se2_2200347.co_reserve

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    private val lockRef = database.getReference("lock")

    private val mySnap = MySnap.getInstance()
    private val firebase = FirebaseReserve()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sp = getSharedPreferences("STU_DATA", Context.MODE_PRIVATE)
        val number = sp.getString("NUM", "")
        var count = 0

        title = getText(R.string.home_tit)

        if (number == "") {
            titleOneWay()
        } else {
            userRef.child("$number").addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mySnap.userSnapshot = snapshot
                    val key = snapshot.child("key").value.toString()    //firebase上のキー値
                    val myKey = sp.getString("KEY", "")         //ログイン時に生成されたキー値
                    //firebaseのキー値が変更され、保存してあるものと一致しなくなった時に強制ログアウトする
                    if (key == myKey) {
                        st_num.text = number
                        st_name.text = snapshot.child("name").value.toString()
                        count = snapshot.child("counter").value.toString().toInt()
                    } else {
                        Toast.makeText(baseContext, R.string.other_log, Toast.LENGTH_SHORT).show()
                        titleOneWay()
                    }
                }

                override fun onCancelled(error: DatabaseError) { Toast.makeText(baseContext, R.string.error_firebase, Toast.LENGTH_SHORT).show() }
            })

            bookRef.orderByChild("user_id").equalTo("$number").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mySnap.myBooking = snapshot
                    getNext()
                }

                override fun onCancelled(error: DatabaseError) { Toast.makeText(baseContext, R.string.error_firebase, Toast.LENGTH_SHORT).show() }
            })

            lockRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) { mySnap.lockSnapshot = snapshot }
                override fun onCancelled(error: DatabaseError) { Log.e("ERROR", error.toString()) }
            })
        }

        btn_reserve.setOnClickListener {
            if (count >= 3) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.er_tit)
                        .setMessage(R.string.error_reserve)
                        .setPositiveButton(R.string.ok) { dialog, which ->  }
                builder.show()
            } else {
                val intent = Intent(this, Reserve::class.java)
                startActivity(intent)
            }
        }
        btn_list.setOnClickListener {
            val intent = Intent(this, List::class.java)
            startActivity(intent)
        }
        btn_entry.setOnClickListener {
            val es = getSharedPreferences("ES", MODE_PRIVATE)
            val switch = es.getInt("SWITCH", -1)
            if (switch != -1) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.error)
                        .setMessage(R.string.error_enter)
                        .setPositiveButton(R.string.ok) { dialog, which -> }
                builder.show()
            } else {
                val intent = Intent(this, Reader::class.java)
                startActivity(intent)
            }
        }
        btn_lock.setOnClickListener {
            val es = getSharedPreferences("ES", MODE_PRIVATE)
            val switch = es.getInt("SWITCH", -1)
            val end = es.getInt("END", -1)
            when {
                switch == -1 -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.error)
                            .setMessage(R.string.error_lock1)
                            .setPositiveButton(R.string.ok) { dialog, which -> }
                    builder.show()
                }
                !firebase.getEnabled(switch, end) -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.error)
                            .setMessage(R.string.error_lock2)
                            .setPositiveButton(R.string.ok){ dialog, which -> }
                            .setOnDismissListener {
                                //登録しておいた予約情報のキー値を取得して、対応する予約情報を削除する
                                val key = es.getString("KEY", "")!!
                                firebase.delete(key)
                                es.edit().clear().apply()
                                getNext()
                            }
                    builder.show()
                }
                else -> {
                    val intent = Intent(this, LockUnlock::class.java)
                    startActivity(intent)
                }
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
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.lo_tit).setMessage(R.string.logout)
                        .setPositiveButton(R.string.yes) { dialog, which -> titleOneWay() }
                        .setNegativeButton(R.string.no) { dialog, which -> }
                builder.show()
                true
            }
            R.id.edit_query -> {
                val intent = Intent(this, Query::class.java)
                startActivity(intent)
                true
            }
            R.id.attention -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.att_tit)
                    .setMessage(R.string.attention)
                    .setPositiveButton(R.string.ok) { dialog, which -> }
                builder.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * 内部ストレージ情報を削除してタイトル画面に遷移させる。同時に裏のアクティビティを全て閉じる
     */
    private fun titleOneWay() {
        getSharedPreferences("STU_DATA", Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("ES", MODE_PRIVATE).edit().clear().apply()
        val intent = Intent(this, Title::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    /**
     * ホーム画面に直近の予約１件を表示させる。入室中や、予約が無いときの表示も行う。
     * param switch 部屋番号
     * param endKey 予約終了時間
     * param reff 予約日と予約開始時間を格納する配列
     */
    private fun getNext() {
        try {
            val es = getSharedPreferences("ES", MODE_PRIVATE)
            val switch = es.getInt("SWITCH", -1)
            val endKey = es.getInt("END", -1)
            val snapshot = mySnap.myBooking
            var reff = arrayListOf<Long>()
            for (i in snapshot.children) {
                val date = i.child("date").value.toString().toLong()
                var convD = date * 10000
                val time = i.child("book_start").value.toString().toLong()
                convD += time
                reff.add(convD)
            }
            reff.sort()
            if (switch != -1 && endKey != -1) {
                time_txt.text = getText(R.string.home_entered)
            } else {
                val resText = Divide(reff[0]).div12()
                time_txt.text = resText[0]
                date_txt.text = resText[1]
            }
            time_txt.textSize = 84F
        } catch (e: Exception) {
            time_txt.text = getText(R.string.no_reserve)
            time_txt.textSize = 40F
            date_txt.text = ""
        }
    }

    override fun onResume() {
        super.onResume()
        getNext()
    }
}