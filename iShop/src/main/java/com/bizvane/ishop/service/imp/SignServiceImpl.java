package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.dao.SignMapper;
import com.bizvane.ishop.entity.Interfacers;
import com.bizvane.ishop.entity.Sign;
import com.bizvane.ishop.entity.Store;
import com.bizvane.ishop.entity.User;
import com.bizvane.ishop.service.SignService;
import com.bizvane.ishop.service.StoreService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yin on 2016/6/23.
 */
@Service
public class SignServiceImpl implements SignService {
    @Autowired
    private SignMapper signMapper;
    @Autowired
    StoreService storeService;

    @Override
    public PageInfo<Sign> selectSignAll(int page_number, int page_size, String corp_code, String search_value) throws SQLException {
        PageHelper.startPage(page_number, page_size);
        List<Sign> signs = signMapper.selectSignAll(corp_code, search_value);
        PageInfo<Sign> page = new PageInfo<Sign>(signs);
        return page;
    }

    @Override
    public PageInfo<Sign> selectSignByInp(int page_number, int page_size, String corp_code, String search_value, String store_code, String area_code, String role_code) throws SQLException {
        String[] stores = null;
        if (!store_code.equals("")) {
            stores = store_code.split(",");
            for (int i = 0; i < stores.length; i++) {
                stores[i] = stores[i].substring(1, stores[i].length());
            }
        }
        if (!area_code.equals("")) {
            String[] areas = area_code.split(",");
            for (int i = 0; i < areas.length; i++) {
                areas[i] = areas[i].substring(1, areas[i].length());
            }
            List<Store> store = storeService.selectByAreaCode(corp_code, areas, "");
            String a = "";
            for (int i = 0; i < store.size(); i++) {
                a = a + store.get(i).getStore_code() + ",";
            }
            stores = a.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("array", stores);
        params.put("search_value", search_value);
        params.put("role_code", role_code);
        params.put("corp_code", corp_code);
        List<Sign> signs;
        PageHelper.startPage(page_number, page_size);
        signs = signMapper.selectSignByInp(params);
        PageInfo<Sign> page = new PageInfo<Sign>(signs);
        return page;
    }

    @Override
    public PageInfo<Sign> selectByUser(int page_number, int page_size, String corp_code, String user_code, String search_value) throws SQLException {
        PageHelper.startPage(page_number, page_size);
        List<Sign> signs = signMapper.selectByUser(corp_code, user_code, search_value);
        PageInfo<Sign> page = new PageInfo<Sign>(signs);
        return page;
    }

    @Override
    public int delSignById(int id) {
        return signMapper.delSignById(id);
    }

    @Override
    public PageInfo<Sign> selectSignAllScreen(int page_number, int page_size, String corp_code, String area_code, String store_code, String role_code, String user_code, Map<String, String> map) {
        Map<String, Object> params = new HashMap<String, Object>();
        String[] stores = null;
        if (!store_code.equals("")) {
            stores = store_code.split(",");
            for (int i = 0; null != stores && i < stores.length; i++) {
                stores[i] = stores[i].substring(1, stores[i].length());
            }
        }
        if (!area_code.equals("")) {
            String[] areas = area_code.split(",");
            for (int i = 0; null != stores && i < stores.length; i++) {
                areas[i] = areas[i].substring(1, areas[i].length());
            }
            List<Store> stores1 = storeService.selectByAreaCode(corp_code, areas, "");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < stores1.size(); i++) {
                sb.append(stores1.get(i).getStore_code()).append(",");
            }
            stores = sb.toString().split(",");
        }
        params.put("array", stores);
        params.put("corp_code", corp_code);
        params.put("store_code", store_code);
        params.put("user_code", user_code);
        params.put("role_code", role_code);
        params.put("map", map);
        List<Sign> signs;
        PageHelper.startPage(page_number, page_size);
        signs = signMapper.selectSignAllScreen(params);
        PageInfo<Sign> page = new PageInfo<Sign>(signs);
        return page;
    }
}
