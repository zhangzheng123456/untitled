var oc = new ObjectControl();
var page=1;
//加载区域
function GetArea(){
    var searchValue="";
    var param={};
    param['pageNumber']=page;
    param['pageSize']="7";
    param['searchValue']=searchValue;
    oc.postRequire("post","/area/findAreaByCorpCode","",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);//获取messagejson对象的DOM对象
            var output=JSON.parse(message.list);
            var ul='';
            var first_area='';
            var output_list=output.list;
            output_list.length>7? $('#select_analyze s').attr('style','display:block'): $('#select_analyze s').attr('style','display:none');
            first_area=output_list[0].area_name;
            for(var i= 1;i<output_list.length;i++){
                ul+="<li>"+output_list[i].area_name+"</li>";
            }
            $('#side_analyze ul li:nth-child(2) s').html(first_area);
            var area_code=output_list[0].area_code
            getStore(area_code);
            $('#select_analyze ul').append(ul);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    });
}
function getStore(a){
    var searchValue='';
    var area_code=a;
    var param={};
    param['pageNumber']=page;
    param['pageSize']=7;
    param['searchValue']=searchValue;
    param["area_code"]=area_code;
    oc.postRequire("post","/shop/findByAreaCode","",param,function(data){
        console.log(data);
        $('#select_analzye')
    });
}
//绑定li的单击事件
function areaClick(e){
    var e= e.target;
    $('#side_analyze ul li:nth-child(2) s').html($(e).html());
    $('#select_analyze').toggle();
//
}
//显示selcet
function show_select(e){
    console.log('OK');
    $(e.target).attr('class').indexOf('area')==-1?$('#select_analyze_shop').toggle(): $('#select_analyze').toggle();
    //根据是否有area控制弹出框位置
    //$(e.target).attr('class').indexOf('area')==-1? $('#select_analyze').css('top','100px'): $('#select_analyze').css('top','69px');
}
$().ready(function(){
    GetArea();
    $('#side_analyze span').click(show_select);
    $('#select_analyze ul').on('click','li',areaClick);
});