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
    // $("#file_list").niceScroll({
    //     cursorcolor:"#999",
    //     cursoropacitymax:1,
    //     touchbehavior:false,
    //     cursorwidth:"5px",
    //     cursorborder:"0",
    //     cursorborderradius:"5px"
    // });
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

    //以下会员档案积分导出，为了解决id冲突，所以重复写了一遍
    //移到右边
    $('#left_shift_t').click(function(){
        //先判断是否有选中
        var Li=$("#file_list_left input[type='checkbox']:checked").parents("li");
        var Input=$("#file_list_left input[type='checkbox']:checked");
        if(Li.length=="0"){
            frame();
            $('.frame').html('请先选择');
        }
        else{
            for(var i=Input.length-1;i>=0;i--){
                Input[i].checked=false;
                $(Li[i]).appendTo('#file_list_right ul');
            }
            bianse1();
        }
    });
    //移到左边
    $('#right_shift_t').click(function(){
        var Li=$("#file_list_right input[type='checkbox']:checked").parents("li");
        var Input=$("#file_list_right input[type='checkbox']:checked");
        //先判断是否有选中
        if(Li.length=="0"){
            frame();
            $('.frame').html('请先选择');
        }
        else{
            for(var i=Input.length-1;i>=0;i--){
                Input[i].checked=false;
                $(Li[i]).appendTo('#file_list_left ul');
            }
            bianse1();
        }
    });
    //全部移到右边
    $('#left_shift_all_t').click(function(){
        //获取全部的选项,删除并追加给对方
        var Li=$("#file_list_left input[type='checkbox']").parents("li");
        var Input=$("#file_list_left input[type='checkbox']");
        for(var i=Input.length-1;i>=0;i--){
            Input[i].checked = false;
            $(Li[i]).appendTo('#file_list_right ul');
        }
        bianse1();
    });
    //全部移到左边
    $('#right_shift_all_t').click(function(){
        var Li=$("#file_list_right input[type='checkbox']").parents("li");
        var Input=$("#file_list_right input[type='checkbox']");
        for(var i=Input.length-1;i>=0;i--){
            Input[i].checked=false;
            $(Li[i]).appendTo('#file_list_left ul');
        }
        bianse1();
    });

    //移到右边
    $('.export_left_shift').click(function(){
        //先判断是否有选中
        var li=$(".file_list_left input[type='checkbox']:checked").parents("li");
        var input=$(".file_list_left input[type='checkbox']:checked");
        if(li.length=="0"){
            frame();
            $('.frame').html('请先选择');
        }
        //获取选中的选项，删除并追加给对方
        else{
            for(var i=input.length-1;i>=0;i--){
                input[i].checked=false;
                $(li[i]).appendTo('.file_list_right ul');
            }
            bianse1();
        }
    });
    //移到左边
    $('.export_right_shift').click(function(){
        var li=$(".file_list_right input[type='checkbox']:checked").parents("li");
        var input=$(".file_list_right input[type='checkbox']:checked");
        //先判断是否有选中
        if(li.length=="0"){
            frame();
            $('.frame').html('请先选择');
        }
        else{
            for(var i=input.length-1;i>=0;i--){
                input[i].checked=false;
                $(li[i]).appendTo('.file_list_left ul');
            }
            bianse1();
        }
    });
    //全部移到右边
    $('.export_left_shift_all').click(function(){
        //获取全部的选项,删除并追加给对方
        var li=$(".file_list_left input[type='checkbox']").parents("li");
        var input=$(".file_list_left input[type='checkbox']");
        for(var i=input.length-1;i>=0;i--){
            input[i].checked = false;
            $(li[i]).appendTo('.file_list_right ul');
        }
        bianse1();
    });
    //全部移到左边
    $('.export_right_shift_all').click(function(){
        var li=$(".file_list_right input[type='checkbox']").parents("li");
        var input=$(".file_list_right input[type='checkbox']");
        for(var i=input.length-1;i>=0;i--){
            input[i].checked=false;
            $(li[i]).appendTo('.file_list_left ul');
        }
        bianse1();
    });
});
function bianse1(){
    $("#file_list_l li:odd,#file_list_left li:odd,.file_list_left li:odd").css("backgroundColor","#fff");
    $("#file_list_l li:even,#file_list_left li:even,.file_list_left li:even").css("backgroundColor","#ededed");
    $("#file_list_r li:odd,#file_list_right li:odd,.file_list_right li:odd").css("backgroundColor","#fff");
    $("#file_list_r li:even,#file_list_right li:even,file_list_right li:even").css("backgroundColor","#ededed");
}
$("#file_list,#file_list_t,.file_list_wrap").on("click","li",function(){
    var input=$(this).find("input")[0];
    var thinput=$("thead input")[0];
    if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
        input.checked = true;
    }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
        input.checked = false;
    }
})