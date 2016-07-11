package com.bizvane.ishop.entity;

/**
<<<<<<< HEAD
 * Created by nanji on 2016/7/8.
 */
public class TemplateType {
    private int id;


    //类型名称
    private String type_name;
    //类型描述
    private String type_description;
    //修改时间
    private String modified_date;
    //修改人
    private String modifier;
    //创建人
    private String creater;
    //创建时间
    private String created_date;
    //是否可用
    private String isactive;
    //企业编号
=======
 * Created by lixiang on 2016/7/10.
 *
 * @@version
 */
public class TemplateType {
    private int id;
    private String type_name;
    private String modified_date;
    private String modifier;
    private String isactive;
    private String creater;
    private String created_date;
>>>>>>> ab1c752ecb366f25799b922460ca904d34932fcd
    private String corp_code;

    public TemplateType() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

<<<<<<< HEAD

=======
>>>>>>> ab1c752ecb366f25799b922460ca904d34932fcd
    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

<<<<<<< HEAD
    public String getType_description() {
        return type_description;
    }

    public void setType_description(String type_description) {
        this.type_description = type_description;
    }

=======
>>>>>>> ab1c752ecb366f25799b922460ca904d34932fcd
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

<<<<<<< HEAD
=======
    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

>>>>>>> ab1c752ecb366f25799b922460ca904d34932fcd
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

<<<<<<< HEAD
    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

=======
>>>>>>> ab1c752ecb366f25799b922460ca904d34932fcd
    public String getCorp_code() {
        return corp_code;
    }

    public void setCorp_code(String corp_code) {
        this.corp_code = corp_code;
    }
}
