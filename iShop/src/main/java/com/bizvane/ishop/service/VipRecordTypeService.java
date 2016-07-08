package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VipRecordType;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/21.
 *
 * @@version
 */
public interface VipRecordTypeService {

    /**
     * 获取VIP标签类型信息
     *
     * @param id
     * @return
     * @throws SQLException
     */
    VipRecordType getVipRecordTypeById(int id) throws SQLException;

    VipRecordType getVipRecordTypeByName(String corp_code, String type_name) throws SQLException;

    /**
     * 插入VIP标签类型信息
     *
     * @param VipRecordType
     * @return
     * @throws SQLException
     */
    int insert(VipRecordType VipRecordType) throws SQLException;

    /**
     * 更新VIP标签类型信息
     *
     * @param VipRecordType
     * @return
     * @throws SQLException
     */
    int update(VipRecordType VipRecordType) throws SQLException;

    /**
     * 获取符合查询条件的分页信息
     *
     * @param page_number  ： 起始页码
     * @param page_size    ： 分页大小
     * @param corp_code    ： 公司编号
     * @param search_value ： 查询条件
     * @return
     */
    PageInfo<VipRecordType> selectBySearch(int page_number, int page_size, String corp_code, String search_value);

    /**
     * 判断VIP标签类型编号是否在公司内唯一
     *
     * @param type_code ： 类型编号
     * @param corp_code ： 公司编号
     * @return
     */
    //String VipRecordTypeCodeExist(String type_code, String corp_code);

    /**
     * 判断VIP标签类型名称是否在公司内唯一
     *
     * @param type_name ： 类型名称
     * @param corp_code ： 公司编号
     * @return
     */
    String VipRecordTypeNameExist(String type_name, String corp_code);

    /**
     * 删除VIPb编号类型，通过ID
     *
     * @param id
     * @return
     */
    int deleteById(int id);

    //List<VipRecordType> getAllVipRecordType(String corp_code, String search_value);
}
