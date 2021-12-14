package se2_2200347.co_reserve

/**
 * 入力された指定の桁数にある日付,時刻データを分離して返す
 * param l Long 年月日や部屋番号など、必要な情報を１列に纏めた数値
 */

class Divide(l : Long) {
    private val org = l

    /**
     * 予約開始時間と終了時間を「hh:MM~hh:MM」の形にして返す
     * param hourStart String 予約開始時間(時)
     * param minStart String 予約開始時間(分)
     * param hourEnd String 予約終了時間(時)
     * param minEnd String 予約終了時間(分)
     */
    fun div8() : String {
        val hourStart = format02(org / 1000000)
        val minStart = format02(org / 10000 % 100)
        val hourEnd = format02(org / 100 % 100)
        val minEnd = format02(org % 100)
        return "$hourStart:$minStart~$hourEnd:$minEnd"
    }

    /**
     * 予約日時を「hh:MM~」と「yyyy/mm/dd」の二つにして返す。Home画面用
     * param year Long 年
     * param month String 月
     * param day String 日
     * param hour String 予約開始時間(時)
     * param min String 予約開始時間(分)
     */
    fun div12() : Array<String> {
        val year = org / 100000000
        val month = format02(org / 1000000 % 100)
        val dayOfMonth = format02(org / 10000 % 100)
        val hour = format02(org / 100 % 100)
        val min = format02(org % 100)
        var array = arrayOf(
            "${hour}:${min}～",
            "${year}/${month}/${dayOfMonth}"
        )

        return array
    }

    /**
     * 予約日時の情報をフォーマットして返す。主に予約完了の最終確認画面で使用
     * param year Long 年
     * param month String 月
     * param dayOfMonth String 日
     * param hourStart String 予約開始時間(時)
     * param minStart String 予約開始時間(分)
     * param hourEnd String 予約終了時間(時)
     * param minEnd String 予約終了時間(分)
     */
    fun div16() : String {
        val year = org / 1000000000000
        val month = format02(org / 10000000000 % 100)
        val dayOfMonth = format02(org / 100000000 % 100)
        val hourStart = format02(org / 1000000 % 100)
        val minStart = format02(org / 10000 % 100)
        val hourEnd = format02(org / 100 % 100)
        val minEnd = format02(org % 100)
        return "${year}年${month}月${dayOfMonth}日\n${hourStart}時${minStart}分～${hourEnd}時${minEnd}分"
    }

    /**
     * 予約日時の情報をフォーマットして返す。主に予約一覧の表示用
     */
    fun div17() : String {
        val year = org / 10000000000000
        val month = format02(org / 100000000000 % 100)
        val day = format02(org / 1000000000 % 100)
        val firstHour = format02(org / 10000000 % 100)
        val firstMin = format02(org / 100000 % 100)
        val endHour = format02(org / 1000 % 100)
        val endMin = format02(org / 10 % 100)
        val room = org % 10

        return "${year}年${month}月${day}日\n${firstHour}時${firstMin}分～${endHour}時${endMin}分\nroom${room}"
    }

    /**
     * 数値を２桁の文字列にして返す
     */
    fun format02(l: Long) : String {
        return "%02d".format(l)
    }
}