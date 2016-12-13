/**
 * Created by Bizvane on 2016/12/13.
 */

//新增页面弹窗事件
$('#jurisdiction').on('click','#add',function(e){
    e.stopPropagation();
    $('#get_more').show();
    //获取企业
    function getcorplist(a){
        $('.searchable-select').remove();
        $('#OWN_STORE').empty();
        //获取所属企业列表
        var corp_command="/shop/findStore";
        oc.postRequire("get", corp_command,"", "", function(data){
            if(data.code=="0"){
                var msg=JSON.parse(data.message);
                msg=JSON.parse(msg.list);
                console.log(msg)
                var index=0;
                var corp_html='';
                for( var i=0;i<msg.length;i++){
                    corp_html+='<option value="'+msg[i].corp_code+'">'+msg[i].corp_name+'</option>';
                }
                $("#OWN_STORE").append(corp_html);
                $('#OWN_STORE').searchableSelect();
                $('.corp_select .searchable-select-input').keydown(function(event){
                    var event=window.event||arguments[0];
                    if(event.keyCode == 13){
                        $("#services").html("");
                        $("input[verify='Code']").val("");
                        $("#BRAND_NAME").val("");
                        $("input[verify='Code']").attr("data-mark","");
                        $("#BRAND_NAME").attr("data-mark","");
                    }
                });
                $('.searchable-select-item').click(function(){
                    $("#services").html("");
                    $("input[verify='Code']").val("");
                    $("#BRAND_NAME").val("");
                    $("input[verify='Code']").attr("data-mark","");
                    $("#BRAND_NAME").attr("data-mark","");
                })
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
    function gender() {
        $('.searchable-select').remove();
        $('#gender').empty();
        //性别
        var corp_html='<option value="男">男</option>'+'<option value="女">女</option>';
        $("#gender").append(corp_html);
        $('#gender').searchableSelect();
        $('#gender').parent().find('.searchable-select-holder').html('男');
        $('#gender').parent().find('.searchable-select-input').remove();
    }
    getcorplist();
    gender();
})
$('#get_more .head_span_r').click(function () {
    $('#get_more').hide();
});
$('#get_more_close').click(function () {
    $('#get_more').hide();
});
