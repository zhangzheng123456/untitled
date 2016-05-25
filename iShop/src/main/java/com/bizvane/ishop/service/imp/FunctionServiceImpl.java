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

    public String selectAllFunction(String user_id,String role_code){
        List<Function> func_info = functionMapper.selectAllFunction(user_id,role_code);
        JSONObject obj = new JSONObject();
        JSONArray modules = new JSONArray();
        String uri;
        if(role_code.contains("R10")){
            uri = "official";}
        else{
            uri = "common";}
        for (int i = 0; i < func_info.size(); i++) {
            String mod = func_info.get(i).getModule_name();
            String fun = func_info.get(i).getFunction_name();
            System.out.println(modules.size());
            JSONObject a = new JSONObject();
            if(modules.size()==0){
                a.put("module", mod);
                a.put("function", fun);
                a.put("uri", uri);
                modules.add(a);
            }
            for (int j = 0; j < modules.size(); j++) {
                System.out.println("-------------"+j);
                JSONObject object = (JSONObject) modules.get(j);
                System.out.println(fun+"-------------"+object.get("function"));
                if (object.get("function").equals(fun)) {
                   break;
                }else {
                    if (j==modules.size()-1) {
                        System.out.println(object.get("function"));
                        a.put("module", mod);
                        a.put("function", fun);
                        a.put("uri", uri);
                        modules.add(a);
                    }
                    continue;
                }
            }
        }
        JSONArray functions = new JSONArray();
        for (int i = 0; i < func_info.size(); i++) {
            String fun = func_info.get(i).getFunction_name();
            String act = func_info.get(i).getAction_name();
            JSONObject a = new JSONObject();
            a.put("function",fun);
            a.put("action",act);
            functions.add(i, a);
            System.out.println(functions.get(i));
        }
        obj.put("modules",modules);
        obj.put("functions",functions);
        return obj.toString();
    }

    public String selectAllFunctions(String user_id,String role_code){
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
            JSONObject obj = new JSONObject();
            if (modules.size()==0){
                obj.put("mod_name",module);
                modules.add(obj);
            }
            JSONArray functions = new JSONArray();
            for (int j = 0; j < modules.size(); j++) {
                JSONObject obj1 = new JSONObject();
                JSONObject object = (JSONObject) modules.get(j);
                System.out.println(func+"-------------"+object.get("function"));
                if (object.get("mod_name").equals(module)) {
                    obj1.put("fun_name",func);
                    functions.add(obj1);
                    object.put("functions",functions);
                    break;
                }else {
                    if (j==modules.size()-1){
                        obj.put("mod_name",module);
                        modules.add(obj);
                    }
                       continue;
                }
            }
        }
        return "";
    }

}
