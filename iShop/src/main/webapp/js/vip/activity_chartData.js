/**
/**
 * Created by ghy on 2017/3/31.
 */
var Num = 1;
var allData="";//所有的结果
function formatCurrency(num) {
    num=String(num);
    var reg=num.indexOf('.') >-1 ? /(\d{1,3})(?=(?:\d{3})+\.)/g : /(\d{1,3})(?=(?:\d{3})+$)/g;//千分符的正则
    num=num.replace(reg, '$1,');//千分位格式化
    return num;
}
//意向客户图表,列表数据
function getCustomerChart() {
    var activity_code=sessionStorage.getItem("activity_code");
    var param={"activity_code":activity_code};
    $("#customer .task_box .listLoading_box").show();//加载遮罩
    oc.postRequire("post","/activityAnaly/intentionUser","",param,function (data) {
        if(data.code==0){
            var msg = JSON.parse(data.message);
            $("#all_customers").text(formatCurrency(msg.target_vips));
            $("#intent_customers").text(formatCurrency(msg.intentionCount));
            $("#click_customers").text(formatCurrency(msg.clickCount));
            customerChart(msg.intentionCount,msg.target_vips-msg.intentionCount);
        }else {
            //console.log(data.message);
        }
        $("#customer .task_box .listLoading_box").eq(0).hide();
    });//饼图
    oc.postRequire("post","/activityAnaly/intentionView","",param,function (data) {
        if(data.code==0){
            var msg = JSON.parse(data.message);
            var list = msg.clickList;
            customerBarchart(list);
        }else {
            //console.log(data.message);
        }
        $("#customer .task_box .listLoading_box").eq(1).hide();
    });//柱状图
}
function getCustomerList() {
    $("#customer .list_box .listLoading_box").show();//加载遮罩
    var activity_code=sessionStorage.getItem("activity_code");
    var type=$("#customer .doType_active").attr("data-type");
    var userCode=type=="store"?"":$("#customer_userCode").val().trim();
    var userName=type=="store"?"":$("#customer_user").val().trim();
    var storeName=type=="store"?$("#customer_store").val().trim():"";
    var storeCode=type=="store"?$("#customer_storeCode").val().trim():"";
    if(type=="store"){
        var div = '<div class="people_title_order">序号</div><div class="people_title_num">店铺编号</div>'
            +'<div class="people_title_shop">店铺名称</div><div class="people_title_shop">所属店铺群组</div><div class="people_title_count">目标会员数</div><div class="people_title_count">意向会员数</div><div class="people_title_plan">意向客户占比</div><div class="influence_head"></div>';
    }else {
        var div = '<div class="people_title_order">序号</div><div class="people_title_num">员工编号</div>'
            +'<div class="people_title_num">员工姓名</div><div class="people_title_shop">所属店铺</div><div class="people_title_count">目标会员数</div><div class="people_title_count">意向会员数</div><div class="people_title_plan">意向客户占比</div><div class="influence_head"></div>';
    }
    var _param={"activity_code":activity_code,"type":type,"page_num":Num,"page_size":"20","store_code":storeCode,"store_name":storeName,"user_code":userCode,"user_name":userName};
    oc.postRequire("post","/activityAnaly/intentionList","",_param,function (data) {
        if(data.code==0){
            var msg = JSON.parse(data.message);
            var list = msg.list;
            has_next_list = msg.hasNext;
            var li = "";
            if(type=="store"){
                if(list.length>0){
                    for(var i=0;i<list.length;i++){
                        var obj = JSON.stringify(list[i]);
                        var width=parseInt(list[i].proportion);
                        var a=i+1+(Num-1)*20;
                        li+=' <li class="people_title"><div class="people_title_order">'
                            + a
                            + '</div><div class="people_title_num ellipsis" title="'+list[i].store_code+'">'
                            + list[i].store_code
                            + '</div><div class="people_title_shop ellipsis" title="'+list[i].store_name+'">'
                            + list[i].store_name
                            + '</div><div class="people_title_shop ellipsis" title="'+list[i].area_name+'">'
                            + list[i].area_name
                            + '</div><div class="people_title_count ellipsis" title="'+list[i].target_vips+'">'
                            + formatCurrency(list[i].target_vips)
                            + '</div><div class="people_title_count ellipsis" title="'+list[i].intentionCount+'">'
                            + formatCurrency(list[i].intentionCount)
                            + '</div><div class="people_title_plan">'
                            + '<div class="undone"><div class="done" style="width:'+width+'%"></div></div>'
                            + '<span class="percent_percent">'
                            + width
                            + '%</span></div>'
                            + "<div class='data_detail' data-info='"+obj+"'><div class='detail'>详情<span class='icon-ishop_8-03' style='color:#5f8bc8'>"
                            +'</span></div></div></li>'
                    }
                    $("#customer .peopleContent").show();
                    $("#customer .peopleError").hide();
                }else {
                    $("#customer .peopleContent").hide();
                    $("#customer .peopleError").show();
                }
            }else {
                if(list.length>0){
                    for(var i=0;i<list.length;i++){
                        var obj = JSON.stringify(list[i]);
                        var width=parseInt(list[i].proportion);
                        var a=i+1+(Num-1)*20;
                        li+=' <li class="people_title"><div class="people_title_order">'
                            + a
                            + '</div><div class="people_title_num ellipsis" title="'+list[i].user_code+'">'
                            + list[i].user_code
                            + '</div><div class="people_title_num ellipsis" title="'+list[i].user_name+'">'
                            + list[i].user_name
                            + '</div><div class="people_title_shop ellipsis" title="'+list[i].store_name+'">'
                            + list[i].store_name
                            + '</div><div class="people_title_count ellipsis" title="'+list[i].target_vips+'">'
                            + formatCurrency(list[i].target_vips)
                            + '</div><div class="people_title_count ellipsis" title="'+list[i].intentionCount+'">'
                            + formatCurrency(list[i].intentionCount)
                            + '</div><div class="people_title_plan">'
                            + '<div class="undone"><div class="done" style="width:'+width+'%"></div></div>'
                            + '<span class="percent_percent">'
                            + width
                            + '%</span></div>'
                            + '<div class="data_detail" data-info='+obj+'><div class="detail">详情<span class="icon-ishop_8-03" style="color:#5f8bc8">'
                            +'</span></div></div></li>'
                    }
                    $("#customer .peopleContent").show();
                    $("#customer .peopleError").hide();
                }else {
                    $("#customer .peopleContent").hide();
                    $("#customer .peopleError").show();
                }
            }
            $("#customer .people_head li").empty();
            $("#customer .people_head li").append(div);
            $("#customer .peopleContent ul").append(li);
        }else {
            $("#customer .people_head li").empty();
            $("#customer .people_head li").append(div);
            //console.log(data.message);
        }
        $("#customer .list_box .listLoading_box").hide();//移除遮罩
    });
}
//导购影响分析图表，列表数据
function getUserChart() {
    var activity_code=sessionStorage.getItem("activity_code");
    var param={"activity_code":activity_code};
    $("#influence .task_box .listLoading_box").show();
    oc.postRequire("post","/activityAnaly/view","",param,function (data) {
        if(data.code==0){
            var msg = JSON.parse(data.message);
            var list = msg.shareList;
            var click_list = msg.clickList;
            var data_x_arr = [];
            var data_y_arr = [];
            var data_y_click_arr = [];
            if(list.length == 0 && click_list.length == 0){
                $("#influence_line_chart").html("暂无数据");
            }else if(list.length > 0){
                for(var i=0;i<list.length;i++){
                    data_x_arr.push(list[i].date);
                    data_y_arr.push(list[i].count);
                }
                for(var j=0;j<click_list.length;j++){
                    data_y_click_arr.push(click_list[j].count);
                }
                line_chart(data_x_arr,data_y_arr,data_y_click_arr);
            }
        }else {
            //console.log(data.message);
        }
        $("#influence .task_box .listLoading_box").eq(1).hide();
    });//折线图
    oc.postRequire("post","/activityAnaly/userCount","",param,function (data) {
        if(data.code==0){
            var msg = JSON.parse(data.message);
            $("#usersAll").text(formatCurrency(msg.users));
            $("#shareUsers").text(formatCurrency(msg.shareUsers));
            userChart(msg.shareUsers,msg.users-msg.shareUsers);
        }else {
            //console.log(data.message);
        }
        $("#influence .task_box .listLoading_box").eq(0).hide();
    });//饼图
}
function getUserList() {
    $("#influence .list_box .listLoading_box").show();
    var activity_code=sessionStorage.getItem("activity_code");
    var userCode=$("#influence_userCode").val().trim();
    var userName=$("#influence_user").val().trim();
    var storeName=$("#influence_store").val().trim();
    var storeCode=$("#influence_storeCode").val().trim();
    var _param={"activity_code":activity_code,"page_num":Num,"page_size":"20","store_code":storeCode,"store_name":storeName,"user_code":userCode,"user_name":userName};
    oc.postRequire("post","/activityAnaly/userList","",_param,function (data) {
        if(data.code==0){
            var msg = JSON.parse(data.message);
            var list = msg.list;
            var next = msg.hasNext;
            has_next_list = next ? true : false;
            var li = "";
            if(list.length>0){
                $("#influence .peopleError").hide();
                for(var i=0;i<list.length;i++){
                    var a=(Num-1)*20+i+1;
                    li+=' <li class="people_title"> <div class="people_title_order">'
                        + a
                        + '</div><div class="people_title_counts ellipsis" title="'+list[i].user_code+'">'
                        + list[i].user_code
                        + '</div><div class="people_title_counts ellipsis" title="'+list[i].user_name+'">'
                        + list[i].user_name
                        + '</div><div class="people_title_counts ellipsis" title="'+list[i].store_code+'">'
                        + list[i].store_code
                        + '</div><div class="people_title_shop ellipsis" title="'+list[i].store_name+'">'
                        + list[i].store_name
                        + '</div><div class="influence_head">'
                        + formatCurrency(list[i].shareUrlCount)
                        + '</div><div class="influence_head">'
                        + formatCurrency(list[i].clickCount)
                        + '</div><div class="influence_head">'
                        + formatCurrency(list[i].vipCount)
                        + '</div><div class="influence_head">'
                        + formatCurrency(list[i].urlVipCount)
                        + '</div><div class="influence_head" style="width: 140px">'
                        + formatCurrency(list[i].noVipCount)
                        + '</div><div class="influence_head">'
                        + formatCurrency(list[i].urlNoVipCount)
                        +'</div></li>'
                }
                $("#influence .peopleContent ul").append(li);
                $("#influence .peopleContent").show();
            }else {
                $("#influence .peopleContent").hide();
                $("#influence .peopleError").show();
            }
        }else {
            //console.log(data.message);
        }
        $("#influence .list_box .listLoading_box").hide();
    });
}
//优惠券分析图表，列表数据，获取店铺，优惠券
function getcouponChart() {
    var activity_code=sessionStorage.getItem("activity_code");
    var param={"activity_code":activity_code};
    $("#coupon .task_box .listLoading_box").show();
    oc.postRequire("post","/activityAnaly/couponUser","",param,function (data) {//饼图
        if(data.code==0){
            var msg = JSON.parse(data.message);
            msg = msg.message;
            var notreceive = msg.vip_number-msg.vip_receive<0?0:msg.vip_number-msg.vip_receive;
            var receive = msg.vip_receive;
            var perReceive =  msg.receive_ratio;
            var perUse = msg.consume_ratio;
            var useVip = msg.vip_consume;
            $("#couponUseper").text("使用占比"+perUse);
            $("#couponReceiveper").text("领取占比"+perReceive);
            couponChart(receive,notreceive,useVip);
            $("#coupon_vipUse").text(formatCurrency(msg.vip_consume));
            $("#coupon_vipGet").text(formatCurrency(msg.vip_receive));
            $("#coupon_vipTarget").text(formatCurrency(msg.vip_number));
        }else {
            //console.log(data.message);
        }
        $("#coupon .task_box .listLoading_box").eq(0).hide();
    });
    oc.postRequire("post","/activityAnaly/couponView","",param,function (data) {//柱状图
        if(data.code==0){
            var msg = JSON.parse(data.message);
            var list = msg.list;
            list = list.voucher_chat_list;
            var date = [];
            var receiveCount = [];
            var useCount = [];
            for(var i=0;i<list.length;i++){
                date.push(list[i].time_id);
                receiveCount.push(list[i].bind_count);
                useCount.push(list[i].verify_count);
            }
            couponbarChart(date,receiveCount,useCount);
        }else {
            //console.log(data.message);
        }
        $("#coupon .task_box .listLoading_box").eq(1).hide();
    });
}
function getcouponList() {
    $("#coupon .list_box .listLoading_box").show();
    var activity_code=sessionStorage.getItem("activity_code");
    var store_code=$("#choose_store_input").attr("data-code");
    var coupon_code=$("#choose_coupon_input").attr("data-code");
    var param={"activity_code":activity_code,"page_num":Num,"page_size":"20","store_code":store_code,"coupon_code":coupon_code};
    oc.postRequire("post","/activityAnaly/couponList","",param,function (data) {
        if(data.code==0){
            var msg = JSON.parse(data.message);
            var list = msg.voucher_chat_list;
            var next = msg.isnext;
            allData = msg.total;
            next == 'true' ? has_next_list = true : has_next_list = false;
            var li = '';
            if(list.length>0){
                for(var i=0;i<list.length;i++){
                    var a=(Num-1)*20+i+1;
                    li+=' <li class="people_title"> <div class="people_title_order">'
                        + a
                        + '</div><div class="people_title_shop ellipsis" title="'+list[i].store_name+'">'
                        + list[i].store_name
                        + '</div><div class="people_title_count" title="'+list[i].verify_vip+'">'
                        + formatCurrency(list[i].verify_vip)
                        + '</div><div class="people_title_count" title="'+list[i].verify_count+'">'
                        + formatCurrency(list[i].verify_count)
                        + '</div><div class="people_title_count" title="'+list[i].vip_statis.num_coupon_card+'">'
                        + formatCurrency(list[i].vip_statis.num_coupon_card)
                        + '</div><div class="people_title_count" title="'+list[i].vip_statis.verify_coupon_offset_price+'">'
                        + formatCurrency(list[i].vip_statis.verify_coupon_offset_price)
                        + '</div><div class="people_title_count" title="'+list[i].vip_statis.verify_coupon_total_price+'">'
                        + formatCurrency(list[i].vip_statis.verify_coupon_total_price)
                        + '</div><div class="people_title_count" title="'+list[i].vip_statis.verify_coupon_price+'">'
                        + formatCurrency(list[i].vip_statis.verify_coupon_price)
                        + '</div><div class="people_title_count" title="'+list[i].vip_statis.verify_coupon_dis+'" style="width: 100px">'
                        + list[i].vip_statis.verify_coupon_dis
                        + '</div><div class="people_title_detadis">'
                        + '<div class="data_detail couponVipdetail" data-info='+JSON.stringify(list[i].verify_coupon_analy)+'><div class="detail">详情<span class="icon-ishop_8-03" style="color:#5f8bc8">'
                        + '</span></div></div></div></li>';
                }
                $("#coupon .peopleContent ul").append(li);
                $("#coupon .peopleContent").show();
                $("#coupon .peopleError").hide();
            }else {
                $("#coupon .peopleContent").hide();
                $("#coupon .peopleError").show();
            }
        }else {
            //console.log(data.message);
        }
        $("#coupon .list_box .listLoading_box").hide();
    });
}
function getStore(){
    var param={};
    param.activity_code=sessionStorage.getItem('activity_code');
    oc.postRequire("post","/vipActivity/getStoreList","",param,function (data) {
        var activity_store_code=JSON.parse(data.message).storeList;
        if(activity_store_code.length>0){
            var li="";
            for(var i=0;i<activity_store_code.length;i++){
                li+="<li title='"+activity_store_code[i].store_name+"' data-code='"+activity_store_code[i].store_code+"'>"+activity_store_code[i].store_name+"</li>";
            }
            $("#store_select").html("<li data-code=''>全部</li>"+li);
        }
    });
}
function getActivityCoupons() {
    var param={};
    param.activity_code=sessionStorage.getItem('activity_code');
    oc.postRequire("post","/vipActivity/detail/selActivityByCodeAndName","",param,function (data) {
        var coupon_type = JSON.parse(data.message).coupon;
        var li = "";
        for(var j=0;j<coupon_type.length;j++){
            li+="<li title='"+coupon_type[j].coupon_name+"' data-code='"+coupon_type[j].coupon_code+"'>"+coupon_type[j].coupon_name+"</li>";
        }
        $("#coupon_select").html("<li data-code=''>全部</li>"+li);
    });
}
//传播分析图表，列表数据
function getspreadChart() {
    var activity_code=sessionStorage.getItem("activity_code");
    var param={"activity_code":activity_code};
    $("#spread .task_box .listLoading_box").show();
    oc.postRequire("post","/activityAnaly/noticeCard","",param,function (data) {//会员开类型点击次数
        if(data.code==0){
            var message = JSON.parse(data.message);
            var list = message.list;
            var legend = [];
            for(var i=0;i<list.length;i++){
                var obj={
                    name:"",
                    value:[]
                };
                obj.name=list[i].vip_card_type_name;
                obj.value.push(list[i].count.one);
                obj.value.push(list[i].count.two);
                obj.value.push(list[i].count.three);
                obj.value.push(list[i].count.four);
                obj.value.push(list[i].count.five);
                obj.value.push(list[i].count.more);
                legend.push(obj);
            }
            var id = "cardTypeChart";
            var y = ['1次','2次','3次','4次','5次','5次以上'];
            spreadbarChart(id,legend,y);
        }else {
            //console.log(data.message);
        }
        $("#spread .task_box .listLoading_box").eq(0).hide();
    });
    oc.postRequire("post","/activityAnaly/noticeStore","",param,function (data) {//店铺点击次数
        if(data.code==0){
            id = "shopChart";
            var message = JSON.parse(data.message);
            var list = message.list;
            var legend = [{name:"1次",value:[]},{name:"2次",value:[]},{name:"3次",value:[]},{name:"4次",value:[]},{name:"5次",value:[]},{name:"5次以上",value:[]}];
            var y = [];
            for(var i=0;i<list.length;i++){
                y.push(list[i].storeName);
                legend[0].value.push(list[i].One);
                legend[1].value.push(list[i].Two);
                legend[2].value.push(list[i].Three);
                legend[3].value.push(list[i].Four);
                legend[4].value.push(list[i].Five);
                legend[5].value.push(list[i].More);
            }
            // var legend = ['1次','2次','3次','4次','5次','5次以上'];
            spreadbarChart(id,legend,y);
        }else {
            //console.log(data.message);
        }
        $("#spread .task_box .listLoading_box").eq(1).hide();
    });
}
function getspreadList() {
    $("#spread .list_box .listLoading_box").show();
    var userName=$("#spreadUsername").val().trim();
    var storeName=$("#spreadStorename").val().trim();
    var activity_code=sessionStorage.getItem("activity_code");
    var type=$("#spread .doType_active").attr("data-type");
    var param={"activity_code":activity_code,"type":type,"page_num":Num,"page_size":"20","store_name":storeName,"user_name":userName};
    oc.postRequire("post","/activityAnaly/noticeList","",param,function (data) {
        if(data.code==0){
            var msg = JSON.parse(data.message);
            var next = msg.hasNext;
            var list = msg.list;
            var li = '';
            next ? has_next_list = true : has_next_list = false;
            if(list.length>0){
                for(var i=0;i<list.length;i++){
                    var name = list[i].storeName;
                    if(name==undefined){
                        name = list[i].userName;
                    }
                    var a=(Num-1)*20+i+1;
                    li+='<li class="people_title"><div class="people_title_order">'
                        + a
                        + '</div><div class="people_title_shop ellipsis" title="'+name+'">'
                        + name
                        + '</div><div class="people_title_num ellipsis" title="'+list[i].shareCount+'">'
                        + formatCurrency(list[i].shareCount)
                        + '</div><div class="people_title_num ellipsis" title="'+list[i].openCount+'">'
                        + formatCurrency(list[i].openCount)
                        + '</div><div class="people_title_num ellipsis" title="'+list[i].One+'">'
                        + formatCurrency(list[i].One)
                        + '</div><div class="people_title_num ellipsis" title="'+list[i].Two+'">'
                        + formatCurrency(list[i].Two)
                        + '</div><div class="people_title_num ellipsis" title="'+list[i].Three+'">'
                        + formatCurrency(list[i].Three)
                        + '</div><div class="people_title_num ellipsis" title="'+list[i].Four+'">'
                        + formatCurrency(list[i].Four)
                        + '</div><div class="people_title_num ellipsis" title="'+list[i].Five+'">'
                        + formatCurrency(list[i].Five)
                        + '</div><div class="people_title_num ellipsis" title="'+list[i].More+'">'
                        + formatCurrency(list[i].More)
                        +'</div></li>'
                }
                $("#spread .peopleContent ul").append(li);
                $("#spread .peopleContent").show();
                $("#spread .peopleError").hide();
            }else {
                $("#spread .peopleContent").hide();
                $("#spread .peopleError").show();
            }
        }else {
            //console.log(data.message);
        }
        $("#spread .list_box .listLoading_box").hide();
    });
}
//报名会员分析
function getSignUpView(){
    var activity_code=sessionStorage.getItem("activity_code");
    var param={"activity_code":activity_code};
    $("#page_signUp .task_box .listLoading_box").show();//加载遮罩
    oc.postRequire("post", '/activityAnaly/applyAnalyView',"0",param,function(data){ //柱状图
        if(data.code==0){
            var msg = JSON.parse(data.message).apply_view;
            signUpAxis(msg,"signUp");//柱状图
        }else{
            //console.log(data.message);
        }
        $("#page_signUp .task_box .listLoading_box").eq(1).hide();
    });
    oc.postRequire("post", '/activityAnaly/applyAnalyRate',"0",param,function(data){ //饼图
        if(data.code==0){
            var msg = JSON.parse(data.message);
            signUpPie(msg,"signUp");//饼图
        }else{
            //console.log(data.message);
        }
        $("#page_signUp .task_box .listLoading_box").eq(0).hide();
    });

}
function getSignUpList(){
    $('#chart_loading').show();
    var activity_code=sessionStorage.getItem("activity_code");
    var storeCode = $("#signUpStoreCode").val().trim();
    var storeName = $("#signUpStoreName").val().trim();
    var param={"activity_code":activity_code,"page_num":Num,"page_size":"20","store_code":storeCode,"store_name":storeName};
    oc.postRequire("post", '/activityAnaly/applyAnalyList',"0",param,function(data){
        if(data.code==0){
            var msg = JSON.parse(data.message);
            has_next_list = msg.hasNext;
            var list = JSON.parse(msg.list);
            setSignUpList(list)
        }else {
            //console.log(data.message);
        }
    });
}
function setSignUpList(list) {
    if (list.length == 0) {
        $("#page_signUp .peopleError").show();
        $('#chart_loading').hide();
        return;
    }
    var tempHTML1 = "<li data-vip='${vips}'> <span>${num}</span> <span title='${shopNo}'>${shopNo}</span><span title='${name}'>${name}</span> <span>${shopArea}</span> <span>${目标会员数}</span><span>${报名会员数}</span><span> <div class='sign_percent'><div style='width:${percent};'></div></div>${percent}<label class='btn_page_signUp' style='float:right;'> 详情 ></label> </span> </li>";
    var html = '';
    for (i = 0; i < list.length; i++) {
        var nowHTML = tempHTML1;
        var apply_detail = JSON.parse(list[i].apply_detail);
        apply_detail = apply_detail.map(function (cur,index) {
            var obj = {};
            if(cur.vip_name != undefined){
                obj.vip_name = cur.vip.vip_name;
                obj.vip_phone = cur.vip.vip_phone;
            }else {
                obj.vip_name = "";
                obj.vip_phone = cur.phone;
            }
            obj.modified_date = cur.modified_date;
            return obj;
        });
        nowHTML = nowHTML.replace('${num}', (Num-1)*20+i + 1);
        nowHTML = nowHTML.replace('${vips}', JSON.stringify(apply_detail));
        nowHTML = nowHTML.replace('${name}', list[i].store_name);//店铺名称
        nowHTML = nowHTML.replace('${name}', list[i].store_name);//店铺名称
        nowHTML = nowHTML.replace('${shopNo}', list[i].store_code);//店铺编号
        nowHTML = nowHTML.replace('${shopNo}', list[i].store_code);//店铺编号
        nowHTML = nowHTML.replace('${shopArea}', list[i].area_name == '' ? '无' : list[i].area_name);//所属区域
        nowHTML = nowHTML.replace('${目标会员数}', formatCurrency(list[i].target_vip_count));//目标会员数
        nowHTML = nowHTML.replace('${报名会员数}', formatCurrency(list[i].apply_count));//报名会员数
        nowHTML = nowHTML.replace('${percent}', list[i].rate);//百分比
        nowHTML = nowHTML.replace('${percent}', list[i].rate);//百分比
        html += nowHTML;
    }
    $('#signUpPeople .peopleContent ul').append(html);
    $('#chart_loading').hide();
}
//线上报名会员分析
function getOnlineView(){
    var activity_code=sessionStorage.getItem("activity_code");
    var param={"activity_code":activity_code};
    $("#online_apply .task_box .listLoading_box").show();//加载遮罩
    oc.postRequire("post", '/activityAnaly/onlineChat',"0",param,function(data){ //柱状图
        if(data.code==0){
            var msg = JSON.parse(data.message).list;
            signUpAxis(msg,"online");//柱状图
        }else{
            //console.log(data.message);
        }
        $("#online_apply .task_box .listLoading_box").eq(1).hide();
    });
    oc.postRequire("post", '/activityAnaly/onlineRate',"0",param,function(data){ //饼图
        if(data.code==0){
            var msg = JSON.parse(data.message);
            signUpPie(msg,"online");//饼图
        }else{
            //console.log(data.message);
        }
        $("#online_apply .task_box .listLoading_box").eq(0).hide();
    });

}
function getOnlineList(){
    $('#online_apply .listLoading_box').show();
    var type = $("#online_apply .doType_active").attr("data-type");
    var activity_code=sessionStorage.getItem("activity_code");
    var screen = '';
    if(type == "store"){
        screen = {"store_code":$("#onlineStoreCode").val().trim(),"store_name":$("#onlineStoreName").val().trim()};
    }else {
        screen = {
            "vip_name":$("#onlineVipName").val().trim(),
            "vip_cardno":$("#onlineVipCode").val().trim(),
            "vip_phone":$("#onlineVipPhone").val().trim(),
            "item_name":$("#onlineItem").val().trim(),
            "order_id":$("#onlinePayCode").val().trim(),
            "pay_type":$("#onlinePayType").val().trim() == "积分" ? "1" : $("#onlinePayType").val().trim() == "现金" ? "2" : "",
            'sign_status':$("#onlineSign").val().trim() == "是" ? "Y" : $("#onlineSign").val().trim() == "否" ? "N" : ""
        }
    }
    var param={"activity_code":activity_code,"page_num":Num,"page_size":"20","type":type,"screen":screen};
    oc.postRequire("post", '/activityAnaly/onlineList',"0",param,function(data){
        if(data.code==0){
            var msg = JSON.parse(data.message);
            has_next_list = msg.is_next;
            allData = msg.total;
            var list = JSON.parse(msg.list);
            setOnlineList(list,type);
        }
    });
}
function setOnlineList(list,type) {
    var html = '';
    if (list.length == 0) {
        $("#online_apply .peopleError").show();
        $("#online_apply .peopleContent ul").empty();
        $("#online_apply .listLoading_box").hide();
    }else {
        $("#online_apply .peopleError").hide();
    }
    if(type == 'store'){
        $("#online_apply").attr("class","online_store_class");
        var tempHTML1 = "<li data-vip='${vips}'> <span>${num}</span> <span title='${shopNo}'>${shopNo}</span><span title='${name}'>${name}</span> <span>${shopArea}</span> <span>${目标会员数}</span><span>${报名会员数}</span><span> <div class='sign_percent'><div style='width:${percent};'></div></div>${percent}<label class='btn_page_signUp' style='float:right;'> 详情 ></label> </span> </li>";
        for (i = 0; i < list.length; i++) {
            var nowHTML = tempHTML1;
            var apply_detail = JSON.parse(list[i].apply_detail);
            apply_detail = apply_detail.map(function (cur,index) {
                var obj = {};
                obj.vip_name = cur.vip_name;
                obj.vip_phone = cur.vip_phone;
                obj.modified_date = cur.modified_date;
                return obj;
            });
            nowHTML = nowHTML.replace('${num}', (Num-1)*20+i + 1);
            nowHTML = nowHTML.replace('${vips}', JSON.stringify(apply_detail));
            nowHTML = nowHTML.replace('${name}', list[i].store_name);//店铺名称
            nowHTML = nowHTML.replace('${name}', list[i].store_name);//店铺名称
            nowHTML = nowHTML.replace('${shopNo}', list[i].store_code);//店铺编号
            nowHTML = nowHTML.replace('${shopNo}', list[i].store_code);//店铺编号
            nowHTML = nowHTML.replace('${shopArea}', list[i].area_name == '' ? '无' : list[i].area_name);//所属区域
            nowHTML = nowHTML.replace('${目标会员数}', formatCurrency(list[i].target_vip_count));//目标会员数
            nowHTML = nowHTML.replace('${报名会员数}', formatCurrency(list[i].apply_vip_count));//报名会员数
            nowHTML = nowHTML.replace('${percent}', list[i].rate);//百分比
            nowHTML = nowHTML.replace('${percent}', list[i].rate);//百分比
            html += nowHTML;
        }
    }else if(type == "vip"){
        $("#online_apply").attr("class","online_vip_class");
        var tempHTML1 = "<li data-vip='${vips}'> <span>${num}</span> <span title='${vipNo}'>${vipNo}</span><span title='${name}'>${name}</span> <span>${手机号}</span> <span>${报名项目}</span><span>${支付方式}</span><span>${支付金额}</span><span>${支付单号}</span><span>${是否签到}</span><span>${签到时间}</span><span><label class='btn_page_signUp'> 详情 ></label></label></span></li>";
        for (i = 0; i < list.length; i++) {
            var nowHTML = tempHTML1;
            // var apply_detail = JSON.parse(list[i].apply_info);
            var apply_detail = JSON.parse(list[i].apply_info);
            nowHTML = nowHTML.replace('${num}', (Num-1)*20+i + 1);
            nowHTML = nowHTML.replace('${vips}', JSON.stringify(apply_detail));
            nowHTML = nowHTML.replace('${vipNo}', list[i].cardno);
            nowHTML = nowHTML.replace('${vipNo}', list[i].cardno);
            nowHTML = nowHTML.replace('${name}', list[i].vip_name);
            nowHTML = nowHTML.replace('${name}', list[i].vip_name);
            nowHTML = nowHTML.replace('${手机号}', list[i].vip_phone);
            nowHTML = nowHTML.replace('${报名项目}', list[i].item_name);
            nowHTML = nowHTML.replace('${支付方式}', list[i].pay_type)
            nowHTML = nowHTML.replace('${支付金额}', formatCurrency(list[i].fee));
            nowHTML = nowHTML.replace('${支付单号}', list[i].order_id);
            nowHTML = nowHTML.replace('${是否签到}', list[i].sign_status);
            nowHTML = nowHTML.replace('${签到时间}', list[i].sign_date == '' ? '无' : list[i].sign_date);
            html += nowHTML;
        }
    }
    Num == 1 ? $('#online_apply .peopleContent ul').html(html):$('#online_apply .peopleContent ul').append(html);
    $("#online_apply .listLoading_box").hide()
}
$("#page_signUp").on("click",".btn_page_signUp",function(){
    btn_page_signUp_1($(this).parent().parent())
});
$("#online_apply").on("click",".btn_page_signUp",function(){
    if($("#online_apply").hasClass("online_store_class")){
        getOnlineVipDetails($(this).parent().parent());
    }else {
        getOnlineApply($(this).parent().parent())
    }
});
//报名会员分析打开人员详情列表
function btn_page_signUp_1(self) {
    var shop = self.find('span').eq(2).text();
    var area = self.find('span').eq(3).text();
    var vips = JSON.parse(self.attr("data-vip"));
    $('#table_signUp #table_shopName').text(shop);
    $('#table_signUp #table_shopName').attr('title',shop);
    $('#table_signUp #table_area').text(area);
    $('#table_signUp #table_area').attr('title',area);
    $('#table_signUp').show();
    $('#table_signUp_area').empty();
    if(vips.length != 0){
        var tempHTML = '';
        for(var i = 0; i < vips.length; i++){
            var n = i+1;
            tempHTML += '<li> <span>'+n+'</span> <span>'+vips[i].vip_name+'</span> <span>'+vips[i].vip_phone+'</span> <span>'+vips[i].modified_date+'</span> </li>';
        }
        $('#table_signUp_area').html(tempHTML);
    }else {
        var html = '<li class="peopleError" style="background-color: white;"> <div style="margin-top: 120px;text-align: center"> 没有相关数据 </div> </li>'
        $('#table_signUp_area').html(html);
    }
}
function getOnlineVipDetails(self) {
    var shop = self.find('span').eq(2).text();
    var area = self.find('span').eq(3).text();
    var vips = JSON.parse(self.attr("data-vip"));
    $('#online_vip_detail_wrap .shopName').text(shop);
    $('#online_vip_detail_wrap .shopName').attr('title',shop);
    $('#online_vip_detail_wrap .shopArea').text(area);
    $('#online_vip_detail_wrap .shopArea').attr('title',area);
    $('#online_vip_detail_wrap').show();
    if(vips.length != 0){
        var tempHTML = '';
        for(var i = 0; i < vips.length; i++){
            var n = i+1;
            tempHTML += '<li> <span>'+n+'</span> <span>'+vips[i].vip_name+'</span> <span>'+vips[i].vip_phone+'</span> <span>'+vips[i].modified_date+'</span> </li>';
        }
        $('#online_vip_detail_wrap .vip_detail').html(tempHTML);
    }else {
        var html = '<li class="peopleError" style="background-color: white;"> <div style="margin-top: 120px;text-align: center"> 没有相关数据 </div> </li>'
        $('#online_vip_detail_wrap .vip_detail').html(html);
    }
}
function getOnlineApply(self) {
    $("#online_apply_detail_wrap").show();
    var temp = "";
    var list = JSON.parse(self.attr("data-vip"));
    list.map(function (cur,index) {
        temp += "<div>"+cur.param_desc+" : "+cur.value+"</div>";
        return temp;
    });
    $("#online_apply_detail_wrap .modify_content").html(temp)
}
//报名会员分析柱状图
function signUpAxis(msg,type) {
    var id = type == "online" ? 'axis_online_apply' : "axis_signUp";
    var myChart = echarts.init(document.getElementById(id));
    var dateArr = [];
    var countArr = [];
    var dataZoom = [];
    for(i=0;i<msg.length;i++){
        dateArr.push(msg[i].date);
        countArr.push(msg[i].count);
    }
    if(dateArr.length>8){
        dataZoom = [
            {
                type: 'slider',
                show: true,
                start:0,
                end: 100,
                handleSize: 8,
                height:15,
                bottom:10
            }
        ]
    }
    option = {
        tooltip : {
            trigger: 'axis',
            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                type : ''        // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        legend: {
            right:"5%",
            data:[                 //柱名称
                {
                    name:'报名人数',
                    icon:'rect'
                }]
        },
        dataZoom:dataZoom,
        grid: {
            //left: '3%',
            //right: '4%',
            bottom: 50
            //containLabel: true
        },
        xAxis : [
            {
                type : 'category',
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                data : dateArr
            }
        ],
        yAxis : [
            {
                type : 'value',
                //minInterval : 1,
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                splitLine: {
                    lineStyle: {
                        type: 'dashed'
                    }
                }
            }
        ],
        series : [
            {
                name:'报名人数',
                barMaxWidth:'15px',
                type:'bar',
                itemStyle:{
                    normal:{
                        color:"#6dc1c8"
                    }
                },
                data:countArr
            }
        ]
    };
    myChart.setOption(option);
    window.addEventListener("resize", function () {
        myChart.resize();
    });
}
function signUpPie(msg,type) {
    var target_vip_count = msg.target_vip_count; //目标会员数
    if(type == "online"){
        var apply_count = msg.enroll_count; //报名人数
        $('#online_apply .shoppers').eq(0).text(formatCurrency(apply_count));
        $('#online_apply .target').text(formatCurrency(target_vip_count));
        var myChart = echarts.init(document.getElementById('pie_online_apply'));
    }else {
        var apply_count = msg.apply_count; //报名人数
        var apply_vip_count = msg.apply_vip_count; //报名会员数
        $('#page_signUp .shoppers').eq(0).text(formatCurrency(apply_count));
        $('#page_signUp .shoppers').eq(1).text(apply_vip_count);
        $('#page_signUp .target').text(formatCurrency(target_vip_count));
        var myChart = echarts.init(document.getElementById('pie_signUp'));
    }
    option = {
        color: ['#7bc7cd', '#eaeaea'],
        tooltip: {
            show:false,
            trigger: 'item',
            formatter: "{a} <br/>{b}: {c} ({d}%)"
        },
        legend: {
            show:false,
            data:['报名人数','剩余']
        }, grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        series: [
            {
                name:'报名人数占比',
                type:'pie',
                radius: ['55%', '70%'],
                avoidLabelOverlap: false,
                itemStyle: {
                    normal:{
                        textStyle: {
                            color: '#7bc7cd',
                            fontSize: 25
                        }
                    }
                },
                labelLine: {
                    normal: {
                        show: false
                    }
                },
                emphasis : {
                    label : {
                        show : true,
                        position : 'center',
                        textStyle : {
                            fontSize : '24',
                            fontWeight : 'bold'
                        }
                    }
                },
                data:[
                    {value:apply_count, name:'报名人数',itemStyle: {
                        normal: {
                            label: {
                                show: true,
                                position: 'center',
                                formatter: '{d}%',
                                textStyle: {
                                    fontSize:18
                                }
                            },
                            labelLine : {
                                show : false
                            }
                        }
                    }},
                    {value:target_vip_count - apply_count, name:'未完成',itemStyle: {
                        normal: {
                            label : {
                                show : false,
                                position : 'center'
                            },
                            emphasis: {
                                color: '#eaeaea'
                            }
                        }
                    }}
                ]
            }
        ]
    }
    myChart.setOption(option);
    window.addEventListener("resize", function () {
        myChart.resize();
    });
}
//查看活动页面
$(".action_openWeb").click(function () {
    var activity_code=sessionStorage.getItem("activity_code");
    var command="/api/activity/openUrl?activity_code="+activity_code;
    whir.loading.add("",0.5);
    $.ajax({
        url: command,
        type: "get",
        dataType: 'json',
        timeout: 120000,
        async:false,
        success:function(data){
            whir.loading.remove();
            if(data.code==0){
                window.open("http://" + window.location.host + "/goods/mobile/activity_web.html?activity_code="+activity_code);
            }else{
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content:data.message
                });
            }
        },
        error:function(data){
            whir.loading.remove();
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content:"操作失败"
            });
        }
    });
});
//查看活动设置
$('.action_set').click(function () {
    $('#activitySetPage').show();
    $('#activitySet').show();
    $('#activitySet').css('height','auto');
});
$("#toClose").click(function () {
    $('#activitySetPage').hide();
});
//取消下拉框
$(document).on('click', function (e) {
    if(e.target.id=='cancel_box'){return};
    if($(e.target).parents('#cancel_box').length==0)$('.action_tip').hide();
});
$('.title_action').click(function () {//活动左上角设置
    $('.action_tip').toggle();
});
//滚动分页
$("#analyze_wrap .people").unbind("scroll").bind("scroll",function(){
    var offsetHeight = $(this).height(),
        scrollHeight = $(this)[0].scrollHeight,
        scrollTop = $(this)[0].scrollTop;
    var type = $('#page_achievement .btnSecond span').eq(0).attr('class') =='doType_active'?'store':'user';
    if(scrollTop + offsetHeight >= scrollHeight && scrollTop != 0){
        if(has_next_list){
            Num++;
        }else{
            return
        }
        var id = $(this).parents(".list_box").parent().attr("id");
        switch (id) {
            case "spread" : getspreadList();break;
            case "coupon" :getcouponList();break;
            case "page_achievement" :achievementList(type);break;
            case "influence" : getUserList();break;
            case "customer" : getCustomerList();break;
            case "vip_effect_content" : vipDetailList();break;
            case "page_signUp" :getSignUpList();break;
            case "online_apply":getOnlineList();break;
        }
    }
});
//线上活动筛选
$("#online_apply").on("click",".doType",function () {
    var type=$(this).attr("data-type");
    $(this).attr("class","doType_active");
    if(type=="vip"){
        $("#online_apply .people_title_temp li").html('<span>序号</span> <span>会员卡号</span><span>会员名称</span><span>手机号</span> <span>报名项目</span><span>支付方式</span><span>支付金额</span><span>支付单号</span><span>是否签到</span><span>签到时间</span><span>报名资料</span>');
        $(this).prev().attr("class","doType");
        $("#online_apply .inputs>ul").eq(1).show();
        $("#online_apply .inputs>ul").eq(0).hide();
    }else {
        $("#online_apply .people_title_temp li").html('<span>序号</span> <span>店铺编号</span><span>店铺名称</span><span>所属店铺群组</span> <span>目标会员数</span><span>报名会员数</span><span>报名会员占比</span>');
        $("#online_apply .inputs>ul").eq(0).show();
        $("#online_apply .inputs>ul").eq(1).hide();
        $(this).next().attr("class","doType");
    }
    Num = 1;
    getOnlineList();
});
$("#online_apply .inputs input").keydown(function (e) {
    var event = e || window.event;
    if(event.keyCode == 13){
        Num = 1;
        getOnlineList();
    }
});
$("#onlineEnter").click(function () {
    Num = 1;
    getOnlineList();
});
$("#online_apply").on("click",".select_input",function () {
    $(this).next(".screen_select").toggle();
});
$("#online_apply").on("mousedown",".screen_select li",function () {
    $(this).parents(".screen_select").prev(".select_input").val($(this).text());
});
$("#online_apply").on("blur",".select_input",function () {
    $(this).next(".screen_select").hide();
});
//导购影响分筛选
$("#influence .inputs input").keydown(function (e) {
    var event = e || window.event;
    if(event.keyCode == 13){
        Num = 1;
        $("#influence .peopleContent ul").empty();
        getUserList();
    }
});
//意向客户影响筛选
$("#customer .inputs input").keydown(function (e) {
    var event = e || window.event;
    if(event.keyCode == 13){
        Num = 1;
        $("#customer .peopleContent ul").empty();
        getCustomerList();
    }
});
$("#customer").on("click",".doType",function () {
    var type=$(this).attr("data-type");
    $(this).attr("class","doType_active");
    if(type=="user"){
        $("#customer_store").val("");
        $("#customer_storeCode").val("");
        $(this).prev().attr("class","doType");
        $("#customer_user").parent().show();
        $("#customer_userCode").parent().show();
        $("#customer_storeCode").parent().hide();
        $("#customer_store").parent().hide();
    }else {
        $("#customer_userCode").val("");
        $("#customer_user").val("");
        $("#customer_user").parent().hide();
        $("#customer_userCode").parent().hide();
        $("#customer_storeCode").parent().show();
        $("#customer_store").parent().show();
        $(this).next().attr("class","doType");
    }
    Num = 1;
    $("#customer .peopleContent ul").empty();
    getCustomerList();
});
//传播分析筛选
$("#spread .inputs input").keydown(function (e) {
    var event = e || window.event;
    if(event.keyCode == 13){
        Num = 1;
        $("#spread .peopleContent ul").empty();
        getspreadList();
    }
});
$("#spread").on("click",".doType",function () {
    var type=$(this).attr("data-type");
    $(this).attr("class","doType_active");
    if(type=="user"){
        $("#spreadStorename").val("");
        $("#spread .people_head div:nth-child(2)").text("员工姓名");
        $(this).prev().attr("class","doType");
        $("#spreadStorename").parent("li").hide();
        $("#spreadUsername").parent("li").show();
    }else {
        $("#spreadUsername").val("");
        $("#spread .people_head div:nth-child(2)").text("店铺名称");
        $(this).next().attr("class","doType");
        $("#spreadStorename").parent("li").show();
        $("#spreadUsername").parent("li").hide();
    }
    Num = 1;
    $("#spread .peopleContent ul").empty();
    getspreadList();
});
//优惠券分析筛选
$("#coupon .inputs input").keydown(function (e) {
    var event = e || window.event;
    if(event.keyCode == 13){
        Num = 1;
        $("#coupon .peopleContent ul").empty();
        getcouponList();
    }
});
//线下活动-报名会员-筛选（回车事件）
$("#page_signUp .inputs input").keydown(function (e) {
    var event = e || window.event;
    if(event.keyCode == 13){
        Num = 1;
        $("#page_signUp .peopleContent ul").empty();
        getSignUpList();
    }
});
//意向客户分析详情列表
$("#customer").on("click","li div.data_detail",function () {
    $("#customer_detail").show();
    var type=$("#customer .doType_active").attr("data-type");
    var param=JSON.parse($(this).attr("data-info"));
    var width=parseInt(param.proportion)+"%";
    var list=param.vipList;
    var html="";
    $("#customer_detail .s6").css("width",width);
    $("#customer_detail .s3").text(param.area_name);
    $("#customer_detail .s7").text(width);
    if(type=="store"){
        $("#customer_detail .customer_title").text("店铺意向客户列表");
        $("#customer_detail .s1").text("店铺:"+param.store_name);
        $("#customer_detail .s2").text("");
        $("#customer_detail .s4").text("");
    }else if(type=="user"){
        $("#customer_detail .s1").text("员工:"+param.user_name);
        $("#customer_detail .s2").text(param.cardno);
        $("#customer_detail .s4").text(param.store_name);
        $("#customer_detail .customer_title").text("员工意向客户列表");
        $("#customer_detail .s4").text(param.store_name);
    }
    for(var i=0;i<list.length;i++){
        var n=i+1;
        html+="<tr><td>"+n+"</td><td>"
            +list[i].vip_name
            +"</td><td>"
            +list[i].vip_phone
            +"</td><td>"
            +list[i].cardno
            +"</td><td>"
            +list[i].vip_card_type
            +"</td></tr>"
    }
    $("#customer_detail tbody").html(html);
});
$("#customerDetails_back").click(function () {
    $("#customer_detail").hide();
});
//券使用拥挤详情
$("#coupon").on("click",".couponVipdetail",function () {
    $("#couponVip").show();
    var store_name = $(this).parents(".people_title").children("div:nth-child(2)").text();
    var coupon_name = $(this).parents(".people_title").children("div:nth-child(3)").text();
    var param = JSON.parse($(this).attr("data-info"));
    var tr = "";
    for(var i=0;i<param.length;i++){
        var a=i+1;
        tr += "<tr><td>"+a+"</td><td>"+param[i].vip_name+"</td><td>"+param[i].vip_card_no+"</td><td>"+param[i].vip_phone+"</td><td>"+param[i].use_count+"</td></tr>";
    }
    $("#couponVip .s1").text("店铺:"+store_name);
    // $("#couponVip .s2").text("优惠券:"+coupon_name);
    $("#couponVip tbody").html(tr);
});
$("#coupon").on("click",".couponUsedetail",function () {
    $("#couponUse").show();
    var store_name = $(this).parents(".people_title").children("div:nth-child(2)").text();
    var coupon_name = $(this).parents(".people_title").children("div:nth-child(3)").text();
    var param = JSON.parse($(this).attr("data-info"));
    var span_head = '<span class="color5f8bc8">'+param.coupon_usage+'</span><span class="color6cc1c8">'+param.num_coupon_card+'</span><span class="color6cc1c8">'+param.num_coupon_vip+'</span><span class="color5f8bc8">'+param.verify_coupon_price+'</span><span class="color5f8bc8">'+param.coupon_rate+'</span>';
    var span_footer = '<span class="color5f8bc8">'+param.verify_coupon_total_price+'</span><span class="color5f8bc8">'+param.verify_coupon_offset_price+'</span><span class="color6cc1c8">'+param.verify_coupon_dis+'</span><span class="colorddd">'+param.coupon_expired_count+'</span><span class="colorddd">'+param.coupon_expired+'</span>';
    $("#couponUse .s1").text("店铺:"+store_name);
    $("#couponUse .s2").text("优惠券:"+coupon_name);
    $("#couponUse .couponvipbox:first-child li:nth-child(2)").html(span_head);
    $("#couponUse .couponvipbox:last-child li:nth-child(2)").html(span_footer);
});
$("#couponUse .vip_status_btn").click(function () {
    $("#couponUse").hide();
});
$("#couponVip .vip_status_btn").click(function () {
    $("#couponVip").hide();
});
//优惠券分析店铺，券类型筛选
$(".coupon_inputs_box").on("click","input,i",function () {
    $(this).nextAll(".screen_select").toggle();
});
$(".coupon_inputs_box").on("click","li",function () {
    $(this).parents(".screen_select").prevAll("input").val($(this).text());
    $(this).parents(".screen_select").prevAll("input").attr("data-code",$(this).attr("data-code"));
    $(this).parents(".screen_select").hide();
    Num = 1;
    $("#coupon .peopleContent ul").empty();
    getcouponList();
});
$(".coupon_inputs_box").mouseleave(function () {
    $(this).children(".screen_select").hide();
});
//优惠券导出
$("#couponListExport").click(function () {
    $("#right_shift_all_t").trigger("click");
    $("#export_chooseCondition").show();
    $("#coupon_export_download").hide();
    $("#coupon_export_wrapper").show();
    $("#export_list").hide();
});
$("#X_points,#hide_export,#coupon_export_download .download_close").click(function () {
    $("#coupon_export_wrapper").hide();
});
$("#points_enter").click(function () {
    var param={};
    var list_html = "";
    var li=$("#file_list_right input[type='checkbox']").parents("li");
    var allPage=Math.ceil(allData/2000);
    var tablemanager=[];
    if(li.length=="0"){
        art.dialog({
            time: 1,
            zIndex: 10010,
            lock: true,
            cancel: false,
            content: '请把要导出的列移到右边'
        });
        return;
    }
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("data-name");
        var z=$(li[i]).children("span").html();
        var param1={"column_name":r,"show_name":z};
        tablemanager.push(param1);
    }
    $("#export_chooseCondition").hide();
    $("#export_list_all ul").html("");
    for(var a=1;a<allPage+1;a++){
        var start_num=(a-1)*2000 + 1;
        var end_num="";
        if (allData < a*2000 ){
            end_num = allData
        }else{
            end_num = a*2000
        }
        list_html+= '<li>'
            +'<span style="float: left">优惠券分析('+start_num+'~'+end_num+')</span>'
            +'<span class="export_list_btn" data-page="'+a+'">导出</span>'
            +'<span style="margin-right:10px;" class="states"></span>'
            +'</li>'
    }
    $("#export_list_all ul").html(list_html);
    $("#export_list").show();
    $("#export_list_all").scrollTop(0);
    $("#export_list_all .export_list_btn").click(function () {
        if($(this).hasClass("btn_active")){
            return
        }
        var self=$(this);
        var page=$(this).attr("data-page");
        $(this).next().text("导出中...");
        $(this).addClass("btn_active");
        param["page_size"]="20000";
        param['activity_code'] = sessionStorage.getItem("activity_code");
        param['store_code'] = $("#choose_store_input").attr("data-code");
        param['coupon_code'] = $("#choose_coupon_input").attr("data-code");
        param['page_num'] = 1;
        param["tablemanager"]=tablemanager;
        oc.postRequire("post","/activityAnaly/export_couponList","0",param,function(data){
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var path=message.path;
                path=path.substring(1,path.length-1);
                self.attr("data-path",path);
                self.next().text("导出完成");
                self.html("<a href='/"+path+"' style='display: inline-block;width: 100%;height: 100%;color: #FFFFFF'>下载</a>");
                self.css("backgroundColor","#637ea4");
            }else if(data.code=="-1"){
                self.removeClass("btn_active");
                self.next().text("导出失败");
            }
        })
    });
});
$("#coupon_export_download .cancel").click(function () {
    $("#export_list").show();
    $("#coupon_export_download").hide();
});
$("#to_zip").click(function(){
    var a=$("#export_list_all ul li a");
    var URL="";
    var param={};
    if(a.length==0){
        art.dialog({
            time: 1,
            zIndex: 10010,
            lock: true,
            cancel: false,
            content: '请先导出文件'
        });
        return;
    }
    for(var i=a.length-1;i>=0;i--){
        if(i>0){
            URL+=$(a[i]).attr("href")+","
        }else{
            URL+=$(a[i]).attr("href");
        }
    }
    param.url=URL;
    param.name="优惠券分析";
    whir.loading.add("","0.5");
    oc.postRequire("post","/vip/exportZip", "",param, function(data){
        if(data.code=="0"){
            var path=JSON.parse(data.message).path;
            path=path.substring(1,path.length-1);
            $("#coupon_export_download a").prop("href","/"+path);
            whir.loading.remove();
            $("#coupon_export_download").show();
        }
        if(data.code=="-1"){
            whir.loading.remove();
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "操作失败"
            });
        }
    })
});
//业绩导出
$("#achievementListExport").click(function () {
    var type = $("#page_achievement .doType_active").attr("data-type");
    var li = "";
    var li_2= "<li data-name='discount'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput10'/><label for='checkboxInput10'></label></div><span class='p15'>折扣</span></li>"
        + "<li data-name='trade_price'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput11'/><label for='checkboxInput11'></label></div><span class='p15'>客单价</span></li>"
        + "<li data-name='sale_price'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput12'/><label for='checkboxInput12'></label></div><span class='p15'>件单价</span></li>"
        + "<li data-name='relate_rate'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput13'/><label for='checkboxInput13'></label></div><span class='p15'>连带率</span></li>"
        + "<li data-name='activity_discount'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput14'/><label for='checkboxInput14'></label></div><span class='p15'>活动折扣</span></li>"
        + "<li data-name='activity_customer_unit_price'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput15'/><label for='checkboxInput15'></label></div><span class='p15'>活动客单价</span></li>"
        + "<li data-name='activity_pieces_unit_price'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput16'/><label for='checkboxInput16'></label></div><span class='p15'>活动件单价</span></li>"
        + "<li data-name='activity_joint_rate'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput17'/><label for='checkboxInput17'></label></div><span class='p15'>活动连带率</span></li>"
        + "<li data-name='vip_discount'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput18'/><label for='checkboxInput18'></label></div><span class='p15'>会销折扣</span></li>"
        + "<li data-name='vip_trade_price'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput19'/><label for='checkboxInput19'></label></div><span class='p15'>会销客单价</span></li>"
        + "<li data-name='vip_sale_price'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput20'/><label for='checkboxInput20'></label></div><span class='p15'>会销件单价</span></li>"
        + "<li data-name='vip_relate_rate'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput21'/><label for='checkboxInput21'></label></div><span class='p15'>会销连带率</span></li>"
        + "<li data-name='activity_sale'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput22'/><label for='checkboxInput22'></label></div><span class='p15'>活动件数</span></li>"
        + "<li data-name='activity_trade'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput23'/><label for='checkboxInput23'></label></div><span class='p15'>活动笔数</span></li>"
    if(type == "store"){
        li = "<li data-name='store_code'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput1'/><label for='checkboxInput1'></label></div><span class='p15'>店铺编号</span></li>"
            + "<li data-name='store_name'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput2'/><label for='checkboxInput2'></label></div><span class='p15'>店铺名称</span></li>"
            + "<li data-name='amt_trade'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput3'/><label for='checkboxInput3'></label></div><span class='p15'>总业绩</span></li>"
            + "<li data-name='activity_money'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput4'/><label for='checkboxInput4'></label></div><span class='p15'>活动金额</span></li>"
            + "<li data-name='vip_money'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput5'/><label for='checkboxInput5'></label></div><span class='p15'>会销金额</span></li>"
            + "<li data-name='all_new_vip'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput6'/><label for='checkboxInput6'></label></div><span class='p15'>会员总数</span></li>"
            + "<li data-name='activity_new_vip'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput7'/><label for='checkboxInput7'></label></div><span class='p15'>新增会员数</span></li>"
            + "<li data-name='num_sale'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput8'/><label for='checkboxInput8'></label></div><span class='p15'>销售件数</span></li>"
            + "<li data-name='num_trade'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput9'/><label for='checkboxInput9'></label></div><span class='p15'>成交笔数</span></li>";
    }else {
        li = "<li data-name='user_code'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput1'/><label for='checkboxInput1'></label></div><span class='p15'>员工编号</span></li>"
            + "<li data-name='user_name'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput2'/><label for='checkboxInput2'></label></div><span class='p15'>员工姓名</span></li>"
            + "<li data-name='store_name'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput3'/><label for='checkboxInput3'></label></div><span class='p15'>所属店铺</span></li>"
            + "<li data-name='store_code'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput4'/><label for='checkboxInput4'></label></div><span class='p15'>店铺编号</span></li>"
            + "<li data-name='amt_trade'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput5'/><label for='checkboxInput5'></label></div><span class='p15'>总业绩</span></li>"
            + "<li data-name='vip_money'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput5'/><label for='checkboxInput5'></label></div><span class='p15'>会销金额</span></li>"
            + "<li data-name='all_new_vip'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput6'/><label for='checkboxInput6'></label></div><span class='p15'>会员总数</span></li>"
            + "<li data-name='activity_new_vip'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput7'/><label for='checkboxInput7'></label></div><span class='p15'>新增会员数</span></li>"
            + "<li data-name='num_sale'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput6'/><label for='checkboxInput6'></label></div><span class='p15'>销售件数</span></li>"
            + "<li data-name='num_trade'><div class='checkbox1'><input type='checkbox' value='' name='test' class='check' id='checkboxInput8'/><label for='checkboxInput8'></label></div><span class='p15'>成交笔数</span></li>"
    }
    $("#file_list_r ul").empty();
    $("#file_list_l ul").empty();
    $("#file_list_l ul").append(li+li_2);
    $("#achievement_chooseCondition").show();
    $("#achievement_export_download").hide();
    $("#achievement_export_wrapper").show();
});
$(".close_achievement").click(function () {
    $(this).parents(".export_wrapper").hide();
});
$("#record_enter").click(function () {
    var param={};
    var li=$("#file_list_r input[type='checkbox']").parents("li");
    var tablemanager=[];
    if(li.length=="0"){
        art.dialog({
            time: 1,
            zIndex: 10010,
            lock: true,
            cancel: false,
            content: '请把要导出的列移到右边'
        });
        return;
    }
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("data-name");
        var z=$(li[i]).children("span").html();
        var param1={"column_name":r,"show_name":z};
        tablemanager.push(param1);
    }
    param['activity_code'] = sessionStorage.getItem("activity_code");
    param['type'] = $("#page_achievement .doType_active").attr("data-type");
    param["tablemanager"]=tablemanager;
    param["screen"]=page_achievement_screen;
    whir.loading.add("处理中,请稍后", 0.5);//加载等待框
    oc.postRequire("post", "/activityAnaly/export_achvList", "0", param, function (data) {
        whir.loading.remove();//移除加载框
        if (data.code == 0) {
            var message = JSON.parse(data.message);
            var path = message.path;
            path = path.substring(1, path.length - 1);
            $('#achievement_export_download .album_enter a').attr("href","/"+path);
            $("#achievement_chooseCondition").hide();
            $("#achievement_export_download").show();
            $('#achievement_export_download .album_enter a').click(function () {
                $("#achievement_export_wrapper").hide();
                $("#achievement_chooseCondition").show();
                $("#achievement_export_download").hide();
            });
        } else if (data.code == -1) {
            art.dialog({
                time: 1,
                zIndex: 10010,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    });
});
$("#achievement_export_download .cancel").click(function () {//返回到提交导出窗口
    $("#achievement_chooseCondition").show();
    $("#achievement_export_download").hide();
});
//线上活动导出
$("#onlineExportBtn").click(function () {
    var type = $("#online_apply .doType_active").attr("data-type");
    var li = "";
    if(type == "store"){
        li = "<li data-name='store_code'><div class='checkbox1'><input type='checkbox' name='test' class='check' id='checkboxInput1'/><label for='checkboxInput1'></label></div><span class='p15'>店铺编号</span></li>"
            + "<li data-name='store_name'><div class='checkbox1'><input type='checkbox' name='test' class='check' id='checkboxInput2'/><label for='checkboxInput2'></label></div><span class='p15'>店铺名称</span></li>"
            + "<li data-name='area_name'><div class='checkbox1'><input type='checkbox' name='test' class='check' id='checkboxInput3'/><label for='checkboxInput3'></label></div><span class='p15'>所属店铺群组</span></li>"
            + "<li data-name='target_vip_count'><div class='checkbox1'><input type='checkbox' name='test' class='check' id='checkboxInput4'/><label for='checkboxInput4'></label></div><span class='p15'>目标会员数</span></li>"
            + "<li data-name='apply_vip_count'><div class='checkbox1'><input type='checkbox' name='test' class='check' id='checkboxInput5'/><label for='checkboxInput5'></label></div><span class='p15'>报名会员数</span></li>"
            + "<li data-name='rate'><div class='checkbox1'><input type='checkbox' name='test' class='check' id='checkboxInput6'/><label for='checkboxInput6'></label></div><span class='p15'>报名会员占比</span></li>"
    }else {
        li = "<li data-name='cardno'><div class='checkbox1'><input type='checkbox' name='test' class='check' id='checkboxInput1'/><label for='checkboxInput1'></label></div><span class='p15'>会员卡号</span></li>"
            + "<li data-name='vip_name'><div class='checkbox1'><input type='checkbox' name='test' class='check' id='checkboxInput2'/><label for='checkboxInput2'></label></div><span class='p15'>会员名称</span></li>"
            + "<li data-name='vip_phone'><div class='checkbox1'><input type='checkbox' name='test' class='check' id='checkboxInput3'/><label for='checkboxInput3'></label></div><span class='p15'>手机号</span></li>"
            + "<li data-name='item_name'><div class='checkbox1'><input type='checkbox' name='test' class='check' id='checkboxInput4'/><label for='checkboxInput4'></label></div><span class='p15'>报名项目</span></li>"
            + "<li data-name='pay_type'><div class='checkbox1'><input type='checkbox' name='test' class='check' id='checkboxInput5'/><label for='checkboxInput5'></label></div><span class='p15'>支付方式</span></li>"
            + "<li data-name='fee'><div class='checkbox1'><input type='checkbox' name='test' class='check' id='checkboxInput5'/><label for='checkboxInput5'></label></div><span class='p15'>支付金额</span></li>"
            + "<li data-name='order_id'><div class='checkbox1'><input type='checkbox' name='test' class='check' id='checkboxInput6'/><label for='checkboxInput6'></label></div><span class='p15'>支付单号</span></li>"
            + "<li data-name='sign_status'><div class='checkbox1'><input type='checkbox' name='test' class='check' id='checkboxInput7'/><label for='checkboxInput7'></label></div><span class='p15'>是否签到</span></li>"
            + "<li data-name='sign_date'><div class='checkbox1'><input type='checkbox' name='test' class='check' id='checkboxInput7'/><label for='checkboxInput7'></label></div><span class='p15'>签到时间</span></li>"
            + "<li data-name='apply_info_format'><div class='checkbox1'><input type='checkbox' name='test' class='check' id='checkboxInput6'/><label for='checkboxInput6'></label></div><span class='p15'>报名资料</span></li>"
    }
    $("#online_export_wrapper .file_list_right ul").empty();
    $("#online_export_wrapper .file_list_left ul").html(li);
    $("#online_export_chooseCondition").show();
    $("#online_export_list").hide();
    $("#online_export_download").hide();
    $("#online_export_wrapper").show();
});
$("#online_export_submit").click(function () {
    var param={};
    var list_html = "";
    var li=$("#online_export_wrapper .file_list_right input[type='checkbox']").parents("li");
    var allPage=Math.ceil(allData/2000);
    var tablemanager=[];
    var type = $("#online_apply .doType_active").attr("data-type");
    if(li.length=="0"){
        art.dialog({
            time: 1,
            zIndex: 10010,
            lock: true,
            cancel: false,
            content: '请把要导出的列移到右边'
        });
        return;
    }
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("data-name");
        var z=$(li[i]).children("span").html();
        var param1={"column_name":r,"show_name":z};
        tablemanager.push(param1);
    }
    $("#online_export_chooseCondition").hide();
    for(var a=1;a<allPage+1;a++){
        var start_num=(a-1)*2000 + 1;
        var end_num="";
        if (allData < a*2000 ){
            end_num = allData
        }else{
            end_num = a*2000
        }
        list_html+= '<li>'
            +'<span style="float: left">报名会员('+start_num+'~'+end_num+')</span>'
            +'<span class="export_list_btn" data-page="'+a+'">导出</span>'
            +'<span style="margin-right:10px;" class="states"></span>'
            +'</li>'
    }
    $("#online_export_list_all ul").html(list_html);
    $("#online_export_list").show();
    $("#online_export_list").scrollTop(0);
    $("#online_export_list_all .export_list_btn").click(function () {
        if($(this).hasClass("btn_active")){
            return
        }
        var self=$(this);
        var page=$(this).attr("data-page");
        var screen = '';
        $(this).next().text("导出中...");
        $(this).addClass("btn_active");
        if(type == "store"){
            screen = {"store_code":$("#onlineStoreCode").val().trim(),"store_name":$("#onlineStoreName").val().trim()};
        }else {
            screen = {
                "vip_name":$("#onlineVipName").val().trim(),
                "vip_cardno":$("#onlineVipCode").val().trim(),
                "vip_phone":$("#onlineVipPhone").val().trim(),
                "item_name":$("#onlineItem").val().trim(),
                "order_id":$("#onlinePayCode").val().trim(),
                "pay_type":$("#onlinePayType").val().trim() == "积分" ? "1" : $("#onlineVipName").val().trim() == "现金" ? "2" : "",
                'sign_status':$("#onlineSign").val().trim() == "是" ? "Y" : $("#onlineVipName").val().trim() == "否" ? "N" : ""
            }
        }
        param["type"]=type;
        param["page_size"]="2000";
        param['activity_code'] = sessionStorage.getItem("activity_code");
        param['screen'] = screen;
        param['page_num'] = Num;
        param["tablemanager"]=tablemanager;
        oc.postRequire("post","/activityAnaly/onlineExportExecl","0",param,function(data){
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var path=message.path;
                path=path.substring(1,path.length-1);
                self.attr("data-path",path);
                self.next().text("导出完成");
                self.html("<a href='/"+path+"' style='display: inline-block;width: 100%;height: 100%;color: #FFFFFF'>下载</a>");
                self.css("backgroundColor","#637ea4");
            }else if(data.code=="-1"){
                self.removeClass("btn_active");
                self.next().text("导出失败");
            }
        })
    });
});
$("#online_to_zip").click(function(){
    var a=$("#online_export_list ul li a");
    var URL="";
    var param={};
    if(a.length==0){
        art.dialog({
            time: 1,
            zIndex: 10010,
            lock: true,
            cancel: false,
            content: '请先导出文件'
        });
        return;
    }
    for(var i=a.length-1;i>=0;i--){
        if(i>0){
            URL+=$(a[i]).attr("href")+","
        }else{
            URL+=$(a[i]).attr("href");
        }
    }
    param.url=URL;
    param.name="报名会员";
    whir.loading.add("","0.5");
    oc.postRequire("post","/vip/exportZip", "",param, function(data){
        if(data.code=="0"){
            var path=JSON.parse(data.message).path;
            path=path.substring(1,path.length-1);
            $("#online_export_download a").prop("href","/"+path);
            whir.loading.remove();
            $("#online_export_download").show();
        }
        if(data.code=="-1"){
            whir.loading.remove();
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "操作失败"
            });
        }
    })
});
$("#online_export_download .cancel").click(function () {
    $("#online_export_list").show();
    $("#online_export_download").hide();
});
$("#online_hide_export,#online_export_download .download_close").click(function () {
    $("#online_export_wrapper").hide();
});
//客户影响图表配置
function customerChart(intentVip,NointentVip) {
    var myChart = echarts.init(document.getElementById('customer_pie'));
    option = {
        color: ['#7bc7cd', '#eaeaea'],
        tooltip: {
            show:false,
            trigger: 'item',
            formatter: "{a} <br/>{b}: {c} ({d}%)"
        },
        legend: {
            show:false,
            data:['意向会员数','无意向会员数']
        },
        series: [
            {
                name:'意向会员数占比',
                type:'pie',
                radius: ['55%', '70%'],
                avoidLabelOverlap: false,
                labelLine: {
                    normal: {
                        show: false
                    }
                },
                data:[
                    {value:intentVip, name:'意向会员数',itemStyle: {
                        normal: {
                            label: {
                                show: true,
                                position: 'center',
                                formatter: '{d}%',
                                textStyle: {
                                    fontSize:18
                                }
                            },
                            labelLine : {
                                show : false
                            }
                        }
                    }},
                    {value:NointentVip, name:'无意向会员数',itemStyle: {
                        normal: {
                            label : {
                                show : false,
                                position : 'center'
                            }
                        },
                        emphasis: {
                            color: '#eaeaea'
                        }
                    }}
                ]
            }
        ]
    };
    myChart.setOption(option);
    window.addEventListener("resize", function () {
        myChart.resize();
    });
}
function customerBarchart(data) {
    var myChart = echarts.init(document.getElementById('customer_bar'));
    var data_count = [];
    var date=[];
    var dataZoom = [];
//        var data=data.reverse();
    for(var i=0;i<data.length;i++){
        date.push(data[i].date);
        data_count.push(data[i].count)
    }
    if(date.length>8){
        dataZoom = [
            {
                type: 'slider',
                show: true,
                start:0,
                end: 100,
                handleSize: 8,
                height:15,
                bottom:10
            }
        ]
    }
    var option = {
        grid: {
            bottom: 50
        },
        xAxis: {
            data: date,
            axisLabel: {
                inside: false,
                textStyle: {
                    color: '#000'
                }
            },
            axisTick: {
                show: false
            },
            axisLine: {
                show: false
            },
            z: 10
        },
        yAxis: {
            axisLine: {
                show: false
            },
            axisTick: {
                show: false
            },
            axisLabel: {
                textStyle: {
                    color: '#999'
                }
            },
            splitLine: {
                show: true,
                lineStyle: {
                    type: 'dashed'
                }
            }
        },
        dataZoom: dataZoom,
        tooltip: {
            show: true,
            position: 'top',
            formatter: '{c}',
            backgroundColor: '#9999cc',
            padding: [2, 8]
        },
        series: [
            {
                type: 'bar',
                barMaxWidth:'15px',
                itemStyle: {
                    normal: {
                        color: '#d8d8e1'
                    },
                    emphasis: {
                        color: '#9999cc'
                    }
                },
                barCategoryGap: '50%',
                data: data_count
            }
        ]
    };
    myChart.setOption(option);
    window.addEventListener("resize", function () {
        myChart.resize();
    });
}
//导购影响图表配置
function line_chart(x,count,click) {
    var myChart = echarts.init(document.getElementById('influence_line_chart')),dataZoom;
    if(x.length>8){
        dataZoom = [
            {
                type: 'slider',
                show: true,
                start:0,
                end: 100,
                handleSize: 8,
                height:15,
                bottom:0
            }
        ]
    }
    option = {
        title: {
            text: '链接点击统计',
            top:10,
            left:30,
            textStyle:{
                color:'#abb4c2',
                fontSize:12,
            }
        },
        tooltip : {
            trigger: 'axis'
        },
        dataZoom: dataZoom,
        legend: {
            data:[{
                name:'链接分享总数',
                icon:'rect',
                textStyle:{
                    backgroundColor:'#5f8bc8'
                }
            },{
                name:'链接总点击数',
                icon:'rect',
                textStyle:{
                    backgroundColor:'#9a9acc'
                }
            }],
            item:30,
            top:10,
            right:20,
            itemHeight:10
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '8%',
            containLabel: true
        },
        xAxis : [
            {
                type : 'category',
                boundaryGap : true,
//                    axisLabel: {
//                        interval: 1
//                    },
                max:'dataMax',
                axisTick:{
                    show:false
                },
                axisLine:{
                    show:false
                },
                data : x
            }
        ],
        yAxis : [
            {
                type : 'value',
                axisTick:{
                    show:false
                },
                splitLine:{
                    lineStyle:{
                        type:"dashed"
                    }
                },
                axisLine:{
                    show:false,
                }
            }
        ],
        series : [

            {
                name:'链接分享总数',
                type:'line',
                smooth:true,

                itemStyle:{
                    normal:{
                        color:'#5f8bc8',
                    }
                },
                data:count
            },

            {
                name:'链接总点击数',
                type:'line',

                itemStyle:{
                    normal:{
                        color:'#9a9acc'
                    }
                },
                data:click
            }
        ]
    };
    myChart.setOption(option);
    window.addEventListener("resize", function () {
        myChart.resize();
    });
}
function userChart(share,notShare) {
    (share==0&&notShare==0)?notShare=1:"";
    var myChart = echarts.init(document.getElementById('user_pie_chart'));
    option = {
        color: ['#7bc7cd', '#eaeaea'],
        tooltip: {
            show:false,
            trigger: 'item',
            formatter: "{a} <br/>{b}: {c} ({d}%)"
        },
        legend: {
            show:false,
            data:['已分享导购','未分享导购']
        },
        series: [
            {
                name:'已分享导购占比',
                type:'pie',
                radius: ['55%', '70%'],
                avoidLabelOverlap: false,
                labelLine: {
                    normal: {
                        show: false
                    }
                },
                data:[
                    {value:share, name:'已分享导购',itemStyle: {
                        normal: {
                            label: {
                                show: true,
                                position: 'center',
                                formatter: '{d}%',
                                textStyle: {
                                    fontSize:18
                                }
                            },
                            labelLine : {
                                show : false
                            }
                        }
                    }},
                    {value:notShare, name:'未分享导购',itemStyle: {
                        normal: {
                            label : {
                                show : false,
                                position : 'center'
                            }
                        },
                        emphasis: {
                            color: '#eaeaea'
                        }
                    }}
                ]
            }
        ]
    };
    myChart.setOption(option);
    window.addEventListener("resize", function () {
        myChart.resize();
    });
}
//优惠券分析图表配置
function couponChart(receive,notreceive,useVip) {
    var notUse=receive-useVip+notreceive;
    var myChart = echarts.init(document.getElementById('coupon_chart_pie'));
    option = {
        tooltip: {
            trigger: 'item',
            formatter: "{a} <br/>{b}: {c} ({d}%)"
        },
        grid:{
            top:"20%"
        },
        legend: {
            orient: 'vertical',
            x: 'left',
            data:['未领取会员','领取会员','使用会员']
        },
        series: [
            {
                name:'访问来源',
                type:'pie',
                selectedMode: 'single',
                radius: ['69%', '75%'],
                label: {
                    normal: {
                        show:false
                    }
                },
                labelLine: {
                    normal: {
                        show: false
                    }
                },
                data:[
                    {value:useVip, name:'使用占比',itemStyle:{
                        normal:{
                            color:"#4a5f7c"
                        }
                    }},
                    {value:notUse,label:{
                        normal:{
                            show:false
                        }
                    },itemStyle:{
                        normal:{
                            opacity:0
                        }
                    }}
                ],
                silent:true
            },
            {
                name:'访问来源',
                type:'pie',
                radius: ['80%', '90%'],
                label: {
                    normal: {
                        show:false
                    }
                },
                labelLine:{
                    normal:{
                        show:false
                    }
                },
                data:[
                    {
                        value:receive,
                        name:'领取占比',
                        itemStyle:{
                            normal:{
                                color:"#6cc1c8"
                            }
                        }
                    },
                    {
                        value:notreceive,
                        itemStyle:{
                            normal:{
                                color:"#eee"
                            }
                        },
                        label:{
                            normal:{
                                show:false
                            }
                        }
                    }
                ],
                silent:true
            }
        ]
    };
    myChart.setOption(option);
    window.addEventListener("resize", function () {
        myChart.resize();
    });
}
function couponbarChart(date,receiveCount,useCount) {
    var myChart = echarts.init(document.getElementById('couponbarChart')),dataZoom = [];
    if(date.length>8){
        dataZoom = [
            {
                type: 'slider',
                show: true,
                start:0,
                end: 100,
                handleSize: 8,
                height:15,
                bottom:0
            }
        ]
    }
    option = {
        tooltip : {
            trigger: 'axis',
            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        legend: {
            right:"5%",
            data:[
                {
                    name:'领取',
                    icon:'rect'
                },{
                    name:'使用',
                    icon:'rect'
                }]
        },
        grid: {
            left: '3%',
            right: '4%',
            bottom: '10%',
            containLabel: true
        },
        dataZoom: dataZoom,
        xAxis : [
            {
                type : 'category',
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                data : date
            }
        ],
        yAxis : [
            {
                type : 'value',
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                splitLine: {
                    lineStyle: {
                        type: 'dashed'
                    }
                }
            }
        ],
        series : [
            {
                name:'领取',
                barMaxWidth:'15px',
                type:'bar',
                itemStyle:{
                    normal:{
                        color:"#6dc1c8"
                    }
                },
                data:receiveCount
            },
            {
                name:'使用',
                type:'bar',
                barMaxWidth:'15px',
                itemStyle:{
                    normal:{
                        color:"#4a5f7c"
                    }
                },
                data:useCount
            }
        ]
    };
    myChart.setOption(option);
    window.addEventListener("resize", function () {
        myChart.resize();
    });
}
//通知传播分析图表配置
function spreadbarChart(id,legend,y) {
    var legendName = [];
    var series = [{
        name: '0次',
        type: 'bar',
        barWidth:'19',
        data: []
    }];
    for(var i=0;i<legend.length;i++){
        var obj={
            type: 'bar',
            stack: "数量",
            barWidth:'8'
        };
        obj.data = legend[i].value;
        obj.name = legend[i].name;
        legendName.push(legend[i].name);
        series.push(obj);
    }
    var dataZoom = [];
    if(y.length>6){
        dataZoom = [
            {
                type: 'slider',
                orient:"vertical",
                show: true,
                start:0,
                end:20,
                width:10,
                handleSize: '20%',
                height:150,
                bottom:40
            }
        ];
    }
    var myChart = echarts.init(document.getElementById(id));
    var option = {
        color:["#fff","#c1e7ed","#6c588f","#7074ad","#9999cc","#cbadd5","#e3a3c3"],
        tooltip : {
            trigger: 'item',
            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        legend: {
            itemGap:5,
            itemWidth:15,
            padding:13,
            data: legendName
        },
        grid: {
            left: '3%',
            right: '8%',
            top: '15%',
            bottom:"1%",
            containLabel: true
        },
        dataZoom: dataZoom,
        xAxis:  {
            show:false,
            type: 'value',
            axisTick: {
                show: false
            },
            axisLine: {
                show: false
            },
            splitLine: {
                show:false
            }
        },
        yAxis: {
            type: 'category',
            axisTick: {
                show: false
            },
            axisLabel:{
                inside:true
            },
            axisLine: {
                show: false
            },
            data: y
        },
        series: series
    };
    myChart.setOption(option);
    window.addEventListener("resize", function () {
        myChart.resize();
    });
}
$("#activitySet_detail").on("click","img",function () {
   if($(this).attr('src') != ''){
       whir.loading.add("",0.5,$(this).attr('src'));
   }else {
       return
   }
});
//查看活动二维码
$(".action_twoCode").click(function () {
    if($(this).attr("data-src") != undefined){
        whir.loading.add("",0.5,$(this).attr("data-src"));
        return;
    }
    var activity_code=sessionStorage.getItem("activity_code");
    var param = {"activity_code":activity_code};
    oc.postRequire('post','/vipActivity/detail/mobile/qrcode','',param,function (data) {
        if(data.code == 0){
            var msg = JSON.parse(data.message);
            var src = msg.qrcode;
            $(".action_twoCode").attr("data-src",src);
            whir.loading.add("",0.5,src);
        }else {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    });
});