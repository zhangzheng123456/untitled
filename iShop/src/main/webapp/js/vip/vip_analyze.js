var oc = new ObjectControl();
var page=1;
var area_code;
var store_code;
var corp_code="C10000";
//????????
function GetArea(){
    var searchValue=$('#select_analyze input').val();
    var param={};
    param['pageNumber']=page;
    param['pageSize']="7";
    param['searchValue']=searchValue;
    oc.postRequire("post","/area/findAreaByCorpCode","",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);//��ȡmessagejson�����DOM����
            var message=JSON.parse(data.message);//��ȡmessagejson�����DOM����
            var message=JSON.parse(data.message);//��ȡmessagejson�����DOM����
            var role_code=message.role_code;//��ɫ����
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
            localStorage.setItem('area_code',area_code);
            //清除内容店铺下拉列表
            $('#select_analyze_shop ul').html('');
            getStore(area_code);
            $('#select_analyze ul').append(ul);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    });
}
function getStore(a){
    var searchValue=$('#select_analyze_shop input').val();
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
        var message=JSON.parse(data.message);//��ȡmessagejson�����DOM����
        var message=JSON.parse(data.message);//��ȡmessagejson�����DOM����
        console.log(message);
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
function showNameClick(e){
    var e= e.target;
    var d=$(e).parent().parent().parent();
    console.log($(d).attr('id'));
    if($(d).attr('id')=='select_analyze'){
        var area_code=$(e).attr('data_area');
        $('#side_analyze ul li:nth-child(2) s').html($(e).html());
        $('#side_analyze ul li:nth-child(2) s').attr('data_area',area_code);
        //清除内容店铺下拉列表
        $('#select_analyze_shop ul').html('');
        getStore(area_code);
        $('#select_analyze').toggle();
    }else{
        var store_code=$(e).attr('data_store');
        $('#side_analyze ul li:nth-child(3) s').html($(e).html());
        $('#side_analyze ul li:nth-child(3) s').attr('data_store',store_code);
        $('#select_analyze_shop').toggle();
    }

}
// function show_select(){
//     var event=window.event||arguments[0];
//     if(event.stopPropagation){
//         event.stopPropagation();
//     }else{
//         event.cancelBubble=true;
//     }
//     console.log(this);
//     if($(this).find('b').html()=='区域'){
//         $('#select_analyze').toggle();
//         $('#select_analyze_shop').hide();
//     }else{
//         $('#select_analyze_shop').toggle()
//     }
//     // $(e.target).attr('class').indexOf('area')==-1?$('#select_analyze_shop').toggle(): $('#select_analyze').toggle();
//     //$(e.target).attr('class').indexOf('area')==-1? $('#select_analyze').css('top','100px'): $('#select_analyze').css('top','69px');
// }
//取消下拉框
$(document).on('click',function(e){
    if(e.target==$($('#side_analyze>ul')[0]).find('li:nth-child(2)')  ){
       return
    }else if(e.target==$('#select_analyze')){
       return
    }else if(e.target==$('#select_analyze div')){
       return
    }else if(e.target==$('#select_analyze div b')){
        return
    }else if(e.target==$('#select_analyze div b input')){
        return
    }else if(e.target==$('#select_analyze div b span')){
        return
    }else if(e.target==$('#select_analyze div ul')){
        return
    }else if(e.target==$('#select_analyze div ul li')){
        return
    }else if(e.target==$('#select_analyze div s')){
        return
    }else{
        $('#select_analyze').hide();
    }
    //第二个框
    if(e.target==$($('#side_analyze_shop>ul')[0]).find('li:nth-child(2)')  ){
        return
    }else if(e.target==$('#select_analyze_shop')){
        return
    }else if(e.target==$('#select_analyze_shop div')){
        return
    }else if(e.target==$('#select_analyze_shop div b')){
        return
    }else if(e.target==$('#select_analyze_shop div b input')){
        return
    }else if(e.target==$('#select_analyze_shop div b span')){
        return
    }else if(e.target==$('#select_analyze_shop div ul')){
        return
    }else if(e.target==$('#select_analyze_shop div ul li')){
        return
    }else if(e.target==$('#select_analyze_shop div s')){
        return
    }else{
        $('#select_analyze_shop').hide();
    }
});
//加载更多
function getMore(e){
    var e= e.target;
    page+=1;
    var area_code=$('#side_analyze ul li:nth-child(2) s').attr('data_area');
    console.log(area_code);
    getStore(area_code);
}
//搜索
function searchValue(e){
    //page初始化
    page=1;
    //进入搜索清空内容
    $(e.target).parent().next().html('');
    //清楚加载更多
    $(e.target).parent().next().next().attr('style','display:block');
    //获取店铺列表的value值e
    var searchValue=$(e.target).prev().val();
    //获得其父级元素的id熟属性
    var parent=$(e.target).parent().parent().parent();
    //判断是区域搜索还是店铺搜索
      if($(parent).attr('id')=='select_analyze'){
          GetArea();
      }else{
          getStore(localStorage.getItem('area_code'));
      }
}
//页面加载前加载区域
GetArea();
//绑定事件
$('#side_analyze>ul:nth-child(1) li:gt(0)').click(
    function(){
        var event=window.event||arguments[0];
        if(event.stopPropagation){
            event.stopPropagation();
        }else{
            event.cancelBubble=true;
        }
        if($(this).find('b').html()=='区域'){
            $('#select_analyze').toggle();
            $('#select_analyze_shop').hide();
        }else{
            $('#select_analyze_shop').toggle()
        }
    });
$().ready(function(){
    newVip_add();
    $('#select_analyze s').click(getMore);
    $('#select_analyze ul').on('click','li',showNameClick);
    $('#select_analyze_shop ul').on('click','li',showNameClick);
    //加载更多
    $('#side_analyze div s').click(getMore);
    //添加搜索
    $('#side_analyze div b span').click(searchValue);
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
                $(".vip_table tbody tr").click(function () {
                    vipTable_lg();
                })

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
                $(".vip_table tbody tr").click(function () {
                    vipTable_lg();
                })

            }

        }else if(data.code=="-1"){
            console.log(data.message);
        }

    });
}

