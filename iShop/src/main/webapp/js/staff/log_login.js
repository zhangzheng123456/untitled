/**
 * Created by chtty on 2017/3/10.
 */
/**
 * Created by chtty on 2017/3/6.
 *
 */
var vip=new Vue({
    el:'#con_table'
    ,data:{
        time:'2017年3周'
        ,nav_range:false
        ,nav_shop:false
        ,now_week:''//当前第几周
        ,nav_date:false//是否显示
        ,time_show:true
        ,year_down:''
        ,chart_count:[]//纵坐标
        ,chart_item:[]//横坐标
        ,option_color:['#4176c0','#41c7db','#9999cc','#ba6e90','#6e578e','#ceabd5','#9585a1','#eaa2c2','#6e73ad','#d1cbd5','#6db0d4','#9ad5e2','#439cca','#2e5994','#13acc3','#434960']//线条颜色
        ,option_legend_data:[]//标题
        ,option_series: []//数据参数
        ,time_start:'2017-02-27'//周显示的开始时间
        ,time_end:'2017-03-05'//周显示的结束时间
        ,isActive_w:true//激活状态周下划线
        ,isActive_m:false//激活状态
        ,isActive_y:false//激活状态
        ,week:true//周选项卡的显示切换
        ,month:false//月选项卡的显示切换
        ,year:false//年选项卡的显示切换
        ,week_obj:[]//周的数据
        ,month_arr:[]//月的数据
        ,year_arr:[]//年的数据
        ,brand_input:''//已选品牌个数
        ,area_input:''//已选区域个数
        ,store_input:''//已选店铺个数
        ,guide_input:''//已选导购个数
        ,brand_input_arr:[]//保存子组件已选的obj
        ,area_input_arr:[]//保存子组件已选的obj
        ,store_input_arr:[]//保存子组件已选的obj
        ,guide_input_arr:[]//保存子组件已选的obj
        ,message_title:''//筛选标题
        ,filtrate:false//不显示弹窗
        ,message_obj:{}//筛选已选中数据
        ,component_url:''//组件的请求参数
        ,post_start_param:''//起始图请求参数
        ,post_table_param:''//起始表请求参数
        ,data_type:''//日期类型
        ,data_value:''//日期具体时间
        ,stop_next_page:''//是否进行下一页
        ,data_no:false//没有数据时
        ,loading_show:''//加载遮罩
        ,next_page:1//请求页码
        ,chart_loading:false
        ,tableData:[]//列表数据
        ,export_show:false//导出
        ,willShow:false//控制隐藏
        ,src:''
    }
    ,methods:{
        productFun:function(){
            this.nav_shop=!this.nav_shop;
        }
        ,functionFun:function(e){
            $('#nav .nav_1>span').html(arguments[0]);
            this.tableData=[];
            this.chartFun();
            this.tableFun();
        }
        ,rangeFun:function(){
            this.nav_range=!this.nav_range;
            if(this.nav_range){
                whir.loading.add("",0.5);//加载等待框
                $('#loading').remove();
                this.nav_date=false;
                this.nav_shop=false;
            }else{
                whir.loading.remove('mask');//移除加载框
            }
        }
        ,timeFun:function (e) {
            if(e.target.className.search('week')!=-1){
                this.week=true;this.month=false;this.year=false;
                this.isActive_w=true;this.isActive_m=false;this.isActive_y=false;
            }
            if(e.target.className.search('month')!=-1){
                this.week=false;this.month=true;this.year=false;
                this.isActive_w=false;this.isActive_m=true;this.isActive_y=false;
            }
            if(e.target.className.search('year')!=-1){
                this.week=false;this.month=false;this.year=true;
                this.isActive_w=false;this.isActive_m=false;this.isActive_y=true;
            }
            if(e.target.className.search('week_arrow_left')!=-1){//递减一年的周
                this.year_down-=1;
                fun.dateFun(this.year_down,this);
            }
            if(e.target.className.search('week_arrow_right')!=-1){//递增一年的周
                this.year_down+=1;
                fun.dateFun(this.year_down,this);
            }
            if(e.target.className.search('month_arrow_left')!=-1){//递减一年的yue
                this.year_down-=1;
                fun.dateFun(this.year_down,this);
            }
            if(e.target.className.search('month_arrow_right')!=-1){//递增一年的月10
                this.year_down+=1;
                fun.dateFun(this.year_down,this);
            }
        }
        ,timeChange:function (e) {
            var time_arr=[];
            this.next_page=1;
            if(e.currentTarget.id=='year_id'){
                this.time_show=false;
                var h=$(e.target).html();
                this.time=$(e.target).html();
                this.year_arr.forEach(function(obj){
                    obj.key=false;
                    if(obj.val==h){
                        obj.key=true;
                    }
                });
                this.data_type='Y';
                this.data_value=parseInt(this.time)+'-01-01';
                // whir.loading.add("",0.5);//加载等待框
                this.chartFun();//调用区分年月
                this.tableData=[];
                this.loading_show=true;
                this.tableFun();
            }else if(e.currentTarget.id=='month_id'){
                this.time_show=false;
                var h=$(e.target).html();
                this.time=$(e.target).html();
                this.month_arr.forEach(function(obj){
                    obj.key=false;
                    if(obj.val==h){
                        obj.key=true;
                    }
                });
                var s=this.time.search('年');
                var e=this.time.search('月');
                var y=this.time.slice(s+1,e)>9?this.time.slice(s+1,e):'0'+this.time.slice(s+1,e);
                this.data_type='M';
                this.data_value=parseInt(this.time)+'-'+y+'-01';
                // whir.loading.add("",0.5);//加载等待框
                this.chartFun();//月
                this.tableData=[];
                this.loading_show=true;
                this.tableFun();
            }else if(e.currentTarget.id=='week_id'){
                time_arr=$(e.target).parent().find('.week_detail_d').html().split('至');
                this.time_start=time_arr[0];
                this.time_end=time_arr[1];
                this.time=$(e.target).parent().find('.week_detail_w').html();
                var n_time=this.time;
                this.week_obj.forEach(function(obj){
                    obj.title_class=false;
                    if(obj.title==n_time){
                        obj.title_class=true;
                    }
                });
                this.time_show=true;
                this.data_type='W';
                this.data_value=time_arr[0];
                // whir.loading.add("",0.5);//加载等待框
                this.chartFun();//调用区分年月
                this.tableData=[];
                this.loading_show=true;
                this.tableFun();
            }
            this.nav_date=false;
        }
        ,paramFun:function () {
            var brand_code=[];
            var area_code=[];
            var store_code=[];
            var user_code=[];
            brand_code=this.brand_input_arr.map(function (val) {
                return val.code;
            });
            area_code=this.area_input_arr.map(function (val) {
                return val.code;
            });
            store_code=this.store_input_arr.map(function (val) {
                return val.code;
            });
            user_code=this.guide_input_arr.map(function (val) {
                return val.code;
            });
            var param={'id':'','message':{
                'corp_code':'C10000'
                ,'brand_code':brand_code.join(',')
                ,'area_code':area_code.join(',')
                ,'store_code':store_code.join(',')
                ,'user_code':user_code.join(',')
                //,'user_code':'ABC123'
                ,'date_type':this.data_type
                ,'date_value':this.data_value
                //,'date_value':'2017-03-08'
            }};
            return param;
        }
        ,chartFun:function () {
            var brand_code=[];
            var area_code=[];
            var store_code=[];
            var user_code=[];
            this.chart_item=[];//初始化横坐标
            this.option_series=[];//初始化数据
            this.chart_count=[]//初始化纵坐标
            var url='/apploginlog/view';
            brand_code=this.brand_input_arr.map(function (val) {
                return val.code;
            });
            area_code=this.area_input_arr.map(function (val) {
                return val.code;
            });
            store_code=this.store_input_arr.map(function (val) {
                return val.code;
            });
            user_code=this.guide_input_arr.map(function (val) {
                return val.code;
            });

            var param={'id':'','message':{
                'corp_code':'C10000'
                ,'brand_code':brand_code.join(',')
                ,'area_code':area_code.join(',')
                ,'store_code':store_code.join(',')
                ,'user_code':user_code.join(',')
                //,'user_code':'ABC123'
                ,'date_type':this.data_type
                ,'date_value':this.data_value
                //,'date_value':'2017-03-08'
            }};
            this.chart_loading=true;
            this.$http.post(url,{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
                console.log(data.data.message);
                var view_type=JSON.parse(data.data.message).view_type;//图类型
                var myChart = echarts.init(document.getElementById('chart'));
                var data_arr=JSON.parse(data.data.message).login_logs;
                console.log(view_type);
                console.log(data_arr)
                //循环遍历取出
                for(var i=0;i<data_arr.length;i++){
                    this.chart_item.push(data_arr[i].date)//横坐标
                }
                //取纵标
                myChart.setOption(this.option_bar);
                this.chart_loading=false;
                window.addEventListener("resize", function () {
                    myChart.resize();
                });
            }, function (data) {

            });
        }
        ,scrollFun:function (e) {
            var offsetHeight = e.currentTarget.offsetHeight,
                scrollHeight = e.target.scrollHeight,
                scrollTop = e.target.scrollTop;
            if(scrollTop>=scrollHeight-offsetHeight){
                this.loading_show=true;
                if(this.stop_next_page=='go'){
                    this.next_page++;
                }else{
                    this.loading_show=false;
                    return
                }
                this.tableFun();
            }
        }
        ,tableFun:function () {
            var param=this.paramFun();
            param.message.pageNumber=this.next_page;
            param.message.pageSize=20;
            this.loading_show=true;
            this.$http.post('/apploginlog/newList',{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
                if(data.data.code==0){
                    var data_arr=JSON.parse(data.data.message).message;
                    if(data_arr.length==0){
                        this.data_no=true;
                        this.loading_show=false;
                        if(this.next_page>=JSON.parse(data.data.message).pages){
                            //禁止请求
                            this.stop_next_page='stop'
                        }else{
                            this.stop_next_page='go'
                        }
                    }else{
                        if(this.next_page>=JSON.parse(data.data.message).pages){
                            //禁止请求
                            this.stop_next_page='stop'
                        }else{
                            this.stop_next_page='go'
                        }
                        console.log(JSON.parse(data_arr));
                        this.tableData=this.tableData.concat(JSON.parse(data_arr));
                        this.loading_show=false;
                        this.data_no=false;
                    }
                }else if(data.data.code==-1){
                    this.loading_show=false;
                    this.data_no=false;
                    art.dialog({
                        time: 2,
                        lock: true,
                        cancel: false,
                        content: data.data.message
                    });
                }
            });
        }
        ,filtrateFun:function (e,str) {
            var role='';
            if(arguments[0]=='所属品牌'){
                role='所属品牌';
                this.message_title='筛选品牌'
            }else if(arguments[0]=='所属区域'){
                role='所属区域';
                this.message_title='筛选区域'
            }else if(arguments[0]=='所属店铺'){
                role='所属店铺';
                this.message_title='筛选店铺'
            }else{
                this.filtrate=!this.filtrate;
                this.message_title='筛选'+$(e.target).parents('.contion_input').find('label').html().slice(2);
                role=$(e.target).parents('.contion_input').find('label').html();
            }
            var brand_code=[];
            var area_code=[];
            var store_code=[];
            if(role=='所属品牌'){
                this.post_start_param={'id':'','message':{'corp_code':'C10000',searchValue:''}};
                this.component_url="/shop/brand";
                this.message_obj.show=false;
            }else if(role=='所属区域'){
                this.post_start_param={'id':'','message':{'corp_code':'C10000',searchValue:'','pageSize':20,'pageNumber':1}};
                this.component_url="/area/selAreaByCorpCode";
                this.message_obj.show=false;
            }else if(role=='所属店铺'){
                brand_code=this.brand_input_arr.map(function (val) {
                    return val.code;
                });
                area_code=this.area_input_arr.map(function (val) {
                    return val.code;
                });
                this.post_start_param={'id':'','message':{'corp_code':'C10000',searchValue:'','pageSize':20,'pageNumber':1,'area_code':area_code.join(','),'brand_code':brand_code.join(',')}};
                this.component_url="/shop/selectByAreaCode";
                this.message_obj.show=true;
                this.message_obj.data=[];
                this.message_obj.data.push({msg1:'已选品牌',msg2:'已选'+brand_code.length+'个'},{msg1:'已选区域',msg2:'已选'+area_code.length+'个'});
            }else if(role=='所属导购'){
                brand_code=this.brand_input_arr.map(function (val) {
                    return val.code;
                });
                area_code=this.area_input_arr.map(function (val) {
                    return val.code;
                });
                store_code=this.store_input_arr.map(function (val) {
                    return val.code;
                });
                this.post_start_param={'id':'','message':{'corp_code':'C10000',searchValue:'','pageSize':20,'pageNumber':1,'area_code':area_code.join(','),'brand_code':brand_code.join(','),'store_code':store_code.join(',')}};
                this.component_url="/user/selectUsersByRole";
                this.message_obj.show=true;
                this.message_obj.data=[];
                this.message_obj.data.push({msg1:'已选品牌',msg2:'已选'+brand_code.length+'个'},{msg1:'已选区域',msg2:'已选'+area_code.length+'个'},{msg1:'已选店铺',msg2:'已选'+store_code.length+'个'});
            }
        }
        ,filtrateReturnFun:function (role,data,action) {
            if(action=='确定'){
                switch (role){
                    case '/shop/brand':this.brand_input_arr=data.slice(0);this.brand_input='已选'+data.length+'个';break;
                    case '/area/selAreaByCorpCode':this.area_input_arr=data.slice(0);this.area_input='已选'+data.length+'个';break;
                    case '/shop/selectByAreaCode':this.store_input_arr=data.slice(0);this.store_input='已选'+data.length+'个';break;
                    case '/user/selectUsersByRole':this.guide_input_arr=data.slice(0);this.guide_input='已选'+data.length+'个';break;
                }
            }
            this.filtrate=false;
        }
        ,filtrateFunMore:function (str) {
            this.filtrateFun(str);
        }
        ,confirm:function () {
            this.nav_range=!this.nav_range;
            if(this.nav_range){
                whir.loading.add("",0.5);//加载等待框
                $('#loading').remove();
            }else{
                whir.loading.remove('mask');//移除加载框
            }
            this.next_page=1;
            // whir.loading.add("",0.5);//加载等待框
            this.chartFun();
            this.tableData=[];
            this.loading_show=true;
            this.tableFun();
        }
        ,iframePop:function(a){//点击预览弹出框
            this.src=a;
            whir.loading.add("mask",0.5);//加载等待框
            this.nav_shop=false;
            this.nav_date=false;
            this.nav_range=false;
            if(this.willShow==true){
                this.willShow=false;
            }else{
                this.willShow=true;
            }
        }
        ,iframeReturn:function(e){//点击返回关闭弹出框
            whir.loading.remove('mask');
            if(this.willShow==true){
                this.willShow=false;
            }else{
                this.willShow=true;
            }
        }
        ,exportFun:function (e) {
            //调接口
            //改名称
            if($(e.target).html()=='确认') {
                var param = this.paramFun();
                whir.loading.add("", 0.5);//加载等待框
                $('#mask').css('z-index',10002);
                $('#loading').css('z-index',10002);
                this.$http.post('/appManager/exportGoodsExecl', {param: JSON.stringify(param)}, {emulateJSON: true}).then(function (data) {
                    $(e.target).html('<a href="/' + JSON.parse(JSON.parse(data.data.message).path) + '">下载文件</a>');
                    $('#export_consumeExport p').html('是否下载列表');
                    whir.loading.remove();//移除加载框
                });
            }else{
                this.export_show=!this.export_show;
                whir.loading.remove('mask');//移除加载框
            }
        }
        ,exportLoading:function(){
            this.export_show=!this.export_show;
            if(this.export_show){
                whir.loading.add("",0.5);//加载等待框
                $('#loading').remove();
                this.nav_shop=false;
                this.nav_date=false;
            }else{
                whir.loading.remove('mask');//移除加载框
            }
        }
    }
    ,computed:{
        option:function () {
            return {
                backgroundColor:"#fff",
                color:this.option_color,
                tooltip: {
                    trigger: 'axis',
                    axisPointer:{
                        lineStyle:{
                            color:"#97c94b"
                        }
                    }
                },
                legend: {
                    show:true,
                    right:'10',
                    data:this.option_legend_data
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                toolbox: {
                    feature: {
                        saveAsImage: {
                            show:false
                        }
                    }
                },
                xAxis: {
                    axisLine:{
                        lineStyle:{
                            color:"#e1e1e1",
                            width:"5"
                        }
                    },
                    nameTextStyle:{
                        color:"#2c323e"
                    },
                    boundaryGap:false,
                    type: 'category',
                    // data: ['12-26','12-27','12-28','12-29','12-30','12-31','1-1','1-2','1-3','1-4','1-5','1-6','1-7','1-8','1-9','1-10','1-11','1-12','1-13','1-14','1-15','1-16','1-17','1-18','1-19','1-20','1-21','1-22','1-23','1-24']
                    data: this.chart_item
                },
                yAxis: {
                    axisLine:{
                        lineStyle:{
                            color:"#e1e1e1",
                            width:"0"
                        }
                    },
                    boundaryGap:false,
                    type: 'value'
                },
                textStyle:{
                    color:"#2c323e"
                },
                series:this.option_series
            };
        }
        ,option_bar:function(){
            return {
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                legend: {
                    show:true,
                    right:'10',
                    data:['未登录','登录']
                },
                textStyle:{
                    color:"#888"
                },
                xAxis : [
                    {
                        type : 'category',
                        axisTick: {
                            show: false
                        },
                        axisLine:{
                            lineStyle: {
                                color: 'eee'
                            }
                        },
                        data : ['1-1','1-2','1-3','1-4','1-5','1-6','1-7','1-8','1-9','1-10','1-11','1-12','1-13','1-14','1-15','1-16']
                    }
                ],
                yAxis : [
                    {
                        type : 'value',
                        axisLine: {
                            show: false
                        },
                        axisTick: {
                            show: false
                        }
                    }
                ],
                tooltip: {
                    trigger: 'item',
                    formatter: "{a} <br/>{b}: {c}"
                },
                series : [
                    {
                        name:'未登录',
                        type:'bar',
                        stack: 'consumption',
                        itemStyle: {
                            normal: {
                                color: '#d8d8e1'
                            }
                        },
                        barWidth:4,
                        xAxisIndex:0,
                        barGap:0,
                        data:[550, 132, 101, 134, 90, 230, 210,550, 132, 101, 134, 90, 230, 210,210,210]
                    },
                    {
                        name:'登录',
                        type:'bar',
                        stack: 'consumption1',
                        itemStyle: {
                            normal: {
                                color: '#5f8bc8'
                            }
                        },
                        barWidth:4,
                        data:[220, 182, 191, 234, 290, 330, 310,220, 182, 191, 234, 290, 330, 310,210,210]
                    },
                ]
            };
        }
        ,param_post:function () {
            return JSON.stringify(this.param)
        }
    }
    ,components: {
        'filtrateComponent':fun.filtrateComponent
    }
    ,beforeMount:function () {
        fun.timeFiltrate(this);
        fun.yearFun(this);
        this.chartFun();
        this.tableFun();
    }
});
