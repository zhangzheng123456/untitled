package com.bizvane.ishop.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.*;

public class AESUtils {
    static String secret_key = "478bfa82223f31bb46ccfdca1ce22d5f";


    /**
     * 转为二进制
     */
    private static byte[] asBin(String src) {
        if (src.length() < 1)
            return null;
        byte[] encrypted = new byte[src.length() / 2];
        for (int i = 0; i < src.length() / 2; i++) {
            int high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);
            encrypted[i] = (byte) (high * 16 + low);
        }
        return encrypted;
    }

    /**
     * 转为十六进制
     */
    private static String asHex(byte buf[]) {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        int i;
        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10)
                strbuf.append("0");
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }

    /**
     * 对字符串加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String Encrytor(String data) throws Exception{
        byte[] key = asBin(secret_key);
        SecretKeySpec sKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sKey);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return asHex(encrypted);
    }

    /**
     * 对字符串解密
     *
     * @param encData
     * @return
     * @throws Exception
     */
    public static String Decryptor(String encData) throws Exception{
        byte[] tmp = asBin(encData);
        byte[] key = asBin(secret_key);
        SecretKeySpec sKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sKey);
        byte[] decrypted = cipher.doFinal(tmp);
        return new String(decrypted);
    }
}
