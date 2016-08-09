var oc = new ObjectControl();

 jQuery(document).ready(function(){
     window.user.init();//初始化
     if($(".pre_title label").text()=="个人资料"){
         var id=sessionStorage.getItem("id");
         var _params={"id":id};
         var _command="/user/myAccount";
         oc.postRequire("get", _command, "", _params, function(data) {
             console.log(data);
             if(data.code=="0"){
                 var msg = JSON.parse(data.message);
                 $("#IMG").val(msg.user_code);
                 $("#OWN_CORP").val(msg.corp_name);
                 $("#USER_ID").val(msg.user_code);
                 $("#USER_NAME").val(msg.user_name);
                 $("#POSITION").val(msg.position);
                 $("#IPHONE").val(msg.phone);
                 $("#USER_EMAIL").val(msg.email);
                 $("#PASSWORD").val(msg.password);
                 $("#USER_SEX").val(msg.sex);
                 $("#OWN_GROUP").val(msg.group_name);
                 $("#OWN_SHOP").val(msg.area_name);
             }
         })
     }
})