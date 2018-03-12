package com.bizvane.ishop.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.VipParam;
import com.bizvane.ishop.service.VipParamService;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yin on 2016/6/30.
 */
public class OutExeclHelper {
    public  static  ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring*.xml");
    public static String OutExecl(String json, List olist, LinkedHashMap<String, String> cols, HttpServletResponse response, HttpServletRequest request,String fileName) {
        String result = "";
        WritableWorkbook book = null;
        try {
            com.alibaba.fastjson.JSONArray array = JSON.parseArray(json);
            List<List<String>> lists = new ArrayList<List<String>>();
            for (int i = 0; i < olist.size(); i++) {
                List<String> temp = new ArrayList<String>();
                for (String key : cols.keySet()) {
                    String bb="";
                    if(array.getJSONObject(i).containsKey(key)) {
                        String aa = String.valueOf(array.getJSONObject(i).get(key) + "");
                        bb = aa.replaceAll("null", "");
                    }
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
            String user_id = request.getSession().getAttribute("user_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            //设置响应头信息，为下载文件方式
//            response.setContentType("APPLICATION/DOWNLOAD");
//            response.setHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("utf-8"),"ISO-8859-1"));
            //设置响应的字符集
//            response.setContentType("application/vnd.ms-excel;charset=utf-8");
//             response.setHeader("Content-Disposition", "attachment;filename=" + name);
            //创建excel空白文档

            String filename =fileName+ "_" + corp_code+user_id + "_" + sdf.format(new Date()) + ".xls";
           // filename = URLEncoder.encode(filename, "utf-8");
            String path = request.getSession().getServletContext().getRealPath("lupload");
            result = filename;
            System.out.println("路径util：" + result);
            File file = new File(path, filename);
            book = Workbook.createWorkbook(file);
            WritableSheet sheet = book.createSheet("报表", 0);

            WritableFont font = new WritableFont(WritableFont.createFont("微软雅黑"), 15,
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat format = new WritableCellFormat(font);
            format.setAlignment(Alignment.CENTRE);
            format.setVerticalAlignment(VerticalAlignment.CENTRE);
            //这里可以改中文
            List<String> values = new ArrayList<String>();
            for (String key : cols.keySet()) {
                values.add(cols.get(key));
            }
            for (int i = 0; i < values.size(); i++) {
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

                for (int j = 0; j < m.size(); j++) {
                    Label lb = null;
                    if (m.get(j) != null) {
                        lb = new Label(j, i + 1, m.get(j), format2);
                    } else {
                        lb = new Label(j, i + 1, "", format2);
                    }
                    sheet.addCell(lb);
                }
//                String str2 = m.toString();
//                str2 = str2.substring(1, str2.length() - 1);
//                String[] split = str2.split(",");
//                for (int j = 0; j < split.length; j++) {
//                    Label lb = null;
//                    if (split[j] != null) {
//                        lb = new Label(j, i + 1, split[j].trim(), format2);
//                    } else {
//                        lb = new Label(j, i + 1, "", format2);
//                    }
//                    sheet.addCell(lb);
//                }
                i++;
            }
            //写入文件
            book.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (book != null) {
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


    public static String OutExecl_vip(JSONArray array, List olist, LinkedHashMap<String, String> cols, HttpServletResponse response, HttpServletRequest request, String fileName) {
        VipParamService vipParamService = (VipParamService) context.getBean("vipParamService");

        String result = "";
        WritableWorkbook book = null;
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
                    String bb="";
                    if(array.getJSONObject(i).containsKey(key)) {
                        String aa = String.valueOf(array.getJSONObject(i).get(key) + "");
                        bb = aa.replaceAll("null", "");
                        /***********处理带参数属性的扩展参数字段************/
                        if(StringUtils.isNotBlank(bb)){
                            String corp_code = request.getSession().getAttribute("corp_code").toString();
                            String role_code = request.getSession().getAttribute("role_code").toString();
                            if (role_code.equals(Common.ROLE_SYS)) {
                                corp_code = "C10000";
                            }
                            if(key.startsWith("CUST")){
                                //VipParamServiceImpl vipParamService = (VipParamServiceImpl) SpringBeanFactoryUtils.getBean("VipParamService");
                                List<VipParam> vipParamList = vipParamService.selectByParamName(corp_code, key, "Y");
                                if (vipParamList.size() > 0) {
                                    VipParam vipParam = vipParamList.get(0);
                                    String param_attribute = vipParam.getParam_attribute();
                                    if ("sex".equals(param_attribute)) {
                                        if ("男,M,1".contains(bb)) {
                                            bb = "男";
                                        } else if ("女,F,W,Y,0".contains(bb)) {
                                            bb = "女";
                                        }
                                    }
                                }
                            }
                        }
                     /**********处理带参数属性的扩展参数字段***************/
                    }
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
            String user_id = request.getSession().getAttribute("user_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            //设置响应头信息，为下载文件方式
//            response.setContentType("APPLICATION/DOWNLOAD");
//            response.setHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("utf-8"),"ISO-8859-1"));
            //设置响应的字符集
//            response.setContentType("application/vnd.ms-excel;charset=utf-8");
//             response.setHeader("Content-Disposition", "attachment;filename=" + name);
            //创建excel空白文档
            String filename =fileName+ "_" +  corp_code+user_id + "_" + sdf.format(new Date()) + ".xls";
    //        filename = URLEncoder.encode(filename, "utf-8");
            String path = request.getSession().getServletContext().getRealPath("lupload");
            result = filename;
            System.out.println("路径：" + result);
            File file = new File(path, filename);
            book = Workbook.createWorkbook(file);
            WritableSheet sheet = book.createSheet("报表", 0);
            WritableFont font = new WritableFont(WritableFont.createFont("微软雅黑"), 15,
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat format = new WritableCellFormat(font);
            format.setAlignment(Alignment.CENTRE);
            format.setVerticalAlignment(VerticalAlignment.CENTRE);
            //这里可以改中文
            List<String> values = new ArrayList<String>();
            for (String key : cols.keySet()) {
                if (key.equals("sex")) {
                    values.add("性别(M/男、F/女)");
                } else {
                    values.add(cols.get(key));
                }
            }
            for (int i = 0; i < values.size(); i++) {
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
                for (int j = 0; j < m.size(); j++) {
                    Label lb = null;
                    if (m.get(j) != null) {
                        lb = new Label(j, i + 1, m.get(j), format2);
                    } else {
                        lb = new Label(j, i + 1, "", format2);
                    }
                    sheet.addCell(lb);
                }
//                String str2 = m.toString();
//                str2 = str2.substring(1, str2.length() - 1);
//                String[] split = str2.split(",");
//                for (int j = 0; j < split.length; j++) {
//                    Label lb = null;
//                    if (split[j] != null) {
//                        lb = new Label(j, i + 1, split[j].trim(), format2);
//                    } else {
//                        lb = new Label(j, i + 1, "", format2);
//                    }
//                    sheet.addCell(lb);
//                }
                i++;
            }
            //写入文件
            book.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (book != null) {
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


    public static String OutExecl_vip2(com.alibaba.fastjson.JSONArray array, LinkedHashMap<String, String> cols,HttpServletRequest request,String fileName) {
        String result = "";
        WritableWorkbook book = null;
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
                    String aa = String.valueOf(array.getJSONObject(i).get(key) + "");
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
            String user_id = request.getSession().getAttribute("user_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            //设置响应头信息，为下载文件方式
//            response.setContentType("APPLICATION/DOWNLOAD");
//            response.setHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("utf-8"),"ISO-8859-1"));
            //设置响应的字符集
//            response.setContentType("application/vnd.ms-excel;charset=utf-8");
//             response.setHeader("Content-Disposition", "attachment;filename=" + name);
            //创建excel空白文档
            String filename =fileName+ "_" + corp_code+user_id + "_" + sdf.format(new Date()) + ".xls";
            //  filename = URLEncoder.encode(filename, "utf-8");
            String path = request.getSession().getServletContext().getRealPath("lupload");
            result = filename;
            System.out.println("路径：" + result);
            File file = new File(path, filename);
            book = Workbook.createWorkbook(file);
            WritableSheet sheet = book.createSheet("报表", 0);
            WritableFont font = new WritableFont(WritableFont.createFont("微软雅黑"), 15,
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat format = new WritableCellFormat(font);
            format.setAlignment(Alignment.CENTRE);
            format.setVerticalAlignment(VerticalAlignment.CENTRE);
            //这里可以改中文
            List<String> values = new ArrayList<String>();
            for (String key : cols.keySet()) {
                if (key.equals("sex")) {
                    values.add("性别(M/男、F/女)");
                } else {
                    values.add(cols.get(key));
                }
            }
            for (int i = 0; i < values.size(); i++) {
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

                for (int j = 0; j < m.size(); j++) {
                    Label lb = null;
                    if (m.get(j) != null) {
                        lb = new Label(j, i + 1, m.get(j), format2);
                    } else {
                        lb = new Label(j, i + 1, "", format2);
                    }
                    sheet.addCell(lb);
                }
//                String str2 = m.toString();
//                str2 = str2.substring(1, str2.length() - 1);
//                String[] split = str2.split(",");
//                for (int j = 0; j < split.length; j++) {
//                    Label lb = null;
//                    if (split[j] != null) {
//                        lb = new Label(j, i + 1, split[j], format2);
//                    } else {
//                        lb = new Label(j, i + 1, "", format2);
//                    }
//                    sheet.addCell(lb);
//                }
                i++;
            }
            //写入文件
            book.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (book != null) {
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


    public static String OutExecl_view(List<String> list_title, List<String> list_value_amt, List<String> list_value_num, List<String> list_value_discount,List<List<String>> all_list,HttpServletRequest request,String fileName) {
        String result = "";
        WritableWorkbook book = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String user_id = request.getSession().getAttribute("user_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
          //  String user_id="jj";
            String filename = fileName + "_" + corp_code+user_id + "_" + sdf.format(new Date()) + ".xls";

     //       filename = URLEncoder.encode(filename, "utf-8");
            String path = request.getSession().getServletContext().getRealPath("lupload");
       //     String path ="E:\\lupload";
            result = filename;
            System.out.println("路径：" + result);
            File file = new File(path, filename);
            book = Workbook.createWorkbook(file);
            WritableSheet sheet = book.createSheet("报表", 0);
            WritableFont font = new WritableFont(WritableFont.createFont("微软雅黑"), 15,
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat format = new WritableCellFormat(font);
            format.setAlignment(Alignment.CENTRE);
            format.setVerticalAlignment(VerticalAlignment.CENTRE);
            //这里可以改中文
            for (int i = 0; i < list_title.size(); i++) {
                sheet.setColumnView(i, 40);
                Label label = new Label(i, 0, list_title.get(i));
                label.setCellFormat(format);
                sheet.addCell(label);
            }
            WritableFont font2 = new WritableFont(WritableFont.createFont("微软雅黑"), 10,
                    WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat format2 = new WritableCellFormat(font2);
            format2.setAlignment(Alignment.CENTRE);
            format2.setVerticalAlignment(VerticalAlignment.CENTRE);
            format2.setShrinkToFit(false);
            for (int i = 0; i < list_value_amt.size(); i++) {
                sheet.setColumnView(i, 40);
                Label label = new Label(i, 1, list_value_amt.get(i));
                label.setCellFormat(format2);
                sheet.addCell(label);
            }
            for (int i = 0; i < list_value_num.size(); i++) {
                sheet.setColumnView(i, 40);
                Label label = new Label(i, 2, list_value_num.get(i));
                label.setCellFormat(format2);
                sheet.addCell(label);
            }
            for (int i = 0; i < list_value_discount.size(); i++) {
                sheet.setColumnView(i, 40);
                Label label = new Label(i, 3, list_value_discount.get(i));
                label.setCellFormat(format2);
                sheet.addCell(label);
            }
            int index=4;
            for (int i = 0; i <all_list.size() ; i++) {
                List<String> list=all_list.get(i);
                if(list.size()>0) {
                    for (int j = 0; j < list.size(); j++) {
                        sheet.setColumnView(j, 40);
                        Label label = new Label(j, index, list.get(j));
                        label.setCellFormat(format2);
                        sheet.addCell(label);
                    }
                    index++;
                }
            }
            //写入文件
            book.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (book != null) {
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


    public static String OutExecl2(String json, JSONArray jsonArray, LinkedHashMap<String, String> cols, HttpServletResponse response, HttpServletRequest request,String fileName) {
        String result = "";
        WritableWorkbook book = null;
        Set<String> keys = cols.keySet();
        try {
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//            String json = mapper.writeValueAsString(olist);
//            System.out.println("---:"+json);
//            JSONObject jsonObject = new JSONObject(json);
//            jsonObject.put("list", olist);
            JSONArray array = JSON.parseArray(json);
            //org.json.JSONArray array = jsonObject.getJSONArray("list");
            List<List<String>> lists = new ArrayList<List<String>>();
            for (int i = 0; i < jsonArray.size(); i++) {
                List<String> temp = new ArrayList<String>();
                for (String key : cols.keySet()) {
                    // System.out.println("key= "+ key + " and value= " + cols.get(key));
                    String bb="";
                    if(array.getJSONObject(i).containsKey(key)) {
                        String aa = String.valueOf(array.getJSONObject(i).get(key) + "");
                        bb = aa.replaceAll("null", "");
                    }
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
            String user_id = request.getSession().getAttribute("user_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            //设置响应头信息，为下载文件方式
//            response.setContentType("APPLICATION/DOWNLOAD");
//            response.setHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("utf-8"),"ISO-8859-1"));
            //设置响应的字符集
//            response.setContentType("application/vnd.ms-excel;charset=utf-8");
//             response.setHeader("Content-Disposition", "attachment;filename=" + name);
            //创建excel空白文档

            String filename =fileName+ "_" + corp_code+user_id + "_" + sdf.format(new Date()) + ".xls";
            // filename = URLEncoder.encode(filename, "utf-8");
            String path = request.getSession().getServletContext().getRealPath("lupload");
            result = filename;
            System.out.println("路径util：" + result);
            File file = new File(path, filename);
            book = Workbook.createWorkbook(file);
            WritableSheet sheet = book.createSheet("报表", 0);

            WritableFont font = new WritableFont(WritableFont.createFont("微软雅黑"), 15,
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat format = new WritableCellFormat(font);
            format.setAlignment(Alignment.CENTRE);
            format.setVerticalAlignment(VerticalAlignment.CENTRE);
            //这里可以改中文
            List<String> values = new ArrayList<String>();
            for (String key : cols.keySet()) {
                values.add(cols.get(key));
            }
            for (int i = 0; i < values.size(); i++) {
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

                for (int j = 0; j < m.size(); j++) {
                    Label lb = null;
                    if (m.get(j) != null) {
                        lb = new Label(j, i + 1, m.get(j), format2);
                    } else {
                        lb = new Label(j, i + 1, "", format2);
                    }
                    sheet.addCell(lb);
                }
//                String str2 = m.toString();
//                str2 = str2.substring(1, str2.length() - 1);
//                String[] split = str2.split(",");
//                for (int j = 0; j < split.length; j++) {
//                    Label lb = null;
//                    if (split[j] != null) {
//                        lb = new Label(j, i + 1, split[j].trim(), format2);
//                    } else {
//                        lb = new Label(j, i + 1, "", format2);
//                    }
//                    sheet.addCell(lb);
//                }
                i++;
            }
            //写入文件
            book.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (book != null) {
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

    public static String OutExecl_vipCheck(String json, List olist, LinkedHashMap<String, String> cols, HttpServletResponse response, HttpServletRequest request,String fileName) {
        String result = "";
        WritableWorkbook book = null;
        try {
            com.alibaba.fastjson.JSONArray array = JSON.parseArray(json);
            List<List<String>> lists = new ArrayList<List<String>>();
            for (int i = 0; i < olist.size(); i++) {
                List<String> temp = new ArrayList<String>();
                for (String key : cols.keySet()) {
                    String bb="";
                    if(array.getJSONObject(i).containsKey(key)) {
                        String aa = String.valueOf(array.getJSONObject(i).get(key) + "");
                        bb = aa.replaceAll("null", "");
                        if (key.equals("status")){
                            if (bb.equals("0"))
                                bb = "未审核";
                            if (bb.equals("1"))
                                bb = "审核通过";
                        }
                    }
                    String replaceStr = WebUtils.StringFilter(bb);
                    temp.add(replaceStr);
                }
                lists.add(temp);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String user_id = request.getSession().getAttribute("user_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();

            String filename =fileName+ "_" + corp_code+user_id + "_" + sdf.format(new Date()) + ".xls";
            String path = request.getSession().getServletContext().getRealPath("lupload");
            result = filename;
            System.out.println("路径util：" + result);
            File file = new File(path, filename);
            book = Workbook.createWorkbook(file);
            WritableSheet sheet = book.createSheet("报表", 0);
            WritableFont font = new WritableFont(WritableFont.createFont("微软雅黑"), 15,
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat format = new WritableCellFormat(font);
            format.setAlignment(Alignment.CENTRE);
            format.setVerticalAlignment(VerticalAlignment.CENTRE);
            //这里可以改中文
            List<String> values = new ArrayList<String>();
            for (String key : cols.keySet()) {
                values.add(cols.get(key));
            }
            for (int i = 0; i < values.size(); i++) {
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

                for (int j = 0; j < m.size(); j++) {
                    Label lb = null;
                    if (m.get(j) != null) {
                        lb = new Label(j, i + 1, m.get(j), format2);
                    } else {
                        lb = new Label(j, i + 1, "", format2);
                    }
                    sheet.addCell(lb);
                }
                i++;
            }
            //写入文件
            book.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (book != null) {
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

    public static ArrayList<String> findFiles(String dirName1,String dirName2,String targetFileName) throws Exception{
        /**
         * 算法简述：
         * 从某个给定的需查找的文件夹出发，搜索该文件夹的所有子文件夹及文件，
         * 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹，则进队列。
         * 队列不空，重复上述操作，队列为空，程序结束，返回结果。
         */
        List<File> fileList = new ArrayList<File>();
        String tempName = null;
        //判断目录是否存在
        File baseDir = new File(dirName1);
        if (!baseDir.exists() || !baseDir.isDirectory()){
            System.out.println("文件查找失败：" + dirName1 + "不是一个目录！");
        } else {
            String[] filelist = baseDir.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(dirName1 + "\\" + filelist[i]);
                if(!readfile.isDirectory()) {
                    tempName =  readfile.getName();
                    if (tempName.contains("_"+targetFileName+"_") && (tempName.endsWith(".xls")||tempName.endsWith(".zip"))){
                        //匹配成功，将文件名添加到结果集
                        fileList.add(readfile);
                    }
                }
            }
        }
//判断目录是否存在
        File baseDir2 = new File(dirName2);
        if (!baseDir2.exists() || !baseDir2.isDirectory()){
            System.out.println("文件查找失败：" + dirName2 + "不是一个目录！");
        } else {
            String[] filelist = baseDir2.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(dirName2 + "\\" + filelist[i]);
                if(!readfile.isDirectory()) {
                    tempName =  readfile.getName();
                    if (tempName.contains("_"+targetFileName+"_") && (tempName.endsWith(".xls")||tempName.endsWith(".zip"))){
                        //匹配成功，将文件名添加到结果集
                        fileList.add(readfile);
                    }
                }
            }
        }

        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                String[] o1s = o1.getName().split("_");
                String[] o2s = o2.getName().split("_");
                long diff = Long.parseLong(o1s[o1s.length-1].replace(".xls","").replace(".zip","")) - Long.parseLong(o2s[o2s.length-1].replace(".xls","").replace(".zip",""));
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;
            }
        });
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < fileList.size(); i++) {
            list.add(fileList.get(i).getName());
        }
        return list;
    }

    public static ArrayList<String> findFiles(String[] dirName,String targetFileName) throws Exception{
        /**
         * 算法简述：
         * 从某个给定的需查找的文件夹出发，搜索该文件夹的所有子文件夹及文件，
         * 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹，则进队列。
         * 队列不空，重复上述操作，队列为空，程序结束，返回结果。
         */
        List<File> fileList = new ArrayList<File>();
        String tempName = null;
        //判断目录是否存在
        for (int j = 0; j <dirName.length ; j++) {
            File baseDir = new File(dirName[j]);
            if (!baseDir.exists() || !baseDir.isDirectory()){
                System.out.println("文件查找失败：" + dirName + "不是一个目录！");
            } else {
                String[] filelist = baseDir.list();
                for (int i = 0; i < filelist.length; i++) {
                    File readfile = new File(dirName + "\\" + filelist[i]);
                    if(!readfile.isDirectory()) {
                        tempName =  readfile.getName();
                        if (tempName.contains("_"+targetFileName+"_") && (tempName.endsWith(".xls")||tempName.endsWith(".zip"))){
                            //匹配成功，将文件名添加到结果集
                            fileList.add(readfile);
                        }
                    }
                }
            }

        }

        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                String[] o1s = o1.getName().split("_");
                String[] o2s = o2.getName().split("_");
                long diff = Long.parseLong(o1s[o1s.length-1].replace(".xls","").replace(".zip","")) - Long.parseLong(o2s[o2s.length-1].replace(".xls","").replace(".zip",""));
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;
            }
        });
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < fileList.size(); i++) {
            list.add(fileList.get(i).getName());
        }
        return list;
    }


    public static void OutExeclSheet(JSONArray array, LinkedHashMap<String, String> cols,WritableWorkbook book,String sheet_name,int order) {
        try {
            //创建excel空白文档
            WritableSheet sheet = book.createSheet(sheet_name, order);

            List<List<String>> lists = new ArrayList<List<String>>();
            for (int i = 0; i < array.size(); i++) {
                List<String> temp = new ArrayList<String>();
                for (String key : cols.keySet()) {
                    String aa = String.valueOf(array.getJSONObject(i).get(key) + "");
                    String bb = aa.replaceAll("null", "");
                    String replaceStr = WebUtils.StringFilter(bb);
                    temp.add(replaceStr);
                }
                lists.add(temp);
            }

            WritableFont font = new WritableFont(WritableFont.createFont("微软雅黑"), 15,
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat format = new WritableCellFormat(font);
            format.setAlignment(Alignment.CENTRE);
            format.setVerticalAlignment(VerticalAlignment.CENTRE);
            //这里可以改中文
            List<String> values = new ArrayList<String>();
            for (String key : cols.keySet()) {
                if (key.equals("sex")) {
                    values.add("性别(M/男、F/女)");
                } else {
                    values.add(cols.get(key));
                }
            }
            for (int i = 0; i < values.size(); i++) {
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

                for (int j = 0; j < m.size(); j++) {
                    Label lb = null;
                    if (m.get(j) != null) {
                        lb = new Label(j, i + 1, m.get(j), format2);
                    } else {
                        lb = new Label(j, i + 1, "", format2);
                    }
                    sheet.addCell(lb);
                }
                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public  void  writeFile(String path, String filename, InputStream inputStream){
        FileOutputStream fileOutputStream=null;
        try{
            File file1=new File(path,filename);
            if(!file1.exists()){
                file1.createNewFile();
            }
            fileOutputStream=new FileOutputStream(file1);
            byte[] bytes=new byte[1024];
            int len=0;
            while ((len=inputStream.read(bytes))!=-1){
                fileOutputStream.write(bytes,0,len);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //本地导入
    public static String OutExeclTest(String json, List olist, LinkedHashMap<String, String> cols,String fileName)throws Exception {
        String result = "";
        WritableWorkbook book = null;
        try {
            JSONArray array = JSON.parseArray(json);
            List<List<String>> lists = new ArrayList<List<String>>();
            for (int i = 0; i < olist.size(); i++) {
                List<String> temp = new ArrayList<String>();
                for (String key : cols.keySet()) {
                    String bb="";
                    if(array.getJSONObject(i).containsKey(key)) {
                        String aa = String.valueOf(array.getJSONObject(i).get(key) + "");
                        bb = aa.replaceAll("null", "");
                    }
                    String replaceStr = WebUtils.StringFilter(bb);
                    temp.add(replaceStr);
                }
                lists.add(temp);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String filename = fileName+sdf.format(new Date()) + ".xls";
            result = filename;
            System.out.println("路径util：" + result);
            File file = new File("d:", filename);
            book = Workbook.createWorkbook(file);
            WritableSheet sheet = book.createSheet("报表", 0);

            WritableFont font = new WritableFont(WritableFont.createFont("微软雅黑"), 15,
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat format = new WritableCellFormat(font);
            format.setAlignment(Alignment.CENTRE);
            format.setVerticalAlignment(VerticalAlignment.CENTRE);
            //这里可以改中文
            List<String> values = new ArrayList<String>();
            for (String key : cols.keySet()) {
                values.add(cols.get(key));
            }
            for (int i = 0; i < values.size(); i++) {
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

                for (int j = 0; j < m.size(); j++) {
                    Label lb = null;
                    if (m.get(j) != null) {
                        lb = new Label(j, i + 1, m.get(j), format2);
                    } else {
                        lb = new Label(j, i + 1, "", format2);
                    }
                    sheet.addCell(lb);
                }
                i++;
            }
            //写入文件
            book.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (book != null) {
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

    //本地读execl
    public  static  String readExecl(String filename) throws Exception{
        File file=new File(filename);
        Workbook rwb = Workbook.getWorkbook(file);
        Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
        int rows = rs.getRows();//得到所有的行
        String param="";
        for (int i = 0; i < rows; i++) {
            String value = rs.getCell(0, i).getContents().toString().trim();
            if (value.equals("") ) {
                continue;
            }
            param+=value+",";
        }
        return param;
    }

}
