var fun={
    timeFiltrate:function (obj) {//接受一个对象
        var  time=new Date().toLocaleDateString();
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
            if(new Date(time_start)>=new Date(time)||(new Date(time)<=new Date(new Date(time_start).setDate(new Date(time_start).getDate()+6)))){
                obj.time=year+'年'+i+'周';
                obj.now_week=i;
                var s_y=(new Date(time_start).getMonth()+1)<=9?'0'+(new Date(time_start).getMonth()+1):(new Date(time_start).getMonth()+1);
                var s_x=new Date(time_start).getDate()<=9?'0'+new Date(time_start).getDate():new Date(time_start).getDate();
                obj.time_start=new Date(time_start).getFullYear()+'-'+s_y+'-'+s_x;
                obj.data_type='W';
                obj.data_value=new Date(time_start).getFullYear()+'-'+s_y+'-'+s_x;
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
    ,data_obj:{area_keep:[],brand_keep:[],store_keep:[],guide_keep:[]}
    ,filtrateComponent:Vue.extend({
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
                        case '/shop/brand':fun.data_obj.brand_keep=obj.slice(0);break;
                        case '/area/selAreaByCorpCode':fun.data_obj.area_keep=obj.slice(0);break;
                        case '/shop/selectByAreaCode':fun.data_obj.store_keep=obj.slice(0);break;
                        case '/user/selectUsersByRole':fun.data_obj.guide_keep=obj.slice(0);break;
                    }
                }
                this.$emit('increment3',html,obj,e.target.innerHTML);
            }
            ,liFun:function (e) {
                $(e.currentTarget).find('label').trigger('click');
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
                        case '/shop/brand':this.node_r=fun.data_obj.brand_keep.slice(0);this.count=fun.data_obj.brand_keep.length;break;
                        case '/area/selAreaByCorpCode':this.node_r=fun.data_obj.area_keep.slice(0);this.count=fun.data_obj.area_keep.length;break;
                        case '/shop/selectByAreaCode':this.node_r=fun.data_obj.store_keep.slice(0);this.count=fun.data_obj.store_keep.length;break;
                        case '/user/selectUsersByRole':this.node_r=fun.data_obj.guide_keep.slice(0);this.count=fun.data_obj.guide_keep.length;break;
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

                        if(right_code.join('').search(data.brands[i].brand_code)==-1){
                            obj.key=false
                        }else{
                            obj.key=true
                        }
                        obj.code=data.brands[i].brand_code;
                        obj.name=data.brands[i].brand_name;

                        this.data_arr.push(obj);
                    }
                }else if( this.postUrl=='/area/selAreaByCorpCode'){
                    var area_arr=JSON.parse(data.list).list;
                    this.scroll_top=JSON.parse(data.list).hasNextPage;
                    for(var i=0;i<area_arr.length;i++){
                        var obj={};
                        if(right_code.join('').search(area_arr[i].area_code)==-1){
                            obj.key=false
                        }else{
                            obj.key=true
                        }
                        obj.code=area_arr[i].area_code;
                        obj.name=area_arr[i].area_name;
                        this.data_arr.push(obj);
                    }
                }else if(this.postUrl=="/shop/selectByAreaCode"){
                    var store_arr=JSON.parse(data.list).list;
                    this.scroll_top=JSON.parse(data.list).hasNextPage;
                    for(var i=0;i<store_arr.length;i++){
                        var obj={};
                        if(right_code.join('').search(store_arr[i].store_code)==-1){
                            obj.key=false
                        }else{
                            obj.key=true
                        }
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
}
var vip=new Vue({
    el:'#con_table'
    ,data:{
        time:'2017年3周'
        ,nav_range:false
        ,now_week:''//当前第几周
        ,nav_date:false//是否显示
        ,time_show:true
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
        ,data_no:''//没有数据时
        ,loading_show:''//加载遮罩
        ,next_page:1//请求页码
        ,chart_loading:false
        ,tableData:[]//列表数据
    }
    ,methods:{
        rangeFun:function(){
            this.nav_range=!this.nav_range;
            if(this.nav_range){
                whir.loading.add("",0.5);//加载等待框
                $('#loading').remove();
            }else{
                whir.loading.remove();//移除加载框
                $('#loading').remove();
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
                'corp_code':'C10141'
                ,'brand_code':brand_code.join(',')
                ,'area_code':area_code.join(',')
                ,'store_code':store_code.join(',')
                ,'user_code':user_code.join(',')
                ,'date_type':this.data_type
                //,'date_value':this.data_value
                ,'date_value':'2016-07-07'
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
            var url='/VipDevelop/view';
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
                'corp_code':'C10141',
                'app_function':$('#nav .nav_1>span').html()
                ,'brand_code':brand_code.join(',')
                ,'area_code':area_code.join(',')
                ,'store_code':store_code.join(',')
                ,'user_code':user_code.join(',')
                ,'date_type':this.data_type
                //,'date_value':this.data_value
                ,'date_value':'2016-07-07'
            }};
            this.chart_loading=true;
            this.$http.post(url,{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
                if(JSON.parse(data.data.message).vip_develop<=0)return;
                var myChart = echarts.init(document.getElementById('chart'));
                var data_arr=JSON.parse(data.data.message).vip_develop;
                //循环遍历取出
                for(var i=0;i<data_arr.length;i++){
                    this.chart_item.push(data_arr[i].date)//横坐标
                    this.chart_count.push(data_arr[i].vip_count);//纵坐标
                }
                myChart.setOption(this.option);
                this.chart_loading=false;
                window.addEventListener("resize", function () {
                    myChart.resize();
                });
            }, function (data) {

            });
        }
        ,tableFun:function () {
            var param=this.paramFun();
            param.message.pageNumber=this.next_page;
            param.message.pageSize=20;
            this.$http.post('/VipDevelop/list',{param:JSON.stringify(param)},{emulateJSON:true}).then(function (data) {
                console.log(JSON.parse(data.data.message).list);
                if(data.data.code==0){
                    if(JSON.parse(data.data.message).list.length==0){
                        this.data_no=true;
                        this.loading_show=false;
                    }else{
                        if(this.next_page>=JSON.parse(data.data.message).pages){
                            //禁止请求
                            this.stop_next_page='stop'
                        }else{
                            this.stop_next_page='go'
                        }
                        this.tableData=this.tableData.concat(JSON.parse(JSON.parse(data.data.message).list));
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
                this.post_start_param={'id':'','message':{'corp_code':'C10055',searchValue:''}};
                this.component_url="/shop/brand";
                this.message_obj.show=false;
            }else if(role=='所属区域'){
                this.post_start_param={'id':'','message':{'corp_code':'C10055',searchValue:'','pageSize':20,'pageNumber':1}};
                this.component_url="/area/selAreaByCorpCode";
                this.message_obj.show=false;
            }else if(role=='所属店铺'){
                brand_code=this.brand_input_arr.map(function (val) {
                    return val.code;
                });
                area_code=this.area_input_arr.map(function (val) {
                    return val.code;
                });
                this.post_start_param={'id':'','message':{'corp_code':'C10055',searchValue:'','pageSize':20,'pageNumber':1,'area_code':area_code.join(','),'brand_code':brand_code.join(',')}};
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
                this.post_start_param={'id':'','message':{'corp_code':'C10055',searchValue:'','pageSize':20,'pageNumber':1,'area_code':area_code.join(','),'brand_code':brand_code.join(','),'store_code':store_code.join(',')}};
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
    }
    ,computed:{
        option:function () {
            return  {
                backgroundColor:"#fff",
                color:['#4176c0'],
                tooltip: {
                    trigger: 'axis',
                    axisPointer:{
                        lineStyle:{
                            color:'#4176c0'
                        }
                    }
                },
                legend: {
                    right:"20",
                    data:['品牌关注度分析'],
                    selected:{
                        '关注人数':true
                    }
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
                series: [
                    {
                        name:'品牌关注度分析',
                        type:'line',
                        stack: '总量1',
                        data:this.chart_count,
                        symbol: "circle",
                        lineStyle:{
                            normal:{
                                width:"1"
                            }
                        }
                    }
                ]
            }
        }
        ,param_post:function () {
            return JSON.stringify(this.param)
        }
    }
    ,components: {
        'filtrateComponent':fun.filtrateComponent
    }
    ,created:function () {
        fun.timeFiltrate(this);
        fun.yearFun(this);
        this.chartFun();
        this.tableFun();
    }
});

