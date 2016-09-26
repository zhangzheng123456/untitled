var ObjectControl = new Function()
ObjectControl.prototype = {
	postRequire:function(require_type,require_url,require_id,require_data,callback){
		var _params = {
			"id":require_id,
			"message":require_data
		};
		jQuery.ajax({
			url: require_url,
			type: require_type,
			dataType: 'json',
			data:{param:JSON.stringify(_params)},
			success:function(data){
				if(data){
					callback(data);
				}else{

				}
			},
			error:function(data){
				console.log(data);
				// window.location.reload();
				console.log(data.responseText);
			}
		});

	}
}



