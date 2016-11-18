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
//加载统计
function check(){
    var TheTarget = '325656';
    var TheCover = '244242';
    $('#TheTarget').text(TheTarget);
    $('#TheCover').text(TheCover);
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
//员工列表加载
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
//页面加载假数据
window.onload = function(){
    check();
   activityType();
    listShow();
}
