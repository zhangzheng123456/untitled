package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.Goods;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
    Goods getGoodsById(int id) throws Exception;

    /**
     * 插入企业
     * @param goods
     * @return
     * @throws SQLException
     */
    int insert(Goods goods) throws Exception;

    int updateExecl(Goods goods) throws Exception;

    int insertGoods(Goods goods,String match_goods) throws Exception;
    /**
     * 更新企业信息，通过编号区分
     * @param goods
     * @return
     * @throws SQLException
     */
    String update(Goods goods) throws Exception;

    /**
     * 删除企业，通过编号区分
     * @param id
     * @return
     * @throws SQLException
     */
    int delete(int id) throws Exception;

    /**
     * 获取分页查询信息
     * @param page_number ：起始页码
     * @param page_size ：页数
     * @param corp_code ：企业编号
     * @param search_value ：搜索信息
     * @return
     */
    PageInfo<Goods> selectBySearch(int page_number, int page_size, String corp_code, String search_value, String[] brand_code)throws Exception;

    PageInfo<Goods> selectBySearch(int page_number, int page_size, String corp_code, String search_value, String[] brand_code,String manager_corp)throws Exception;


    PageInfo<Goods> selectAllGoodsByBrand(int page_number, int page_size, String corp_code, String search_value, String[] brand_code) throws Exception;

    PageInfo<Goods> selectBySearchForApp(int page_number, int page_size, String corp_code,String goods_quarter,String goods_wave,
                                         String brand_code, String time_start,String time_end,String search_value) throws Exception;

    PageInfo<Goods> matchGoodsList(int page_number, int page_size,String corp_code, String search_value,String goods_code,String brand_code) throws Exception;

    PageInfo<Goods> selectAllGoodsScreen(int page_number, int page_size, String corp_code, Map<String,String> map,String[] brand_code)throws Exception;

    PageInfo<Goods> selectAllGoodsScreen(int page_number, int page_size, String corp_code, Map<String,String> map,String[] brand_code,String manager_corp)throws Exception;

    /**
     * 通过商品编号，获取商品信息
     * @param corp_code
     * @param goods_code
     * @return
     */
    Goods getGoodsByCode(String corp_code, String goods_code,String isactive)throws Exception;

    /**
     * 判断商品名称是否在企业内唯一
     * @param corp_code
     * @param goods_name
     * @return
     */
    Goods goodsNameExist(String corp_code, String goods_name,String isactive)throws Exception;

    List<Goods> selectCorpGoodsQuarter(String corp_code) throws Exception;

    List<Goods> selectCorpGoodsWave(String corp_code) throws Exception;

    List<Goods> selectCorpPublicImgs(String corp_code, String brand_code, String search_value) throws Exception;

    PageInfo<Goods> getMatchFab(int page_number, int page_size,String corp_code,String search_value) throws SQLException;

}
