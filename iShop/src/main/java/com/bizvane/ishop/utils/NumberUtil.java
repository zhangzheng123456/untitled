package com.bizvane.ishop.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by maoweidong on 16/9/23.
 */

/**
 * 数字格式化工具类
 */
public class NumberUtil {
    /**
     * 格式化为指定位小数的数字,返回未使用科学计数法表示的具有指定位数的字符串。
     * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。
     * <pre>
     *  "3.1415926", 1          --> 3.1
     *  "3.1415926", 3          --> 3.142
     *  "3.1415926", 4          --> 3.1416
     *  "3.1415926", 6          --> 3.141593
     *  "1234567891234567.1415926", 3   --> 1234567891234567.142
     * </pre>
     *
     * @param
     * @param precision 小数精确度总位数,如2表示两位小数
     * @return 返回数字格式化后的字符串表示形式(注意返回的字符串未使用科学计数法)
     */
    public static String keepPrecision(String number, int precision) {
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(precision, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    public static String keepPrecision(String number) {
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    /**
     * 格式化为指定位小数的数字,返回未使用科学计数法表示的具有指定位数的字符串。<br>
     * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。<br>
     * 如果给定的数字没有小数，则转换之后将以0填充；例如：int 123  1 --> 123.0<br>
     * <b>注意：</b>如果精度要求比较精确请使用 keepPrecision(String number, int precision)方法
     *
     * @param
     * @param precision 小数精确度总位数,如2表示两位小数
     * @return 返回数字格式化后的字符串表示形式(注意返回的字符串未使用科学计数法)
     */
    public static String keepPrecision(Number number, int precision) {
        return keepPrecision(String.valueOf(number), precision);
    }

    /**
     * 对double类型的数值保留指定位数的小数。<br>
     * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。<br>
     * <b>注意：</b>如果精度要求比较精确请使用 keepPrecision(String number, int precision)方法
     *
     * @param number    要保留小数的数字
     * @param precision 小数位数
     * @return double 如果数值较大，则使用科学计数法表示
     */
    public static String keepPrecision(double number, int precision) {
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(precision, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    public static String keepPrecision(double number) {
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    /**
     * 对float类型的数值保留指定位数的小数。<br>
     * 该方法舍入模式：向“最接近的”数字舍入，如果与两个相邻数字的距离相等，则为向上舍入的舍入模式。<br>
     * <b>注意：</b>如果精度要求比较精确请使用 keepPrecision(String number, int precision)方法
     *
     * @param number    要保留小数的数字
     * @param precision 小数位数
     * @return float 如果数值较大，则使用科学计数法表示
     */
    public static float keepPrecision(float number, int precision) {
        BigDecimal bg = new BigDecimal(number);
        return bg.setScale(precision, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 对float类型的数值进行过滤
     * 先进行向上舍入
     * 再进行判断，若小数点后第三位有数字，则转换为百分数显示；若小数点后底位有数字，则转换成千分位显示；else则算0%；
     *
     * @param number
     * @return String
     */
    public static String fmtMicrometer(String number){

        //向上舍入
        Double number1 = Double.parseDouble(keepPrecision(number,4));

        DecimalFormat df;
        if (number1*1000 >= 1){
            df = new DecimalFormat("##.#%");
        }else if (number1*10000 >= 1){
            df = new DecimalFormat("##.#\u2030");
        }else {
            df = new DecimalFormat("##.#%");
        }
        return df.format(number1);
    }

    /**
     * 对float类型的数值进行过滤
     * 先进行向上舍入
     * 再进行判断，若小数点后第三位有数字，则转换为百分数显示；若小数点后底位有数字，则转换成千分位显示；else则算0%；
     *
     * @param number
     * @return String
     */
    public static String percent(double number){
        DecimalFormat df = new DecimalFormat("##.#%");
        return df.format(number);
    }

    public static void main(String[] args){
        System.out.println(fmtMicrometer("0.00007"));
//        BigDecimal bg = new BigDecimal(0.0003);
//        Double b = bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
//        String number = "0.0003";
//        float a = Float.parseFloat(number);
//        System.out.println(b);
    }

}
