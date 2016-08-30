package com.bizvane.ishop.service;

import com.bizvane.ishop.dao.CorpMapper;
import com.bizvane.ishop.entity.Corp;
import com.bizvane.ishop.utils.RedisClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhouZhou on 2016/8/30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring.xml",
        "classpath:spring-mybatis.xml"})
public class RedisTest {
    @Autowired
    private CorpMapper corpMapper = null;

    @Autowired
    RedisClient redisClient;

    //成功
    @Test
    public void test() {
        try {
            List<Corp> list = new ArrayList<Corp>();
            if (redisClient.get("CorpList") == null) {
                list = corpMapper.selectCorps("");
                redisClient.set("CorpList", list);
            } else {
                list = (List<Corp>) redisClient.get("CorpList");
            }
            System.out.println("------corp"+list);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
