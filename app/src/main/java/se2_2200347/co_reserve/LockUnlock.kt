package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_lock_unlock.*
import com.google.firebase.database.FirebaseDatabase

class LockUnlock : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val firebase = FirebaseReserve()

    private lateinit var lockRef :DatabaseReference

    private var c = 0
    private val flags = listOf(true, false)
    private val handler = Handler()
    //ロックボタンを押した後、２秒間ボタンを押せなくする処理
    private val btnLock = object : Runnable {
        override fun run() {
            c = 1 - c
            unlock_btn.isEnabled = flags[c]
            lock_btn.isEnabled = flags[c]
            if (c == 1) {
                handler.postDelayed(this, 2000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_unlock)

        title = getText(R.string.lock_tit)

        val es = getSharedPreferences("ES", MODE_PRIVATE)
        val endKey = es.getInt("END", -1)
        val roomNumber = es.getInt("SWITCH", -1)
        lockRef = database.getReference("lock/$roomNumber/lock")

        //利用時間が過ぎている場合に強制的にホーム画面に戻させる処理をするダイアログ
        val builder = AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(R.string.error_lock2)
                .setPositiveButton(R.string.ok) { dialog, which -> }
                .setOnDismissListener {

                    //内部ストレージに保存してある予約情報のキー値から、予約情報を削除する
                    val key = es.getString("KEY", "")!!
                    firebase.delete(key)

                    //入室処理に関わる内部ストレージ情報をリセット
                    es.edit().clear().apply()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }

        unlock_btn.setOnClickListener {
            if (firebase.getEnabled(roomNumber, endKey)) setLock(0)
            else builder.show()
        }
        lock_btn.setOnClickListener {
            if (firebase.getEnabled(roomNumber, endKey)) setLock(1)
            else builder.show()
        }
        quit_btn.setOnClickListener {
            val intent = Intent(this, CheckSheet::class.java)
            intent.putExtra("ROOM", roomNumber)
            startActivity(intent)
        }
    }

    /**
     * 扉のロック情報を書き換えるメソッド
     * param data ０か１
     */
    private fun setLock(data: Int) {
        Toast.makeText(this, R.string.lock_locked, Toast.LENGTH_SHORT).show()
        handler.post(btnLock)
        lockRef.setValue("$data")
    }
}