package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.StoreAchvGoal;
import com.bizvane.ishop.entity.TableManager;
import com.bizvane.ishop.entity.UserAchvGoal;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.TableManagerService;
import com.bizvane.ishop.service.UserAchvGoalService;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.TimeUtils;
import com.github.pagehelper.PageInfo;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.log4j.Logger;
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
import java.util.Random;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
@Controller
@RequestMapping("/userAchvGoal")
public class UserAchvGoalControl {
    private static final Logger logger = Logger.getLogger(LoginController.class);

    @Autowired
    private UserAchvGoalService userAchvGoalService = null;
    @Autowired
    private FunctionService functionService = null;
    @Autowired
    private TableManagerService managerService;
    String id;

    /**
     * 用户管理
     * 用户业绩目标的列表展示
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String userAchvGoalManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String group_code = request.getSession(false).getAttribute("group_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String function_code = request.getParameter("funcCode").toString();
            JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);

            org.json.JSONObject result = new org.json.JSONObject();

            int page_number = Integer.parseInt(request.getParameter("pageNumber").toString());
            int page_size = Integer.parseInt(request.getParameter("pageSize").toString());
            PageInfo<UserAchvGoal> pages = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                pages = userAchvGoalService.selectBySearch(page_number, page_size, "", "");
            } else if (role_code.equals(Common.ROLE_GM)) {
                pages = this.userAchvGoalService.selectBySearch(page_number, page_size, corp_code, "");
            } else if (role_code.equals(Common.ROLE_AM)) {
                String area_code = request.getSession(false).getAttribute("area_code").toString();
                pages = this.userAchvGoalService.selectBySearchPart(page_number, page_size, corp_code, "", "", area_code, Common.ROLE_AM);
            } else if (role_code.equals(Common.ROLE_SM)) {
                String store_code = request.getSession(false).getAttribute("store_code").toString();
                pages = this.userAchvGoalService.selectBySearchPart(page_number, page_size, corp_code, "", store_code, "", Common.ROLE_SM);
            }else {
                List<UserAchvGoal> goal = userAchvGoalService.userAchvGoalExist(corp_code,user_code);
                pages = new PageInfo<UserAchvGoal>();
                pages.setList(goal);
            }
            result.put("list", JSON.toJSONString(pages));
            result.put("actions", actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 用户业绩目标
     * 编辑
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String editUserAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObject = JSONObject.parseObject(jsString);
            id = jsonObject.get("id").toString();
            String message = jsonObject.get("message").toString();
            JSONObject jsonObj = JSONObject.parseObject(message);

            UserAchvGoal userAchvGoal = new UserAchvGoal();
            userAchvGoal.setId(Integer.parseInt(jsonObj.get("id").toString()));
            userAchvGoal.setCorp_code(jsonObj.get("corp_code").toString());
            userAchvGoal.setStore_code(jsonObj.get("store_code").toString());
            userAchvGoal.setUser_code(jsonObj.get("user_code").toString());
            userAchvGoal.setUser_target(jsonObj.get("achv_goal").toString());
            String achv_type = jsonObj.get("achv_type").toString();
            userAchvGoal.setTarget_type(achv_type);

            if (achv_type.equals(Common.TIME_TYPE_WEEK)) {
                String time = jsonObj.get("end_time").toString();
                String week = TimeUtils.getWeek(time);
                userAchvGoal.setTarget_time(week);
            } else {
                userAchvGoal.setTarget_time(jsonObj.get("end_time").toString());
            }
            Date now = new Date();
            userAchvGoal.setModifier(user_id);
            userAchvGoal.setModified_date(Common.DATETIME_FORMAT.format(now));
            userAchvGoal.setIsactive(jsonObj.get("isactive").toString());
            userAchvGoalService.updateUserAchvGoal(userAchvGoal);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("edit success");
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
            logger.info(e.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 用户业绩目标
     * 删除
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String deleteUserAchvGoalById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id  = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            String user_id = jsonObject.get("id").toString();
            String[] ids = user_id.split(",");
            for (int i = 0; i < ids.length; i++) {
                userAchvGoalService.deleteUserAchvGoalById(ids[i]);
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("success");
        } catch (SQLException e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(e.getMessage());
            e.printStackTrace();
            logger.info(e.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 用户业绩目标
     * 添加
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String insertUserAchvGoal(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--user add-------------" + jsString);
            System.out.println("json---------------" + jsString);

            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            UserAchvGoal userAchvGoal = new UserAchvGoal();
            userAchvGoal.setCorp_code(jsonObject.get("corp_code").toString());
            userAchvGoal.setStore_code(jsonObject.get("store_code").toString());
            userAchvGoal.setUser_code(jsonObject.get("user_code").toString());
            userAchvGoal.setUser_target(jsonObject.get("achv_goal").toString());
            String achv_type = jsonObject.get("achv_type").toString();
            userAchvGoal.setTarget_type(achv_type);

            if (achv_type.equals(Common.TIME_TYPE_WEEK)) {
                String time = jsonObject.get("end_time").toString();
                String week = TimeUtils.getWeek(time);
                userAchvGoal.setTarget_time(week);
            } else {
                userAchvGoal.setTarget_time(jsonObject.get("end_time").toString());
            }
            Date now = new Date();
            userAchvGoal.setModifier(user_id);
            userAchvGoal.setModified_date(Common.DATETIME_FORMAT.format(now));
            userAchvGoal.setCreater(user_id);
            userAchvGoal.setCreated_date(Common.DATETIME_FORMAT.format(now));
            userAchvGoal.setIsactive(jsonObject.get("isactive").toString());

            userAchvGoalService.insert(userAchvGoal);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage("add SUCCESS！");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 用户业绩目标管理
     * 选择用户业绩目标
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String data = null;
        try {

            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String userAchvGoalId = jsonObject.get("id").toString();
            data = JSON.toJSONString(userAchvGoalService.getUserAchvGoalById(Integer.parseInt(userAchvGoalId)));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(data);
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            logger.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 用户业绩管理
     * 页面查找
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public String search(HttpServletRequest request) {
        String id = "";
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<UserAchvGoal> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                //系统管理员
                list = userAchvGoalService.selectBySearch(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_GM)) {
                    list = userAchvGoalService.selectBySearch(page_number, page_size, corp_code, search_value);
                } else if (role_code.equals(Common.ROLE_AM)) {
                    String area_code = request.getSession(false).getAttribute("area_code").toString();
                    list = this.userAchvGoalService.selectBySearchPart(page_number, page_size, corp_code, search_value, "", area_code, Common.ROLE_AM);
                } else if (role_code.equals(Common.ROLE_SM)) {
                    String store_code = request.getSession(false).getAttribute("store_code").toString();
                    list = this.userAchvGoalService.selectBySearchPart(page_number, page_size, corp_code, search_value, store_code, "", Common.ROLE_SM);
                }
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
    @RequestMapping(value = "/getCols", method = RequestMethod.POST)
    @ResponseBody
    public String selAllByCode(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String function_code = jsonObject.get("function_code").toString();
            List<TableManager> tableManagers = managerService.selAllByCode(function_code);
            org.json.JSONObject result = new org.json.JSONObject();
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
            PageInfo<UserAchvGoal> pages = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                pages = userAchvGoalService.selectBySearch(1, 10000, "", "");
            } else if (role_code.equals(Common.ROLE_GM)) {
                pages = this.userAchvGoalService.selectBySearch(1, 10000, corp_code, "");
            } else if (role_code.equals(Common.ROLE_AM)) {
                String area_code = request.getSession(false).getAttribute("area_code").toString();
                pages = this.userAchvGoalService.selectBySearchPart(1, 10000, corp_code, "", "", area_code, Common.ROLE_AM);
            } else if (role_code.equals(Common.ROLE_SM)) {
                String store_code = request.getSession(false).getAttribute("store_code").toString();
                pages = this.userAchvGoalService.selectBySearchPart(1, 10000, corp_code, "", store_code, "", Common.ROLE_SM);
            }
            List<UserAchvGoal> userAchvGoals = pages.getList();
            String column_name = jsonObject.get("column_name").toString();
            String[] cols = column_name.split(",");//前台传过来的字段
            OutExeclHelper.OutExecl(userAchvGoals,cols,response,request);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
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
            Cell[] column = rs.getColumn(3);
            for (int i = 3; i <column.length; i++) {
                if(!column[i].getContents().toString().equals("D")||!column[i].getContents().toString().equals("W")||!column[i].getContents().toString().equals("M")||!column[i].getContents().toString().equals("Y")){
                    result ="第"+(i+1)+"列的业绩日期类型缩写不对";
                    int b=5/0;
                    break;
                }
            }
            for(int i=3;i < rows;i++) {
                for (int j = 0; j < clos; j++) {
                    UserAchvGoal userAchvGoal=new UserAchvGoal();
                    userAchvGoal.setCorp_code(corp_code);
                    userAchvGoal.setStore_code(rs.getCell(j++,i).getContents());
                    userAchvGoal.setUser_code(rs.getCell(j++,i).getContents());
                    userAchvGoal.setUser_target(rs.getCell(j++,i).getContents());
                    userAchvGoal.setTarget_type(rs.getCell(j++,i).getContents());
                    userAchvGoal.setTarget_time(rs.getCell(j++,i).getContents());
                    if(rs.getCell(j++,i).getContents().toString().toUpperCase().equals("Y")){
                        userAchvGoal.setIsactive("Y");
                    }else{
                        userAchvGoal.setIsactive("N");
                    }
                    userAchvGoal.setCreater(user_id);
                    Date now = new Date();
                    userAchvGoal.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    result=String.valueOf(userAchvGoalService.insert(userAchvGoal));
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
