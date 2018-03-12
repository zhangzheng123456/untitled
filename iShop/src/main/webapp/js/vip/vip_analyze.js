var oc = new ObjectControl();
var page = 1;
var jump = 1;//标签跳转
var jump_s = "";//消费记录标签
var query_type = '';//创建活跃会员的标签请求
var month_type = '';//会员生日月份类型
var count = '';
var un_push = '';
var proportion_list = {};
//点击更多加载的页码参数
var page_shop = 1;
var page_group = 1;
var page_area = 1;
var next_shop_page = '';
var next_group_page = '';
var next_area_page = '';
var area_code="";
var brand_code="";
var store_code="";
var chart={
    "myChart":"",
    "myChart1":"",
    "myChart2":"",
    "myChart3":"",
    "myChart4":"",
    "myChart5":"",
    "myChart6":"",
    "chartMId":"", //中类偏好
    "chartSm":"",//小类偏好
    "chartSeries":""//系列偏好
};
var chart_data={
    type:"",
    weeks:"",
    price:"",
    month:"",
    season:"",
    series:"",
    area:"",
    typeMid:"",
    typeSm:"",
    typeSeries:"",
    age:"",
    cardType:"",
    sex_vip_num:"",
    birth_vip_num:"",
    source:""
};
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);//取key_val的值
var funcCode=key_val.func_code;
function sendMsgVal(){
    var param = {};
    param["funcCode"] = funcCode;
    oc.postRequire("post", " /list/action", "", param, function (data) {
        if(data.code == 0){
            var message = JSON.parse(data.message);
            var actions = message.actions;
            for(i=0;i<actions.length;i++){
                if(actions[i].act_name == 'sendSms'){
                    $(".vip_table_lg .vip_analyze_operate").show()
                }else{
                    $('.vip_table_lg .vip_analyze_operate').hide();
                }
            }

        }else if(data.code == -1){
            console.log("error");
        }
    })
}
// var echarts;
/**********************左侧数据**************************************************************************************/
//获取品牌
function getBrand() {
    var search_param = arguments.length;
    var param = {};
    var ul = '';
    param["corp_code"] = "C10000";
    param["searchValue"] = $("#select_analyze_brand input").val();
    $("#select_analyze_brand ul").append("<div style='text-align: center;height: 30px;line-height: 30px'>数据加载中...</div>");
    oc.postRequire("post", "/brand/findBrandByCorpCode", "", param, function (data) {
        //     oc.postRequire("post","/shop/brands", "",param, function(data){
        if (data.code == 0) {
            var message = JSON.parse(data.message)
            var brands = message.brands;//数组
            if (brands.length <= 0) {
                return
            }
            for (var i = 0; i < brands.length; i++) {
                ul += "<li brand_cord='" + brands[i].brand_code + "'>" + brands[i].brand_name + "</li>";
                // brands[i].brand_name == '全部' ? '' : ul += "<li brand_cord='" + brands[i].brand_code + "'>" + brands[i].brand_name + "</li>";
            }
            var brand_name = brands[0].brand_name;
            var brand_code = brands[0].brand_code;//区域号
            if (search_param == 0) {
                $('#side_analyze ul li:nth-child(1) s').html(brand_name);
                $('#side_analyze ul li:nth-child(1) s').attr('brand_code', brand_code);
            }
            $('#select_analyze_brand ul div').remove();
            $('#select_analyze_brand ul').append(ul);
        } else if (data.code == -1) {
            alert(data.message);
        }
    });
}
getBrand();
getGroup();
GetArea();
//获取区域
function GetArea() {
    var search_param = arguments.length;
    var searchValue = $('#select_analyze input').val();
    var param = {};
    param['pageNumber'] = arguments[0] ? arguments[0] : page;
    param['pageSize'] = 8;
    param['searchValue'] = searchValue;
    param["corp_code"] = "C10000";
    $("#select_analyze ul").append("<div style='text-align: center;height: 30px;line-height: 30px'>数据加载中...</div>");
    oc.postRequire("post", "/area/findAreaByCorpCode", "", param, function (data) {
        if (data.code == "0") {
            var message = JSON.parse(data.message);//��ȡmessagejson�����DOM����
            var output = JSON.parse(message.list);
            var ul = '';
            var first_area = '';
            var first_area_code = '';
            var output_list = output.list;
            next_area_page = output.hasNextPage;
            // output_list.length<7&&($('#select_analyze s').attr('style','display:none'));
            if (output_list.length < 8) {
                $('#select_analyze s').attr('style', 'display:none')
            } else {
                $('#select_analyze s').attr('style', 'display:block')
            }
            first_area = output_list[0].area_name;
            first_area_code = output_list[0].area_code;//区域号
            //设置本地缓存企业编号
            localStorage.setItem('corp_code', 'C10000');
            for (var i = 0; i < output_list.length; i++) {
                ul += "<li data_area='" + output_list[i].area_code + "'>" + output_list[i].area_name + "</li>";
                // output_list[i].area_name == '全部' ? '' : ul += "<li data_area='" + output_list[i].area_code + "'>" + output_list[i].area_name + "</li>";
            }
            if (search_param == 0) {
                $('#side_analyze ul li:nth-child(2) s').html(first_area);
                $('#side_analyze ul li:nth-child(2) s').attr('data_area', first_area_code);
            }
            var area_code = output_list[0].area_code;
            // localStorage.setItem('area_code',area_code);
            //如果是加载跟多就不清除内容店铺下拉列表
            if (un_push) {
                un_push = 0;
            } else {
                $('#select_analyze_shop ul').html('');
                getStore(area_code);
            }
            $("#select_analyze ul div").remove();
            $('#select_analyze ul').append(ul);
        } else if (data.code == "-1") {
            alert(data.message);
        }
    });
}
//获取店铺
function getStore(a, b) {
    var searchValue = $('#select_analyze_shop input').val();
    var area_code = a;
    var param = {};
    param['brand_code'] = $('#side_analyze ul li:nth-child(1) s').attr('brand_code');
    param['pageNumber'] = arguments[1] ? arguments[1] : page;
    param['pageSize'] = 8;
    param['searchValue'] = searchValue;
    param["area_code"] = area_code;
    param["corp_code"] = "C10000";
    next_shop_page = false;
    $("#select_analyze_shop ul").append("<div style='text-align: center;height: 30px;line-height: 30px'>数据加载中...</div>");
    oc.postRequire("post", "/shop/findByAreaCode", "", param, function (data) {
        var ul = '';
        var first_store_name = '';
        var first_store_code = '';
        var message = JSON.parse(data.message);
        var output = JSON.parse(message.list);
        next_shop_page = output.hasNextPage;
        var total = output.total;
        var output_list = output.list;
        if (output_list.length < 8) {
            $('#select_analyze_shop s').attr('style', 'display:none')
        } else {
            $('#select_analyze_shop s').attr('style', 'display:block')
        }
        for (var i = 0; i < output_list.length; i++) {
            ul += "<li data_store='" + output_list[i].store_code + "'>" + output_list[i].store_name + "</li>";
            // output_list[i].store_name == '全部' ? '' : ul += "<li data_store='" + output_list[i].store_code + "'>" + output_list[i].store_name + "</li>";
        }
        $("#select_analyze_shop ul div").remove();
        $('#select_analyze_shop ul').append(ul);
        // un_push?un_push=0;page=1:brithVipGet();getData();//正常调用当为加载更多时不调用
        if (un_push) {
            un_push = 0;
        } else {
            chartShow();
            if (total > 500) {
                $("#start_analyze_left").show();
                $("#start_analyze_right").show();
                return;
            }
            if (total < 500) {
                $("#start_analyze_left").hide();
                $("#start_analyze_right").hide();
                $($('.vip_nav_bar li[class="liactive"]')[0]).trigger('click');
                getData();
            }
            page = 1;
        }
    });
}
//获取导购
function getGroup() {
    var param = {};
    var ul = '';
    param["corp_code"] = "C10000";
    param["area_code"] =$('#side_analyze ul li:nth-child(2) s').attr('data_area');
    param["brand_code"]=$('#side_analyze ul li:nth-child(1) s').attr('brand_code');
    param["store_code"]=$('#side_analyze ul li:nth-child(3) s').attr('data_store');
    param["pageNumber"]=page_group;
    param["pageSize"]=8;
    param["searchValue"] = $("#select_analyze_group input").val();
    next_group_page = false;
    $("#select_analyze_group ul").append("<div style='text-align: center;height: 30px;line-height: 30px'>数据加载中...</div>");
    oc.postRequire("post", "/user/findUsersByRole", "", param, function (data) {
        if (data.code == 0) {
            var message = JSON.parse(data.message);
            var groups = JSON.parse(message.list);//数组
            next_group_page=groups.hasNextPage;
            groups=groups.list;
            if (groups.length <= 0) {
                return
            }
            for (var i = 0; i < groups.length; i++) {
                ul += "<li group_code='" + groups[i].user_code + "'>" + groups[i].user_name + "</li>";
                // groups[i].vip_group_name == '全部' ? '' : ul += "<li group_code='" + groups[i].user_code + "'>" + groups[i].user_name + "</li>";
            }
            $("#select_analyze_group ul div").remove();
            $('#select_analyze_group ul').append(ul);
        } else if (data.code == -1) {
            console.log(data.message);
        }
    });
}
//点击li填充s中的数据显示
function showNameClick(e) {
    var e = e.target;
    $(e).addClass('choose_li').siblings('.choose_li').removeClass('choose_li');
    var d = $(e).parent().parent().parent();
    if ($(d).attr('id') == 'select_analyze') {
        page_group = 1;
        page_shop = 1;
        var area_code = $(e).attr('data_area');
        $('#side_analyze ul li:nth-child(2) s').html($(e).html());
        $('#side_analyze ul li:nth-child(2) s').attr('data_area', area_code);
        $('#side_analyze ul li:nth-child(3) s').html("全部");
        $('#side_analyze ul li:nth-child(3) s').attr('data_store', "");
        $('#side_analyze ul li:nth-child(4) s').html("全部");
        $('#side_analyze ul li:nth-child(4) s').attr('group_code', "");
        //清除内容店铺下拉列表
        $('#select_analyze_shop ul').html('');
        $("#select_analyze_group ul").html("");
        getStore(area_code,"");
        getGroup();
        $('#select_analyze').toggle();
    } else if ($(d).attr('id') == 'select_analyze_brand') {
        page_group = 1;
        page_shop = 1;
        $('#select_analyze_brand').toggle();
        var brand_code = $(e).attr('brand_cord');
        $('#side_analyze ul li:nth-child(1) s').html($(e).html());
        $('#side_analyze ul li:nth-child(1) s').attr('brand_code', brand_code);
        $('#side_analyze ul li:nth-child(3) s').html("全部");
        $('#side_analyze ul li:nth-child(3) s').attr('data_store', "");
        $('#side_analyze ul li:nth-child(4) s').html("全部");
        $('#side_analyze ul li:nth-child(4) s').attr('group_code', "");
        //清除内容店铺下拉列表
        $('#select_analyze_shop ul').html('');
        $("#select_analyze_group ul").html("");
        getStore($('#side_analyze ul li:nth-child(2) s').attr('data_area'),"");
        getGroup();
        // $($('.vip_nav_bar li[class="liactive"]')[0]).trigger('click');
    } else if ($(d).attr('id') == 'select_analyze_shop') {
        page_group = 1;
        $("#start_analyze_left").hide();
        $("#start_analyze_right").hide();
        var store_code = $(e).attr('data_store');
        $('#side_analyze ul li:nth-child(3) s').html($(e).html());
        $('#side_analyze ul li:nth-child(3) s').attr('data_store', store_code);
        $('#side_analyze ul li:nth-child(4) s').html("全部");
        $('#side_analyze ul li:nth-child(4) s').attr('group_code', "");
        $('#select_analyze_shop').toggle();
        $("#select_analyze_group ul").html("");
        //添加店铺时，找到显示的DIV发起请求
        $($('.vip_nav_bar li[class="liactive"]')[0]).trigger('click');
        getGroup();
        getData();
        chartShow()
    } else if ($(d).attr('id') == 'select_analyze_group') {
        var group_code = $(e).attr('group_code');
        $('#side_analyze ul li:nth-child(4) s').html($(e).html());
        $('#side_analyze ul li:nth-child(4) s').attr('group_code', group_code);
        $('#select_analyze_group').toggle();
        //添加分组时，找到显示的DIV发起请求
        $($('.vip_nav_bar li[class="liactive"]')[0]).trigger('click');
        chartShow()
    } else if ($(d).attr('id') == 'select_analyze_source') {
        var data_type = $(e).attr('data-type');
        $('#side_analyze ul li:nth-child(5) s').html($(e).html());
        $('#side_analyze ul li:nth-child(5) s').attr('data-type', data_type);
        $('#select_analyze_source').toggle();
        //添加分组时，找到显示的DIV发起请求
        $($('.vip_nav_bar li[class="liactive"]')[0]).trigger('click');
        getData();
        chartShow()
    }
}
//取消下拉框
$(document).on('click', function (e) {
    if (!(e.target == $($('#side_analyze>ul')[0]).find('li:nth-child(2)')
        || e.target == $('#select_analyze')
        || e.target == $('#select_analyze div')[0]
        || e.target == $('#select_analyze div b')[0]
        || e.target == $('#select_analyze div b input')[0]
        || e.target == $('#select_analyze div b span')[0]
        || e.target == $('#select_analyze div ul')[0]
        || e.target == $('#select_analyze div ul li')[0]
        || e.target == $('#select_analyze div s')[0]))$('#select_analyze').hide();
    if (!(e.target == $($('#side_analyze_shop>ul')[0]).find('li:nth-child(2)')
        || e.target == $('#select_analyze_shop')
        || e.target == $('#select_analyze_shop div')[0]
        || e.target == $('#select_analyze_shop div b')[0]
        || e.target == $('#select_analyze_shop div b input')[0]
        || e.target == $('#select_analyze_shop div b span')[0]
        || e.target == $('#select_analyze_shop div ul')[0]
        || e.target == $('#select_analyze_shop div ul li')[0]
        || e.target == $('#select_analyze_shop div s')[0]))$('#select_analyze_shop').hide();
    if (!(e.target == $($('#side_analyze_brand>ul')[0]).find('li:nth-child(2)')
        || e.target == $('#select_analyze_brand')
        || e.target == $('#select_analyze_brand div')[0]
        || e.target == $('#select_analyze_brand div b')[0]
        || e.target == $('#select_analyze_brand div b input')[0]
        || e.target == $('#select_analyze_brand div b span')[0]
        || e.target == $('#select_analyze_brand div ul')[0]
        || e.target == $('#select_analyze_brand div ul li')[0]
        || e.target == $('#select_analyze_brand div s')[0]))$('#select_analyze_brand').hide();
    if (!(e.target == $($('#side_analyze>ul')[0]).find('li:nth-child(2)')
        || e.target == $('#select_analyze_group')
        || e.target == $('#select_analyze_group div')[0]
        || e.target == $('#select_analyze_group div b')[0]
        || e.target == $('#select_analyze_group div b input')[0]
        || e.target == $('#select_analyze_group div b span')[0]
        || e.target == $('#select_analyze_group div ul')[0]
        || e.target == $('#select_analyze_group div ul li')[0]
        || e.target == $('#select_analyze_group div s')[0]))$('#select_analyze_group').hide();
    if (!(e.target == $($('#select_analyze_source>ul')[0]).find('li:nth-child(2)')
        || e.target == $('#select_analyze_source')
        || e.target == $('#select_analyze_source div')[0]
        || e.target == $('#select_analyze_source div b')[0]
        || e.target == $('#select_analyze_source div b input')[0]
        || e.target == $('#select_analyze_source div b span')[0]
        || e.target == $('#select_analyze_source div ul')[0]
        || e.target == $('#select_analyze_source div ul li')[0]
        || e.target == $('#select_analyze_source div s')[0]))$('#select_analyze_source').hide();
    if(!($(e.target).is(".chart_year_input"))){$("#chart_year_select").hide();}
});
//加载更多
function getShopMore() {
    un_push = 1;
    var area_code = $('#side_analyze ul li:nth-child(2) s').attr('data_area');
    page_shop++;
    getStore(area_code, page_shop);
}
function getAreaMore() {
    un_push = 1;
    page_area++;
    GetArea(page_area);
}
function getGroupMore(){
    un_push=1;
    page_group++;
    getGroup();
}
//滚动监听加载更多
//区域滚动事件
$("#select_analyze_shop ul").bind("scroll", function () {
    var nScrollHight = $(this)[0].scrollHeight;
    var nScrollTop = $(this)[0].scrollTop;
    var nDivHight = $(this).height();
    if (nScrollTop + nDivHight >= nScrollHight && nScrollHight !== 0) {
        next_shop_page ? getShopMore() : page_shop = 1;
    }
});
//导购滚动事件
$("#select_analyze_group ul").bind("scroll", function () {
    var nScrollHight = $(this)[0].scrollHeight;
    var nScrollTop = $(this)[0].scrollTop;
    var nDivHight = $(this).height();
    if (nScrollTop + nDivHight >= nScrollHight && nScrollHight !== 0) {
        next_group_page ? getGroupMore() : page_group = 1;
    }
});
$("#select_analyze ul").bind("scroll", function () {
    var nScrollHight = $(this)[0].scrollHeight;
    var nScrollTop = $(this)[0].scrollTop;
    var nDivHight = $(this).height();
    if (nScrollTop + nDivHight >= nScrollHight && nScrollHight !== 0) {
        next_area_page ? getAreaMore() : page_area = 1;
    }
});
//搜索
function searchValue(e) {
    //page初始化
    page = 1;
    page_group = 1;
    un_push = 1;
    //进入搜索清空内容
    $(e.target).parent().next().html('');
    //清楚加载更多
    $(e.target).parent().next().next().attr('style', 'display:block');
    //获取店铺列表的value值e
    var searchValue = $(e.target).prev().val();
    //获得其父级元素的id熟属性
    var parent = $(e.target).parent().parent().parent();
    //判断是区域搜索还是店铺搜索
    if ($(parent).attr('id') == 'select_analyze') {
        GetArea();
    } else if ($(parent).attr('id') == 'select_analyze_brand') {
        getBrand();
    } else if ($(parent).attr('id') == 'select_analyze_shop') {
        getStore($($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area'));
    } else if ($(parent).attr('id') == 'select_analyze_group') {
        getGroup();
    }
}
//页面加载前加载区域
//绑定事件下拉事件
$('#side_analyze>ul:nth-child(1) li').click(function () {
    var event = window.event || arguments[0];
    if (event.stopPropagation) {
        event.stopPropagation();
    } else {
        event.cancelBubble = true;
    }
    var top=$(this).position().top;
    var height=$(this).height();
    if ($(this).find('b').attr("data-name") == 'area') {
        $('#select_analyze').css("top",top+height);
        $('#select_analyze').toggle();
        if($('#select_analyze').css("display")!=="none"){
            $("#select_analyze .scroll_bar").getNiceScroll().resize();
        }
        $('#select_analyze_shop').hide();
        $('#select_analyze_group').hide();
        $('#select_analyze_source').hide();
        // if ($('#select_analyze input').val()) {
        //     $('#select_analyze input').val('');
        //     $('#select_analyze ul').html('');
        //     GetArea();
        // }
    } else if ($(this).find('b').attr("data-name") == 'brand') {
        $('#select_analyze_brand').css("top",top+height);
        $('#select_analyze_brand').toggle();
        if($('#select_analyze_brand').css("display")!=="none"){
            $("#select_analyze_brand .scroll_bar").getNiceScroll().resize();
        }
        $('#select_analyze_shop').hide();
        $('#select_analyze').hide();
        $('#select_analyze_group').hide();
        $('#select_analyze_source').hide();
        // if ($('#select_analyze_brand input').val()) {
        //     $('#select_analyze_brand input').val('');
        //     $('#select_analyze_brand ul').html('');
        //     getBrand();
        // }
    } else if ($(this).find('b').attr("data-name") == 'shop') {
        $('#select_analyze_shop').css("top",top+height);
        $('#select_analyze_shop').toggle();
        if($('#select_analyze_shop').css("display")!=="none"){
            $("#select_analyze_shop .scroll_bar").getNiceScroll().resize();
        }
        $('#select_analyze_group').hide();
        $('#select_analyze_source').hide();
        // //下拉搜索内容重置
        // if ($('#select_analyze_shop input').val()) {
        //     $('#select_analyze_shop input').val('');
        //     $('#select_analyze_shop ul').html('');
        //     un_push = 1;
        //     getStore($($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area'));
        // }
    } else if ($(this).find('b').attr("data-name") == 'group') {
        $('#select_analyze_group').css("top",top+height);
        $('#select_analyze_group').toggle();
        if($('#select_analyze_group').css("display")!=="none"){
            // $('#select_analyze_group ul').empty();
            // getGroup();
            $("#select_analyze_group .scroll_bar").getNiceScroll().resize();
        }
        $('#select_analyze_shop').hide();
        $('#select_analyze').hide();
        $('#select_analyze_brand').hide();
        $('#select_analyze_source').hide();
        //下拉搜索内容重置
        // if ($('#select_analyze_group input').val()) {
        //     $('#select_analyze_group input').val('');
        //     $('#select_analyze_group ul').html('');
        // }
    } else if ($(this).find('b').attr("data-name") == 'source') {
        $('#select_analyze_source').css("top",top+height);
        $('#select_analyze_source').toggle();
        if($('#select_analyze_source').css("display")!=="none"){
            // $('#select_analyze_group ul').empty();
            // getGroup();
            $("#select_analyze_source .scroll_bar").getNiceScroll().resize();
        }
        $('#select_analyze_shop').hide();
        $('#select_analyze').hide();
        $('#select_analyze_brand').hide();
    }
});
//按时间查看数据
function getData() {
    var param = {};
    param['brand_code'] = $($('#side_analyze ul li:nth-child(1) s')[0]).attr('brand_code');
    param['store_code'] = $($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    param['corp_code'] = localStorage.getItem('corp_code');
    param["area_code"] = $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    param["user_code"] = $($('#side_analyze ul li:nth-child(4) s')[0]).attr('group_code');
    //param["source"] = $($('#side_analyze ul li:nth-child(5) s')[0]).attr('data-type');
    param["type"] = $(".title_l").attr("data-type");
    param["time"] = $($('.date_title .date input')[0]).val();
    $("#left_shadow").show();
    oc.postRequire("post", "/vipAnalysis/vipScale", "", param, function (data) {
        if(data.code == 0){
            var message = JSON.parse(data.message);
            for (var key in message) {
                switch (key) {
                    case 'all':
                        fillVip(message[key], $('.all_vip span b'), $('.all_vip table tbody tr'));
                        break;
                    case 'new':
                        fillVip(message[key], $('.new_vip span b'), $('.new_vip table tbody tr'));
                        break;
                    case 'old':
                        fillVip(message[key], $('.old_vip span b'), $('.old_vip table tbody tr'));
                        break;
                }
            }
        }else if(data.code == -1){
            console.log("error");
        }
    })
}
function formatCurrency(num) {
    num=String(num);
    var reg=num.indexOf('.') >-1 ? /(\d{1,3})(?=(?:\d{3})+\.)/g : /(\d{1,3})(?=(?:\d{3})+$)/g;//千分符的正则
    num=num.replace(reg, '$1,');//千分位格式化
    return num;
}
//会员填充数据
function fillVip(data, count, container) {
    $(count).empty();
    $(container).empty();
    var html = '';
    // $(count).html('总数：' + data['number']);
    html += "<td>" + data['percent'] + "</td>"
        + "<td>" + formatCurrency(data['k_price_all']) + "</td>"
        + "<td>" + formatCurrency(data['k_price_one']) + "</td>"
        + "<td>" + formatCurrency(data['price_one']) + "</td>";
    $(container).html(html);
    $("#left_shadow").hide();
}
function compare(a,b) {
   return a-b ;
}
//图表显示接口
function chartShow(order) {
    var key_val = sessionStorage.getItem("key_val");//取页面的function_code
    key_val = JSON.parse(key_val);
    var funcCode = key_val.func_code;
    if (order !== "" && order !== undefined) {
        var id = $("#chart_analyze").attr("data-id");
        var param = {
            "corp_code": "C10000",
            "function_code": funcCode,
            "type": "order",
            "id": id,
            "order": order
        };
    } else {
        var param = {
            "corp_code": "C10000",
            "function_code": funcCode,
            "type": "show"
        };
    }
    oc.postRequire("post", "/privilege/vip/chartOrder", "0", param, function (data) {
        if (data.code == 0) {
            if (data.message == "success") {
            } else {
                var message = JSON.parse(data.message);
                var list =message.list;
                if(message.can_add=="N"){
                    $("#add_chart").hide();
                }else{
                    $("#add_chart").show();
                }
                if (list.length == 0) {
                    return;
                }
                var l = list.length - 1;
                var id = list[l].id;
                if (id !== undefined) {
                    $("#chart_analyze").attr("data-id", id);
                    var column = JSON.parse(list[l].column_name);
                    for (var i = 0; i < column.length; i++) {
                        var a = column[i];
                        $(".chart_module").each(function () {
                            if (a == $(this).attr("data-id")) {
                                chartData(a);
                                $(this).find(".chart_analyze_condition span").text("按件数分析");
                                $("#chart_analyze").append($(this));
                                $(this).show();
                                //var ID = $(this).attr("data-id");
                                //init_chart(ID);
                            }
                        });
                    }
                }
            }
        } else if (data.code == -1) {
            console.log(data.message);
        }
    });
}
function init_chart(id,type) {
    if (id == "type") {
        if(chart_data.type==""){
            $("#type").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#type").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var data = chart_data.type['Type'];
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#type").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#type").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr = [];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
            head+='<li title="'+arr[i].name+'">'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#type").next().html(head+value);
        $("#type").next().find("li").css("width",($("#type").next().width()-10)/$(head).find("li").length+"px")
        var option_pie = {
            color: ['#44556c', '#4a5f7c', '#5475a2', '#5f8bc8', '#7c92ca', '#9999cc', '#6db0d3','#93d7e2', '#c1e7ed','#e5e8ea', '#41c7db'],
            tooltip: {
                textStyle: {
                    fontSize: '10'
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: 'true',
                orient: 'vertical',
                x: 'left',
                y: 'center',
                data: Arr
            },
            series: [
                {
                    name: '消费分类',
                    center: ['60%', '50%'],
                    type: 'pie',
                    radius: ['50%', '60%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                                position: 'center'
                            },
                            labelLine: {
                                show: false
                            }
                        },
                        emphasis: {
                            label: {
                                show: true,
                                textStyle: {
                                    fontSize: '20',
                                    fontWeight: 'bold'
                                }
                            }
                        }
                    },
                    data: arr
                }
            ]
        };
        chart.myChart = echarts.init(document.getElementById('type'));
        chart.myChart.setOption(option_pie);
        reSize(chart.myChart);
    }
    if (id == "sex_vip_num") {
        if(chart_data.sex_vip_num==""){
            $("#sex_vip_num").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#sex_vip_num").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var data = chart_data.sex_vip_num['sex_vip_num'];
        var arr=data;
        if(arr.length==0){
            $("#sex_vip_num").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#sex_vip_num").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr = [];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
            head+='<li title="'+arr[i].name+'">'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value="<ul><li>人数</li>"+value+"</ul>";
        $("#sex_vip_num").next().html(head+value);
        $("#sex_vip_num").next().find("li").css("width",($("#sex_vip_num").next().width()-10)/$(head).find("li").length+"px");
        var option_pie = {
            color: ['#2f4553','#c33531'],
            tooltip: {
                textStyle: {
                    fontSize: '10'
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: 'true',
                orient: 'vertical',
                x: 'left',
                y: 'center',
                data: Arr
            },
            series: [
                {
                    name: '性别比例',
                    center: ['60%', '50%'],
                    type: 'pie',
                    radius: ['50%', '60%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        normal: {
                            //label: {
                            //    show: false,
                            //    position: 'center'
                            //},
                            //labelLine: {
                            //    show: false
                            //}
                        },
                        emphasis: {
                            label: {
                                //show: true,
                                //textStyle: {
                                //    fontSize: '20',
                                //    fontWeight: 'bold'
                                //}
                            }
                        }
                    },
                    data: arr
                }
            ]
        };
        chart.myChart_sex = echarts.init(document.getElementById('sex_vip_num'));
        chart.myChart_sex.setOption(option_pie);
        reSize(chart.myChart_sex);
    }
    if (id == "series") {
        if(chart_data.series==""){
            $("#series").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#series").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var data = chart_data.series['Series'];
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:type=="people"?arr=data.size_array:arr=data.discount;
        if(arr.length==0){
            $("#series").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#series").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr = [];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
            head+='<li title="'+arr[i].name+'">'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#series").next().html(head+value);
        $("#series").next().find("li").css("width",($("#series").next().width()-10)/$(head).find("li").length+"px");
        var option_pie_series = {
            color: ['#44556c', '#4a5f7c', '#5475a2', '#5f8bc8', '#7c92ca', '#9999cc', '#6db0d3','#93d7e2', '#c1e7ed','#e5e8ea', '#41c7db'],
            tooltip: {
                textStyle: {
                    fontSize: '10'
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: 'true',
                orient: 'vertical',
                x: 'left',
                y: 'center',
                data: Arr
            },
            series: [
                {
                    name: '品牌分类',
                    center: ['60%', '50%'],
                    type: 'pie',
                    radius: ['50%', '60%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                                position: 'center'
                            },
                            labelLine: {
                                show: false
                            }
                        },
                        emphasis: {
                            label: {
                                show: true,
                                textStyle: {
                                    fontSize: '20',
                                    fontWeight: 'bold'
                                }
                            }
                        }
                    },
                    data: arr
                }
            ]
        };
        chart.myChart5 = echarts.init(document.getElementById('series'));
        chart.myChart5.setOption(option_pie_series);
        reSize(chart.myChart5);
    }
    if (id == "weeks") {
        if(chart_data.weeks==""){
            $("#weeks").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#weeks").next().html("<div style='font-size: 18px;padding-top:30px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var data = chart_data.weeks['Week'];
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#weeks").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#weeks").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr=[];
        var newArr=[];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].value);
            newArr.push(arr[i].value);
            head+='<li title="'+arr[i].name+'">'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#weeks").next().html(head+value);
        $("#weeks").next().find("li").css("width",($("#weeks").next().width()-10)/$(head).find("li").length+"px");
        var option_week_radar = {
            color: ['#A7DADE'],
            axis: {
                areaStyle: {
                    color: ['red']
                }
            },
            tooltip: {
                trigger: 'axis',
                confine:true
            },
            polar: [
                {
                    indicator: [
                        {text: '周一', max: 100},
                        {text: '周二', max: 100},
                        {text: '周三', max: 100},
                        {text: '周四', max: 100},
                        {text: '周五', max: 100},
                        {text: '周六', max: 100},
                        {text: '周日', max: 100}
                    ],
                    radius: 100,
                    splitNumber: 7,
                    startAngle: 68,
                    splitArea: {
                        show: true,
                        areaStyle: {
                            color: '#fff'
                        }
                    }
                }
            ],
            series: [
                {
                    symbol: 'circle',
                    type: 'radar',
                    tooltip: {
                        trigger: 'item'
                    },
                    itemStyle: {normal: {areaStyle: {type: 'default'}}},
                    symbolSize: '0',
                    data: [
                        {
                            value: Arr,
                            name: '星期偏好'
                        }
                    ]
                }
            ]
        };
        newArr.sort(compare);
        for(var j=0;j<option_week_radar.polar[0].indicator.length;j++){
            option_week_radar.polar[0].indicator[j].max=newArr[newArr.length-1]*1.1==0?"10":newArr[newArr.length-1]*1.1;
        }
        chart.myChart1 = echarts.init(document.getElementById('weeks'));
        chart.myChart1.setOption(option_week_radar);
        reSize(chart.myChart1);
    }
    if (id == "price") {
        if(chart_data.price==""){
            $("#price").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#price").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var data=chart_data.price['Price'];
        var arr=[];
        var dataZoom=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#price").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#price").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var ArrName=[];
        var ArrValue=[];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            ArrName.push(arr[i].name);
            ArrValue.push(arr[i].value);
            head+='<li title="'+arr[i].name+'">'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        if(ArrName.length>8){
          dataZoom=[
                {
                    type: 'slider',
                    show: true,
                    handleSize: 8,
                    width:15,
                    yAxisIndex: [0],
                    right:"20",
                    start:100,
                    end: 50
                },
                {
                    type: 'inside',
                    yAxisIndex: [0],
                    start: 0,
                    end: 100
                }
            ]
        }
        ArrName.reverse();
        ArrValue.reverse();
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#price").next().html(head+value);
        $("#price").next().find("li").css("width",($("#price").next().width()-10)/$(head).find("li").length+"px");
        chart.myChart2 = echarts.init(document.getElementById('price'));
        var option_bar = {
            color: ['#6CC1C8'],
            tooltip: {
                trigger: 'item',
                formatter : function (params) {
                    return params.seriesName+"<br/>"+params.name + ' : '+params.value;
                }
            },
            grid:{
                borderWidth:0,
                x:'100',
                y:'20',
                x2:'40',
                y2:'30'
            },
            xAxis: [

                {
                    show: false,
                    type: 'value',
                    boundaryGap: [0, 0.01]

                }
            ],
            yAxis: [
                {
                    axisLine: {
                        show: false
                    },
                    axisTick: {
                        show: false
                    },
                    splitLine: {
                        show: false
                    },
                    type: 'category',
                    data: ArrName
                }
            ],
            dataZoom: dataZoom,
            series: [
                {
                    itemStyle: {
                        normal: {
                            barBorderRadius: 0
                        }
                    },
                    name: '价格偏好',
                    type: 'bar',
                    barWidth: 10,
                    data: ArrValue
                }
            ]
        };
        chart.myChart2.setOption(option_bar);
        reSize(chart.myChart2);
    }
    if (id == "age") {
        if(chart_data.age==""){
            $("#age").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#age").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var data=chart_data.age['age'];
        var arr=[];
        var dataZoom=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#age").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#age").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var ArrName=[];
        var ArrValue=[];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            ArrName.push(arr[i].name);
            ArrValue.push(arr[i].value);
            head+='<li title="'+arr[i].name+'">'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        if(ArrName.length>8){
          dataZoom=[
                {
                    type: 'slider',
                    show: true,
                    handleSize: 8,
                    width:15,
                    yAxisIndex: [0],
                    right:"20",
                    start:100,
                    end: 50
                },
                {
                    type: 'inside',
                    yAxisIndex: [0],
                    start: 0,
                    end: 100
                }
            ]
        }
        ArrName.reverse();
        ArrValue.reverse();
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#age").next().html(head+value);
        $("#age").next().find("li").css("width",($("#age").next().width()-10)/$(head).find("li").length+"px");
        chart.myChartAge = echarts.init(document.getElementById('age'));
        var option_bar = {
            color: ['#6CC1C8'],
            tooltip: {
                trigger: 'item',
                formatter : function (params) {
                    return params.seriesName+"<br/>"+params.name + ' : '+params.value;
                }
            },
            grid:{
                borderWidth:0,
                x:'100',
                y:'20',
                x2:'40',
                y2:'30'
            },
            xAxis: [

                {
                    show: false,
                    type: 'value',
                    boundaryGap: [0, 0.01]

                }
            ],
            yAxis: [
                {
                    axisLine: {
                        show: false
                    },
                    axisTick: {
                        show: false
                    },
                    splitLine: {
                        show: false
                    },
                    type: 'category',
                    data: ArrName
                }
            ],
            dataZoom: dataZoom,
            series: [
                {
                    itemStyle: {
                        normal: {
                            barBorderRadius: 0
                        }
                    },
                    name: '年龄偏好',
                    type: 'bar',
                    barWidth: 10,
                    data: ArrValue
                }
            ]
        };
        chart.myChartAge.setOption(option_bar);
        reSize(chart.myChartAge);
    }
    if (id == "cardType") {
        if(chart_data.cardType==""){
            $("#cardType").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#cardType").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var data=chart_data.cardType['cardType'];
        var arr=[];
        var dataZoom=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:type=="people"?arr=data.size_array:arr=data.discount;
        if(arr.length==0){
            $("#cardType").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#cardType").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var ArrName=[];
        var ArrValue=[];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            ArrName.push(arr[i].name);
            ArrValue.push(arr[i].value);
            head+='<li title="'+arr[i].name+'">'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        if(ArrName.length>8){
          dataZoom=[
                {
                    type: 'slider',
                    show: true,
                    handleSize: 8,
                    width:15,
                    yAxisIndex: [0],
                    right:"20",
                    start:100,
                    end: 50
                },
                {
                    type: 'inside',
                    yAxisIndex: [0],
                    start: 0,
                    end: 100
                }
            ]
        }
        ArrName.reverse();
        ArrValue.reverse();
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#cardType").next().html(head+value);
        $("#cardType").next().find("li").css("width",($("#cardType").next().width()-10)/$(head).find("li").length+"px");
        chart.myChartCardType = echarts.init(document.getElementById('cardType'));
        var option_bar = {
            color: ['#6CC1C8'],
            tooltip: {
                trigger: 'item',
                formatter : function (params) {
                    return params.seriesName+"<br/>"+params.name + ' : '+params.value;
                }
            },
            grid:{
                borderWidth:0,
                x:'100',
                y:'20',
                x2:'40',
                y2:'30'
            },
            xAxis: [

                {
                    show: false,
                    type: 'value',
                    boundaryGap: [0, 0.01]

                }
            ],
            yAxis: [
                {
                    axisLine: {
                        show: false
                    },
                    axisTick: {
                        show: false
                    },
                    splitLine: {
                        show: false
                    },
                    type: 'category',
                    data: ArrName
                }
            ],
            dataZoom: dataZoom,
            series: [
                {
                    itemStyle: {
                        normal: {
                            barBorderRadius: 0
                        }
                    },
                    name: '会员类型',
                    type: 'bar',
                    barWidth: 10,
                    data: ArrValue
                }
            ]
        };
        chart.myChartCardType.setOption(option_bar);
        reSize(chart.myChartCardType);
    }
    if (id == "month") {
        if(chart_data.month==""){
            $("#month").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#month").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var data=chart_data.month['Month'],
            arr=new Array();
        switch (type){
            case "amt":
                arr=data.trade_amt;
                break;
            case "num":
                arr=data.trade_num;
                break;
            case "discount":
                arr=data.discount;
                break;
            case "relate_rate":
                arr=data.relate_rate;
                break;
            case "trade_price":
                arr=data.trade_price;
                break;
            case "times":
                arr=data.stroke_num;
                break;
        }
        if(arr.length==0){
            $("#month").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#month").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr=[];
        var newArr=[];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].value);
            newArr.push(arr[i].value);
            head+='<li title="'+arr[i].name+'">'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        switch (type){
            case "amt":
                value="<ul><li>金额</li>"+value+"</ul>";
                break;
            case "num":
                value="<ul><li>件数</li>"+value+"</ul>";
                break;
            case "discount":
                value="<ul><li>金额</li>"+value+"</ul>";
                break;
            case "relate_rate":
                value="<ul><li>连带率</li>"+value+"</ul>";
                break;
            case "trade_price":
                value="<ul><li>客单价</li>"+value+"</ul>";
                break;
            case "times":
                value="<ul><li>次数</li>"+value+"</ul>";
                break;
        }
        $("#month").next().html(head+value);
        $("#month").next().find("li").css("width",($("#month").next().width()-10)/$(head).find("li").length+"px");
        var option_month_radar = {
            color: ['#A7DADE'],
            axis: {
                areaStyle: {
                    color: ['red']
                }
            },
            tooltip: {
                trigger: 'axis',
                confine:true
            },
            polar: [
                {
                    indicator: [
                        {text: '一月', max: 100},
                        {text: '二月', max: 100},
                        {text: '三月', max: 100},
                        {text: '四月', max: 100},
                        {text: '五月', max: 100},
                        {text: '六月', max: 100},
                        {text: '七月', max: 100},
                        {text: '八月', max: 100},
                        {text: '九月', max: 100},
                        {text: '十月', max: 100},
                        {text: '十一月', max: 100},
                        {text: '十二月', max: 100}
                    ],
                    radius: 100,
                    splitNumber: 8,
                    startAngle: 45,
                    splitArea: {
                        show: true,
                        areaStyle: {
                            color: '#fff'
                        }
                    }
                }
            ],
            series: [
                {
                    symbol: 'circle',
                    type: 'radar',
                    tooltip: {
                        trigger: 'item'
                    },
                    itemStyle: {normal: {areaStyle: {type: 'default'}}},
                    symbolSize: '0',
                    data: [
                        {
                            value: Arr,
                            name: '月份偏好'
                        }
                    ]
                }
            ]
        };
        newArr.sort(compare);
        for(var j=0;j<option_month_radar.polar[0].indicator.length;j++){
            option_month_radar.polar[0].indicator[j].max=newArr[newArr.length-1]*1.1==0?"10":newArr[newArr.length-1]*1.1;
        }
        chart.myChart4 = echarts.init(document.getElementById('month'));
        chart.myChart4.setOption(option_month_radar);
        reSize(chart.myChart4);
    }
    if (id == "birth_vip_num") {
        if(chart_data.birth_vip_num==""){
            $("#birth_vip_num").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#birth_vip_num").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var data=chart_data.birth_vip_num['birth_vip_num'],
            arr=data;
        if(arr.length==0){
            $("#birth_vip_num").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#birth_vip_num").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr=[];
        var newArr=[];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].value);
            newArr.push(arr[i].value);
            head+='<li title="'+arr[i].name+'">'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value="<ul><li>人数</li>"+value+"</ul>";
        $("#birth_vip_num").next().html(head+value);
        $("#birth_vip_num").next().find("li").css("width",($("#birth_vip_num").next().width()-10)/$(head).find("li").length+"px");
        var option_month_radar = {
            color: ['#A7DADE'],
            axis: {
                areaStyle: {
                    color: ['red']
                }
            },
            tooltip: {
                trigger: 'axis',
                confine:true
            },
            polar: [
                {
                    indicator: [
                        {text: '一月', max: 100},
                        {text: '二月', max: 100},
                        {text: '三月', max: 100},
                        {text: '四月', max: 100},
                        {text: '五月', max: 100},
                        {text: '六月', max: 100},
                        {text: '七月', max: 100},
                        {text: '八月', max: 100},
                        {text: '九月', max: 100},
                        {text: '十月', max: 100},
                        {text: '十一月', max: 100},
                        {text: '十二月', max: 100}
                    ],
                    radius: 100,
                    splitNumber: 8,
                    startAngle: 45,
                    splitArea: {
                        show: true,
                        areaStyle: {
                            color: '#fff'
                        }
                    }
                }
            ],
            series: [
                {
                    symbol: 'circle',
                    type: 'radar',
                    tooltip: {
                        trigger: 'item'
                    },
                    itemStyle: {normal: {areaStyle: {type: 'default'}}},
                    symbolSize: '0',
                    data: [
                        {
                            value: Arr,
                            name: '月份偏好'
                        }
                    ]
                }
            ]
        };
        newArr.sort(compare);
        for(var j=0;j<option_month_radar.polar[0].indicator.length;j++){
            option_month_radar.polar[0].indicator[j].max=newArr[newArr.length-1]*1.1==0?"10":newArr[newArr.length-1]*1.1;
        }
        option_month_radar.series[0].data[0].name="生日分布";
        chart.myChart_birth_vip_num = echarts.init(document.getElementById('birth_vip_num'));
        chart.myChart_birth_vip_num.setOption(option_month_radar);
        reSize(chart.myChart_birth_vip_num);
    }
    if (id == "season") {
        if(chart_data.season==""){
            $("#season").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#season").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var data = chart_data.season['Season'];
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#season").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#season").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }else {
            var head="";
            var value="";
            for(var a=0;a<arr.length;a++){
                head+='<li title="'+arr[i].name+'">'+arr[a].name+'</li>';
                value+='<li style="word-break: break-all" title="'+arr[a].value+'">'+arr[a].value+'</li>';
            }
            head="<ul><li></li>"+head+"</ul>";
            value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
            $("#season").next().html(head+value);
            $("#season").next().find("li").css("width",($("#season").next().width()-10)/$(head).find("li").length+"px")
        }
        var Arr = [];
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
        }
        var option_pie_season = {
            color: ['#44556c', '#4a5f7c', '#5475a2', '#5f8bc8', '#7c92ca', '#9999cc', '#6db0d3','#93d7e2', '#c1e7ed','#e5e8ea', '#41c7db'],
            tooltip: {
                textStyle: {
                    fontSize: '10',
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: 'true',
                orient: 'vertical',
                x: 'left',
                y: 'center',
                data: Arr
            },
            series: [
                {
                    name: '季节分类',
                    center: ['60%', '50%'],
                    type: 'pie',
                    radius: ['50%', '60%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                                position: 'center'
                            },
                            labelLine: {
                                show: false
                            }
                        },
                        emphasis: {
                            label: {
                                show: true,
                                textStyle: {
                                    fontSize: '20',
                                    fontWeight: 'bold'
                                }
                            }
                        }
                    },
                    data: arr
                }
            ]
        };
        chart.myChart3 = echarts.init(document.getElementById('season'));
        chart.myChart3.setOption(option_pie_season);
        reSize(chart.myChart3);
    }
    if (id == "area") {
        if(chart_data.area==""){
            $("#area").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#area").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var data=chart_data.area['Area'];
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#area").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#area").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        chart.myChart6 = echarts.init(document.getElementById('area'));
        var valArr=[];
        for(var i=0;i<arr.length;i++){
            arr[i].name=arr[i].name.replace(/省|市/,"");
            if(arr[i].name=="宁夏回族自治区"){
                arr[i].name="宁夏"
            }
            if(arr[i].name=="内蒙古自治区"){
                arr[i].name="内蒙古"
            }
            if(arr[i].name=="新疆维吾尔自治区"){
                arr[i].name="新疆"
            }
            if(arr[i].name=="西藏自治区"){
                arr[i].name="西藏"
            }
            if(arr[i].name=="广西壮族自治区"){
                arr[i].name="广西"
            }
            valArr.push(arr[i].value)
        }
        valArr.sort(function(a,b){return a-b});
        var option_map = {
            tooltip: {
                trigger: 'item'
            },
            dataRange: {
                itemWidth: 5,
                itemGap: 0.2,
                color: ['#3C95A2', '#A7CFD5'],
                splitNumber: '20',
                orient: 'horizontal',
                min: 0,
                max: Number(valArr[valArr.length-1]<10?10:valArr[valArr.length-1]),
                x: 'left',
                y: 'top',
                text: ['高','低']      // 文本，默认为数值文本
            },
            series: [
                {
                    name: '地域偏好',
                    type: 'map',
                    mapType: 'china',
                    roam: false,
                    label: {
                        normal: {
                            show: false
                        },
                        emphasis: {
                            show: true
                        }
                    },
                    itemStyle: {
                        normal: {
                            label: {
                                show: true, textStyle: {
                                    color: "#434960",
                                    fontSize: 10
                                }
                            }
                        },
                        emphasis: {label: {show: true}}
                    },
                    data:arr
                }
            ]
        };
        chart.myChart6.setOption(option_map);
        reSize(chart.myChart6);
    }
    if (id == "typeMid") {
        if(chart_data.typeMid==""){
            $("#typeMid").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#typeMid").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var data = chart_data.typeMid['TypeMid'];
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#typeMid").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#typeMid").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr = [];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
            head+='<li title="'+arr[i].name+'">'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#typeMid").next().html(head+value);
        $("#typeMid").next().find("li").css("width",($("#typeMid").next().width()-10)/$(head).find("li").length+"px");
        var option_pie_series = {
            color: ['#44556c', '#4a5f7c', '#5475a2', '#5f8bc8', '#7c92ca', '#9999cc', '#6db0d3','#93d7e2', '#c1e7ed','#e5e8ea', '#41c7db'],
            tooltip: {
                textStyle: {
                    fontSize: '10',
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: 'true',
                orient: 'vertical',
                x: 'left',
                y: 'center',
                data: Arr
            },
            series: [
                {
                    name: '消费分类',
                    center: ['60%', '50%'],
                    type: 'pie',
                    radius: ['50%', '60%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                                position: 'center'
                            },
                            labelLine: {
                                show: false
                            }
                        },
                        emphasis: {
                            label: {
                                show: true,
                                textStyle: {
                                    fontSize: '20',
                                    fontWeight: 'bold'
                                }
                            }
                        }
                    },
                    data: arr
                }
            ]
        };
        chart.chartMId = echarts.init(document.getElementById('typeMid'));
        chart.chartMId.setOption(option_pie_series);
        reSize(chart.chartMId);
    }
    if (id == "typeSm") {
        if(chart_data.typeSm==""){
            $("#typeSm").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#typeSm").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var data = chart_data.typeSm['TypeSm'];
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#typeSm").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#typeSm").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr = [];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
            head+='<li title="'+arr[i].name+'">'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#typeSm").next().html(head+value);
        $("#typeSm").next().find("li").css("width",($("#typeSm").next().width()-10)/$(head).find("li").length+"px");
        var option_pie_series = {
            color: ['#44556c', '#4a5f7c', '#5475a2', '#5f8bc8', '#7c92ca', '#9999cc', '#6db0d3','#93d7e2', '#c1e7ed','#e5e8ea', '#41c7db'],
            tooltip: {
                textStyle: {
                    fontSize: '10',
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: 'true',
                orient: 'vertical',
                x: 'left',
                y: 'center',
                data: Arr
            },
            series: [
                {
                    name: '消费分类',
                    center: ['60%', '50%'],
                    type: 'pie',
                    radius: ['50%', '60%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                                position: 'center'
                            },
                            labelLine: {
                                show: false
                            }
                        },
                        emphasis: {
                            label: {
                                show: true,
                                textStyle: {
                                    fontSize: '20',
                                    fontWeight: 'bold'
                                }
                            }
                        }
                    },
                    data: arr
                }
            ]
        };
        chart.chartSm = echarts.init(document.getElementById('typeSm'));
        chart.chartSm.setOption(option_pie_series);
        reSize(chart.chartSm);
    }
    if (id == "typeSeries") {
        if(chart_data.typeSeries==""){
            $("#typeSm").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#typeSm").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
        }
        var data = chart_data.typeSeries['TypeSeries'];
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:arr=data.discount;
        if(arr.length==0){
            $("#typeSeries").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#typeSeries").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr = [];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
            head+='<li title="'+arr[i].name+'">'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#typeSeries").next().html(head+value);
        $("#typeSeries").next().find("li").css("width",($("#typeSeries").next().width()-10)/$(head).find("li").length+"px");
        var option_pie_series = {
            color: ['#44556c', '#4a5f7c', '#5475a2', '#5f8bc8', '#7c92ca', '#9999cc', '#6db0d3','#93d7e2', '#c1e7ed','#e5e8ea', '#41c7db'],
            tooltip: {
                textStyle: {
                    fontSize: '10',
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: 'true',
                orient: 'vertical',
                x: 'left',
                y: 'center',
                data: Arr
            },
            series: [
                {
                    name: '消费分类',
                    center: ['60%', '50%'],
                    type: 'pie',
                    radius: ['50%', '60%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                                position: 'center'
                            },
                            labelLine: {
                                show: false
                            }
                        },
                        emphasis: {
                            label: {
                                show: true,
                                textStyle: {
                                    fontSize: '20',
                                    fontWeight: 'bold'
                                }
                            }
                        }
                    },
                    data: arr
                }
            ]
        };
        chart.chartSeries = echarts.init(document.getElementById('typeSeries'));
        chart.chartSeries.setOption(option_pie_series);
        reSize(chart.chartSeries);
    }
    if (id == "source") {
        if(chart_data.source==""){
            $("#source").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#source").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var data = chart_data.source['source'];
        var arr=[];
        type=="amt"?arr=data.trade_amt:type=="num"?arr=data.trade_num:type=="people"?arr=data.size_array:arr=data.discount;
        if(arr.length==0){
            $("#source").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#source").next().html("<div style='font-size: 18px;padding-top:30px;color: #666;text-align: center'>暂无数据</div>");
            return
        }
        var Arr = [];
        var head="";
        var value="";
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
            head+='<li title="'+arr[i].name+'">'+arr[i].name+'</li>';
            value+='<li style="word-break: break-all" title="'+arr[i].value+'">'+arr[i].value+'</li>';
        }
        head="<ul><li></li>"+head+"</ul>";
        value=type=="amt"?"<ul><li>金额</li>"+value+"</ul>":type=="num"?"<ul><li>件数</li>"+value+"</ul>":type=="people"?"<ul><li>人数</li>"+value+"</ul>":"<ul><li>折扣</li>"+value+"</ul>";
        $("#source").next().html(head+value);
        $("#source").next().find("li").css("width",($("#source").next().width()-10)/$(head).find("li").length+"px");
        var option_pie = {
            color: ['#44556c', '#4a5f7c', '#5475a2', '#5f8bc8', '#7c92ca', '#9999cc', '#6db0d3','#93d7e2', '#c1e7ed','#e5e8ea', '#41c7db'],
            tooltip: {
                textStyle: {
                    fontSize: '10'
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show: 'true',
                orient: 'vertical',
                x: 'left',
                y: 'center',
                data: Arr
            },
            series: [
                {
                    name: '渠道偏好',
                    center: ['60%', '50%'],
                    type: 'pie',
                    radius: ['50%', '60%'],
                    avoidLabelOverlap: false,
                    itemStyle: {
                        normal: {
                            label: {
                                show: false,
                                position: 'center'
                            },
                            labelLine: {
                                show: false
                            }
                        },
                        emphasis: {
                            label: {
                                show: true,
                                textStyle: {
                                    fontSize: '20',
                                    fontWeight: 'bold'
                                }
                            }
                        }
                    },
                    data: arr
                }
            ]
        };
        chart.myChart_source = echarts.init(document.getElementById('source'));
        chart.myChart_source.setOption(option_pie);
        reSize(chart.myChart_source);
    }
}
function reSize(chart) {
    window.addEventListener("resize", function () {
        chart.resize();
    });
}
//图表数据
function chartData(lable){
    // $("#chart_shadow").show();
    $("#"+lable).empty();
    $("#"+lable).next(".data_table").empty();
    $("#"+lable).nextAll(".right_shadow").show();
    var param={};
    param.store_label=lable;
    param["type"]="analysis";
    param['corp_code'] = localStorage.getItem('corp_code');
    param["area_code"] = $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    param['brand_code'] = $($('#side_analyze ul li:nth-child(1) s')[0]).attr('brand_code');
    param['vip_group_code'] = $($('#side_analyze ul li:nth-child(4) s')[0]).attr('group_code');
    param['store_code'] = $($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    //param['source'] = $($('#side_analyze ul li:nth-child(5) s')[0]).attr('data-type');
    param['year'] = $("#chart_year_input").val()=='全部'?'':$("#chart_year_input").val();
    oc.postRequire("post","/vipAnalysis/vipChart","0",param,function (data) {
        if(data.code == 0){
            var msg = data.message==""?"":JSON.parse(data.message);
            chart_data[lable]= msg==""?"":msg;
            //chartShow();
             init_chart(lable,"num");
            $("#"+lable).nextAll(".right_shadow").hide();
            // $("#chart_shadow").hide();
        }else if(data.code == -1){
            $("#chart_shadow").hide();
        }
    });
}
//图表数据导出
$(".chart_head .icon-ishopwebicon_6-24").click(function(){
    var arr=whir.loading.getPageSize();
    var self=$(this);
    var id=self.parents(".chart_module").attr("data-id");
    var data="";
    switch (id){
        case "type":
            data=chart_data.type['Type'];
            break;
        case "weeks":
            data=chart_data.weeks['Week'];
            break;
        case "price":
            data=chart_data.price['Price'];
            break;
        case "month":
            data=chart_data.month['Month'];
            break;
        case "season":
            data=chart_data.season['Season'];
            break;
        case "typeMid":
            data=chart_data.typeMid['TypeMid'];
            break;
        case "typeSm":
            data=chart_data.typeSm['TypeSm'];
            break;
        case "typeSeries":
            data=chart_data.typeSeries['TypeSeries'];
            break;
        case "age":
            data=chart_data.typeSeries['age'];
            break;
        case "cardType":
            data=chart_data.typeSeries['cardType'];
            break;
        case "sex_vip_num":
            data=chart_data.sex_vip_num;
            break;
        case "birth_vip_num":
            data=chart_data.birth_vip_num;
            break;
        case "source":
            data=chart_data.source["source"];
            break;
    }
    if(data==undefined){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            zIndex:10011,
            content: "暂无数据"
        });
        return
    }
    if( id!="sex_vip_num" && id!="birth_vip_num" && (data.trade_amt["length"]=="0" && data.trade_num["length"]=="0" &&data.discount["length"]=="0")){
        art.dialog({
            time: 1,
            lock: true,
            cancel: false,
            zIndex:10011,
            content: "暂无数据"
        });
        return
    }
    $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
    $("#p").show();
    $("#tk_export").show();
    $("#enter a").attr("data-src","");
    $("#enter a").removeAttr("href");
    $("#enter a").html("确认");
    $("#X_export").next("p").html("是否导出该图表数据?");
    $("#enter").unbind("click").bind("click",function(){
        if($("#enter a").attr("data-src")!=""){
            $("#tk_export").hide();
            $("#p").hide();
        }else{
            whir.loading.add("","0.5");
            oc.postRequire("post","/vipAnalysis/exportExecl2View","0",data,function (reg) {
                whir.loading.remove();
                if(reg.code=="0"){
                    var src=JSON.parse(reg.message);
                    var path=src.path;
                    path=path.substring(1,path.length-1);
                    $("#X_export").next("p").html("是否下载文件?");
                    $("#enter a").attr("href","/"+path);
                    $("#enter a").attr("data-src",src.path);
                    $("#enter a").html("下载文件");
                }else if(reg.code=="-1"){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        zIndex:10011,
                        content: reg.message
                    });
                }
            })
        }
    });
    $("#X_export,#cancel_export").click(function(){
        $("#tk_export").hide();
        $("#enter a").attr("data-src","");
        $("#enter a").removeAttr("href");
        $("#enter a").html("确认");
        $("#X_export p").html("是否导出该图表数据?");
        $("#p").hide();
    })
});
/******************************表格分析数据*************************************************************************/
// 列表点击效果
$(".vip_nav_bar li:nth-child(1)").click(function () {
    $('.birthVip').show();
    $(".newVip").hide();
    $(".activeVip").hide();
    $(".rank").hide();
    $(".freq").hide();
    $("#page_row").val("10行/页");
    brithVipGet();
    jump = 1;
    //点击时，将第一个按钮设置class为btn_bg
    $($(".date_btn span")[0]).css({"color": "#fff", "background": "#6cc1c8"});
    //取消其他的class;
    var lis = $($(".date_btn span")[0]).parent().nextAll();
    for (var i = 0; i < lis.length; i++) {
        $(lis[i]).find('span').css({"color": "", "background": ""});
    }
});
$(".vip_nav_bar li:nth-child(2)").click(function () {
    $('.birthVip').hide();
    $(".newVip").show();
    $(".activeVip").hide();
    $(".rank").hide();
    $(".freq").hide();
    $("#page_row").val("10行/页");
    newVipGet();
    jump = 2;
    $($(".new_btn span")[0]).css({"color": "#fff", "background": "#6cc1c8"});
    var lis = $($(".new_btn span")[0]).parent().nextAll();
    for (var i = 0; i < lis.length; i++) {
        $(lis[i]).find('span').css({"color": "", "background": ""});
    }
});
$(".vip_nav_bar li:nth-child(3)").click(function () {
    $('.birthVip').hide();
    $(".newVip").hide();
    $(".activeVip").show();
    $(".rank").hide();
    $(".freq").hide();
    $("#page_row").val("10行/页");
    vm.slide = [0,3];
    getVipSleepRate().then(function () {
        sleepVipGet();
    });
    jump = 3;
    //点击时，将第一个按钮设置class为btn_bg
    $($(".month_btn span")[0]).css({"color": "#fff", "background": "#6cc1c8"});
    //取消其他的class;
    var lis = $($(".month_btn span")[0]).parent().nextAll();
    for (var i = 0; i < lis.length; i++) {
        $(lis[i]).find('span').css({"color": "", "background": ""});
    }
});
$(".vip_nav_bar li:nth-child(4)").click(function () {
    $('.birthVip').hide();
    $(".newVip").hide();
    $(".activeVip").hide();
    $(".rank").show();
    $(".freq").hide();
    $("#page_row").val("10行/页");
    consumeVipGet();
    jump = 4;
    //点击时，将第一个按钮设置class为btn_bg
    $($(".rank .month_btn span")[0]).css({"color": "#fff", "background": "#6cc1c8"});
    //取消其他的class;
    var lis = $($(".rank .month_btn span")[0]).parent().nextAll();
    for (var i = 0; i < lis.length; i++) {
        $(lis[i]).find('span').css({"color": "", "background": ""});
    }
});
$(".vip_nav_bar li:nth-child(5)").click(function () {
    $('.birthVip').hide();
    $(".newVip").hide();
    $(".activeVip").hide();
    $(".rank").hide();
    $(".freq").show();
    $("#page_row").val("10行/页");
    consumefreq();
    jump = 5;
    //点击时，将第一个按钮设置class为btn_bg
    $($(".freq .new_btn span")[0]).css({"color": "#fff", "background": "#6cc1c8"});
    //取消其他的class;
    var lis = $($(".freq .new_btn span")[0]).parent().nextAll();
    for (var i = 0; i < lis.length; i++) {
        $(lis[i]).find('span').css({"color": "", "background": ""});
    }
});
$(".vip_nav_bar li").click(function () {
    $(this).addClass("liactive");
    $(this).siblings().removeClass("liactive");
});
$(".date_btn span").click(function () {
    $(this).css({"color": "#fff", "background": "#6cc1c8"});
    $(this).parent("li").siblings().children("span").css({"color": "", "background": ""});
});
$(".new_btn span").click(function () {
    $(this).css({"color": "#fff", "background": "#6cc1c8"});
    $(this).parent("li").siblings().children("span").css({"color": "", "background": ""});
});
$(".month_btn span").click(function () {
    $(this).css({"color": "#fff", "background": "#6cc1c8"});
    $(this).parent("li").siblings().children("span").css({"color": "", "background": ""});
});
$(".more_data").click(function () {
    sendMsgVal();
    vipTable_lg();
});
//加载更多页面放大
function vipTable_lg() {
    $("#title").children().eq("2").after("<span class='icon-ishop_8-03'></span><span>会员分析详情</span>");
    $("#table_analyze").addClass("vip_table_lg");
    $(".more_data").hide();
    $("#side_analyze").hide();
    $("#chart_container").hide();
    $("html").css("background", "#e8e8e8");
    $("#table_analyze").css("background", "#e8e8e8");
    $(".vip_table_lg .icon-ishop_8-03").hide();
    $(".foot").show();
    $("#side_bar").show();
    $(".vip_table_lg .right_shadow").css({"position":"fixed"});
}
//点击标题返回
$("#vipAnalyze_return").click(function () {
    $("#title span#vipAnalyze_return").nextAll().remove();
    $("#table_analyze").removeClass("vip_table_lg");
    $(".more_data").show();
    $("#chart_container").show();
    $("#side_analyze").show();
    $("#table_analyze").css("background", "#fff");
    $(".foot").hide();
    $('.vip_analyze_operate').hide();
    $(".newVip .vip_table tbody tr").each(function (i) {
        if (i > 9) {
            $(this).hide();
        }
    });
    $(".birthVip .vip_table tbody tr").each(function (i) {
        if (i > 9) {
            $(this).hide();
        }
    });
    $(".activeVip .vip_table tbody tr").each(function (i) {
        if (i > 9) {
            $(this).hide();
        }
    });
    $(".rank .vip_table tbody tr").each(function (i) {
        if (i > 9) {
            $(this).hide();
        }
    });
    $(".right_shadow").css({"position":"absolute"});
});
/******************生日会员****************************/
function brithVipGet() {
    if ($("#start_analyze_right").css("display") == "block") {
        return;
    }
    $("#right_shadow").show();//加载等待框
    var type = 'birth';
    var param = {};
    var pageIndex = arguments[0] ? arguments[0] : page;
    var month_type = arguments[2] ? arguments[2] : 'today';
    var pageSize = arguments[1] ? arguments[1] : 10;
    param['pageNumber'] = pageIndex;
    param['pageSize'] = pageSize;
    param['query_type'] = month_type;
    param['store_code'] = $($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    param['corp_code'] = localStorage.getItem('corp_code');
    param["area_code"] = $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    param['brand_code'] = $($('#side_analyze ul li:nth-child(1) s')[0]).attr('brand_code');
    param['group_code'] = $($('#side_analyze ul li:nth-child(4) s')[0]).attr('group_code');
    //param['source'] = $($('#side_analyze ul li:nth-child(5) s')[0]).attr('data-type');
    oc.postRequire("post", "/vipAnalysis/vipBirth", "", param, function (data) {
        if (data.code == "0") {
            $('.birthVip .vip_table tbody').empty();
            var msg = JSON.parse(data.message);
            $("#birthCount").text(msg.count);
            count = msg.pages;
            var pageIndex = msg.pageNum;
            msg = msg.birthday_vip_list;
            if (msg.length == 0) {
                var len = $(".birthVip thead tr th").length;
                var len2=Math.ceil(len/2);
                var i;
                for (i = 0; i < 10; i++) {
                    $(".birthVip tbody").append("<tr></tr>");
                    if(i==4){
                        for (var j = 0; j < len; j++) {
                            if(j==0){
                                $($(".birthVip tbody tr")[i]).append("<td colspan='"+len+"'><span style='line-height:45px;font-size: 18px;color:#999'>暂无数据</span></td>");
                            }else{
                                $($(".birthVip tbody tr")[i]).append("<td></td>");
                            }
                        }
                    }else {
                        for (var j = 0; j < len; j++) {
                            $($(".birthVip tbody tr")[i]).append("<td></td>");
                        }
                    }
                }
            }
            if (msg.length > 0) {
                for (var i = 0; i < msg.length; i++) {
                    if (pageIndex >= 2) {
                        var a = i + 1 + (pageIndex - 1) * pageSize;
                    } else {
                        var a = i + 1;
                    }
                    $(".birthVip tbody").append('<tr id="' + msg[i].vip_id + '" data-code="'+msg[i].corp_code+'"><td class="first_th" style="width: 50px;text-align: left;"><div class="checkbox"><input  type="checkbox" name="test" class="check" id="checkboxTwoInput'
                        + i
                        + 1
                        + '"/><label for="checkboxTwoInput'
                        + i
                        + 1
                        + '"></label></div></td><td>'
                        + a
                        + '</td><td>'
                        + msg[i].vip_name
                        + '</td><td>'
                        + msg[i].vip_card_type
                        + '</td><td>'
                        + msg[i].vip_card_no
                        + '</td><td>'
                        + msg[i].mobile
                        + '</td><td>'
                        + formatCurrency(msg[i].amount)
                        + '</td><td>'
                        + formatCurrency(msg[i].trade_count)
                        + '</td><td>'
                        + msg[i].vip_birthday
                        + '</td></tr>');
                }
                $(".birthVip .vip_table tbody tr td:not(:first-child)").click(function () {
                    //vipTable_lg();
                    var ID = $(this).parent().attr("id");
                    var corpCode= $(this).parent().attr("data-code");
                    sessionStorage.setItem("id", ID);
                    sessionStorage.setItem("corp_code", corpCode);
                    window.open("http://" + window.location.host + "/navigation_bar.html?url=/vip/vip_data.html&func_code=F0010");
                })
            }
        } else if (data.code == "-1") {
            art.dialog({
                zIndex: 10010,
                time: 2,
                lock: true,
                cancel: false,
                content: "数据加载失败"
            });
        }
        $(".birthVip thead th:first-child input").removeAttr("checked");
        $("#right_shadow").hide();//移除加载框
        //调用生成页码
        setPage($('#table_analyze .foot .foot-num')[0], count, pageIndex, pageSize, type, month_type);
    });

}
function birthVipGet_sub(ali) {
    var ali = ali;//当前对象
    switch ($($(ali).html()).html()) {
        case '今日':
            month_type = 'today';
            brithVipGet('', '', month_type);
            break;
        case '本月':
            month_type = 'current_month';
            brithVipGet('', '', month_type);
            break;
        case '次月':
            month_type = 'next_month';
            brithVipGet('', '', month_type);
            break;
    }
}
/******************新VIP会员***************************/
//新VIP模块数据请求加载
function newVipGet() {
    if ($("#start_analyze_right").css("display") == "block") {
        return;
    }
    $("#right_shadow").show();//加载等待框
    var type = 'new';
    var param = {};
    var month_type = arguments[2] ? arguments[2] : 'daily';
    var pageSize = arguments[1] ? arguments[1] : 10;
    var pageIndex = arguments[0] ? arguments[0] : page;
    param['query_type'] = month_type;
    param['pageNumber'] = pageIndex;
    param['pageSize'] = pageSize;
    param['store_code'] = $($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    param['corp_code'] = localStorage.getItem('corp_code');
    param["area_code"] = $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    param['brand_code'] = $($('#side_analyze ul li:nth-child(1) s')[0]).attr('brand_code');
    param['group_code'] = $($('#side_analyze ul li:nth-child(4) s')[0]).attr('group_code');
    //param['source'] = $($('#side_analyze ul li:nth-child(5) s')[0]).attr('data-type');
    oc.postRequire("post", "/vipAnalysis/vipNew", "", param, function (data) {
        if (data.code == "0") {
            $('.newVip .vip_table tbody').empty();
            var msg = JSON.parse(data.message);
            count = msg.pages;
            var pageIndex = msg.pageNum;
            $("#newCount").text(msg.count);
            msg = msg.new_vip_list;
            if (msg.length) {
                for (var i = 0; i < msg.length; i++) {
                    if (pageIndex >= 2) {
                        var a = i + 1 + (pageIndex - 1) * pageSize;
                    } else {
                        var a = i + 1;
                    }
                    $(".newVip tbody").append('<tr id="' + msg[i].vip_id + '" data-code="'+msg[i].corp_code+'"><td class="first_th" style="width: 50px;text-align: left;"><div class="checkbox"><input  type="checkbox" name="test" class="check" id="checkboxnew'
                        + i
                        + 1
                        + '"/><label for="checkboxnew'
                        + i
                        + 1
                        + '"></label></div></td><td>'
                        + a
                        + '</td><td>'
                        + msg[i].vip_name
                        + '</td><td>'
                        + msg[i].vip_card_type
                        + '</td><td>'
                        + msg[i].vip_card_no
                        + '</td><td>'
                        + msg[i].mobile
                        + '</td><td title="'+msg[i].store_name+'"><span class="ellipsis">'
                        + msg[i].store_name
                        + '</span></td><td>'
                        + msg[i].join_date
                        + '</td><td>'
                        + msg[i].vip_birthday
                        + '</td></tr>');
                }
                $(".vip_table tbody tr td:not(:first-child)").click(function () {
                    //vipTable_lg();
                    var ID = $(this).parent().attr("id");
                    var corpCode= $(this).parent().attr("data-code");
                    sessionStorage.setItem("id", ID);
                    sessionStorage.setItem("corp_code", corpCode);
                    window.open("http://" + window.location.host + "/navigation_bar.html?url=/vip/vip_data.html&func_code=F0010");
                })
            } else if (msg.length == 0) {
                var len = $(".newVip thead tr th").length;
                var len2=Math.ceil(len/2);
                var i;
                for (i = 0; i < 10; i++) {
                    $(".newVip tbody").append("<tr></tr>");
                    if(i==4){
                        for (var j = 0; j < len; j++) {
                            if(j==0){
                                $($(".newVip tbody tr")[i]).append("<td colspan='"+len+"'><span style='line-height:45px;font-size: 18px;color:#999'>暂无数据</span></td>");
                            }else{
                                $($(".newVip tbody tr")[i]).append("<td></td>");
                            }
                        }
                    }else {
                        for (var j = 0; j < len; j++) {
                            $($(".newVip tbody tr")[i]).append("<td></td>");
                        }
                    }
                }
            }
        } else if (data.code == "-1") {
            art.dialog({
                zIndex: 10010,
                time: 2,
                lock: true,
                cancel: false,
                content: "数据加载失败"
            });
        }
        //调用生成页码
        $(".newVip thead th:first-child input").removeAttr("checked");
        setPage($('#table_analyze .foot .foot-num')[0], count, pageIndex, pageSize, type, month_type);
        $("#right_shadow").hide();//移除加载框
    });
}
function newVipGet_sub(ali) {
    var ali = ali;//当前对象
    switch ($($(ali).html()).html()) {
        case '今日':
            month_type = 'daily';
            newVipGet('', '', month_type);
            break;
        case '本周':
            month_type = 'weekly';
            newVipGet('', '', month_type);
            break;
        case '本月':
            month_type = 'monthly';
            newVipGet('', '', month_type);
            break;
    }
}
/******************活跃会员**************************/
//获取活跃会员占比
function getVipSleepRate(){
    var def=$.Deferred();
    if ($("#start_analyze_right").css("display") == "block") {
        return def;
    }
    var param = {};
    var query_type = arguments[2] ? arguments[2] : 0;
    var pageSize = arguments[1] ? arguments[1] : 10;
    var pageIndex = arguments[0] ? arguments[0] : page;
    param['pageNumber'] = pageIndex;
    param['pageSize'] = pageSize;
    param['query_type'] = query_type;
    param['store_code'] = $($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    param['corp_code'] = localStorage.getItem('corp_code');
    param["area_code"] = $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    param['brand_code'] = $($('#side_analyze ul li:nth-child(1) s')[0]).attr('brand_code');
    param['group_code'] = $($('#side_analyze ul li:nth-child(4) s')[0]).attr('group_code');
    //param['source'] = $($('#side_analyze ul li:nth-child(5) s')[0]).attr('data-type');
    $("#right_shadow").show();//加载等待框
    oc.postRequire("post", "/vipAnalysis/vipSleepRate", "", param, function (data) {
        if(data.code==0){
            var msg=JSON.parse(data.message);
            proportion_list = msg.proportion_list;
            var spans = $('.activeVip .month_btn span');
            $($('.activeVip .month_btn span')[0]).html('0-3个月' + proportion_list.active_vip_proportion + '%');
            $($('.activeVip .month_btn span')[1]).html('3-6个月' + proportion_list.three_vip_proportion + "%");
            $($('.activeVip .month_btn span')[2]).html('6-9个月' + proportion_list.six_vip_proportion + '%');
            $($('.activeVip .month_btn span')[3]).html('9-12个月' + proportion_list.nine_vip_proportion + "%");
            $($('.activeVip .month_btn span')[4]).html('12个月及以后' + proportion_list.year_vip_proportion + '%');
            $("#activeVipPer").attr("data-num",proportion_list.all_vip_proportion);
            def.resolve(proportion_list.all_vip_proportion);
        }else{
            art.dialog({
                time: 1,
                lock: true,
                cancel: false,
                content: "获取活跃会员占比失败"
            });
        }
    })
    return def;
}
//获取活跃用户
function sleepVipGet() {
    if ($("#start_analyze_right").css("display") == "block") {
        return;
    }
    $("#right_shadow").show();//加载等待框
    var arr = vm.slide.reduce(function (pre,cur) {
        cur = cur == 13 ? "": cur;
        var obj = pre+"-"+cur;
        return obj
    });//取得时间段
    var type = 'sleep';
    var param = {};
    var query_type = arguments[2] ? arguments[2] : 0;
    var pageSize = arguments[1] ? arguments[1] : 10;
    var pageIndex = arguments[0] ? arguments[0] : page;
    var time = query_type == "define" ? arr :"";
    param['pageNumber'] = pageIndex;
    param['pageSize'] = pageSize;
    param['query_type'] = query_type;
    param['time'] = time;
    param['store_code'] = $($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    param['corp_code'] = localStorage.getItem('corp_code');
    param["area_code"] = $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    param['brand_code'] = $($('#side_analyze ul li:nth-child(1) s')[0]).attr('brand_code');
    param['group_code'] = $($('#side_analyze ul li:nth-child(4) s')[0]).attr('group_code');
    //param['source'] = $($('#side_analyze ul li:nth-child(5) s')[0]).attr('data-type');
    oc.postRequire("post", "/vipAnalysis/vipSleep", "", param, function (data) {
        if (data.code == "0") {
            $('.activeVip .vip_table tbody').empty();
            var msg = JSON.parse(data.message);
            count = msg.pages;
            $("#activeVipCount").text(msg.count);
            var part = parseInt(msg.count);
            var all = $("#activeVipPer").attr("data-num");
            (all&&all!=0)?$("#activeVipPer").text((part/all*100).toFixed(2)):all==0?$("#activeVipPer").text(0):"";
            var pageIndex = msg.pageNum;
            msg = msg.sleep_vip_list;
            if (msg.length) {
                for (var i = 0; i < msg.length; i++) {
                    if (pageIndex >= 2) {
                        var a = i + 1 + (pageIndex - 1) * pageSize;
                    } else {
                        var a = i + 1;
                    }
                    $(".activeVip tbody").append('<tr id="' + msg[i].vip_id + '" data-code="'+msg[i].corp_code+'"><td class="first_th" style="width: 50px;text-align: left;"><div class="checkbox"><input  type="checkbox" name="test" class="check" id="checkboxactive'
                        + i
                        + 1
                        + '"/><label for="checkboxactive'
                        + i
                        + 1
                        + '"></label></div></td><td>'
                        + a
                        + '</td><td>'
                        + msg[i].vip_name
                        + '</td><td>'
                        + msg[i].vip_card_type
                        + '</td><td>'
                        + msg[i].vip_card_no
                        + '</td><td>'
                        + msg[i].mobile
                        + '</td><td>'
                        + formatCurrency(msg[i].amount)
                        + '</td><td>'
                        + formatCurrency(msg[i].consume_times)
                        + '</td><td>'
                        + msg[i].recently_consume_date
                        + '</td></tr>');
                }
                $(".activeVip .vip_table tbody tr td:not(:first-child)").click(function () {
                    var ID = $(this).parent().attr("id");
                    var corpCode= $(this).parent().attr("data-code");
                    sessionStorage.setItem("id", ID);
                    sessionStorage.setItem("corp_code", corpCode);
                    window.open("http://" + window.location.host + "/navigation_bar.html?url=/vip/vip_data.html&func_code=F0010");
                })
            } else if (msg.length == 0) {
                var len = $(".activeVip thead tr th").length;
                var len2=Math.ceil(len/2);
                var i;
                for (i = 0; i < 10; i++) {
                    $(".activeVip tbody").append("<tr></tr>");
                    //for (var j = 0; j < len; j++) {
                    //    $($(".activeVip tbody tr")[i]).append("<td></td>");
                    //}

                    if(i==4){
                        for (var j = 0; j < len; j++) {
                            if(j==0){
                                $($(".activeVip tbody tr")[i]).append("<td colspan='"+len+"'><span style='line-height:45px;font-size: 18px;color:#999'>暂无数据</span></td>");
                            }else{
                                $($(".activeVip tbody tr")[i]).append("<td></td>");
                            }
                        }
                    }else {
                        for (var j = 0; j < len; j++) {
                            $($(".activeVip tbody tr")[i]).append("<td></td>");
                        }
                    }
                }
            }
        } else if (data.code == "-1") {
            art.dialog({
                zIndex: 10010,
                time: 2,
                lock: true,
                cancel: false,
                content: "获取数据失败"
            });
        }
        $("#right_shadow").hide();//移除加载框
        $(".activeVip thead th:first-child input").removeAttr("checked");
        //调用生成页码
        setPage($('#table_analyze .foot .foot-num')[0], count, pageIndex, pageSize, type, query_type);
    });
}
function sleepVipGet_sub(ali) {
    switch (ali) {
        case 0 :
            query_type = 0;
            sleepVipGet('', '', query_type);
            break;
        case 1 :
            query_type = 1;
            sleepVipGet('', '', query_type);
            break;
        case 2 :
            query_type = 2;
            sleepVipGet('', '', query_type);
            break;
        case 3 :
            query_type = 3;
            sleepVipGet('', '', query_type);
            break;
        case 4 :
            query_type = 4;
            sleepVipGet('', '', query_type);
            break;
    }
}
/*************消费排行****************/
function consumeVipGet() {
    if ($("#start_analyze_right").css("display") == "block") {
        return;
    }
    $("#right_shadow").show();//加载等待框
    var type = 'consume';
    var param = {};
    var query_type = arguments[2] ? arguments[2] : "recent";
    var pageSize = arguments[1] ? arguments[1] : 10;
    var pageIndex = arguments[0] ? arguments[0] : page;
    param['pageNumber'] = pageIndex;
    param['pageSize'] = pageSize;
    param['query_type'] = query_type;
    param['store_code'] = $($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    param['corp_code'] = localStorage.getItem('corp_code');
    param["area_code"] = $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    param['brand_code'] = $($('#side_analyze ul li:nth-child(1) s')[0]).attr('brand_code');
    param['group_code'] = $($('#side_analyze ul li:nth-child(4) s')[0]).attr('group_code');
    //param['source'] = $($('#side_analyze ul li:nth-child(5) s')[0]).attr('data-type');
    oc.postRequire("post", "/vipAnalysis/vipConsume", "", param, function (data) {
        if (data.code == "0") {
            $('.rank .vip_table tbody').empty();
            $('.rank .vip_table thead').empty();
            var msg = JSON.parse(data.message);
            $("#rankCount").text(msg.count);
            count = msg.pages;
            var pageIndex = msg.pageNum;
            msg = msg.vip_consume_recently_list;
            $(".rank thead").append('<tr>'
                +'<th class="first_th" style="width:50px;text-align: left"><div class="checkbox">'
                + '<input onClick="if(this.checked==true) {checkAll(\'test\')} else { clearAll(\'test\')}" type="checkbox" name="test" class="check" id="checkboxTwoInputconsume"/>'
                + '<label for="checkboxTwoInputconsume"></label></div></th>'
                + '<th>序号</th>'
                + '<th>会员名称</th>'
                + '<th>会员类型</th>'
                + '<th>会员卡号</th>'
                + '<th>手机号</th>'
                + '<th>消费总额</th>'
                + '<th>最近消费日期</th></tr>');
            if (msg.length) {
                for (var i = 0; i < msg.length; i++) {
                    if (pageIndex >= 2) {
                        var a = i + 1 + (pageIndex - 1) * pageSize;
                    } else {
                        var a = i + 1;
                    }
                    $(".rank tbody").append('<tr id="' + msg[i].vip_id + '" data-code="'+msg[i].corp_code+'"><td class="first_th" style="width: 50px;text-align: left;"><div class="checkbox"><input  type="checkbox" name="test" class="check" id="checkboxconsume'
                        + i
                        + 1
                        + '"/><label for="checkboxconsume'
                        + i
                        + 1
                        + '"></label></div></td><td>'
                        + a
                        + '</td><td>'
                        + msg[i].vip_name
                        + '</td><td>'
                        + msg[i].vip_type
                        + '</td><td>'
                        + msg[i].vip_card_no
                        + '</td><td>'
                        + msg[i].mobile
                        + '</td><td>'
                        + formatCurrency(msg[i].amount)
                        + '</td><td>'
                        + msg[i].consume_time
                        + '</td></tr>');
                }
                $(".rank .vip_table tbody tr td:not(:first-child)").click(function () {
                    //vipTable_lg();
                    var ID = $(this).parent().attr("id");
                    var corpCode= $(this).parent().attr("data-code");
                    sessionStorage.setItem("id", ID);
                    sessionStorage.setItem("corp_code", corpCode);
                    window.open("http://" + window.location.host + "/navigation_bar.html?url=/vip/vip_data.html&func_code=F0010");
                })
            } else if (msg.length == 0) {
                var len = $(".rank thead tr th").length;
                var len2=Math.ceil(len/2);
                var i;
                for (i = 0; i < 10; i++) {
                    $(".rank tbody").append("<tr></tr>");
                    //for (var j = 0; j < len; j++) {
                    //    $($(".rank tbody tr")[i]).append("<td></td>");
                    //}

                    if(i==4){
                        for (var j = 0; j < len; j++) {
                            if(j==0){
                                $($(".rank tbody tr")[i]).append("<td colspan='"+len+"'><span style='line-height:45px;font-size: 18px;color:#999'>暂无数据</span></td>");
                            }else{
                                $($(".rank tbody tr")[i]).append("<td></td>");
                            }
                        }
                    }else {
                        for (var j = 0; j < len; j++) {
                            $($(".rank tbody tr")[i]).append("<td></td>");
                        }
                    }
                }
            }
        } else if (data.code == "-1") {
            art.dialog({
                zIndex: 10010,
                time: 2,
                lock: true,
                cancel: false,
                content: "数据加载失败"
            });
        }
        //调用生成页码
        $(".rank thead th:first-child input").removeAttr("checked");
        setPage($('#table_analyze .foot .foot-num')[0], count, pageIndex, pageSize, type, query_type);
        $("#right_shadow").hide();//移除加载框
    });
}
function consumeVipGetam() {
    if ($("#start_analyze_right").css("display") == "block") {
        return;
    }
    $("#right_shadow").show();//加载等待框
    var type = 'consume';
    var param = {};
    var query_type = arguments[2] ? arguments[2] : "recent";
    var pageSize = arguments[1] ? arguments[1] : 10;
    var pageIndex = arguments[0] ? arguments[0] : page;
    param['pageNumber'] = pageIndex;
    param['pageSize'] = pageSize;
    param['query_type'] = query_type;
    param['store_code'] = $($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    param['corp_code'] = localStorage.getItem('corp_code');
    param["area_code"] = $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    param['brand_code'] = $($('#side_analyze ul li:nth-child(1) s')[0]).attr('brand_code');
    param['group_code'] = $($('#side_analyze ul li:nth-child(4) s')[0]).attr('group_code');
    param['source'] = $($('#side_analyze ul li:nth-child(4) s')[0]).attr('data-type');
    oc.postRequire("post", "/vipAnalysis/vipConsume", "", param, function (data) {
        if (data.code == "0") {
            $('.rank .vip_table tbody').empty();
            $('.rank .vip_table thead').empty();
            var msg = JSON.parse(data.message);
            $("#rankCount").text(msg.count);
            count = msg.pages;
            var pageIndex = msg.pageNum;
            msg = msg.amount_list;
            $(".rank thead").append('<tr>'
                +'<th class="first_th" style="width:50px;text-align: left"><div class="checkbox">'
                + '<input onClick="if(this.checked==true) {checkAll(\'test\')} else { clearAll(\'test\')}" type="checkbox" name="test" class="check" id="checkboxTwoInputconsumeam"/>'
                + '<label for="checkboxTwoInputconsumeam"></label></div></th>'
                + '<th>序号</th>'
                + '<th>会员名称</th>'
                + '<th>会员类型</th>'
                + '<th>会员卡号</th>'
                + '<th>手机号</th>'
                + '<th>消费总额</th></tr>');
            if (msg.length > 0) {
                for (var i = 0; i < msg.length; i++) {
                    if (pageIndex >= 2) {
                        var a = i + 1 + (pageIndex - 1) * pageSize;
                    } else {
                        var a = i + 1;
                    }
                    $(".rank tbody").append('<tr id="' + msg[i].vip_id + '" data-code="'+msg[i].corp_code+'"><td class="first_th" style="width: 50px;text-align: left;"><div class="checkbox"><input  type="checkbox" name="test" class="check" id="checkboxconsumeam'
                        + i
                        + 1
                        + '"/><label for="checkboxconsumeam'
                        + i
                        + 1
                        + '"></label></div></td><td>'
                        + a
                        + '</td><td>'
                        + msg[i].vip_name
                        + '</td><td>'
                        + msg[i].vip_card_type
                        + '</td><td>'
                        + msg[i].vip_card_no
                        + '</td><td>'
                        + msg[i].mobile
                        + '</td><td>'
                        + formatCurrency(msg[i].amount)
                        + '</td></tr>');
                }
                $(".rank .vip_table tbody tr td:not(:first-child)").click(function () {
                    //vipTable_lg();
                    var ID = $(this).parent().attr("id");
                    var corpCode= $(this).parent().attr("data-code");
                    sessionStorage.setItem("id", ID);
                    sessionStorage.setItem("corp_code", corpCode);
                    window.open("http://" + window.location.host + "/navigation_bar.html?url=/vip/vip_data.html&func_code=F0010");
                })
            } else if (msg.length == 0) {
                var len = $(".rank thead tr th").length;
                var len2= Math.ceil(len/2);
                var i;
                for (i = 0; i < 10; i++) {
                    $(".rank tbody").append("<tr></tr>");
                    //for (var j = 0; j < len; j++) {
                    //    $($(".rank tbody tr")[i]).append("<td></td>");
                    //}

                    if(i==4){
                        for (var j = 0; j < len; j++) {
                            if(j==0){
                                $($(".rank tbody tr")[i]).append("<td colspan='"+len+"'><span style='line-height:45px;font-size: 18px;color:#999'>暂无数据</span></td>");
                            }else{
                                $($(".rank tbody tr")[i]).append("<td></td>");
                            }
                        }
                    }else {
                        for (var j = 0; j < len; j++) {
                            $($(".rank tbody tr")[i]).append("<td></td>");
                        }
                    }
                }
            }
        } else if (data.code == "-1") {
            art.dialog({
                zIndex: 10010,
                time: 2,
                lock: true,
                cancel: false,
                content: "数据加载失败"
            });
        }
        $(".rank thead th:first-child input").removeAttr("checked");
        //调用生成页码
        setPage($('#table_analyze .foot .foot-num')[0], count, pageIndex, pageSize, type, query_type);
        // $('.rank .vip_table tbody').html()?'':$('.rank .vip_table tbody').append('<span class="no_data'+'">暂无数据</span>');
        // pageShow($('.rank .vip_table tbody'));
        $("#right_shadow").hide();//移除加载框
    });
}
function consumeVipGet_sub(ali) {
    var ali = ali;//当前对象
    query_type = '';//创建活跃会员的标签请求
    month_type = '';//会员生日月份类型
    switch ($($(ali).html()).html()) {
        case '最近消费':
            query_type = "recent";
            consumeVipGet('', '', query_type);
            break;
        case '本月消费':
            query_type = "month";
            consumeVipGetam('', '', query_type);
            break;
        case '近三月消费':
            query_type = "three_month";
            consumeVipGetam('', '', query_type);
            break;
        case '历史总额':
            query_type = "history";
            consumeVipGetam('', '', query_type);
            break;
    }
}
/*************消费频率****************/
function consumefreq() {
    if ($("#start_analyze_right").css("display") == "block") {
        return;
    }
    $("#right_shadow").show();//加载等待框
    var type = 'freq';
    var param = {};
    var query_type = arguments[2] ? arguments[2] : "three";
    var pageSize = arguments[1] ? arguments[1] : 10;
    var pageIndex = arguments[0] ? arguments[0] : page;
    param['pageNumber'] = pageIndex;
    param['pageSize'] = pageSize;
    param['query_type'] = "freq";
    param['freq_type'] = query_type;
    param['store_code'] = $($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    param['corp_code'] = localStorage.getItem('corp_code');
    param["area_code"] = $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    param['brand_code'] = $($('#side_analyze ul li:nth-child(1) s')[0]).attr('brand_code');
    param['group_code'] = $($('#side_analyze ul li:nth-child(4) s')[0]).attr('group_code');
    oc.postRequire("post", "/vipAnalysis/vipConsume", "", param, function (data) {
        if (data.code == "0") {
            $('.freq .vip_table tbody').empty();
            $('.freq .vip_table thead').empty();
            var msg = JSON.parse(data.message);
            count = msg.pages;
            var pageIndex = msg.pageNum;
            msg = msg.vip_cost_freq_list;
            $(".freq thead").append('<tr>'
                +'<th class="first_th" style="width:50px;text-align: left"><div class="checkbox">'
                + '<input onClick="if(this.checked==true) {checkAll(\'test\')} else { clearAll(\'test\')}" type="checkbox" name="test" class="check" id="checkboxTwoInputfreq"/>'
                + '<label for="checkboxTwoInputfreq"></label></div></th>'
                + '<th>序号</th>'
                + '<th>会员名称</th>'
                + '<th>会员类型</th>'
                + '<th>会员卡号</th>'
                + '<th>手机号</th>'
                + '<th>平均消费</th>'
                + '<th>月消费次数</th></tr>');
            if (msg.length) {
                for (var i = 0; i < msg.length; i++) {
                    if (pageIndex >= 2) {
                        var a = i + 1 + (pageIndex - 1) * pageSize;
                    } else {
                        var a = i + 1;
                    }
                    $(".freq tbody").append('<tr id="' + msg[i].vip_id + '" data-code="'+msg[i].corp_code+'"><td class="first_th" style="width: 50px;text-align: left;"><div class="checkbox"><input  type="checkbox" name="test" class="check" id="checkboxfreq'
                        + i
                        + 1
                        + '"/><label for="checkboxfreq'
                        + i
                        + 1
                        + '"></label></div></td><td>'
                        + a
                        + '</td><td>'
                        + msg[i].vip_name
                        + '</td><td>'
                        + msg[i].vip_type
                        + '</td><td>'
                        + msg[i].vip_card_no
                        + '</td><td>'
                        + msg[i].mobile
                        + '</td><td>'
                        + formatCurrency(msg[i].avg_amount)
                        + '</td><td>'
                        + formatCurrency(msg[i].consume_frequently)
                        + '</td></tr>');
                }
                $(".freq .vip_table tbody tr td:not(:first-child)").click(function () {
                    // vipTable_lg();
                    var ID = $(this).parent().attr("id");
                    var corpCode= $(this).parent().attr("data-code");
                    sessionStorage.setItem("id", ID);
                    sessionStorage.setItem("corp_code", corpCode);
                    window.open("http://" + window.location.host + "/navigation_bar.html?url=/vip/vip_data.html&func_code=F0010");
                })
            } else if (msg.length == 0) {
                var len = $(".freq thead tr th").length;
                var len2 =Math.ceil(len/2);
                var i;
                for (i = 0; i < 10; i++) {
                    $(".freq tbody").append("<tr></tr>");
                    if(i==4){
                        for (var j = 0; j < len; j++) {
                            if(j==0){
                                $($(".freq tbody tr")[i]).append("<td colspan='"+len+"'><span style='line-height:45px;font-size: 18px;color:#999'>暂无数据</span></td>");
                            }else{
                                $($(".freq tbody tr")[i]).append("<td></td>");
                            }
                        }
                    }else {
                        for (var j = 0; j < len; j++) {
                            $($(".freq tbody tr")[i]).append("<td></td>");
                        }
                    }

                }
            }
        } else if (data.code == "-1") {
            art.dialog({
                zIndex: 10010,
                time: 2,
                lock: true,
                cancel: false,
                content: "数据加载失败"
            });
        }
        $(".freq thead th:first-child input").removeAttr("checked");
        //调用生成页码
        setPage($('#table_analyze .foot .foot-num')[0], count, pageIndex, pageSize, type, query_type);
        $("#right_shadow").hide();//移除加载框
    });
}
function consumefreq_sub(ali) {
    var ali = ali;//当前对象
    switch ($($(ali).html()).html()) {
        case '三个月':
            query_type = "three";
            consumefreq('', '', query_type);
            break;
        case '六个月':
            query_type = "six";
            consumefreq('', '', query_type);
            break;
        case '十二个月':
            query_type = "twelve";
            consumefreq('', '', query_type);
            break;
    }
}
/*******************共用方法***************************/
//生成分页
function setPage(container, count, pageindex, pageSize, type, query_type) {
    count == 0 ? count = 1 : '';
    var type = type;
    var query_type = query_type;
    var container = container;//节点
    var count = count;//5
    var pageindex = parseInt(pageindex);//1
    var pageSize = pageSize;//10
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
        } else if (pageindex >= count - 3) {
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
    } else {
        a[a.length] = "<li><span class=\"icon-ishop_4-02\"></span></li>";
    }
    container.innerHTML = a.join("");
    var pageClick = function () {
        var oAlink = container.getElementsByTagName("span");
        inx = pageindex; //初始的页码
        $("#input-txt").val(inx);
        $(".foot-sum .zy").html("共 " + count + "页");
        oAlink[0].onclick = function () { //点击上一页
            if (inx == 1) {
                return false;
            }
            inx--;
            dian(inx, pageSize, type, query_type);
            // setPage(container, count, inx,pageSize,funcCode,value,filtrate);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function () {
                inx = parseInt(this.innerHTML);
                dian(inx, pageSize, type, query_type);
                // setPage(container, count, inx,pageSize,funcCode,value,filtrate);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function () { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx, pageSize, type, query_type);
            // setPage(container, count, inx,pageSize,funcCode,value,filtrate);
            return false;
        }
    }()
}
//页码仿写select
$(function () {
        $("#page_row,.page_p .icon-ishop_8-02").click(function () {
            if ("block" == $("#liebiao").css("display")) {
                hideLi();
            } else {
                showLi();
            }
        });
        $("#liebiao li").each(function (i, v) {
            $(this).click(function () {
                pageSize = $(this).attr('id');
                var a = 1;
                jump == 2 && (newVipGet(a, pageSize, month_type));
                jump == 3 && (sleepVipGet(a, pageSize, query_type));
                jump == 1 && (brithVipGet(a, pageSize, month_type));
                jump == 4 && jump_s == 0 && (consumeVipGet(a, pageSize, query_type));
                // jump==4&&jump_s==1&&(consumeVipGetre(a,pageSize,query_type));
                jump == 4 && jump_s >= 1 && (consumeVipGetam(a, pageSize, query_type));
                jump == 5 && (consumefreq(a, pageSize, query_type));
                $("#page_row").val($(this).html());
                hideLi();
            });

        });
        $("#page_row").blur(function () {
            setTimeout(hideLi, 200);
        });
    }
);
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
//页码显示
function showLi() {
    $("#liebiao").show();
}
//页码隐藏
function hideLi() {
    $("#liebiao").hide();
}
//页码点击事件
function dian(a, b, c, query_type) {
    var a = a;
    var b = b;
    var c = c;//页码请求类型
    var query_type = query_type;
    c == 'new' && (newVipGet(a, b, query_type));
    //当请求类型为sleep时
    //判断query_type的值执行不同的请求
    if (c == 'sleep' && (query_type == 0)) {
        sleepVipGet(a, b)
    } else if (c == 'sleep' && (query_type)) {
        sleepVipGet(a, b, query_type);
    }
    //当请求类型为birth 判断请求的类型
    if (c == 'birth' && (query_type == 'today')) {
        brithVipGet(a, b);
    } else if (c == 'birth' && (query_type)) {
        brithVipGet(a, b, query_type)
    }
    //当请求类型为consume 判断请求的类型
    if (c == 'consume' && (query_type == 'recent')) {
        consumeVipGet(a, b);
    } else if (c == 'consume' && (query_type == "month" || query_type == "three_month" || query_type == "history")) {
        consumeVipGetam(a, b, query_type);
    }
    //当请求类型为freq 判断请求的类型
    if (c == 'freq' && query_type == 'three') {
        consumefreq(a, b);
    } else if (c == 'freq' && query_type == "six") {
        consumefreq(a, b, query_type);
    } else if (c == 'freq' && query_type == "twelve") {
        consumefreq(a, b, query_type);
    }
}
//指定页面的跳转
$("#input-txt").keydown(function () {
    var event = window.event || arguments[0];
    var inx = this.value.replace(/[^0-9]/g, '');
    var inx = parseInt(inx);
    if (inx > count) {
        inx = count
    }
    if (inx > 0) {
        if (event.keyCode == 13) {
            jump == 2 && (newVipGet(inx, '', month_type));
            jump == 3 && (sleepVipGet(inx, '', query_type));
            jump == 1 && (brithVipGet(inx, '', month_type));
            jump == 4 && jump_s == 0 && (consumeVipGet(inx, '', month_type));
            // jump==4&&jump_s==1&&(consumeVipGetre(inx,'',query_type));
            jump == 4 && jump_s >= 1 && (consumeVipGetam(inx, '', query_type));
            jump == 5 && (consumefreq(inx, "", query_type));
            $("#page_row").val("10行/页");
        }
    }
});
//左侧开始分析
$("#start_analyze_left").click(function () {
    $("#start_analyze_left").hide();
    getData();
});
//右侧开始分析
$("#start_analyze_right").click(function () {
    $("#start_analyze_right").hide();
    $($('.vip_nav_bar li[class="liactive"]')[0]).trigger('click');
});
//获取图标筛选的年份
function geyCurrentYear() {
    var date = new Date(),
        Year = date.getFullYear(),
        temp = '<li>全部</li>',
        n = 10;
    while(n > 0){
        temp += '<li>'+Year+'</li>';
        $("#chart_year_select").html(temp);
        n--;
        Year--;
    }
}
/*********************页面加载时**********************************************/
$().ready(function () {
    geyCurrentYear();
    $("#chart_analyze .chart_module>ul,#chart_analyze .chart_module>ul>li").height($(".chart_module").height()-30);
    //页面加载时，异步加载显示的数据
    $($(".date_btn span")[0]).css({"color": "#fff", "background": "#6cc1c8"});
    $('#select_analyze ul').on('click', 'li', showNameClick);
    $('#select_analyze_shop ul').on('click', 'li', showNameClick);
    $('#select_analyze_brand ul').on('click', 'li', showNameClick);
    $('#select_analyze_group ul').on('click', 'li', showNameClick);
    $('#select_analyze_source ul').on('click', 'li', showNameClick);
    //添加搜索
    $('#side_analyze div b span').click(searchValue);
    $('#side_analyze div b input').keydown(function () {
        var event = window.event || arguments[0];
        if (event.keyCode == 13) {
            searchValue(event);
        }
    });
    //活跃用户切换
    $('.activeVip .month_btn li').click(function () {
        var index = $(this).index();
        var range;
        index == 0?range=[0,3]:index==1?range=[3,6]:index==2?range=[6,9]:index==3?range=[9,12]:range=[12,13];
        vm.slide = range;
        sleepVipGet_sub(index);
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
    //消费排行
    $('.rank .month_btn li').click(function () {
        jump_s = $(this).index();
        consumeVipGet_sub(this);
        $("#page_row").val("10行/页");
    });
    //消费排频率
    $('.freq .new_btn li').click(function () {
        consumefreq_sub(this);
        $("#page_row").val("10行/页");
    });
    var date = new Date();
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if(month<10){
        month="0"+month;
    }
    if(strDate<10){
        strDate="0"+strDate;
    }
    date = year + "-" + month + "-" + strDate;
    $(".date_title .date input").val(date);
    $('#chart_analyze').dad({
        placeholder:"拖放到这里",
        draggable: '.drag_area',
        callback: function (data) {
            var index = $(data).index();
            var order = [];
            $(".chart_module").each(function () {
                if ($(this).attr("data-id") !== undefined && $(this).css("display") == "block") {
                    order.push($(this).attr("data-id"));
                }
            });
            order.pop();
            chartShow(order);
        }
    });
});
var achv = {
    elem: '#date',
    format: 'YYYY-MM-DD',
    max: laydate.now(), //最大日期
    istime:false,
    istoday: false,
    choose: function (datas) {
        getData();
    }
};
laydate(achv);
/*****************************************************************************************************************/
//左侧业绩选择日周年月
$(".choose").click(function () {
    $(".select_Date").toggle();
}).mouseleave(function () {
    $(".select_Date").hide();
});
$(".select_Date li").click(function () {
    var content = $(this).html();
    $("input.title_l").val(content);
    switch (content) {
        case '按日查看':
            $(".title_l").attr("data-type","day");
            break;
        case '按周查看':
            $(".title_l").attr("data-type","week");
            break;
        case '按月查看':
            $(".title_l").attr("data-type","month");
            break;
        case '按年查看':
            $(".title_l").attr("data-type","year");
            break;
    }
    getData();
    $(".select_Date").hide();
});
//图标新增弹窗
$("#add_chart").click(function () {
    var arr = whir.loading.getPageSize();
    var w = arr[0];
    var h = arr[1];
    var left = (arr[0] - $("#chart_add_wrap").width()) / 2;
    var tp = (arr[3] - $("#chart_add_wrap").height()) / 2;
    $("#chart_add_wrap").css({"left": left, "top": tp, "position": "fixed"});
    $("#p").css({"width": w, "height": h});
    $("#chart_add_wrap").show();
    $("#p").show();
    $("#chart_analyze .chart_module").each(function () {
        var val = $(this).find(".drag_area").children("span").html();
        if ($(this).css("display") == "block") {
            $("#chart_add_details li").each(function () {
                if ($(this).find(".chart_name").html() == val) {
                    $(this).find(".chart_icon").html("-");
                    $(this).find(".chart_icon").removeClass("chart_icon").addClass("chart_icon_remove");
                }
            });
        }else if($(this).css("display") == "none"){
            $("#chart_add_details li").each(function () {
                if ($(this).find(".chart_name").html() == val) {
                    $(this).find(".chart_icon_remove").html("+");
                    $(this).find(".chart_icon_remove").removeClass("chart_icon_remove").addClass("chart_icon");
                }
            });
        }
    });
});
$(".chart_close").click(function () {
    $("#chart_add_wrap").hide();
    $("#p").hide();
});
$("#chart_add_enter").click(function () {
    $("#chart_add_details .chart_icon_remove").each(function () {
        var val = $(this).prev().find(".chart_name").html();
        $("#chart_analyze .chart_module").each(function (i,e) {
            if ($(e).css("display") == "none" && val == $(e).find(".drag_area span").html()) {
                $("#chart_analyze").append($(e));
                $(e).show();
                var ID = $(e).attr("data-id");
                //init_chart(ID);
                chartData(ID);
            } else {
                return;
            }
        });
    });
    $("#chart_add_details .chart_icon").each(function () {
        var val = $(this).prev().find(".chart_name").html();
        $("#chart_analyze .chart_module").each(function (i,e) {
            if ($(e).css("display") !== "none" && val == $(e).find(".drag_area span").html()) {
                $(e).hide();
            } else {
                return;
            }
        });
    });
    var order = [];
    $(".chart_module").each(function () {
        if ($(this).attr("data-id") !== undefined && $(this).css("display") == "block") {
            order.push($(this).attr("data-id"));
        }
    });
    $("#chart_add_wrap").hide();
    $("#p").hide();
    chartShow(order);
});
$("#chart_add_details").on("click",".chart_icon",function () {
    $(this).removeClass().addClass("chart_icon_remove");
    $(this).html("-");
});
$("#chart_add_details").on("click",".chart_icon_remove",function () {
    $(this).removeClass().addClass("chart_icon");
    $(this).html("+");
});
//图表分析下拉
$(".chart_analyze_condition").click(function () {
    $(this).children("ul").toggle();
});
$('.chart_analyze_condition ul li').click(function () {
    $(this).parent().prevAll("span").html($(this).html());
    var ID=$(this).parents(".chart_module").attr("data-id");
    var type=$(this).html();
    switch (type){
        case "按金额分析":
            type="amt";
            break;
        case "按件数分析":
            type="num";
            break;
        case "按折扣分析":
            type="discount";
            break;
        case "按客单价分析":
            type="trade_price";
            break;
        case "按连带率分析":
            type="relate_rate";
            break;
        case "按笔数分析":
            type="times";
            break;
        case "按人数分析":
            type="people";
            break;
    }
    //type=="按金额分析"?type="amt":type=="按件数分析"?type="num":"";
    init_chart(ID,type);
});
//图表双击放大
$("#chart_analyze").on("dblclick",".chart_head",function (e) {
    if(!($(e.target).is(".chart_head"))){
        return ;
    }
    var is_lg = $(this).parents(".chart_module").hasClass("chart_lg");
    if(is_lg == true){
        $(this).find(".chart_close_icon").trigger("click");
    }else if(is_lg == false){
        whir.loading.add("mask",0.5);
        $(this).parents(".chart_module").addClass("chart_lg");
        $("#chart_analyze .chart_module.chart_lg>ul>li").height($(".chart_lg").height()-30);
        $(this).parents(".chart_module").find(".data_table ul li").css("width",($(this).parents(".chart_module").width()-20)/ $(this).parents(".chart_module").find(".data_table ul:first-child li").length+"px");
        var ID = $(this).next().attr("id");
        switch (ID){
            case "type" :chart.myChart!==""?chart.myChart.resize():"";break;
            case "series" :chart.myChart5!==""?chart.myChart5.resize():"";break;
            case "weeks" :chart.myChart1!==""?chart.myChart1.resize():"";break;
            case "month" :chart.myChart4!==""?chart.myChart4.resize():"";break;
            case "season" :chart.myChart3!==""?chart.myChart3.resize():"";break;
            case "area" :chart.myChart6!==""?chart.myChart6.resize():"";break;
            case "price" :chart.myChart2!==""?chart.myChart2.resize():"";break;
            case "typeMid" :chart.chartMId!==""?chart.chartMId.resize():"";break;
            case "typeSm" :chart.chartSm!==""?chart.chartSm.resize():"";break;
            case "typeSeries" :chart.chartSeries!==""?chart.chartSeries.resize():"";break;
            case "age" :chart.myChartAge!==""?chart.myChartAge.resize():"";break;
            case "cardType" :chart.myChartCardType!==""?chart.myChartCardType.resize():"";break;
            case "sex_vip_num" :chart.myChart_sex!==""?chart.myChart_sex.resize():"";break;
            case "birth_vip_num" :chart.myChart_birth_vip_num!==""?chart.myChart_birth_vip_num.resize():"";break;
            case "source" :chart.myChart_source!==""?chart.myChart_source.resize():"";break;
        }
    }
});
window.addEventListener("resize",function(){
    $("#chart_analyze .chart_module.chart_lg>ul>li").height($(".chart_lg").height()-30);
});
//图表删除
$(".chart_close_icon").click(function () {
    var is_lg = $(this).parents(".chart_module").hasClass("chart_lg");
    if(is_lg == true){
        $("#chart_analyze .chart_module.chart_lg>ul>li").height($(".chart_module").height()-30);
        $(this).parents(".chart_module").removeClass("chart_lg");
        $(this).parents(".chart_module").find(".data_table ul li").css("width",($(this).parents(".chart_module").width()-20)/ $(this).parents(".chart_module").find(".data_table ul:first-child li").length+"px");
        var ID=$(this).parents(".chart_head").next().attr("id");
        switch (ID){
            case "type" :chart.myChart!==""?chart.myChart.resize():"";break;
            case "series" :chart.myChart5!==""?chart.myChart5.resize():"";break;
            case "weeks" :chart.myChart1!==""?chart.myChart1.resize():"";break;
            case "month" :chart.myChart4!==""?chart.myChart4.resize():"";break;
            case "season" :chart.myChart3!==""?chart.myChart3.resize():"";break;
            case "area" :chart.myChart6!==""?chart.myChart6.resize():"";break;
            case "price" :chart.myChart2!==""?chart.myChart2.resize():"";break;
            case "typeMid" :chart.chartMId!==""?chart.chartMId.resize():"";break;
            case "typeSm" :chart.chartSm!==""?chart.chartSm.resize():"";break;
            case "typeSeries" :chart.chartSeries!==""?chart.chartSeries.resize():"";break;
            case "age" :chart.myChartAge!==""?chart.myChartAge.resize():"";break;
            case "cardType" :chart.myChartCardType!==""?chart.myChartCardType.resize():"";break;
            case "sex_vip_num" :chart.myChart_sex!==""?chart.myChart_sex.resize():"";break;
            case "birth_vip_num" :chart.myChart_birth_vip_num!==""?chart.myChart_birth_vip_num.resize():"";break;
            case "source" :chart.myChart_source!==""?chart.myChart_source.resize():"";break;
        }
        whir.loading.remove("mask");
    }else if(is_lg == false){
        var ID=$(this).parents(".chart_head").next().attr("id");
        $(this).parents(".chart_module").hide();
        if(ID!="sex_vip_num" && ID!="birth_vip_num"){
            $(this).prev().children().eq(0).text("按件数分析");
        }
        var order = [];
        $(".chart_module").each(function () {
            if ($(this).attr("data-id") !== undefined && $(this).css("display") == "block") {
                order.push($(this).attr("data-id"));
            }
        });
        chartShow(order);
    }
});
//列表图表切换
$(".chart_list_icon").click(function () {
    var li = $(this).parent(".chart_head").nextAll(".data_table");
    var id = $(this).parent(".chart_head").next('li').attr("id");
    if($(li).css("display") == "block"){
        $(li).hide();
        $(li).prev("li").show();
        switch (id){
            case "type" :chart.myChart!==""?chart.myChart.resize():"";break;
            case "series" :chart.myChart5!==""?chart.myChart5.resize():"";break;
            case "weeks" :chart.myChart1!==""?chart.myChart1.resize():"";break;
            case "month" :chart.myChart4!==""?chart.myChart4.resize():"";break;
            case "season" :chart.myChart3!==""?chart.myChart3.resize():"";break;
            case "area" :chart.myChart6!==""?chart.myChart6.resize():"";break;
            case "price" :chart.myChart2!==""?chart.myChart2.resize():"";break;
            case "typeMid" :chart.chartMId!==""?chart.chartMId.resize():"";break;
            case "typeSm" :chart.chartSm!==""?chart.chartSm.resize():"";break;
            case "typeSeries" :chart.chartSeries!==""?chart.chartSeries.resize():"";break;
            case "age" :chart.myChartAge!==""?chart.myChartAge.resize():"";break;
            case "cardType" :chart.myChartCardType!==""?chart.myChartCardType.resize():"";break;
            case "sex_vip_num" :chart.myChart_sex!==""?chart.myChart_sex.resize():"";break;
            case "birth_vip_num" :chart.myChart_birth_vip_num!==""?chart.myChart_birth_vip_num.resize():"";break;
            case "source" :chart.myChart_source!==""?chart.myChart_source.resize():"";break;
        }
    }else if($(li).css("display") == "none"){
        $(li).show();
        $(li).prev("li").hide();
    }
});
//群发消息
$(".vip_analyze_operate").click(function(){
    var tr=$("#table_analyze tbody input[type='checkbox']:checked").parents("tr");
    if(tr.length==0){
        art.dialog({
        			time: 1,
        			lock: true,
        			cancel: false,
        			content: "请选择会员"
        		});
    }else if(tr.length>0){
        var arr = whir.loading.getPageSize();
        var w = arr[0];
        var h = arr[1];
        $(".messageSend_wrap").show();
        $("#p").css({"width": w, "height": h,"z-index":"1000"});
        $("#p").show();
    }
});
$("#message_input").click(function(){
    $("#message_send_type").toggle();
});
$("#message_input").blur(function(){
    setTimeout(function(){
         $("#message_send_type").hide();
    },200)
});
$("#message_send_type li").click(function(){
    $("#message_input").val($(this).html());
});
$("#message_close").click(function(){
    $(".messageSend_wrap").hide();
    $("#p").hide();
    $("#message_content").val("");
    $("#message_input").val("");
});
$("#message_send").click(function(){
        var tr=$("#table_analyze tbody input[type='checkbox']:checked").parents("tr");
        var param={};
    	var sms_vips={};
    	var corp_code=sessionStorage.getItem("corp_code");//企业编号
    	var send_scope="vip";//发送范围
    	var content=$("#message_content").val().trim();
    	var id="";
    	sms_vips["type"]="2";
    	var send_type="";
        if($("#message_input").val() == "短信"){
        	send_type = "sms";
        }else if($("#message_input").val() == "微信群发消息"){
        	send_type = "wxmass";
        }
        for(var i=0;i<tr.length;i++){
            if(i==tr.length-1){
                id+=$(tr[i]).attr("id");
            }else {
                id+=$(tr[i]).attr("id")+",";
            }
        }
    	sms_vips["vips"]=id;
    	param["sms_vips"]=sms_vips;
    	param["vip_group_code"]="";
    	param["corp_code"]=corp_code;
    	param["content"]=content;
    	param["send_type"]=send_type;
    	param["send_scope"]=send_scope;
    	if(send_type == ""){
    		art.dialog({
    			time: 1,
    			zIndex:10010,
    			lock: true,
    			cancel: false,
    			content: "发送类型不能为空"
    		});
    		return;
    	}
    	if(content==""){
    		art.dialog({
    			time: 1,
    			zIndex:10010,
    			lock: true,
    			cancel: false,
    			content: "发送内容不能为空"
    		});
    		return;
    	}
    	whir.loading.add("",0.5);//加载等待框
    	oc.postRequire("post","/vipFsend/add","",param,function(data){
    		if(data.code=="0"){
    			art.dialog({
    				time: 1,
    				zIndex:10010,
    				lock: true,
    				cancel: false,
    				content: "发送成功"
    		    });
    		}else if(data.code=="-1"){
    			art.dialog({
    				time: 1,
    				zIndex:10010,
    				lock: true,
    				cancel: false,
    				content: "发送失败"
    		    });
    		}
    		$(".messageSend_wrap").hide();
            $("#p").hide();
    		whir.loading.remove();//移除加载框
    	});
});
//防止ajax重复请求
var pendingRequests = {};
$.ajaxPrefilter(function( options, originalOptions, jqXHR ) {
    var key = options.url;
    var name = JSON.parse(originalOptions.data.param).message.store_label;
    if(key.indexOf("vipAnalysis")>-1 || key == "/privilege/vip/chartOrder"){
        if(key == "/vipAnalysis/vipChart" && !pendingRequests[name]){
            pendingRequests[name] = jqXHR;
        }else if(!pendingRequests[key]){
            pendingRequests[key] = jqXHR;
        }else {
            // jqXHR.abort();    //放弃后触发的提交
            if(pendingRequests[name]){
                pendingRequests[name].abort();
                pendingRequests[name] = jqXHR;
            }else {
                pendingRequests[key].abort();
                pendingRequests[key] = jqXHR;
            }
            // pendingRequests[name]?pendingRequests[name].abort(): pendingRequests[key].abort();// 放弃先触发的提交
        }
        var complete = options.complete;
        options.complete = function(jqXHR, textStatus) {
            // pendingRequests[key] = null;
            // pendingRequests[name] = null;
            if ($.isFunction(complete)) {
                complete.apply(this, arguments);
            }
        };
    }
});

