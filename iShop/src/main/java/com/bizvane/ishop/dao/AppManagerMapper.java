package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.AppManager;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by PC on 2017/2/9.
 */
public interface AppManagerMapper {
    List<AppManager> getFunctionList(@Param("corp_az") String corp_az) throws Exception;

    List<AppManager> getActionList(@Param("app_function")String app_function,@Param("corp_az") String corp_az, @Param("corp_jnby") String corp_jnby) throws Exception;
}
