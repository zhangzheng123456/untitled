/**
 * Created by Bizvane on 2017/2/9.
 */
//注册全局组件
var data_obj={};
data_obj.area_keep=[];//保存状态
data_obj.brand_keep=[];
data_obj.store_keep=[];
data_obj.guide_keep=[];
Vue.component('nav-component', {
    template:'<ul class="down_ul" ><li @click="increment($event)"v-for="obj in nav_dom":title="obj.app_function" :class="{header_bg:obj.is_active }":data-app="obj.app_function">{{obj.app_function}}</li></ul>'
    ,data: function () {
        return {
            nav_dom:staff.show_nav_dom_data
        }
    }
    ,methods: {
        increment: function (e) {
            this.nav_dom.forEach(function(val){
                return val.is_active==1&&(val.is_active=0);
            })
            this.nav_dom.forEach(function(val){
                return val.app_function==e.target.dataset.app&&(val.is_active=1);
            })
            this.$emit('increment',e.target)
        }
    }
});
// 定义
var MyComponent = Vue.extend({
    template:'<ul class="down_ul" ><li  @click="increment($event)" v-for="obj in nav_dom":title="obj.app_action_name":class="{header_bg:obj.is_active }" :data-app="obj.app_action_code">{{obj.app_action_name}}</li></ul>'
    ,data: function () {
        return {
            nav_dom:staff.show_nav_dom_data2
        }
    }
    ,methods: {
        increment: function (e) {
            this.nav_dom.forEach(function(val){
                return val.is_active==1&&(val.is_active=0);
            })
            this.nav_dom.forEach(function(val){
                return val.app_action_code==e.target.dataset.app&&(val.is_active=1);
            })
            this.$emit('increment2',e.target)
        }
    }
});
var filtrateComponent= Vue.extend({
    template:'<div id="screen_staff"><div class="screen_title"><h4>{{messageTitle}}</h4>'//接口变量1
    +'<div class="xuanzhong"v-if="messageObj.show">'//显示隐藏控制 节点变量2
    +'<div class="xuanzhong_p" v-for="obj in messageObj.data"><label>{{obj.msg1}}</label><input type="text" :value="obj.msg2"><span :data-role="obj.msg1" class="icon_r icon-ishop_8-03"@click="choose($event)"></span></div></div>'//数据变量3
    +'<div class="screen_close"><span class="icon-ishop_6-12"@click="increment($event)"></span></div><div class="xian"></div>'
    +'</div>'//title结尾
    +'<div class="input_s"><div class="input_search">'
    +'<input type="text" placeholder="搜索"@keyup.enter="submitFun($event)">'//绑定变量4
    +'<span class="icon-ishop_3-05" @click="submitFun($event)"></span></div>'//触发动作变量5
    +'<div class="s_center"></div>'
    +'<div class="s_pitch">已添加&nbsp;<span>{{count}}</span>&nbsp;个</div>'//右侧数据变量6
    +'</div>'//搜索结尾
    +'<div class="screen_content"><div class="screen_content_l"@scroll="scrollFun($event)"><ul><li v-for="obj in messageData" @click="liFun($event)"><div class="checkbox1"><input type="checkbox":checked="obj.key"  :data-code="obj.code" name="test" class="check" :id="obj.code"><label :for="obj.code"></label></div><span class="p16">{{obj.name}}</span></li></ul></div>'//数据循环变量7
    +'<div class="screen_content_z"><div class="shift shift_right" title="右移"@click="moveFunR($event)">&gt;</div>' +//动作变量
    '<div class="shift shift_right_all" title="右移全部"@click="moveFunRAll($event)">&gt;&gt;</div>' +//动作函数变量8
    '<div class="shift shift_left" title="左移"@click="moveFunL($event)">&lt;</div>' +//动作函数变量9
    '<div class="shift shift_left_all" title="左移全部"@click="moveFunLAll($event)">&lt;&lt;</div></div>'+//动作函数变量10
    '<div class="screen_content_r"><ul><li v-for="obj in node_r" @click="liFun($event)"><div class="checkbox1"><input type="checkbox"  :data-code="obj.code" name="test" class="check" :id="obj.name+0"><label :for="obj.name+0"></label></div><span class="p16">{{obj.name}}</span></li></ul></div>'+//数据添加变量11
    '</div>'
    +'<div class="screen_que"@click="increment($event)">确定</div></div>'//确定按钮函数变量12
    ,data: function () {
        return {
            searchValue:''
            ,messageData:''
            ,param:{'id':'','message':{'corp_code':'C10000',searchValue:this.searchValue}}
            ,node_r:[]
            ,count:0
            ,data_arr:[]
            ,scroll_top:''
            ,search:'search'
            ,'keep':''
        }
    }
    ,props:['messageTitle','messageObj','postUrl','startParam']//传参
    ,computed:{}
    ,methods: {
        increment: function (e) {
            var html=this.postUrl;
            var obj=this.node_r;
            //注释内容为关闭的操作效果
            if(e.target.innerHTML){
                switch (html){
                    case '/shop/brand':data_obj.brand_keep=obj.slice(0);break;
                    case '/area/selAreaByCorpCode':data_obj.area_keep=obj.slice(0);break;
                    case '/shop/selectByAreaCode':data_obj.store_keep=obj.slice(0);break;
                    case '/user/selectUsersByRole':data_obj.guide_keep=obj.slice(0);break;
                }
            }
            this.$emit('increment3',html,obj,e.target.innerHTML);
        }
        ,liFun:function (e) {
            console.log($(e.currentTarget).find('input')[0].checked);
            if($(e.currentTarget).find('input')[0].checked==false){
                $(e.currentTarget).find('input')[0].checked=true;
            }else if($(e.currentTarget).find('input')[0].checked==true){
                $(e.currentTarget).find('input')[0].checked=false;
            }
        }
        ,moveFunR:function (e) {
            var left_code=[];
            var right_code=[];
            //不清空右侧的数据，循环取出右侧的code号
            for(var i=0;i<this.node_r.length;i++){
                right_code.push(this.node_r[i].code);
            }
            //取出右侧的
            //搜索不走
            $('.screen_content_l').find('.check:checked ').each(function (index,val) {
                left_code.push(val.dataset.code);
            });
            //从左侧取出code
            //去除左侧已有的code
            for(var i=left_code.length-1;i>=0;i--){
                for(var r=0;r<right_code.length;r++){
                    if(left_code[i]==right_code[r]){
                        left_code.splice(i,1)
                    }
                }
            }
            for(var r=0;r<this.messageData.length;r++){
                for(var i=0;i<left_code.length;i++){
                    if(this.messageData[r].code==left_code[i]){
                        this.messageData[r].key=true
                        this.node_r.push(this.messageData[r]);
                    }
                }
            }
            this.count=this.node_r.length;
        }
        ,moveFunRAll:function () {
            var left_code=[];
            var right_code=[];
            for(var i=0;i<this.node_r.length;i++){
                right_code.push(this.node_r[i].code);
            }
            $('.screen_content_l').find('.check').each(function (index,val) {
                left_code.push(val.dataset.code);
            });
            for(var i=left_code.length-1;i>=0;i--){
                for(var r=0;r<right_code.length;r++){
                    if(left_code[i]==right_code[r]){
                        left_code.splice(i,1)
                    }
                }
            }
            for(var r=0;r<this.messageData.length;r++){
                for(var i=0;i<left_code.length;i++){
                    if(this.messageData[r].code==left_code[i]){
                        this.messageData[r].key=true
                        this.node_r.push(this.messageData[r]);
                    }
                }
            }
            this.count=this.node_r.length;
            $('.screen_content_l').find('.check ').each(function (index,val) {
                $(val).attr('checked','true');
            });
        }
        ,moveFunL:function () {
            var right_code=[];
            $('.screen_content_r').find('.check:checked ').each(function (index,val) {
                right_code.push(val.dataset.code);
            });
            for(var r=0;r<right_code.length;r++){
                for(var i=this.node_r.length-1;i>=0;i--){
                    if(right_code[r]==this.node_r[i].code){
                        this.node_r.splice(i,1)
                    }
                }
            }
            for(var i=0;i<this.messageData.length;i++){
                for(var r=0;r<right_code.length;r++){
                    if(this.messageData[i].code==right_code[r]){
                        this.messageData[i].key=false;
                    }
                }
            }
            $('.screen_content_r').find('.check').removeAttr('checked');
            this.count=this.node_r.length;
        }
        ,moveFunLAll:function () {
            this.node_r=[];
            this.count=this.node_r.length;
            for(var r=0;r<this.messageData.length;r++){
                this.messageData[r].key=false
            }
        }
        ,dataFun:function (data) {
            //主要是为了点击回复数据
            if(this.keep){
                switch (this.postUrl){
                    case '/shop/brand':this.node_r=data_obj.brand_keep.slice(0);this.count=data_obj.brand_keep.length;break;
                    case '/area/selAreaByCorpCode':this.node_r=data_obj.area_keep.slice(0);this.count=data_obj.area_keep.length;break;
                    case '/shop/selectByAreaCode':this.node_r=data_obj.store_keep.slice(0);this.count=data_obj.store_keep.length;break;
                    case '/user/selectUsersByRole':this.node_r=data_obj.guide_keep.slice(0);this.count=data_obj.guide_keep.length;break;
                }
                this.keep=''
            }
            //右侧节点code
            var right_code=this.node_r.map(function(val){
                return val.code
            });
            if(this.postUrl=="/shop/brand"){
                for(var i=0;i<data.brands.length;i++){
                    var obj={};
                    for(var r=0;r<right_code.length;r++){
                        if(right_code[r]==data.brands[i].brand_code){
                            obj.key=true
                        }
                    }
                    if(!obj.key){obj.key=false}
                    obj.code=data.brands[i].brand_code;
                    obj.name=data.brands[i].brand_name;

                    this.data_arr.push(obj);
                }
            }else if( this.postUrl=='/area/selAreaByCorpCode'){
                var area_arr=JSON.parse(data.list).list;
                this.scroll_top=JSON.parse(data.list).hasNextPage;
                for(var i=0;i<area_arr.length;i++){
                    var obj={};
                    for(var r=0;r<right_code.length;r++){
                        if(right_code[r]==area_arr[i].area_code){
                            obj.key=true
                        }
                    }
                    if(!obj.key){obj.key=false}
                    obj.code=area_arr[i].area_code;
                    obj.name=area_arr[i].area_name;
                    this.data_arr.push(obj);
                }
            }else if(this.postUrl=="/shop/selectByAreaCode"){
                var store_arr=JSON.parse(data.list).list;
                this.scroll_top=JSON.parse(data.list).hasNextPage;
                for(var i=0;i<store_arr.length;i++){
                    var obj={};
                    for(var r=0;r<right_code.length;r++){
                        if(right_code[r]==store_arr[i].store_code){
                            obj.key=true
                        }
                    }
                    if(!obj.key){obj.key=false}
                    obj.code=store_arr[i].store_code;
                    obj.name=store_arr[i].store_name;
                    this.data_arr.push(obj);
                }
            }else if(this.postUrl=="/user/selectUsersByRole"){
                var guide_arr=JSON.parse(data.list).list;
                this.scroll_top=JSON.parse(data.list).hasNextPage;
                for(var i=0;i<guide_arr.length;i++){
                    var obj={};
                    if(right_code.join('').search(guide_arr[i].user_code)==-1){
                        obj.key=false
                    }else{
                        obj.key=true
                    }
                    obj.code=guide_arr[i].user_code;
                    obj.name=guide_arr[i].user_name+'('+guide_arr[i].user_code+")";
                    this.data_arr.push(obj);
                }
            }
            this.messageData=this.data_arr;
            $('.screen_content_l').find('.check').removeAttr('checked');
        }
        ,choose:function (e) {
            this.keep='keep';
            this.data_arr=[];
            var str='';
            switch (e.target.dataset.role){
                case '已选品牌':str='所属品牌';break;
                case '已选区域':str='所属区域';break;
                case '已选店铺':str='所属店铺';break;
            }
            this.$emit('increment',str);
        }
        ,postFun:function () {//接口数据
            this.$http.post(this.postUrl,{param:JSON.stringify(this.startParam)},{emulateJSON:true}).then(function (data) {
                //数据格式统一
                this.dataFun(JSON.parse(data.data.message));
            }, function (data) {

            });
        }
        ,scrollFun:function (e) {
            var offsetHeight = e.currentTarget.offsetHeight,
                scrollHeight = e.target.scrollHeight,
                scrollTop = e.target.scrollTop;
            if(scrollTop>=scrollHeight-offsetHeight){
                if(this.postUrl=="/shop/brand"){
                    return;
                }else {
                    if(!this.scroll_top)return
                    this.startParam.message.pageNumber+=1;
                    whir.loading.add("",0.5);//加载等待框
                    $('#mask').css('z-index',10002);
                    $('#loading').css('z-index',10002);
                    this.$http.post(this.postUrl,{param:JSON.stringify(this.startParam)},{emulateJSON:true}).then(function (data) {
                        //数据格式统一
                        this.dataFun(JSON.parse(data.data.message));
                        whir.loading.remove();//移除加载框
                    }, function (data) {

                    });
                }
            }
        }
        ,submitFun:function (e) {
            //控制滚动条滚
            $(e.currentTarget).blur();
            $('.screen_content_l').scrollTop(0);
            whir.loading.add("",0.5);//加载等待框
            $('#mask').css('z-index',10002);
            $('#loading').css('z-index',10002);
            this.data_arr=[];
            this.search='';
            this.startParam.message.pageNumber=1;
            this.startParam.message.searchValue=$(e.target).parent().find('input').val();
            this.$http.post(this.postUrl,{param:JSON.stringify(this.startParam)},{emulateJSON:true}).then(function (data) {
                //数据格式统一
                this.dataFun(JSON.parse(data.data.message));
                whir.loading.remove();//移除加载框
            }, function (data) {

            });
        }
    }
    ,mounted:function(){
        this.keep='keep';//组件初始化，回复数据
        whir.loading.add("",0.5);//加载等待框
        $('#mask').css('z-index',10002);
        $('#loading').css('z-index',10002);
        this.$http.post(this.postUrl,{param:JSON.stringify(this.startParam)},{emulateJSON:true}).then(function (data) {
            //数据格式统一
            this.dataFun(JSON.parse(data.data.message));
            whir.loading.remove();//移除加载框
        }, function (data) {

        });
    }
    ,watch: {
        postUrl: function () {
        }
        ,startParam:function () {
            this.postFun();
        }
    }
})
var staff=new Vue({
    el:'#con_table'
    ,data:{
         nav_dom_data:[{'app_function':'会员基本信息','app_action_name':'会员搜索'}]
        ,nav_dom_data2:[{'app_function':'会员基本信息','app_action_name':'会员搜索'}]
        ,nav_1:false
        ,nav_2:false
        ,nav_3:false
        ,nav_4:false
        ,filtrate:false
        ,function_nav:''
        ,week:true
        ,month:false
        ,year:false
        ,time_show:true
        ,chart_count:[]//纵坐标
        ,chart_item:[]//横坐标
        ,option_color:['#4176c0','#41c7db','#9999cc','#ba6e90','#6e578e','#ceabd5','#9585a1','#eaa2c2','#6e73ad','#d1cbd5','#6db0d4','#9ad5e2','#439cca','#2e5994','#13acc3','#434960']//线条颜色
        ,option_legend_data:[]//标题
        ,option_series: []//数据参数
        ,week_obj:[]//日期中的周对象
        ,month_arr:[]//日期中的月数组
        ,year_arr:[]//日期中的年对象
        ,param:{'id':'','message':{'corp_code':'C10000'}}
        ,time:''//筛选时间段
        ,time_start:''//周的开始时间
        ,time_end:''//周的结束时间
        ,year_down:''//下拉中的年份
        ,app_action_code:''//传动作
        ,data_value:'2017-02-03'//传时间
        ,data_type:'M'//传日期类型
        ,brand_input:''
        ,area_input:''
        ,store_input:''
        ,guide_input:''
        ,brand_input_arr:[]//保存子组件已选的obj
        ,area_input_arr:[]
        ,store_input_arr:[]
        ,guide_input_arr:[]
        ,message_title:''//筛选标题
        ,message_obj:{}//筛选已选中数据
        ,component_url:''//组件的请求参数
        ,post_start_param:''
        ,post_table_param:''
        ,tableData:[]
        ,next_page:1
        ,stop_next_page:'go'//是否继续请求
        ,export_show:false
        ,loading_show:false
        ,data_no:false
        ,chart_loading:false
        ,isActive_w:true//周的下划线
        ,isActive_m:false//月下划线
        ,isActive_y:false//年下划线
        ,now_week:0//匹配当前周
        ,click_color_w:true//默认周背景色
        ,click_color_m:true//默认月背景色
        ,click_color_y:true//默认年背景色
        ,click_color_w_2:false//默认周背景色
        ,click_color_m_2:false//默认月背景色
        ,click_color_y_2:false//默认年背景色
    }
    ,methods:{
        navDomShow:function (e) {
            if(e.target.className.search('nav_1')!=-1){//功能
                this.nav_1=!this.nav_1;
                this.nav_2=false;
                this.nav_3=false;
                this.nav_4=false;
            }
            if(e.target.className.search('nav_2')!=-1){//会员生日
                this.nav_2=!this.nav_2;
                this.nav_1=false;
                this.nav_3=false;
                this.nav_4=false;
            }
            if(e.target.className.search('nav_3')!=-1){//选择范围
                this.nav_3=!this.nav_3;
                this.nav_1=false;
                this.nav_2=false;
                this.nav_4=false;
                if(this.nav_3){
                    whir.loading.add("",0.5);//加载等待框
                    $('#loading').remove();
                }else{
                    whir.loading.remove('mask');//移除加载框
                }
            }
            if(e.target.className.search('nav_4')!=-1){//版本
                this.nav_4=!this.nav_4;
                this.nav_1=false;
                this.nav_2=false;
                this.nav_3=false;
            }
        }
        ,rangeFun:function(){
            this.nav_3=!this.nav_3;
            if(this.nav_3){
                whir.loading.add("",0.5);//加载等待框
                $('#loading').remove();
            }else{
                whir.loading.remove('mask');//移除加载框
            }
        }
        ,actionFun:function () {
            // whir.loading.add("",0.5);//加载等待框
            $('#nav .nav_2>span').html($(arguments[0])[0].innerHTML);
            $('#nav .nav_2>span').attr('data-app',$(arguments[0]).attr('data-app'));
            $('#nav .nav_2>span').attr('title',$(arguments[0])[0].innerHTML);
            this.nav_2=false;
            this.next_page=1;
            this.app_action_code=$(arguments[0]).attr('data-app');
            this.app_action_code=='check_all'&&( this.app_action_code=(this.show_nav_dom_data2.slice(1).map(function(arr){
                return arr.app_action_code;
            })).join(','));
            this.chartFun();
            this.loading_show=true;
            this.tableData=[];
            this.tableFun();
        }
        ,chartFun:function (app_action_code) {
            var brand_code=[];
            var area_code=[];
            var store_code=[];
            var user_code=[];
            this.chart_item=[];//初始化横坐标
            this.option_legend_data=[];//初始化纵坐标
            this.option_series=[];//初始化数据
            var url='/appManager/getObtainEvents';
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
                'corp_code':'C10000',
                'app_function':$('#nav .nav_1>span').html()
                ,'brand_code':brand_code.join(',')
                ,'area_code':area_code.join(',')
                ,'store_code':store_code.join(',')
                ,'user_code':user_code.join(',')
                ,'date_type':this.data_type
                ,'date_value':this.data_value.trim()
                ,'app_action_code':this.app_action_code
            }};
            var myChart = echarts.init(document.getElementById('chart'));
            this.chart_loading=true;
            this.$http.post(url,{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
                this.chart_loading=false;
                if(JSON.parse(data.data.message).message.length<=0){
                    $('#text').show();
                    return;
                };
                if(JSON.parse(data.data.message).message.length>=0){
                    $('#text').hide();
                };
                var data_arr=JSON.parse(JSON.parse(data.data.message).message);
                //循环遍历取出
                var data_i=0;
                try{
                    for(var r=0;r<data_arr[data_i].list.length;r++){
                        this.chart_item.push(data_arr[0].list[r].date)//横坐标
                    }
                }catch(err){
                    data_i++
                    for(var r=0;r<data_arr[data_i].list.length;r++){
                        this.chart_item.push(data_arr[0].list[r].date)//横坐标
                    }
                }
                for(var i=0;i<data_arr.length;i++){
                    this.option_legend_data.push(data_arr[i].app_action_name);//option_legend_data:[]//标题
                    var obj={};   //option_series
                    var data_y=[];
                    var data_list=data_arr[i].list;
                    //遍历list
                    for(var j=0;j<data_list.length;j++){
                         data_y.push(data_list[j].count);//纵坐标
                    }
                    obj={
                        name:data_arr[i].app_action_name,
                        type:'line',
                        stack: "总量"+i,
                        symbol: "circle",
                        data:data_y,
                        lineStyle:{
                            normal:{
                                width:"1"
                            }
                        }
                    }
                    this.option_series.push(obj)
                }
                myChart.setOption(this.option);
                this.chart_loading=false;
                window.addEventListener("resize", function () {
                    myChart.resize();
                });
            }, function (data) {

            });
        }
        ,functionFun:function (e) {//功能函数
             $('#nav .nav_2>span').html('全部');
             $('#nav .nav_1>span').html($(arguments[0]).attr('data-app'));
              this.nav_1=false;
              var url='/appManager/getActionList';
              var param={'id':'','message':{'corp_code':'C10000','app_function':$(arguments[0]).attr('data-app')}};
              this.getFun(url,JSON.stringify(param));
        }
        ,getFun:function (url,post) {
            this.$http.post(url,{param:post},{emulateJSON:true}).then(function (data) {
                //改变节点数据
                var data_arr=JSON.parse(data.data.message);
                var dom=[];
                var app_action_code=[];//获取图表的传参
                //遍历数据
                for(var i=0;i<data_arr.length;i++){
                    var obj={};
                    obj.app_action_code=data_arr[i].app_action_code;
                    obj.app_function=data_arr[i].app_function;
                    obj.app_action_name=data_arr[i].app_action_name;
                    obj.is_active=0;
                    dom.push(obj);
                    app_action_code.push(data_arr[i].app_action_code);
                }
                this.nav_dom_data2=dom;
                this.nav_dom_data2[0].is_active=1;
                //生成图表
                this.app_action_code=app_action_code.slice(1).join(',');
                this.chartFun();
                this.tableData=[];
                this.loading_show=true;
                this.tableFun();
            });
        }
        ,timeFun:function (e) {
            //$(e.target).addClass('border').siblings('div').removeClass('border');
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
                this.dateFun(this.year_down);
            }
            if(e.target.className.search('week_arrow_right')!=-1){//递增一年的周
                this.year_down+=1;
                this.dateFun(this.year_down);
            }
            if(e.target.className.search('month_arrow_left')!=-1){//递减一年的yue
                this.year_down-=1;
                this.dateFun(this.year_down);
            }
            if(e.target.className.search('month_arrow_right')!=-1){//递增一年的月
                this.year_down+=1;
                this.dateFun(this.year_down);
            }
        }
        ,dateFun:function (year) {
            this.year_down=year;
            this.week_obj=[];
            this.month_arr=[];
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
                // console.log('日：'+new Date(time_start).getDate()+'月：'+(new Date(time_start).getMonth()+1)+'年'+new Date(time_start).getFullYear());
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
                var obj={}
                obj.title=week[i];
                obj.title_date=week_date[i];
                //显示当前周期的class
                if((i==(this.now_week-1))&&(year==new Date().getFullYear())){
                    obj.title_class=true
                }else{
                    obj.title_class=false
                }
                this.week_obj.push(obj);
            }
            //月数据处理
            for(var i=1;i<13;i++){
                if((i==(new Date().getMonth()+1))&&(year==new Date().getFullYear())){
                    this.month_arr.push({'val':year+'年'+i+'月','key':true});
                }else{
                    this.month_arr.push({'val':year+'年'+i+'月','key':false});
                }
            }
        }
        ,yearFun:function () {
            var year=new Date().getFullYear();
            //年数据处理
            for(var i=0;i<10;i++){
                if((i==0)&&(year==new Date().getFullYear())){
                    this.year_arr.push({'val':(year-i)+'年','key':true});
                }else{
                    this.year_arr.push({'val':(year-i)+'年','key':false});
                }
            }
        }
        ,timeFiltrate:function () {
            var  time=new Date();
            var  year=new Date().getFullYear();
            var time_start='';
            var time_end='';
            switch (new Date(year+'/1/1').getDay()){
                case 0:time_start=year+'/1/2';break//星期天
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
            while (new Date(time_start)<=new Date(time_end)){
                i++;
                if(new Date(time_start)<=new Date(time)&&(new Date(time)<=new Date(new Date(new Date(new Date(new Date(time_start).setDate(new Date(time_start).getDate()+6)).setHours(23))).setMinutes(59)))){
                    this.time=year+'年'+i+'周';
                    this.now_week=i;
                    var s_y=(new Date(time_start).getMonth()+1)<=9?'0'+(new Date(time_start).getMonth()+1):(new Date(time_start).getMonth()+1);
                    var s_x=new Date(time_start).getDate()<=9?'0'+new Date(time_start).getDate():new Date(time_start).getDate();
                    this.time_start=new Date(time_start).getFullYear()+'-'+s_y+'-'+s_x;
                    this.data_type='W';
                    this.data_value=new Date(time_start).getFullYear()+'-'+s_y+'-'+s_x;
                    var e_time=new Date(new Date(time_start).setDate(new Date(time_start).getDate()+6));
                    var e_y=(new Date(e_time).getMonth()+1)<=9?'0'+(new Date(e_time).getMonth()+1):(new Date(e_time).getMonth()+1);
                    var e_x=new Date(e_time).getDate()<=9?'0'+new Date(e_time).getDate():new Date(e_time).getDate();
                    this.time_end=new Date(e_time).getFullYear()+'-'+e_y+'-'+e_x;
                    //退出之前生成周期
                    this.dateFun(new Date().getFullYear());
                    return;
                }
                time_start=new Date(new Date(time_start).setDate(new Date(time_start).getDate()+7));
            }
        }
        ,timeChange:function (e) {
            var time_arr=[];
            this.time_show=false;
            this.next_page=1;
            if(e.currentTarget.id=='year_id'){
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
                var h=$(e.target).html();
                this.time=$(e.target).html();
                this.time_show=false;
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
            this.nav_4=false;
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
        ,confirm:function () {
            this.nav_3=!this.nav_3;
            this.next_page=1;
            // whir.loading.add("",0.5);//加载等待框
            if(this.nav_3){
                whir.loading.add("",0.5);//加载等待框
                $('#loading').remove();
            }else{
                whir.loading.remove('mask');//移除加载框
            }
            this.chartFun();
            this.tableData=[];
            this.loading_show=true;
            this.tableFun();
        }
        ,filtrateFunMore:function (str) {
            this.filtrateFun(str);
        }
        ,scrollFun:function (e) {
            var offsetHeight = e.currentTarget.offsetHeight,
                scrollHeight = e.target.scrollHeight,
                scrollTop = e.target.scrollTop;
            if(scrollTop>=scrollHeight-offsetHeight){
                this.loading_show=true;
                if(this.stop_next_page='go'){
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
            this.$http.post('/appManager/getObtainEventTable',{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
                if(data.data.code==0){
                if(JSON.parse(data.data.message).message.length==0){
                    this.data_no=true;
                    this.loading_show=false;
                }else{
                    if(this.next_page>=JSON.parse(data.data.message).pages){
                        //禁止请求
                        this.stop_next_page='stop'
                    }else{
                        this.stop_next_page='go'
                    }
                    this.tableData=this.tableData.concat(JSON.parse(JSON.parse(data.data.message).message));
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
                ,'date_type':this.data_type
                ,'date_value':this.data_value.trim()
                ,'app_action_code':this.app_action_code
            }};
            return param;
        }
        ,exportFun:function (e) {
            //调接口
            //改名称
            if($(e.target).html()=='确认') {
                var param = this.paramFun();
                whir.loading.add("", 0.5);//加载等待框
                $('#mask').css('z-index',10002);
                $('#loading').css('z-index',10002);
                this.$http.post('/appManager/exportExecl', {param: JSON.stringify(param)}, {emulateJSON: true}).then(function (data) {
                    $(e.target).html('<a href="/' + JSON.parse(JSON.parse(data.data.message).path) + '">下载文件</a>');
                    $('#export_consumeExport p').html('是否下载列表');
                    whir.loading.remove();//移除加载框
                });
            }else{
                this.export_show=!this.export_show;
                whir.loading.remove();//移除加载框
            }
        }
        ,exportLoading:function(){
            this.export_show=!this.export_show;
            if(this.export_show){
                whir.loading.add("",0.5);//加载等待框
                $('#loading').remove();
                this.nav_4=false;
            }else{
                whir.loading.remove('mask');//移除加载框
            }
        }
    },
    components: {
        'MyComponent': MyComponent
        ,'filtrateComponent':filtrateComponent
    },
    computed:{
        show_nav_dom_data:function () {
            return this.nav_dom_data;
        },
        show_nav_dom_data2:function () {
            return this.nav_dom_data2;
        },
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
    ,mounted: function() {

    }
    ,beforeCreate:function () {
        // whir.loading.add("",0.5);//加载等待框
    }
    ,created:function () {
        this.$http.post('/appManager/getFunctionList',{param:this.param_post},{emulateJSON:true}).then(function (data) {
            //改变节点数据
            var data_arr=JSON.parse(data.data.message);
            var dom=[];
            //遍历数据
            $('#nav .nav_1>span').html(data_arr[0].app_function);
            $('#nav .nav_1>span').attr('data-app',data_arr[0].app_function);
            for(var i=0;i<data_arr.length;i++){
                var obj={};
                obj.app_function=data_arr[i].app_function;
                obj.app_action_name=data_arr[i].app_action_name;
                obj.is_active=0;
                dom.push(obj);
            }
            this.nav_dom_data=dom;
            this.nav_dom_data[0].is_active=1;//默认首先项
            var param={'id':'','message':{'corp_code':'C10000','app_function':data_arr[0].app_function}};
            $('#nav .nav_2>span').html('全部');
            this.getFun('/appManager/getActionList',JSON.stringify(param));
        }, function (data) {

        });
        this.timeFiltrate();
        this.yearFun();
    }
});
$(function () {
    // whir.loading.add("",0.5);//加载等待框
});

