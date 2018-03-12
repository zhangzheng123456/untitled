package com.bizvane.ishop.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by zhouzhou on 2017/1/15.
 *
 * 获取附近门店，采用mysql的st_distance函数
 *
 * 方法1：
 * select lng,lat,
 * (st_distance (point (lng, lat),point(#{lng},#{lat}) ) *111195) AS distance
 * from table
 * having distance > 范围
 *
 * st_distance 是mysql 5.6.1才加入
 *
 * 方法2：
 * ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lng}*PI()/180-lng*PI()/180)/2),2)))*1000)  AS distance
 *
 */

public class BaiduMapUtils {

    private static double EARTH_RADIUS = 6378.137;//地球半径

    private static String BaiduMap_AK = "PjLbAZIlSjheHKhGImUoFX1bhZyu0y4m"; //BaiduMap AK
    /**
     * 坐标转换
     * @param coords （格式：经度,纬度;经度,纬度，...,例如114.21892734521,29.575429778924;114.21892734521,29.575429778924）
     * @param from 源坐标类型（
     *             1：GPS设备获取的角度坐标，wgs84坐标;

    2：GPS获取的米制坐标、sogou地图所用坐标;

    3：google地图、soso地图、aliyun地图、mapabc地图和amap地图所用坐标，国测局坐标;

    4：3中列表地图坐标对应的米制坐标;

    5：百度地图采用的经纬度坐标;

    6：百度地图采用的米制坐标;

    7：mapbar地图坐标;

    8：51地图坐标）
     * @param to 目的坐标类型（5：bd09ll(百度经纬度坐标),6：bd09mc(百度米制经纬度坐标);）
     * @return
     */
    public static final JSONArray ConvertCoords(String coords,String from ,String to){
        JSONArray array = new JSONArray();
        String url = "http://api.map.baidu.com/geoconv/v1/?coords="+coords+"&from="+from+"&to="+to+"&ak="+BaiduMap_AK;

        String result = IshowHttpClient.get(url);
        JSONObject obj = JSONObject.parseObject(result);
        if (obj.get("status").toString().equals("0")){
            array = obj.getJSONArray("result");
        }
        return array;
    }


    public static double GetDistance(String latitude1, String longitude1, String latitude2, String longitude2) {
        double lat1 = Double.parseDouble(latitude1);
        double lat2 = Double.parseDouble(latitude2);
        double lng1 = Double.parseDouble(longitude1);
        double lng2 = Double.parseDouble(longitude2);

        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double a = radLat1 - radLat2;
        double b = Math.toRadians(lng1) - Math.toRadians(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;// 取WGS84标准参考椭球中的地球长半径(单位:m)
//        s = Math.round(s);
        return s;
    }


    public static void main(String[] args){
        System.out.print("==="+ConvertCoords("114.21892734521,29.575429778924","1","5"));
    }
}
