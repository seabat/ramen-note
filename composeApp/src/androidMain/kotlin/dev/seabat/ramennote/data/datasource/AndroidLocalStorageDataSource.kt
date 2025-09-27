package dev.seabat.ramennote.data.datasource

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.FileInputStream

class AndroidLocalStorageDataSource(private val context: Context) : LocalStorageDataSourceContract {
    
    override suspend fun save(imageBytes: ByteArray, name: String) {
        withContext(Dispatchers.IO) {
            try {
                val fileName = "$name.png"
                val file = File(context.filesDir, fileName)
                FileOutputStream(file).use { fos ->
                    fos.write(imageBytes)
                }
            } catch (e: Exception) {
                // Handle error silently or log it
            }
        }
    }

    override suspend fun load(name: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "$name.png"
                val file = File(context.filesDir, fileName)
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

    override suspend fun rename(oldName: String, newName: String) {
        withContext(Dispatchers.IO) {
            try {
                val oldFileName = "$oldName.png"
                val newFileName = "$newName.png"
                val oldFile = File(context.filesDir, oldFileName)
                val newFile = File(context.filesDir, newFileName)
                
                if (oldFile.exists()) {
                    oldFile.renameTo(newFile)
                }
            } catch (e: Exception) {
                // Handle error silently or log it
            }
        }
    }

    override suspend fun delete(name: String) {
        withContext(Dispatchers.IO) {
            try {
                val fileName = "$name.png"
                val file = File(context.filesDir, fileName)
                
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                // Handle error silently or log it
            }
        }
    }
}