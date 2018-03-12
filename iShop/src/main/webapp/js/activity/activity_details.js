//停止活动
var moreDetail={};//更多详情
var staffData=[];//页面的数据
var inx=1;//默认是第一页
var count=1;
var pageNumber=1;//删除默认第一页
var pageSize=10;//默认传的每页多少行
var _param={};//筛选定义的内容
var role=1;
var show_filtrate='';
var oc = new ObjectControl();
var store='';//门店数
var target_vips_count = '';//参与会员数
var corp_code='';//当前活动企业号
var action={};
var add_store_code='';//原始店铺号
var add_store_name='';//原始店铺名称
var isscroll=false;
var task_code='';
var min_date='';
// var nav_role='';//nav显示与否
var laydate_start={};
var laydate_end={};
var new_add="";
var noTaskStoreDefault="";
var isDefault=false;
function formatCurrency(num) {
    num=String(num);
    var reg=num.indexOf('.') >-1 ? /(\d{1,3})(?=(?:\d{3})+\.)/g : /(\d{1,3})(?=(?:\d{3})+$)/g;//千分符的正则
    num=num.replace(reg, '$1,');//千分位格式化
    return num;
}
$('#showDone').click(function(){
    var data=[];
    staffData.map(function (val,index,arr) {
        val.complete_rate==100?data.push(val):'';
    });
    listShow(data);
    var nowLength = $('.people .people_title').length;
    // $(".percent_percent").each(function(){
    //     var nowVal=($(this).text().replace('%',''));
    //     if(nowVal<100){
    //         $(this).parents('.people_title ').hide();
    //         nowLength -=1;
    //     }
    // })
    if(nowLength <1) {
        $('#peopleError').show();
        $('#peopleError div').text('未发现已完成');
    }
    data=null;
    $('#screening').slideUp(600,function () {
        $(".people").getNiceScroll().resize();
    });
    if(!(show_filtrate=='show')) {
        //收起
        $('#progress .choose').html('筛选');
        $('#progress .empty').hide();
        state = 0;
        show_filtrate=''
    }
});
//显示未完成
$('#showDoing').click(function(){
   var data=[];
    staffData.map(function (val,index,arr) {
        val.complete_rate<100?data.push(val):'';
    })
    listShow(data);
    var nowLength = $('.people_title').length;
    if(nowLength <=1) {
        $('#peopleError div').text('未发现未完成')
        $('#peopleError').show();
    }
    data=null;
    $('#screening').slideUp(600,function () {
        $(".people").getNiceScroll().resize();
    });
    if(!(show_filtrate=='show')) {
        //收起
        $('#progress .choose').html('筛选');
        $('#progress .empty').hide();
        state = 0;
        show_filtrate=''
    }
});
//显示全部
$('#showAll').click(function(){
    listShow(staffData);
    var nowLength = $('.people_title').length;
    if(nowLength <=1) {
        $('#peopleError div').text('暂无数据');
        $('#peopleError').show();
    }
    if(!(show_filtrate=='show')) {
        //收起
        $('#progress .choose').html('筛选');
        $('#progress .empty').hide();
        state = 0;
    }
    show_filtrate=='show'?show_filtrate='':$('#screening').slideUp(600,function () {
        $(".people").getNiceScroll().resize();
    });
});
//点击radio时
$('.btnSecond .radio_b').click(function () {
    $(this).next().trigger('click');
});
//筛选按钮
$('.choose').click(function(){
    $(this).parent().next('.screening').slideToggle(600,function () {
        //$(".people").getNiceScroll().resize();
    });
    if($(this).html()=="收起筛选"){
        //收起
        $(this).html('筛选');
        $(this).next('.empty').hide();
    }else{
        //展开
        $(this).html('收起筛选');
        $(this).next('.empty').show();
    }
});
//导购，客户..清空
$(".empty").click(function () {
    var id=$(this).attr("data-id");
    $(this).parent().next().find(".inputs input").val("");
    if(id=="task"){
        show_filtrate='show';
        $('#showAll').trigger('click');
    }else if(id=="customer"){
        Num = 1;
        $("#customer .peopleContent ul").empty();
        getCustomerList();
    }else if(id=="influence"){
        Num = 1;
        $("#influence .peopleContent ul").empty();
        getUserList();
    }else if(id=="spread"){
        Num = 1;
        $("#spreadPeople .peopleContent ul").empty();
        getspreadList();
    }else if(id=='online'){
        Num = 1;
        getOnlineList();
    }
});
//收起
$('#pack_up').click(function(){
    $('#screening').slideToggle(600,function () {
        $(".people").getNiceScroll().resize();
    });
});
function searchDown() {
    $('.btnSecond input:checked').removeAttr('checked');
    var name = $('#p1').val();
    var num = $('#p2').val();
    var area = $('#p3').val();
    var shop = $('#p4').val();
    if(name==""&&num==""&&area==""&&shop==""){
        $("#ck_3").prop("checked",true);
    }
    search(name,num,area,shop);
    var nowLength = $('#peopleContent li').length;
    if(nowLength <1) {
        $('#peopleError div').text('未找到相关信息');
        $('#peopleError').show();
    }
}
$("#p1").keydown(function(){
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        $('#find').trigger('click');
        searchDown()
    }
});
$("#p2").keydown(function(){
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        $('#find').trigger('click');
        searchDown()
    }
});
$("#p3").keydown(function(){
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        $('#find').trigger('click');
        searchDown()
    }
});
$("#p4").keydown(function(){
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        $('#find').trigger('click');
        searchDown()
    }
});
function search(name,num,area,shop){
    var data=[];
    var param=[];
    for(var i=0;i<arguments.length;i++){
        arguments[i]==''?'':param.push(i);
    }
    if(param.length==0){
        data=staffData;
    }else if(param.length==1){
        staffData.map(function (val,index,arr) {
            //           0                  1                      2                3
            // if(val.user_name==name||val.user_code==num||val.area_name==area||val.store_name==shop){
            //     data.push(val)
            // }
            // console.log(param[0])
            if((param.indexOf(0)!=-1)&&(val.user_name.search(name)!=-1)){data.push(val)};
            if((param.indexOf(1)!=-1)&&(val.user_code.search(num)!=-1)){data.push(val)};
            if((param.indexOf(2)!=-1)&&(val.store_code.search(area)!=-1)){data.push(val)};
            if((param.indexOf(3)!=-1)&&(val.store_name.search(shop)!=-1)){data.push(val)};
        });
    }else if(param.length==2){
        if((param.indexOf(0)!=-1) && (param.indexOf(1)!=-1)){ // 0 1
            staffData.map(function (val,index,arr) {
                // if ((val.user_name == name) && (val.user_code == num)) {
                //     data.push(val)
                // }
                if ((val.user_name.search(name)!=-1) && (val.user_code.search(num)!=-1)) {
                    data.push(val)
                }
            });
        }else if((param.indexOf(0)!=-1) && (param.indexOf(2)!=-1)){ //0  2
            staffData.map(function (val,index,arr) {
                // if ((val.user_name == name) && (val.area_name==area)) {
                //     data.push(val)
                // }
                if ((val.user_name.search(name)!=-1) && (val.store_code.search(area)!=-1)) {
                    data.push(val)
                }
            })
        }else if((param.indexOf(0)!=-1) && (param.indexOf(3)!=-1)){  //0  3
            staffData.map(function (val,index,arr) {
                // if ((val.user_name == name) && (val.store_name==shop)) {
                //     data.push(val)
                // }
                if ((val.user_name.search(name)!=-1) && (val.store_name.search(shop)!=-1)) {
                    data.push(val)
                }
            })
        }else if((param.indexOf(1)!=-1) && (param.indexOf(2)!=-1)){    //1  2
            staffData.map(function (val,index,arr) {
                // if ((val.user_code==num) && (val.area_name==area)) {
                //     data.push(val)
                // }
                if ((val.user_code.search(num)!=-1) && (val.store_code.search(area)!=-1)) {
                    data.push(val)
                }
            })
        }else if((param.indexOf(1)!=-1) && (param.indexOf(3)!=-1)){   //1  3
            staffData.map(function (val,index,arr) {
                // if ((val.user_code==num) && (val.store_name==shop)) {
                //         data.push(val)
                // }
                if ((val.user_code.search(num)!=-1) && (val.store_name.search(shop)!=-1)) {
                    data.push(val)
                }
            })
        }else if((param.indexOf(2)!=-1) && (param.indexOf(3)!=-1)){  //2  3
            staffData.map(function (val,index,arr) {
                // if ((val.area_name==area) && (val.store_name==shop)) {
                //     data.push(val)
                // }
                if ((val.store_code.search(area)!=-1) && (val.store_name.search(shop)!=-1)) {
                    data.push(val)
                }
            })
        }
    }else if(param.length==3){
        if((param.indexOf(0)!=-1) && (param.indexOf(1)!=-1)&&  (param.indexOf(2)!=-1) ){   // 012
            staffData.map(function (val,index,arr) {
                if( (val.user_name.search(name)!=-1)&&(val.user_code.search(num)!=-1)&&(val.store_code.search(area)!=-1) ){
                    data.push(val)
                }
            });
        }else if((param.indexOf(0)!=-1) && (param.indexOf(1)!=-1)&&  (param.indexOf(3)!=-1)){ //013
            staffData.map(function (val,index,arr) {
                if( (val.user_name.search(name)!=-1)&&(val.user_code.search(num)!=-1)&&(val.store_name.search(shop)!=-1) ){
                    data.push(val)
                }
            });
        }else if((param.indexOf(0)!=-1) && (param.indexOf(2)!=-1)&&  (param.indexOf(3)!=-1)){ //023
            staffData.map(function (val,index,arr) {
                if( (val.user_name.search(name)!=-1)&&(val.store_code.search(area)!=-1)&&(val.store_name.search(shop)!=-1) ){
                    data.push(val)
                }
            });
        }else if((param.indexOf(1)!=-1) && (param.indexOf(2)!=-1)&&  (param.indexOf(3)!=-1)){  //123
            staffData.map(function (val,index,arr) {
                if( (val.user_code.search(num)!=-1)&&(val.store_code.search(area)!=-1)&&(val.store_name.search(shop)!=-1) ){
                    data.push(val)
                }
            });
        }
    }else if(param.length==4){
        staffData.map(function (val,index,arr) {
            if( (val.user_name.search(name)!=-1)&&(val.user_code.search(num)!=-1)&&(val.store_code.search(area)!=-1)&&(val.store_name.search(shop)!=-1) ){
                data.push(val)
            }
        });
    }
    listShow(data);
    data=null;
}
function barChart(data) {
    var myChart = echarts.init(document.getElementById('chart_bar'));
    var data_count = [];
    var date=[];
    var data=data.reverse();
    for(var i=0;i<data.length;i++){
        date.push(data[i].date);
        data_count.push(data[i].complete_count)
    }
    var dataZoom = [];
    if(date.length>5){
        dataZoom = [
            {
                type: 'slider',
                show: true,
                start:0,
                end: 100,
                handleSize: 8,
                height:15,
                left:80,
                right:80,
                bottom:10,
                fillerColor:"#ddd"
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
                barMaxWidth:10,
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
//获取活动执行情况
function getExecuteDetail(param){
    $("#progress .listLoading_box").show();
    oc.postRequire("post", '/activityAnaly/executeDetail',"0",param,function(data){
        if(data.code==0){
            if(data.message==''){
                $('.btnSecond').hide();
                $('.people_head').hide();
                $('.people').hide();
            }else {
                var message = JSON.parse(data.message);
                if(message.taskComplete.length==0){
                  $("#chart_bar").html("暂无数据");
                }else {
                    barChart(message.taskComplete);
                }
                pieChart(message.complete_vips_count,message.target_vips_count);
                doExecuteDetail(message);
                var complete_vips_count = message.complete_vips_count;
                var userList =message.userList;
                staffData=userList;
                listShow(userList);
            }
        }else if(data.code==-1){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
        $("#progress .listLoading_box").hide();
    });
}
//加载员工列表
function listShow(userList){
    $("#peopleContent").empty();
    $('.people').animate({scrollTop:0}, 'fast');
    var tempHTML = '<li class="people_title"> <div class="people_title_order ellipsis" style="text-align: center">${order}</div> <div class="people_title_name ellipsis"title="${title_name}">${name}</div> <div class="people_title_num ellipsis"title="${title_num}">${num}</div> <div class="people_title_area ellipsis" title="${title}">${area}</div> <div class="people_title_shop ellipsis"title="${title_shop}">${shop}</div> <div class="people_title_plan"><div class="undone"><div class="done" style="width: ${percent}%"></div></div><span class="percent_percent">${percent}%</span></div> '
        +'<div class="data_detail" style="float: right;margin-right: 5px;height: 40px"><div class="detail">详情 <span class="icon-ishop_8-03"style="color:#5f8bc8"></span></div></div></li>';
    var html = '';
    for(var i=0;i<userList.length;i++) {
        //随机进度
        var order = i + 1;
        var nowHTML1 = tempHTML;
        var percent = userList[i].complete_rate;
        nowHTML1 = nowHTML1.replace("${order}", order);
        nowHTML1 = nowHTML1.replace("${name}", userList[i].user_name);
        nowHTML1 = nowHTML1.replace("${title_name}", userList[i].user_name);
        nowHTML1 = nowHTML1.replace("${num}", userList[i].user_code);
        nowHTML1 = nowHTML1.replace("${title_num}", userList[i].user_code);
        nowHTML1 = nowHTML1.replace("${area}", userList[i].store_code);
        nowHTML1 = nowHTML1.replace("${title}", userList[i].store_code);
        nowHTML1 = nowHTML1.replace("${shop}", userList[i].store_name);
        nowHTML1 = nowHTML1.replace("${title_shop}", userList[i].store_name);
        nowHTML1 = nowHTML1.replace("${percent}", percent);
        nowHTML1 = nowHTML1.replace("${percent}", percent);
        html += nowHTML1;
        //判断进度颜色
        if (percent < 100 && percent > 0) {
            $('.percent_percent').css('color', '#42a0a8');
        } else if (percent == 100) {
            $('.percent_percent').css('color', '#42a0a8');
        } else if (percent == 0) {
            $('.percent_percent').css('color', '#42a0a8');
        }
    }
    $("#peopleContent").append(html);
    var nowLength = $('.people_title').length;
    if (nowLength > 1) {
        $('#peopleError').hide();
    }else{
        $('#peopleError div').text('暂无数据');
        $('#peopleError').show();
    }
}
$('#peopleContent').on('click','li div.data_detail',function () {
    $('#detail').show();
    //获取该行数据
    $('.action_detail_box .s1').html('员工：'+$(this).parent().find('.people_title_name ').html())
    $('.action_detail_box .s2').html($(this).parent().find('.people_title_num  ').html())
    $('.action_detail_box .s3').html($(this).parent().find('.people_title_area  ').html())
    $('.action_detail_box .s4').html($(this).parent().find('.people_title_shop  ').html())
    $('.action_detail_box .s6').css('width',$(this).parent().find('.percent_percent').html())
    $('.action_detail_box .s7').html($(this).parent().find('.percent_percent').html())
    //处理函数
    var param={}
    param.task_code=$('.progress_nav').find('.progress_nav_btn_action')[0].dataset.role;
    param.user_code=$(this).parent().find('.people_title_num  ').html();
    param.corp_code=corp_code;
    oc.postRequire("post","/activityAnaly/userExecuteDetail","0",param,function(data){
        if(data.code==0){
            doResponse(JSON.parse(data.message).list);
        }else if(data.code==-1){

        }
    });
    function doResponse(msg) {
        if(msg.length==0){
            $('#showList3  tbody').empty();
            $('#showList3  tbody').append(' <div class="no_data">暂无数据</div>');
            return;
        }
        var arr=msg;
        var html='';
        $('#showList3 tbody').empty();
        for(var i=0;i<arr.length;i++){
            var a=i+1;
            var status='';
            if(arr[i].status=="Y"){
                status='已执行'
            }else if(arr[i].status=='N'){
               if(arr[i].is_assort=='Y'){
                   status='已分配'
               }else{
                   status='未执行'
               }
            }
            html+="<tr><td>"+a+"</td><td>"+arr[i].vip_info.NAME_VIP+"</td><td>"+arr[i].vip_info.MOBILE_VIP+"</td><td>"+status+"</td></tr>"
        }
        html=html==''?'<div class="no_data">暂无数据</div>':html;
        $('#showList3  tbody').append(html);
    }
});
$('.vip_status_btn').click(function () {
    $('#detail').hide();
});
/******************************************************************************************/
function getActive() {
    var _param={};
    var param={};
    param.activity_code=sessionStorage.getItem('activity_code');
    whir.loading.add("", 0.5);
    oc.postRequire("post","/vipActivity/select","0",param,function(data){
        if(data.code==0){
            var active_data=JSON.parse(JSON.parse(data.message).activityVip);
            var noTaskStore=JSON.parse(data.message).noTaskStore;
            //var store=JSON.parse(active_data.activity_store_code);
            //if(store.length>0){
            //    var li="";
            //    for(var i=0;i<store.length;i++){
            //        li+="<li title='"+store[i].store_name+"' data-code='"+store[i].store_code+"'>"+store[i].store_name+"</li>";
            //    }
            //    $("#store_select").html("<li data-code=''>全部</li>"+li);
            //}
            if(noTaskStore!=""){
                $("#noTaskStore_tip").show();
                noTaskStoreDefault=noTaskStore
            }
            corp_code=active_data.corp_code;
            _param.corp_code=active_data.corp_code;
            _param.activity_code=active_data.activity_code;
            _param.task_code=active_data.task_code.split(',')[0];
            doActiveResponse(active_data);
            _param.task_code==''?'':getExecuteDetail(_param);
            whir.loading.remove();//移除加载框
            getActiveSet();
        }else if(data.code==-1){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
            whir.loading.remove();//移除加载框
        }
    });
}
function doExecuteDetail (message) {
    $('#progress .shoppers').html(message.user_count);
    $('#progress .covers').html(message.complete_vips_count);
    $('#progress .target').html(message.target_vips_count);
    var taskInfo=JSON.parse(message.taskInfo);
    var start_time=new Date();
    var end_time=new Date(taskInfo.target_end_time);
    var time=(end_time.getTime()-start_time.getTime())/1000;
    var d=parseInt(time/(24*60*60))+1;
    d<=0&&(d=0);
    $('.task_detail_l .p1').html(taskInfo.task_title);
    $('.task_detail_l .p2').html(taskInfo.task_type_name);
    $('.task_detail_l .p3').html(store);
    $('.task_detail_l .p4').html(taskInfo.target_start_time+' 开始');
    $('.task_detail_l .p5').html(taskInfo.target_end_time+' 结束');
    $('.task_detail_l .p6').html(d);
    $('.task_detail_l .p7').html(taskInfo.task_description);
    $('.task_detail_l .p7').attr("title",taskInfo.task_description);
    $('.task_detail_l .p8').html(d==0?"已经结束":"正在执行");
}
function pieChart(a,b) {
    //a  完成 b  目标
    var myChart = echarts.init(document.getElementById('chart_pie'));
    var NoCover ='';
    var TheCover ='';
    if(b==0){
        NoCover=100;
        TheCover = 0;
    }else{
        NoCover = ((b - a)/b*100).toFixed(2);
        TheCover = (a/b*100).toFixed(2);
    }
    // 指定图表的配置项和数据
    var labelTop = {
        normal : {
            color:'#7bc7cd',
            label : {
                show : false,
                position : 'top',
                formatter : '{b}'
            },
            labelLine : {
                show : false
            }
        }
    };
    var labelFromatter = {
        normal : {
            label : {
                formatter : function (params){
                    var data=(100 - params.value).toString().length>3?(100 - params.value).toFixed(2):(100 - params.value);
                    data=data>100?100:data;
                    return data+ '%'
                },
                textStyle: {
                    color:'#7bc7cd',
                    fontSize: 18
                }
            }
        }
    }
    var labelBottom = {
        normal : {
            color: '#eaeaea',
            label : {
                show : true,
                position : 'center'
            },
            labelLine : {
                show : false
            }
        },
        emphasis: {
            color: '#eaeaea'
        }
    };
    var radius = ['55%', '70%'];
    var  option = {
        series : [
            {
                type : 'pie',
                radius : radius,
                clockwise:false,
                // x: '0%', // for funnel
                itemStyle : labelFromatter,
                data : [
                    {name:'未覆盖会员数', value:NoCover,itemStyle :labelBottom },
                    {name:'已覆盖会员数', value:TheCover, itemStyle : labelTop}
                ]
            }
        ]
    };
    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
    window.addEventListener("resize", function () {
        myChart.resize();
    });
}
function doActiveResponse(active_data) {
    var store_code=[];
    //activity_store_code
    corp_code=active_data.corp_code;
    //if(active_data.activity_store_code==''){
    //    store=0;
    //}else{
    //    var store_obj=JSON.parse(active_data.activity_store_code);
    //    action.store_code=store_obj;
    //    for(var i=0;i<store_obj.length;i++){
    //        store_code.push(store_obj[i].store_code);
    //    }
    //    store=store_code.length;
    //    add_store_code=store_code.join(',');
    //}
    //action存值
    action.activity_code=active_data.activity_code;
    action.start_time=active_data.start_time;
    action.end_time=active_data.end_time;
    min_date=active_data.start_time;
    //run_mode
    var run_mode=active_data.run_mode;
    // switch (active_data.run_mode){
    //     case 'recruit':run_mode='招募';break;
    //     case 'h5':run_mode='H5';break;
    //     case 'sales':run_mode='促销';break;
    //     case 'coupon':run_mode='优惠券';break;
    //     case 'invite':run_mode='线下邀约';break;
    //     case 'festival':run_mode='节日';break;
    // }
    //time
    var target_vips_count=active_data.target_vips_count;
    var target_vips =active_data.target_vips;
    sessionStorage.setItem("target_vips",target_vips);
    var over=active_data.end_time;
    var start=active_data.start_time;
    var over_time=new Date(over).getTime();
    var start_time=new Date(start).getTime();
    var now_judge=new Date().getTime();
    if(start_time<now_judge&&(over_time< now_judge)){//活动已开始并结束
        $('.header_m .p_2').html('0天0小时0分钟0秒');
    }else if(start_time<now_judge&&(over_time >now_judge)){//已经开始但未结束
            var timer=setInterval(function () {
            var now_time=new Date();
            var time=(over_time-now_time.getTime())/1000;
            var d=parseInt(time/(24*60*60));
            var h=parseInt((time-d*(24*60*60))/(60*60));
            var m=parseInt((time-d*(24*60*60)-h*(60*60))/60);
            var s=parseInt(time-d*(24*60*60)-h*(60*60)-m*60);
            d<=0&&(d=0);
            h<=0&&(h=0);
            m<=0&&(m=0);
            s<=0&&(s=0);
            $('.header_m .p_2').html(d+'天'+h+'小时'+m+'分钟'+s+'秒');
             if(d==0&(h==0)&(m==0)&(s==0)){
                 clearInterval(timer);
             }
        },1000);
        var now_time=new Date();
        var time=(over_time-now_time.getTime())/1000;
        var d=parseInt(time/(24*60*60));
        var h=parseInt((time-d*(24*60*60))/(60*60));
        var m=parseInt((time-d*(24*60*60)-h*(60*60))/60);
        var s=parseInt(time-d*(24*60*60)-h*(60*60)-m*60);
        d<=0&&(d=0);
        h<=0&&(h=0);
        m<=0&&(m=0);
        s<=0&&(s=0);
        $('.header_m .p_2').html(d+'天'+h+'小时'+m+'分钟'+s+'秒');
    }else if(start_time>now_judge){//还没开始
            var timer=setInterval(function () {
                var now_judge=new Date().getTime();
                if(start_time<now_judge&&(over_time>now_judge)){
                    var now_time=new Date();
                    var time=(over_time-now_time.getTime())/1000;
                    var d=parseInt(time/(24*60*60));
                    var h=parseInt((time-d*(24*60*60))/(60*60));
                    var m=parseInt((time-d*(24*60*60)-h*(60*60))/60);
                    var s=parseInt(time-d*(24*60*60)-h*(60*60)-m*60);
                    d<=0&&(d=0);
                    h<=0&&(h=0);
                    m<=0&&(m=0);
                    s<=0&&(s=0);
                    $('.header_m .p_2').html(d+'天'+h+'小时'+m+'分钟'+s+'秒');
                    if(d==0&(h==0)&(m==0)&(s==0)){
                        clearInterval(timer);
                    }
                }
            },1000);
        $('.header_m_c .p_3').html('距离活动开始还剩');
        var timerStart=setInterval(function () {
            var now_judge=new Date().getTime();
            var now_time=new Date();
            var time=(start_time-now_time.getTime())/1000;
            var d=parseInt(time/(24*60*60));
            var h=parseInt((time-d*(24*60*60))/(60*60));
            var m=parseInt((time-d*(24*60*60)-h*(60*60))/60);
            var s=parseInt(time-d*(24*60*60)-h*(60*60)-m*60);
            d<=0&&(d=0);
            h<=0&&(h=0);
            m<=0&&(m=0);
            s<=0&&(s=0);
            $('.header_m .p_2').html(d+'天'+h+'小时'+m+'分钟'+s+'秒');
            if(start_time<now_judge&&(over_time >now_judge)){
                var time=(over_time-now_time.getTime())/1000;
                var d=parseInt(time/(24*60*60));
                var h=parseInt((time-d*(24*60*60))/(60*60));
                var m=parseInt((time-d*(24*60*60)-h*(60*60))/60);
                var s=parseInt(time-d*(24*60*60)-h*(60*60)-m*60);
                d<=0&&(d=0);
                h<=0&&(h=0);
                m<=0&&(m=0);
                s<=0&&(s=0);
                $('.header_m .p_2').html(d+'天'+h+'小时'+m+'分钟'+s+'秒');
                $('.header_m_c .p_3').html('距离活动结束还剩');
                clearInterval(timerStart)
            }
        },1000);
        var now_time=new Date();
        var time=(start_time-now_time.getTime())/1000;
        var d=parseInt(time/(24*60*60));
        var h=parseInt((time-d*(24*60*60))/(60*60));
        var m=parseInt((time-d*(24*60*60)-h*(60*60))/60);
        var s=parseInt(time-d*(24*60*60)-h*(60*60)-m*60);
        d<=0&&(d=0);
        h<=0&&(h=0);
        m<=0&&(m=0);
        s<=0&&(s=0);
        $('.header_m .p_2').html(d+'天'+h+'小时'+m+'分钟'+s+'秒');
    }
    $('.title_l').html(active_data.activity_theme);
    $('.title_l').attr('title',active_data.activity_theme);
    $("#activitySet_title").html(active_data.activity_theme);//活动设置弹窗标题
    $("#appName").text(active_data.app_name);
    $("#corpName").text(active_data.corp_name);
    $("#activityType").text(active_data.run_mode);
    $("#activityDesc").text(active_data.activity_desc==""?"无":active_data.activity_desc);
    $("#activityDesc").attr("title",active_data.activity_desc==""?"无":active_data.activity_desc);
    $('.time_l').html(active_data.start_time);//处理
    $('.time_r').html(active_data.end_time);
    $('.name_l').html(run_mode);//转换
    $('.name_r').html(active_data.corp_name);
    $('.header_sm .p_2').eq(0).html(formatCurrency(target_vips_count));
    store=active_data.store_count;
    $('.header_sm .p_2').eq(1).html(formatCurrency(active_data.store_count));//处理
    //dom节点控制
    task_code=active_data.task_code.split(',');
    var active_dom=active_data.task_code.split(',');
    if(active_data.task_code==''){
        $('#progress').hide();
        $("#task_nav").hide();
        $("#customer_nav").trigger("click");
        // nav_role='N';
    }else{
        var dom=[];
        for(var i=0;i<active_dom.length;i++){
            if(i==0){
                dom.push('<div class="progress_nav_btn progress_nav_btn_action" data-role="'+active_dom[i]+'">任务'+(i+1)+'</div>');
            }else{
                dom.push('<div class="progress_nav_btn" data-role="'+active_dom[i]+'">任务'+(i+1)+'</div>');
            }
        }
        $('.progress_nav').html(dom.join(''));
    }
}
//nav控制
$('#nav').on('click','.nav_btn',function () {
    var index=$(this).index();
    var navType=$(this).attr("data-nav");
    $(this).addClass('nav_action').siblings('.nav_action').removeClass('nav_action');
    // if(nav_role=='N')return;
    $("#analyze_wrap").children("div").eq(index).show();
    $("#analyze_wrap").children("div").eq(index).siblings("div").hide();
    if(navType=="customer"){//意向客户分析
        Num = 1;
        $("#customer .peopleContent ul").empty();
        getCustomerChart();
        getCustomerList();
    }
    if(navType=="influence"){//导购分析
        Num = 1;
        $("#influence .peopleContent ul").empty();
        getUserChart();
        getUserList();
    }
    if(navType=="spread"){//传播分析
        Num = 1;
        $("#spreadPeople .peopleContent ul").empty();
        getspreadList();
        getspreadChart();
    }
    if(navType=="coupon"){//优惠券分析
        Num = 1;
        $("#coupon .peopleContent ul").empty();
        getStore();
        getActivityCoupons();
        getcouponChart();
        getcouponList();
    }
    if(navType=="signUp"){//报名会员费分析
        Num = 1;
        $("#page_signUp .peopleContent ul").empty();
        cache.tabType = '#page_signUp';
        cache.tabPeople = '#signUpPeople';
        getSignUpView();//图表
        getSignUpList();//列表
    }
    if(navType=="online"){//报名会员费分析
        Num = 1;
        $("#online_apply .peopleContent ul").empty();
        getOnlineView();//图表
        getOnlineList();//列表
    }
    //if(navType=='achievement_percent'){//业绩占比
    //    //禁止弹窗  -->  activity_details_listPage.js
    //    $("#analyze_wrap").children("div").eq(index).hide();
    //}
});
//任务控制
$('.progress_nav').on('click','.progress_nav_btn',function () {
    $(this).addClass('progress_nav_btn_action').siblings('.progress_nav_btn_action').removeClass('progress_nav_btn_action');
    whir.loading.add("", 0.5);
    var param={};
    param.corp_code=corp_code;
    param.activity_code=sessionStorage.getItem('activity_code');
    param.task_code=this.dataset.role;
    getExecuteDetail(param);
    whir.loading.remove();//移除加载框
});
//action操作
$('.modify_footer_r').click(function () {
    action.start_time=$('#start_time').val();
    action.end_time=$('#over_time').val();
    if(action.start_time==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "开始时间不能为空"
        });
    }else if(action.end_time==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "结束时间不能为空"
        });
    }else {
        actionPost(this.className.trim());
    }
});
$('.action_footer_r').click(function () {
    actionPost(this.className.trim());
    //vip_activity_statistics.html
    //$(window.parent.document).find('#iframepage').attr("src",'/vip/vip_activity_statistics.html');
});
$('.add_footer_r').click(function () {
     var obj=[];
    $('#showList tr td:nth-child(2)').each(function () {
        var param={};
        param.store_name=$(this).html();
        param.store_code=this.dataset.code;
        obj.push(param);
    });
    actionPost(this.className.trim(),obj);
});
$("#activitySet_detail").on("click",".slideArea",function () {
    $(this).css('opacity') == 1 ? $(this).animate({"opacity":0},0) : $(this).animate({"opacity":1},0);
    $(this).parents(".wrapperHeader").next(".wrapperContent").slideToggle(200);
    $(".activitySet").getNiceScroll().resize();
});
$("#activitySet_detail").on("click","#checkGoods",function () {
    $('#activitySetPage').hide();
    $('#activitySet').hide();
    $('#activityVipListPage').show();
    $('#activityVipList').show();
    $('#activityVipList').addClass("goodsListWrap");
    $("#listTitle").text("商品列表");
    $("#vip_list .vip_list_title").html('<span class="per-5">序号</span><span class="per-10">图片</span><span class="per-15">商品名称</span><span class="per-15">款号</span><span class="per-15">条码</span><span class="per-10">价格</span><span class="per-10">季节</span><span class="per-10">大类</span><span class="per-10">中类</span>');
    inx = 1;
    var condition = $(this).attr("data-goods");
    getGoodsList(inx,condition);
});
function getActiveSet() {
    whir.loading.add("", 0.5);
    var param={};
    param.activity_code=sessionStorage.getItem('activity_code');
    oc.postRequire("post","/vipActivity/detail/select","0",param,function(data){
        if(data.code==0){
            var message = JSON.parse(data.message);
            var activityVip = message.activityVip;
            var activity_type = activityVip.activity_type;//活动类型
            var activity_url = activityVip.activity_url;//推广链接
            var batch_no = activityVip.batch_no == '' ? "无" :  activityVip.batch_no;
            if(activity_url=='http://'||activity_url==''){
                activity_url = '无';
            }
            if(activity_type=='招募活动'){
                $('#activitySetPage').css('position','fixed');
                var html = '<div><span class="width52">推广链接</span><span class="color6cc1c8 urlOpen">'+activity_url+'</span></div>';
                var recruit = JSON.parse(activityVip.recruit);
                var div = '';
                for(i=0;i<recruit.length;i++){
                    div += '<div><span class="width52">招募级别</span><span class="width230">'+recruit[i].vip_card_type_name+'</span><span class="width52">招募金额</span><span>'+recruit[i].join_threshold+'</span></div>';
                }
                html = div+html;
            }else if(activity_type=='促销活动'){
                var html = '<div><span class="width52">促销编号</span><span>'+activityVip.sales_no+'</span></div><div><span class="width52">推广链接</span><span class="color6cc1c8 urlOpen">'+activity_url+'</span></div>';
            }else if(activity_type=='网页活动'){
                var html = '<div><span class="width52">推广链接</span><span class="color6cc1c8 urlOpen">'+activity_url+'</span></div>';
            }else if(activity_type=='优惠券活动'){
                $("#coupon_nav").show();
                var html = '<div><span class="width52">发券类型</span><span class="width230">${msg}</span><span></span><span></span></div>'
                    + '<div><span class="width52">推广链接</span><span class="color6cc1c8 urlOpen">'+activity_url+'</span></div>';
                var detail = '<div class="detailsWrap">'
                    + '<div class="wrapperHeader"><span>活动计划${n}</span><span class="colorAaa slideArea left5px" style="opacity: 1">- 点击展开</span></div>'
                    + '<div class="wrapperContent"><div class="ContentLeft"><span class="marginBottom10px">参与活动条件</span>${condition}</div>'
                    + '<div class="ContentRight"><div><span class="marginBottom10px">任务奖励</span>'
                    + '<div><span class="width52">积分</span><span>${points}</span></div>'
                    + '<div><span class="width52 floot-l">优惠券</span><span class="ellipsis-4">${coupons}</span></div>'
                    + '<div><span class="width52">批次号</span><span>'+batch_no+'</span></div>'
                    + '</div></div></div></div>';
                var send_coupon_type = activityVip.send_coupon_type;//发券类型
                if(send_coupon_type == "consume"){
                    var consume_condition = JSON.parse(activityVip.consume_condition);
                    var join_count = activityVip.join_count == '' ? "无" : activityVip.join_count;
                    html = '<div><span class="width52">发券类型</span><span class="width230">消费后送券</span><span style="margin-right: 5px">会员参与总数</span><span>'+join_count+'</span></div>'
                        + '<div><span class="width52">推广链接</span><span class="color6cc1c8 urlOpen">'+activity_url+'</span></div>';
                    for(var i=0;i<consume_condition.length;i++){
                        var coupon_type = JSON.parse(consume_condition[i].coupon_type);
                        var nowHtml = detail;
                        var points = consume_condition[i].send_points == "" ? "无" : consume_condition[i].send_points;
                        var coupons;
                        if(coupon_type.length>0){
                            coupons = [];
                            for(var j=0;j<coupon_type.length;j++){
                                coupons.push(coupon_type[j].coupon_name);
                            }
                            coupons = coupons.toString();
                        }else {
                            coupons = "无";
                        }
                        var n=i+1;
                        var consume_goods = consume_condition[i].consume_goods;
                        var vip_join_count = consume_condition[i].vip_join_count == '' ? "无" : "";
                        var condition = "<div><span class='width52'>指定商品</span><span id='checkGoods' data-goods='"+consume_goods+"' class='color6cc1c8'>查看 <i class='icon-ishop_8-03'></i></span></div>"
                                + '<div><span class="width52">整单金额</span><span>'+consume_condition[i].trade_start+'~'+consume_condition[i].trade_end+' 元</span></div>'
                                + '<div><span class="width52">消费折扣</span><span>'+consume_condition[i].discount_start+'~'+consume_condition[i].discount_end+'</span></div>'
                                + '<div><span class="width52">购买件数</span><span>'+consume_condition[i].num_start+'~'+consume_condition[i].num_end+'</span></div>'
                                + '<div><span class="width52">会员参与</span><span>'+vip_join_count+'</span></div>';
                        nowHtml = nowHtml.replace('${condition}',condition);
                        nowHtml = nowHtml.replace('${points}',points);
                        nowHtml = nowHtml.replace('${coupons}',coupons);
                        nowHtml = nowHtml.replace('${n}',n);
                        html+=nowHtml;
                    }
                }else {
                    var coupon_type = JSON.parse(activityVip.coupon_type);//内容List
                    if(send_coupon_type =='card'){
                        var card = '开卡送券';
                        html = html.replace('${msg}',card);
                        for(i=0;i<coupon_type.length;i++){
                            var points = coupon_type[i].present_point == "" ? "无" : coupon_type[i].present_point;
                            var nowHtml = detail;
                            var n=i+1;
                            var condition = '<div><span class="width52">卡类型</span><span>'+ coupon_type[i].vip_card_type_name+'</span></div>';
                            nowHtml = nowHtml.replace('${condition}',condition);
                            nowHtml = nowHtml.replace('${points}',points);
                            nowHtml = nowHtml.replace('${coupons}',coupon_type[i].coupon_name);
                            nowHtml = nowHtml.replace('${n}',n);
                            html+=nowHtml;
                        }
                    }else if(send_coupon_type == 'anniversary'){
                        var card = '纪念日发券';
                        html = html.replace('${msg}',card);
                        for(i=0;i<coupon_type.length;i++){
                            var nowHtml = detail;
                            var points = coupon_type[i].send_points == "" ? "无" : coupon_type[i].send_points;
                            var coupon = JSON.parse(coupon_type[i].coupon_type);
                            var anniversary_time = coupon_type[i].run_time_type == "M" ? "当月" : "当日";
                            var coupons;
                            if(coupon.length == 0){
                                coupons = "无";
                            }else {
                                coupons = [];
                                for(var j=0;j<coupon.length;j++){
                                    coupons.push(coupon[j].coupon_name);
                                }
                                coupons = coupons.toString();
                            }
                            var n=i+1;
                            var condition = '<div><span class="width65">纪念日类型</span><span>'+ coupon_type[i].param_desc+'</span></div>'
                                        + '<div><span class="width52">执行时间</span><span>'+anniversary_time+'</span></div>';
                            nowHtml = nowHtml.replace('${condition}',condition);
                            nowHtml = nowHtml.replace('${points}',points);
                            nowHtml = nowHtml.replace('${coupons}',coupons);
                            nowHtml = nowHtml.replace('${n}',n);
                            html+=nowHtml;
                        }
                    }else if(send_coupon_type=='batch'){
                        var card = '批量发券';
                        var points = activityVip.present_point == "" ? "无" : activityVip.present_point;
                        html = html.replace('${msg}',card);
                        var coupons = [];
                        if(coupon_type.length != 0){
                            for(i=0;i<coupon_type.length;i++){
                                coupons.push(coupon_type[i].coupon_name);
                            }
                            coupons = coupons.toString();
                        }else {
                            coupons = '无';
                        }
                        var condition = '<div><span class="width52">无</span></div>';
                        detail = detail.replace('${n}',"");
                        detail = detail.replace('${condition}',condition);
                        detail = detail.replace('${points}',points);
                        detail = detail.replace('${coupons}',coupons);
                        html+=detail;
                    }
                }
            }else if(activity_type=='线下报名活动'){
                $('#btn_signUp').show();
                var html = '';
                var apply_title = activityVip.apply_title;//判断依据
                if(apply_title.trim()==''){
                    html = '<div><span class="width52">推广链接</span><span class="color6cc1c8">'+activity_url+'</span></div>';
                }else{//标准模板
                    var apply_allow_vip = activityVip.apply_allow_vip == "Y" ? "是" : "否";
                    html = '<div><span class="width52">报名标题</span><span>'+apply_title+'</span></div>'
                        + '<div><span class="width52">截止时间</span><span>'+activityVip.apply_endtime+'</span></div>'
                        + '<div><span class="width52">报名简介</span><span>'+activityVip.apply_desc+'</span></div>'
                        + '<div><span class="width52">成功提示</span><span>'+activityVip.apply_success_tips+'</span></div>'
                        + '<div><span class="width65">允许非会员</span><span>'+apply_allow_vip+'</span></div>'
                        + '<div><span class="width65" style="vertical-align: top">logo图片</span><span><img style="width: 100px;height: 100px" src="'+activityVip.apply_logo+'"></span></div>';
                        + '<div><span class="width52">推广链接</span><span class="color6cc1c8 urlOpen">'+activity_url+'</span></div>';
                }
            }else if(activity_type=='节日活动'){
                var html = '<div><span class="width52">时间范围</span><span>'+activityVip.festival_start+'&nbsp;至&nbsp;'+activityVip.festival_end+'</span></div>'
                        + '<div><span class="width52">推广链接</span><span class="color6cc1c8 urlOpen">'+activity_url+'</span></div>';
            }else if(activity_type=='邀请注册'){
                var register = JSON.parse(activityVip.register_data);
                var html = '<div><span class="width52">推广链接</span><span class="color6cc1c8 urlOpen">'+activity_url+'</span></div>';
                var detail = '<div class="detailsWrap">'
                    + '<div class="wrapperHeader"><span>活动计划${n}</span><span class="colorAaa slideArea left5px" style="opacity: 1">- 点击展开</span></div>'
                    + '<div class="wrapperContent"><div class="ContentLeft"><span class="marginBottom10px">参与活动条件</span>${condition}</div>'
                    + '<div class="ContentRight"><div><span class="marginBottom10px">任务奖励</span>'
                    + '<div><span class="width52">积分</span><span>${points}</span></div>'
                    + '<div><span class="width52 floot-l">优惠券</span><span class="ellipsis-4">${coupons}</span></div>'
                    + '<div><span class="width52">批次号</span><span>'+batch_no+'</span></div>'
                    + '</div></div></div></div>';
                for(var i=0;i<register.length;i++){
                    var nowHtml = detail;
                    var points = register[i].present.point == "" ? "无" : register[i].present.point;
                    var coupons;
                    var coupon = register[i].present.coupon;
                    if(coupon.length == 0){
                        coupons = "无";
                    }else {
                        coupons = [];
                        for(var j=0;j<coupon.length;j++){
                            coupons.push(coupon[j].coupon_name);
                        }
                    }
                    var n=i+1;
                    var condition = '<div><span class="width52">人数区间</span><span>'+ register[i].number_interval.start+'~'+register[i].number_interval.end+'</span></div>'
                        + '<div><span class="width52">每邀请</span><span>'+register[i].invite+'人</span></div>';
                    nowHtml = nowHtml.replace('${condition}',condition);
                    nowHtml = nowHtml.replace('${points}',points);
                    nowHtml = nowHtml.replace('${coupons}',coupons);
                    nowHtml = nowHtml.replace('${n}',n);
                    html+=nowHtml;
                }
                $("#vip_effect").show();
            }else if(activity_type=='线上报名活动'){
                var arr = JSON.parse(activityVip.apply_condition),
                    couponList = JSON.parse(activityVip.coupon_type),
                    td_allow = activityVip.td_allow == "Y" ? "是" : "否",
                    td_end_time = activityVip.td_end_time == '' ? '' : '<div><span class="width52">退订时间</span><span>'+activityVip.td_end_time+'</span></div>',
                    present_time_type = JSON.parse(activityVip.present_time).type == "timely" ? "即时" : "定时",
                    present_time_date = JSON.parse(activityVip.present_time).date == '' ? '' : '<div><span class="width52">发送时间</span><span>'+JSON.parse(activityVip.present_time).date+'</span></div>',
                    temp = '',
                    apply_type_info = JSON.parse(activityVip.apply_type_info),
                    apply_agreement = activityVip.apply_agreement != '' ? '<a target="_blank" href="'+activityVip.apply_agreement+'" style="color: #6cc1c8">预览<i class="icon-ishop_8-03"></i></a>' : '<span>无</span>',
                    item_type = activityVip.apply_type == "sku" ? "商品" : '自定义项目',
                    apply_condition = arr.map(function (item) {
                        return item.param_desc;
                    }).join(","),
                    coupon = couponList.map(function (item) {
                        return item.coupon_name;
                    }).join(","),
                    coupon = coupon == "" ? "无" : coupon,
                    present_point = activityVip.present_point == "" ? "无" : activityVip.present_point,
                    batch_no = activityVip.batch_no == "" ? "无" : activityVip.batch_no;
                for(var i=0;i<apply_type_info.length;i++){
                    var n = i+1,cost = '',
                        img = apply_type_info[i].item_picture != '' ? apply_type_info[i].item_picture : '../img/goods_default_image.png';
                    if(apply_type_info[i].fee_money == ''){
                        cost = "积分 : "+apply_type_info[i].fee_points;
                    }else if(apply_type_info[i].fee_points == ''){
                        cost = "金额 : "+apply_type_info[i].fee_money;
                    }else {
                        cost = "金额 : "+apply_type_info[i].fee_money+" 或 积分 : "+apply_type_info[i].fee_points;
                    }
                    temp += '<li><span>'+n+'</span><span>'+apply_type_info[i].item_name+'</span><span>'+apply_type_info[i].limit_count+'</span><span>'+cost+'</span><span><img src="'+img+'"></span></li>';
                }
                html = '<div class="detailsWrap">'
                    + '<div class="wrapperHeader"><span>活动基本设置</span><span class="colorAaa slideArea left5px" style="opacity: 1;">- 点击展开</span></div>'
                    + '<div class="wrapperContent">'
                    + '<div><span class="width52">是否退订</span><span>'+td_allow+'</span></div>'+td_end_time
                    + '<div><span class="width52">奖励发放</span><span>'+present_time_type+'</span></div>'+present_time_date
                    + '</div></div>'
                    + '<div class="detailsWrap">'
                    + '<div class="wrapperHeader"><span>活动项目 '+item_type+'</span><span class="colorAaa slideArea left5px" style="opacity: 1;">- 点击展开</span></div>'
                    + '<div class="wrapperContent">'
                    + '<ul class="itemBox"><li><span>序号</span><span>项目名称</span><span>人数上限</span><span>报名费用</span><span>项目图片</span></li>'
                    + temp
                    +'</ul></div></div>'
                    + '<div class="detailsWrap">'
                    + '<div class="wrapperHeader"><span>免责协议</span><span class="colorAaa slideArea left5px" style="opacity: 1;">- 点击展开</span></div>'
                    + '<div class="wrapperContent">'
                    + '<div><span class="width52">免责协议</span>'+apply_agreement+'</div>'
                    + '</div></div>'
                    + '<div class="detailsWrap">'
                    + '<div class="wrapperHeader"><span>报名资料</span><span class="colorAaa slideArea left5px" style="opacity: 1;">- 点击展开</span></div>'
                    + '<div class="wrapperContent">'
                    + '<div><span class="width52 floot-l">报名资料</span><span class="text_overflow_line">'+apply_condition+'</span></div>'
                    + '</div></div>'
                    + '<div class="detailsWrap">'
                    + '<div class="wrapperHeader"><span>活动赠送</span><span class="colorAaa slideArea left5px" style="opacity: 1;">- 点击展开</span></div>'
                    + '<div class="wrapperContent">'
                    + '<div style="width: 100%"><span class="width52 floot-l">积分</span><span>'+present_point+'</span></div>'
                    + '<div style="width: 100%"><span class="width52 floot-l" class="text_overflow_line">优惠券</span><span>'+coupon+'</span></div>'
                    + '<div style="width: 100%"><span class="width52 floot-l">批次号</span><span>'+batch_no+'</span></div>'
                    + '</div></div>';
                $('#btn_online').show();
                $("#coupon_nav").show();
            }
            $("#activitySet_detail").html(html);
            toSee();
            whir.loading.remove();//移除加载框
        }else if(data.code==-1){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
            whir.loading.remove();//移除加载框
        }
    });
}
function toSee(){
    $('#toSee').click(function(){
        $('#activityVipList').hasClass("goodsListWrap") ? $('#activityVipList').removeClass("goodsListWrap") : "";
        $('#activitySetPage').hide();
        $('#activitySet').hide();
        $('#activityVipListPage').show();
        $('#activityVipList').show();
        $("#listTitle").text("参与会员列表");
        $("#vip_list .vip_list_title").html('<span>序号</span><span>姓名</span><span>手机号</span><span>会员类型</span><span>会员卡号</span>');
        inx=1;
        GET(inx,pageSize);
    })
}
function getGoodsList(num,condition) {
    $("#vip_list ul").empty();
    var param = {};
    param.corp_code = sessionStorage.getItem("corp_code");
    param.page_num = num;
    param.page_size = "10";
    param.param = condition;
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post", "/vipActivity/getSku", "", param, function (data) {
        if(data.code == 0){
            var msg = JSON.parse(data.message);
            var count = Math.ceil(msg.count/10);
            var list = msg.sku_list;
            for(var i=0;i<list.length;i++){
                $("#vip_list ul").append(
                    "<li>"
                    +"<span class='per-5'>"+((num-1)*10+i+1)+"</span>"
                    +"<span class='per-10'><img style='width: 50px' src='"+list[i].PRODUCT_IMG+"' onerror='imgError(this)'></span>"
                    +"<span class='per-15'>"+list[i].PRODUCT_NAME+"</span>"
                    +"<span class='per-15'>"+list[i].PRODUCT_CODE+"</span>"
                    +"<span class='per-15'>"+list[i].SKU_CODE+"</span>"
                    +"<span class='per-10'>"+list[i].PRICE_SUG+"</span>"
                    +"<span class='per-10'>"+list[i].SEASON_PRD+"</span>"
                    +"<span class='per-10'>"+list[i].CATA1_PRD+"</span>"
                    +"<span class='per-10'>"+list[i].CATA2_PRD+"</span>"
                    +"</li>"
                );
            }
            setPage($("#foot-num")[0],count,num,10,condition);
        }else if(data.code == -1){
            art.dialog({
                time: 3,
                lock: true,
                cancel: false,
                content: "加载失败"
            });
        }
        whir.loading.remove();//移除加载框
    });
}
function imgError(image) {
    $(image).attr("src", "../img/goods_default_image.png");
}
//修改活动时间
$('.action_time').click(function () {
    laydate_start = {
        min: laydate.now(0,'YYYY-MM-DD hh:mm:ss')
        ,max: '2099-06-16 23:59:59'
        ,istoday: false
        , istime: true //是否开启时间选择
        ,fixed:true
        , format: 'YYYY-MM-DD hh:mm:ss' //日期格式
        ,choose: function(datas){
            laydate_end.min = datas; //开始日选好后，重置结束日的最小日期
            laydate_end.start = datas //将结束日的初始值设定为开始日
        }
        ,  elem: '#start_time', //需显示日期的元素选择器
    };
    laydate_end= {
        min: laydate.now(1)
        ,max: '2099-06-16 23:59:59'
        , istime: true //是否开启时间选择
        , format: 'YYYY-MM-DD hh:mm:ss' //日期格式
        ,fixed:true
        ,istoday: false
        ,choose: function(datas){
            laydate_start.max = datas; //结束日选好后，重置开始日的最大日期
        },
        elem: '#over_time', //需显示日期的元素选择器
    };
    var s=new Date(min_date);
    var n=new Date();
    if(s<n){
        $('#start_time').attr('disabled','disabled');
        $('#start_time').css('color','#aaa');
        $('#start_time').removeAttr('onclick');
    }
    $('#changeTime').show();
    $('#start_time').val(action.start_time);
    $('#over_time').val(action.end_time);
});
$("#activitySet").on("click",".urlOpen",function () {
    if($(this).text() != "无"){
        var activity_code=sessionStorage.getItem("activity_code");
        window.open("http://" + window.location.host + "/goods/mobile/activity_web.html?activity_code="+activity_code);
    }
});
function actionPost(role,obj) {//第二个参数是store_code
    whir.loading.add("",0.5);
    if(obj){action.store_code=obj;}
    if(role=='action_footer_r'){
        // var myDate = new Date();
        // var result=myDate.getFullYear()+'-'+(myDate.getMonth()+1)+'-'+myDate.getDate() +' '+myDate.getHours()+':'+myDate.getMinutes()+":"+myDate.getSeconds();
        action.status=2;
        action.start_time='';
        action.end_time='';
    }else{
        action.status='';
    }
    oc.postRequire("post","/vipActivity/changeState","0",action,function(data){
        var msg=typeof (data.message)=="string"?data.message:JSON.parse(data.message);
        if(data.code==0){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: '保存成功'
            });
            whir.loading.remove();
            $('.add_store').hide();
            //location.reload();
            if(role=='action_footer_r'){
                $(window.parent.document).find('#iframepage').attr("src",'/vip/vip_activity_statistics.html');
            }else if(msg.count>0 && new_add!=""){
                setTimeout(function(){
                    $("#distribution_num").text(msg.count);
                    $("#distribution").show();
                },1000)
            }else{
                location.reload();
            }
        }else if (data.code==-1){
            whir.loading.remove();
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    });
}
function getTask(){
    var param={};
    param.activity_code=sessionStorage.getItem("activity_code");
    param.corp_code=corp_code;
    oc.postRequire("post","/vipActivity/unComplTask","0",param,function(data){
        var msg=JSON.parse(data.message);
        var list=msg.list;
        var html="";
        for(var i=0;i<list.length;i++){
              html+='<li data-name="task_id" data-code="'+list[i].task_code+'">'
                    +'<div class="checkbox1">'
                    +'<input type="checkbox" value="" name="test" class="check" id="checkboxInput0'+i+'">'
                    +'<label for="checkboxInput0'+i+'"></label>'
                    +'</div>'
                    +'<span class="p15">'+list[i].task_title+'</span>'
                    +'</li>'
        }
        $("#task_all ul").html(html);
    })
}
function distribution_que(){
    var param={};
    var task_code="";
    var li=$("#task_all input:checked").parents("li");
    if(li.length==0){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "至少选择一项任务"
        });
        return;
    }
    for(var a=li.length-1;a>=0;a--){
        if(a>0){
            task_code+=$(li[a]).attr("data-code")+","
        }else {
            task_code+=$(li[a]).attr("data-code")
        }
    }
    param.activity_code=sessionStorage.getItem("activity_code");
    param.corp_code=corp_code;
    param.task_code=task_code;
    param.store_code=new_add==""?noTaskStoreDefault:new_add;
    whir.loading.add("",0.5);
    oc.postRequire("post","/vipActivity/allocUnComplTask","0",param,function(data){
        if(data.code==0){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "分配成功"
            });
            location.reload();
        }else if(data.code==-1){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
            whir.loading.remove();
        }
    })
}
$("#distribution_que").bind("click", function () {
    distribution_que();
});
$(".distribution_footer .cancel").bind("click",function(){
    if(isDefault){
        $("#distribution_task").hide();
        isDefault=false;
        $("#P").hide();
        return
    }
    $("#distribution").hide();
    $(".add_store_box").hide();
    $("#distribution_task").hide();
    location.reload();
});
$("#now_distribution").bind("click",function(){
    $("#distribution").hide();
    $("#distribution_task").show();
    getTask();
});
$('#return_back').click(function () {
    $(window.parent.document).find('#iframepage').attr("src",'/activity/activity.html');
});
function storeShow(store_code) {
    var role=arguments[1];
    var param={};
    param.activity_code=sessionStorage.getItem('activity_code');
    //param.corp_code=corp_code;
    //param.store_code=store_code;
    whir.loading.add("", 0.5);
    oc.postRequire("post","/vipActivity/getStoreList","0",param,function(data){
        if(data.code==0){
            storeShowList(JSON.parse(data.message).storeList,role);
            whir.loading.remove();//移除加载框
        }else if(data.code==-1){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
            whir.loading.remove();//移除加载框
        }
    })
}
function storeShowList(arr) {
    var html="";
    $('#showList tbody').empty();
    $('#showList2 tbody').empty();
   for(var i=0;i<arr.length;i++){
       html+='<tr><td>'+(i+1)+'</td><td data-code="'+arr[i].store_code+'">'+arr[i].store_name+'</td><td>'
           +arr[i].province+arr[i].city+arr[i].area+arr[i].street+'</td></tr>';
   }
    arguments[1]?$('#showList2 tbody').html(html):$('#showList tbody').html(html);
}
$('.add_header_r').click(function () {
    // var left=($('.add_store_box').width()-$("#screen_shop").width())/2-120;
    // var tp=50;
    $('.add_store').hide();
    $("#screen_shop").show();
    $("#screen_shop").css({"position":"fixed"});
    $("#screen_staff .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    getstorelist(1)
});
//参与门店
$('.task_list').click(function () {
    $('.join_store_box').toggle();
    storeShow(add_store_code,'store');
});
$('#header .btn').click(function () {
    $(window.parent.document).find('#iframepage').attr("src",'/vip/vip_strategy_supplement.html?t='+$.now());
});
$(function () {
    getActive();
    $($('.btnSecond input')[2]).attr('checked','true');
});
//自定义选择器（优惠券分析店铺搜索）
$.expr[":"].searchableSelectContains = $.expr.createPseudo(function(arg) {
    return function( elem ) {
        return $(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0;
    };
});
$("#choose_store_input").on('keyup', function(event){
    var text=$(this).val();
    if(text == ""){
        $(this).parent().find('li').removeClass('store_list_kuang_hide');
    }else {
        $(this).parent().find('li').addClass('store_list_kuang_hide');
        $(this).parent().find('li:searchableSelectContains('+text+')').removeClass('store_list_kuang_hide');
    }
});
/************************************************************************************/
$(".screen_content").on("click","li",function(){
    var input=$(this).find("input")[0];
    var thinput=$("thead input")[0];
    if(input.type=="checkbox"&&input.checked==false){
        input.checked = true;
    }else if(input.type=="checkbox"&&input.checked==true){
        input.checked = false;
    }
});
//区域搜索
$("#area_search").keydown(function(){
    var event=window.event||arguments[0];
    area_num=1;
    if(event.keyCode == 13){
        isscroll=false;
        $("#screen_area .screen_content_l").unbind("scroll");
        $("#screen_area .screen_content_l ul").empty();
        getarealist(area_num);
    }
});
//店铺搜索
$("#store_search").keydown(function(){
    var event=window.event||arguments[0];
    shop_num=1;
    if(event.keyCode==13){
        isscroll=false;
        $("#screen_shop .screen_content_l ul").unbind("scroll");
        $("#screen_shop .screen_content_l ul").empty();
        getstorelist(shop_num);
    }
});
//品牌搜索
$("#brand_search").keydown(function(){
    var event=window.event||arguments[0];
    if(event.keyCode==13){
        $("#screen_brand .screen_content_l ul").empty();
        getbrandlist();
    }
});
//员工搜索
$("#staff_search").keydown(function(){
    var event=window.event||arguments[0];
    staff_num=1;
    if(event.keyCode==13){
        isscroll=false;
        $("#screen_staff .screen_content_l").unbind("scroll");
        $("#screen_staff .screen_content_l ul").empty();
        getstafflist(staff_num);
    }
});
//店铺放大镜搜索
$("#store_search_f").click(function(){
    shop_num=1;
    isscroll=false;
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    getstorelist(shop_num);
});
//区域放大镜收索
$("#area_search_f").click(function(){
    area_num=1;
    isscroll=false;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    getarealist(area_num);
});
$("#staff_search_f").click(function(){
    staff_num=1;
    isscroll=false;
    $("#screen_staff .screen_content_l").unbind("scroll");
    $("#screen_staff .screen_content_l ul").empty();
    getstafflist(staff_num);
});
$("#brand_search_f").click(function(){
    $("#screen_brand .screen_content_l ul").empty();
    getbrandlist();
});
//区域关闭
$("#screen_close_area").click(function(){
    $("#screen_area").hide();
    $("#screen_shop").show();
    // whir.loading.remove();//移除遮罩层
});
//员工关闭
$("#screen_close_staff").click(function(){
    $("#screen_staff").hide();
    // whir.loading.remove();//移除遮罩层
});
//店铺关闭
$("#screen_close_shop").click(function(){
    //$('.add_store').show();
    $("#screen_shop").hide();
    $(".add_store_box").hide();
    // whir.loading.remove();//移除遮罩层
    $("#screen_shop .screen_content_l").unbind("scroll");
    //storeShow(add_store_code);
});
//品牌关闭
$("#screen_close_brand").click(function(){
    $("#screen_brand").hide();
    $("#screen_shop").show();
    // whir.loading.remove();//移除遮罩层
});
//获取品牌列表
var shop_num=1;
function getbrandlist(){
    var corp_code = $('#OWN_CORP').val();
    var searchValue=$("#brand_search").val();
    var _param={};
    _param["corp_code"]=corp_code;
    _param["searchValue"]=searchValue;
    whir.loading.add("",0.5);//加载等待框
    $("#mask").css("z-index","10002");
    oc.postRequire("post","/shop/brand", "",_param, function(data){
        if (data.code == "0") {
            var message=JSON.parse(data.message);
            var list=message.brands;
            var brand_html_left = '';
            var brand_html_right='';
            if (list.length == 0){
                for(var h=0;h<9;h++){
                    brand_html_left+="<li></li>"
                }
            } else {
                if(list.length<9){
                    for (var i = 0; i < list.length; i++) {
                        brand_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxThreeInput"
                            + i
                            + 1
                            + "'/><label for='checkboxThreeInput"
                            + i
                            + 1
                            + "'></label></div><span class='p16'>"+list[i].brand_name+"</span></li>"
                    }
                    for(var j=0;j<9-list.length;j++){
                        brand_html_left+="<li></li>"
                    }
                }else if(list.length>=9){
                    for (var i = 0; i < list.length; i++) {
                        brand_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxThreeInput"
                            + i
                            + 1
                            + "'/><label for='checkboxThreeInput"
                            + i
                            + 1
                            + "'></label></div><span class='p16'>"+list[i].brand_name+"</span></li>"
                    }
                }
            }
            $("#screen_brand .screen_content_l ul").append(brand_html_left);
            var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
            for(var k=0;k<li.length;k++){
                $("#screen_brand .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
            }
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    })
}
//拉取区域
function getarealist(a){
    var corp_code = $('#OWN_CORP').val();
    var area_command = "/area/selAreaByCorpCode";
    var searchValue=$("#area_search").val().trim();
    var pageSize=20;
    var pageNumber=a;
    var _param = {};
    _param["corp_code"] = corp_code;
    _param["searchValue"]=searchValue;
    _param["pageSize"]=pageSize;
    _param["pageNumber"]=pageNumber;
    whir.loading.add("",0.5);//加载等待框
    $("#mask").css("z-index","10002");
    oc.postRequire("post", area_command, "", _param, function(data) {
        if (data.code == "0") {
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var hasNextPage=list.hasNextPage;
            var cout=list.pages;
            var list=list.list;
            var area_html_left ='';
            var area_html_right='';
            if (list.length == 0) {

            } else {
                if(list.length>0){
                    for (var i = 0; i < list.length; i++) {
                        area_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].area_code+"' data-areaname='"+list[i].area_name+"' name='test'  class='check'  id='checkboxOneInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxOneInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>"+list[i].area_name+"</span></li>"
                    }
                }
            }
            if(hasNextPage==true){
                area_num++;
                area_next=false;
            }
            if(hasNextPage==false){
                area_next=true;
            }
            $("#screen_area .screen_content_l ul").append(area_html_left);
            if(!isscroll){
                $("#screen_area .screen_content_l").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(area_next){
                            return;
                        }
                        getarealist(area_num);
                    };
                })
            }
            isscroll=true;
            var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
            for(var k=0;k<li.length;k++){
                $("#screen_area .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
            }
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    })
}
//获取店铺列表
function getstorelist(a){
    var area_code =$('#area_num').attr("data-areacode");
    var brand_code=$('#brand_num').attr("data-brandcode");
    var searchValue=$("#store_search").val();
    var pageSize=20;
    var pageNumber=a;
    var _param={};
    _param['corp_code']= corp_code;
    _param['area_code']=area_code;
    _param['brand_code']=brand_code;
    _param['searchValue']=searchValue;
    _param['pageNumber']=pageNumber;
    _param['pageSize']=pageSize;
    whir.loading.add("",0.5);//加载等待框
    $("#mask").css("z-index","10002");
    //$("#screen_shop .screen_content_l ul").empty();
    // oc.postRequire("post","/user/stores","", _param, function(data) {
    oc.postRequire("post","/shop/selectByAreaCode","", _param, function(data) {
        if (data.code == "0") {
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var hasNextPage=list.hasNextPage;
            var cout=list.pages;
            var list=list.list;
            var store_html = '';
            if (list.length == 0){

            } else {
                if(list.length>0){
                    for (var i = 0; i < list.length; i++) {
                        store_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].store_code+"' data-storename='"+list[i].store_name+"' name='test'  class='check'  id='checkboxTowInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxTowInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>"+list[i].store_name+"</span></li>"
                    }
                }
            }
            if(hasNextPage==true){
                shop_num++;
                shop_next=false;
            }
            if(hasNextPage==false){
                shop_next=true;
            }
            $("#screen_shop .screen_content_l ul").append(store_html);
            if(!isscroll){
                $("#screen_shop .screen_content_l").unbind("scroll").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(shop_next){
                            return;
                        }
                        getstorelist(shop_num);
                    }
                })
            }
            isscroll=true;
            var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
            for(var k=0;k<li.length;k++){
                $("#screen_shop .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
            }
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            // art.dialog({
            //     time: 1,
            //     lock: true,
            //     cancel: false,
            //     content: data.message
            // });
        }
    })
}
//获取员工列表
function getstafflist(a){
    var corp_code = $('#OWN_CORP').val();
    var searchValue=$('#staff_search').val();
    var area_code =$("#staff_area_num").attr("data-areacode");
    var brand_code=$("#staff_brand_num").attr("data-brandcode");
    var store_code=$("#staff_shop_num").attr("data-storecode");
    var pageSize=20;
    var pageNumber=a;
    var _param={};
    _param["corp_code"]=corp_code;
    _param['area_code']=area_code;
    _param['brand_code']=brand_code;
    _param['store_code']=store_code;
    _param['searchValue']=searchValue;
    _param['pageNumber']=pageNumber;
    _param['pageSize']=pageSize;
    whir.loading.add("",0.5);//加载等待框
    $("#mask").css("z-index","10002");
    oc.postRequire("post","/user/selectUsersByRole","", _param, function(data) {
        if (data.code == "0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var hasNextPage=list.hasNextPage;
            var cout=list.pages;
            var list=list.list;
            var staff_html = '';
            if (list.length == 0){

            } else {
                if(list.length>0){
                    for (var i = 0; i < list.length; i++) {
                        staff_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].user_code+"' data-phone='"+list[i].phone+"' data-storename='"+list[i].user_name+"' name='test'  class='check'  id='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'/><label for='checkboxFourInput"
                            + i
                            + a
                            + 1
                            + "'></label></div><span class='p16'>"+list[i].user_name+"\("+list[i].user_code+"\)</span></li>"
                    }
                }
            }
            if(hasNextPage==true){
                staff_num++;
                staff_next=false;
            }
            if(hasNextPage==false){
                staff_next=true;
            }
            $("#screen_staff .screen_content_l ul").append(staff_html);
            if(!isscroll){
                $("#screen_staff .screen_content_l").unbind("scroll").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(staff_next){
                            return;
                        }
                        getstafflist(staff_num);
                    }
                })
            }
            isscroll=true;
            $("#screen_staff .screen_content_l li:odd").css("backgroundColor","#fff");
            $("#screen_staff .screen_content_l li:even").css("backgroundColor","#ededed");
            $("#screen_staff .screen_content_r li:odd").css("backgroundColor","#fff");
            $("#screen_staff .screen_content_r li:even").css("backgroundColor","#ededed");
            var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
            for(var k=0;k<li.length;k++){
                $("#screen_staff .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
            }
            whir.loading.remove();//移除加载框
        } else if (data.code == "-1") {
            alert(data.message);
        }
    })
}
//点击区域的确定
$("#screen_que_area").click(function(){
    var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
    var area_code="";
    var name="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        var h=$(li[i]).find(".p16").html();
        if(i<li.length-1){
            area_code+=r+",";
            name+=h+","
        }else{
            area_code+=r;
            name+=h;
        }
    }
    isscroll=false;
    shop_num=1;
    $("#area_num").attr("data-areacode",area_code);
    $("#area_num").val("已选"+li.length+"个");
    $("#screen_shop .screen_content_l ul").empty();
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_area").hide();
    $("#screen_shop").show();
    getstorelist(shop_num);
});
//点击店铺的确定
$("#screen_que_shop").click(function(){
    var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
    var store_code="";
    var name="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        var h=$(li[i]).find(".p16").html();
        if(i<li.length-1){
            store_code+=r+",";
            name+=h+",";
        }else{
            store_code+=r;
            name+=h;
        }
    }
    var stroe_code_array=store_code.split(",");
    var add_store_code_array=add_store_code.split(",");
    var tip_text="";
    for(var s=0;s<stroe_code_array.length;s++){
        for(var n=0;n<add_store_code_array.length;n++){
            if(stroe_code_array[s]==add_store_code_array[n]){
                tip_text=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li#"+stroe_code_array[s]).find("span").html();
                break
            }
        }
    }
    if(tip_text!=""){
        art.dialog({
            zIndex:10003,
            time: 2,
            lock: true,
            cancel: false,
            content: tip_text+"已存在，不能重复添加"
        });
        return;
    }
    new_add=store_code;
    //拼接store-code
    if(store_code==""){
        $("#screen_shop").hide();
        $('.add_store_box').hide();
    }else {
        //请求门店
        var post_arr = store_code.split(',');
        var post_arr_name = name.split(',');
        Array.prototype.unique3 = function () {
            var res = [];
            var json = {};
            for (var i = 0; i < this.length; i++) {
                if (!json[this[i]]) {
                    res.push(this[i]);
                    json[this[i]] = 1;
                }
            }
            return res;
        };
        var allStoreCode = post_arr.unique3();
        var allStoreName = post_arr_name.unique3();
        var arr = [];
        for (var m = 0; m < allStoreCode.length; m++) {
            var param = {};
            param.store_code = allStoreCode[m];
            param.store_name = allStoreName[m];
            arr.push(param)
        }
        actionPost(1, arr);
        //storeShow(post_arr.unique3().join(','));}
    }
});
//点击员工的确定
$("#screen_que_staff").click(function(){
    var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
    var store_code="";
    var phone="";
    var name="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        var p=$(li[i]).find("input").attr("data-phone");
        var h=$(li[i]).find(".p16").html();
        if(i<li.length-1){
            store_code+=r+",";
            phone+=p+",";
            name+=h+",";
        }else{
            store_code+=r;
            phone+=p;
            name+=h;
        }
    }
    $("#sendee_r").attr("data-code",store_code);
    $("#sendee_r").attr("data-phone",phone);
    $("#sendee_r").attr("data-name",name);
    $("#screen_staff").hide();
    $("#sendee_r").val("已选"+li.length+"个");
    $("#sendee_r").attr("data-type","staff");
    whir.loading.remove();//移除遮罩层
})
//点击品牌的确定
$("#screen_que_brand").click(function(){
    var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
    var brand_code="";
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("id");
        if(i<li.length-1){
            brand_code+=r+",";
        }else{
            brand_code+=r;
        }
    }
        isscroll=false;
        shop_num=1;
        $("#brand_num").attr("data-brandcode",brand_code);
        $("#brand_num").val("已选"+li.length+"个");
        $("#screen_shop .screen_content_l ul").empty();
        $("#screen_shop .screen_content_l").unbind("scroll");
        $("#screen_brand").hide();
        $("#screen_shop").show();
        getstorelist(shop_num);
})
//店铺里面的区域点击
$("#shop_area").click(function(){
    isscroll=false;
    area_num=1;
    // var left=($('.add_store_box').width()-$("#screen_shop").width())/2-120;
    // var tp=25;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    $("#screen_area").css("position","fixed");
    $("#screen_area").show();
    $("#screen_shop").hide();
    getarealist(area_num);
})
//店铺里面的品牌点击
$("#shop_brand").click(function(){
    // var left=($('.add_store_box').width()-$("#screen_shop").width())/2-120;
    // var tp=25;
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand").css("position","fixed");
    $("#screen_brand").show();
    $("#screen_shop").hide();
    getbrandlist();
})
//员工里面的区域点击
$("#staff_area").click(function(){
    isscroll=false;
    area_num=1;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    $("#screen_area").css("position","fixed");
    $("#screen_area").show();
    $("#screen_staff").hide();
    getarealist(area_num);
})
//员工里面的店铺点击
$("#staff_shop").click(function(){
    isscroll=false;
    shop_num=1;
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    $("#screen_shop").css({"position":"fixed"});
    $("#screen_shop").show();
    $("#screen_staff").hide();
    getstorelist(shop_num);
})
//员工里面的品牌点击
$("#staff_brand").click(function(){
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand").css("position","fixed");
    $("#screen_brand").show();
    $("#screen_staff").hide();
    getbrandlist();
})
//移到右边
function removeRight(a,b){
    var li="";
    if(a=="only"){
        li=$(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li");
    }
    if(a=="all"){
        li=$(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']").parents("li");
    }
    if(li.length=="0"){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请先选择"
        });
        return;
    }
    if(li.length>0){
        for(var i=0;i<li.length;i++){
            var html=$(li[i]).html();
            var id=$(li[i]).find("input[type='checkbox']").val();
            $(li[i]).find("input[type='checkbox']")[0].checked=true;
            var input=$(b).parents(".screen_content").find(".screen_content_r li");
            for(var j=0;j<input.length;j++){
                if($(input[j]).attr("id")==id){
                    $(input[j]).remove();
                }
            }
            $(b).parents(".screen_content").find(".screen_content_r ul").prepend("<li id='"+id+"'>"+html+"</li>");
            $(b).parents(".screen_content").find(".screen_content_r input[value='"+id+"']").removeAttr("checked");
        }
    }
    var num=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
    $("#screen_staff .screen_content_l li:odd").css("backgroundColor","#fff");
    $("#screen_staff .screen_content_l li:even").css("backgroundColor","#ededed");
    $("#screen_staff .screen_content_r li:odd").css("backgroundColor","#fff");
    $("#screen_staff .screen_content_r li:even").css("backgroundColor","#ededed");
}
//移到左边
function removeLeft(a,b){
    var li="";
    if(a=="only"){
        li=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']:checked").parents("li");
    }
    if(a=="all"){
        li=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li");
    }
    if(li.length=="0"){
        art.dialog({
            zIndex:10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请先选择"
        });
        return;
    }
    if(li.length>0){
        for(var i=li.length-1;i>=0;i--){
            $(li[i]).remove();
            $(b).parents(".screen_content").find(".screen_content_l input[value='"+$(li[i]).attr("id")+"']").removeAttr("checked");
        }
    }
    var num=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
}
//点击右移
$(".shift_right").click(function(){
    var right="only";
    var div=$(this);
    removeRight(right,div);
})
//点击右移全部
$(".shift_right_all").click(function(){
    var right="all";
    var div=$(this);
    removeRight(right,div);
})
//点击左移
$(".shift_left").click(function(){
    var left="only";
    var div=$(this);
    removeLeft(left,div);
})
//点击左移全部
$(".shift_left_all").click(function(){
    var left="all";
    var div=$(this);
    removeLeft(left,div);
});
$("#noTaskStore_tip_close").click(function(){
    $("#noTaskStore_tip").hide();
    isDefault=false;
});
$("#setTask").click(function(){
    isDefault=true;
    $("#P").show();
    $("#distribution_task").show();
    getTask();
});
$("#task_all").on("click","li",function(){
    $(this).find("input").prop("checked")==true?$(this).find("input").prop("checked",false):$(this).find("input").prop("checked",true)
});