var oc = new ObjectControl();
var pageNumber=1;
var pageSize=10;
var searchValue='';
/*
 area/findAreaByCorpCode   post      pageNumber,pageSize,searchValue
*/
//��������
var param={};
param['pageNumber']=pageNumber;
param['pageSize']=pageSize;
param['searchValue']=searchValue;
console.log(param);
oc.postRequire("post","/area/findAreaByCorpCode","",param,function(data){
    console.log(data);//��ȡ��������
    var message=JSON.parse(data.message);//��ȡmessagejson�����DOM����
    console.log(message);
    console.log(message.list);
    var output=JSON.parse(message.list);
    console.log(output);
    console.log(output.list);//����
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
//��ʾselcet
function show_select(){
    $('#select_analyze').toggle();
}