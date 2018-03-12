package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.ApprovedMapper;
import com.bizvane.ishop.entity.Approved;
import com.bizvane.ishop.service.ApprovedService;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.ishop.utils.WebUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yanyadong on 2017/4/17.
 */



@Service
public class ApprovedServiceImpl implements ApprovedService {
    @Autowired
    ApprovedMapper approvedMapper;
    @Autowired
    MongoDBClient mongodbClient;

    @Override
    public Approved selectById(int id) throws Exception {
        Approved approved=approvedMapper.selectById(id);
        return approved;
    }

    @Override
    public Approved selectByApprovedName(String approved_name, String corp_code) throws Exception {
        Approved approved=approvedMapper.selectByApprovedName(approved_name,corp_code);
        return approved;
    }

    @Override
    public PageInfo<Approved> selectAll(int page_num, int page_size, String corp_code, String search_value) throws Exception {
        PageHelper.startPage(page_num,page_size);
        List<Approved> list=approvedMapper.selectAll(corp_code,search_value);
        add(list);
        PageInfo<Approved> pageInfo=new PageInfo<Approved>(list);
        return pageInfo;
    }
    @Override
    public  JSONObject approvalDetails( String corp_code,int approved_id,int page_size,int page_number,String created_date) throws Exception {
        //approved_id":"100","corp_code":"C10000","created_date":"2017-06-03","pageNumber":1,"pageSize":10
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_approved_log);
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("corp_code", corp_code);
        dbObject.put("approved_id", approved_id+"");
        dbObject.put("create_date", created_date);
        DBCursor details = cursor.find(dbObject);
        int total = details.count();
        int pages = 0;
        pages = MongoUtils.getPages(details,page_size);
        DBCursor dbCursor = null;
        dbCursor = MongoUtils.sortAndPage(details,page_number,page_size,"create_date",-1);
        JSONObject json_data = new JSONObject();
        json_data.put("details",(MongoUtils.dbCursorToList_id(dbCursor)));
        json_data.put("pages", pages);
        json_data.put("page_number", page_number);
        json_data.put("page_size", page_size);
        json_data.put("total", total);
        return json_data;
    }

    @Override
    public int deleteById(int id) throws Exception {
        return approvedMapper.deleteById(id);
    }

    @Override
    public int insertApproved(Approved approved) throws Exception {
        return approvedMapper.insertApproved(approved);
    }

    @Override
    public int updateApproved(Approved approved) throws Exception {
        return approvedMapper.updateApproved(approved);
    }

    @Override
    public PageInfo<Approved> selectAllScreen(int page_num, int page_size,String corp_code,Map<String, Object> params) throws Exception {
        List<Approved> list;
        PageHelper.startPage(page_num,page_size);
        HashMap<String,Object> map=new HashMap<String, Object>();

        Set<String> sets=params.keySet();
        if(sets.contains("created_date")) {
            JSONObject date = JSONObject.parseObject(params.get("created_date").toString());
            map.put("created_date_start", date.get("start").toString());
            String end = date.get("end").toString();
             if (!end.equals(""))
                 end = end + " 23:59:59";
            map.put("created_date_end", end);
            params.remove("created_date");
        }
        map.put("corp_code",corp_code);
        map.put("map",params);
        list=approvedMapper.selectAllScreen(map);

        add(list);
        PageInfo<Approved> pageInfo=new PageInfo<Approved>(list);
        return pageInfo;
    }

    @Override
    public List<Approved> selectAll2(String corp_code, String search_value) throws Exception {
        List<Approved> list=approvedMapper.selectAll(corp_code,search_value);
        return list;
    }
    public PageInfo<Approved> switchApproved(PageInfo<Approved> list) throws  Exception{

        for(int i=0;i<list.getList().size();i++){
            Approved approved=list.getList().get(i);
            if(approved.getApproved_type().equals("Y")){
                String[] cycle=approved.getApproved_cycle().split(" ");
                approved.setApproved_cycle(cycle[4]+"-"+cycle[3]);
            }else{
                String[] cycle=approved.getApproved_cycle().split(" ");
                approved.setApproved_cycle(cycle[3]);
            }
        }
        return  list;
    }
    @Override
    public void add(List<Approved> list) {
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_approved_log);
        for (int i = 0; i < list.size(); i++) {
            Approved approved = list.get(i);
            String corpCode = approved.getCorp_code();
            int approved_id = approved.getId();
            BasicDBObject dbObject = new BasicDBObject();
            dbObject.put("corp_code", corpCode);
            dbObject.put("approved_id", approved_id);
            DBCursor time = cursor.find(dbObject);
            int expiredMember = time.count(); //过期的会员数量
            approved.setExpired_member(expiredMember);
            while (time.hasNext()) {
                DBObject dbObject4 = time.next();
                String created_date = dbObject4.get("created_date").toString();
                approved.setCreated_date(created_date);  //核准时间
            }
            BasicDBObject dbObject1 = new BasicDBObject();
            String degrade = "degrade";
            dbObject1.put("corp_code", corpCode);
            dbObject1.put("approved_id", approved_id);
            dbObject1.put("type", degrade);
            int degrade1 = cursor.find(dbObject1).count(); //降级的会员数量
            approved.setDegrade(degrade1);

            BasicDBObject dbObject2 = new BasicDBObject();
            String keep = "keep";
            dbObject2.put("corp_code", corpCode);
            dbObject2.put("approved_id", approved_id);
            dbObject2.put("type", keep);
            int keep1 = cursor.find(dbObject2).count(); //原级的会员数量
            approved.setKeep(keep1);

        }

    }

    @Override
    public  JSONObject approvedList(JSONObject jsonObject,String corp_code) throws Exception {
        int page_number = Integer.parseInt(jsonObject.getString("pageNumber").toString());    //取出页数
        int page_size = Integer.parseInt(jsonObject.getString("pageSize").toString());         //取出页数
        String searchValue = jsonObject.getString("searchValue");
        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_approved_log);
        BasicDBObject match = new BasicDBObject();                              //定义一个match
        //判断参数中的type是不是1是1代表是搜索的接口
            List<Approved> list = selectAll2(corp_code, searchValue);
//            System.out.println("=======size====="+list.size());
            BasicDBList basicDBList = new BasicDBList();
            for (int i = 0; i < list.size(); i++) {
                Approved approved = list.get(i);
                String id = approved.getId()+"";
                basicDBList.add(id);
//                System.out.println("===id===="+id);
            }
            match.put("approved_id", new BasicDBObject("$in", basicDBList));
        if(!corp_code.equals("")){
            match.put("corp_code",corp_code);
        }
        //查询
        BasicDBObject match1 = new BasicDBObject("$match", match);
         /* Group操作*/
        DBObject groupField = new BasicDBObject("_id", new BasicDBObject("approved_id", "$approved_id").append("create_date", "$create_date"));
        groupField.put("count", new BasicDBObject("$sum", 1));
        DBObject group= new BasicDBObject("$group", groupField);
        //分页
        BasicDBObject basicSkip=new BasicDBObject("$skip",(page_number - 1) * page_size);
        BasicDBObject basicLimit=new BasicDBObject("$limit",page_size);
        AggregationOutput output;
        AggregationOutput output1;
        //分组
        output = cursor.aggregate(match1, group, basicSkip, basicLimit);

        //获取数据集合
        JSONArray objects=new JSONArray();

        for(DBObject dbObject:output.results()) {
            //会员信息
            String all = dbObject.get("_id").toString();
            JSONObject all2=JSON.parseObject(all);
            String approvedId=all2.getString("approved_id").toString();
            String createDate = all2.getString("create_date");
            BasicDBObject dbObject1 = new BasicDBObject();
            dbObject1.put("approved_id", approvedId);
            dbObject1.put("create_date", createDate);
            DBCursor  details= cursor.find(dbObject1);
            int expiredMember = details.count(); //过期的会员数量

            BasicDBObject dbObject2 = new BasicDBObject();
            String degrade = "degrade";
            dbObject2.put("create_date", createDate);
            dbObject2.put("approved_id", approvedId);
            dbObject2.put("type", degrade);
            int degrade1 = cursor.find(dbObject2).count(); //降级的会员数量


            BasicDBObject dbObject3 = new BasicDBObject();
            String keep = "keep";
            dbObject3.put("create_date", createDate);
            dbObject3.put("approved_id", approvedId);
            dbObject3.put("type", keep);
            int keep1 = cursor.find(dbObject3).count(); //原级的会员数量
            Approved approved=selectById(Integer.parseInt(approvedId));
//            if(approved==null){
//                continue;
//            }
            JSONObject result=new JSONObject();
            if(approved.getApproved_type().equals("Y")){
                String[] cycle=approved.getApproved_cycle().split(" ");
                approved.setApproved_cycle(cycle[4]+"-"+cycle[3]);
            }else{
                String[] cycle=approved.getApproved_cycle().split(" ");
                approved.setApproved_cycle(cycle[3]);
            }
            result.put("expired_member",expiredMember);                    //过期的会员数量
            result.put("degrade",degrade1);                              //降级的会员数量
            result.put("keep",keep1);
            result.put("created_date",createDate);//最近核准时间
            result.put("approved_name",approved.getApproved_name());
            result.put("remarks",approved.getRemarks());
            result.put("approved_cycle",approved.getApproved_cycle());
            result.put("approved_id",approved.getId());
            result.put("corp_code",approved.getCorp_code());
            result.put("approved_type",approved.getApproved_type());

            //原级的会员数量
           // result.put("details",(MongoUtils.dbCursorToList_id(details)));//全部详情
            objects.add(result);
        }

        //总页数
        int count = 0;
        output1 = cursor.aggregate(match1, group);
        for(DBObject objects1:output1.results()){
//            String all = objects1.get("_id").toString();
//            JSONObject all2=JSON.parseObject(all);
//           int  approvedId=Integer.parseInt(all2.getString("approved_id").toString());
//            Approved approved=selectById(approvedId);
//            if (approved==null){
//                continue;
//            }
            ++count;
        }

        int pages = 0;
        if (count % page_size == 0) {
            pages = count / page_size;
        } else {
            pages = count / page_size + 1;
        }
        boolean flag=true;
        if(page_number>=pages){
            flag=false;
        }
        JSONObject result2=new JSONObject();
        result2.put("total",count);
        result2.put("page_number",page_number);
        result2.put("page_size",page_size);
        result2.put("pages",pages);
        result2.put("list",objects);
        return result2;
    }

    @Override
    public  JSONObject screenApprovedList(String message,String role_code,String corp_code) throws Exception {

        MongoTemplate mongoTemplate = this.mongodbClient.getMongoTemplate();
        DBCollection cursor = mongoTemplate.getCollection(CommonValue.table_vip_approved_log);
        BasicDBList  matchList=new BasicDBList();           //new一个BasicDBList
        JSONObject jsonObject=JSON.parseObject(message);        //从json里面拿出message
        int  page_number=Integer.parseInt(jsonObject.getString("pageNumber").toString());   //从jsonObject里面拿出页数相关
        int page_size=Integer.parseInt(jsonObject.getString("pageSize").toString());
        String timeValue = "";                                   //定义一个空字符串用来放时间
        /**
         *  .....................筛选操作......................
         */
        if(!jsonObject.getString("list").equals("")) {                  //把list里面的created_date取出来再remove掉
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject screenJson = jsonArray.getJSONObject(i);
                if (screenJson.getString("screen_key").equals("created_date")) {
                    timeValue = screenJson.getString("screen_value");
                    jsonArray.remove(i);
                    break;
                }
            }
            jsonObject.put("list", jsonArray);      //把去掉created_date的jsonArray放到jsonObject
//            System.out.println("===筛选==="+jsonObject.toString());
            Map<String, Object> map = WebUtils.Json2Map(jsonObject);  //把jsonObject放到map里面
            PageInfo<Approved> list = null;
            if (role_code.equals(Common.ROLE_SYS)) {
                list = selectAllScreen(1, Common.EXPORTEXECLCOUNT, "", map);
            } else {
                list = selectAllScreen(1, Common.EXPORTEXECLCOUNT, corp_code, map);
            }
            List<Approved> aa = list.getList();
            BasicDBList basicDBList = new BasicDBList();
            for (int i = 0; i < aa.size(); i++) {               //for循环取出approved_id
                Approved approved = aa.get(i);
                int id = approved.getId();
                basicDBList.add(id + "");
 //               System.out.println("=====id==="+id);
            }
            matchList.add(new BasicDBObject("approved_id", new BasicDBObject("$in", basicDBList))); //往matchList里面添加approved_id条件用$in连接查多个
        }
        if(!role_code.equals(Common.ROLE_SYS)){              //判断是否是管理员
            matchList.add(new BasicDBObject("corp_code",corp_code));//添加新查询用new BasicDBObject("corp_code",corp_code)格式
        }
        if(!timeValue.equals("")){          //判断是否是用时间搜索
            JSONObject timeJson=  JSON.parseObject(timeValue);
            String start=timeJson.getString("start");
            String end=timeJson.getString("end");
            if(!start.equals("")) {     //判断是否填写了开始时间
                matchList.add(new BasicDBObject("create_date", new BasicDBObject(QueryOperators.GTE, start)));      //在matchList里面加入了
            }
            if(!end.equals("")) {       //判断是否填写了结束时间
                matchList.add(new BasicDBObject("create_date", new BasicDBObject(QueryOperators.LTE, end)));
            }
        }
        BasicDBObject match=new BasicDBObject();
        match.put("$and",matchList);
        //查询
        BasicDBObject match1 = new BasicDBObject("$match", match);
//
//        BasicDBObject dbObject = new BasicDBObject();
//        dbObject.put("activity_code", activity_code);
//        dbObject.put("store_code", store_code);
//        BasicDBObject match = new BasicDBObject("$match", dbObject);

         /* Group操作*/
        DBObject groupField = new BasicDBObject("_id", new BasicDBObject("approved_id", "$approved_id").append("create_date", "$create_date"));
        groupField.put("count", new BasicDBObject("$sum", 1));
        DBObject group= new BasicDBObject("$group", groupField);
        //分页
        BasicDBObject basicSkip=new BasicDBObject("$skip",(page_number - 1) * page_size);
        BasicDBObject basicLimit=new BasicDBObject("$limit",page_size);
        AggregationOutput output;
        AggregationOutput output1;
        output = cursor.aggregate(match1, group, basicSkip, basicLimit);
        int count=0;
        JSONArray objects=new JSONArray();

        for(DBObject dbObject:output.results()) {
            //会员信息

            String all = dbObject.get("_id").toString();
            JSONObject all2=JSON.parseObject(all);
            int  approvedId=Integer.parseInt(all2.getString("approved_id").toString());
            String createDate = all2.getString("create_date");
            BasicDBObject dbObject1 = new BasicDBObject();
            dbObject1.put("approved_id", approvedId+"");
            dbObject1.put("create_date", createDate);
            DBCursor  details= cursor.find(dbObject1);
            int expiredMember = details.count(); //过期的会员数量

            BasicDBObject dbObject2 = new BasicDBObject();
            String degrade = "degrade";
            dbObject2.put("create_date", createDate);
            dbObject2.put("approved_id", approvedId+"");
            dbObject2.put("type", degrade);
            int degrade1 = cursor.find(dbObject2).count(); //降级的会员数量


            BasicDBObject dbObject3 = new BasicDBObject();
            String keep = "keep";
            dbObject3.put("create_date", createDate);
            dbObject3.put("approved_id", approvedId+"");
            dbObject3.put("type", keep);
            int keep1 = cursor.find(dbObject3).count(); //原级的会员数量
            Approved approved=selectById(approvedId);
//            if(approved==null){
//                continue;
//            }
            if(approved.getApproved_type().equals("Y")){
                String[] cycle=approved.getApproved_cycle().split(" ");
                approved.setApproved_cycle(cycle[4]+"-"+cycle[3]);
            }else{
                String[] cycle=approved.getApproved_cycle().split(" ");
                approved.setApproved_cycle(cycle[3]);
            }
            JSONObject result=new JSONObject();
            result.put("expired_member",expiredMember);                    //过期的会员数量
            result.put("degrade",degrade1);                              //降级的会员数量
            result.put("keep",keep1);
            result.put("created_date",createDate);//最近核准时间
            result.put("approved_name",approved.getApproved_name());
            result.put("remarks",approved.getRemarks());
            result.put("approved_cycle",approved.getApproved_cycle());
            result.put("approved_id",approved.getId());
            result.put("corp_code",approved.getCorp_code());
            result.put("approved_type",approved.getApproved_type());
            //原级的会员数量
            //result.put("details",(MongoUtils.dbCursorToList_id(details)));//全部详情
            objects.add(result);

        }
        output1 = cursor.aggregate(match1, group);
        for(DBObject objects1:output1.results()) {
//            String all = objects1.get("_id").toString();
//            JSONObject all2 = JSON.parseObject(all);
//            int approvedId = Integer.parseInt(all2.getString("approved_id").toString());
//            Approved approved = selectById(approvedId);
//            if (approved == null) {
//                continue;
//            }
            ++count;
        }
        int pages = 0;
        if (count % page_size == 0) {
            pages = count / page_size;
        } else {
            pages = count / page_size + 1;
        }
        boolean flag=true;
        if(page_number>=pages){
            flag=false;
        }
        JSONObject result2=new JSONObject();
        result2.put("total",count);
        result2.put("page_number",page_number);
        result2.put("page_size",page_size);
        result2.put("pages",pages);
        result2.put("list",objects);
        return result2;

    }


}
