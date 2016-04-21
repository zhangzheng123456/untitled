package com.bizvane.ishop.bean;

import java.util.Date;

public class User {
	//编号
	private int id;

	//账号
	private String user_code;
	//姓名
	private String name;
	//性别
	private String sex;
	//头像
	private String photo;
	//电话
	private String tel;
	//邮箱
	private String email;
	//所属企业
	private String company;
	//所属角色
	private String role;
	//最后登录时间
	private Date last_login;
	//创建人
	private String creater;
	//创建时间
	private Date created_time;
	//修改人
	private String modifier;
	//修改时间
	private Date modified_time;
	//是否可用
	private boolean enable;

	public User() {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}


	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Date getLast_login() {
		return last_login;
	}

	public void setLast_login(Date last_login) {
		this.last_login = last_login;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public Date getCreated_time() {
		return created_time;
	}

	public void setCreated_time(Date created_time) {
		this.created_time = created_time;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public Date getModified_time() {
		return modified_time;
	}

	public void setModified_time(Date modified_time) {
		this.modified_time = modified_time;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public User(int id, String user_code, String name, String sex, String photo, String tel, String email, String company, String role, Date last_login, String creater, Date created_time, String modifier, Date modified_time, boolean enable) {
		this.id = id;
		this.user_code = user_code;
		this.name = name;
		this.sex = sex;
		this.photo = photo;
		this.tel = tel;
		this.email = email;
		this.company = company;
		this.role = role;
		this.last_login = last_login;
		this.creater = creater;
		this.created_time = created_time;
		this.modifier = modifier;
		this.modified_time = modified_time;
		this.enable = enable;
	}
}
