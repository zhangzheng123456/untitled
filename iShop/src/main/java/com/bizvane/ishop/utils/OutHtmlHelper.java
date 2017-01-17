package com.bizvane.ishop.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by PC on 2017/1/17.
 */
public class OutHtmlHelper {
    public static String OutHtml(JSONArray array,HttpServletRequest request) throws Exception {
        String result="";
        FileWriter fw=null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String user_id = request.getSession().getAttribute("user_code").toString();

            StringBuilder builder = new StringBuilder();
            builder.append("<html lang=\"en\">\n" +
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
                    "            height: 160px;\n"+
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
                    "        }" +
                    "         .tip{\n" +
                    "            text-align: center;\n" +
                    "            font-size: 12px;\n" +
                    "            height: 20px;\n" +
                    "            color: #aaaaaa;\n" +
                    "            line-height: 30px;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>");
            builder.append("<body>\n" +
                    "    <div class=\"album_title\">会员相册</div>" +
                    "<div class=\"tip\">建议按快捷键 Ctrl+S 将网页保存到本地以便随时查看</div>");

            for (int i = 0; i < array.size(); i++) {
                JSONObject object = JSON.parseObject(array.get(i).toString());
                String vip_name = object.get("vip_name").toString();
                String card_no = object.get("card_no").toString();
                String phone = object.get("phone").toString();
                builder.append("<div class=\"line\">");
                builder.append("<div class=\"album_info\">");
                builder.append(" <span >"+vip_name+"</span>");
                builder.append(" <span >手机:</span><span>"+phone+"</span>");
                builder.append(" <span>卡号:</span><span>"+card_no+"</span>");
                builder.append("</div>");
                builder.append("  <div class=\"album_list\">\n" +
                        "            <ul>");

                String album_obj = object.get("album").toString();
                JSONArray album = new JSONArray();
                if(!album_obj.equals("")) {
                    album  = JSONArray.parseArray(album_obj);
                }
                for (int j = 0; j < album.size(); j++) {
                    JSONObject object_album = JSON.parseObject(album.get(j).toString());
                    String image_url = object_album.get("image_url").toString();
                    String time = object_album.get("time").toString();
                    builder.append("  <li class=\"album_parent\">\n" +
                            "                    <div >");
                    builder.append("<img src=\""+image_url+"\" alt=\"\" title=\""+time+"\"> ");
                    builder.append(" </div>\n" +
                            "                </li>");
                }
                builder.append("</ul>\n" +
                        "        </div>");
            }
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
            filename = URLEncoder.encode(filename, "utf-8");
            String path = request.getSession().getServletContext().getRealPath("api");
            result = filename;
            System.out.println("路径：" + result);
            File file = new File(path, filename);
            fw=new FileWriter(file);
            fw.write(builder.toString());
            fw.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(fw!=null){
                fw.close();
            }
            System.gc();
        }
        return result;
    }
}
