var ObjectControl = new Function();
function getTopWindow(){
	var p = window;
	while(p != p.parent){
		p = p.parent;
	}
	return p;
}
ObjectControl.prototype = {
	postRequire:function(require_type,require_url,require_id,require_data,callback,errorCallback){
		var _params = {
			"id":require_id,
			"message":require_data
		};
        jQuery.ajax({
			url: require_url,
			type: require_type,
			dataType: 'json',
			data:{param:JSON.stringify(_params)},
			timeout:120000,
			success:function(data){
				if(data){
				    callback(data);
				}else{
					
				}
			},
			error:function(data){
				if(data.statusText=="timeout"){
					errorCallback(data);
				}
				if(data.statusText !== "abort"){//会员分析里，为了防止重复请求通过abort方法抛出error，过滤这层
                    //var str=data.responseText.trim().substring(252,257);
                    var str=$($(data.responseText)[5]).html();
                    if(str=="iShop"){
						var top=getTopWindow();
						top.location.href=data;
                    }
				}
			}
		});
	}
};