var oc = new ObjectControl();
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);
var funcCode=key_val.func_code;
function GET(){
    oc.postRequire("get","/corp/list?funcCode="+funcCode+"","","",function(data){
            console.log(data);
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                console.log(message);
                var list=JSON.parse(message.list);
                console.log(list);
                console.log(list);
                var id=list.id;
                console.log(list);
                var actions=message.actions;
                if(list.avatar!==undefined){
                    if(list.avatar.indexOf("http")==-1){
                        $(".editor_1 .shop_logo img").attr("src","../img/bg1.png");
                    }
                    if(list.avatar.indexOf("http")!==-1){
                        $(".editor_1 .shop_logo img").attr("src",list.avatar);
                    }
                }
                if(list.avatar==undefined){
                    $(".editor_1 .shop_logo img").attr("src","../img/bg1.png");
                }
                $("#corp_code").html(list.corp_code);
                $('#corp_name').html(list.corp_name);
                $('#address').html(list.address);
                $('#address').attr('title',list.address);
                $('#contact').html(list.contact);
                $('#contact_phone').html(list.contact_phone);
                $('#modifier').html(list.modifier);
                $('#created_date').html(list.created_date);
                $('#creater').html(list.creater);
                $('#modified_date').html(list.modified_date);
                sessionStorage.setItem("id",id);
            }else if(data.code=="-1"){
                art.dialog({
                    time: 1,
                    lock:true,
                    cancel: false,
                    content: data.message
                });
            }
    });
}
GET();
//页面加载调权限接口
function qjia(){
    var param={};
    param["funcCode"]=funcCode;
    oc.postRequire("post","/list/action","0",param,function(data){
        var message=JSON.parse(data.message);
        var actions=message.actions;
        if(actions[0].act_name=="edit"){
            $("#compile").html("<a href='crop_edit.html'><div class='shop_editor'>编辑</div></a>")
        }
    })
}
qjia(); 