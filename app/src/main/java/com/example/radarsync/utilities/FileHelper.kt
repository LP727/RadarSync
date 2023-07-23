package com.example.radarsync.utilities

import android.app.Application
import java.io.File

class FileHelper {
    companion object {
        fun saveTextToFile(app: Application, json: String?) {
            val file = File(app.getExternalFilesDir("positions"), "test_positions.json")
            file.writeText(json ?: "", Charsets.UTF_8)
        }

        fun readTextFile(app: Application): String? {
            val file = File(app.getExternalFilesDir("position"), "test_positions.json")
            return if (file.exists()) {
                file.readText()
            } else null
        }
    }
}
