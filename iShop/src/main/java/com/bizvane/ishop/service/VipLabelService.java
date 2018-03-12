package com.bizvane.ishop.service;

import com.bizvane.ishop.entity.RelViplabel;
import com.bizvane.ishop.entity.VipLabel;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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
     * @throws Exception
     */
    VipLabel getVipLabelById(int id) throws Exception;

    /**
     * 插入VIP用户的标签信息
     *
     * @param
     * @return
     * @throws Exception
     */
    String insert(VipLabel vipLabel) throws Exception;

    /**
     * 删除VIP用户的标签信息，通过VIP用户的标签编号
     *
     * @param id
     * @return
     */
    int delete(int id) throws Exception;

    /**
     * 更新VIP标签信息
     *
     * @param
     * @return
     */
    String update(VipLabel vipLabel) throws Exception;

    /**
     * 获取用户分页信息
     *
     * @param page_number  ： 起始页码
     * @param page_size    ： 分页大小
     * @param corp_code    ： 公司编号
     * @param search_value ： 搜索字段
     * @return
     */
    PageInfo<VipLabel> selectBySearch(int page_number, int page_size, String corp_code, String search_value)throws Exception;

    PageInfo<VipLabel> selectAllVipScreen(int page_number, int page_size, String corp_code, Map<String,String> map,String[] brand)throws Exception;

    /**
     * 判断VIP标签名称是否公司内唯一
     *
     * @param corp_code ： 公司编号
     * @param tag_name  ： VIP标签名称
     * @return
     */
    List<VipLabel> VipLabelNameExist(String corp_code, String tag_name,String brand_code) throws Exception;


    VipLabel countLable(String corp_code, String label_id)throws Exception;

    List<VipLabel> lableList(String corp_code, String label_group_code) throws Exception;

    List<VipLabel> selectLabelByVip(String corp_code, String vip_code) throws Exception;

    List<VipLabel> findHotViplabel(String corp_code,String brand_list)throws Exception;

    PageInfo<VipLabel> findViplabelByType(int page_number, int page_size,String corp_code,String label_type,String search_value,String brand_code)throws Exception;

    List<RelViplabel> checkRelViplablel(String corp_code,String vip_code,String label_id)throws Exception;

    int addRelViplabel(RelViplabel relViplabel)throws Exception;

    String addRelViplabel(String label_id, String corp_code, String vip_code, String store_code, String user_id) throws Exception;

    int delRelViplabel(String rid)throws Exception;

    int delAllRelViplabel(String label_id) throws Exception;

    List<VipLabel> findViplabelID(String corp_code,String label_name)throws Exception;

    List<VipLabel> selectViplabelByName(String corp_code, String label_name,String isactive) throws Exception;

    List<VipLabel> selectViplabelByName(String corp_code, String vips) throws Exception;

     List<VipLabel> selectLabelByVipToHbase(String corp_code, String vip_code) throws Exception ;

     String addRelViplabelToHbase(String label_id, String corp_code, String vip_code, String store_code, String user_id) throws Exception;

     int checkRelViplablelToHbase(String corp_code, String vip_code, String label_id) throws Exception;
}
