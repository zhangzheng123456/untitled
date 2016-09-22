$(function(){
    getVipInfo();
});
function getVipInfo(){
    var param_info={};
    param_info["vip_id"]="1";
    param_info["corp_code"]="2";
    oc.postRequire("post","/vip/vipInfo","",param_info,function(data){
        console.log(data)
    })
}
