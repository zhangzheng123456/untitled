package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.controller.AppBottomIconCfgController;
import com.bizvane.ishop.dao.AppBottomIConCfgMapper;
import com.bizvane.ishop.dao.AppBottomIConCfgMapper;
import com.bizvane.ishop.entity.AppBottomIConCfg;
import com.bizvane.ishop.entity.AppBottomIConCfg;
import com.bizvane.ishop.service.AppBottomIconCfgService;
import com.bizvane.ishop.utils.CheckUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2017/2/21.
 */
@Service
public class AppBottomIconCfgServiceImpl implements AppBottomIconCfgService {

    @Autowired
    AppBottomIConCfgMapper appBottomIConCfgMapper;
    private static final Logger logger = Logger.getLogger(AppBottomIconCfgServiceImpl.class);

    @Override
    public AppBottomIConCfg getAppBottomIConCfgById(int id) throws Exception {
        AppBottomIConCfg appHelp = appBottomIConCfgMapper.selAppBottomIConCfgById(id);
        return appHelp;
    }

    @Override
    public PageInfo<AppBottomIConCfg> getAppBottomIConCfgByPage(int page_number, int page_size, String search_value) throws Exception {
        List<AppBottomIConCfg> appHelps;
        PageHelper.startPage(page_number, page_size);
        appHelps = appBottomIConCfgMapper.selectAllCfg( search_value);

        for (AppBottomIConCfg appHelps1 : appHelps) {
            String infos="[";
            appHelps1.setIsactive(CheckUtils.CheckIsactive(appHelps1.getIsactive()));
            String info=appHelps1.getCfg_info();
            JSONObject obj= JSON.parseObject(info);
            //vips":"Y","community":"Y","achievement":"Y","goods":"Y"}


            if(obj.containsKey("vips")){
                if(obj.getString("vips").equals("Y")){
                    infos=infos+"会员,";
                }

            }  if(obj.containsKey("community")){
                if(obj.getString("community").equals("Y")){
                    infos=infos+"社区,";
                }

            }  if(obj.containsKey("achievement")){
                if(obj.getString("achievement").equals("Y")){
                    infos=infos+"业绩,";
                }

            } if(obj.containsKey("goods")){
                if(obj.getString("goods").equals("Y")){
                    infos=infos+"商品,";
                }

            }
                infos=infos+"我的"+"]";

            appHelps1.setIcon_name(infos);

        }

        PageInfo<AppBottomIConCfg> page = new PageInfo<AppBottomIConCfg>(appHelps);

        return page;
    }

    @Override
    public String insert(String message, String user_id) throws Exception {
        String status = "";
        JSONObject jsonObject = JSONObject.parseObject(message);
        String cfg_info1= jsonObject.get("cfg_info").toString();

        JSONObject obj2=JSON.parseObject(cfg_info1);
       String m1=obj2.getString("vips");
        String   m2=obj2.getString("community");
        String m3=obj2.getString("achievement");
        String  m4=obj2.getString("goods");


        StringBuffer buffer = new StringBuffer();
        buffer.append("{\"vips\":" +  "\""+ m1+"\""+ ","
                + "\"community\":" + "\""+m2+"\""+ ","
                + "\"achievement\":" +"\""+m3+"\""+ ","
                + "\"goods\":" + "\""+m4+"\""+ "}");

        String corp_code = jsonObject.get("corp_code").toString().trim();

        String isactive = jsonObject.get("isactive").toString().trim();
        Date now = new Date();

        AppBottomIConCfg appBottomIConCfg1= this.getAppBottomIConCfgByCorp(Common.IS_ACTIVE_Y,corp_code);
        if(appBottomIConCfg1==null){
            AppBottomIConCfg appBottomIConCfg=new AppBottomIConCfg();
            appBottomIConCfg.setCorp_code(corp_code);
            appBottomIConCfg.setCfg_info(buffer.toString());
            appBottomIConCfg.setModified_date(Common.DATETIME_FORMAT.format(now));
            appBottomIConCfg.setModifier(user_id);
            appBottomIConCfg.setIsactive(isactive);
            appBottomIConCfg.setCreater(user_id);
            appBottomIConCfg.setCreated_date(Common.DATETIME_FORMAT.format(now));
          int m=  appBottomIConCfgMapper.insertAppBottomIConCfg(appBottomIConCfg);

                AppBottomIConCfg appBottomIConCfg2= this.getAppBottomIConCfgByCorp(Common.IS_ACTIVE_Y,corp_code);
                if(appBottomIConCfg2!=null) {
                    status = appBottomIConCfg2.getId();
                }


        }else{
            status="该企业已存在模块配置";
        }

        return status;
    }

    @Override
    public String update(String message, String user_id) throws Exception {
        String status = "";
        JSONObject jsonObject = JSONObject.parseObject(message);
        Date now = new Date();

        String ids = jsonObject.get("id").toString().trim();
        String cfg_info1 = jsonObject.get("cfg_info").toString().trim();

        JSONObject obj2=JSON.parseObject(cfg_info1);
        String m1=obj2.getString("vips");
        String   m2=obj2.getString("community");
        String m3=obj2.getString("achievement");
        String  m4=obj2.getString("goods");


        StringBuffer buffer = new StringBuffer();
        buffer.append("{\"vips\":" +  "\""+ m1+"\""+ ","
                + "\"community\":" + "\""+m2+"\""+ ","
                + "\"achievement\":" +"\""+m3+"\""+ ","
                + "\"goods\":" + "\""+m4+"\""+ "}");
        String corp_code = jsonObject.get("corp_code").toString().trim();
        String isactive = jsonObject.get("isactive").toString().trim();

        AppBottomIConCfg appBottomIConCfg3= this.getAppBottomIConCfgByCorp(Common.IS_ACTIVE_Y,corp_code);

        if(appBottomIConCfg3==null||appBottomIConCfg3.getId().equals(ids)){
          //  logger.info("============cfg_info============="+cfg_info);
            AppBottomIConCfg appBottomIConCfg=new AppBottomIConCfg();
            appBottomIConCfg.setId(ids);
            appBottomIConCfg.setCorp_code(corp_code);
            appBottomIConCfg.setCfg_info(buffer.toString());
            appBottomIConCfg.setModified_date(Common.DATETIME_FORMAT.format(now));
            appBottomIConCfg.setModifier(user_id);
            appBottomIConCfg.setIsactive(isactive);
            int m=   appBottomIConCfgMapper.updateAppBottomIConCfg(appBottomIConCfg);

              if(m>0){
                  status=Common.DATABEAN_CODE_SUCCESS;
              }else{
                  status=Common.DATABEAN_CODE_ERROR;
              }

        }else {
            status="该企业已存在模块配置";
        }

        return status;
    }

    @Override
    public int delete(int id) throws Exception {
        return appBottomIConCfgMapper.delAppBottomIConCfgById(id);
    }

    @Override
    public PageInfo<AppBottomIConCfg> getAppBottomIConCfgScreen(int page_number, int page_size, Map<String, String> map) throws Exception {
         Map<String, Object> params = new HashMap<String, Object>();
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<AppBottomIConCfg> list1 = appBottomIConCfgMapper.selectAppBottomIConCfgScreen(params);
        for (AppBottomIConCfg appHelp : list1) {
            String infos="[";
            appHelp.setIsactive(CheckUtils.CheckIsactive(appHelp.getIsactive()));
            String info=appHelp.getCfg_info();
            JSONObject obj= JSON.parseObject(info);
            //vips":"Y","community":"Y","achievement":"Y","goods":"Y"}

            if(obj.containsKey("vips")){
                infos=infos+"会员,";
            }else  if(obj.containsKey("community")){
                infos=infos+"社区,";
            } else if(obj.containsKey("achievement")){
                infos=infos+"业绩,";
            }else if(obj.containsKey("goods")){
                infos=infos+"商品,";
            }else{
                infos=infos+"我的"+"]";
            }
            appHelp.setIcon_name(infos);
        }
        PageInfo<AppBottomIConCfg> page = new PageInfo<AppBottomIConCfg>(list1);
        return page;
    }

    @Override
    public AppBottomIConCfg getAppBottomIConCfgByCorp(String isactive, String corp_code) throws Exception {
        return appBottomIConCfgMapper.selAppBottomIConCfgByCode(isactive,corp_code);
    }

    @Override
    public List<AppBottomIConCfg> getListByCorp(String isactive, String corp_code) throws Exception {
        return appBottomIConCfgMapper.getListByCorp(isactive,corp_code);
    }

}

