package se2_2200347.co_reserve

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.view.isInvisible
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_lock_unlock.*
import com.google.firebase.database.FirebaseDatabase

class LockUnlock : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val firebase = FirebaseReserve()
    private val dialog = ShowDialog(this)

    private lateinit var lockRef : DatabaseReference

    private var c = 0
    private val flags = listOf(true, false)
    private val handler = Handler()
    //ロックボタンを押した後、２秒間ボタンを押せなくする処理
    private val btnLock = object : Runnable {
        override fun run() {
            c = 1 - c
            unlock_btn.isEnabled = flags[c]
            lock_btn.isEnabled = flags[c]
            if (c == 1) handler.postDelayed(this, 2000)
        }
    }

    private var admin = false
    private var endKey : Int = 0
    private var roomNumber : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_unlock)

        title = getText(R.string.lock_tit)

        val es = getSharedPreferences("ES", MODE_PRIVATE)
        admin = getSharedPreferences("STU_DATA", MODE_PRIVATE).getBoolean("ADMIN", false)

        if (!admin) {
            endKey = es.getInt("END", -1)
            roomNumber = es.getInt("SWITCH", -1)
            lockRef = database.getReference("lock/$roomNumber/lock")
            lock_room_spi.isInvisible = true
        } else if (admin)
            quit_btn.isEnabled = false

        //利用時間が過ぎている場合に強制的にホーム画面に戻させる処理をするダイアログ
        val dismiss = Runnable {
            //内部ストレージに保存してある予約情報のキー値から、予約情報を削除する
            val key = es.getString("KEY", "")!!
            firebase.delete(key)

            //入室処理に関わる内部ストレージ情報をリセット
            es.edit().clear().apply()

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val posDis = Runnable {
            dialog.getPD(
                "${getText(R.string.error)}", "${getText(R.string.error_lock2)}",
                "${getText(R.string.ok)}", {},
                dismiss)
        }

        unlock_btn.setOnClickListener {
            when {
                admin -> setLock(0)
                firebase.getEnabled(roomNumber, endKey) -> setLock(0)
                else -> handler.post(posDis)
            }
        }

        lock_btn.setOnClickListener {
            when {
                admin -> setLock(1)
                firebase.getEnabled(roomNumber, endKey) -> setLock(1)
                else -> handler.post(posDis)
            }
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
        val texts = arrayOf(R.string.lock_unlocked, R.string.lock_locked)
        Toast.makeText(this, texts[data], Toast.LENGTH_SHORT).show()
        handler.post(btnLock)
        if (admin) lockRef = database.getReference("lock/${lock_room_spi.selectedItemPosition + 1}/lock")
        lockRef.setValue("$data")
    }
}