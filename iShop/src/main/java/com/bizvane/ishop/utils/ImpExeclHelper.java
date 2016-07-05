package com.bizvane.ishop.utils;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
/**
 * Created by yin on 2016/7/5.
 */
public class ImpExeclHelper {
        @SuppressWarnings("unchecked")
        public void createData(String path,String sheetname){
            try {
                String sourcefile = path;
                InputStream is = new FileInputStream(sourcefile);
                Workbook rwb = Workbook.getWorkbook(is);

                //get sheet
                Sheet sheet = rwb.getSheet(sheetname);
                //System.out.println(sheet.getName());7

                //get rows
                int cr =sheet.getRows();

                String header = "";
                String preheader = "";
                List<String> fieldsList = new ArrayList<String>();
                List dataList = new ArrayList();
                for(int i = 0;i<cr;i++){
                    Cell[] testcell  = sheet.getRow(i);

                    if(testcell.length == 0) continue;
                    if(!header.equals(preheader)){
                        fieldsList.clear();
                        preheader = header;
                    }
                    String tempString  = testcell[0].getContents();
                    int datatype = tempString.indexOf("&&");
                    if(datatype != -1 ) continue;

                    System.out.println("第"+i+"行------");
                    //get cells of row
                    for (int j = 0; j < testcell.length; j++) {
                        String str1 = testcell[j].getContents();

                        if(str1 != null && !"".equals(str1)){

                            int fields = str1.indexOf("#");
                            int cheader = str1.indexOf("**");
//                        int coment = str1.indexOf("$$");
                            //int datatype = str1.indexOf("&&");
                            int length = str1.length();
                            //get table name
                            if(cheader != -1){
                                header = str1.substring(cheader+2, length);
                            }else if(fields != -1){
                                fieldsList.add(str1.substring(fields+1, length));
                            }else{
                                dataList.add(str1);
                            }
                        }

                    }
                    if(!header.equals("") && fieldsList.size() != 0 && dataList.size() != 0 ){
                        deleteData(header,dataList);
                        System.out.println("成功删除");
                        inserData(header,fieldsList,dataList);
                        System.out.println("成功插入");
                        dataList.clear();
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        public void deleteDataByid(String path,String sheetname){
            try {
                String sourcefile = path;
                InputStream is = new FileInputStream(sourcefile);
                Workbook rwb = Workbook.getWorkbook(is);

                //get sheet
                Sheet sheet = rwb.getSheet(sheetname);
                //System.out.println(sheet.getName());

                //get rows
                int cr =sheet.getRows();

                String header = "";
                String preheader = "";
                List<String> fieldsList = new ArrayList<String>();
                List dataList = new ArrayList();
                for(int i = 0;i<cr;i++){
                    Cell[] testcell  = sheet.getRow(i);

                    if(testcell.length == 0) continue;
                    if(!header.equals(preheader)){
                        fieldsList.clear();
                        preheader = header;
                    }
                    String tempString  = testcell[0].getContents();
                    int datatype = tempString.indexOf("&&");
                    if(datatype != -1 ) continue;

                    System.out.println("第"+i+"行------");
                    //get cells of row
                    for (int j = 0; j < testcell.length; j++) {
                        String str1 = testcell[j].getContents();

                        if(str1 != null && !"".equals(str1)){

                            int fields = str1.indexOf("#");
                            int cheader = str1.indexOf("**");
//                        int coment = str1.indexOf("$$");
                            //int datatype = str1.indexOf("&&");
                            int length = str1.length();
                            //get table name
                            if(cheader != -1){
                                header = str1.substring(cheader+2, length);
                            }else if(fields != -1){
                                fieldsList.add(str1.substring(fields+1, length));
                            }else{
                                dataList.add(str1);
                            }
                        }

                    }
                    if(!header.equals("") && fieldsList.size() != 0 && dataList.size() != 0 ){
                        deleteData(header,dataList);
                        System.out.println("成功删除");
//                    inserData(header,fieldsList,dataList);
//                    System.out.println("成功插入");
                        dataList.clear();
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        public void deleteData(String header,List dataList){
            try {
                String sql = "delete from "+header + " where 1=1 and id = "+dataList.get(0);
                excute(sql);

            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }


        public int inserData(String header,List<String> fieldsList,List<String> dataList){
            StringBuffer sql = new StringBuffer("insert into ");
            sql.append(header+" (");
            for(int i = 0; i<fieldsList.size();i++){
                sql.append(fieldsList.get(i)+",");

            }
            sql.delete(sql.length()-1, sql.length());
            sql.append(") values(");

            for(int i = 0; i<dataList.size();i++){
                sql.append("'"+dataList.get(i)+"',");
            }

            sql.delete(sql.length()-1, sql.length());
            sql.append(")");

            excute(sql.toString());

            return 0;
        }


        private void excute(String sql){
            //载入Oracle驱动程序
            try {
                //Class.forName("oracle.jdbc.OracleDriver").newInstance();
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (InstantiationException ex) {
                ex.printStackTrace();
                System.out.println("载入MySQL数据库驱动时出错");
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                System.out.println("载入MySQL数据库驱动时出错");
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
                System.out.println("载入MySQL数据库驱动时出错");
            }
            /////////////////////////////////////////////////////////////////////////

            //连接到Oracle数据库
            java.sql.Connection conn = null;
            try{
                //连接Oracle数据库
//            conn = java.sql.DriverManager.getConnection(
//                    "jdbc:oracle:thin:@192.168.1.2:1521:dbname", "username", "password");

                //连接Mysql库


                conn = java.sql.DriverManager.getConnection(

                        "jdbc:mysql://dev.bizvane.com:3306/ishow_dev?useUnicode=true&characterEncoding=utf-8&useSSL=false", "root", "rootpassword");
            } catch (Exception ex){
                ex.printStackTrace();
                System.out.println("连接到MySQL数据库时出错！");
                System.exit(0);
            }
            ////////////////////////////////////////////////////////////////////////

            //得到MySQL操作流

            try {
                System.out.println("-----------------  "+sql);
                java.sql.PreparedStatement stat = conn.prepareStatement(sql);
                boolean rs = stat.execute();

            } catch(Exception ex) {
                ex.printStackTrace();
                System.exit(0);
            }

            //关半程序所占用的资源
            try{
                conn.close();
            }catch(Exception ex){
                ex.printStackTrace();
                System.out.println("关闭程序所占用的资源时出错");
                System.exit(0);
            }
        }


        public List<String> getData(String path,String sheetname){

            List<String> dataList = new ArrayList();
            try {
                String sourcefile = path;
                InputStream is = new FileInputStream(sourcefile);
                Workbook rwb = Workbook.getWorkbook(is);

                //get sheet
                Sheet sheet = rwb.getSheet(sheetname);
                //System.out.println(sheet.getName());

                //get rows
                int cr =sheet.getRows();

                String header = "";
                List<String> fieldsList = new ArrayList<String>();

                for(int i = 0;i<cr;i++){
                    Cell[] testcell  = sheet.getRow(i);

                    //get cells of row
                    for (int j = 0; j < testcell.length; j++) {
                        String str1 = testcell[j].getContents();

                        if(str1 != null && !"".equals(str1)){

                            int fields = str1.indexOf("#");
                            int cheader = str1.indexOf("**");
                            int length = str1.length();
                            //get table name
                            if(cheader != -1){
                                header = str1.substring(cheader+2, length);
                            }else if(fields != -1){
                                fieldsList.add(str1.substring(fields+1, length));
                            }else{
                                dataList.add(str1);
                            }
                        }

                    }
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return dataList;

        }

        public void deleteData(String path,String sheetname){

            try {
                String sourcefile = path;
                InputStream is = new FileInputStream(sourcefile);
                Workbook rwb = Workbook.getWorkbook(is);

                //get sheet
                Sheet sheet = rwb.getSheet(sheetname);
                //System.out.println(sheet.getName());

                //get rows
                int cr =sheet.getRows();

                for(int i = 0;i<cr;i++){
                    String header = "";
                    Cell[] testcell  = sheet.getRow(i);

                    //get cells of row
                    for (int j = 0; j < testcell.length; j++) {
                        String str1 = testcell[j].getContents();

                        if(str1 != null && !"".equals(str1)){

                            int cheader = str1.indexOf("**");
                            int length = str1.length();
                            //get table name
                            if(cheader != -1){
                                header = str1.substring(cheader+2, length);
                                String sql = "delete from "+header;
                                excute(sql);
                            }
                        }

                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }







    //2、测试类



        /**
         * @param args
         */
        public static void main(String[] args) {
            ImpExeclHelper readData = new ImpExeclHelper();

            //造数据----------------------------------------------------------
            String path = "E:/09.testdata/xxx.xls";

            readData.createData(path, "yxl");

//删除数据
      readData.deleteDataByid(path, "md_data");
//        readData.deleteData(path, "delete_data");
        }

}

