var oc = new ObjectControl();
var page=1;
var param={};//定义传值对象
$(function(){
    getConsumCount();
    upLoadAlbum();
});
function getConsumCount(){
    //whir.loading.add("",0.5);//加载等待框
    var id=sessionStorage.getItem("id");
    var param={};
    param["corp_code"]="C10000";
    param["vip_id"]=id;
    oc.postRequire("post","/vip/vipConsumCount","",param,function(data){
       var Data=JSON.parse(data.message);
       var album=JSON.parse(Data.Album);
       var label=JSON.parse(Data.Label);
       var conSumData=JSON.parse(Data.Consum);
       var HTML="";
       var LABEL="";
      $("#total_amount_Y").html(conSumData.total_amount_Y);
      $("#consume_times_Y").html(conSumData.consume_times_Y);
      $("#total_amount").html(conSumData.total_amount);
      $("#consume_times").html(conSumData.consume_times);
      $("#dormant_time").html(conSumData.dormant_time);
      $("#last_date").html(conSumData.last_date);
        for(var i=0;i<album.length;i++){
            if(i<16){
                HTML+="<span><img src="+album[0].image_url+" /></span>"
            }
            }
        $("#images").html(HTML);
        for(var i=0;i<label.length;i++){
                LABEL+="<span >"+label[i].label_name+"</span>"
        }
        $("#labels").html(LABEL);
        lg_img()
    })

}
function lg_img(){
    //点击图片放大
    $("#images span").click(function(){
        var src=$(this).children().attr("src");
        whir.loading.add("",0.8,src);//显示图片
    });
}
$("#fenLei").click(function(){//点击查看更多调到编辑资料
   $("#VIP_Message").hide();
   $("#VIP_edit").show();
    gethotVIPlabel();
});
$("#VIP_message_back").click(function(){//回到会员信息
   $("#VIP_Message").show();
   $("#VIP_edit").hide();
});

function gethotVIPlabel() {
    //热门标签
    var param={};
    param["corp_code"]="C10000";
    oc.postRequire("post","/VIP/label/findHotViplabel","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
                msg=JSON.parse(msg.list);
            var html="";
            console.log(msg.length);
            for(var i=0;i<msg.length;i++){
                if(msg[i].label_type=="user"){
                    html+="<span class="+'label_u'+">"+msg[i].label_name+"</span>"
                }else if(msg[i].label_type=="org"){
                    html+="<span>"+msg[i].label_name+"</span>"
                }
            }
            $("#hotlabel").append(html);
        }
    })
}
//官方会员搜索标签
function getOtherlabel() {
    oc.postRequire("post","/VIP/label/findViplabelByType ","",param,function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
                msg=JSON.parse(msg.list)
                msg=msg.list;
            console.log(msg);
            if(msg[0].label_type=="org"){
                for(var i=0;i<msg.length;i++){
                    var html="";
                    html+="<span class="+'label_g'+">"+msg[i].label_name+"</span>"
                }
                $("#label_org").append(html);
            }else if(msg[0].label_type=="user"){
                for(var j=0;j<msg.length;j++){
                    var html="";
                    html+="<span class="+'label_u'+">"+msg[j].label_name+"</span>"
                }
                $("#label_user").append(html);
            }

        }
    })
}
$("#label_li_org").click(function () {
    $("#label_org").empty();
    param["corp_code"]="C10000";
    param['pageNumber']=page;
    param['searchValue']="";
    param['type']="2";
    getOtherlabel();
})
$("#label_li_user").click(function () {
    $("#label_user").empty();
    param["corp_code"]="C10000";
    param['pageNumber']=page;
    param['searchValue']="";
    param['type']="3";
    getOtherlabel();
})


//回到会员列表
$("#VIP_LIST").click(function(){
    $(window.parent.document).find('#iframepage').attr("src","/vip/vip.html");
});
//导航点击切换窗口
$("#nav_bar li").click(function () {
    var index=$(this).index();
    $(this).addClass("active1");
    $(this).siblings().removeClass("active1");
    $(".all_list").children().eq(index).show();
    $(".all_list").children().eq(index).siblings().hide();
}).mouseover(function(){
    var len=$(this).width();
    var index=$(this).index();
    $("#remark").animate({left:len*index},200);
}).mouseout(function(){
    $("#remark").stop(true,true);
});
$(".nav_bar").mouseleave(function() {
    $("#remark").stop();
    var _this = $(".active1").index();
    $(this).children().eq(_this).addClass("active1");
    var len = $(this).children().width();
    $("#remark").animate({left: len * _this}, 200);})

//相册关闭按钮显示
$(".album img").mouseover(function () {
    $(this).next(".cancel_img").show();
}).mouseleave(function () {
    $(this).next(".cancel_img").hide();
})
$(".cancel_img").mouseover(function () {
    $(this).show();
}).mouseleave(function () {
    $(this).hide();
})

//相册图片点击放大.关闭
$(".album li").click(function () {
  var src=$(this).find("img").attr("src");
    whir.loading.add("",0.8,src);
})

//标签导航切换窗口
$(".label_nav li").click(function () {
    var index=$(this).index()+1;
    $(this).addClass("label_li_active");
    $(this).siblings().removeClass("label_li_active");
    $(".label_box").eq(index).show();
    $(".label_box").eq(index).siblings("div").hide();
})
//添加，删除标签
$(".labeladd_btn").click(function () {
    var val=$(".labeladd_box input").val();
    console.log(val);
    if(val!==""){
        $("#label_box span:last-child").after('<span class="label_u_active">'+val+'<i class="icon-ishop_6-12"></i></span>')
    }
    $("#label_box span i").click(function () {
        $(this).parent("span").remove();
    })
});
$("#label_box span i").click(function () {
    $(this).parent("span").remove();
});
function upLoadAlbum(){
    var client = new OSS.Wrapper({
        region: 'oss-cn-hangzhou',
        accessKeyId: 'O2zXL39br8rSn1zC',
        accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
        bucket: 'products-image'
    });
    document.getElementById('upAlbum').addEventListener('change', function (e) {
        var file = e.target.files[0];
        //var corp_code=$("#OWN_CORP").val()//公司编号
        //var user_code=$("#USERID").val()//员工编号
        // console.log(corp_code);
        // console.log(user_code);
        //var storeAs="";
        //if(user_code==""||user_code==undefined){
        //    storeAs = '/Corp_logo/ishow/'+corp_code.trim()+'.jpg';
        //    //Album/Vip/iShow/C10141-123-20160920186524.jpg
        //}
        //if(user_code!==""&&user_code!==undefined){
        //    storeAs = '/Avatar/User/iShow/'+corp_code.trim()+user_code.trim()+'.jpg';
        //    //Album/Vip/iShow/C10141-123-20160920186524.jpg
        //}
        var storeAs='Album/Vip/iShow/C10141-123-20160920186524.jpg';
        client.multipartUpload(storeAs, file).then(function (result) {
            $("#imghead").attr("src",result.url);
            $("#upAlbum").val("");
            console.log(result.url);
        }).catch(function (err) {
             console.log(err);
        });
    });
}
