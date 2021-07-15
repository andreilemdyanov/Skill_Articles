package ru.skillbranch.skillarticles.extensions

import java.text.SimpleDateFormat
import java.util.*

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.shortFormat(): String {
    val pattern = if(this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return  dateFormat.format(this)
}

fun Date.isSameDay(date: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = date.time / DAY
    return  day1 == day2
}

fun Date.add(value: Int, units: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time

    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }

    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {

    var dif = this.time - date.time
    val positive = dif >= 0
    //Поправка
    if (positive) dif += 1000L

    val day = dif / DAY
    dif %= DAY
    val hour = dif / HOUR
    dif %= HOUR
    val min = dif / MINUTE
    dif %= MINUTE
    val sec = dif / SECOND
    var result: String
    if (!positive) {
        result =
            if (day < -360) "более года назад"
            else if ((day == -1L && hour < -2) || (-360 < day && day < -2))
                "${TimeUnits.DAY.plural(day.toInt().unaryMinus())} назад"
            else if (hour < -22 || (day == -1L && hour > -2)) "день назад"
            else if ((hour == -1L && min < -15) || (-22 < hour && hour < -1)) {
                "${TimeUnits.HOUR.plural(hour.toInt().unaryMinus())} назад"
            } else if (min < -45 || (hour == -1L && min > -15)) "час назад"
            else if ((min == -1L && sec < -15) || (-45 < min && min < -2)) {
                "${TimeUnits.MINUTE.plural(min.toInt().unaryMinus())} назад"
            } else if (sec < -45 || (min == -1L && sec > -15)) "минуту назад"
            else if (-45 < sec && sec < -1) "несколько секунд назад"
            else "только что"

    } else {
        result =
            if (day > 360) "более чем через год"
            else if ((day == 1L && hour > 2) || day in 2..360)
                "через ${TimeUnits.DAY.plural(day.toInt())}"
            else if (hour > 22 || (day == 1L && hour < 2)) "через день"
            else if ((hour == 1L && min > 15) || hour in 2..22) {
                "через ${TimeUnits.HOUR.plural(hour.toInt())}"
            } else if (min > 45 || (hour == 1L && min < 15)) "через час"
            else if ((min == 1L && sec > 15) || min in 2..45) {
                "через ${TimeUnits.MINUTE.plural(min.toInt())}"
            } else if (sec > 45 || (min == 1L && sec < 15)) "через минуту"
            else if (sec in 1..45) "через несколько секунд"
            else "только что"
    }
    result = result.replace("-", "")
    println(result)
    return result
}

enum class TimeUnits : IUnitsCorrectOut {
    SECOND {
        override fun plural(value: Int): String {
            return if (value % 10 in 2..4) {
                if (value % 100 !in 12..14) "$value секунды"
                else "$value секунд"
            } else if (value % 10 == 1) {
                if (value % 100 != 11) "$value секунду"
                else "$value секунд"
            } else "$value секунд"
        }
    },
    MINUTE {
        override fun plural(value: Int): String {
            return if (value % 10 in 2..4) {
                if (value % 100 !in 12..14) "$value минуты"
                else "$value минут"
            } else if (value % 10 == 1) {
                if (value % 100 != 11) "$value минуту"
                else "$value минут"
            } else "$value минут"
        }
    },
    HOUR {
        override fun plural(value: Int): String {
            return if (value % 10 in 2..4) {
                if (value % 100 !in 12..14) "$value часа"
                else "$value часов"
            } else if (value % 10 == 1) {
                if (value % 100 != 11) "$value час"
                else "$value часов"
            } else "$value часов"

        }
    },
    DAY {
        override fun plural(value: Int): String {
            return if (value % 10 in 2..4) {
                if (value % 100 !in 12..14)
                    "$value дня"
                else "$value дней"
            } else if (value % 10 == 1) {
                if (value % 100 != 11) "$value день"
                else "$value дней"
            } else "$value дней"
        }
    }
}

interface IUnitsCorrectOut {
    fun plural(value: Int): String
}