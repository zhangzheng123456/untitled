/**
 * Created by Administrator on 2017/5/23.
 */
var oc = new ObjectControl();
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);
var funcCode=key_val.func_code;
var  message={
    clickMark:"",//员工里面的品牌，区域点击标志，为了关闭品牌，区域窗口后打开员工窗口；
    cache:{//缓存变量
        "area_codes":"",
        "area_names":"",
        "brand_codes":"",
        "brand_names":"",
        "store_codes":"",
        "store_names":""
    }
};
var colors=["#c1e7ed",'#41c7db',"#9999cc","#5475a2"];
var commodity_analysis={
    allData:"",
    sku_id:"",
    vip_page_num:1,
    vip_page_size:10,
    vip_next_page:false,
    shop_num:1,
    area_num:1,
    shop_next:false,
    area_next:false,
    isscroll:false,
    vip_isscroll:false,
    time_start:"",
    time_end:"",
    time:"",
    year_down:"",
    year_arr:[],
    week_obj:[],
    month_arr:[],
    corp_code:"C10234",
    store_code:"",
    page_num:1,
    page_size:10,
    param:"",
    time_type:"",
    time_value:"",
    count:"",
    option: {
    tooltip : {
        trigger: 'axis',
        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
            type : 'cross'        // 默认为直线，可选为：'line' | 'shadow'
        }
    },
    color:colors,
    legend: {
        right:"33%",
        top:"2%",
        data: ['笔数','件数','折扣','金额']
    },
    grid: {
        left: '3%',
        right: '8%',
        bottom: '3%',
        containLabel: true
    },
    xAxis :{
            //name: '性别',
            type : 'category',
            axisTick: {
                show: false
            },
            axisLine: {
                show: true,
                lineStyle:{
                    color:"#999"
                }
            },
            data : []
        },
    yAxis : [
        {
            type : 'value',
            position: 'left',
            name: '笔数',
            splitLine:{
                show:false
            },
            offset: 50,
            axisLine: {
                lineStyle: {
                    color: colors[0]
                }
            },
            axisLabel: {
                formatter: '{value} 笔'
            }
        },
        {
            type: 'value',
            name: '件数',
            position: 'left',
            minInterval: 1,
            splitLine:{
                show:false
            },
            axisLine: {
                lineStyle: {
                    color: colors[1]
                }
            },
            axisLabel: {
                formatter: '{value} 件'
            }
        },
        {
            type: 'value',
            name: '折扣',
            position: 'right',
            splitLine:{
                show:false
            },
            axisLine: {
                lineStyle: {
                    color: colors[2]
                }
            },
            axisLabel: {
                formatter: '{value} 折'
            }
        },
        {
            type: 'value',
            name: '金额',
            position: 'right',
            splitLine:{
                show:false
            },
            offset: 60,
            axisLine: {
                lineStyle: {
                    color: colors[3]
                }
            },
            axisLabel: {
                formatter: '{value} 元'
            }
        }
    ],
    series : [{
        name:'笔数',
        barMaxWidth:'15px',
        type:'bar',
        data:[]
    },{
        name:'件数',
        barMaxWidth:'15px',
        type:'bar',
        yAxisIndex: 1,
        data:[]
    },{
        name:'折扣',
        type:'line',
        yAxisIndex: 2,
        data:[]

    },{
        name:'金额',
        type:'bar',
        barMaxWidth:'15px',
        yAxisIndex: 3,
        data:[]
    }]
},
    option_map: {
        tooltip: {
            trigger: 'item'
        },
        visualMap: {
            min: 0,
            max: 2500,
            left: 'left',
            color: ['#3C95A2', '#A7CFD5'],
            top: 'bottom',
            text: ['高','低'],           // 文本，默认为数值文本
            calculable: true
        },
        series: {
                name: '金额',
                type: 'map',
                map: 'china',
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
                label: {
                    normal: {
                        show: true
                    },
                    emphasis: {
                        show: true
                    }
                },
                data:[]
            }
    },
    init:function(){
        this.bind();
        this.yearFun(this);
        this.timeFiltrate(this);
//            this.DateList();
        this.screenOperate();
        this.initData();
    },
    initData:function(){
        var len = $(".table thead tr th").length;
        var i;
        $(".table tbody").html("");
        for(i=0;i<10;i++){
            $(".table tbody").append("<tr></tr>");
            if(i==4){
                $($(".table tbody tr")[i]).append("<td colspan='"+len+"'>暂无内容</td>");
            }else {
                $($(".table tbody tr")[i]).append("<td colspan='"+len+"'></td>");
            }
        }
        this.jumpBianse();
        this.setPage($("#foot-num")[0],0,1,10,0);
    },
    randomData:function () {
     return Math.round(Math.random()*1000);
    }
    ,bind:function (){
        this.dateTabs();
        this.chooseGoods();
        this.chooseStore();
        this.chooseBrand();
        this.chooseArea();
        this.toPage();
        this.export();
        this.selectMore();
        this.selectMoreTable();
        this.movePop(".goods_vip_content",".goods_vip_title")
    }
    ,screenOperate:function(){
        var self=this;
        $("#clear_select").click(function(){
            $("#resetSelectGoods").trigger("click");
            message.cache.store_codes="";
            message.cache.store_names="";
            message.cache.brand_codes="";
            message.cache.brand_names="";
            message.cache.area_codes="";
            message.cache.area_names="";
            self.store_code="";
            self.page_num=1;
            self.allData="";
            $("#store_search").html("");
            $("#brand_search").html("");
            $("#area_search").html("");
            $("#brand_num").val("全部");
            $("#area_num").val("全部");
            $("#chooseStore>span").html("请选择店铺");
            self.timeFiltrate(self);
            $(".date_nav div:first-child").trigger("click");
            self.initData();
        });
        $("#to_select").click(function(){
            self.getListData();
        });
        //$("#screen_shop ")
    }
    //删除弹框
    ,frame:function frame(){
        var def= $.Deferred();
        var left=($(window).width()-$("#frame").width())/2;//弹框定位的left值
        var tp=($(window).height()-$("#frame").height())/2;//弹框定位的top值
        $('.frame').remove();
        $('.content').append('<div class="frame" style="left:'+left+'px;top:'+tp+'px;"></div>');
        $(".frame").animate({opacity:"1"},1000);
        $(".frame").animate({opacity:"0"},1000);
        setTimeout(function(){
            $(".frame").hide();
            def.resolve();
        },2000);
        return def;
    }
    ,export:function(){
        var self=this;
        $("#export").click(function(){
            var param={};
            var l=$(window).width();
            var h=$(document.body).height();
            $(".file").css("position","fixed");
            $("#p").show();
            $("#p").css({"width":+l+"px","height":+h+"px"});
            param["function_code"]=funcCode;
            oc.postRequire("post","/list/getCols","0",param,function(data){
                if(data.code=="0"){
                    var message=JSON.parse(data.message);
                    var message=JSON.parse(message.tableManagers);
                    $("#file_list_l ul").empty();
                    for(var i=0;i<message.length;i++){
                        $("#file_list_l ul").append("<li data-name='"+message[i].column_name+"'><div class='checkbox1'><input type='checkbox' value='' name='test'  class='check'  id='checkboxInput"
                            +i+1+"'/><label for='checkboxInput"+i+1+"'></label></div><span class='p15'>"+message[i].show_name+"</span></li>")
                    }
                    self.bianse();
                    $("#file_list_r ul").empty();
                }else if(data.code=="-1"){
                    alert(data.message);
                }
            });
            $(".file").show();
        });
        $("#hide_export").click(function(){
            $("#export_list").hide();
            $("#p").hide();
        });
        $('#file_close').click(function(){
            $("#p").hide();
            $('.file').hide();
            $('#file_submit').show();
            $('#download').hide();
        });
        //导出提交的
        $("#file_submit").click(function(){
            //$("#export_list").show();
            //whir.loading.add("",0.5);//加载等待框
            var li=$("#file_list_r input[type='checkbox']").parents("li");
            var list_html="";
            if(li.length=="0"){
                self.frame();
                $('.frame').html('请把要导出的列移到右边');
                return;
            }
            var allPage=Math.ceil(self.allData/2000);
            var param={};
            var tablemanager=[];
            for(var i=0;i<li.length;i++){
                var r=$(li[i]).attr("data-name");
                var z=$(li[i]).children("span").html();
                var param1={"column_name":r,"show_name":z};
                tablemanager.push(param1);
            }
            tablemanager.reverse();
            $(".file").hide();
            $("#export_list_all ul").html("");
            for(var a=1;a<allPage+1;a++){
                var start_num=(a-1)*2000 + 1;
                var end_num="";
                if (self.allData < a*2000 ){
                    end_num = self.allData
                }else{
                    end_num = a*2000
                }
                list_html+= '<li>'
                    +'<span style="float: left">商品分析('+start_num+'~'+end_num+')</span>'
                    +'<span class="export_list_btn" data-page="'+a+'">导出</span>'
                    +'<span style="margin-right:10px;" class="state"></span>'
                    +'</li>'
            }
            $("#export_list_all ul").html(list_html);
            $("#export_list").show();
            $("#export_list_all").scrollTop(0);
            $("#export_list_all .export_list_btn").click(function () {
                if($(this).hasClass("btn_active")){
                    return
                }
                var $this=$(this);
                var page=$(this).attr("data-page");
                $(this).next().text("导出中...");
                $(this).addClass("btn_active");
                param={
                    tablemanager:tablemanager,
                    param:self.param,
                    corp_code:self.corp_code,
                    store_code:self.store_code,
                    time_type:self.date_type,
                    time_value:self.date_value,
                    page_num:page,
                    page_size:2000
                };
//                    param["searchValue"]="";
//                    param["list"]="";
                oc.postRequire("post","/vipAnalysis/skuAnalysisExport","0",param,function(data){
                    if(data.code=="0"){
                        var message=JSON.parse(data.message);
                        var path=message.path;
                        path=path.substring(1,path.length-1);
                        $this.attr("data-path",path);
                        $this.next().text("导出完成");
                        $this.html("<a href='/"+path+"' style='display: inline-block;width: 100%;height: 100%;color: #FFFFFF'>下载</a>");
                        $this.css("backgroundColor","#637ea4");
                    }else if(data.code=="-1"){
                        $this.removeClass("btn_active");
                        $this.next().text("导出失败");
                    }
                })
            });
        });
        $("#to_zip").click(function(){
            var a=$("#export_list_all ul li a");
            var URL="";
            var param={};
            if(a.length==0){
                self.frame();
                $('.frame').html("请先导出文件");
                return;
            }
            for(var i=a.length-1;i>=0;i--){
                if(i>0){
                    URL+=$(a[i]).attr("href")+","
                }else{
                    URL+=$(a[i]).attr("href");
                }
            }
            param.url=URL;
            param.name="商品分析";
            whir.loading.add("","0.5");
            oc.postRequire("post","/vip/exportZip", "",param, function(data){
                if(data.code=="0"){
                    var path=JSON.parse(data.message).path;
                    path=path.substring(1,path.length-1);
                    $("#download_all a").prop("href","/"+path);
                    $("#p").css("zIndex","789");
                    whir.loading.remove();
                    $("#download_all").show();
                }
                if(data.code=="-1"){
                    whir.loading.remove();
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "操作失败"
                    });
                }
            })
        });
        $("#cancel_download,#X_download").click(function(){
            $("#p").css("zIndex","787");
            $("#download_all").hide();
            $("#download_all a").removeProp("href");
        });
    },bianse:function (){
        $("#file_list_l li:odd").css("backgroundColor","#fff");
        $("#file_list_l li:even").css("backgroundColor","#ededed");
        $("#file_list_r li:odd").css("backgroundColor","#fff");
        $("#file_list_r li:even").css("backgroundColor","#ededed");
    }
    ,getstorelist:function(a){
        var self=this;
        var searchValue=$("#store_search").val().trim();
        var pageSize=20;
        var pageNumber=a;
        var _param={};
        _param['corp_code']="C10000";
        _param['area_code']=message.cache.area_codes;
        _param['brand_code']=message.cache.brand_codes;
        _param['searchValue']=searchValue;
        _param['pageNumber']=pageNumber;
        _param['pageSize']=pageSize;
        whir.loading.add("",0.5);//加载等待框
        $("#mask").css("z-index","10002");
        // oc.postRequire("post","/user/stores","", _param, function(data) {
        oc.postRequire("post","/shop/selectByAreaCode","", _param, function(data) {
            if (data.code == "0") {
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                var hasNextPage=list.hasNextPage;
                var cout=list.pages;
                var list=list.list;
                var store_html = '';
                if (list.length == 0){

                } else {
                    if(list.length>0){
                        for (var i = 0; i < list.length; i++) {
                            store_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].store_code+"' data-storename='"+list[i].store_name+"' name='test'  class='check'  id='checkboxTowInput"
                                + i
                                + a
                                + 1
                                + "'/><label for='checkboxTowInput"
                                + i
                                + a
                                + 1
                                + "'></label></div><span class='p16'>"+list[i].store_name+"</span></li>"
                        }
                    }
                }
                if(hasNextPage==true){
                    self.shop_num++;
                    self.shop_next=false;
                }
                if(hasNextPage==false){
                    self.shop_next=true;
                }
                $("#screen_shop .screen_content_l ul").append(store_html);
                if(!self.isscroll){
                    $("#screen_shop .screen_content_l").unbind("scroll").bind("scroll",function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight=$(this).height();
                        if(nScrollTop + nDivHight >= nScrollHight && nScrollTop != 0){
                            if(self.shop_next){
                                return;
                            }
                            self.getstorelist(self.shop_num);
                        }
                    })
                }
                isscroll=true;
                var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
                for(var k=0;k<li.length;k++){
                    $("#screen_shop .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
                }
                whir.loading.remove();//移除加载框
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        })
    }
    ,getbrandlist:function (){
        var searchValue=$("#brand_search").val().trim();
        var _param={};
        _param['corp_code']="C10000";
        _param["searchValue"]=searchValue;
        whir.loading.add("",0.5);//加载等待框
        $("#mask").css("z-index","10002");
        oc.postRequire("post","/shop/brand", "",_param, function(data){
            if (data.code == "0") {
                var message=JSON.parse(data.message);
                var list=message.brands;
                var brand_html_left = '';
                var brand_html_right='';
                if (list.length == 0){
                    for(var h=0;h<9;h++){
                        brand_html_left+="<li></li>"
                    }
                } else {
                    if(list.length<9){
                        for (var i = 0; i < list.length; i++) {
                            brand_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxThreeInput"
                                + i
                                + 1
                                + "'/><label for='checkboxThreeInput"
                                + i
                                + 1
                                + "'></label></div><span class='p16'>"+list[i].brand_name+"</span></li>"
                        }
                        for(var j=0;j<9-list.length;j++){
                            brand_html_left+="<li></li>"
                        }
                    }else if(list.length>=9){
                        for (var i = 0; i < list.length; i++) {
                            brand_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxThreeInput"
                                + i
                                + 1
                                + "'/><label for='checkboxThreeInput"
                                + i
                                + 1
                                + "'></label></div><span class='p16'>"+list[i].brand_name+"</span></li>"
                        }
                    }
                }
                $("#screen_brand .screen_content_l ul").append(brand_html_left);
                var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
                for(var k=0;k<li.length;k++){
                    $("#screen_brand .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
                }
                whir.loading.remove();//移除加载框
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        })
    }
    ,getarealist:function (a){
        var self=this;
        var area_command = "/area/selAreaByCorpCode";
        var searchValue=$("#area_search").val().trim();
        var pageSize=20;
        var pageNumber=a;
        var _param = {};
        _param['corp_code']="C10000";
        _param["searchValue"]=searchValue;
        _param["pageSize"]=pageSize;
        _param["pageNumber"]=pageNumber;
        whir.loading.add("",0.5);//加载等待框
        $("#mask").css("z-index","10002");
        oc.postRequire("post", area_command, "", _param, function(data) {
            if (data.code == "0") {
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                var hasNextPage=list.hasNextPage;
                var cout=list.pages;
                var list=list.list;
                var area_html_left ='';
                var area_html_right='';
                if (list.length == 0) {

                } else {
                    if(list.length>0){
                        for (var i = 0; i < list.length; i++) {
                            area_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].area_code+"' data-areaname='"+list[i].area_name+"' name='test'  class='check'  id='checkboxOneInput"
                                + i
                                + a
                                + 1
                                + "'/><label for='checkboxOneInput"
                                + i
                                + a
                                + 1
                                + "'></label></div><span class='p16'>"+list[i].area_name+"</span></li>"
                        }
                    }
                }
                if(hasNextPage==true){
                    self.area_num++;
                    self.area_next=false;
                }
                if(hasNextPage==false){
                    self.area_next=true;
                }
                $("#screen_area .screen_content_l ul").append(area_html_left);
                if(!isscroll){
                    $("#screen_area .screen_content_l").unbind("scroll").bind("scroll",function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight=$(this).height();

                        if(nScrollTop + nDivHight >= nScrollHight && nScrollTop != 0){
                            if(self.area_next){
                                return;
                            }
                            self.getarealist(self.area_num);
                        }
                    })
                }
                self.isscroll=true;
                var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
                for(var k=0;k<li.length;k++){
                    $("#screen_area .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
                }
                whir.loading.remove();//移除加载框
            } else if (data.code == "-1") {
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        })
    }
    ,timeFun:function (e) {
        if(e.target.className.search('week')!=-1){
            this.date_type = 'weekly';
        }
        if(e.target.className.search('month')!=-1){
            this.date_type = 'monthly';
        }
        if(e.target.className.search('year')!=-1){
            this.date_type = 'yearly';
        }
        if(e.target.className.search('week_arrow_left')!=-1){//递减一年的周
            this.year_down-=1;
            this.dateFun(this.year_down,this);
        }
        if(e.target.className.search('week_arrow_right')!=-1){//递增一年的周
            this.year_down+=1;
            this.dateFun(this.year_down,this);
        }
        if(e.target.className.search('month_arrow_left')!=-1){//递减一年的yue
            this.year_down-=1;
            this.dateFun(this.year_down,this);
        }
        if(e.target.className.search('month_arrow_right')!=-1){//递增一年的月10
            this.year_down+=1;
            this.dateFun(this.year_down,this);
        }
    }
    ,timeFiltrate:function (obj) {//接受一个对象
        var  time=new Date();
        var  year=new Date().getFullYear();
        var time_start='';
        var time_end='';
        switch (new Date(year+'/1/1').getDay()){
            case 0:time_start=year+'/1/2';break //星期天
            case 1:time_start=year+'/1/1';break//星期一
            case 2:time_start=(year-1)+'/12/31';break//星期二
            case 3:time_start=(year-1)+'/12/30';break//星期三
            case 4:time_start=year+'/1/5';break//星期四
            case 5:time_start=year+'/1/4';break//星期五
            case 6:time_start=year+'/1/3';break//星期六
        }
        switch (new Date(year+'/12/31').getDay()){
            case 0:time_end=year+'/12/31';break//星期天
            case 1:time_end=year+'/12/30';break//星期一
            case 2:time_end=year+'/12/29';break//星期二
            case 3:time_end=year+'/12/28';break//星期三
            case 4:time_end=(year+1)+'/1/3';break//星期四
            case 5:time_end=(year+1)+'/1/2';break//星期五
            case 6:time_end=(year+1)+'/1/1';break//星期六
        }
        var i=0;
        time=time.getFullYear()+"/"+(time.getMonth()+1)+"/"+time.getDate();
        while (new Date(time_start)<=new Date(time_end)){
            i++;
//                console.log(time_start)
//                if(new Date(time_start)<=new Date(time)){
//                    console.log(new Date(time))
//                    console.log(new Date(time_start))
//                    console.log(new Date(new Date(time_start).setDate(new Date(time_start).getDate()+6)))
//                }
//                time_start=new Date(new Date(time_start).setDate(new Date(time_start).getDate()-7));
            if(new Date(time_start)>=new Date(time) || (new Date(time)<=new Date(new Date(time_start).setDate(new Date(time_start).getDate()+6)))){
                obj.time=year+'年'+i+'周';
                obj.now_week=i;
                var s_y=(new Date(time_start).getMonth()+1)<=9?'0'+(new Date(time_start).getMonth()+1):(new Date(time_start).getMonth()+1);
                var s_x=new Date(time_start).getDate()<=9?'0'+new Date(time_start).getDate():new Date(time_start).getDate();
                obj.time_start=new Date(time_start).getFullYear()+'-'+s_y+'-'+s_x;
                obj.date_type='weekly';
                obj.date_value=new Date(time_start).getFullYear()+'-'+s_y+'-'+s_x;
                var e_time=new Date(new Date(time_start).setDate(new Date(time_start).getDate()+6));
                var e_y=(new Date(e_time).getMonth()+1)<=9?'0'+(new Date(e_time).getMonth()+1):(new Date(e_time).getMonth()+1);
                var e_x=new Date(e_time).getDate()<=9?'0'+new Date(e_time).getDate():new Date(e_time).getDate();
                obj.time_end=new Date(e_time).getFullYear()+'-'+e_y+'-'+e_x;
                //退出之前生成周期
                this.dateFun(new Date().getFullYear(),obj);
                return;
            }
            time_start=new Date(new Date(time_start).setDate(new Date(time_start).getDate()+7));
        }
    }
    ,dateFun:function (year,vue_obj) {//第一个参数年/第二个参数为vue对象
        vue_obj.year_down=year;//显示下拉中的年
        vue_obj.week_obj=[];//周下拉数据
        vue_obj.month_arr=[];//月下拉数据
        var week=[];//周
        var week_date=[];//周日期
        var time_start='';//循环开始时间
        var time_end=''//循环结束时间
        //判断开始时间日期
        switch (new Date(year+'/01/01').getDay()){
            case 0:time_start=year+'/01/02';break//星期天
            case 1:time_start=year+'/01/01';break//星期一
            case 2:time_start=(year-1)+'/12/31';break//星期二
            case 3:time_start=(year-1)+'/12/30';break//星期三
            case 4:time_start=year+'/01/05';break//星期四
            case 5:time_start=year+'/01/04';break//星期五
            case 6:time_start=year+'/01/03';break//星期六
        }
        switch (new Date(year+'/12/31').getDay()){
            case 0:time_end=year+'/12/31';break//星期天
            case 1:time_end=year+'/12/30';break//星期一
            case 2:time_end=year+'/12/29';break//星期二
            case 3:time_end=year+'/12/28';break//星期三
            case 4:time_end=(year+1)+'/01/3';break//星期四
            case 5:time_end=(year+1)+'/01/02';break//星期五
            case 6:time_end=(year+1)+'/01/01';break//星期六
        }
        var i=0;
        while (new Date(time_start)<=new Date(time_end)){
            var s_y=(new Date(time_start).getMonth()+1)<=9?'0'+(new Date(time_start).getMonth()+1):(new Date(time_start).getMonth()+1);
            var s_x=new Date(time_start).getDate()<=9?'0'+new Date(time_start).getDate():new Date(time_start).getDate();
            var e_time=new Date(new Date(time_start).setDate(new Date(time_start).getDate()+6))
            var e_y=(new Date(e_time).getMonth()+1)<=9?'0'+(new Date(e_time).getMonth()+1):(new Date(e_time).getMonth()+1);
            var e_x=new Date(e_time).getDate()<=9?'0'+new Date(e_time).getDate():new Date(e_time).getDate();
            week_date.push(new Date(time_start).getFullYear()+'-'+s_y+'-'+s_x+' 至 '+new Date(e_time).getFullYear()+'-'+e_y+'-'+e_x);
            time_start=new Date(new Date(time_start).setDate(new Date(time_start).getDate()+7))
            i++;
            week.push(year+'年'+(i)+'周');
        }
        for(var i=0;i<week.length;i++){
            var obj={};
            obj.title=week[i];
            obj.title_date=week_date[i];
            //显示当前周期的class
            if((i==(vue_obj.now_week-1))&&(year==new Date().getFullYear())){
                obj.title_class=true
            }else{
                obj.title_class=false
            }
            vue_obj.week_obj.push(obj);
        }
        //月数据处理
        for(var i=1;i<13;i++){
            if((i==(new Date().getMonth()+1))&&(year==new Date().getFullYear())){
                vue_obj.month_arr.push({'val':year+'年'+i+'月','key':true});
            }else{
                vue_obj.month_arr.push({'val':year+'年'+i+'月','key':false});
            }
        }
        vue_obj.DateList(year);
    }
    ,yearFun:function (obj) {//接受对象
        var year=new Date().getFullYear();
        //年数据处理
        for(var i=0;i<10;i++){
            if((i==0)&&(year==new Date().getFullYear())){
                obj.year_arr.push({'val':(year-i)+'年','key':true});
            }else{
                obj.year_arr.push({'val':(year-i)+'年','key':false});
            }
        }
    }
    //,
    //timeChange:function (e) {
    //    var time_arr=[];
    //    this.next_page=1;
    //    if(e.currentTarget.id=='year_id'){
    //        $("#time_show").hide();
    //        this.time=$(e.target).find("span").html();
    //        this.date_type='yearly';
    //        this.date_value=parseInt(this.time)+'-01-01';
    //        $("#time").html(this.time);
    //        // whir.loading.add("",0.5);//加载等待框
    //        this.tableData=[];
    //        this.loading_show=true;
    //    }else if(e.currentTarget.id=='month_id'){
    //        $("#time_show").hide();
    //        this.time=$(e.target).find("span").html();
    //        var s=this.time.search('年');
    //        var e=this.time.search('月');
    //        var y=this.time.slice(s+1,e)>9?this.time.slice(s+1,e):'0'+this.time.slice(s+1,e);
    //        $("#time").html(this.time);
    //        this.date_type='monthly';
    //        this.date_value=parseInt(this.time)+'-'+y+'-01';
    //        // whir.loading.add("",0.5);//加载等待框
    //        this.tableData=[];
    //        this.loading_show=true;
    //    }else if(e.currentTarget.id=='week_id'){
    //        time_arr=$(e.target).parent().find('.week_detail_d').html().split('至');
    //        this.time_start=time_arr[0];
    //        this.time_end=time_arr[1];
    //        this.time=$(e.target).parent().find('.week_detail_w').html();
    //        $("#time_start").html(this.time_start);
    //        $("#time_end").html(this.time_end);
    //        $("#time").html(this.time);
    //        $("#time_show").show();
    //        this.date_type='weekly';
    //        this.date_value=time_arr[0];
    //        // whir.loading.add("",0.5);//加载等待框
    //        this.tableData=[];
    //        this.loading_show=true;
    //    }
    //    this.page_num=1;
    //    this.getListData();
    //    $("#date").hide();
    //}
    ,DateList:function(year){
        var self=this;
        var week_obj=this.week_obj;
        var week_html="";
        $("#time_start").html(this.time_start);
        $("#time_end").html(this.time_end);
        $(".year_down").html(this.year_down);
        for(var i=0;i<week_obj.length;i++){
            var box='<div class="checkbox" style="float: left;margin-top: 12px;vertical-align: middle;margin-right: 5px;"><input type="checkbox" value="" name="test" title="全选/取消" class="check" id=checkboxweek'+i+'><label for=checkboxweek'+i+'></label></div>'
            if((i==(self.now_week)-1)&&(year==new Date().getFullYear())){
                week_html+='<li id="week_id">'+box
                    +'<div class="week_detail_w click_font">'+week_obj[i].title+'</div>'
                    +'<div class="week_detail_d">'+week_obj[i].title_date+'</div>'
                    +'</li>'
            }else{
                week_html+='<li id="week_id">'+box
                    +'<div class="week_detail_w">'+week_obj[i].title+'</div>'
                    +'<div class="week_detail_d">'+week_obj[i].title_date+'</div>'
                    +'</li>'
            }
        }
        $(".week_detail").html(week_html);
        $(".week_detail .click_font").prev(".checkbox").find("input").attr("checked",true);

        var month_arr=this.month_arr;
        var month_html="";
        for(var m=0;m<month_arr.length;m++){
            var box='<div class="checkbox" style="vertical-align: middle;margin-right: 5px;"><input type="checkbox" value="" name="test" title="全选/取消" class="check" id=checkboxmonth'+m+'><label for=checkboxmonth'+m+'></label></div>'
            if((m==(new Date().getMonth()))&&(year==new Date().getFullYear())){
                month_html+='<li id="month_id">'+box+'<span class="click_font" style="vertical-align: middle">'+month_arr[m].val+'</span></li>'
            }else{
                month_html+='<li id="month_id">'+box+'<span style="vertical-align: middle">'+month_arr[m].val+'</span></li>'
            }
        }
        $(".month_detail").html(month_html);
        $(".month_detail .click_font").prev(".checkbox").find("input").attr("checked",true);

        var year_arr=this.year_arr;
        var year_html="";
        for(var y=0;y<year_arr.length;y++){
            var box='<div class="checkbox" style="vertical-align: middle;margin-right: 5px;"><input type="checkbox" value="" name="test" title="全选/取消" class="check" id=checkboxyear'+y+'><label for=checkboxyear'+y+'></label></div>'
            if((y==0)&&(year==new Date().getFullYear())){
                year_html+='<li id="year_id">'+box+'<span class="click_font" style="vertical-align: middle">'+year_arr[y].val+'</span></li>';
            }else{
                year_html+='<li id="year_id">'+box+'<span style="vertical-align: middle">'+year_arr[y].val+'</span></li>'
            }
        }
        $(".year_detail").html(year_html);
        $(".year_detail .click_font").prev(".checkbox").find("input").attr("checked",true);

        var len=this.date_value.split(",").length;
        $("#time").html("已选"+len+"个周");
        $("#date_sure_num").html("已选"+len+"个");
        
        if($("#date_box ul:visible li").length!=len){
            $(".date_opt_checked .checkbox input").attr("checked",false)
        }else{
            $(".date_opt_checked .checkbox input").attr("checked",true)
        }
    }
    ,dateTabs:function(){
        var self=this;
        $(".year_detail,.month_detail").on("click","li",function(e){

            var checked=$(this).find(".checkbox input").attr("checked");
                if(checked){
                    $(this).find("span").removeClass("click_font");
                }else{
                    $(this).find("span").addClass("click_font");
                }
                $(this).find(".checkbox input").attr("checked",checked==undefined?true:false);

                //self.timeChange(e);
            var len=$("#date_box ul:visible li input:checked").length;
            $("#date_sure_num").html("已选"+len+"个");
            if($("#date_box ul:visible li").length!=len){
                $(".date_opt_checked .checkbox input").attr("checked",false)
            }else{
                $(".date_opt_checked .checkbox input").attr("checked",true)
            }

        });
        $(".week_detail").on("click","li",function(e){
            var checked=$(this).find(".checkbox input").attr("checked");
            if(checked){
                $(this).find(".week_detail_w").removeClass("click_font");
            }else{
                $(this).find(".week_detail_w").addClass("click_font");
            }
            $(this).find(".checkbox input").attr("checked",checked==undefined?true:false);

            var len=$("#date_box ul:visible li input:checked").length;
            $("#date_sure_num").html("已选"+len+"个");
            if($("#date_box ul:visible li").length!=len){
                $(".date_opt_checked .checkbox input").attr("checked",false)
            }else{
                $(".date_opt_checked .checkbox input").attr("checked",true)
            }
        });
        $(".date_nav div").click(function(){
            var index=$(this).index();
            $(this).siblings().removeClass("active");
            $(this).addClass("active");
            $("#date_box").children().hide();
            $("#date_box").children().eq(index).show();
            var len=$("#date_box ul:visible li input:checked").length;
            $("#date_sure_num").html("已选"+len+"个");
            if($("#date_box ul:visible li").length!=len){
                $(".date_opt_checked .checkbox input").attr("checked",false)
            }else{
                $(".date_opt_checked .checkbox input").attr("checked",true)
            }
            var parent=$("#date_box ul:visible").find(".click_font").eq(0).parent();
            var h=parent.index()*parent.height();
            $("#date_box ul:visible").scrollTop(h);
        });
        $(".week_arrow_left,.week_arrow_right,.month_arrow_left,.month_arrow_right").click(function(e){
            self.timeFun(e);
            var len=$("#date_box ul:visible li input:checked").length;
            $("#date_sure_num").html("已选"+len+"个");
            if($("#date_box ul:visible li").length!=len){
                $(".date_opt_checked .checkbox input").attr("checked",false)
            }else{
                $(".date_opt_checked .checkbox input").attr("checked",true)
            }
        });
        $(".nav_date .nav_down").click(function(e){
            e.stopPropagation();
            if($("#date").is(":visible")){
                $("#date").hide()
            }else{
                $("#date").show();
            }
            var parent=$("#date_box ul:visible").find(".click_font").eq(0).parent();
            var h=parent.index()*parent.height();
            $("#date_box ul:visible").scrollTop(h);
            $("#date_box ul").getNiceScroll().resize();
            var len=$("#date_box ul:visible li input:checked").length;
            $("#date_sure_num").html("已选"+len+"个");
            if($("#date_box ul:visible li").length!=len){
                $(".date_opt_checked .checkbox input").attr("checked",false)
            }else{
                $(".date_opt_checked .checkbox input").attr("checked",true)
            }
        });
        $("#date").click(function(e){
            e.stopPropagation();
        });
        $(document).click(function(){
            $("#date").hide();
        })
    },
    chooseGoods:function(){
        var self=this;
        $("#chooseShopperWrap").on("click",".select_input,.checkbox_select_input",function () {
            if($(this).nextAll("ul").children().length!=0){
                $(this).nextAll("ul").toggle();
            }else{
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "暂无数据"
                });
            }
            $(this).parent("div").siblings().find(".shopperSelect").hide();
        });
        $("#resetSelectGoods").bind("click",function(){
            var input=$("#chooseShopperCondition .text_input");
            for(var i=0;i<input.length;i++){
                if($(input[i]).hasClass("checkbox_select_input")){
                    $(input[i]).attr("data-code","");
                    $(input[i]).val("全部");
                    $(input[i]).next("ul").find("input").attr("checked",false);
                }else{
                    $(input[i]).val("");
                }
            }
            $("#chooseShopperSwitch").removeClass("on");
            $("#chooseShopperSwitch").find("em").animate({left:"2px"},100);
            $("#chooseShopperWrap").hide();
            //self.getListData();
        });
        $("#chooseShopperWrap").on("click",".shopperSelect input",function () {
            var input = $(this).parents(".shopperSelect").find("li input:checked");
            var text="";
            var code="";
            for(var i=input.length-1;i>=0;i--){
                if(i>0){
                    text+=$(input[i]).parent("li").text()+","
                    code+=$(input[i]).parent("li").attr("data-code")+","
                }else{
                    text+=$(input[i]).parent("li").text();
                    code+=$(input[i]).parent("li").attr("data-code");
                }
            }
            var textArray=text.split(",").reverse();
            var codeArray=code.split(",").reverse();
            text=textArray.join(",");
            code=codeArray.join(",");
            $(this).parents("ul").prev().val(text);
            $(this).parents("ul").prev().attr("data-code",code);
        });
        $("#chooseGoods").click(function(){
            self.getBrand();
            self.getGoodsType();
            $("#chooseShopperWrap").show();
        });
        $(".closeShopperWrap").click(function(){
            $("#chooseShopperWrap").hide();
        });
        $("#chooseShopperSwitch").click(function(){
            if($(this).hasClass("on")){
                $(this).removeClass("on");
                $(this).find("em").animate({left:"2px"},100)
            }else{
                $(this).addClass("on");
                $(this).find("em").animate({left:"21px"},100)
            }
        });
        $("#chooseShopEnter").click(function(){
            self.page_num=1;
            $("#chooseShopperWrap").hide();
            //self.getListData();
        });
        //$(window).keydown(function(){
        //    var event=window.event||arguments[0];
        //    if(event.keyCode==13){
        //       if($("#chooseShopperWrap").is(":visible")){
        //           $("#chooseShopEnter").trigger("click");
        //       }
        //    }
        //});
    },
    chooseStore:function(){
        var self=this;
        $("#chooseStore").bind("click",function(){
            if(message.cache.store_codes!==""){
                var store_codes=message.cache.store_codes.split(',');
                var store_names=message.cache.store_names.split(',');
                var store_html_right="";
                for(var h=0;h<store_codes.length;h++){
                    store_html_right+="<li id='"+store_codes[h]+"'>\
                        <div class='checkbox1'><input type='checkbox' value='"+store_names[h]+"'  data-storename='"+store_names[h]+"' name='test' class='check'>\
                        <label></div><span class='p16'>"+store_names[h]+"</span>\
                        \</li>"
                }
                $("#screen_shop .s_pitch span").html(h);
                $("#screen_shop .screen_content_r ul").html(store_html_right);
            }else{
                $("#screen_shop .s_pitch span").html("0");
                $("#screen_shop .screen_content_r ul").empty();
            }
            self.isscroll=false;
            self.shop_num=1;
            $("#screen_shop .screen_content_l ul").empty();
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop").show();
            $("#screen_shop").parent().show();
            self.getstorelist(self.shop_num);
        });
        //店铺关闭
        $("#screen_close_shop").click(function(){
            $("#screen_shop").hide();
            $("#screen_shop").parent().hide();
        });
        $("#screen_que_shop").click(function(){
            var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
            var store_codes="";
            var store_names="";
            self.page_num=1;
            for(var i=li.length-1;i>=0;i--){
                var r=$(li[i]).attr("id");
                var p=$(li[i]).find(".p16").html();
                if(i>0){
                    store_codes+=r+",";
                    store_names+=p+",";
                }else{
                    store_codes+=r;
                    store_names+=p;
                }
            }
            message.cache.store_codes=store_codes;
            message.cache.store_names=store_names;
            $("#screen_shop").hide();
            $("#screen_shop").parent().hide();
            self.store_code=store_codes;
            var len=store_codes.split(",").length
            if(store_codes==""){
                $("#chooseStore>span").html("请选择店铺")
            }else{
                $("#chooseStore>span").html("已选"+len+"个")
            }
        });
        //店铺放大镜搜索
        $("#store_search_f").click(function(){
            self.shop_num=1;
            self.isscroll=false;
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop .screen_content_l ul").empty();
            self.getstorelist(self.shop_num);
        });
        $("#store_search").keydown(function(){
            var event=window.event||arguments[0];
            self.shop_num=1;
            if(event.keyCode==13){
                self.isscroll=false;
                $("#screen_shop .screen_content_l ul").unbind("scroll");
                $("#screen_shop .screen_content_l ul").empty();
                self.getstorelist(self.shop_num);
            }
        });
    },
    chooseBrand:function(){
        var self=this;
        $("#shop_brand").click(function(){
            if(message.cache.brand_codes!==""){
                var brand_codes=message.cache.brand_codes.split(',');
                var brand_names=message.cache.brand_names.split(',');
                var brand_html_right="";
                for(var h=0;h<brand_codes.length;h++){
                    brand_html_right+="<li id='"+brand_codes[h]+"'>\
                        <div class='checkbox1'><input type='checkbox' value='"+brand_codes[h]+"'  data-storename='"+brand_names[h]+"' name='test' class='check'>\
                        <label></div><span class='p16'>"+brand_names[h]+"</span>\
                        \</li>"
                }
                $("#screen_brand .s_pitch span").html(h);
                $("#screen_brand .screen_content_r ul").html(brand_html_right);
            }else{
                $("#screen_brand .s_pitch span").html("0");
                $("#screen_brand .screen_content_r ul").empty();
            }
            $("#screen_brand .screen_content_l ul").empty();
            $("#screen_brand").show();
            $("#screen_brand").parent().show();
            $("#screen_shop").hide();
            $("#screen_shop").parent().hide();
            self.getbrandlist();
        });
        $("#screen_que_brand").click(function(){
            var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
            var brand_codes="";
            var brand_names="";
            for(var i=li.length-1;i>=0;i--){
                var r=$(li[i]).attr("id");
                var p=$(li[i]).find(".p16").html();
                if(i>0){
                    brand_codes+=r+",";
                    brand_names+=p+",";
                }else{
                    brand_codes+=r;
                    brand_names+=p;
                }
            }
            message.cache.brand_codes=brand_codes;
            message.cache.brand_names=brand_names;
            $("#screen_brand").hide();
            $("#screen_brand").parent().hide();
            self.shop_num=1;
            self.isscroll=false;
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop .screen_content_l ul").empty();
            self.getstorelist(self.shop_num);
            $("#screen_shop").show();
            $("#screen_shop").parent().show();

            $("#screen_brand_num").val("已选"+li.length+"个");
            $("#screen_brand_num").attr("data-code",brand_codes);
            $("#screen_brand_num").attr("data-name",brand_names);
            $(".brand_num").val("已选"+li.length+"个");
        });
        $("#screen_close_brand").click(function(){
            $("#screen_brand").hide();
            $("#screen_brand").parent().hide();
            $("#screen_shop").show();
            $("#screen_shop").parent().show();
        });
        //品牌搜索
        $("#brand_search").keydown(function(){
            var event=window.event||arguments[0];
            if(event.keyCode==13){
                $("#screen_brand .screen_content_l ul").empty();
                self.getbrandlist();
            }
        });
        //品牌放大镜收索
        $("#brand_search_f").click(function(){
            $("#screen_brand .screen_content_l ul").empty();
            self.getbrandlist();
        });
    },
    chooseArea:function(){
        //店铺里面的区域点击
        var self=this;
        $("#shop_area").click(function(){
            if(message.cache.area_codes!==""){
                var area_codes=message.cache.area_codes.split(',');
                var area_names=message.cache.area_names.split(',');
                var area_html_right="";
                for(var h=0;h<area_codes.length;h++){
                    area_html_right+="<li id='"+area_codes[h]+"'>\
                        <div class='checkbox1'><input type='checkbox' value='"+area_codes[h]+"'  data-storename='"+area_names[h]+"' name='test' class='check'>\
                        <label></div><span class='p16'>"+area_names[h]+"</span>\
                        \</li>"
                }
                $("#screen_area .s_pitch span").html(h);
                $("#screen_area .screen_content_r ul").html(area_html_right);
            }else{
                $("#screen_area .s_pitch span").html("0");
                $("#screen_area .screen_content_r ul").empty();
            }
            self.isscroll=false;
            self.area_num=1;
            $("#screen_area .screen_content_l").unbind("scroll");
            $("#screen_area .screen_content_l ul").empty();
            $("#screen_area").parent().show();
            $("#screen_area").show();
            $("#screen_shop").hide();
            $("#screen_shop").parent().hide();
            self.getarealist(self.area_num);
        });
        //点击区域确定按钮
        $("#screen_que_area").click(function(){
            var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
            var area_codes="";
            var area_names="";
            for(var i=li.length-1;i>=0;i--){
                var r=$(li[i]).attr("id");
                var p=$(li[i]).find(".p16").html();
                if(i>0){
                    area_codes+=r+",";
                    area_names+=p+",";
                }else{
                    area_codes+=r;
                    area_names+=p;
                }
            };
            message.cache.area_codes=area_codes;
            message.cache.area_names=area_names;
            $("#screen_area").hide();
            $("#screen_area").parent().hide();
            self.shop_num=1;
            self.isscroll=false;
            $("#screen_shop .screen_content_l").unbind("scroll");
            $("#screen_shop .screen_content_l ul").empty();
            self.getstorelist(self.shop_num);
            $("#screen_shop").parent().show();
            $("#screen_shop").show();
            $("#screen_area_num").val("已选"+li.length+"个");
            $("#screen_area_num").attr("data-code",area_codes);
            $("#screen_area_num").attr("data-name",area_names);
            $(".area_num").val("已选"+li.length+"个");
        });
        $("#screen_close_area").click(function(){
            $("#screen_area").hide();
            $("#screen_area").parent().hide();
            $("#screen_shop").parent().show();
            $("#screen_shop").show();
        });
        //区域搜索
        $("#area_search").keydown(function(){
            var event=window.event||arguments[0];
            self.area_num=1;
            if(event.keyCode == 13){
                self.isscroll=false;
                $("#screen_area .screen_content_l").unbind("scroll");
                $("#screen_area .screen_content_l ul").empty();
                self.getarealist(self.area_num);
            }
        });
        //区域放大镜收索
        $("#area_search_f").click(function(){
            self.area_num=1;
            self.isscroll=false;
            $("#screen_area .screen_content_l").unbind("scroll");
            $("#screen_area .screen_content_l ul").empty();
            self.getarealist(self.area_num);
        });
    },
    getListData:function(){
        var self=this;
        var isRemove = "";
        $("#chooseShopperSwitch").hasClass("on")==true?isRemove="NOT":isRemove="AND";
        var params = {
            "TYPE":isRemove,
            "BRAND_ID":$("#brandID").attr("data-code"),
            "PRODUCT_NAME":$("#goodsName").val().trim(),
            "SEASON_PRD":$("#seasonInput").val()=="全部"?"":$("#seasonInput").val().trim(),
            "CATA1_PRD":$("#bgTypeInput").val()=="全部"?"":$("#bgTypeInput").val().trim(),
            "CATA2_PRD":$("#midTypeInput").val()=="全部"?"":$("#midTypeInput").val().trim(),
            "CATA3_PRD":$("#smTypeInput").val()=="全部"?"":$("#smTypeInput").val().trim(),
            "PRODUCT_CODE":$("#goodsCode").val().trim(),
            "SKU_CODE":$("#skuCode").val().trim()
        };
        this.param=params;
        var param={
            param:this.param,
            corp_code:this.corp_code,
            page_num:this.page_num,
            page_size:this.page_size,
            store_code:this.store_code,
            time_type:this.date_type,
            time_value:this.date_value
        };
        whir.loading.add("",0.5);
        oc.postRequire("post","/vipAnalysis/skuAnalysis","0",param,function(data){
            $("#table tbody").empty();
            var message=JSON.parse(data.message);
            var list=message.sku_list;
            var total=Number(message.count);
            self.allData=total;
            self.count=Number(message.pages);
            self.page_size=Number(message.page_size);
            self.page_num=Number(message.page_num);
            self.superaddition(list,self.page_num);
            self.jumpBianse();
            self.setPage($("#foot-num")[0],self.count,self.page_num,self.page_size,total);
        })
    },
    getBrand:function(){
        var param ={};
        param.corp_code ="C10000";
        param.searchValue = "";
        oc.postRequire("post","/shop/brand","",param,function (data) {
            if(data.code == 0){
                var message=JSON.parse(data.message);
                var list=message.brands;
                var li = "";
                var brandIDOld=getOldValue("brandID");
                for(var i=0;i<list.length;i++){
                    if(brandIDOld!=undefined){
                        var has=false;
                        for(var b=0;b<brandIDOld.length;b++){
                            if(brandIDOld[b]==list[i].brand_name){
                                has=true;
                                return
                            }
                        }
                        has==true?"checked=checked":"";
                        li += "<li data-code='"+list[i].brand_code+"' title='"+list[i].brand_name+"'><input type='checkbox' "+has+">"+list[i].brand_name+"</li>";
                    }else{
                        li += "<li data-code='"+list[i].brand_code+"' title='"+list[i].brand_name+"'><input type='checkbox'>"+list[i].brand_name+"</li>";
                    }
                }
                $("#brandCondition").empty();
                $("#brandCondition").append(li);
            } else {
                console.log(data.message);
            }
        });
    },
    getGoodsType:function () {
        var param ={};
        param.corp_code = "C10000";
        oc.postRequire("post","/vipGroup/getClassQuarter","",param,function (data) {
            if(data.code == 0){
                var msg = JSON.parse(data.message);
                var typeMid = msg.CATA2_PRD;//中类
                var typeBg = msg.CATA1_PRD;//大类
                var typeSm = msg.CATA3_PRD;//小类
                var typeSeason =msg.SEASON_PRD;//季节
                var bg = "";
                var sm = "";
                var mid = "";
                var season = "";
                var seasonInputOld= getOldValue("seasonInput");
                var bgTypeInputOld=getOldValue("bgTypeInput");
                var midTypeInputOld=getOldValue("midTypeInput");
                var smTypeInputOld=getOldValue("smTypeInput");
                for(var i=0;i<typeMid.length;i++){
                    if(midTypeInputOld!=undefined){
                        var has=false;
                        for(var m=0;m<midTypeInputOld.length;m++){
                            if(midTypeInputOld[m]==typeMid[i].name){
                                has=true;
                                return
                            }
                        }
                        has==true?"checked=checked":"";
                        mid += "<li  title='"+typeMid[i].name+"'><input type='checkbox' "+has+">"+typeMid[i].name+"</li>";
                    }else{
                        mid += "<li  title='"+typeMid[i].name+"'><input type='checkbox'>"+typeMid[i].name+"</li>";
                    }
                }
                for(var j=0;j<typeBg.length;j++){
                    if(bgTypeInputOld!=undefined){
                        var has=false;
                        for(var b=0;b<bgTypeInputOld.length;b++){
                            if(bgTypeInputOld[b]==typeBg[j].name){
                                has=true;
                                return
                            }
                        }
                        has==true?"checked=checked":"";
                        bg += "<li  title='"+typeBg[j].name+"'><input type='checkbox' "+has+">"+typeBg[j].name+"</li>";
                    }else{
                        bg += "<li  title='"+typeBg[j].name+"'><input type='checkbox'>"+typeBg[j].name+"</li>";
                    }
                }
                for(var k=0;k<typeSm.length;k++){

                    if(smTypeInputOld!=undefined){
                        var has=false;
                        for(var s=0;s<smTypeInputOld.length;s++){
                            if(smTypeInputOld[s]==typeSm[k].name){
                                has=true;
                                return
                            }
                        }
                        has==true?"checked=checked":"";
                        sm += "<li  title='"+typeSm[k].name+"'><input type='checkbox' "+has+">"+typeSm[k].name+"</li>";
                    }else{
                        sm += "<li  title='"+typeSm[k].name+"'><input type='checkbox'>"+typeSm[k].name+"</li>";
                    }

                }
                for(var l=0;l<typeSeason.length;l++){
                    if(seasonInputOld!=undefined){
                        var has=false;
                        for(var v=0;v<seasonInputOld.length;v++){
                            if(seasonInputOld[v]==typeSeason[l].name){
                                has=true;
                                return
                            }
                        }
                        has==true?"checked=checked":"";
                        season += "<li  title='"+typeSeason[l].name+"'><input type='checkbox' "+has+">"+typeSeason[l].name+"</li>";
                    }else{
                        season += "<li  title='"+typeSeason[l].name+"'><input type='checkbox'>"+typeSeason[l].name+"</li>";
                    }
                }
                $("#typeBg").html(bg);
                $("#typeMid").html(mid);
                $("#typeSm").html(sm);
                $("#season").html(season);
            }else {
                console.log(data.message);
            }
        });
    },
    jumpBianse:function jumpBianse(){
        $(document).ready(function(){//隔行变色
            $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
            $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
        });
        //点击tr input是选择状态  tr增加class属性
        //$(".table tbody tr").click(function(){
        //    var input=$(this).find("input")[0];
        //    var thinput=$("thead input")[0];
        //    $(this).toggleClass("tr");
        //    if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
        //        input.checked = true;
        //        $(this).addClass("tr");
        //    }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
        //        if(thinput.type=="checkbox"&&input.name=="test"&&input.checked==true){
        //            thinput.checked=false;
        //        }
        //        input.checked = false;
        //        $(this).removeClass("tr");
        //    }
        //});
    },
    superaddition:function (data,num){//页面加载循环
        if(data.length==1 && num>1){
            this.page_num=num-1;
        }else{
            this.page_num=num;
        }
        if(data.length == 0){
            var len = $(".table thead tr th").length;
            var i;
            for(i=0;i<10;i++){
                $(".table tbody").append("<tr></tr>");
                if(i==4){
                    $($(".table tbody tr")[i]).append("<td colspan='"+len+"'>暂无内容</td>");
                }else {
                    $($(".table tbody tr")[i]).append("<td colspan='"+len+"'></td>");
                }
            }
        }
        for (var i = 0; i < data.length; i++) {
            if(num>=2){
                var a=i+1+(num-1)*this.page_size
            }else{
                var a=i+1;
            }

            $("#table tbody").append("<tr data-vip_num='"+data[i].VIP_COUNT+"' data-sku_id='"+data[i].SKU_ID+"' data-product_name='"+data[i].PRODUCT_NAME+"' data-sku_code='"+data[i].PRODUCT_CODE+"'>"
/*                +"<td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
                + i
                + 1
                + "'/><label for='checkboxTwoInput"
                + i
                + 1
                + "'></label></div>"
                + "</td>*/
                +"<td style='text-align:center;'>"
                + a
                + "</td>"
                +"<td ><span style='border: 1px solid #ddd'> <img style='height: 40px' src='"+data[i].PRODUCT_IMG+"' onerror=\"javascript:this.src='../img/goods_default_image.png'\"  alt=''></span></td>"
                +"<td ><span>"+data[i].PRODUCT_CODE+"</span></td>"
                +"<td ><span title='"+data[i].PRODUCT_NAME+"'>"+data[i].PRODUCT_NAME+"</span></td>"
                +"<td ><span>"+data[i].SKU_CODE+"</span></td>"
                +"<td ><span>"+data[i].NUM_SALES+"</span></td>"
                +"<td ><span>"+data[i].AMT_TRADE +"</span></td>"
                +"<td ><span>"+data[i].DISCOUNT +"</span></td>"
                +"<td ><span>"+data[i].VIP_NUM_SALES +"</span></td>"
                +"<td ><span>"+data[i].VIP_AMT_TRADE +"</span></td>"
                +"<td ><span>"+data[i].VIP_DISCOUNT+"</span></td>"
                +"<td ><span>"+data[i].VIP_COUNT +"</span></td>"
                +"<td ><a href='javascript:void(0)' class='details detail_list'>查看</a></td>"
                +"<td ><a href='javascript:void(0)' class='details detail_table'>查看</a></td>"
                +"</tr>")
        }
        whir.loading.remove();//移除加载框
        $(".th th:first-child input").removeAttr("checked");
    },
    dian:function (){//点击分页的时候调什么接口
        this.getListData();
//            if (value==""&&filtrate=="") {
//                GET(a,b);
//            }else if (value!==""){
//                param["pageNumber"] = a;
//                param["pageSize"] = b;
//                POST(a,b);
//            }else if (filtrate!=="") {
//                _param["pageNumber"] = a;
//                _param["pageSize"] = b;
//                filtrates(a,b);
//            }
    },
    toPage:function (){
        var self=this;
        //跳转页面的键盘按下事件
        $("#input-txt").keydown(function() {
            var event=window.event||arguments[0];
            var inx= this.value.replace(/[^0-9]/g, '');
            self.page_num=parseInt(inx);
            if (self.page_num > self.count) {
                self.page_num = self.count
            }
            if (self.page_num > 0) {
                if (event.keyCode == 13) {
                    self.getListData();
                }
            }
        });
        $("#page_row,.page_p .icon-ishop_8-02").click(function(){
            if("block" == $("#liebiao").css("display")){
                hideLi();
            }else{
                showLi();
            }
        });
        $("#liebiao li").each(function(){
            $(this).click(function(){
                self.page_num=1;
                self.page_size=$(this).attr('id');
                self.getListData();
                $("#page_row").val($(this).html());
                hideLi();
            });
        });
        $("#page_row").blur(function(){
            setTimeout(hideLi,200);
        });
        function showLi(){
            $("#liebiao").show();
        }
        function hideLi(){
            $("#liebiao").hide();
        }
    },
    setPage:function (container, count, pageindex,pageSize,total){
        var self=this;
        var container = container;
        var count = count=="0"?1:count;
        var pageindex = pageindex;
        var pageSize=pageSize;
        var total=total;
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
            self.page_num = pageindex; //初始的页码
            $("#input-txt").val(self.page_num);
            $(".foot-sum .zy").html("共 "+count+"页,"+total+'条记录');
            oAlink[0].onclick = function() { //点击上一页
                if (self.page_num == 1) {
                    return false;
                }
                self.page_num--;
                self.dian();
                // setPage(container, count, inx,pageSize,funcCode,value);
                return false;
            };
            for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
                oAlink[i].onclick = function() {
                    self.page_num = parseInt(this.innerHTML);
                    self.dian();
                    // setPage(container, count, inx,pageSize,funcCode,value);
                    return false;
                }
            }
            oAlink[oAlink.length - 1].onclick = function() { //点击下一页
                if (self.page_num == count) {
                    return false;
                }
                self.page_num++;
                self.dian();
                // setPage(container, count, inx,pageSize,funcCode,value);
                return false;
            }
        }()
    },
    init_goods_list:function(list){
        var html="";
        var self=this;
        if(list.length==0 && self.vip_page_num==1){
            $("#goods_vip_list_p").next().show();
        }else{
            $("#goods_vip_list_p").show();
            for(var i=0;i<list.length;i++){
                var order_detail=list[i].order_detail;
                var order_detail_html="";
                var onlyOrderId=[];
                var orderIdObject={};
                var orderId=[];
                for(var n=0;n<order_detail.length;n++){
                    orderId.push(order_detail[n].order_id)
                }
                for(var a=0;a<orderId.length;a++){
                    if(!orderIdObject[orderId[a]]){
                        orderIdObject[orderId[a]] = true; //存入hash表
                        onlyOrderId.push(orderId[a]);
                    }
                }
                for(var s=0;s<onlyOrderId.length;s++){
                    var TR="";
                    var date="";
                    var name="";
                    var points="";
                    for(var o=0;o<order_detail.length;o++){
                        TR+="";
                        if(order_detail[o].order_id==onlyOrderId[s]){
                            var n=$(TR).length+1;
                            date=order_detail[o].buy_time;
                            name=order_detail[o].user_name;
                            points=order_detail[o].points;
                            TR+='<tr>'
                                +'<td>'+n+'</td>'
                                +'<td>'+order_detail[o].goods_num+'</td>'
                                +'<td>'+order_detail[o].sku_code+'</td>'
                                +'<td>'+order_detail[o].goods_sug+'</td>'
                                +'<td>'+order_detail[o].discount+'</td>'
                                +'<td>'+order_detail[o].goods_price+'</td>'
                                +'</tr>'
                        }
                    }
                    order_detail_html+='<div class="goods_vip_list_line_content">'
                        +'<div class="goods_vip_list_line_content_title">'
                        +'<span class="name">日期：</span><span style="margin-right: 20px">'+date+'</span>'
                        +'<span class="name">订单号：</span><span style="margin-right: 20px">'+onlyOrderId[s]+'</span>'
                        +'<span class="name">积分：</span><span>'+points+'</span>'
                        +'<div style="float: right"><span class="name">导购：</span><span>'+name+'</span></div>'
                        +'</div>'
                        +'<div class="goods_vip_list_line_content_table">'
                        +'<table>'
                        +'<thead>'
                        +'<tr><th>序号</th><th>购买件数</th><th>商品条码</th><th>吊牌价</th><th>折扣</th><th>价格</th></tr>'
                        +'</thead>'
                        +'<tbdoy>'
                        +TR
                        +'</tbdoy>'
                        +'</table>'
                        +'</div>'
                        +'</div>'
                }
                html+='<div class="goods_vip_list">'
                    +'<div class="goods_vip_list_line">'
                    +'<div class="goods_vip_list_line_title">'
                    +'<span class="name">会员名:</span><span title="'+list[i].vip_name+'">'+list[i].vip_name+'</span>'
                    +'<span class="name">手机号:</span><span title="'+list[i].vip_phone+'">'+list[i].vip_phone+'</span>'
                    +'<span class="name">会员等级:</span><span title="'+list[i].vip_card_type+'">'+list[i].vip_card_type+'</span>'
                    +'<span class="name">会员卡号:</span><span title="'+list[i].card_no+'">'+list[i].card_no+'</span>'
                    +'<div style="position: absolute;right: 30px;top: 0;"><span class="name">单数:</span><span>'+list[i].order_count+'</span></div>'
                    + '<span class="icon-ishop_8-02" ></span>'
                    +'</div>'
                    +'</div>'
                    +order_detail_html
                    +'</div>'
            }
            $("#goods_vip_list_p").append(html);
            if(!self.vip_isscroll){
                $("#goods_vip_list_p").scrollTop(0);
                $("#goods_vip_list_p").unbind("scroll").bind("scroll",function(){
                    var nScrollHight = $(this)[0].scrollHeight;
                    var nScrollTop = $(this)[0].scrollTop;
                    var nDivHight=$(this).height();
                    if(nScrollTop + nDivHight >= (nScrollHight-5) && nScrollTop != 0){
                        if(!self.vip_next_page){
                            return;
                        }
                        self.vip_next_page=false;
                        self.getMoreData(self.sku_id,"vip");
                    }
                });
            }
            self.vip_isscroll=true;
        }
    },
    initChart:function(message,type_echart){
        var self=this;
        var list_chart=message.list;
        if(list_chart.length==0){
            $(".goods_vip_table_content div:last-child").show();
            $(".goods_vip_table_content div:last-child").siblings().hide();
            return
        }
        $(".goods_vip_table_content div:last-child").hide();
        $(".goods_vip_table").show();
        if(type_echart=="province"){
            var chart_province=echarts.init(document.getElementById("chart_province"));
            self.option_map.series.data=[];
            for(var s=0;s<list_chart.length;s++){
                var name=list_chart[s].key;
                name=name.replace(/省|市/,'');
                name=name.replace(/广西壮族自治区/,'广西');
                name=name.replace(/内蒙古自治区/,'内蒙古');
                name=name.replace(/新疆维吾尔自治区/,'新疆');
                name=name.replace(/西藏自治区/,'西藏');
                name=name.replace(/宁夏回族自治区/,'宁夏');
                name=name.replace(/香港特别行政区/,'香港');
                name=name.replace(/澳门特别行政区/,'澳门');
                if(name=="钓鱼岛"){
                    continue
                }
                self.option_map.series.data.push({
                    name:name,
                    value:list_chart[s].amt_trade
                });
            }
            chart_province.setOption(self.option_map);
        }else if(type_echart=="age") {
            //self.option["xAxis"].name="年龄";
            self.option.xAxis.data=[];
            self.option.series[0].data=[];
            self.option.series[1].data=[];
            self.option.series[2].data=[];
            self.option.series[3].data=[];
            for(var s=0;s<list_chart.length;s++){
                self.option.xAxis.data.push(list_chart[s].key);
                self.option.series[0].data.push(list_chart[s].num_trade);
                self.option.series[1].data.push(list_chart[s].num_sales);
                self.option.series[2].data.push(list_chart[s].discount);
                self.option.series[3].data.push(list_chart[s].amt_trade);
            }
            var chart_age=echarts.init(document.getElementById("chart_age"));
            chart_age.setOption(self.option);
        }else{
            //self.option["xAxis"].name="性别";
            self.option.xAxis.data=[];
            self.option.series[0].data=[];
            self.option.series[1].data=[];
            self.option.series[2].data=[];
            self.option.series[3].data=[];
            for(var s=0;s<list_chart.length;s++){
                self.option.xAxis.data.push(list_chart[s].key);
                self.option.series[0].data.push(list_chart[s].num_trade);
                self.option.series[1].data.push(list_chart[s].num_sales);
                self.option.series[2].data.push(list_chart[s].discount);
                self.option.series[3].data.push(list_chart[s].amt_trade);
            }
            var chart_sex=echarts.init(document.getElementById("chart_sex"));
            chart_sex.setOption(self.option);
        }
    },
    getMoreData:function (sku_id,type,type_echart){
        var self=this;
        var param ={};
        param.corp_code = this.corp_code;
        param.page_num = this.vip_page_num;
        param.page_size = this.vip_page_size;
        param.store_code = this.store_code;
        param.time_type = this.date_type;
        param.time_value = this.date_value.trim();
        param.sku_id = sku_id;
        param.query_type=type;
        param.row_key=type_echart;
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post","/vipAnalysis/skuSales","",param,function (data) {
            whir.loading.remove("",0.5);//加载等待框
            if(data.code==0){
                $("#goods_vip").show();
                var message= JSON.parse(data.message);
                if(type=="group"){
                    self.initChart(message,type_echart)
                }else if(type=="vip"){
                    var list=message.vip_list;
                    $("#vips").html(message.total);
                    if(message.pages>self.vip_page_num){
                        self.vip_next_page=true;
                        self.vip_page_num++;
                    }else {
                        self.vip_next_page=false
                    }
                    self.init_goods_list(list)
                }
            }
        })
    }
    ,selectMore:function(){
        var self=this;
        $(".table").on("click",".detail_list",function(e){
            e.stopPropagation();
            self.sku_id=$(this).parents("tr").attr("data-sku_id");
            var sku_code=$(this).parents("tr").attr("data-sku_code");
            var product_name=$(this).parents("tr").attr("data-product_name");
            $("#product_name").html(product_name);
            $("#sku_code").html(sku_code);
            self.vip_page_num=1;
            $("#goods_vip_list_p").empty();
            $(".goods_vip_title span:last-child").html("购买会员");
            self.getMoreData(self.sku_id,"vip");
        });
        $("#hide_goods_vip").click(function () {
            $("#goods_vip").hide();
            $("#goods_vip_list_p").hide();
            $("#goods_vip_list_p").next().hide();
            $(".goods_vip_table").hide();
            self.vip_isscroll=false;
            self.vip_page_num=1;
            $("#goods_vip_list_p").unbind("scroll");
        });
        $("#goods_vip_list_p").on("click",".goods_vip_list_line_title",function(e){
            var self=$(this);
            var timer= setInterval(function(){$(".goods_vip_list_p").getNiceScroll().resize()},0);
            if($(this).parents(".goods_vip_list_line").nextAll().length!=0 && $(this).hasClass("active")==false){
                $(this).parents(".goods_vip_list_line").css("borderBottom","1px solid #d7d7d7");
                $(this).parents(".goods_vip_list").css("paddingBottom","10px");
            }
            $(this).find(".icon-ishop_8-02").hasClass("active")==true?$(this).find(".icon-ishop_8-02").removeClass("active"):$(this).find(".icon-ishop_8-02").addClass("active");
            $(this).parents(".goods_vip_list_line").nextAll().slideToggle(function(){
                clearInterval(timer);
                if(!self.find(".icon-ishop_8-02").hasClass("active")){
                    self.parents(".goods_vip_list_line").css("borderBottom","none");
                    self.parents(".goods_vip_list").css("paddingBottom","0");
                }
            });
        });
    },
    selectMoreTable:function(){
        var self=this;
        //window.addEventListener("resize", function () {
        //    chart_sex.resize();
        //});
        $(".table").on("click",".detail_table",function(e){
            e.stopPropagation();
            self.sku_id=$(this).parents("tr").attr("data-sku_id");
            var sku_code=$(this).parents("tr").attr("data-sku_code");
            var product_name=$(this).parents("tr").attr("data-product_name");
            $("#vips").html($(this).parents("tr").attr("data-vip_num"));
            $("#product_name").html(product_name);
            $("#sku_code").html(sku_code);
             $(".goods_vip_table_menu").children().eq(0).trigger("click");
             $(".goods_vip_title span:last-child").html("分组统计");

             //self.getMoreData(sku_id,"group","sex");
        });
        $(".goods_vip_table_menu li").click(function(){
            var index=$(this).index();
            $(this).addClass("active").siblings().removeClass("active");
            $(".goods_vip_table_content").children().eq(index).show().siblings().hide();
            if($(this).html()=="省份"){
                self.getMoreData(self.sku_id,"group","province");
            }else if($(this).html()=="性别"){
                self.getMoreData(self.sku_id,"group","sex");
            }else if($(this).html()=="年龄"){
                self.getMoreData(self.sku_id,"group","age");
            }
        })
    },
    movePop:function(ele,handle){
        $(ele).mousedown(function (e) {
            //设置移动后的默认位置
            var endx = 0;
            var endy = 0;
            //获取div的初始位置，要注意的是需要转整型，因为获取到值带px
            var left = parseInt($(ele).css("left"));
            var top = parseInt($(ele).css("top"));

            //获取鼠标按下时的坐标，区别于下面的es.pageX,es.pageY
            var downx = e.pageX;
            var downy = e.pageY;     //pageY的y要大写，必须大写！！

            //    鼠标按下时给div挂事件
            $(ele).bind("mousemove", function (es) {
                //es.pageX,es.pageY:获取鼠标移动后的坐标
                endx = es.pageX - downx + left;     //计算div的最终位置
                endy = es.pageY - downy + top;
                //带上单位
                $(ele).css("left", endx + "px").css("top", endy + "px")
            });
            if (!$(e.target).is($(handle))) {
                $(ele).unbind("mousemove");
            }
        }); //移动筛选框
        $(ele).mouseup(function () {
            //鼠标弹起时给div取消事件
            $(ele).unbind("mousemove")
        });
    }
};
commodity_analysis.init();
//点击右移
$(".shift_right").click(function () {
    var right = "only";
    var div = $(this);
    removeRight(right, div);
});
//点击右移全部
$(".shift_right_all").click(function () {
    var right = "all";
    var div = $(this);
    removeRight(right, div);
});
//点击左移
$(".shift_left").click(function () {
    var left = "only";
    var div = $(this);
    removeLeft(left, div);
});
//点击左移全部
$(".shift_left_all").click(function () {
    var left = "all";
    var div = $(this);
    removeLeft(left, div);
});
//移到右边
function removeRight(a, b) {
    var li = "";
    if (a == "only") {
        li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li:visible");
    }
    if (a == "all") {
        li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox'][data-type!='define']").parents("li:visible");
    }
    if (li.length == "0") {
        art.dialog({
            zIndex: 10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请先选择"
        });
        return;
    }
    if (li.length > 0) {
        for (var i = 0; i < li.length; i++) {
            var html = $(li[i]).html();
            var id = $(li[i]).find("input[type='checkbox']").val();
            $(li[i]).find("input[type='checkbox']")[0].checked = true;
            var input = $(b).parents(".screen_content").find(".screen_content_r li");
            for (var j = 0; j < input.length; j++) {
                if ($(input[j]).attr("id") == id) {
                    $(input[j]).remove();
                }
            }
            $(b).parents(".screen_content").find(".screen_content_r ul").prepend("<li id='" + id + "'>" + html + "</li>");
            $(b).parents(".screen_content").find(".screen_content_r input[value='" + id + "']").removeAttr("checked");
        }
    }
    var num = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
    // $("#screen_staff .screen_content_l li:odd").css("backgroundColor", "#fff");
    // $("#screen_staff .screen_content_l li:even").css("backgroundColor", "#ededed");
    // $("#screen_staff .screen_content_r li:odd").css("backgroundColor", "#fff");
    // $("#screen_staff .screen_content_r li:even").css("backgroundColor", "#ededed");
}
//移到左边
function removeLeft(a, b) {
    var li = "";
    if (a == "only") {
        li = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']:checked").parents("li");
    }
    if (a == "all") {
        li = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li");
    }
    if (li.length == "0") {
        art.dialog({
            zIndex: 10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请先选择"
        });
        return;
    }
    if (li.length > 0) {
        for (var i = li.length - 1; i >= 0; i--) {
            $(li[i]).remove();
            $(b).parents(".screen_content").find(".screen_content_l input[value='" + $(li[i]).attr("id") + "']").removeAttr("checked");
        }
    }
    var num = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
}
//点击列表显示选中状态
$(".screen_content").on("click", "li", function () {
    var input = $(this).find("input")[0];
//        var type=$(this).find("input").eq(0).attr("data-type");
//        if(type=="define"){
//            art.dialog({
//                zIndex: 10010,
//                time: 1,
//                lock: true,
//                cancel: false,
//                content: "暂不支持选择自定义分组"
//            });
//            return false;
//        }
    if (input.type == "checkbox" && input.checked == false ) {
        input.checked = true;
    } else if (input.type == "checkbox" && input.checked == true) {
        input.checked = false;
    }
});
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
$(document).click(function (e) {
    if(!($(e.target).is(".shopperSelect")||$(e.target).is(".shopperSelect input")||$(e.target).is(".shopperSelect li")||$(e.target).is(".checkbox_select_input"))){
        $(".shopperSelect").hide();
    }
});

$(".goods_vip_list_p").niceScroll({
    cursorborder:"0 none",cursoropacitymin:"0",boxzoom:false,
    cursorcolor:" #dddddd",
    cursoropacitymax:1,
    touchbehavior:false,
    cursorminheight:200,
    autohidemode:false,
    cursorwidth:"9px",
    cursorborderradius:"10px"});

function getOldValue(dom){
    if($("#"+dom).val()!="全部"){
        return  $("#"+dom).val().split(",");
    }
}

$("#date_select_all").parent("label").click(function(){
    var check=$(".date_opt_checked .checkbox input").attr("checked");
    if(check!=undefined && check){
        $("#date_box ul:visible li span").addClass("click_font");
        if($("#date_box ul:visible").is($(".week_detail"))){
            $("#date_box ul:visible li .week_detail_w").addClass("click_font");
        }
    }else{
        $("#date_box ul:visible li span").removeClass("click_font");
        if($("#date_box ul:visible").is($(".week_detail"))){
            $("#date_box ul:visible li .week_detail_w").removeClass("click_font");
        }
    }

    $("#date_box ul:visible li input").attr("checked",check==undefined?false:check);
    var len=$("#date_box ul:visible li input:checked").length;
    $("#date_sure_num").html("已选"+len+"个")
});

$("#date_sure").click(function(){
   var dateClass=$("#date_box ul:visible").attr("class");
   var dateValue="";
    switch (dateClass){
        case "week_detail":
            var input=$(".week_detail li .checkbox input:checked");
             dateValue=  input.map(function(){
                return $(this).parents("#week_id").find('.week_detail_d').html().split('至')[0].trim();
            }).get().join(",");
            commodity_analysis.date_type='weekly';
            commodity_analysis.date_value=dateValue;
            if(dateValue==""){
                $("#time").html("请选择时间");
            }else{
                $("#time").html("已选"+dateValue.split(",").length+"个周");
            }
            break;
        case "month_detail":
            var input=$(".month_detail li .checkbox input:checked");
             dateValue=  input.map(function(){
                var time=$(this).parents("#month_id").find('span').html();
                var s=time.search('年');
                var e=time.search('月');
                var y=time.slice(s+1,e)>9?time.slice(s+1,e):'0'+time.slice(s+1,e);
                return parseInt(time)+'-'+y+'-01';
            }).get().join(",");
            commodity_analysis.date_type='monthly';
            commodity_analysis.date_value=dateValue;
            if(dateValue==""){
                $("#time").html("请选择时间");
            }else{
                $("#time").html("已选"+dateValue.split(",").length+"个月");
            }
            break;
        case "year_detail":
            var input=$(".year_detail li .checkbox input:checked");
            dateValue=  input.map(function(){
                var time=$(this).parents("#year_id").find('span').html();
                return parseInt(time)+'-01-01';
            }).get().join(",");
            commodity_analysis.date_type='yearly';
            commodity_analysis.date_value=dateValue;
            if(dateValue==""){
                $("#time").html("请选择时间");
            }else{
                $("#time").html("已选"+dateValue.split(",").length+"个年");
            }
    }
    $("#date").hide();
});