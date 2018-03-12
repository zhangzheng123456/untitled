package com.bizvane.ishop.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bizvane.ishop.entity.Questionnaire;
import com.github.pagehelper.PageInfo;
import com.mongodb.DBCursor;

import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2017/8/29.
 */
public interface QuestionnaireService {

    Questionnaire selectById(int id) throws Exception;

    Questionnaire selectByQtnaireName(String corp_code,String title) throws Exception;

    PageInfo<Questionnaire> selectAll(int page_num, int page_size, String corp_code, String search_value)throws Exception;

    int deleteById(int id) throws Exception;

    int insertQtnaire(Questionnaire questionnaire) throws Exception;

    int updateQtnaire(Questionnaire questionnaire) throws  Exception;

    List<Questionnaire> selectAllScreen(String corp_code,Map<String, String> params) throws Exception;

    List<Questionnaire> selectAllQtNaire(String corp_code) throws Exception;

    Object switchQtNaire(Object list) throws Exception;

    List<String> getIdsFromVipTask(String corp_code,String task_type) throws Exception;

    public Object getQtNaireNum(Object list,JSONObject jsonObject) throws Exception;

    public JSONObject getQtNaireInfo(Questionnaire questionnaire,JSONObject jsonObj) throws Exception;

    public JSONArray dbCursorToList(DBCursor dbCursor) throws Exception;

    public JSONObject getQtNaireAnswer(Questionnaire questionnaire,JSONObject jsonObj);

    public  JSONArray getQtNaireVipAnswer(Questionnaire questionnaire,JSONObject json_obj)throws Exception;
}
