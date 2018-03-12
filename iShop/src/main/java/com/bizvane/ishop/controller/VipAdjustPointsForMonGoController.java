package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.entity.VipPointsAdjust;
import com.bizvane.ishop.service.IceInterfaceAPIService;
import com.bizvane.ishop.service.IceInterfaceService;
import com.bizvane.ishop.service.VipGroupService;
import com.bizvane.ishop.service.VipPointsAdjustService;
import com.bizvane.ishop.service.imp.MongoHelperServiceImpl;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.OutExeclHelper;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.ishop.utils.mongodb.BathUpdateOptions;
import com.bizvane.ishop.utils.mongodb.UpdateMongoDBUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.bizvane.sun.v1.common.DataBox;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yanyadong on 2017/11/10.
 *
 *
 * 会员积分调整单 保存 修改
 */
@Controller
@RequestMapping("/adjust/points")
public class VipAdjustPointsForMonGoController {


    Logger logger=Logger.getLogger(VipAdjustPointsForMonGoController.class);

    @Autowired
    VipGroupService vipGroupService;

    @Autowired
    IceInterfaceService iceInterfaceService;

    @Autowired
    IceInterfaceAPIService iceInterfaceAPIService;

    @Autowired
    MongoDBClient mongodbClient;

    @Autowired
    VipPointsAdjustService vipPointsAdjustService;



    //会员筛选与搜索
    @RequestMapping(value = "/screen",method = RequestMethod.POST)
    @ResponseBody
    public  String screenVip(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        String id="";
        try {

           // String corp_code = request.getSession().getAttribute("corp_code").toString();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String brand_code = request.getSession().getAttribute("brand_code").toString();
            String area_code = request.getSession().getAttribute("area_code").toString();
            String store_code = request.getSession().getAttribute("store_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject message_obj = JSON.parseObject(message);
            String search_value = message_obj.getString("search_value"); //搜索条件
            String screen_value = message_obj.getString("screen_value");//筛选条件
            String page_num = message_obj.get("page_num").toString();
            String page_size = message_obj.get("page_size").toString();
            String corp_code1=message_obj.getString("corp_code");
            String sort_key = "join_date";
            String sort_value = "desc";

            JSONArray  jsonArray=null;

            //[{"key":"NAME_VIP","value":[{"opera":"","logic":"contains","value":"1111"}],"type":"text","groupName":"姓名"}]

            JSONObject obj=JSON.parseObject(search_value);
            if (StringUtils.isNotBlank(obj.getString("NAME_VIP"))||StringUtils.isNotBlank(obj.getString("CARD_NO_VIP"))||StringUtils.isNotBlank(obj.getString("MOBILE_VIP"))) { //搜索条件
                JSONObject search_obj=obj;
                Set<Map.Entry<String, Object>> entries= search_obj.entrySet();
                Iterator<Map.Entry<String,Object>> iterator=entries.iterator();

                JSONArray  jsonArray_search=new JSONArray();
                while (iterator.hasNext()){
                    Map.Entry<String,Object> entry=iterator.next();
                    String key=entry.getKey();
                    String value=entry.getValue().toString();
                    if(StringUtils.isBlank(value)){
                        continue;
                    }
                    //拼接搜索条件
                    if(!key.contains("store_code")&&!key.contains("user_code")) {
                        JSONObject value_obj = new JSONObject();
                        JSONArray array_obj = new JSONArray();
                        JSONObject value_obj1 = new JSONObject();
                        value_obj1.put("opera", "");
                        value_obj1.put("logic", "contains");
                        value_obj1.put("value", value);
                        array_obj.add(value_obj1);
                        value_obj.put("key", key);
                        value_obj.put("value", array_obj);
                        value_obj.put("type", "text");
                        jsonArray_search.add(value_obj);
                    }else{
                        JSONObject value_obj=new JSONObject();
                        value_obj.put("key",key);
                        value_obj.put("value",value);
                        value_obj.put("type","text");
                        jsonArray_search.add(value_obj);
                    }
                }
                jsonArray=jsonArray_search;
            } else {//筛选
                if(StringUtils.isBlank(screen_value)){
                    screen_value="[]";
                }
                JSONArray jsonArray_screen=JSON.parseArray(screen_value);
                jsonArray=jsonArray_screen;
            }

            //系统管理员筛选权限到企业 其他账号筛选根据自身权限
            JSONArray post_array=null;
            if(role_code.equals(Common.ROLE_SYS)){
                post_array  = vipGroupService.vipScreen2ArrayNew(jsonArray, corp_code1, Common.ROLE_GM, "", "", "", "");
            }else{
                post_array  = vipGroupService.vipScreen2ArrayNew(jsonArray, corp_code1, role_code, brand_code, area_code, store_code, user_code);
            }

            DataBox dataBox = iceInterfaceService.newStyleVipSearchForWeb(page_num, page_size, corp_code1,JSON.toJSONString(post_array),sort_key,sort_value);
            if (dataBox.status.toString().equals("SUCCESS")){
                String result = dataBox.data.get("message").value;
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage(result);
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("筛选失败");
            }

        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    @RequestMapping(value = "/save" ,method = RequestMethod.POST)
    @ResponseBody
    public  String save(HttpServletRequest request){
        DataBean dataBean = new DataBean();
        String id="";
        try{
            MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();

            String role_code = request.getSession().getAttribute("role_code").toString();
            String brand_code = request.getSession().getAttribute("brand_code").toString();
            String area_code = request.getSession().getAttribute("area_code").toString();
            String store_code = request.getSession().getAttribute("store_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject message_obj = JSON.parseObject(message);
            String adjust_id=message_obj.getString("adjust_id");//调整单id
            String search_value = message_obj.getString("search_value"); //搜索条件
            String screen_value = message_obj.getString("screen_value");//筛选条件
            String corp_code1=message_obj.getString("corp_code");
            String sort_key = "join_date";
            String sort_value = "desc";

            String  type= message_obj.getString("type"); /*全选/选中当页*/
            String  select_value= message_obj.getString("vip_id");//选中的结果（vip_id逗号分隔）
            String sendPoints=message_obj.getString("sendPoints");//赠送的积分
            if(sendPoints.contains("+")){
                sendPoints=sendPoints.replace("+","");
            }
            DataBox dataBox=null;

            VipPointsAdjust vipPointAdjust=vipPointsAdjustService.selectPointsAdjustById(Integer.parseInt(adjust_id));
            JSONObject adjust_obj=WebUtils.bean2JSONObject(vipPointAdjust);
            String bill_code=vipPointAdjust.getBill_code();//单据编号

            if(type.equals("all")) {
                JSONArray  jsonArray=null;
                JSONObject obj=JSON.parseObject(search_value);
                if (StringUtils.isNotBlank(obj.getString("NAME_VIP"))||StringUtils.isNotBlank(obj.getString("CARD_NO_VIP"))||StringUtils.isNotBlank(obj.getString("MOBILE_VIP"))) { //搜索条件
                   JSONObject search_obj = obj;
                   Set<Map.Entry<String, Object>> entries = search_obj.entrySet();
                   Iterator<Map.Entry<String, Object>> iterator = entries.iterator();

                   JSONArray jsonArray_search = new JSONArray();
                   while (iterator.hasNext()) {
                       Map.Entry<String, Object> entry = iterator.next();
                       String key = entry.getKey();
                       String value = entry.getValue().toString();
                       if (StringUtils.isBlank(value)) {
                           continue;
                       }
                       //拼接搜索条件
                       if (!key.contains("store_code") && !key.contains("user_code")) {
                           JSONObject value_obj = new JSONObject();
                           JSONArray array_obj = new JSONArray();
                           JSONObject value_obj1 = new JSONObject();
                           value_obj1.put("opera", "");
                           value_obj1.put("logic", "contains");
                           value_obj1.put("value", value);
                           array_obj.add(value_obj1);
                           value_obj.put("key", key);
                           value_obj.put("value", array_obj);
                           value_obj.put("type", "text");
                           jsonArray_search.add(value_obj);
                       } else {
                           JSONObject value_obj = new JSONObject();
                           value_obj.put("key", key);
                           value_obj.put("value", value);
                           value_obj.put("type", "text");
                           jsonArray_search.add(value_obj);
                       }
                   }
                   jsonArray = jsonArray_search;
               } else {//筛选
                    if(StringUtils.isBlank(screen_value)){
                        screen_value="[]";
                    }
                   JSONArray jsonArray_screen = JSON.parseArray(screen_value);
                   jsonArray = jsonArray_screen;
               }
               //系统管理员筛选权限到企业 其他账号筛选根据自身权限
               JSONArray post_array = null;
               if (role_code.equals(Common.ROLE_SYS)) {
                   post_array = vipGroupService.vipScreen2ArrayNew(jsonArray, corp_code1, Common.ROLE_GM, "", "", "", "");
               } else {
                   post_array = vipGroupService.vipScreen2ArrayNew(jsonArray, corp_code1, role_code, brand_code, area_code, store_code, user_code);
               }
                dataBox = iceInterfaceService.newStyleVipSearchForWebV2("1", "5000", corp_code1, JSON.toJSONString(post_array), sort_key, sort_value);
           }else if(type.equals("select")){
              dataBox = iceInterfaceService.vipSearchByHbase(corp_code1,select_value);
           }

            if (dataBox.status.toString().equals("SUCCESS")){
                String result = dataBox.data.get("message").value;
                JSONObject vip_obj=JSON.parseObject(result);
                JSONArray all_vip_list=vip_obj.getJSONArray("all_vip_list");
                int size=all_vip_list.size();
                List<BathUpdateOptions> list_value = new ArrayList<BathUpdateOptions>();
                for (int i = 0; i < size; i++) {
                    JSONObject vipInfo=all_vip_list.getJSONObject(i);
                    String vip_id=vipInfo.getString("vip_id");
                    Query query=new Query();
                    query.addCriteria(Criteria.where("_id").is(bill_code+vip_id));

                    Update update=new Update();
                    BSONObject bsonObject=new BasicBSONObject();
                    bsonObject.putAll(vipInfo);
                    BSONObject bsonObject1=new BasicBSONObject();
                    bsonObject1.putAll(adjust_obj);
                    update.set("vip",bsonObject);
                    update.set("adJustInfo",bsonObject1);//单据信息
                    update.set("corp_code",corp_code1);
                    update.set("sendPoints", sendPoints);
                    update.set("modified_date",Common.DATETIME_FORMAT.format(new Date())); //调整时间
                    update.set("_id",bill_code+vip_id);
                    update.set("state","N");//未提交
                    update.set("vip_bill_code",bill_code+vip_id);//线下单据
                    list_value.add(new BathUpdateOptions(query, update, true, true));
                }
                if(list_value.size()>0){
                    List<List> lists= WebUtils.cutListByArray(list_value,1000);
                    for (int i = 0; i < lists.size(); i++) {
                        //插入更新数据
                        UpdateMongoDBUtils.bathUpdate(mongoTemplate, CommonValue.table_vip_points_adjust, lists.get(i));
                    }
                }else{
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage("保存失败");
                    return  dataBean.getJsonStr();
                }
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setId("1");
                dataBean.setMessage("保存成功");
            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("保存失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setId(id);
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage(e.getMessage());
        }
        return  dataBean.getJsonStr();
    }


    //列表+筛选
    @RequestMapping(value = "/select",method = RequestMethod.POST)
    @ResponseBody
    public  String  selectVip(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{

            MongoTemplate mongoTemplate=this.mongodbClient.getMongoTemplate();
            DBCollection dbCollection= mongoTemplate.getCollection(CommonValue.table_vip_points_adjust);
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject message_obj = JSON.parseObject(message);
            String adjust_id=message_obj.getString("adjust_id");//调整单id
            String page_num=message_obj.getString("page_num");
            String page_size=message_obj.getString("page_size");
          //  String search_value=message_obj.getString("search_value");
            String screen=message_obj.getString("screen");
            JSONObject screen_obj=JSON.parseObject(screen);
            String vip_name=screen_obj.getString("vip_name");
            String vip_cardno=screen_obj.getString("vip_cardno");
            String remarks=screen_obj.getString("remarks");
            String state =screen_obj.getString("state");
          //  String corp_code1=message_obj.getString("corp_code");
            VipPointsAdjust vipPointsAdjust=vipPointsAdjustService.selectPointsAdjustById(Integer.parseInt(adjust_id));
            BasicDBList basicList=new BasicDBList();//adJustInfo
            BasicDBObject basicDBObject=new BasicDBObject();
            basicList.add(new BasicDBObject("adJustInfo.bill_code",vipPointsAdjust.getBill_code()));
            if(StringUtils.isNotBlank(state)){
                basicList.add(new BasicDBObject("state",state));
            }
            if(StringUtils.isNotBlank(vip_name)){
                basicList.add(new BasicDBObject("vip.vip_name",new BasicDBObject("$regex",vip_name)));
            }
            if(StringUtils.isNotBlank(vip_cardno)){
                basicList.add(new BasicDBObject("vip.cardno",new BasicDBObject("$regex",vip_cardno)));
            }
            if(StringUtils.isNotBlank(remarks)){
                basicList.add(new BasicDBObject("remarks",new BasicDBObject("$regex",remarks)));
            }
            basicDBObject.put("$and",basicList);
            DBCursor dbCursor=dbCollection.find(basicDBObject);
            int total = dbCursor.count();
            int pages = MongoUtils.getPages(dbCursor, Integer.parseInt(page_size));

            if(Integer.parseInt(page_num)>pages){
                if(pages==0){
                    pages=1;
                }
                dbCursor = MongoUtils.sortAndPage(dbCursor, pages, Integer.parseInt(page_size), "modified_date", -1);
            }else{
                dbCursor = MongoUtils.sortAndPage(dbCursor, Integer.parseInt(page_num), Integer.parseInt(page_size), "modified_date", -1);
            }


            ArrayList arrayList= MongoHelperServiceImpl.dbCursorToList_id(dbCursor);
            JSONObject result=new JSONObject();
            result.put("list", arrayList);
            result.put("pages", pages);
            result.put("page_num", page_num);
            result.put("page_size", page_size);
            result.put("total", total);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
            dataBean.setMessage(result.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return  dataBean.getJsonStr();
    }



    //删除
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    @ResponseBody
    public  String  deleteVip(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{

            MongoTemplate mongoTemplate=this.mongodbClient.getMongoTemplate();
            DBCollection dbCollection= mongoTemplate.getCollection(CommonValue.table_vip_points_adjust);
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject message_obj = JSON.parseObject(message);
            String type=message_obj.getString("type");
            String vip_id=message_obj.getString("vip_id");
            String adjust_id=message_obj.getString("adjust_id");//调整单id
            VipPointsAdjust vipPointsAdjust=vipPointsAdjustService.selectPointsAdjustById(Integer.parseInt(adjust_id));
            BasicDBList basicList=new BasicDBList();//adJustInfo
            BasicDBObject basicDBObject=new BasicDBObject();
            if("select".equals(type)) {
                String[] vip_ids = vip_id.split(",");
                BasicDBList cardList = new BasicDBList();
                for (int i = 0; i < vip_ids.length; i++) {
                    cardList.add(vip_ids[i]);
                }
                basicList.add(new BasicDBObject("vip.vip_id",new BasicDBObject("$in",cardList)));
            }
            basicList.add(new BasicDBObject("adJustInfo.bill_code",vipPointsAdjust.getBill_code()));
            basicDBObject.put("$and",basicList);
            dbCollection.remove(basicDBObject);

            dataBean.setMessage("删除成功");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setMessage("删除失败");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
        }
        return dataBean.getJsonStr();
    }

    //编辑积分 备注
    @RequestMapping(value = "/edit",method = RequestMethod.POST)
    @ResponseBody
    public  String editVips(HttpServletRequest request){
        DataBean dataBean=new DataBean();
        try{
            MongoTemplate mongoTemplate=this.mongodbClient.getMongoTemplate();
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject message_obj = JSON.parseObject(message);
            String type=message_obj.getString("type");
            String vip_id=message_obj.getString("vip_id");
            String adjust_id=message_obj.getString("adjust_id");//调整单id
            String sendPoints=message_obj.getString("sendPoints");
            String remarks=message_obj.getString("remarks");
            VipPointsAdjust vipPointsAdjust=vipPointsAdjustService.selectPointsAdjustById(Integer.parseInt(adjust_id));
            String bill_code=vipPointsAdjust.getBill_code();
            List<BathUpdateOptions> list_value = new ArrayList<BathUpdateOptions>();
            if(type.equals("select")){
                String[] vip_ids = vip_id.split(",");
                if(vip_ids.length>1){
                    for (int i = 0; i < vip_ids.length; i++) {
                        Query query=new Query();
                        query.addCriteria(Criteria.where("_id").is(bill_code+vip_ids[i]));
                        Update update=new Update();
                        update.set("sendPoints",sendPoints);
                        list_value.add(new BathUpdateOptions(query, update, false, true));
                    }
                }else{
                    Query query=new Query();
                    query.addCriteria(Criteria.where("_id").is(bill_code+vip_ids[0]));
                    Update update=new Update();
                    update.set("remarks",remarks);
                    update.set("sendPoints",sendPoints);
                    list_value.add(new BathUpdateOptions(query, update, false, true));
                }

            }else{
                Query query=new Query();
                query.addCriteria(Criteria.where("adJustInfo.bill_code").is(bill_code));
                Update update=new Update();
                update.set("sendPoints",sendPoints);
                list_value.add(new BathUpdateOptions(query, update, false, true));
            }

            if(list_value.size()>0){
                List<List> lists= WebUtils.cutListByArray(list_value,1000);
                for (int i = 0; i < lists.size(); i++) {
                    //插入更新数据
                    UpdateMongoDBUtils.bathUpdate(mongoTemplate, CommonValue.table_vip_points_adjust, lists.get(i));
                }
            }else{
                dataBean.setMessage("编辑失败");
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                return  dataBean.getJsonStr();
            }
            dataBean.setMessage("编辑成功");
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId("1");
        }catch (Exception e){
            dataBean.setMessage("编辑失败");
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            e.printStackTrace();
        }
        return dataBean.getJsonStr();
    }


    /***
     * Execl增加
     */
    @RequestMapping(value = "/excludeMultipart", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String addByExecl(HttpServletRequest request) {
        DataBean dataBean = new DataBean();
        String result = "";
        Workbook rwb = null;
        try {
            MongoTemplate mongoTemplate=this.mongodbClient.getMongoTemplate();
            String role_code = request.getSession().getAttribute("role_code").toString();
            String brand_code = request.getSession().getAttribute("brand_code").toString();
            String area_code = request.getSession().getAttribute("area_code").toString();
            String store_code = request.getSession().getAttribute("store_code").toString();
            String user_code = request.getSession().getAttribute("user_code").toString();

            //创建你要保存的文件的路径
            String path = request.getSession().getServletContext().getRealPath("lupload");

            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> items=upload.parseRequest(request);
            Iterator<FileItem> iter = items.iterator() ;//将全部的内容变为Iterator实例

            List<HashMap<String,Object>> list=new ArrayList<HashMap<String, Object>>();
            while (iter.hasNext()){
                HashMap<String,Object> map=new HashMap<String, Object>();
                FileItem item= iter.next();
                String fieldName = item.getFieldName() ;  // 取得表单控件的名称
                if(!item.isFormField()){    // 判断是否为普通文本
                    String filename = item.getName() ;  // 取得文件的名称
                    logger.info("loggggggggggggggggggg"+fieldName+"        "+filename);
                    filename=filename.split("\\.")[0]+Common.DATETIME_FORMAT_NUM.format(new Date())+".xls";
                    InputStream inputStream= item.getInputStream();
                    OutExeclHelper outExecl=new OutExeclHelper();
                    outExecl.writeFile(path,filename,inputStream);//写入文件
                    map.put("name",fieldName);
                    map.put("value",filename);
                } else {
                    String value = item.getString();
                    map.put("name", fieldName);
                    map.put("value", value);
                }
                list.add(map);
            }
            String adjust_id="";
            String corp_code1="";
            String file_name="";
            for (int i = 0; i < list.size(); i++) {
                HashMap<String,Object> map=list.get(i);
                String name=map.get("name").toString();
                String value= map.get("value").toString();
                if(name.equals("adjust_id")){
                    adjust_id=value;
                }
                if(name.equals("corp_code")){
                    corp_code1=value;
                }
                if(name.equals("file")){
                    file_name=value;
                }
            }

            logger.info("=========="+adjust_id+"   "+corp_code1+"   "+file_name);

            VipPointsAdjust vipPointsAdjust=vipPointsAdjustService.selectPointsAdjustById(Integer.parseInt(adjust_id));

            JSONObject adjust_obj=WebUtils.bean2JSONObject(vipPointsAdjust);

            String bill_code=vipPointsAdjust.getBill_code();//单据编号

            File targetFile=new File(path,file_name);
            if(!targetFile.exists()){
                targetFile.createNewFile();
            }

            rwb = Workbook.getWorkbook(targetFile);
            Sheet rs = rwb.getSheet(0);//或者rwb.getSheet(0)
            int rows = rs.getRows();//得到所有的行
            if (rows < 4) {
                //删除文件
                File file2=new File(path,file_name);
                if(file2.exists()&&file2.isFile()){
                    file2.delete();
                }
                result = "：请从模板第4行开始插入正确数据";
                int i = 5 / 0;
            }
            if (rows > 5000) {
                //删除文件
                File file2=new File(path,file_name);
                if(file2.exists()&&file2.isFile()){
                    file2.delete();
                }
                result = "：数据量过大，导入失败";
                int i = 5 / 0;
            }

            JSONArray cardArray=new JSONArray();
            for (int i = 3; i < rows; i++) {
                String cardno = rs.getCell(0, i).getContents().toString().trim();
                String  sendPoints = rs.getCell(1, i).getContents().toString().trim();
                String remarks = rs.getCell(2, i).getContents().toString().trim();
                if (cardno.equals("") && sendPoints.equals("") && remarks.equals("")) {
                    continue;
                }
                if (cardno.equals("") || sendPoints.equals("")) {
                    //删除文件
                    File file2=new File(path,file_name);
                    if(file2.exists()&&file2.isFile()){
                        file2.delete();
                    }
                    result = "：第" + (i + 1) + "行信息不完整,请参照Execl中对应的批注";
                    int a = 5 / 0;
                }
                JSONObject cardObj=new JSONObject();
                cardObj.put("cardno",cardno);
                cardObj.put("sendPoints",sendPoints);
                cardObj.put("remarks",remarks);
                cardArray.add(cardObj);
            }

            //系统管理员筛选权限到企业 其他账号筛选根据自身权限
            JSONArray post_array=null;
            if(role_code.equals(Common.ROLE_SYS)){
                post_array  = vipGroupService.vipScreen2ArrayNew(new JSONArray(), corp_code1, Common.ROLE_GM, "", "", "", "");
            }else{
                post_array  = vipGroupService.vipScreen2ArrayNew(new JSONArray(), corp_code1, role_code, brand_code, area_code, store_code, user_code);
            }
            DataBox dataBox=iceInterfaceService.getVipFromCardBySolr(cardArray.toString(),post_array.toString(),corp_code1);

            if (dataBox.status.toString().equals("SUCCESS")){
                String message_value = dataBox.data.get("message").value;
                JSONObject vip_obj=JSON.parseObject(message_value);
                JSONArray all_vip_list=vip_obj.getJSONArray("all_vip_list");
                //logger.info("导入==============="+all_vip_list.toString());
                int size=all_vip_list.size();
                List<BathUpdateOptions> list_value = new ArrayList<BathUpdateOptions>();
                for (int i = 0; i < size; i++) {
                    JSONObject vipInfo=all_vip_list.getJSONObject(i);
                    String vip_id=vipInfo.getString("vip_id");
                    String sendPoints=vipInfo.getString("sendPoints");
                    String remarks=vipInfo.getString("remarks");

                    vipInfo.remove("sendPoints");
                    vipInfo.remove("remarks");

                    Query query=new Query();
                    query.addCriteria(Criteria.where("_id").is(bill_code+vip_id));

                    Update update=new Update();

                    BSONObject bsonObject=new BasicBSONObject();
                    bsonObject.putAll(vipInfo);
                    update.set("vip",bsonObject);

                    BSONObject bsonObject1=new BasicBSONObject();
                    bsonObject1.putAll(adjust_obj);
                    update.set("adJustInfo",bsonObject1);//单据信息

                    update.set("corp_code",corp_code1);
                    update.set("sendPoints",sendPoints);
                    update.set("modified_date",Common.DATETIME_FORMAT.format(new Date())); //调整时间
                    update.set("_id",bill_code+vip_id);
                    update.set("state","N");//未提交
                    update.set("vip_bill_code",bill_code+vip_id);//线下单据
                    if(StringUtils.isBlank(remarks)){
                        update.set("remarks","手动导入调整积分");
                    }else{
                        update.set("remarks",remarks==null?"":remarks);
                    }
                    list_value.add(new BathUpdateOptions(query, update, true, true));
                }

                logger.info("====list_value===="+list_value.size());
                //插入更新数据
                if(list_value.size()>0){
                   List<List> lists= WebUtils.cutListByArray(list_value,1000);
                    for (int i = 0; i < lists.size(); i++) {
                        UpdateMongoDBUtils.bathUpdate(mongoTemplate, CommonValue.table_vip_points_adjust, lists.get(i));
                    }
                    dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                    dataBean.setId("1");
                    dataBean.setMessage("导入成功");
                }else{
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setId("1");
                    dataBean.setMessage("导入数据信息有误");
                }


            }else {
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setId("1");
                dataBean.setMessage("导入失败");
            }

            //删除文件
            File file2=new File(path,file_name);
            if(file2.exists()&&file2.isFile()){
                file2.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(result);
        } finally {
            if (rwb != null) {
                rwb.close();
            }
        }
        return dataBean.getJsonStr();
    }


    @RequestMapping(value = "/outExecl", method = RequestMethod.POST)
    @ResponseBody
    public  String outExecl(HttpServletRequest request, HttpServletResponse response){
        DataBean dataBean=new DataBean();
        String errormessage = "数据异常，导出失败";
        try{
            MongoTemplate mongoTemplate=this.mongodbClient.getMongoTemplate();
            DBCollection dbCollection= mongoTemplate.getCollection(CommonValue.table_vip_points_adjust);
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject message_obj = JSON.parseObject(message);
            String adjust_id=message_obj.getString("adjust_id");//调整单id
            String page_num=message_obj.getString("page_num");
            String page_size=message_obj.getString("page_size");
            String search_value=message_obj.getString("search_value");
            JSONObject screen_obj=JSON.parseObject(search_value);
            String vip_name_screen=screen_obj.getString("vip_name");
            String vip_cardno_screen=screen_obj.getString("vip_cardno");
            String remarks_screen=screen_obj.getString("remarks");
            String state_screen =screen_obj.getString("state");
            VipPointsAdjust vipPointsAdjust=vipPointsAdjustService.selectPointsAdjustById(Integer.parseInt(adjust_id));
            BasicDBList basicList=new BasicDBList();//adJustInfo
            BasicDBObject basicDBObject=new BasicDBObject();
            basicList.add(new BasicDBObject("adJustInfo.bill_code",vipPointsAdjust.getBill_code()));
            if(StringUtils.isNotBlank(state_screen)){
                basicList.add(new BasicDBObject("state",state_screen));
            }
            if(StringUtils.isNotBlank(vip_name_screen)){
                basicList.add(new BasicDBObject("vip.vip_name",new BasicDBObject("$regex",vip_name_screen)));
            }
            if(StringUtils.isNotBlank(vip_cardno_screen)){
                basicList.add(new BasicDBObject("vip.cardno",new BasicDBObject("$regex",vip_cardno_screen)));
            }
            if(StringUtils.isNotBlank(remarks_screen)){
                basicList.add(new BasicDBObject("remarks",new BasicDBObject("$regex",remarks_screen)));
            }
            basicDBObject.put("$and",basicList);
            DBCursor dbCursor=dbCollection.find(basicDBObject);
            int total = dbCursor.count();
            int pageNum = Integer.parseInt(page_num);
            int pageSize = Integer.parseInt(page_size);
            int start_line = (pageNum-1) * pageSize + 1;
            int end_line = pageNum*pageSize;
            if (total < pageNum*pageSize)
                end_line = total;
            dbCursor = MongoUtils.sortAndPage(dbCursor,pageNum, pageSize, "modified_date", -1);
            ArrayList arrayList=MongoHelperServiceImpl.dbCursorToList_id(dbCursor);
            int size=arrayList.size();
            JSONArray viparray=new JSONArray();
            for (int i = 0; i < size; i++) {
                Map<String,Object> json_obj= (Map<String, Object>) arrayList.get(i);
                JSONObject vip_obj=JSON.parseObject(json_obj.get("vip").toString());
                String cardno=vip_obj.getString("cardno");
                String vip_name=vip_obj.getString("vip_name");
                String points=vip_obj.getString("points");
                String sendPoints=json_obj.get("sendPoints").toString();
                String remarks=json_obj.get("remarks")==null?"":json_obj.get("remarks").toString();
                String fr_active=vip_obj.getString("fr_active");
                String state=json_obj.get("state").toString();
                String state_info=json_obj.get("state_info")==null?"":json_obj.get("state_info").toString();
                JSONObject obj=new JSONObject();
                obj.put("cardno",cardno);
                obj.put("vip_name",vip_name);
                obj.put("points",points);
                obj.put("sendPoints",sendPoints);
                obj.put("remarks",remarks);
                obj.put("fr_active",fr_active);
                if(state.equals("未提交")){
                    if(!"".equals(state_info)){
                        state=state+"("+JSON.parseObject(state_info).getString("message")+")";
                    }
                }
                obj.put("state",state);
                viparray.add(obj);
            }
            LinkedHashMap linkMap=new LinkedHashMap();
            linkMap.put("vip_name","会员名称");
            linkMap.put("cardno","会员卡号");
            linkMap.put("points","积分余额");
            linkMap.put("sendPoints","调整积分");
            linkMap.put("remarks","备注");
            linkMap.put("state","提交状态");
            String pathname=OutExeclHelper.OutExecl2(viparray.toString(),viparray,linkMap,response,request,"积分调整("+start_line+"-"+end_line+")");
            JSONObject result2 = new JSONObject();
            if (pathname == null || pathname.equals("")) {
                errormessage = "数据异常，导出失败";
                int a = 8 / 0;
            }
            result2.put("path", "lupload/" + pathname);
            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
            dataBean.setId(id);
            dataBean.setMessage(result2.toString());
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("1");
            dataBean.setMessage(errormessage);
        }
        return  dataBean.getJsonStr();
    }

    //提交
    @RequestMapping(value = "/commit",method = RequestMethod.POST)
    @ResponseBody
    public  String  commitVip(HttpServletRequest request){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DataBean dataBean=new DataBean();
        try{
            String jsString = request.getParameter("param");
            JSONObject jsonObj = JSONObject.parseObject(jsString);
            String id = jsonObj.get("id").toString();
            String message = jsonObj.get("message").toString();
            JSONObject message_obj = JSON.parseObject(message);
            String adjust_id=message_obj.getString("adjust_id");//调整单id
           // String bill_voucher=message_obj.getString("bill_voucher");
            VipPointsAdjust vipPointAdjust=vipPointsAdjustService.selectPointsAdjustById(Integer.parseInt(adjust_id));
            String bill_code=vipPointAdjust.getBill_code();
            DataBox dataBox=iceInterfaceAPIService.adjustVipPoints(bill_code,vipPointAdjust.getCorp_code());
            if(dataBox.status.toString().equals("SUCCESS")){
                String value=dataBox.data.get("message").value;
                JSONObject jsonObject=JSON.parseObject(value);
                int success_count = jsonObject.getInteger("success_count");
                int fail_count = jsonObject.getInteger("fail_count");
                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
                dataBean.setMessage("提交成功数目:"+success_count+"条"+"\n\r"+"提交失败条数:"+fail_count+"条");
                dataBean.setId("1");

                if(success_count==0) {
                    dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                    dataBean.setMessage("提交成功数目:"+success_count+"条"+"\n\r"+"提交失败条数:"+fail_count+"条");
                    dataBean.setId("1");
                    return  dataBean.getJsonStr();
                }

                //更新表
                vipPointsAdjustService.updateBillState(Integer.parseInt(adjust_id),null,"1",simpleDateFormat.format(new Date()));

            }else{
                dataBean.setCode(Common.DATABEAN_CODE_ERROR);
                dataBean.setMessage("提交失败");
                dataBean.setId("1");
            }
        }catch (Exception e){
            e.printStackTrace();
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setMessage("提交失败");
            dataBean.setId("1");
        }
        return dataBean.getJsonStr();
    }


}
