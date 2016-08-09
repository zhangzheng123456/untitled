var oc = new ObjectControl();

 jQuery(document).ready(function(){
     if($(".pre_title label").text()=="个人资料"){
         var id=sessionStorage.getItem("id");
         var _params={"id":id};
         var _command="/user/myAccount";
         oc.postRequire("get", _command, "", _params, function(data) {
             console.log(data);
             if(data.code=="0"){
                 var msg = JSON.parse(data.message);
                     msg=JSON.parse(msg.user);
                 console.log(msg);
                 $("#IMG").val(msg.user_code);
                 $("#OWN_CORP").val(msg.corp_name);
                 $("#USER_ID").val(msg.user_code);
                 $("#USER_NAME").val(msg.user_name);
                 $("#POSITION").val(msg.position);
                 $("#IPHONE").val(msg.phone);
                 $("#USER_EMAIL").val(msg.email);
                 $("#PASSWORD").val(msg.password);
                 if(msg.sex=="F"){
                     $("#USER_SEX").val("女");
                 }else if(msg.sex=="M"){
                     $("#USER_SEX").val("男");
                 }
                 $("#OWN_GROUP").val(msg.group_name);
                 $("#OWN_SHOP").val(msg.area_name);
             }
         })
     }
})