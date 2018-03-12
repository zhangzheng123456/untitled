package com.bizvane.ishop.utils;

/**
 * Created by ZhouZhou on 2017/4/20.
 */

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Random;
import javax.imageio.ImageIO;

import com.bizvane.ishop.entity.BufferedImageLuminanceSource;
import com.google.protobuf.ByteString;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


public class QrcodeUtils {

    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    // 二维码尺寸
//    private static final int QRCODE_SIZE = 300;
    private static final int QRCODE_SIZE = 1000;

    // LOGO宽度
//    private static final int WIDTH = 60;
    private static final int WIDTH = 250;

    // LOGO高度
//    private static final int HEIGHT = 60;
    private static final int HEIGHT = 250;

    private static BufferedImage createImage(String content, String imgPath) throws Exception {
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF);
            }
        }
        if (imgPath == null || "".equals(imgPath)) {
            return image;
        }
        // 插入图片
        insertImage(image, imgPath);
        return image;
    }


    private static void insertImage(BufferedImage source, String imgPath) throws Exception {
        //网络图片地址必须先转成输入量，再转为BufferedImage
        URL url = new URL(imgPath);
        //打开链接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置请求方式为"GET"
        conn.setRequestMethod("GET");
        //超时响应时间为5秒
        conn.setConnectTimeout(5 * 1000);
        //通过输入流获取图片数据
        InputStream inStream = conn.getInputStream();

        BufferedImage src = ImageIO.read(inStream);
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        //  若logo比二维码大，压缩LOGO
        if (width > WIDTH) {
            width = WIDTH;
        }
        if (height > HEIGHT) {
            height = HEIGHT;
        }
        Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = tag.getGraphics();
        g.drawImage(image, 0, 0, null); // 绘制缩小后的图
        g.dispose();
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(image, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }


    //生成带图片的二维码
    public static void encode(String content, String imgPath, String destPath,String file_name) throws Exception {
        BufferedImage image = createImage(content, imgPath);
        mkdirs(destPath);
        //文件名后面加随机数，以免重复
        String file = file_name +"_"+ new Random().nextInt(99999999)+".jpg";
        System.out.print("==============="+image);
        ImageIO.write(image, FORMAT_NAME, new File(destPath+"/"+file));


    }



    public static void mkdirs(String destPath) {
        File file =new File(destPath);
        //当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }


    //二维码地址解码
    public static String decode(String path) throws Exception {
        File file = new File(path);
        BufferedImage image;
        image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(
                image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        Hashtable hints = new Hashtable();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        String resultStr = result.getText();
        return resultStr;
    }




    public static void
    main(String[] args) throws Exception {

        String text = "http://weixin.qq.com/q/02DyKVpZXPd-010000g07a";
        String path=+System.currentTimeMillis()+"阿吉豆lallala.jpg";
        encode(text, "", "D://","阿吉豆1");


        System.out.print("==========path========"+path);


    }

}
