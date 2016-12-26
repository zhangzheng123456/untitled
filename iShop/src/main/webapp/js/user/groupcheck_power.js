//编辑页面点击弹出角色权限的框
$('#edit_power').click(function(){
    group_code=$("#GROUP_ID").val();
    corp_code=$('#OWN_CORP').val();
    var group_name=$('#GROUP_NAME').val();
    var role_code=$('#OWN_ROLE').attr("data-mygcode");
    var group_corp={"corp_code":corp_code,role_code:role_code,"group_code":group_code,"group_name":group_name};
    sessionStorage.setItem("group_corp",JSON.stringify(group_corp));
    $(window.parent.document).find('#iframepage').attr("src","user/groupcheck_power1.html");
    $('#group_name').val(group_name);
    $("#page-wrapper").hide();
    $(".content").show();
})