$("#edit_power").click(function(){
    var corp_code=$('#OWN_CORP').val();
	var group_code=$('#OWN_RIGHT').attr("data-myrcode");
    var user_code=$('#USERID').val();
    var user_name=$('#USER_NAME').val();
    var role_code=$('#OWN_RIGHT').attr("data-myjcode");
    var group_corp={"corp_code":corp_code,"role_code":role_code,"group_code":group_code,"user_code":user_code,"user_name":user_name};
    sessionStorage.setItem("group_corp",JSON.stringify(group_corp));
    $(window.parent.document).find('#iframepage').attr("src","user/usercheck_power1.html");
});