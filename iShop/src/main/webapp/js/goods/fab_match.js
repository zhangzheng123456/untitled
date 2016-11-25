/**
 * Created by Administrator on 2016/11/25.
 */
var oc = new ObjectControl();
var left=($(window).width()-$("#tk").width())/2;//弹框定位的left值
var tp=($(window).height()-$("#tk").height())/2;//弹框定位的top值
var inx=1;//默认是第一页
var pageNumber=1;//删除的默认的第一页;
var pageSize=10;//默认传的每页多少行
var value="";//收索的关键词
var param={};//定义的对象
var _param={};//筛选定义的内容
var list="";
var cout="";
var filtrate="";//筛选的定义的值
var key_val=sessionStorage.getItem("key_val");//取页面的function_code
key_val=JSON.parse(key_val);
var funcCode=key_val.func_code;
/*
 抛开瀑布流布局各种乱七八糟的算法，基于masonry的瀑布流，很是简单的，而且通过扩展animate,能实现瀑布流布局的晃动、弹球等效果。
 masonry还有很多参数我这里注解了常用的参数
 */
$(function(){
    /*瀑布流开始*/
    var container = $('.waterfull ul');
    var loading=$('#imloading');
    // 初始化loading状态
    loading.data("on",false);
    /*判断瀑布流最大布局宽度，最大为1280*/
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
            isFitWidth: false,//是否根据浏览器窗口大小自动适应默认false
            isAnimated: false,//是否采用jquery动画进行重拍版
            isRTL:false,//设置布局的排列方式，即：定位砖块时，是从左向右排列还是从右向左排列。默认值为false，即从左向右
            isResizable: true,//是否自动布局默认true
            animationOptions: {
                duration: 800,
                easing: 'easeInOutBack',//如果你引用了jQeasing这里就可以添加对应的动态动画效果，如果没引用删除这行，默认是匀速变化
                queue: false//是否队列，从一点填充瀑布流
            }
        });
    });

    function loadImage(url) {
        var img = new Image();
        //创建一个Image对象，实现图片的预下载
        img.src = url;
        if (img.complete) {
            return img.src;
        }
        img.onload = function () {
            return img.src;
        };
    };
    loadImage('images/one.jpeg');
//        /*item hover效果*/
//        var rbgB=['#71D3F5','#F0C179','#F28386','#8BD38B'];
//        $('#waterfull').on('mouseover','.item',function(){
//            var random=Math.floor(Math.random() * 4);
//            $(this).stop(true).animate({'backgroundColor':rbgB[random]},1000);
//        });
//        $('#waterfull').on('mouseout','.item',function(){
//            $(this).stop(true).animate({'backgroundColor':'#fff'},1000);
//        });
});
//权限配置
function jurisdiction(actions){
    $('#jurisdiction').empty();
    for(var i=0;i<actions.length;i++){
        if(actions[i].act_name=="add"){
            $('#jurisdiction').append("<li id='add'><a href='javascript:void(0);'><span class='icon-ishop_6-01'></span>新增</a></li>");
        }else if(actions[i].act_name=="delete"){
            $('#jurisdiction').append("<li id='remove'><a href='javascript:void(0);'><span class='icon-ishop_6-02'></span>删除</a></li>");
        }else if(actions[i].act_name=="edit"){
            $('#jurisdiction').append("<li id='compile' class='bg'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>编辑</a></li>");
        }
    }
}
//页面加载调权限接口
function qjia(){
    console.log('权限接口调用');
    var param={};
    param["funcCode"]='';
    oc.postRequire("post","/list/action","0",param,function(data){
        var message=JSON.parse(data.message);
        var actions=message.actions;
        jurisdiction(actions);
        jumpBianse();
    })
}
qjia();
//加载完成以后页面进行的操作
function jumpBianse(){
    $(document).ready(function(){//隔行变色
        $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
        $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
    })
    //点击tr input是选择状态  tr增加class属性
    $(".table tbody tr").click(function(){
        var input=$(this).find("input")[0];
        var thinput=$("thead input")[0];
        $(this).toggleClass("tr");
        if(input.type=="checkbox"&&input.name=="test"&&input.checked==false){
            input.checked = true;
            $(this).addClass("tr");
        }else if(input.type=="checkbox"&&input.name=="test"&&input.checked==true){
            if(thinput.type=="checkbox"&&input.name=="test"&&input.checked==true){
                thinput.checked=false;
            }
            input.checked = false;
            $(this).removeClass("tr");
        }
    })
    //点击新增时页面进行的跳转
    $('#add').click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/staff/checkin_add.html");
    })
    //删除
    $("#remove").click(function(){
        var l=$(window).width();
        var h=$(document.body).height();
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==0){
            frame();
            $('.frame').html("请先选择");
            return;
        }
        $("#p").show();
        $("#tk").show();
        $("#p").css({"width":+l+"px","height":+h+"px"});
        $("#tk").css({"left":+left+"px","top":+tp+"px"});
    })
}
//新增
function getAdd(){
    var _params= {
        "id":"0",
        'goods_code':'',
        "message":""
    };
    $.ajax({
        url: '/defmatch/addMatch',
        type: 'post',
        dataType: "JSON",
        data:{
            param:JSON.stringify(_params)
        },
        success: function (data) {
            console.log('新增接口调用成功'+JSON.stringify(data));

            //pageVal();
        },
        error: function (data) {
            console.log('新增接口调用失败')
        }
    });
}
//获取数据
function getVal(){
    var _params= {
        "id":"0",
        "message":""
    };
    $.ajax({
        url: '/defmatch/list',
        type: 'get',
        dataType: "JSON",
        data:{
            param:JSON.stringify(_params)
        },
        success: function (data) {
            console.log('获取数据成功'+JSON.stringify(data));

            //pageVal();
        },
        error: function (data) {
            console.log('获取数据失败')
        }
    });
}
//数据模板
function pageVal(){
    //盒子上部分+复选框
    var tempHTML1='<li class="item"><div class="boxArea"><input type="checkbox"/>';
    //内容（图片+文字）迭代生成
    var tempHTML2='<div class="oneArea"> <img src="" alt=""/> <div>AJEL059598</div> </div>';
    //盒子下部分
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
    getAdd();
}