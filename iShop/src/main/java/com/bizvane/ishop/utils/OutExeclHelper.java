package com.bizvane.ishop.utils;


import com.alibaba.fastjson.JSON;
import com.bizvane.ishop.entity.ValidateCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.formula.functions.T;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yin on 2016/6/30.
 */
public class OutExeclHelper {
    public static String OutExecl(String json,List olist, LinkedHashMap<String,String> cols, HttpServletResponse response, HttpServletRequest request){
        String result="";
        WritableWorkbook book=null;
        Set<String> keys = cols.keySet();
        try {
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//            String json = mapper.writeValueAsString(olist);
//            System.out.println("---:"+json);
//            JSONObject jsonObject = new JSONObject(json);
//            jsonObject.put("list", olist);
            com.alibaba.fastjson.JSONArray array = JSON.parseArray(json);
            //org.json.JSONArray array = jsonObject.getJSONArray("list");
            List<List<String>> lists = new ArrayList<List<String>>();
            for (int i = 0; i < olist.size(); i++) {
                List<String> temp = new ArrayList<String>();
                    for (String key : cols.keySet()) {
                       // System.out.println("key= "+ key + " and value= " + cols.get(key));
                        String aa=String.valueOf(array.getJSONObject(i).get(key)+"");
                        String bb = aa.replaceAll("null", "");
                        String replaceStr = WebUtils.StringFilter(bb);
                        temp.add(replaceStr);
                    }
                lists.add(temp);
            }
            //输出流
        //    OutputStream os = response.getOutputStream();
            //------------------------开启响应头---------------------------------------
      //      response.reset(); // 非常重要 
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String user_id =request.getSession().getAttribute("user_code").toString();
            //设置响应头信息，为下载文件方式
//            response.setContentType("APPLICATION/DOWNLOAD");
//            response.setHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("utf-8"),"ISO-8859-1"));
            //设置响应的字符集
//            response.setContentType("application/vnd.ms-excel;charset=utf-8");
//             response.setHeader("Content-Disposition", "attachment;filename=" + name);
            //创建excel空白文档
            String  filename = user_id +"_"+ sdf.format(new Date()) + ".xls";
            filename = URLEncoder.encode(filename, "utf-8");
            String path = request.getSession().getServletContext().getRealPath("lupload");
            result=filename;
            System.out.println("路径："+result);
            File file =new File(path,filename);
            book = Workbook.createWorkbook(file);
            WritableSheet sheet = book.createSheet("报表", 0);
            WritableFont font = new WritableFont(WritableFont.createFont("微软雅黑"), 15,
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat format = new WritableCellFormat(font);
            format.setAlignment(Alignment.CENTRE);
            format.setVerticalAlignment(VerticalAlignment.CENTRE);
            //这里可以改中文
            List<String> values=new ArrayList<String>();
            for (String key : cols.keySet()) {
                values.add(cols.get(key));
            }
            for(int i=0;i<values.size();i++){
                sheet.setColumnView(i, 40);
                Label label = new Label(i, 0, values.get(i));
                label.setCellFormat(format);
                sheet.addCell(label);
            }
            WritableFont font2 = new WritableFont(WritableFont.createFont("微软雅黑"), 10,
                    WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat format2 = new WritableCellFormat(font2);
            format2.setAlignment(Alignment.CENTRE);
            format2.setVerticalAlignment(VerticalAlignment.CENTRE);
            format2.setShrinkToFit(false);
            int i = 0;
            for (List<String> m : lists) {
                String str2 = m.toString();
                str2 = str2.substring(1, str2.length() - 1);
                String[] split = str2.split(",");
                for (int j = 0; j < split.length; j++) {
                    Label lb = null;
                    if (split[j] != null) {
                        lb = new Label(j, i + 1, split[j], format2);
                    } else {
                        lb = new Label(j, i + 1, "", format2);
                    }
                    sheet.addCell(lb);
                }
                i++;
            }
            //写入文件
            book.write();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(book!=null){
                try {
                    book.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
            System.gc();
        }
    return result;
    }



    public static String OutExecl_vip(JSONArray array,List olist, LinkedHashMap<String,String> cols, HttpServletResponse response, HttpServletRequest request){
        String result="";
        WritableWorkbook book=null;
        Set<String> keys = cols.keySet();
        try {
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//            String json = mapper.writeValueAsString(olist);
//            System.out.println("---:"+json);
//            JSONObject jsonObject = new JSONObject(json);
//            jsonObject.put("list", olist);
           // com.alibaba.fastjson.JSONArray array = JSON.parseArray(json);
            //org.json.JSONArray array = jsonObject.getJSONArray("list");
            List<List<String>> lists = new ArrayList<List<String>>();
            for (int i = 0; i < olist.size(); i++) {
                List<String> temp = new ArrayList<String>();
                for (String key : cols.keySet()) {
                    // System.out.println("key= "+ key + " and value= " + cols.get(key));
                    String aa=String.valueOf(array.getJSONObject(i).get(key)+"");
                    String bb = aa.replaceAll("null", "");
                    String replaceStr = WebUtils.StringFilter(bb);
                    temp.add(replaceStr);
                }
                lists.add(temp);
            }
            //输出流
            //    OutputStream os = response.getOutputStream();
            //------------------------开启响应头---------------------------------------
            //      response.reset(); // 非常重要 
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String user_id =request.getSession().getAttribute("user_code").toString();
            //设置响应头信息，为下载文件方式
//            response.setContentType("APPLICATION/DOWNLOAD");
//            response.setHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("utf-8"),"ISO-8859-1"));
            //设置响应的字符集
//            response.setContentType("application/vnd.ms-excel;charset=utf-8");
//             response.setHeader("Content-Disposition", "attachment;filename=" + name);
            //创建excel空白文档
            String  filename = user_id +"_"+ sdf.format(new Date()) + ".xls";
            filename = URLEncoder.encode(filename, "utf-8");
            String path = request.getSession().getServletContext().getRealPath("lupload");
            result=filename;
            System.out.println("路径："+result);
            File file =new File(path,filename);
            book = Workbook.createWorkbook(file);
            WritableSheet sheet = book.createSheet("报表", 0);
            WritableFont font = new WritableFont(WritableFont.createFont("微软雅黑"), 15,
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat format = new WritableCellFormat(font);
            format.setAlignment(Alignment.CENTRE);
            format.setVerticalAlignment(VerticalAlignment.CENTRE);
            //这里可以改中文
            List<String> values=new ArrayList<String>();
            for (String key : cols.keySet()) {
                if(key.equals("sex")){
                    values.add("性别(M/男、F/女)");
                }else {
                    values.add(cols.get(key));
                }
            }
            for(int i=0;i<values.size();i++){
                sheet.setColumnView(i, 40);
                Label label = new Label(i, 0, values.get(i));
                label.setCellFormat(format);
                sheet.addCell(label);
            }
            WritableFont font2 = new WritableFont(WritableFont.createFont("微软雅黑"), 10,
                    WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat format2 = new WritableCellFormat(font2);
            format2.setAlignment(Alignment.CENTRE);
            format2.setVerticalAlignment(VerticalAlignment.CENTRE);
            format2.setShrinkToFit(false);
            int i = 0;
            for (List<String> m : lists) {
                String str2 = m.toString();
                str2 = str2.substring(1, str2.length() - 1);
                String[] split = str2.split(",");
                for (int j = 0; j < split.length; j++) {
                    Label lb = null;
                    if (split[j] != null) {
                        lb = new Label(j, i + 1, split[j], format2);
                    } else {
                        lb = new Label(j, i + 1, "", format2);
                    }
                    sheet.addCell(lb);
                }
                i++;
            }
            //写入文件
            book.write();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(book!=null){
                try {
                    book.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
            System.gc();
        }
        return result;
    }


    public static String OutExecl_vip2(com.alibaba.fastjson.JSONArray array, LinkedHashMap<String,String> cols){
        String result="";
        WritableWorkbook book=null;
        Set<String> keys = cols.keySet();
        try {
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//            String json = mapper.writeValueAsString(olist);
//            System.out.println("---:"+json);
//            JSONObject jsonObject = new JSONObject(json);
//            jsonObject.put("list", olist);
            // com.alibaba.fastjson.JSONArray array = JSON.parseArray(json);
            //org.json.JSONArray array = jsonObject.getJSONArray("list");
            List<List<String>> lists = new ArrayList<List<String>>();
            for (int i = 0; i < array.size(); i++) {
                List<String> temp = new ArrayList<String>();
                for (String key : cols.keySet()) {
                    // System.out.println("key= "+ key + " and value= " + cols.get(key));
                    String aa=String.valueOf(array.getJSONObject(i).get(key)+"");
                    String bb = aa.replaceAll("null", "");
                    String replaceStr = WebUtils.StringFilter(bb);
                    temp.add(replaceStr);
                }
                lists.add(temp);
            }
            //输出流
            //    OutputStream os = response.getOutputStream();
            //------------------------开启响应头---------------------------------------
            //      response.reset(); // 非常重要 
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String user_id ="MWD";
            //设置响应头信息，为下载文件方式
//            response.setContentType("APPLICATION/DOWNLOAD");
//            response.setHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("utf-8"),"ISO-8859-1"));
            //设置响应的字符集
//            response.setContentType("application/vnd.ms-excel;charset=utf-8");
//             response.setHeader("Content-Disposition", "attachment;filename=" + name);
            //创建excel空白文档
            String  filename = user_id +"_"+ sdf.format(new Date()) + ".xls";
            filename = URLEncoder.encode(filename, "utf-8");
            String path = "E:\\lupload";
            result=filename;
            System.out.println("路径："+result);
            File file =new File(path,filename);
            book = Workbook.createWorkbook(file);
            WritableSheet sheet = book.createSheet("报表", 0);
            WritableFont font = new WritableFont(WritableFont.createFont("微软雅黑"), 15,
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat format = new WritableCellFormat(font);
            format.setAlignment(Alignment.CENTRE);
            format.setVerticalAlignment(VerticalAlignment.CENTRE);
            //这里可以改中文
            List<String> values=new ArrayList<String>();
            for (String key : cols.keySet()) {
                if(key.equals("sex")){
                    values.add("性别(M/男、F/女)");
                }else {
                    values.add(cols.get(key));
                }
            }
            for(int i=0;i<values.size();i++){
                sheet.setColumnView(i, 40);
                Label label = new Label(i, 0, values.get(i));
                label.setCellFormat(format);
                sheet.addCell(label);
            }
            WritableFont font2 = new WritableFont(WritableFont.createFont("微软雅黑"), 10,
                    WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat format2 = new WritableCellFormat(font2);
            format2.setAlignment(Alignment.CENTRE);
            format2.setVerticalAlignment(VerticalAlignment.CENTRE);
            format2.setShrinkToFit(false);
            int i = 0;
            for (List<String> m : lists) {
                String str2 = m.toString();
                str2 = str2.substring(1, str2.length() - 1);
                String[] split = str2.split(",");
                for (int j = 0; j < split.length; j++) {
                    Label lb = null;
                    if (split[j] != null) {
                        lb = new Label(j, i + 1, split[j], format2);
                    } else {
                        lb = new Label(j, i + 1, "", format2);
                    }
                    sheet.addCell(lb);
                }
                i++;
            }
            //写入文件
            book.write();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(book!=null){
                try {
                    book.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
            System.gc();
        }
        return result;
    }
}
