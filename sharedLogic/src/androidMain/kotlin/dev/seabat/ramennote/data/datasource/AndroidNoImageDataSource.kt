package dev.seabat.ramennote.data.datasource

import android.content.Context
import dev.seabat.ramennote.data.datasource.NoImageDataSourceContract

class AndroidNoImageDataSource(
    private val context: Context
) : NoImageDataSourceContract {
    override fun create(): ByteArray =
        context.resources
            .openRawResource(
                context.resources.getIdentifier("noimage", "raw", context.packageName)
            ).use { inputStream ->
                inputStream.readBytes()
            }
}
