var oc = new ObjectControl();
$(function(){
    var Time=getNowFormatDate();
    $(".laydate-icon").val(Time);
    //staffRanking(Time);
    achAnalysis(Time)
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
                    //achieveChart(datas);
                    break;
                case 2:
                    staffRanking(datas);
                    break;
                case 3:
                    //storeRanking(datas);
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
function staffRanking(T){//导购排行
    var param={};
    var a =T.replace(/[-]/g, "");
    param["time"]=a;
    param["user_name"]='';
    oc.postRequire("post","/home/staffRanking","", param, function(data){
      console.log(data)
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