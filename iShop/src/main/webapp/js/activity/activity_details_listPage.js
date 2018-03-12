/**
 * Created by Administrator on 20170519/3/14.
 */
var oc=new ObjectControl();
var inx=1;//默认是第一页
var count=1;
var pageNumber=1;//删除默认第一页
var pageSize=10;//默认传的每页多少行
var _param={};//筛选定义的内容
var list_size=20;
var has_next_list=false;
var list_data=[];
var filtrate="";//筛选的定义的值
var area_num = 1;
var area_next = false;
var shop_num = 1;
var shop_next = false;
var staff_num = 1;
var staff_next = false;
var bd=[];//品牌
var ar=[];//区域
var sp=[];//店铺
var sf=[];//员工
var nowScreen=[];
var isscroll=false;
var corp_code=$("#tabs").children().eq(0).attr("data-corp");
var page_achievement_screen={
    store_code:"",
    store_name:"",
    user_code:"",
    user_name:""
};
var page_vip_detail_screen={
    "vip_name":"",
    "cardno":"",
    "vip_phone":"",
    "vip_card_type":""
};
var  message={
    cache:{//缓存变量
        "group_codes":"",
        "brand_codes":"",
        "area_codes":"",
        "store_codes":""

    }
};
var cache = {
    'tabType':'',
    'tabPeople':''
};
//返回
$('#activityVipList #toPre').unbind('click').bind('click', function () {
    $('#activitySetPage').show();
    $('#activitySet').show();
    $('#activityVipListPage').hide();
    $('#activityVipList').hide();
})

//页面加载时list请求
function GET(inx,pageSize){
    whir.loading.add("",0.5);//加载等待框
    var param={};
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["corp_code"]=corp_code;
    param["screen"]=sessionStorage.getItem("target_vips");
    oc.postRequire("post","/vip/vipScreen","0",param,function(data){
        if(data.code=="0"){
            $("#vip_list ul").empty();
            var messages=JSON.parse(data.message);
            var list=messages.all_vip_list;
            count=messages.pages;
            var pageNum = messages.pageNum;
            $("#num").html(messages.count);
            superaddition(list,pageNum);
            filtrate="";
            $('.contion .input').val("");
            setPage($("#foot-num")[0],count,pageNum,pageSize);
        }else if(data.code=="-1"){
            whir.loading.remove();//移除加载框
            art.dialog({
                time: 3,
                lock: true,
                cancel: false,
                content: "加载失败"
            });
        }
    });
}
function superaddition(data,num){//页面加载循环
    if(data.length == 0){
        for (var a=0;a<10;a++){
            $("#vip_list ul").append(
                "<li style='height: 30px;line-height: 30px'></li>"
            )
        }
        $("#vip_list ul").children().eq(4).html("暂无会员");
        var allList=$("#vip_list li");
        for(var i=0;i<allList.length;i++){
            i%2=="0"?$(allList[i]).css("backgroundColor","#fff"):$(allList[i]).css("backgroundColor","#efefef");
        }
        setPage($("#foot-num")[0],count,inx,pageSize);
        return
    }
    if(data.length==1&&num>1){
        pageNumber=num-1;
    }else{
        pageNumber=num;
    }
    for (var i = 0; i < data.length; i++) {
        $("#vip_list ul").append(
            "<li>"
            +"<span>"+((pageNumber-1)*10+i+1)+"</span>"
            +"<span>"+data[i].vip_name+"</span>"
            +"<span>"+data[i].vip_phone+"</span>"
            +"<span>"+data[i].vip_card_type+"</span>"
            +"<span>"+data[i].cardno+"</span>"
            +"</li>"
        );
    }
    var allList=$("#vip_list li");
    for(var i=0;i<allList.length;i++){
        i%2=="0"?$(allList[i]).css("backgroundColor","#fff"):$(allList[i]).css("backgroundColor","#efefef");
    }
    whir.loading.remove();//移除加载框
}
function setPage(container, count, pageindex,pageSize,condition){
    count==0?count=1:'';
    var container = container;
    var count = count;
    var pageindex = pageindex;
    var pageSize=pageSize;
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
            dian(inx,pageSize,condition);
            return false;
        };
        for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
            oAlink[i].onclick = function() {
                inx = parseInt(this.innerHTML);
                dian(inx,pageSize,condition);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function() { //点击下一页
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx,pageSize,condition);
            return false;
        }
    }()
}
function dian(a,b,condition){//点击分页的时候调什么接口
    if(typeof (_param["screen"])=="string"){
        return
    }
    if($("#listTitle").text() == "参与会员列表"){
        GET(a,b)
    }else {
        getGoodsList(a,condition);
    }
}
//筛选封装
function screen(i,val,tempHTML){ //数字该筛选字段在li显示的eq()位置
    var num = 0;  //显示的条数
    $('#signUpPeople li').each(function () {
        var checkVal =  $(this).find('span').eq(i).text();
        var display = $(this).css('display');
        if(checkVal.indexOf(val) !=  -1 && display != 'none'){
            $(this).show();
        }else{
            $(this).hide();
        }
    });
    $('#signUpPeople li').each(function () {
        if( $(this).css('display') != 'none'){
            console.log($(this).css('display'))
        }
    });
    if(num == 0){
        $('#signUpPeople').append(tempHTML);
    }
}
//清空筛选
$('#page_signUp .empty').click(function () {
    Num = 1;
    $('#page_signUp .sxk input').val('');
    $("#page_signUp .peopleContent ul").empty();
    getSignUpList();
})
//**********************************业绩占比*********************************************
$('#achievement_percent').click(function () {
    cache.tabType = '#page_achievement';
    cache.tabPeople = '#achievementPeople';
    Num=1;
    has_next_list=false;
    page_achievement_screen={
        store_code:"",
        store_name:"",
        user_code:"",
        user_name:""
    };
    $('#page_achievement').show();
    $("#page_achievement .task_box .listLoading_box").show();//加载遮罩
    achievementView();//图表
    $('#page_achievement .inputs ul').html('<li><label>店铺编号</label><input type="text" placeholder="请输入店铺编号" class="store_code"><li> <label>店铺名称</label><input class="store_name" type="text" placeholder="请输入店铺名称"> </li></li>');
    var type = $('#page_achievement .btnSecond span').eq(0).attr('class') =='doType_active'?'store':'user';
    achievementList(type);//列表
});
//点击加载
$('#page_achievement .btnSecond span').eq(0).click(function () {
    if($(this).hasClass("doType_active")){
        return
    }
    has_next_list=false;
    $(this).attr('class','doType_active');
    $(this).next().attr('class','doType');
    Num=1;
    page_achievement_screen={
        store_code:"",
        store_name:"",
        user_code:"",
        user_name:""
    };
    $('#page_achievement .inputs ul').html(' <li> <label>店铺编号</label><input type="text" placeholder="请输入店铺编号" class="store_code"> </li><li> <label>店铺名称</label><input class="store_name" type="text" placeholder="请输入店铺名称"> </li>');
    achievementList('store');//列表
    $(this).parent().find('.choose').text() == '收起筛选'? $(this).parent().find('.choose').click():'';
});
function achievementView(){
    var param={"activity_code":sessionStorage.getItem("activity_code")};
    oc.postRequire("post", '/activityAnaly/achvRate',"0",param,function(data){ //饼图
        if(data.code==0){
            var msg = JSON.parse(data.message);
            achievementPie(msg);
            $("#page_achievement .task_box .listLoading_box").eq(0).hide();
        }else{
            console.log(data.message);
        }
    });
    oc.postRequire("post", '/activityAnaly/achvView',"0",param,function(data){ //柱状图
        if(data.code==0){
            var msg = JSON.parse(data.message);
            achievementAxis(msg);
            $("#page_achievement .task_box .listLoading_box").eq(1).hide();
        }else{
            console.log(data.message);
        }
    });
}
function vipDetailView(){  //会员影响力分析 图表数据
    var param={"activity_code":sessionStorage.getItem("activity_code")};
    oc.postRequire("post", '/activityAnaly/vipEffectRate',"0",param,function(data){ //饼图
        if(data.code==0){
            var msg = JSON.parse(data.message);
            vipDetailPie(msg);
            $("#vip_effect_content .task_box .listLoading_box").eq(0).hide();
        }else{
            console.log(data.message);
        }
    });
    oc.postRequire("post", '/activityAnaly/vipEffectChat',"0",param,function(data){ //柱状图
        $("#vip_effect_content .task_box .listLoading_box").eq(1).hide();
        if(data.code==0){
            var msg = JSON.parse(data.message);
            var list = msg.share_chat;
            var click_list = msg.invite_chat;
            var data_x_arr = [];
            var data_y_arr = [];
            var data_y_click_arr = [];
            if(list.length == 0 && click_list.length == 0){
                $("#vip_effect_chart").html("暂无数据");
            }else if(list.length > 0){
                for(var i=0;i<list.length;i++){
                    data_x_arr.push(list[i].date);
                    data_y_arr.push(list[i].count);
                }
                for(var j=0;j<click_list.length;j++){
                    data_y_click_arr.push(click_list[j].count);
                }
                vipDetailAxis(data_x_arr,data_y_arr,data_y_click_arr);
            }
        }else{
            console.log(data.message);
        }
    });
}
//视图
function  achievementList(type){
    //whir.loading.add("", 0.5);
    $('#load_1').show();

    var param={
        "activity_code":sessionStorage.getItem("activity_code"),
        "pageNumber":Num,
        "pageSize":list_size,
        "type":type,
        "screen":page_achievement_screen
    };
    if(Num==1){
        $("#achievementPeople").html("");
        list_data=[];
    }
    oc.postRequire("post", '/activityAnaly/achvList',"0",param,function(data){
        if(data.code==0){
            var msg = JSON.parse(data.message);
            var list = msg.list;
            list_data=list_data.concat(list);
            if(Num>=msg.total_page){
                has_next_list=false
            }else{
                has_next_list=true;
            }
            setAchievementList(list,type);
        }else {
            console.log(data.message);
        }
    });
}   //列表
 function  vipDetailList(type){  //会员影响力分析
     $("#vip_effect_content .list_box .listLoading_box").show();
    var param={
        "activity_code":sessionStorage.getItem("activity_code"),
        "page_num":Num,
        "page_size":list_size,
        "screen":page_vip_detail_screen
    };
    if(Num==1){
        $("#vip_effect_list").html("");
        list_data=[];
    }
    oc.postRequire("post", '/activityAnaly/vipEffectList',"0",param,function(data){
        if(data.code==0){
            var msg = JSON.parse(data.message);
            var list = msg.list;
            list_data=list_data.concat(list);
            if(Num>=msg.pages){
                has_next_list=false
            }else{
                has_next_list=true;
            }
            $("#vip_effect_list").attr("data-app_id",msg.app_id);
            setvipDetailList(list,type);
        }else {
            console.log(data.message);
        }
    });
}   //列表
function achievementPie(msg) {
    var vip_trade = msg.vip_trade;
    var amt_trade = msg.amt_trade;
    var no_trade = amt_trade - vip_trade ;
    (vip_trade==0&&no_trade==0)?no_trade=1:"";
    $('#page_achievement .shoppers').eq(0).text(formatCurrency(vip_trade));
    $('#page_achievement .target').text(formatCurrency(amt_trade));
    var myChart = echarts.init(document.getElementById('pie_achievement'));
    option = {
        color: ['#7bc7cd', '#eaeaea'],
        tooltip: {
            show:false,
            trigger: 'item',
            formatter: "{a} <br/>{b}: {c} ({d}%)"
        },
        legend: {
            show:false,
            data:['活动金额','非活动金额']
        }, grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        series: [
            {
                name:'活动占比',
                type:'pie',
                radius: ['55%', '70%'],
                avoidLabelOverlap: false,
                labelLine: {
                    normal: {
                        show: false
                    }
                },
                data:[
                    {value:vip_trade, name:'活动金额',itemStyle: {
                        normal: {
                            label: {
                                show: true,
                                position: 'center',
                                formatter: '{d}%',
                                textStyle: {
                                    fontSize:18
                                }
                            },
                            labelLine : {
                                show : false
                            }
                        }
                    }},
                    {value:no_trade, name:'非活动金额',itemStyle: {
                        normal: {
                            label : {
                                show : false,
                                position : 'center'
                            }
                        },
                            emphasis: {
                                color: '#eaeaea'
                            }
                        }
                    }
                ]
            }
        ]
    }
    myChart.setOption(option);
    window.addEventListener("resize", function () {
        myChart.resize();
    });
}  //饼图
 function vipDetailPie(msg) {  //会员影响力分析 占比图
    var vip_register = msg.invite_num;
    var share_count = msg.share_num;
    var unSuccess = share_count - vip_register ;
     unSuccess=unSuccess<0?"0":unSuccess;
     var bi=share_count==0?"0%":((vip_register/share_count)*100).toFixed(2)+"%";
    $('#registered_members_num').text(formatCurrency(vip_register));
    $('#link_open').text(formatCurrency(share_count));
    var myChart = echarts.init(document.getElementById('invitation_success_rate'));
    option = {
        color: ['#7bc7cd', '#eaeaea'],
        tooltip: {
            show:false,
            trigger: 'item',
            formatter: "{a} <br/>{b}: {c} ({d}%)"
        },
        legend: {
            show:false,
            data:['会员注册数','会员未注册数']
        }, grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
        },
        series:
            [{
                name:'邀请成功率',
                type:'pie',
                radius: ['55%', '70%'],
                avoidLabelOverlap: false,
                labelLine: {
                    normal: {
                        show: false
                    }
                },
                data:[
                    {value:vip_register, name:'注册会员数',itemStyle: {
                        normal: {
                            label: {
                                show: true,
                                position: 'center',
                                formatter: bi,
                                textStyle: {
                                    fontSize:18,
                                }
                            },
                            labelLine : {
                                show : false
                            }
                        }
                    }},
                    {value:unSuccess, name:'未注册会员数',itemStyle: {
                        normal: {
                            label : {
                                show : false,
                                position : 'center'
                            }
                        },
                            emphasis: {
                                color: '#eaeaea'
                            }
                        }
                    }
                ]
            }]
    };
     if(vip_register==0 && share_count==0){
        var data=[
            {value:vip_register, name:'注册会员数',itemStyle: {
                normal: {
                    label: {
                        show: true,
                        position: 'center',
                        formatter: bi,
                        textStyle: {
                            fontSize:18,
                            color:'#7bc7cd'
                        }
                    },
                    labelLine : {
                        show : false
                    }
                },
                emphasis: {
                    color: '#eaeaea'
                }
            }}
        ];
         option.series[0]["data"]=data;
         option.color= ['#eaeaea']
     }
    myChart.setOption(option);
    window.addEventListener("resize", function () {
        myChart.resize();
    });
}  //饼图
function achievementAxis(msg) {
    var msg = msg.achvView;
    var myChart = echarts.init(document.getElementById('axis_achievement'));
    var dateArr = [];
    var countArr = [];
    var countArr2 = [];
    var percentArr = []; //百分比
    var dataZoom = [];
    for(i=0;i<msg.length;i++){
        dateArr.push(msg[i].date);
        countArr.push(msg[i].vip_trade != ''? msg[i].vip_trade : 0);
        countArr2.push(msg[i].amt_trade != ''?msg[i].amt_trade : 0);
        percentArr.push(toPoint(msg[i].rate) != ''? toPoint(msg[i].rate) : 0);
    }
    if(dateArr.length>0){
        dataZoom = [
            {
                type: 'slider',
                show: true,
                start:0,
                end: 100,
                handleSize: 8,
                height:15,
                bottom:0
            }
        ]
    }
    //countArr == '' ? countArr.push(0) :'';
    //countArr2 == '' ? countArr.push(0) :'';
    option = {
        tooltip : {
            trigger: 'axis',
            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                type : ''        // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        legend: {
            right:"33%",
            top:"2%",
            type: 'category',
            data: ['活动占比','活动金额','销售总额']
        },
        dataZoom: dataZoom,
        grid: {
            left: '3%',
            right: '4%',
            bottom: '7%',
            containLabel: true
        },
        xAxis : [
            {
                type : 'category',
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                data : dateArr
            }
        ],
        yAxis : [
            {
                type : 'value',
                name: '金额/元',
                axisLabel : {
                    formatter: '{value} '
                },
                maxLine:'6',
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                splitLine: {
                    lineStyle: {
                        type: 'dashed'
                    }
                },
                interval:'auto'
            },{
                type : 'value',
                //min: 0,
                name: '占比',
                axisLabel : {
                    formatter: '{value} '
                },
                max:'1',
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                splitLine: {
                    lineStyle: {
                        type: 'dashed'
                    }
                },
                interval:0
            }
        ],
        series : [{
                name:'活动金额',
                barMaxWidth:'15px',
                type:'bar',
                itemStyle:{
                    normal:{
                        color:"#41c7db"
                    }
                },
                data:countArr
            },{
                name:'销售总额',
                barMaxWidth:'15px',
                type:'bar',
                itemStyle:{
                    normal:{
                        color:"#94b2da"
                    }
                },
                data:countArr2
            },{
            name:'活动占比',
            type:'line',
            itemStyle : {  /*设置折线颜色*/
                normal : {
                    color:'#9986c7'
                }
            },
            data:percentArr,
            yAxisIndex: 1
        }]
    };
    myChart.setOption(option);
    window.addEventListener("resize", function () {
        myChart.resize();
    });
} //柱状图
function vipDetailAxis(x,count,click) {     //会员影响力分析  柱状图
     var myChart = echarts.init(document.getElementById('vip_effect_chart')),dataZoom = [];
     if(x.length>8){
         dataZoom = [
             {
                 type: 'slider',
                 show: true,
                 start:0,
                 end: 100,
                 handleSize: 8,
                 height:15,
                 bottom:0
             }
         ]
     }
     option = {
         title: {
             text: '注册人数统计',
             top:10,
             left:30,
             textStyle:{
                 color:'#abb4c2',
                 fontSize:12
             }
         },
         tooltip : {
             trigger: 'axis',
             axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                 type : ''        // 默认为直线，可选为：'line' | 'shadow'
             }
         },
         dataZoom: dataZoom,
         legend: {
             right:20,
             top:"2%",
             type: 'category',
             data: ['分享次数','注册会员数']
         },
         grid: {
             left: '3%',
             right: '4%',
             bottom: '10%',
             containLabel: true
         },
         xAxis : [
             {
                 type : 'category',
                 axisTick: {
                     show: false
                 },
                 axisLine: {
                     show: false
                 },
                 data : x
             }
         ],
         yAxis : { splitLine: {
             lineStyle: {
                    type: 'dashed'
                 }
             },
             axisLine: {
                 show: false
             },
             axisTick: {
                 show: false
             },
             axisLabel: {
                 textStyle: {
                     color: '#999'
                 }
             }},
         series : [{
             name:'分享次数',
             barMaxWidth:'15px',
             type:'bar',
             itemStyle:{
                 normal:{
                     color:"#c7afd7"
                 }
             },
             data:count
         },{
             name:'注册会员数',
             barMaxWidth:'15px',
             type:'bar',
             itemStyle:{
                 normal:{
                     color:"#5f8bc8"
                 }
             },
             data:click
         }]
     };
    myChart.setOption(option);
    window.addEventListener("resize", function () {
        myChart.resize();
    });
} //柱状图
function  setAchievementList(list,type) {
    var titleHTML1 = '<li><span title="序号" style="width: 4%">序号</span> <span title="店铺编号">店铺编号</span><span title="店铺名称">店铺名称</span><span title="总业绩">总业绩</span> <span title="活动金额">活动金额</span><span title="会销金额">会销金额</span><span title="会员总数">会员总数</span><span title="新增会员数">新增会员数</span><span title="销售件数">销售件数</span><span title="成交笔数">成交笔数</span><span title="业绩详情" style="width: 6%">业绩详情</span></li> ';
    var titleHTML2 = '<li><span title="序号" style="width: 4%">序号</span> <span title="员工编号">员工编号</span><span title="员工姓名">员工姓名</span><span title="所属店铺">所属店铺</span> <span title="店铺编号">店铺编号</span><span title="总业绩">总业绩</span><span title="会销金额">会销金额</span><span title="会员总数">会员总数</span><span title="新增会员数">新增会员数</span><span title="销售件数">销售件数</span><span title="成交笔数">成交笔数</span><span title="业绩详情" style="width: 6%">业绩详情</span></li> ';
    var tempHTML1 = '<li> <span style="width: 4%">${no}</span> <span>${shop_no}</span><span title="${shopName}">${shopName}</span><span>${all_sale}</span> <span>${money}</span><span>${willPin}</span><span>${allvips}</span><span>${activityvips}</span><span>${num_sale}</span><span>${sale_num}</span><span style="cursor: pointer;color: #5f8bc8;width: 6%" onclick="achievementDetail(this,0)">详情 <lable class="icon-ishop_8-03" style="color:#5f8bc8"></lable></span></span></li>';
    var tempHTML2 = '<li> <span style="width: 4%">${no}</span> <span>${shopper_no}</span><span>${shopper_name}</span><span title="${own_shop}">${own_shop}</span> <span title="${shop_no}">${shop_no}</span><span>${all_sale}</span><span>${willPin}</span><span>${allvips}</span><span>${activityvips}</span><span>${num_sale}</span><span>${sale_num}</span><span style="cursor: pointer;color: #5f8bc8;width: 6%" onclick="achievementDetail(this,1)">详情 <lable class="icon-ishop_8-03" style="color:#5f8bc8"></lable></span></li>'
    var html = '';
    if(type == 'store'){
        $('#achievementPeople').prev('.people_title_temp').html(titleHTML1);

    }else if(type =='user'){
        $('#achievementPeople').prev('.people_title_temp').html(titleHTML2);
        $('#page_achievement .people_area li span').css('width','9%');
        $('#page_achievement .people_area li span').eq(0).css('width','4%');
        $('#page_achievement .people_area li span:last-child').css('width','6%');
    }
    if (list.length == 0 || list =='') {
        $('#load_1').hide();//移除加载框
        $("#achievementPeople").html( '<li class="peopleError" style="background-color: white;"> <div> 没有相关数据 </div> </li>');
        return;
    }
    var len=20*(Num-1);
   if(type == 'store'){
        for (i = 0; i < list.length; i++) {
            var nowHTML =tempHTML1;
            nowHTML = nowHTML.replace('${no}', len+i+1);
            nowHTML = nowHTML.replace('${shopName}',list[i].store_name);//店铺名称
            nowHTML = nowHTML.replace('${shopName}',list[i].store_name);//店铺名称
            nowHTML = nowHTML.replace('${shop_no}', list[i].store_code);//店铺编号
            nowHTML = nowHTML.replace('${shop_no}', list[i].store_code);//店铺编号
            nowHTML = nowHTML.replace('${all_sale}',formatCurrency(list[i].amt_trade));//总业绩
            nowHTML = nowHTML.replace('${money}', formatCurrency(list[i].activity_money));//活动金额
            nowHTML = nowHTML.replace('${willPin}', formatCurrency(list[i].vip_money));//会销金额
            nowHTML = nowHTML.replace('${allvips}', formatCurrency(list[i].all_new_vip));//新增总会员数
            nowHTML = nowHTML.replace('${activityvips}', formatCurrency(list[i].activity_new_vip));//活动新增会员数
            nowHTML = nowHTML.replace('${num_sale}', formatCurrency(list[i].num_sale));//销售件数
            nowHTML = nowHTML.replace('${sale_num}', formatCurrency(list[i].num_trade));//成交笔数
            html += nowHTML;
        }
    }else if(type == 'user'){
        for (i = 0; i < list.length; i++) {
            var nowHTML =tempHTML2;
            nowHTML = nowHTML.replace('${no}', len+i+1);
            nowHTML = nowHTML.replace('${shopper_name}', list[i].user_name);//导购名称
            nowHTML = nowHTML.replace('${shopper_no}', list[i].user_code);//导购编号
            nowHTML = nowHTML.replace('${shopper_no}', list[i].user_code);//导购编号
            nowHTML = nowHTML.replace('${own_shop}', list[i].store_name);//所属店铺
            nowHTML = nowHTML.replace('${own_shop}', list[i].store_name);//所属店铺
            nowHTML = nowHTML.replace('${shop_no}', list[i].store_code);//店铺编号
            nowHTML = nowHTML.replace('${shop_no}', list[i].store_code);//店铺编号
            nowHTML = nowHTML.replace('${all_sale}', list[i].amt_trade);//总业绩
            nowHTML = nowHTML.replace('${willPin}', list[i].vip_money);//会销金额
            nowHTML = nowHTML.replace('${allvips}', list[i].all_new_vip);//新增总会员数
            nowHTML = nowHTML.replace('${activityvips}', list[i].activity_new_vip);//活动新增会员数
            nowHTML = nowHTML.replace('${num_sale}', list[i].num_sale);//销售件数
            nowHTML = nowHTML.replace('${sale_num}', list[i].num_trade);//成交笔数
            html += nowHTML;
        }
    }
    $('#achievementPeople').append(html);
    //whir.loading.remove();//移除加载框
    $('#load_1').hide();
}
function  setvipDetailList(list) {  //会员影响力分析  列表
    var tempHTML2 = '<li> <span style="width: 4%">${no}</span> <span class="vip_name">${vip_name}</span><span class="vip_card">${vip_card}</span><span>${vip_phone}</span><span>${vip_type}</span><span>${share_count}</span><span>${registe_count}</span><span data-openid="${openid}"  style="cursor: pointer;color: #5f8bc8;"onclick="vipDetail(this)">详情 <lable class="icon-ishop_8-03" style="color:#5f8bc8"></lable></span></li>';
    var html = '';
    if (list.length == 0 || list =='') {
        $("#vip_effect_content .list_box .listLoading_box").eq(0).hide()
        $("#vip_effect_list").html( '<li class="peopleError" style="background-color: white;"> <div> 没有相关数据 </div> </li>');
        return;
    }
    var len=20*(Num-1);
        for (i = 0; i < list.length; i++) {
            var nowHTML =tempHTML2;
            nowHTML = nowHTML.replace('${no}', len+i+1);
            nowHTML = nowHTML.replace('${vip_name}', list[i].vip_name);//会员名称
            nowHTML = nowHTML.replace('${vip_card}', list[i].cardno);//会员卡号
            nowHTML = nowHTML.replace('${vip_phone}', list[i].vip_phone);//会员手机号
            nowHTML = nowHTML.replace('${vip_type}', list[i].vip_card_type);//会员类型
            nowHTML = nowHTML.replace('${share_count}', formatCurrency(list[i].share_num));//邀请次数
            nowHTML = nowHTML.replace('${registe_count}', formatCurrency(list[i].invite_num));//注册会员数
            nowHTML = nowHTML.replace('${openid}', list[i].open_id);
            html += nowHTML;
        }
    $('#vip_effect_list').append(html);
    $('#vip_effect_list li').css('overflow','hidden');
    //whir.loading.remove();//移除加载框
    $("#vip_effect_content .list_box .listLoading_box").eq(0).hide()
}
function vipDetail(dom){
    $("#vip_consume_detail_list").html("");
    $("#detail_vip_name").html($(dom).siblings(".vip_name").html());
    $("#detail_vip_card").html($(dom).siblings(".vip_card").html());
    $("#vip_consume_detail").show();
    var param={
        app_id:$("#vip_effect_list").attr("data-app_id"),
        open_id:$(dom).attr("data-openid"),
        activity_code:sessionStorage.getItem("activity_code")
    };
    whir.loading.add("","0.5");
    oc.postRequire("post","/activityAnaly/registerList","0",param,function(data){
        whir.loading.remove();
        var list=JSON.parse(data.message).list;
        setVipConsumeDetailList(list)
    })
}
function setVipConsumeDetailList(list){
    var html="";
    $("#vip_consume_detail_list").removeClass("not_data");
    if(list.length==0){
        $("#vip_consume_detail_list").html("暂无数据");
        $("#vip_consume_detail_list").addClass("not_data")
    }else{
        for(var l=0;l<list.length;l++){
            var a=l+1;
            html+='<div>'
                    +'<ul>'
                    +'<li>'+a+'</li>'
                    +'<li title="'+list[l].vip_name+'">'+list[l].vip_name+'</li>'
                    +'<li title="'+list[l].cardno+'">'+list[l].cardno+'</li>'
                    +'<li title="'+list[l].vip_phone+'">'+list[l].vip_phone+'</li>'
                    +'<li title="'+list[l].vip_card_type+'">'+list[l].vip_card_type+'</li>'
                    +'<li title="'+list[l].store_name+'">'+list[l].store_name+'</li>'
                    +'<li title="'+list[l].user_name+'">'+list[l].user_name+'</li>'
                    +'<li>暂无</li>'
                    +'</ul>'
                +'</div>'
        }
        $("#vip_consume_detail_list").html(html);
        $("#vip_consume_detail_list").getNiceScroll().show();
        $("#vip_consume_detail_list").getNiceScroll().resize();
    }
}
function achievementDetail(dom,val){
    type =  val == '0'? 'store':'user';
    $('#table_achievement_area').show();
    var val = $(dom).parent().find('span').eq(1).text();
            var list=list_data;
            for(var i=0;i<list.length;i++){
                if(type == 'store'){
                    if(list[i].store_code ==val){   //store
                        var area_name = list[i].area_name != undefined? (list[i].area_name != ''?list[i].area_name:'区域为空'):'暂无区域';
                        var discount = Math.floor(list[i].discount * 100) / 100;
                        var trade_price = Math.floor(list[i].trade_price * 100) / 100;
                        var sale_price = Math.floor(list[i].sale_price * 100) / 100;
                        var relate_rate = Math.floor(list[i].relate_rate * 100) / 100;
                        var activity_discount = Math.floor(list[i].activity_discount * 100) / 100;
                        var activity_customer_unit_price = Math.floor(list[i].activity_customer_unit_price * 100) / 100;
                        var activity_pieces_unit_price = Math.floor(list[i].activity_pieces_unit_price * 100) / 100;
                        var activity_joint_rate = Math.floor(list[i].activity_joint_rate * 100) / 100;
                        var vip_discount = Math.floor(list[i].vip_discount * 100) / 100;
                        var vip_trade_price = Math.floor(list[i].vip_trade_price * 100) / 100;
                        var vip_sale_price = Math.floor(list[i].vip_sale_price * 100) / 100;
                        var vip_relate_rate = Math.floor(list[i].vip_relate_rate * 100) / 100;
                        var activity_sale = list[i].activity_sale ;
                        var activity_trade = list[i].activity_trade;
                        $('#table_achievement_area #achievement_shopName').text('店铺：'+list[i].store_name);
                        $('#table_achievement_area #achievement_shopName').attr('title','店铺：'+list[i].store_name);
                        $('#table_achievement_area #achievement_area').text(area_name);
                        $('#table_achievement_area #achievement_area').attr('title',area_name);
                        var span = $('#table_achievement_area li span');
                        span.eq(0).text(discount);
                        span.eq(1).text(formatCurrency(trade_price));
                        span.eq(2).text(formatCurrency(sale_price));
                        span.eq(3).text(relate_rate);
                        span.eq(4).text(activity_discount);
                        span.eq(5).text(formatCurrency(activity_customer_unit_price));
                        span.eq(6).text(formatCurrency(activity_pieces_unit_price));
                        span.eq(7).text(activity_joint_rate);
                        span.eq(8).text(vip_discount);
                        span.eq(9).text(formatCurrency(vip_trade_price));
                        span.eq(10).text(formatCurrency(vip_sale_price));
                        span.eq(11).text(vip_relate_rate);
                        span.eq(12).text(formatCurrency(activity_sale));
                        span.eq(13).text(formatCurrency(activity_trade))
                    }
                }else if(type == 'user'){
                    if(list[i].user_code ==val){  //user
                        var discount = Math.floor(list[i].discount * 100) / 100;
                        var trade_price = Math.floor(list[i].trade_price * 100) / 100;
                        var sale_price = Math.floor(list[i].sale_price * 100) / 100;
                        var relate_rate = Math.floor(list[i].relate_rate * 100) / 100;
                        var activity_discount = Math.floor(list[i].activity_discount * 100) / 100;
                        var activity_customer_unit_price = Math.floor(list[i].activity_customer_unit_price * 100) / 100;
                        var activity_pieces_unit_price = Math.floor(list[i].activity_pieces_unit_price * 100) / 100;
                        var activity_joint_rate = Math.floor(list[i].activity_joint_rate * 100) / 100;
                        var vip_discount = Math.floor(list[i].vip_discount * 100) / 100;
                        var vip_trade_price = Math.floor(list[i].vip_trade_price * 100) / 100;
                        var vip_sale_price = Math.floor(list[i].vip_sale_price * 100) / 100;
                        var vip_relate_rate = Math.floor(list[i].vip_relate_rate * 100) / 100;
                        var activity_sale = list[i].activity_sale ;
                        var activity_trade = list[i].activity_trade;
                        $('#table_achievement_area #achievement_shopName').text('导购：'+list[i].user_name);
                        $('#table_achievement_area #achievement_shopName').attr('title','导购：'+list[i].user_name);
                        $('#table_achievement_area #achievement_area').text(list[i].store_name);
                        $('#table_achievement_area #achievement_area').attr('title',list[i].store_name);
                        var span = $('#table_achievement_area li span');
                        span.eq(0).text(discount);
                        span.eq(1).text(trade_price);
                        span.eq(2).text(sale_price);
                        span.eq(3).text(relate_rate);
                        span.eq(4).text(activity_discount);
                        span.eq(5).text(activity_customer_unit_price);
                        span.eq(6).text(activity_pieces_unit_price);
                        span.eq(7).text(activity_joint_rate);
                        span.eq(8).text(vip_discount);
                        span.eq(9).text(vip_trade_price);
                        span.eq(10).text(vip_sale_price);
                        span.eq(11).text(vip_relate_rate);
                        span.eq(12).text(activity_sale);
                        span.eq(13).text(activity_trade);
                    }
                }
            }
}
//业绩占比-筛选（回车事件）
$("#page_achievement .inputs").on("keydown","input",function (e) {
    var event = e || window.event;
    if(event.keyCode == 13){
        has_next_list=false;
        Num=1;
        var type = $('#page_achievement .btnSecond span').eq(0).attr('class') =='doType_active'?'store':'user';
        page_achievement_screen={
            store_code:$("#page_achievement .store_code").val().trim(),
            store_name:$("#page_achievement .store_name").val().trim(),
            user_code:type=="user"?$("#page_achievement .user_code").val().trim():"",
            user_name:type=="user"?$("#page_achievement .user_name").val().trim():""
        };
        achievementList(type)
    }
});
//筛选封装
function screen(i,val,tempHTML,areaPeople){ //数字该筛选字段在li显示的eq()位置
    var num = 0;  //显示的条数
    var check = 0;
    areaPeople.find('li').each(function () {
        var checkVal =  $(this).find('span').eq(i).text();
        var display = $(this).css('display');
        if(checkVal.indexOf(val) !=  -1 && display != 'none'){
            $(this).show();
            if(check == 0){
                $(this).find('span').css('background-color','white');
                check+=1;
            }else if(check == 1){
                $(this).find('span').css('background-color','#efefef');
                check-=1;
            }
        }else{
            $(this).hide();
        }
    });
    if(num == 0){
        areaPeople.append(tempHTML);
    }
}
//清空筛选
$('#page_achievement .empty').click(function () {
    has_next_list=false;
    Num=1;
    $('#page_achievement .sxk input').val('');
    var type = $('#page_achievement .btnSecond span').eq(0).attr('class') =='doType_active'?'store':'user';
    page_achievement_screen={
        store_code:"",
        store_name:"",
        user_code:"",
        user_name:""
    };
    achievementList(type)

});
$('#vip_effect_content .empty').click(function () {
    has_next_list=false;
    Num=1;
    $('#vip_effect_content .sxk input').val('');
    page_vip_detail_screen={
        "vip_name":"",
        "cardno":"",
        "vip_phone":"",
        "vip_card_type":""
    };
    vipDetailList();

});
//保留两位小数
function math(num){
    Math.floor(num * 100) / 100;

}
//百分比转小数
function toPoint(percent){
    var str=percent.replace("%","");
    str= str/100;
    return str;
}
//*****************会员影响力分析************
$('#vip_effect').click(function () {
    cache.tabType = '#vip_effect_content';
    cache.tabPeople = '#vip_effect_list';
    Num=1;
    has_next_list=false;
    page_vip_detail_screen={"vip_name":"","cardno":"","vip_phone":"","vip_card_type":""};
    $('#page_achievement').show();
    $("#vip_effect_content .task_box .listLoading_box").show();//加载遮罩
    vipDetailView();//图表
    //$('#page_achievement .inputs ul').html('<li><label>店铺编号</label><input type="text" placeholder="请输入店铺编号"><li> <label>店铺名称</label><input type="text" placeholder="请输入店铺名称"> </li></li>');
    vipDetailList();//列表
});
//导购影响分筛选
$("#vip_effect_content .inputs input").keydown(function (e) {
    var event = e || window.event;
    if(event.keyCode == 13){
        Num = 1;
        $("#influence .peopleContent ul").empty();
        page_vip_detail_screen={
            "vip_name":$("#vip_name").val().trim(),
            "cardno":$("#vip_card").val().trim(),
            "vip_phone":$("#vip_phone").val().trim(),
            "vip_card_type":$("#vip_card_type").val().trim()
        };
        vipDetailList();
    }
});
$("#hide_detail").click(function(){
    $("#vip_consume_detail").hide();
    $("#vip_consume_detail_list").getNiceScroll().hide();
});