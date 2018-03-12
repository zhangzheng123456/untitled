package com.bizvane.ishop.utils;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import org.apache.commons.lang.StringUtils;
import org.mongojack.Aggregation;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yin on 2016/7/12.
 */
public class LuploadHelper {
    /**
     * 上传execl文件
     */
    public static File lupload(HttpServletRequest request,MultipartFile file,ModelMap model){
        //创建你要保存的文件的路径
        String path = request.getSession().getServletContext().getRealPath("lupload");
        //获取该文件的文件名
        String fileName = file.getOriginalFilename();
        System.out.println(path);
        File targetFile = new File(path, fileName);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        // 保存
        try {
            file.transferTo(targetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //将该文件的路径给客户端，让其可以请求该wenjian
        model.addAttribute("fileUrl", request.getContextPath() + "/lupload/" + fileName);
        return targetFile;
    }

    public static File lupload2_zip(HttpServletRequest request,MultipartFile file){
        //创建你要保存的文件的路径
        String path = request.getSession().getServletContext().getRealPath("lupload");
        //获取该文件的文件名
        String fileName = file.getOriginalFilename();
        System.out.println(path);
        File targetFile = new File(path, fileName);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        // 保存
        try {
            file.transferTo(targetFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetFile;
    }


    /***
     * 验证execl中唯一的列是否有重复值
     */
    public  static  String CheckOnly(Cell[] cells){
        String result="";
        int cellCount=0;
        LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
        for (Cell cell: cells) {
            if(cell.getContents().toString().trim().equals("")){
                continue;
            }
            Integer num = map.get(cell.getContents().toString().trim());
            num = null == num ? 1 : num + 1;
            map.put(cell.getContents().toString().trim(), num);
            cellCount++;
        }
        if (cellCount != map.size())
        {
            result="存在重复值";
        }
        return result;
    }

    /***
     * 验证String数组是否有重复值
     */
    public  static  String CheckStringOnly(String[] strs){
        String result="";
        int strCount=0;
        LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
        for (String str: strs) {
            if(str.trim().equals("")){
                continue;
            }
            Integer num = map.get(str.trim());
            num = null == num ? 1 : num + 1;
            map.put(str.trim(), num);
            strCount++;
        }
        if (strCount != map.size())
        {
            result="存在重复值";
        }
        return result;
    }
    public static String checkDate(String date){
       // System.out.println("---------------------------:"+date);
        String el="(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)";
        Pattern p = Pattern.compile(el);
        Matcher m = p.matcher(date);
        boolean b = m.matches();
        if(b==true){
            return date;
        }else{
            return "导入格式错误";
        }
    }
    /***
     * 把jxl.jar中日期类型进行转换
     */
    public  static String getCellTypeForDate(Cell cellObject,String target_type) {
        String dateStr="格式错误";
       // System.out.println("------------时间---------:-"+cellObject.getContents().toString() +"------类型----------"+cellObject.getType());
        if(cellObject.getContents().toString().trim().equals("")){
            dateStr="";
        }else if(cellObject.getType()== CellType.DATE) {
            DateCell cellValue = (DateCell) cellObject;
            Date dt = cellValue.getDate();
            SimpleDateFormat formatter = null;
            if (target_type.equals("Y")) {
                formatter = new SimpleDateFormat("yyyy");
            } else if (target_type.equals("M")) {
                formatter = new SimpleDateFormat("yyyy-MM");
            } else {
                formatter = new SimpleDateFormat("yyyy-MM-dd");
            }
            dateStr = formatter.format(dt);
        }else{
            dateStr=cellObject.getContents().toString().trim();
        }
//        }else if(Integer.parseInt(cellObject.getContents().toString().trim()) < 2099 &&  Integer.parseInt(cellObject.getContents().toString().trim())>=2016){
//          //  DateCell cellValue   =   (DateCell)cellObject;
//            dateStr=cellObject.getContents().toString().trim();
//        }
        return dateStr;
    }
    //返回去掉空行的记录数
    public static int getRightRows(Sheet sheet) {
        int rsCols = sheet.getColumns(); //列数
        int rsRows = sheet.getRows(); //行数
        int nullCellNum;
        int afterRows = rsRows;
        for (int i = 1; i < rsRows; i++) { //统计行中为空的单元格数
            nullCellNum = 0;
            for (int j = 0; j < rsCols; j++) {
                String val = sheet.getCell(0, i).getContents().toString().trim();
                val = StringUtils.trimToEmpty(val);
                if (StringUtils.isBlank(val))
                    nullCellNum++;
            }
            if (nullCellNum >= rsCols) { //如果nullCellNum大于或等于总的列数
                afterRows--;          //行数减一
            }
        }
        return afterRows;
    }


    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

        /**
         * 删除目录及目录下的文件
         *
         * @param dir
         *            要删除的目录的文件路径
         * @return 目录删除成功返回true，否则返回false
         */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            System.out.println("删除目录失败：" + dir + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = LuploadHelper.deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = LuploadHelper.deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }



}
