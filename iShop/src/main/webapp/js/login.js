var oc = new ObjectControl();
$(function(){
	$('.btn_login').click(function(){
		var phone=$('#login').val();
	    var password=$('#password1').val();
	    var param={};
	    param[phone]=phone;
	    param[password]=password;
	    oc.postRequire("post","/login","0",param,function(data){
	    	console.log(data);
	    })
	})
})