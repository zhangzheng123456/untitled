package com.bizvane.ishop.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;


/**
 * Created by yin on 2016/7/6.
 */
public class WageDao {
    public int insert(Connection conn, Vector<String> titleList,//传过来的数进行插入处理。
                      ArrayList<String> v, String tbName) throws SQLException  {
        int row=0;
    //    String id = UUID.randomUUID().toString().replace("-", "");//生成随机数
        String sql = "insert into " + tbName + "(id ";//拼接插入字段
        int titles = 0;
        for (String s : titleList) {
            String t = "," + s;
            sql += t;
            titles++;
        }
        sql += ")";
        sql = sql + " values ('" + 0 + "' ";//拼接插入数据
        for (int i = 0; i < titles; i++) {
            String s = ", ? ";
            sql += s;
        }
        sql += ")";
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < titles; i++) {
            ps.setString(i + 1, v.get(i));
        }
        ps.executeUpdate();
        ps.close();
        return row;
    }

//    public void createTable(Vector<String> titleList, String tbName)
//            throws Exception {
//        QueryRunner run = new QueryRunner(ConnUtils.getDataSource(), true);//SQL数据库要加
//        String dele = " drop table " + tbName;//删除同名的表，覆盖数据使用
//        String sql = "create table " + tbName + "( id varchar(100)";//准备拼接使用
//        String creadPk = "ALTER TABLE " + tbName + "  ADD UNIQUE (工号)";//生成工号约束，不能重复。
//        for (String s : titleList) {//创建语句拼接
//            String s1 = ", " + s + " varchar(50) default 0 ";
//            sql += s1;
//        }
//        sql += ")";
//        if (isExist(tbName)) {
//            run.update(dele);
//        }
//        run.update(sql);
//        run.update(creadPk);
//    }

//    // 判断表是否存在
//    public static boolean isExist(String tbName) throws SQLException {
//        QueryRunner run = new QueryRunner(ConnUtils.getDataSource(), true);
//        String sql = "select * from dbo.sysobjects where id = object_id(N'["
//                + tbName + "]')";
//        Object[] b = run.query(sql, new ArrayHandler());
//        if (b.length > 0) {
//            return true;
//        } else {
//            return false;
//        }
//
//    }
}
