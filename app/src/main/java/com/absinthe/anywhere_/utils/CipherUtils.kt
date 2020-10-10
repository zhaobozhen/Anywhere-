package com.absinthe.anywhere_.utils

import android.text.TextUtils
import android.util.Base64
import androidx.annotation.Keep
import com.absinthe.anywhere_.utils.manager.IzukoHelper.cipherKey
import timber.log.Timber
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Keep
object CipherUtils {

    private const val CipherMode = "AES/CFB/NoPadding" //Use CFB to encrypt, IV is need
    private val KEY = cipherKey

    private fun generateKey(): SecretKeySpec {
        val data: ByteArray = KEY.toByteArray(StandardCharsets.UTF_8)
        return SecretKeySpec(data, "AES")
    }

    /**
     * Encrypt a string
     *
     * @param data Source string
     * @return Encrypted string
     */
    @JvmStatic //For JNI
    fun encrypt(data: String): String? {
        return if (TextUtils.isEmpty(data)) {
            null
        } else try {
            val cipher = Cipher.getInstance(CipherMode)
            cipher.init(Cipher.ENCRYPT_MODE, generateKey(), IvParameterSpec(ByteArray(cipher.blockSize)))
            val encrypted = cipher.doFinal(data.toByteArray())
            Base64.encodeToString(encrypted, Base64.DEFAULT)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    /**
     * Decrypted a string
     *
     * @param data Encrypted string
     * @return Decrypted string
     */
    @JvmStatic //For JNI
    fun decrypt(data: String): String? {
        return try {
            val encrypted = Base64.decode(data.toByteArray(), Base64.DEFAULT)
            val cipher = Cipher.getInstance(CipherMode)
            cipher.init(Cipher.DECRYPT_MODE, generateKey(), IvParameterSpec(ByteArray(cipher.blockSize)))

            val original = cipher.doFinal(encrypted)
            Timber.d(original.toString())
            String(original, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}