/**
 * Created by Administrator on 2016/12/13.
 */
var oc = new ObjectControl();
//充值弹窗
$('#toTopUp').click(function(){
    $('#topUp').css('display','block');
    $('#refund').css('display','none');
    $('#execution li').eq(0).click();
    $('#topUpShopSelcet li').eq(0).click();
    $('#topUpPeopleSelect li').eq(0).click();
    $('#topUpMoneyReality').parent().find('.hint').css('display','none');
    $('#topUpMoney').parent().find('.hint').css('display','none');
});
$('#toRefund').click(function(){
    $('#refund').css('display','block');
    $('#topUp').css('display','none');
    refunBalanceShow();//默认余额退款
    $('#refunType li').eq(1).click();
    $('#refunShopSelcet li').eq(0).click();
});

// 关闭
$('#screen_close_shop').click(function () {
    $('#topUp').css('display','none');
});
$('#refundClose').click(function () {
    $('#refund').css('display','none');
});

//单据编号
var mydate = new Date();
var str = "" + mydate.getFullYear() + "";
str += (mydate.getMonth()+1) + "";
str += mydate.getDate() + "";
str += mydate.getHours() + "";
str += mydate.getMinutes() + "";
str += mydate.getSeconds() + "";
$('#topUpNum').val(str);
$('#refundNum').val(str);
//单据日期
var mydate = new Date();
var str = "" + mydate.getFullYear() + "-";
str += (mydate.getMonth()+1) + "-";
str += mydate.getDate() + " ";
str += mydate.getHours() + ":";
str += mydate.getMinutes() + ":";
str += mydate.getSeconds() + "";
$('#chooseDate').val(str);
$('#refundDate').val(str);
console.log(str)
var chooseDate = {
    elem: '#chooseDate',
    format: 'YYYY-MM-DD hh:mm:ss',
    istime: true,
    min: laydate.now(),
    max: '2099-06-16 23:59:59',
    istoday: false,
    choose: function (datas) {
        start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
var refundDate = {
    elem: '#refundDate',
    format: 'YYYY-MM-DD hh:mm:ss',
    istime: true,
    min: laydate.now(),
    max: '2099-06-16 23:59:59',
    istoday: false,
    choose: function (datas) {
        start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
laydate(chooseDate);
laydate(refundDate);
//充值类型
$('#execution_input').click(function () {
    $('#execution').toggle();
});

//下拉选择
$("#execution li").click(function () {
    var val = $(this).html();
    $(this).addClass("liactive").siblings("li").removeClass("liactive");
    $("#execution_input").val(val);
    $('#execution').css('display','none');
});
//退款类型
$('#refunTypeInput').click(function () {
    $('#refunType').toggle();
})
//下拉选择
$("#refunType li").click(function () {
    var val = $(this).html();
    $(this).addClass("liactive").siblings("li").removeClass("liactive");
    $("#refunTypeInput").val(val);
    $('#refunType').css('display','none');
});
//充值店仓
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
                console.log('删除');
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
//下拉选择
function topUpShopSelcetClick(dom){
    var val =$(dom).text();
    $(dom).addClass("liactive").siblings("li").removeClass("liactive");
    $("#topUpShop").val(val);
    $('#topUpShopSelcet').css('display','none');
}
//退款店仓
$('#refunShop').click(function () {
    $('#refunShopSelcet').toggle();
})
//下拉选择
$("#refunShopSelcet li").click(function () {
    var val = $(this).html();
    $(this).addClass("liactive").siblings("li").removeClass("liactive");
    $("#refunShop").val(val);
    $('#refunShopSelcet').css('display','none');
});
//经办人
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
                console.log('删除');
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

//下拉选择
function topUpPeopleClick(dom){
    var val =$(dom).text();
    $(dom).addClass("liactive").siblings("li").removeClass("liactive");
    $("#topUpPeople").val(val);
    $('#topUpPeopleSelect').css('display','none');
}
//vip卡号、vip姓名
function topUpPerson(){
    var vipName  = $('#vip_name').text();
    var vipCard  = $('#vip_card_no').text();
    $('#topUpVipName').val(vipName);
    $('#topUpCard').val(vipCard);
}
//吊牌折合金额、实付金额、充值折扣
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
// <!--若选择按充值单退款：-->（调接口/vip/checkBillNo 传单号，和退款类型，vip_id,corp_code）
function refunTopUpShow(){
    $('#refunTopUp').css('display','block');
    $('#refunBalance').css('display','none');
    $('#refundReality').parent('span').parent('div').css('display','block');
    $('#refundMoneyDiscount').parent('span').parent('div').css('display','block');
    $('#refundBalance').parent('span').parent('div').css('display','none');
    $('#refunTopUpFrom').blur(function () {
        var val = $('#refunTopUpFrom').val();
        if(val != ''){
            var param = {};
            param["billNo"] = $('#refunTopUpFrom').val();//单据编号
            param["type"] = 'billNo';//退款类型
            param["corp_code"] = sessionStorage.getItem("corp_code");//企业编号
            param["vip_id"] = sessionStorage.getItem("id");//会员编号
            oc.postRequire("post", " /vip/checkBillNo", "", param, function (data) {
                if (data.code == "0") {
                    var msg = JSON.parse(data.message);
                    var pay_price = msg.pay_price; //实付金额
                    var can_pass = msg.can_pass;
                    var price = msg.price; //吊牌金额
                    var refundMoneyDiscount =pay_price/price;
                    $('#refundReality').val(pay_price);
                    $('#refundMoneyDiscount').val(refundMoneyDiscount);
                } else if (data.code == "-1") {
                    alert(data.message);
                }
            });
        }else{
            console.log('单号不能为空！');
        }
    });

}
// <!--若选择余额退款：-->（调接口/vip/checkBillNo 传退款类型，vip_id,corp_code）
function refunBalanceShow(){
    $('#refunTopUp').css('display','none');
    $('#refunBalance').css('display','block');
    $('#refundReality').parent('span').parent('div').css('display','none');
    $('#refundMoneyDiscount').parent('span').parent('div').css('display','none');
    $('#refundBalance').parent('span').parent('div').css('display','block');
    param["type"] = 'balances';//退款类型
    param["corp_code"] = sessionStorage.getItem("corp_code");//企业编号
    param["vip_id"] = sessionStorage.getItem("id");//会员编号
    oc.postRequire("post", " /vip/checkBillNo", "", param, function (data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            var balance = msg.balance;
            $('#refundBalance').val(balance);
        } else if (data.code == "-1") {
            alert(data.message);
        }
    });
}
//保存
$('#toSave').click(function(){
    var topUpNum = $('#topUpNum').val();//单据编号
    var topData = $('#chooseDate').val();//单据日期
    var topType = $('#execution_input').val(); //充值类型
    var topUpShop = $('#topUpShop').val();  //充值店仓
    var topUpPeople = $('#topUpPeople').val();//经办人
    var topUpCard = $('#topUpCard').val();//会员卡号
    var topUpVipName = $('#topUpVipName').val();
    var topUpMoney = $('#topUpMoney').val(); //吊牌金额
    var topUpMoneyReality = $('#topUpMoneyReality').val(); //实付金额
    var topUpMoneyDiscount = $('#topUpMoneyDiscount').val();//折扣
    var topUpNote = $('#topUpNote').val();//备注
    //if(topType == ''|| topUpShop == '' || topUpPeople == '' ) {
    //    art.dialog({
    //        time: 1,
    //        lock: true,
    //        cancel: false,
    //        content: "充值类型、充值店仓、经办人不能为空"
    //    });
    //}else
     if(topUpMoney == '' || topUpMoneyReality=='' ) {
        //art.dialog({
        //    time: 1,
        //    lock: true,
        //    cancel: false,
        //    content: "折合吊牌金额、实付金额不能为空"
        //});
         $('#topUpMoneyReality').parent().find('.hint').css('display','block');
         $('#topUpMoney').parent().find('.hint').css('display','block');
    }else{
        var param = {};
        param["corp_code"] = sessionStorage.getItem("corp_code");//企业编号
        param["vip_id"] = sessionStorage.getItem("id");//会员编号
        param["card_no"] = topUpCard;//会员卡号
        param["type"] = 'pay';
        param["billNo"] = topUpNum;//单据编号
        param["date"] = topData;//单据日期
        param["pay_type"] = topType;//充值类型
        param["store_code"] = topUpShop;//充值店铺
        param["user_code"] = topUpPeople;//经办人
        param["price"] = topUpMoney;//吊牌金额
        param["pay_price"] = topUpMoneyReality;//实付金额
        param["discount"] = topUpMoneyDiscount;//折扣
        param["remark"] = topUpNote;//备注
        oc.postRequire("post", " /vip/recharge", "", param, function (data) {
            if (data.code == "0") {
                $('#topUp').css('display','none');
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "保存成功"
                });
                return ;
            } else if (data.code == "-1") {
                alert(data.message);
            }
        });
    }

})
//退款保存
function toSave(){
    var refunTypeInput = $('#refunTypeInput').val();
    console.log(refunTypeInput);
    var param = {};
    param["corp_code"] = sessionStorage.getItem("corp_code");//企业编号
    param["vip_id"] = sessionStorage.getItem("id");//会员编号
    param["card_no"] = $('#vip_card_no').text();//会员卡号
    param["type"] = 'refund';
    param["billNo"] = $('#refundNum').val();//单据编号
    param["refund_type"] = refunTypeInput;//退款类型
    param["remark"] = $('#refundNote').val();//备注
    param["store_code"] = $('#refunShop').val();//退款店铺
    param["remark"] = $('#refundNote').val();//备注
    if(refunTypeInput == '按充值单退款'){
        param["sourceNo"] = $('#refunTopUpFrom').val();//来源单号
        if(refunTypeInput!='' && $('#refunTopUpFrom').val() != ''&& $('#refunShop').val() != ''){
            oc.postRequire("post", " /vip/recharge", "", param, function (data) {
                if (data.code == "0") {
                    $('#refund').css('display','none');
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "保存成功"
                    });
                    return ;
                } else if (data.code == "-1") {
                    alert(data.message);
                }
            });
        }else{
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "请输入来源单号"
            });
            return ;
        }
    }
    if(refunTypeInput == '余额退款'){
        param["sourceNo"] = $('#refunBalanceFrom').val();//来源单号
        oc.postRequire("post", " /vip/recharge", "", param, function (data) {
            if (data.code == "0") {
                $('#refund').css('display','none');
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "保存成功"
                });
                return ;
            } else if (data.code == "-1") {
                alert(data.message);
            }
        });
    }
}
//取消
$('#toFalse').click(function(){
    $('#topUp').css('display','none');
});
function toFalse(){
    $('#refund').css('display','none');
}
//移动窗体
var mouseX, mouseY;
var objX, objY;
var isDowm = false;  //是否按下鼠标
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
    if (e && e.stopPropagation) {//非IE浏览器
        e.stopPropagation();
    }
    else {//IE浏览器
        window.event.cancelBubble = true;
    }
}
//遮罩层
window.onload = function(){
    topUpPerson();  //充值弹窗会员卡号、姓名
    topUpShop();    //充值弹窗充值店仓列表
    topUpPeople();  //充值弹窗经办人列表

}