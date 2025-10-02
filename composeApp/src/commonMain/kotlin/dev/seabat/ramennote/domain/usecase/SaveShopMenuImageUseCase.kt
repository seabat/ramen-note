package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalAreaImageRepositoryContract

class SaveShopMenuImageUseCase(
    private val localAreaImageRepository: LocalAreaImageRepositoryContract
) : SaveShopMenuImageUseCaseContract {
    override suspend operator fun invoke(fileName: String, byteArray: ByteArray) {
        localAreaImageRepository.save(byteArray, fileName)
    }
}
