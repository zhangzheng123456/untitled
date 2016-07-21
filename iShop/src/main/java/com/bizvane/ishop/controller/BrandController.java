package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Area;
import com.bizvane.ishop.entity.Brand;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.TableManager;
import com.bizvane.ishop.service.BrandService;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.TableManagerService;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.v1.common.Data;
import com.github.pagehelper.PageInfo;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.log4j.Logger;
import org.json.JSONObject;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/brand")
public class BrandController {

    String id;

    @Autowired
    private BrandService brandService;
    @Autowired
    private FunctionService functionService;
    @Autowired
    private TableManagerService managerService;
    private static final Logger logger = Logger.getLogger(BrandController.class);


    /**
     * 品牌列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String brandManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            int user_id = Integer.parseInt(request.getSession().getAttribute("user_id").toString());
            String role_code = request.getSession().getAttribute("role_code").toString();
            String group_code = request.getSession().getAttribute("group_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);
            JSONObject result = new JSONObject();
            PageInfo<Brand> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = brandService.getAllBrandByPage(page_number, page_size, "", "");
            } else {
                list = brandService.getAllBrandByPage(page_number, page_size, corp_code, "");
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
        }
        return dataBean.getJsonStr();
    }

    /**
     * 品牌新增
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addBrand(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--brand add-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();

            String result = brandService.insert(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("add success");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 品牌编辑
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editBrand(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession().getAttribute("user_code").toString();
        try {
            String jsString = request.getParameter("param");
            logger.info("json--brand edit-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            String result = brandService.update(message, user_id);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId(id);
                dataBean.setMessage("edit success");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
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
    public String delete(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String jsString = request.getParameter("param");
            logger.info("json-- delete-------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String brand_id = jsonObject.get("id").toString();
            String[] ids = brand_id.split(",");
            String msg = null;
            for (int i = 0; i < ids.length; i++) {
                logger.info("-------------delete--" + Integer.valueOf(ids[i]));
                Brand brand = brandService.getBrandById(Integer.valueOf(ids[i]));
                if (brand != null) {
                    logger.info("------------得到brand" + brand.getId());
                    String brand_code = brand.getBrand_code();
                    String corp_code = brand.getCorp_code();
                    logger.info("-------------获取企业店铺之前---" + corp_code);
                    int count = 0;
                    count = brandService.getGoodsCount(corp_code, brand_code);
                    if (count > 0) {
                        msg = "有使用品牌" + brand_code + "的商品，请先行处理！";
                        break;
                    }
                    count = brandService.getStoresCount(corp_code, brand_code);
                    if (count > 0) {
                        msg = "有使用品牌" + brand_code + "的店铺，请先行处理！";
                        break;
                    }
                }
                brandService.delete(Integer.valueOf(ids[i]));
            }
            if (msg == null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("删除成功！");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId(id);
                dataBean.setMessage(msg);
            }
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            return dataBean.getJsonStr();
        }
        logger.info("delete-----" + dataBean.getJsonStr());
        return dataBean.getJsonStr();
    }

    /**
     * 品牌管理
     * 选择品牌
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    @ResponseBody
    public String findById(HttpServletRequest request) {
        DataBean bean = new DataBean();
        String data = null;
        try {
            String jsString = request.getParameter("param");

            logger.info("json-select-------------" + jsString);
            System.out.println("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            String user_id = jsonObject.get("id").toString();
            data = JSON.toJSONString(brandService.getBrandById(Integer.parseInt(user_id)));
            bean.setCode(Common.DATABEAN_CODE_SUCCESS);
            bean.setId("1");
            bean.setMessage(data);
        } catch (Exception e) {
            bean.setCode(Common.DATABEAN_CODE_ERROR);
            bean.setId("1");
            bean.setMessage("品牌信息异常");
        }
        logger.info("info-----" + bean.getJsonStr());
        return bean.getJsonStr();
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
            String search_value = jsonObject.get("searchValue").toString();

            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<Brand> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                //系统管理员
                list = brandService.getAllBrandByPage(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession().getAttribute("corp_code").toString();
                list = brandService.getAllBrandByPage(page_number, page_size, corp_code, search_value);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/Brand_codeExist", method = RequestMethod.POST)
    @ResponseBody
    public String Brand_codeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String brand_code = jsonObject.get("brand_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            Brand brand = brandService.getBrandByCode(corp_code, brand_code);
            if (brand != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("品牌编号已被使用！！！");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("品牌编号不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/Brand_nameExist", method = RequestMethod.POST)
    @ResponseBody
    public String Brand_nameExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String brand_name = jsonObject.get("brand_name").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            Brand brand = brandService.getBrandByName(corp_code, brand_name);
            if (brand != null) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("品牌名称已被使用！！！");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("品牌名称不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
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
        String errormessage="";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String role_code = request.getSession().getAttribute("role_code").toString();
            String corp_code = request.getSession().getAttribute("corp_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<Brand> list;
            if(screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    //系统管理员
                    list = brandService.getAllBrandByPage(1, 30000, "", search_value);
                } else {
                    list = brandService.getAllBrandByPage(1, 30000, corp_code, search_value);
                }
            }else{
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = brandService.getAllBrandScreen(1, 30000, "", map);
                } else {
                    list = brandService.getAllBrandScreen(1, 30000, corp_code, map);
                }
            }
            List<Brand> brands = list.getList();
            if(brands.size()>=29999){
                errormessage="导出数据过大";
                int i=9/0;
            }
            String column_name = jsonObject.get("column_name").toString();
            String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(brands, cols, response, request);
            JSONObject result = new JSONObject();
            if(pathname==null||pathname.equals("")){
                errormessage="数据异常，导出失败";
                int a=8/0;
            }
            result.put("path",JSON.toJSONString("lupload/"+pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
        }
        return dataBean.getJsonStr();
    }

    /***
     * Execl增加
     */
    @RequestMapping(value = "/addByExecl", method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    @Transactional()
    public String addByExecl(HttpServletRequest
                                     request, @RequestParam(value = "file", required = false) MultipartFile file, ModelMap model) throws SQLException {
        DataBean dataBean = new DataBean();
        File targetFile = LuploadHelper.lupload(request, file, model);
        String user_id = request.getSession().getAttribute("user_code").toString();
        String result = "";
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        try {
            Workbook rwb = Workbook.getWorkbook(targetFile);
            Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
            int clos = rs.getColumns();//得到所有的列
            int rows = rs.getRows();//得到所有的行
            if(rows>9999){
                result="数据量过大，导入失败";
                int i=5 /0;
            }
            Pattern pattern = Pattern.compile("B\\d{4}");
            Cell[] column = rs.getColumn(0);
            for (int i = 3; i < column.length; i++) {
                Matcher matcher = pattern.matcher(column[i].getContents().toString());
                if (matcher.matches() == false) {
                    result = "第" + (i + 1) + "列品牌编号格式不对";
                    int b = 5 / 0;
                    break;
                }
                Brand brand = brandService.getBrandByCode(corp_code, column[i].getContents().toString());
                if (brand != null) {
                    result = "第" + (i + 1) + "列品牌编号已存在";
                    int b = 5 / 0;
                    break;
                }
            }
            Cell[] column1 = rs.getColumn(1);
            for (int i = 3; i < column.length; i++) {
                Brand brand = brandService.getBrandByName(corp_code, column1[i].getContents().toString());
                if (brand != null) {
                    result = "第" + (i + 1) + "列品牌名称已存在";
                    int b = 5 / 0;
                    break;
                }
            }
            for (int i = 3; i < rows; i++) {
                for (int j = 0; j < clos; j++) {
                    Brand brand = new Brand();
                    brand.setBrand_code(rs.getCell(j++, i).getContents());
                    brand.setBrand_name(rs.getCell(j++, i).getContents());
                    if (rs.getCell(j++, i).getContents().toString().toUpperCase().equals("Y")) {
                        brand.setIsactive("Y");
                    } else {
                        brand.setIsactive("N");
                    }
                    brand.setCorp_code(corp_code);
                    Date now = new Date();
                    brand.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    brand.setCreater(user_id);
                    brand.setModified_date(Common.DATETIME_FORMAT.format(now));
                    brand.setModifier(user_id);
                    result = brandService.insertExecl(brand);
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
        }
        return dataBean.getJsonStr();
    }

    /**
     * 品牌筛选
     */
    @RequestMapping(value = "/screen", method = RequestMethod.POST)
    @ResponseBody
    public String Screen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            logger.info("json---------------" + jsString);
            JSONObject jsonObj = new JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
//            String screen = jsonObject.get("screen").toString();
//            JSONObject jsonScreen = new JSONObject(screen);
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
            String role_code = request.getSession().getAttribute("role_code").toString();
            JSONObject result = new JSONObject();
            PageInfo<Brand> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = brandService.getAllBrandScreen(page_number, page_size, "", map);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = brandService.getAllBrandScreen(page_number, page_size, corp_code, map);
            }
            result.put("list", JSON.toJSONString(list));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage() + ex.toString());
        }
        return dataBean.getJsonStr();
    }
}
