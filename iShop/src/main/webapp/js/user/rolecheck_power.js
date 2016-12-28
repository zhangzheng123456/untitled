//编辑页面点击弹出角色权限的框
$('#edit_power').click(function(){
    var role_code=$("#ROLE_NUM").val();
    var role_name=$("#ROLE_NAME").val();
    var group_corp={"role_code":role_code,"role_name":role_name};
    sessionStorage.setItem("group_corp",JSON.stringify(group_corp));
    $(window.parent.document).find('#iframepage').attr("src","user/rolecheck_power1.html");
})
