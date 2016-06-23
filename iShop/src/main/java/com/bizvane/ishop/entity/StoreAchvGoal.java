package com.bizvane.ishop.entity;

import java.util.Date;

/**
 * Created by lixiang on 2016/6/1.
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
    private double achv_goal;
    //业绩类型
    private String achv_type;
    //开始时间
    private String start_time;
    //结束时间
    private String end_time;
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
    //区域名称
    private String area_name;
    private Area area;
    private Corp corp;

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

    public double getAchv_goal() {
        return achv_goal;
    }

    public void setAchv_goal(double achv_goal) {
        this.achv_goal = achv_goal;
    }

    public String getAchv_type() {
        return achv_type;
    }

    public void setAchv_type(String achv_type) {
        this.achv_type = achv_type;
    }


    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
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

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }
}
