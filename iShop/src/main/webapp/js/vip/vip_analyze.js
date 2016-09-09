var oc = new ObjectControl();
var pageNumber=1;
var pageSize=10;
var searchValue='';
/*
 area/findAreaByCorpCode   post      pageNumber,pageSize,searchValue
*/
//加载区域
var param={};
param['pageNumber']=pageNumber;
param['pageSize']=pageSize;
param['searchValue']=searchValue;
console.log(param);
oc.postRequire("post","/area/findAreaByCorpCode","",param,function(data){
    console.log(data);//获取返回数据
    var message=JSON.parse(data.message);//获取messagejson对象的DOM对象
    console.log(message);
    console.log(message.list);
    var output=JSON.parse(message.list);
    console.log(output);
    console.log(output.list);//对象
    console.log(output.list[0]);
    console.log(output.list[0].area_name);
    if(data.code=="0"){
        //$(".table tbody").empty();
        //var message=JSON.parse(data.message);
        //console.log(message);
        //var list=message.all_vip_list;
        //cout=message.pages;
        ////var list=list.list;
        //superaddition(list,a);
        //jumpBianse();
        //setPage($("#foot-num")[0],cout,a,b,funcCode);
    }else if(data.code=="-1"){
        alert(data.message);
    }
});
//显示selcet
function show_select(){
    $('#select_analyze').toggle();
}