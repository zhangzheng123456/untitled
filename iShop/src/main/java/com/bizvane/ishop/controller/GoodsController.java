package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Brand;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.Goods;
import com.bizvane.ishop.entity.TableManager;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.v1.common.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @Autowired
    private CorpService corpService;
    @Autowired
    private BrandService brandService;
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
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();

            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
            int page_size = Integer.parseInt(request.getParameter("pageSize"));
            org.json.JSONObject result = new org.json.JSONObject();
            PageInfo<Goods> list;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = this.goodsService.selectBySearch(page_number, page_size, "", "");
            } else {
                list = goodsService.selectBySearch(page_number, page_size, corp_code, "");
            }
            for (int i = 0; list.getList() != null && list.getList().size() > i; i++) {
                String goods_image = list.getList().get(i).getGoods_image();
                if (goods_image != null && !goods_image.isEmpty()) {
                    list.getList().get(i).setGoods_image(goods_image.split(",")[0]);
                }
            }
            result.put("list", JSON.toJSONString(list));
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
    @RequestMapping(value = "/fab/exportExecl", method = RequestMethod.POST)
    @ResponseBody
    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
        DataBean dataBean = new DataBean();
        String errormessage = "数据异常，导出失败";
        try {
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();

            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String search_value = jsonObject.get("searchValue").toString();
            String screen = jsonObject.get("list").toString();
            PageInfo<Goods> list;
            if (screen.equals("")) {
                if (role_code.equals(Common.ROLE_SYS)) {
                    list = this.goodsService.selectBySearch(1, 30000, "", search_value);
                } else {
                    //   String corp_code = request.getParameter("corp_code");
                    list = goodsService.selectBySearch(1, 30000, corp_code, search_value);
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.contains(Common.ROLE_SYS)) {
                    list = goodsService.selectAllGoodsScreen(1, 30000, "", map);
                } else {
                    list = goodsService.selectAllGoodsScreen(1, 30000, corp_code, map);
                }
            }
//            for (int i = 0; list.getList() != null && list.getList().size() > i; i++) {
//                String goods_image = list.getList().get(i).getGoods_image();
//                if (goods_image != null && !goods_image.isEmpty()) {
//                    list.getList().get(i).setGoods_image(goods_image.split(",")[0]);
//                }
//            }
            List<Goods> goodses = list.getList();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(goodses);
            if (goodses.size() >= 29999) {
                errormessage = "导出数据过大";
                int i = 9 / 0;
            }
            LinkedHashMap<String,String> map = WebUtils.Json2ShowName(jsonObject);
            // String column_name1 = "corp_code,corp_name";
            // String[] cols = column_name.split(",");//前台传过来的字段
            String pathname = OutExeclHelper.OutExecl(json,goodses, map, response, request);
            JSONObject result = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result.put("path", JSON.toJSONString("lupload/" + pathname));
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage(errormessage);
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


    /**
     * 商品管理
     * 商品筛选
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/fab/screen", method = RequestMethod.POST)
    @ResponseBody
    public String selectByScreen(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            Map<String, String> map = WebUtils.Json2Map(jsonObject);
//            String jlist = jsonObject.get("list").toString();
//            JSONArray array = JSONArray.parseArray(jlist);
//            Map<String,String> map = new HashMap<String, String>();
//            for(int i=0;i<array.size();i++){
//                String info = array.get(i).toString();
//                JSONObject json = new JSONObject(info);
//                String screen_key = json.get("screen_key").toString();
//                String screen_value = json.get("screen_value").toString();
//                map.put(screen_key,screen_value);
//            }
            PageInfo<Goods> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = goodsService.selectAllGoodsScreen(page_number, page_size, "", map);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                list = goodsService.selectAllGoodsScreen(page_number, page_size, corp_code, map);
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
     * Execl增加
     */
    @RequestMapping(value = "/fab/addByExecl", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    @Transactional()
    public String addByExecl(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file, ModelMap model) throws SQLException {
        DataBean dataBean = new DataBean();
        File targetFile = LuploadHelper.lupload(request, file, model);
        String user_id = request.getSession().getAttribute("user_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
        String role_code = request.getSession().getAttribute("role_code").toString();
        String result = "";
        Workbook rwb=null;
        try {
            rwb = Workbook.getWorkbook(targetFile);
            Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
            int clos = rs.getColumns();//得到所有的列
            int rows = rs.getRows();//得到所有的行
            int actualRows = LuploadHelper.getRightRows(rs);
            if(actualRows != rows){
                if(rows-actualRows==1){
                    result = "：第"+rows+"行存在空白行,请删除";
                }else{
                    result = "：第"+(actualRows+1)+"行至第"+rows+"存在空白行,请删除";
                }
                int i = 5 / 0;
            }
            if(rows<4){
                result="：请从模板第4行开始插入正确数据";
                int i=5/0;
            }
            if (rows > 9999) {
                result = "：数据量过大，导入失败";
                int i = 5 / 0;
            }
            Cell[] column3 = rs.getColumn(0);
            Pattern pattern1 = Pattern.compile("C\\d{5}");
            if(!role_code.equals(Common.ROLE_SYS)){
                for (int i = 3; i < column3.length; i++) {
                    if (!column3[i].getContents().toString().trim().equals(corp_code)) {
                        result = "：第" + (i + 1) + "行企业编号不存在";
                        int b = 5 / 0;
                        break;
                    }
                    Matcher matcher = pattern1.matcher(column3[i].getContents().toString().trim());
                    if (matcher.matches() == false) {
                        result = "：第" + (i + 1) + "行企业编号格式有误";
                        int b = 5 / 0;
                        break;
                    }
                }
            }
            for (int i = 3; i < column3.length; i++) {
                Matcher matcher = pattern1.matcher(column3[i].getContents().toString().trim());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行企业编号格式有误";
                    int b = 5 / 0;
                    break;
                }
                Corp corp = corpService.selectByCorpId(0, column3[i].getContents().toString().trim(),Common.IS_ACTIVE_Y);
                if (corp == null) {
                    result = "：第" + (i + 1) + "行企业编号不存在";
                    int b = 5 / 0;
                    break;
                }
            }
            String onlyCell1 = LuploadHelper.CheckOnly(rs.getColumn(1));
            if(onlyCell1.equals("存在重复值")){
                result = "：Execl中商品编号存在重复值";
                int b = 5 / 0;
            }
            String onlyCell2 = LuploadHelper.CheckOnly(rs.getColumn(2));
            if(onlyCell2.equals("存在重复值")){
                result = "：Execl中商品名称存在重复值";
                int b = 5 / 0;
            }
            Cell[] column = rs.getColumn(1);
            for (int i = 3; i < column.length; i++) {
                String goodsCodeExist = goodsService.goodsCodeExist(column3[i].getContents().toString().trim(), column[i].getContents().toString().trim());
                if (goodsCodeExist.contains(Common.DATABEAN_CODE_ERROR)) {
                    result = "：第" + (i + 1) + "行商品编号已存在";
                    int b = 5 / 0;
                    break;
                }
            }
            Cell[] column1 = rs.getColumn(2);
            for (int i = 3; i < column1.length; i++) {
                String goodsNameExist = goodsService.goodsNameExist(column3[i].getContents().toString().trim(), column1[i].getContents().toString().trim());
                if (goodsNameExist.contains(Common.DATABEAN_CODE_ERROR)) {
                    result = "：第" + (i + 1) + "行商品名称已存在";
                    int b = 5 / 0;
                    break;
                }
            }
            Cell[] column4 = rs.getColumn(3);
            Cell[] column6 = rs.getColumn(5);
            Pattern pattern2 = Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])");
            for (int i = 3; i < column4.length; i++) {
                Matcher matcher = pattern2.matcher(column4[i].getContents().toString().trim());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行商品价格输入有误";
                    int b = 5 / 0;
                    break;
                }
                if(!column6[i].getContents().toString().trim().equals("第一季度") && !column6[i].getContents().toString().trim().equals("第二季度")&&!column6[i].getContents().toString().trim().equals("第三季度")&&!column6[i].getContents().toString().trim().equals("第四季度")){
                    result = "：第" + (i + 1) + "行商品季度输入有误";
                    int b = 5 / 0;
                    break;
                }
            }
            Cell[] column5 = rs.getColumn(4);

            Pattern pattern5 = Pattern.compile("(^(http:\\/\\/)(.*?)(\\/(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$))");
            for (int i = 3; i < column5.length; i++) {
                String images = column5[i].getContents().toString().trim();
                String[] splitImages = images.split(",");
                if(splitImages.length>5){
                    result = "：第"+(i+1)+"行上传图片数量过多,上限5张";
                    int b = 5 / 0;
                    break;
                }
                for (int j=0;j<splitImages.length;j++){
                    Matcher matcher = pattern5.matcher(splitImages[j]);
                    if(matcher.matches()==false){
                        result = "：第" + (i + 1) + "行,第"+(j+1)+"个图片地址输入有误";
                        int b = 5 / 0;
                        break;
                    }
                }
            }


            Pattern pattern = Pattern.compile("B\\d{4}");
            Cell[] column7 = rs.getColumn(7);
            for (int i = 3; i < column7.length; i++) {
                Matcher matcher = pattern.matcher(column7[i].getContents().toString().trim());
                if (column7[i].getContents().toString()==null || matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行品牌编号格式有误";
                    int b = 5 / 0;
                    break;
                }
                Brand brand = brandService.getBrandByCode(column3[i].getContents().toString().trim(), column7[i].getContents().toString().trim());
                if (brand == null) {
                    result = "：第" + (i + 1) + "行品牌编号不存在";
                    int b = 5 / 0;
                    break;
                }
            }
            for (int i = 3; i < rows; i++) {
                for (int j = 0; j < clos; j++) {
                    Goods goods = new Goods();
                    String cellCorp = rs.getCell(j++, i).getContents().toString().trim();
                    if(!role_code.equals(Common.ROLE_SYS)){
                        goods.setCorp_code(corp_code);
                    }else{
                        goods.setCorp_code(cellCorp);
                    }
                    goods.setGoods_code(rs.getCell(j++, i).getContents().toString().trim());
                    goods.setGoods_name(rs.getCell(j++, i).getContents().toString().trim());
                    goods.setGoods_price(Float.parseFloat(rs.getCell(j++, i).getContents().toString().trim()));
                    goods.setGoods_image(rs.getCell(j++, i).getContents().toString().trim()+"  ");
                    String quarter = rs.getCell(j++, i).getContents().toString().trim();
                    if(quarter==null||quarter.equals("")) {
                        goods.setGoods_quarter("第一季度");
                    }else{
                        goods.setGoods_quarter(quarter);
                    }
                    String wave = rs.getCell(j++, i).getContents().toString().trim();
                    if(wave==null||wave.equals("")){
                        goods.setGoods_wave("   ");
                    }else{
                        goods.setGoods_wave(wave);
                    }
                    String brand_code = rs.getCell(j++, i).getContents().toString().trim();
                    if(brand_code==null || brand_code.equals("")){
                        result = "：第" + (i + 1) + "行品牌编号格式有误";
                        int b = 5 / 0;
                        break;
                    }else{
                        goods.setBrand_code(brand_code);
                    }
                    String cellTypeForDate = LuploadHelper.getCellTypeForDate(rs.getCell(j++, i),"D");
                    goods.setGoods_time(cellTypeForDate);
                    goods.setGoods_description(rs.getCell(j++, i).getContents());
                    if (rs.getCell(j++, i).getContents().toString().trim().toUpperCase().equals("N")) {
                        goods.setIsactive("N");
                    } else {
                        goods.setIsactive("Y");
                    }
                    Date now = new Date();
                    goods.setCreater(user_id);
                    goods.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    goods.setModified_date(Common.DATETIME_FORMAT.format(now));
                    goods.setModifier(user_id);
                    result = String.valueOf(goodsService.insert(goods));
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
            System.gc();
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
        String user_id = request.getSession(false).getAttribute("user_code").toString();
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
            goods.setGoods_price(Float.parseFloat(jsonObject.getString("goods_price")));
            goods.setModified_date(Common.DATETIME_FORMAT.format(now));
            goods.setModifier(user_id);
            goods.setCreated_date(Common.DATETIME_FORMAT.format(now));
            goods.setCreater(user_id);
            String existInfo1 = this.goodsService.goodsCodeExist(corp_code, goods.getGoods_code());
            String existInfo2 = this.goodsService.goodsNameExist(corp_code, goods.getGoods_name());

            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            if (existInfo1.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setMessage("商品编号已存在");
            } else if (existInfo2.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setMessage("商品名称已存在");
            } else {
                this.goodsService.insert(goods);
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("success");
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
            String user_id = request.getSession(false).getAttribute("user_code").toString();
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
                dataBean.setMessage("商品更改成功");
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("edit error");
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
            String existInfo = goodsService.goodsCodeExist(corp_code, goods_code);
            if (existInfo.contains(Common.DATABEAN_CODE_ERROR)) {
                dataBean.setId(id);
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("商品编号已被使用");
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
     *
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
                dataBean.setMessage("商品名称已被使用");
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


    /**
     * 获取企业商品（用于商品搭配）
     */
    @RequestMapping(value = "/corp_fab", method = RequestMethod.POST)
    @ResponseBody
    public String corpFab(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String goods_code = jsonObject.get("goods_code").toString();
            String search_value = jsonObject.get("searchValue").toString();
            List<Goods> list = goodsService.selectBySearch(corp_code, search_value,goods_code);
            JSONObject result = new JSONObject();
            result.put("list", JSON.toJSONString(list));
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setMessage(result.toString());
        } catch (Exception ex) {
            dataBean.setId(id);
            dataBean.setMessage(ex.getMessage());
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
        }
        return dataBean.getJsonStr();
    }

}
