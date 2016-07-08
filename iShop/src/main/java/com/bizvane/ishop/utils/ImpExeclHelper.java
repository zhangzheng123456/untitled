package com.bizvane.ishop.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.junit.Test;
//import com.dao.WageDao;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * Created by yin on 2016/7/5.
 */
public class ImpExeclHelper {
    Vector<String> titleList;
    private WageDao wagedao = new WageDao();
    public String importXls(String tbName, String file) throws Exception  {
//        Workbook rwb=Workbook.getWorkbook(new File(file));
//        Sheet rs=rwb.getSheet(0);//或者rwb.getSheet(0)
//        int clos=rs.getColumns();//得到所有的列
//        int rows=rs.getRows();//得到所有的行


        File file1=new File(file);

      //  HSSFWorkbook book=new HSSFWorkbook(new FileInputStream(file1));
      XSSFWorkbook book = new XSSFWorkbook(new FileInputStream(file1));// 获得文件
         XSSFSheet sheet = book.getSheetAt(0);// 获得第一个工作表
        XSSFRow title = sheet.getRow(1);// 获得标题行
        int titles = title.getLastCellNum();// 获得字段总数
        int rows = sheet.getLastRowNum();// 获得总行数
        String message = "更新成功";
        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory )webApplicationContext.getBean("sqlSessionFactory");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Connection conn = sqlSession.getConnection();
     //   Connection conn = ConnUtils.getConnection();//获得数据库连接，开启事务控制插入出错。
        titleList = new Vector<String>();//接收第一行字段名
        for (int i = 0; i < titles; i++) {
            XSSFCell cel = title.getCell(i);
            String result = getStringCellValue(cel);
            titleList.add(result);
        }
        try {
          //  wagedao.createTable(titleList, tbName);//将字段名交给数据库处理类生成表。
            conn.setAutoCommit(false);//开启事务
            for (int i = 5; i <= rows; i++) {// 遍历将表数据装进数组
                ArrayList<String> v = new ArrayList<String>();
                XSSFRow row = sheet.getRow(i);
                int cels = row.getLastCellNum();
                for (int j = 0; j < cels; j++) {
                    String result = "";
                    XSSFCell cel = row.getCell(j);
                    result = getStringCellValue(cel);
                    v.add(result);
                }
                wagedao.insert(conn,titleList, v, tbName);// 将数级插入数据库。
            }
            conn.setAutoCommit(true);//关闭事务，插入的数据会回滚，但是新表会建成只是没有数据。
        } catch (Exception e) {

            message = e.getMessage()+"更新失败";
            e.printStackTrace();
        }finally{
            conn.close();

        }
       // book.close();
        return message;
    }
    public Vector<String> getTitles() {
        return titleList;
    }
    private static String getStringCellValue(XSSFCell cell) {// 将XLSX内容转为STRING，空的将默认为0
        String strCell = "";
        int type = 0;
        try {
            switch (cell.getCellType()) {
                case XSSFCell.CELL_TYPE_BLANK:
                    strCell = "";
                    break;
                case XSSFCell.CELL_TYPE_STRING:
                    strCell = cell.getStringCellValue();
                    break;
                case XSSFCell.CELL_TYPE_NUMERIC:
                    strCell = String.valueOf(cell.getNumericCellValue());
                    break;
                case XSSFCell.CELL_TYPE_BOOLEAN:
                    strCell = String.valueOf(cell.getBooleanCellValue());
                    break;
                default:
                    strCell = "";
                    break;
            }
        } catch (Exception e) {
            if (e.getMessage() == null) {
                strCell = " ";
            }
        }

        if (strCell.equals("") || strCell == null) {
            return " ";
        }
        if (cell == null) {
            return " ";
        }
        return strCell;
    }
}

