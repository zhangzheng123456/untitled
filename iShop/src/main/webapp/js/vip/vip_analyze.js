var oc = new ObjectControl();
var page=1;
var area_code;
var store_code;
var corp_code="C10000";
//????????
function GetArea(){
    var searchValue="";
    var param={};
    param['pageNumber']=page;
    param['pageSize']="7";
    param['searchValue']=searchValue;
    oc.postRequire("post","/area/findAreaByCorpCode","",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);//???messagejson?????DOM????
            var message=JSON.parse(data.message);//???messagejson?????DOM????
            var message=JSON.parse(data.message);//???messagejson?????DOM????
            var role_code=message.role_code;//???????
            var output=JSON.parse(message.list);
            var ul='';
            var first_area='';
            var first_area_code='';
            var output_list=output.list;
            console.log(output_list);
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
    //清除内容
    $('#select_analyze_shop ul').html('');
    var searchValue='';
    var area_code=a;
    var param={};
    param['pageNumber']=page;
    param['pageSize']=7;
    param['searchValue']=searchValue;
    param["area_code"]=area_code;
    oc.postRequire("post","/shop/findByAreaCode","",param,function(data){
        var ul='';
        var first_store_name='';
        var first_store_code='';
        var message=JSON.parse(data.message);//???messagejson?????DOM????
        var message=JSON.parse(data.message);//???messagejson?????DOM????
        var output=JSON.parse(message.list);
        var output_list=output.list;
        first_store_name=output_list[0].store_name;
        first_store_code=output_list[0].sstore_code;
        for(var i= 0;i<output_list.length;i++){
            ul+="<li data_store='"+output_list[i].store_code+"'>"+output_list[i].store_name+"</li>";
        }
        $('#side_analyze ul li:nth-child(3) s').html( first_store_name);
        $('#side_analyze ul li:nth-child(3) s').attr('data_store',first_store_code);
        $('#select_analyze_shop ul').append(ul);
    });
}
//点击li填充s中的数据显示
function show_name_Click(e){
    var e= e.target;
    var d=$(e).parent().parent().parent();
    console.log($(d).attr('id'));
    if($(d).attr('id')=='select_analyze'){
        var area_code=$(e).attr('data_area');
        $('#side_analyze ul li:nth-child(2) s').html($(e).html());
        $('#side_analyze ul li:nth-child(2) s').attr('data_area',area_code);
        getStore(area_code);
        $('#select_analyze').toggle();
    }else{
        var store_code=$(e).attr('data_store');
        $('#side_analyze ul li:nth-child(3) s').html($(e).html());
        $('#side_analyze ul li:nth-child(3) s').attr('data_store',store_code);
        $('#select_analyze_shop').toggle();
    }

}
//???selcet
function show_select(e){
    $(e.target).attr('class').indexOf('area')==-1?$('#select_analyze_shop').toggle(): $('#select_analyze').toggle();
    //?????????area?????????λ??
    //$(e.target).attr('class').indexOf('area')==-1? $('#select_analyze').css('top','100px'): $('#select_analyze').css('top','69px');
}
//加载更多
function getMore(e){
    var e= e.target;
    page+=1;

    //getStore(a)
    console.log(e);

}
$().ready(function(){
    GetArea();
    newVip_add();
    $('#side_analyze>ul span').click(show_select);
    //加载更多
    $('#side_analyze div s').click(getMore);
    $('#select_analyze ul').on('click','li',show_name_Click);
    $('#select_analyze_shop ul').on('click','li',show_name_Click);
});
/*****************************************************************************************************************/
//新入会员
function newVip_add(){
    var param={};
    param['pageNumber']=page;
    param['pageSize']=7;
    param['store_code']=store_code;
    param['corp_code']=corp_code;
    param["area_code"]=area_code;
    oc.postRequire("post","/vipAnalysis/vipNew","",param,function(data){
        console.log(data);
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
                msg=msg.new_vip_list;
            console.log(msg.length);
            if(msg.length<10){
                for(var i=0;i<msg.length;i++){
                    var a=i+1;
                    $(".newVip tbody").append('<tr><td>'
                        + a
                        +'</td><td>'
                        + msg[i].vip_name
                        +'</td><td>'
                        + msg[i].vip_card_type
                        +'</td><td>'
                        + msg[i].join_date
                        +'</td><td>'
                        + msg[i].vip_birthday
                        +'</td></tr>');
                }
            }else if(msg.length>=10){
                for(var i=0;i<10;i++){
                    var a=i+1;
                    $(".newVip tbody").append('<tr><td>'
                        + a
                        +'</td><td>'
                        + msg[i].vip_name
                        +'</td><td>'
                        + msg[i].vip_card_type
                        +'</td><td>'
                        + msg[i].join_date
                        +'</td><td>'
                        + msg[i].vip_birthday
                        +'</td></tr>');
                }
            }

        }else if(data.code=="-1"){
            console.log(data.message);
        }

    });
}

