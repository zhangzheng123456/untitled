package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.*;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/VIP")
public class VIPController {

    @Autowired
    private UserService userService;
    @Autowired
    private VipLabelService vipLabelService;
    @Autowired
    private FunctionService functionService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private CorpService corpService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private VipService vipService;
    @Autowired
    private VipRecordService vipRecordService;
    @Autowired
    private TableManagerService managerService;
    //    @Autowired
//    private VipLabelTypeService VipLabelTypeService;
    private static final Logger log = Logger.getLogger(LoginController.class);

    String id;

    /**
     * 会员列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String VIPManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String group_code = request.getSession(false).getAttribute("group_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();

            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);

            JSONObject result = new JSONObject();
            PageInfo<VIPInfo> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = vipService.selectBySearch(page_number, page_size, "", "");
            } else {
                list = vipService.selectBySearch(page_number, page_size, corp_code, "");
            }
            result.put("list", list);
            result.put("actions", actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员列表
     * 新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addVIP(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        try {
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            //  String message = jsonObj.getString("message");
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            VIPInfo vipInfo = WebUtils.JSON2Bean(jsonObject, VIPInfo.class);
            Date now = new Date();
            //      vipInfo.setRegister_time(Common.DATETIME_FORMAT.format(now));
            vipInfo.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipInfo.setModifier(user_id);
            vipInfo.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipInfo.setCreater(user_id);

            String existInfo1 = vipService.vipCodeExist(vipInfo.getVip_code(), vipInfo.getCorp_code());
            String existInfo2 = vipService.vipNameExist(vipInfo.getVip_name(), vipInfo.getCorp_code());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            if (existInfo1.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setMessage("VIP 用户编号已经存在！！！");
            } else if (existInfo2.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setMessage("VIP 用户名称已经存在！！！");
            } else {
                vipService.insert(vipInfo);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("add success!!!");
            }

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员列表
     * 编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editVIP(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String user_id = request.getSession(false).getAttribute("user_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.getString("message");
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            VIPInfo vipInfo = WebUtils.JSON2Bean(jsonObject, VIPInfo.class);
            vipInfo.setModifier(user_id);
            vipInfo.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
            String result = vipService.update(vipInfo);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("商品更改成功！！");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("edit error !!! ");
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 编辑前获取数据
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findVipById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String data = null;
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int vip_id = Integer.parseInt(jsonObject.getString("id"));
            VIPInfo vipInfo = vipService.getVipInfoById(vip_id);
            data = JSON.toJSONString(vipInfo);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(data);
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员列表
     * 查找
     */
    @RequestMapping(value = "/find", method = RequestMethod.POST)
    @ResponseBody
    public String findVIP(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.getString("message");
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_Number = jsonObject.getInt("pageNumber");
            int page_Size = jsonObject.getInt("pageSize");
            String search_value = jsonObject.getString("search_value").toString();
            String role_code = jsonObject.getString("role_code");
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<VIPInfo> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = vipService.selectBySearch(page_Number, page_Size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = vipService.selectBySearch(page_Number, page_Size, corp_code, search_value);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员标签管理
     * 列表
     */
    @RequestMapping(value = "/label/list", method = RequestMethod.GET)
    @ResponseBody
    public String VIPLabelManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String group_code = request.getSession(false).getAttribute("group_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);

            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<VipLabel> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = vipLabelService.selectBySearch(page_number, page_size, "", "");
            } else {
                list = vipLabelService.selectBySearch(page_number, page_size, corp_code, "");
            }
            result.put("list", JSON.toJSONString(list));
            result.put("actions", actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
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
    @RequestMapping(value = "/label/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "";
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<VipLabel> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = vipLabelService.selectBySearch(1, 30000, "", search_value);
                } else {
                    list = vipLabelService.selectBySearch(1, 30000, corp_code, search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = this.vipLabelService.selectAllVipScreen(1, 30000, "", map);
                } else {
                    list = this.vipLabelService.selectAllVipScreen(1, 30000, corp_code, map);
                }
            }
            List<VipLabel> vipLabels = list.getList();
            if (vipLabels.size() >= 29999) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            Map<String,String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(vipLabels, map, response, request);
            org.json.JSONObject result = new org.json.JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /***
     * Execl增加
     */
    @RequestMapping(value = "/label/addByExecl", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    @Transactional()
    public String addByExecl(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file, ModelMap model) throws SQLException {
        DataBean dataBean = new DataBean();
        File targetFile = LuploadHelper.lupload(request, file, model);
        String user_id = request.getSession().getAttribute("user_code").toString();
        String role_code = request.getSession(false).getAttribute("role_code").toString();
        String corp_code = request.getSession(false).getAttribute("corp_code").toString();
        String result = "";
        Workbook rwb=null;
        try {
            rwb = Workbook.getWorkbook(targetFile);
            Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
            int clos = rs.getColumns();//得到所有的列
            int rows = rs.getRows();//得到所有的行
            if(rows<4){
                result="请从模板第4行开始插入正确数据";
                int i=5/0;
            }
            if (rows > 9999) {
                result = "数据量过大，导入失败";
                int i = 5 / 0;
            }
            Cell[] column3 = rs.getColumn(0);
            Pattern pattern1 = Pattern.compile("C\\d{5}");
            if(!role_code.equals(Common.ROLE_SYS)){
                for (int i=3;i<column3.length;i++){
                    if(!column3[i].getContents().toString().equals(corp_code)){
                        result = "第" + (i + 1) + "行企业编号不存在";
                        int b = 5 / 0;
                        break;
                    }
                    Matcher matcher = pattern1.matcher(column3[i].getContents().toString());
                    if (matcher.matches() == false) {
                        result = "第" + (i + 1) + "行企业编号格式不对";
                        int b = 5 / 0;
                        break;
                    }
                }
            }
            for (int i = 3; i < column3.length; i++) {
                Matcher matcher = pattern1.matcher(column3[i].getContents().toString());
                if (matcher.matches() == false) {
                    result = "第" + (i + 1) + "行企业编号格式不对";
                    int b = 5 / 0;
                    break;
                }
                Corp corp = corpService.selectByCorpId(0, column3[i].getContents().toString());
                if (corp == null) {
                    result = "第" + (i + 1) + "行企业编号不存在";
                    int b = 5 / 0;
                    break;
                }

            }
            Cell[] column = rs.getColumn(1);
            for (int i = 3; i < column.length; i++) {
                String existInfo = this.vipLabelService.VipLabelNameExist(column3[i].getContents().toString(), column[i].getContents().toString());
                if (!existInfo.contains(Common.DATABEAN_CODE_SUCCESS)) {
                    result = "第" + (i + 1) + "列的会员标签已存在";
                    int b = 5 / 0;
                    break;
                }
            }
            for (int i = 3; i < rows; i++) {
                for (int j = 0; j < clos; j++) {
                    VipLabel vipLabel = new VipLabel();
                    vipLabel.setCorp_code(rs.getCell(j++, i).getContents());
                    vipLabel.setLabel_name(rs.getCell(j++, i).getContents());
                    if (role_code.equals(Common.ROLE_SYS)) {
                        vipLabel.setLabel_type("sys");
                    } else {
                        vipLabel.setLabel_type("org");
                    }
                    if (rs.getCell(j++, i).getContents().toString().toUpperCase().equals("N")) {
                        vipLabel.setIsactive("N");
                    } else {
                        vipLabel.setIsactive("Y");
                    }
                    Date now = new Date();
                    vipLabel.setCreater(user_id);
                    vipLabel.setModified_date(Common.DATETIME_FORMAT.format(now));
                    vipLabel.setModifier(user_id);
                    vipLabel.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    result = String.valueOf(vipLabelService.insert(vipLabel));
                }
            }

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result);
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(result);
        }finally {
            if(rwb!=null){
                rwb.close();
            }
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员标签管理
     * 新增
     */
    @RequestMapping(value = "/label/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addVIPLabel(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        try {
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String messsage = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(messsage);
            VipLabel vipLabel = WebUtils.JSON2Bean(jsonObject, VipLabel.class);
            Date now = new Date();
            vipLabel.setModified_date(Common.DATETIME_FORMAT.format(now));
            vipLabel.setModifier(user_id);
            vipLabel.setCreated_date(Common.DATETIME_FORMAT.format(now));
            vipLabel.setCreater(user_id);
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            if (Common.ROLE_SYS.equals(role_code)) {
                vipLabel.setLabel_type("sys");
            } else {
                vipLabel.setLabel_type("org");
            }
            String existInfo = vipLabelService.insert(vipLabel);
            if (existInfo.contains(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("标签名称未被使用！！！");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("标签名称已被使用！！！");
            }

        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员标签管理
     * 编辑
     */
    @RequestMapping(value = "/label/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editVIPLabel(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String role_code = request.getSession(false).getAttribute("role_code").toString();

        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonobj = new org.json.JSONObject(jsString);
            id = jsonobj.get("id").toString();
            String message = jsonobj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            VipLabel vipLabel = WebUtils.JSON2Bean(jsonObject, VipLabel.class);
            int label_id = vipLabel.getId();
            String label_type = vipLabelService.getVipLabelById(label_id).getLabel_type();
            if (!role_code.equals(Common.ROLE_SYS) && label_type.equals(Common.VIP_LABEL_TYPE_SYS)) {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("对不起，您不可以修改系统标签");
            } else {
                Date now = new Date();
                vipLabel.setModified_date(Common.DATETIME_FORMAT.format(now));
                vipLabel.setModifier(user_id);

                String result = vipLabelService.update(vipLabel);
                if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setMessage("更改成功");
                } else {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage(result);
                }
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("会员标签更新失败");
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 编辑标签前，获取数据
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/label/select", method = RequestMethod.POST)
    @ResponseBody
    public String findVipLabelById(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String data = null;
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int vip_tag_id = Integer.parseInt(jsonObject.getString("id"));
            VipLabel vipLabel = vipLabelService.getVipLabelById(vip_tag_id);
            data = JSON.toJSONString(vipLabel);
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(data);
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员标签管理
     * 查找
     */
    @RequestMapping(value = "/label/find", method = RequestMethod.GET)
    @ResponseBody
    public String findVIPLabel(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.getString("message");
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_Number = jsonObject.getInt("pageNumber");
            int page_Size = jsonObject.getInt("pageSize");
            String search_value = jsonObject.getString("search_value").toString();
            String role_code = jsonObject.getString("role_code");
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<VipLabel> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = this.vipLabelService.selectBySearch(page_Number, page_Size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = this.vipLabelService.selectBySearch(page_Number, page_Size, corp_code, search_value);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员标签类型管理
     * 删除
     */
    @RequestMapping(value = "/label/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String findVIPLabelDelete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String messageType_id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String[] ids = jsonObject.get("id").toString().split(",");
            for (int i = 0; ids != null && i < ids.length; i++) {
                this.vipLabelService.delete(Integer.parseInt(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("scuccess!!!!");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 会员标签类型管理
     * 获取标签类型
     */
    @RequestMapping(value = "/label/getTypes", method = RequestMethod.POST)
    @ResponseBody
    public String getTypes(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObject = new org.json.JSONObject(jsString);
            String message = jsonObject.get("message").toString();
            org.json.JSONObject jsonObject1 = new org.json.JSONObject(message);
            String type_name = jsonObject1.getString("type_name");

            org.json.JSONObject result = new org.json.JSONObject();

            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员标签管理
     * 查看是否有重复的标签
     */
//    @RequestMapping(value = "/label/VipLabelCodeExist", method = RequestMethod.POST)
//    @ResponseBody
//    public String VipLabelCodeExist(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObject1 = new org.json.JSONObject(jsString);
//            String message = jsonObject1.get("message").toString();
//            org.json.JSONObject jsonObject2 = new org.json.JSONObject(message);
//            String corp_code = jsonObject2.getString("corp_code");
//            String tag_code = jsonObject2.getString("tag_code");
//            String existInfo = this.vipLabelService.VipLabelCodeExist(corp_code, tag_code);
//            if (existInfo.contains(Common.DATABEAN_CODE_SUCCESS)) {
//                dataBean.setId(id);
//                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//                dataBean.setMessage("标签编号未被使用！！！");
//            } else {
//                dataBean.setId(id);
//                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//                dataBean.setMessage("标签编号已被使用！！！");
//            }
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.getMessage());
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

    /**
     * 会员标签管理
     * 判断企业内标签名称是否唯一
     */
    @RequestMapping(value = "/label/VipLabelNameExist")
    @ResponseBody
    public String VipLabelNameExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObject1 = new org.json.JSONObject(jsString);
            String message = jsonObject1.get("message").toString();
            org.json.JSONObject jsonObject2 = new org.json.JSONObject(message);
            String tag_name = jsonObject2.getString("tag_name");
            String corp_code = jsonObject2.getString("corp_code");
            String existInfo = this.vipLabelService.VipLabelNameExist(corp_code, tag_name);

            if (existInfo.contains(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("标签名称未被使用！！！");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("标签名称已被使用！！！");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 会员标签管理
     * 查找
     */
    @RequestMapping(value = "/label/find", method = RequestMethod.POST)
    @ResponseBody
    public String findVIPLabelFind(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_Number = jsonObject.getInt("pageNumber");
            int page_Size = jsonObject.getInt("pageSize");
            String search_value = jsonObject.getString("searchValue").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<VipLabel> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = this.vipLabelService.selectBySearch(page_Number, page_Size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = this.vipLabelService.selectBySearch(page_Number, page_Size, corp_code, search_value);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
    /**
     * 会员标签管理
     * 筛选
     */
    @RequestMapping(value = "/label/screen", method = RequestMethod.POST)
    @ResponseBody
    public String selectAllVipScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.getString("id");
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_Number = jsonObject.getInt("pageNumber");
            int page_Size = jsonObject.getInt("pageSize");
//            String screen = jsonObject.get("screen").toString();
//            org.json.JSONObject jsonScreen = new org.json.JSONObject(screen);
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession().getAttribute("role_code").toString();
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<VipLabel> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = this.vipLabelService.selectAllVipScreen(page_Number, page_Size, "", map);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = this.vipLabelService.selectAllVipScreen(page_Number, page_Size, corp_code, map);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


//
//    /**
//     * 会员标签类型管理
//     * 查找
//     */
//    @RequestMapping(value = "/label/type/find", method = RequestMethod.GET)
//    @ResponseBody
//    public String findVIPLabelTypeFind(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.getString("id");
//            String message = jsonObj.getString("message");
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            int page_Number = jsonObject.getInt("pageNumber");
//            int page_Size = jsonObject.getInt("pageSize");
//            String search_value = jsonObject.getString("search_value").toString();
//            String role_code = jsonObject.getString("role_code");
//            org.json.JSONObject result = new org.json.JSONObject();
//            PageInfo<VipLabelType> list;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                list = this.VipLabelTypeService.selectBySearch(page_Number, page_Size, "", search_value);
//            } else {
//                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//                list = this.VipLabelTypeService.selectBySearch(page_Number, page_Size, corp_code, search_value);
//            }
//            result.put("list", JSON.toJSONString(list));
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId(id);
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
//    /**
//     * 会员标签类型管理
//     * 列表
//     */
//    @RequestMapping(value = "/label/type/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String findVIPLabelType(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        try {
//            int user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
//            String role_code = request.getSession(false).getAttribute("role_code").toString();
//            String group_code = request.getSession(false).getAttribute("group_code").toString();
//            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//            String user_code = request.getSession().getAttribute("user_code").toString();
//
//            String function_code = request.getParameter("funcCode");
//            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
//            int page_size = Integer.parseInt(request.getParameter("pageSize"));
//            JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);
//            org.json.JSONObject result = new org.json.JSONObject();
//            PageInfo<VipLabelType> list;
//            if (role_code.equals(Common.ROLE_SYS)) {
//                list = this.VipLabelTypeService.selectBySearch(page_number, page_size, "", "");
//            } else {
//                list = VipLabelTypeService.selectBySearch(page_number, page_size, corp_code, "");
//            }
//            result.put("list", JSON.toJSONString(list));
//            result.put("actions", actions);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage(result.toString());
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId("1");
//            dataBean.setMessage(ex.getMessage());
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
//
//    /**
//     * 会员标签类型管理
//     * 添加
//     */
//    @RequestMapping(value = "/label/type/add", method = RequestMethod.POST)
//    @ResponseBody
//    @Transactional
//    public String findVIPLabelTypeAdd(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//
//        String id = "";
//        String user_id = request.getSession(false).getAttribute("user_code").toString();
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.getString("id");
//            String messsage = jsonObj.getString("message");
//            org.json.JSONObject jsonObject = new org.json.JSONObject(messsage);
//            VipLabelType VipLabelType = WebUtils.JSON2Bean(jsonObject, VipLabelType.class);
//            Date now = new Date();
//            VipLabelType.setModified_date(Common.DATETIME_FORMAT.format(now));
//            VipLabelType.setModifier(user_id);
//            VipLabelType.setCreated_date(Common.DATETIME_FORMAT.format(now));
//            VipLabelType.setCreater(user_id);
//            this.VipLabelTypeService.insert(VipLabelType);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setMessage("标签类型创建成功！！");
//            dataBean.setId(id);
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
//
//    /**
//     * 会员标签类型管理
//     * 删除
//     */
//    @RequestMapping(value = "/label/type/delete", method = RequestMethod.POST)
//    @ResponseBody
//    @Transactional
//    public String findVIPLabelTypeDelete(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String jsString = WebUtils.getValueForSession(request, "param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.get("id").toString();
//            String messageType_id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            String[] ids = messageType_id.split(",");
//            for (int i = 0; ids != null && i < ids.length; i++) {
//                this.VipLabelTypeService.deleteById(Integer.parseInt(ids[i]));
//            }
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setId("1");
//            dataBean.setMessage("scuccess!!!!");
//        } catch (Exception ex) {
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//
//
//    /**
//     * 会员标签类型管理
//     * 修改
//     */
//    @RequestMapping(value = "/label/type/edit", method = RequestMethod.POST)
//    @ResponseBody
//    @Transactional
//    public String findVIPLabelTypeEdit(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String user_id = request.getSession(false).getAttribute("user_code").toString();
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            String message = jsonObj.get("message").toString();
//            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            VipLabelType VipLabelType = WebUtils.JSON2Bean(jsonObject, VipLabelType.class);
//            VipLabelType.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
//            VipLabelType.setModifier(user_id);
//            String result = this.VipLabelTypeService.update(VipLabelType);
//
////            String existInfo = this.VipLabelTypeService.VipLabelTypeCodeExist(VipLabelType.getType_code(), corp_code);
////            if (!existInfo.contains(Common.DATABEAN_CODE_SUCCESS)) {
////                dataBean.setId(id);
////                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
////                dataBean.setMessage("edit success!!!s");
////            } else {
////                this.VipLabelTypeService.update(VipLabelType);
////                dataBean.setId(id);
////                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////                dataBean.setMessage("edit error !!!");
////            }
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.getMessage());
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }
//

    /***
     * 导出数据
     */
    @RequestMapping(value = "/callback/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExeclCallback(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "";
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<VipRecord> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = this.vipRecordService.selectBySearch(1, 30000, "", search_value);
                } else {

                    list = vipRecordService.selectBySearch(1, 30000, corp_code, search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.contains(Common.ROLE_SYS)) {
                    list = vipRecordService.selectAllVipRecordScreen(1, 30000, "", map);
                } else {
                    list = vipRecordService.selectAllVipRecordScreen(1, 30000, corp_code, map);
                }
            }
            List<VipRecord> vipRecords = list.getList();
            if (vipRecords.size() >= 29999) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            Map<String,String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(vipRecords, map, response, request);
            org.json.JSONObject result = new org.json.JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /**
     * 回访记录管理
     * 列表
     */
    @RequestMapping(value = "/callback/list", method = RequestMethod.GET)
    @ResponseBody
    public String callBackManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            int user_id = Integer.parseInt(request.getSession(false).getAttribute("user_id").toString());
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String group_code = request.getSession(false).getAttribute("group_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);

            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<VipRecord> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = this.vipRecordService.selectBySearch(page_number, page_size, "", "");
            } else {

                list = vipRecordService.selectBySearch(page_number, page_size, corp_code, "");
            }
            result.put("list", JSON.toJSONString(list));
            result.put("actions", actions);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

//    /**
//     * 回访记录管理
//     * 新增
//     */
//    @RequestMapping(value = "/callback/add", method = RequestMethod.POST)
//    @ResponseBody
//    @Transactional
//    public String addCallBack(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String user_id = request.getSession(false).getAttribute("user_code").toString();
//        String id = "";
//        try {
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            id = jsonObj.get("id").toString();
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            VipRecord VipRecord = WebUtils.JSON2Bean(jsonObject, VipRecord.class);
//            Date now = new Date();
//            VipRecord.setModified_date(Common.DATETIME_FORMAT.format(now));
//            VipRecord.setModifier(user_id);
//            this.vipRecordService.insert(VipRecord);
//            dataBean.setId(id);
//            dataBean.setMessage("add successs");
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setMessage(ex.getMessage());
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

    /**
     * 回访记录管理
     * 编辑前获取数据
     */

    @RequestMapping(value = "/callback/select", method = RequestMethod.POST)
    @ResponseBody
    public String selectCallBack(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String user_id = request.getSession(false).getAttribute("user_id").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            //  int VipRecord_id = Integer.parseInt(jsonObject.getString("id"));
            int VipRecord_id = jsonObject.getInt("id");
            VipRecord VipRecord = this.vipRecordService.getVipRecord(VipRecord_id);
            if (VipRecord != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(JSON.toJSONString(VipRecord));
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("not found VipRecord error !!!");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

//
//    /**
//     * 回访记录管理
//     * 编辑
//     */
//    @RequestMapping(value = "/callback/edit", method = RequestMethod.POST)
//    @ResponseBody
//    @Transactional
//    public String editCallBack(HttpServletRequest request) {
//        DataBean dataBean = new DataBean();
//        String id = "";
//        try {
//            String user_id = request.getSession(false).getAttribute("user_code").toString();
//            String jsString = request.getParameter("param");
//            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
//            String message = jsonObj.get("message").toString();
//            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
//            VipRecord VipRecord = WebUtils.JSON2Bean(jsonObject, VipRecord.class);
//            VipRecord.setModified_date(Common.DATETIME_FORMAT.format(new Date()));
//            VipRecord.setModifier(user_id);
//            this.vipRecordService.update(VipRecord);
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
//            dataBean.setMessage("edit success!!!s");
//        } catch (Exception ex) {
//            dataBean.setId(id);
//            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
//            dataBean.setMessage(ex.getMessage());
//            log.info(ex.getMessage());
//        }
//        return dataBean.getJsonStr();
//    }

    /**
     * 回访记录管理
     * 查找
     */
    @RequestMapping(value = "/callback/find", method = RequestMethod.POST)
    @ResponseBody
    public String findCallBack(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonobj = new org.json.JSONObject(jsString);
            String message = jsonobj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("searchValue").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<VipRecord> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = vipRecordService.selectBySearch(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = vipRecordService.selectBySearch(page_number, page_size, corp_code, search_value);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 回访记录管理
     * 筛选
     */
    @RequestMapping(value = "/callback/screen", method = RequestMethod.POST)
    @ResponseBody
    public String findCallBackScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonobj = new org.json.JSONObject(jsString);
            String message = jsonobj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
//            String screen = jsonObject.get("screen").toString();
//            org.json.JSONObject jsonScreen = new org.json.JSONObject(screen);
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<VipRecord> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = vipRecordService.selectAllVipRecordScreen(page_number, page_size, "", map);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = vipRecordService.selectAllVipRecordScreen(page_number, page_size, corp_code, map);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 回访记录管理
     * 删除
     */
    @RequestMapping(value = "/callback/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String callbackDelete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String[] ids = jsonObject.get("id").toString().split(",");
            for (int i = 0; ids != null && i < ids.length; i++) {
                this.vipRecordService.delete(Integer.parseInt(ids[i]));
            }
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("scuccess!!!!");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            log.info(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
}
