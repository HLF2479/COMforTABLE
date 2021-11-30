package se2_2200347.co_reserve

/**
 * 入力された指定の桁数にある日付,時刻データを分離して返す
 */

class Divide(l : Long) {
    private val org = l

    fun div12() : String {
        val year = org / 100000000
        val month = format02(org / 1000000 % 100)
        val day = format02(org / 10000 % 100)
        val hour = format02(org / 100 % 100)
        val min = format02(org % 100)

        return "$year/$month/$day、$hour:${min}から"
    }

    fun div17() : String {
        val year = org / 10000000000000
        val month = format02(org / 100000000000 % 100)
        val day = format02(org / 1000000000 % 100)
        val firstHour = format02(org / 10000000 % 100)
        val firstMin = format02(org / 100000 % 100)
        val endHour = format02(org / 1000 % 100)
        val endMin = format02(org / 10 % 100)
        val room = org % 10

        return "$year/$month/$day $firstHour:$firstMin~$endHour:$endMin\n${room}番部屋"
    }
    private fun format02(l: Long) : String {
        return "%02d".format(l)
    }
}