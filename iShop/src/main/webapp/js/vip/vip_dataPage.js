/**
 * Created by Administrator on 2016/12/13.
 */
var oc = new ObjectControl();
//��ֵ����
$('#toTopUp').click(function(){
    $('#topUp').css('display','block')
});
// �ر�
$('#screen_close_shop').click(function () {
    $('#topUp').css('display','none');
});
//���ݱ��
var mydate = new Date();
var str = "" + mydate.getFullYear() + "";
str += (mydate.getMonth()+1) + "";
str += mydate.getDate() + "";
str += mydate.getHours() + "";
str += mydate.getMinutes() + "";
str += mydate.getSeconds() + "";
$('#topUpNum').val(str);
//��������
var mydate = new Date();
var str = "" + mydate.getFullYear() + "-";
str += (mydate.getMonth()+1) + "-";
str += mydate.getDate() + " ";
str += mydate.getHours() + ":";
str += mydate.getMinutes() + ":";
str += mydate.getSeconds() + "";
$('#chooseDate').val(str);
console.log(str)
var chooseDate = {
    elem: '#chooseDate',
    format: 'YYYY-MM-DD hh:mm:ss',
    istime: true,
    min: laydate.now(),
    max: '2099-06-16 23:59:59',
    istoday: false,
    choose: function (datas) {
        start.max = datas; //������ѡ�ú����ÿ�ʼ�յ��������
    }
};
laydate(chooseDate);
//��ֵ����
$('#execution_input').click(function () {
    $('#execution').toggle();
})
//����ѡ��
$("#execution li").click(function () {
    var val = $(this).html();
    $(this).addClass("liactive").siblings("li").removeClass("liactive");
    $("#execution_input").val(val);
    $('#execution').css('display','none');
});
//��ֵ���
function topUpShop(){
    var param={};
    param["pageSize"]='20'
    param["pageNumber"]='1';
    param["searchValue"]='';
    param["corp_code"]=sessionStorage.getItem("corp_code");
    oc.postRequire("post","/shop/selectByAreaCode","",param,function(data){
        var msg = JSON.parse(data.message);
        var list = JSON.parse(msg.list);
        var listList = list.list;
        topUpShopShow(listList);
        $('#topUpShopSelcet li').each(function () {
            var val = $(this).text();
            if(val == ''){
                $(this).remove();
                console.log('ɾ��');
            }
        });
    })
}
function topUpShopShow(listList){
    for(i=0;i<listList.length;i++){
        var tempHTML = '<li id=${id} name=${name} onclick="topUpShopSelcetClick(this)">${msg}<li>';
        var html = '';
        var store_code = listList[i].store_code;
        var store_name = listList[i].store_name;
        var nowHTML1 = tempHTML;
        nowHTML1 = nowHTML1.replace("${id}", store_code);
        nowHTML1 = nowHTML1.replace("${msg}", store_name);
        nowHTML1 = nowHTML1.replace("${name}", i);
        html += nowHTML1;
        console.log(html);
        $("#topUpShopSelcet").append(html);
    }
}
$('#topUpShop').click(function(){
    $('#topUpShopSelcet').toggle();
});
//����ѡ��
function topUpShopSelcetClick(dom){
    var val =$(dom).text();
    $(dom).addClass("liactive").siblings("li").removeClass("liactive");
    $("#topUpShop").val(val);
    $('#topUpShopSelcet').css('display','none');
}
//������
function topUpPeople(){
    var param={};
    param["store_code"]=sessionStorage.getItem("store_id");
    param["corp_code"]=sessionStorage.getItem("corp_code");
    oc.postRequire("post","/shop/staff","",param,function(data){
        var msg = JSON.parse(data.message);
        topUpPeopleShow(msg);
        $('#topUpPeopleSelect li').each(function () {
            var val = $(this).text();
            if(val == ''){
                $(this).remove();
                console.log('ɾ��');
            }
        });
    })

}
function topUpPeopleShow(msg){
    for(i=0;i<msg.length;i++){
        var tempHTML = '<li id="${id}"onclick="topUpPeopleClick(this)">${msg}<li>';
        var html = '';
        var user_name = msg[i].user_name;
        var user_id = msg[i].user_id;
        var nowHTML1 = tempHTML;
        nowHTML1 = nowHTML1.replace("${id}", user_id);
        nowHTML1 = nowHTML1.replace("${msg}", user_name);
        html += nowHTML1;
        $("#topUpPeopleSelect").append(html);
    }
}
$('#topUpPeople').click(function(){
    $('#topUpPeopleSelect').toggle();
});

//����ѡ��
function topUpPeopleClick(dom){
    var val =$(dom).text();
    $(dom).addClass("liactive").siblings("li").removeClass("liactive");
    $("#topUpPeople").val(val);
    $('#topUpPeopleSelect').css('display','none');
}
//vip���š�vip����
function topUpPerson(){
    var vipName  = $('#vip_name').text();
    var vipCard  = $('#vip_card_no').text();
    $('#topUpVipName').val(vipName);
    $('#topUpCard').val(vipCard);
}
//�����ۺϽ�ʵ������ֵ�ۿ�
$('#topUpMoneyReality').focus(function () {
    setInterval(function () {
        var topUpMoneyReality = $('#topUpMoneyReality').val().trim();
        var topUpMoney = $('#topUpMoney').val().trim();
        if(topUpMoneyReality ==''){
            $('#topUpMoneyReality').parent().find('.hint').css('display','block');
        }else{
            $('#topUpMoneyReality').parent().find('.hint').css('display','none');
        }
        if(topUpMoney ==''){
            $('#topUpMoney').parent().find('.hint').css('display','block');
        }else{
            $('#topUpMoney').parent().find('.hint').css('display','none');
        }
        if(topUpMoneyReality!=''&&topUpMoney!='' ){
            $('#topUpMoneyReality').parent().find('.hint').css('display','none');
            $('#topUpMoney').parent().find('.hint').css('display','none');
            topUpMoneyDiscount = topUpMoneyReality/topUpMoney;
            $('#topUpMoneyDiscount').val(topUpMoneyDiscount);
        }
    },1000);
});

//����
$('#toSave').click(function(){
    var topUpNum = $('#topUpNum').val();
    var topData = $('#chooseDate').val();
    var topType = $('#execution_input').val();
    var topUpShop = $('#topUpShop').val();
    var topUpPeople = $('#topUpPeople').val();
    var topUpCard = $('#topUpCard').val();
    var topUpVipName = $('#topUpVipName').val();
    var topUpMoney = $('#topUpMoney').val();
    var topUpMoneyReality = $('#topUpMoneyReality').val();
    var topUpMoneyDiscount = $('#topUpMoneyDiscount').val();
    var topUpNote = $('#topUpNote').val();
});
//ȡ��
$('#toFalse').click(function(){
    $('#topUp').css('display','none');
});

//�ƶ�����
var mouseX, mouseY;
var objX, objY;
var isDowm = false;  //�Ƿ������
function mouseDown(obj, e) {
    obj.style.cursor = "move";
    objX = obj.style.left;
    objY = obj.style.top;
    mouseX = e.clientX;
    mouseY = e.clientY;
    isDowm = true;
}
function mouseMove(e) {
    var div = document.getElementById("topUp");
    var x = e.clientX;
    var y = e.clientY;
    if (isDowm) {
        div.style.left = parseInt(objX) + parseInt(x) - parseInt(mouseX) + "px";
        div.style.top = parseInt(objY) + parseInt(y) - parseInt(mouseY) + "px";
    }
}
function mouseUp(e) {
    if (isDowm) {
        var x = e.clientX;
        var y = e.clientY;
        var div = document.getElementById("topUp");
        div.style.left = (parseInt(x) - parseInt(mouseX) + parseInt(objX)) + "px";
        div.style.top = (parseInt(y) - parseInt(mouseY) + parseInt(objY)) + "px";
//            mouseX = x;
//            rewmouseY = y;
        div.style.cursor = "default";
        isDowm = false;
    }
}

function stopBubble(e) {
    if (e && e.stopPropagation) {//��IE�����
        e.stopPropagation();
    }
    else {//IE�����
        window.event.cancelBubble = true;
    }
}
window.onload = function(){
    topUpPerson();  //��ֵ������Ա���š�����
    topUpShop();    //��ֵ������ֵ����б�
    topUpPeople();  //��ֵ�����������б�
}