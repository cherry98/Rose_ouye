package com.orange.oy.encryption;


import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AESCrypt.java
 *
 * @author dushan
 */
public class AESCrypt {
    private static final String IV_STRING = "A-16-Byte-String";
    private static final String charset = "UTF-8";

    /**
     * AES加密
     *
     * @param content 加密内容
     * @param key     密钥
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    public static String aesEncryptString(String content, String key) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException {
        byte[] contentBytes = content.getBytes(charset);
        byte[] keyBytes = key.getBytes(charset);
        byte[] encryptedBytes = aesEncryptBytes(contentBytes, keyBytes);
        /*Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(encryptedBytes);*/
        return android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT);
//        return android.util.Base64.encodeToString(encryptedBytes);
    }

    /**
     * AES解密
     *
     * @param content 解密内容
     * @param key     密钥
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    public static String aesDecryptString(String content, String key) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
            UnsupportedEncodingException {
//		Decoder decoder = Base64.getDecoder();
//        byte[] encryptedBytes = Base64.decodeBase64(content);
        byte[] encryptedBytes = android.util.Base64.decode(content, android.util.Base64.DEFAULT);
        byte[] keyBytes = key.getBytes(charset);
        byte[] decryptedBytes = aesDecryptBytes(encryptedBytes, keyBytes);
        return new String(decryptedBytes, charset);
    }

    public static byte[] aesEncryptBytes(byte[] contentBytes, byte[] keyBytes) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {
        return cipherOperation(contentBytes, keyBytes, Cipher.ENCRYPT_MODE);
    }

    public static byte[] aesDecryptBytes(byte[] contentBytes, byte[] keyBytes) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {
        return cipherOperation(contentBytes, keyBytes, Cipher.DECRYPT_MODE);
    }

    private static byte[] cipherOperation(byte[] contentBytes, byte[] keyBytes, int mode) throws UnsupportedEncodingException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        byte[] initParam = IV_STRING.getBytes(charset);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, secretKey, ivParameterSpec);

        return cipher.doFinal(contentBytes);
    }

    /**
     * 生成16位随机字符
     *
     * @return
     */
    public static String getRandom16() {
        String value = "";
        String AESstring = "abcdefghijklmnopqrstuvwxyz1234567890!@#$%^&*()";
        int stringlength = AESstring.length();
        int index = 0;
        for (int i = 0; i < 16; i++) {
            index = (int) Math.round(Math.random() * stringlength);
            if (index >= stringlength || index < 0) {
                index = 0;
            }
            value += AESstring.charAt(index);
        }
        return value;
    }

//    /**
//     * 生成AES密钥
//     *
//     * @return
//     * @throws NoSuchAlgorithmException
//     */
//    public SecretKey getAESkey() throws NoSuchAlgorithmException {
//        KeyGenerator keygen = KeyGenerator.getInstance("AES");
//        //2.根据ecnodeRules规则初始化密钥生成器
//        //生成一个128位的随机源,根据传入的字节数组，使用时间戳
//        keygen.init(128, new SecureRandom(getRandom16().getBytes()));
//        //3.产生原始对称密钥
//        SecretKey original_key = keygen.generateKey();
//        //4.获得原始对称密钥的字节数组
//        byte[] raw = original_key.getEncoded();
//        //5.根据字节数组生成AES密钥
//        return new SecretKeySpec(raw, "AES");
//    }
//
//    /**
//     * 使用AES加密
//     *
//     * @param content 加密的内容
//     * @param key     生成的随机密钥
//     * @return
//     */
//    public String AESEncode(String content, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException,
//            InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
//        Cipher cipher = Cipher.getInstance("AES");
//        //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
//        cipher.init(Cipher.ENCRYPT_MODE, key);
//        //8.获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
//        byte[] byte_encode = content.getBytes("utf-8");
//        //9.根据密码器的初始化方式--加密：将数据加密
//        byte[] byte_AES = cipher.doFinal(byte_encode);
//        return android.util.Base64.encodeToString(byte_AES);
//    }
}