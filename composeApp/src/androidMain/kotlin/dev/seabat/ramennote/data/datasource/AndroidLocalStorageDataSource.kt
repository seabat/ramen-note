package dev.seabat.ramennote.data.datasource

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.FileInputStream

class AndroidLocalStorageDataSource(private val context: Context) : LocalStorageDataSourceContract {
    private val imageFileName = "area_image.png"
    
    override suspend fun save(imageBytes: ByteArray) {
        withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, imageFileName)
                FileOutputStream(file).use { fos ->
                    fos.write(imageBytes)
                }
            } catch (e: Exception) {
                // Handle error silently or log it
            }
        }
    }

    override suspend fun load(): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, imageFileName)
                if (file.exists()) {
                    FileInputStream(file).use { fis ->
                        fis.readBytes()
                    }
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}