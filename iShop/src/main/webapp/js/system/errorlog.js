var oc = new ObjectControl();
var key_val=sessionStorage.getItem("key_val");//取function_code的值
key_val=JSON.parse(key_val);
var funcCode=key_val.func_code;
$(function(){
    qjia();
});
$("#filtrate").click(function(){//点击筛选框弹出下拉框
    $(".sxk").slideToggle();
});
$("#pack_up").click(function(){//点击收回 取消下拉框
    $(".sxk").slideUp();
});
//权限配置
function jurisdiction(actions){
    $('#jurisdiction').empty();
    for(var i=0;i<actions.length;i++){
        if(actions[i].act_name=="add"){
            $('#jurisdiction').append("<li id='add'><a href='javascript:void(0);'><span class='icon-ishop_6-01'></span>新增</a><div class='fen'><div id='admin'>新增系统管理员</div><div id='corp'>新增企业用户</div></div></li>");
        }else if(actions[i].act_name=="delete"){
            $('#jurisdiction').append("<li id='remove'><a href='javascript:void(0);'><span class='icon-ishop_6-02'></span>删除</a></li>");
        }else if(actions[i].act_name=="edit"){
            $('#jurisdiction').append("<li id='compile'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>编辑</a></li>");
        }else if(actions[i].act_name=="qrcode"){
            $('#jurisdiction').append("<li id='qrcode'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>生成</a></li>");
        }
    }
}
//页面加载调权限接口
function qjia(){
    var param={};
    param["funcCode"]=funcCode;
    oc.postRequire("post","/list/action","0",param,function(data){
        var message=JSON.parse(data.message);
        var actions=message.actions;
        jurisdiction(actions);
        //jumpBianse();
    })
}
//筛选按钮
oc.postRequire("get","/list/filter_column?funcCode="+funcCode+"","0","",function(data){
    //if(data.code=="0"){
    //    var message=JSON.parse(data.message);
    //    var filter=message.filter;
    //    $("#sxk .inputs ul").empty();
    //    var li="";
    //    for(var i=0;i<filter.length;i++){
    //        if(filter[i].type=="text"){
    //            li+="<li><label>"+filter[i].show_name+"</label><input type='text' id='"+filter[i].col_name+"'></li>";
    //        }else if(filter[i].type=="select"){
    //            var msg=filter[i].value;
    //            console.log(msg);
    //            var ul="<ul class='isActive_select_down'>";
    //            for(var j=0;j<msg.length;j++){
    //                ul+="<li data-code='"+msg[j].value+"'>"+msg[j].key+"</li>"
    //            }
    //            ul+="</ul>";
    //            li+="<li class='isActive_select'><label>"+filter[i].show_name+"</label><input type='text' id='"+filter[i].col_name+"' data-code='' readonly>"+ul+"</li>"
    //        }
    //
    //    }
    //    $("#sxk .inputs ul").html(li);
    //    if(filtrate!==""){
    //        $(".sxk").slideDown();
    //        for(var i=0;i<list.length;i++){
    //            if($("#"+list[i].screen_key).parent("li").attr("class")!=="isActive_select"){
    //                $("#"+list[i].screen_key).val(list[i].screen_value);
    //            }else if($("#"+list[i].screen_key).parent("li").attr("class")=="isActive_select"){
    //                var svalue=$("#"+list[i].screen_key).next(".isActive_select_down").find("li[data-code='"+list[i].screen_value+"']").html();
    //                $("#"+list[i].screen_key).val(svalue);
    //            }
    //        }
    //    }
    //    filtrateDown();
    //    //筛选的keydow事件
    //    $('#sxk .inputs input').keydown(function(){
    //        var event=window.event||arguments[0];
    //        if(event.keyCode == 13){
    //            getInputValue();
    //        }
    //    })
    //}
});

