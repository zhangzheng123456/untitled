/**
 * Created by huxue on 2016/12/28.
 */
//    ѡ�
$('.main_select div').click(function () {
    $('.main_select div').css('background-color','#ededed');
    $(this).css('background-color','white');
})
$('.main_select div').eq(0).click(function () {
    $('.main_content').eq(0).css('display','block');
    $('.main_content').eq(1).css('display','none');
});
$('.main_select div').eq(1).click(function () {
    $('.main_content').eq(1).css('display','block');
    $('.main_content').eq(0).css('display','none');
});
//    �ײ�
$('.bottom div img').click(function () {
    var src = $(this).attr("src");
    //    ����
    if(src =='image/icon_����@2x.png'){
        $(this).attr('src','image/icon_����_�ѵ���@2x.png');
        var val = $(this).next('.num').text();
        console.log(val);
        $(this).next('.num').text(parseInt(val)+1);
    }else if (src =='image/icon_����_�ѵ���@2x.png'){
        $(this).attr('src','image/icon_����@2x.png');
        var val = $(this).next('.num').text();
        $(this).next('.num').text(parseInt(val)-1);
    }
    //    ����
    if(src =='image/icon_����@2x.png'){
        alert('���޸ù���')
    }
    //    �ղ�
    if(src =='image/icon_�ղ�@2x.png'){
        $(this).attr('src','image/icon_�ղ�_���ղ�@2x.png');
        var val = $(this).next('.num').text();
        $(this).next('.num').text(parseInt(val)+1);
    }else if (src =='image/icon_�ղ�_���ղ�@2x.png'){
        $(this).attr('src','image/icon_�ղ�@2x.png');
        var val = $(this).next('.num').text();
        $(this).next('.num').text(parseInt(val)-1);
    }
});
window.onload = function () {
    setInterval(function () {
        var heightVal = document.body.clientWidth;
        $('.main_img').css('height',heightVal);
        $('.main_img img').css('height',heightVal);
        var imgHeight = $('.main_list_main img').height();
        $('.main_list_main img').css('width',imgHeight);
    },1);
    $('.main_select div').eq(0).click();
}