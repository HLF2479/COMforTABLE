package se2_2200347.co_reserve

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast

class ListAdapter(context: Context, userList: ArrayList<String>): BaseAdapter(){
    private val context = context
    private val userList = userList
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_reserve, null)
        val textView = view.findViewById<TextView>(R.id.reserve_tv)

        val reserve = userList[position]
        textView.text = reserve

        val updater = view.findViewById<TextView>(R.id.update_tv)
        val remover = view.findViewById<TextView>(R.id.remove_tv)

        updater.setOnClickListener {
            Toast.makeText(context, "${position+1}件目の更新が押されました", Toast.LENGTH_SHORT).show()
        }
        remover.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("予約取消")
                    .setMessage("${reserve}で登録した予約を取り消します。\nよろしいですか？")
                    .setPositiveButton("取消", DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(context, "予約を取り消しました", Toast.LENGTH_SHORT).show()
                    })
                    .setNegativeButton("戻る", DialogInterface.OnClickListener { dialog, which ->  })
            builder.show()
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return userList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return userList.size
    }
}