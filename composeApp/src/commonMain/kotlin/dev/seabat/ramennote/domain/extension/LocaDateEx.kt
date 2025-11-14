package dev.seabat.ramennote.domain.extension

import dev.seabat.ramennote.domain.util.createTodayLocalDate
import kotlinx.datetime.LocalDate

fun LocalDate.isFuture(): Boolean = this > createTodayLocalDate()

fun LocalDate.isTodayOrFuture(): Boolean = this >= createTodayLocalDate()
