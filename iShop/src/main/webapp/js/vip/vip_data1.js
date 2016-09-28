$(function(){
    getVipInfo();
});
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
        var conSumData=vipData.list
        $("#total_amount_Y").html(conSumData.total_amount_Y);
        $("#consume_times_Y").html(conSumData.consume_times_Y);
        $("#total_amount").html(conSumData.total_amount);
        $("#consume_times").html(conSumData.consume_times);
        $("#dormant_time").html(conSumData.dormant_time);
        $("#last_date").html(conSumData.last_date);
        $("#remark_value").html(vipData.remark);
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
                selectValue=selectValue.split(",");
                var ul="<ul class='expand_selection'>";
                for(var b=0;b<selectValue.length;b++){
                    ul+="<li>"+selectValue[b]+"</li>";
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
        fuzhi(vipDataList);
        getVipPoints(vipDataList.corp_code);
        showOption();
        getoselectvalue();
        upLoadAlbum(vipDataList);
    })
}
function getVipPoints(code,type){
    var parmam_jifen={};
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
            jifenContent(listData,pointsData);
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
            var listData=JSON.parse(pointsData.list);//积分list
            jifenContent(listData,pointsData);
            xiaofeiContent(consumnData,consumnlistData);
        }
    });
}
function jifenContent(listData,pointsData){
    $("#points_total").html(pointsData.points);
    $("#vip_card").html(pointsData.vip_card_type);
    $("#vip_card_num").html(pointsData.cardno);
    $("#vip_name1").html(pointsData.vip_name);
    var listHtml="";
    var listHtmlAll="";
    for(var i=0;i<listData.length;i++){
        var a=i+1;
        if(i<10){
            listHtml+='<tr>'
                +'<td>'+listData[i].points+'</td>'
                +'<td>'+listData[i].date+'<i class="icon-ishop_8-03 style"></i></td>'
                +'</tr>'
        }
        listHtmlAll+='<tr>'
            +'<td>'
            +a+'</td>'
            +'<td>'+listData[i].points+'</td>'
            +'<td>'+listData[i].date+'</td>'
            +'</tr>'
    }
    if(listHtml.length!==0){
        $("#points tbody").html(listHtml);
    }else {
        listHtml='<span>暂无数据</span>'
        $("#points tbody").html(listHtml);
    }
    $("#points_all tbody").html(listHtmlAll)
}
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
                    +'<td>'+n+'</td>'
                    +'<td><img src="'+consumnlistData[j].goods_img+'" /></td>'
                    +'<td class="product_name"><span>'+consumnlistData[j].goods_name+'</span></td>'
                    +'<td class="product_name">'+consumnlistData[j].goods_id+'</td>'
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
            +'<div class="list_head"><span class="black_font">日期:</span> '+date+' <ul><li><em>￥</em><span class="consume_total">'+total_money+'</span></li><li>'+discount+'折</li><li>'+tr+'件商品</li></ul></div>'
            +'<div class="list_head"><span class="black_font">订单号:</span> '+unqiuearr[i]+' <ul><span class="black_font">导购:</span> '+consumnlistData[i].user_name+'</ul></div>'
            +'</div>'
            +'<hr/>'
            +'<table class="list_table">'
            +'<thead>'
            +'<tr>'
            +'<th>序号</th>'
            +'<th>商品图片</th>'
            +'<th class="product_name">商品名称</th>'
            +'<th class="product_name">商品编号</th>'
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
    // for(var i=0;i<consumnlistData.length;i++){
    //     var TR="";
    //     var orderdata=consumnlistData[i].order;
    //     consumnHtml+='<tr>'
    //         +'<td >'+consumnlistData[i].buy_time+'</td>'
    //         +'<td>'+consumnlistData[i].order_count+'</td>'
    //         +'<td>'+consumnlistData[i].order_discount+'</td>'
    //         +'<td>'+consumnlistData[i].order_total+'<i class="icon-ishop_8-03 style"></i></td>'
    //         +'</tr>';
    //     for(var a=0;a<orderdata.length;a++){
    //         var n=a+1;
    //         TR+='<tr>'
    //             +'<td>'+n+'</td>'
    //             +'<td><img src="'+orderdata[a].goods_img+'" /></td>'
    //             +'<td class="product_name"><span>'+orderdata[a].goods_name+'</span></td>'
    //             +'<td class="product_name">'+orderdata[a].goods_id+'</td>'
    //             +'<td>'+orderdata[a].goods_num+'</td>'
    //             +'<td class="money">￥'+orderdata[a].goods_price+'</td>'
    //             +'</tr>'
    //     }
    //     consumnHtmlall+='<div class="record_list">'
    //         +'<div class="order_list">'
    //         +'<div class="list_head"><span class="black_font">日期:</span> '+consumnlistData[i].buy_time+' <ul><li><em>￥</em><span class="consume_total">'+consumnlistData[i].order_total+'</span></li><li>'+consumnlistData[i].order_discount+'折</li><li>'+consumnlistData[i].order_count+'件商品</li></ul></div>'
    //         +'<div class="list_head"><span class="black_font">订单号:</span> '+consumnlistData[i].order_no+' <ul><span class="black_font">导购:</span> '+consumnlistData[i].emp_name+'</ul></div>'
    //         +'</div>'
    //         +'<hr/>'
    //         +'<table class="list_table">'
    //         +'<thead>'
    //         +'<tr>'
    //         +'<th>序号</th>'
    //         +'<th>商品图片</th>'
    //         +'<th class="product_name">商品名称</th>'
    //         +'<th class="product_name">商品编号</th>'
    //         +'<th>件数</th>'
    //         +'<th>价格</th>'
    //         +'</tr>'
    //         +'</thead>'
    //         +'<tbody>'
    //         +TR
    //         +'</tbody>'
    //         +'</table>'
    //         +'</div>'
    // }
    if(consumnHtml.length!==0){
        $("#consum tbody").html(consumnHtml);
    }else {
        $("#consum tbody").html('<span>暂无数据</span>');
    }
    $("#consum_all").html(consumnHtmlall)
}
function fuzhi(data){
    var sex=data.sex;
    if(sex=="male"){
        $("#vip_name").next().addClass("icon-ishop_9-03");
        $("#USER_SEX").val("男");
    }else{
        $("#vip_name").next().addClass("icon-ishop_9-02");
        $("#USER_SEX").val("女");
    }
    $("#vip_name").html(data.vip_name);
    $("#vip_name_edit").val(data.vip_name);
    $("#vip_card_no").html(data.cardno);
    $("#vip_card_no_edit").val(data.cardno);
    $("#vip_card_type").html(data.vip_card_type);
    $("#vip_card_type_edit").val(data.vip_card_type);
    $("#vip_phone").html(data.vip_phone);
    $("#vip_phone_edit").val(data.vip_phone);
    $("#user_name").html(data.user_name);
    $("#user_name_edit").val(data.user_name);
    $("#vip_total_amount").html(data.total_amount+'&nbsp元');
    $("#join_date").html(data.join_date);
    $("#vip_birthday").html(data.vip_birthday);
    $("#vip_birthday_edit").val(data.vip_birthday);
    $("#corp_code").html(data.corp_code);
    $("#vip_consume_times").html(data.consume_times+'&nbsp次');
    if(data.dormant_time=='无'){
        $("#vip_dormant_time").html(data.dormant_time);
    }else{
        $("#vip_dormant_time").html(data.dormant_time+'&nbsp天');
    }
    if(data.vip_avatar){
        $(".person-img").css('background','url('+data.vip_avatar+')')
    }else{
        $(".person-img").css('backgroundImage','url(../img/head.png)')
    }
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
function showOption(){
    $(".drop_down input").click(function (){
        var ul=$(this).next(".expand_selection");
        if(ul.css("display")=="none"){
            ul.show();
        }else{
            ul.hide();
        }
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
        $("#delete").attr("data-id","");
        return false;
    });

    $("#delete").click(function(){//确认删除相册
        $("#tk").hide();
        var id=$(this).attr("data-id");
        var param={};
        param["id"]=id;
        oc.postRequire("post","/vipAlbum/delete","",param,function(data){
            if(data.message="success"){
                $("#"+id).parent().remove();
                frame();
                $('.frame').html('删除成功');
            }else{
                frame();
                $('.frame').html('删除成功');
            }
        })
    });


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
    postInfo('expand',param);
});
$("#remark_keep").click(function(){
    postInfo('remark',$("#remark_value").val())
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
            $('.frame').html('保存成功');
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
