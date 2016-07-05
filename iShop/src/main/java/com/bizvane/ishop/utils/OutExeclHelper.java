package com.bizvane.ishop.utils;


import com.bizvane.ishop.entity.ValidateCode;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.ss.formula.functions.T;
import org.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yin on 2016/6/30.
 */
public class OutExeclHelper {
    public static void OutExecl(List olist, String[] cols, HttpServletResponse response){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("list", olist);
            org.json.JSONArray array = jsonObject.getJSONArray("list");
            List<List<String>> lists = new ArrayList<List<String>>();
            for (int i = 0; i < olist.size(); i++) {
                List<String> temp = new ArrayList<String>();
                for (int j = 0; j < cols.length; j++) {
                    String aa = array.getJSONObject(i).get(cols[j]).toString();
                    temp.add(aa);
                }
                lists.add(temp);
            }
            //------------------------开启响应头---------------------------------------
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            //设置响应的字符集
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            String name = URLEncoder.encode("报表_" + sdf.format(new Date()) + ".xls", "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + name);
            //创建excel空白文档
            WritableWorkbook book = Workbook.createWorkbook(response.getOutputStream());
            WritableSheet sheet = book.createSheet("报表", 0);
            WritableFont font = new WritableFont(WritableFont.createFont("微软雅黑"), 15,
                    WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
            WritableCellFormat format = new WritableCellFormat(font);
            format.setAlignment(Alignment.CENTRE);
            format.setVerticalAlignment(VerticalAlignment.CENTRE);
            for (int i = 0; i < cols.length; i++) {
                sheet.setColumnView(i, 40);
                Label label = new Label(i, 0, cols[i]);
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
            //写入结束
            book.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
