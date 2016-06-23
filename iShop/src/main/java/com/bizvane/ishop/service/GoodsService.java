package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Goods;
import com.github.pagehelper.PageInfo;

import java.sql.SQLException;

/**
 * Created by nanji on 2016/5/30.
 */
public interface GoodsService {
    /**
     * 获取企业通过企业编号
     * @param id
     * @return
     * @throws SQLException
     */
    Goods getGoodsById(int id) throws SQLException;

    /**
     * 插入企业
     * @param goods
     * @return
     * @throws SQLException
     */
    int insert(Goods goods) throws SQLException;

    /**
     * 更新企业信息，通过编号区分
     * @param goods
     * @return
     * @throws SQLException
     */
    int update(Goods goods) throws SQLException;

    /**
     * 删除企业，通过编号区分
     * @param id
     * @return
     * @throws SQLException
     */
    int delete(int id) throws SQLException;

    /**
     * 获取分页查询信息
     * @param page_number ：起始页码
     * @param page_size ：页数
     * @param corp_code ：企业编号
     * @param search_value ：搜索信息
     * @return
     */
    PageInfo<Goods> selectBySearch(int page_number, int page_size, String corp_code, String search_value);

    /**
     * 通过商品编号，获取商品信息
     * @param corp_code
     * @param goods_code
     * @return
     */
    Goods getGoodsByCode(String corp_code, String goods_code);

    /**
     * 判断企业编号信息是否在企业内唯一
     * @param corp_code
     * @param goods_code
     * @return
     */
    String goodsCodeExist(String corp_code, String goods_code);

    /**
     * 判断商品名称是否在企业内唯一
     * @param corp_code
     * @param goods_name
     * @return
     */
    String goodsNameExist(String corp_code, String goods_name);
}
