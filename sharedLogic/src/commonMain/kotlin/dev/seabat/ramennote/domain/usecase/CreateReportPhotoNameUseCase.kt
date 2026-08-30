package dev.seabat.ramennote.domain.usecase

import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class CreateReportPhotoNameUseCase : CreateReportPhotoNameUseCaseContract {
    override fun invoke(): String {
        val now =
            Clock.System.now().toLocalDateTime(
                TimeZone.currentSystemDefault()
            )
        val currentTime = "${now.year}${now.month.number.toString()
            .padStart(2, '0')}${now.day.toString()
            .padStart(2, '0')}T${now.hour.toString()
            .padStart(2, '0')}${now.minute.toString()
            .padStart(2, '0')}${now.second.toString()
            .padStart(2, '0')}"
        return "R_$currentTime"
    }
}
