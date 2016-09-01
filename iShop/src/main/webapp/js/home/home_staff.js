var oc = new ObjectControl();
$(function(){
    var Time=getNowFormatDate();
    $(".laydate-icon").val(Time);
    staffRanking(Time);
    achAnalysis(Time);
    getShopList();
    vipRanking(Time);
    achieveChart(Time)
});
function getNowFormatDate() {//获取当前日期
    var date = new Date();
    var seperator1 = "-";
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = year + seperator1 + month + seperator1 + strDate;
    return currentdate
}
function lay1(InputID){//定义日期格式
    var start = {
        elem:InputID,
        format: 'YYYY-MM-DD',
        max: laydate.now(), //最大日期
        istime: true,
        istoday: false,
        choose: function(datas) {
            var type=Number(InputID.slice(-1));
            switch (type){
                case 0:
                    achAnalysis(datas);
                    break;
                case 1:
                    achieveChart(datas);
                    break;
                case 2:
                    staffRanking(datas);
                    break;
            }
        }
    };
    laydate(start)
}
$(".laydate-icon").click(//点击input 显示日期控件界面
    function () {
        var InputID='#'+$(this).attr("id");
        lay1(InputID)
    }
);

//导购加载
function superadditionStaff(c) {
    var staff_list = "";
    for (var i = 0; i < c.length; i++) {
        var a = i + 1;
        var b="";
        if(c[i].devote_rate==0){
            b=0;
        }
        if(c[i].devote_rate<=20){
            b=10;
        }
        if(c[i].devote_rate<=30&&c[i].devote_rate>20){
            b=20;
        }
        if(c[i].devote_rate<=40&&c[i].devote_rate>30){
            b=30;
        }
        if(c[i].devote_rate<=50&&c[i].devote_rate>40){
            b=40;
        }
        if(c[i].devote_rate<=60&&c[i].devote_rate>50){
            b=50;
        }
        if(c[i].devote_rate<=70&&c[i].devote_rate>60){
            b=60;
        }
        if(c[i].devote_rate<=80&&c[i].devote_rate>70){
            b=70;
        }
        if(c[i].devote_rate<=90&&c[i].devote_rate>80){
            b=80;
        }
        if(c[i].devote_rate<100&&c[i].devote_rate>90){
            b=90;
        }
        if(c[i].devote_rate=="100"){
            b=100;
        }
        staff_list += "<tr><td style='windth:13%'>" + a + "</td><td>" + c[i].user_name//导购名称
            + "</td><td>" + c[i].amount //业绩
            + "</td><td><img src='../img/contribution_"+b+".png' style='width:4px;height: 20px'>"
            + c[i].devote_rate //贡献度
            + "%</td></tr>"
    }
    var nodata="<tr><td colSpan='4' style='padding-top:70px;'>暂无数据</td></tr>";
    if(c.length==0){
        $("#staff_list tbody").html(nodata);
    }else {
        $("#staff_list tbody").html(staff_list);
    }
}

function staffRanking(T){//导购排行
    var param={};
    var a =T.replace(/[-]/g, "");
    param["time"]=a;
    param["user_name"]='';
    var value=$("#daoGouRanking_prev").html();
    oc.postRequire("post","/home/staffRanking","", param, function(data){
        var message=JSON.parse(data.message);
        var D=message.store_achv_d;
        var M=message.store_achv_m;
        var W=message.store_achv_w;
        var Averageyeji_D=message.store_d_value;
        var Averageyeji_W=message.store_w_value;
        var Averageyeji_M=message.store_m_value;
        var total=message.user_count;
        $("#daoGouTotal").html(total);
        superadditionStaff(D);
        $(".reg_testdate li").click(function() {
            value = $(this).html();
            var id = $(this).parent("ul").attr("id");
            $(this).parent("ul").prev(".title").html(value);
            $(this).parent("ul").hide();
            $(this).parent("ul").parent(".choose").removeClass("cur");
            if (value == "按日查看" && id == "daoGouRanking") {
                superadditionStaff(D);
                $("#Average").html(Averageyeji_D)
            } else if (value == "按周查看" && id == "daoGouRanking") {
                superadditionStaff(W);
                $("#Average").html(Averageyeji_W)
            } else if (value == "按月查看" && id == "daoGouRanking") {
                superadditionStaff(M);
                $("#Average").html(Averageyeji_M)
            }
        });
        if (value == "按日查看") {
            superadditionStaff(D);
            $("#Average").html(Averageyeji_D)
        } else if (value == "按周查看") {
            superadditionStaff(W);
            $("#Average").html(Averageyeji_W)
        } else if (value == "按月查看") {
            superadditionStaff(M);
            $("#Average").html(Averageyeji_M)
        }
    })
}


function achAnalysis(T){//业绩加载
    var param={};
    var a =T.replace(/[-]/g, "");
    param["time"]=a;
    var value=$('#sm_achv_prev').html();
    oc.postRequire("post","/home/achAnalysis","", param, function(data){
      var message=JSON.parse(data.message);
      var D=JSON.parse(message.D);
      var M=JSON.parse(message.M);
      var W=JSON.parse(message.W);
      var Y=JSON.parse(message.Y);
        $(".reg_testdate li").click(function() {
            value = $(this).html();
            var id = $(this).parent("ul").attr("id");
            $(this).parent("ul").prev(".title").html(value);
            $(this).parent("ul").hide();
            $(this).parent("ul").parent(".choose").removeClass("cur");
            if (value == "按日查看" && id == "sm_achv") {
                superadditionAchv(D);
            } else if (value == "按周查看" && id == "sm_achv") {
                superadditionAchv(W);
            } else if (value == "按月查看" && id == "sm_achv") {
                superadditionAchv(M);
            } else if (value == "按年查看" && id == "sm_achv") {
                superadditionAchv(Y);
            }
        });
        if (value == "按日查看") {
            superadditionAchv(D);
        } else if (value == "按周查看") {
            superadditionAchv(W);
        } else if (value == "按月查看") {
            superadditionAchv(M);
        } else if (value == "按年查看") {
            superadditionAchv(Y);
        }
    })
}
//业绩追加
function superadditionAchv(c){
   $("#yeJiTotal").html(c.staff.total);
   $("#yeJiRanking").html(c.staff.ranking);
   $("#zanTime").html(c.staff.praised_count);
   $("#Contribution_degree").html(c.staff.contribute);
   $("#My_Vip").html(c.staff.vip_count);

    //$("#area_ranking").attr("data-percent",c.am.area_ranking);
}

function getShopList(){//切换店铺
    oc.postRequire("get", "/shop/findStore", "", "", function(data) {
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            //console.log(list);
            $(".area_name").html(list[0].store_name);
            $(".area_name").attr("title",list[0].store_name);
            $(".area_name").attr("data-code",list[0].store_code);
            var html="";
            for(var i=0;i<list.length;i++){
                html+="<li data-code='"+list[i].store_code+"'>"+list[i].store_name+"<>"
            }
            $(".c_a_shoppe ul").html(html);
            $(".area_name").click(function(){
                $(".c_a_shoppe ul").show();
            });
            $(".c_a_shoppe ul li").click(function(){
                var area_code=$(this).attr("data-code");
                $(".area_name").attr("data-code",area_code);
                $(".area_name").html($(this).html());
                console.log($(this).html());
                //staffRanking(today,area_code);
                //achAnalysis(today,area_code);
            })
        }
    })
}

function achieveChart(data){//获取折线图
    var param={
        time:data.replace(/-/g,"")
    };
    oc.postRequire("post","/home/achInfo","", param, function(data){
        var infodata_W=JSON.parse(data.message).W;
        var infodata_M=JSON.parse(data.message).M;
        var infodata_Y=JSON.parse(data.message).Y;
        var TimeData=JSON.parse(infodata_W).amount;
        var value=$("#chart_prev").html();
        var perArr=[];
        var dateArr=[];
        if (value == "按周查看") {
            TimeData=JSON.parse(infodata_W).amount;
            $("#yeJiToTal_chart").html(JSON.parse(infodata_W).total);
        } else if (value == "按月查看") {
            TimeData=JSON.parse(infodata_M).amount;
            $("#yeJiToTal_chart").html(JSON.parse(infodata_M).total);
        } else if (value == "按年查看") {
            TimeData=JSON.parse(infodata_Y).amount;
            $("#yeJiToTal_chart").html(JSON.parse(infodata_Y).total);
        }
        for(index in TimeData){
            perArr.push(TimeData[index].trade);
            if(value == "按年查看"){
                dateArr.push(TimeData[index].date.substring(2,7));
            }else {
                dateArr.push(TimeData[index].date);
            }
        }
        init(perArr,dateArr);
        function setData(V){
            for(index in TimeData){
                perArr.push(TimeData[index].trade);
                if(V == "按年查看"){
                    dateArr.push(TimeData[index].date.substring(2,7));
                }else {
                    dateArr.push(TimeData[index].date);
                }
            }
        }
        $(".reg_testdate li").click(function() {
            perArr=[];
            dateArr=[];
            var value = $(this).html();
            var id = $(this).parent("ul").attr("id");
            $(this).parent("ul").prev(".title").html(value);
            $(this).parent("ul").hide();
            $(this).parent("ul").parent(".choose").removeClass("cur");
            if (value == "按周查看" && id == "chart") {
                TimeData=JSON.parse(infodata_W).amount;
                $("#yeJiToTal_chart").html(JSON.parse(infodata_W).total);
                setData(value);
                init(perArr,dateArr);
            } else if (value == "按月查看" && id == "chart") {
                TimeData=JSON.parse(infodata_M).amount;
                $("#yeJiToTal_chart").html(JSON.parse(infodata_M).total);
                setData(value);
                init(perArr,dateArr);
            } else if (value == "按年查看" && id == "chart") {
                TimeData=JSON.parse(infodata_Y).amount;
                $("#yeJiToTal_chart").html(JSON.parse(infodata_Y).total);
                setData(value);
                init(perArr,dateArr);
            }

        });
        $(window).resize(function() {
            init(perArr,dateArr);
        });
    })
}


function vipRanking(T){//会员排行
    var param={};
    var a =T.replace(/[-]/g, "");
    var area_code=$('.area_name').attr("data-code");
    var html="";
    if(area_code!=''&&area_code!=undefined){
        param["store_code"]=area_code;
    }
    param["time"]=a;
    oc.postRequire("post","/home/vipRanking","", param, function(data){
        var message=JSON.parse(data.message);
        var allVipList=message.all_vip_list;
      $("#dormant_vip_count").html(message.dormant_vip_count);
      $("#birth_vip_count").html(message.birth_vip_count);
      $("#all_vip_count").html(message.all_vip_count);
      $("#new_vip_count").html(message.new_vip_count);
       for(var i= 0;i<allVipList.length;i++){
           var a=1+i;
       html+="<tr>"
          +"<td style='width:11%'>"+a+"</td>"
              + "<td style='width:16%'>"+allVipList[i].vip_name+"</td>"
               +"<td style='width:21%'>"+allVipList[i].vip_card_type+"</td>"
               +"<td style='width:21%'><em>￥</em>"+allVipList[i].amount+"</td>"
               +"<td style='width:18%'>"+allVipList[i].consume_times+"</td>"
               //+"<td style='width:13%'>朱亚晓</td>"
               +"</tr>"
       }
        if(allVipList.length==0){
        var nodata="<tr><td colSpan='5' style='padding-top:70px;'>暂无数据</td></tr>";
            $("#vip_list tbody").html(nodata);
        }else {
            $("#vip_list tbody").html(html);
        }
    })
}


// 鼠标经过显示日周年月
$(".title").mouseover(function() {
    var ul = $(this).nextAll("ul");
    ul.show();
    $(this).parent(".choose").toggleClass("cur");
});
$(".title").mouseout(function() {
    var ul = $(this).nextAll("ul");
    $(this).parent(".choose").toggleClass("cur");
    ul.hide();
});
$(".select_Date").mouseover(function(){
    $(this).parent(".choose").toggleClass("cur");
    $(this).show()
});
$(".select_Date").mouseout(function(){
    $(this).parent(".choose").toggleClass("cur");
    $(this).hide()
});