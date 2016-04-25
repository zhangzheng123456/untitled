package com.bizvane.ishop.bean;

/**
 * Created by maoweidong on 2016/2/24.
 */
public class VIP {
    private Integer id;

    private String cname;

    private String caddress;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname == null ? null : cname.trim();
    }

    public String getCaddress() {
        return caddress;
    }

    public void setCaddress(String caddress) {
        this.caddress = caddress == null ? null : caddress.trim();
    }

}
