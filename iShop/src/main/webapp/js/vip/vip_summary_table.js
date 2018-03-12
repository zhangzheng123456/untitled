var oc = new ObjectControl();
$(document).click(function (e) {
    var selector=$(".select_list_name_wrap");
    if(!selector.is(e.target) && selector.has(e.target).length === 0){
        selector.hide();
    }
});
var vip_management={
    brand_data:"",
    query_type:"",
    brand_code:"",
    achv_source:"",
    start_time:"",
    end_time:"",
    init:function(){
        this.bind();
    },
    bind:function(){
        this.filterBtn();
        this.resetScrollBar(".scroll_area");
    },
    formatCurrency:function (num) {
    num=String(num);
    var reg=num.indexOf('.') >-1 ? /(\d{1,3})(?=(?:\d{3})+\.)/g : /(\d{1,3})(?=(?:\d{3})+$)/g;//千分符的正则
    num=num.replace(reg, '$1,');//千分位格式化
    return num;
    },
    resetScrollBar:function(select){
        $(select).niceScroll({
            cursorborder: "0 none",
            cursoropacitymin: "0",
            boxzoom: false,
            cursorcolor: " #dfdfdf",
            cursoropacitymax: 1,
            touchbehavior: false,
            cursorminheight: 50,
            autohidemode: false,
            cursorwidth: "5px",
            cursorborderradius: "2px"
        });
    },
    get_brand_list:function(){
        var _param = {};
        _param["searchValue"] ="";
        _param["corp_code"] ="C10000";
        oc.postRequire("post", "/shop/brand", "", _param, function (data) {
            var message=JSON.parse(data.message);
            vip_management.brand_data=message.brands;
            vip_management.splicing_brand_list(vip_management.brand_data)
        })
    },
    listNoData:function(list){
        if(list.length==0){
            var tr = "";
            $(".table_tbodyWrap").css("overflow-y","hidden");
            for(var i=0;i<7;i++){
                if(i==2){
                    tr += '<tr><td style="text-align: center;padding-left: 0" colspan="10">获取数据失败</td></tr>'
                }else {
                    tr += '<tr><td colspan="10"></td></tr>'
                }
            }
            $("#table_tbody tbody").html(tr)
        }
    },
    splicing_brand_list:function(data){
        var li="";
        var liHtml="<li data-type=''>请选择品牌</li>";
        for(var i=0;i<data.length;i++){
            liHtml+="<li data-type='"+data[i].brand_code+"'>"+data[i].brand_name+"</li>"
        }
        $("#brand_list").html(liHtml);
        this.resetScrollBar("#brand_list");
    },
    get_management:function () {
        var type=vip_management.query_type;
        var self=this;
        var param = {
            "query_type" : type,
            "start_time" : vip_management.start_time,
            "end_time" : vip_management.end_time,
            "brand_code": vip_management.brand_code,
            "vip_source":"",
            "achv_source": vip_management.achv_source
        };
        $("#table_loading").show();
        oc.postRequire("post","/board/managerKpi","",param,function (data) {
            $("#table_tbody tbody").html("");
            $("#table_thead thead tr").html("");
            if(data.code == 0){
                var msg = JSON.parse(data.message);
                var list="";
                switch (type){
                    case "brand" :
                        list = msg.brand;
                        var head=["品牌","会员数量","人数占比","业绩","业绩占比","会销占比"];
                        head.map(function(item) {
                            $("#table_thead thead tr").append("<th>" + item + "</th>");
                        });
                        list.map(function(item){
                            var tr='<tr>' +
                                '<td style="text-overflow: ellipsis;overflow: hidden;white-space: nowrap;">'+item.brand_name+'</td>' +
                                '<td>'+self.formatCurrency(item.vip_count)+'</td>' +
                                '<td>'+item.vip_rate+'</td>' +
                                '<td>'+self.formatCurrency(item.brand_amt_trade)+'</td>' +
                                '<td>'+item.amt_rate+'</td>' +
                                '<td>'+item.vip_amt_rate+'</td>' +
                                '</tr>';
                            $("#table_tbody tbody").append(tr);
                        });
                        break;
                    case "source" :
                       list = msg.sourceRank;
                        var head=["渠道","会员数量","人数占比","业绩","业绩占比","会销占比"];
                        head.map(function(item) {
                            $("#table_thead thead tr").append("<th>" + item + "</th>");
                        });
                        list.map(function(item){
                            var tr='<tr>' +
                                   '<td>'+item.source+'</td>' +
                                    '<td>'+self.formatCurrency(item.vip_count)+'</td>' +
                                    '<td>'+item.vip_rate+'</td>' +
                                    '<td>'+self.formatCurrency(item.brand_amt_trade)+'</td>' +
                                    '<td>'+item.amt_rate+'</td>' +
                                    '<td>'+item.vip_amt_rate+'</td>' +
                                    '</tr>';
                            $("#table_tbody tbody").append(tr);
                        });
                        break;
                    case "cardType" :
                       list = msg.cardType;
                        //var head=["会员等级","会员数量","人数占比","会员消费","会销占比"];
                        var head=["会员等级","会员数量","人数占比"];
                        head.map(function(item) {
                            $("#table_thead thead tr").append("<th>" + item + "</th>");
                        });
                        list.map(function(item){
                            var tr='<tr>' +
                                   '<td>'+item.card_type+'</td>' +
                                    '<td>'+self.formatCurrency(item.vip_num)+'</td>' +
                                    '<td>'+item.vip_rate+'</td>' +
                                    //'<td>'+item.amt_trade+'</td>' +
                                    //'<td>'+item.vip_amt_rate+'</td>' +
                                    '</tr>';
                            $("#table_tbody tbody").append(tr);
                        });
                        break;
                    case "newVip" :
                       list = msg.newVip;
                        var head=["新老会员","会员数量","人数占比","会员消费","会销占比"];
                        head.map(function(item) {
                            $("#table_thead thead tr").append("<th>" + item + "</th>");
                        });
                        list.map(function(item){
                            var tr='<tr>' +
                                   '<td>'+item.key+'</td>' +
                                    '<td>'+self.formatCurrency(item.vip_count)+'</td>' +
                                    '<td>'+item.vip_rate+'</td>' +
                                    '<td>'+self.formatCurrency(item.amt_trade)+'</td>' +
                                    '<td>'+item.amt_rate+'</td>' +
                                    '</tr>';
                            $("#table_tbody tbody").append(tr);
                        });
                        break;
                    case "actVip" :
                       list = msg.actVip;
                        var head=["活跃会员","会员数量","人数占比"];
                        head.map(function(item) {
                            $("#table_thead thead tr").append("<th>" + item + "</th>");
                        });
                        list.map(function(item){
                            var tr='<tr>' +
                                   '<td>'+item.name+'</td>' +
                                    '<td>'+self.formatCurrency(item.size)+'</td>' +
                                    '<td>'+item.rate+'</td>' +
                                    '</tr>';
                            $("#table_tbody tbody").append(tr);
                        });
                        break;
                }
                self.listNoData(list);
                $("#table_loading").hide();
            }else {
                $("#table_loading").hide();
                var tr = "";
                $(".table_tbodyWrap").css("overflow-y","hidden");
                for(var i=0;i<7;i++){
                    if(i==2){
                        tr += '<tr><td style="text-align: center;padding-left: 0" colspan="10">获取数据失败</td></tr>'
                    }else {
                        tr += '<tr><td colspan="10"></td></tr>'
                    }
                }
                $("#table_tbody tbody").html(tr)
            }
        })
    },
    filterBtn:function(){
        var that=this;
        $("#filterBtn").click(function(){
            that.brand_code=$("#brandInput").attr("data-value")==undefined?"":$("#brandInput").attr("data-value");
            that.achv_source=$("#skuInput").attr("data-value")==undefined?"":$("#skuInput").attr("data-value");
            that.start_time=$("#search_time").attr("data-start")==undefined?"":$("#search_time").attr("data-start");
            that.end_time=$("#search_time").attr("data-end")==undefined?"":$("#search_time").attr("data-end");
            that.get_management();
        });
        $("#emptyFilterBtn").click(function(){
            $("#skuInput").attr("data-value","").val("请选择渠道");
            $("#brandInput").attr("data-value","").val("请选择品牌");
            $("#search_time").attr("data-start","").attr("data-end","").val("");
            $("#filterBtn").trigger("click");
        })
    }
};
//列表nav切换
$("#table_nav li").click(function () {
    if(vip_management.nav_type == $(this).attr("data-nav"))return;
    //$("#table_title span").eq(0).text($(this).text());
    $(this).parent().find(".nav-active").removeClass("nav-active");
    $(this).addClass("nav-active");
   if($("#brand_list li[data-type="+vip_management.brand_code+"]").html()!=undefined){
       $("#brandInput").attr("data-value",vip_management.brand_code).val($("#brand_list li[data-type="+vip_management.brand_code+"]").html());
   }
    if(vip_management.achv_source==""){
        $("#skuInput").attr("data-value",vip_management.achv_source).val("请选择渠道");
    }else{
        $("#skuInput").attr("data-value",vip_management.achv_source).val(vip_management.achv_source);
    }
    if(vip_management.start_time=="" || vip_management.end_time==""){
        $("#search_time").val("").attr("data-start","").attr("data-end","");
    }else{
        $("#search_time").val(vip_management.start_time+" - "+vip_management.end_time).attr("data-start",vip_management.start_time).attr("data-end",vip_management.end_time);
    }
    vip_management.query_type = $(this).attr("data-nav");
    vip_management.get_management();
});
$(".select_list_name").on("click","li",function(){
    $(this).parents(".select_list_name_wrap").prev("input").attr("data-value",$(this).attr("data-type")).val($(this).html());
    $(this).parents(".select_list_name_wrap").hide();
});
$("#skuInput").click(function(e){
    e.stopPropagation();
    $(this).next().toggle();
    $("#brandInput").next().hide();
});
$("#brandInput").click(function(e){
    e.stopPropagation();
    $(this).next().toggle();
    $("#skuInput").next().hide();
    if(vip_management.brand_data==""){
        vip_management.get_brand_list();
    }
    $("#brand_list").getNiceScroll().resize();
});
$(function(){
    vip_management.init();
    laydate.render({
        elem: '#search_time'
        ,range: true,
        done: function(value, date, endDate){
            if(value==""){
                $("#search_time").attr("data-start","");
                $("#search_time").attr("data-end","");
            }else{
                value=value.split(" - ");
                $("#search_time").attr("data-start",value[0]);
                $("#search_time").attr("data-end",value[1]);
            }

        }
    });
    $("#table_nav li.nav-active").trigger("click");
});

//防止ajax重复请求
var pendingRequests = {};
$.ajaxPrefilter(function( options, originalOptions, jqXHR ) {
    var key = options.url;
   if(!pendingRequests[key]){
        pendingRequests[key] = jqXHR;
    }else {
        pendingRequests[key].abort();
        pendingRequests[key] = jqXHR;
    }
    var complete = options.complete;
    options.complete = function(jqXHR, textStatus) {
        //pendingRequests[key] = null;
        //pendingRequests[name] = null;
        if ($.isFunction(complete)) {
            complete.apply(this, arguments);
        }
    };
});