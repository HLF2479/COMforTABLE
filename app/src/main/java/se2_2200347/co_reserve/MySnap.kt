package se2_2200347.co_reserve

import android.app.Application
import com.google.firebase.database.DataSnapshot

/**
 * グローバル変数
 * 参照元：https://qiita.com/Ritz/items/0f6b34e0fcf6d3cd1ce5
 */
class MySnap : Application(){
    lateinit var bookSnapshot : DataSnapshot
    lateinit var userSnapshot: DataSnapshot
    lateinit var lockSnapshot: DataSnapshot
    lateinit var myBooking: DataSnapshot

    companion object {
        private var instance : MySnap? = null

        fun getInstance() : MySnap {
            if (instance == null)
                instance = MySnap()

            return instance!!
        }
    }
}