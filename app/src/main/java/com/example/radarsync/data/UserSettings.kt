package com.example.radarsync.data

data class UserSettings(
    var url: String?,
    var port: Int?,
    var username: String?,
    var password: String?
) {
    constructor() : this("_", -1, "_", "_")

    fun encode(): ByteArray {
        // Encode the data with placeholders for null values
        return "${url ?: "_"}:${port ?: -1}:${username ?: "_"}:${password ?: "_"}".toByteArray()
    }

    fun decode(data: ByteArray) {
        val decoded = String(data)
        val split = decoded.split(":")
        url = if (split[0] != "_") split[0] else null
        port = if (split[1] != "-1") split[1].toInt() else null
        username = if (split[2] != "_") split[2] else null
        password = if (split[3] != "_") split[3] else null
    }
}
