package com.example.radarsync.utilities

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class CryptoManager {

    private val ks = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null) // apply is a scope function, it allows to call multiple methods on the same object
    }

    private val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
        init(Cipher.ENCRYPT_MODE, ks.getKey(KEY_ALIAS, null))
    }

    // Function used for the decryption cipher because it needs an IV (initialisation vector)
    private fun getDecryptCipher(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(KEY_ALIAS), IvParameterSpec(iv))
        }
    }

    // Get Key function: Looks for our key in the keystore, if it doesn't exist, it creates it
    private fun getKey(alias: String): SecretKey {
       val existingKey = ks.getEntry(alias, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey(alias)
    }

    // Key creation function, should get called if we don't already have a key in our store
    private fun createKey(alias: String): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM, "AndroidKeyStore").apply {
            init(
                KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                ).apply {
                    setBlockModes(BLOCK_MODE)
                    setEncryptionPaddings(PADDING)
                    setUserAuthenticationRequired(false)
                    setRandomizedEncryptionRequired(true)
                }.build()
            )
        }.generateKey()
    }

    fun encrypt(bytes: ByteArray, outputStream: OutputStream): ByteArray {
        val encryptedBytes = encryptCipher.doFinal(bytes)
        // Use ensure the stream will be closed after the use, write would have worked here too
        outputStream.use {
            it.write(encryptCipher.iv.size) // size to read to fetch iv
            it.write(encryptCipher.iv) // actual iv
            it.write(encryptedBytes.size) // size to read to fetch encrypted data
            it.write(encryptedBytes) // actual encrypted data
        }
        return encryptedBytes
    }

    fun decrypt(inputStream: InputStream): ByteArray {
        return inputStream.use {
            // Recover the IV
            val ivSize = it.read()
            val iv = ByteArray(ivSize)
            it.read(iv)

            // Recover the encrypted data
            val encryptedDataSize = it.read()
            val encryptedData = ByteArray(encryptedDataSize)
            it.read(encryptedData)

            getDecryptCipher(iv).doFinal(encryptedData)
        }
    }

    // The companion object holds our encryption parameters
    companion object {
        private const val KEY_ALIAS = "SyncKey"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES // AES is a common encryption algorithm
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC // CBC is a common block mode
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7 // PKCS7 is a common padding scheme
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING" // This is the transformation that will be used to encrypt and decrypt the data
    }
}
