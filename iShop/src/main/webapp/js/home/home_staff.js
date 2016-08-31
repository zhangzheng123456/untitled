var oc = new ObjectControl();
$(function(){
    var Time=getNowFormatDate();
    $(".laydate-icon").val(Time);
    staffRanking(Time);
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
                    achieveChart(datas);
                    break;
                case 2:
                    staffRanking(datas);
                    break;
                case 3:
                    storeRanking(datas);
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
function staffRanking(T){
    var param={};
    var a =T.replace(/[-]/g, "");
    param["time"]=a;
    param["user_name"]='';
    oc.postRequire("post","/home/staffRanking","", param, function(data){
      console.log(data)
    })
}
function achAnalysis(T){
    var param={};
    var a =T.replace(/[-]/g, "");
    param["time"]=a;
    oc.postRequire("post","/home/achAnalysis","", param, function(data){
      console.log(data)
    })
}