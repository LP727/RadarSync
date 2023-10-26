package com.example.radarsync.utilities

import android.content.Context
import android.util.Log
import com.example.radarsync.LOG_TAG
import com.example.radarsync.data.UserSettings
import java.io.File
import java.io.FileOutputStream

class FileHelper {
    companion object {
        // Get the user settings from encrypted file, returns default settings if file doesn't exist
        fun loadUserSettings(context: Context, cryptoManager: CryptoManager): UserSettings{
            val directoryPath = context.getExternalFilesDir(null)?.absolutePath + File.separator + "radarDirectory"
            val fileName = "radarSettings.txt"
            val file = File(directoryPath, fileName)
            val settings = UserSettings()

            if(file.exists()) {
                val fis = file.inputStream()
                settings.decode(cryptoManager.decrypt(fis))
            }
            return settings
        }

        fun saveUserSettings(context: Context, cryptoManager: CryptoManager, settings: UserSettings) {
            val directoryPath = context.getExternalFilesDir(null)?.absolutePath + File.separator + "radarDirectory"
            val fileName = "radarSettings.txt"

            Log.i(
                LOG_TAG,
                "Directory : $directoryPath"
            )
            // Create the directory if it doesn't exist

            val directory = File(directoryPath)
            if (!directory.exists()) {
                directory.mkdir()
            }

            Log.i(
                LOG_TAG,
                "File : ${directoryPath + File.separator + fileName}}"
            )

            val file = File(directoryPath, fileName)
            if(!file.exists()) {
                file.createNewFile()
            }

            val fos = FileOutputStream(file)

            cryptoManager.encrypt(
                bytes = settings.encode(),
                outputStream = fos
            )
        }

        fun getTextFromAssets(context: Context, fileName: String): String
        {
            return context.assets.open(fileName).use {
                it.bufferedReader().use {
                    it.readText()
                }
            }
        }
    }
}
