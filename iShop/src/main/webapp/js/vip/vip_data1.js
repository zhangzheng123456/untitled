$(function(){
    getVipInfo();
});
var text_first="";
var text_second="";
var text_third="";
var time_start="";
var time_end="";
var inx=1;//默认是第一页
function getVipInfo(){
    var param_info={};
    param_info["vip_id"]=sessionStorage.getItem("id");
    param_info["corp_code"]=sessionStorage.getItem("corp_code");
    oc.postRequire("post","/vip/vipInfo","",param_info,function(data){
        var vipData=JSON.parse(data.message);
        var vipDataList=vipData.list;
        var extend=vipData.extend;
        var extend_info=vipData.extend_info==""?{}:JSON.parse(vipData.extend_info);
        var extendhtml="";
        var conSumData=vipData.list;
        $("#total_amount_Y").html(conSumData.amount_Y);
        $("#consume_times_Y").html(conSumData.times_Y);
        $("#total_amount").html(conSumData.total_amount);
        $("#consume_times").html(conSumData.consume_times);
        $("#dormant_time").html(conSumData.dormant_time);
        $("#last_date").html(conSumData.last_date);
        $("#remark_value").val(vipData.remark);
        function getvalue(){
            var VALUE="";
            for(data in extend_info){
                if(extend[i].key==data){
                   VALUE=extend_info[data]
                 }
            }
            return VALUE;
        }
        for(var i=0;i<extend.length;i++){
                if(extend[i].type=='rule'){
                    extendhtml+="<li class='rule'>"
                        +"</li>"
                }
            if(extend[i].type=="text"){
                var value=getvalue();
                extendhtml+="<li>"
                    +"<b>"+extend[i].name+"</b>"
                    +"<div>"
                    +"<input type='text' value='"+value+"' data-key='"+extend[i].key+"' />"
                    +"</div>"
                    +"</li>"
            }
            if(extend[i].type=="select"){
                var value=getvalue();
                var selectValue=extend[i].values;
                var ul="";
                if(selectValue!=""){
                    selectValue=selectValue.split(",");
                     ul="<ul class='expand_selection'>";
                    for(var b=0;b<selectValue.length;b++){
                        ul+="<li title='"+selectValue[b]+"'>"+selectValue[b]+"</li>";
                    }
                }
                extendhtml+='<li class="drop_down item_1">'
                    +'<b>'+extend[i].name+'</b>'
                    +'<div class="position" >'
                    +'<input class="input_select" readonly value="'+value+'" type="text" data-key="'+extend[i].key+'" />'
                    +ul
                    +'</div>'
                    +'</li>'
            }
            if(extend[i].type=="date"){
                var value=getvalue();
                extendhtml+='<li >'
                +'<b>'+extend[i].name+'</b>'
                +'<div>'
                +'<input type="text" data-key="'+extend[i].key+'" readonly="true" placeholder="请输入日期" class="laydate-icon" value="'+value+'" onclick="laydate({istime: true, format: \'YYYY-MM-DD\'})">'
                +'</div>'
                +'</li>'
            }
            if(extend[i].type=="longtext"){
                var value=getvalue();
                extendhtml+='<li style="width: 100%">'
                +'<b>'+extend[i].name+'</b>'
                +'<div>'
                +'<input type="text"  value="'+value+'" data-key="'+extend[i].key+'"/>'
                +'</div>'
                +'</li>'
            }
        }
        $("#extend ul").html(extendhtml);
        if(extendhtml==''){
            $("#expand_send").hide();
            $("#expand_send").html("暂无数据");
        }
        fuzhi(vipDataList);
        getVipPoints(vipDataList.corp_code);
        showOption();
        getoselectvalue();
    })
}
function getVipPoints(code,type){
    var parmam_jifen={};
    parmam_jifen['time_start']=time_start;
    parmam_jifen['time_end']=time_end;
    parmam_jifen['order_id']=text_first;
    parmam_jifen['goods_name']=text_second;
    parmam_jifen['goods_code']=text_third;
    parmam_jifen['vip_id']=sessionStorage.getItem("id");
    parmam_jifen['store_id']=sessionStorage.getItem("store_id");
    parmam_jifen['corp_code']=sessionStorage.getItem("corp_code");
    if(type!==undefined){
        parmam_jifen['type']=type;
    }

    oc.postRequire("post","/vip/vipPoints","",parmam_jifen,function(data){
        var Data=JSON.parse(data.message);
        if(type=='1'){
            var pointsData=Data.result_points;//积分
            var listData=JSON.parse(pointsData.list);//积分list
            //jifenContent(listData,pointsData);
        }
        if(type=='2'){
            var consumnData=Data.result_consumn;//消费
            var consumnlistData=consumnData.list_wardrobe;//消费list
            xiaofeiContent(consumnData,consumnlistData)
        }
        if(type==undefined){
            var pointsData=Data.result_points;//积分
            var consumnData=Data.result_consumn;//消费
            var consumnlistData=consumnData.list_wardrobe;//消费list
            var listData=pointsData.date_current_score;//积分list
            //jifenContent(listData,pointsData);
            xiaofeiContent(consumnData,consumnlistData);
        }
    });
}
//function jifenContent(listData,pointsData){
//    $("#points_total").html(pointsData.score);
//    $("#vip_card").html(pointsData.vip_info["card_type_id"]);
//    $("#vip_card_num").html(pointsData.vip_info["card_no_vip"]);
//    $("#vip_name1").html(pointsData.vip_info["name_vip"]);
//    var listHtml="";
//    var listHtmlAll="";
//    for(var i=0;i<listData.length;i++){
//        var a=i+1;
//        if(i<10){
//            listHtml+='<tr>'
//                +'<td>'+listData[i].current_score+'</td>'
//                +'<td>'+listData[i].date+'<i class="icon-ishop_8-03 style"></i></td>'
//                +'</tr>'
//        }
//        listHtmlAll+='<tr>'
//            +'<td>'
//            +a+'</td>'
//            +'<td>'+listData[i].current_score+'</td>'
//            +'<td>'+listData[i].date+'</td>'
//            +'</tr>'
//    }
//    if(listHtml.length!==0){
//        $("#points tbody").html(listHtml);
//    }else {
//        listHtml='<span>暂无数据</span>';
//        $("#points tbody").html(listHtml);
//    }
//    $("#points_all tbody").html(listHtmlAll)
//}
function xiaofeiContent(consumnData,consumnlistData){
    var consumnHtml="";
    var consumnHtmlall="";
    var arr=[];
    var unqiuearr=[];
    var hash={};
    $("#consume_total").html(consumnData.total_amount);
    $("#amount_total").html(consumnData.consume_times);
    $("#vip_card_1").html(consumnData.vip_card_type);
    $("#vip_card_num_1").html(consumnData.vip_card_no);
    $("#vip_name_1").html(consumnData.vip_name);
    for(var i=0;i<consumnlistData.length;i++){
        arr.push(consumnlistData[i].order_id);
    }
    for(var i=0;i<arr.length;i++){
        if(!hash[arr[i]]){
            hash[arr[i]] = true; //存入hash表
            unqiuearr.push(arr[i]);
        }
    }
    for(var i=0;i<unqiuearr.length;i++){
        var TR="";
        var total_money="0.0";
        var total_sug ="0.0";
        var discount = "";
        var date="";
        for(var j=0;j<consumnlistData.length;j++){
            if(consumnlistData[j].order_id==unqiuearr[i]){
                var n=$(TR).length+1;
                date=consumnlistData[j].buy_time;
                total_money = parseFloat(total_money)+parseFloat(consumnlistData[j].goods_price);
                total_sug = parseFloat(total_sug)+parseFloat(consumnlistData[j].goods_sug)
                TR+='<tr>'
                    +'<td style="width:5%">'+n+'</td>'
                    +'<td style="width:10%"><img src="'+consumnlistData[j].goods_img+'" onerror="imgError(this);" /></td>'
                    +'<td class="product_name">'+consumnlistData[j].goods_name+'</td>'
                    +'<td class="product_name">'+consumnlistData[j].goods_id+'</td>'
                    +'<td>'+consumnlistData[j].sku_id+'</td>'
                    +'<td>'+consumnlistData[j].goods_num+'</td>'
                    +'<td class="money">￥'+consumnlistData[j].goods_price+'</td>'
                    +'</tr>'
            }
        }
        discount = parseFloat(total_money/total_sug*10);
        discount = discount.toFixed(1);
        var tr=$(TR).length; //统计单数
        consumnHtml+='<tr>'
            +'<td >'+date+'</td>'
            +'<td>'+tr+'</td>'
            +'<td>'+discount+'</td>'
            +'<td>'+total_money+'<i class="icon-ishop_8-03 style"></i></td>'
            +'</tr>';
        consumnHtmlall+='<div class="record_list">'
            +'<div class="order_list">'
            +'<div class="list_head"><span class="black_font">日期:</span> '+date+'<span style="margin-left:10px">'+tr+'件商品</span><ul><li><span class="black_font">消费</span><em>￥</em><span class="consume_total">'+total_money+'</span></li><li>('+discount+'折<span style="margin-left: 5px">'+consumnlistData[i].discount+'</span>)</li><li style="color:#d39d6d ">'+consumnlistData[i].use_points+'积分</li></ul></div>'
            +'<div class="list_head"><span class="black_font">订单号:</span> '+unqiuearr[i]+'<span class="black_font" style="margin-left:10px">导购:</span> '+consumnlistData[i].user_name+'<ul><span style="color:#58b4bc">+'+consumnlistData[i].get_points+'积分</span></ul></div>'
            +'</div>'
            +'<hr/>'
            +'<table class="list_table">'
            +'<thead>'
            +'<tr>'
            +'<th>序号</th>'
            +'<th>商品图片</th>'
            +'<th class="product_name">商品名称</th>'
            +'<th class="product_name">商品编号</th>'
            +'<th>商品条码</th>'
            +'<th>件数</th>'
            +'<th>价格</th>'
            +'</tr>'
            +'</thead>'
            +'<tbody>'
            +TR
            +'</tbody>'
            +'</table>'
            +'</div>'
    }
    if(consumnHtml.length!==0){
        $("#consum tbody").html(consumnHtml);
    }else {
        $("#consum tbody").html('<span>暂无数据</span>');
    }
    if(consumnHtmlall.length==0){
        $("#consum_all").html("<p>暂无相关消费记录</p>");
    }else {
        $("#consum_all").html(consumnHtmlall);
    }
}
function fuzhi(data){
    var sex=data.sex;
    if(sex=="M"){
        $("#vip_name").next().addClass("icon-ishop_9-03");
        $("#USER_SEX").val("男");
    }else{
        $("#vip_name").next().addClass("icon-ishop_9-02");
        $("#USER_SEX").val("女");
    }
    $("#vip_name").html(data.vip_name);
    $("#topUpVipName").val(data.vip_name);
    $("#topUpCard").val(data.cardno);
    $("#vip_name").attr("data-id",data.vip_id);
    $('#vip_name_edit').attr('data_vip_id',data.vip_id);
    $("#vip_name_edit").val(data.vip_name);
    $("#vip_card_no").html(data.cardno);
    $("#vip_card_no_edit").val(data.cardno);
    $("#vip_card_type").html(data.vip_card_type);
    $("#vip_card_type_edit").val(data.vip_card_type);
    $("#balance_message").val(data.balance);
    $("#monetary").val(data.total_amount);
    $("#consume_cishu").val(data.consume_times);
    $("#open_card_date").val(data.join_date);
    $("#vip_phone").html(data.vip_phone);
    $("#vip_phone_edit").val(data.vip_phone);
    $("#user_name").html(data.user_name);
    $("#user_name_edit").val(data.user_name);
    $("#vip_total_amount").html(data.total_amount+'&nbsp元');
    $("#join_date").html(data.join_date);
    $("#vip_birthday").html(data.vip_birthday);
    $("#store_name").html(data.store_name);
    $("#store_name_edit").val(data.store_name);
    $("#user_group_edit").val(data.vip_group_name);
    $("#vip_birthday_edit").val(data.vip_birthday);
    $("#corp_code").html(data.corp_code);
    $("#vip_consume_times").html(data.consume_times+'&nbsp次');
    if(data.dormant_time=='无'){
        $("#vip_dormant_time").html(data.dormant_time);
    }else{
        $("#vip_dormant_time").html(data.dormant_time+'&nbsp天');
    }
    if(data.vip_avatar){
        $("#person-img").attr("src",data.vip_avatar);
        $("#IMG").attr("src",data.vip_avatar);
    }else{
        $("#person-img").attr("src",'../img/head.png');
        $("#IMG").attr("src",'../img/head.png');
    }
}
function imgError(image){//图片404默认图片
    $(image).attr("src", "../img/goods_default_image.png");
}
$("#more_message").click(function(){
    gotovipallmessage();
});
$("#edit_message").click(function(){
    gotovipallmessage();
});
function gotovipallmessage(){
    $('html,body').animate({
        'scrollTop': 0
    },0);
    $("#VIP_Message").hide();
    $("#VIP_edit").show();
    $("#nav_bar").children().eq(0).addClass("active1");
    $("#nav_bar").children().eq(0).siblings().removeClass("active1");
    $(".all_list").children().eq(0).show();
    $(".all_list").children().eq(0).siblings().hide();
    $("#remark").animate({left:0},0.1);
}
Array.prototype.removeByValue = function(val) {
    for(var i=0; i<this.length; i++) {
        if(this[i] == val) {
            this.splice(i, 1);
            break;
        }
    }
}
function showOption(){
    $(".drop_down input").click(function (){
        var ul=$(this).next(".expand_selection");
        if(ul.length){
            if(ul.css("display")=="none"){
                ul.show();
            }else{
                ul.hide();
            }
        }else{
            frame();
            $('.frame').html('暂无数据');
        }

    });
    $(".drop_down input").blur(function(){
        var ul=$(this).next(".expand_selection");
        setTimeout(function(){
            ul.hide();
        },200);
    });
    $(document).click(function(e){
        if($(e.target).is(".drop_down input")){
            return;
        }else{
            $(".expand .expand_selection").hide();
        }
    })
}
function getoselectvalue(){//点击模拟的select 获取值给input
    $(".expand_selection li").click(function(){
        $(this).parent().prev().val($(this).html());
        $(this).parent().hide()
    })
}
$("#cancel").click(function(){//关闭删除相册时的提示框,取消删除相册
        $("#tk").hide();
        $("#delete").attr("data-time","");
        whir.loading.remove("mask");
        return false;
    });
$("#delete").click(function(){//确认删除相册
        $("#tk").hide();
        var time=$(this).attr("data-time");
        var url=$("#Ablum-all").find("div[data-time='"+time+"']").prev().attr("src");
        var src = url;
            url=url.substring(url.indexOf("Album"));
        var param={};
        param["vip_id"]=sessionStorage.getItem("id");
        param["corp_code"]=sessionStorage.getItem("corp_code");
        param["time"]=time;
        oc.postRequire("post","/vip/vipAlbumDelete","",param,function(data){
            if(data.message="success"){
                deleteAblum(url);
                $("#Ablum-all").find("div[data-time='"+time+"']").parent().remove();
                frame();
                $('.frame').html('删除成功');
                swip_image.removeByValue(src);
            }else{
                frame();
                $('.frame').html('删除失败');
            }
            whir.loading.remove("mask");
        })
    });
function deleteAblum(key){
    var client = new OSS.Wrapper({
        region: 'oss-cn-hangzhou',
        accessKeyId: 'O2zXL39br8rSn1zC',
        accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
        bucket: 'products-image'
    });
    var storeAs=key;
    client.delete(storeAs).then(function (result) {
    }).catch(function (err) {
    });
}
function frame(){
    var left=($(window).width())/2;//弹框定位的left值
    var tp=($(window).height())/2;//弹框定位的top值
    $('.frame').remove();
    $('.con_table').append('<div class="frame" style="left:'+left+'px;top:'+tp+'px;"></div>');
    $(".frame").animate({opacity:"1"},1000);
    $(".frame").animate({opacity:"0"},1000);
    setTimeout(function(){
        $(".frame").hide();
    },2000);
}
$("#expand_send").click(function(){
    var param=getexpandValue();
    postInfo('extend',param);
});
$("#remark_keep").click(function(){
    postInfo('remark',$("#remark_value").val());
});
function postInfo(type,value){//修改拓展信息和备注
    var param_expand={};
    param_expand['vip_id']=sessionStorage.getItem("id");
    param_expand['card_no']=$("#vip_card_no").text();
    param_expand['phone']=$("#vip_phone").text();
    param_expand['corp_code']=sessionStorage.getItem("corp_code");
    param_expand[type]=value;
    oc.postRequire("post","/vip/vipSaveInfo","",param_expand,function(data){
        if(data.code=="0"){
            frame();
            $('.frame').html('保存成功');
        }else{
            frame();
            $('.frame').html('保存失败');
        }
    })
}
function getexpandValue(){
    var param_expand={};
    var INPUT=$("#extend ul").find('input');
    for(var i=0;i<INPUT.length;i++){
        var KEY="";
        var VALUE="";
        KEY=$(INPUT[i]).attr("data-key");
        VALUE=$(INPUT[i]).val().trim();
        param_expand[KEY]=VALUE;
    }
    return param_expand;
}
function gradeChange(grade) {//会员升降级
    var param={};
    param['vip_id']=sessionStorage.getItem("id");
    param['vip_card_type']=$("#vip_card_type").html();
    param['type']=grade;
    param['corp_code']=sessionStorage.getItem("corp_code");
    oc.postRequire("post","/vip/changeVipType","0",param,function (data) {
       if(data.code == 0){
            
       } else if(data.code == -1){
           console.log(data.message);
       }
    });
}
//收藏夹列表
function superaddition(data){//页面加载循环
    if(data.length == 0){
        var len = $("#collect_table thead tr th").length;
        var i;
        for(i=0;i<10;i++){
            $("#collect_table tbody").append("<tr></tr>");
            for(var j=0;j<len;j++){
                $($("#collect_table tbody tr")[i]).append("<td></td>");
            }
        }
        $("#collect_table tbody tr:nth-child(5)").append("<span style='position:absolute;left:54%;font-size: 15px;color:#999'>暂无内容</span>");
    }
    for (var i = 0; i < data.length; i++) {
        var TD="";
        var a=i+1;
        var product_image=data[i].product_image;
        if(product_image==""){
            product_image="../img/goods_default_image.png";
        }
        TD="<td><img src='"+product_image+"'></td><td>"
            + data[i].product_id
            +"</td><td><span title='"+data[i].product_name+"'>"
            + data[i].product_name
            + "</span></td><td>"
            + data[i].create_time
            + "</td><td>"
            + data[i].original_price
            + "</td><td>"
            + data[i].price
            + "</td><td><span title='"+data[i].description+"'>"
            + data[i].description
            + "</span></td>"
        $("#collect_table tbody").append("<tr><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
            + i
            + 1
            + "'/><label for='checkboxTwoInput"
            + i
            + 1
            + "'></label></div>"
            + "</td><td style='text-align:left;'>"
            + a
            + "</td>"
            + TD
            + "</tr>");
    }
    $(".th th:first-child input").removeAttr("checked");
}
function jumpBianse(){
    $(document).ready(function(){//隔行变色
        $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
        $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
    })
    //点击tr input是选择状态  tr增加class属性
    $(".table tbody tr").click(function(){
        var input=$(this).find("input")[0];
        var thinput=$("thead input")[0];
        $(this).toggleClass("tr");
        if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
            input.checked = true;
            $(this).addClass("tr");
        }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
            if(thinput.type=="checkbox"&&input.name=="test"&&input.checked==true){
                thinput.checked=false;
            }
            input.checked = false;
            $(this).removeClass("tr");
        }
    });
}
function GET(){
    var param={};
    param["corp_code"]=sessionStorage.getItem("corp_code");;
    param['open_id']='';
    param["vip_card_no"]=$("#vip_card_no").html();
    oc.postRequire("post","/vip/avorites","",param,function(data){
        if(data.code=="0"){
            $(".table tbody").empty();
            var messages=JSON.parse(data.message);
            var list=messages.message.list;
            //var list=list.list;
            superaddition(list);
            jumpBianse();
        }else if(data.code=="-1"){
            console.log(data.message);
        }
    });
}
//全选
function checkAll(name){
    var el=$("tbody input");
    el.parents("tr").addClass("tr");
    var len = el.length;

    for(var i=0; i<len; i++)
    {
        if((el[i].type=="checkbox") && (el[i].name==name))
        {
            el[i].checked = true;
        }
    }
}
//取消全选
function clearAll(name){
    var el=$("tbody input");
    el.parents("tr").removeClass("tr");
    var len = el.length;
    for(var i=0; i<len; i++)
    {
        if((el[i].type=="checkbox") && (el[i].name==name))
        {
            el[i].checked = false;
        }
    }
}
$("#page_row").click(function(){
    if("block" == $("#liebiao").css("display")){
        hideLi();
    }else{
        showLi();
    }
});
$("#liebiao li").each(function(i,v){
    $(this).click(function(){
        pageSize=$(this).attr('id');
        if(value==""&&filtrate==""){
            inx=1;
            GET(inx,pageSize);
        }else if(value!==""){
            inx=1;
            param["pageSize"]=pageSize;
            param["pageNumber"]=inx;
            POST(inx,pageSize);
        }else if(filtrate!==""){
            inx=1;
            _param["pageNumber"]=inx;
            _param["pageSize"]=pageSize;
            filtrates(inx,pageSize);
        }
        $("#page_row").val($(this).html());
        hideLi();
    });
});
$("#page_row").blur(function(){
    setTimeout(hideLi,200);
});
$("#VIP_avatar").change(function(e){
    var corp_code=sessionStorage.getItem("corp_code");
    var id=sessionStorage.getItem("id");
    var file = e.target.files[0];
    var client = new OSS.Wrapper({
        region: 'oss-cn-hangzhou',
        accessKeyId: 'O2zXL39br8rSn1zC',
        accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
        bucket: 'products-image'
    });
    var storeAs = 'Avatar/Vip/iShow/'+corp_code.trim()+'-'+id.trim()+'.jpg';
    client.multipartUpload(storeAs, file).then(function (result) {
        var url="http://products-image.oss-cn-hangzhou.aliyuncs.com/"+result.name;
        $("#IMG").attr("src",url+'?'+Math.random());
        $(".person-img").css('backgroundImage','url('+url+'?'+Math.random()+')');
        postInfo('avatar',url);
    }).catch(function (err) {
    });
});
//会员升降级
$("#grade_up").click(function () {
    gradeChange("upgrade");
});
$("#grade_down").click(function () {
   gradeChange("degrade");
});
//消费记录搜索
$(".record_input input").keydown(function () {
    var event=window.event||arguments[0];
    time_start=$(".record_date li:nth-child(1) input").val().trim();
    time_end=$(".record_date li:nth-child(2) input").val().trim();
    text_first=$(".record_input li:nth-child(1) input").val().trim();
    text_second=$(".record_input li:nth-child(2) input").val().trim();
    text_third=$(".record_input li:nth-child(3) input").val().trim();
    if(event.keyCode == 13){
        getVipPoints();
    }
});
$(".record_search").click(function () {
    time_start=$(".record_date li:nth-child(1) input").val().trim();
    time_end=$(".record_date li:nth-child(2) input").val().trim();
    text_first=$(".record_input li:nth-child(1) input").val().trim();
    text_second=$(".record_input li:nth-child(2) input").val().trim();
    text_third=$(".record_input li:nth-child(3) input").val().trim();
    getVipPoints();
});
//发送手机验证码
$("#send_code i,#send_code input").click(function () {
    $("#send_code ul").toggle();
});
$("#send_code ul li").click(function () {
    $("#send_code input").val($(this).html());
    $("#send_code ul").hide();
});
