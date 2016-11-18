/**
 * Created by Administrator on 2016/11/16.
 */

//停止活动
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
    $(".percent_percent").each(function(){
        var nowVal=($(this).text().replace('%',''));
        if(nowVal<100){
            $(this).parents('.people_title ').hide();
            console.log('隐藏了。。。！');
        }
    })
});
//显示未完成
$('#showDoing').click(function(){
    listShow();
    $(".percent_percent").each(function(){
        var nowVal=($(this).text().replace('%',''));
        if(nowVal==100||nowVal==0){
            $(this).parents('.people_title ').hide();
            console.log('隐藏了。。。！');
        }
    })
})
//加载统计模块
function check(){
    var TheTarget = '325656';
    var TheCover = '244242';
    table(TheTarget,TheCover);
}
//加载活动状态
function activityType(){
    var beiginTime = '2016-9-12';
    var endTime = '2016-12-12'
    var activityState = '正在执行';
    var activityTheme = '华东区域门店换季促销'
    var activityState2 = '执行中';
    if(activityState =='正在执行'){
        $('#activityState').css('color','#50acb4');
    }else if(activityState =='尚未开始'){
        $('#activityState').css('color','red');
    }else if(activityState =='已结束'){
        $('#activityState').css('color','blue');
    }
    $('#activityState').text(activityState);
    $('#activityTheme').text(activityTheme);
    $('#activityState2').text(activityState2);
    $('#beiginTime').text(beiginTime);
    $('#endTime').text(endTime);
}

//加载员工列表
function listShow(){
    $('.people').animate({scrollTop:0}, 'fast');
    var name = '张某某';
    var num = '100000';
    var area = '华东一区';
    var shop='上海闵行分店一店上海闵行分店一店';
    var percent = Math.floor(Math.random()*40+60);
    var tempHTML = '<li class="people_title"> <div class="people_title_order" style="text-align: center">${order}</div> <div class="people_title_name">${name}</div> <div class="people_title_num">${num}</div> <div class="people_title_area">${area}</div> <div class="people_title_shop">${shop}</div> <div class="people_title_plan"> <div class="undone"><div class="done"></div></div><span class="percent_percent">${percent}%</span></div> </li>';
    for(i=0;i<11;i++){
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
    }
}

//插件-饼图
function table(TheTarget,TheCover) {
    $('#TheTarget').text(TheTarget);
    $('#TheCover').text(TheCover);
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
            //var TheTarget = cache.TheTarget;
            console.log(TheTarget);
            //var TheCover = cache.TheCover;
            console.log(TheCover);
            var msg = [
                {value: TheTarget, name: "目标会员数"},
                {value: TheCover, name: '已覆盖会员数'},
            ];
            var myChart = ec.init(document.getElementById('main'));
            var option = {
                color: ['#7bc7cd', '#eaeaea'],
                series: [
                    {
                        name: '消费分类',
                        center: ['50%', '50%'],
                        type: 'pie',
                        radius: ['80%', '100%'],
                        itemStyle: {
                            normal: {
                                label: {
                                    show: false
                                },
                                labelLine: {
                                    show: false
                                }
                            },
                            emphasis: {
                                label: {
                                    show: false,
                                    position: 'center',
                                    textStyle: {
                                        fontSize: '20',
                                        fontWeight: 'bold'
                                    }
                                }
                            }
                        },
                        data: msg
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
//页面加载数据
window.onload = function(){
    //加载统计模块
    check();
    //加载活动状态
   activityType();
    //加载员工列表
    listShow();
}
