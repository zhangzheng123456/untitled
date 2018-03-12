/**
 * Created by hu.x on 2017/3/17.
 *
 */
var vip=new Vue({
    el:'#con_table'
    ,data:{
        time:'2017年3周'
        ,nav_range:false
        ,nav_shop:false
        ,show_store:false
        ,shop_type:true
        ,show_shop:true
        ,show_store_group:false
        ,nav_staff:false
        ,nav_shop_public:false
        ,nav_shop_type:false
        ,nav_store_group:false
        ,nav_store:false
        ,nav_shop_type_first:false
        ,show_staff:false
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
        ,store_group_page:1//请求页码
        ,store_page:1//请求页码
        ,staff_page:1//请求页码
        ,chart_loading:false
        ,tableData:[]//列表数据
        ,export_show:false//导出
        ,willShow:false//控制隐藏
        ,src:''
        ,tabToggle:'0'  //列表选项卡切换
        ,isActive: true
        ,nav_dom:[]   //appName
        ,nav_type_dom_first:[{'typename':'渠道二维码分析','type':'qudao'},{'typename':'店铺二维码分析','type':'store'},{'typename':'导购二维码分析','type':'staff'}]
        //,nav_type_dom_first:[{'typename':'渠道二维码分析','type':'qudao'},{'typename':'导购二维码分析','type':'staff'}]
        ,nav_type_dom:[{'typename':'材料类','type':'material'},{'typename':'印刷类','type':'print'},{'typename':'礼品类','type':'gift'}]
        ,nav_name_dom:[]  //二维码名称
        ,nav_type_staff:[]  //导购
        ,store_group_data:[]  //店铺群组
        ,store_data:[]  //店铺
        ,show_public:'' //appName展示
        ,show_type:'材料类'
        ,store_group_name:'请选择店铺群组'
        ,store_name:'请选择店铺'
        ,staff_name:'请选择导购'
        ,store_group_code:''
        ,store_code:''
        ,staff_code:''
        ,show_type_first:'渠道二维码分析'
        ,show_name:'暂无二维码'  //二维码名称展示
        ,aging:false    //时效 永久 true   30天  false
        ,date_type :'W'    //date_type W周 M月 Y年
        ,app_id:''    //app_id
        ,qrcode_id:''  //二维码id
        ,isActive_0:true  //关注度分析表
        ,isActive_1:false  //新增会员分析表
        ,'corp_code':'C10000'
        ,'type' : 'material'
        ,'chart_temp':false,
        'store_group_has_next':false,
        'store_has_next':false,
        'staff_has_next':false,
        people_all:0
    }
    ,methods:{
        hide: function () {
            this.nav_shop_public = false;
            this.nav_shop_type = false;
            this.nav_shop_type_first = false;
            this.nav_shop = false;
            this.nav_date=false;
            this.nav_staff=false;
            this.nav_store=false;
            this.nav_store_group=false;
        }
        //请选择公众号
        ,productFun_public: function () {
            this.nav_shop_public = !this.nav_shop_public;
            this.nav_shop_type = false;
            this.nav_shop = false;
            this.nav_shop_type_first = false;
            this.nav_staff = false;
            this.nav_store_group = false;
            this.nav_store = false;
            this.nav_date=false;
        }
        ,increment: function (e,name,app_id) {
            this.$emit('increment',$(e.target).html());
            this.show_public = name;
            this.tableData=[];
            this.next_page=1;
            for(i=0;i<this.nav_dom.length;i++){
                if(this.nav_dom[i].app_name == this.show_public){
                    this.app_id = this.nav_dom[i].app_id;
                }
            }
            if(this.show_type_first=="导购二维码分析"){
                if(this.staff_code!=""){
                    this.chartStaff();
                    this.tableStaff()
                }
            }else if(this.show_type_first=="店铺二维码分析"){
                    this.chartStore();
                    this.tableStore();
            }else{
                this.getPublic();
            }

        },
        getPublic: function () {
            //二维码名称
            this.nav_name_dom = [];
            var param={'id':'','message':{'app_id':this.app_id,type:this.type}};
            this.chart_loading=true;
            this.$http.post('/qrCode/qrcodeList',{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
                if(data.data.code ==0){
                    var msg = JSON.parse(data.data.message);
                    var list = JSON.parse(msg.list);
                    if(list!=''){
                        for(i=0;i<list.length;i++){
                            this.nav_name_dom.push({'qrcode_name':list[i].qrcode_name,'qrcode_id':list[i].id,'aging':list[i].aging});
                        }
                        this.show_name = this.nav_name_dom[0].qrcode_name;
                        this.qrcode_id = this.nav_name_dom[0].qrcode_id;
                        this.aging=(this.nav_name_dom[0].aging == '永久有效')?true:false;
                        this.chart_temp = false;
                        this.tableData = [];
                        this.chartFun();
                        this.tableFun();
                    }else{
                        this.show_name = '暂无二维码';
                        this.qrcode_id = '';
                        this.chart_item=[];//初始化横坐标
                        this.option_series=[];//初始化数据
                        this.chart_count=[]; //初始化纵坐标
                        this.aging = false;
                        this.chart_temp = true;
                        this.chart_loading=false;
                        this.tableData=[];
                        this.chartFun();
                        this.tableFun();
                    }
                }else if(data.data.code == -1){
                    this.show_name = '暂无二维码';
                    this.qrcode_id = '';
                    this.chart_item=[];//初始化横坐标
                    this.option_series=[];//初始化数据
                    this.chart_count=[];//初始化纵坐标
                    this.aging = false;
                    this.chart_temp = true;
                    this.chart_loading=false;
                    this.tableData=[];
                    this.chartFun();
                    this.tableFun();
                }

            })
        }
        //请选择类型
        ,productFun_type: function () {
            this.nav_shop_type = !this.nav_shop_type;
            this.nav_shop_type_first = false;
            this.nav_shop_public = false;
            this.nav_shop = false;
            this.nav_staff=false;
            this.nav_date=false;
        }  ,
        productFun_type_first: function () {
            this.nav_shop_type_first = !this.nav_shop_type_first;
            this.nav_shop_type = false;
            this.nav_shop_public = false;
            this.nav_shop = false;
            this.nav_staff=false;
            this.nav_store_group=false;
            this.nav_store=false;
            this.nav_date=false;
        },
        productFun_staff: function () {
            this.nav_staff = !this.nav_staff;
            this.nav_shop_type_first = false;
            this.nav_shop_type = false;
            this.nav_shop_public = false;
            this.nav_shop = false;
            this.nav_store_group=false;
            this.nav_store=false;
            this.nav_date=false;
        },
        productFun_store_group: function () {
            this.nav_store_group = !this.nav_store_group;
            this.nav_staff = false;
            this.nav_store = false;
            this.nav_shop_type_first = false;
            this.nav_shop_type = false;
            this.nav_shop_public = false;
            this.nav_shop = false;
            this.nav_date=false;
        },
        productFun_store: function () {
            this.nav_store = !this.nav_store;
            this.nav_store_group=false;
            this.nav_staff = false;
            this.nav_shop_type_first = false;
            this.nav_shop_type = false;
            this.nav_shop_public = false;
            this.nav_shop = false
            this.nav_date=false;
        },
        productFun_date: function () {
            this.nav_date=!this.nav_date;
            this.nav_store = false;
            this.nav_store_group=false;
            this.nav_staff = false;
            this.nav_shop_type_first = false;
            this.nav_shop_type = false;
            this.nav_shop_public = false;
            this.nav_shop = false;
        }
        ,increment_1: function (e,name) {
            this.$emit('increment',$(e.target).html());
            this.show_type = name;
            for(i = 0;i<this.nav_type_dom.length;i++){
                this.show_type == this.nav_type_dom[i].typename ? this.type = this.nav_type_dom[i].type:''
            }
            //二维码名称
            this.nav_name_dom = [];
            var param={'id':'','message':{'app_id':this.app_id,type:this.type}};
            this.chart_loading=true;
            this.next_page=1;
            this.$http.post('/qrCode/qrcodeList',{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
                if(data.data.code==0){
                    var msg = JSON.parse(data.data.message);
                    var list = JSON.parse(msg.list);
                    if(list!=''){
                        for(i=0;i<list.length;i++){
                            this.nav_name_dom.push({'qrcode_name':list[i].qrcode_name,'qrcode_id':list[i].id,'aging':list[i].aging});
                        }
                        this.show_name = this.nav_name_dom[0].qrcode_name;
                        this.qrcode_id = this.nav_name_dom[0].qrcode_id;
                        this.aging=(this.nav_name_dom[0].aging == '永久有效')?true:false;
                        this.chart_temp = false;
                        this.tableData = [];
                        this.chartFun();
                        this.tableFun();
                    }else{
                        this.show_name = '暂无二维码';
                        this.chart_item=[];//初始化横坐标
                        this.option_series=[];//初始化数据
                        this.chart_count=[]; //初始化纵坐标
                        this.aging =false;
                        this.chart_temp = true;
                        this.chart_loading =false;
                        this.tableData=[];
                        this.chartFun();
                        this.tableFun();
                    }
                }else if(data.code == -1){
                    this.show_name = '暂无二维码';
                    this.app_id = '';
                    this.qrcode_id = '';
                    this.chart_item=[];//初始化横坐标
                    this.option_series=[];//初始化数据
                    this.chart_count=[]; //初始化纵坐标
                    this.aging =false;
                    this.chart_temp = true;
                    this.chart_loading = false;
                    this.chart_item = [];
                    this.tableData=[];
                    this.chartFun();
                    this.tableFun();
                }
            })
        }
        //请选择二维码名称
        ,productFun:function(){
            this.nav_shop=!this.nav_shop;
            this.nav_shop_public = false;
            this.nav_shop_type = false;
            this.nav_shop_type_first=false;
            this.nav_date=false;
        }
        ,increment_2: function (e,name,aging) {
            this.$emit('increment',$(e.target).html());
            this.show_name = name;
            this.aging=(aging == '永久有效')?true:false;
            for(i=0;i<this.nav_name_dom.length;i++){
                if(this.nav_name_dom[i].qrcode_name == this.show_name){
                    this.qrcode_id = this.nav_name_dom[i].qrcode_id;
                }
            }
            this.tableData = [];
            this.next_page=1;
            this.chartFun();
            this.tableFun();
        } ,
        increment_store_shop:function(e,name,code){
            this.store_group_name = name;
            this.store_group_code=code;
            this.store_page=1;
            this.next_page=1;
            this.staff_page=1;
            this.store_data=[];
            this.tableData=[];
            this.nav_type_staff=[];
            this.store_name="请选择店铺";
            this.staff_name="请选择导购";
            this.store_code="";
            this.staff_code="";
            this.getStore()
        },
        increment_store:function(e,name,code){
            this.next_page=1;
            this.store_name = name;
            this.store_code=code;
            //this.staff_page=1;
            //this.staff_code="";
            //this.staff_name="请选择导购";
            //this.nav_type_staff=[];
            //this.getStaff();
            this.tableData=[];
            if(this.show_type_first=="店铺二维码分析"){
                this.chartStore();
                this.tableStore();
            }else  if(this.show_type_first=="导购二维码分析"){
                this.staff_page=1;
                this.staff_code="";
                this.staff_name="请选择导购";
                this.nav_type_staff=[];
                this.getStaff()
            }
        },
        increment_staff:function(e,name,code){
          this.next_page=1;
          this.staff_name=name;
          this.staff_code=code;
          this.tableData=[];
          this.chartStaff();
          this.tableStaff();
        },
        increment_first: function (e,name) {
            this.$emit('increment',$(e.target).html());
            this.show_type_first = name;
            this.next_page=1;
            this.tableData=[];
            if( name=="导购二维码分析"){
                this.show_shop=false;
                this.shop_type=false;
                this.show_staff=true;
                this.show_store=true;
                this.show_store=true;
                this.show_store_group=true;
                this.store_group_page=1;
                this.store_page=1;
                this.staff_page=1;
                this.store_group_data=[];
                this.store_data=[];
                this.nav_type_staff=[];
                this.store_group_code="";
                this.store_code="";
                this.staff_code="";
                this.aging=true;
                this.getStoreGroup();
            }else if(name=="店铺二维码分析"){
                this.show_shop=false;
                this.shop_type=false;
                this.show_staff=false;
                this.show_store=true;
                this.show_store=true;
                this.show_store_group=true;
                this.aging=true;
                this.store_group_page=1;
                this.store_page=1;
                this.store_group_code="";
                this.store_code="";
                this.store_group_data=[];
                this.store_data=[];
                this.getStoreGroup();
            }else{
                this.show_shop=true;
                this.shop_type=true;
                this.show_staff=false;
                this.show_store=false;
                this.show_store=false;
                this.show_store_group=false;
                this.aging=false;
                this.getPublic();
            }
            //this.aging=(aging == '永久有效')?true:false;
            //for(i=0;i<this.nav_name_dom.length;i++){
            //    if(this.nav_name_dom[i].qrcode_name == this.show_name){
            //        this.qrcode_id = this.nav_name_dom[i].qrcode_id;
            //    }
            //}
            //this.tableData = [];
            //this.chartFun();
            //this.tableFun();
        }
        ,functionFun:function(e){
            //$('#nav .nav_1>span').html(arguments[0]);
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
                this.date_type = 'W'
                this.week=true;this.month=false;this.year=false;
                this.isActive_w=true;this.isActive_m=false;this.isActive_y=false;
            }
            if(e.target.className.search('month')!=-1){
                this.date_type = 'M'
                this.week=false;this.month=true;this.year=false;
                this.isActive_w=false;this.isActive_m=true;this.isActive_y=false;
            }
            if(e.target.className.search('year')!=-1){
                this.date_type = 'Y'
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
                this.tableData=[];
                this.loading_show=true;
                //if(this.show_type_first=="导购二维码分析"){
                //    this.chartStaff();
                //    this.tableStaff();
                //}else if(this.show_type_first=="店铺二维码分析"){
                //    this.chartStore();
                //    this.tableStore()
                //}else{
                //    this.chartFun();//调用区分年月
                //    this.tableFun();
                //}
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
                this.tableData=[];
                this.loading_show=true;
                //if(this.show_type_first=="导购二维码分析"){
                //    this.chartStaff();
                //    this.tableStaff();
                //}else{
                //    this.chartFun();//调用区分年月
                //    this.tableFun();
                //}
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
                this.tableData=[];
                this.loading_show=true;
                //if(this.show_type_first=="导购二维码分析"){
                //    this.chartStaff();
                //    this.tableStaff();
                //}else{
                //    this.chartFun();//调用区分年月
                //    this.tableFun();
                //}
            }
            if(this.show_type_first=="导购二维码分析"){
                this.chartStaff();
                this.tableStaff();
            }else if(this.show_type_first=="店铺二维码分析"){
                this.chartStore();
                this.tableStore()
            }else{
                this.chartFun();//调用区分年月
                this.tableFun();
            }
            this.nav_date=false;
        }
        ,allTable: function () {
            if(this.isActive_0){
                return
            }
            this.tabToggle = 0;
            this.tableData = [];
            this.isActive_0 = true;
            this.isActive_1 = false;
            this.next_page=1;
            if(this.show_type_first=="导购二维码分析" && this.staff_code!=""){
                this.tableStaff();
            }else  if(this.show_type_first=="店铺二维码分析" && this.store_code!=""){
                this.tableStore();
            } else if(this.show_type_first=="渠道二维码分析"){
                this.tableFun();
            }
        }
        ,vipTable: function () {
            if(this.isActive_1){
                return
            }
            this.tabToggle = 1;
            this.tableData = [];
            this.next_page=1;
            this.isActive_0 = false;
            this.isActive_1 = true;
            if(this.show_type_first=="导购二维码分析" && this.staff_code!=""){
                this.tableStaff();
            }else if(this.show_type_first=="店铺二维码分析" && this.store_code!=""){
                this.tableStore();
            } else if(this.show_type_first=="渠道二维码分析"){
                this.tableFun();
            }
        }
        ,paramFun:function () {
            var app_id = this.app_id;
            var qrcode_id = this.qrcode_id;
            var type = this.tabToggle == 0?'scan':'vip';//scan(关注度分析），vip（新增会员分析）
            var param={'id':'','message':{
                'app_id':app_id
                ,'qrcode_id':qrcode_id
                ,'type':type
            }};
            return param;
        },
        getStoreGroup:function(){
            var param={'id':'','message':{
                'corp_code':"C10000"
                ,'searchValue':""
                ,'pageSize':"20"
                ,pageNumber:this.store_group_page
            }};
            this.$http.post('/area/selAreaByCorpCode',{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
               var data=data.data;
                if(data.code=="0"){
                    var message=JSON.parse(data.message);
                    var msg=JSON.parse(message.list);
                    var list =msg.list;
                    this.store_group_data=this.store_group_data.concat(list);
                    if(list.length>0 && this.store_group_page==1){
                        this.store_group_name=list[0].area_name;
                        this.store_group_code=list[0].area_code;
                        this.getStore()
                    }else if(list.length==0 && this.store_group_page==1){
                        this.tableData=[];
                        this.chart_temp=true;
                        this.chart_item=[];//初始化横坐标
                        this.option_series=[];//初始化数据
                        this.chart_count=[];//初始化纵坐标
                        var myChart = echarts.init(document.getElementById('chart'));
                        myChart.setOption(this.option);
                    }
                    if(msg.hasNextPage){
                        this.store_group_has_next=msg.hasNextPage;
                        this.store_group_page++
                    }else{
                        this.store_group_has_next=msg.hasNextPage;
                    }
                }
            })
        },
        getStore:function(){
            var param={'id':'','message':{
                'corp_code':"C10000",
                'area_code':this.store_group_code
                ,'searchValue':""
                ,'brand_code':""
                ,'pageSize':"20"
                ,pageNumber:this.store_page
            }};
            this.$http.post('/shop/selectByAreaCode',{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
               var data=data.data;
                if(data.code=="0"){
                    var message=JSON.parse(data.message);
                    var msg=JSON.parse(message.list);
                    var list =msg.list;
                    this.store_data=this.store_data.concat(list);
                    if(list.length>0 && this.store_page==1){
                        this.store_name=list[0].store_name;
                        this.store_code=list[0].store_code;
                       if(this.show_type_first=="店铺二维码分析"){
                                this.tableData=[];
                                this.chartStore();
                                this.tableStore()
                       }else if(this.show_type_first=="导购二维码分析"){
                           this.getStaff()
                       }

                    }else if(list.length==0 && this.store_page==1){
                        this.tableData=[];
                        this.chart_temp=true;
                        this.chart_item=[];//初始化横坐标
                        this.option_series=[];//初始化数据
                        this.chart_count=[];//初始化纵坐标
                        var myChart = echarts.init(document.getElementById('chart'));
                        myChart.setOption(this.option);
                    }
                    if(msg.hasNextPage){
                        this.store_has_next=msg.hasNextPage;
                        this.store_page++
                    }else{
                        this.store_has_next=msg.hasNextPage;
                    }
                }
            })
        },
        getStaff:function(){
            var param={'id':'','message':{
                'corp_code':"C10000",
                'area_code':this.store_group_code
                ,'searchValue':""
                ,'brand_code':""
                ,'store_code':this.store_code
                ,'pageSize':"20"
                ,pageNumber:this.staff_page
            }};
            this.$http.post('/user/selectUsersByRole',{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
               var data=data.data;
                if(data.code=="0"){
                    var message=JSON.parse(data.message);
                    var msg=JSON.parse(message.list);
                    var list =msg.list;
                    this.nav_type_staff=this.nav_type_staff.concat(list);
                    if(list.length>0 && this.staff_page==1){
                        this.staff_name=list[0].user_name;
                        this.staff_code=list[0].user_code;
                        this.chartStaff();
                        this.tableStaff();
                    }else {
                        this.tableData=[];
                        this.chart_temp=true;
                        this.chart_item=[];//初始化横坐标
                        this.option_series=[];//初始化数据
                        this.chart_count=[];//初始化纵坐标
                        var myChart = echarts.init(document.getElementById('chart'));
                        myChart.setOption(this.option);
                    }
                    if(msg.hasNextPage){
                        this.staff_has_next=msg.hasNextPage;
                        this.staff_page++
                    }else{
                        this.staff_has_next=msg.hasNextPage;
                    }
                }
            })
        }
        ,chartFun:function () {
            var app_id = this.app_id;
            var qrcode_id = this.qrcode_id;
            if(this.aging ==true){
                var date_type = this.date_type;
                var date_value = this.data_value.trim();
            }else{
                var date_type ='';
                var date_value ='';
            }
            this.chart_item=[];//初始化横坐标
            this.option_series=[];//初始化数据
            this.chart_count=[];//初始化纵坐标
            var url='/qrCode/analy/view';
            var param={'id':'','message':{
                'app_id':app_id
                ,'qrcode_id':qrcode_id
                ,'date_type':date_type
                ,'date_value':date_value
            }};
            this.chart_loading=true;
            this.$http.post(url,{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
                var myChart = echarts.init(document.getElementById('chart'));
                var data_all_vip=[];
                var data_vip=[];
                var obj_vip={};
                var obj_all_vip={};
                this.option_legend_data=['关注人数','新增会员数'];
                var code = data.data.code;
                //if(code == 0){
                var data_arr=JSON.parse(data.data.message).analyView;
                if(data_arr != ''){
                //循环遍历取出
                    for(var i=0;i<data_arr.length;i++){
                        this.chart_item.push(data_arr[i].date);//横坐标
                        data_all_vip.push(data_arr[i].all);//纵坐标
                        data_vip.push(data_arr[i].newVip);//纵坐标
                    }
                }else{
                    this.chart_item = [];//横坐标
                    data_all_vip = []; //纵坐标
                    data_vip = [];//纵坐标
                }
                //}else if(code == -1){
                //    var data_arr=[]
                //    this.chart_item = []
                //}
                obj_all_vip={
                    name:'关注人数',
                    type:'line',
                    stack: "总量1",
                    symbol: "circle",
                    data:data_all_vip,
                    lineStyle:{
                        normal:{
                            width:"1"
                        }
                    }
                };
                obj_vip={
                    name:'新增会员数',
                    type:'line',
                    stack: "总量2",
                    symbol: "circle",
                    data:data_vip,
                    lineStyle:{
                        normal:{
                            width:"1"
                        }
                    }
                };
                this.option_series.push(obj_all_vip);
                this.option_series.push(obj_vip);
                myChart.setOption(this.option);
                this.chart_loading=false;
                window.addEventListener("resize", function () {
                    myChart.resize();
                });
            }, function (data) {

            });
        },
        chartStaff:function () {
            var app_id = this.app_id;
            var user_code = this.staff_code;

            if(this.aging ==true){
                var date_type = this.date_type;
                var date_value = this.data_value.trim();
            }else{
                var date_type ='';
                var date_value ='';
            }
            this.chart_item=[];//初始化横坐标
            this.option_series=[];//初始化数据
            this.chart_count=[]; //初始化纵坐标
            var url='/qrCode/analyEmp/view';
            var param={'id':'','message':{
                'app_id':app_id
                ,'user_code':user_code
                ,'date_type':date_type
                ,'date_value':date_value
            }};
            this.chart_loading=true;
            this.$http.post(url,{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
                var myChart = echarts.init(document.getElementById('chart'));
                var data_all_vip=[];
                var data_vip=[];
                var obj_vip={};
                var obj_all_vip={};
                this.option_legend_data=['关注人数','新增会员数'];
                var code = data.data.code;
                //if(code == 0){
                var data_arr=JSON.parse(data.data.message).analyView;
                if(data_arr != ''){
                    this.chart_temp=false
                //循环遍历取出
                    for(var i=0;i<data_arr.length;i++){
                        this.chart_item.push(data_arr[i].date);//横坐标
                        data_all_vip.push(data_arr[i].all);//纵坐标
                        data_vip.push(data_arr[i].newVip);//纵坐标
                    }
                }else{
                    this.chart_item = [];//横坐标
                    data_all_vip = []; //纵坐标
                    data_vip = [];//纵坐标
                    this.chart_temp=true;
                }
                //}else if(code == -1){
                //    var data_arr=[]
                //    this.chart_item = []
                //}
                obj_all_vip={
                    name:'关注人数',
                    type:'line',
                    stack: "总量1",
                    symbol: "circle",
                    data:data_all_vip,
                    lineStyle:{
                        normal:{
                            width:"1"
                        }
                    }
                };
                obj_vip={
                    name:'新增会员数',
                    type:'line',
                    stack: "总量2",
                    symbol: "circle",
                    data:data_vip,
                    lineStyle:{
                        normal:{
                            width:"1"
                        }
                    }
                };
                this.option_series.push(obj_all_vip);
                this.option_series.push(obj_vip);
                myChart.setOption(this.option);
                this.chart_loading=false;
                window.addEventListener("resize", function () {
                    myChart.resize();
                });
            }, function (data) {

            });
        },
        chartStore:function () {
            var app_id = this.app_id;
            var store_code = this.store_code;

            if(this.aging ==true){
                var date_type = this.date_type;
                var date_value = this.data_value.trim();
            }else{
                var date_type ='';
                var date_value ='';
            }
            this.chart_item=[];//初始化横坐标
            this.option_series=[];//初始化数据
            this.chart_count=[]; //初始化纵坐标
            var url='/qrCode/analyStore/view';
            var param={'id':'','message':{
                'app_id':app_id
                ,'store_code':store_code
                ,'date_type':date_type
                ,'date_value':date_value
            }};
            this.chart_loading=true;
            this.$http.post(url,{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
                var myChart = echarts.init(document.getElementById('chart'));
                var data_all_vip=[];
                var data_vip=[]
                var obj_vip={};
                var obj_all_vip={};
                this.option_legend_data=['关注人数','新增会员数'];
                var code = data.data.code;
                //if(code == 0){
                var data_arr=JSON.parse(data.data.message).analyView;
                if(data_arr != ''){
                    this.chart_temp=false;
                //循环遍历取出
                    for(var i=0;i<data_arr.length;i++){
                        this.chart_item.push(data_arr[i].date);//横坐标
                        data_all_vip.push(data_arr[i].all);//纵坐标
                        data_vip.push(data_arr[i].newVip);//纵坐标
                    }
                }else{
                    this.chart_item = [];//横坐标
                    data_all_vip = []; //纵坐标
                    data_vip = [];//纵坐标
                    this.chart_temp=true;
                }
                //}else if(code == -1){
                //    var data_arr=[]
                //    this.chart_item = []
                //}
                obj_all_vip={
                    name:'关注人数',
                    type:'line',
                    stack: "总量1",
                    symbol: "circle",
                    data:data_all_vip,
                    lineStyle:{
                        normal:{
                            width:"1"
                        }
                    }
                };
                obj_vip={
                    name:'新增会员数',
                    type:'line',
                    stack: "总量2",
                    symbol: "circle",
                    data:data_vip,
                    lineStyle:{
                        normal:{
                            width:"1"
                        }
                    }
                };
                this.option_series.push(obj_all_vip);
                this.option_series.push(obj_vip);
                myChart.setOption(this.option);
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
                if(this.show_type_first=="导购二维码分析"){
                    this.tableStaff();
                }else  if(this.show_type_first=="店铺二维码分析"){
                    this.tableStore();
                } else if(this.show_type_first=="渠道二维码分析"){
                    this.tableFun();
                }
            }
        },
        scrollStoreGroup:function (e) {
            var offsetHeight = e.currentTarget.offsetHeight,
                scrollHeight = e.target.scrollHeight,
                scrollTop = e.target.scrollTop;
            if(scrollTop>=scrollHeight-offsetHeight){
                if(this.store_group_has_next){
                    this.getStoreGroup();
                }
            }
        },
        scrollStore:function (e) {
            var offsetHeight = e.currentTarget.offsetHeight,
                scrollHeight = e.target.scrollHeight,
                scrollTop = e.target.scrollTop;
            if(scrollTop>=scrollHeight-offsetHeight){
                if(this.store_has_next){
                    this.getStore();
                }
            }
        },
        scrollStaff:function (e) {
            var offsetHeight = e.currentTarget.offsetHeight,
                scrollHeight = e.target.scrollHeight,
                scrollTop = e.target.scrollTop;
            if(scrollTop>=scrollHeight-offsetHeight){
                if(this.staff_has_next){
                    this.getStaff();
                }
            }
        }
        ,tableFun:function () {
            var param=this.paramFun();
            param.message.pageNumber=this.next_page;
            param.message.pageSize=20;
            param.message.date_type=this.date_type;
            param.message.date_value=this.data_value.trim();
            this.loading_show=true;
            this.$http.post('/qrCode/analy/list',{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
                if(data.data.code == 0){
                    var data_arr=JSON.parse(data.data.message).analyList;
                    this.people_all=data_arr.length;
                    if(data_arr.length==0){
                        this.data_no=true;
                        this.loading_show=false;
                        if(this.next_page>=JSON.parse(data.data.message).pages || JSON.parse(data.data.message).pages == undefined){
                            //禁止请求
                            this.stop_next_page='stop'
                        }else{
                            this.stop_next_page='go'
                        }
                    }else{
                        if(this.next_page>=JSON.parse(data.data.message).pages || JSON.parse(data.data.message).pages == undefined){
                            //禁止请求
                            this.stop_next_page='stop'
                        }else{
                            this.stop_next_page='go'
                        }
                        this.tableData=this.tableData.concat(data_arr);
                        this.loading_show=false;
                        this.data_no=false;
                    }
                }else if(data.data.code == -1){
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
        ,tableStaff:function () {
            var param=this.paramFun();
            param.message.pageNumber=this.next_page;
            param.message.pageSize=20;
            param.message.user_code=this.staff_code;
            param.message.date_type=this.date_type;
            param.message.date_value=this.data_value.trim();
            this.loading_show=true;
            this.$http.post('/qrCode/analyEmp/list',{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
                if(data.data.code == 0){
                    var data_arr=JSON.parse(data.data.message).analyList;
                    this.people_all=data_arr.length;
                    if(data_arr.length==0){
                        this.data_no=true;
                        this.loading_show=false;
                        if(this.next_page>=JSON.parse(data.data.message).pages || JSON.parse(data.data.message).pages == undefined){
                            //禁止请求
                            this.stop_next_page='stop'
                        }else{
                            this.stop_next_page='go'
                        }
                    }else{
                        if(this.next_page>=JSON.parse(data.data.message).pages || JSON.parse(data.data.message).pages == undefined){
                            //禁止请求
                            this.stop_next_page='stop'
                        }else{
                            this.stop_next_page='go'
                        }
                        this.tableData=this.tableData.concat(data_arr);
                        this.loading_show=false;
                        this.data_no=false;
                    }
                }else if(data.data.code == -1){
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
        },
        tableStore:function () {
            var param=this.paramFun();
            param.message.pageNumber=this.next_page;
            param.message.pageSize=20;
            param.message.store_code=this.store_code;
            param.message.date_type=this.date_type;
            param.message.date_value=this.data_value.trim();
            this.loading_show=true;
            this.$http.post('/qrCode/analyStore/list',{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
                if(data.data.code == 0){
                    var data_arr=JSON.parse(data.data.message).analyList;
                    this.people_all=data_arr.length;
                    if(data_arr.length==0){
                        this.data_no=true;
                        this.tableData=[];
                        this.loading_show=false;
                        if(this.next_page>=JSON.parse(data.data.message).pages || JSON.parse(data.data.message).pages == undefined){
                            //禁止请求
                            this.stop_next_page='stop'
                        }else{
                            this.stop_next_page='go'
                        }
                    }else{
                        if(this.next_page>=JSON.parse(data.data.message).pages || JSON.parse(data.data.message).pages == undefined){
                            //禁止请求
                            this.stop_next_page='stop'
                        }else{
                            this.stop_next_page='go'
                        }
                        this.tableData=this.tableData.concat(data_arr);
                        this.loading_show=false;
                        this.data_no=false;
                    }
                }else if(data.data.code == -1){
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
                this.post_start_param={'id':'','message':{'corp_code':this.corp_code,searchValue:''}};
                this.component_url="/shop/brand";
                this.message_obj.show=false;
            }else if(role=='所属区域'){
                this.post_start_param={'id':'','message':{'corp_code':this.corp_code,searchValue:'','pageSize':20,'pageNumber':1}};
                this.component_url="/area/selAreaByCorpCode";
                this.message_obj.show=false;
            }else if(role=='所属店铺'){
                brand_code=this.brand_input_arr.map(function (val) {
                    return val.code;
                });
                area_code=this.area_input_arr.map(function (val) {
                    return val.code;
                });
                this.post_start_param={'id':'','message':{'corp_code':this.corp_code,searchValue:'','pageSize':20,'pageNumber':1,'area_code':area_code.join(','),'brand_code':brand_code.join(',')}};
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
                this.post_start_param={'id':'','message':{'corp_code':this.corp_code,searchValue:'','pageSize':20,'pageNumber':1,'area_code':area_code.join(','),'brand_code':brand_code.join(','),'store_code':store_code.join(',')}};
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
        ,param_post:function () {
            return JSON.stringify(this.param)
        }
    }
    ,components: {
        'filtrateComponent':fun.filtrateComponent
    }
    ,beforeMount:function () {
        //公众号
        var param={'id':'','message':{'corp_code':this.corp_code}};
        this.$http.post('/corp/selectWx',{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
            var msg = JSON.parse(data.data.message);
            var list = msg.list;
            if(list.length == 0){
                this.chart_temp = true;
                this.chart_loading=false;
                this.data_no = true;
                this.show_public = '暂无公众号';
                this.app_id = '';
                this.qrcode_id = '';
                this.chart_item=[];//初始化横坐标
                this.option_series=[];//初始化数据
                this.chart_count=[];//初始化纵坐标
                this.aging = false;
                this.chart_temp = true;
                this.chart_loading=false;
                this.tableData=[];
                this.nav_type_dom=[];
                this.chartFun();
                this.tableFun();
            }else{
                this.nav_type_dom=[{'typename':'材料类','type':'material'},{'typename':'印刷类','type':'print'},{'typename':'礼品类','type':'gift'}]
                for(i=0;i<list.length;i++){
                    this.nav_dom.push({'app_name':list[i].app_name,'app_id':list[i].app_id});
                }
                this.show_public = this.nav_dom[0].app_name;//公众号显示
                this.app_id = this.nav_dom[0].app_id; //app_id
                //二维码名称
                var param={'id':'','message':{'app_id':this.app_id,type:this.type}};
                this.chart_loading=true;
                this.$http.post('/qrCode/qrcodeList',{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
                    if(data.data.code == 0){
                        var msg = JSON.parse(data.data.message);
                        var list = JSON.parse(msg.list);
                        if(list != ''){
                            for(i=0;i<list.length;i++){
                                this.nav_name_dom.push({'qrcode_name':list[i].qrcode_name,'qrcode_id':list[i].id,'aging':list[i].aging});
                            }
                            this.show_name = this.nav_name_dom[0].qrcode_name;
                            this.qrcode_id = this.nav_name_dom[0].qrcode_id;
                            this.aging=(this.nav_name_dom[0].aging == '永久有效')?true:false;
                            this.chartFun();
                            this.tableFun();
                        }else{
                            this.show_name = '暂无二维码';
                            this.qrcode_id = ''
                            this.chart_item=[];//初始化横坐标
                            this.option_series=[];//初始化数据
                            this.chart_count=[]//初始化纵坐标
                            this.aging = false;
                            this.chart_temp = true;
                            this.chart_loading=false;
                            this.tableData=[];
                            this.chartFun();
                            this.tableFun();
                        }
                    }else if(data.data.code == -1){
                        this.show_name = '暂无二维码';
                        this.qrcode_id = ''
                        this.chart_item=[0];//初始化横坐标
                        this.option_series=[0];//初始化数据
                        this.chart_count=[0]//初始化纵坐标
                        this.aging = false;
                        this.chart_temp = true;
                        this.chart_loading=false;
                        this.tableData=[];
                        this.chartFun();
                        this.tableFun();
                    }
                })
            }
        });
        fun.timeFiltrate(this);
        fun.yearFun(this);
    },
    mounted:function(){
        var start = {
            elem: '#select_date_start',
            format: 'YYYY-MM-DD',
            min: '1900-01-01', //设定最小日期为当前日期
            max: '2099-12-31', //最大日期
            istime: true,
            istoday: false,
            choose: function(datas){
                end.min = datas; //开始日选好后，重置结束日的最小日期
                end.start = datas; //将结束日的初始值设定为开始日
            }
        };
        var end = {
            elem: '#select_date_end',
            format: 'YYYY-MM-DD',
            min:'1900-01-01',
            max: '2099-12-31',
            istime: true,
            istoday: false,
            choose: function(datas){
                start.max = datas; //结束日选好后，重置开始日的最大日期
            }
        };
        laydate(start);
        laydate(end);
    }
});
