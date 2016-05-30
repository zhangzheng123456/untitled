var oc = new ObjectControl();
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);
var funcCode=key_val.func_code;
function GET(){
    oc.postRequire("get","/corp/list?funcCode="+funcCode+"","","",function(data){
            console.log(data);
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                var cout=list.pages;
                var list=list.list;
                console.log(list);
                var actions=message.actions;
                jurisdiction(actions);
            }else if(data.code=="-1"){
                // alert(data.message);
            }
    });
}
GET();