package dev.seabat.ramennote.ui.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

fun createFormattedDateString(date: LocalDate): String =
    buildString {
        append(date.year)
        append("年")
        append(
            date.month.number
                .toString()
                .padStart(2, '0')
        )
        append("月")
        append(date.day.toString().padStart(2, '0'))
        append("日")
    }
