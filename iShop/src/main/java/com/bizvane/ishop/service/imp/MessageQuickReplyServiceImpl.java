package com.bizvane.ishop.service.imp;

import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.dao.MessageQuickReplyMapper;
import com.bizvane.ishop.entity.MessageQuickReply;
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
    @Override
    public MessageQuickReply getQuickReplyById(int id) throws Exception {
        return messageQuickReplyMapper.selectQuickReplyById(id);
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
        messageQuickReplies = messageQuickReplyMapper.selectAllMessageQuickReply(corp_code, search_value);
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
        MessageQuickReply messageQuickReply1 = getQuickReplyByCode(corp_code, content, Common.IS_ACTIVE_Y);

        if (messageQuickReply1 != null) {
            result = "该消息快捷回复模板已存在";
        } else {
            Date now = new Date();
            MessageQuickReply messageQuickReply = new MessageQuickReply();
            messageQuickReply.setCorp_code(corp_code);
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

        if (messageQuickReply1 != null && messageQuickReply1.getId() != id) {
            result = "该消息快捷回复模板已存在";
        } else {
            MessageQuickReply messageQuickReply = new MessageQuickReply();
            Date now = new Date();
            messageQuickReply.setId(id);
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
    public PageInfo<MessageQuickReply> getAllQuickReplyScreen(int page_number, int page_size, String corp_code, Map<String, String> map) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
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
}
