package com.bizvane.ishop.utils;


import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Encode an MD5 digest into a String. <p>
 * <p>
 * The 128 bit MD5 hash is converted into a 32 character long String. Each
 * character of the String is the hexadecimal representation of 4 bits of the
 * digest.
 */
public class MD5Sum {
    private MessageDigest md;
    private long length;

    /**
     * File that will be checksummed.
     */
    //private File file;
    private InputStream input;
    /**
     * Checksum
     */
    private String checksum;

    /**
     * Hex digits.
     */
    private static final char[] hexadecimal =
            {
                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'a', 'b', 'c', 'd', 'e', 'f'
            };

    public MD5Sum() {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void setInputStream(InputStream stream) {
        this.input = stream;
    }


    /**
     * Set the checksum
     *
     * @param checksum The checksum.
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    /**
     * Get the checksum
     *
     * @return The calculated checksum.
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * Encodes the 128 bit (16 bytes) MD5 into a 32 character String.
     *
     * @param binaryData Array containing the digest
     * @return Encoded MD5, or null if encoding failed
     */
    public String encode(byte[] binaryData) {

        if (binaryData.length != 16) {
            return null;
        }

        char[] buffer = new char[32];

        for (int i = 0; i < 16; i++) {
            int low = (int) (binaryData[i] & 0x0f);
            int high = (int) ((binaryData[i] & 0xf0) >> 4);
            buffer[i * 2] = hexadecimal[high];
            buffer[i * 2 + 1] = hexadecimal[low];
        }

        return new String(buffer);
    }

    /**
     * Pull in static content and store it
     *
     * @param file The file to read.
     * @return The bytes of the file.
     * @throws Exception If an error occurs reading in the file's bytes.
     */
    public byte[] getBytes(File file)
            throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream stream = new FileInputStream(file);

        byte buf[] = new byte[1024];
        int len = 0;

        while ((len = stream.read(buf, 0, 1024)) != -1) {
            baos.write(buf, 0, len);
        }

        return baos.toByteArray();
    }

    /**
     * Perform the MD5-Sum work.
     *
     * @throws Exception If an error occurs while calculating the sum.
     */
    public void execute()
            throws IOException {
        /*if ( file.exists() == false )
        {
            throw new IOException("File "+ file.getAbsolutePath() + " not exists.");
        }*/
        readInputStream();
        setChecksum(encode(md.digest()));
    }

    private void update(byte[] buffer, int offset, int len) {
        md.update(buffer, offset, len);
        length += len;
    }

    private void readInputStream() throws IOException {
        BufferedInputStream bis = null;
        try {

            bis = new BufferedInputStream(input);
            reset();
            int len = 0;
            byte[] buffer = new byte[8192];
            while ((len = bis.read(buffer)) > -1) {
                update(buffer, 0, len);
            }

        } finally {
            if (bis != null) try {
                bis.close();
            } catch (Exception e) {
            }
        }
    }

    private void reset() {
        md.reset();
        length = 0;
    }

    /**
     * Get MD5 Checksum String of file content
     *
     * @param str
     */
    public static String toCheckSumStr(String str) throws IOException {
        MD5Sum sum = new MD5Sum();
        sum.setInputStream(new ByteArrayInputStream(str.getBytes("UTF-8")));
        sum.execute();
        return sum.getChecksum();
    }

    /**
     * Get MD5 Checksum String of file content
     *
     * @param fileStream any stream for file
     */
    public static String toCheckSum(InputStream fileStream) throws IOException {
        MD5Sum sum = new MD5Sum();
        sum.setInputStream(fileStream);
        sum.execute();
        return sum.getChecksum();
    }

    /**
     * Get MD5 Checksum String of file content
     *
     * @param file should be a file name with path, can not be jar entry
     */
    public static String toCheckSum(String file) throws IOException {
        MD5Sum sum = new MD5Sum();
        FileInputStream fileStream = new FileInputStream(file);
        try {
            sum.setInputStream(fileStream);
            sum.execute();
            return sum.getChecksum();
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * MD5加密
     *
     * @param ts     时间戳
     * @param userId ID
     * @param skey   值
     * @return
     */
    public static String getSign(String ts, String userId, String skey) {
        String sign = null;
        try {
            new MD5Sum();
            sign = MD5Sum.toCheckSumStr(userId + ts + skey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sign;
    }

    /**
     * 将明文用MD5算法加密后并压缩成16字节数组
     *
     * @param str 明文
     * @return 16字节数组
     */
    public static byte[] getMD5Byte16(String str) {
        if (str == null || "".equals(str)) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes("UTF-8"));
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取MD5加密后的32位字符串
     *
     * @param str 明文
     * @return 返回MD5加密后的32位串
     */
    public static String getMD5Str32(String str) {
        if (str == null || "".equals(str)) {
            return null;
        }
        return byteToHexString(getMD5Byte16(str));
    }

    /**
     * 把byte[]数组转换成十六进制字符串表示形式
     *
     * @param tmp 要转换的byte[]
     * @return 十六进制字符串表示形式
     */
    public static String byteToHexString(byte[] tmp) {
        String s;
        // 用字节表示就是 16 个字节
        char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
        // 所以表示成 16 进制需要 32 个字符
        int k = 0; // 表示转换结果中对应的字符位置
        for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
            // 转换成 16 进制字符的转换
            byte byte0 = tmp[i]; // 取第 i 个字节
            str[k++] = hexadecimal[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
            // >>> 为逻辑右移，将符号位一起右移
            str[k++] = hexadecimal[byte0 & 0xf]; // 取字节中低 4 位的数字转换
        }
        s = new String(str); // 换后的结果转换为字符串
        return s;
    }
}
