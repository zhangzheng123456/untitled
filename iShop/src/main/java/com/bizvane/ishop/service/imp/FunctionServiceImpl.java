package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.dao.FunctionMapper;
import com.bizvane.ishop.entity.Action;
import com.bizvane.ishop.entity.Function;
import com.bizvane.ishop.service.FunctionService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
@Service
public class FunctionServiceImpl implements FunctionService{

    @Autowired
    FunctionMapper functionMapper;

    public JSONArray selectAllFunctions(int user_id,String role_code){
        List<Function> func_info = functionMapper.selectAllFun(user_id,role_code);
        JSONArray modules = new JSONArray();
        String uri;
        if(role_code.contains("R10")){
            uri = "official";}
        else{
            uri = "common";}
        for (int i = 0; i < func_info.size(); i++) {
            String module = func_info.get(i).getModule_name();
            String func = func_info.get(i).getFunction_name();
            System.out.println(module+"---------"+func);
            if(func.equals("0")){
                JSONObject obj1 = new JSONObject();
                obj1.put("mod_name",module);
                obj1.put("uri",uri);
                modules.add(obj1);
            }else {
                if (modules.size()==0){
                    JSONObject obj = new JSONObject();
                    JSONArray functions = new JSONArray();
                    JSONObject obj1 = new JSONObject();
                    obj1.put("fun_name",func);
                    functions.add(obj1);
                    obj.put("mod_name",module);
                    obj.put("uri",uri);
                    obj.put("functions",functions);
                    modules.add(obj);
                }else {
                    for (int j = 0; j < modules.size(); j++) {
                        JSONObject object = (JSONObject) modules.get(j);
                        if (object.get("mod_name").equals(module)) {
                            String a = object.get("functions").toString();
                            JSONArray qq = JSONArray.parseArray(a);
                            JSONObject obj2 = new JSONObject();
                            obj2.put("fun_name", func);
                            qq.add(obj2);
                            object.put("functions",qq);
                            break;
                        }else {
                            if (j==modules.size()-1){
                                JSONObject mod = new JSONObject();
                                JSONArray functions = new JSONArray();
                                mod.put("mod_name",module);
                                mod.put("uri",uri);
                                mod.put("functions",functions);
                                modules.add(mod);
                            }
                            continue;}
                    }
                }
            }
        }return modules;
    }

    public String selectAllFunction(int user_id,String role_code){
        List<Function> act_info = functionMapper.selectAllFunction(user_id,role_code);

        return "";
    }
}
