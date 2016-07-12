package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.VipLabel;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;

/**
 * Created by lixiang on 2016/6/13.
 *
 * @@version
 */
public interface VipLabelService {

    /**
     * 获取VIP标签信息：通过变迁的ID
     *
     * @param id
     * @return
     * @throws SQLException
     */
    VipLabel getVipLabelById(int id) throws SQLException;

    /**
     * 插入VIP用户的标签信息
     *
     * @param
     * @return
     * @throws SQLException
     */
    String insert(VipLabel vipLabel) throws SQLException;

    /**
     * 删除VIP用户的标签信息，通过VIP用户的标签编号
     *
     * @param id
     * @return
     * @throws SQLException
     */
    int delete(int id) throws SQLException;

    /**
     * 更新VIP标签信息
     *
     * @param
     * @return
     */
    String update(VipLabel vipLabel) throws SQLException;

    /**
     * 获取用户分页信息
     *
     * @param page_number  ： 起始页码
     * @param page_size    ： 分页大小
     * @param corp_code    ： 公司编号
     * @param search_value ： 搜索字段
     * @return
     */
    PageInfo<VipLabel> selectBySearch(int page_number, int page_size, String corp_code, String search_value);

    /**
     * 判断VIP标签名称是否公司内唯一
     *
     * @param corp_code ： 公司编号
     * @param tag_name  ： VIP标签名称
     * @return
     * @throws SQLException
     */
    String VipLabelNameExist(String corp_code, String tag_name) throws SQLException;


}
