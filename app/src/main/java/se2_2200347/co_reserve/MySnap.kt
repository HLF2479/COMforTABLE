package se2_2200347.co_reserve

import android.app.Application
import com.google.firebase.database.DataSnapshot

class MySnap : Application(){
    lateinit var bookSnapshot : DataSnapshot
    lateinit var userSnapshot: DataSnapshot
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