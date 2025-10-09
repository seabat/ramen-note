package dev.seabat.ramennote.ui.util

import kotlinx.datetime.LocalDate

fun createFormattedDateString(date: LocalDate): String {
    return buildString {
        append(date.year)
        append("年")
        append(date.monthNumber.toString().padStart(2, '0'))
        append("月")
        append(date.dayOfMonth.toString().padStart(2, '0'))
        append("日")
    }
}