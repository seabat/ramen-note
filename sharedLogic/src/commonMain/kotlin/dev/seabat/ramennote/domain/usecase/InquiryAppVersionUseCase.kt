package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AppVersionRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class InquiryAppVersionUseCase(
    private val appVersionRepository: AppVersionRepositoryContract
) : InquiryAppVersionUseCaseContract {
    override suspend operator fun invoke(): RunStatus<String> =
        try {
            val versionName = appVersionRepository.getVersionName()
            RunStatus.Success(versionName)
        } catch (e: Exception) {
            RunStatus.Error(e.message ?: "Failed to get version name")
        }
}
