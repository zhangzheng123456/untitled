/**
 * Created by Administrator on 2016/12/13.
 */
var oc = new ObjectControl();
cache={
    user_code:'',
    No:''
}
//充值弹窗
$('#toTopUp').click(function(){
    val = $('#topUp .searchable-select-holder').text();
    if(val==""){topUpShop(sessionStorage.getItem("corp_code"))};//充值店仓列表
    $('#topUp').show();
    $('#refund').hide();
    $('.warp').show();
    $('#execution li').eq(0).click();
    $('#topUpShopSelcet li').eq(0).click();
    $('#topUpPeopleSelect li').eq(0).click();
    $('#topUpMoneyReality').parent().find('.hint').css('display','none');
    $('#topUpMoney').parent().find('.hint').css('display','none');
    $("body").css({overflow:"hidden"});
    //充值操作人员
    if($('#topUpPeople').val()==''){
        var store_code = $('.searchable-select-item.selected').attr('data-value');
        topUpPeople(store_code);  //充值弹窗经办人列表
    }
});
//退款弹窗
$('#toRefund').unbind('click').bind('click',(function(){
    val = $('#refund .searchable-select-holder').text();
    if(val==''){topUpShop(sessionStorage.getItem("corp_code"))}; //充值店仓列表
    $('#refund').show();
    $('#topUp').hide();
    $('.warp').show();
    $('#refunType li').eq(1).click();
    $('#refunShopSelcet li').eq(0).click();
    $("body").css({overflow:"hidden"});
}));
//充值记录弹窗
$('#toRecord').click(function(){
    getPayRecord();
    $('#topUp').hide();
    $('#record').show();
    $('.warp').show();
    $("body").css({overflow:"hidden"});
});
// 关闭
$('#screen_close_shop').click(function () {
    $('#topUp').hide();
    $('.warp').hide();
    $("body").css({overflow:"auto"});
});
$('#refundClose').click(function () {
    $('#refund').hide();
    $('.warp').hide();
    $("body").css({overflow:"auto"});
});
$('#record .screen_close').click(function () {
    $('#record').hide();
    $('.warp').hide();
    $("body").css({overflow:"auto"});
});
//单据日期
var mydate = new Date();
var month = mydate.getMonth()+1;
var date = mydate.getDate();
//var hours = mydate.getHours();
//var minutes = mydate.getMinutes();
//var seconds = mydate.getSeconds();
var str = "" + mydate.getFullYear() + "-";
str +=  (month>9?month:'0'+month )+ "-";
str +=  (date>9?date:'0'+date )+ " ";
//str +=  (hours>9?hours:'0'+hours )+ ":";
//str +=  (minutes>9?minutes:'0'+minutes )+ ":";
//str +=  (seconds>9?seconds:'0'+seconds )+ "";
$('#chooseDate').val(str);
$('#refundDate').val(str);
var chooseDate = {
    elem: '#chooseDate',
    format: 'YYYY-MM-DD',
    //format: 'YYYY-MM-DD hh:mm:ss',
    istime: true,
    min: laydate.now(),
    max: '2099-06-16',
    //max: '2099-06-16 23:59:59',
    istoday: false,
    choose: function (datas) {
        start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
var refundDate = {
    elem: '#refundDate',
    format: 'YYYY-MM-DD',
    //format: 'YYYY-MM-DD hh:mm:ss',
    istime: true,
    min: laydate.now(),
    max: '2099-06-16',
    //max: '2099-06-16 23:59:59',
    istoday: false,
    choose: function (datas) {
        start.max = datas; //结束日选好后，重置开始日的最大日期
    }
};
laydate(chooseDate);
laydate(refundDate);
//阻止冒泡
//得到事件
function getEvent(){
    if(window.event){return window.event;}
    func=getEvent.caller;
    while(func!=null){
        var arg0=func.arguments[0];
        if(arg0){
            if((arg0.constructor==Event || arg0.constructor ==MouseEvent
                || arg0.constructor==KeyboardEvent)
                ||(typeof(arg0)=="object" && arg0.preventDefault
                && arg0.stopPropagation)){
                return arg0;
            }
        }
        func=func.caller;
    }
    return null;
}
function cancelBubble()
{
    var e=getEvent();
    if(window.event){
        //e.returnValue=false;//阻止自身行为
        e.cancelBubble=true;//阻止冒泡
    }else if(e.preventDefault){
        //e.preventDefault();//阻止自身行为
        e.stopPropagation();//阻止冒泡
    }
}
//充值类型
$('#execution_input').click(function () {
    cancelBubble();
    $('body').click();
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
    cancelBubble();
    $('body').click();
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
function topUpShop(a) {
    whir.loading.add("",0.5);
    oc.postRequire("get", "/shop/findStore", "","", function (data) {
        if (data.code == "0") {
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            var corp_html='';
            for(var i=0;i<list.length;i++){
                corp_html+='<option value="'+list[i].store_code +'">'+list[i].store_name+'</option>';
            }
            $("#OWN_CORP").append(corp_html);
            $("#OWN_CORP_refund").append(corp_html);
            $('#OWN_CORP').searchableSelect();
            $("#OWN_CORP_refund").searchableSelect();
            var storeCodetopUp=$("#OWN_CORP").val();
            var storeCoderefund=$("#OWN_CORP_refund").val();
            topUpPeople(storeCodetopUp,"all");
            $('#corp_parent .searchable-select-input').keydown(function(event) {
                var event = window.event || arguments[0];
                if (event.keyCode == 13) {
                    var storeCodetopUp1 = $("#OWN_CORP").val();
                    if (storeCodetopUp !== storeCodetopUp1) {
                        storeCodetopUp=storeCodetopUp1;
                        topUpPeople(storeCodetopUp,"topUp");
                    }
                }
            });
            $('#corp_parent .searchable-select-item').click(function() {
                var storeCodetopUp1 = $("#OWN_CORP").val();
                if (storeCodetopUp !== storeCodetopUp1) {
                    storeCodetopUp=storeCodetopUp1;
                    topUpPeople(storeCodetopUp,"topUp");
                }
            })
            $('#refund_parent .searchable-select-input').keydown(function(event) {
                var event = window.event || arguments[0];
                if (event.keyCode == 13) {
                    var storeCoderefund1 = $("#OWN_CORP_refund").val();
                    if (storeCoderefund !== storeCoderefund1) {
                        storeCoderefund=storeCoderefund1;
                        topUpPeople(storeCoderefund,"refund");
                    }
                }
            });
            $('#refund_parent .searchable-select-item').click(function() {
                var storeCoderefund1 = $("#OWN_CORP_refund").val();
                if (storeCoderefund !== storeCoderefund1) {
                    storeCoderefund=storeCoderefund1;
                    topUpPeople(storeCoderefund,"refund");
                }
            })
        } else if (data.code == "-1") {
            //art.dialog({
            //    time: 1,
            //    lock: true,
            //    cancel: false,
            //    content: data.message
            //});
            $("#editTk").show();
            $("#tkWarp").show();
            $("#msg").html(data.message);
        }
        whir.loading.remove();//移除加载框
    })
}
//经办人
function topUpPeople(store_code,type){
    var param={};
    param["store_code"]=store_code;
    param["corp_code"]=sessionStorage.getItem("corp_code");
    oc.postRequire("post","/shop/staff","",param,function(data){
        if(data.code == '0'){
            var msg = JSON.parse(data.message);
            var html="";
            if(type=="all"){
                $("#topUpPeople_parent .searchable-select").remove();
                $("#refundPeople_parent .searchable-select").remove();
            };
            if(type=="topUp"){
               $("#topUpPeople_parent .searchable-select").remove();
            };
            if(type=="refund"){
                $("#refundPeople_parent .searchable-select").remove();
            };
            for(var i=0;i<msg.length;i++){
                html+="<option value='"+msg[i].user_code+"'>"+msg[i].user_name+"</option>"
            }
            if(type=="all"){
                $("#topUpPeople").html(html);
                $("#topUpPeople_parent select").searchableSelect();
                $("#refundPeople").html(html);
                $("#refundPeople").searchableSelect();
            };
            if(type=="topUp"){
                $("#topUpPeople").html(html);
                $("#topUpPeople_parent select").searchableSelect();
            };
            if(type=="refund"){
                $("#refundPeople").html(html);
                $("#refundPeople").searchableSelect();
            };
        }else if(date.code=="-1"){
            $("#editTk").show();
            $("#tkWarp").show();
            $("#msg").html(data.message);
        }
    })
}
$('#topUpPeople').click(function(){
    event.stopPropagation();
    $('#execution').css('display','none');
    $('#refunShopSelcet').css('display','none');
    $('#topUpPeopleSelect').toggle();
});
//下拉选择
function topUpPeopleClick(dom){
    var val =$(dom).text();
    $(dom).addClass("liactive").siblings("li").removeClass("liactive");
    $("#topUpPeople").val(val);
    cache.user_code = $(dom).attr('id');
    console.log(cache.user_code);
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
            $('#topUpMoneyDiscount').val('');
            $('#topUpMoneyDiscount').attr('placeholder','由系统自动生成');
        }else{
            $('#topUpMoneyReality').parent().find('.hint').css('display','none');
        }
        if(topUpMoney ==''){
            $('#topUpMoney').parent().find('.hint').css('display','block');
            $('#topUpMoneyDiscount').val('');
            $('#topUpMoneyDiscount').attr('placeholder','由系统自动生成');
        }else{
            $('#topUpMoney').parent().find('.hint').css('display','none');
        }
        if(topUpMoneyReality!=''&&topUpMoney!='' ){
            $('#topUpMoneyReality').parent().find('.hint').css('display','none');
            $('#topUpMoney').parent().find('.hint').css('display','none');
            topUpMoneyDiscount = (topUpMoneyReality/topUpMoney).toFixed(2);
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
}
$("#activityContent").blur(function(){
    var val=$(this).val().trim();
    if(val!=""){
        $('#activityContent').parent().find('.hint').css('display','none');
    }
})
$('#refunTopUpFrom').blur(function () {
    var type = $('#refunTypeInput').val();
    var val = $('#refunTopUpFrom').val();
    //判断输入的单据号是否与上次相同
    if(cache.No ==''||cache.No!=val){
        cache.No = val;
        if(val != ''&& type =='按充值单退款'){
            console.log('调用接口' + type);
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
                    //art.dialog({
                    //    time: 1,
                    //    lock: true,
                    //    cancel: false,
                    //    content: data.message
                    //});
                    $("#editTk").show();
                    $("#tkWarp").show();
                    $("#msg").html(data.message);
                }
            });
        }else{
            console.log('单号不能为空！');
        }
    }
});
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
            if(msg!=''){
                var balance = msg.balance;
                $('#refundBalance').val(balance);
            }else{
                $('#refundBalance').val('0');
            }
        } else if (data.code == "-1") {
            //art.dialog({
            //    time: 1,
            //    lock: true,
            //    cancel: false,
            //    content: data.message
            //});
            $("#editTk").show();
            $("#tkWarp").show();
            $("#msg").html(data.message);
        }
    });
}
//保存
$('#toSave').click(function(){
    //var topUpNum = $('#topUpNum').val();//单据编号
    var topData = $('#chooseDate').val();//单据日期
    var topType = $('#execution_input').val(); //充值类型
    var topUpShop = $("#OWN_CORP").find("option:selected").text();//充值店铺名称
    var store_code=$("#OWN_CORP").val();//充值店铺编号
    var topUpPeople = $('#topUpPeople').find("option:selected").text();//经办人
    var user_code=$('#topUpPeople').val();//人的code
    var topUpCard = $('#vip_card_no').text();//会员卡号
    var topUpVipName = $('#topUpVipName').val();
    var topUpMoney = $('#topUpMoney').val(); //吊牌金额
    var topUpMoneyReality = $('#topUpMoneyReality').val(); //实付金额
    var topUpMoneyDiscount = $('#topUpMoneyDiscount').val();//折扣
    var topUpNote = $('#topUpNote').val().trim();//备注
    var activityContent = $('#activityContent').val().trim();//关联活动
    if(topUpMoney == '' || topUpMoneyReality=='' ) {
        $(".topUp_main").scrollTop($(".topUp_main")[0].scrollHeight);
         $("#editTk").show();
         $("#tkWarp").show();
         $("#msg").html('折合吊牌金额、实付金额不能为空');
        $('#topUpMoneyReality').parent().find('.hint').css('display','block');
        $('#topUpMoney').parent().find('.hint').css('display','block');

    }else if(activityContent==""){
        $(".topUp_main").scrollTop($(".topUp_main")[0].scrollHeight);
        $("#editTk").show();
        $("#tkWarp").show();
        $("#msg").html('关联活动不能为空');
        $('#activityContent').parent().find('.hint').css('display','block');
    }else{
         if(topType == '直接充值'){
             pay_type =1;
         }else if(topType == '退换转充值'){
             pay_type =2;
         }
         var param = {};
         param["corp_code"] = sessionStorage.getItem("corp_code");//企业编号
         param["vip_id"] = sessionStorage.getItem("id");//会员编号
         param["vip_name"] = $('#vip_name').text();//会员名称
         param["card_no"] = topUpCard;//会员卡号
         param["remark"] = topUpNote;//备注
         param["activity_content"] = activityContent;//关联活动
         param["store_name"] = topUpShop;//充值店铺
         param["store_code"] = store_code;//充值编号
         param["date"] = topData;//单据日期
         param["pay_type"] = pay_type;//充值类型
         param["user_code"] = user_code//经办人编号
         param["user_name"] = topUpPeople;//经办人
         param["price"] = topUpMoney;//吊牌金额
         param["pay_price"] = topUpMoneyReality;//实付金额
         param["discount"] = topUpMoneyDiscount;//折扣
         param["type"] = 'pay';
        //param["billNo"] = topUpNum;//单据编号
        // param["pay_price"] = topUpMoneyReality;//实付金额
        oc.postRequire("post", " /vip/recharge", "", param, function (data) {
            if (data.code == "0") {
                $('#topUp').css('display','none');
                $('.warp').css('display','none');
                $("body").css({overflow:"auto"});
                $("#editTk").show();
                $("#tkWarp").show();
                $("#msg").html('保存成功');
                return ;
            } else if (data.code == "-1") {
                $("#editTk").show();
                $("#tkWarp").show();
                $("#msg").html(data.message);
            }
        });
    }
})
//退款保存
function toSave(){
    var refunTypeInput = $('#refunTypeInput').val();
    var param = {};
    param["store_name"] = $('#OWN_CORP_refund').find("option:selected").text();//充值店铺名称
    param["store_code"] = $('#OWN_CORP_refund').val();//充值店铺编号
    param["user_code"] = $('#refundPeople').val();//经办人code
    param["user_name"] = $('#refundPeople').find("option:selected").text();//经办人
    param["corp_code"] = sessionStorage.getItem("corp_code");//企业编号
    param["vip_id"] = sessionStorage.getItem("id");//会员编号
    param["vip_name"] = $('#vip_name').text();//会员名称
    param["card_no"] = $('#vip_card_no').text();//会员卡号
    param["remark"] = $('#refundNote').val();//备注
    param["date"] = $('#refundDate').val();//单据日期
    //param["price"] = topUpMoney;//吊牌金额
    param["pay_price"] = $('#refundReality').val();//实付金额
    param["discount"] = $('#refundMoneyDiscount').val();//折扣
    param["type"] = 'refund';
    if(refunTypeInput == '按充值单退款'){
        param["sourceNo"] = $('#refunTopUpFrom').val();//来源单号
        param["pay_type"] ='1'; //充值类型
        if(refunTypeInput!='' && $('#refunTopUpFrom').val() != ''&& $('#refunShop').val() != ''){
            oc.postRequire("post", " /vip/recharge", "", param, function (data) {
                if (data.code == "0") {
                    $('#refund').css('display','none');
                    $('.warp').css('display','none');
                    $("body").css({overflow:"auto"});
                    $("#editTk").show();
                    $("#tkWarp").show();
                    $("#msg").html('保存成功');
                    return ;
                } else if (data.code == "-1") {
                    $("#editTk").show();
                    $("#tkWarp").show();
                    $("#msg").html(data.message);
                }
            });
        }else{
            $("#editTk").show();
            $("#tkWarp").show();
            $("#msg").html('请输入来源单号');
            return ;
        }
    }
    if(refunTypeInput == '余额退款'){
        param["sourceNo"] = $('#refunBalanceFrom').val();//来源单号
        param["pay_type"] ='2'; //充值类型
        oc.postRequire("post", " /vip/recharge", "", param, function (data) {
            if (data.code == "0") {
                $('#refund').css('display','none');
                $('.warp').css('display','none');
                $("body").css({overflow:"auto"});
                $("#editTk").show();
                $("#tkWarp").show();
                $("#msg").html('保存成功');
                return ;
            } else if (data.code == "-1") {
                //alert(data.message);
                $("#editTk").show();
                $("#tkWarp").show();
                $("#msg").html(data.message);
            }
        });
    }
}
//取消
$('#toFalse').click(function(){
    $('#topUp').hide();
    $('.warp').hide();
    $("body").css({overflow:"auto"});
});
function toFalse(){
    $('#refund').hide();
    $('.warp').hide();
    $("body").css({overflow:"auto"});
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
function recordVal(record_num,record_type,record_money,record_balance){
    var tempHTML = '<li onclick="showDetail()"><span class="record_num">${record_num}</span><span class="record_type">${record_type}</span><span class="record_money">${record_money}</span><span class="record_balance">${record_balance}</span></li>';
    var html = '';
    var nowHTML1 = tempHTML;
    nowHTML1 = nowHTML1.replace("${record_num}", record_num);
    nowHTML1 = nowHTML1.replace("${record_type}", record_type);
    nowHTML1 = nowHTML1.replace("${record_money}", record_money);
    nowHTML1 = nowHTML1.replace("${record_balance}", record_balance);
    html += nowHTML1;
    $("#record_body").append(html);
}
//点击查看详细
function showDetail(){
    $("#editTk").show();
    $("#tkWarp").show();
    $("#msg").html('暂无详细');
}
//加载更多
$('#toAddMore').click(function () {
    $("#editTk").show();
    $("#tkWarp").show();
    $("#msg").html('功能暂未开放');
})
//选择日期点击，影藏其他下拉框
$('#chooseDate').click(function () {
    $('#execution').hide();
    $('#topUpShopSelcet').hide();
    $('#topUpShopSelcet_ul').hide();
    $('#topUpPeopleSelect').hide();
});
function getPayRecord(){
    var param={};
    var corp_code=sessionStorage.getItem("corp_code");//企业编号
    var vip_id=sessionStorage.getItem("id");//会员编号
    param["corp_code"]=corp_code;//"C10016";
    param["vip_id"]=vip_id//"36637";
    oc.postRequire("post", "/vipCheck/getPayRecord","", param, function (data) {
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=message.list;
            var html="";
            if(list.length>0){
                for(var i=0;i<list.length;i++){
                    html+="<li><span class='record_num'title='"+list[i].bill_no+"'>"+list[i].bill_no
                        +"</span><span class='record_type'title='"+list[i].recharge_type+"'>"+list[i].recharge_type+
                        "</span><span class='record_money'title='"+list[i].tag_price+"'>"+list[i].tag_price
                        +"</span><span class='record_money'title='"+list[i].pay_price+"'>"+list[i].pay_price
                        +"</span><span class='record_money' title='"+list[i].modified_date+"'>"+list[i].modified_date+"</span></li>"
                }
                $("#record_body").html(html);
            }else if(list.length==0){
                $("#record_body").html("<li style='line-height: 300px;text-align: center; border-bottom:0px;'>暂无充值记录</li>");
            }
        }else if(data.code=="-1"){
            $("#editTk").show();
            $("#tkWarp").show();
            $("#msg").html(data.message);
        }
        
    })
}
//点击清除提示弹窗
function clearOut(){
    $('.aui_outer').parent().click(function () {
        $('.aui_outer').parent().css('display','none');
        $('.ascrail2006-hr').next().remove();
    });
}
$("#editEnter,#editX").click(function(){
    $("#editTk").hide();
    $("#tkWarp").hide();
    $("#msg").html("");
    whir.loading.remove("mask")
});
//优惠券
$('.coupon_nav li').click(function () {
    $('.coupon_nav li').css('background-color','white');
    $(this).css('background-color','#e4e4ea');
    var val = $(this).text();
    var type = '';
    whir.loading.add("",0.5);
    $(".coupon_main").empty();
    if(val == '已使用'){
        type='IS_VERIFYED';
        getCoupon(type)
    }else if(val == '未使用'){
        type='CAN_USE';
        getCoupon(type)
    }else if(val == '已过期'){
        type='IS_EXPIRED';
        getCoupon(type)
    }
});
//优惠券-调用接口
function getCoupon(type){
    var param={};
    param["corp_code"]=sessionStorage.getItem("corp_code");
    param["app_id"]= sessionStorage.getItem("app_id");
    param["open_id"]=sessionStorage.getItem("open_id");
    param["vip_id"]=sessionStorage.getItem("id");
    param["phone"]=$('.all_list #vip_phone_edit').val();
    param["type"]=type;
    oc.postRequire("post","/vip/coupon","",param,function(data){
        if(data.code == '0'){
            var message = JSON.parse(data.message);
            couponVal(message);
        }else if(date.code=="-1"){
            //alert(date.message);
            $("#editTk").show();
            $("#tkWarp").show();
            $("#msg").html(data.message);
        }
    })
}
function formatCouponDate (date){
    var y=date.substring(0,4);
    var m=date.substring(4,6);
    var d=date.substring(6,8);
    return y+"-"+m+"-"+d
}
//优惠券-页面展现
function couponVal(message){
    $("#couponList").html("共0张");
    if(message == ''){
        var html = '<p class="coupon_main_temp">暂无优惠券</p>';
        $('.coupon_nav').css('height','40');
        //$('.coupon_nav').css('border-bottom','1px dashed #d4d8e1');
        $(".coupon_main").html(html);
        whir.loading.remove();//移除加载框
        return;
    }
    var couponList = message.couponList;
    $("#coupon_num").html("共"+couponList.length+"张");
    if(couponList.length != 0){
        var tempHTML = '<li id="${no}"><div class="coupon_main_l"> <span>${num}</span>${type} <p>${couponName}</p><p>使用期限 ${startTime} 至 ${endTime}</p> </div> <div class="coupon_main_r" style="width: 770px"> <p>${textTitle}</p></div> </li>';
        var html = '';
        for(i=0;i<couponList.length;i++){
            var nowHTML = tempHTML;
            if(couponList[i].quan_type==1){
                nowHTML = nowHTML.replace("${type}","元");
                nowHTML = nowHTML.replace("${num}",couponList[i].price);
            }else if(couponList[i].quan_type==2){
                nowHTML = nowHTML.replace("${type}","折");
                nowHTML = nowHTML.replace("${num}",couponList[i].discount);
            }
            nowHTML = nowHTML.replace("${no}",  couponList[i].no);
            nowHTML = nowHTML.replace("${couponName}",  couponList[i].name);
            nowHTML = nowHTML.replace("${startTime}", formatCouponDate(couponList[i].start_time));
            nowHTML = nowHTML.replace("${endTime}",  formatCouponDate(couponList[i].end_time));
            nowHTML = nowHTML.replace("${textTitle}",  couponList[i].description);
            //nowHTML = nowHTML.replace("${textText}",  couponList[i].tips);
            html += nowHTML;
        }
        //$('.coupon_nav').css('height','0');
        //$('.coupon_nav').css('border-bottom','none');
    }else{
        var html = '<p class="coupon_main_temp">暂无优惠券</p>';
        $('.coupon_nav').css('height','40');
        $('.coupon_nav').css('border-bottom','1px dashed #d4d8e1');
    }
    $(".coupon_main").html(html);
    whir.loading.remove();//移除加载框
}
$(window).ready(function(){
    topUpPerson();  //充值弹窗会员卡号、姓名
    setInterval(function () {
        $('#topUp .laydate_box').css('position','fixed');
        $('#refund .laydate_box').css('position','fixed');
        var val = $('.laydate_box').css('display');
        if(val == 'block'){
            $('.topUp_main').scroll(function () {
                $('.laydate_box').hide();
            })
        }else if(val =='none'){
            $('#chooseDate').click(function () {
                $('.laydate_box').toggle();
            })
        }
    },500);
    $('body').click(function () {
        $('#execution').hide();
        $('#topUpShopSelcet').hide();
        $('#topUpShopSelcet_ul').hide();
        $('#topUpPeopleSelect').hide();
        $('#refunType').hide();
        $('#refunShopSelcet').hide();
    });

});