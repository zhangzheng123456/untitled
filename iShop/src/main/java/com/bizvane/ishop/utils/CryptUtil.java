package com.bizvane.ishop.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.security.MessageDigest;

/**
 * <ul>
 * <li>BASE64的加密解密是双向的，可以求反解。</li>
 * <li>MD5、SHA以及HMAC是单向加密，任何数据加密后只会产生唯一的一个加密串，通常用来校验数据在传输过程中是否被修改。</li>
 * <li>HMAC算法有一个密钥，增强了数据传输过程中的安全性，强化了算法外的不可控因素。</li>
 * <li>DES DES-Data Encryption Standard,即数据加密算法。 DES算法的入口参数有三个:Key、Data、Mode。
 * <ul>
 * <li>Key:8个字节共64位,是DES算法的工作密钥;</li>
 * <li>Data:8个字节64位,是要被加密或被解密的数据;</li>
 * <li>Mode:DES的工作方式,有两种:加密或解密。</li>
 * </ul>
 * </li>
 * <ul>
 * 
 */
public class CryptUtil {
	private static final String KEY_MD5 = "MD5";
	/**
	 * MD5 Hash加密
	 *
	 * @param
	 * @return
	 * @throws Exception
	 */
	public static String encryptMD5Hash(String s) throws Exception {

		if (s == null) {
			return null;
		}
		MessageDigest digest;
		StringBuffer hasHexString;

		digest = MessageDigest.getInstance("MD5");
		digest.update(s.getBytes(), 0, s.length());
		byte messageDigest[] = digest.digest();
		hasHexString = new StringBuffer();
		for (int i = 0; i < messageDigest.length; i++) {
			String hex = Integer.toHexString(0xFF & messageDigest[i]);
			if (hex.length() == 1)
				hasHexString.append('0');
			hasHexString.append(hex);
		}
		return hasHexString.toString();

	}

	//图片转化成base64字符串
	public static String GetImageStr()
	{//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		String imgFile = "d://test.jpg";//待处理的图片
		InputStream in = null;
		byte[] data = null;
		//读取图片字节数组
		try
		{
			in = new FileInputStream(imgFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		//对字节数组Base64编码
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);//返回Base64编码过的字节数组字符串
	}



	//base64字符串转化成图片
	public static boolean GenerateImage(String imgStr,String filePath){
		//对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null) //图像数据为空
			return false;
		BASE64Decoder decoder = new BASE64Decoder();
		if (imgStr.startsWith("data:image"))
			imgStr = imgStr.split(",")[0];
		try {
			//Base64解码
			byte[] b = decoder.decodeBuffer(imgStr);
			for(int i=0;i<b.length;++i) {
				if(b[i]<0)
				{//调整异常数据
					b[i]+=256;
				}
			}
			OutputStream out = new FileOutputStream(filePath);
			out.write(b);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
