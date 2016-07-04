package com.bizvane.ishop.entity;

/**
 * Created by lixiang on 2016/6/1.
 *
 * @@version
 */
public class UserAchvGoal {

    private int id;
    //用户编号
    private String user_code;
    //店仓编号
    private String store_code;
    //企业编号
    private String corp_code;
    //业绩目标
    private String user_target;
    //业绩类型
    private String target_type;
    //结束时间
    private String target_time;
    //修改时间
    private String modified_date;
    //修改人
    private String modifier;
    //创建时间
    private String created_date;
    //创建人
    private String creater;
    //是否可用
    private String isactive;

    private User user;
    private Store store;
    private Corp corp;


    public UserAchvGoal() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getStore_code() {
        return store_code;
    }

    public void setStore_code(String store_code) {
        this.store_code = store_code;
    }

    public String getUser_target() {
        return user_target;
    }

    public void setUser_target(String user_target) {
        this.user_target = user_target;
    }

    public String getTarget_type() {
        return target_type;
    }

    public void setTarget_type(String target_type) {
        this.target_type = target_type;
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

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public String getTarget_time() {
        return target_time;
    }

    public void setTarget_time(String target_time) {
        this.target_time = target_time;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
