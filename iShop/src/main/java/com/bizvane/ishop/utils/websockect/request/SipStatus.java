package com.bizvane.ishop.utils.websockect.request;

import java.util.HashMap;
import java.util.Map;

/**
 * Desc:
 * <p/>
 */
public enum SipStatus
{
    ERROR( "error", 							"0000"),//��������ʧ��
    SUCCESS( "success", 						"9999"),//��������ɹ�
    SIGNATURE_INVALID( "signatureInvalid", 		"1001"),//ǩ����Ч
    REQ_TIMEOUT( "reqTimeout", 					"1002"),//�������
    NEED_APPKEY( "needAppKey", 					"1003"),//��Ҫ�ṩAppKey
    NEED_APINAME( "needApiName", 				"1004"),//��Ҫ�ṩ������
    NEED_SIGN( "needSign", 						"1005"),//��Ҫ�ṩǩ��
    NEED_TIMESTAMP( "needTimeStamp", 			"1006"),//��Ҫ�ṩʱ���
    NORIGHT_CALLSERVICE( "noRightCallService", 	"1007"),//��Ȩ���ʷ���
    SERVICE_NOTEXIST( "service", 				"1008"),//���񲻴���
    NEED_USERNAME( "username",					"1009"),//��Ҫ�ṩ�û���
	NOT_ONLINE("online",                        "1010");//�û�������

    /*
{"0000":"��������ʧ��","9999":"��������ɹ�","1001":"ǩ����Ч","1002":"�������","1003":"�û���ʧ��","1004":"��Ҫ���û�","1005":"/��Ҫ�ṩAppKey","1006":"��Ҫ�ṩ������","1007":"��Ҫ�ṩǩ��","1008":"��Ҫ�ṩʱ���","1009":"�û���֤ʧ��","1010":"��Ȩ���ʷ���","1011":"���񲻴���","1012":"��Ҫ�ṩSessionId","1013":"��Ҫ�ṩ�û���"}
     */
    
    private String v;
    private String c;

    private static Map<String,SipStatus> status ;

    SipStatus(String value, String code)
    {
        v = value;
        c = code;
    }

    @Override
    public String toString() {
        return v;
    }

    public String getCode() {
        return c;
    }

    public static SipStatus getStatus(String code) {
        if(status == null) {
            status = new HashMap<String,SipStatus>();
            status.put("0000",ERROR);
            status.put("9999",SUCCESS);
            status.put("1001",SIGNATURE_INVALID);
            status.put("1002",REQ_TIMEOUT);
            status.put("1003",NEED_APPKEY);
            status.put("1004",NEED_APINAME);
            status.put("1005",NEED_SIGN);
            status.put("1006",NEED_TIMESTAMP);
            status.put("1007",NORIGHT_CALLSERVICE);
            status.put("1008",SERVICE_NOTEXIST);
            status.put("1009",NEED_USERNAME);
            status.put("1010", NOT_ONLINE);
        }
        return status.get(code);
    }
}