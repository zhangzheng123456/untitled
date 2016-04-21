//package com.bizvane.ishop.dao.imp;
//
//
//import com.bizvane.ishop.bean.User;
//import com.bizvane.ishop.dao.UserDao;
//import org.mybatis.spring.SqlSessionTemplate;
//
//import javax.annotation.Resource;
//import java.util.List;
//
//@SuppressWarnings("unchecked")
//public class IUserDao implements UserDao{
//        /*sql 语句*/
//        private static final String INSERT = "insert";
//
//        private static final String UPDATE = "update";
//
//        private static final String DELETE = "delete";
//
//        private static final String SELECTALL = "selectAll";
//
//        private static final String SELECTBYID = "selectById";
//
//        private SqlSessionTemplate sqlSession;
//
//        @Resource
//        public void setSqlSession(SqlSessionTemplate sqlSession)
//        {
//            this.sqlSession = sqlSession;
//        }
//
//        public boolean delete(int id)
//        {
//            String sql = this.getStatementId(User.class, DELETE);
//            sqlSession.delete(sql, id);
//            return true;
//        }
//
//        public List<User> findAll()
//        {
//            String sql = this.getStatementId(User.class, SELECTALL);
//            List<User> list = sqlSession.selectList(sql);
//            return list;
//        }
//
//        public User findById(int id)
//        {
//            String sql = this.getStatementId(User.class, SELECTBYID);
//            User user = (User)sqlSession.selectOne(sql, id);
//            return user;
//        }
//
//        @SuppressWarnings("static-access")
//        public boolean insert(User user)
//        {
//            String sql = this.getStatementId(User.class, INSERT);
//            this.sqlSession.insert(sql, user);
//            return true;
//        }
//
//        public boolean update(User user)
//        {
//            String sql = this.getStatementId(User.class, UPDATE);
//            this.sqlSession.update(sql, user);
//            return true;
//        }
//
//        /**
//         * 映射sqlid
//         */
//        private String getStatementId(Class entityClass, String suffix)
//        {
//            String sqlStr = entityClass.getName() + "." + suffix;
//            System.out.println("getStatementId:" + sqlStr);
//            return sqlStr;
//        }
//
//    }
