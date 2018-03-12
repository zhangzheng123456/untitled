package com.bizvane.ishop.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.constant.CommonValue;
import com.bizvane.ishop.dao.QuestionnaireMapper;
import com.bizvane.ishop.entity.Questionnaire;
import com.bizvane.ishop.entity.VipTask;
import com.bizvane.ishop.service.QuestionnaireService;
import com.bizvane.ishop.service.VipTaskService;
import com.bizvane.ishop.utils.MongoUtils;
import com.bizvane.sun.common.service.mongodb.MongoDBClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by yanyadong on 2017/8/29.
 */
@Service
public class QuestionnaireServiceImpl implements QuestionnaireService{
    @Autowired
    QuestionnaireMapper questionnaireMapper;

    @Autowired
    VipTaskService vipTaskService;

    @Autowired
    MongoDBClient mongoDBClient;

    @Override
    public Questionnaire selectById(int id) throws Exception {
        Questionnaire questionnaire=questionnaireMapper.selectById(id);
        return questionnaire;
    }

    @Override
    public Questionnaire selectByQtnaireName(String corp_code, String title) throws Exception {
        Questionnaire questionnaire=questionnaireMapper.selectByQtnaireName(corp_code,title);
        return questionnaire;
    }

    @Override
    public PageInfo<Questionnaire> selectAll(int page_num, int page_size, String corp_code, String search_value) throws Exception {
        PageHelper.startPage(page_num,page_size);
        List<Questionnaire> list=questionnaireMapper.selectAll(corp_code,search_value);
        PageInfo<Questionnaire> pageInfo=new PageInfo<Questionnaire>(list);
        return pageInfo;
    }

    @Override
    public int deleteById(int id) throws Exception {

       int state= questionnaireMapper.deleteById(id);
        return state;
    }

    @Override
    public int insertQtnaire(Questionnaire questionnaire) throws Exception {
        int state=questionnaireMapper.insertQtnaire(questionnaire);
        return state;
    }

    @Override
    public int updateQtnaire(Questionnaire questionnaire) throws Exception {
        int state=questionnaireMapper.updateQtnaire(questionnaire);
        return state;
    }

    @Override
    public List<Questionnaire> selectAllScreen(String corp_code, Map<String, String> params) throws Exception {
        List<Questionnaire> questionnaireList;
        HashMap<String,Object> param=new HashMap<String, Object>();
        Set<String> sets=params.keySet();

        if(sets.contains("created_date")) {
            JSONObject date = JSONObject.parseObject(params.get("created_date").toString());
            param.put("created_date_start", date.get("start").toString());
            String end = date.get("end").toString();
             if (!end.equals(""))
                 end = end + " 23:59:59";
            param.put("created_date_end", end);
            params.remove("created_date");
        }

        if(sets.contains("modified_date")) {
            JSONObject date = JSONObject.parseObject(params.get("modified_date").toString());
            param.put("modified_date_start", date.get("start").toString());
            String end = date.get("end").toString();
             if (!end.equals(""))
                 end = end + " 23:59:59";
            param.put("modified_date_end", end);
            params.remove("modified_date");
        }

        param.put("corp_code",corp_code);
        param.put("map",params);
        questionnaireList=questionnaireMapper.selectAllScreen(param);
        return questionnaireList;
    }

    @Override
    public List<Questionnaire> selectAllQtNaire(String corp_code) throws Exception {
        List<Questionnaire> list=questionnaireMapper.selectAllQtNaire(corp_code);
        return list;
    }

    @Override
    public Object switchQtNaire(Object list) throws Exception {

        List<Questionnaire> list1=new ArrayList<Questionnaire>();
        PageInfo<Questionnaire> pageInfoList=null;
        if(list instanceof PageInfo){
            pageInfoList = (PageInfo<Questionnaire>) list;
            list1=pageInfoList.getList();
        }else{
            list1= (List<Questionnaire>) list;
        }

        int size=list1.size();
        for (int i = 0; i < size; i++) {
            Questionnaire qtNaire=list1.get(i);
            if(qtNaire.getIsrelease().equals("Y")){
                qtNaire.setIsrelease("已发布");
            }else{
                qtNaire.setIsrelease("未发布");
            }
        }
        if(list instanceof  PageInfo){
            return  pageInfoList;
        }else if(list instanceof List){
            return  list1;
        }
        return  0;
    }


    //获取所有与问卷调查相关的会员任务
    @Override
    public List<String> getIdsFromVipTask(String corp_code, String task_type) throws Exception {
        List<String> idsList=new ArrayList<String>();
       List<VipTask> list=vipTaskService.selectVipTaskByTaskType(corp_code,task_type);
        for (int i = 0; i < list.size(); i++) {
            VipTask vipTask=list.get(i);
            String condition=vipTask.getTask_condition();
            JSONObject jsonObj=JSON.parseObject(condition);
            String id=jsonObj.getString("id");
            idsList.add(id);
        }
        return idsList;
    }


    //mongoDB 获取问卷提交数量
    public Object getQtNaireNum(Object list,JSONObject jsonObject) throws Exception{

        MongoTemplate mongoTemplate=mongoDBClient.getMongoTemplate();

        DBCollection cursor=mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
        String start="";
        String end="";
        if(jsonObject!=null) {
             start= jsonObject.getString("start");
             end= jsonObject.getString("end");
        }
        List<Questionnaire> list1=new ArrayList<Questionnaire>();
        PageInfo<Questionnaire> pageInfoList=null;
        if(list instanceof PageInfo){
            pageInfoList= (PageInfo<Questionnaire>) list;
            list1=pageInfoList.getList();
        }else{
            list1= (List<Questionnaire>) list;
        }
        for (int i = 0; i <list1.size() ; i++) {
            Questionnaire qtNaire=list1.get(i);
            BasicDBList basicList=new BasicDBList();
            basicList.add(new BasicDBObject("task.task_type","questionnaire"));
            basicList.add(new BasicDBObject("corp_code",qtNaire.getCorp_code()));
            basicList.add(new BasicDBObject("task.qtNaire_id",qtNaire.getId()+""));
            basicList.add(new BasicDBObject("status","1"));
            if(StringUtils.isNotBlank(start)){
                basicList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, start+" 00:00:00")));
            }
            if(StringUtils.isNotBlank(end)){
                basicList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, end+" 23:59:59")));
            }
            BasicDBObject basic=new BasicDBObject();
            basic.put("$and",basicList);
            DBCursor dbCursor= cursor.find(basic);
            int count=dbCursor.count();
            qtNaire.setQtNaire_num(count);
        }

        if(list instanceof  PageInfo){
            return  pageInfoList;
        }else if(list instanceof List){
            return  list1;
        }
        return  0;
    }


    public JSONObject getQtNaireInfo(Questionnaire questionnaire,JSONObject jsonObj) throws Exception{
        final DecimalFormat df = new DecimalFormat("#.##");
        MongoTemplate mongoTemplate=mongoDBClient.getMongoTemplate();
        DBCollection cursor=mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
        //获取筛选时间
        String screen=jsonObj.getString("screen");
        JSONObject time_obj=JSON.parseObject(screen);
        String start=time_obj.getString("start");
        String end=time_obj.getString("end");
        int page_num=Integer.parseInt(jsonObj.getString("page_num"));
        int page_size=Integer.parseInt(jsonObj.getString("page_size"));
        String type=jsonObj.getString("type");

        JSONArray jsonarray=new JSONArray();
        BasicDBList basicDbList1=new BasicDBList();
        BasicDBObject basicObj1=new BasicDBObject();
        basicDbList1.add(new BasicDBObject("task.task_type","questionnaire"));
        basicDbList1.add(new BasicDBObject("corp_code",questionnaire.getCorp_code()));
        basicDbList1.add(new BasicDBObject("task.qtNaire_id",questionnaire.getId()+""));
        basicDbList1.add(new BasicDBObject("status","1"));
        //筛选条件 答题时间
        if(StringUtils.isNotBlank(start)){
            basicDbList1.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, start+" 00:00:00")));
        }
        if(StringUtils.isNotBlank(end)){
            basicDbList1.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, end+" 23:59:59")));
        }
        basicObj1.put("$and",basicDbList1);
        DBCursor dbCursor1=cursor.find(basicObj1);
        int all_count=dbCursor1.count();//答此问卷的总人数

        if (type.equals("all")) {
            String template=questionnaire.getTemplate();
            JSONArray templateArray=JSON.parseArray(template);

            for (int i = 0; i < templateArray.size(); i++) { //遍历问卷
                JSONObject template_obj=templateArray.getJSONObject(i);
                String name=template_obj.getString("name");
                String qt_type=template_obj.getString("type");
                String check=template_obj.getString("check");
                if(qt_type.equals("radio")){
                    JSONArray op_array=template_obj.getJSONArray("options");
                    JSONArray array=new JSONArray();
                    JSONObject json1=new JSONObject();
                    for (int j = 0; j < op_array.size(); j++) {
                        JSONObject json=new JSONObject();
                        BasicDBList basicDbList=new BasicDBList();
                        BasicDBObject basicObj=new BasicDBObject();
                        String op_value=op_array.getString(j);
                        basicDbList.add(new BasicDBObject("task.task_type","questionnaire"));
                        basicDbList.add(new BasicDBObject("corp_code",questionnaire.getCorp_code()));
                        basicDbList.add(new BasicDBObject("task.qtNaire_id",questionnaire.getId()+""));
                        basicDbList.add(new BasicDBObject("status","1"));
                        //筛选
                        /********/
                        if(StringUtils.isNotBlank(start)){
                            basicDbList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, start+" 00:00:00")));
                        }
                        if(StringUtils.isNotBlank(end)){
                            basicDbList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, end+" 23:59:59")));
                        }
                        BasicDBObject query=new BasicDBObject();
                        query.put("name",name);
                        query.put("type",qt_type);
                        query.put("value",op_value);
                        query.put("check",check);
                        basicDbList.add(new BasicDBObject("schedule",new BasicDBObject("$elemMatch",query)));
                        basicObj.put("$and",basicDbList);
                        int count=cursor.find(basicObj).size();
                        double rt=0.0;
                        if(all_count!=0){
                            rt =(double)count/all_count*100;
                        }
                        String rate=df.format(rt);
                        json.put("rate",rate+"%");
                        json.put("option",op_value);
                        json.put("num",count);
                        array.add(json);
                    }
                    json1.put("name",name);
                    json1.put("options",array);
                    json1.put("check",check);
                    json1.put("type",qt_type);
                    jsonarray.add(json1);
                }else if(qt_type.equals("checkbox")){
                    JSONArray op_array=template_obj.getJSONArray("options");
                    JSONArray array=new JSONArray();
                    JSONObject json1=new JSONObject();
                    for (int j = 0; j < op_array.size(); j++) {
                        JSONObject json=new JSONObject();
                        BasicDBList basicDbList=new BasicDBList();
                        BasicDBObject basicObj=new BasicDBObject();
                        String op_value=op_array.getString(j);
                        basicDbList.add(new BasicDBObject("task.task_type","questionnaire"));
                        basicDbList.add(new BasicDBObject("corp_code",questionnaire.getCorp_code()));
                        basicDbList.add(new BasicDBObject("task.qtNaire_id",questionnaire.getId()+""));
                        basicDbList.add(new BasicDBObject("status","1"));
                        //筛选
                        /********/
                        if(StringUtils.isNotBlank(start)){
                            basicDbList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, start+" 00:00:00")));
                        }
                        if(StringUtils.isNotBlank(end)){
                            basicDbList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, end+" 23:59:59")));
                        }
                        BasicDBObject query=new BasicDBObject();
                        query.put("name",name);
                        query.put("type",qt_type);
                        query.put("check",check);
                        query.put("value",new BasicDBObject("$regex",op_value));
                        basicDbList.add(new BasicDBObject("schedule",new BasicDBObject("$elemMatch",query)));
                        basicObj.put("$and",basicDbList);
                        int count=cursor.find(basicObj).size();
                        double rt=0.0;
                        if(all_count!=0){
                            rt =(double)count/all_count*100;
                        }
                        String rate=df.format(rt);
                        json.put("rate",rate+"%");
                        json.put("option",op_value);
                        json.put("num",count);
                        array.add(json);
                    }
                    json1.put("name",name);
                    json1.put("options",array);
                    json1.put("check",check);
                    json1.put("type",qt_type);
                    jsonarray.add(json1);

                }else if(qt_type.equals("input")){
                    JSONObject json=new JSONObject();
                    BasicDBList basicDbList=new BasicDBList();
                    BasicDBObject basicObj=new BasicDBObject();
                    basicDbList.add(new BasicDBObject("task.task_type","questionnaire"));
                    basicDbList.add(new BasicDBObject("corp_code",questionnaire.getCorp_code()));
                    basicDbList.add(new BasicDBObject("task.qtNaire_id",questionnaire.getId()+""));
                    basicDbList.add(new BasicDBObject("status","1"));
                    //筛选
                    /********/
                    if(StringUtils.isNotBlank(start)){
                        basicDbList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, start+" 00:00:00")));
                    }
                    if(StringUtils.isNotBlank(end)){
                        basicDbList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, end+" 23:59:59")));
                    }
                    BasicDBObject query=new BasicDBObject();
                    query.put("name",name);
                    query.put("type",qt_type);
                    basicDbList.add(new BasicDBObject("schedule",new BasicDBObject("$elemMatch",query)));
                    basicObj.put("$and",basicDbList);
                    DBCursor dbCursor=cursor.find(basicObj);
                    List<String> input_list=new ArrayList<String>();
                    while (dbCursor.hasNext()){
                        String input_info="";
                        DBObject dbObject=dbCursor.next();
                        String array=dbObject.get("schedule").toString();
                        JSONArray input_array=JSON.parseArray(array);
                        for (int j = 0; j <input_array.size() ; j++) {
                            JSONObject input_obj=input_array.getJSONObject(j);
                            String input_type=input_obj.getString("type");
                            String input_name=input_obj.getString("name");
                            if("input".equals(input_type)&&name.equals(input_name)){
                                 input_info=input_obj.getString("value");
                            }
                        }
                        if(StringUtils.isNotBlank(input_info.trim())) {
                            input_list.add(input_info);
                        }
                    }
                    json.put("size",input_list.size());
                   // json.put("list",input_list);
                    json.put("check",check);
                    json.put("name",name);
                    json.put("type",qt_type);
                    jsonarray.add(json);

                }else if(qt_type.equals("grade")){
                    String[] options=new String[]{"1","2","3","4","5"};
                    JSONObject json1=new JSONObject();
                    JSONArray array=new JSONArray();
                    for (int j = 0; j < options.length; j++) {
                        JSONObject json=new JSONObject();
                        BasicDBList basicDbList=new BasicDBList();
                        BasicDBObject basicObj=new BasicDBObject();
                        basicDbList.add(new BasicDBObject("task.task_type","questionnaire"));
                        basicDbList.add(new BasicDBObject("corp_code",questionnaire.getCorp_code()));
                        basicDbList.add(new BasicDBObject("task.qtNaire_id",questionnaire.getId()+""));
                        basicDbList.add(new BasicDBObject("status","1"));
                        //筛选
                        /********/
                        if(StringUtils.isNotBlank(start)){
                            basicDbList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, start+" 00:00:00")));
                        }
                        if(StringUtils.isNotBlank(end)){
                            basicDbList.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, end+" 23:59:59")));
                        }
                        BasicDBObject query=new BasicDBObject();
                        query.put("name",name);
                        query.put("type",qt_type);
                        query.put("check",check);
                        query.put("value",options[j]);
                        basicDbList.add(new BasicDBObject("schedule",new BasicDBObject("$elemMatch",query)));
                        System.out.println("====name===="+name);
                        System.out.println("=====type===="+type);
                        basicObj.put("$and",basicDbList);
                        DBCursor dbCursor=cursor.find(basicObj);
                        int count=dbCursor.count();
                        double rt=0.0;
                        if(all_count!=0){
                            rt =(double)count/all_count*100;
                        }
                        String rate=df.format(rt);
                        json.put("rate",rate+"%");
                        json.put("option",options[j]);
                        json.put("num",count);
                        array.add(json);
                    }
                    json1.put("name",name);
                    json1.put("options",array);
                    json1.put("check",check);
                    json1.put("type",qt_type);
                    jsonarray.add(json1);
                }else if(qt_type.equals("title")){
                    jsonarray.add(template_obj);
                }
            }
        } else if(type.equals("single")){
            dbCursor1=MongoUtils.sortAndPage(dbCursor1,page_num,page_size,"modified_date",-1);
            jsonarray=dbCursorToList(dbCursor1);
        }
        JSONObject reslut_obj=new JSONObject();
        reslut_obj.put("list",jsonarray);
        reslut_obj.put("info",JSON.toJSONString(questionnaire));
        reslut_obj.put("page_num",page_num);
        reslut_obj.put("page_size",page_size);
        reslut_obj.put("all_count",all_count);
        return  reslut_obj;
    }


    public  JSONArray dbCursorToList(DBCursor dbCursor) throws Exception{
        JSONArray array=new JSONArray();
        while (dbCursor.hasNext()) {
            DBObject dbObject=dbCursor.next();
            String vip=dbObject.get("vip").toString();
            JSONObject jsonObject=JSON.parseObject(vip);
            String vip_name=jsonObject.get("vip_name")==null?"":jsonObject.getString("vip_name");
            String cardno=jsonObject.get("cardno")==null?"":jsonObject.getString("cardno");
            String vip_phone=jsonObject.get("vip_phone")==null?"":jsonObject.getString("vip_phone");
            String vip_id=jsonObject.getString("vip_id");
            String modified_date=dbObject.get("modified_date")==null?"":dbObject.get("modified_date").toString();
            String task=dbObject.get("task").toString();
            JSONObject task_obj=JSON.parseObject(task);
            String qtNaire_id=task_obj.getString("qtNaire_id");
            //答题数
            String schedule=dbObject.get("schedule").toString();
            JSONArray jsonArray=JSON.parseArray(schedule);
            List<String> list1=new ArrayList<String>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json=jsonArray.getJSONObject(i);
                String value=json.getString("value");
                String type=json.getString("type");
                String name=json.getString("name");
                if(StringUtils.isNotBlank(value)&&!type.equals("title")){
                    list1.add(value);
                }
            }
            int answer_num=list1.size(); //回答数

            JSONObject jsObj=new JSONObject();
            jsObj.put("vip_name",vip_name);
            jsObj.put("cardno",cardno);
            jsObj.put("vip_phone",vip_phone);
            jsObj.put("modified_date",modified_date);
            jsObj.put("answer_num",answer_num);
            jsObj.put("vip_id",vip_id);
            jsObj.put("info",jsonArray);
            jsObj.put("qtNaire_id",qtNaire_id);
            array.add(jsObj);
        }
        return array;
    }


    public JSONObject getQtNaireAnswer(Questionnaire questionnaire,JSONObject jsonObj){
        MongoTemplate mongoTemplate=mongoDBClient.getMongoTemplate();
        DBCollection cursor=mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
        //获取筛选时间
        String screen=jsonObj.getString("screen");
        JSONObject time_obj=JSON.parseObject(screen);
        String start=time_obj.getString("start");
        String end=time_obj.getString("end");
        int page_num=Integer.parseInt(jsonObj.getString("page_num"));
        int page_size=Integer.parseInt(jsonObj.getString("page_size"));
        String name=jsonObj.getString("name");
        String check=jsonObj.getString("check");
        String type=jsonObj.getString("type");
        JSONArray jsonarray=new JSONArray();
        BasicDBList basicDbList1=new BasicDBList();
        BasicDBObject basicObj1=new BasicDBObject();
        basicDbList1.add(new BasicDBObject("task.task_type","questionnaire"));
        basicDbList1.add(new BasicDBObject("corp_code",questionnaire.getCorp_code()));
        basicDbList1.add(new BasicDBObject("task.qtNaire_id",questionnaire.getId()+""));
        basicDbList1.add(new BasicDBObject("status","1"));
        BasicDBObject query=new BasicDBObject();
        query.put("name",name);
        query.put("type",type);
        query.put("check",check);
        basicDbList1.add(new BasicDBObject("schedule",new BasicDBObject("$elemMatch",query)));
        //筛选条件 答题时间
        if(StringUtils.isNotBlank(start)){
            basicDbList1.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.GTE, start+" 00:00:00")));
        }
        if(StringUtils.isNotBlank(end)){
            basicDbList1.add(new BasicDBObject("modified_date", new BasicDBObject(QueryOperators.LTE, end+" 23:59:59")));
        }
        basicObj1.put("$and",basicDbList1);
        DBCursor dbCursor1=cursor.find(basicObj1);
        int all_count=dbCursor1.count();
        dbCursor1=MongoUtils.sortAndPage(dbCursor1,page_num,page_size,"modified_date",-1);
        while (dbCursor1.hasNext()){
            DBObject dbObject=dbCursor1.next();
            String vip=dbObject.get("vip").toString();
            JSONObject jsonObject=JSON.parseObject(vip);
            String vip_name=jsonObject.get("vip_name")==null?"":jsonObject.getString("vip_name");
            String cardno=jsonObject.get("cardno")==null?"":jsonObject.getString("cardno");
            String vip_phone=jsonObject.get("vip_phone")==null?"":jsonObject.getString("vip_phone");
            String modified_date=dbObject.get("modified_date")==null?"":dbObject.get("modified_date").toString();
            String schedule=dbObject.get("schedule").toString();
            JSONArray jsonArray=JSON.parseArray(schedule);
            String value="";
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json=jsonArray.getJSONObject(i);
                String name1=json.getString("name");
                String check1=json.getString("check");
                String type1=json.getString("type");
                if(name1.equals(name)&&check1.equals(check)&&type1.equals(type)){
                    value=json.getString("value");
                    break;
                }
            }
            JSONObject jsObj=new JSONObject();
            jsObj.put("vip_name",vip_name);
            jsObj.put("cardno",cardno);
            jsObj.put("vip_phone",vip_phone);
            jsObj.put("modified_date",modified_date);
            jsObj.put("info",value);
            if(StringUtils.isNotBlank(value.trim())){
                jsonarray.add(jsObj);
            }
        }

        JSONObject result=new JSONObject();
        result.put("list",jsonarray);
        result.put("info",JSON.toJSONString(questionnaire));
        result.put("all_count",all_count);
        result.put("page_num",page_num);
        result.put("page_size",page_size);
        return result;
    }

    public  JSONArray getQtNaireVipAnswer(Questionnaire questionnaire,JSONObject json_obj)throws Exception{
        String vip_id=json_obj.getString("vip_id");
        MongoTemplate mongoTemplate=mongoDBClient.getMongoTemplate();
        DBCollection cursor=mongoTemplate.getCollection(CommonValue.table_vip_task_schedule);
        BasicDBList basicDbList1=new BasicDBList();
        BasicDBObject basicObj1=new BasicDBObject();
        basicDbList1.add(new BasicDBObject("task.task_type","questionnaire"));
        basicDbList1.add(new BasicDBObject("corp_code",questionnaire.getCorp_code()));
        basicDbList1.add(new BasicDBObject("task.qtNaire_id",questionnaire.getId()+""));
        basicDbList1.add(new BasicDBObject("status","1"));
        basicDbList1.add(new BasicDBObject("vip.vip_id",vip_id));
        basicObj1.put("$and",basicDbList1);
        DBObject  dbObj=cursor.findOne(basicObj1);
        String sched=dbObj.get("schedule").toString();
        JSONArray array=JSON.parseArray(sched);
        return  array;
    }

}
