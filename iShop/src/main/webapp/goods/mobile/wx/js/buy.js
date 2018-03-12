/**
 * Created by lishun on 20170519/5/15.
 */
var oc = new ObjectControl();
var buy={
    param:{
        list:"",
        wx_productitem:"",
        ids:"",
        num:"",
        space_type:"",
        space_sort:"",
    },
    init:function(){
        this.allEvent();
        this.getSizeList();
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
        $(".dapei_buy_content").on("click",".select",function(){
            if($(this).parents(".parent").attr("data-stasus")=="N"){
                return;
            }
            var num=$(this).parents(".parent").index();
            $("#dapeidetails").addClass("translate-to-o");
            $("#cover").show();
            var list=self.param.list[num];
            var spec=JSON.parse(list.longmessage).spec;
            var ids=JSON.parse(list.longmessage).ids;
            var size_id=$(".dapeidetails_content").eq(0).attr("data-id");
            var color_id=$(".dapeidetails_content").eq(1).attr("data-id");
            var color="";
            var size="";
            var html="";
            self.param.ids=ids;
            self.param.num=num;
            for(var i=0;i<spec.length;i++){
                html+="<div class='dapeidetails_content' data-index='"+i+"' ><div class='fengexian'><div class='xian'></div><div class='text'>选择"+spec[i].name+"</div></div><ul>"
                for(var j=0;j<spec[i].child.length;j++){
                    html+="<li id='"+spec[i].child[j].id+"'>"+spec[i].child[j].name+"</li>"
                }
                html+="</ul></div>";
            }
            $("#dapeidetails_parent").html(html);
            $("#checkd_guige").html("请选择规格");
            $("#dapeidetails").attr("data-id",list.id);
            $("#dapeidetails_img").attr("src",list.image_url);
            $("#dapeidetails_name").html(list.name);
            $("#dapeidetails_pirce").html("￥"+list.sc_price);
            $("#dapeidetails_stock").html("库存"+list.count+"件");
            $("#"+$("#dapei_buy_content .parent:eq('"+self.param.num+"')").attr("data-size")).click();
            $("#"+$("#dapei_buy_content .parent:eq('"+self.param.num+"')").attr("data-color")).click();
        });
        $("#close").click(function(){
            $("#dapeidetails").removeClass("translate-to-o");
            $("#cover").hide();
        });
        $("#cover").click(function(){
            $("#dapeidetails").removeClass("translate-to-o");
            $("#cover").hide();
        });
        $("#dapeidetails_parent").on("click","li",function(){
            $(this).toggleClass("active");
            $(this).siblings("li").removeClass("active");
            var num=$(".dapeidetails_content").length;
            var index=$(this).parents(".dapeidetails_content").attr("data-index");
            if($(this).attr("class")=="active"){
                var id=$(this).attr("id");
                var wx_productitem_id="";
                var text="已选"+$(this).text();
                if(num=="2"){
                    if($(this).parents(".dapeidetails_content").siblings().find(".active").length>0){
                        var next_id=$(this).parents(".dapeidetails_content").siblings().find(".active").attr("id");
                        text="已选"+$(this).parents(".dapeidetails_content").siblings().find(".active").text()+","+$(this).text();
                        if(index=="0"){
                            wx_productitem_id=id+"-"+next_id;
                        }
                        if(index=="1"){
                            wx_productitem_id=next_id+"-"+id;
                        }
                    }
                }
                if(index=="0"){
                    $("#dapei_buy_content .parent:eq('"+self.param.num+"')").attr("data-size",id);
                }
                if(index=="1"){
                    $("#dapei_buy_content .parent:eq('"+self.param.num+"')").attr("data-color",id);
                }
                if(num=="1"){
                    wx_productitem_id=id;
                    text="已选"+$(this).text();
                }
                wx_productitem_id=self.param.ids[wx_productitem_id];
                $("#checkd_guige").html(text);
                $("#dapei_buy_content .parent:eq('"+self.param.num+"')").find(".select").html(text);
                $(this).parents(".dapeidetails_content").attr("data-id",id);
                var wx_productitem=self.param.wx_productitem["WX_PRODUCT_ID_"+$("#dapeidetails").attr("data-id")];
                $("#dapei_buy_content .parent:eq('"+self.param.num+"')").attr("data-ids",wx_productitem_id);
                for(var i=0;i<wx_productitem.length;i++){
                    if(wx_productitem[i].id==wx_productitem_id){
                        $("#dapeidetails_img").attr("src",wx_productitem[i].image);
                        $("#dapeidetails_pirce").html("￥"+wx_productitem[i].sc_price);
                        $("#dapeidetails_stock").html("库存"+wx_productitem[i].stock+"件");
                        $("#dapei_buy_content .parent:eq('"+self.param.num+"')").find(".img img").attr("src",wx_productitem[i].image);
                        $("#dapei_buy_content .parent:eq('"+self.param.num+"')").find(".shop_pirce").html(wx_productitem[i].sc_price);
                        break;
                    }
                }
                var el=$("#dapei_buy_content input");
                var num=$("#dapei_buy_content input[type='checkbox']:checked").length;
                var pice_list=$("#dapei_buy_content .shop_pirce");
                var pice_total=0;
                var len = el.length;
                for(var i=0; i<len; i++)
                {
                    if((el[i].type=="checkbox") && (el[i].checked==true))
                    {
                        pice_total+=parseFloat($(pice_list[i]).text());
                    }
                }
                $("#pirce_total").html("￥"+pice_total);
                $("#num_total").html("已选"+num+"件");
            }else{
                $("#checkd_guige").html("请选择规格");
                $("#dapei_buy_content .parent:eq('"+self.param.num+"')").find(".select").html("请选择规格");
                $("#dapei_buy_content .parent:eq('"+self.param.num+"')").attr("data-ids","");
                $(this).parents(".dapeidetails_content").attr("data-id","");
                if(index=="0"){
                    $("#dapei_buy_content .parent:eq('"+self.param.num+"')").attr("data-size","");
                }
                if(index=="1"){
                    $("#dapei_buy_content .parent:eq('"+self.param.num+"')").attr("data-color","");
                }
                var list=self.param.list[self.param.num];
                $("#dapeidetails_img").attr("src",list.image_url);
                $("#dapeidetails_name").html(list.name);
                $("#dapeidetails_pirce").html("￥"+list.sc_price);
                $("#dapeidetails_stock").html("库存"+list.count+"件");
                $("#dapei_buy_content .parent:eq('"+self.param.num+"')").find(".img img").attr("src",list.image_url);
                $("#dapei_buy_content .parent:eq('"+self.param.num+"')").find(".shop_pirce").html(list.sc_price);
                var el=$("#dapei_buy_content input");
                var num=$("#dapei_buy_content input[type='checkbox']:checked").length;
                var pice_list=$("#dapei_buy_content .shop_pirce");
                var pice_total=0;
                var len = el.length;
                for(var i=0; i<len; i++)
                {
                    if((el[i].type=="checkbox") && (el[i].checked==true))
                    {
                        pice_total+=parseFloat($(pice_list[i]).text());
                    }
                }
                $("#pirce_total").html("￥"+pice_total);
                $("#num_total").html("已选"+num+"件");
            }
        });
        $(".dapei_buy_content").on("click",".checkbox",function(){
           if($(this).parent(".parent").attr("data-stasus")=="N"){
                return;
           }
            var input=$(this).find("input")[0];
            var thinput=$("thead input")[0];
            if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
                input.checked = true;
                var el=$("#dapei_buy_content input");
                var pice_list=$("#dapei_buy_content .shop_pirce");
                var num=$("#dapei_buy_content input[type='checkbox']:checked").length;
                var pice_total=0;
                var len = el.length;
                for(var i=0; i<len; i++)
                {
                    if((el[i].type=="checkbox") && (el[i].checked==true))
                    {
                        pice_total+=parseFloat($(pice_list[i]).text());
                    }

                }
                console.log(pice_total);
                $("#pirce_total").html("￥"+pice_total);
                $("#num_total").html("已选"+num+"件");
            }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
                input.checked = false;
                var el=$("#dapei_buy_content input");
                var num=$("#dapei_buy_content input[type='checkbox']:checked").length;
                var pice_list=$("#dapei_buy_content .shop_pirce");
                var pice_total=0;
                var len = el.length;
                for(var i=0; i<len; i++)
                {
                    if((el[i].type=="checkbox") && (el[i].checked==true))
                    {
                        pice_total+=parseFloat($(pice_list[i]).text());
                    }
                }
                $(".dapei_buy_header .left input")[0].checked=false;
                $("#pirce_total").html("￥"+pice_total);
                $("#num_total").html("已选"+num+"件");
            }
        });
        $("#buy").click(function(){
            var param={};
            var ids="";
            var counts="";
            var list=$("#dapei_buy_content .parent");
            var checkd=$("#dapei_buy_content input[type='checkbox']:checked");
            if(checkd.length<="0"){
                $('.success').html('请先选择商品');
                $('.success').css('display','block');
                setTimeout(function () {
                    $('.success').css('display','none');
                },1200);
                return;
            }
            for(var i=list.length-1,ID="";i>=0;i--){
                if($(list[i]).find('input')[0].checked==true){
                    var r=$(list[i]).attr("data-ids");
                    var b="1";
                    if(r==""){
                        $('.success').html('选择规格不完整');
                        $('.success').css('display','block');
                        setTimeout(function () {
                            $('.success').css('display','none');
                        },1200);
                        return;
                    }
                    if(i>0){
                        ids+=r+",";
                        counts+=b+",";
                    }else{
                        ids+=r;
                        counts+=b;
                    }
                }
            };
            var param={};
            param["ids"]=ids;
            param["corp_code"]=self.geturlparam().corp_code;
            param["d_match_code"]=self.geturlparam().d_match_code;
            param["counts"]=counts;
            param["user_id"]=self.geturlparam().user_id;
            oc.postRequire("post","/api/shopMatch/getUrlByWx","",param, function(data){
                if(data.code=="0"){
                    location.href=data.message;
                }
            })
        })
    },
    getSizeList:function(){
        var self=this;
        var corp_code=self.geturlparam().corp_code;
        var productId=self.geturlparam().productId;
        var param={};
        param["corp_code"]=corp_code;
        param["productId"]=productId;
        oc.postRequire("post","/api/shopMatch/getProuctDetails","",param, function(data){
            var message=JSON.parse(data.message).data;
            var list=JSON.parse(message).list;
            console.log(list);
            console.log(list.length);
            if(list.length>0){
                var wx_productitem=JSON.parse(message).intables.wx_productitem;
                self.param.list=list;
                self.param.wx_productitem=wx_productitem;
                self.renderPage(list);
                $("#shop_num").html(list.length);
                console.log(self.param.list);
                console.log(self.param.wx_productitem);
            }
        })
    },
    renderPage:function(list){
        var html="";
        for(var i=0;i<list.length;i++){
            var stasus="";
            var parent="parent";
            if(list[i].merchant_status=="N"){
                stasus="已下架";
                parent="parent opa";
            }
           html+="<div class='"+parent+"' data-ids='' data-stasus='"+list[i].merchant_status+"'><div class='checkbox'><input type='checkbox' value='' name='test'  class='check'><label></label></div><div class='shop'><div class='img'><img src='"
               +list[i].image_url+"' alt=''></div><div class='shop-t'><p>"
               +list[i].name+"</p><div class='select'>选择规格</div></div><div class='merchant_stasus'>"+stasus+"</div><div class='shop_num'>" +
               "<div class='pirce'>￥<span class='shop_pirce'>"+list[i].sc_price+"</span></div><div class='num'>1件</div></div></div></div>"
        }
        $("#dapei_buy_content").html(html);
    }
};
$(function(){
    buy.init();
})
