package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.data.datasource.NoImageDataSourceContract

class NoImageRepository(
    private val noImageDataSource: NoImageDataSourceContract
) : NoImageRepositoryContract {
    override fun create(): ByteArray {
        return noImageDataSource.create()
    }
}
