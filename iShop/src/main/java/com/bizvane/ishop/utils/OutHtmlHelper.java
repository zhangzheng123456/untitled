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
                    "\t<meta charset=\"UTF-8\">\n" +
                    "\t<title>会员头像</title>\n" +
                    "\t<style>\n" +
                    "\t\t*{\n" +
                    "\t\t\tmargin:0;\n" +
                    "\t\t\tpadding:0;\n" +
                    "\t\t\tlist-style:none;\n" +
                    "\t\t}\n" +
                    "\t\t.vip_parent li{\n" +
                    "\t\t\ttext-align:center;\n" +
                    "\t\t\twidth:33.333%;\n" +
                    "\t\t\tfloat:left;\n" +
                    "\t\t\theight:200px;\n" +
                    "\t\t}\n" +
                    "\t\t.vip_head{\n" +
                    "\t\t\toverflow:hidden;\n" +
                    "\t\t}\n" +
                    "\t\t.vip_head li{\n" +
                    "\t\t\tfloat:left;\n" +
                    "\t\t}\n" +
                    "\t\t.vip_head .cad{\n" +
                    "\t\t\tmargin-left: 10px;\n" +
                    "\t\t}\n" +
                    "\t</style>\n" +
                    "</head>");
            builder.append("<body>\n" +
                    "\t<div>");

            for (int i = 0; i < array.size(); i++) {
                JSONObject object = JSON.parseObject(array.get(i).toString());
                String vip_name = object.get("vip_name").toString();
                String card_no = object.get("card_no").toString();
                String phone = object.get("phone").toString();
                builder.append("<div class=\"vip_head\">\n" +
                        "\t\t\t\t<ul>\n" +
                        "\t\t\t\t\t<li>会员名称:</li>\n" +
                        "\t\t\t\t\t<li>" + vip_name + "</li>\n" +
                        "\t\t\t\t\t<li class=\"cad\">会员卡号:</li>\n" +
                        "\t\t\t\t\t<li>" + card_no + "</li>\n" +
                        "\t\t\t\t\t<li class=\"cad\">手机号:</li>\n" +
                        "\t\t\t\t\t<li>" + phone + "</li>\n" +
                        "\t\t\t\t</ul>\n" +
                        "\t\t\t</div>");
                builder.append("<ul class=\"vip_parent\">");
                String album_obj = object.get("album").toString();
                JSONArray album = new JSONArray();
                if(!album_obj.equals("")) {
                    album  = JSONArray.parseArray(album_obj);
                }
                for (int j = 0; j < album.size(); j++) {
                    JSONObject object_album = JSON.parseObject(album.get(i).toString());
                    String image_url = object_album.get("image_url").toString();
                    String time = object_album.get("time").toString();
                    builder.append("<li>");
                    builder.append("<img src=\"" + image_url + "\">");
                    builder.append("</li>");
                }
                builder.append("</ul>");
            }
            builder.append("\t</div>\n" +
                    "</body>\n" +
                    "</html>");
            String filename = user_id + "_" + sdf.format(new Date()) + ".html";
            filename = URLEncoder.encode(filename, "utf-8");
            String path = request.getSession().getServletContext().getRealPath("lupload");
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
        }
        return result;
    }
}
