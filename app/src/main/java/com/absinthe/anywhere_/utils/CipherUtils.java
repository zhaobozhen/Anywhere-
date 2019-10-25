package com.absinthe.anywhere_.utils;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtils {

    private static final String CipherMode = "AES/CFB/NoPadding";//使用 CFB 加密，需要设置 IV
    private static final String KEY = "absinthe";

    private static byte[] generateKey() {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(128, new SecureRandom(CipherUtils.KEY.getBytes()));
            SecretKey secretKey = generator.generateKey();
            return secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            LogUtil.e(CipherUtils.class, e.getMessage());
            return null;
        }
    }

    /**
     * 对字符串加密
     *
     * @param data 源字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(CipherMode);
            SecretKeySpec keySpec = new SecretKeySpec(generateKey(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(CipherUtils.class, e.getMessage());
            return null;
        }
    }

    /**
     * 对字符串解密
     *
     * @param data 已被加密的字符串
     * @return 解密得到的字符串
     */
    public static String decrypt(String data) {
        try {
            byte[] encrypted = Base64.decode(data.getBytes(), Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance(CipherMode);
            SecretKeySpec keySpec = new SecretKeySpec(generateKey(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));
            byte[] original = cipher.doFinal(encrypted);
            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}