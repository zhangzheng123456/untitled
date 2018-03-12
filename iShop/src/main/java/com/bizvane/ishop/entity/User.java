package com.bizvane.ishop.entity;

import java.util.List;

public class User {
    private String id;
    //用户编号
    private String user_code;
    //头像
    private String avatar;
    //姓名
    private String user_name;
    //性别
    private String sex;
    //手机号
    private String phone;
    //邮箱
    private String email;
    //密码
    private String password;
    //生日
    private String birthday;
    //企业编号
    private String corp_code;
    //店铺编号
    private String store_code;
    //群组编号
    private String group_code;
    //上次登录时间
    private String login_time_recently;
    //修改时间
    private String modified_date;
    //修改人
    private String modifier;
    //创建时间
    private String created_date;
    //创建者
    private String creater;
    //是否可用
    private String isactive;
    //是否可登录
    private String can_login;
    //区域编号
    private String area_code;
    //职位
    private String position;
    //是否在线
    private String isonline;
    //品牌编号
    private String brand_code;

    private String area_name;

    private String brand_name;

    private String store_name;

    private String role_code;

    private String version_id;

    private String isshow;
    private Group group;
    private String corp_name;
    private String group_name;
    private Corp corp;
    private Sign sign;
    private String user_id;
    private String qrcode;
    private String user_back;
    //集团管理员选企业
    private String manager_corp;

    private String manager_corp_name;
    //城市经理选城市
    private String city;

    private  String master_user_code; //master_code
    private  String master_group_code;//master_code


    public String getMaster_user_code() {
        return master_user_code;
    }

    public void setMaster_user_code(String master_user_code) {
        this.master_user_code = master_user_code;
    }

    public String getMaster_group_code() {
        return master_group_code;
    }

    public void setMaster_group_code(String master_group_code) {
        this.master_group_code = master_group_code;
    }

    public String getManager_corp_name() {
        return manager_corp_name;
    }

    public void setManager_corp_name(String manager_corp_name) {
        this.manager_corp_name = manager_corp_name;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getManager_corp() {
        return manager_corp;
    }

    public void setManager_corp(String manager_corp) {
        this.manager_corp = manager_corp;
    }

    public String getUser_back() {
        return user_back;
    }

    public void setUser_back(String user_back) {
        this.user_back = user_back;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    private List<UserQrcode> qrcodeList;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public User() {
    }

    public String getCorp_name() {
        return corp_name;
    }

    public void setCorp_name(String corp_name) {
        this.corp_name = corp_name;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
    }

    public User(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }

    public String getStore_code() {
        return store_code;
    }

    public void setStore_code(String store_code) {
        this.store_code = store_code;
    }

    public String getGroup_code() {
        return group_code;
    }

    public void setGroup_code(String group_code) {
        this.group_code = group_code;
    }

    public String getLogin_time_recently() {
        return login_time_recently;
    }

    public void setLogin_time_recently(String login_time_recently) {
        this.login_time_recently = login_time_recently;
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

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
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

    public String getCan_login() {
        return can_login;
    }

    public void setCan_login(String can_login) {
        this.can_login = can_login;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getIsonline() {
        return isonline;
    }

    public void setIsonline(String isonline) {
        this.isonline = isonline;
    }

    public String getBrand_code() {
        return brand_code;
    }

    public void setBrand_code(String brand_code) {
        this.brand_code = brand_code;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getRole_code() {
        return role_code;
    }

    public void setRole_code(String role_code) {
        this.role_code = role_code;
    }

    public String getVersion_id() {
        return version_id;
    }

    public void setVersion_id(String version_id) {
        this.version_id = version_id;
    }

    public String getIsshow() {
        return isshow;
    }

    public void setIsshow(String isshow) {
        this.isshow = isshow;
    }

    public Corp getCorp() {
        return corp;
    }

    public void setCorp(Corp corp) {
        this.corp = corp;
    }

    public List<UserQrcode> getQrcodeList() {
        return qrcodeList;
    }

    public void setQrcodeList(List<UserQrcode> qrcodeList) {
        this.qrcodeList = qrcodeList;
    }
}