//package com.bizvane.ishop.controller;
//
//import com.bizvane.ishop.service.BaseService;
//import com.bizvane.ishop.service.SignService;
//import com.bizvane.ishop.service.StoreService;
//import com.bizvane.sun.common.service.mongodb.MongoDBClient;
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
///**
// * Created by yin on 2016/6/23.
// */
//@Controller
//@RequestMapping("/sign")
//public class SignController {
//    @Autowired
//    private SignService signService;
//    @Autowired
//    private StoreService storeService;
//    @Autowired
//    private BaseService baseService;
//    String id;
//    @Autowired
//   private MongoDBClient mongodbClient;
//    private static final Logger logger = Logger.getLogger(InterfaceController.class);
//
////    //列表
////    @RequestMapping(value = "/list", method = RequestMethod.GET)
////    @ResponseBody
////    public String selectAll(HttpServletRequest request) {
////        DataBean dataBean = new DataBean();
////        int pages = 0;
////        try {
////            String role_code = request.getSession().getAttribute("role_code").toString();
////            String group_code = request.getSession().getAttribute("group_code").toString();
////            String corp_code = request.getSession().getAttribute("corp_code").toString();
////            String user_code = request.getSession().getAttribute("user_code").toString();
////            int page_number = Integer.parseInt(request.getParameter("pageNumber"));
////            int page_size = Integer.parseInt(request.getParameter("pageSize"));
////            JSONObject result = new JSONObject();
////            PageInfo<Sign> list = null;
////            if (role_code.equals(Common.ROLE_SYS)) {
////                //系统管理员
////                list = signService.selectSignByInp(page_number, page_size, "", "","", "", role_code,"");
////            } else if (role_code.equals(Common.ROLE_GM)) {
////                //企业管理员
////                list = signService.selectSignByInp(page_number, page_size, corp_code, "","", "", role_code,"");
////            } else if (role_code.equals(Common.ROLE_BM)) {
////                //品牌管理员
////                String brand_code = request.getSession().getAttribute("brand_code").toString();
////                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
////                List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
////                String store_code = "";
////                for (int i = 0; i < stores.size(); i++) {
////                    store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
////                }
////                list = signService.selectSignByInp(page_number, page_size, corp_code, "", store_code, "", role_code,"");
////            }else if (role_code.equals(Common.ROLE_SM)) {
////                //店长
////                String store_code = request.getSession().getAttribute("store_code").toString();
////                list = signService.selectSignByInp(page_number, page_size, corp_code, "", store_code, "", role_code,"");
////            } else if (role_code.equals(Common.ROLE_AM)) {
////                //区经
////                String area_code = request.getSession().getAttribute("area_code").toString();
////                String store_code = request.getSession().getAttribute("store_code").toString();
////                list = signService.selectSignByInp(page_number, page_size, corp_code, "", "", area_code, role_code,store_code);
////            } else if (role_code.equals(Common.ROLE_STAFF)) {
////                list = signService.selectByUser(page_number, page_size, corp_code, user_code, "");
////            }
////            //  System.out.println(list.getList().get(0).getSign_time()+"---"+list.getList().get(0).getUser_code());
////            result.put("list", JSON.toJSONString(list));
////            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
////            dataBean.setId("1");
////            dataBean.setMessage(result.toString());
////        } catch (Exception ex) {
////            ex.printStackTrace();
////            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////            dataBean.setId("1");
////            dataBean.setMessage(ex.getMessage());
////        }
////        return dataBean.getJsonStr();
////    }
//
//
//
//
//    //条件查询
////    @RequestMapping(value = "/search", method = RequestMethod.POST)
////    @ResponseBody
////    public String search(HttpServletRequest request) {
////        DataBean dataBean = new DataBean();
////        try {
////            String jsString = request.getParameter("param");
////            logger.info("json---------------" + jsString);
////            JSONObject jsonObj = new JSONObject(jsString);
////            id = jsonObj.get("id").toString();
////            String message = jsonObj.get("message").toString();
////            JSONObject jsonObject = new JSONObject(message);
////            int page_number = Integer.valueOf(jsonObject.get("pageNumber").toString());
////            int page_size = Integer.valueOf(jsonObject.get("pageSize").toString());
////            String search_value = jsonObject.get("searchValue").toString();
////            String role_code = request.getSession().getAttribute("role_code").toString();
////            String user_code = request.getSession().getAttribute("user_code").toString();
////            JSONObject result = new JSONObject();
////            PageInfo<Sign> list = null;
////            if (role_code.equals(Common.ROLE_SYS)) {
////                //系统管理员
////                list =signService.selectSignByInp(page_number, page_size, "", search_value, "", "", role_code,"");
////            } else {
////                String corp_code = request.getSession().getAttribute("corp_code").toString();
////                if (role_code.equals(Common.ROLE_GM)) {
////                    //企业管理员
////                    list = signService.selectSignByInp(page_number, page_size, corp_code, search_value, "", "", role_code,"");
////                } else if (role_code.equals(Common.ROLE_BM)) {
////                    //品牌管理员
////                    String brand_code = request.getSession().getAttribute("brand_code").toString();
////                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
////                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
////                    String store_code = "";
////                    for (int i = 0; i < stores.size(); i++) {
////                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
////                    }
////                    list = signService.selectSignByInp(page_number, page_size, corp_code, search_value, store_code, "", role_code,"");
////                } else if (role_code.equals(Common.ROLE_SM)) {
////                    //店长
////                    String store_code = request.getSession().getAttribute("store_code").toString();
////                    list = signService.selectSignByInp(page_number, page_size, corp_code, search_value, store_code, "", role_code,"");
////                } else if (role_code.equals(Common.ROLE_AM)) {
////                    //区经
////                    String area_code = request.getSession().getAttribute("area_code").toString();
////                    String store_code = request.getSession().getAttribute("store_code").toString();
////                    list = signService.selectSignByInp(page_number, page_size, corp_code, search_value, "", area_code, role_code,store_code);
////                } else if (role_code.equals(Common.ROLE_STAFF)) {
////                    list = signService.selectByUser(page_number, page_size, corp_code, user_code, search_value);
////                }
////            }
////            result.put("list", JSON.toJSONString(list));
////            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
////            dataBean.setId(id);
////            dataBean.setMessage(result.toString());
////        } catch (Exception ex) {
////            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////            dataBean.setId(id);
////            dataBean.setMessage(ex.getMessage());
////        }
////        return dataBean.getJsonStr();
////    }
//
//
//
//    /**
//     * 删除(用了事务)
//     */
////    @RequestMapping(value = "/delete", method = RequestMethod.POST)
////    @ResponseBody
////    @Transactional
////    public String delete(HttpServletRequest request) {
////        DataBean dataBean = new DataBean();
////        try {
////            String jsString = request.getParameter("param");
////            logger.info("json--delete-------------" + jsString);
////            JSONObject jsonObj = new JSONObject(jsString);
////            id = jsonObj.get("id").toString();
////            String message = jsonObj.get("message").toString();
////            JSONObject jsonObject = new JSONObject(message);
////            String inter_id = jsonObject.get("id").toString();
////            String[] ids = inter_id.split(",");
////            for (int i = 0; i < ids.length; i++) {
////                //logger.info("-------------delete--" + Integer.valueOf(ids[i]));
////                Sign sign = signService.selSignById(Integer.valueOf(ids[i]));
////                signService.delSignById(Integer.valueOf(ids[i]));
////                dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
////                dataBean.setId(id);
////                dataBean.setMessage("success");
////
//////----------------行为日志开始------------------------------------------
////                /**
////                 * mongodb插入用户操作记录
////                 * @param operation_corp_code 操作者corp_code
////                 * @param operation_user_code 操作者user_code
////                 * @param function 功能
////                 * @param action 动作
////                 * @param corp_code 被操作corp_code
////                 * @param code 被操作code
////                 * @param name 被操作name
////                 * @throws Exception
////                 */
////                String operation_corp_code = request.getSession().getAttribute("corp_code").toString();
////                String operation_user_code = request.getSession().getAttribute("user_code").toString();
////                String function = "员工管理_签到管理";
////                String action = Common.ACTION_DEL;
////                String t_corp_code = sign.getCorp_code();
////                String t_code = sign.getUser_code();
////                String t_name = sign.getUser_name();
////                String remark = sign.getSign_time()+"("+sign.getStatus()+")";
////                baseService.insertUserOperation(operation_corp_code, operation_user_code, function, action, t_corp_code, t_code, t_name,remark);
////            }
////        } catch (Exception ex) {
////            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////            dataBean.setId(id);
////            dataBean.setMessage(ex.getMessage());
////            return dataBean.getJsonStr();
////        }
////        logger.info("delete-----" + dataBean.getJsonStr());
////        return dataBean.getJsonStr();
////    }
//
//
//    /***
//     * 导出数据
//     */
////    @RequestMapping(value = "/exportExecl", method = RequestMethod.POST)
////    @ResponseBody
////    public String exportExecl(HttpServletRequest request, HttpServletResponse response) {
////        DataBean dataBean = new DataBean();
////        String errormessage = "数据异常，导出失败";
////        try {
////            String role_code = request.getSession().getAttribute("role_code").toString();
////            String corp_code = request.getSession().getAttribute("corp_code").toString();
////            String user_code = request.getSession().getAttribute("user_code").toString();
////            String jsString = request.getParameter("param");
////             JSONObject jsonObj = JSONObject.parseObject(jsString);
////            String message = jsonObj.get("message").toString();
////             JSONObject jsonObject = JSONObject.parseObject(message);
////            String search_value = jsonObject.get("searchValue").toString();
////            String screen = jsonObject.get("list").toString();
////            PageInfo<Sign> list = null;
////            if (screen.equals("")) {
////                if (role_code.equals(Common.ROLE_SYS)) {
////                    //系统管理员
////                    list = signService.selectSignByInp(1, Common.EXPORTEXECLCOUNT, "", search_value, "", "", role_code,"");
////                } else if (role_code.equals(Common.ROLE_GM)) {
////                    //系统管理员
////                    list = signService.selectSignByInp(1, Common.EXPORTEXECLCOUNT, corp_code, search_value, "", "", role_code,"");
////                } else if (role_code.equals(Common.ROLE_BM)) {
////                    //品牌管理员
////                    String brand_code = request.getSession().getAttribute("brand_code").toString();
////                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
////                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
////                    String store_code = "";
////                    for (int i = 0; i < stores.size(); i++) {
////                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
////                    }
////                    list = signService.selectSignByInp(1, Common.EXPORTEXECLCOUNT, corp_code, search_value, store_code, "", role_code,"");
////                } else if (role_code.equals(Common.ROLE_SM)) {
////                    //店长
////                    String store_code = request.getSession().getAttribute("store_code").toString();
////                    list = signService.selectSignByInp(1, Common.EXPORTEXECLCOUNT, corp_code, search_value, store_code, "", role_code,"");
////                } else if (role_code.equals(Common.ROLE_AM)) {
////                    //区经
////                    String area_code = request.getSession().getAttribute("area_code").toString();
////                    String store_code = request.getSession().getAttribute("store_code").toString();
////                    list = signService.selectSignByInp(1, Common.EXPORTEXECLCOUNT, corp_code, search_value, "", area_code, role_code,store_code);
////                } else if (role_code.equals(Common.ROLE_STAFF)) {
////                    list = signService.selectByUser(1, Common.EXPORTEXECLCOUNT, corp_code, user_code, search_value);
////                }
////
////            } else {
////                Map<String, String> map = WebUtils.Json2Map(jsonObject);
////                if (role_code.equals(Common.ROLE_SYS)) {
////                    list = signService.selectSignAllScreen(1, Common.EXPORTEXECLCOUNT, "", "", "", "", map,"");
////                } else if (role_code.equals(Common.ROLE_GM)) {
////                    list = signService.selectSignAllScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", "", "", map,"");
////                }  else if (role_code.equals(Common.ROLE_BM)) {
////                    //品牌管理员
////                    String brand_code = request.getSession().getAttribute("brand_code").toString();
////                    brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
////                    List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
////                    String store_code = "";
////                    for (int i = 0; i < stores.size(); i++) {
////                        store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
////                    }
////                    list = signService.selectSignAllScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", store_code, role_code, map,"");
////                }else if (role_code.equals(Common.ROLE_AM)) {
////                    String area_code = request.getSession(false).getAttribute("area_code").toString();
////                    String store_code = request.getSession().getAttribute("store_code").toString();
////                    list = signService.selectSignAllScreen(1, Common.EXPORTEXECLCOUNT, corp_code, area_code, "", role_code, map,store_code);
////                } else if (role_code.equals(Common.ROLE_SM)) {
////                    String store_code = request.getSession(false).getAttribute("store_code").toString();
////                    list = signService.selectSignAllScreen(1, Common.EXPORTEXECLCOUNT, corp_code, "", store_code, role_code, map,"");
////                } else if (role_code.equals(Common.ROLE_STAFF)) {
////                    list = signService.selectSignAllScreenByUser(1, Common.EXPORTEXECLCOUNT, corp_code, user_code, map);
////                }
////            }
////            List<Sign> signs = list.getList();
////            for (Sign sign :signs) {
////                String location = sign.getLocation();
////                String replaceStr = WebUtils.StringFilter(location);
////                sign.setLocation(replaceStr);
////            }
////            if (signs.size() >= Common.EXPORTEXECLCOUNT) {
////                errormessage = "导出数据过大";
////                int i = 9 / 0;
////            }
////            ObjectMapper mapper = new ObjectMapper();
////            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
////            String json = mapper.writeValueAsString(signs);
////            LinkedHashMap<String, String> map = WebUtils.Json2ShowName(jsonObject);
////            // String column_name1 = "corp_code,corp_name";
////            // String[] cols = column_name.split(",");//前台传过来的字段
////            String pathname = OutExeclHelper.OutExecl(json,signs, map, response, request);
////            JSONObject result = new JSONObject();
////            if (pathname == null || pathname.equals("")) {
////                errormessage = "数据异常，导出失败";
////                int a = 8 / 0;
////            }
////            result.put("path", JSON.toJSONString("lupload/" + pathname));
////            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
////            dataBean.setId(id);
////            dataBean.setMessage(result.toString());
////        } catch (Exception e) {
////            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////            dataBean.setId("1");
////            dataBean.setMessage(errormessage);
////        }
////        return dataBean.getJsonStr();
////    }
//
//
//
//    /**
//     * 签到管理
//     * 筛选
//     */
////    @RequestMapping(value = "/screen", method = RequestMethod.POST)
////    @ResponseBody
////    public String selectAllSignScreen(HttpServletRequest request) {
////        DataBean dataBean = new DataBean();
////        String id = "";
////        try {
////            String jsString = request.getParameter("param");
////            org.json.JSONObject jsonObject1 = new org.json.JSONObject(jsString);
////            id = jsonObject1.getString("id");
////            String message = jsonObject1.get("message").toString();
////            JSONObject jsonObject2 = new JSONObject(message);
////            int page_number = Integer.parseInt(jsonObject2.get("pageNumber").toString());
////            int page_size = Integer.parseInt(jsonObject2.get("pageSize").toString());
////            String role_code = request.getSession().getAttribute("role_code").toString();
////            String group_code = request.getSession().getAttribute("group_code").toString();
////            String corp_code = request.getSession().getAttribute("corp_code").toString();
////            String user_code = request.getSession().getAttribute("user_code").toString();
////
////            Map<String, String> map = WebUtils.Json2Map(jsonObject2);
////            JSONObject result = new JSONObject();
////            PageInfo<Sign> list = null;
////            if (role_code.equals(Common.ROLE_SYS)) {
////                list = signService.selectSignAllScreen(page_number, page_size, "", "", "", role_code, map,"");
////            } else if (role_code.equals(Common.ROLE_GM)) {
////                list = signService.selectSignAllScreen(page_number, page_size, corp_code, "", "", role_code, map,"");
////            } else if (role_code.equals(Common.ROLE_BM)) {
////                //品牌管理员
////                String brand_code = request.getSession().getAttribute("brand_code").toString();
////                brand_code = brand_code.replace(Common.SPECIAL_HEAD, "");
////                List<Store> stores = storeService.selStoreByAreaBrandCode(corp_code, "", brand_code, "","");
////                String store_code = "";
////                for (int i = 0; i < stores.size(); i++) {
////                    store_code = store_code + Common.SPECIAL_HEAD + stores.get(i).getStore_code() + ",";
////                }
////                list = signService.selectSignAllScreen(page_number, page_size, corp_code, "", store_code, role_code, map,"");
////            }else if (role_code.equals(Common.ROLE_AM)) {
////                String area_code = request.getSession(false).getAttribute("area_code").toString();
////                String store_code = request.getSession(false).getAttribute("store_code").toString();
////                list = signService.selectSignAllScreen(page_number, page_size, corp_code, area_code, "", role_code, map,store_code);
////            } else if (role_code.equals(Common.ROLE_SM)) {
////                String store_code = request.getSession(false).getAttribute("store_code").toString();
////                list = signService.selectSignAllScreen(page_number, page_size, corp_code, "", store_code, role_code, map,"");
////            } else if (role_code.equals(Common.ROLE_STAFF)) {
////                list = signService.selectSignAllScreenByUser(page_number, page_size, corp_code, user_code, map);
////            }
////            result.put("list", JSON.toJSONString(list));
////            dataBean.setCode(Common.DATABEAN_CODE_SUCCESS);
////            dataBean.setId("1");
////            dataBean.setMessage(result.toString());
////        } catch (Exception ex) {
////            ex.printStackTrace();
////            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
////            dataBean.setId("1");
////            dataBean.setMessage(ex.getMessage());
////        }
////        return dataBean.getJsonStr();
////    }
//
//
//}
