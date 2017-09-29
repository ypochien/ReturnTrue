/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.returntrue.util.encrypt;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author JosephWang
 */
public class AeSimpleSHA1
{

    public static String encryptAES(String iv, String key, String text)
    {
        try
        {
            AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
            SecretKeySpec mSecretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            mCipher.init(Cipher.ENCRYPT_MODE, mSecretKeySpec, mAlgorithmParameterSpec);
            return Base64.encodeToString(mCipher.doFinal(text.getBytes("UTF-8")), Base64.NO_WRAP);
        }
        catch (Exception ex)
        {
            return "";
        }
    }

    public static String decryptAES(String iv, String key, String text)
    {
        try
        {
            AlgorithmParameterSpec mAlgorithmParameterSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
            SecretKeySpec mSecretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, mAlgorithmParameterSpec);
            return Base64.encodeToString(mCipher.doFinal(text.getBytes("UTF-8")), Base64.NO_WRAP);
        }
        catch (Exception ex)
        {
            return "";
        }
    }

    private static String convertToHex(byte[] data)
    {
        StringBuilder buf = new StringBuilder();
        for (byte b : data)
        {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do
            {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }
}
