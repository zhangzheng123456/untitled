var oc = new ObjectControl();
$(function(){
    var Time=getNowFormatDate();
    $(".icon-text").val(Time);
    weekFun('date1',Time,'week');
    staffRanking(Time);
    achAnalysis(Time);
    getShopList();
    vipRanking(Time);
    achieveChart(Time)
});
function strReponse(str){
    var time='';
    if(str.search('至')!=-1){//周
        time=str.split('至')[0]
    }else{//日月年
        if(str.split('-').length==1){//年
            time=str+'0101';
        }else if(str.split('-').length==2){//月
            time=str+'01'
        }else if(str.split('-').length==3){//月
            time=str;
        }
    }
    return time;
}
function weekFun(node,date,role){
    var value='';
    var time=getNowFormatDate();
    //周数据处理
    if(role=='week'){
        var start_week=new Date(date).getDay();
        var start_s='';
        var end_s='';
        if(date==time&&node=="date1"){
            start_s=new Date(date).setDate(new Date(date).getDate()+1-7);
            end_s=new Date(date).setDate(new Date(date).getDate());
        }else {
            if(start_week==0){
                start_s=new Date(date).setDate(new Date(date).getDate()-6);
                end_s=new Date(date);
            }else{
                start_s=new Date(date).setDate(new Date(date).getDate()+1-start_week);
                end_s=new Date(date).setDate(new Date(date).getDate()+(7-start_week));
            }
        }
        var start=new Date(start_s).getFullYear()+'-'+((new Date(start_s).getMonth()+1)<=9?('0'+(new Date(start_s).getMonth()+1)):(new Date(start_s).getMonth()+1))+'-'+(new Date(start_s).getDate()<=9?('0'+new Date(start_s).getDate()):new Date(start_s).getDate());
        var  end=new Date(end_s).getFullYear()+'-'+((new Date(end_s).getMonth()+1)<=9?('0'+(new Date(end_s).getMonth()+1)):(new Date(end_s).getMonth()+1))+'-'+(new Date(end_s).getDate()<=9?('0'+new Date(end_s).getDate()):new Date(end_s).getDate());
        value=start+' 至 '+end;
        document.getElementById(node).style.width='200px';
    }else if(role=='month'){
        value=date.split('-').slice(0,2).join('-');
        document.getElementById(node).style.width='100px';
    }else if(role=='day'){
        value=date.split('-').join('-');
        document.getElementById(node).style.width='100px';
    }else if(role=='year'){
        value=date.split('-')[0];
        document.getElementById(node).style.width='70px';
    }
    document.getElementById(node).value=value;
};
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
        istime: false, //是否开启时间选择
        isclear:false, //是否显示清空
        istoday: false,
        issure: false,// 是否显示确认
        choose: function(datas) {
            var type=Number(InputID.slice(-1));
            if(type==0){
                if ($('#sm_achv_prev').html() == '按周查看') {
                    weekFun('date0', datas, 'week');
                } else if ($('#sm_achv_prev').html() == '按月查看') {
                    weekFun('date0', datas, 'month');
                } else document.getElementById('date0').style.width = '100px';
                achAnalysis(datas);
            }else if(type==1){
                if($('#chart_prev').html()=='按周查看'){
                    weekFun('date1',datas,'week');
                }else if($('#chart_prev').html()=='按月查看'){
                    weekFun('date1',datas,'month');
                }else document.getElementById('date1').style.width='100px';
                achieveChart(datas);
            }else if(type==2){
                if ($('#daoGouRanking_prev').html() == '按周查看') {
                    weekFun('date2', datas, 'week');
                } else if ($('#daoGouRanking_prev').html() == '按月查看') {
                    weekFun('date2', datas, 'month');
                } else document.getElementById('date2').style.width = '100px';
                staffRanking(datas);
            }
        }
    };
    laydate(start)
}
$(".icon-text").click(//点击input 显示日期控件界面
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
        if(c[i].devote_rate<=0){
            b=0;
        }
        if(c[i].devote_rate<=20&&c[i].devote_rate>0){
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
        if(c[i].devote_rate>=100){
            b=100;
        }
        staff_list += "<tr><td style='width:97px'>" + a + "</td><td style='width:97px'>" + c[i].user_name//导购名称
            + "</td><td style='width:97px'>" + c[i].amount //业绩
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
    $("#staff_mask").hide();
}

function staffRanking(T){//导购排行
    var param={};
    var a =T.replace(/[-]/g, "");
    param["time"]=a;
    param["user_name"]='';
    var store_code=$(".area_name").attr("data-code");
    if(store_code!=''&&store_code!=undefined){
        param["store_code"]=store_code;
    }
    var value=$("#daoGouRanking_prev").html();
    $("#staff_mask").show();
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
                $("#staff_mask").show();
                superadditionStaff(D);
                $("#Average").html(Averageyeji_D)
            } else if (value == "按周查看" && id == "daoGouRanking") {
                $("#staff_mask").show();
                superadditionStaff(W);
                $("#Average").html(Averageyeji_W)
            } else if (value == "按月查看" && id == "daoGouRanking") {
                $("#staff_mask").show();
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
    $("#achv_mask").show();
    var store_code=$(".area_name").attr("data-code");
    if(store_code!=''&&store_code!=undefined){
        param["store_code"]=store_code;
    }
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
                $("#achv_mask").show();
                superadditionAchv(D);
            } else if (value == "按周查看" && id == "sm_achv") {
                $("#achv_mask").show();
                superadditionAchv(W);
            } else if (value == "按月查看" && id == "sm_achv") {
                $("#achv_mask").show();
                superadditionAchv(M);
            } else if (value == "按年查看" && id == "sm_achv") {
                $("#achv_mask").show();
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
   //$("#yeJiRanking").attr("title",c.staff.ranking);
   $("#zanTime").html(c.staff.praised_count);
   //$("#zanTime").attr("title",c.staff.praised_count);
   $("#Contribution_degree").html(c.staff.contribute);
   //$("#Contribution_degree").attr("title",c.staff.contribute);
   $("#My_Vip").html(c.staff.vip_count);
   //$("#My_Vip").attr("title",c.staff.vip_count);
   // $("#area_ranking").attr("data-percent",c.staff.achieve_rate);
   $("#achv_mask").hide();
   // $(".yield_rate canvas").remove();
   // var chart = window.chart = new EasyPieChart(document.querySelector('.yield_rate span'), {
   //      easing: 'easeOutElastic',
   //      delay: 3000,
   //      barColor: '#6cc1c8',
   //      trackColor: '#4a5f7c',
   //      scaleColor: false,
   //      lineWidth: 10,
   //      trackWidth: 10,
   //      lineCap: 'butt',
   //      onStep: function(from, to, percent) {
   //          this.el.children[0].innerHTML = Math.round(percent)+"%"+"<div style='color:#97a4b6'>达成率</div>";
   //      }
   //  });
}
//点击店铺
$(".c_a_shoppe").click(function(){
    var ul=$(".c_a_shoppe ul");
    if(ul.css("display")=="none"){
        ul.show();
        $("#drop_down_m").attr("src","../img/img_arrow_up.png");
    }else{
        ul.hide();
        $("#drop_down_m").attr("src","../img/img_arrow_down.png");
    }
});
function getShopList(){//切换店铺
    oc.postRequire("get", "/shop/findStore", "", "", function(data) {
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            $(".area_name").html(list[0].store_name);
            $(".area_name").attr("title",list[0].store_name);
            $(".area_name").attr("data-code",list[0].store_code);
            var html="";
            for(var i=0;i<list.length;i++){
                html+="<li data-code='"+list[i].store_code+"'>"+list[i].store_name+"</li>"
            }
            $(".c_a_shoppe ul").html(html);
            //$(".area_name").click(function(){
            //    $(".c_a_shoppe ul").show();
            //});
            $(".c_a_shoppe ul li").click(function(){
                var area_code=$(this).attr("data-code");
                $(".area_name").attr("data-code",area_code);
                $(".area_name").html($(this).html());
                //$(".c_a_shoppe ul").hide();
                staffRanking(strReponse($("#date2").val()));
                achAnalysis(strReponse($("#date0").val()));
                achieveChart(strReponse($("#date1").val()));
                vipRanking(getNowFormatDate())
            })
        }
    })
}

$(".reg_testdate li").click(function() {
    var value = $(this).html();
    var id = $(this).parent("ul").attr("id");
    $(this).parent("ul").prev(".title").html(value);
    $(this).parent("ul").hide();
    $(this).parent("ul").parent(".choose").removeClass("cur");
    var getTime=$("#date1").val();
    var dateType=$(this).attr("date-type");
    $(this).parent("ul").prev(".title").attr("date-type",dateType);
  if(id == "chart"){
      var time=[];
      var role=this.innerHTML=='按周查看'?'week':'month';
      time.push(new Date().getFullYear());
      time.push((new Date().getMonth()+1)<=9?'0'+(new Date().getMonth()+1):(new Date().getMonth()+1));
      time.push(new Date().getDate()<=9?'0'+new Date().getDate():new Date().getDate());
      weekFun('date1',time.join('-'),role);
      achieveChart(time.join(''));
  }else if(id=='sm_achv'){
      var time=[];
      var role='';
      if(this.innerHTML=='按日查看'){
          role='day'
      }else if(this.innerHTML=='按周查看'){
          role='week'
      }else if(this.innerHTML=='按月查看'){
          role='month'
      }
      time.push(new Date().getFullYear());
      time.push((new Date().getMonth()+1)<=9?'0'+(new Date().getMonth()+1):(new Date().getMonth()+1));
      time.push(new Date().getDate()<=9?'0'+new Date().getDate():new Date().getDate());
      weekFun('date0',time.join('-'),role);
  }else if(id=='daoGouRanking'){
      var time=[];
      var role='';
      if(this.innerHTML=='按日查看'){
          role='day'
      }else if(this.innerHTML=='按周查看'){
          role='week'
      }else if(this.innerHTML=='按月查看'){
          role='month'
      }
      time.push(new Date().getFullYear());
      time.push((new Date().getMonth()+1)<=9?'0'+(new Date().getMonth()+1):(new Date().getMonth()+1));
      time.push(new Date().getDate()<=9?'0'+new Date().getDate():new Date().getDate());
      weekFun('date2',time.join('-'),role);
  }
});

function achieveChart(data){//获取折线图
    var param={};
    param["time"]=data.replace(/-/g,"");
    var store_code=$(".area_name").attr("data-code");
    if(store_code!=''&&store_code!=undefined){
        param["store_code"]=store_code;
    }
    $("#chart_mask").show();
    var value=$("#chart_prev").html();
    var date_type=$("#chart_prev").attr("date-type");
    param["date_type"]=date_type;

    oc.postRequire("post","/home/achInfo","", param, function(data){
        var date_type=param["date_type"];
        var perArr=[];
        var dateArr=[];
       var  dateData=JSON.parse(data.message)[date_type];
        $("#yeJiToTal_chart").html(JSON.parse(dateData).total);
        var TimeData=JSON.parse(dateData).amount;
        for(index in TimeData){
            perArr.push(TimeData[index].trade);
            if(value == "按年查看"){
                dateArr.push(TimeData[index].date.substring(2,7));
            }else {
                dateArr.push(TimeData[index].date);
            }
        }
        init(perArr,dateArr);
        $("#chart_mask").hide();
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
    //if(area_code!=''&&area_code!=undefined){
    //    param["store_code"]=area_code;
    //}
    param["time"]=a;
    $("#vip_mask").show();
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
        $("#vip_mask").hide();
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
    $(this).parent(".choose").addClass("cur");
    $(this).show()
});
$(".select_Date").mouseout(function(){
    $(this).parent(".choose").removeClass("cur");
    $(this).hide()
});
$(document).click(function(e){
    if(!$(e.target).parents('.c_a_shoppe').length){
        $('.c_a_shoppe ul').hide();
    }
})