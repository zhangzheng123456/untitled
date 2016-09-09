var oc = new ObjectControl();
var pageNumber=1;
var pageSize=7;
var searchValue='';
//加载区域
function GetArea(){
    var param={};
    param['pageNumber']=pageNumber;
    param['pageSize']=pageSize;
    param['searchValue']=searchValue;
    oc.postRequire("post","/area/findAreaByCorpCode","",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);//获取messagejson对象的DOM对象
            var output=JSON.parse(message.list);
            var ul='';
            var output_list=output.list;
            output_list.length>7? $('#select_analyze s').attr('style','display:block'): $('#select_analyze s').attr('style','display:none');
            for(var i= 0;i<output_list.length;i++){
                ul+="<li>"+output_list[i].area_name+"</li>";
            }
            $('#select_analyze ul').append(ul);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    });
}
GetArea();
//绑定li的单击事件
function areaClick(e){
    var e= e.target;
    $('#side_analyze ul li:nth-child(2) s').html($(e).html());
    $('#select_analyze').toggle();
//    
}
//显示selcet
function show_select(e){
    $('#select_analyze').toggle();
    //根据是否有area控制弹出框位置
    $(e.target).attr('class').indexOf('area')==-1? $('#select_analyze').css('top','100px'): $('#select_analyze').css('top','69px');
}
$().ready(function(){
    $('#side_analyze span').click(show_select);
    $('#select_analyze ul').on('click','li',areaClick);
});