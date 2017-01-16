package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.service.CRMInterfaceService;
import com.bizvane.ishop.service.VipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;


/**
 * Created by ZhouZhou on 2017/1/16.
 */
@Service
public class VipServiceImpl implements VipService {

    @Autowired
    CRMInterfaceService crmInterfaceService;

    public String addVip(HashMap<String,Object> vipInfo) throws Exception{
        String result = crmInterfaceService.addVip(vipInfo);

        System.out.println("===="+result);

        JSONArray array = JSONArray.parseArray(result);
        JSONObject result_obj = array.getJSONObject(0);

        String code = result_obj.getString("code");
        if (code.equals("0")){
            result = result_obj.getString("rows");
        }else {
            return Common.DATABEAN_CODE_ERROR;
        }
        return result;
    }

}
