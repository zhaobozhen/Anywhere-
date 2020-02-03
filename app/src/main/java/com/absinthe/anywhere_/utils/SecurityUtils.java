package com.absinthe.anywhere_.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {

    /**
     * Get application signature
     *
     * @param context context
     * @return hashcode of signature
     */
    @SuppressLint("PackageManagerGetSignatures")
    public static int getSignature(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi;
        StringBuilder sb = new StringBuilder();

        try {
            pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signatures = pi.signatures;
            for (Signature signature : signatures) {
                sb.append(signature.toCharsString());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return sb.toString().hashCode();
    }

    /**
     * MD5 encryption
     *
     * @param byteStr Source string
     * @return MD5 value of the string
     */
    public static String MD5Encryption(byte[] byteStr) {
        MessageDigest messageDigest;
        StringBuilder md5StrBuff = new StringBuilder();

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
            for (byte b : byteArray) {
                if (Integer.toHexString(0xFF & b).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & b));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & b));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }

    public static String md5AndBase64Encode(String message) {
        String result = "";
        try {
            /**指定信息摘要算法为MD5，并通过MD5的哈希计算获取源数据的摘要
             * 注意事项：
             * 1、md5Byte不是普通的数组，而是源数据的摘要，摘要只是源数据局部，所以想要返回去，是不行的
             * 2、摘要好比指纹，每个人都是唯一的，相同的数据源，摘要也一样，不同的数据，摘要则不一样*/
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] md5Byte = md5.digest(message.getBytes(StandardCharsets.UTF_8));
            /**BASE64将二进制数据转为可见字符
             * 注意事项：
             * 1、可见字符是不含汉字的，很多时候，不方便显示汉字的时候，就可以用Base64编码转成可见字符
             * 2、因为MD5的摘要长度是固定的，所以转成BASE64后，字符长度仍然是固定的，无论源数据是多么大，都不会影响*/
            result = Base64.encodeToString(md5Byte, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get MD5 value of signature
     *
     * @param context context
     * @return MD5 value of signature
     */
    public static String getSignatureMD5Value(Context context) {
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            return MD5Encryption(sign.toByteArray());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
