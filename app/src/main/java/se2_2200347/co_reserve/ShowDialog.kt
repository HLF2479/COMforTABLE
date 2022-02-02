package se2_2200347.co_reserve

import android.content.Context
import android.os.Handler
import androidx.appcompat.app.AlertDialog

/**
 * 対応するDialogを生成して表示するクラスファイル
 */

class ShowDialog(context: Context) {

    private val handler = Handler()
    private val context = context

    /**
     * positiveButtonのみのDialogを生成するメソッド
     * param title Dialogのタイトル
     * param message Dialogに表示する文章
     * param pB positiveButtonに表示する文字
     * param pRun positiveButtonを押した時に行う処理
     */
    fun getP(title: String, message: String, pB: String, pRun: Runnable) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(pB) { _, _ -> handler.post(pRun) }
        builder.show()
    }

    /**
     * positiveButtonとDialogを閉じた時の処理をもったDialogを生成するメソッド
     * param title Dialogのタイトル
     * param message Dialogに表示する文章
     * param pB positiveButtonに表示する文字
     * param pRun positiveButtonを押した時に行う処理
     * param dismiss Dialogを閉じた時に行う処理
     */
    fun getPD(title: String, message: String, pB: String, pRun: Runnable, dismiss: Runnable) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(pB) { _, _ -> handler.post(pRun) }
            .setOnDismissListener { handler.post(dismiss) }
        builder.show()
    }

    /**
     * positiveButtonとnegativeButtonをもったDialogを生成するメソッド
     * param title Dialogのタイトル
     * param message Dialogに表示する文章
     * param pB positiveButtonに表示する文字
     * param pRun positiveButtonを押した時に行う処理
     * param nB negativeButtonに表示する文字
     * param nRun negativeButtonを押した時に行う処理
     */
    fun getPN(title: String, message: String, pB: String, pRun: Runnable, nB:String, nRun: Runnable) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(pB) { _, _ -> handler.post(pRun) }
            .setNegativeButton(nB) { _, _ -> handler.post(nRun) }
        builder.show()
    }

    /**
     * positiveButtonとnegativeButtonとDialogを閉じた時の処理をもったDialogを生成するメソッド
     * param title Dialogのタイトル
     * param message Dialogに表示する文章
     * param pB positiveButtonに表示する文字
     * param pRun positiveButtonを押した時に行う処理
     * param nB negativeButtonに表示する文字
     * param nRun negativeButtonを押した時に行う処理
     * param dismiss Dialogを閉じた時に行う処理
     */
    fun getPND(title: String, message: String, pB: String, pRun: Runnable, nB:String, nRun:Runnable, dismiss: Runnable) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(pB) { _, _ -> handler.post(pRun) }
            .setNegativeButton(nB) { _, _ -> handler.post(nRun) }
            .setOnDismissListener { handler.post(dismiss) }
        builder.show()
    }

}