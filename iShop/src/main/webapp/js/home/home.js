$(function(){
    var oc = new ObjectControl();
    var _command="/menu";
    oc.postRequire("get", _command,"", "", function(data){
        console.log(data);
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            var message=JSON.parse(msg.message);
            console.log(message);
            var index=0;
            var html =" ";
            var li_html=" ";
            var p=null;
            for(index in message.menu){
                var index_li=0;
                p=message.menu[index];

                if(p.functions.length!==0){
                    html +='<li>'
                    +'<a>'
                        +'<i class="'+p.icon+'"></i>'
                        +'<span class="nav-label">'+p.mod_name+'</span>'
                        +'<span class="fa arrow"></span>'
                    +'</a>'
                    +'<ul class="nav nav-second-level">';
                }else{
                    html +='<li id="skip" data-url="'+p.url+'" data-func_code="'+p.func_code+'">'
                    +'<a href="../../navigation_bar.html" onclick="menuclick(this);">'
                        +'<i class="'+p.icon+'"></i>'
                        +'<span class="nav-label">'+p.mod_name+'</span>'
                    +'</a>'
                    +'<ul class="nav nav-second-level">';
                }
                if(p.functions.length!==0){
                    for(index_li in p.functions){
                        html +='<li data-url="'+p.functions[index_li].url+'" data-func_code="'+p.functions[index_li].func_code+'"><a href="../../navigation_bar.html" onclick="menuclick(this)">'+p.functions[index_li].fun_name+'</a>'
                         +'</li>';
                    }
                }
                html +='</ul>'
                 +'</li>';

            }
            $(html).insertAfter('.navbar-static-side .sidebar-collapse #side-menu .nav-header');
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
    });
	
    
});
function menuclick(obj){
    location.href="../../navigation_bar.html";
    _this=$(obj);
    console.log(_this.parent().data("url"));
    var skip_url=_this.parent().data("url");
    var skip_funcode=_this.parent().data("func_code");
    var key_val={"url":skip_url,"func_code":skip_funcode};

    sessionStorage.setItem("key_val",JSON.stringify(key_val));
}
function login_out(){
    var _command="/login_out";
    oc.postRequire("get", _command,"", "", function(data){
    });
}