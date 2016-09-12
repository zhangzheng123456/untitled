var oc = new ObjectControl();
var page=1;
var area_code;
var store_code;
var corp_code="C10000";
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
function GetArea(){
    var searchValue=$('#select_analyze input').val();
    var param={};
    param['pageNumber']=page;
    param['pageSize']="7";
    param['searchValue']=searchValue;
    oc.postRequire("post","/area/findAreaByCorpCode","",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);//��ȡmessagejson�����DOM����
            var message=JSON.parse(data.message);//��ȡmessagejson�����DOM����
            var message=JSON.parse(data.message);//��ȡmessagejson�����DOM����
            var role_code=message.role_code;//��ɫ����
            var message=JSON.parse(data.message);//???messagejson?????DOM????
            var role_code=message.role_code;//???????
            var output=JSON.parse(message.list);
            var ul='';
            var first_area='';
            var first_area_code='';
            var output_list=output.list;
            console.log(output_list);
            output_list.length>7? $('#select_analyze s').attr('style','display:block'): $('#select_analyze s').attr('style','display:none');
            first_area=output_list[0].area_name;
            first_area_code=output_list[0].area_code;
            for(var i= 0;i<output_list.length;i++){
                ul+="<li data_area='"+output_list[i].area_code+"'>"+output_list[i].area_name+"</li>";
            }
            $('#side_analyze ul li:nth-child(2) s').html(first_area);
            $('#side_analyze ul li:nth-child(2) s').attr('data_area',first_area_code);
            var area_code=output_list[0].area_code;
            localStorage.setItem('area_code',area_code);
            //清除内容店铺下拉列表
            $('#select_analyze_shop ul').html('');
            getStore(area_code);
            $('#select_analyze ul').append(ul);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    });
}
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
        var first_corp_name='';
        var first_corp_code='';
        var message=JSON.parse(data.message);//��ȡmessagejson�����DOM����
        var message=JSON.parse(data.message);//��ȡmessagejson�����DOM����
        console.log(message);
        var first_store_name='';
        var first_store_code='';
        var message=JSON.parse(data.message);//???messagejson?????DOM????
        var message=JSON.parse(data.message);//???messagejson?????DOM????
        var output=JSON.parse(message.list);
        var output_list=output.list;
        first_store_name=output_list[0].store_name;
        first_store_code=output_list[0].sstore_code;
        for(var i= 0;i<output_list.length;i++){
            ul+="<li data_store='"+output_list[i].store_code+"'>"+output_list[i].store_name+"</li>";
        }
        $('#side_analyze ul li:nth-child(3) s').html( first_store_name);
        $('#side_analyze ul li:nth-child(3) s').attr('data_store',first_store_code);
        $('#select_analyze_shop ul').append(ul);
    });
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
    }else{
        var store_code=$(e).attr('data_store');
        $('#side_analyze ul li:nth-child(3) s').html($(e).html());
        $('#side_analyze ul li:nth-child(3) s').attr('data_store',store_code);
        $('#select_analyze_shop').toggle();
    }

}
// function show_select(){
//     var event=window.event||arguments[0];
//     if(event.stopPropagation){
//         event.stopPropagation();
//     }else{
//         event.cancelBubble=true;
//     }
//     console.log(this);
//     if($(this).find('b').html()=='区域'){
//         $('#select_analyze').toggle();
//         $('#select_analyze_shop').hide();
//     }else{
//         $('#select_analyze_shop').toggle()
//     }
//     // $(e.target).attr('class').indexOf('area')==-1?$('#select_analyze_shop').toggle(): $('#select_analyze').toggle();
//     //$(e.target).attr('class').indexOf('area')==-1? $('#select_analyze').css('top','100px'): $('#select_analyze').css('top','69px');
// }
//取消下拉框
$(document).on('click',function(e){
    if(e.target==$($('#side_analyze>ul')[0]).find('li:nth-child(2)')  ){
       return
    }else if(e.target==$('#select_analyze')){
       return
    }else if(e.target==$('#select_analyze div')){
       return
    }else if(e.target==$('#select_analyze div b')){
        return
    }else if(e.target==$('#select_analyze div b input')){
        return
    }else if(e.target==$('#select_analyze div b span')){
        return
    }else if(e.target==$('#select_analyze div ul')){
        return
    }else if(e.target==$('#select_analyze div ul li')){
        return
    }else if(e.target==$('#select_analyze div s')){
        return
    }else{
        $('#select_analyze').hide();
    }
    //第二个框
    if(e.target==$($('#side_analyze_shop>ul')[0]).find('li:nth-child(2)')  ){
        return
    }else if(e.target==$('#select_analyze_shop')){
        return
    }else if(e.target==$('#select_analyze_shop div')){
        return
    }else if(e.target==$('#select_analyze_shop div b')){
        return
    }else if(e.target==$('#select_analyze_shop div b input')){
        return
    }else if(e.target==$('#select_analyze_shop div b span')){
        return
    }else if(e.target==$('#select_analyze_shop div ul')){
        return
    }else if(e.target==$('#select_analyze_shop div ul li')){
        return
    }else if(e.target==$('#select_analyze_shop div s')){
        return
    }else{
        $('#select_analyze_shop').hide();
    }
});
//加载更多
function getMore(e){
    var e= e.target;
    page+=1;
    var area_code=$('#side_analyze ul li:nth-child(2) s').attr('data_area');
    console.log(area_code);
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
//绑定事件
$('#side_analyze>ul:nth-child(1) li:gt(0)').click(
    function(){
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
$().ready(function(){
    newVip_add();
    $('#select_analyze s').click(getMore);
    $('#select_analyze ul').on('click','li',showNameClick);
    $('#select_analyze_shop ul').on('click','li',showNameClick);
    //加载更多
    $('#side_analyze div s').click(getMore);
    //添加搜索
    $('#side_analyze div b span').click(searchValue);
});
/*****************************************************************************************************************/
//新入会员
function newVip_add(){
    var param={};
    param['pageNumber']=page;
    param['pageSize']=7;
    param['store_code']=store_code;
    param['corp_code']=corp_code;
    param["area_code"]=area_code;
    oc.postRequire("post","/vipAnalysis/vipNew","",param,function(data){
        console.log(data);
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
                msg=msg.new_vip_list;
            console.log(msg.length);
            if(msg.length<10){
                for(var i=0;i<msg.length;i++){
                    var a=i+1;
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

            }else if(msg.length>=10){
                for(var i=0;i<10;i++){
                    var a=i+1;
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
        }

    });
}

