package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.NoImageRepositoryContract

class CreateNoImageUseCase(
    private val noImageRepository: NoImageRepositoryContract
) : CreateNoImageUseCaseContract {
    override operator fun invoke(): ByteArray {
        return noImageRepository.create()
    }
}
