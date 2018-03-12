package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.MessageQuickReplyMapper;
import com.bizvane.ishop.entity.Brand;
import com.bizvane.ishop.entity.MessageQuickReply;
import com.bizvane.ishop.service.BrandService;
import com.bizvane.ishop.service.MessageQuickReplyService;
import com.bizvane.ishop.utils.CheckUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nanji on 2016/9/10.
 */
@Service
public class MessageQuickReplyServiceImpl implements MessageQuickReplyService {
    @Autowired
    MessageQuickReplyMapper messageQuickReplyMapper;
    @Autowired
    private BrandService brandService;

    @Override
    public MessageQuickReply getQuickReplyById(int id) throws Exception {
        MessageQuickReply messageQuickReply= messageQuickReplyMapper.selectQuickReplyById(id);
        String brand_code=messageQuickReply.getBrand_code();
        String corp_code=messageQuickReply.getCorp_code();
        String brand_name = "";
        if (brand_code != null && !brand_code.equals("")){
        brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
        String[] brandCodes = brand_code.split(",");
        String brandCode = "";
        for (int i = 0; i < brandCodes.length; i++) {
            Brand brand = brandService.getBrandByCode(corp_code, brandCodes[i], Common.IS_ACTIVE_Y);
            if (brand != null) {
                String brand_name1 = brand.getBrand_name();
                brand_name = brand_name + brand_name1;
                brandCode = brandCode + brandCodes[i];
                if (i != brandCodes.length - 1) {
                    brand_name = brand_name + ",";
                    brandCode = brandCode + ",";
                }
            }
        }
            messageQuickReply.setBrand_code(brandCode);
            messageQuickReply.setBrand_name(brand_name);
    }
        return messageQuickReply;
    }

    /**
     * 分页显示消息快捷回复模板
     * @param page_number
     * @param page_size
     * @param corp_code
     * @param search_value
     * @return
     * @throws Exception
     */
    @Override
    public PageInfo<MessageQuickReply> getAllQuickReplyByPage(int page_number, int page_size, String corp_code, String search_value) throws Exception {
        List<MessageQuickReply> messageQuickReplies;
        PageHelper.startPage(page_number, page_size);
        messageQuickReplies = messageQuickReplyMapper.selectAllMessageQuickReply(corp_code, search_value,null);
        for (MessageQuickReply messageQuickReply : messageQuickReplies) {
            messageQuickReply.setIsactive(CheckUtils.CheckIsactive(messageQuickReply.getIsactive()));
        }
        PageInfo<MessageQuickReply> page = new PageInfo<MessageQuickReply>(messageQuickReplies);
        return page;
    }

    @Override
    public PageInfo<MessageQuickReply> getAllQuickReplyByPage(int page_number, int page_size, String corp_code, String search_value,String manager_corp) throws Exception {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        List<MessageQuickReply> messageQuickReplies;
        PageHelper.startPage(page_number, page_size);
        messageQuickReplies = messageQuickReplyMapper.selectAllMessageQuickReply(corp_code, search_value,manager_corp_arr);
        for (MessageQuickReply messageQuickReply : messageQuickReplies) {
            messageQuickReply.setIsactive(CheckUtils.CheckIsactive(messageQuickReply.getIsactive()));
        }
        PageInfo<MessageQuickReply> page = new PageInfo<MessageQuickReply>(messageQuickReplies);
        return page;
    }

    @Override
    public List<MessageQuickReply> getAllQuickReply(String corp_code) throws Exception {
        List<MessageQuickReply> messageQuickReplies;
        messageQuickReplies = messageQuickReplyMapper.selectMessageQuickReplys(corp_code);

        return messageQuickReplies;
    }

    @Override
    public String insert(String message, String user_id) throws Exception {
        String result = Common.DATABEAN_CODE_ERROR;
        JSONObject jsonObject = new JSONObject(message);
        String content = jsonObject.get("content").toString();
        String corp_code = jsonObject.get("corp_code").toString().trim();

        String brand_code = jsonObject.get("brand_codes").toString().trim();
        String[] codes = brand_code.split(",");
        String brand_code1 = "";
        for (int i = 0; i < codes.length; i++) {
            codes[i] = Common.SPECIAL_HEAD + codes[i] + ",";
            brand_code1 = brand_code1 + codes[i];
        }

        MessageQuickReply messageQuickReply1 = getQuickReplyByCode(corp_code, content, Common.IS_ACTIVE_Y);

        if (messageQuickReply1 != null) {
            result = "该消息快捷回复模板已存在";
        } else {
            Date now = new Date();
            MessageQuickReply messageQuickReply = new MessageQuickReply();
            messageQuickReply.setCorp_code(corp_code);
            messageQuickReply.setBrand_code(brand_code1);
            messageQuickReply.setContent(content);
            messageQuickReply.setCreated_date(Common.DATETIME_FORMAT.format(now));
            messageQuickReply.setCreater(user_id);
            messageQuickReply.setModified_date(Common.DATETIME_FORMAT.format(now));
            messageQuickReply.setModifier(user_id);
            messageQuickReply.setIsactive(jsonObject.get("isactive").toString());
            messageQuickReplyMapper.insertMessageQuickReply(messageQuickReply);
            MessageQuickReply messageQuickReply2=this.getQuickReplyByCode(messageQuickReply.getCorp_code(),messageQuickReply.getContent(),messageQuickReply.getIsactive());

            result = String.valueOf(messageQuickReply2.getId());
        }
        return result;
    }

    @Override
    public String update(String message, String user_id) throws Exception {
        String result = "";
        JSONObject jsonObject = new JSONObject(message);
        String quickReply_id = jsonObject.get("id").toString().trim();
        int id = Integer.parseInt(quickReply_id);
        String content = jsonObject.get("content").toString();
        String corp_code = jsonObject.get("corp_code").toString().trim();
        MessageQuickReply messageQuickReply1 = getQuickReplyByCode(corp_code, content, Common.IS_ACTIVE_Y);
        String brand_code = jsonObject.get("brand_codes").toString().trim();
        if (messageQuickReply1 != null && messageQuickReply1.getId() != id) {
            result = "该消息快捷回复模板已存在";
        } else {

            String[] codes = brand_code.split(",");
            String brand_code1 = "";
            for (int i = 0; i < codes.length; i++) {
                codes[i] = Common.SPECIAL_HEAD + codes[i] + ",";
                brand_code1 = brand_code1 + codes[i];
            }
            MessageQuickReply messageQuickReply = new MessageQuickReply();
            Date now = new Date();
            messageQuickReply.setId(id);
            messageQuickReply.setBrand_code(brand_code1);
            messageQuickReply.setCorp_code(corp_code);
            messageQuickReply.setContent(content);
            messageQuickReply.setModified_date(Common.DATETIME_FORMAT.format(now));
            messageQuickReply.setModifier(user_id);
            messageQuickReply.setIsactive(jsonObject.get("isactive").toString());
            messageQuickReplyMapper.updateMessageQuickReply(messageQuickReply);
            result = Common.DATABEAN_CODE_SUCCESS;
        }
        return result;

    }

    @Override
    public int delete(int id) throws Exception {
        return messageQuickReplyMapper.deleteMessageQuickReplyById(id);
    }

    @Override
    public MessageQuickReply getQuickReplyByCode(String corp_code, String code, String isactive) throws Exception {
        return messageQuickReplyMapper.selectByMessageQuickReplyCode(corp_code, code, isactive);
    }

    @Override
    public PageInfo<MessageQuickReply> getAllQuickReplyScreen(int page_number, int page_size, String corp_code,String brand_codes, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        String[] brands = null;
        PageHelper.startPage(page_number, page_size);
        if (!brand_codes.equals("")) {
            brand_codes = brand_codes.replace(Common.SPECIAL_HEAD, "");
            brands = brand_codes.split(",");
            for (int i = 0; i < brands.length; i++) {
                brands[i] = Common.SPECIAL_HEAD + brands[i] + ",";
            }
        }
        int flg = 0;
        for (int i = 0; i < map.size(); i++) {
            if (map.containsKey("brand_name") && !map.get("brand_name").equals("")) {
                flg = 1;
            }
        }
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<MessageQuickReply> list1 = messageQuickReplyMapper.selectQuickReplyScreen(params);
        for (MessageQuickReply messageQuickReply : list1) {
            messageQuickReply.setIsactive(CheckUtils.CheckIsactive(messageQuickReply.getIsactive()));
        }
        PageInfo<MessageQuickReply> page = new PageInfo<MessageQuickReply>(list1);
        return page;
    }

    @Override
    public PageInfo<MessageQuickReply> getAllQuickReplyScreen(int page_number, int page_size, String corp_code,String brand_codes, Map<String, String> map,String manager_corp) throws Exception {
        String[] manager_corp_arr = null;
        if (!manager_corp.equals("")) {
            manager_corp_arr = manager_corp.split(",");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("manager_corp_arr", manager_corp_arr);
        params.put("corp_code", corp_code);
        params.put("map", map);
        PageHelper.startPage(page_number, page_size);
        List<MessageQuickReply> list1 = messageQuickReplyMapper.selectQuickReplyScreen(params);
        for (MessageQuickReply messageQuickReply : list1) {
            messageQuickReply.setIsactive(CheckUtils.CheckIsactive(messageQuickReply.getIsactive()));
        }
        PageInfo<MessageQuickReply> page = new PageInfo<MessageQuickReply>(list1);
        return page;
    }

    @Override
    public List<MessageQuickReply> selectQuickReplyByBrand(String corp_code, String brand_code, String search_value, String isactive) throws Exception {
        String[] brand_codes = brand_code.split(",");
        for (int i = 0; i < brand_codes.length; i++) {
            brand_codes[i] = Common.SPECIAL_HEAD + brand_codes[i] + ",";
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("corp_code", corp_code);
        params.put("array", brand_codes);
        params.put("search_value", search_value);
        params.put("isactive", isactive);
        List<MessageQuickReply> list = messageQuickReplyMapper.selQuickReplyCountByBrand(params);
        return  list;
    }
}
