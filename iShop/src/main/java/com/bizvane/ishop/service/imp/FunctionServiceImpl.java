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
        for (int i = 0; i < func_info.size(); i++) {
            String module = func_info.get(i).getModule_name();
            String func = func_info.get(i).getFunction_name();
            String func_code = func_info.get(i).getFunction_code();
            System.out.println(module+"---------"+func);
            if(func.equals("0")){
                JSONObject obj1 = new JSONObject();
                obj1.put("mod_name",module);
                obj1.put("func_code",func_code);
                modules.add(obj1);
            }else {
                if (modules.size()==0){
                    JSONObject obj = new JSONObject();
                    JSONArray functions = new JSONArray();
                    JSONObject obj1 = new JSONObject();
                    obj1.put("fun_name",func);
                    obj1.put("func_code",func_code);
                    functions.add(obj1);
                    obj.put("mod_name",module);
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
                            obj2.put("func_code", func_code);
                            qq.add(obj2);
                            object.put("functions",qq);
                            break;
                        }else {
                            if (j==modules.size()-1){
                                JSONObject mod = new JSONObject();
                                JSONArray functions = new JSONArray();
                                mod.put("mod_name",module);
                                mod.put("functions",functions);
                                modules.add(mod);
                            }
                            continue;}
                    }
                }
            }
        }return modules;
    }

    public JSONArray selectAllActions(int user_id,String role_code){
        List<Function> act_info = functionMapper.selectAllFunction(user_id,role_code);
        JSONArray functions = new JSONArray();

        for (int i = 0; i < act_info.size(); i++) {
            String func_code = act_info.get(i).getFunction_code();
     //       String func = act_info.get(i).getFunction_name();
            String act = act_info.get(i).getAction_name();
            System.out.println(act+"---------"+func_code);
            if (functions.size()==0){
                JSONObject obj = new JSONObject();
                JSONArray actions = new JSONArray();
                JSONObject obj1 = new JSONObject();
                obj1.put("act_name",act);
                actions.add(obj1);
                obj.put("func_code",func_code);
                obj.put("actions",actions);
                functions.add(obj);
            }else {
                for (int j = 0; j < functions.size(); j++) {
                    JSONObject object = (JSONObject) functions.get(j);
                    System.out.println(object);
                    if (object.get("func_code").equals(func_code)) {
                        String a = object.get("actions").toString();
                        JSONArray qq = JSONArray.parseArray(a);
                        JSONObject obj2 = new JSONObject();
                        obj2.put("act_name", act);
                        qq.add(obj2);
                        object.put("actions",qq);
                        break;
                    }else {
                        if (j==functions.size()-1){
                            JSONObject fun = new JSONObject();
                            JSONArray actions = new JSONArray();
                            fun.put("func_code",func_code);
                            fun.put("actions",actions);
                            functions.add(fun);
                        }continue;
                    }
                }
            }
        }return functions;
    }
}
