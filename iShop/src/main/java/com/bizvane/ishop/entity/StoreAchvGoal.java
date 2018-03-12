package com.bizvane.ishop.entity;

/**
 * Created by xiaohua on 2016/6/1.
 * 店铺业绩目标
 * @@version
 */
public class StoreAchvGoal {

    private int id;
    //店铺业绩目标编号
    private String store_code;
    //店铺名称
    private String store_name;
    //公司编号
    private String corp_code;
    //业绩目标
    private String target_amount;
    //业绩类型
    private String time_type;
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
    //是否平均分配
    private String isaverage;
    //比例
    private String proportion;
    //周比例对应的业绩目标
    private String targets_arr;

    private Corp corp;
    private  String corp_name;
    private String area_name;

    public String getIsaverage() {
        return isaverage;
    }

    public void setIsaverage(String isaverage) {
        this.isaverage = isaverage;
    }

    public String getProportion() {
        return proportion;
    }

    public void setProportion(String proportion) {
        this.proportion = proportion;
    }

    public String getTargets_arr() {
        return targets_arr;
    }

    public void setTargets_arr(String targets_arr) {
        this.targets_arr = targets_arr;
    }

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public StoreAchvGoal() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStore_code() {
        return store_code;
    }

    public void setStore_code(String store_code) {
        this.store_code = store_code;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getTarget_amount() {
        return target_amount;
    }

    public void setTarget_amount(String target_amount) {
        this.target_amount = target_amount;
    }

    public String getTime_type() {
        return time_type;
    }

    public void setTime_type(String time_type) {
        this.time_type = time_type;
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


    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }
}
