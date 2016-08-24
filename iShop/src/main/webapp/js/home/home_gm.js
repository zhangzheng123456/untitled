var oc = new ObjectControl();
function storeRanking(){//店铺排行
	var param={};
	param["time"]="20160823";
	param["store_name"]="";
	oc.postRequire("post","/home/storeRanking","", param, function(data){
		console.log(data);
	})
}
storeRanking();