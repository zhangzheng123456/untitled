package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.Goods;
import com.bizvane.ishop.entity.TableManager;
import com.bizvane.ishop.service.FunctionService;
import com.bizvane.ishop.service.GoodsService;
import com.bizvane.ishop.service.TableManagerService;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.v1.common.Data;
import com.github.pagehelper.PageInfo;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouying on 2016-04-20.
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private FunctionService functionService;

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private TableManagerService managerService;
    String id;
    /**
     * 商品培训
     * 列表
     */
    @RequestMapping(value = "/fab/list", method = RequestMethod.GET)
    @ResponseBody
    public String goodsTrainManage(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String group_code = request.getSession(false).getAttribute("group_code").toString();
            String user_code = request.getSession(false).getAttribute("user_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();

            String function_code = request.getParameter("funcCode");
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            com.alibaba.fastjson.JSONArray actions = functionService.selectActionByFun(corp_code + user_code, corp_code + group_code, role_code, function_code);
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<Goods> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = this.goodsService.selectBySearch(page_number, page_size, "", "");
            } else {
                //   String corp_code = request.getParameter("corp_code");
                list = goodsService.selectBySearch(page_number, page_size, corp_code, "");
            }
            for (int i = 0; list.getList() != null && list.getList().size() > i; i++) {
                String goods_image = list.getList().get(i).getGoods_image();
                if (goods_image != null && !goods_image.isEmpty()) {
                    list.getList().get(i).setGoods_image(goods_image.split(",")[0]);
                }
            }
            result.put("list", JSON.toJSONString(list));
            result.put("actions", actions);
            dataBean.setId("1");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
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
    @RequestMapping(value = "/fab/exportExecl", method = RequestMethod.POST)
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

            PageInfo<Goods> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = this.goodsService.selectBySearch(1, 10000, "", "");
            } else {
                //   String corp_code = request.getParameter("corp_code");
                list = goodsService.selectBySearch(1, 10000, corp_code, "");
            }
            for (int i = 0; list.getList() != null && list.getList().size() > i; i++) {
                String goods_image = list.getList().get(i).getGoods_image();
                if (goods_image != null && !goods_image.isEmpty()) {
                    list.getList().get(i).setGoods_image(goods_image.split(",")[0]);
                }
            }
            List<Goods> goodses = list.getList();
            String column_name = jsonObject.get("column_name").toString();
            String[] cols = column_name.split(",");//前台传过来的字段
            OutExeclHelper.OutExecl(goodses,cols,response);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage("word success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 商品管理
     * 商品查找
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/fab/search", method = RequestMethod.GET)
    @ResponseBody
    public String selectBySearch(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            String search_value = jsonObject.get("searchValue").toString();
            PageInfo<Goods> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = goodsService.selectBySearch(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = goodsService.selectBySearch(page_number, page_size, corp_code, search_value);
            }
            for (int i = 0; list.getList() != null && list.getList().size() > i; i++) {
                String goods_image = list.getList().get(i).getGoods_image();
                if (goods_image != null && !goods_image.isEmpty()) {
                    list.getList().get(i).setGoods_image(goods_image.split(",")[0]);
                }
            }
            org.json.JSONObject result = new org.json.JSONObject();
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }
    /***
     * Execl增加用户
     */
    @RequestMapping(value="/addByExecl",method = RequestMethod.POST)
    @ResponseBody
    @Transactional()
    public String addByExecl(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file, ModelMap model) throws SQLException {
        DataBean dataBean = new DataBean();
        File targetFile = LuploadHelper.lupload(request, file, model);
        String user_id = request.getSession().getAttribute("user_id").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String result = "";
        try {
            Workbook rwb = Workbook.getWorkbook(targetFile);
            Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
            int clos = rs.getColumns();//得到所有的列
            int rows = rs.getRows();//得到所有的行
            Cell[] column = rs.getColumn(0);
            for (int i = 3; i <column.length; i++) {
                String goodsCodeExist = goodsService.goodsCodeExist(corp_code, column[i].getContents().toString());
                if(goodsCodeExist.contains(Common.DATABEAN_CODE_ERROR)){
                    result ="第"+(i+1)+"列商品编号已存在";
                    int b=5/0;
                    break;
                }
            }
            Cell[] column1 = rs.getColumn(1);
            for (int i = 3; i <column1.length; i++) {
                String goodsNameExist = goodsService.goodsNameExist(corp_code,column1[i].getContents().toString());
                if(goodsNameExist.contains(Common.DATABEAN_CODE_ERROR)){
                    result ="第"+(i+1)+"列商品名称已存在";
                    int b=5/0;
                    break;
                }
            }
            for(int i=3;i < rows;i++) {
                for (int j = 0; j < clos; j++) {
                    Goods goods=new Goods();
                    goods.setCorp_code(corp_code);
                    goods.setGoods_code(rs.getCell(j++,i).getContents());
                    goods.setGoods_name(rs.getCell(j++,i).getContents());
                    goods.setGoods_price(Float.parseFloat(rs.getCell(j++,i).getContents().toString()));
                    goods.setGoods_image(rs.getCell(j++,i).getContents());
                    goods.setGoods_quarter(rs.getCell(j++,i).getContents());
                    goods.setGoods_wave(rs.getCell(j++,i).getContents());
                    goods.setGoods_time(rs.getCell(j++,i).getContents());
                    goods.setGoods_description(rs.getCell(j++,i).getContents());
                    if(rs.getCell(j++,i).getContents().toString().toUpperCase().equals("Y")){
                        goods.setIsactive("Y");
                    }else{
                        goods.setIsactive("N");
                    }
                    goods.setCreater(user_id);
                    Date now = new Date();
                    goods.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    result = String.valueOf(goodsService.insert(goods));
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
    /**
     * 商品培训
     * 添加
     */
    @RequestMapping(value = "/fab/add", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String addGoodsTrain(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_id").toString();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            // String user_code = jsonObject.get("user_code").toString();
            //   String temp = jsonObject.get("goods_image").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            Goods goods = WebUtils.JSON2Bean(jsonObject, Goods.class);
            //goods.setGoods_time(sdf.parse);
            Date now = new Date();

            goods.setModified_date(Common.DATETIME_FORMAT.format(now));
            goods.setModifier(user_id);
            goods.setCreated_date(Common.DATETIME_FORMAT.format(now));
            goods.setCreater(user_id);
            String existInfo1 = this.goodsService.goodsCodeExist(corp_code, goods.getGoods_code());
            String existInfo2 = this.goodsService.goodsNameExist(corp_code, goods.getGoods_name());

            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            if (existInfo1.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setMessage("编号已存在");
            } else if (existInfo2.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setMessage("商品名称已存在！！！");
            } else {
                this.goodsService.insert(goods);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("success !!!");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 商品编辑之前
     * 获取数据
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/fab/select", method = RequestMethod.POST)
    @ResponseBody
    public String selectGoodsTrain(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            int goods_id = Integer.parseInt(jsonObject.getString("id"));
            //   Goods goods = this.goodsService.getGoodsByCode(corp_code, goods_code);
            Goods goods = this.goodsService.getGoodsById(goods_id);
            org.json.JSONObject result = new org.json.JSONObject();
            result.put("goods", JSON.toJSONString(goods));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 商品培训
     * 编辑
     */
    @RequestMapping(value = "/fab/edit", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String editGoodsTrain(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String user_id = request.getSession(false).getAttribute("user_id").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            dataBean.setId(id);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            Goods goods = WebUtils.JSON2Bean(jsonObject, Goods.class);
            Date now = new Date();
            goods.setModified_date(Common.DATETIME_FORMAT.format(now));
            goods.setModifier(user_id);
            String result = goodsService.update(goods);
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
        }
        return dataBean.getJsonStr();
    }


    /**
     * 商品培训
     * 查找
     */
    @RequestMapping(value = "/fab/find", method = RequestMethod.POST)
    @ResponseBody
    public String findGoodsTrain(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
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
            PageInfo<Goods> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = this.goodsService.selectBySearch(page_number, page_size, "", search_value);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = this.goodsService.selectBySearch(page_number, page_size, corp_code, search_value);
            }
            org.json.JSONObject result = new org.json.JSONObject();
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }

    /**
     * 商品培训
     * 删除
     */
    @RequestMapping(value = "/fab/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public String deleteGoodsTrain(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String goods_id = jsonObject.getString("id");
            String ids[] = goods_id.split(",");

            for (int i = 0; i < ids.length; i++) {
                this.goodsService.delete(Integer.parseInt(ids[i]));
            }
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage("success");
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
        }
        return dataBean.getJsonStr();
    }


    /**
     * 商品管理
     * 确保商品中的商品编号在其公司内唯一性存在
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/FabCodeExist", method = RequestMethod.POST)
    @ResponseBody
    public String UserCodeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String goods_code = jsonObject.get("goods_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String existInfo = goodsService.goodsCodeExist(goods_code, corp_code);
            if (existInfo.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("商品编号已被使用！！！");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("商品编号不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }


    /**
     * 确保商品名称在其公司内的唯一性存在.
     * @param request
     * @return
     */
    @RequestMapping(value = "/FabNameExist", method = RequestMethod.POST)
    @ResponseBody
    public String FabNameExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String goods_name = jsonObject.get("goods_name").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            String existInfo = goodsService.goodsNameExist(corp_code, goods_name);
            if (existInfo.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("商品名称已被使用！！！");
            } else {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("商品名称不存在");
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

//    /**
//     * 秀搭管理
//     */
//    @RequestMapping(value = "/xiuda/list", method = RequestMethod.GET)
//    @ResponseBody
//    public String showMatchManage(HttpServletRequest request) {
//
//        return "xiuda";
//    }

}
