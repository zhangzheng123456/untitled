/**
 * Created by Administrator on 2016/11/16.
 */

//停止活动
var moreDetail={};//更多详情
function stop(){
    window.location.href = 'activity.html';
}
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
    listShow();
    var nowLength = $('.people_title').length;
    $(".percent_percent").each(function(){
        var nowVal=($(this).text().replace('%',''));
        if(nowVal<100){
            $(this).parents('.people_title ').hide();
            nowLength -=1;
            console.log('隐藏了。。。！');
        }
    })
    if(nowLength <=1) {

        $('#peopleError').show();
        $('#peopleError div').text('未发现已完成');
    }
});
//显示未完成
$('#showDoing').click(function(){
    listShow();
    var nowLength = $('.people_title').length;
    $(".percent_percent").each(function(){
        var nowVal=($(this).text().replace('%',''));
        if(nowVal==100||nowVal==0){
            $(this).parents('.people_title ').hide();
            nowLength -=1;
            console.log('隐藏了。。。！');
        }
    })
    if(nowLength <=1) {
        $('#peopleError div').text('未发未完成')
        $('#peopleError').show();
    }
})
//筛选按钮
$('#choose').click(function(){
    $('#screening').slideToggle(600);
    listShow();
});
//清空筛选
$('#empty').click(function(){
    $('.inputs input').val('');
    listShow();
});
//收起
$('#pack_up').click(function(){
    $('#screening').slideToggle(600);
    listShow();
})
//查找
$('#find').click(function(){
    var nowLength = $('.people_title').length;
    var name = $('#p1').val();
    var num = $('#p2').val();
    var area = $('#p3').val();
    var shop = $('#p4').val();
    search(name,num,area,shop,nowLength);

});
function search(name,num,area,shop,nowLength){
    if(name!=''){
        $('.people_title_name').each(function(){
            var nameText = $(this).text();
            console.log(nameText);
            if(nameText.indexOf(name.trim())<0 && nameText!= '员工姓名'){
                $(this).parent('.people_title ').hide();
                nowLength -=1;
                console.log('员工姓名已隐藏'+nowLength);
            }
        })
    }

    if(num!=''){
        $('.people_title_num').each(function(){
            var numText = $(this).text();
            console.log(numText);
            if(numText.indexOf(num.trim())<0 && numText!= '员工编号'){
                $(this).parent('.people_title ').hide();
                nowLength -=1;
                console.log('员工编号已隐藏'+nowLength);
            }
        })
    }
    if(area!=''){
        console.log(area);
        $('.people_title_area').each(function(){
            var areaText = $(this).text();
            console.log(areaText);
            if(areaText.indexOf(area.trim())<0 && areaText!= '所属区域'){
                $(this).parent('.people_title ').hide();
                nowLength -=1;
                console.log('所属区域已隐藏'+nowLength);
            }
        })
    }
    if(shop!=''){
        console.log(shop);
        $('.people_title_shop').each(function(){
            var shopText = $(this).text();
            console.log(shopText);
            if(shopText.indexOf(shop.trim())<0 && shopText!= '所属店铺'){
                $(this).parent('.people_title ').hide();
                nowLength -=1;
                console.log('所属区域已隐藏'+nowLength);
            }
        })
    }
    if(nowLength <= 1) {
        $('#peopleError').show();
        $('#peopleError div').text('没有匹配信息，请核对关键字重新搜索');
    }

}

//获取活动执行情况
function getExecuteDetail(){
    var id=sessionStorage.getItem("id");//获取
    console.log('获取的id是：'+id)
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
            var message = JSON.parse(data.message);
            // console.log('获取活动执行情况'+JSON.stringify(message));
            getSelect(id);
            var complete_vips_count = message.complete_vips_count;
            var userList =message.userList;
            var target_vips_count = message.target_vips_count;
            var count = userList.length;
            for(var i=0;i<userList.length;i++){
                var name = userList[i].user_name;
                var num = userList[i].user_code;
                var area = userList[i].area_name;
                var shop = userList[i].store_name;
                var rate = userList[i].complete_rate;
                listShow(name,num,area,shop,rate,count);
            }
            // console.log(complete_vips_count);
            // console.log(userList);
            // console.log(target_vips_count);
            check(target_vips_count,complete_vips_count);
        },
        error: function (data) {
            console.log('获取活动执行情况失败');
        }
    });
}
//获取活动详情
function getSelect(id){
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
            var activity_state = activityVip.activity_state;
            var activity_theme = activityVip.activity_theme;
            var runMode = activityVip.run_mode;
            var beiginTime = activityVip.start_time;
            var endTime = activityVip.end_time;
            activityType(activity_state,activity_theme,runMode,beiginTime,endTime);
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
    table(TheTarget,TheCover);
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
}

//加载员工列表
function listShow(name,num,area,shop,rate,count){
    $('.people').animate({scrollTop:0}, 'fast');
    var name = name;
    var num = num;
    var area = area;
    var shop = shop;
    // var percent = Math.floor(Math.random()*40+60);
    var percent = rate;
    // console.log('员工姓名：'+name+'员工编号：'+num+'所属区域'+area+'所属店铺'+shop+'百分比：'+percent);
    var tempHTML = '<li class="people_title"> <div class="people_title_order ellipsis" style="text-align: center">${order}</div> <div class="people_title_name ellipsis">${name}</div> <div class="people_title_num ellipsis">${num}</div> <div class="people_title_area ellipsis"title="${area}">${area}</div> <div class="people_title_shop ellipsis">${shop}</div> <div class="people_title_plan"> <div class="undone"><div class="done"></div></div><span class="percent_percent">${percent}%</span></div> </li>';
    console.log(tempHTML);
    for(var i=0;i<count;i++){
        //随机进度
        var html = '';
        var order = i+1;
        var nowHTML1 = tempHTML;
        nowHTML1 = nowHTML1.replace("${order}", order);
        nowHTML1 = nowHTML1.replace("${name}", name);
        nowHTML1 = nowHTML1.replace("${num}", num);
        nowHTML1 = nowHTML1.replace("${area}", area);
        nowHTML1 = nowHTML1.replace("${shop}", shop);
        $('.done').css('width',percent+'%');
        nowHTML1 = nowHTML1.replace("${percent}", percent);
        html += nowHTML1;
        $("#peopleContent").append(html);
        //判断进度颜色
        if(percent<100&&percent>0){
            $('.percent_percent').css('color','#42a0a8');
        }else if(percent==100){
            $('.percent_percent').css('color','blue');
        }else if(percent==0){
            $('.percent_percent').css('color','red');
        }
        var nowLength = $('.people_title').length;
        if(nowLength >1) {
            $('#peopleError').hide();
        }
    }
}

//插件-饼图
function table(TheTarget,TheCover) {
    $('#TheTarget').text(TheTarget);
    $('#TheCover').text(TheCover);
    var NoCover = ((TheTarget - TheCover)/TheTarget*100).toFixed(2);
    console.log('未覆盖'+NoCover+'%');
    var TheCover = (TheCover/TheTarget*100).toFixed(2);
    console.log('已覆盖'+TheCover+'%');
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
                            return 100 - params.value + '%'
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
$('#rightMore').click(function () {
    whir.loading.add("",0.5);//加载等待框
    $('#loading').remove();
    //赋值
    $('#content .content_r ').each(function(a,obj){
        switch (a){
            case 0:active();break;
            case 1:  $(obj).html(moreDetail.activity_theme);;break;
            case 2: $('#beiginTime_1').html(moreDetail.start_time);$('#endTime_1').html(moreDetail.end_time);break;
            case 3:  $(obj).html(moreDetail.run_mode);;break;
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
    getExecuteDetail();
    //加载统计模块
    // check();
    //加载活动状态
   //activityType();
    //加载员工列表
    //listShow();
}
