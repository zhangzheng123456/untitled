package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.StoreAchvGoal;
import com.bizvane.ishop.entity.TableManager;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.StoreAchvGoalService;
import com.bizvane.ishop.service.TableManagerService;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.TimeUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.github.pagehelper.PageInfo;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 店铺业绩目标管理
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
@Controller
@RequestMapping("/storeAchvGoal")
public class StoreAchvGoalController {

    private static Logger logger = LoggerFactory.getLogger((UserController.class));

    @Autowired
    StoreAchvGoalService storeAchvGoalService = null;
    @Autowired
    FunctionService functionService = null;
    @Autowired
    private TableManagerService managerService;
    String id;

    /**
     * 用户管理
     * 列表展示
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String getStoreAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();

            String function_code = request.getParameter("funcCode");
            int user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);

            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<StoreAchvGoal> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = storeAchvGoalService.selectBySearch(page_number, page_size, "", "","","");
            } else if(role_code.equals(Common.ROLE_GM)){
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code,"","", "");
            }else if(role_code.equals(Common.ROLE_AM)){
                String area_code = request.getSession().getAttribute("area_code").toString();
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code,area_code,"", "");
            }else{
                String store_code = request.getSession().getAttribute("store_code").toString();
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code,"",store_code, "");
            }
            result.put("list", JSON.toJSONString(list));
            result.put("actions", actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
            logger.info(e.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺业绩目标修改或添加
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addStoreAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String corp_code = request.getSession(false).getAttribute("corp_code").toString();

        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            StoreAchvGoal storeAchvGoal1 = new StoreAchvGoal();
            storeAchvGoal1.setCorp_code(jsonObject.get("corp_code").toString());
            storeAchvGoal1.setStore_name(jsonObject.get("store_name").toString());
            storeAchvGoal1.setStore_code(jsonObject.get("store_code").toString());
            storeAchvGoal1.setTarget_amount(jsonObject.get("achv_goal").toString());
            String achv_type = jsonObject.get("achv_type").toString();
            storeAchvGoal1.setTime_type(achv_type);

            if (achv_type.equals(Common.TIME_TYPE_WEEK)) {
                String time = jsonObject.get("end_time").toString();
                String week = TimeUtils.getWeek(time);
                storeAchvGoal1.setTarget_time(week);
            } else {
                storeAchvGoal1.setTarget_time(jsonObject.get("end_time").toString());
            }
            Date now = new Date();
            storeAchvGoal1.setModifier(user_id);
            storeAchvGoal1.setModified_date(Common.DATETIME_FORMAT.format(now));
            storeAchvGoal1.setCreater(user_id);
            storeAchvGoal1.setCreated_date(Common.DATETIME_FORMAT.format(now));
            storeAchvGoal1.setIsactive(jsonObject.get("isactive").toString());
            String result = String.valueOf(storeAchvGoalService.insert(storeAchvGoal1));
            if (result.equals(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("add success");
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            ex.printStackTrace();
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺业绩目标
     * 选择业绩
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String editbefore(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int store_id = Integer.parseInt(jsonObject.get("id").toString());
            StoreAchvGoal storeAchvGoal = storeAchvGoalService.selectlById(store_id);
//            JSONObject result = new JSONObject();
//            result.put("storeAchvGoal", storeAchvGoal);
            org.json.JSONObject result = new org.json.JSONObject();
            result.put("storeAchvGoal", JSON.toJSONString(storeAchvGoal));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(Common.DATABEAN_CODE_ERROR);
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺业绩目标编辑
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String editStoreAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = WebUtils.getValueForSession(request, "user_code");
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);

            StoreAchvGoal storeAchvGoal = new StoreAchvGoal();
            storeAchvGoal.setId(Integer.parseInt(jsonObject.get("id").toString()));
            storeAchvGoal.setCorp_code(jsonObject.get("corp_code").toString());
            storeAchvGoal.setStore_name(jsonObject.get("store_name").toString());
            storeAchvGoal.setStore_code(jsonObject.get("store_code").toString());
            storeAchvGoal.setTarget_amount(jsonObject.get("achv_goal").toString());
            String achv_type = jsonObject.get("achv_type").toString();
            storeAchvGoal.setTime_type(achv_type);
            String time = jsonObject.get("end_time").toString();
            if (achv_type.equals(Common.TIME_TYPE_WEEK)) {
                String week = TimeUtils.getWeek(time);
                storeAchvGoal.setTarget_time(week);
            } else {
                storeAchvGoal.setTarget_time(time);
            }
            Date now = new Date();
            storeAchvGoal.setModifier(user_id);
            storeAchvGoal.setModified_date(Common.DATETIME_FORMAT.format(now));

            storeAchvGoal.setIsactive(jsonObject.get("isactive").toString());
            String result = String.valueOf(storeAchvGoalService.update(storeAchvGoal));
            if (result.equals(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("add success");
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("edit success ");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 店铺业绩目标删除
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
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String storeAchvGoal_id = jsonObject.get("id").toString();
            String[] ids = storeAchvGoal_id.split(",");
            for (int i = 0; ids != null && i < ids.length; i++) {
                storeAchvGoalService.deleteById(Integer.parseInt(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("scuccess");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 页面查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            int user_id = Integer.parseInt(request.getSession().getAttribute("user_code").toString());

            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<StoreAchvGoal> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = storeAchvGoalService.selectBySearch(page_number, page_size, "", "","",search_value);
            } else if(role_code.equals(Common.ROLE_GM)){
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code,"","", search_value);
            }else if(role_code.equals(Common.ROLE_AM)){
                String area_code = request.getSession().getAttribute("area_code").toString();
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code,area_code,"", search_value);
            }else{
                String store_code = request.getSession().getAttribute("store_code").toString();
                list = storeAchvGoalService.selectBySearch(page_number, page_size, corp_code,"",store_code, search_value);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
    /***
     * 查出要导出的列
     */
    @RequestMapping(value = "getCols", method = RequestMethod.POST)
    public String selAllByCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String function_code = jsonObject.get("function_code").toString();
            List<TableManager> tableManagers = managerService.selAllByCode(function_code);
            JSONObject result = new JSONObject();
            result.put("tableManagers", JSON.toJSONString(tableManagers));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }
    /***
     * 导出数据
     */
    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            PageInfo<StoreAchvGoal> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = storeAchvGoalService.selectBySearch(1, 10000, "", "","","");
            } else if(role_code.equals(Common.ROLE_GM)){
                list = storeAchvGoalService.selectBySearch(1, 10000, corp_code,"","", "");
            }else if(role_code.equals(Common.ROLE_AM)){
                String area_code = request.getSession().getAttribute("area_code").toString();
                list = storeAchvGoalService.selectBySearch(1, 10000, corp_code,area_code,"", "");
            }else{
                String store_code = request.getSession().getAttribute("store_code").toString();
                list = storeAchvGoalService.selectBySearch(1, 10000, corp_code,"",store_code, "");
            }
            List<StoreAchvGoal> storeAchvGoals = list.getList();
            String column_name = jsonObject.get("column_name").toString();
            String[] cols = column_name.split(",");//前台传过来的字段
            OutExeclHelper.OutExecl(storeAchvGoals,cols,response);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("word success");
        }catch (Exception e){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(e.getMessage());
        }
        return dataBean.getJsonStr();
    }
    /***
     * Execl增加
     */
    @RequestMapping(value="/addByExecl",method = RequestMethod.POST)
    @ResponseBody
    @Transactional()
    public String addByExecl(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file, ModelMap model) throws SQLException {
        DataBean dataBean = new DataBean();
        File targetFile = LuploadHelper.lupload(request, file, model);
        String user_id = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession(false).getAttribute("corp_code").toString();

        String result = "";
        try {
            Workbook rwb = Workbook.getWorkbook(targetFile);
            Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
            int clos = rs.getColumns();//得到所有的列
            int rows = rs.getRows();//得到所有的行
            Cell[] column = rs.getColumn(2);
            for (int i = 3; i <column.length; i++) {
                if(!column[i].getContents().toString().equals("D")||!column[i].getContents().toString().equals("W")||!column[i].getContents().toString().equals("M")||!column[i].getContents().toString().equals("Y")){
                    result ="第"+(i+1)+"列的业绩日期类型缩写不对";
                    int b=5/0;
                    break;
                }
            }
            for(int i=3;i < rows;i++) {
                for (int j = 0; j < clos; j++) {
                    StoreAchvGoal storeAchvGoal=new StoreAchvGoal();
                    storeAchvGoal.setCorp_code(corp_code);
                    storeAchvGoal.setStore_code(rs.getCell(j++,i).getContents());
                    storeAchvGoal.setTarget_amount(rs.getCell(j++,i).getContents());
                    storeAchvGoal.setTime_type(rs.getCell(j++,i).getContents());
                    storeAchvGoal.setTarget_time(rs.getCell(j++,i).getContents());
                    if(rs.getCell(j++,i).getContents().toString().toUpperCase().equals("Y")){
                        storeAchvGoal.setIsactive("Y");
                    }else{
                        storeAchvGoal.setIsactive("N");
                    }
                    storeAchvGoal.setCreater(user_id);
                    Date now = new Date();
                    storeAchvGoal.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    result=String.valueOf(storeAchvGoalService.insert(storeAchvGoal));
                }
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(result);
        }
        return dataBean.getJsonStr();
    }
}
