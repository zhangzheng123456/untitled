package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Questionnaire;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by yanyadong on 2017/8/29.
 */
public interface QuestionnaireMapper {

    Questionnaire selectById(int id) throws Exception;

    Questionnaire selectByQtnaireName(@Param("corp_code")String corp_code, @Param("title")String title) throws Exception;

    List<Questionnaire> selectAll(@Param("corp_code")String corp_code,@Param("search_value") String search_value)throws Exception;

    int deleteById(int id) throws Exception;

    int insertQtnaire(Questionnaire questionnaire) throws Exception;

    int updateQtnaire(Questionnaire questionnaire) throws  Exception;

    List<Questionnaire> selectAllScreen(Map<String, Object> params) throws Exception;

    List<Questionnaire> selectAllQtNaire(@Param("corp_code")String corp_code) throws Exception;
}
