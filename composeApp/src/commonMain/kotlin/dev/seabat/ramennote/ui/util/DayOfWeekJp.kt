package dev.seabat.ramennote.ui.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber

fun dayOfWeekJp(date: LocalDate): String {
    return when (date.dayOfWeek.isoDayNumber) {
        1 -> "月"
        2 -> "火"
        3 -> "水"
        4 -> "木"
        5 -> "金"
        6 -> "土"
        7 -> "日"
        else -> ""
    }
}