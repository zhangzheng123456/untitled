/**
 * Created by Administrator on 2016/11/25.
 */
/*
 �׿��ٲ������ָ������߰�����㷨������masonry���ٲ��������Ǽ򵥵ģ�����ͨ����չanimate,��ʵ���ٲ������ֵĻζ��������Ч����
 masonry���кܶ����������ע���˳��õĲ���
 */
$(function(){
    /*�ٲ�����ʼ*/
    var container = $('.waterfull ul');
    var loading=$('#imloading');
    // ��ʼ��loading״̬
    loading.data("on",false);
    /*�ж��ٲ�����󲼾ֿ�ȣ����Ϊ1280*/
    function tores(){
        var tmpWid=$(window).width();
        if(tmpWid>1280){
            tmpWid=1280;
        }else{
            var column=Math.floor(tmpWid/340);
            tmpWid=column*340;
        }
        $('.waterfull').width(tmpWid);
    }
    tores();
    $(window).resize(function(){
        tores();
    });
    container.imagesLoaded(function(){
        container.masonry({
            columnWidth: 340,
            itemSelector : '.item',
            isFitWidth: false,//�Ƿ������������ڴ�С�Զ���ӦĬ��false
            isAnimated: false,//�Ƿ����jquery�����������İ�
            isRTL:false,//���ò��ֵ����з�ʽ��������λש��ʱ���Ǵ����������л��Ǵ����������С�Ĭ��ֵΪfalse������������
            isResizable: true,//�Ƿ��Զ�����Ĭ��true
            animationOptions: {
                duration: 800,
                easing: 'easeInOutBack',//�����������jQeasing����Ϳ�����Ӷ�Ӧ�Ķ�̬����Ч�������û����ɾ�����У�Ĭ�������ٱ仯
                queue: false//�Ƿ���У���һ������ٲ���
            }
        });
    });

    function loadImage(url) {
        var img = new Image();
        //����һ��Image����ʵ��ͼƬ��Ԥ����
        img.src = url;
        if (img.complete) {
            return img.src;
        }
        img.onload = function () {
            return img.src;
        };
    };
    loadImage('images/one.jpeg');
//        /*item hoverЧ��*/
//        var rbgB=['#71D3F5','#F0C179','#F28386','#8BD38B'];
//        $('#waterfull').on('mouseover','.item',function(){
//            var random=Math.floor(Math.random() * 4);
//            $(this).stop(true).animate({'backgroundColor':rbgB[random]},1000);
//        });
//        $('#waterfull').on('mouseout','.item',function(){
//            $(this).stop(true).animate({'backgroundColor':'#fff'},1000);
//        });
});


//��ȡ����
function getVal(){
    var _params={
        //a:a
    };
    $.ajax({
        url: url,
        type: type,
        dataType: "JSON",
        data:{
            param:JSON.stringify(_params)
        },
        success: function (data) {
            console.log('��ȡ���ݳɹ�')
            pageVal();
        },
        error: function (data) {
            console.log('��ȡ����ʧ��')
        }
    });
}
//����ģ��
function pageVal(){
    //�����ϲ���+��ѡ��
    var tempHTML1='<li class="item"><div class="boxArea"><input type="checkbox"/>';
    //���ݣ�ͼƬ+���֣���������
    var tempHTML2='<div class="oneArea"> <img src="" alt=""/> <div>AJEL059598</div> </div>';
    //�����²���
    var tempHTML3='</div></li>';
    var html = '';
    var html2 = '';
    for(i=0;i<12;i++) {
        var nowHTML1 = tempHTML1;
        var k = Math.floor(Math.random()*4);
        for(a=0;a<k;a++) {
            var nowHTML2 = tempHTML2;
            html2 += nowHTML2;
        }
        var nowHTML3 = tempHTML3;
        //nowHTML1 = nowHTML1.replace("${value}", value);
        html += nowHTML1;
        html += html2;
        html += nowHTML3;
        $(".waterfull ul").append(html);
    }
}
window.onload =function() {
    getVal();
}