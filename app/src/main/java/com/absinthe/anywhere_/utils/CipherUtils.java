package com.absinthe.anywhere_.utils;

import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.Keep;

import com.absinthe.anywhere_.utils.manager.IzukoHelper;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import timber.log.Timber;

@Keep
public class CipherUtils {

    private static final String CipherMode = "AES/CFB/NoPadding";   //Use CFB to encrypt, IV is need
    private static final String KEY = IzukoHelper.getCipherKey();

    private static SecretKeySpec generateKey() {
        byte[] data;

        data = KEY.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(data, "AES");
    }

    /**
     * Encrypt a string
     *
     * @param data Source string
     * @return Encrypted string
     */
    public static String encrypt(String data) {
        if (TextUtils.isEmpty(data)) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.ENCRYPT_MODE, generateKey(), new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            Timber.e(e);
            return null;
        }
    }

    /**
     * Decrypted a string
     *
     * @param data Encrypted string
     * @return Decrypted string
     */
    public static String decrypt(String data) {
        try {
            byte[] encrypted = Base64.decode(data.getBytes(), Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance(CipherMode);
            cipher.init(Cipher.DECRYPT_MODE, generateKey(), new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));
            byte[] original = cipher.doFinal(encrypted);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}