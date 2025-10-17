package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.NoImageRepositoryContract

class CreateNoImageByteArrayUseCase(
    private val noImageRepository: NoImageRepositoryContract
) : CreateNoImageByteArrayUseCaseContract {
    override operator fun invoke(): ByteArray {
        return noImageRepository.create()
    }
}
