package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VIPInfo;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;

/**
 * Created by lixiang on 2016/6/12.
 *
 * @@version
 */
public interface VipService {

    /**
     * 获取VIP用户信息
     *
     * @param id
     * @return
     * @throws SQLException
     */
    VIPInfo getVipInfoById(int id) throws SQLException;

    /**
     * 插入VIP用户信息
     *
     * @param vipInfo
     * @return
     * @throws SQLException
     */
    int insert(VIPInfo vipInfo) throws SQLException;

    /**
     * 更新VIP用户信息
     *
     * @param vipInfo
     * @return
     * @throws SQLException
     */
    String update(VIPInfo vipInfo) throws SQLException;

    /**
     * 删除VIP用户信息，通过ID
     *
     * @param id
     * @return
     * @throws SQLException
     */
    int delete(int id) throws SQLException;

    /**
     * 获取分页信息
     *
     * @param page_number  ： 起始分页
     * @param page_size    　：　分页大小
     * @param corp_code    ： 公司编号
     * @param search_value ： 查询字段
     * @return
     */
    PageInfo<VIPInfo> selectBySearch(int page_number, int page_size, String corp_code, String search_value);

    /**
     * 判断VIP用户编号是否存在
     *
     * @param vip_code  　：　vip 编号
     * @param corp_code ： 企业编号
     * @return
     * @throws SQLException
     */
    String vipCodeExist(String vip_code, String corp_code) throws SQLException;


    /**
     * 判断VIP用户名称是否存在
     *
     * @param vip_name  ： VIP 用户的标签名称
     * @param corp_code ： 公司编号
     * @return
     * @throws SQLException
     */
    String vipNameExist(String vip_name, String corp_code) throws SQLException;
}
