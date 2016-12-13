/**
 * Created by Bizvane on 2016/12/13.
 */

//新增页面弹窗事件
var getNewVip={
    init:function () {
        $('#jurisdiction').on('click', '#add', function (e) {
            whir.loading.add("",0.5);//加载等待框
            $('#loading').remove();
            e.stopPropagation();
            $('#get_more').show();
            this.getMoreStore();
            this.gender();
        }.bind(this));
        $('#get_more .head_span_r').click(function () {
            $('#get_more').hide();
            whir.loading.remove();//移除加载框
        });
        $('#get_more_close').click(function () {
            $('#get_more').hide();
            whir.loading.remove();//移除加载框
        });
        $('#get_more_save').click(function () {
            this.postParma();
        }.bind(this));
    },
    getMoreStore:function (){
        whir.loading.add("",0.5);//加载等待框
        var me=this;
       $('#OWN_STORE').empty().next().remove();
      //获取所属企业列表
      var corp_command="/shop/findStore";
      oc.postRequire("get", corp_command,"", "", function(data){
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            msg=JSON.parse(msg.list);
            var corp_html='';
            for( var i=0;i<msg.length;i++){
                corp_html+='<option value="'+msg[i].store_code+'-'+msg[i].corp_code+'">'+msg[i].store_name+'</option>';
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
                    //调导购
                    me.getMoreStaff();
                }
            });
            $('.searchable-select-item').click(function(){
                $("#services").html("");
                $("input[verify='Code']").val("");
                $("#BRAND_NAME").val("");
                $("input[verify='Code']").attr("data-mark","");
                $("#BRAND_NAME").attr("data-mark","");
                //调导购
                me.getMoreStaff();
            })
            whir.loading.remove();//移除加载框
            me.getMoreStaff();
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
    });
},
    gender:function () {
    $('.searchable-select').remove();
    $('#gender').empty();
    //性别
    var corp_html='<option value="男">男</option>'+'<option value="女">女</option>';
    $("#gender").append(corp_html);
    $('#gender').searchableSelect();
    $('#gender').parent().find('.searchable-select-holder').html('男');
    $('#gender').parent().find('.searchable-select-input').remove();
},
    getMoreStaff:function(){
        whir.loading.add("",0.5);//加载等待框
        $('#OWN_SHOPPERS').empty().next().remove();
        var _param={};
        var  code=$('#OWN_STORE').val().split('-');
        console.log(code)
        _param.store_code=code[0];
        _param.corp_code=code[1];
      oc.postRequire("post","/shop/staff","", _param, function(data) {
          if(data.code=="0"){
              console.log(data)
              var msg=JSON.parse(data.message);
              console.log(msg)
              var corp_html='';
              for( var i=0;i<msg.length;i++){
                  corp_html+='<option value="'+msg[i].user_code+'">'+msg[i].user_name+'</option>';
              }
              $("#OWN_SHOPPERS").append(corp_html);
              $('#OWN_SHOPPERS').searchableSelect();
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
              whir.loading.remove();//移除加载框
          }else if(data.code=="-1"){
              art.dialog({
                  time: 1,
                  lock:true,
                  cancel: false,
                  content: data.message
              });
              whir.loading.remove();//移除加载框
          }
       })
   },
    postParma:function () {
        //获取参数
        console.log(sessionStorage.getItem('corp_code'))
    }
}
$(document).ready(function () {
    getNewVip.init();
});
