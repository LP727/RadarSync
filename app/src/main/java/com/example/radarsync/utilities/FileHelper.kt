package com.example.radarsync.utilities

import android.content.Context
import android.util.Log
import com.example.radarsync.LOG_TAG
import com.example.radarsync.data.PositionEntity
import com.example.radarsync.data.UserSettings
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import java.io.FileOutputStream

class FileHelper {
    companion object {
        // Get the user settings from encrypted file, returns default settings if file doesn't exist
        private val listType = Types.newParameterizedType(
            List::class.java, PositionEntity::class.java // custom type for JSON parsing
        )
        fun loadUserSettings(context: Context, cryptoManager: CryptoManager): UserSettings{
            val directoryPath = context.getExternalFilesDir("settings")?.absolutePath + File.separator + "radarDirectory"
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
            // Caching the user settings in an encrypted file
            val directoryPath = context.getExternalFilesDir("settings")?.absolutePath + File.separator + "radarDirectory"
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

        fun parseText(text: String): MutableList<PositionEntity>
        {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val adapter: JsonAdapter<MutableList<PositionEntity>> = moshi.adapter(listType)
            val positionData = adapter.fromJson(text)

            for (position in positionData ?: emptyList()) {
                Log.i(
                    LOG_TAG,
                    "${position.name}, time: ${position.time}"
                )
            }
            return positionData ?: mutableListOf<PositionEntity>()
        }
    }
}
