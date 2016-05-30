var oc = new ObjectControl();
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
function GET(){
    oc.postRequire("get","/corp/list?funcCode="+funcCode+"","","",function(data){
            console.log(data);
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                var cout=list.pages;
                var list=list.list;
                var actions=message.actions;
                superaddition(list);
                jurisdiction(actions);
                jumpBianse();
            }else if(data.code=="-1"){
                // alert(data.message);
            }
    });
}
GET();