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
    })
});
//左移和右移
$(function(){   
    //移到右边
    $('#left_shift').click(function(){
        //先判断是否有选中
        var li=$("#file_list_l input[type='checkbox']:checked").parents("li");
        if(li.length=="0"){
            frame();
            $('.frame').html('请先选择'); 
        }
        //获取选中的选项，删除并追加给对方
        else{
            for(var i=li.length;i>=0;i--){
               $(li[i]).appendTo('#file_list_r ul'); 
            }
            bianse();
        }   
    });
    //移到左边
    $('#right_shift').click(function(){
        var li=$("#file_list_r input[type='checkbox']:checked").parents("li");
        //先判断是否有选中
        if(li.length=="0"){         
            frame();
            $('.frame').html('请先选择'); 
        }
        else{
            for(var i=li.length;i>=0;i--){
               $(li[i]).appendTo('#file_list_l ul'); 
            }
            bianse();
        }
    });
    //全部移到右边
    $('#left_shift_all').click(function(){
        //获取全部的选项,删除并追加给对方
        var li=$("#file_list_l input[type='checkbox']").parents("li");
        for(var i=li.length;i>=0;i--){
            $(li[i]).appendTo('#file_list_r ul');
        }
        bianse();
    });
    //全部移到左边
    $('#right_shift_all').click(function(){
        var li=$("#file_list_r input[type='checkbox']").parents("li");
        for(var i=li.length;i>=0;i--){
            $(li[i]).appendTo('#file_list_l ul'); 
        }
        bianse();
    });
});
function bianse(){
    $("#file_list_l li:odd").css("backgroundColor","#fff");
    $("#file_list_l li:even").css("backgroundColor","#ededed");
    $("#file_list_r li:odd").css("backgroundColor","#fff");
    $("#file_list_r li:even").css("backgroundColor","#ededed");
}