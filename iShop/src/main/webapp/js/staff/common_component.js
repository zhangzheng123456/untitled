/**
 * Created by Bizvane on 20170519/2/9.
 */
//定义筛选弹窗
var fun={
    timeFiltrate:function (obj) {//接受一个对象
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
        +'<div class="screen_content"><div class="screen_content_l"@scroll="scrollFun($event)"><ul><li v-for="obj in messageData" @click.self="liFun($event)"><div class="checkbox1"><input type="checkbox":checked="obj.key"  :data-code="obj.code" name="test" class="check" :id="obj.code"><label :for="obj.code"></label></div><span @click.self="spanFun($event)" class="p16">{{obj.name}}</span></li></ul></div>'//数据循环变量7
        +'<div class="screen_content_z"><div class="shift shift_right" title="右移"@click="moveFunR($event)">&gt;</div>' +//动作变量
        '<div class="shift shift_right_all" title="右移全部"@click="moveFunRAll($event)">&gt;&gt;</div>' +//动作函数变量8
        '<div class="shift shift_left" title="左移"@click="moveFunL($event)">&lt;</div>' +//动作函数变量9
        '<div class="shift shift_left_all" title="左移全部"@click="moveFunLAll($event)">&lt;&lt;</div></div>'+//动作函数变量10
        '<div class="screen_content_r"><ul><li v-for="obj in node_r" @click.self="liFun($event)"><div class="checkbox1"><input type="checkbox"  :data-code="obj.code" name="test" class="check" :id="obj.name+0"><label :for="obj.name+0"></label></div><span @click.self="spanFun($event)" class="p16">{{obj.name}}</span></li></ul></div>'+//数据添加变量11
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
            ,spanFun:function(e){
                $(e.currentTarget).prev().find('label').trigger('click');
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
                $(e.currentTarget).blur();
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