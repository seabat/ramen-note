package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.NoImageRepositoryContract

class CreateNoImageUseCase(
    private val noImageRepository: NoImageRepositoryContract
) : CreateNoImageUseCaseContract {
    override fun create(): ByteArray {
        return noImageRepository.create()
    }
}
