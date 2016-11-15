package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.VIPRelationMapper;
import com.bizvane.ishop.entity.VIPEmpRelation;
import com.bizvane.ishop.entity.VIPStoreRelation;
import com.bizvane.ishop.service.WebService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by zhou on 2016/6/1.
 *
 * @@version
 */

@Service
public class WebServiceImpl implements WebService {


    @Autowired
    VIPRelationMapper vipRelationMapper;


    public List<VIPEmpRelation> selectEmpVip(String app_user_name, String open_id) throws SQLException {
        return vipRelationMapper.selectEmpVip(open_id, app_user_name);
    }

    public List<VIPStoreRelation> selectStoreVip(String app_user_name, String open_id) throws SQLException {
        return vipRelationMapper.selectStoreVip(open_id, app_user_name);
    }
}
