$(function(){
    select_oper();
	$('#add_record').click(function(){
        if($('#sign_record #sign_ul li').length!==0){
            $('<li>'
                    +'<span>'
                        +'<div class="s_item"><em>时间</em><input type="text" />'
                            +'<em>状态</em>'
                            +'<span class="select_state" onclick="onclick_select(this)">'
                                +'<input class="input_select" type="text" readonly/><span class="down_icon"><i class="icon-ishop_8-02"></i></span>'
                                +'<ul>'
                                    +'<li>日</li>'
                                    +'<li>周</li>'
                                    +'<li>月</li>'
                                    +'<li>年</li>'
                                +'</ul>'
                            +'</span>'
                        +'</div>'
                        +'<div class="address"><em>地点</em><input type="text" /></div>'
                    +'</span>'
                    +'<span class="delete_record"><i class="icon-ishop_6-12"></i></span>'
                +'</li>').insertBefore('#sign_record #sign_ul li:first');
        }else{
            $('#sign_record #sign_ul').append('<li>'
                    +'<span>'
                        +'<div class="s_item"><em>时间</em><input type="text" />'
                            +'<em>状态</em>'
                            +'<span class="select_state" onclick="onclick_select(this)">'
                                +'<input class="input_select" type="text" readonly/><span class="down_icon"><i class="icon-ishop_8-02"></i></span>'
                                +'<ul>'
                                    +'<li>日</li>'
                                    +'<li>周</li>'
                                    +'<li>月</li>'
                                    +'<li>年</li>'
                                +'</ul>'
                            +'</span>'
                        +'</div>'
                        +'<div class="address"><em>地点</em><input type="text" /></div>'
                    +'</span>'
                    +'<span class="delete_record"><i class="icon-ishop_6-12"></i></span>'
                +'</li>');
        }
        select_oper();
	});

});
function select_oper(){
	var deletes=$('#sign_record .delete_record');
	for(var i=0;i<deletes.length;i++){
		$(deletes[i]).click(function() {
			$(this).parent().remove();
		});
	}
}
function onclick_select(obj){
    var ul=$(obj).children('ul');
    if(ul.css("display")=="none"){
        ul.show();
        $(obj).children("ul").children('li').click(function(){
            var this_=this;
            var txt = $(this_).text();
            $(this_).parent().parent().children(".input_select").val(txt);
            $(this_).addClass('rel').siblings().removeClass('rel');
        });
    }else{
        ul.hide();
    }
}