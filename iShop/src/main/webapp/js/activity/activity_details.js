//停止活动
var moreDetail={};//更多详情
var staffData=[];//页面的数据
var role=1;
var left=($(window).width()-$("#tk").width())/2;//弹框定位的left值
var tp=($(window).height()-$("#tk").height())/2;//弹框定位的top值
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
    $('#screening').slideUp(600);
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
    $('#screening').slideUp(600);
});
//显示全部
$('#showAll').click(function(){
    listShow(staffData);
    var nowLength = $('.people_title').length;
    if(nowLength <=1) {
        $('#peopleError div').text('暂无数据');
        $('#peopleError').show();
    }
    $('#screening').slideUp(600);
});
//点击radio时
$('.btnSecond .radio_b').click(function () {
    $(this).next().trigger('click');
});
//筛选按钮
$('#choose').click(function(){
    $('#screening').slideToggle(600);
    // listShow();
});
//清空筛选
$('#empty').click(function(){
    $('.btnSecond input:checked').removeAttr('checked');
    $('.inputs input').val('');
    listShow(staffData);
    var nowLength = $('.people_title').length;
    if(nowLength <=0) {
        $('#peopleError div').text('暂无数据');
        $('#peopleError').show();
    }
});
//收起
$('#pack_up').click(function(){
    $('#screening').slideToggle(600);
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
$("#p1").keydown(function(){
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        $('#find').trigger('click');
    }
});
$("#p2").keydown(function(){
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        $('#find').trigger('click');
    }
});
$("#p3").keydown(function(){
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        $('#find').trigger('click');
    }
});
$("#p4").keydown(function(){
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        $('#find').trigger('click');
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
            if((param.indexOf(2)!=-1)&&(val.area_name.search(area)!=-1)){data.push(val)};
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
                if ((val.user_name.search(name)!=-1) && (val.area_name.search(area)!=-1)) {
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
                if ((val.user_code.search(num)!=-1) && (val.area_name.search(area)!=-1)) {
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
                if ((val.area_name.search(area)!=-1) && (val.store_name.search(shop)!=-1)) {
                    data.push(val)
                }
            })
        }
    }else if(param.length==3){
        if((param.indexOf(0)!=-1) && (param.indexOf(1)!=-1)&&  (param.indexOf(2)!=-1) ){   // 012
            staffData.map(function (val,index,arr) {
                if( (val.user_name.search(name)!=-1)&&(val.user_code.search(num)!=-1)&&(val.area_name.search(area)!=-1) ){
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
                if( (val.user_name.search(name)!=-1)&&(val.area_name.search(area)!=-1)&&(val.store_name.search(shop)!=-1) ){
                    data.push(val)
                }
            });
        }else if((param.indexOf(1)!=-1) && (param.indexOf(2)!=-1)&&  (param.indexOf(3)!=-1)){  //123
            staffData.map(function (val,index,arr) {
                if( (val.user_code.search(num)!=-1)&&(val.area_name.search(area)!=-1)&&(val.store_name.search(shop)!=-1) ){
                    data.push(val)
                }
            });
        }
    }else if(param.length==4){
        staffData.map(function (val,index,arr) {
            if( (val.user_name.search(name)!=-1)&&(val.user_code.search(num)!=-1)&&(val.area_name.search(area)!=-1)&&(val.store_name.search(shop)!=-1) ){
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
function getExecuteDetail(vip_count){
    var id=sessionStorage.getItem("id");//获取
    var _params={
        "id":"",
        "message":{
            "id":id
        }
    };
    $.ajax({
        url: '/activity/executeDetail',
        type: 'POST',
        dataType: "JSON",
        data:{
            param:JSON.stringify(_params)
        },
        success: function (data) {
            if(data.message==''){
                $('.btnSecond').hide();
                $('.people_head').hide();
                $('.people').hide();
                check(vip_count,vip_count);
            }else {
                var message = JSON.parse(data.message);
                var complete_vips_count = message.complete_vips_count;
                var userList =message.userList;
                staffData=userList;
                listShow(userList);
                check(vip_count,complete_vips_count);
            }
        },
        error: function (data) {
            console.log('获取活动执行情况失败');
        }
    });
}
//获取活动详情
function getSelect(id){
    whir.loading.add("",0.5);//加载等待框
    var _params={
        "id":"",
        "message":{
            "id":id
        }
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
    $("#peopleContent").empty();
    $('.people').animate({scrollTop:0}, 'fast');
    var tempHTML = '<li class="people_title"> <div class="people_title_order ellipsis" style="text-align: center">${order}</div> <div class="people_title_name ellipsis"title="${title_name}">${name}</div> <div class="people_title_num ellipsis"title="${title_num}">${num}</div> <div class="people_title_area ellipsis" title="${title}">${area}</div> <div class="people_title_shop ellipsis"title="${title_shop}">${shop}</div> <div class="people_title_plan"> <div class="undone"><div class="done" style="width: ${percent}%"></div></div><span class="percent_percent">${percent}%</span></div> </li>';
    for(var i=0;i<userList.length;i++) {
        //随机进度
        var html = '';
        var order = i + 1;
        var nowHTML1 = tempHTML;
        var percent = userList[i].complete_rate;
        nowHTML1 = nowHTML1.replace("${order}", order);
        nowHTML1 = nowHTML1.replace("${name}", userList[i].user_name);
        nowHTML1 = nowHTML1.replace("${title_name}", userList[i].user_name);
        nowHTML1 = nowHTML1.replace("${num}", userList[i].user_code);
        nowHTML1 = nowHTML1.replace("${title_num}", userList[i].user_code);
        nowHTML1 = nowHTML1.replace("${area}", userList[i].area_name);
        nowHTML1 = nowHTML1.replace("${title}", userList[i].area_name);
        nowHTML1 = nowHTML1.replace("${shop}", userList[i].store_name);
        nowHTML1 = nowHTML1.replace("${title_shop}", userList[i].store_name);
        nowHTML1 = nowHTML1.replace("${percent}", percent);
        nowHTML1 = nowHTML1.replace("${percent}", percent);
        html += nowHTML1;
        $("#peopleContent").append(html);
        //判断进度颜色
        if (percent < 100 && percent > 0) {
            $('.percent_percent').css('color', '#42a0a8');
        } else if (percent == 100) {
            $('.percent_percent').css('color', '#42a0a8');
        } else if (percent == 0) {
            $('.percent_percent').css('color', '#42a0a8');
        }
        var nowLength = $('.people_title').length;
        if (nowLength > 1) {
            $('#peopleError').hide();
        }
    }
    var el=$('.people')[0];
    if(!(el.scrollHeight > el.clientHeight)){
        $('.people').css('padding-right','9px');
    };
}
//插件-饼图
function table(TheTarget,TheCover) {
    $('#TheTarget').text(TheTarget);
    $('#TheCover').text(TheCover);
    var NoCover = ((TheTarget - TheCover)/TheTarget*100).toFixed(2);
    // console.log('未覆盖'+NoCover+'%');
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
                            console.log((100 - params.value).toString().length);
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
    $('#loading').remove();
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
window.onload = function(){
    //获取活动执行情况
    // getExecuteDetail();
    getSelect(sessionStorage.getItem('id'));
    //加载统计模块
    // check();
    //加载活动状态
   //activityType();
    //加载员工列表
    //listShow();
    $($('.btnSecond input')[2]).attr('checked','true');
}
