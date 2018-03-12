var text_first="";
var text_second="";
var text_third="";
var time_start="";
var time_end="";
var inx=1;//默认是第一页
var row_num = 0;//消费记录分页
var consume_next =false;//消费下一页标志
$("#consum_all").unbind("scroll").bind("scroll",function(){
    var offsetHeight = $(this).height(),
        scrollHeight = $(this)[0].scrollHeight,
        scrollTop = $(this)[0].scrollTop;
    if(scrollTop + offsetHeight >= scrollHeight && scrollTop != 0){
        if(consume_next){
            getVipPoints("","2");
        }else{
            return
        }
    }
});
function getVipInfo(){
    var param_info={};
    // whir.loading.add("",0.5);
    param_info["vip_id"]= sessionStorage.getItem("id");
    param_info["corp_code"]=sessionStorage.getItem("corp_code");
    $("#right_module,#left_module").show();
    oc.postRequire("post","/vip/vipInfo","",param_info,function(data){
        var vipData=JSON.parse(data.message);
        var vipDataList=vipData.list;
        //********
        var open_id = vipDataList.open_id;
        var app_id = vipDataList.app_id;
        sessionStorage.setItem("open_id",open_id);
        sessionStorage.setItem("app_id",app_id);
        //********
        var extend=vipData.extend;
        var extend_info=vipData.extend_info==""?{}:JSON.parse(vipData.extend_info);
        var extendhtml="";
        var conSumData=vipData.list;
        var month=conSumData.month;
        if(month>11){
            $("#amt").html("近一年消费金额(元)");
            $("#num").html("近一年消费单数")
        }else{
            $("#amt").html("近"+month+"个月消费金额(元)");
            $("#num").html("近"+month+"个月消费单数");
        }
        $("#total_amount_Y").html(formatCurrency(conSumData.amount_Y));
        $("#consume_times_Y").html(formatCurrency(conSumData.times_Y));
        $("#total_amount").html(formatCurrency(conSumData.total_amount));
        $("#consume_times").html(formatCurrency(conSumData.consume_times));
        $("#dormant_time").html(formatCurrency(conSumData.dormant_time));
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
            var extend_name = extend[i].required == "Y" ? "<b title='"+extend[i].name+"' style='color: #c26655'>"+extend[i].name+"*</b>" : "<b title='"+extend[i].name+"'>"+extend[i].name+"</b>";
            if(extend[i].type=='rule'){
                extendhtml+="<li class='rule'>"
                    +"</li>"
            }
            if(extend[i].type=="text"){
                var value=getvalue();
                extendhtml+="<li>"
                    + extend_name
                    +"<div>"
                    +"<input type='text' value='"+value+"' data-key='"+extend[i].key+"' data-attr='"+extend[i].param_attribute+"' />"
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
                    + extend_name
                    +'<div class="position" >'
                    +'<input class="input_select" readonly value="'+value+'" type="text" data-key="'+extend[i].key+'" />'
                    +ul
                    +'</div>'
                    +'</li>'
            }
            if(extend[i].type=="check"){
                var value=getvalue();
                var valueArray=value.split(",");  //3.5
                var selectValue=extend[i].values;
                var ul="";
                if(selectValue!=""){
                    selectValue=selectValue.split(",");  //3.5.6.7
                     ul="<ul class='expand_selection'>";
                    for(var b=0;b<selectValue.length;b++){
                        var ck ="";
                        for(var a=0;a<valueArray.length;a++){
                            if(valueArray[a]==selectValue[b]){
                                ck ="checked = 'checked'";
                            }
                        }
                        ul+="<li title='"+selectValue[b]+"' class='type_check'><input type='checkbox' "+ck+"  style='background: none;width: 15px;vertical-align: top;margin:-2px 5px 0 0;padding: 0;'/><span style='vertical-align: top'>"+selectValue[b]+"</span></li>";
                    }
                }
                extendhtml+='<li class="drop_down item_1">'
                    +extend_name
                    +'<div class="position" >'
                    +'<input class="input_select" readonly value="'+value+'" title="'+value+'" type="text" data-type="check" data-key="'+extend[i].key+'" />'
                    +ul
                    +'</div>'
                    +'</li>';
            }
            if(extend[i].type=="date"){
                var value=getvalue();
                extendhtml+='<li >'
                +extend_name
                +'<div>'
                +'<input type="text" data-key="'+extend[i].key+'" id="extendDate'+i+'" readonly="true" placeholder="请输入日期" class="laydate-icon" value="'+value+'" onclick="laydate({elem:\'#extendDate'+i+'\',istime: false, format: \'YYYY-MM-DD\',choose:Ownformat,istoday:false})">'
                +'</div>'
                +'</li>'
            }
            if(extend[i].type=="longtext"){
                var value=getvalue();
                extendhtml+='<li style="width: 100%">'
                +extend_name
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
        // whir.loading.remove();
        $("#right_module,#left_module").hide();
        row_num = 0;
        getVipPoints(vipDataList.corp_code);
        showOption();
        getoselectvalue();
        GET(vipDataList.app_id,vipDataList.open_id);
    });
}
function Ownformat(datas) {//拓展资料日期格式MM—DD
    var date=datas.split("-");
    date=date[1]+"-"+date[2];
    $(this.elem).val(date);
}
function getVipPoints(code,type){
    var parmam_jifen={};
    parmam_jifen['time_start']=time_start;
    parmam_jifen['time_end']=time_end;
    parmam_jifen['order_id']=text_first;
    parmam_jifen['goods_name']=text_second;
    parmam_jifen['goods_code']=text_third;
    parmam_jifen['row_num']=row_num;
    parmam_jifen['vip_id']=sessionStorage.getItem("id");
    parmam_jifen['corp_code']=sessionStorage.getItem("corp_code");
    if(type!==undefined){
        parmam_jifen['type']=type;
    }
    oc.postRequire("post","/vip/vipPoints","",parmam_jifen,function(data){
        var Data=JSON.parse(data.message);
        if(type=='1'){
            var pointsData=Data.result_points;//积分
            var listData=pointsData.vip_integral_watercourse;//积分list
            jifenContent(listData,pointsData);
        }
        if(type=='2'){
            var consumnData=Data.result_consumn;//消费
            var consumnlistData=consumnData.list_wardrobe;//消费list
            xiaofeiContent(consumnData,consumnlistData);
        }
        if(type==undefined){
            var pointsData=Data.result_points;//积分
            var consumnData=Data.result_consumn;//消费
            var consumnlistData=consumnData.list_wardrobe;//消费list
            var listData=pointsData.vip_integral_watercourse;//积分list
            jifenContent(listData,pointsData);
            xiaofeiContent(consumnData,consumnlistData);
        }
    });
}
function jifenContent(listData,pointsData){
   //$("#points_total").html(pointsData.recordscount);
   var listHtml="";
   var listHtmlAll="";
   $("#point_table tbody").empty();
   if(listData.length>0){
        for(var i=0;i<listData.length;i++){
            var a=i+1;
            var is_valis="";
            if(listData[i].is_valis=="Y"){
                is_valis="是";
            }else if(listData[i].is_valis=="N"){
                is_valis="否"
            }
            // if(i<10){
            //     listHtml+='<tr>'
            //         +'<td>'+listData[i].current_score+'</td>'
            //         +'<td>'+listData[i].date+'<i class="icon-ishop_8-03 style"></i></td>'
            //         +'</tr>'
            // }
            listHtmlAll+='<tr>'
                +'<td>'
                +a+'</td>'
                +'<td>'+listData[i].time+'</td>'
                +'<td>'+listData[i].docno+'</td>'
                +'<td>'+is_valis+'</td>'
                +'<td>'+listData[i].validdate+'</td>'
                +'<td>'+formatCurrency(listData[i].point)+'</td>'
                +'<td style="width: 400px"><span style="width: 400px" title="'+listData[i].memo+'">'+listData[i].memo+'</span></td>'
                +'</tr>';
        }
        // if(listHtml.length!==0){
        //     $("#points tbody").html(listHtml);
        // }else {
        //     listHtml='<span>暂无数据</span>';
        //     $("#points tbody").html(listHtml);
        // }
        $("#point_table tbody").html(listHtmlAll);
    }else {
       for(var i=0;i<10;i++){
           var td="";
           if(i==4){
               td+="<tr><td></td><td></td><td></td><td><span style='width: auto'>暂无内容</span></td><td></td><td></td><td></td></tr>";
           }else {
               td+="<tr><td></td><td></td><td><span style='width: auto'></span></td><td></td><td></td><td></td><td></td></tr>"
           }
           $("#point_table tbody").append(td);
       }
    }
}
function xiaofeiContent(consumnData,consumnlistData){
    var consumnHtml="";
    var consumnHtmlall="";
    var arr=[];
    var unqiuearr=[];
    var hash={};
    $("#consume_total").html(formatCurrency(consumnData.total_amount));
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
        var name="";
        var storeName = "";
        var tr = 0;
        var total_points=0;
        for(var j=0;j<consumnlistData.length;j++){
            if(consumnlistData[j].order_id==unqiuearr[i]){
                var n=$(TR).length+1;
                tr+=parseFloat(consumnlistData[j].goods_num);
                date=consumnlistData[j].buy_time;
                name=name+consumnlistData[j].user_name+",";
                storeName=consumnlistData[j].store_name;
                total_money = parseFloat(total_money)+parseFloat(consumnlistData[j].goods_price);
                total_money = total_money.toFixed(2);
                total_sug = parseFloat(total_sug)+parseFloat(consumnlistData[j].goods_sug);
                total_points = consumnlistData[j].points;
                TR+='<tr>'
                    +'<td style="width:5%">'+n+'</td>'
                    +'<td style="width:10%"><img src="'+consumnlistData[j].goods_img+'" onerror="imgError(this);" /></td>'
                    +'<td class="product_name">'+consumnlistData[j].goods_id+'</td>'
                    +'<td class="product_name">'+consumnlistData[j].goods_name+'</td>'
                    +'<td class="product_name">'+consumnlistData[j].sku_code+'</td>'
                    +'<td>'+formatCurrency(consumnlistData[j].goods_num)+'</td>'
                    +'<td>'+formatCurrency(consumnlistData[j].goods_sug)+'</td>'
                    +'<td>'+consumnlistData[j].discount+'</td>'
                    +'<td class="money">￥'+formatCurrency(consumnlistData[j].goods_price)+'</td>'
                    +'</tr>'
            }
        }
        var nameArr = name.split(",");
        var newArr = [];
        var nameObj = {};
        nameArr.sort();
        for(var k=1;k<nameArr.length;k++){//name导购
            if(!nameObj[nameArr[k]]){
                newArr.push(nameArr[k]);
                nameObj[nameArr[k]] = true;
            }
        }
        name = newArr.join(",");
        if(total_sug==0.0){
            discount=0;
        }else {
            discount = parseFloat(total_money/total_sug*10);
            discount = discount.toFixed(1);
        }
        consumnHtml+='<tr>'
            +'<td >'+date+'</td>'
            +'<td>'+formatCurrency(tr)+'</td>'
            +'<td>'+discount+'</td>'
            +'<td>'+formatCurrency(total_money)+'<i class="icon-ishop_8-03 style"></i></td>'
            +'</tr>';
        consumnHtmlall+='<div class="record_list">'
            +'<div class="order_list">'
            +'<div class="list_head"><span class="black_font">日期:</span> '+date+'<span style="margin-left:10px">'+tr+'款商品</span><ul><li><span class="black_font">消费</span><em>￥</em><span class="consume_total">'+formatCurrency(total_money)+'</span></li><li>('+discount+'折)</li></ul></div>'
            +'<div class="list_head"><span class="black_font">订单号:</span> '+unqiuearr[i]+'<span class="black_font" style="margin-left:10px">导购:</span> '+name+'<span class="black_font" style="margin-left:10px">店铺:</span>'+storeName+'<ul><span class="record_total_points" style="color:#58b4bc">'+total_points+'积分</span></ul></div>'
            +'</div>'
            +'<hr/>'
            +'<table class="list_table">'
            +'<thead>'
            +'<tr>'
            +'<th>序号</th>'
            +'<th>商品图片</th>'
            +'<th class="product_name">款号</th>'
            +'<th class="product_name">商品名称</th>'
            +'<th>商品条码</th>'
            +'<th>件数</th>'
            +'<th>吊牌价</th>'
            +'<th>折扣</th>'
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
        var td="";
        for(var i=0;i<10;i++){
            if(i==4){
                td+="<tr style='border: none'><td colspan='4'>暂无数据</td></tr>";
            }else {
                td+="<tr style='border: none'><td></td><td></td><td></td></tr>"
            }
        }
        $("#consum tbody").html(td);
    }
    if(consumnHtmlall.length==0){
        $("#consum_all").html("<p style='height: auto'>暂无相关消费记录</p>");
    }else {
        row_num == 0 ?$("#consum_all").html(consumnHtmlall):$("#consum_all").append(consumnHtmlall);
    }
    if(consumnlistData.length>=40){
        consume_next=true;
        row_num+=40;
    }else {
        consume_next=false;
    }
}
function formatCurrency(num) {
    num=String(num);
    var reg=num.indexOf('.') >-1 ? /(\d{1,3})(?=(?:\d{3})+\.)/g : /(\d{1,3})(?=(?:\d{3})+$)/g;//千分符的正则
    num=num.replace(reg, '$1,');//千分位格式化
    return num;
}
function fuzhi(data){
    var sex=data.sex;
    var state=data.fr_active;
    if(state=="Y"){
        state="未冻结";
    }else if(state=="N") {
        state="已冻结"
    }
    if(sex=="M"){
        $("#vip_name").next().addClass("icon-ishop_9-03");
        $("#USER_SEX").val("男");
    }else{
        $("#vip_name").next().addClass("icon-ishop_9-02");
        $("#USER_SEX").val("女");
    }
    var address = (data.province == '' && data.city == '' && data.area == '')?'':data.province+"/"+data.city+"/"+data.area;
    $("#vip_name").html(data.vip_name);
    $("#points_vip_name").html(data.vip_name);
    $("#topUpVipName").val(data.vip_name);
    $("#topUpCard").val(data.cardno);
    $("#vip_name").attr("data-id",data.vip_id);
    $('#vip_name_edit').attr('data_vip_id',data.vip_id);
    $("#vip_name_edit").val(data.vip_name);
    $("#vip_card_no").html(data.cardno);
    $("#points_card_num").html(data.cardno);
    $("#vip_card_no_edit").val(data.cardno);
    $("#vip_card_type").html(data.vip_card_type);
    $("#vip_card_type_edit").val(data.vip_card_type);
    $("#points_card_grade").html(data.vip_card_type);
    $("#STORE_address").val(address);//获取省市区
    $("#address_details_input").val(data.address);//获取详细地址
    $("#act_store_input").val(data.act_store_name);//活跃门店
    $("#balance_message").val(formatCurrency(data.balance));
    $("#balance").text(formatCurrency(data.balance));
    $("#monetary").val(formatCurrency(data.total_amount));
    $("#vip_points").val(formatCurrency(data.points));
    $("#vip_point").html(formatCurrency(data.points));
    $("#points_total").html(data.points);
    $("#vip_protect_point").html(formatCurrency(data.protect_points));
    $("#protective_integral").val(formatCurrency(data.protect_points));
    $("#vip_state").val(state);
    $("#consume_cishu").val(formatCurrency(data.consume_times));
    $("#open_card_date").val(data.join_date);
    $("#vip_phone").html(data.vip_phone);
    $("#vip_phone_edit").val(data.vip_phone);
    $("#user_name").html(data.user_name);
    $("#user_name_edit").val(data.user_name);
    $("#user_name_edit").attr("data-code",data.user_code);
    $("#vip_total_amount").html(formatCurrency(data.total_amount)+'&nbsp元');
    $("#join_date").html(data.join_date);
    $("#vip_birthday").html(data.vip_birthday);
    $("#store_name").html(data.store_name);
    $("#store_name").attr("title",data.store_name);
    $("#store_name").attr("data-store_code",data.store_code);
    $("#store_name_edit").val(data.store_name);
    $("#store_name_edit").attr("data-code",data.store_code);
    $("#user_group_edit").val(data.vip_group_name);
    $("#vip_birthday_edit").val(data.vip_birthday);
    $("#corp_code").html(data.corp_code);
    $("#vip_consume_times").html(formatCurrency(data.consume_times)+'&nbsp次');
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
    userlist();
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
};
function showOption(){
    $(".drop_down input[type=text]").click(function (){
        $(".expand_selection").hide();
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
        if($(this).attr("data-type")=="check") return;
        setTimeout(function(){
            ul.hide();
        },200);
    });
    $(".drop_down li.type_check input").click(function(){
        var input=$(this).parents("ul.expand_selection").find("li.type_check input[type='checkbox']:checked");
        var text="";
        for(var i=input.length-1;i>=0;i--){
            if(i>0){
                text+=$(input[i]).next().html()+","
            }else{
                text+=$(input[i]).next().html();
            }
        }
        var textArray=text.split(",").reverse();
        text=textArray.join(",");
        $(this).parents("ul").prev().val(text);
        $(this).parents("ul").prev().attr("title",text)
    });
    $(document).click(function(e){
        if($(e.target).is(".drop_down input") || $(e.target).is(".drop_down li.type_check")){
            return;
        }else{
            $(".expand .expand_selection").hide();
        }
    })
}
function getoselectvalue(){//点击模拟的select 获取值给input
    $(".expand_selection li").click(function(){
        if($(this).find("input").length!=0){
            return
        }
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
        //var src = url;
        var  key=url.replace("http://products-image.oss-cn-hangzhou.aliyuncs.com/","");
        //var  key=url.substring(url.indexOf("Album"));
        var param={};
            param.oss_url=key;
        //param["vip_id"]=sessionStorage.getItem("id");
        //param["corp_code"]=sessionStorage.getItem("corp_code");
        //param["time"]=time;
        whir.loading.add("",0.1);
        oc.postRequire("post","/goods/deleteOss","",param,function(data){
            if(data.message=="success"){
                //deleteAblum(url);
                $("#Ablum-all").find("div[data-time='"+time+"']").parent().remove();
                swip_image.removeByValue(url);
                deleteAblum();
            }else{
                whir.loading.remove("",0.1);
                $("#editTk").show();
                $("#msg").html(data.message==""?"删除失败":data.message)
            }
        })
    });
function deleteAblum(){
    var param={};
    var time=$("#delete").attr("data-time");
    param["vip_id"]=sessionStorage.getItem("id");
    param["corp_code"]=sessionStorage.getItem("corp_code");
    param["time"]=time;
    oc.postRequire("post","/vip/vipAlbumDelete","",param,function(data){
        whir.loading.remove("",0.1);
        if(data.code=="0" && data.message=="success"){
            $("#editTk").show();
            $("#msg").html("删除成功");
        }else{
            $("#editTk").show();
            $("#msg").html(data.message==""?"删除失败":data.message)
        }
    })
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
    if(param == 'true'){
        art.dialog({
            time: 1,
            zIndex: 10010,
            lock: true,
            cancel: false,
            content: "必填的拓展信息不能为空"
        });
        return
    }
    if(param == 'phone'){
        art.dialog({
            time: 1,
            zIndex: 10010,
            lock: true,
            cancel: false,
            content: "手机号格式不正确"
        });
        return
    }
    if(param == 'email'){
        art.dialog({
            time: 1,
            zIndex: 10010,
            lock: true,
            cancel: false,
            content: "邮箱格式不正确"
        });
        return
    }
    if(param == 'zip_code'){
        art.dialog({
            time: 1,
            zIndex: 10010,
            lock: true,
            cancel: false,
            content: "邮编不正确"
        });
        return
    }
    if(param == 'ID_number'){
        art.dialog({
            time: 1,
            zIndex: 10010,
            lock: true,
            cancel: false,
            content: "身份证格式不正确"
        });
        return
    }
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
    if(type=="avatar"){
    }else{
        whir.loading.add("",0.5);
    }
    oc.postRequire("post","/vip/vipSaveInfo","",param_expand,function(data){
        if(type=="avatar"){
            whir.loading.remove("上传中,请稍后...",0.1);
        }else{
            whir.loading.remove("",0.5);
        }
        whir.loading.add("mask");
        if(data.code=="0"){
           $("#editTk").show();
           $("#msg").html("保存成功")
        }else{
            $("#editTk").show();
            $("#msg").html(data.message==""?"保存失败":data.message)
        }
    })
}
function getexpandValue(){
    var param_expand={};
    var INPUT=$("#extend ul").find('input');
    var flag,reg;
    for(var i=0;i<INPUT.length;i++){
        var KEY="";
        var VALUE="";
        $(INPUT[i]).parents("li").find("b").text().indexOf("*") > 0 ? param_expand["required"] = "Y" : '';
        KEY=$(INPUT[i]).attr("data-key");
        VALUE=$(INPUT[i]).val().trim();
        if($(INPUT[i]).parents("li").find("b").text().indexOf("*") > 0 && VALUE == ''){
            flag = "true";
        }
        if($(INPUT[i]).attr("data-attr") == 'phone' && VALUE != ''){
            reg = /^(1[0-9]{10})$/;
            reg.test(VALUE) ? '' : flag = 'phone';
        }
        if($(INPUT[i]).attr("data-attr") == 'email' && VALUE != ''){
            reg = /\w@\w*\.\w/;
            reg.test(VALUE) ? '' : flag = 'email';
        }
        if($(INPUT[i]).attr("data-attr") == 'zip_code' && VALUE != ''){
            reg = /^[1-9][0-9]{5}$/;
            reg.test(VALUE) ? '' : flag = 'zip_code';
        }
        if($(INPUT[i]).attr("data-attr") == 'ID_number' && VALUE != ''){
            reg = /^\d{6}(18|19|20)?\d{2}(0[1-9]|1[12])(0[1-9]|[12]\d|3[01])\d{3}(\d|[xX])$/
            reg.test(VALUE) ? '' : flag = 'ID_number';
        }
        param_expand[KEY]=VALUE;
    }
    return flag != undefined ? flag : param_expand;
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
           art.dialog({
               time: 1,
               zIndex: 10010,
               lock: true,
               cancel: false,
               content: data.message
           });
       }
    });
}
//收藏夹列表
function superaddition(data){//页面加载循环
    if(data.length == 0){
        var len = $("#collect_table thead tr th").length;
        var len2=Math.ceil(len/2);
        var i;
        for(i=0;i<10;i++){
            $("#collect_table tbody").append("<tr></tr>");
            for(var j=0;j<len;j++){
                $($("#collect_table tbody tr")[i]).append("<td></td>");
            }
        }
        $("#collect_table tbody tr:nth-child(5) td:nth-child("+len2+")").append("<span style='font-size: 15px;color:#999'>暂无内容</span>");
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
            + "</span></td>";
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
        $("#collect_table tbody tr:odd").css("backgroundColor","#fff");
        $("#collect_table tbody tr:even").css("backgroundColor","#e8e8ed");
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
function GET(app_id,open_id){
    var param={};
    param["corp_code"]=sessionStorage.getItem("corp_code");
    param['open_id']=open_id;
    param["vip_card_no"]=$("#vip_card_no").html() != undefined ? $("#vip_card_no").html() : $("#card_wrap_s .vip_card_border").attr("data-cardno");
    param['app_id']=app_id;
    oc.postRequire("post","/vip/avorites","",param,function(data){
        if(data.code=="0"){
            $("#table tbody").empty();
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
var opt={
    id:"upAlbum",
    frameName:"upload",
    url:"/goods/uploadOss",
    fileName:"fileInput",
    format:['jpg','png','gif','bmp'],
    callBack:function(data){
        var data=JSON.parse(data);
        $("#upAlbum").replaceWith('<input type="file" id="upAlbum" name="fileInput" accept="image/jpg,image/jpeg,image/png,img/bmp" style="font-size: 10px;" autocomplete="off">')
        if(data.code=="0"){
            var path=JSON.parse(data.message).oss_path;
            console.log(path);
            addVipAlbum(path);
        }else{
            whir.loading.remove("上传中,请稍后...",0.1);
            art.dialog({
                time: 2,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
    }
};
var opt_avatar={
    id:"VIP_avatar",
    frameName:"upload",
    url:"/goods/uploadOss",
    fileName:"vipAvatar",
    format:['jpg','png','gif','bmp'],
    callBack:function(data){
        var data=JSON.parse(data);
        $("#VIP_avatar").replaceWith('<input type="file" id="VIP_avatar" name="vipAvatar" style="position: absolute;top:0;filter:alpha(opacity:0);opacity: 0;width:100px;height: 30px">')
        if(data.code=="0"){
            var path=JSON.parse(data.message).oss_path;
                var url=$("#IMG").attr("src");
                if(url.indexOf("http://products-image.oss")=="-1"){
                    $("#IMG").attr("src",path+'?'+Math.random());
                    $(".person-img").css('backgroundImage','url('+path+'?'+Math.random()+')');
                    postInfo('avatar',path);
                }else{
                    $("#IMG").attr("src",path+'?'+Math.random());
                    $(".person-img").css('backgroundImage','url('+path+'?'+Math.random()+')');
                    var key = url.replace("http://products-image.oss-cn-hangzhou.aliyuncs.com/","");
                    var  param={};
                    param.oss_url=key;
                    oc.postRequire("post","/goods/deleteOss","",param,function(data){
                        if(data.message=="success"){
                            postInfo('avatar',path);
                        }else{
                            whir.loading.remove("上传中,请稍后...",0.1);
                            whir.loading.remove("",0.1);
                            $("#editTk").show();
                            $("#msg").html(data.message==""?"删除图片失败":data.message)
                        }
                    });
                }
        }else{
            whir.loading.remove("上传中,请稍后...",0.1);
            art.dialog({
                time: 2,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
    }
};
function ajaxUpload(opt){
    /*
     参数说明:
     opt.id : 页面里file控件的ID;
     opt.frameName : iframe的name值;
     opt.url : 文件要提交到的地址;
     opt.format : 文件格式，以数组的形式传递，如['jpg','png','gif','bmp'];
     opt.callBack : 上传成功后回调;
     */
    var iName=opt.frameName; //太长了，变短点
    var iframe,form,file,fileParent;
    //创建iframe和form表单
    iframe = $('<iframe name="'+iName+'" style="display:none;"/>');
    form = $('<form method="post" style="display:none;" target="'+iName+'" action="'+opt.url+'"  name="form_'+iName+'" enctype="multipart/form-data" />');
    file = $('#'+opt.id); //通过id获取flie控件
    fileParent = file.parent(); //存父级
    file.appendTo(form);
    //插入body
    $(document.body).append(iframe).append(form);
    //取得所选文件的扩展名
    var str=file.val();
    str.substring( str.lastIndexOf('.')+1 );
    var fileFormat=/\.[a-zA-Z]+$/.exec(file.val())[0].substring(1);
    if(opt.format.join('-').indexOf(fileFormat)!=-1){
        form.submit();//格式通过验证后提交表单;
        whir.loading.add("上传中,请稍后...",0.1);
    }else{
        file.appendTo(fileParent); //将file控件放回到页面
        iframe.remove();
        form.remove();
        art.dialog({
            time: 2,
            lock:true,
            cancel: false,
            content: "请上传支持的文件格式"
        });
    }

    //文件提交完后
    iframe.load(function(){
        var data = $(this).contents().find('body').html();
        file.appendTo(fileParent);
        iframe.remove();
        form.remove();
        opt.callBack(data);
    })
}
$("#add_new_album").on("change","#upAlbum",function () {
    ajaxUpload(opt);
});
$(".change_touxiang").on("change","#VIP_avatar",function () {
    ajaxUpload(opt_avatar);
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
        row_num = 0;
        getVipPoints();
    }
});
$("#record_search").click(function () {
    time_start=$(".record_date li:nth-child(1) input").val().trim();
    time_end=$(".record_date li:nth-child(2) input").val().trim();
    text_first=$(".record_input li:nth-child(1) input").val().trim();
    text_second=$(".record_input li:nth-child(2) input").val().trim();
    text_third=$(".record_input li:nth-child(3) input").val().trim();
    row_num = 0;
    getVipPoints();
});
//消费记录导出
$("#record_export").click(function () {
    var arr=whir.loading.getPageSize();
    $("#export_consumeExport").show();
    $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
    $("#p").show();
});
$("#record_enter").click(function () {
    var param={};
    var li=$("#file_list_r input[type='checkbox']").parents("li");
    var tablemanager=[];
    if(li.length=="0"){
        frame();
        $('.frame').html('请把要导出的列移到右边');
        return;
    }
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("data-name");
        var z=$(li[i]).children("span").html();
        var param1={"column_name":r,"show_name":z};
        tablemanager.push(param1);
    }
    param['card_num'] = $("#vip_card_no").text() ? $("#vip_card_no").text() : $("#card_wrap_s .vip_card_border").attr("data-cardNo");;
    param['time_start'] = time_start;
    param['time_end'] = time_end;
    param['order_id'] = text_first;
    param['goods_name'] = text_second;
    param['type'] = "consume";
    param['goods_code'] = text_third;
    param['vip_id'] = sessionStorage.getItem("id");
    param['corp_code'] = sessionStorage.getItem("corp_code");
    param["tablemanager"]=tablemanager;
    whir.loading.add("处理中,请稍后", 0.5);//加载等待框
    oc.postRequire("post", "/vip/exportExeclvipPoints", "0", param, function (data) {
        whir.loading.remove();//移除加载框
        if (data.code == 0) {
            var message = JSON.parse(data.message);
            var path = message.path;
            path = path.substring(1, path.length - 1);
            $('#download_consume .album_enter a').attr("href","/"+path);
            $("#export_consumeExport").hide();
            $("#download_consume").show();
        } else if (data.code == -1) {
            art.dialog({
                time: 1,
                zIndex: 10010,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    });
});
$("#download_consume .album_enter,.download_close").click(function () {//导出下载后关闭
    $(this).parents(".tk").hide();
    $("#p").hide();
});
$("#download_consume .cancel").click(function () {//返回到提交导出窗口
    $("#export_consumeExport").show();
    $("#download_consume").hide();
});
$("#file_close").click(function () {
    $("#export_consumeExport").hide();
    $("#p").hide();
});
//积分导出
$("#point_export").click(function () {
    var arr=whir.loading.getPageSize();
    $("#export_points").show();
    $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
    $("#p").show();
});
$("#points_enter").click(function () {
    var param={};
    var li=$("#file_list_right input[type='checkbox']").parents("li");
    var tablemanager=[];
    if(li.length=="0"){
        frame();
        $('.frame').html('请把要导出的列移到右边');
        return;
    }
    for(var i=li.length-1;i>=0;i--){
        var r=$(li[i]).attr("data-name");
        var z=$(li[i]).children("span").html();
        var param1={"column_name":r,"show_name":z};
        tablemanager.push(param1);
    }
    param['card_num'] = $("#vip_card_no").text() ? $("#vip_card_no").text() : $("#card_wrap_s .vip_card_border").attr("data-cardNo");
    param['type'] = "point";
    param['vip_id'] = sessionStorage.getItem("id");
    param['corp_code'] = sessionStorage.getItem("corp_code");
    param["tablemanager"]=tablemanager;
    whir.loading.add("处理中,请稍后", 0.5);//加载等待框
    oc.postRequire("post", "/vip/exportExeclvipPoints", "0", param, function (data) {
        whir.loading.remove();//移除加载框
        if (data.code == 0) {
            var message = JSON.parse(data.message);
            var path = message.path;
            path = path.substring(1, path.length - 1);
            $('#download_points .album_enter a').attr("href","/"+path);
            $("#export_points").hide();
            $("#download_points").show();
        } else if (data.code == -1) {
            art.dialog({
                time: 1,
                zIndex: 10010,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
    });
});
$("#download_points .album_enter,.download_close").click(function () {//导出下载后关闭
    $(this).parents(".tk").hide();
    $("#p").hide();
});
$("#download_points .cancel").click(function () {//返回到提交导出窗口
    $("#export_points").show();
    $("#download_points").hide();
});
$("#X_points").click(function () {
    $("#export_points").hide();
    $("#p").hide();
});
//备忘录新增,删除
$("#memorandum_save").click(function ()  {
    var date=getNowDate();
    var val=$(".memorandum_top textarea").val().trim();
    var corp_code=sessionStorage.getItem("corp_code");
    var vip_id=sessionStorage.getItem("id");
    var memoid=corp_code+vip_id+date[1];
    var param={};
    param["vip_id"]=vip_id;
    param["memo"]=val;
    param["time"]=date[0];
    param["phone"]=$("#vip_phone").html();
    param["card_no"]=$("#vip_card_no").html()?$("#vip_card_no").html():$("#card_wrap_s .vip_card_border").attr("data-cardNo");
    param["corp_code"]=corp_code;
    whir.loading.add("mask");
    if(val==""){
       $("#editTk").show();
        $("#msg").html("请先输入要添加的备忘内容");
        return ;
    }
    whir.loading.add("",0.5);
    oc.postRequire("post","/vip/vipSaveInfo","",param,function(data){
        whir.loading.remove();
        if(data.code==0){
            var html=$("#memorandum_table tbody tr td:first-child").html();
            if(html==""){
                $("#memorandum_table tbody").empty();
            }
            var tr="<tr id='"+memoid+"'><td>"
                + date[0]
                + "</td><td><span title='"+val+"'>"
                + val
                +"</span></td><td class='icon-ishop_6-12'></td></tr>";
            $("#memorandum_table tbody").append(tr);
            $("#editTk").show();
            $("#msg").html("新增成功");
        }else if(data.code==-1){
            $("#editTk").show();
            $("#msg").html(data.message==""?"新增失败":data.message);
        }
    });
    $(".memorandum_top textarea").val("");
});
$("#memorandum_table").on("click",".icon-ishop_6-12",function () {
    var param={};
    var _this=$(this);
    param["vip_id"]=sessionStorage.getItem("id");
    param["memoid"]=$(this).parent("tr").attr("id");
    param["corp_code"]=sessionStorage.getItem("corp_code");
    oc.postRequire("post","/vip/vipMemoDelete","",param,function (data) {
        if(data.code=="0"){
            $(_this).parents("tr").remove();
            var len=$("#memorandum_table tbody tr").length;
            if(len==0){
                for(var i=0;i<10;i++){
                    var td="";
                    if(i==4){
                        td+="<tr><td colspan='3' style='text-align: center'>暂无内容</td></tr>";
                    }else {
                        td+="<tr><td colspan='3'></td></tr>"
                    }
                    $("#memorandum_table tbody").append(td);
                }
            }
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "删除失败"
            });
        }
    });
});
function getNowDate() {//获取当前日期
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    var H=date.getHours();
    var M=date.getMinutes();
    var S=date.getSeconds();
    var m=date.getMilliseconds();
    var currentdate=[];
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    if (H >= 0 && H <= 9) {
        H = "0" + H;
    }
    if (M >= 0 && M <= 9) {
        M = "0" + M;
    }
    if (S >= 0 && S <= 9) {
        S = "0" + S;
    }
    if (m >= 0 && m <= 9) {
        m = "00" + m;
    }
    if (m >= 10 && m <= 99) {
        m = "0" + m;
    }
    var date1 =year+"-"+month+"-"+strDate+" "+H+":"+M+":"+S;
    var date2 = year+month+strDate+H+M+S+m;
    currentdate.push(date1,date2);
    return currentdate;
}
//发送手机验证码
$("#send_code i,#send_code input").click(function () {
    $("#send_code ul").toggle();
});
$("#send_code ul li").click(function () {
    $("#send_code input").val($(this).html());
    $("#send_code input").attr("data-type",$(this).attr("data-type"));
    $("#send_code ul").hide();
});
$("#sendAuthcode").click(function () {
    var param={};
    param['corp_code']=sessionStorage.getItem("corp_code");
    param['vip_id']=sessionStorage.getItem("id");
    param['phone']=$("#vip_phone").html();
    param['type']= $("#send_code input").attr("data-type");
    param["vip_name"]=$("#vip_name").html();
    whir.loading.add("",0.5);
    oc.postRequire("post","/vip/sendAuthcode","0",param,function (data) {
        if(data.code=="0"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "发送成功"
            });
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: data.message
            });
        }
        whir.loading.remove();
    })
});
$("#vip_sex_select li").click(function () {
    $("#USER_SEX").val($(this).html());
    $("#vip_sex_select").hide();
});
$("#editX,#editEnter").click(function () {
    $("#editTk").hide();
    whir.loading.remove();
});
