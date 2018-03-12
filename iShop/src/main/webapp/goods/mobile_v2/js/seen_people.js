var oc = new ObjectControl();
var buy={
    param:{
        query_type:"ALL",
        pageNumber:"1",
        pageSize:"10",
        is_shop:"",
        next:"false"
    },
    init:function(){
        this.allEvent();
        this.getPeopleList();
    },
    geturlparam:function(){
        var url = decodeURI(location.search); //获取url中"?"符后的字串
        var theRequest = new Object();
        if (url.indexOf("?") != -1) {
            var str = url.substr(1);
            strs = str.split("&");
            for (var i = 0; i < strs.length; i++) {
                theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
            }
        }
        return theRequest;
    },
    //全选
    checkAll:function(name){
        var el=$("#dapei_buy_content input");
        var pice_list=$("#dapei_buy_content .shop_pirce");
        var pice_total=0;
        var len = el.length;
        for(var i=0; i<len; i++)
        {
            if((el[i].type=="checkbox") && (el[i].name==name)&&$(el[i]).parents(".parent").attr("data-stasus")=="Y")
            {
                el[i].checked = true;
                pice_total+=parseFloat($(pice_list[i]).text());
            }
        }
        $("#pirce_total").html("￥"+pice_total);
        $("#num_total").html("已选"+i+"件");
    },
    //取消全选
    clearAll:function(name){
        var el=$("#dapei_buy_content input");
        var len = el.length;
        for(var i=0; i<len; i++)
        {
            if((el[i].type=="checkbox") && (el[i].name==name))
            {
                el[i].checked = false;
            }
        }
        $("#pirce_total").html("￥0");
        $("#num_total").html("已选0件");
    },
    allEvent:function(){
        var self=this;
        $("#seen_people_header li").click(function () {
            self.param.query_type=$(this).attr("data-type");
            $(this).addClass("active").siblings("li").removeClass("active");
            $("#people_list ul").empty();
            self.param.pageNumber=1;
            self.getPeopleList();
        });
        $("#check_parent").click(function(){
            var input=$(this).find("input")[0];
            if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
                input.checked = true;
                self.param.is_shop="Y";
            }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
                input.checked = false;
                self.param.is_shop="";
            }
            self.param.pageNumber=1;
            $("#people_list ul").empty();
            self.getPeopleList();
        });
        $(window).unbind().bind("scroll",function() {
            var bot = 50; //bot是底部距离的高度
            if ((bot + $(window).scrollTop()) >= ($(document).height() - $(window).height())) {
                if(!self.param.next){
                    return;
                }
                self.param.next=false;
                self.param.pageNumber++;
                self.getPeopleList();
            }
        });
        $("#people_list").on("click",".people",function () {
            var store_id="";
            if($(this).attr("store_id")==""||$(this).attr("store_id")=="undefined"){
                store_id="";
            }
            if($(this).attr("store_id")!==""&&$(this).attr("store_id")!=="undefined"){
                store_id=$(this).attr("store_id");
            }
            if($(this).attr("data-type")!=="VIP"){
                return;
            }
            var param={};
            param["vip_id"]=$(this).attr("vip_id");
            param["store_id"]=store_id;
            param["nick_name"]=$(this).attr("nickName");
            var vip_param={};
            vip_param["corp_code"]=self.geturlparam().corp_code;
            vip_param["user_id"]=self.geturlparam().user_id;
            vip_param["vip_id"]=$(this).attr("vip_id");
            $("#loading").show();
            oc.postRequire("post","/api/shopMatch/checkStaff","",vip_param, function(data){
                $("#loading").hide();
                var result=JSON.parse(data.message).result;
                if(result==true){
                    self.doAppWebRefresh(param);
                }
                if(result==false){
                    $(".success .msg").html("该会员属于其他导购");
                    $(".success").show();
                    setTimeout(function () {
                        $('.success').css('display','none');
                    },1200);
                }
            })

        })
    },
    getPeopleList:function(){
        var self=this;
        var param={};
        param["corp_code"]=self.geturlparam().corp_code;
        param["user_id"]=self.geturlparam().user_id;
        param["d_match_code"]=self.geturlparam().d_match_code;
        param["user_type"]=self.geturlparam().user_type;
        param["query_type"]=self.param.query_type;
        param["pageNumber"]=self.param.pageNumber;
        param["pageSize"]=self.param.pageSize;
        param["is_shop"]=self.param.is_shop;
        oc.postRequire("post","/api/shopMatch/getShopMatchPageViewsLog","",param, function(data){
            var list=JSON.parse(data.message).list;
            $("#vip").html("("+JSON.parse(data.message).vip_count+")");
            $("#yk").html("("+JSON.parse(data.message).yk_count+")");
            var all=parseInt(JSON.parse(data.message).yk_count)+parseInt(JSON.parse(data.message).vip_count);
            $("#all").html("("+all+")");
            var html="";
            if(list.length=="0"&&self.param.pageNumber=="1"){
                $("#kong_img").show();
            }
            if(list.length==0){
                self.param.next=false;
            }
            if(list.length>0){
                $("#kong_img").hide();
                self.param.next=true;
               for(var i=0;i<list.length;i++){
                   var buy="";
                   var bg="";
                   var img="";
                   var type="";
                   if(list[i].is_shop=="Y"){
                       buy="display:block";
                   }
                   if(list[i].visit=="YK"){
                       bg="display:none";
                   }
                   if(list[i].visit=="VIP"){
                       bg="display:block";
                   }
                   if(list[i].visit=="YK"&&list[i].is_shop=="N"){
                       type="num_yk";
                   }
                   if(list[i].visit=="YK"&&list[i].is_shop=="Y"){
                       type="num_yk_y";
                   }
                   if(list[i].visit=="VIP"&&list[i].is_shop=="Y"){
                       type="num_vip_y";
                   }
                   if(list[i].visit=="VIP"&&list[i].is_shop=="N"){
                       type="num_vip_n";
                   }
                   if(list[i].headImg==""){
                       img="image/head_none.png";
                   }
                   if(list[i].headImg!==""){
                       img=list[i].headImg;
                   }
                   html+="<li class='people' data-type='"+list[i].visit+"' vip_id='"+list[i].vip_id+"' store_id='"+list[i].store_id+"' nickName='"+list[i].nickName+"'><div class='img'><img src="+img+
                       " alt=''/></div><div class='people_t'><div class='name'>"+list[i].nickName+
                       "<span></span></div><div class='date'>"+list[i].date+
                       "</div><div class='buy_b' style='"+buy+"'>已买</div><div class="+type+">"+list[i].pageviews_count+"次</div><div class='bg' style='"+bg+"'></div></div></li>"
               }
            }
           $("#people_list ul").append(html);
        })
    },
    //获取手机系统
    getWebOSType:function() {
        var browser = navigator.userAgent;
        var isAndroid = browser.indexOf('Android') > -1 || browser.indexOf('Adr') > -1; //android终端
        var isiOS = !!browser.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
        if(isAndroid){
            return "Android";
        }else if (isiOS) {
            return "iOS";
        }else{
            return "Unknown"
        }
    },
    //调用APP方法传参 param 格式 type：** ;url:**
    doAppWebRefresh:function(param){
        var param=JSON.stringify(param);
        console.log(param);
        var osType = this.getWebOSType();
        if(osType=="iOS"){
            window.webkit.messageHandlers.NSJumpToWebViewForVip.postMessage(param);
        }else if(osType == "Android"){
            //iShop.returnAddResult(param);
            iShop.jumpToWebViewForVip(param);
        }
    }
};
function doAppWebHeaderRefresh(param){
    if(param=="headerRefresh"){
        $(".check_parent").css({"position":"absolute"});
        $(".seen_people_header").css({"position":"absolute"});
    }else{
        $(".check_parent").css({"position":"fixed"});
        $(".seen_people_header").css({"position":"fixed"});
    }
}
$(function(){
    buy.init();
})
