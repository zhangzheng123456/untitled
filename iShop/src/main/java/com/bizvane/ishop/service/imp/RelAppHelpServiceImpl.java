package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.RelAppHelpMapper;
import com.bizvane.ishop.entity.RelAppHelp;
import com.bizvane.ishop.service.RelAppHelpService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OssUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2017/2/23.
 */
@Service
public class RelAppHelpServiceImpl implements RelAppHelpService{

    @Autowired
    RelAppHelpMapper relAppHelpMapper;

    @Override
    public RelAppHelp getRelAppHelpById(int id) throws Exception {
        RelAppHelp relAppHelp=relAppHelpMapper.selectById(id);
        return relAppHelp;
    }

    @Override
    public PageInfo<RelAppHelp> getAllRelAppHelpByPage(int page_number, int page_size, String search_value) throws Exception {
        List<RelAppHelp> relAppHelps;
        PageHelper.startPage(page_number, page_size);
        relAppHelps = relAppHelpMapper.selectAllRelAppHelp(search_value);
        for (RelAppHelp relAppHelps1 : relAppHelps) {
            relAppHelps1.setIsactive(CheckUtils.CheckIsactive(relAppHelps1.getIsactive()));
        }
        PageInfo<RelAppHelp> page = new PageInfo<RelAppHelp>(relAppHelps);

        return page;
    }

    @Override
    public List<RelAppHelp> getRelAppHelpList( String isactive) throws Exception {
        return relAppHelpMapper.selectRelHelps(isactive);
    }

    @Override
    public String insert(String message, String user_id,HttpServletRequest request) throws Exception {
        String status = "";
        JSONObject jsonObject = JSONObject.parseObject(message);
        Date now = new Date();
        String app_help_code = jsonObject.get("app_help_code").toString().trim();

        RelAppHelp relAppHelp = WebUtils.JSON2Bean(jsonObject, RelAppHelp.class);
//富文本上传图片
        String app_help_content=relAppHelp.getApp_help_content();
                List<String> htmlImageSrcList = OssUtils.getHtmlImageSrcList(app_help_content);

        OssUtils ossUtils = new OssUtils();
        String bucketName = "products-image";
        String path = "";
        path = request.getSession().getServletContext().getRealPath("/");
        for (int k = 0; k < htmlImageSrcList.size(); k++) {
            String time = "AppHelp/" + "_" + Common.DATETIME_FORMAT_DAY_NUM.format(new Date()) + ".jpg";
            if (!htmlImageSrcList.get(k).contains("image/upload")) {
                continue;
            }
            ossUtils.putObject(bucketName, time, path + "/" + htmlImageSrcList.get(k));
            app_help_content = app_help_content.replace(htmlImageSrcList.get(k), "http://" + bucketName + ".oss-cn-hangzhou.aliyuncs.com/" + time);
            LuploadHelper.deleteFile(path + "/" + htmlImageSrcList.get(k));
        }

        RelAppHelp relAppHelp1 = this.getByHelpTitle( relAppHelp.getApp_help_code(), relAppHelp.getApp_help_title(), relAppHelp.getIsactive());
        int num = 0;
        String rel_help_code= "RAP" + Common.DATETIME_FORMAT_DAY_NUM.format(now);
        if (relAppHelp1 ==null||(relAppHelp1.getId().equals(relAppHelp.getId()))) {
            relAppHelp.setApp_help_code(app_help_code);
            relAppHelp.setModified_date(Common.DATETIME_FORMAT.format(now));
            relAppHelp.setRel_help_code(rel_help_code);
            relAppHelp.setApp_help_content(app_help_content);
            relAppHelp.setCreater(user_id);
            relAppHelp.setModifier(user_id);
            relAppHelp.setCreated_date(Common.DATETIME_FORMAT.format(now));
            num = relAppHelpMapper.insertRelAppHelp(relAppHelp);
            if (num > 0) {
               RelAppHelp vipRules2 = this.getByHelpTitle( relAppHelp.getApp_help_code(), relAppHelp.getApp_help_title(), relAppHelp.getIsactive());
                status = String.valueOf(vipRules2.getId());
                return status;
            } else {
                status = Common.DATABEAN_CODE_ERROR;
            }
        }else{
            status = "该企业的帮助功能下已存在该标题";

        }
        return status;
    }

    @Override
    public String update(String message, String user_id,HttpServletRequest request) throws Exception {
        String status = "";
        JSONObject jsonObject = JSONObject.parseObject(message);

        int id = Integer.parseInt(jsonObject.get("id").toString());
        String app_help_code = jsonObject.get("app_help_code").toString().trim();
        Date now = new Date();
        RelAppHelp relAppHelp = WebUtils.JSON2Bean(jsonObject, RelAppHelp.class);

        //富文本上传图片
        String app_help_content=relAppHelp.getApp_help_content();
        List<String> htmlImageSrcList = OssUtils.getHtmlImageSrcList(app_help_content);

        OssUtils ossUtils = new OssUtils();
        String bucketName = "products-image";
        String path = "";
        path = request.getSession().getServletContext().getRealPath("/");
        for (int k = 0; k < htmlImageSrcList.size(); k++) {
            String time = "AppHelp/" + relAppHelp.getRel_help_code()+ "_" + Common.DATETIME_FORMAT_DAY_NUM.format(new Date()) + ".jpg";
            if (!htmlImageSrcList.get(k).contains("image/upload")) {
                continue;
            }
            ossUtils.putObject(bucketName, time, path + "/" + htmlImageSrcList.get(k));
            app_help_content = app_help_content.replace(htmlImageSrcList.get(k), "http://" + bucketName + ".oss-cn-hangzhou.aliyuncs.com/" + time);
            LuploadHelper.deleteFile(path + "/" + htmlImageSrcList.get(k));
        }

        RelAppHelp relAppHelp1 = this.getByHelpTitle(relAppHelp.getApp_help_code(),relAppHelp.getApp_help_title(),relAppHelp.getIsactive());
        if (relAppHelp1==null||relAppHelp1.getId().equals(relAppHelp.getId())) {

            relAppHelp.setApp_help_code(app_help_code);
            relAppHelp.setApp_help_content(app_help_content);
            relAppHelp.setModifier(user_id);
            relAppHelp.setModified_date(Common.DATETIME_FORMAT.format(now));
            int info = 0;
            info = relAppHelpMapper.updateRelAppHelp(relAppHelp);
            if (info > 0) {
                status = Common.DATABEAN_CODE_SUCCESS;
            } else {
                status = "编辑失败";
            }
        } else {
            status = "该企业已存在该帮助标题";
        }
        return status;


    }

    @Override
    public int delete(int id) throws Exception {
        return relAppHelpMapper.deleteById(id);
    }

    @Override
    public PageInfo<RelAppHelp> getAllRelAppHelpsScreen(int page_number, int page_size, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<RelAppHelp> list1 = relAppHelpMapper.selectRelAppHelpScreen(params);
        for (RelAppHelp relAppHelp1 : list1) {
            relAppHelp1.setIsactive(CheckUtils.CheckIsactive(relAppHelp1.getIsactive()));
        }
        PageInfo<RelAppHelp> page = new PageInfo<RelAppHelp>(list1);
        return page;
    }




    @Override
    public RelAppHelp getByHelpTitle( String app_help_code, String app_help_title, String isactive) throws Exception {
        return relAppHelpMapper.selectByAppHelp(  app_help_code, app_help_title, isactive);

    }

    @Override
    public List<RelAppHelp> getByAppHelpCode( String app_help_code) throws Exception {
        return relAppHelpMapper.selectByAppHelpCode(app_help_code);
    }
}
