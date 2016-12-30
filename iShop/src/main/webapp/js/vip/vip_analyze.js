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
var page_brand = 1;
var page_area = 1;
var page_group = 1;
var next_shop_page = '';
var next_area_page = '';
var chart={
    "myChart":"",
    "myChart1":"",
    "myChart2":"",
    "myChart3":"",
    "myChart4":"",
    "myChart5":"",
    "myChart6":""
};
var chart_data;
// var echarts;
/**********************左侧数据**************************************************************************************/
//获取品牌
function getBrand() {
    var search_param = arguments.length;
    var param = {};
    var ul = '';
    // param["corp_code"]= localStorage.getItem('corp_code');
    param["corp_code"] = "C10000";
    param["searchValue"] = $("#select_analyze_brand input").val();
    oc.postRequire("post", "/brand/findBrandByCorpCode", "", param, function (data) {
        //     oc.postRequire("post","/shop/brands", "",param, function(data){
        if (data.code == 0) {
            var message = JSON.parse(data.message)
            var brands = message.brands;//数组
            //遍历数组添加页面元素
            // if(brands.length<=8){
            //     $('#select_analyze_brand s').attr('style','display:none')
            // }else{
            //     $('#select_analyze_brand s').attr('style','display:block')
            // }
            if (brands.length <= 0) {
                return
            }
            for (var i = 0; i < brands.length; i++) {
                brands[i].brand_name == '全部' ? '' : ul += "<li brand_cord='" + brands[i].brand_code + "'>" + brands[i].brand_name + "</li>";
            }
            var brand_name = brands[0].brand_name;
            var brand_code = brands[0].brand_code;//区域号
            if (search_param == 0) {
                $('#side_analyze ul li:nth-child(1) s').html(brand_name);
                $('#side_analyze ul li:nth-child(1) s').attr('brand_code', brand_code);
            }
            $('#select_analyze_brand ul').append(ul);
            GetArea();
        } else if (data.code == -1) {
            alert(data.message);
        }
    });
}
getBrand();
getGroup();
//获取区域
function GetArea() {
    var search_param = arguments.length;
    var searchValue = $('#select_analyze input').val();
    var param = {};
    param['pageNumber'] = arguments[0] ? arguments[0] : page;
    param['pageSize'] = 8;
    param['searchValue'] = searchValue;
    param["corp_code"] = "C10000";
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
            // localStorage.setItem('corp_code',output_list[0].corp_code?output_list[0].corp_code:'');
            localStorage.setItem('corp_code', 'C10000');
            for (var i = 0; i < output_list.length; i++) {
                output_list[i].area_name == '全部' ? '' : ul += "<li data_area='" + output_list[i].area_code + "'>" + output_list[i].area_name + "</li>";
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
            $('#select_analyze ul').append(ul);
        } else if (data.code == "-1") {
            alert(data.message);
        }
    });
}
//获取店铺
function getStore(a, b, btn) {
    var searchValue = $('#select_analyze_shop input').val();
    var area_code = a;
    var param = {};
    param['brand_code'] = $('#side_analyze ul li:nth-child(1) s').attr('brand_code');
    param['pageNumber'] = arguments[1] ? arguments[1] : page;
    param['pageSize'] = 8;
    param['searchValue'] = searchValue;
    param["area_code"] = area_code;
    param["corp_code"] = "C10000";
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
        // output_list.length<7&&($('#select_analyze_shop s').attr('style','display:none'));
        first_store_name = output_list[0].store_name;
        first_store_code = output_list[0].store_code;
        for (var i = 0; i < output_list.length; i++) {
            // ul+="<li data_store='"+output_list[i].store_code+"'>"+output_list[i].store_name+"</li>";
            output_list[i].store_name == '全部' ? '' : ul += "<li data_store='" + output_list[i].store_code + "'>" + output_list[i].store_name + "</li>";
        }
        $('#side_analyze ul li:nth-child(3) s').html(first_store_name);
        $('#side_analyze ul li:nth-child(3) s').attr('data_store', first_store_code);
        $('#select_analyze_shop ul').append(ul);
        chartData();
        chartShow();
        // un_push?un_push=0;page=1:brithVipGet();getData();//正常调用当为加载更多时不调用
        if (un_push) {
            un_push = 0;
            console.log(total);
            if (total > 500 && btn == undefined) {
                $("#start_analyze_left").show();
                $("#start_analyze_right").show();
                return;
            }
            if (total > 500 && btn == "start_l") {
                $("#start_analyze_left").hide();
                getData();
            }
            if (total > 500 && btn == "start_r") {
                $("#start_analyze_right").hide();
                brithVipGet();
            }
            if (total < 500) {
                $("#start_analyze_left").hide();
                $("#start_analyze_right").hide();
                brithVipGet();
                getData();
            }
        } else {
            if (total > 500 && btn == undefined) {
                $("#start_analyze_left").show();
                $("#start_analyze_right").show();
                return;
            }
            if (total > 500 && btn == "start_l") {
                $("#start_analyze_left").hide();
                getData();
            }
            if (total > 500 && btn == "start_r") {
                $("#start_analyze_right").hide();
                $(".vip_nav_bar li").each(function () {
                    if ($(this).attr("class") == "liactive") {
                        $(this).trigger("click");
                    }
                });
            }
            if (total < 500) {
                $("#start_analyze_left").hide();
                $("#start_analyze_right").hide();
                brithVipGet();
                getData();
            }
            page = 1;
        }
    });
}
//获取分组
function getGroup() {
    var param = {};
    var ul = '';
    param["corp_code"] = "C10000";
    param["search_value"] = $("#select_analyze_group input").val();
    oc.postRequire("post", "/vipGroup/getCorpGroups", "", param, function (data) {
        if (data.code == 0) {
            var message = JSON.parse(data.message)
            var groups = JSON.parse(message.list);//数组
            if (groups.length <= 0) {
                return
            }
            for (var i = 0; i < groups.length; i++) {
                groups[i].vip_group_name == '全部' ? '' : ul += "<li group_code='" + groups[i].vip_group_code + "'>" + groups[i].vip_group_name + "</li>";
            }
            $('#select_analyze_group ul').append(ul);
        } else if (data.code == -1) {
            console.log(data.message);
        }
    });
}
//点击li填充s中的数据显示
function showNameClick(e) {
    un_push = 1;
    var e = e.target;
    $(e).addClass('choose_li').siblings('.choose_li').removeClass('choose_li');
    var d = $(e).parent().parent().parent();
    if ($(d).attr('id') == 'select_analyze') {
        var area_code = $(e).attr('data_area');
        $('#side_analyze ul li:nth-child(2) s').html($(e).html());
        $('#side_analyze ul li:nth-child(2) s').attr('data_area', area_code);
        //清除内容店铺下拉列表
        $('#select_analyze_shop ul').html('');
        getStore(area_code);
        $('#select_analyze').toggle();
        $($('.vip_nav_bar li[class="liactive"]')[0]).trigger('click');
    } else if ($(d).attr('id') == 'select_analyze_brand') {
        $('#select_analyze_brand').toggle();
        var brand_code = $(e).attr('brand_cord');
        $('#side_analyze ul li:nth-child(1) s').html($(e).html());
        $('#side_analyze ul li:nth-child(1) s').attr('brand_code', brand_code);
        //清除内容店铺下拉列表
        $('#select_analyze_shop ul').html('');
        getStore($('#side_analyze ul li:nth-child(2) s').attr('data_area'));
        $($('.vip_nav_bar li[class="liactive"]')[0]).trigger('click');
    } else if ($(d).attr('id') == 'select_analyze_shop') {
        $("#start_analyze_left").hide();
        $("#start_analyze_right").hide();
        var store_code = $(e).attr('data_store');
        $('#side_analyze ul li:nth-child(3) s').html($(e).html());
        $('#side_analyze ul li:nth-child(3) s').attr('data_store', store_code);
        $('#select_analyze_shop').toggle();
        //添加店铺时，找到显示的DIV发起请求
        $($('.vip_nav_bar li[class="liactive"]')[0]).trigger('click')
        getData();
        chartData();
    } else if ($(d).attr('id') == 'select_analyze_group') {
        var group_code = $(e).attr('group_code');
        $('#side_analyze ul li:nth-child(4) s').html($(e).html());
        $('#side_analyze ul li:nth-child(4) s').attr('group_code', group_code);
        $('#select_analyze_group').toggle();
        //添加分组时，找到显示的DIV发起请求
        $($('.vip_nav_bar li[class="liactive"]')[0]).trigger('click')
        chartData();
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

});
//加载更多
function getMore(e) {
    // page+=1;
    un_push = 1;
    var area_code = $('#side_analyze ul li:nth-child(2) s').attr('data_area');
    var e = e.target;
    if ($(e).hasClass('select_analyze_shop')) {
        page_shop++;
        getStore(area_code, page_shop);
    }
    if ($(e).hasClass('select_analyze')) {
        page_area++;
        GetArea(page_area);
    }
    if ($(e).hasClass('select_analyze_brand')) {
        page_brand++;
        getBrand(page_brand);
    }
    if ($(e).hasClass('select_analyze_group')) {
        page_group++;
        getGroup(page_group);
    }
}
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
//滚动监听加载更多
//区域滚动事件
$("#select_analyze_shop ul").bind("scroll", function () {
    var nScrollHight = $(this)[0].scrollHeight;
    var nScrollTop = $(this)[0].scrollTop;
    var nDivHight = $(this).height();
    if (nScrollTop + nDivHight >= nScrollHight) {
        next_shop_page ? getShopMore() : page_shop = 1;
    }
});
$("#select_analyze ul").bind("scroll", function () {
    var nScrollHight = $(this)[0].scrollHeight;
    var nScrollTop = $(this)[0].scrollTop;
    var nDivHight = $(this).height();
    if (nScrollTop + nDivHight >= nScrollHight) {
        next_area_page ? getAreaMore() : page_area = 1;
    }
});
//搜索
function searchValue(e) {
    //page初始化
    page = 1;
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
    if ($(this).find('b').html() == '区域') {
        $('#select_analyze').toggle();
        $('#select_analyze_shop').hide();
        $('#select_analyze_group').hide();
        if ($('#select_analyze input').val()) {
            $('#select_analyze input').val('');
            $('#select_analyze ul').html('');
            GetArea();
        }
    } else if ($(this).find('b').html() == '品牌') {
        $('#select_analyze_brand').toggle();
        $('#select_analyze_shop').hide();
        $('#select_analyze').hide();
        $('#select_analyze_group').hide();
        if ($('#select_analyze_brand input').val()) {
            $('#select_analyze_brand input').val('');
            $('#select_analyze_brand ul').html('');
            getBrand();
        }
    } else if ($(this).find('b').html() == '店铺') {
        $('#select_analyze_shop').toggle();
        $('#select_analyze_group').hide();
        //下拉搜索内容重置
        if ($('#select_analyze_shop input').val()) {
            $('#select_analyze_shop input').val('');
            $('#select_analyze_shop ul').html('');
            getStore($($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area'));
        }
    } else if ($(this).find('b').html() == '分组') {
        $('#select_analyze_group').toggle();
        $('#select_analyze_shop').hide();
        $('#select_analyze').hide();
        $('#select_analyze_brand').hide();
        //下拉搜索内容重置
        if ($('#select_analyze_group input').val()) {
            $('#select_analyze_group input').val('');
            $('#select_analyze_group ul').html('');
            getGroup();
        }
    }
});
//按时间查看数据
function getData() {
    var param = {};
    param['store_code'] = $($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    param['corp_code'] = localStorage.getItem('corp_code');
    param["area_code"] = $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    param["type"] = "D";
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
//会员填充数据
function fillVip(data, count, container) {
    $(count).empty();
    $(container).empty();
    var html = '';
    $(count).html('总数：' + data['count']);
    html += "<td>" + data['scale'] + "</td>"
        + "<td>" + data['vip_amount'] + "</td>"
        + "<td>" + data['vip_price'] + "</td>"
        + "<td>" + data['price'] + "</td>";
    $(container).html(html);
    $("#left_shadow").hide();
}
function compare(value1, value2) {
    if (value1 < value2) {
        return -1;
    } else if (value1 > value2) {
        return 1;
    } else {
        return 0;
    }

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
                console.log("成功");
            } else {
                var list = JSON.parse(data.message);
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
                                $(this).show();
                                var ID = $(this).attr("data-id");
                                init_chart(ID);
                                $("#add_chart").before($(this));
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
        var data = chart_data.Type;
        var arr=[];
        type=="amt"?arr=data.trade_amt:arr=data.trade_num;
        var Arr = [];
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
        }
        var option_pie = {
            color: ['#9AD8DB', '#8BC0C8', '#7BA8B5', '#6C8FA2', '#5C778F', '#4D5F7C', '#444960', '#2C3244'],
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
        chart.myChart = echarts.init(document.getElementById('type'));
        chart.myChart.setOption(option_pie);
        reSize(chart.myChart);
    }
    if (id == "series") {
        var data = chart_data.Series;
        var arr=[];
        type=="amt"?arr=data.trade_amt:arr=data.trade_num;
        var Arr = [];
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].name);
        }
        var option_pie_series = {
            color: ['#9AD8DB', '#8BC0C8', '#7BA8B5', '#6C8FA2', '#5C778F', '#4D5F7C', '#444960', '#2C3244'],
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
        chart.myChart5 = echarts.init(document.getElementById('series'));
        chart.myChart5.setOption(option_pie_series);
        reSize(chart.myChart5);
    }
    if (id == "weeks") {
        var data = chart_data.Week;
        var arr=[];
        type=="amt"?arr=data.trade_amt:arr=data.trade_num;
        var Arr=[];
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].value);
        }
        var option_week_radar = {
            color: ['#A7DADE'],
            axis: {
                areaStyle: {
                    color: ['red']
                }
            },
            tooltip: {
                trigger: 'axis'
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
                            name: '周变'
                        }
                    ]
                }
            ]
        };
        Arr.sort(compare);
        for(var j=0;j<option_week_radar.polar[0].indicator.length;j++){
            option_week_radar.polar[0].indicator[j].max=Arr[Arr.length-1]*1.1;
        }
        chart.myChart1 = echarts.init(document.getElementById('weeks'));
        chart.myChart1.setOption(option_week_radar);
        reSize(chart.myChart1);
    }
    if (id == "price") {
        var data=chart_data.Price;
        var arr=[];
        type=="amt"?arr=data.trade_amt:arr=data.trade_num;
        var ArrName=[];
        var ArrValue=[];
        for(var i=0;i<arr.length;i++){
            ArrName.push(arr[i].name);
            ArrValue.push(arr[i].value);
        }
        chart.myChart2 = echarts.init(document.getElementById('price'));
        var option_bar = {
            color: ['#6CC1C8'],
            tooltip: {
                trigger: 'item',
                formatter: function (params) {
                    return params.seriesName + ' :' + params.value;
                }
            },
            grid: {
                borderWidth: 0,
                x: '100',
                y: '20',
                x2: '0',
                y2: '20'
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
    if (id == "times") {
        var data=chart_data.Time;
        var arr=[];
        type=="amt"?arr=data.trade_amt:arr=data.trade_num;
        var ArrName=[];
        var ArrValue=[];
        for(var i=0;i<arr.length;i++){
            ArrName.push(arr[i].name);
            ArrValue.push(arr[i].value);
        }
        chart.myChart3 = echarts.init(document.getElementById('times'));
        var option_line = {
            color: ['#6DADC8'],
            tooltip: {
                trigger: 'item'
            },
            grid: {
                borderWidth: 0,
                x: '50',
                y: '20',
                x2: '20',
                y2: '50'
            },
            xAxis: [
                {
                    axisLine: {
                        lineStyle: {color: '#58A0C0'}
                    },
                    splitLine: {
                        show: false
                    },
                    axisLabel: {
                        rotate: 45
                    },
                    axisTick: {
                        show: false
                    },
                    type: 'category',
                    boundaryGap: false,
                    data: ArrName
                }
            ],
            yAxis: [
                {
                    axisLine: {
                        show: false
                    },
                    splitArea: {
                        show: false
                    },
                    splitLine: {
                        lineStyle: {
                            color: '#999',
                            type: 'dashed'
                        }
                    },
                    type: 'value'
                }
            ],
            series: [
                {
                    itemStyle: {
                        symbolSize: '0',
                        normal: {
                            borderRadius: 0,
                            nodeStyle: {
                                borderRadius: 0
                            }
                        }
                    },
                    name: '购买时段',
                    type: 'line',
                    stack: '总量',
                    symbolSize: 0,
                    smooth: false,
                    data: ArrValue
                }
            ]
        };
        chart.myChart3.setOption(option_line);
        reSize(chart.myChart3);
    }
    if (id == "month") {
        var data=chart_data.Month;
        var arr=[];
        type=="amt"?arr=data.trade_amt:arr=data.trade_num;
        var Arr=[];
        for(var i=0;i<arr.length;i++){
            Arr.push(arr[i].value);
        }
        var option_month_radar = {
            color: ['#A7DADE'],
            axis: {
                areaStyle: {
                    color: ['red']
                }
            },
            tooltip: {
                trigger: 'axis'
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
                            name: '月变'
                        }
                    ]
                }
            ]
        };
        Arr.sort(compare);
        for(var j=0;j<option_month_radar.polar[0].indicator.length;j++){
            option_month_radar.polar[0].indicator[j].max=Arr[Arr.length-1]*1.1;
        }
        chart.myChart4 = echarts.init(document.getElementById('month'));
        chart.myChart4.setOption(option_month_radar);
        reSize(chart.myChart4);
    }
    if (id == "areas") {
        var data=chart_data.Area;
        var arr=[];
        type=="amt"?arr=data.trade_amt:arr=data.trade_num;
        chart.myChart6 = echarts.init(document.getElementById('areas'));
        var option_map = {
            tooltip: {
                trigger: 'item'
            },
            dataRange: {
                itemWidth: 5,
                itemGap: 0.2,
                color: ['#A7CFD5', '#3C95A2'],
                splitNumber: '20',
                orient: 'horizontal',
                min: 0,
                max: 2500,
                x: 'left',
                y: 'top',
                text: ['低','高']      // 文本，默认为数值文本
            },
            series: [
                {
                    name: 'iphone3',
                    type: 'map',
                    mapType: 'china',
                    roam: false,
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
}
function reSize(chart) {
    window.addEventListener("resize", function () {
        chart.resize();
    });
}
//图表数据
function chartData() {
    var param={};
    param["type"]="analysis";
    param['corp_code'] = localStorage.getItem('corp_code');
    param["area_code"] = $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    param['brand_code'] = $($('#side_analyze ul li:nth-child(1) s')[0]).attr('brand_code');
    param['vip_group_code'] = $($('#side_analyze ul li:nth-child(4) s')[0]).attr('group_code');
    param['store_code'] = $($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    oc.postRequire("post","/vipAnalysis/vipChart","0",param,function (data) {
        if(data.code == 0){
            var msg = JSON.parse(data.message);
            chart_data= msg;
            chartShow();
        }else if(data.code == -1){
            console.log(data.message);
        }
    });
}
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
    sleepVipGet();
    jump = 3;
    //点击时，将第一个按钮设置class为btn_bg
    $($(".month_btn span")[0]).css({"color": "#fff", "background": "#6cc1c8"});
    ;
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
    ;
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
    ;
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
    vipTable_lg();
});
//加载更多页面放大
function vipTable_lg() {
    $("#table_analyze").addClass("vip_table_lg");
    $("#chart_analyze").hide();
    $(".more_data").hide();
    $("#side_analyze").hide();
    $("html").css("background", "#e8e8e8");
    $("#table_analyze").css("background", "#e8e8e8");
    $(".vip_table_lg .icon-ishop_8-03").hide();
    $(".foot").show();
    $("#side_bar").show();
    var arr = whir.loading.getPageSize();
    var w = arr[0];
    var h = arr[1];
    $(".vip_table_lg .right_shadow").css({"height": h, "width": w, "left": "-220px", "top": "-50px"});
}
//点击标题返回
$("#vipAnalyze_return").click(function () {
    $("#table_analyze").removeClass("vip_table_lg");
    $("#chart_analyze").show();
    $(".more_data").show();
    $("#side_analyze").show();
    $("#table_analyze").css("background", "#fff");
    $(".foot").hide();
    $("#side_bar").hide();
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
    $(".right_shadow").css({"height": "100%", "width": "100%", "left": 0, "top": 0});
});
/******************生日会员****************************/
function brithVipGet() {
    if ($("#start_analyze_right").css("display") == "block") {
        return;
    }
    $("#right_shadow").show();//加载等待框
    var type = 'birth';
    $('.birthVip .vip_table tbody').empty();
    var param = {};
    var month_type = arguments[2] ? arguments[2] : 'today';
    var pageSize = arguments[1] ? arguments[1] : 10;
    var pageIndex = arguments[0] ? arguments[0] : page;
    param['pageNumber'] = pageIndex;
    param['pageSize'] = pageSize;
    param['query_type'] = month_type;
    param['store_code'] = $($('#side_analyze ul li:nth-child(3) s')[0]).attr('data_store');
    param['corp_code'] = localStorage.getItem('corp_code');
    param["area_code"] = $($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area');
    param['brand_code'] = $($('#side_analyze ul li:nth-child(1) s')[0]).attr('brand_code');
    param['group_code'] = $($('#side_analyze ul li:nth-child(4) s')[0]).attr('group_code');
    oc.postRequire("post", "/vipAnalysis/vipBirth", "", param, function (data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            count = msg.pages;
            var pageIndex = msg.pageNum;
            msg = msg.birthday_vip_list;
            if (msg.length == 0) {
                var len = $(".birthVip thead tr th").length;
                var i;
                for (i = 0; i < 10; i++) {
                    $(".birthVip tbody").append("<tr></tr>");
                    for (var j = 0; j < len; j++) {
                        $($(".birthVip tbody tr")[i]).append("<td></td>");
                    }
                }
                $(".birthVip tbody tr:nth-child(5)").append("<span style='position:absolute;left:45%;line-height:45px;font-size: 15px;color:#999'>暂无数据</span>");
            }
            if (msg.length > 0) {
                for (var i = 0; i < msg.length; i++) {
                    if (pageIndex >= 2) {
                        var a = i + 1 + (pageIndex - 1) * pageSize;
                    } else {
                        var a = i + 1;
                    }
                    $(".birthVip tbody").append('<tr id="' + msg[i].vip_id + '"><td>'
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
                    //vipTable_lg();
                    var ID = $(this).attr("id");
                    sessionStorage.setItem("id", ID);
                    sessionStorage.setItem("corp_code", "C10000");
                    window.open("http://" + window.location.host + "/navigation_bar.html?url=/vip/vip_data.html&func_code=F0010");
                })
            }
        } else if (data.code == "-1") {
            console.log(data.message);
        }
        console.log(1);
        $("#right_shadow").hide();//移除加载框
        //调用生成页码
        setPage($('#table_analyze .foot .foot-num')[0], count, pageIndex, pageSize, type, month_type);
        // pageShow($('.birthVip .vip_table tbody'));
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
    var page_show = '';
    $('.newVip .vip_table tbody').empty();
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
    oc.postRequire("post", "/vipAnalysis/vipNew", "", param, function (data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            count = msg.pages;
            var pageIndex = msg.pageNum;
            msg = msg.new_vip_list;
            if (msg.length) {
                for (var i = 0; i < msg.length; i++) {
                    if (pageIndex >= 2) {
                        var a = i + 1 + (pageIndex - 1) * pageSize;
                    } else {
                        var a = i + 1;
                    }
                    $(".newVip tbody").append('<tr id="' + msg[i].vip_id + '"><td>'
                        + a
                        + '</td><td>'
                        + msg[i].vip_name
                        + '</td><td>'
                        + msg[i].vip_card_type
                        + '</td><td>'
                        + msg[i].join_date
                        + '</td><td>'
                        + msg[i].vip_birthday
                        + '</td></tr>');
                }
                $(".vip_table tbody tr").click(function () {
                    //vipTable_lg();
                    var ID = $(this).attr("id");
                    sessionStorage.setItem("id", ID);
                    sessionStorage.setItem("corp_code", "C10000");
                    window.open("http://" + window.location.host + "/navigation_bar.html?url=/vip/vip_data.html&func_code=F0010");
                })
            } else if (msg.length == 0) {
                var len = $(".newVip thead tr th").length;
                var i;
                for (i = 0; i < 10; i++) {
                    $(".newVip tbody").append("<tr></tr>");
                    for (var j = 0; j < len; j++) {
                        $($(".newVip tbody tr")[i]).append("<td></td>");
                    }
                }
                $(".newVip tbody tr:nth-child(5)").append("<span style='position:absolute;left:45%;line-height:45px;font-size: 15px;color:#999'>暂无数据</span>");
            }
        } else if (data.code == "-1") {
            // whir.loading.remove();//移除加载框
        }
        //调用生成页码
        setPage($('#table_analyze .foot .foot-num')[0], count, pageIndex, pageSize, type, month_type)
        $("#right_shadow").hide();//移除加载框
        //如果页面没有数据
        // $('.newVip .vip_table tbody').html()?page_show=1:$('.newVip .vip_table tbody').append('<span class="no_data'+'">暂无数据</span>'),page_show=0;
        // pageShow($('.newVip .vip_table tbody'));
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
//获取活跃用户
function sleepVipGet() {
    if ($("#start_analyze_right").css("display") == "block") {
        return;
    }
    $("#right_shadow").show();//加载等待框
    var type = 'sleep';
    $('.activeVip .vip_table tbody').empty();
    var param = {};
    var query_type = arguments[2] ? arguments[2] : 0;
    ;
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
    oc.postRequire("post", "/vipAnalysis/vipSleep", "", param, function (data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            count = msg.pages;
            proportion_list = msg.proportion_list;
            //循环添加页面的百分比
            var spans = $('.activeVip .month_btn span');
            $($('.activeVip .month_btn span')[0]).html('活跃会员' + proportion_list.active_vip_proportion + '%');
            $($('.activeVip .month_btn span')[1]).html('3个月' + proportion_list.three_vip_proportion + "%");
            $($('.activeVip .month_btn span')[2]).html('6个月' + proportion_list.six_vip_proportion + '%');
            $($('.activeVip .month_btn span')[3]).html('9个月' + proportion_list.nine_vip_proportion + "%");
            $($('.activeVip .month_btn span')[4]).html('12个月' + proportion_list.year_vip_proportion + '%');
            var pageIndex = msg.pageNum;
            msg = msg.sleep_vip_list;
            if (msg.length) {
                for (var i = 0; i < msg.length; i++) {
                    if (pageIndex >= 2) {
                        var a = i + 1 + (pageIndex - 1) * pageSize;
                    } else {
                        var a = i + 1;
                    }
                    $(".activeVip tbody").append('<tr id="' + msg[i].vip_id + '"><td>'
                        + a
                        + '</td><td>'
                        + msg[i].vip_name
                        + '</td><td>'
                        + msg[i].vip_card_type
                        + '</td><td>'
                        + msg[i].amount
                        + '</td><td>'
                        + msg[i].consume_times
                        + '</td><td>'
                        + msg[i].recently_consume_date
                        + '</td></tr>');
                }
                $(".activeVip .vip_table tbody tr").click(function () {
                    var ID = $(this).attr("id");
                    sessionStorage.setItem("id", ID);
                    sessionStorage.setItem("corp_code", "C10000");
                    alert(window.location.host);
                    window.open("http://" + window.location.host + "/navigation_bar.html?url=/vip/vip_data.html&func_code=F0010");
                })
            } else if (msg.length == 0) {
                var len = $(".activeVip thead tr th").length;
                var i;
                for (i = 0; i < 10; i++) {
                    $(".activeVip tbody").append("<tr></tr>");
                    for (var j = 0; j < len; j++) {
                        $($(".activeVip tbody tr")[i]).append("<td></td>");
                    }
                }
                $(".activeVip tbody tr:nth-child(5)").append("<span style='position:absolute;left:45%;line-height:45px;font-size: 15px;color:#999'>暂无数据</span>");
            }
        } else if (data.code == "-1") {
        }
        $("#right_shadow").hide();//移除加载框
        //调用生成页码
        setPage($('#table_analyze .foot .foot-num')[0], count, pageIndex, pageSize, type, query_type)
        // $('.activeVip .vip_table tbody').html()?'':$('.activeVip .vip_table tbody').append('<span class="no_data'+'">暂无数据</span>');
        // pageShow($('.activeVip .vip_table tbody'));
    });
    // whir.loading.remove();//移除加载框
}
function sleepVipGet_sub(ali) {
    var ali = ali;//当前对象
    switch ($($(ali).html()).html()) {
        case '活跃会员' + proportion_list.active_vip_proportion + '%':
            query_type = 0;
            sleepVipGet('', '', query_type);
            break;
        case '3个月' + proportion_list.three_vip_proportion + "%":
            query_type = 1;
            sleepVipGet('', '', query_type);
            break;
        case '6个月' + proportion_list.six_vip_proportion + '%':
            query_type = 2;
            sleepVipGet('', '', query_type);
            break;
        case '9个月' + proportion_list.nine_vip_proportion + "%":
            query_type = 3;
            sleepVipGet('', '', query_type);
            break;
        case '12个月' + proportion_list.year_vip_proportion + '%':
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
    $('.rank .vip_table tbody').empty();
    $('.rank .vip_table thead').empty();
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
    oc.postRequire("post", "/vipAnalysis/vipConsume", "", param, function (data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            count = msg.pages;
            var pageIndex = msg.pageNum;
            msg = msg.vip_consume_recently_list;
            $(".rank thead").append('<tr>'
                + '<th>序号</th>'
                + '<th>会员名称</th>'
                + '<th>会员等级</th>'
                + '<th>消费总额</th>'
                + '<th>最近消费日期</th></tr>');
            if (msg.length) {
                for (var i = 0; i < msg.length; i++) {
                    if (pageIndex >= 2) {
                        var a = i + 1 + (pageIndex - 1) * pageSize;
                    } else {
                        var a = i + 1;
                    }
                    $(".rank tbody").append('<tr id="' + msg[i].vip_id + '"><td>'
                        + a
                        + '</td><td>'
                        + msg[i].vip_name
                        + '</td><td>'
                        + msg[i].vip_type
                        + '</td><td>'
                        + msg[i].amount
                        + '</td><td>'
                        + msg[i].consume_time
                        + '</td></tr>');
                }
                $(".rank .vip_table tbody tr").click(function () {
                    //vipTable_lg();
                    var ID = $(this).attr("id");
                    sessionStorage.setItem("id", ID);
                    sessionStorage.setItem("corp_code", "C10000");
                    window.open("http://" + window.location.host + "/navigation_bar.html?url=/vip/vip_data.html&func_code=F0010");
                })
            } else if (msg.length == 0) {
                var len = $(".rank thead tr th").length;
                var i;
                for (i = 0; i < 10; i++) {
                    $(".rank tbody").append("<tr></tr>");
                    for (var j = 0; j < len; j++) {
                        $($(".rank tbody tr")[i]).append("<td></td>");
                    }
                }
                $(".rank tbody tr:nth-child(5)").append("<span style='position:absolute;left:45%;line-height:45px;font-size: 15px;color:#999'>暂无数据</span>");
            }
        } else if (data.code == "-1") {
            // whir.loading.remove();//移除加载框
        }
        //调用生成页码
        setPage($('#table_analyze .foot .foot-num')[0], count, pageIndex, pageSize, type, query_type)
        // $('.rank .vip_table tbody').html()?'':$('.rank .vip_table tbody').append('<span class="no_data'+'">暂无数据</span>');
        // pageShow($('.rank .vip_table tbody'));
        $("#right_shadow").hide();//移除加载框
    });
}
function consumeVipGetam() {
    if ($("#start_analyze_right").css("display") == "block") {
        return;
    }
    $("#right_shadow").show();//加载等待框
    var type = 'consume';
    $('.rank .vip_table tbody').empty();
    $('.rank .vip_table thead').empty();
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
    oc.postRequire("post", "/vipAnalysis/vipConsume", "", param, function (data) {
        if (data.code == "0") {
            var msg = JSON.parse(data.message);
            count = msg.pages;
            var pageIndex = msg.pageNum;
            msg = msg.amount_list;
            $(".rank thead").append('<tr>'
                + '<th>序号</th>'
                + '<th>会员名称</th>'
                + '<th>会员等级</th>'
                + '<th>消费总额</th></tr>');
            if (msg.length > 0) {
                for (var i = 0; i < msg.length; i++) {
                    if (pageIndex >= 2) {
                        var a = i + 1 + (pageIndex - 1) * pageSize;
                    } else {
                        var a = i + 1;
                    }
                    $(".rank tbody").append('<tr id="' + msg[i].vip_id + '"><td>'
                        + a
                        + '</td><td>'
                        + msg[i].vip_name
                        + '</td><td>'
                        + msg[i].vip_card_type
                        + '</td><td>'
                        + msg[i].amount
                        + '</td></tr>');
                }
                $(".rank .vip_table tbody tr").click(function () {
                    //vipTable_lg();
                    var ID = $(this).attr("id");
                    sessionStorage.setItem("id", ID);
                    sessionStorage.setItem("corp_code", "C10000");
                    window.open("http://" + window.location.host + "/navigation_bar.html?url=/vip/vip_data.html&func_code=F0010");
                })
            } else if (msg.length == 0) {
                var len = $(".rank thead tr th").length;
                var i;
                for (i = 0; i < 10; i++) {
                    $(".rank tbody").append("<tr></tr>");
                    for (var j = 0; j < len; j++) {
                        $($(".rank tbody tr")[i]).append("<td></td>");
                    }
                }
                $(".rank tbody tr:nth-child(5)").append("<span style='position:absolute;left:45%;line-height:45px;font-size: 15px;color:#999'>暂无数据</span>");
            }
        } else if (data.code == "-1") {
        }
        //调用生成页码
        setPage($('#table_analyze .foot .foot-num')[0], count, pageIndex, pageSize, type, query_type)
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
        case '前三月消费':
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
    $('.freq .vip_table tbody').empty();
    $('.freq .vip_table thead').empty();
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
            var msg = JSON.parse(data.message);
            count = msg.pages;
            var pageIndex = msg.pageNum;
            msg = msg.vip_cost_freq_list;
            $(".freq thead").append('<tr>'
                + '<th>序号</th>'
                + '<th>会员名称</th>'
                + '<th>会员等级</th>'
                + '<th>平均消费</th>'
                + '<th>月消费次数</th></tr>');
            if (msg.length) {
                for (var i = 0; i < msg.length; i++) {
                    if (pageIndex >= 2) {
                        var a = i + 1 + (pageIndex - 1) * pageSize;
                    } else {
                        var a = i + 1;
                    }
                    $(".freq tbody").append('<tr><td>'
                        + a
                        + '</td><td>'
                        + msg[i].vip_name
                        + '</td><td>'
                        + msg[i].vip_type
                        + '</td><td>'
                        + msg[i].avg_amount
                        + '</td><td>'
                        + msg[i].consume_frequently
                        + '</td></tr>');
                }
                $(".freq .vip_table tbody tr").click(function () {
                    vipTable_lg();
                })
            } else if (msg.length == 0) {
                var len = $(".freq thead tr th").length;
                var i;
                for (i = 0; i < 10; i++) {
                    $(".freq tbody").append("<tr></tr>");
                    for (var j = 0; j < len; j++) {
                        $($(".rank tbody tr")[i]).append("<td></td>");
                    }
                }
                $(".freq tbody tr:nth-child(5)").append("<span style='position:absolute;left:45%;line-height:45px;font-size: 15px;color:#999'>暂无数据</span>");
            }
        } else if (data.code == "-1") {
            console.log(data.message);
        }
        //调用生成页码
        setPage($('#table_analyze .foot .foot-num')[0], count, pageIndex, pageSize, type, query_type)
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
    var pageindex = pageindex;//1
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
        $("#page_row").click(function () {

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
        sleepVipGet(a, b, query_type)
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
    } else if (c == 'consume' && query_type == "twelve") {
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
    ;
    if (inx > 0) {
        if (event.keyCode == 13) {
            jump == 2 && (newVipGet(inx, '', month_type));
            jump == 3 && (sleepVipGet(inx, '', query_type));
            jump == 1 && (brithVipGet(inx, '', month_type));
            jump == 4 && jump_s == 0 && (consumeVipGet(inx, '', month_type));
            // jump==4&&jump_s==1&&(consumeVipGetre(inx,'',query_type));
            jump == 4 && jump_s >= 1 && (consumeVipGetam(inx, '', query_type));
            jump == 5 && (consumefreq(a, pageSize, query_type));
        }
    }
});
//左侧开始分析
$("#start_analyze_left").click(function () {
    getStore($($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area'), "", "start_l");
});
//右侧开始分析
$("#start_analyze_right").click(function () {
    getStore($($('#side_analyze ul li:nth-child(2) s')[0]).attr('data_area'), "", "start_r");
});
//页码显示或隐藏
// function pageShow(table) {
//     if( $(table).text()=='暂无数据'){
//         $($('#table_analyze .foot .foot-jum')[0]).hide();
//         $($('#table_analyze .foot .foot-num')[0]).hide();
//         $($('#table_analyze .foot .foot-sum')[0]).hide();
//     }else{
//         $($('#table_analyze .foot .foot-jum')[0]).show();
//         $($('#table_analyze .foot .foot-num')[0]).show();
//         $($('#table_analyze .foot .foot-sum')[0]).show();
//     }
// }
/*********************页面加载时**********************************************/
$().ready(function () {
    $("#chart_analyze .chart_module>ul,#chart_analyze .chart_module>ul>li").height($(".chart_module").height()-30);
    //页面加载时，异步加载显示的数据
    $($(".date_btn span")[0]).css({"color": "#fff", "background": "#6cc1c8"});
    $('#select_analyze ul').on('click', 'li', showNameClick);
    $('#select_analyze_shop ul').on('click', 'li', showNameClick);
    $('#select_analyze_brand ul').on('click', 'li', showNameClick);
    $('#select_analyze_group ul').on('click', 'li', showNameClick);
    //加载更多
    $('#side_analyze div s').click(getMore);
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
    //消费排行
    $('.rank .month_btn li').click(function () {
        jump_s = $(this).index();
        console.log(jump_s);
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
    date = year + "-" + month + "-" + strDate
    $(".date_title .date input").val(date);
    $('#chart_analyze').dad({
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
    istime: true,
    istoday: false,
    choose: function (datas) {
        getData();
    }
};
laydate(achv);
/*****************************************************************************************************************/
//左侧业绩选择日周年月
// $(".choose").mouseover(function () {
//     $(".select_Date").show();
// }).mouseleave(function () {
//     $(".select_Date").hide();
// })
$(".select_Date li").click(function () {
    var content = $(this).html();
    $("input.title_l").val(content);
    switch (content) {
        case '按日查看':
            console.log('按日查看');
            break;
        case '按周查看':
            console.log('按周查看');
            break;
        case '按月查看':
            console.log('按月查看');
            break;
        case '按年查看':
            console.log('按年查看');
            break;
    }
    getData();
    $($(this).parent()).toggle();
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
    $("#chart_analyze .chart_module").each(function (i, e) {
        var val = $(this).find(".drag_area").children("span").html();
        if ($(this).css("display") == "block") {
            $("#chart_add_details li").each(function () {
                if ($(this).find(".chart_checkbox span").html() == val) {
                    $(this).find(".check")[0].checked = true;
                }
            });
        } else {
            $("#chart_add_details li").each(function () {
                if ($(this).find(".chart_checkbox span").html() == val) {
                    $(this).find(".check")[0].checked = false;
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
    $("#chart_add_details input[type='checkbox']:checked").each(function () {
        var val = $(this).nextAll("span").html();
        $("#chart_analyze .chart_module").each(function () {
            if ($(this).css("display") == "none" && val == $(this).find(".drag_area span").html()) {
                $(this).show();
                var ID = $(this).attr("data-id");
                init_chart(ID);
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
    chartShow(order);
    $("#chart_add_wrap").hide();
    $("#p").hide();
});
$("#chart_add_details li").click(function () {
    var input = $(this).find("input")[0];
    if(input.checked==true){
        input.checked=false;
    }else {
        input.checked=true;
    }
});
//图表分析下拉
$(".chart_analyze_condition").click(function () {
    $(this).children("ul").toggle();
});
$('.chart_analyze_condition ul li').click(function () {
    $(this).parent().prevAll("span").html($(this).html());
    var ID=$(this).parents(".chart_module").attr("data-id");
    var type=$(this).html();
    type=="按金额分析"?type="amt":"";
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
        $("#chart_analyze .chart_module>ul,#chart_analyze .chart_module>ul>li").height($(".chart_lg").height()-30);
        var ID = $(this).next().attr("id");
        switch (ID){
            case "type" :chart.myChart.resize();break;
            case "series" :chart.myChart5.resize();break;
            case "weeks" :chart.myChart1.resize();break;
            case "month" :chart.myChart4.resize();break;
            case "times" :chart.myChart3.resize();break;
            case "areas" :chart.myChart6.resize();break;
            case "price" :chart.myChart2.resize();break;
        }
    }
});
//图表删除
$(".chart_close_icon").click(function () {
    var is_lg = $(this).parents(".chart_module").hasClass("chart_lg");
    if(is_lg == true){
        $(this).parents(".chart_module").removeClass("chart_lg");
        $("#chart_analyze .chart_module>ul,#chart_analyze .chart_module>ul>li").height($(".chart_module").height()-30);
        var ID=$(this).parents(".chart_head").next().attr("id");
        switch (ID){
            case "type" :chart.myChart.resize();break;
            case "series" :chart.myChart5.resize();break;
            case "weeks" :chart.myChart1.resize();break;
            case "month" :chart.myChart4.resize();break;
            case "times" :chart.myChart3.resize();break;
            case "areas" :chart.myChart6.resize();break;
            case "price" :chart.myChart2.resize();break;
        }
        whir.loading.remove("mask");
    }else if(is_lg == false){
        $(this).parents(".chart_module").hide();
        var order = [];
        $(".chart_module").each(function () {
            if ($(this).attr("data-id") !== undefined && $(this).css("display") == "block") {
                order.push($(this).attr("data-id"));
            }
        });
        chartShow(order);
    }
});
