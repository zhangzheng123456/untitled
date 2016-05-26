package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Action;
import com.bizvane.ishop.entity.Function;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
public interface FunctionMapper {

    List<Function> selectAllFunction(@Param("user_id")int user_id,@Param("role_code")String role_code);

    List<Action> selectAllAction(@Param("user_id")int user_id,@Param("role_code")String role_code);

    List<Function> selectAllFun(@Param("user_id")int user_id,@Param("role_code")String role_code);

    List<Action> selectActionByFun(@Param("user_id")int user_id,@Param("role_code")String role_code,@Param("function_code") String function_code);

}
