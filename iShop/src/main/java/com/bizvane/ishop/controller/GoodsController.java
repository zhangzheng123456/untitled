package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Brand;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.entity.Goods;
import com.bizvane.ishop.service.*;
import com.bizvane.ishop.utils.LuploadHelper;
import com.bizvane.ishop.utils.OssUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
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
    private GoodsService goodsService;
    @Autowired
    private CorpService corpService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private BaseService baseService;
    String id;


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
            PageInfo<Goods> list = null;
            if (screen.equals("")) {
                if (role_code.contains(Common.ROLE_SYS)) {
                    list = goodsService.selectBySearch(1, Common.EXPORTEXECLCOUNT, "", search_value,null);
                } else {
                    if (role_code.equals(Common.ROLE_GM)) {
                        list = goodsService.selectBySearch(1, Common.EXPORTEXECLCOUNT, corp_code, search_value,null);
                    }else if (role_code.equals(Common.ROLE_BM)){
                        String brand_code = request.getSession().getAttribute("brand_code").toString();
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD,"");
                        String[] brands = brand_code.split(",");
                        list = goodsService.selectBySearch(1, Common.EXPORTEXECLCOUNT, corp_code, search_value,brands);
                    }else {
                        list = goodsService.selectBySearch(1, Common.EXPORTEXECLCOUNT,  corp_code, search_value,null);
                    }
                }
            } else {
                Map<String, String> map = WebUtils.Json2Map(jsonObject);
                if (role_code.contains(Common.ROLE_SYS)) {
                    list = goodsService.selectAllGoodsScreen(1, Common.EXPORTEXECLCOUNT, "", map,null);
                } else {
                    if (role_code.equals(Common.ROLE_GM)) {
                        list = goodsService.selectAllGoodsScreen(1, Common.EXPORTEXECLCOUNT, corp_code, map,null);
                    }else if (role_code.equals(Common.ROLE_BM)){
                        String brand_code = request.getSession().getAttribute("brand_code").toString();
                        brand_code = brand_code.replace(Common.SPECIAL_HEAD,"");
                        String[] brands = brand_code.split(",");
                        list = goodsService.selectAllGoodsScreen(1, Common.EXPORTEXECLCOUNT, corp_code, map,brands);
                    }else {
                        list = goodsService.selectAllGoodsScreen(1, Common.EXPORTEXECLCOUNT, corp_code, map,null);
                    }
                }
            }

            List<Goods> goodses = list.getList();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            String json = mapper.writeValueAsString(goodses);
            if (goodses.size() >= Common.EXPORTEXECLCOUNT) {
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
    @RequestMapping(value = "/fab/search", method = RequestMethod.POST)
    @ResponseBody
    public String selectBySearch(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String role_code = request.getSession(false).getAttribute("role_code").toString();
        String corp_code = request.getSession().getAttribute("corp_code").toString();
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
            PageInfo<Goods> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = goodsService.selectBySearch(page_number, page_size, "", search_value,null);
            } else {
                if (role_code.equals(Common.ROLE_GM)) {
                    list = goodsService.selectBySearch(page_number, page_size, corp_code, search_value,null);
                }else if (role_code.equals(Common.ROLE_BM)){
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD,"");
                    String[] brands = brand_code.split(",");
                    list = goodsService.selectBySearch(page_number, page_size, corp_code, search_value,brands);
                }else {
                    list = goodsService.selectBySearch(page_number, page_size, corp_code, search_value,null);
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
            PageInfo<Goods> list = null;
            if (role_code.contains(Common.ROLE_SYS)) {
                list = goodsService.selectAllGoodsScreen(page_number, page_size, "", map,null);
            } else {
                String corp_code = request.getSession(false).getAttribute("corp_code").toString();
                if (role_code.equals(Common.ROLE_GM)) {
                    list = goodsService.selectAllGoodsScreen(page_number, page_size, corp_code, map,null);
                }else if (role_code.equals(Common.ROLE_BM)){
                    String brand_code = request.getSession().getAttribute("brand_code").toString();
                    brand_code = brand_code.replace(Common.SPECIAL_HEAD,"");
                    String[] brands = brand_code.split(",");
                    list = goodsService.selectAllGoodsScreen(page_number, page_size, corp_code, map,brands);
                }else {
                    list = goodsService.selectAllGoodsScreen(page_number, page_size, corp_code, map,null);
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
            int clos = 11;//得到所有的列
            int rows = rs.getRows();//得到所有的行
     //       int actualRows = LuploadHelper.getRightRows(rs);
//            if(actualRows != rows){
//                if(rows-actualRows==1){
//                    result = "：第"+rows+"行存在空白行,请删除";
//                }else{
//                    result = "：第"+(actualRows+1)+"行至第"+rows+"存在空白行,请删除";
//                }
//                int i = 5 / 0;
//            }
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
                    if(column3[i].getContents().toString().trim().equals("")){
                        continue;
                    }
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
                if(column3[i].getContents().toString().trim().equals("")){
                    continue;
                }
                Matcher matcher = pattern1.matcher(String.valueOf(column3[i].getContents().toString().trim()));
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
//            String onlyCell2 = LuploadHelper.CheckOnly(rs.getColumn(2));
//            if(onlyCell2.equals("存在重复值")){
//                result = "：Execl中商品名称存在重复值";
//                int b = 5 / 0;
//            }
            Cell[] column = rs.getColumn(1);
            for (int i = 3; i < column.length; i++) {
                if(column[i].getContents().toString().trim().equals("")){
                    continue;
                }
                Goods goods = goodsService.getGoodsByCode(column3[i].getContents().toString().trim(), column[i].getContents().toString().trim(),Common.IS_ACTIVE_Y);
                if (goods != null) {
                    result = "：第" + (i + 1) + "行商品编号已存在";
                    int b = 5 / 0;
                    break;
                }
            }
//            Cell[] column1 = rs.getColumn(2);
//            for (int i = 3; i < column1.length; i++) {
//                if(column1[i].getContents().toString().trim().equals("")){
//                    continue;
//                }
//                String goodsNameExist = goodsService.goodsNameExist(column3[i].getContents().toString().trim(), column1[i].getContents().toString().trim());
//                if (goodsNameExist.contains(Common.DATABEAN_CODE_ERROR)) {
//                    result = "：第" + (i + 1) + "行商品名称已存在";
//                    int b = 5 / 0;
//                    break;
//                }
//            }
            Cell[] column4 = rs.getColumn(3);
            Cell[] column6 = rs.getColumn(5);
            Pattern pattern2 = Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])");
            for (int i = 3; i < column4.length; i++) {
                if(column4[i].getContents().toString().trim().equals("")){
                    continue;
                }
                Matcher matcher = pattern2.matcher(column4[i].getContents().toString().trim());
                if (matcher.matches() == false) {
                    result = "：第" + (i + 1) + "行商品价格输入有误";
                    int b = 5 / 0;
                    break;
                }
            }
            Cell[] column5 = rs.getColumn(4);

          //  Pattern pattern5 = Pattern.compile("(^(http:\\/\\/)(.*?)(\\/(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$))");
            for (int i = 3; i < column5.length; i++) {
                if(column5[i].getContents().toString().trim().equals("")){
                    continue;
                }
                String images = column5[i].getContents().toString().trim();

//                if(splitImages.length>5){
//                    result = "：第"+(i+1)+"行上传图片数量过多,上限5张";
//                    int b = 5 / 0;
//                    break;
//                }
//                for (int j=0;j<splitImages.length;j++){
//                    Matcher matcher = pattern5.matcher(splitImages[j]);
//                    if(matcher.matches()==false){
//                        result = "：第" + (i + 1) + "行,第"+(j+1)+"个图片地址输入有误";
//                        int b = 5 / 0;
//                        break;
//                    }
//                }
            }


            Pattern pattern = Pattern.compile("B\\d{4}");
            Cell[] column7 = rs.getColumn(7);
            for (int i = 3; i < column7.length; i++) {
                if(column7[i].getContents().toString().trim().equals("")){
                    continue;
                }
                Brand brand = brandService.getBrandByCode(column3[i].getContents().toString().trim(), column7[i].getContents().toString().trim(),Common.IS_ACTIVE_Y);
                if (brand == null) {
                    result = "：第" + (i + 1) + "行品牌编号不存在";
                    int b = 5 / 0;
                    break;
                }
            }
//            Cell[] column10 = rs.getColumn(10);
//            for(int i=3;i<column10.length;i++){
//                if(column10[i].getContents().toString().trim().equals("")){
//                    continue;
//                }
//                String goods = column10[i].getContents().toString().trim();
//                String[] splitGoods = goods.split(",");
//                if(splitGoods.length>10){
//                    result = "：第"+(i+1)+"行关联商品数量过多,上限10个";
//                    int b = 5 / 0;
//                    break;
//                }
//                String onlyCell10 = LuploadHelper.CheckStringOnly(splitGoods);
//                if(onlyCell10.equals("存在重复值")){
//                    result = "：第" + (i + 1) + "行中Execl关联的商品编号存在重复值";
//                    int b = 5 / 0;
//                }
//                for (int j=0;j<splitGoods.length;j++){
////                    Matcher matcher = pattern5.matcher(splitGoods[j]);
////                    if(matcher.matches()==false){
//
//                        Goods good = goodsService.getGoodsByCode(column3[i].getContents().toString().trim(), splitGoods[j],Common.IS_ACTIVE_Y);
//                        if (good == null) {
//                            result = "：第" + (i + 1) + "行,第"+(j+1)+"个关联的商品编号不存在";
//                            int b = 5 / 0;
//                            break;
//                        }
//                 //   }
//                }
//            }
            ArrayList<Goods> goodses=new ArrayList<Goods>();
            for (int i = 3; i < rows; i++) {
                for (int j = 0; j < clos; j++) {
                    Goods goods = new Goods();
                    String cellCorp = rs.getCell(j++, i).getContents().toString().trim();
                    String goods_code = rs.getCell(j++, i).getContents().toString().trim();
                    String goods_name = rs.getCell(j++, i).getContents().toString().trim();
                    String goods_price = rs.getCell(j++, i).getContents().toString().trim();
                    String goods_image = rs.getCell(j++, i).getContents().toString().trim();
                    String quarter = rs.getCell(j++, i).getContents().toString().trim();
                    String wave = rs.getCell(j++, i).getContents().toString().trim();
                    String brand_code = rs.getCell(j++, i).getContents().toString().trim();
                    String date= rs.getCell(j++, i).getContents().toString().trim();
                    String cellTypeForDate = LuploadHelper.checkDate(date);
                    String goods_description = rs.getCell(j++, i).getContents().toString().trim();
                    String share_description = rs.getCell(j++, i).getContents().toString().trim();
//                    String match_goods = rs.getCell(j++, i).getContents().toString().trim();
                    String isactive = rs.getCell(j++, i).getContents().toString().trim();
//                    if(cellCorp.equals("")  && goods_code.equals("") && goods_name.equals("") && goods_price.equals("") && goods_image.equals("") && quarter.equals("") && wave.equals("")  && brand_code.equals("")  && cellTypeForDate.equals("") && goods_description.equals("") && isactive.equals("")){
//                        result = "：第"+(i+1)+"行存在空白行,请删除";
//                        int a=5/0;
//                    }
                    if(cellCorp.equals("") && goods_code.equals("") && goods_name.equals("")   && brand_code.equals("")){
                        continue;
                    }
                    if(cellCorp.equals("")||goods_code.equals("") || goods_name.equals("")  || brand_code.equals("")|| date.equals("")){
                        result = "：第"+(i+1)+"行信息不完整,请参照Execl中对应的批注";
                        int a=5/0;
                    }
                    if(!role_code.equals(Common.ROLE_SYS)){
                        goods.setCorp_code(corp_code);
                    }else{
                        goods.setCorp_code(cellCorp);
                    }
                    goods.setGoods_code(goods_code);
                    goods.setGoods_name(goods_name);
                    goods.setGoods_price(goods_price);
                    if(goods_image==null||goods_image.equals("")){
                        goods.setGoods_image("");
                    }else{
                        String[] splitImages = goods_image.split(",");
                        JSONArray images_array = new JSONArray();
                        for (int k = 0; k < splitImages.length; k++) {
                            JSONObject obj = new JSONObject();
                            obj.put("image",splitImages[k]);
                            obj.put("is_public","N");
                            images_array.add(obj);
                        }
                        goods.setGoods_image(images_array.toJSONString());
                    }
                    if(quarter==null||quarter.equals("")) {
                        goods.setGoods_quarter("");
                    }else{
                        goods.setGoods_quarter(quarter);
                    }
//                    if(quarter==null||quarter.equals("")) {
//                        goods.setGoods_quarter("第一季度");
//                    }else if(!quarter.equals("第一季度") && !quarter.equals("第二季度") && !quarter.equals("第三季度") && !quarter.equals("第四季度")){
//                        result = "：第" + (i + 1) + "行商品季度输入有误";
//                        int b = 5 / 0;
//                        break;
//                    }else{
//                        goods.setGoods_quarter(quarter);
//                    }
                    if(wave==null||wave.equals("")){
                        goods.setGoods_wave("   ");
                    }else{
                        goods.setGoods_wave(wave);
                    }
                    if(brand_code==null || brand_code.equals("")){
                        result = "：第" + (i + 1) + "行品牌编号不能为空";
                        int b = 5 / 0;
                        break;
                    }else{
                        goods.setBrand_code(brand_code);
                    }
                    goods.setGoods_time(cellTypeForDate);
                    goods.setGoods_description(goods_description);
                    goods.setShare_description(share_description);
//                    goods.setMatch_goods(match_goods);
                    if (isactive.toUpperCase().equals("N")) {
                        goods.setIsactive("N");
                    } else {
                        goods.setIsactive("Y");
                    }
                    Date now = new Date();
                    goods.setCreater(user_id);
                    goods.setCreated_date(Common.DATETIME_FORMAT.format(now));
                    goods.setModified_date(Common.DATETIME_FORMAT.format(now));
                    goods.setModifier(user_id);
                    goodses.add(goods);
                   // result = String.valueOf(goodsService.insert(goods));
                }
            }
            for (Goods goods:goodses
                 ) {
                result = String.valueOf(goodsService.insert(goods));
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
    public String addGoods(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String user_id = request.getSession(false).getAttribute("user_code").toString();
        String id = "";
        String path="";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            // String user_code = jsonObject.get("user_code").toString();
//            String match_goods = jsonObject.get("match_goods").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            Goods goods = WebUtils.JSON2Bean(jsonObject, Goods.class);
            String goods_description = goods.getGoods_description();
            String share_description = goods.getShare_description();

            List<String> htmlImageSrcList = OssUtils.getHtmlImageSrcList(goods_description);
            List<String> htmlImageSrcList1 = OssUtils.getHtmlImageSrcList(share_description);

            OssUtils ossUtils=new OssUtils();
            String bucketName="products-image";

             path = request.getSession().getServletContext().getRealPath("/");
            for (int k = 0; k < htmlImageSrcList.size(); k++) {
                String time="FAB/"+corp_code+"/"+goods.getGoods_code()+"_"+Common.DATETIME_FORMAT_DAY_NUM.format(new Date())+".jpg";
                if(!htmlImageSrcList.get(k).contains("image/upload")){
                    continue;
                }
                ossUtils.putObject(bucketName,time,path+"/"+htmlImageSrcList.get(k));
                goods_description = goods_description.replace(htmlImageSrcList.get(k),"http://"+bucketName+".oss-cn-hangzhou.aliyuncs.com/"+time);
                LuploadHelper.deleteFile(path+"/"+htmlImageSrcList.get(k));
            }
            for (int k = 0; k < htmlImageSrcList1.size(); k++) {
                String time="FAB/"+corp_code+"/"+goods.getGoods_code()+"_"+Common.DATETIME_FORMAT_DAY_NUM.format(new Date())+".jpg";
                if(!htmlImageSrcList1.get(k).contains("image/upload")){
                    continue;
                }
                ossUtils.putObject(bucketName,time,path+"/"+htmlImageSrcList1.get(k));
                share_description = share_description.replace(htmlImageSrcList1.get(k),"http://"+bucketName+".oss-cn-hangzhou.aliyuncs.com/"+time);
                LuploadHelper.deleteFile(path+"/"+htmlImageSrcList1.get(k));
            }
            goods.setGoods_description(goods_description);
            goods.setShare_description(share_description);

            Date now = new Date();
            goods.setModified_date(Common.DATETIME_FORMAT.format(now));
            goods.setModifier(user_id);
            goods.setCreated_date(Common.DATETIME_FORMAT.format(now));
            goods.setCreater(user_id);
            Goods existInfo1 = goodsService.getGoodsByCode(corp_code, goods.getGoods_code(),Common.IS_ACTIVE_Y);

            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            if (existInfo1 != null) {
                dataBean.setMessage("商品编号已存在");
            } else {
                this.goodsService.insert(goods);
                Goods goods1 = goodsService.getGoodsByCode(corp_code,goods.getGoods_code(),goods.getIsactive());
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage(String.valueOf(goods1.getId()));

                //----------------行为日志------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "商品管理_商品培训(FAB)";
                String action = Common.ACTION_ADD;
                String t_corp_code = action_json.get("corp_code").toString();
                String t_code = action_json.get("goods_code").toString();
                String t_name = action_json.get("goods_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束--------------------------------------------------------------------------------
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
        String path="";
        try {
            String user_id = request.getSession(false).getAttribute("user_code").toString();
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            id = jsonObj.get("id").toString();
            dataBean.setId(id);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String corp_code = jsonObject.get("corp_code").toString();
            String delImgPath = jsonObject.get("delImgPath").toString();
            Goods goods = WebUtils.JSON2Bean(jsonObject, Goods.class);
            String goods_description = goods.getGoods_description();
            String share_description= goods.getShare_description();

            List<String> htmlImageSrcList = OssUtils.getHtmlImageSrcList(goods_description);
            List<String> htmlImageSrcList1 = OssUtils.getHtmlImageSrcList(share_description);

            List<String> delImgPaths = OssUtils.getHtmlImageSrcList(delImgPath);
            OssUtils ossUtils=new OssUtils();
            String bucketName="products-image";
            path =   request.getSession().getServletContext().getRealPath("/");
            for (int i = 0; i < delImgPaths.size(); i++) {
                String replace = delImgPaths.get(i).replace("http://" + bucketName + ".oss-cn-hangzhou.aliyuncs.com/", "");
                ossUtils.deleteObject(bucketName,replace);
            }
            for (int k = 0; k < htmlImageSrcList.size(); k++) {
                String time="FAB/"+corp_code+"/"+goods.getGoods_code()+"_"+Common.DATETIME_FORMAT_DAY_NUM.format(new Date())+".jpg";
                if(!htmlImageSrcList.get(k).contains("image/upload")){
                    continue;
                }
                ossUtils.putObject(bucketName,time,path+"/"+htmlImageSrcList.get(k));
                goods_description = goods_description.replace(htmlImageSrcList.get(k),"http://"+bucketName+".oss-cn-hangzhou.aliyuncs.com/"+time);
                LuploadHelper.deleteFile(path+"/"+htmlImageSrcList.get(k));
            }
            for (int k = 0; k < htmlImageSrcList1.size(); k++) {
                String time="FAB/"+corp_code+"/"+goods.getGoods_code()+"_"+Common.DATETIME_FORMAT_DAY_NUM.format(new Date())+".jpg";
                if(!htmlImageSrcList1.get(k).contains("image/upload")){
                    continue;
                }
                ossUtils.putObject(bucketName,time,path+"/"+htmlImageSrcList1.get(k));
                share_description = share_description.replace(htmlImageSrcList1.get(k),"http://"+bucketName+".oss-cn-hangzhou.aliyuncs.com/"+time);
                LuploadHelper.deleteFile(path+"/"+htmlImageSrcList1.get(k));
            }
            goods.setGoods_description(goods_description);
            goods.setShare_description(share_description);

            Date now = new Date();
            goods.setModified_date(Common.DATETIME_FORMAT.format(now));
            goods.setModifier(user_id);
            String result = goodsService.update(goods);
            if (result.equals(Common.DATABEAN_CODE_SUCCESS)) {
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("商品更改成功");

                //----------------行为日志开始------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
//                com.alibaba.fastjson.JSONObject action_json = com.alibaba.fastjson.JSONObject.parseObject(message);
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "商品管理_商品培训(FAB)";
                String action = Common.ACTION_UPD;
                String t_corp_code = jsonObject.get("corp_code").toString();
                String t_code = jsonObject.get("goods_code").toString();
                String t_name = jsonObject.get("goods_name").toString();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
            } else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage(result);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("edit error");
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
                Goods goodsById = goodsService.getGoodsById(Integer.parseInt(ids[i]));
                this.goodsService.delete(Integer.parseInt(ids[i]));

                //----------------行为日志开始------------------------------------------
                /**
                 * mongodb插入用户操作记录
                 * @param operation_corp_code 操作者corp_code
                 * @param operation_user_code 操作者user_code
                 * @param function 功能
                 * @param action 动作
                 * @param corp_code 被操作corp_code
                 * @param code 被操作code
                 * @param name 被操作name
                 * @throws Exception
                 */
                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
                String operation_user_code = request.getSession().getAttribute("user_code").toString();
                String function = "商品管理_商品培训(FAB)";
                String action = Common.ACTION_DEL;
                String t_corp_code = goodsById.getCorp_code();
                String t_code = goodsById.getGoods_code();
                String t_name = goodsById.getGoods_name();
                String remark = "";
                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
                //-------------------行为日志结束-----------------------------------------------------------------------------------
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
    public String FabCodeExist(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            org.json.JSONObject jsonObj = new org.json.JSONObject(jsString);
            String message = jsonObj.get("message").toString();
            org.json.JSONObject jsonObject = new org.json.JSONObject(message);
            String goods_code = jsonObject.get("goods_code").toString();
            String corp_code = jsonObject.get("corp_code").toString();
            Goods existInfo = goodsService.getGoodsByCode(corp_code, goods_code,Common.IS_ACTIVE_Y);
            if (existInfo != null) {
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
     * 获取企业商品（用于商品搭配）
     */
    @RequestMapping(value = "/getMatchFab", method = RequestMethod.POST)
    @ResponseBody
    public String getMatchFab(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String id = "";
        try {
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = JSONObject.parseObject(message);
            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
            String search_value = jsonObject.get("search_value").toString();
            String corp_code_json = jsonObject.get("corp_code").toString();
            String role_code = request.getSession(false).getAttribute("role_code").toString();
            String corp_code = request.getSession(false).getAttribute("corp_code").toString();
            PageInfo<Goods> list=null;
            //sys新增
            if(role_code.equals(Common.ROLE_SYS) && corp_code_json.equals("")){
                list = goodsService.getMatchFab(page_number,page_size,"C10000",search_value);
             //sys编辑
            }else if(role_code.equals(Common.ROLE_SYS) && !corp_code_json.equals("")){
                list = goodsService.getMatchFab(page_number,page_size,corp_code_json,search_value);
            }else{
                list = goodsService.getMatchFab(page_number,page_size,corp_code,search_value);
            }
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
