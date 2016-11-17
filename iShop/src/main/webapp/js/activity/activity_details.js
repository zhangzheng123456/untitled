/**
 * Created by Administrator on 2016/11/16.
 */
//员工列表加载
function listShow(){
    var name = '张某某';
    var num = '10000';
    var area = '华东一区';
    var shop='上海敏行分店一店';
    var percent = Math.floor(Math.random()*40+60);
    var tempHTML = '<li class="people_title"> <div class="people_title_order" style="text-align: center">${order}</div> <div class="people_title_name">${name}</div> <div class="people_title_num">${num}</div> <div class="people_title_area">${area}</div> <div class="people_title_shop">${shop}</div> <div class="people_title_plan"> <div class="undone"><div class="done"></div></div><span class="percent_percent">${percent}%</span></div> </li>';
    for(i=0;i<11;i++){
        //判断进度颜色
        if(percent<100&&percent>0){
            $('.percent_percent').css('color','#42a0a8');
        }else if(percent==100){
            $('.percent_percent').css('color','blue');
        }else if(percent==0){
            $('.percent_percent').css('color','red');
        }
        var html = '';
        var order = i+1;
        var nowHTML1 = tempHTML;
        nowHTML1 = nowHTML1.replace("${order}", order);
        nowHTML1 = nowHTML1.replace("${name}", order);
        nowHTML1 = nowHTML1.replace("${num}", num);
        nowHTML1 = nowHTML1.replace("${area}", area);
        nowHTML1 = nowHTML1.replace("${shop}", shop);
        $('.done').css('width',percent+'%');
        nowHTML1 = nowHTML1.replace("${percent}", percent);
        html += nowHTML1;
        $("#peopleContent").append(html);
    }
}
window.onload = function(){
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
    listShow()
}
