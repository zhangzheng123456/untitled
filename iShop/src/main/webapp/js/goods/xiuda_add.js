/**
 * Created by Administrator on 2017/1/16.
 */
var oc = new ObjectControl();
//    var corp_code =GetRequest().corp_code;
var num=1;
var next=false;

//�����Ϊ�ռ��
function checkTemp(){
    $('.the_listTitle').find('input').blur(function(){
        if($(this).val()==''){
            $(this).next('.hint').show();
        }else{
            $(this).next('.hint').hide();
        }
    });
}

//��Ʒ����
$('#add_one').click(function () {
    console.log('show');
    //������ƥ����Ʒ����
    $("#search_match_goods").show();
    if(num>1){
        return;
    }else {
        num=1;
        next=false;
        jQuery('#search_match_goods ul').empty();
        getmatchgoodsList(num);;
    }
});
//�رյ���
$('#close_match_goods').click(function () {
    $("#search_match_goods").hide();
})
//���������Ʒ
$("#d_search").click(function () {
    jQuery('#search_match_goods ul').empty();
    num=1;
    getmatchgoodsList(num);
});
$("#search_match_goods").keydown(function () {
    var event=window.event||arguments[0];
    if(event.keyCode == 13){
        jQuery('#search_match_goods ul').empty();
        num=1;
        getmatchgoodsList(num);
    }
})
function public_click(a) {
    if ($(a).prev().attr('checked')) {
        $(a).prev().removeAttr('checked');
        $(a).parent().addClass("checkbox_isactive");
        $(a).addClass('check_public');
    } else {
        $(a).prev()[0].checked = 'true';
        $(a).parent().addClass("checkbox_isactive");
        $(a).prev().attr('checked', 'true')
    }
}
function stopBubble(e) {
    if (e && e.stopPropagation) {//��IE�����
        e.stopPropagation();
    }
    else {//IE�����
        window.event.cancelBubble = true;
    }
}
function getmatchgoodsList(a) {
    //��ȡ�����Ʒ�����б�
    var param={};
    var search_value=$("#search").val();
    var goods_code=''; //������ʱ��goods_code��''���༭��goods_code������Զ��ŷָ�
    var pageNumber=a;
    var pageSize=20;
    var corp_code = '';
    param["goods_code"]=goods_code;
    param["pageNumber"] =pageNumber;
    param["pageSize"] =pageSize;
    param["search_value"]=search_value;
    param["corp_code"]=corp_code;
    whir.loading.add("",0.5);//���صȴ���
    oc.postRequire("post", "/goods/getMatchFab","",param, function(data){
        if(data.code=="0"){
            console.log(data);
            var msg=JSON.parse(data.message);
            var list=JSON.parse(msg.list);
            var hasNextPage=list.hasNextPage;
            var list=list.list;
            console.log(list);
            if(list.length<=0){
                jQuery('#search_match_goods ul').append("<p>û�������Ʒ��</p>");
            }else{
                var len = $(".conpany_msg li").length;
                for(var i=0;i<list.length;i++){
                    var imgUrl="";
                    if(list[i].goods_image.indexOf("http")!==-1){
                        imgUrl = list[i].goods_image;
                    }
                    if(list[i].goods_image.indexOf("http")==-1){
                        imgUrl="../img/goods_default_image.png";
                    }
                    jQuery('#search_match_goods ul').append('<li><img class="goodsImg" src="'
                        + imgUrl
                        + '"><span class="goods_code">'
                        + list[i].goods_code + '</span><span>'
                        + list[i].goods_name + '</span><span class="goods_add">'
                        +'+</span><i class="icon-ishop_6-12"></i></li>');
                    if(len>0){
                        for(var j=0;j<len;j++){
                            var code = $($(".conpany_msg li")[j]).find(".goods_code").html();
                            if(code == list[i].goods_code){
                                $($("#search_match_goods ul li")[i]).find("i").show();
                                $($("#search_match_goods ul li")[i]).find(".goods_add").hide();
                            }
                        }
                    }
                }
            }
            if(hasNextPage==true){
                num++;
                a++;
                next=false;
            }
            if(hasNextPage==false){
                next=true;
            }
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
        whir.loading.remove();//�Ƴ����ؿ�
        // goodsAddHide();
    });
}
$("#search_match_goods ul").scroll(function () {
    var nScrollHight = $(this)[0].scrollHeight;
    var nScrollTop = $(this)[0].scrollTop;
    var nDivHight=$(this).height();

    if(nScrollTop + nDivHight >= nScrollHight){
        if(next){
            return;
        }
        getmatchgoodsList(num);
    };
})
//******************��װ���******************
//��ȡ����׺
function GetRequest() {
    var url = decodeURI(location.search); //��ȡurl��"?"������ִ�
    var theRequest = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.substr(1);
        strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            theRequest[strs[i].split("=")[0]] = unescape(strs[i].split("=")[1]);
        }
    }
    return theRequest;
}
window.onload = function () {
    checkTemp();
//        //����Ч��ͼ
//        $('#add_all').click(function () {
//
//        });
}
