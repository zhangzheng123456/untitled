package com.bizvane.ishop.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bizvane.ishop.bean.DataBean;
import com.bizvane.ishop.constant.Common;
import com.bizvane.ishop.entity.Task;
import com.bizvane.ishop.service.TaskService;
import com.bizvane.ishop.utils.WebUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by PC on 2017/1/5.
 */
@Controller
@RequestMapping("/activityMake")
public class VipActivityNewMakeController {
    @Autowired
    private TaskService taskService;



    @ResponseBody
    @Transactional
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addActivityMake(HttpServletRequest request) {
        DataBean dataBean = new DataBean();

        try {
            String user_code = request.getSession().getAttribute("user_code").toString();

            String jsString = request.getParameter("param");
            JSONObject jsonObj = new JSONObject(jsString);

            String message = jsonObj.get("message").toString();
            JSONObject jsonObject = new JSONObject(message);

            String tasklist = jsonObject.get("tasklist").toString();
            JSONArray jsonArray_task = JSON.parseArray(tasklist);

            String sendlist = jsonObject.get("sendlist").toString();
            JSONArray jsonArray_send = JSON.parseArray(sendlist);

            int count=0;
            for (int i=0;i<jsonArray_task.size();i++){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                String task_code = "T" + sdf.format(new Date()) + Math.round(Math.random() * 9);
                JSONObject task_obj = new JSONObject(jsonArray_task.get(i).toString());
                Task task = WebUtils.JSON2Bean(task_obj, Task.class);
                task.setTask_code(task_code);

                Date now = new Date();
                task.setCreated_date(Common.DATETIME_FORMAT.format(now));
                task.setCreater(user_code);
                task.setModified_date(Common.DATETIME_FORMAT.format(now));
                task.setModifier(user_code);
                task.setIsactive("Y");

                count= taskService.insertTask(task);

            }
            for (int i=0;i<jsonArray_send.size();i++){
                JSONObject send_obj = new JSONObject(jsonArray_send.get(i).toString());
                String corp_code = send_obj.get("corp_code").toString();
                String send_type = send_obj.get("send_type").toString();
                String sms_code = "Fs" + corp_code + Common.DATETIME_FORMAT_DAY_NUM.format(new Date());


                if(send_type.equals("wx")){

                }else if(send_type.equals("sms")){

                }else if(send_type.equals("email")){

                }
            }
        }catch (Exception ex){
            dataBean.setCode(Common.DATABEAN_CODE_ERROR);
            dataBean.setId("-1");
            dataBean.setMessage("新增失败");
        }
        return  dataBean.getJsonStr();
    }

}
