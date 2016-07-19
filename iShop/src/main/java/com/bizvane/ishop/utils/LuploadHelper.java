package com.bizvane.ishop.utils;

import com.bizvane.ishop.bean.DataBean;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * Created by yin on 2016/7/12.
 */
public class LuploadHelper {
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
}
