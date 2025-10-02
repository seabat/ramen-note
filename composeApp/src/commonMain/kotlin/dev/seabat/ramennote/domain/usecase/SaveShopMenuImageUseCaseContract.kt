package dev.seabat.ramennote.domain.usecase


interface SaveShopMenuImageUseCaseContract {
    suspend operator fun invoke(fileName: String, byteArray: ByteArray)
}
