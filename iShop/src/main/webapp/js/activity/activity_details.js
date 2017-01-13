//停止活动
var moreDetail={};//更多详情
var staffData=[];//页面的数据
var role=1;
var show_filtrate='';
var left=($(window).width()-$("#tk").width())/2;//弹框定位的left值
var tp=($(window).height()-$("#tk").height())/2;//弹框定位的top值
var oc = new ObjectControl();
var store='';//门店数
var corp_code='';//当前活动企业号
var action={};
var add_store_code='';//原始店铺号
var isscroll=false;
var task_code='';
var min_date='';
var nav_role='';//nav显示与否
var laydate_start={};
var laydate_end={};
function stop(){
    var _params={
        "id":"",
        "message":{
            "id":sessionStorage.getItem('id')
        }
    };
    $.ajax({
        url: '/activity/changeState',
        type: 'POST',
        dataType: "JSON",
        data:{
            param:JSON.stringify(_params)
        },
        success: function (data) {
            // $('#stop').html()=='中止活动'?$('#stop').html('恢复活动'):$('#stop').html('中止活动');
            getSelect(sessionStorage.getItem('id'));
        },
        error: function (data) {
            alert(data.message);
            whir.loading.remove();//移除加载框
        }
    });
}
$('#stop').click(function () {
    whir.loading.add("",0.5);//加载等待框
    $('#loading').remove();
    $("#tk").show();
    $("#tk").css({"left":+left+"px","top":+tp+"px"});
});
$('#cancel').click(function () {
    $("#tk").hide();
    whir.loading.remove();//移除加载框
});
$('#complete').click(function () {
    stop();
    $('#stop').remove();
    $("#tk").hide();
    whir.loading.remove();//移除加载框
});
$('#delete_tk').click(function () {
    $("#tk").hide();
    whir.loading.remove();//移除加载框
});
//通知相关人
function notice(){
    window.location.href = 'activity_noyifyTheExecutor.html';
}
//关闭
function closePage(){
    window.location.href = 'activity.html';
}
//仅显示已完成
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
    console.log(nowLength);
    if(nowLength <1) {
        $('#peopleError').show();
        $('#peopleError div').text('未发现已完成');
    }
    data=null;
    $('#screening').slideUp(600,function () {
        $(".people").getNiceScroll().resize();
    });
});
//显示未完成
$('#showDoing').click(function(){
   var data=[];
    staffData.map(function (val,index,arr) {
        val.complete_rate<100?data.push(val):'';
    })
    listShow(data);
    var nowLength = $('.people_title').length;
    // $(".percent_percent").each(function(){
    //     var nowVal=($(this).text().replace('%',''));
    //     if(nowVal==100){
    //         $(this).parents('.people_title ').hide();
    //         nowLength -=1;
    //     }
    // })
    if(nowLength <=1) {
        $('#peopleError div').text('未发未完成')
        $('#peopleError').show();
    }
    data=null;
    $('#screening').slideUp(600,function () {
        $(".people").getNiceScroll().resize();
    });
});
//显示全部
$('#showAll').click(function(){
    listShow(staffData);
    var nowLength = $('.people_title').length;
    if(nowLength <=1) {
        $('#peopleError div').text('暂无数据');
        $('#peopleError').show();
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
var state=0;//判断收起与展开
$('#choose').click(function(){
    $('#screening').slideToggle(600,function () {
        $(".people").getNiceScroll().resize();
    });
    if(state){
        //收起
        $(this).html('筛选')
        $('#empty').hide()
    }else{
        //展开
        $(this).html('收起筛选')
        $('#empty').show()
    }
    state=state==0?1:0;
});
//清空筛选
$('#empty').click(function(){
    show_filtrate='show';
    // $('.btnSecond input:checked').removeAttr('checked');
    $('.inputs input').val('');
    $('#showAll').trigger('click');
    $('#choose').trigger('click');
    // listShow(staffData);
    // var nowLength = $('.people_title').length;
    // if(nowLength <=0) {
    //     $('#peopleError div').text('暂无数据');
    //     $('#peopleError').show();
    // }
});
//收起
$('#pack_up').click(function(){
    $('#screening').slideToggle(600,function () {
        $(".people").getNiceScroll().resize();
    });
})
//查找
$('#find').click(function(){
    $('.btnSecond input:checked').removeAttr('checked');
    var name = $('#p1').val();
    var num = $('#p2').val();
    var area = $('#p3').val();
    var shop = $('#p4').val();
    search(name,num,area,shop);
    var nowLength = $('.people_title').length;
    if(nowLength <=1) {
        $('#peopleError div').text('暂无匹配数据');
        $('#peopleError').show();
    }
});
function searchDown() {
    $('.btnSecond input:checked').removeAttr('checked');
    var name = $('#p1').val();
    var num = $('#p2').val();
    var area = $('#p3').val();
    var shop = $('#p4').val();
    search(name,num,area,shop);
    var nowLength = $('.people_title').length;
    if(nowLength <=1) {
        $('#peopleError div').text('暂无匹配数据');
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
    listShow(data)
    // if(name!=''){
    //     $('.people_title_name').each(function(){
    //         var nameText = $(this).text();
    //         console.log(nameText);
    //         if(nameText.indexOf(name.trim())<0 && nameText!= '员工姓名'){
    //             $(this).parent('.people_title ').hide();
    //             nowLength -=1;
    //             console.log('员工姓名已隐藏'+nowLength);
    //         }
    //     })
    // }
    // if(num!=''){
    //     $('.people_title_num').each(function(){
    //         var numText = $(this).text();
    //         console.log(numText);
    //         if(numText.indexOf(num.trim())<0 && numText!= '员工编号'){
    //             $(this).parent('.people_title ').hide();
    //             nowLength -=1;
    //             console.log('员工编号已隐藏'+nowLength);
    //         }
    //     })
    // }
    // if(area!=''){
    //     console.log(area);
    //     $('.people_title_area').each(function(){
    //         var areaText = $(this).text();
    //         console.log(areaText);
    //         if(areaText.indexOf(area.trim())<0 && areaText!= '所属区域'){
    //             $(this).parent('.people_title ').hide();
    //             nowLength -=1;
    //             console.log('所属区域已隐藏'+nowLength);
    //         }
    //     })
    // }
    // if(shop!=''){
    //     console.log(shop);
    //     $('.people_title_shop').each(function(){
    //         var shopText = $(this).text();
    //         console.log(shopText);
    //         if(shopText.indexOf(shop.trim())<0 && shopText!= '所属店铺'){
    //             $(this).parent('.people_title ').hide();
    //             nowLength -=1;
    //             console.log('所属区域已隐藏'+nowLength);
    //         }
    //     })
    // }
    // if(nowLength <= 1) {
    //     $('#peopleError').show();
    //     $('#peopleError div').text('没有匹配信息，请核对关键字重新搜索');
    // }
    data=null;
}
//获取活动执行情况
function getExecuteDetail(param){
    oc.postRequire("post", '/vipActivity/executeDetail',"0",param,function(data){
        if(data.code==0){
            if(data.message==''){
                $('.btnSecond').hide();
                $('.people_head').hide();
                $('.people').hide();
            }else {
                var message = JSON.parse(data.message);
                pieChart(message.complete_vips_count,message.target_vips_count);
                doExecuteDetail(message);
                var complete_vips_count = message.complete_vips_count;
                var userList =message.userList;
                staffData=userList;
                listShow(userList);
            }
        }else if(data.code==-1){
            console.log(data.message)
        }
    });
}
//获取活动详情
function getSelect(param){
    whir.loading.add("",0.5);//加载等待框
    var _params={
        "id":"",
        "message":param
    };

    $.ajax({
        url: '/activity/select',
        type: 'POST',
        dataType: "JSON",
        data:{
            param:JSON.stringify(_params)
        },
        success: function (data) {
            var message = JSON.parse(data.message);
            var activityVip = JSON.parse(message.activityVip);
            moreDetail=activityVip;
            var target_vips_count=activityVip.target_vips_count;
            var activity_state = activityVip.activity_state;
            var activity_theme = activityVip.activity_theme;
            var runMode = activityVip.run_mode;
            var beiginTime = activityVip.start_time;
            var endTime = activityVip.end_time;
            getExecuteDetail(target_vips_count);
            activityType(activity_state,activity_theme,runMode,beiginTime,endTime);
            whir.loading.remove();//移除加载框
            var corp_code = activityVip.corp_code;
            sessionStorage.setItem("corp_code",corp_code);//存储的方法
        },
        error: function (data) {
            console.log('获取活动详情失败');
        }
    });
}
//加载统计模块
function check(a,b){
    var TheTarget = a;
    var TheCover = b;
    // $('#TheTarget').text(TheTarget);
    // $('#TheCover').text(TheCover);
    table(TheTarget,TheCover);
    //canvas
   // var wd=parseFloat(window.getComputedStyle($('#c1').parent()[0]).width);
   //  var ht=parseFloat(window.getComputedStyle($('#c1').parent()[0]).height);
   //  $('#c1').attr('width',wd);
   //  $('#c1').attr('height',wd);
   //  // var ctx= $('#c1')[0].getContext('2d')
   //  // ctx.beginPath();
   //  // ctx.strokeStyle='red';
   //  // ctx.arc(wd/2,ht/2,wd/2,0,2*Math.PI);
   //  // ctx.fill();
   //  console.log();
   //  var c=document.getElementById("c1");
   //  var ctx=c.getContext("2d");
   //  ctx.beginPath();
   //  ctx.arc(wd/2,wd/2,50,0,2*Math.PI);
   //  ctx.stroke();
}
//加载活动状态
function activityType(activityState,activityTheme,runMode,beiginTime,endTime){
    var beiginTime = beiginTime;
    var endTime = endTime;
    var activityState = activityState;
    var activityTheme = activityTheme;
    var runMode = runMode;
    if(activityState =='执行中'){
        $('#activityState').css('color','#50acb4');
    }else if(activityState =='尚未开始'){
        $('#activityState').css('color','red');
    }else if(activityState =='已结束'){
        $('#activityState').css('color','blue');
    }
    $('#activityState').text(activityState);
    $('#activityTheme').text(activityTheme);
    $('#activityState2').text(runMode);
    $('#beiginTime').text(beiginTime);
    $('#endTime').text(endTime);
    $('#activityState').text()=='已结束'?$('#stop').hide():'';
}

//加载员工列表
function listShow(userList){
    // var userList=[{
    //     user_name:'员工1',
    //     user_code:10010,
    //     area_name:'南京',
    //     store_name:'店铺1',
    //     complete_rate:90
    //    },{
    //     user_name:'员工1',
    //     user_code:10010,
    //     area_name:'南京',
    //     store_name:'店铺1',
    //     complete_rate:90
    // }
    // ]
    $("#peopleContent").empty();
    $('.people').animate({scrollTop:0}, 'fast');
    var tempHTML = '<li class="people_title"> <div class="people_title_order ellipsis" style="text-align: center">${order}</div> <div class="people_title_name ellipsis"title="${title_name}">${name}</div> <div class="people_title_num ellipsis"title="${title_num}">${num}</div> <div class="people_title_area ellipsis" title="${title}">${area}</div> <div class="people_title_shop ellipsis"title="${title_shop}">${shop}</div> <div class="people_title_plan"> <div class="undone"><div class="done" style="width: ${percent}%"></div></div><span class="percent_percent">${percent}%</span></div> '
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
    }
    // var el=$('.people')[0];
    // if(!(el.scrollHeight > el.clientHeight)){
    //     $('.people').css('padding-right','9px');
    // };
}
//插件-饼图
function table(TheTarget,TheCover) {
    $('#TheTarget').text(TheTarget);
    $('#TheCover').text(TheCover);
    var NoCover = ((TheTarget - TheCover)/TheTarget*100).toFixed(2);
    // console.log(NoCover);
    var TheCover = (TheCover/TheTarget*100).toFixed(2);
    TheCover=TheCover>100?100:TheCover;
    // console.log('已覆盖'+TheCover+'%');
    require.config({
        paths: {
            echarts: '../js/dist'
        }
    });
    require(
        [
            'echarts',
            'echarts/chart/pie',  // 按需加载所需图表，如需动态类型切换功能，别忘了同时加载相应图表
            'echarts/chart/radar',
            'echarts/chart/map',
            'echarts/chart/bar',
            'echarts/chart/line'
        ],
        function (ec) {
            var myChart = ec.init(document.getElementById('main'));
            var labelTop = {
                normal : {
                    color:'#7bc7cd',
                    label : {
                        show : false,
                        position : 'center',
                        formatter : '{b}',
                        textStyle: {
                            baseline : 'bottom'
                        }
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
                            fontSize: 25
                        }
                    }
                },
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
            var radius = [55, 70];
                option = {
                series : [
                    {
                        type : 'pie',
                        center : ['50%', '50%'],
                        radius : radius,
                        x: '0%', // for funnel
                        itemStyle : labelFromatter,
                        //var NoCover = TheTarget - TheCover;

                        data : [
                            {name:'未覆盖会员数', value:NoCover,itemStyle :labelBottom },
                            {name:'已覆盖会员数', value:TheCover, itemStyle : labelTop}
                        ]
                    }
                ]
            };
            myChart.setOption(option);
            window.addEventListener("resize", function () {
                myChart.resize();
            });
        }
    );
}
//封装函数
//jq获取text();
function getText(name){
    return $(name).text();
}
//jq获取val();
function getVal(name){
    return $('name').val();
}
//获取更多
$('#rightMore').click(function () {
    whir.loading.add("",0.5);//加载等待框
    $('#loading').hide();
    $('#fab_describe').empty();
    //赋值
    $('#content .content_r ').each(function(a,obj){
        switch (a){
            case 0:active();break;
            case 1:  $(obj).html(moreDetail.activity_theme);;break;
            case 3: $('#beiginTime_1').html(moreDetail.start_time);$('#endTime_1').html(moreDetail.end_time);break;
            case 2:  $(obj).html(moreDetail.run_mode);break;
            case 4:  $(obj).html('已选'+JSON.parse(moreDetail.operators).length+'个');;break;
            case 5:  $(obj).html('已选'+moreDetail.target_vips_count+'个');break;
        }
        function active() {
            $(obj).html(moreDetail.activity_state);
            if(moreDetail.activity_state =='执行中'){
                $(obj).css('color','#50acb4');
            }else if(moreDetail.activity_state =='尚未开始'){
                $(obj).css('color','red');
            }else if(moreDetail.activity_state =='已结束'){
                $(obj).css('color','blue');
            }
        }
    });
    $('#fab_describe').append(moreDetail.activity_content);
    $('#get_more').show();
});
$('#get_more .head_span_r').click(function () {
    $('#get_more').hide();
    whir.loading.remove();//移除加载框
});
//页面加载数据
//每一条数据的详情按钮
// $('#peopleContent').on('click','li',function () {
//     var html='<div class="data_detail" style="float: right;margin-right: 5px;height: 40px"><div class="detail">详情 <span class="icon-ishop_8-03"></span></div></div>';
//     if( $(this).children().length<=6) {
//         var user_code = $(this).find('.people_title_num ').html();
//         $(this).append(html).attr('data_user_code', user_code);
//         $(this).siblings().each(function () {
//             if ($(this).children().length == 7) {
//                 $(this).find('div.data_detail').remove();
//             }
//             ;
//         });
//     }
// });
//详情弹框
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
    param.task_code=task_code.join(',');
    param.user_code=$(this).parent().find('.people_title_num  ').html();
    param.corp_code=corp_code;
    oc.postRequire("post","/vipActivity/userExecuteDetail","0",param,function(data){
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
        var arr=msg[0].vips;
        var html='';
        $('#showList3 tbody').empty();
        for(var i=0;i<arr.length;i++){
            var a=i+1;
            var status='';
            if(arr[i].is_assort=="N"){
                status='未分配'
            }else if(arr[i].is_assort=='Y'){
               if(arr[i].status=='Y'){
                   status='已执行'
               }else{
                   status='未执行'
               }
            }
            html+="<tr><td>"+a+"</td><td>"+arr[i].user_name+"</td><td>"+arr[i].user_code+"</td><td>"+status+"</td></tr>"
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
    var param={};
    var _param={};
    param.activity_code=sessionStorage.getItem('activity_code');
    whir.loading.add("", 0.5);
    oc.postRequire("post","/vipActivity/select","0",param,function(data){
        if(data.code==0){
            var active_data=JSON.parse(JSON.parse(data.message).activityVip);
            corp_code=active_data.corp_code;
            _param.corp_code=active_data.corp_code;
            _param.activity_code=active_data.activity_code;
            _param.task_code=active_data.task_code.split(',')[0];
            doActiveResponse(active_data);
            _param.task_code==''?'':getExecuteDetail(_param);
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
function   doExecuteDetail(message) {
    $('.shoppers').html(message.user_count);
    $('.covers').html(message.complete_vips_count);
    $('.target').html(message.target_vips_count);
    var taskInfo=JSON.parse(message.taskInfo);
    var start_time=new Date();
    var end_time=new Date(taskInfo.target_end_time);
    var time=(end_time.getTime()-start_time.getTime())/1000;
    var d=parseInt(time/(24*60*60));
    d<=0&&(d=0);
    $('.task_detail_l .p1').html(taskInfo.task_title);
    $('.task_detail_l .p2').html(taskInfo.task_type_name);
    $('.task_detail_l .p3').html(store);
    $('.task_detail_l .p4').html(taskInfo.target_start_time+' 开始');
    $('.task_detail_l .p5').html(taskInfo.target_end_time+' 结束');
    $('.task_detail_l .p6').html(d);
    $('.task_detail_l .p7').html(taskInfo.task_description);
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
                position : 'center',
                formatter : '{b}',
                textStyle: {
                    baseline : 'bottom'
                }
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
                    fontSize: 25
                }
            }
        },
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
    var radius = [55, 70];
    var  option = {
        series : [
            {
                type : 'pie',
                center : ['50%', '50%'],
                radius : radius,
                x: '0%', // for funnel
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
    if(active_data.activity_store_code==''){
        store=0;
    }else{
        var store_obj=JSON.parse(active_data.activity_store_code);
        action.store_code=store_obj;
        for(var i=0;i<store_obj.length;i++){
            store_code.push(store_obj[i].store_code);
        }
        store=store_code.length;
        add_store_code=store_code.join(',');
    }
    //action存值
    action.activity_code=active_data.activity_code;
    action.start_time=active_data.start_time;
    action.end_time=active_data.end_time;
    min_date=active_data.start_time;
    //run_mode
    var run_mode='';
    switch (active_data.run_mode){
        case 'recruit':run_mode='招募';break;
        case 'h5':run_mode='H5';break;
        case 'sales':run_mode='促销';break;
        case 'coupon':run_mode='优惠券';break;
        case 'invite':run_mode='线下邀约';break;
        case 'festival':run_mode='节日';break;
    }
    //time
    var over=active_data.end_time;
    var start=active_data.start_time;
    setInterval(function () {
        var over_time=new Date(over);
        var now_time=new Date();
        var time=(over_time.getTime()-now_time.getTime())/1000;
        var d=parseInt(time/(24*60*60));
        var h=parseInt((time-d*(24*60*60))/(60*60));
        var m=parseInt((time-d*(24*60*60)-h*(60*60))/60);
        var s=parseInt(time-d*(24*60*60)-h*(60*60)-m*60);
        d<=0&&(d=0);
        h<=0&&(h=0);
        m<=0&&(m=0);
        s<=0&&(s=0);
        $('.header_m .p_2').html(d+'天'+h+'小时'+m+'分钟'+s+'秒');
    },1000);
    var over_time=new Date(over);
    var now_time=new Date();
    var time=(over_time.getTime()-now_time.getTime())/1000;
    var d=parseInt(time/(24*60*60));
    var h=parseInt((time-d*(24*60*60))/(60*60));
    var m=parseInt((time-d*(24*60*60)-h*(60*60))/60);
    var s=parseInt(time-d*(24*60*60)-h*(60*60)-m*60);
    d<=0&&(d=0);
    h<=0&&(h=0);
    m<=0&&(m=0);
    s<=0&&(s=0);
    $('.header_m .p_2').html(d+'天'+h+'小时'+m+'分钟'+s+'秒');
    $('.title_l').html(active_data.activity_theme);
    $('.time_l').html(active_data.start_time);//处理
    $('.time_r').html(active_data.end_time);
    $('.name_l').html(run_mode);//转换
    $('.name_r').html(active_data.corp_name);
    $('.header_sm .p_2').html(store);//处理
    //dom节点控制
    task_code=active_data.task_code.split(',');
    var active_dom=active_data.task_code.split(',');
    if(active_data.task_code==''){
        $('#progress').hide();
        nav_role='N';
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
    $(this).addClass('nav_action').siblings('.nav_action').removeClass('nav_action');
    if(nav_role=='N')return;
    if($(this).html().trim()=='任务完成度分析'){
        $('#progress').show()
        $('#customer').hide();
    }else{
        $('#customer').show();
        $('#progress').hide()
    }
});
//任务控制
$('.progress_nav').on('click','.progress_nav_btn',function () {
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
    actionPost(this.className.trim());
});
$('.action_footer_r').click(function () {
    actionPost(this.className.trim());
});
$('.add_footer_r').click(function () {
    var obj=[];
    $('#showList tr td:nth-child(2)').each(function () {
        var param={}
        param.store_name=$(this).html();
        param.store_code=this.dataset.code;
        obj.push(param);
    });
    actionPost(this.className.trim(),obj);
});
//修改活动时间
$('.action_time').click(function () {
    laydate_start = {
        min: laydate.now()
        ,max: '2099-06-16 23:59:59'
        ,istoday: false
        , istime: true //是否开启时间选择
        , format: 'YYYY-MM-DD hh:mm:ss' //日期格式
        ,choose: function(datas){
            end.min = datas; //开始日选好后，重置结束日的最小日期
            end.start = datas //将结束日的初始值设定为开始日
        }
        ,  elem: '#start_time', //需显示日期的元素选择器
    };
    laydate_end= {
        min: laydate.now()
        ,max: '2099-06-16 23:59:59'
        , istime: true //是否开启时间选择
        , format: 'YYYY-MM-DD hh:mm:ss' //日期格式
        ,istoday: false
        ,choose: function(datas){
            start.max = datas; //结束日选好后，重置开始日的最大日期
        },
        elem: '#over_time', //需显示日期的元素选择器
    };
    var s=new Date(min_date);
    var n=new Date();
    if(s<n){
        $('#start_time').attr('disabled','disabled');
        $('#start_time').removeAttr('onclick');
    }
    $('.modify_time_box').show();
    $('#start_time').val(action.start_time);
    $('#over_time').val(action.end_time);
});
function actionPost(role,obj) {//第二个参数是store_code
    if(obj){action.store_code=obj;}
    if(role=='action_footer_r'){
        action.status=2;
        action.start_time='';
        action.end_time='';
    }else{
        action.status='';
    }
    oc.postRequire("post","/vipActivity/changeState","0",action,function(data){
        if(data.code==0){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: '保存成功'
            });
            location.reload();
        }else if (data.code==-1){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    });
}
$('#return_back').click(function () {
    window.location.href='activity.html';
});
function storeShow(store_code) {
    var role=arguments[1];
    var param={};
    param.corp_code=corp_code;
    param.store_code=store_code;
    whir.loading.add("", 0.5);
    oc.postRequire("post","/shop/getStoreList","0",param,function(data){
        if(data.code==0){
            storeShowList(JSON.parse(JSON.parse(data.message).list),role)
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
    var html=[];
    $('#showList tbody').empty();
    $('#showList2 tbody').empty();
   for(var i=0;i<arr.length;i++){
     html.push('<tr><td>'+(i+1)+'</td><td data-code="'+arr[i].store_code+'">'+arr[i].store_name+'</td><td>'
         +arr[i].province+arr[i].city+arr[i].area+arr[i].street+'</td></tr>');
   }
    console.log(arguments);
   arguments[1]?$('#showList2 tbody').html(html.join(',')):$('#showList tbody').html(html.join(','));
}
$('.add_header_r').click(function () {
    var left=($('.add_store_box').width()-$("#screen_shop").width())/2;
    var tp=50;
    $('.add_store').hide();
    $("#screen_shop").show();
    $("#screen_shop").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_staff .screen_content_l").unbind("scroll");
    getstorelist(1)
});
//参与门店
$('.task_list').click(function () {
    $('.join_store_box').toggle();
    storeShow(add_store_code,'store');
});
// window.onload = function(){
//     //获取活动执行情况
//     // getExecuteDetail();
//     // getSelect(param);
//     //加载统计模块
//     // check();
//     //加载活动状态
//    //activityType();
//     //加载员工列表
//     //listShow();
//     $($('.btnSecond input')[2]).attr('checked','true');
// }
//点击列表显示选中状态
$(function () {
    getActive();
    $($('.btnSecond input')[2]).attr('checked','true');
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
})
//品牌搜索
$("#brand_search").keydown(function(){
    var event=window.event||arguments[0];
    if(event.keyCode==13){
        $("#screen_brand .screen_content_l ul").empty();
        getbrandlist();
    }
})
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
})
//店铺放大镜搜索
$("#store_search_f").click(function(){
    shop_num=1;
    isscroll=false;
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    getstorelist(shop_num);
})
//区域放大镜收索
$("#area_search_f").click(function(){
    area_num=1;
    isscroll=false;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    getarealist(area_num);
})
$("#staff_search_f").click(function(){
    staff_num=1;
    isscroll=false;
    $("#screen_staff .screen_content_l").unbind("scroll");
    $("#screen_staff .screen_content_l ul").empty();
    getstafflist(staff_num);
})
$("#brand_search_f").click(function(){
    $("#screen_brand .screen_content_l ul").empty();
    getbrandlist();
})
//区域关闭
$("#screen_close_area").click(function(){
    $("#screen_area").hide();
    $("#screen_shop").show();
    // whir.loading.remove();//移除遮罩层
})
//员工关闭
$("#screen_close_staff").click(function(){
    $("#screen_staff").hide();
    // whir.loading.remove();//移除遮罩层
})
//店铺关闭
$("#screen_close_shop").click(function(){
    $('.add_store').show();
    $("#screen_shop").hide();
    // whir.loading.remove();//移除遮罩层
    $("#screen_shop .screen_content_l").unbind("scroll");
    storeShow(add_store_code);
})
//品牌关闭
$("#screen_close_brand").click(function(){
    $("#screen_brand").hide();
    $("#screen_shop").show();
    // whir.loading.remove();//移除遮罩层
})
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
};
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
                $("#screen_shop .screen_content_l").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(shop_next){
                            return;
                        }
                        getstorelist(shop_num);
                    };
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
                $("#screen_staff .screen_content_l").bind("scroll",function () {
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= nScrollHight){
                        if(staff_next){
                            return;
                        }
                        getstafflist(staff_num);
                    };
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
})
//点击店铺的确定
$("#screen_que_shop").click(function(){
    var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
    var store_code="";
    var name=""
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
    //拼接store-code
    $('.add_store').show();
    $("#screen_shop").hide();
    //请求门店
    store_code=store_code+add_store_code;
    storeShow(store_code)
})
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
    var left=($('.add_store_box').width()-$("#screen_shop").width())/2;
    var tp=25;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    $("#screen_area").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_area").show();
    $("#screen_shop").hide();
    getarealist(area_num);
})
//店铺里面的品牌点击
$("#shop_brand").click(function(){
    var left=($('.add_store_box').width()-$("#screen_shop").width())/2;
    var tp=25;
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_brand").show();
    $("#screen_shop").hide();
    getbrandlist();
})
//员工里面的区域点击
$("#staff_area").click(function(){
    console.log(123);
    isscroll=false;
    area_num=1;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[1]-$("#screen_shop").height())/2+63;
    $("#screen_area .screen_content_l").unbind("scroll");
    $("#screen_area .screen_content_l ul").empty();
    $("#screen_area").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_area").show();
    $("#screen_staff").hide();
    getarealist(area_num);
})
//员工里面的店铺点击
$("#staff_shop").click(function(){
    isscroll=false;
    shop_num=1;
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[1]-$("#screen_shop").height())/2+63;
    $("#screen_shop .screen_content_l").unbind("scroll");
    $("#screen_shop .screen_content_l ul").empty();
    $("#screen_shop").css({"left":+left+"px","top":+tp+"px"});
    $("#screen_shop").show();
    $("#screen_staff").hide();
    getstorelist(shop_num);
})
//员工里面的品牌点击
$("#staff_brand").click(function(){
    var arr=whir.loading.getPageSize();
    var left=(arr[0]-$("#screen_shop").width())/2;
    var tp=(arr[1]-$("#screen_shop").height())/2+63;
    $("#screen_brand .screen_content_l ul").empty();
    $("#screen_brand").css({"left":+left+"px","top":+tp+"px"});
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
})
$("#send").click(function(){
    var param={};
    var corp_code = $('#OWN_CORP').val();//企业编号
    var send_mode=$('#send_mode').attr("data-type");
    var title=$("#message_title").val();
    var message_content=$("#message_content").val();
    param["corp_code"]=corp_code;
    param["title"]=title;
    param["message_content"]=message_content;
    param["receiver_type"]=send_mode;
    if(corp_code==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "所属企业不能为空"
        });
        return;
    }
    if(send_mode==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "发送范围不能为空"
        });
        return;
    }
    if(send_mode=="corp"){
        param["store_id"]="";
        param["user_id"]="";
        param["area_code"]="";
    }
    if(send_mode=="area"){
        var area_code=$("#sendee_r").attr("data-code");
        var area_code=area_code.split(",");
        var area_codes=[];
        for(var i=0;i<area_code.length;i++){
            var param1={"area_code":area_code[i]};
            area_codes.push(param1);
        }
        param["store_id"]="";
        param["user_id"]="";
        param["area_code"]=area_codes;
        if(area_code==""){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "接受对象不能为空"
            });
            return;
        }
    }
    if(send_mode=="store"){
        var store_id=$("#sendee_r").attr("data-code");
        var store_id=store_id.split(",");
        var store_ids=[];
        for(var i=0;i<store_id.length;i++){
            var param1={"store_id":store_id[i]};
            store_ids.push(param1);
        }
        param["store_id"]=store_ids;
        param["user_id"]="";
        param["area_code"]="";
        if(store_id==""){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "接受对象不能为空"
            });
            return;
        }
    }
    if(send_mode=="staff"){
        var user_id=$("#sendee_r").attr("data-code");
        var phone=$("#sendee_r").attr("data-phone");
        var user_id=user_id.split(",");
        var phone=phone.split(",");
        var user_ids=[];
        for(var i=0;i<user_id.length;i++){
            var param1={"user_id":user_id[i],"phone":phone[i]};
            user_ids.push(param1);
        }
        param["store_id"]="";
        param["user_id"]=user_ids;
        param["area_code"]="";
        if(user_id==""){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "接受对象不能为空"
            });
            return;
        }
    }
    if(title==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "通知标题不能为空"
        });
        return;
    }
    if(message_content==""){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            content: "通知内容不能为空"
        });
        return;
    }
    whir.loading.add("",0.5);//加载等待框
    oc.postRequire("post","/message/add","",param, function(data){
        if(data.code=="0"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "发送成功"
            });
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "发送失败"
            });
        }
        whir.loading.remove();//移除加载框
    })
});