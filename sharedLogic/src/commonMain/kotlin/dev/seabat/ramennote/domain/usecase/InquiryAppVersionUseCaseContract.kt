package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.RunStatus

interface InquiryAppVersionUseCaseContract {
    suspend operator fun invoke(): RunStatus<String>
}
