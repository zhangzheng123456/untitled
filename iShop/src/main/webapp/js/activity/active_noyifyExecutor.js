/**
 * Created by Bizvane on 2016/11/23.
 */
cache = {
    "selectPeopleNum": '',
};
//添加执行人
$('#add_sendee').click(function(){
    $("#screen_staff").show();
    //写死的数据
    var num = 32;
    cache.selectPeopleNum = num;
    $('#selectPeople').val('已选择'+num+'人');
});
//关闭筛选数据
$('#screen_close_staff').click(function () {
    $("#screen_staff").hide();
});
//发送
function send(){
    var callitile = $('#callitile').val();
    console.log('通知标题:'+callitile);
    var callMain = $('#callMain').val();
    console.log('通知正文:'+callMain);
    if(callitile==''||callMain==''){
        alert('通知标题或者正文不能为空！');
    }else if(callitile.length){}
}
//关闭
$('#send_close').click(function () {
    window.location.href = 'activity_details.html';    ///未获得具体地址
});
//页面加载
window.onload = function(){

}
