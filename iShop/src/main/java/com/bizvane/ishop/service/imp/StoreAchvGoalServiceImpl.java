package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.StoreAchvGoalMapper;
import com.bizvane.ishop.entity.StoreAchvGoal;
import com.bizvane.ishop.service.StoreAchvGoalService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
@Service
public class StoreAchvGoalServiceImpl implements StoreAchvGoalService {

    @Autowired
    StoreAchvGoalMapper storeAchvGoalMapper = null;
    private static final Logger log = Logger.getLogger(StoreServiceImpl.class);
    String[] arg = new String[]{""};


    @Override
    public int update(StoreAchvGoal storeAchvGoal) {


        return storeAchvGoalMapper.update(storeAchvGoal);
    }

    @Override
    public PageInfo<StoreAchvGoal> selectBySearch(int page_number, int page_size, String corp_code,String area_code,String store_code, String search_value)
            throws SQLException {
        String area_codes = "";
        String store_codes = "";

        if (!area_code.equals("")){
            String[] areaCodes = area_code.split(",");
            for (int i = 0; i < areaCodes.length; i++) {
                areaCodes[i] = areaCodes[i].substring(1, areaCodes[i].length());
                System.out.println(areaCodes[i] + "-----");
                area_codes = area_codes + areaCodes[i];
                if (i != areaCodes.length - 1) {
                    area_codes = area_codes + ",";
                }
            }
        }
        if(!store_code.equals("")){
            String[] ids = store_code.split(",");
            for (int i = 0; i < ids.length; i++) {
                ids[i] = ids[i].substring(1, ids[i].length());
                store_codes = store_codes + ids[i];
                if (i != ids.length - 1) {
                    store_codes = store_codes + ",";
                }
            }
        }
        PageHelper.startPage(page_number, page_size);
        List<StoreAchvGoal> storeAchvGoals;
        storeAchvGoals = storeAchvGoalMapper.selectBySearch(corp_code, area_codes,store_codes,search_value);
        PageInfo<StoreAchvGoal> page = new PageInfo<StoreAchvGoal>(storeAchvGoals);
        return page;
    }

    @Override
    public String storeAchvExist(String corp_code, String store_code) {
        //this.storeAchvGoalMapper.selectById(1);
        try {
            if (null != this.storeAchvGoalMapper.selectByCorpAndUserCode(corp_code, store_code)) {
                return Common.DATABEAN_CODE_SUCCESS;
            }
        } catch (Exception ex) {
            return Common.DATABEAN_CODE_ERROR;
        }
        return Common.DATABEAN_CODE_ERROR;
    }

    @Override
    public int deleteById(int id) {
        return this.storeAchvGoalMapper.deleteById(id);
    }

    @Override
    public int insert(StoreAchvGoal storeAchvGoal) {
        return this.storeAchvGoalMapper.insert(storeAchvGoal);
    }

    @Override
    public StoreAchvGoal selectlById(int id) {
        return this.storeAchvGoalMapper.selectById(id);
    }

    @Override
    public List<StoreAchvGoal> selectUsersBySearch(String corp_code, String search_value) {
        return null;
    }
}
