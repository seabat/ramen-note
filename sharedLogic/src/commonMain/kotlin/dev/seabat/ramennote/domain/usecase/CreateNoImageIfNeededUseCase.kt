package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.NoImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

const val REPORT_NO_IMAGE_FILE_NAME = "R_NO_IMAGE"

class CreateNoImageIfNeededUseCase(
    private val localImageRepository: LocalImageRepositoryContract,
    private val noImageRepository: NoImageRepositoryContract
) : CreateNoImageIfNeededUseCaseContract {
    override suspend operator fun invoke(fileName: String) {
        val result = localImageRepository.load(fileName)
        if (result is RunStatus.Error) {
            val byteArray = noImageRepository.create()
            localImageRepository.save(byteArray, fileName)
        }
    }
}
