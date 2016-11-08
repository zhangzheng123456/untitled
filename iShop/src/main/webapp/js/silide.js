$(function() {
    $(window).scroll(function() {
        if ($(window).scrollTop() > 100) {
            $("#side_bar .gotop").fadeIn();
        } else {
            $("#side-bar .gotop").hide();
        }
    });
    $("#side_bar .gotop").click(function() {
        $('html,body').animate({
            'scrollTop': 0
        }, 500);
    });
    $("#side_bar .gobottom").click(function() {
        var btm = $('html,body').height();
        $('html,body').animate({
            'scrollTop': btm
        }, 500);
    });
    $("#file_list").niceScroll({
        cursorcolor:"#999",
        cursoropacitymax:1,
        touchbehavior:false,
        cursorwidth:"5px",
        cursorborder:"0",
        cursorborderradius:"5px"
    });
});
//左移和右移
$(function(){   
    //移到右边
    $('#left_shift').click(function(){
        //先判断是否有选中
        var li=$("#file_list_l input[type='checkbox']:checked").parents("li");
        var input=$("#file_list_l input[type='checkbox']:checked");
        if(li.length=="0"){
            frame();
            $('.frame').html('请先选择'); 
        }
        //获取选中的选项，删除并追加给对方
        else{
            for(var i=input.length-1;i>=0;i--){
               input[i].checked=false;
               $(li[i]).appendTo('#file_list_r ul'); 
            }
            bianse1();
        }   
    });
    //移到左边
    $('#right_shift').click(function(){
        var li=$("#file_list_r input[type='checkbox']:checked").parents("li");
        var input=$("#file_list_r input[type='checkbox']:checked");
        //先判断是否有选中
        if(li.length=="0"){
            frame();
            $('.frame').html('请先选择');
        }
        else{
            for(var i=input.length-1;i>=0;i--){
                input[i].checked=false;
                $(li[i]).appendTo('#file_list_l ul'); 
            }
            bianse1();
        }
    });
    //全部移到右边
    $('#left_shift_all').click(function(){
        //获取全部的选项,删除并追加给对方
        var li=$("#file_list_l input[type='checkbox']").parents("li");
        var input=$("#file_list_l input[type='checkbox']");
        for(var i=input.length-1;i>=0;i--){
            input[i].checked = false;
            $(li[i]).appendTo('#file_list_r ul');
        }
       bianse1();
    });
    //全部移到左边
    $('#right_shift_all').click(function(){
        var li=$("#file_list_r input[type='checkbox']").parents("li");
        var input=$("#file_list_r input[type='checkbox']");
        for(var i=input.length-1;i>=0;i--){
            input[i].checked=false;
            $(li[i]).appendTo('#file_list_l ul'); 
        }
        bianse1();
    });
});
function bianse1(){
    $("#file_list_l li:odd").css("backgroundColor","#fff");
    $("#file_list_l li:even").css("backgroundColor","#ededed");
    $("#file_list_r li:odd").css("backgroundColor","#fff");
    $("#file_list_r li:even").css("backgroundColor","#ededed");
}
$("#file_list").on("click","li",function(){
    var input=$(this).find("input")[0];
    var thinput=$("thead input")[0];
    if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
        input.checked = true;
    }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
        input.checked = false;
    }
})
$("#reload").click(function(){
    window.location.reload();
})