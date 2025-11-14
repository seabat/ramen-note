package dev.seabat.ramennote.domain.usecase

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class CreateReportPhotoNameUseCase : CreateReportPhotoNameUseCaseContract {
    override fun invoke(): String {
        val now =
            Clock.System.now().toLocalDateTime(
                TimeZone.currentSystemDefault()
            )
        val currentTime = "${now.year}${now.monthNumber.toString()
            .padStart(2, '0')}${now.dayOfMonth.toString()
            .padStart(2, '0')}T${now.hour.toString()
            .padStart(2, '0')}${now.minute.toString()
            .padStart(2, '0')}${now.second.toString()
            .padStart(2, '0')}"
        return "R_$currentTime"
    }
}
