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

    private var admin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sp = getSharedPreferences("STU_DATA", Context.MODE_PRIVATE)
        val number = sp.getString("NUM", "")
        admin = sp.getBoolean("ADMIN", false)
        var count = 0

        title = getText(R.string.home_tit)

        if (number == "") {
            titleOneWay()
        } else {
            userRef.child("$number").addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mySnap.userSnapshot = snapshot      //ユーザー情報をグローバル変数に格納
                    val key = snapshot.child("key").value.toString()    //firebase上のキー値
                    val myKey = sp.getString("KEY", "")         //ログイン時に生成されたキー値
                    //firebaseのキー値が変更され、保存してあるものと一致しなくなった時に強制ログアウトする
                    when {
                        admin -> {
                            st_num.text = "-"
                            st_name.text = snapshot.child("name").value.toString()
                        }
                        key == myKey -> {
                            st_num.text = number
                            st_name.text = snapshot.child("name").value.toString()
                            count = snapshot.child("counter").value.toString().toInt()
                        }
                        else -> {
                            Toast.makeText(baseContext, R.string.other_log, Toast.LENGTH_SHORT).show()
                            titleOneWay()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) { Toast.makeText(baseContext, R.string.error_firebase, Toast.LENGTH_SHORT).show() }
            })

            if (admin) {
                bookRef.orderByChild("user_id").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        mySnap.myBooking = snapshot         //ユーザーの予約情報をグローバル変数に格納
                        time_txt.text = getText(R.string.home_admin)
                        time_txt.textSize = (resources.getDimension(R.dimen.main_next_none) / resources.displayMetrics.density)
                        date_txt.text = ""
                    }

                    override fun onCancelled(error: DatabaseError) { Toast.makeText(baseContext, R.string.error_firebase, Toast.LENGTH_SHORT).show() }
                })
            } else if (!admin) {
                bookRef.orderByChild("user_id").equalTo("$number").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        mySnap.myBooking = snapshot         //ユーザーの予約情報をグローバル変数に格納
                        getNext()
                    }

                    override fun onCancelled(error: DatabaseError) { Toast.makeText(baseContext, R.string.error_firebase, Toast.LENGTH_SHORT).show() }
                })
            }

            lockRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) { mySnap.lockSnapshot = snapshot }
                override fun onCancelled(error: DatabaseError) { Log.e("ERROR", error.toString()) }
            })
        }

        btn_reserve.setOnClickListener {
//            val builder = AlertDialog.Builder(this)
            when {
                admin -> {
                    showDialog("${getText(R.string.error)}", "${getText(R.string.error_adm_reserve)}", "${getText(R.string.ok)}", apply{})
//                    builder.setTitle(R.string.error)
//                            .setMessage(R.string.error_adm_reserve)
//                            .setPositiveButton(R.string.ok) { _, _ -> }
//                    builder.show()
                }
                count >= 3 -> {
                    showDialog("${getText(R.string.er_tit)}", "${getText(R.string.error_reserve)}", "${getText(R.string.ok)}", apply{})
//                    builder.setTitle(R.string.er_tit)
//                            .setMessage(R.string.error_reserve)
//                            .setPositiveButton(R.string.ok) { dialog, which ->  }
//                    builder.show()
                }
                else -> {
                    val intent = Intent(this, Reserve::class.java)
                    startActivity(intent)
                }
            }
        }
        btn_list.setOnClickListener {
            val intent = Intent(this, List::class.java)
            startActivity(intent)
        }
        btn_entry.setOnClickListener {
            val es = getSharedPreferences("ES", MODE_PRIVATE)
            val switch = es.getInt("SWITCH", -1)
            when {
                admin -> {
                    val objects = apply { Log.d("TEST", "表示できるかテスト") }
                    showDialog("${getText(R.string.error)}", "${getText(R.string.error_adm_enter)}", "${getText(R.string.ok)}", objects)
//                    builder.setTitle(R.string.error)
//                            .setMessage(R.string.error_adm_enter)
//                            .setPositiveButton(R.string.ok) { _, _ -> }
//                    builder.show()
                }
                switch != -1 -> {
                    showDialog("${getText(R.string.error)}", "${getText(R.string.error_enter)}", "${getText(R.string.ok)}", apply{})
//                    builder.setTitle(R.string.error)
//                            .setMessage(R.string.error_enter)
//                            .setPositiveButton(R.string.ok) { dialog, which -> }
//                    builder.show()
                }
                else -> {
                    val intent = Intent(this, Reader::class.java)
                    startActivity(intent)
                }
            }
        }
        btn_lock.setOnClickListener {
            val es = getSharedPreferences("ES", MODE_PRIVATE)
            val switch = es.getInt("SWITCH", -1)
            val end = es.getInt("END", -1)
            when {
                admin -> {
                    val intent = Intent(this, LockUnlock::class.java)
                    startActivity(intent)
                }
                switch == -1 -> {
                    showDialog("${getText(R.string.error)}", "${getText(R.string.error_lock1)}", "${getText(R.string.ok)}", apply{})
//                    val builder = AlertDialog.Builder(this)
//                    builder.setTitle(R.string.error)
//                            .setMessage(R.string.error_lock1)
//                            .setPositiveButton(R.string.ok) { dialog, which -> }
//                    builder.show()
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
            val snapshot = mySnap.myBooking     //グローバル変数からユーザーの予約情報を取得
            var reff = arrayListOf<Long>()      //予約情報を格納する配列
            //ユーザー単位の予約情報を表示用文章に整地して配列に格納
            for (i in snapshot.children) {
                val date = i.child("date").value.toString().toLong()        //日付
                var convD = date * 10000
                val time = i.child("book_start").value.toString().toLong()  //予約開始時間
                convD += time
                reff.add(convD)
            }
            reff.sort()
            //現在状態が入室中なら「入室中」を、そうでなければ直近の予約情報を表示する
            if (switch != -1 && endKey != -1) {
                time_txt.text = getText(R.string.home_entered)
            } else {
                val resText = Divide(reff[0]).div12()
                time_txt.text = resText[0]
                date_txt.text = resText[1]
            }
//            time_txt.textSize = 84F
            time_txt.textSize = (resources.getDimension(R.dimen.main_next_kin) / resources.displayMetrics.density)
        } catch (e: Exception) {
            //予約が無い時はエラーを起こすので、専用のメッセージを表示する
            time_txt.text = getText(R.string.no_reserve)
//            time_txt.textSize = 40F
            time_txt.textSize = (resources.getDimension(R.dimen.main_next_none) / resources.displayMetrics.density)
            date_txt.text = ""
        }
    }

    /**
     * positiveButtonのみのDialogを生成するメソッド
     * param title Dialogのタイトル
     * param message Dialogに表示する文章
     * param pos positiveButtonに表示する文字
     * param objects positiveButtonを押した時に行う処理
     */
    private fun showDialog(title: String, message: String, pos: String, objects: Any) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(pos) { _, _ -> objects }
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        if (!admin) getNext()
    }
}