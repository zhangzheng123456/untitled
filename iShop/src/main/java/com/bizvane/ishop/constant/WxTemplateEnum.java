package com.bizvane.ishop.constant;

/**
 * Created by yanyadong on 2017/6/26.
 */
public enum  WxTemplateEnum {

    TEMPLATE_NAME_1("服务状态提醒"),
    TEMPLATE_NAME_2("邀请注册成功通知"),
    TEMPLATE_NAME_3("任务处理通知"),
    TEMPLATE_NAME_4("积分到期提醒"),
    TEMPLATE_NAME_5("会员等级变更提醒");

    private WxTemplateEnum(String templateName){
        this.templateName=templateName;
    }
    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    private  String templateName;

    public String toStringV2() {
        String name="";
        for (WxTemplateEnum wxTemplateEnum : WxTemplateEnum.values()) {
          name+=wxTemplateEnum.getTemplateName()+",";
        }
        return name;
    }
}
