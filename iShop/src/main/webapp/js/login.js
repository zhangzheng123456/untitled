var oc = new ObjectControl();
$(function(){
	$('.btn_login').click(function(){
		var phone=$('#login').val();
	    var password=$('#password1').val();
	    var param={};
	    param["phone"]=phone;
	    param["password"]=password;
	    console.log(param);
	    if(phone==''||password==''){
	    	if(phone==""){
	    		$(".portlet-msg-error").html("用户名不能为空");
	    	}
	    	if(password==''){
	    		$(".portlet-msg-error").html("密码不能为空");
	    	}
	    	return;	
	    }
	    oc.postRequire("post","/login","0",param,function(data){
	    	if(data.code=="0"){
	    		
	    	}
	    })
	})
})