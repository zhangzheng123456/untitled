package com.bizvane.ishop.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.controller.StoreController;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.TaskAllocation;
import com.bizvane.ishop.entity.User;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Created by PC on 2017/1/17.
 */
public class OutHtmlHelper {

    private static final Logger logger = Logger.getLogger(StoreController.class);


    public static String OutHtml(JSONArray array, HttpServletRequest request) throws Exception {
        String result = "";
        FileWriter fw = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String user_id = request.getSession().getAttribute("user_code").toString();

            StringBuilder builder = new StringBuilder();
            builder.append("<!DOCTYPE html><html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>会员相册</title>\n" +
                    "    <style>\n" +
                    "        *{\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "        }\n" +
                    "        body{\n" +
                    "            padding: 20px;\n" +
                    "            background-color: #fcfcfc;\n" +
                    "            color: #888888;\n" +
                    "        }\n" +
                    "        .album_title{\n" +
                    "            text-align: center;\n" +
                    "            font-size: 18px;\n" +
                    "        }\n" +
                    "        .line{\n" +
                    "            margin-top: 20px;\n" +
                    "        }\n" +
                    "        .album_info{\n" +
                    "            font-size: 14px;\n" +
                    "            padding-left: 10px;\n" +
                    "            background-color: #e9e9e9;\n" +
                    "            height: 30px;\n" +
                    "            line-height: 30px;\n" +
                    "            margin-bottom: 10px;\n" +
                    "        }\n" +
                    "        .album_list{\n" +
                    "            overflow: hidden;\n" +
                    "            margin-bottom: -10px;\n" +
                    "        }\n" +
                    "        .album_list ul{\n" +
                    "            margin-right: -10px;\n" +
                    "        }\n" +
                    "        .album_list ul li{\n" +
                    "            float: left;\n" +
                    "            list-style: none;\n" +
                    "            padding-right: 10px;\n" +
                    "            margin-bottom: 10px;\n" +
                    "            box-sizing: border-box;\n" +
                    "            height: 160px;\n" +
                    "        }\n" +
                    "        .album_list ul li div{\n" +
                    "            border: 1px solid #dadada;\n" +
                    "            position: relative;\n" +
                    "            box-sizing: border-box;\n" +
                    "            height: 100%;\n" +
                    "        }\n" +
                    "        .album_list ul li img {\n" +
                    "            padding: 5px;\n" +
                    "            box-sizing: border-box;\n" +
                    "            max-width: 100%;\n" +
                    "            max-height: 100%;\n" +
                    "            position: absolute;\n" +
                    "            left: 50%;\n" +
                    "            top: 50%;\n" +
                    "            -webkit-transform: translate(-50%, -50%);\n" +
                    "            -moz-transform: translate(-50%, -50%);\n" +
                    "            -ms-transform: translate(-50%, -50%);\n" +
                    "            -o-transform: translate(-50%, -50%);\n" +
                    "            transform: translate(-50%, -50%);\n" +
                    "        }\n" +
                    "        @media screen and (max-width: 720px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 33.33%\n" +
                    "               \n" +
                    "            }\n" +
                    "        }\n" +
                    "        @media screen and (min-width: 720px) and (max-width: 840px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 25%\n" +
                    "               \n" +
                    "            }\n" +
                    "        }\n" +
                    "        @media screen and (min-width: 840px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 20%\n" +
                    "               \n" +
                    "            }\n" +
                    "        }\n" +
                    "        @media screen and (min-width: 960px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 16.66%;\n" +
                    "               \n" +
                    "            }\n" +
                    "        }\n" +
                    "        @media screen and (min-width: 1280px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 12.5%;\n" +
                    "            }\n" +
                    "        }\n" +
                    "        .tip{\n" +
                    "            text-align: center;\n" +
                    "            font-size: 12px;\n" +
                    "            height: 20px;\n" +
                    "            color: #aaaaaa;\n" +
                    "            line-height: 30px;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>");
            builder.append("<body>\n" +
                    "    <div class=\"album_title\">会员相册</div>\n" +
                    "    <div class=\"tip\">建议按快捷键 Ctrl+S 将网页保存到本地以便随时查看</div>");

            for (int i = 0; i < array.size(); i++) {
                JSONObject object = JSON.parseObject(array.get(i).toString());
                String vip_name = object.get("vip_name").toString();
                String card_no = object.get("card_no").toString();
                String phone = object.get("phone").toString();
                builder.append("<div class=\"line\">");
                builder.append("<div class=\"album_info\">");
                builder.append("  <span>" + vip_name + "</span>");
                builder.append("  <span >手机:</span><span>" + phone + "</span>");
                builder.append(" <span>卡号:</span><span>" + card_no + "</span>");
                builder.append("</div>");
                builder.append(" <div class=\"album_list\">\n" +
                        "            <ul>");

                String album_obj = object.get("album").toString();
                JSONArray album = new JSONArray();
                if (!album_obj.equals("")) {
                    album = JSONArray.parseArray(album_obj);
                }
                for (int j = 0; j < album.size(); j++) {
                    JSONObject object_album = JSON.parseObject(album.get(j).toString());
                    String image_url = object_album.get("image_url").toString();
                    String time = object_album.get("time").toString();
                    builder.append("  <li class=\"album_parent\">\n" +
                            "                    <div >");
                    builder.append("<img src=\"" + image_url + "\" alt=\"\" title=\"" + time + "\"> ");
                    builder.append(" </div>\n" +
                            "                </li>");
                }
                builder.append("</ul>\n" +
                        "        </div>");
            }
            builder.append("    <div style=\"text-align: right;height: 50px;line-height: 50px\">——数据由<a style=\"cursor: pointer;text-decoration: none;color: #4a5f7c\" href=\"http://www.bizvane.com/\" target=\"_blank\">上海商帆信息科技有限公司</a>提供</div>\n");
            builder.append("</body>");
            builder.append("<script>\n" +
                    "        function setHeight() {\n" +
                    "            var div=document.getElementsByClassName(\"album_parent\");\n" +
                    "            if(div.length==0) return;\n" +
                    "            var W=div[0].offsetWidth;\n" +
                    "            for(var i=0;i<div.length;i++){\n" +
                    "                div[i].style.height=W+\"px\";\n" +
                    "            }\n" +
                    "        }\n" +
                    "        function showTip(){\n" +
                    "            var all_album=document.getElementsByClassName(\"album_list\");\n" +
                    "            if(all_album.length==0) return;\n" +
                    "            for(var a=0;a<all_album.length;a++){\n" +
                    "                if(all_album[a].getElementsByTagName(\"li\").length==0){\n" +
                    "                    all_album[a].innerHTML=\"<div style='text-align: center'>暂无图片</div>\";\n" +
                    "                }\n" +
                    "            }\n" +
                    "            console.log(all_album)\n" +
                    "        }\n" +
                    "        setHeight();\n" +
                    "        window.onresize=function(){\n" +
                    "            setHeight();\n" +
                    "        };\n" +
                    "        window.onload=function(){\n" +
                    "            showTip();\n" +
                    "        }\n" +
                    "</script>");
            builder.append("</html>");
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String filename = corp_code + user_id + "_" + sdf.format(new Date()) + ".html";
            //   filename = URLEncoder.encode(filename, "utf-8");
            String path = request.getSession().getServletContext().getRealPath("api");
            result = filename;
            System.out.println("路径：" + result);
            File file = new File(path, filename);
            fw = new FileWriter(file);
            fw.write(builder.toString());
            fw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                fw.close();
            }
            System.gc();
        }
        return result;
    }

    /**
     * 打包下载店铺二维码
     *
     * @param stores
     * @param request
     * @return
     * @throws Exception
     */
    public static String OutZip_store(List<Store> stores, HttpServletRequest request) throws Exception {
        String result = "";
        String user_id = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String path = request.getSession().getServletContext().getRealPath("api");
        String filename = "店铺二维码" + "_" + corp_code + user_id + "_" + sdf.format(new Date());
        File file_new = new File(path, filename);
        if (!file_new.exists()) {
            file_new.mkdir();
        }
        try {

            for (int i = 0; i < stores.size(); i++) {
                Store store = stores.get(i);
                String store_name = store.getStore_name();
                if (store_name.contains("/")) {
                    store_name = store_name.replace("/", "");
                }
                String store_code = store.getStore_code();
                String album_obj = store.getQrcode();
                String[] album = null;

                if (!album_obj.equals("")) {
                    album = album_obj.split("、");
                }
                if (album != null) {
                    for (int j = 0; j < album.length; j++) {
                        String image_url = album[j];
                        //new一个URL对象
                        URL url = new URL(image_url);
                        //打开链接
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        //设置请求方式为"GET"
                        conn.setRequestMethod("GET");
                        //超时响应时间为5秒
                        conn.setConnectTimeout(5 * 1000);
                        //通过输入流获取图片数据
                        InputStream inStream = conn.getInputStream();
                        //得到图片的二进制数据，以二进制封装得到数据，具有通用性
                        byte[] data = readInputStream(inStream);
                        //new一个文件对象用来保存图片，默认保存当前工程根目录
                        File imageFile = new File(file_new, store_code + "_" + store_name + "_" + j + ".jpg");
                        //创建输出流
                        FileOutputStream outStream = new FileOutputStream(imageFile);
                        //写入数据
                        outStream.write(data);
                        //关闭输出流
                        outStream.close();
                    }
                }

            }
            ZipCompressorByAnt zca = new ZipCompressorByAnt(path, filename + ".zip");
            zca.compressExe(path, filename);


            result = filename + ".zip";
            File srcdir = new File(path, filename + ".zip");
            if (!srcdir.exists()) {
                result = "空文件夹";
            }
            System.out.println("路径：" + result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return result;

    }

    /**
     * logo二维码
     *
     * @param stores
     * @param request
     * @return
     * @throws Exception
     */
    public static String OutZip_store_new(List<Store> stores, String path1, HttpServletRequest request) throws Exception {
        String result = "";
        String user_id = request.getSession().getAttribute("user_code").toString();
        String corp_code_s = request.getSession().getAttribute("corp_code").toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String path = request.getSession().getServletContext().getRealPath("api");
        String filename = "店铺二维码" + "_" + corp_code_s + user_id + "_" + sdf.format(new Date());
        File file_new = new File(path, filename);
//        if (!file_new.exists()) {
//            file_new.mkdir();
//        }

        FileOutputStream fos = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        ZipOutputStream zos = null;
        try {

            //  String corp_code = stores.get(0).getCorp_code();
            String sourceFilePath = "/work1/env/web/ishop/apache-tomcat-8.5.3/webapps/iShop/image/storeQrcode/" + path1;
            File sourceFile = new File(sourceFilePath);
            File zipFile = new File(path, filename + ".zip");
            File[] sourceFiles = sourceFile.listFiles();
            logger.info("===========================sourceFiles.length===========================");
            if (null == sourceFiles || sourceFiles.length < 1) {
                System.out.println("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩.");
                result ="空文件夹";
                // logger.info("===========================sourceFiles.length=============111111111==============");
            } else {
                fos = new FileOutputStream(zipFile);
                zos = new ZipOutputStream(new BufferedOutputStream(fos));
                byte[] bufs = new byte[1024 * 10];
                for (int i = 0; i < sourceFiles.length; i++) {
                    //创建ZIP实体，并添加进压缩包
                    ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());
                    zos.putNextEntry(zipEntry);
                    //读取待压缩的文件并写进压缩包里
                    fis = new FileInputStream(sourceFiles[i]);
                    bis = new BufferedInputStream(fis, 1024 * 10);
                    int read = 0;
                    while ((read = bis.read(bufs, 0, 1024 * 10)) != -1) {
                        zos.write(bufs, 0, read);
                    }
                }
                result = filename + ".zip";
            }
            System.out.println("路径：" + result);
//
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            //关闭流
            try {
                if (null != bis) bis.close();
                if (null != zos) zos.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }


        return result;

    }

    public static String OutZip_user(List<User> users, HttpServletRequest request) throws Exception {
        String result = "";
        String user_id = request.getSession().getAttribute("user_code").toString();
        String corp_code_s = request.getSession().getAttribute("corp_code").toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String path = request.getSession().getServletContext().getRealPath("api");
        String filename = "员工二维码" + "_" + corp_code_s + user_id + "_" + sdf.format(new Date());
        File file_new = new File(path, filename);
        if (!file_new.exists()) {
            file_new.mkdir();
        }
        try {

            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                String user_code = user.getUser_code();
                String user_name = user.getUser_name();
                String store_code = user.getStore_code();
                if(null!=store_code && store_code.length()>15){
                    store_code="店铺编号过长";
                }
                String album_obj = user.getQrcode();
                String[] album = null;

                if (!album_obj.equals("")) {
                    album = album_obj.split("、");
                }
                if (album != null) {
                    for (int j = 0; j < album.length; j++) {
                        String image_url = album[j];
                        //new一个URL对象
                        URL url = new URL(image_url);
                        //打开链接
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        //设置请求方式为"GET"
                        conn.setRequestMethod("GET");
                        //超时响应时间为5秒
                        conn.setConnectTimeout(5 * 1000);
                        //通过输入流获取图片数据
                        InputStream inStream = conn.getInputStream();
                        //得到图片的二进制数据，以二进制封装得到数据，具有通用性
                        byte[] data = readInputStream(inStream);
                        //new一个文件对象用来保存图片，默认保存当前工程根目录
                        File imageFile = new File(file_new, store_code + "_" + user_code + "_" + user_name + "_" + j + ".jpg");
                        //创建输出流
                        FileOutputStream outStream = new FileOutputStream(imageFile);
                        //写入数据
                        outStream.write(data);
                        //关闭输出流
                        outStream.close();
                    }
                }

            }
            ZipCompressorByAnt zca = new ZipCompressorByAnt(path, filename + ".zip");
            zca.compressExe(path, filename);

            result = filename + ".zip";
            File srcdir = new File(path, filename + ".zip");
            if (!srcdir.exists()) {
                result = "空文件夹";
            }
            System.out.println("路径：" + result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return result;

    }

    /**
     * 任务列表详情
     *
     * @param taskAllocations
     * @param request
     * @return
     * @throws Exception
     */
    public static String OutZip_Task_user(List<TaskAllocation> taskAllocations, HttpServletRequest request) throws Exception {
        String result = "";
        String user_id = request.getSession().getAttribute("user_code").toString();
        String store_id = request.getSession().getAttribute("store_code").toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String path = request.getSession().getServletContext().getRealPath("task");
        String filename = "任务凭证" + "_" + store_id + user_id + "_" + sdf.format(new Date());
        File file_new = new File(path, filename);
        if (!file_new.exists()) {
            file_new.mkdir();
        }
        try {

            for (int i = 0; i < taskAllocations.size(); i++) {

                TaskAllocation taskAllocation = taskAllocations.get(i);
                String user_code = taskAllocation.getUser_code();

                String user_name = taskAllocation.getUser_name();
                String store_code = taskAllocation.getStore_code();
                String proof_obj = taskAllocation.getTask_proof_img_pz();
                String[] proof = null;

                if (!proof_obj.equals("")) {
                    if (!proof_obj.contains(",")) {
                        proof_obj = proof_obj + ",";
                    }
                    proof = proof_obj.split(",");
                }
                if (proof != null) {
                    for (int j = 0; j < proof.length; j++) {
                        String image_url = proof[j];
                        //new一个URL对象
                        URL url = new URL(image_url);
                        //打开链接
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        //设置请求方式为"GET"
                        conn.setRequestMethod("GET");
                        //超时响应时间为5秒
                        conn.setConnectTimeout(5 * 1000);
                        //通过输入流获取图片数据
                        InputStream inStream = conn.getInputStream();
                        //得到图片的二进制数据，以二进制封装得到数据，具有通用性
                        byte[] data = readInputStream(inStream);
                        //new一个文件对象用来保存图片，默认保存当前工程根目录
                        File imageFile = new File(file_new, store_code + "_" + user_code + "_" + user_name + "_" + j + ".jpg");
                        //创建输出流
                        FileOutputStream outStream = new FileOutputStream(imageFile);
                        //写入数据
                        outStream.write(data);
                        //关闭输出流
                        outStream.close();
                    }
                }

            }
            ZipCompressorByAnt zca = new ZipCompressorByAnt(path, filename + ".zip");
            zca.compressExe(path, filename);

            result = filename + ".zip";
            File srcdir = new File(path, filename + ".zip");
            if (!srcdir.exists()) {
                result = "空文件夹";
            }
            System.out.println("路径：" + result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return result;

    }

    public static String OutZip_vip(String[] urls, String name, HttpServletRequest request) throws Exception {
        String result = "";
        String user_id = request.getSession().getAttribute("user_code").toString();
        String corp_code_s = request.getSession().getAttribute("corp_code").toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String path = request.getSession().getServletContext().getRealPath("api");
        String path_root = request.getSession().getServletContext().getRealPath("/");
        String filename = name + "_" + corp_code_s + user_id + "_" + sdf.format(new Date());
        File file_new = new File(path, filename);
        if (!file_new.exists()) {
            file_new.mkdir();
        }
        try {
            if (urls.length > 0){
                if (urls[0].startsWith("http")){
                    for (int i = 0; i < urls.length; i++) {
                        String image_url = urls[i];
                        //new一个URL对象
                        URL url = new URL(image_url);
                        //打开链接
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        //设置请求方式为"GET"
                        conn.setRequestMethod("GET");
                        //超时响应时间为5秒
                        conn.setConnectTimeout(5 * 1000);
                        //通过输入流获取图片数据
                        InputStream inStream = conn.getInputStream();
                        //得到图片的二进制数据，以二进制封装得到数据，具有通用性
                        byte[] data = readInputStream(inStream);
                        //new一个文件对象用来保存图片，默认保存当前工程根目录
                        File imageFile = new File(file_new, image_url.replace("http://products-image.oss-cn-hangzhou.aliyuncs.com/exportExcel/","").replace("/","_"));
                        //创建输出流
                        FileOutputStream outStream = new FileOutputStream(imageFile);
                        //写入数据
                        outStream.write(data);
                        //关闭输出流
                        outStream.close();
                    }
                    ZipCompressorByAnt zca = new ZipCompressorByAnt(path, filename + ".zip");
                    zca.compressExe(path, filename);
                }else {
                    ZipCompressorByAnt zca = new ZipCompressorByAnt(path, filename + ".zip");
                    zca.compressExeVip(path_root, urls, path, filename);
                }
            }else {
                ZipCompressorByAnt zca = new ZipCompressorByAnt(path, filename + ".zip");
                zca.compressExeVip(path_root, urls, path, filename);
            }

            result = filename + ".zip";
            File srcdir = new File(path, filename + ".zip");
            if (!srcdir.exists()) {
                result = "空文件夹";
            }
            System.out.println("路径：" + result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return result;

    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    //复制文件内容
    public static void fileCopy(FileInputStream fis, FileOutputStream fos) throws Exception {
        int len = 0;
        byte[] buf = new byte[1024];
        if ((len = fis.read(buf)) != -1) {
            fos.write(buf, 0, len);
        }
    }

    public static void reName(String path, String filename, String store_code, String store_name) {
        File file = new File(path, filename);
        String dirPath = file.getAbsolutePath();//directory
        if (file.isDirectory()) {
            File[] files = file.listFiles();//fileList
            for (File fileFrom : files) {
                String fromFile = fileFrom.getName();//fileName
                String toFileName;
                //      if (fromFile.toLowerCase().endsWith(".jpg")) {
//
                toFileName = dirPath + "\\"
                        + store_code + "_" + store_name + ".jpg";
                File toFile = new File(toFileName);
                if (fileFrom.exists() && !toFile.exists()) {
//reName
                    fileFrom.renameTo(toFile);
                }
                LuploadHelper.deleteFile(path + "/" + filename);
            }
            //  }
        }
    }

    public static String OutHtml_store(List<Store> stores, HttpServletRequest request) throws Exception {
        String result = "";
        FileWriter fw = null;


        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String user_id = request.getSession().getAttribute("user_code").toString();

            StringBuilder builder = new StringBuilder();
            builder.append("<!DOCTYPE html><html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>店铺二维码</title>\n" +
                    "    <style>\n" +
                    "        *{\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "        }\n" +
                    "        body{\n" +
                    "            padding: 20px;\n" +
                    "            background-color: #fcfcfc;\n" +
                    "            color: #888888;\n" +
                    "        }\n" +
                    "        .album_title{\n" +
                    "            text-align: center;\n" +
                    "            font-size: 18px;\n" +
                    "        }\n" +
                    "        .line{\n" +
                    "            margin-top: 20px;\n" +
                    "        }\n" +
                    "        .album_info{\n" +
                    "            font-size: 14px;\n" +
                    "            padding-left: 10px;\n" +
                    "            background-color: #e9e9e9;\n" +
                    "            height: 30px;\n" +
                    "            line-height: 30px;\n" +
                    "            margin-bottom: 10px;\n" +
                    "        }\n" +
                    "        .album_list{\n" +
                    "            overflow: hidden;\n" +
                    "            margin-bottom: -10px;\n" +
                    "        }\n" +
                    "        .album_list ul{\n" +
                    "            margin-right: -10px;\n" +
                    "        }\n" +
                    "        .album_list ul li{\n" +
                    "            float: left;\n" +
                    "            list-style: none;\n" +
                    "            padding-right: 10px;\n" +
                    "            margin-bottom: 10px;\n" +
                    "            box-sizing: border-box;\n" +
                    "            height: 160px;\n" +
                    "        }\n" +
                    "        .album_list ul li div{\n" +
                    "            border: 1px solid #dadada;\n" +
                    "            position: relative;\n" +
                    "            box-sizing: border-box;\n" +
                    "            height: 100%;\n" +
                    "        }\n" +
                    "        .album_list ul li img {\n" +
                    "            padding: 5px;\n" +
                    "            box-sizing: border-box;\n" +
                    "            max-width: 100%;\n" +
                    "            max-height: 100%;\n" +
                    "            position: absolute;\n" +
                    "            left: 50%;\n" +
                    "            top: 50%;\n" +
                    "            -webkit-transform: translate(-50%, -50%);\n" +
                    "            -moz-transform: translate(-50%, -50%);\n" +
                    "            -ms-transform: translate(-50%, -50%);\n" +
                    "            -o-transform: translate(-50%, -50%);\n" +
                    "            transform: translate(-50%, -50%);\n" +
                    "        }\n" +
                    "        @media screen and (max-width: 720px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 33.33%\n" +
                    "               \n" +
                    "            }\n" +
                    "        }\n" +
                    "        @media screen and (min-width: 720px) and (max-width: 840px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 25%\n" +
                    "               \n" +
                    "            }\n" +
                    "        }\n" +
                    "        @media screen and (min-width: 840px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 20%\n" +
                    "               \n" +
                    "            }\n" +
                    "        }\n" +
                    "        @media screen and (min-width: 960px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 16.66%;\n" +
                    "               \n" +
                    "            }\n" +
                    "        }\n" +
                    "        @media screen and (min-width: 1280px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 12.5%;\n" +
                    "            }\n" +
                    "        }\n" +
                    "        .tip{\n" +
                    "            text-align: center;\n" +
                    "            font-size: 12px;\n" +
                    "            height: 20px;\n" +
                    "            color: #aaaaaa;\n" +
                    "            line-height: 30px;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>");
            builder.append("<body>\n" +
                    "    <div class=\"album_title\">店铺二维码</div>\n" +
                    "    <div class=\"tip\">建议按快捷键 Ctrl+S 将网页保存到本地以便随时查看</div>");


            for (int i = 0; i < stores.size(); i++) {
                Store store = stores.get(i);

                String store_name = store.getStore_name();
                String store_code = store.getStore_code();


                builder.append("<div class=\"line\">");
                builder.append("<div class=\"album_info\">");
                builder.append(" <span>店铺编号:</span><span>" + store_code + "</span>");
                builder.append("  <span >店铺名称:</span><span>" + store_name + "</span>");
                builder.append("</div>");
                builder.append(" <div class=\"album_list\">\n" +
                        "            <ul>");

                String album_obj = store.getQrcode();

                String[] album = null;

                if (!album_obj.equals("")) {
                    album = album_obj.split("、");
                }
                if (album != null) {
                    for (int j = 0; j < album.length; j++) {

                        String image_url = album[j];

                        builder.append("  <li class=\"album_parent\">\n" +
                                "                    <div >");
                        builder.append("<img src=\"" + image_url + "\" alt=\"\" title=\"" + image_url + "\"> ");
                        builder.append(" </div>\n" +
                                "                </li>");
                    }
                }
                builder.append("</ul>\n" +
                        "        </div>");
            }
            builder.append("    <div style=\"text-align: right;height: 50px;line-height: 50px\">——数据由<a style=\"cursor: pointer;text-decoration: none;color: #4a5f7c\" href=\"http://www.bizvane.com/\" target=\"_blank\">上海商帆信息科技有限公司</a>提供</div>\n");
            builder.append("</body>");
            builder.append("<script>\n" +
                    "        function setHeight() {\n" +
                    "            var div=document.getElementsByClassName(\"album_parent\");\n" +
                    "            if(div.length==0) return;\n" +
                    "            var W=div[0].offsetWidth;\n" +
                    "            for(var i=0;i<div.length;i++){\n" +
                    "                div[i].style.height=W+\"px\";\n" +
                    "            }\n" +
                    "        }\n" +
                    "        function showTip(){\n" +
                    "            var all_album=document.getElementsByClassName(\"album_list\");\n" +
                    "            if(all_album.length==0) return;\n" +
                    "            for(var a=0;a<all_album.length;a++){\n" +
                    "                if(all_album[a].getElementsByTagName(\"li\").length==0){\n" +
                    "                    all_album[a].innerHTML=\"<div style='text-align: center'>暂无图片</div>\";\n" +
                    "                }\n" +
                    "            }\n" +
                    "            console.log(all_album)\n" +
                    "        }\n" +
                    "        setHeight();\n" +
                    "        window.onresize=function(){\n" +
                    "            setHeight();\n" +
                    "        };\n" +
                    "        window.onload=function(){\n" +
                    "            showTip();\n" +
                    "        }\n" +
                    "</script>");
            builder.append("</html>");

            String filename = user_id + "_" + sdf.format(new Date()) + ".html";
            //   filename = URLEncoder.encode(filename, "utf-8");
            String path = request.getSession().getServletContext().getRealPath("api");
            result = filename;
            System.out.println("路径：" + result);
            File file = new File(path, filename);
            fw = new FileWriter(file);
            fw.write(builder.toString());
            fw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                fw.close();
            }
            System.gc();
        }
        return result;
    }

    public static String OutHtml_store_new(List<Store> stores, HttpServletRequest request) throws Exception {
        String result = "";

        FileWriter fw = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String user_id = request.getSession().getAttribute("user_code").toString();

            StringBuilder builder = new StringBuilder();
            builder.append("<!DOCTYPE html><html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>店铺二维码</title>\n" +
                    "    <style>\n" +
                    "        *{\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "        }\n" +
                    "        body{\n" +
                    "            padding: 20px;\n" +
                    "            background-color: #fcfcfc;\n" +
                    "            color: #888888;\n" +
                    "        }\n" +
                    "        .album_title{\n" +
                    "            text-align: center;\n" +
                    "            font-size: 18px;\n" +
                    "        }\n" +
                    "        .line{\n" +
                    "            margin-top: 20px;\n" +
                    "        }\n" +
                    "        .album_info{\n" +
                    "            font-size: 14px;\n" +
                    "            padding-left: 10px;\n" +
                    "            background-color: #e9e9e9;\n" +
                    "            height: 30px;\n" +
                    "            line-height: 30px;\n" +
                    "            margin-bottom: 10px;\n" +
                    "        }\n" +
                    "        .album_list{\n" +
                    "            overflow: hidden;\n" +
                    "            margin-bottom: -10px;\n" +
                    "        }\n" +
                    "        .album_list ul{\n" +
                    "            margin-right: -10px;\n" +
                    "        }\n" +
                    "        .album_list ul li{\n" +
                    "            float: left;\n" +
                    "            list-style: none;\n" +
                    "            padding-right: 10px;\n" +
                    "            margin-bottom: 10px;\n" +
                    "            box-sizing: border-box;\n" +
                    "            height: 160px;\n" +
                    "        }\n" +
                    "        .album_list ul li div{\n" +
                    "            border: 1px solid #dadada;\n" +
                    "            position: relative;\n" +
                    "            box-sizing: border-box;\n" +
                    "            height: 100%;\n" +
                    "        }\n" +
                    "        .album_list ul li img {\n" +
                    "            padding: 5px;\n" +
                    "            box-sizing: border-box;\n" +
                    "            max-width: 100%;\n" +
                    "            max-height: 100%;\n" +
                    "            position: absolute;\n" +
                    "            left: 50%;\n" +
                    "            top: 50%;\n" +
                    "            -webkit-transform: translate(-50%, -50%);\n" +
                    "            -moz-transform: translate(-50%, -50%);\n" +
                    "            -ms-transform: translate(-50%, -50%);\n" +
                    "            -o-transform: translate(-50%, -50%);\n" +
                    "            transform: translate(-50%, -50%);\n" +
                    "        }\n" +
                    "        @media screen and (max-width: 720px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 33.33%\n" +
                    "               \n" +
                    "            }\n" +
                    "        }\n" +
                    "        @media screen and (min-width: 720px) and (max-width: 840px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 25%\n" +
                    "               \n" +
                    "            }\n" +
                    "        }\n" +
                    "        @media screen and (min-width: 840px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 20%\n" +
                    "               \n" +
                    "            }\n" +
                    "        }\n" +
                    "        @media screen and (min-width: 960px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 16.66%;\n" +
                    "               \n" +
                    "            }\n" +
                    "        }\n" +
                    "        @media screen and (min-width: 1280px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 12.5%;\n" +
                    "            }\n" +
                    "        }\n" +
                    "        .tip{\n" +
                    "            text-align: center;\n" +
                    "            font-size: 12px;\n" +
                    "            height: 20px;\n" +
                    "            color: #aaaaaa;\n" +
                    "            line-height: 30px;\n" +
                    "        }\n" +
                    " .brand_logo{\n" +
                    "            padding: 5px;\n" +
                    "            box-sizing: border-box;\n" +
                    "            width: 30%;\n" +
                    "            border: none!important;\n" +
                    "            position: absolute;\n" +
                    "            left: 50%;\n" +
                    "            top: -50%;\n" +
                    "            -webkit-transform: translate(-50%, -50%);\n" +
                    "            -moz-transform: translate(-50%, -50%);\n" +
                    "            -ms-transform: translate(-50%, -50%);\n" +
                    "            -o-transform: translate(-50%, -50%);\n" +
                    "            transform: translate(-50%, -50%);\n" +
                    "        }" +
                    ".brand_logo img{\n" +
                    "            padding: 4px!important;\n" +
                    "            border: 1px solid #ddd;\n" +
                    "            background-color: #fff;\n" +
                    "            border-radius: 5px;\n" +
                    "        }" +


                    "    </style>\n" +
                    "</head>");
            builder.append("<body>\n" +
                    "    <div class=\"album_title\">店铺二维码</div>\n" +
                    "    <div class=\"tip\">建议按快捷键 Ctrl+S 将网页保存到本地以便随时查看</div>");

            for (int i = 0; i < stores.size(); i++) {
                Store store = stores.get(i);
//
                String store_name = store.getStore_name();
                String store_code = store.getStore_code();
                builder.append("<div class=\"line\">");
                builder.append("<div class=\"album_info\">");
                builder.append(" <span>店铺编号:</span><span>" + store_code + "</span>");
                builder.append("  <span >店铺名称:</span><span>" + store_name + "</span>");
                builder.append("</div>");
                builder.append(" <div class=\"album_list\">\n" +
                        "            <ul>");


                String[] album = null;
                String album_obj = store.getQrcode();
                if (!album_obj.equals("")) {
                    album = album_obj.split("、");
                }

                if (album != null) {
                    for (int j = 0; j < album.length; j++) {
                        String image_url = album[j];
                        builder.append("  <li class=\"album_parent\" style=\"height: 188px;position: relative;\">" +
                                "                    <div >");

                        builder.append("<img src=\"" + image_url + "\" alt=\"\" title=\"" + image_url + "\"> ");
                        builder.append(" </div>\n");

                        // String logo_url = store.getQrcode_logo();
                        String qrcode_info = store.getQrcode_logo();
                        JSONArray arr = JSON.parseArray(qrcode_info);
                        for (int k = 0; k < arr.size(); k++) {
                            JSONObject obj = arr.getJSONObject(k);
                            String image = obj.getString("qrcode");
                            String logo = obj.getString("logo");
                            if (image.equals(album[j])) {
                                if (!logo.equals("")) {
                                    //logo链接截取？之前字符串

                                    if (logo.contains("?")) {
                                        logo = StringUtils.substringBefore(logo, "?");
                                    }
                                    //  logger.info("===============logo_url==========="+logo_url);
                                    builder.append(" <div class=\"brand_logo\"><img src=\"" + logo + "@300h_300w_1e_1c" + "\"></div>\n" +
                                            "               ");
                                }
                            }

                        }

//                        if (null!=logo_url&&!logo_url.equals("")) {
//                            //logo链接截取？之前字符串
//
//                            if(logo_url.contains("?")){
//                                logo_url=  StringUtils.substringBefore(logo_url, "?");
//                            }
//                            //  logger.info("===============logo_url==========="+logo_url);
//                            builder.append(" <div class=\"brand_logo\"><img src=\"" + logo_url + "@300h_300w_1e_1c" + "\"></div>\n" +
//                                    "               ");
//                        }


                        builder.append("                </li>");
                    }
                }

                builder.append("</ul>\n" +
                        "        </div>");
            }
            builder.append("    <div style=\"text-align: right;height: 50px;line-height: 50px\">——数据由<a style=\"cursor: pointer;text-decoration: none;color: #4a5f7c\" href=\"http://www.bizvane.com/\" target=\"_blank\">上海商帆信息科技有限公司</a>提供</div>\n");

            builder.append("</body>");
            builder.append("<script>\n" +
                    "        function setHeight() {\n" +
                    "            var div=document.getElementsByClassName(\"album_parent\");\n" +
                    "            if(div.length==0) return;\n" +
                    "            var W=div[0].offsetWidth;\n" +
                    "            for(var i=0;i<div.length;i++){\n" +
                    "                div[i].style.height=W+\"px\";\n" +
                    "            }\n" +
                    "        }\n" +
                    "        function showTip(){\n" +
                    "            var all_album=document.getElementsByClassName(\"album_list\");\n" +
                    "            if(all_album.length==0) return;\n" +
                    "            for(var a=0;a<all_album.length;a++){\n" +
                    "                if(all_album[a].getElementsByTagName(\"li\").length==0){\n" +
                    "                    all_album[a].innerHTML=\"<div style='text-align: center'>暂无图片</div>\";\n" +
                    "                }\n" +
                    "            }\n" +
                    "            console.log(all_album)\n" +
                    "        }\n" +
                    "        setHeight();\n" +
                    "        window.onresize=function(){\n" +
                    "            setHeight();\n" +
                    "        };\n" +
                    "        window.onload=function(){\n" +
                    "            showTip();\n" +
                    "        }\n" +
                    "</script>");
            builder.append("</html>");

            String filename = user_id + "_" + sdf.format(new Date()) + ".html";
            //   filename = URLEncoder.encode(filename, "utf-8");
            String path = request.getSession().getServletContext().getRealPath("api");
            result = filename;
            System.out.println("路径：" + result);
            File file = new File(path, filename);
            fw = new FileWriter(file);
            fw.write(builder.toString());
            fw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                fw.close();
            }
            System.gc();
        }

        return result;
    }


    public static String OutHtml_user(List<User> users, HttpServletRequest request) throws Exception {
        String result = "";
        FileWriter fw = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String user_id = request.getSession().getAttribute("user_code").toString();

            StringBuilder builder = new StringBuilder();
            builder.append("<!DOCTYPE html><html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>员工二维码</title>\n" +
                    "    <style>\n" +
                    "        *{\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "        }\n" +
                    "        body{\n" +
                    "            padding: 20px;\n" +
                    "            background-color: #fcfcfc;\n" +
                    "            color: #888888;\n" +
                    "        }\n" +
                    "        .album_title{\n" +
                    "            text-align: center;\n" +
                    "            font-size: 18px;\n" +
                    "        }\n" +
                    "        .line{\n" +
                    "            margin-top: 20px;\n" +
                    "        }\n" +
                    "        .album_info{\n" +
                    "            font-size: 14px;\n" +
                    "            padding-left: 10px;\n" +
                    "            background-color: #e9e9e9;\n" +
                    "            height: 30px;\n" +
                    "            line-height: 30px;\n" +
                    "            margin-bottom: 10px;\n" +
                    "        }\n" +
                    "        .album_list{\n" +
                    "            overflow: hidden;\n" +
                    "            margin-bottom: -10px;\n" +
                    "        }\n" +
                    "        .album_list ul{\n" +
                    "            margin-right: -10px;\n" +
                    "        }\n" +
                    "        .album_list ul li{\n" +
                    "            float: left;\n" +
                    "            list-style: none;\n" +
                    "            padding-right: 10px;\n" +
                    "            margin-bottom: 10px;\n" +
                    "            box-sizing: border-box;\n" +
                    "            height: 160px;\n" +
                    "        }\n" +
                    "        .album_list ul li div{\n" +
                    "            border: 1px solid #dadada;\n" +
                    "            position: relative;\n" +
                    "            box-sizing: border-box;\n" +
                    "            height: 100%;\n" +
                    "        }\n" +
                    "        .album_list ul li img {\n" +
                    "            padding: 5px;\n" +
                    "            box-sizing: border-box;\n" +
                    "            max-width: 100%;\n" +
                    "            max-height: 100%;\n" +
                    "            position: absolute;\n" +
                    "            left: 50%;\n" +
                    "            top: 50%;\n" +
                    "            -webkit-transform: translate(-50%, -50%);\n" +
                    "            -moz-transform: translate(-50%, -50%);\n" +
                    "            -ms-transform: translate(-50%, -50%);\n" +
                    "            -o-transform: translate(-50%, -50%);\n" +
                    "            transform: translate(-50%, -50%);\n" +
                    "        }\n" +
                    "        @media screen and (max-width: 720px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 33.33%\n" +
                    "               \n" +
                    "            }\n" +
                    "        }\n" +
                    "        @media screen and (min-width: 720px) and (max-width: 840px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 25%\n" +
                    "               \n" +
                    "            }\n" +
                    "        }\n" +
                    "        @media screen and (min-width: 840px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 20%\n" +
                    "               \n" +
                    "            }\n" +
                    "        }\n" +
                    "        @media screen and (min-width: 960px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 16.66%;\n" +
                    "               \n" +
                    "            }\n" +
                    "        }\n" +
                    "        @media screen and (min-width: 1280px)  {\n" +
                    "            .album_list ul li{\n" +
                    "                width: 12.5%;\n" +
                    "            }\n" +
                    "        }\n" +
                    "        .tip{\n" +
                    "            text-align: center;\n" +
                    "            font-size: 12px;\n" +
                    "            height: 20px;\n" +
                    "            color: #aaaaaa;\n" +
                    "            line-height: 30px;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>");
            builder.append("<body>\n" +
                    "    <div class=\"album_title\">员工二维码</div>\n" +
                    "    <div class=\"tip\">建议按快捷键 Ctrl+S 将网页保存到本地以便随时查看</div>");

            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                String user_code = user.getUser_code();
                String user_name = user.getUser_name();
                String phone = user.getPhone();

                builder.append("<div class=\"line\">");
                builder.append("<div class=\"album_info\">");
                builder.append("  <span>" + user_code + "</span>");
                builder.append("  <span >员工姓名:</span><span>" + user_name + "</span>");
                builder.append(" <span>手机号:</span><span>" + phone + "</span>");
                builder.append("</div>");
                builder.append(" <div class=\"album_list\">\n" +
                        "            <ul>");

                String album_obj = user.getQrcode();
                String[] album = null;

                if (!album_obj.equals("")) {
                    album = album_obj.split("、");
                }
                if (album != null) {
                    for (int j = 0; j < album.length; j++) {

                        String image_url = album[j];

                        builder.append("  <li class=\"album_parent\">\n" +
                                "                    <div >");
                        builder.append("<img src=\"" + image_url + "\" alt=\"\" title=\"" + image_url + "\"> ");
                        builder.append(" </div>\n" +
                                "                </li>");
                    }
                }
                builder.append("</ul>\n" +
                        "        </div>");
            }
            builder.append("    <div style=\"text-align: right;height: 50px;line-height: 50px\">——数据由<a style=\"cursor: pointer;text-decoration: none;color: #4a5f7c\" href=\"http://www.bizvane.com/\" target=\"_blank\">上海商帆信息科技有限公司</a>提供</div>\n");
            builder.append("</body>");
            builder.append("<script>\n" +
                    "        function setHeight() {\n" +
                    "            var div=document.getElementsByClassName(\"album_parent\");\n" +
                    "            if(div.length==0) return;\n" +
                    "            var W=div[0].offsetWidth;\n" +
                    "            for(var i=0;i<div.length;i++){\n" +
                    "                div[i].style.height=W+\"px\";\n" +
                    "            }\n" +
                    "        }\n" +
                    "        function showTip(){\n" +
                    "            var all_album=document.getElementsByClassName(\"album_list\");\n" +
                    "            if(all_album.length==0) return;\n" +
                    "            for(var a=0;a<all_album.length;a++){\n" +
                    "                if(all_album[a].getElementsByTagName(\"li\").length==0){\n" +
                    "                    all_album[a].innerHTML=\"<div style='text-align: center'>暂无图片</div>\";\n" +
                    "                }\n" +
                    "            }\n" +
                    "            console.log(all_album)\n" +
                    "        }\n" +
                    "        setHeight();\n" +
                    "        window.onresize=function(){\n" +
                    "            setHeight();\n" +
                    "        };\n" +
                    "        window.onload=function(){\n" +
                    "            showTip();\n" +
                    "        }\n" +
                    "</script>");
            builder.append("</html>");

            String filename = user_id + "_" + sdf.format(new Date()) + ".html";
            //   filename = URLEncoder.encode(filename, "utf-8");
            String path = request.getSession().getServletContext().getRealPath("api");
            result = filename;
            System.out.println("路径：" + result);
            File file = new File(path, filename);
            fw = new FileWriter(file);
            fw.write(builder.toString());
            fw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                fw.close();
            }
            System.gc();
        }
        return result;
    }
}
