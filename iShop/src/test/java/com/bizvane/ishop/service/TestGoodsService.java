package com.bizvane.ishop.service;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Goods;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by lixiang on 2016/5/30.
 *
 * @@version
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml",
        "classpath:spring-mybatis.xml"})
public class TestGoodsService {
    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(TestGoodsService.class);

    private DateFormat sdf = new SimpleDateFormat(Common.DATE_FORMATE);

    @Autowired
    private GoodsService logService;

    @Test
    public void testDelete() {
        try {
            this.logService.delete(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(1111111);
    }

    @Test
    public void testQuery() throws SQLException {
        Goods goods = this.logService.getGoodsById(1);
        System.out.println(goods.getCreater());

    }

    @Test
    public void testInsert() throws SQLException {
        Goods goods = new Goods();

        goods.setGoods_code("12334");
        goods.setGoods_name("kong");
        goods.setGoods_image("kong");
        goods.setGoods_price(12.323f);
        goods.setGoods_time("kong");
        goods.setGoods_quarter("kong");
        goods.setGoods_wave("zhi");
        Date now = new Date();
        goods.setModified_date(sdf.format(now));

        goods.setCreated_date(sdf.format(now));
        goods.setCreater("person");
        goods.setIsactive("123");
        logService.insert(goods);
    }

    @Test
    public void testUpdate() throws SQLException {
        Goods goods = new Goods();
        Date now = new Date();
        goods.setId(1);
        goods.setGoods_code("12334");
        goods.setGoods_name("kong");
        goods.setGoods_image("kong");
        goods.setGoods_price(12.323f);
        goods.setGoods_time("kong");
        goods.setGoods_quarter("kong");
        goods.setGoods_wave("zhi");
        goods.setModified_date(sdf.format(now));

        goods.setCreated_date(sdf.format(now));
        goods.setCreater("personperson");
        goods.setIsactive("123");

        this.logService.update(goods);

    }


}
