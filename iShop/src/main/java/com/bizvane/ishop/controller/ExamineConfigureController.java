package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/examine")
public class ExamineConfigureController {


    @Autowired
    ExamineConfigureService  examineConfigureService;
    @Autowired
    UserService userService;
    @Autowired
    FunctionService functionService;
    @Autowired
    BrandService brandService;
    @Autowired
    AreaService areaService;
    @Autowired
    StoreService storeService;
    @Autowired
    AppversionService appversionService;

    String id="";

    @RequestMapping(value = "/select",method = RequestMethod.POST)
    @ResponseBody
    public String selectExamine(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            int examine_id=Integer.parseInt(jsonObject.get("id").toString());
            ExamineConfigure examineConfigure=examineConfigureService.selectById(examine_id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(JSON.toJSONString(examineConfigure));
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/search",method = RequestMethod.POST)
    @ResponseBody
    public String selectAllExamine(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            int  page_number=Integer.parseInt(jsonObject.getString("page_num").toString());
            int page_size=Integer.parseInt(jsonObject.getString("page_size").toString());
            String search_value=jsonObject.getString("searchValue").toString();
            PageInfo<ExamineConfigure> list;
            if(role_code.equals(Common.ROLE_SYS)){
                list=examineConfigureService.selectAll("",search_value,page_number,page_size);
            }else{
                list=examineConfigureService.selectAll(corp_code,search_value,page_number,page_size);
            }
            JSONObject result=new JSONObject();
            result.put("list",JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public String deleteExamine(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            String integral_id=jsonObject.get("id").toString();
            String[] ids=integral_id.split(",");
            for (int i = 0; i <ids.length; i++) {
                String status = examineConfigureService.deleteById(Integer.parseInt(ids[i]));
                if (!status.equals(Common.DATABEAN_CODE_SUCCESS)){
                    dataBean.setId("1");
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage(status);
                    return  dataBean.getJsonStr();
                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("success");
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/screen",method = RequestMethod.POST)
    @ResponseBody
    public  String  screenExamine(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String corp_code=request.getSession().getAttribute("corp_code").toString();
            String role_code=request.getSession().getAttribute("role_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);

            int  page_number=Integer.parseInt(jsonObject.getString("page_num").toString());
            int page_size=Integer.parseInt(jsonObject.getString("page_size").toString());
            Map<String,Object> map =  WebUtils.Json2Map(jsonObject);
            PageInfo<ExamineConfigure> list;
            if(role_code.equals(Common.ROLE_SYS)){
                list=examineConfigureService.selectAllScreen("",map,page_number,page_size);
            }else{
                list=examineConfigureService.selectAllScreen(corp_code,map,page_number,page_size);
            }
            JSONObject result=new JSONObject();
            result.put("list",JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    //插入
    @RequestMapping(value = "/insert",method = RequestMethod.POST)
    @ResponseBody
    public String insertExamine(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String user_id=request.getSession().getAttribute("user_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String function_bill_name=jsonObject.getString("function_bill_name");
            String corp_code=jsonObject.getString("corp_code");
            ExamineConfigure examineConfigure1=examineConfigureService.selectByName(corp_code,function_bill_name);
            if(examineConfigure1!=null){
                dataBean.setId("1");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("功能单据已存在");
                return  dataBean.getJsonStr();
            }
            ExamineConfigure examineConfigure2= WebUtils.JSON2Bean(jsonObject, ExamineConfigure.class);
            examineConfigure2.setCorp_code(corp_code);
            examineConfigure2.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            examineConfigure2.setCreater(user_id);
            examineConfigure2.setModifier(user_id);
            examineConfigure2.setCreated_date(Common.DATETIME_FORMAT.format(new Date()));
            examineConfigureService.insertExamine(examineConfigure2);
            ExamineConfigure examineConfigure3= examineConfigureService.selectByName(corp_code,function_bill_name);
            JSONObject result=new JSONObject();
            result.put("id",examineConfigure3.getId());
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    //修改
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    public String updateExamine(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String user_id=request.getSession().getAttribute("user_code").toString();

            String jsString=request.getParameter("param").toString();
            JSONObject jsonObject1= JSON.parseObject(jsString);
            id=jsonObject1.get("id").toString();
            String message=jsonObject1.get("message").toString();
            JSONObject jsonObject=JSON.parseObject(message);
            String function_bill_name=jsonObject.getString("function_bill_name");
            String corp_code=jsonObject.getString("corp_code");
            int id1=Integer.parseInt(jsonObject.getString("id"));
            ExamineConfigure examineConfigure1=examineConfigureService.selectById(id1);
            if(!(examineConfigure1.getFunction_bill_name().equals(function_bill_name)&&examineConfigure1.getCorp_code().equals(corp_code))){
                ExamineConfigure examineConfigure2= examineConfigureService.selectByName(corp_code,function_bill_name);
                if(examineConfigure2!=null){
                    dataBean.setId("1");
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage("功能单据已存在");
                    return  dataBean.getJsonStr();
                }
            }

            ExamineConfigure examineConfigure3= WebUtils.JSON2Bean(jsonObject, ExamineConfigure.class);

            examineConfigure3.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            examineConfigure3.setModifier(user_id);
            String staus = examineConfigureService.updateExamine(examineConfigure3);
            if (!staus.equals(Common.DATABEAN_CODE_SUCCESS)){
                dataBean.setId("1");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(staus);
                return  dataBean.getJsonStr();
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("success");
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    //获取审核员工
    @RequestMapping(value = "/examineUsers",method = RequestMethod.POST)
    @ResponseBody
    public  String  getExamineUser(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String param = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(param);
            id = jsonObj.get("id").toString();

            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String function_name = jsonObject.get("function_name").toString();
            String corp_code_v2=jsonObject.getString("corp_code");

            if(StringUtils.isNotBlank(corp_code_v2)){
                corp_code=corp_code_v2;
            }

            //获取所有的
            List<Privilege> privileges=functionService.selectMasterCodeByFunctionName(function_name,"check");
            String[] privi_array=new String[privileges.size()];
            for (int i = 0; i < privileges.size(); i++) {
                privi_array[i]=privileges.get(i).getMaster_code();
            }

            System.out.println("privi_array>>>"+JSON.toJSONString(privi_array));

            if(privi_array.length==0||function_name.equals("")){
                JSONObject result=new JSONObject();
                result.put("list",JSON.toJSONString(new ArrayList<String>()));
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage(result.toString());
                return dataBean.getJsonStr();
            }

            //PageInfo<User> userPageInfo=new PageInfo<User>();
            List<User> userPageInfo=new ArrayList<User>();
            //根据权限获取权限下的所有员工
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                userPageInfo = userService.selectUserByMasterCode(corp_code, privi_array, null);
            } else if (role_code.equals(Common.ROLE_CM)) {
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                corp_code = WebUtils.getCorpCodeByCm(manager_corp, request.getSession().getAttribute("corp_code_cm"));
                userPageInfo = userService.selectUserByMasterCode(corp_code, privi_array, null);
            } else {
                 corp_code = request.getSession().getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_GM)) {
                    //企业管理员
                    userPageInfo = userService.selectUserByMasterCode(corp_code, privi_array, null);
                } else if (role_code.equals(Common.ROLE_BM)) {
                    //品牌管理员
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    PageInfo<Brand> allBrandByPage = brandService.getAllBrandByPage(1, 20, corp_code, "", "");
                    List<Brand>  list1 = allBrandByPage.getList();
                    if(list1.size()==1 && area_code.equals("")){
                        userPageInfo = userService.selectUserByMasterCode(corp_code, privi_array, null);
                    }else {
                        List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, area_code, brand_code, "", "");
                        String[] storeCodes = new String[stores.size()];
                        for (int i = 0; i < stores.size(); i++) {
                            storeCodes[i]= Common.SPECIAL_HEAD + stores.get(i).getStore_code()+",";
                        }
                        if (storeCodes.length>0){
                            userPageInfo = userService.selectUserByMasterCode(corp_code, privi_array, storeCodes);
                        }
                    }

                } else if (role_code.equals(Common.ROLE_SM)) {
                    //店长
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    store_code=store_code.replace(Common.SPECIAL_HEAD,"");
                    String[] storeCodes=store_code.split(",");
                    String[] storeCodesV2=new String[storeCodes.length];
                    for (int i = 0; i < storeCodes.length; i++) {
                        storeCodesV2[i]=Common.SPECIAL_HEAD + storeCodes[i]+",";
                    }
                    storeCodes=storeCodesV2;
                    userPageInfo = userService.selectUserByMasterCode(corp_code, privi_array, storeCodes);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    //区经
                    String area_code = request.getSession().getAttribute("area_code").toString();
                    String store_code = request.getSession().getAttribute("store_code").toString();
                    area_code = area_code.replace(Common.SPECIAL_HEAD, "");
                    String[] areaCodes = area_code.split(",");
                    String[] storeCodes = null;
                    if (!store_code.equals("")) {
                        store_code = store_code.replace(Common.SPECIAL_HEAD, "");
                        storeCodes = store_code.split(",");
                    }
                    String[] areaCodesV2=new String[areaCodes.length];
                    for (int i = 0; i < areaCodes.length; i++) {
                        areaCodesV2[i]=Common.SPECIAL_HEAD + areaCodes[i]+",";
                    }
                    List<Store> stores = storeService.selectByAreaBrand(corp_code, areaCodesV2, storeCodes, null, Common.IS_ACTIVE_Y);
                    String[] storeCodesV2 = new String[stores.size()];
                    for (int i = 0; i < stores.size(); i++) {
                        storeCodesV2[i]= Common.SPECIAL_HEAD + stores.get(i).getStore_code()+",";
                    }
                    if (storeCodesV2.length>0){
                        userPageInfo = userService.selectUserByMasterCode(corp_code, privi_array, storeCodesV2);
                    }
                }
            }
            JSONObject result=new JSONObject();
            result.put("list",JSON.toJSONString(userPageInfo));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }

    /**
     * 获取导航栏
     */
    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    @ResponseBody
    public String menu(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            List<String> nameList=new ArrayList<String>();
            nameList.add("会员任务");
            nameList.add("会员活动");
            nameList.add("群发消息");
            nameList.add("积分清零");
            nameList.add("积分调整");
            nameList.add("核准计划");

            JSONObject menus = new JSONObject();
            JSONArray menu;
            String user_id = request.getSession().getAttribute("user_id").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String user_type = request.getSession().getAttribute("user_type").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            menu = functionService.selectAllFunctions(corp_code,user_code, group_code, role_code);
            List<String> stringList=new ArrayList<String>();
            for (int i = 0; i < menu.size(); i++) {
                JSONObject jsonObject=menu.getJSONObject(i);
                String functions=jsonObject.getString("functions");
                if(StringUtils.isBlank(functions)){
                    String mod_name= jsonObject.getString("mod_name");
                    if(nameList.contains(mod_name))
                    stringList.add(mod_name);
                }else{
                    JSONArray jsonArray=JSONArray.parseArray(functions);
                    for (int j = 0; j <jsonArray.size() ; j++) {
                        JSONObject jsonObject1=jsonArray.getJSONObject(j);
                        String fun_name=jsonObject1.getString("fun_name");
                        if(nameList.contains(fun_name)){
                            stringList.add(fun_name);
                        }
                    }
                }
            }
            menus.put("menu", stringList);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(menus.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
        }
        return dataBean.getJsonStr();
    }

}
