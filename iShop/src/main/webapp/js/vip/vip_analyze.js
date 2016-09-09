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
            var first_area_code='';
            var output_list=output.list;
            output_list.length>7? $('#select_analyze s').attr('style','display:block'): $('#select_analyze s').attr('style','display:none');
            first_area=output_list[0].area_name;
            first_area_code=output_list[0].area_code;
            for(var i= 0;i<output_list.length;i++){
                ul+="<li data_area='"+output_list[i].area_code+"'>"+output_list[i].area_name+"</li>";
            }
            $('#side_analyze ul li:nth-child(2) s').html(first_area);
            $('#side_analyze ul li:nth-child(2) s').attr('data_area',first_area_code);
            var area_code=output_list[0].area_code;
            console.log(area_code);
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
        var ul='';
        var first_corp_name='';
        var first_corp_code='';
        var message=JSON.parse(data.message);//获取messagejson对象的DOM对象
        var output=JSON.parse(message.list);
        var output_list=output.list;
        first_corp_name=output_list[0].corp.corp_name;
        first_corp_code=output_list[0].corp.corp_code;
        for(var i= 0;i<output_list.length;i++){
            console.log(output_list[i].corp);
            ul+="<li data_corp='"+output_list[i].corp.corp_code+"'>"+output_list[i].corp.corp_name+"</li>";
        }
        $('#side_analyze ul li:nth-child(3) s').html( first_corp_name);
        $('#side_analyze ul li:nth-child(3) s').attr('data_corp',first_corp_code);
        $('#select_analyze_shop ul').append(ul);
    });
}
//绑定li的单击事件
function areaClick(e){
    var e= e.target;
    var d=$(e).parent().parent().parent();
    console.log($(d).attr('id'));
    if($(d).attr('id')=='select_analyze'){console.log('OK')}
    var area_code=$(e).attr('data_area');
    $('#side_analyze ul li:nth-child(2) s').html($(e).html());
    $('#side_analyze ul li:nth-child(2) s').attr('data_area','area_code');
    getStore(area_code);
    $('#select_analyze').toggle();
//
}
//显示selcet
function show_select(e){
    $(e.target).attr('class').indexOf('area')==-1?$('#select_analyze_shop').toggle(): $('#select_analyze').toggle();
    //根据是否有area控制弹出框位置
    //$(e.target).attr('class').indexOf('area')==-1? $('#select_analyze').css('top','100px'): $('#select_analyze').css('top','69px');
}
$().ready(function(){
    GetArea();
    $('#side_analyze span').click(show_select);
    $('#select_analyze ul').on('click','li',areaClick);
    $('#select_analyze_shop ul').on('click','li',areaClick);
});