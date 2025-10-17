package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract

class SaveShopMenuImageUseCase(
    private val localAreaImageRepository: LocalImageRepositoryContract
) : SaveShopMenuImageUseCaseContract {
    override suspend operator fun invoke(fileName: String, byteArray: ByteArray) {
        localAreaImageRepository.save(byteArray, fileName)
    }
}
