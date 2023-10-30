package com.example.radarsync.data

data class UserSettings(
    var url: String,
    var port: Int,
    var username: String,
    var password: String
) {
    constructor() : this("_", -1, "_", "_")

    fun encode(): ByteArray {
        // Encode the data with placeholders for null values
        return "${url}|${port}|${username}|${password}".toByteArray()
    }

    fun decode(data: ByteArray) {
        val decoded = String(data)
        val split = decoded.split("|")
        url = split[0]
        port = split[1].toInt()
        username = split[2]
        password = split[3]
    }
}
