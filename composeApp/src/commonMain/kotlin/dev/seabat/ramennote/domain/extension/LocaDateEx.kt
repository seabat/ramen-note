package dev.seabat.ramennote.domain.extension

import dev.seabat.ramennote.domain.util.createTodayLocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun LocalDate.isFuture(): Boolean {
    return this > createTodayLocalDate()
}

fun LocalDate.isTodayOrFuture(): Boolean {
    return this >= createTodayLocalDate()
}