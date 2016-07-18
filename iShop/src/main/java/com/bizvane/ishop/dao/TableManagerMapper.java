package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.TableManager;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by yin on 2016/6/30.
 */
public interface TableManagerMapper {
    List<TableManager> selAllByCode(@Param("function_code")String function_code);

    List<TableManager> selByCode(@Param("function_code")String function_code);

}
