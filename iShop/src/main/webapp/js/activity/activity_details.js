/**
 * Created by Administrator on 2016/11/16.
 */
//禁用所有input
$(":input").attr("disabled",true);
//刷新
function reload(){
    window.location.reload();
}
//删除
function remove(){
    confirm('确认删除？');
}
//页面加载请求数据
function pageVal(){
    var activityTheme = '华东区域门店换季促销';
    var activityState = '执行中';
    var activityBeginTime = '2016-09-12';
    var activityEndTime = '2016-12-12';
    var activityWay = '电话通知';
    var activityExecutorNum = '32';
    var theVipNum = '325656';
    var describeActivities = '测试';
    var setTime = '2016-03-03 12:39:22';
    var setPeople = '韦海鸥';
    var changeTime = '2016-03-02 16:55:37';
    var changePeople = '陈磊'
    replaceInput(activityTheme,activityState,activityBeginTime,activityEndTime,activityWay,activityExecutorNum,theVipNum,describeActivities,setTime,setPeople,changeTime,changePeople)
}
//动态加载input内容
function replaceInput(activityTheme,activityState,activityBeginTime,activityEndTime,activityWay,activityExecutorNum,theVipNum,describeActivities,setTime,setPeople,changeTime,changePeople){
    $('#activityTheme').val(activityTheme);
    $('#activityState').val(activityState);
    $('#activityBeginTime').val(activityBeginTime);
    $('#activityEndTime').val(activityEndTime);
    $('#activityWay').val(activityWay);
    $('#activityExecutor').val('已选择'+ activityExecutorNum +'人');
    $('#theVip').val('已选择'+ theVipNum +'人');
    $('#describeActivities').val(describeActivities);
    $('#setTime').val(setTime);
    $('#setPeople').val(setPeople);
    $('#changeTime').val(changeTime);
    $('#changePeople').val(changePeople);
}
//通知执行人
function notice(){
    window.location.href="activity_noyifyTheExecutor.html";    ///未获得具体地址
}
//关闭 == 返回哪里？
function closePage(){
    alert('关了这页去哪？！');
}
//页面加载
window.onload = function(){
    pageVal();
}