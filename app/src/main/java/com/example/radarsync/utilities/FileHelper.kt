package com.example.radarsync.utilities

import android.content.Context

class FileHelper {
    companion object {
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
