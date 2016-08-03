package com.bizvane.ishop.utils;

import com.bizvane.ishop.bean.DataBean;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

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

    /***
     * 验证execl中唯一的列是否有重复值
     */
    public  static  String CheckOnly(Cell[] cells){
        String result="";
        LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
        for (Cell cell: cells) {
            Integer num = map.get(cell.getContents().toString());
            num = null == num ? 1 : num + 1;
            map.put(cell.getContents().toString(), num);
        }
        if (cells.length != map.size())
        {
            result="存在重复值";
        }
        return result;
    }

    /***
     * 把jxl.jar中日期类型进行转换
     */
    public  static String getCellTypeForDate(Cell cellObject) {
        String dateStr="";
        if(cellObject.getType()== CellType.DATE){
            DateCell cellValue   =   (DateCell)cellObject;
            Date dt   =   cellValue.getDate();
            SimpleDateFormat formatter   =   new   SimpleDateFormat("yyyy-MM-dd");
            dateStr  =  formatter.format(dt);
        }
        return dateStr;
    }


}
