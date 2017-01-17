/**
 * Created by Bizvane on 2016/12/13.
 */

//新增页面弹窗事件
var getNewVip={
    init:function () {
        $('#jurisdiction').on('click', '#add', function (e) {
            whir.loading.add("mask",0.5);//加载等待框
            // $('#loading').remove();
            e.stopPropagation();
            $('#get_more').show();
            this.getMoreStore();
            this.getparamCtrol();
        }.bind(this));
        $('#get_more .head_span_r').click(function () {
            $('#get_more').hide();
            whir.loading.remove('mask');//移除加载框
        });
        $('#get_more_close').click(function () {
            $('#get_more').hide();
            whir.loading.remove('mask');//移除加载框
        });
        $('#get_more_save').click(function () {
            if($('#content .vipName').val().trim()==''){$('#content .vipName').trigger('blur');return}
            if($('#content .birthday').val().trim()==''){$('#content .birthday').trigger('blur');return}
            if($('#content .cardNo').val().trim()==''){$('#content .cardNo').trigger('blur');return}
            if($('#content .phone').val().trim()==''){
                $('#content .phone').trigger('blur');
                return
            }else{
                if($('#test_2').find('.hint_l').length==1)return;
            }
            this.postParma();
        }.bind(this));
        var me=this;
        $('#content').on('blur','input',function () {
            var html=''
            if(this.className=='vipName'){
                if($(this).val().trim()==''){
                    if($('#test_1').find('.hint_l').length==1)return;
                    $('#test_1').show();
                html='<span class="hint hint_l" style="display: inline-block;width: 25%;">姓名不能为空</span>';
                    $('#test_1').append(html);
                }else{
                    $('#test_1').find('.hint_l').remove();
                }
            }else if(this.className.search('birthday')!=-1){
                if($(this).val().trim()==''){
                    if($('#test_1').find('.hint_r').length==1)return;
                    $('#test_1').show();
                    html='<span class="hint hint_r" style="float:right;width: 25%;">生日不能为空</span>';
                    $('#test_1').append(html);
                }else{
                    $('#test_1').find('.hint_r').remove();
                }
            }else if(this.className=='phone'){
                if($(this).val().trim()==''){
                    if($('#test_2').find('.hint_l').length==1)return;
                    $('#test_2').show();
                    html='<span class="hint hint_l" style="display: inline-block;width: 30%;">手机号不能为空</span>';
                    $('#test_2').html(html);
                }else{
                    var reg = /^1[3|4|5|7|8][0-9]{9}$/; //验证规则
                    console.log();
                    if(!reg.test($(this).val().trim())){
                        html='<span class="hint hint_l" style="display: inline-block;width: 30%;">手机号格式不正确</span>';
                        $('#test_2').html(html);
                        return
                    }
                    $('#test_2').find('.hint_l').remove();
                }
            }
        });
        //失去焦点
        $('.billNo').focus(function () {
            $('.down_icon_vip').trigger('click');
        });
        $('.down_icon_vip').click(function () {
            $('#billNo_drop_down').toggle();
            $("#billNo_drop_down").niceScroll({
                cursorborder:"0 none",cursoropacitymin:"0",boxzoom:false,
                cursorcolor:" #dfdfdf",
                cursoropacitymax:1,
                touchbehavior:false,
                cursorminheight:50,

                cursorwidth:"5px",
                cursorborderradius:"10px"});
        });
        $('#billNo_drop_down').on('click','li',function () {
            $(this).addClass('selected').siblings('.selected').removeClass('selected');
            $('.billNo').val(this.innerHTML);
            $('#billNo_drop_down').toggle();
        });
    },
    getBillNo:function () {
        var param={};
        param.store_code=String($('#OWN_STORE').val()).search('null')!=-1?'':$('#OWN_STORE').val().split('-')[0];
        param.corp_code='C10000';
        oc.postRequire("post","/vip/dayNoVipBill","", param, function(data) {
            console.log(data.message)
        });
    },
    getparamCtrol:function () {
        oc.postRequire("get","/vip/paramController?corp_code="+sessionStorage.getItem('corp_code'),"", '', function(data) {
            if(data.code==0){
                //控制两个节点
                var  obj=JSON.parse(data.message);
                for(var key in obj){
                    console.log(key+':'+obj[key])
                    if(key=='is_show_cardNo'){
                       if(obj[key]=='Y'){
                           $('#content .cardNo').val('').removeAttr('readonly');
                       }else{
                           $('#content .cardNo').val('由系统自动生成').attr('readonly');
                       }
                    }else if(key=='is_show_billNo'){
                       if(obj[key]=='N'){
                           // $('#content .billNo').hide();
                           $('#content li:first-child').hide();
                       }else{
                           $('#content li:first-child').show();
                       }
                    }
                }
            }else if(data.code==-1){
                art.dialog({
                    time: 1,
                    lock:true,
                    cancel: false,
                    content: data.message
                });

            }
        })
    },
    getMoreStore:function (){
        whir.loading.add("",0.5);//加载等待框
        var me=this;
       $('#OWN_STORE').empty().next().remove();
      //获取所属企业列表
      var corp_command="/shop/findStore";
      oc.postRequire("get", corp_command,"", "", function(data){
        if(data.code=="0"){
            console.log(data);
            var msg=JSON.parse(data.message);
            msg=JSON.parse(msg.list);
            console.log(msg);
            var corp_html='';
            for( var i=0;i<msg.length;i++){
                corp_html+='<option value="'+msg[i].store_code+'-'+msg[i].corp_code+'">'+msg[i].store_name+'</option>';
            }
            $("#OWN_STORE").append(corp_html);
            $('#OWN_STORE').searchableSelect();
            // $('#OWN_STORE').parent().find('.searchable-select-input')
            $('#get_more_store .corp_select .searchable-select-input').keydown(function(event){
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
            $('#get_more_store .searchable-select-item').click(function(){
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
    var corp_htm2='<option value="东鹏玖姿贵宾卡">东鹏玖姿贵宾卡</option>'
        +'<option value="玖姿贵宾卡">玖姿贵宾卡</option>'
        +'<option value="南阳贵宾卡">南阳贵宾卡</option>'
        +'<option value="玖姿累积卡">玖姿累积卡</option>';
        $("#vipCardType").append(corp_htm2);
        $('#vipCardType').searchableSelect();
        $('#vipCardType').parent().find('.searchable-select-holder').html('东鹏玖姿贵宾卡');
        $('#vipCardType').parent().find('.searchable-select-input').remove();
    },
    getMoreStaff:function(){
        whir.loading.add("",0.5);//加载等待框
        $('#OWN_SHOPPERS').empty().next().remove();
        var _param={};
        var  code=$('#OWN_STORE').val().split('-');
        _param.store_code=code[0];
        _param.corp_code=code[1];
      oc.postRequire("post","/shop/staff","", _param, function(data) {
          if(data.code=="0"){
              var msg=JSON.parse(data.message);
              var corp_html='';
              for( var i=0;i<msg.length;i++){
                  corp_html+='<option value="'+msg[i].user_code+'-'+msg[i].user_name+'">'+msg[i].user_name+'</option>';
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
        var param={};
        param.corp_code='C10000';
        console.log($('#OWN_SHOPPERS').val());
        param.phone=$('#content').find('.phone').val();
        param.card_no=$('#content').find('.cardNo').val();
        param.vip_name=$('#content').find('.vipName').val();
        param.billNo=$('#content').find('.billNo').val();
        param.vip_card_type=$('#vipCardType').val();
        param.birthday=$('#content').find('.birthday').val();
        param.sex=$('#content').find('.gender').val();
        param.user_code=String($('#OWN_SHOPPERS').val()).search('null')!=-1?'':$('#OWN_SHOPPERS').val().split('-')[0];
        param.user_name=String($('#OWN_SHOPPERS').val()).search('null')!=-1?'':$('#OWN_SHOPPERS').val().split('-')[1];
        param.store_code=String($('#OWN_STORE').val()).search('null')!=-1?'':$('#OWN_STORE').val().split('-')[0];
        oc.postRequire("post","/vip/addVip","", param, function(data) {
            if(data.code==0){
                art.dialog({
                    zIndex:10003,
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "新增成功"
                });
            }else if(data.code==-1){
                art.dialog({
                    zIndex:10003,
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: "新增失败"
                });
            }
        })
    },
    testBlur:function () {
        if($('#content').find('.billNo').val()=='')return;
        var param={};
        param.corp_code='C10000';
        param.type='billNo';
        param.billNo=$('#content').find('.billNo').val();
        oc.postRequire("post","/vip/checkBillNo","", param, function(data) {
            if(data.code==0){
                var obj=JSON.parse(data.message);
                obj.can_pass=='N'?$('#content .content_hint').show():$('#content .content_hint').hide();
            }else if(data.code==-1){
                art.dialog({
                    zIndex:10003,
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        })
    },
    testInput:function (node) {
        if($(node).val().trim()==''){
            var reg = /^1[3|4|5|7|8][0-9]{9}$/; //验证规则
        }
        return;
        //不为空验证
        var html='';
        if($(node).val().trim()==''){
            html=$(node).parent().prev().html().slice(0,-1);
            //left
            var HTML='<li class="hint_li"><div class="content_hint"style="display: block;overflow: hidden"><div style="width:50%;float: left"><span class="hint">'+html+'不能为空</span></div></div></li>';
            //right
            var HTML='<li class="hint_li"><div class="content_hint"style="display: block;overflow: hidden"><div style="width:50%;float:right"><span class="hint">'+html+'不能为空</span></div></div></li>';
            var nd=$(node).parent().parent().next()[0];
            if(!nd.className){
                $(node).parent().parent().after(HTML);
            }
        }else{
            $(node).parent().parent().next('.hint_li').remove();
        }
    }
}
function checkStart(data) {
    getNewVip.testInput($('#content .birthday')[0]);
    $('#test_1').find('.hint_r').hide();
}
function scroll() {
    console.log('触发');
}
$(document).ready(function () {
    getNewVip.init();
    $('#content').scroll(function () {
        laydate.reset();
    })
});
