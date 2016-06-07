package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.Action;
import com.bizvane.ishop.entity.Function;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2016/5/24.
 */
public interface FunctionMapper {

    //获取user所有功能模块
    List<Function> selectAllFun(@Param("user_id") int user_id, @Param("role_code") String role_code, @Param("group_code") String group_code);
    //按功能获取user动作权限
    List<Action> selectActionByFun(@Param("user_id") int user_id, @Param("role_code") String role_code, @Param("function_code") String function_code, @Param("group_code") String group_code);

    //系统管理员获取所有模块功能
    List<Function> selectAll();
    //系统管理员获取所有功能操作
    List<Action> selectAllAction(String function_code);

    List<Function> selectPrivilege(@Param("user_id") int user_id, @Param("role_code") String role_code, @Param("group_code") String group_code);

    List<Function> selectAllPrivilege();

}
