package com.bizvane.ishop.entity;

/**
 * Created by Administrator on 2016/5/19.
 */
public class Message {
    private int id;

    //消息标题
    private String message_title;

    //消息发送方
    private String message_sender;

    //消息接收人类型
    private String receiver_type;

    //消息接收方
    private String message_receiver;

    //消息状态
    private String status;

    //消息内容
    private String message_content;

    //消息类型
    private String message_type;

    //企业编号
    private String corp_code;

    //修改日期
    private String modified_date;

    //修改人
    private String modifier;

    //创建人
    private String creater;

    //创建日期
    private String created_date;

    //是否可用
    private String isactive;

    private Corp corp;

    private MessageType messageType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage_title() {
        return message_title;
    }

    public void setMessage_title(String message_title) {
        this.message_title = message_title;
    }

    public String getMessage_sender() {
        return message_sender;
    }

    public void setMessage_sender(String message_sender) {
        this.message_sender = message_sender;
    }

    public String getReceiver_type() {
        return receiver_type;
    }

    public void setReceiver_type(String receiver_type) {
        this.receiver_type = receiver_type;
    }

    public String getMessage_receiver() {
        return message_receiver;
    }

    public void setMessage_receiver(String message_receiver) {
        this.message_receiver = message_receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage_content() {
        return message_content;
    }

    public void setMessage_content(String message_content) {
        this.message_content = message_content;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
