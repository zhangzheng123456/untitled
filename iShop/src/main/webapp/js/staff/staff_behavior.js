/**
 * Created by Bizvane on 2017/2/6.
 */
//全局组件
Vue.component('filtrate', {
    template: ' <ul><li v-for="(val,index) in counter"><label>{{val}}</label><input v-if="!(val==\'开始时间\'||(val==\'截止时间\'))"type="text" >'
    +'<input  v-if="(val==\'开始时间\'||(val==\'截止时间\'))"type="text"class="laydate-icon" onclick="laydate({min:\'1900-01-01 00:00:00\',max: \'2099-12-31 23:59:59\',istime: true, format: \'YYYY-MM-DD\'})"></li></ul>',
    data: function () {
        return {
            counter:vm.data_filtrate
        }
    }
    ,methods: {
        increment: function () {
            this.$emit('increment')
        }
    }
});
//创建组件
Vue.component('model', {
    template:'<tr><th v-for="val in counter">{{val}}</th></tr>'
    ,data: function () {
        return {
            counter:['公司名称','公司地址','联系人','联系电话','修改人','修改时间','可用']
        }
    }
    ,methods: {
        increment: function () {
            this.$emit('increment')
        }
    }
});
var vm=new Vue({
    el:'#con_table'
    ,data:{
         login_way_show:false
        ,login_times_show:false
        ,login_way:'登录注册'
        ,login_times:'登录次数'
        ,login_way_arr:['登录注册','线下注册','app注册']
        ,login_times_arr:['登录1次','登录2次','登录3次']
        ,data_filtrate:['姓名','员工编号','所属群组','动作描述','开始时间','截止时间']
        ,sxk:false
        ,th_arr:['公司名称','公司地址','联系人','联系电话','修改人','修改时间','可用']
    }
    ,methods:{
       document:function (e) {//关闭过滤
            if(!($(e.target).parents('#login_way').length==1||(e.target.className=='login_way'))){
               this.login_way_show=false
            }
            if(!($(e.target).parents('#login_times').length==1||(e.target.className=='login_times'))){
                this.login_times_show=false
            }
            //切换action选项
            if($(e.target).parents('#login_way').length==1){//登录方式切换
                this.login_way=e.target.innerHTML;
            }
            if($(e.target).parents('#login_times').length==1){//登录次数切换
                this.login_times=e.target.innerHTML;
            }
        }
      ,empty:function () {
            var arr=this.$refs.child.$el.children;
            for(var i=0;i<arr.length;i++){
                $(arr[i]).find('input').val('');
            }
        }
     ,search:function () {
            var searchValue=[];
            var arr=this.$refs.child.$el.children;
            for(var i=0;i<arr.length;i++){
                var obj={};
                obj[$(arr[i]).find('label').html()]=$(arr[i]).find('input').val()
                searchValue.push(obj);
            }
            console.log(searchValue)
        }
    },
    components: {
    },
    computed:{
    }
    ,ready: function() {

    }
});
//        创建组件
//        注册组件
