var oc = new ObjectControl();
var myDate = new Date();       
var year=myDate.getFullYear();
var month=myDate.getMonth()+1;
var data=myDate.getDate();
var today=year+"-"+month+"-"+data;
console.log(today);
function storeRanking(a){//店铺排行
	var param={};
	param["time"]=a;
	param["store_name"]="";
	oc.postRequire("post","/home/storeRanking","", param, function(data){
		console.log(data);
	})
}
//店铺排行
var start = {
    elem: '#storeRanking',
    format: 'YYYY-MM-DD',
    max: laydate.now(), //最大日期
    istime: true,
    istoday: false,
    choose: function(datas) {
    	var datas=datas.replace(/[-]/g,"");
    	storeRanking(datas);
    }
};
laydate(start);
storeRanking(today);

