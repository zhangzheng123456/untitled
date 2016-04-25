package com.bizvane.ishop.exception;

public class UserException extends Exception{

    String message; // 定义String类型变量
    public UserException(String ErrorMessagr) { // 父类方法
        message = ErrorMessagr;
    }

    public String getMessage() { // 覆盖getMessage()方法
        return message;
    }

}
