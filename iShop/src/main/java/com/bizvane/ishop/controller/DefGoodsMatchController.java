package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.DefGoodsMatch;

import com.bizvane.ishop.service.DefGoodsMatchService;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by PC on 2016/11/24.
 */
@Controller
@RequestMapping("/defmatch")
public class DefGoodsMatchController {
    @Autowired
    private DefGoodsMatchService defGoodsMatchService;
    String id;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String getList(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            JSONObject result = new JSONObject();
            List<DefGoodsMatch> defGoodsMatches;
            if (role_code.equals(Common.ROLE_SYS)) {
                defGoodsMatches = defGoodsMatchService.selectMatchGoods("");
            } else if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                defGoodsMatches = defGoodsMatchService.selectMatchGoods("",manager_corp);
            }else {
                defGoodsMatches = defGoodsMatchService.selectMatchGoods(corp_code);
            }
            result.put("list", JSON.toJSONString(defGoodsMatches));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String searchAll(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            JSONObject result = new JSONObject();
            List<DefGoodsMatch> defGoodsMatches;
            if (role_code.equals(Common.ROLE_SYS)) {
                defGoodsMatches = defGoodsMatchService.selMatchBySeach("",search_value);
            } else if(role_code.equals(Common.ROLE_CM)){
                String manager_corp = request.getSession().getAttribute("manager_corp").toString();
                System.out.println("manager_corp=====>"+manager_corp);
                defGoodsMatches = defGoodsMatchService.selMatchBySeach("",search_value,manager_corp);
            }else {
                defGoodsMatches = defGoodsMatchService.selMatchBySeach(corp_code,search_value);
            }
            result.put("list", JSON.toJSONString(defGoodsMatches));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }
    @RequestMapping(value = "/getMatchByCode", method = RequestMethod.POST)
    @ResponseBody
    public String getMatchlist(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String goods_match_code = jsonObject.get("goods_match_code").toString();
            List<DefGoodsMatch> taskAllocations = defGoodsMatchService.selectMatchByCode(corp_code, goods_match_code);
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(taskAllocations));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 删除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");

            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String list = jsonObject.get("list").toString();
            JSONArray jsonArray = JSON.parseArray(list);
            int msg = 0;
            for (int i = 0; i < jsonArray.size(); i++) {
                String str = jsonArray.get(i).toString();
         //       System.out.println("=========split========"+str);
                com.alibaba.fastjson.JSONObject object = JSON.parseObject(str);
           //     System.out.println("=========object========"+object.toJSONString());
                String corp_code = object.get("corp_code").toString();
                String goods_match_code = object.get("goods_match_code").toString();
             //   System.out.println("=============商品搭配============="+corp_code+"--------------------"+goods_match_code);
                msg+=  defGoodsMatchService.delMatchByCode(corp_code,goods_match_code);
            }
            if (msg >0) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("删除成功");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("删除失败");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }

        return dataBean.getJsonStr();
    }

    @RequestMapping(value = "/addMatch", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addMatch(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        int count = 0;
        try {
            String user_code = request.getSession().getAttribute("user_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String goods_match_code =sdf.format(new Date()) + Math.round(Math.random() * 9);
            String goods_code = jsonObject.get("goods_code").toString();
            String goods_match_title = jsonObject.get("goods_match_title").toString();
            String goods_match_desc = jsonObject.get("goods_match_desc").toString();
            String goods_match_display = jsonObject.get("match_display").toString();
            String isactive = jsonObject.get("isactive").toString();
            System.out.println("---------111isactive1111-------"+isactive);
            String[] split = goods_code.split(",");
            if (split.length > 10){
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("最多只能添加10个商品");
                return dataBean.getJsonStr();
            }
            for (int i=0;i<split.length;i++){
                DefGoodsMatch defGoodsMatch=new DefGoodsMatch();
                if(role_code.equals(Common.ROLE_SYS)) {
                    defGoodsMatch.setCorp_code("C10000");
                }else{
                    defGoodsMatch.setCorp_code(corp_code);
                }
                defGoodsMatch.setGoods_match_code(goods_match_code);
                defGoodsMatch.setGoods_match_title(goods_match_title);
                defGoodsMatch.setMatch_display(goods_match_display);
                defGoodsMatch.setGoods_match_desc(goods_match_desc);
                defGoodsMatch.setGoods_code(split[i]);
                Date date = new Date();
                defGoodsMatch.setCreated_date(Common.DATETIME_FORMAT.format(date));
                defGoodsMatch.setCreater(user_code);
                defGoodsMatch.setModified_date(Common.DATETIME_FORMAT.format(date));
                defGoodsMatch.setModifier(user_code);
                defGoodsMatch.setIsactive(isactive);
                count+= defGoodsMatchService.addMatch(defGoodsMatch);
            }
            if (count >0) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                com.alibaba.fastjson.JSONObject object=new com.alibaba.fastjson.JSONObject();
                if(role_code.equals(Common.ROLE_SYS)) {
                    object.put("corp_code","C10000");
                }else{
                    object.put("corp_code",corp_code);
                }
                object.put("goods_match_code",goods_match_code);
                dataBean.setMessage(object.toJSONString());
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("新增失败");
            }
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("新增失败");
        }
        return  dataBean.getJsonStr();
    }

    @RequestMapping(value = "/editMatch", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String editMatch(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        int count = 0;
        try {
            String user_code = request.getSession().getAttribute("user_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
          //  String goods_match_code =sdf.format(new Date()) + Math.round(Math.random() * 9);
            String corp_code_json = jsonObject.get("corp_code").toString();
            String goods_match_code = jsonObject.get("goods_match_code").toString();
            String goods_match_display = jsonObject.get("match_display").toString();
            String goods_match_title = jsonObject.get("goods_match_title").toString();
            String goods_match_desc = jsonObject.get("goods_match_desc").toString();
             int delCount=defGoodsMatchService.delMatchByCode(corp_code_json,goods_match_code);
            System.out.println("======删除关联商品========="+delCount);

            String goods_code = jsonObject.get("goods_code").toString();
            String isactive = jsonObject.get("isactive").toString();
            if(!goods_code.equals("")) {
                String[] split = goods_code.split(",");
                if (split.length > 10){
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId(id);
                    dataBean.setMessage("最多只能添加10个商品");
                    return dataBean.getJsonStr();
                }
                for (int i = 0; i < split.length; i++) {
                    DefGoodsMatch defGoodsMatch = new DefGoodsMatch();
                    defGoodsMatch.setCorp_code(corp_code_json);
                    defGoodsMatch.setCorp_code(corp_code_json);
                    defGoodsMatch.setGoods_match_code(goods_match_code);
                    defGoodsMatch.setGoods_match_title(goods_match_title);
                    defGoodsMatch.setMatch_display(goods_match_display);
                    defGoodsMatch.setGoods_match_desc(goods_match_desc);
                    defGoodsMatch.setGoods_code(split[i]);
                    Date date = new Date();
                    defGoodsMatch.setCreated_date(Common.DATETIME_FORMAT.format(date));
                    defGoodsMatch.setCreater(user_code);
                    defGoodsMatch.setModified_date(Common.DATETIME_FORMAT.format(date));
                    defGoodsMatch.setModifier(user_code);
                    defGoodsMatch.setIsactive(isactive);
                    count += defGoodsMatchService.addMatch(defGoodsMatch);
                }
            }else{
                count=1;
            }
            if (count >0) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("编辑成功");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage("编辑失败");
            }
        }catch (Exception ex){
            ex.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("编辑失败");
        }
        return  dataBean.getJsonStr();
    }

}
