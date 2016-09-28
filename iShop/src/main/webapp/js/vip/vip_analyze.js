var oc = new ObjectControl();
var page=1;
var jump=1;//标签跳转
var jump_s="";//消费记录标签
var query_type='';//创建活跃会员的标签请求
var month_type='';//会员生日月份类型
var count='';
/**********************左侧数据**************************************************************************************/
//获取区域
function GetArea(){
    var searchValue=$('#select_analyze input').val();
    var param={};
    param['pageNumber']=page;
    param['pageSize']="7";
    param['searchValue']=searchValue;
    oc.postRequire("post","/area/findAreaByCorpCode","",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);//��ȡmessagejson�����DOM����
            var output=JSON.parse(message.list);
            var ul='';
            var first_area='';
            var first_area_code='';
            var output_list=output.list;
            output_list.length<7&&($('#select_analyze s').attr('style','display:none'));
            first_area=output_list[0].area_name;
            first_area_code=output_list[0].area_code;//区域号
            //设置本地缓存企业编号
            localStorage.setItem('corp_code',output_list[0].corp_code?output_list[0].corp_code:'');
            for(var i= 0;i<output_list.length;i++){
                ul+="<li data_area='"+output_list[i].area_code+"'>"+output_list[i].area_name+"</li>";
            }
            $('#side_analyze ul li:nth-child(2) s').html(first_area);
            $('#side_analyze ul li:nth-child(2) s').attr('data_area',first_area_code);
            var area_code=output_list[0].area_code;
            // console.log(area_code);
            // localStorage.setItem('area_code',area_code);
            //清除内容店铺下拉列表
            $('#select_analyze_shop ul').html('');
            getStore(area_code);
            $('#select_analyze ul').append(ul);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    });
}
//获取店铺
function getStore(a){
    var searchValue=$('#select_analyze_shop input').val();
    var area_code=a;
    var param={};
    param['pageNumber']=page;
    param['pageSize']=7;
    param['searchValue']=searchValue;
    param["area_code"]=area_code;
    oc.postRequire("post","/shop/findByAreaCode","",param,function(data){
        var ul='';
        var message=JSON.parse(data.message);
        var message=JSON.parse(data.message);
        var first_store_name='';
        var first_store_code='';
        var message=JSON.parse(data.message);
        var message=JSON.parse(data.message);
        var output=JSON.parse(message.list);
        var output_list=output.list;
        if(output_list.length<7){
            $('#select_analyze_shop s').attr('style','display:none')
        }else{
            $('#select_analyze_shop s').attr('style','display:block')
        }
        // output_list.length<7&&($('#select_analyze_shop s').attr('style','display:none'));
        first_store_name=output_list[0].store_name;
        first_store_code=output_list[0].store_code;
        for(var i= 0;i<output_list.length;i++){
            // ul+="<li data_store='"+output_list[i].store_code+"'>"+output_list[i].store_name+"</li>";
            output_list[i].store_name=='全部'?'':ul+="<li data_store='"+output_list[i].store_code+"'>"+output_list[i].store_name+"</li>";
        }
        $('#side_analyze ul li:nth-child(3) s').html( first_store_name);
        $('#side_analyze ul li:nth-child(3) s').attr('data_store',first_store_code);
        $('#select_analyze_shop ul').append(ul);
        // newVipGet();//获取新会员
        // sleepVipGet();//获取活跃会员
       // un_push?un_push=0:brithVipGet();//正常调用当为加载更多时不调用
    });
    page=1;
}
//点击li填充s中的数据显示
function showNameClick(e){
    var e= e.target;
    var d=$(e).parent().parent().parent();
    console.log($(d).attr('id'));
    if($(d).attr('id')=='select_analyze'){
        var area_code=$(e).attr('data_area');
        $('#side_analyze ul li:nth-child(2) s').html($(e).html());
        $('#side_analyze ul li:nth-child(2) s').attr('data_area',area_code);
        //清除内容店铺下拉列表
        $('#select_analyze_shop ul').html('');
        getStore(area_code);
        $('#select_analyze').toggle();
        $($('.vip_nav_bar li[class="liactive"]')[0]).trigger('click')
    }else{
        var store_code=$(e).attr('data_store');
        $('#side_analyze ul li:nth-child(3) s').html($(e).html());
        $('#side_analyze ul li:nth-child(3) s').attr('data_store',store_code);
        $('#select_analyze_shop').toggle();
        //添加店铺时，找到显示的DIV发起请求
        $($('.vip_nav_bar li[class="liactive"]')[0]).trigger('click')
    }
}
//取消下拉框
$(document).on('click',function(e){
    if(!(e.target==$($('#side_analyze>ul')[0]).find('li:nth-child(2)')
    || e.target==$('#select_analyze')
    || e.target==$('#select_analyze div')[0]
    || e.target==$('#select_analyze div b')[0]
    || e.target==$('#select_analyze div b input')[0]
    || e.target==$('#select_analyze div b span')[0]
    || e.target==$('#select_analyze div ul')[0]
    || e.target==$('#select_analyze div ul li')[0]
    || e.target==$('#select_analyze div s')[0]))$('#select_analyze').hide();
    if(!(e.target==$($('#side_analyze_shop>ul')[0]).find('li:nth-child(2)')
        || e.target==$('#select_analyze_shop')
        || e.target==$('#select_analyze_shop div')[0]
        || e.target==$('#select_analyze_shop div b')[0]
        || e.target==$('#select_analyze_shop div b input')[0]
        || e.target==$('#select_analyze_shop div b span')[0]
        || e.target==$('#select_analyze_shop div ul')[0]
        || e.target==$('#select_analyze_shop div ul li')[0]
        || e.target==$('#select_analyze_shop div s')[0]))$('#select_analyze_shop').hide();
});
//加载更多
function getMore(e){
    var e= e.target;
    page+=1;
    var area_code=$('#side_analyze ul li:nth-child(2) s').attr('data_area');
    getStore(area_code);
}
//搜索
function searchValue(e){
    //page初始化
    page=1;
    //进入搜索清空内容
    $(e.target).parent().next().html('');
    //清楚加载更多
    $(e.target).parent().next().next().attr('style','display:block');
    //获取店铺列表的value值e
    var searchValue=$(e.target).prev().val();
    //获得其父级元素的id熟属性
    var parent=$(e.target).parent().parent().parent();
    //判断是区域搜索还是店铺搜索
      if($(parent).attr('id')=='select_analyze'){
          GetArea();
      }else{
          getStore(localStorage.getItem('area_code'));
      }
}
//页面加载前加载区域
GetArea();
//绑定事件下拉事件
$('#side_analyze>ul:nth-child(1) li:gt(0)').click(function(){
        var event=window.event||arguments[0];
        if(event.stopPropagation){
            event.stopPropagation();
        }else{
            event.cancelBubble=true;
        }
        if($(this).find('b').html()=='区域'){
            $('#select_analyze').toggle();
            $('#select_analyze_shop').hide();
        }else{
            $('#select_analyze_shop').toggle()
        }
    });
/******************************表格分析数据*************************************************************************/
// 列表点击效果
$(".vip_nav_bar li:nth-child(1)").click(function () {
    $('.birthVip').show();
    $(".newVip").hide();
    $(".activeVip").hide();
    $(".rank").hide();
    $("#page_row").val("10行/页");
    brithVipGet();
    jump=1;
    //点击时，将第一个按钮设置class为btn_bg
    $($(".date_btn span")[0]).css({"color":"#fff","background":"#6cc1c8"});
    //取消其他的class;
    var lis= $($(".date_btn span")[0]).parent().nextAll();
    for(var i=0;i<lis.length;i++){
        console.log($(lis[i]).find('span'));
        $(lis[i]).find('span').css({"color":"","background":""});
    }
})
$(".vip_nav_bar li:nth-child(2)").click(function () {
    $('.birthVip').hide();
    $(".newVip").show();
    $(".activeVip").hide();
    $(".rank").hide();
    $("#page_row").val("10行/页");
    newVipGet();
    jump=2;
    $($(".new_btn span")[0]).css({"color":"#fff","background":"#6cc1c8"});
    var lis= $($(".new_btn span")[0]).parent().nextAll();
    for(var i=0;i<lis.length;i++){
        $(lis[i]).find('span').css({"color":"","background":""});
    }
})
$(".vip_nav_bar li:nth-child(3)").click(function () {
    $('.birthVip').hide();
    $(".newVip").hide();
    $(".activeVip").show();
    $(".rank").hide();
    $("#page_row").val("10行/页");
    sleepVipGet();
    jump=3;
    //点击时，将第一个按钮设置class为btn_bg
    $($(".month_btn span")[0]).css({"color":"#fff","background":"#6cc1c8"});;
    //取消其他的class;
    var lis= $($(".month_btn span")[0]).parent().nextAll();
    for(var i=0;i<lis.length;i++){
        console.log($(lis[i]).find('span'));
        $(lis[i]).find('span').css({"color":"","background":""});
    }
})
$(".vip_nav_bar li:nth-child(4)").click(function () {
    $('.birthVip').hide();
    $(".newVip").hide();
    $(".activeVip").hide();
    $(".rank").show();
    $("#page_row").val("10行/页");
    consumeVipGet();
    jump=4;
    //点击时，将第一个按钮设置class为btn_bg
    $($(".rank .month_btn span")[0]).css({"color":"#fff","background":"#6cc1c8"});;
    //取消其他的class;
    var lis= $($(".rank .month_btn span")[0]).parent().nextAll();
    for(var i=0;i<lis.length;i++){
        console.log($(lis[i]).find('span'));
        $(lis[i]).find('span').css({"color":"","background":""});
    }
})
$(".vip_nav_bar li").click(function () {
    // $(this).css("border-bottom","2px solid #6cc1c8");
    // $(this).siblings().css("border-bottom","");
    $(this).addClass("liactive");
    $(this).siblings().removeClass("liactive");
})
$(".date_btn span").click(function () {
    $(this).css({"color":"#fff","background":"#6cc1c8"});
    $(this).parent("li").siblings().children("span").css({"color":"","background":""});
})
$(".new_btn span").click(function () {
    $(this).css({"color":"#fff","background":"#6cc1c8"});
    $(this).parent("li").siblings().children("span").css({"color":"","background":""});
})
$(".month_btn span").click(function () {
    $(this).css({"color":"#fff","background":"#6cc1c8"});
    $(this).parent("li").siblings().children("span").css({"color":"","background":""});
})
$(".more_data").click(function () {
    console.log($('#table_analyze .foot .foot-num')[0]);
    vipTable_lg();
})
//加载更多页面放大
function vipTable_lg() {
    $("#table_analyze").addClass("vip_table_lg");
    $("#chart_analyze").hide();
    $(".more_data").hide();
    $("#side_analyze").hide();
    $("html").css("background","#e8e8e8");
    $("#table_analyze").css("background","#e8e8e8");
    $(".vip_table_lg .icon-ishop_8-03").hide();
    $(".foot").show();
}
//点击标题返回
$("#vipAnalyze_return").click(function () {
    $("#table_analyze").removeClass("vip_table_lg");
    $("#chart_analyze").show();
    $(".more_data").show();
    $("#side_analyze").show();
    $("#table_analyze").css("background","#fff");
    $(".foot").hide();
    $(".newVip .vip_table tbody tr").each(function(i){
        if(i>9){
            $(this).hide();
        }
    });
    $(".birthVip .vip_table tbody tr").each(function(i){
        if(i>9){
            $(this).hide();
        }
    });
    $(".activeVip .vip_table tbody tr").each(function(i){
        if(i>9){
            $(this).hide();
        }
    });
    $(".rank .vip_table tbody tr").each(function(i){
        if(i>9){
            $(this).hide();
        }
    });
});
/******************生日会员****************************/
function brithVipGet() {
    whir.loading.add("",0.5);//加载等待框
    var type='birth';
    $('.birthVip .vip_table tbody').empty();
    var param={};
    var month_type=arguments[2]?arguments[2]:'today';
    var pageSize=arguments[1]?arguments[1]:10;
    var pageIndex=arguments[0]?arguments[0]:page;
    param['pageNumber']=pageIndex;
    param['pageSize']=pageSize;
    param['query_type']=month_type;
    param['store_code']=$($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    param['corp_code']=localStorage.getItem('corp_code');
    param["area_code"]= $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    oc.postRequire("post","/vipAnalysis/vipBirth","",param,function(data) {
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            count=msg.pages;
            console.log(count);
            var pageIndex=msg.pageNum;
            msg=msg.birthday_vip_list;
            if(msg.length) {
                for (var i = 0; i < msg.length; i++) {
                    if (pageIndex >= 2) {
                        var a = i + 1 + (pageIndex - 1) * pageSize;
                    } else {
                        var a = i + 1;
                    }
                    $(".birthVip tbody").append('<tr><td>'
                        + a
                        + '</td><td>'
                        + msg[i].vip_name
                        + '</td><td>'
                        + msg[i].vip_card_type
                        + '</td><td>'
                        + msg[i].amount
                        + '</td><td>'
                        + msg[i].trade_count
                        + '</td><td>'
                        + msg[i].vip_birthday
                        + '</td></tr>');
                }
                $(".birthVip .vip_table tbody tr").click(function () {
                    vipTable_lg();
                })
            }
        }else if(data.code=="-1"){
            console.log(data.message);
        }
        whir.loading.remove();//移除加载框
        //调用生成页码
        setPage($('#table_analyze .foot .foot-num')[0],count,pageIndex,pageSize,type,month_type)
    });
}
function birthVipGet_sub(ali) {
    var ali=ali;//当前对象
    switch($($(ali).html()).html()){
        case '今日': month_type='today';brithVipGet('','',month_type);break;
        case '本月': month_type='current_month';brithVipGet('','',month_type);break;
        case '次月': month_type='next_month';brithVipGet('','',month_type);break;
    }
}
/******************新VIP会员***************************/
//新VIP模块数据请求加载
function newVipGet(){
    whir.loading.add("",0.5);//加载等待框
    var type='new';
    $('.newVip .vip_table tbody').empty();
    var param={};
    var month_type=arguments[2]?arguments[2]:'daily';
    var pageSize=arguments[1]?arguments[1]:10;
    var pageIndex=arguments[0]?arguments[0]:page;
    param['query_type']=month_type;
    param['pageNumber']=pageIndex;
    param['pageSize']=pageSize;
    param['store_code']=$($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    param['corp_code']=localStorage.getItem('corp_code');
    param["area_code"]= $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    oc.postRequire("post","/vipAnalysis/vipNew","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
             count=msg.pages;
            var pageIndex=msg.pageNum;
            msg=msg.new_vip_list;
            if(msg.length){
                for(var i=0;i<msg.length;i++){
                    if(pageIndex>=2){
                        var a=i+1+(pageIndex-1)*pageSize;
                    }else{
                        var a=i+1;
                    }
                    $(".newVip tbody").append('<tr><td>'
                        + a
                        +'</td><td>'
                        + msg[i].vip_name
                        +'</td><td>'
                        + msg[i].vip_card_type
                        +'</td><td>'
                        + msg[i].join_date
                        +'</td><td>'
                        + msg[i].vip_birthday
                        +'</td></tr>');
                }
                $(".vip_table tbody tr").click(function () {
                    vipTable_lg();
                })
            }
        }else if(data.code=="-1"){
            console.log(data.message);
            // whir.loading.remove();//移除加载框
        }
        //调用生成页码
        setPage($('#table_analyze .foot .foot-num')[0],count,pageIndex,pageSize,type,month_type)
        whir.loading.remove();//移除加载框
        //如果页面没有数据，设置提示信息
        $('.newVip .vip_table tbody').html()?'':$('.newVip .vip_table tbody').append('<span class="no_data'+'">暂无数据</span>');
    });
}
function newVipGet_sub(ali) {
    var ali=ali;//当前对象
    switch($($(ali).html()).html()){
        case '今日': month_type='daily';newVipGet('','',month_type);break;
        case '本周': month_type='weekly';newVipGet('','',month_type);break;
        case '本月': month_type='monthly';newVipGet('','',month_type);break;
    }
}
/******************活跃会员*****************************/
//获取活跃用户
function sleepVipGet() {
    whir.loading.add("",0.5);//加载等待框
    var type='sleep';
    $('.activeVip .vip_table tbody').empty();
    var param={};
    var query_type=arguments[2]?arguments[2]:0;;
    var pageSize=arguments[1]?arguments[1]:10;
    var pageIndex=arguments[0]?arguments[0]:page;
    param['pageNumber']=pageIndex;
    param['pageSize']=pageSize;
    param['query_type']=query_type;
    param['store_code']=$($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    param['corp_code']=localStorage.getItem('corp_code');
    param["area_code"]= $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    oc.postRequire("post","/vipAnalysis/vipSleep","",param,function(data) {
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            count=msg.pages;
            var pageIndex=msg.pageNum;
            msg=msg.sleep_vip_list;
            if(msg.length){
                for(var i=0;i<msg.length;i++){
                    if(pageIndex>=2){
                        var a=i+1+(pageIndex-1)*pageSize;
                    }else{
                        var a=i+1;
                    }
                    $(".activeVip tbody").append('<tr><td>'
                        + a
                        +'</td><td>'
                        + msg[i].vip_name
                        +'</td><td>'
                        + msg[i].vip_card_type
                        +'</td><td>'
                        + msg[i].amount
                        +'</td><td>'
                        + msg[i].consume_times
                        +'</td><td>'
                        + msg[i].recently_consume_date
                        +'</td></tr>');
                }
                $(".activeVip .vip_table tbody tr").click(function () {
                    vipTable_lg();
                })
            }
        }else if(data.code=="-1"){
            console.log(data.message);

        }
        whir.loading.remove();//移除加载框
        //调用生成页码
        setPage($('#table_analyze .foot .foot-num')[0],count,pageIndex,pageSize,type,query_type)
    });
}
function sleepVipGet_sub(ali) {
    var ali=ali;//当前对象
    switch($($(ali).html()).html()){
        case '活跃会员1.26%': query_type=0;sleepVipGet('','',query_type);break;
        case '3个月23.95%': query_type=1;sleepVipGet('','',query_type);break;
        case '6个月31.09%': query_type=2;sleepVipGet('','',query_type);break;
        case '9个月26.05%': query_type=3;sleepVipGet('','',query_type);break;
        case '12个月20.01%': query_type=4;sleepVipGet('','',query_type);break;
    }
}
/*************消费排行****************/
function consumeVipGet() {
    whir.loading.add("",0.5);//加载等待框
    var type='consume';
    $('.rank .vip_table tbody').empty();
    $('.rank .vip_table thead').empty();
    var param={};
    var query_type=arguments[2]?arguments[2]:"recent";
    var pageSize=arguments[1]?arguments[1]:10;
    var pageIndex=arguments[0]?arguments[0]:page;
    param['pageNumber']=pageIndex;
    param['pageSize']=pageSize;
    param['query_type']=query_type;
    param['store_code']=$($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    param['corp_code']=localStorage.getItem('corp_code');
    param["area_code"]= $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    oc.postRequire("post","/vipAnalysis/vipConsume","",param,function(data) {
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            count=msg.pages;
            var pageIndex=msg.pageNum;
            msg=msg.vip_consume_recently_list;
            if(msg.length){
                $(".rank thead").append('<tr>'
                    + '<th>序号</th>'
                    + '<th>会员</th>'
                    + '<th>会员等级</th>'
                    + '<th>消费总额</th>'
                    + '<th>最近消费日期</th></tr>')
                for(var i=0;i<msg.length;i++){
                    if(pageIndex>=2){
                        var a=i+1+(pageIndex-1)*pageSize;
                    }else{
                        var a=i+1;
                    }
                    $(".rank tbody").append('<tr><td>'
                        + a
                        +'</td><td>'
                        + msg[i].vip_name
                        +'</td><td>'
                        + msg[i].vip_type
                        +'</td><td>'
                        + msg[i].amount
                        +'</td><td>'
                        + msg[i].consume_time
                        +'</td></tr>');
                }
                $(".rank .vip_table tbody tr").click(function () {
                    vipTable_lg();
                })
            }
        }else if(data.code=="-1"){
            console.log(data.message);
            // whir.loading.remove();//移除加载框
        }
        //调用生成页码
        setPage($('#table_analyze .foot .foot-num')[0],count,pageIndex,pageSize,type,query_type)
        whir.loading.remove();//移除加载框
    });
}
function consumeVipGetre() {
    whir.loading.add("",0.5);//加载等待框
    var type='consume';
    $('.rank .vip_table tbody').empty();
    $('.rank .vip_table thead').empty();
    var param={};
    var query_type=arguments[2]?arguments[2]:"recent";
    var pageSize=arguments[1]?arguments[1]:10;
    var pageIndex=arguments[0]?arguments[0]:page;
    param['pageNumber']=pageIndex;
    param['pageSize']=pageSize;
    param['query_type']=query_type;
    param['store_code']=$($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    param['corp_code']=localStorage.getItem('corp_code');
    param["area_code"]= $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    oc.postRequire("post","/vipAnalysis/vipConsume","",param,function(data) {
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            console.log(msg);
            count=msg.pages;
            var pageIndex=msg.pageNum;
            msg=msg.vip_cost_freq_list;
            if(msg.length>0){
                $(".rank thead").append('<tr>'
                    + '<th>序号</th>'
                    + '<th>会员</th>'
                    + '<th>会员等级</th>'
                    + '<th>平均消费</th>'
                    + '<th>月消费次数</th></tr>')
                for(var i=0;i<msg.length;i++){
                    if(pageIndex>=2){
                        var a=i+1+(pageIndex-1)*pageSize;
                    }else{
                        var a=i+1;
                    }
                    $(".rank tbody").append('<tr><td>'
                        + a
                        +'</td><td>'
                        + msg[i].vip_name
                        +'</td><td>'
                        + msg[i].vip_type
                        +'</td><td>'
                        + msg[i].avg_amount
                        +'</td><td>'
                        + msg[i].consume_frequently
                        +'</td></tr>');
                }
                $(".rank .vip_table tbody tr").click(function () {
                    vipTable_lg();
                })
            }
        }else if(data.code=="-1"){
            console.log(data.message);
            // whir.loading.remove();//移除加载框
        }
        //调用生成页码
        setPage($('#table_analyze .foot .foot-num')[0],count,pageIndex,pageSize,type,query_type)
        whir.loading.remove();//移除加载框
    });
}
function consumeVipGetam() {
    whir.loading.add("",0.5);//加载等待框
    var type='consume';
    $('.rank .vip_table tbody').empty();
    $('.rank .vip_table thead').empty();
    var param={};
    var query_type=arguments[2]?arguments[2]:"recent";
    var pageSize=arguments[1]?arguments[1]:10;
    var pageIndex=arguments[0]?arguments[0]:page;
    param['pageNumber']=pageIndex;
    param['pageSize']=pageSize;
    param['query_type']=query_type;
    param['store_code']=$($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    param['corp_code']=localStorage.getItem('corp_code');
    param["area_code"]= $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    oc.postRequire("post","/vipAnalysis/vipConsume","",param,function(data) {
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            count=msg.pages;
            var pageIndex=msg.pageNum;
            msg=msg.amount_list;
            if(msg.length>0){
                $(".rank thead").append('<tr>'
                    + '<th>序号</th>'
                    + '<th>会员</th>'
                    + '<th>会员等级</th>'
                    + '<th>消费总额</th></tr>')
                for(var i=0;i<msg.length;i++){
                    if(pageIndex>=2){
                        var a=i+1+(pageIndex-1)*pageSize;
                    }else{
                        var a=i+1;
                    }
                    $(".rank tbody").append('<tr><td>'
                        + a
                        +'</td><td>'
                        + msg[i].vip_name
                        +'</td><td>'
                        + msg[i].vip_card_type
                        +'</td><td>'
                        + msg[i].amount
                        +'</td></tr>');
                }
                $(".rank .vip_table tbody tr").click(function () {
                    vipTable_lg();
                })
            }
        }else if(data.code=="-1"){
            console.log(data.message);
            // whir.loading.remove();//移除加载框
        }
        //调用生成页码
        setPage($('#table_analyze .foot .foot-num')[0],count,pageIndex,pageSize,type,query_type)
        whir.loading.remove();//移除加载框
    });
}
function consumeVipGet_sub(ali) {
    var ali=ali;//当前对象
    switch($($(ali).html()).html()){
        case '最近消费': query_type="recent";consumeVipGet('','',query_type);break;
        case '消费频率': query_type="freq";consumeVipGetre('','',query_type);break;
        case '本月消费': query_type="month";consumeVipGetam('','',query_type);break;
        case '前三月消费': query_type="three_month";consumeVipGetam('','',query_type);break;
        case '历史总额': query_type="history";consumeVipGetam('','',query_type);break;
    }
}
$('.rank .month_btn li').click(function () {
    jump_s=$(this).index();
    console.log(jump_s);
    consumeVipGet_sub(this);
    $("#page_row").val("10行/页");
});
/*******************共用方法***************************/
//生成分页
function setPage(container, count, pageindex,pageSize,type,query_type) {
    var type=type;
    var query_type=query_type;
    var container = container;//节点
    var count = count;//5
    var pageindex = pageindex;//1
    var pageSize=pageSize;//10
    var a = [];
    //总页数少于10 全部显示,大于10 显示前3 后3 中间3 其余....
    if (pageindex == 1) {
        a[a.length] = "<li><span class=\"icon-ishop_4-01 unclick\"></span></li>";
    } else {
        a[a.length] = "<li><span class=\"icon-ishop_4-01\"></span></li>";
    }
    function setPageList() {
        if (pageindex == i) {
            a[a.length] = "<li><span class=\"p-bg\">" + i + "</span></li>";
        } else {
            a[a.length] = "<li><span>" + i + "</span></li>";
        }
    }
    //总页数小于10
    if (count <= 10) {
        for (var i = 1; i <= count; i++) {
            setPageList();
        }
    }
    //总页数大于10页
    else {
        if (pageindex <= 4) {
            for (var i = 1; i <= 5; i++) {
                setPageList();
            }
            a[a.length] = "...<li><span>" + count + "</span></li>";
        }else if (pageindex >= count - 3) {
            a[a.length] = "<li><span>1</span></li>...";
            for (var i = count - 4; i <= count; i++) {
                setPageList();
            }
        }
        else { //当前页在中间部分
            a[a.length] = "<li><span>1</span></li>...";
            for (var i = pageindex - 2; i <= pageindex + 2; i++) {
                setPageList();
            }
            a[a.length] = "...<li><span>" + count + "</span></li>";
        }
    }
    if (pageindex == count) {
        a[a.length] = "<li><span class=\"icon-ishop_4-02 unclick\"></span></li>";
    }else{
        a[a.length] = "<li><span class=\"icon-ishop_4-02\"></span></li>";
    }
    container.innerHTML = a.join("");
    var pageClick = function() {
        var oAlink = container.getElementsByTagName("span");
        inx = pageindex; //初始的页码
        $("#input-txt").val(inx);
        $(".foot-sum .zy").html("共 "+count+"页");
        oAlink[0].onclick = function() { //点击上一页
            if (inx == 1) {
                return false;
            }
            inx--;
            dian(inx,pageSize,type,query_type);
            // setPage(container, count, inx,pageSize,funcCode,value,filtrate);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function() {
                inx = parseInt(this.innerHTML);
                dian(inx,pageSize,type,query_type);
                // setPage(container, count, inx,pageSize,funcCode,value,filtrate);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function() { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx,pageSize,type,query_type);
            // setPage(container, count, inx,pageSize,funcCode,value,filtrate);
            return false;
        }
    }()
}
//页码仿写select
$(function(){
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
                var a=1;
                jump==2&&(newVipGet(a,pageSize,month_type));
                jump==3&&(sleepVipGet(a,pageSize,query_type));
                jump==1&&(brithVipGet(a,pageSize,month_type));
                jump==4&&jump_s==0&&(consumeVipGet(a,pageSize,month_type));
                jump==4&&jump_s==1&&(consumeVipGetre(a,pageSize,query_type));
                jump==4&&jump_s>1&&(consumeVipGetam(a,pageSize,query_type));
                // if(value==""&&filtrate==""){
                //     inx=1;
                //     GET(inx,pageSize);
                // }else if(value!==""){
                //     inx=1;
                //     param["pageNumber"]=inx;
                //     param["pageSize"]=pageSize;
                //     POST(inx,pageSize);
                // }else if(filtrate!==""){
                //     inx=1;
                //     _param["pageNumber"]=inx;
                //     _param["pageSize"]=pageSize;
                //     filtrates(inx,pageSize);
                // }
                $("#page_row").val($(this).html());
                hideLi();
            });

        });
        $("#page_row").blur(function(){
            setTimeout(hideLi,200);
        });
    }
);
//页码显示
function showLi(){
    $("#liebiao").show();
}
//页码隐藏
function hideLi(){
    $("#liebiao").hide();
}
//页码点击事件
function dian(a,b,c,query_type){
    var a=a;
    var b=b;
    var c=c;//页码请求类型
    var query_type=query_type;
    c=='new'&&(newVipGet(a,b,query_type));
    //当请求类型为sleep时
    //判断query_type的值执行不同的请求
    if(c=='sleep'&&(query_type==0)){
        sleepVipGet(a,b)
    }else if( c=='sleep'&&(query_type) ){
        sleepVipGet(a,b,query_type)
    }
    //当请求类型为birth 判断请求的类型
    if(c=='birth'&&(query_type=='today')){
        brithVipGet(a,b);
    }else if( c=='birth'&&(query_type) ){
        brithVipGet(a,b,query_type)
    }
    //当请求类型为consume 判断请求的类型
    if(c=='consume'&&(query_type=='recent')){
        consumeVipGet(a,b);
    }else if( c=='consume'&&(query_type=="freq") ){
        consumeVipGetre(a,b,query_type);
    }else if( c=='consume'&&(query_type=="month"||query_type=="three_month"||query_type=="history") ){
        consumeVipGetam(a,b,query_type);
    }
}
//指定页面的跳转
$("#input-txt").keydown(function() {
    var event=window.event||arguments[0];
    var inx= this.value.replace(/[^0-9]/g, '');
    var inx=parseInt(inx);
    if (inx > count) {
        inx = count
    };
    if (inx > 0) {
        if (event.keyCode == 13) {
            console.log(month_type,jump);
            console.log(jump_s);
                jump==2&&(newVipGet(inx,'',month_type));
                jump==3&&(sleepVipGet(inx,'',query_type));
                jump==1&&(brithVipGet(inx,'',month_type));
                jump==4&&jump_s==0&&(consumeVipGet(inx,'',month_type));
                jump==4&&jump_s==1&&(consumeVipGetre(inx,'',query_type));
                jump==4&&jump_s>1&&(consumeVipGetam(inx,'',query_type));
            }
        };
})
/***************************图表分析数据***************************************/
//图表
require.config({
    paths: {
        echarts: '../js/dist'
    }
});
require(
    [
        'echarts',
        'echarts/chart/pie',  // 按需加载所需图表，如需动态类型切换功能，别忘了同时加载相应图表
        'echarts/chart/radar',
        'echarts/chart/map',
        'echarts/chart/bar',
        'echarts/chart/line'
    ],
    function (ec) {
        var aa=[
            {value:122,name:"衬衫"},
            {value:310, name:'背心'},
            {value:234, name:'T恤'},
            {value:135, name:'外套'},
            {value:1548, name:'长裙'},
            {value:1548, name:'短裙'},
            {value:1548, name:'连衣裙'},
            {value:1548, name:'裤子'}
        ];
        var myChart = ec.init(document.getElementById('main'));
        var myChart1 = ec.init(document.getElementById('main1'));
        var myChart2 = ec.init(document.getElementById('main2'));
        var myChart3 = ec.init(document.getElementById('main3'));
        var myChart4 = ec.init(document.getElementById('main4'));
        var myChart5 = ec.init(document.getElementById('main5'));
        var myChart6 = ec.init(document.getElementById('main6'));
        var myChart7 = ec.init(document.getElementById('main7'));
        var option = {
            color:['#9AD8DB', '#8BC0C8', '#7BA8B5', '#6C8FA2','#5C778F','#4D5F7C','#444960','#2C3244'] ,
            tooltip : {
                textStyle : {
                    fontSize : '10',
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show:'true',
                orient : 'vertical',
                x : 'left',
                y:'center',
                data:[{
                    name : '衬衫',
                    icon : '2'
                },{
                    name : '背心',
                    icon : '12'
                },{
                    name : '连衣裙',
                    icon : '12'
                },{
                    name : 'T恤',
                    icon : '12'
                },{
                    name : '外套',
                    icon : '12'
                },{
                    name : '长裙',
                    icon : '12'
                },{
                    name : '短裙',
                    icon : '12'
                },{
                    name : '裤子',
                    icon : '12'
                }]
            },
            series : [
                {   name:'消费分类',
                    center:['60%','50%'],
                    type:'pie',
                    radius : ['50%', '60%'],
                    itemStyle : {
                        normal : {
                            label : {
                                show : false
                            },
                            labelLine : {
                                show : false
                            }
                        },
                        emphasis : {
                            label : {
                                show : true,
                                position : 'center',
                                textStyle : {
                                    fontSize : '20',
                                    fontWeight : 'bold'
                                }
                            }
                        }
                    },
                    data:aa
                }
            ]
        };
        var option1 = {
            color:['#A7DADE'],
            axis:{
                areaStyle:{
                    color:['red']
                }
            },
            tooltip : {
                trigger: 'axis'
            },
            polar : [
                {
                    indicator : [
                        {text : '周一', max  : 100},
                        {text : '周二', max  : 100},
                        {text : '周三', max  : 100},
                        {text : '周四', max  : 100},
                        {text : '周五', max  : 100},
                        {text : '周六', max  : 100},
                        {text : '周七', max  : 100},
                        {text : '无数据', max  : 100}
                    ],
                    radius : 100,
                    splitNumber: 8,
                    startAngle: 68,
                    splitArea : {
                        show : true,
                        areaStyle : {
                            color: '#fff'
                        }
                    }
                }
            ],
            series : [
                {
                    symbol:'circle',
                    type: 'radar',
                    itemStyle: {
                        normal: {
                            areaStyle: {
                                type: 'default'
                            },
                            lineStyle:{
                                width:'0'
                            }
                        }
                    },
                    symbolSize:'0',
                    data : [
                        {
                            value : [97, 42, 88, 94, 90, 86,'20','null'],
                            name : '周变'
                        }
                    ]
                }
            ]
        };
        var option2 = {
            color:['#A7DADE'],
            axis:{
                areaStyle:{
                    color:['red']
                }
            },
            tooltip : {
                trigger: 'axis'
            },
            polar : [
                {
                    indicator : [
                        {text : '一月', max  : 100},
                        {text : '二月', max  : 100},
                        {text : '三月', max  : 100},
                        {text : '四月', max  : 100},
                        {text : '五月', max  : 100},
                        {text : '六月', max  : 100},
                        {text : '七月', max  : 100},
                        {text : '八月', max  : 100},
                        {text : '九月', max  : 100},
                        {text : '十月', max  : 100},
                        {text : '十一月', max  : 100},
                        {text : '十二月', max  : 100}
                    ],
                    radius : 100,
                    splitNumber: 8,
                    startAngle: 45,
                    splitArea : {
                        show : true,
                        areaStyle : {
                            color: '#fff'
                        }
                    }
                }
            ],
            series : [
                {
                    symbol:'circle',
                    type: 'radar',
                    itemStyle: {
                        normal: {
                            areaStyle: {
                                type: 'default'
                            },
                            lineStyle:{
                                width:'0'
                            }
                        }
                    },
                    symbolSize:'0',
                    data : [
                        {
                            value : [97, 42, 88, 94, 90, 86,69,66,33,58,44,55,66],
                            name : '月变'
                        }
                    ]
                }
            ]
        };
        var option3 = {
            tooltip : {
                trigger: 'item'
            },
            dataRange: {
                itemWidth:5,
                itemGap:0.2,
                color:['#A7CFD5','#3C95A2'],
                splitNumber:'20',
                orient:'horizontal',
                min: 0,
                max: 2500,
                x: 'left',
                y: 'top',
                text:['高','低']      // 文本，默认为数值文本
            },
            series : [
                {
                    name: 'iphone3',
                    type: 'map',
                    mapType: 'china',
                    roam: false,
                    itemStyle:{
                        normal:{label:{show:true, textStyle: {
                            color: "#434960",
                            fontSize:10
                        }}},
                        emphasis:{label:{show:true}}
                    },
                    data:[
                        {name: '北京',value: Math.round(Math.random()*1000)},
                        {name: '天津',value: Math.round(Math.random()*1000)},
                        {name: '上海',value: Math.round(Math.random()*1000)},
                        {name: '重庆',value: Math.round(Math.random()*1000)},
                        {name: '河北',value: Math.round(Math.random()*1000)},
                        {name: '河南',value: Math.round(Math.random()*1000)},
                        {name: '云南',value: Math.round(Math.random()*1000)},
                        {name: '辽宁',value: Math.round(Math.random()*1000)},
                        {name: '黑龙江',value: Math.round(Math.random()*1000)},
                        {name: '湖南',value: Math.round(Math.random()*1000)},
                        {name: '安徽',value: Math.round(Math.random()*1000)},
                        {name: '山东',value: Math.round(Math.random()*1000)},
                        {name: '新疆',value: Math.round(Math.random()*1000)},
                        {name: '江苏',value: Math.round(Math.random()*1000)},
                        {name: '浙江',value: Math.round(Math.random()*1000)},
                        {name: '江西',value: Math.round(Math.random()*1000)},
                        {name: '湖北',value: Math.round(Math.random()*1000)},
                        {name: '广西',value: Math.round(Math.random()*1000)},
                        {name: '甘肃',value: Math.round(Math.random()*1000)},
                        {name: '山西',value: Math.round(Math.random()*1000)},
                        {name: '内蒙古',value: Math.round(Math.random()*1000)},
                        {name: '陕西',value: Math.round(Math.random()*1000)},
                        {name: '吉林',value: Math.round(Math.random()*1000)},
                        {name: '福建',value: Math.round(Math.random()*1000)},
                        {name: '贵州',value: Math.round(Math.random()*1000)},
                        {name: '广东',value: Math.round(Math.random()*1000)},
                        {name: '青海',value: Math.round(Math.random()*1000)},
                        {name: '西藏',value: Math.round(Math.random()*1000)},
                        {name: '四川',value: Math.round(Math.random()*1000)},
                        {name: '宁夏',value: Math.round(Math.random()*1000)},
                        {name: '海南',value: Math.round(Math.random()*1000)},
                        {name: '台湾',value: Math.round(Math.random()*1000)},
                        {name: '香港',value: Math.round(Math.random()*1000)},
                        {name: '澳门',value: Math.round(Math.random()*1000)}
                    ]
                }
            ]
        };
        var option4 = {
            color:['#6CC1C8'],
            tooltip : {
                trigger: 'item',
                formatter : function (params) {
                    return params.seriesName + ' :'+params.value;
                }
            },
            grid:{
                borderWidth:0,
                x:'100',
                y:'20',
                x2:'0',
                y2:'20'
            },
            xAxis : [

                {	show:false,
                    type : 'value',
                    boundaryGap:[0,0.01]

                }
            ],
            yAxis : [
                {   axisLine:{
                    show:false
                },
                    axisTick:{
                        show:false
                    },
                    splitLine:{
                        show:false
                    },
                    type : 'category',
                    data : ['10000以上','2000-10000','1000-1999','800-999','600-799','400-599','200-399','200以下']
                }
            ],
            series : [
                {	itemStyle: {
                    normal: {
                        barBorderRadius:0
                    }
                },
                    name:'价格偏好',
                    type:'bar',
                    barWidth:10,
                    data:[100, 600, 650, 470, 1000, 900,750,500]
                }
            ]
        };
        var option5= {
            color:['#6DADC8'],
            tooltip : {
                trigger: 'item'
            },
            grid:{
                borderWidth:0,
                x:'50',
                y:'20',
                x2:'20',
                y2:'50'
            },
            xAxis : [
                { axisLine:{
                    lineStyle:{color:'#58A0C0'}
                },
                    splitLine:{
                        show:false
                    },
                    axisLabel:{
                        rotate:45
                    },
                    axisTick:{
                        show:false
                    },
                    type : 'category',
                    boundaryGap : false,
                    data : ['10.00','11;00','12:00','13:00','14:00','15:00','16:00','17:00','18:00','19:00','20:00']
                }
            ],
            yAxis : [
                {	axisLine:{
                    show:false
                },
                    splitArea:{
                        show:false
                    },
                    splitLine:{
                        lineStyle:{
                            color:'#999',
                            type: 'dashed'
                        }
                    },
                    type : 'value'
                }
            ],
            series : [
                {
                    itemStyle: {
                        symbolSize:'0',
                        normal: {
                            borderRadius:0,
                            nodeStyle:{
                                borderRadius:0
                            }
                        }
                    },
                    name:'购买时段',
                    type:'line',
                    stack: '总量',
                    symbolSize:0,
                    smooth:false,
                    data:[1000, 3000, 1500, 2800, 1000,5000,4444,6666,3333,2222,5555]
                }
            ]
        };

//            myChart.showLoading();
        myChart.setOption(option);
        myChart1.setOption(option1);
        myChart2.setOption(option4);
        myChart3.setOption(option5);
        myChart4.setOption(option2);
        myChart5.setOption(option);
        myChart6.setOption(option);
        myChart7.setOption(option3);
//            myChart.hideLoading();
        window.addEventListener("resize", function () {
            myChart.resize();
            myChart1.resize();
            myChart2.resize();
            myChart3.resize();
            myChart4.resize();
            myChart5.resize();
            myChart6.resize();
            myChart7.resize();
        });
    }
);
/*********************页面加载时**********************************************/
$().ready(function(){
    //页面加载时，异步加载显示的数据
    $($('.vip_nav_bar li[class="liactive"]')[0]).trigger('click')
    $($(".date_btn span")[0]).css({"color":"#fff","background":"#6cc1c8"});
    $('#select_analyze s').click(getMore);
    $('#select_analyze ul').on('click','li',showNameClick);
    $('#select_analyze_shop ul').on('click','li',showNameClick);
    //加载更多
    $('#side_analyze div s').click(getMore);
    //添加搜索
    $('#side_analyze div b span').click(searchValue);
    //活跃用户切换
    $('.activeVip .month_btn li').click(function () {
        sleepVipGet_sub(this);
        $("#page_row").val("10行/页");
    });
    //生日会员切换
    $('.birthVip .date_btn li').click(function () {
        birthVipGet_sub(this)
        $("#page_row").val("10行/页");
    });
    //新会员切换
    $('.newVip .new_btn li').click(function () {
        newVipGet_sub(this)
        $("#page_row").val("10行/页");
    });
});
/*****************************************************************************************************************/
//左侧业绩选择日周年月
$(".choose").mouseover(function () {
    $(".select_Date").show();
}).mouseleave(function () {
    $(".select_Date").hide();
})
$(".select_Date li").click(function () {
    var content=$(this).html();
    $(".title_l").html(content);
})
