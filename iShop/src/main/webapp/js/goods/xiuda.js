/**
 * Created by Administrator on 2017/1/19.
 */
var oc = new ObjectControl();
var left=($(window).width()-$("#tk").width())/2;//����λ��leftֵ
var tp=($(window).height()-$("#tk").height())/2;//����λ��topֵ
var inx=1;//Ĭ���ǵ�һҳ
var pageNumber=1;//ɾ����Ĭ�ϵĵ�һҳ;
var pageSize=10;//Ĭ�ϴ���ÿҳ������
var value="";//�����Ĺؼ���
var param={};//����Ķ���
var _param={};//ɸѡ���������
var list="";
var cout="";
var filtrate="";//ɸѡ�Ķ����ֵ
var titleArray=[];
var key_val=sessionStorage.getItem("key_val");//ȡҳ���function_code
key_val=JSON.parse(key_val);//ȡkey_val��ֵ
var funcCode=key_val.func_code;
var return_jump=sessionStorage.getItem("return_jump");//��ȡ��ҳ���״̬
return_jump=JSON.parse(return_jump);
//ģ��select
$(function(){
        $("#page_row").click(function(){
            if("block" == $("#liebiao").css("display")){
                hideLi();
            }else{
                showLi();
            }
        });
        $("#liebiao li").each(function(i,v){
            $(this).click(function(){
                pageSize=$(this).attr('id');
                if(value==""&&filtrate==""){
                    inx=1;
                    param["pageNumber"]=inx;
                    param["pageSize"]=pageSize;
                    param["searchValue"]="";
                    GET(inx,pageSize);
                }else if(value!==""){
                    inx=1;
                    param["pageSize"]=pageSize;
                    param["pageNumber"]=inx;
                    POST(inx,pageSize);
                }else if(filtrate!==""){
                    inx=1;
                    _param["pageSize"]=pageSize;
                    _param["pageNumber"]=inx;
                    filtrates(inx,pageSize);
                }
                $("#page_row").val($(this).html());
                hideLi();
            });
        });
        $("#page_row").blur(function(){
            setTimeout(hideLi,200);
        });
    }
);
function showLi(){
    $("#liebiao").show();
}
function hideLi(){
    $("#liebiao").hide();
}
$("#filtrate").click(function(){//���ɸѡ�򵯳�������
    $(".sxk").slideToggle();
    $('.file').hide();
    $(".into_frame").hide();
})
$("#pack_up").click(function(){//����ջ� ȡ��������
    $(".sxk").slideUp();
})
//������  ���input��valueֵ
$("#empty").click(function(){
    var input=$(".inputs input");
    for(var i=0;i<input.length;i++){
        input[i].value="";
        $(input[i]).attr("data-code","");
    }
    value="";
    filtrate="";
    $('#search').val("");
    $(".table p").remove();
    inx=1;
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["searchValue"]="";
    GET(inx,pageSize);
})

function setPage(container, count, pageindex,pageSize,funcCode) {
    count==0?count=1:'';
    var container = container;
    var count = count;
    var pageindex = pageindex;
    var pageSize=pageSize;
    var a = [];
    //��ҳ������10 ȫ����ʾ,����10 ��ʾǰ3 ��3 �м�3 ����....
    if (pageindex == 1) {
        a[a.length] = "<li><span class=\"icon-ishop_4-01 unclick\"></span></li>";
    } else {
        a[a.length] = "<li><span class=\"icon-ishop_4-01\"></span></li>";
    }
    function setPageList() {
        if (pageindex == i) {
            a[a.length] = "<li><span class=\"p-bg\">" + i + "</span></li>";
        } else {
            a[a.length] = "<li><span>" + i + "</span></li>";
        }
    }
    //��ҳ��С��10
    if (count <= 10) {
        for (var i = 1; i <= count; i++) {
            setPageList();
        }
    }
    //��ҳ������10ҳ
    else {
        if (pageindex <= 4) {
            for (var i = 1; i <= 5; i++) {
                setPageList();
            }
            a[a.length] = "...<li><span>" + count + "</span></li>";
        }else if (pageindex >= count - 3) {
            a[a.length] = "<li><span>1</span></li>...";
            for (var i = count - 4; i <= count; i++) {
                setPageList();
            }
        }
        else { //��ǰҳ���м䲿��
            a[a.length] = "<li><span>1</span></li>...";
            for (var i = pageindex - 2; i <= pageindex + 2; i++) {
                setPageList();
            }
            a[a.length] = "...<li><span>" + count + "</span></li>";
        }
    }
    if (pageindex == count) {
        a[a.length] = "<li><span class=\"icon-ishop_4-02 unclick\"></span></li>";
    }else{
        a[a.length] = "<li><span class=\"icon-ishop_4-02\"></span></li>";
    }
    container.innerHTML = a.join("");
    var pageClick = function() {
        var oAlink = container.getElementsByTagName("span");
        inx = pageindex; //��ʼ��ҳ��
        $("#input-txt").val(inx);
        $(".foot-sum .zy").html("�� "+count+"ҳ");
        oAlink[0].onclick = function() { //�����һҳ
            if (inx == 1) {
                return false;
            }
            inx--;
            dian(inx,pageSize);
            // setPage(container, count, inx,pageSize,funcCode,value,filtrate);
            return false;
        }
        for (var i = 1; i < oAlink.length - 1; i++) { //���ҳ��
            oAlink[i].onclick = function() {
                inx = parseInt(this.innerHTML);
                dian(inx,pageSize);
                // setPage(container, count, inx,pageSize,funcCode,value,filtrate);
                return false;
            }
        }
        oAlink[oAlink.length - 1].onclick = function() { //�����һҳ
            if (inx == count) {
                return false;
            }
            inx++;
            dian(inx,pageSize);
            // setPage(container, count, inx,pageSize,funcCode,value,filtrate);
            return false;
        }
    }()
}
function dian(a,b){//�����ҳ��ʱ���ʲô�ӿ�
    if (value==""&&filtrate=="") {
        param["pageNumber"]=inx;
        param["pageSize"]=pageSize;
        param["searchValue"]="";
        GET(a,b);
    }else if (value!==""){
        param["pageNumber"] = a;
        param["pageSize"] = b;
        POST(a,b);
    }else if (filtrate!=="") {
        _param["pageNumber"] = a;
        _param["pageSize"] = b;
        filtrates(a,b);
    }
}
function superaddition(data,num){//ҳ�����ѭ��
    // if(data.length>=1&&num>1&&num==cout){
    //     pageNumber=num-1;
    // }else{
    //     pageNumber=num;
    // }
    pageNumber=num;
    if(data.length == 0){
        var len = $(".table thead tr th").length;
        var i;
        for(i=0;i<10;i++){
            $(".table tbody").append("<tr></tr>");
            for(var j=0;j<len;j++){
                $($(".table tbody tr")[i]).append("<td></td>");
            }
        }
        $(".table tbody tr:nth-child(5)").append("<span style='position:absolute;left:54%;font-size: 15px;color:#999'>��������</span>");
    }
    for (var i = 0; i < data.length; i++) {
        var TD="";
        if(num>=2){
            var a=i+1+(num-1)*pageSize;
        }else{
            var a=i+1;
        }
        for (var c=0;c<titleArray.length;c++){
            (function(j){
                var code=titleArray[j].column_name;
                TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
            })(c)
        }
        $(".table tbody").append("<tr id='"+data[i].id+"''><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='ȫѡ/ȡ��' class='check'  id='checkboxTwoInput"
            + i
            + 1
            + "'/><label for='checkboxTwoInput"
            + i
            + 1
            + "'></label></div>"
            + "</td><td style='text-align:left;'>"
            + a
            + "</td>" +
            TD+
            "</tr>");
    }
    $(".th th:first-child input").removeAttr("checked");
    whir.loading.remove();//�Ƴ����ؿ�
    sessionStorage.removeItem("return_jump");
};
//Ȩ������
function jurisdiction(actions){
    $('#jurisdiction').empty();
    for(var i=0;i<actions.length;i++){
        if(actions[i].act_name=="add"){
            $('#jurisdiction').append("<li id='add'><a href='javascript:void(0);'><span class='icon-ishop_6-01'></span>����</a></li>");
        }else if(actions[i].act_name=="delete"){
            $('#jurisdiction').append("<li id='remove'><a href='javascript:void(0);'><span class='icon-ishop_6-02'></span>ɾ��</a></li>");
        }else if(actions[i].act_name=="edit"){
            $('#jurisdiction').append("<li id='compile' class='bg'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>�༭</a></li>");
        }else if(actions[i].act_name=="output"){
            $("#more_down").append("<div id='leading_out'>����</div>");
        }else if(actions[i].act_name=="input"){
            $("#more_down").append("<div id='guide_into'>����</div>");
        }
    }
}
function InitialState(){
    if(return_jump!==null){
        inx=return_jump.inx;
        pageSize=return_jump.pageSize;
        value=return_jump.value;
        filtrate=return_jump.filtrate;
        list=return_jump.list;
        param=JSON.parse(return_jump.param);
        _param=JSON.parse(return_jump._param);
    }
    if(return_jump==null){
        if(value==""&&filtrate==""){
            param["pageNumber"]=inx;
            param["pageSize"]=pageSize;
            param["searchValue"]="";
            GET(inx,pageSize);
        }
    }else if(return_jump!==null){
        if(pageSize==10){
            $("#page_row").val("10��/ҳ");
        }
        if(pageSize==30){
            $("#page_row").val("30��/ҳ");
        }
        if(pageSize==50){
            $("#page_row").val("50��/ҳ");
        }
        if(pageSize==100){
            $("#page_row").val("100��/ҳ");
        }
        if(value==""&&filtrate==""){
            param["pageNumber"]=inx;
            param["pageSize"]=pageSize;
            param["searchValue"]="";
            GET(inx,pageSize);
        }else if(value!==""){
            $("#search").val(value);
            POST(inx,pageSize);
        }else if(filtrate!==""){
            filtrates(inx,pageSize);
        }
    }
}
//ҳ����ص�Ȩ�޽ӿ�
function qjia(){
    var param={};
    param["funcCode"]=funcCode;
    oc.postRequire("post","/list/action","0",param,function(data){
        var message=JSON.parse(data.message);
        var actions=message.actions;
        titleArray=message.columns;
        jurisdiction(actions);
        jumpBianse();
        InitialState();
        tableTh();
    })
}
function tableTh(){ //table  �ı�ͷ
    var TH="";
    for(var i=0;i<titleArray.length;i++){
        TH+="<th>"+titleArray[i].show_name+"</th>"
    }
    $("#tableOrder").after(TH);
}
qjia();
//ҳ�����ʱlist����
function GET(a,b){
    whir.loading.add("",0.5);//���صȴ���
    //oc.postRequire("get","/corp/list?pageNumber="+a+"&pageSize="+b
    //    +"&funcCode="+funcCode+"","","",function(data){
    oc.postRequire("post","/corp/search","0",param,function(data){
        if(data.code=="0"){
            $(".table tbody").empty();
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            cout=list.pages;
            var pageNum = list.pageNum;
            var list=list.list;
            superaddition(list,pageNum);
            jumpBianse();
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode);
        }else if(data.code=="-1"){
            // alert(data.message);
        }
    });
}
//��������Ժ�ҳ����еĲ���
function jumpBianse(){
    $(document).ready(function(){//���б�ɫ
        $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
        $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
    })
    //���tr input��ѡ��״̬  tr����class����
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
    //�������ʱҳ����е���ת
    $('#add').click(function(){
        $(window.parent.document).find('#iframepage').attr("src","/corp/crop_add.html");
    })
    //����༭ʱҳ����е���ת
    $('#compile').click(function(){
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==1){
            id=$(tr).attr("id");
            var return_jump={};//����һ������
            return_jump["inx"]=inx;//��ת���ڼ�ҳ
            return_jump["value"]=value;//������ֵ;
            return_jump["filtrate"]=filtrate;//ɸѡ��ֵ
            return_jump["param"]=JSON.stringify(param);//���������ֵ
            return_jump["_param"]=JSON.stringify(_param)//ɸѡ�����ֵ
            return_jump["list"]=list;//ɸѡ�������list;
            return_jump["pageSize"]=pageSize;//ÿҳ������
            sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
            sessionStorage.setItem("id",id);
            if(id == "" || id == undefined){
                return ;
            }else{
                $(window.parent.document).find('#iframepage').attr("src","/corp/crop_edit.html");
            }
        }else if(tr.length==0){
            frame();
            $('.frame').html("����ѡ��");
        }else if(tr.length>1){
            frame();
            $('.frame').html("����ѡ����");
        }
    });
    //˫����ת
    $(".table tbody tr").dblclick(function(){
        var id=$(this).attr("id");
        var return_jump={};//����һ������
        return_jump["inx"]=inx;//��ת���ڼ�ҳ
        return_jump["value"]=value;//������ֵ;
        return_jump["filtrate"]=filtrate;//ɸѡ��ֵ
        return_jump["param"]=JSON.stringify(param);//���������ֵ
        return_jump["_param"]=JSON.stringify(_param)//ɸѡ�����ֵ
        return_jump["list"]=list;//ɸѡ�������list;
        return_jump["pageSize"]=pageSize;//ÿҳ������
        sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
        sessionStorage.setItem("id",id);
        if(id == "" || id == undefined){
            return ;
        }else{
            $(window.parent.document).find('#iframepage').attr("src","/corp/crop_edit.html");
        }
    });
    //ɾ��
    $("#remove").click(function(){
        var l=$(window).width();
        var h=$(document.body).height();
        var tr=$("tbody input[type='checkbox']:checked").parents("tr");
        if(tr.length==0){
            frame();
            $('.frame').html("����ѡ��");
            return;
        }
        $("#p").show();
        $("#tk").show();
        $("#p").css({"width":+l+"px","height":+h+"px"});
        $("#tk").css({"left":+left+"px","top":+tp+"px"});
    })
}
//��갴��ʱ����������
$("#search").keydown(function() {
    var event=window.event||arguments[0];
    inx=1;
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["funcCode"]=funcCode;
    if(event.keyCode == 13){
        value=this.value.trim();
        param["searchValue"]=value;
        POST(inx,pageSize);
    }
});
//����Ŵ󾵴�������
$("#d_search").click(function(){
    value=$("#search").val().replace(/\s+/g,"");
    inx=1;
    param["searchValue"]=value;
    param["pageNumber"]=inx;
    param["pageSize"]=pageSize;
    param["funcCode"]=funcCode;
    POST(inx,pageSize);
});
//������������
function POST(a,b){
    whir.loading.add("",0.5);//���صȴ���
    oc.postRequire("post","/corp/search","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            cout=list.pages;
            var pageNum = list.pageNum;
            var list=list.list;
            var actions=message.actions;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>û���ҵ���<span class='color'>��"+value+"��</span>��ص���Ϣ������������</p>");
                whir.loading.remove();//�Ƴ����ؿ�
            }else if(list.length>0){
                $(".table p").remove();
                superaddition(list,pageNum);
                jumpBianse();
            }
            var input=$(".inputs input");
            for(var i=0;i<input.length;i++){
                input[i].value="";
            }
            filtrate="";
            list="";
            $(".sxk").slideUp();
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    })
}
//����ر�
$("#X").click(function(){
    $("#p").hide();
    $("#tk").hide();
})
//ȡ���ر�
$("#cancel").click(function(){
    $("#p").hide();
    $("#tk").hide();
})
//����ɾ���ر�
$("#delete").click(function(){
    $("#p").hide();
    $("#tk").hide();
    var tr=$("tbody input[type='checkbox']:checked").parents("tr");
    for(var i=tr.length-1,ID="";i>=0;i--){
        var r=$(tr[i]).attr("id");
        if(i>0){
            ID+=r+",";
        }else{
            ID+=r;
        }
    }
    var params={};
    params["id"]=ID;
    whir.loading.add("",0.5);//���صȴ���
    oc.postRequire("post","/corp/delete","0",params,function(data){
        whir.loading.remove();//�Ƴ����ؿ�
        if(data.code=="0"){
            if(value==""&&filtrate==""){
                frame().then(function(){
                    GET(pageNumber,pageSize);
                });
                $('.frame').html('ɾ���ɹ�');
                param["pageNumber"]=inx;
                param["pageSize"]=pageSize;
                param["searchValue"]="";
            }else if(value!==""){
                frame().then(function(){
                    POST(pageNumber,pageSize);
                });
                $('.frame').html('ɾ���ɹ�');
                param["pageNumber"]=pageNumber;
            }else if(filtrate!==""){
                frame().then(function(){
                    filtrates(pageNumber,pageSize);
                });
                $('.frame').html('ɾ���ɹ�');
                _param["pageNumber"]=pageNumber;
            }
            var thinput=$("thead input")[0];
            thinput.checked =false;
        }else if(data.code=="-1"){
            frame();
            $('.frame').html(data.message);
        }
    })
})
//ɾ������
function frame(){
    var def= $.Deferred();
    var left=($(window).width()-$("#frame").width())/2;//����λ��leftֵ
    var tp=($(window).height()-$("#frame").height())/2;//����λ��topֵ
    $('.frame').remove();
    $('.content').append('<div class="frame" style="left:'+left+'px;top:'+tp+'px;"></div>');
    $(".frame").animate({opacity:"1"},1000);
    $(".frame").animate({opacity:"0"},1000);
    setTimeout(function(){
        $(".frame").hide();
        def.resolve();
    },2000);
    return def;
}
//ȫѡ
function checkAll(name){
    var el=$("tbody input");
    el.parents("tr").addClass("tr");
    var len = el.length;

    for(var i=0; i<len; i++)
    {
        if((el[i].type=="checkbox") && (el[i].name==name))
        {
            el[i].checked = true;
        }
    }
};

//ȡ��ȫѡ
function clearAll(name){
    var el=$("tbody input");
    el.parents("tr").removeClass("tr");
    var len = el.length;
    for(var i=0; i<len; i++)
    {
        if((el[i].type=="checkbox") && (el[i].name==name))
        {
            el[i].checked = false;
        }
    }
};
//��������list
$("#more_down").on("click","#leading_out",function(){
    var l=$(window).width();
    var h=$(document.body).height();
    var left=($(window).width()-$(".file").width())/2;//����λ��leftֵ
    var tp=($(window).height()-$(".file").height())/2;//����λ��topֵ
    $(".file").css({"left":+left+"px","top":+tp+"px"});
    $("#p").show();
    $("#p").css({"width":+l+"px","height":+h+"px"});
    $('.file').show();
    $(".into_frame").hide();
    var param={};
    param["function_code"]=funcCode;
    whir.loading.add("",0.5);//���صȴ���
    oc.postRequire("post","/list/getCols","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var message=JSON.parse(message.tableManagers);
            var html="";
            for(var i=0;i<message.length;i++){
                html+="<li data-name='"+message[i].column_name+"'><div class='checkbox1'><input type='checkbox' value='' name='test'  class='check'  id='checkboxInput"
                    +i+1+"'/><label for='checkboxInput"+i+1+"'></label></div><span class='p15'>"+message[i].show_name+"</span></li>";
            }
            $("#file_list_l ul").html(html);
            bianse();
            $("#file_list_r ul").empty();
            whir.loading.remove();//�Ƴ����ؿ�
        }else if(data.code=="-1"){
            alert(data.message);
            whir.loading.remove();//�Ƴ����ؿ�
        }
    })
})
function bianse(){
    $("#file_list_l li:odd").css("backgroundColor","#fff");
    $("#file_list_l li:even").css("backgroundColor","#ededed");
    $("#file_list_r li:odd").css("backgroundColor","#fff");
    $("#file_list_r li:even").css("backgroundColor","#ededed");
}
//�����ύ��
$("#file_submit").click(function(){
    var li=$("#file_list_r input[type='checkbox']").parents("li");
    var param={};
    var tablemanager=[];
    if(li.length=="0"){
        frame();
        $('.frame').html('���Ҫ���������Ƶ��ұ�');
        return;
    }
    for(var i=0;i<li.length;i++){
        var r=$(li[i]).attr("data-name");
        var z=$(li[i]).children("span").html();
        var param1={"column_name":r,"show_name":z};
        tablemanager.push(param1);
    }
    tablemanager.reverse();
    param["tablemanager"]=tablemanager;
    param["searchValue"]=value;
    if(filtrate==""){
        param["list"]="";
    }else if(filtrate!==""){
        param["list"]=list;
    }
    whir.loading.add("",0.5);//���صȴ���
    oc.postRequire("post","/corp/exportExecl","0",param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var path=message.path;
            var path=path.substring(1,path.length-1);
            $('#download').html("<a href='/"+path+"'>�����ļ�</a>");
            $('#download').addClass("download");
            $('#file_submit').hide();
            $('#download').show();
            //�����رհ�ť
            $('#file_close').click(function(){
                $('.file').hide();
            })
            $('#download').click(function(){
                $("#p").hide();
                $('.file').hide();
                $('#file_submit').show();
                $('#download').hide();
            })
        }else if(data.code=="-1"){
            alert(data.message);
        }
        whir.loading.remove();//�Ƴ����ؿ�
    })
})
//�����رհ�ť
$('#file_close').click(function(){
    $("#p").hide();
    $('.file').hide();
    $('#file_submit').show();
    $('#download').hide();
})
//�������
$("#more_down").on("click","#guide_into",function(){
    var l=$(window).width();
    var h=$(document.body).height();
    var left=($(window).width()-$(".into_frame").width())/2;//����λ��leftֵ
    var tp=($(window).height()-$(".into_frame").height())/2;//����λ��topֵ
    $(".into_frame").css({"left":+left+"px","top":+tp+"px"});
    $("#p").show();
    $("#p").css({"width":+l+"px","height":+h+"px"});
    $('.file').hide();
    $(".into_frame").show();
})
//����رհ�ť
$("#x1").click(function(){
    $("#p").hide();
    $(".into_frame").hide();
})
//�ϴ��ļ�
function UpladFile() {
    whir.loading.add("",0.5);//���صȴ���
    var fileObj = document.getElementById("file").files[0];
    var FileController = "/corp/addByExecl"; //�����ϴ��ļ��ĺ�̨��ַ
    var form = new FormData();
    form.append("file", fileObj); // �ļ�����
    // XMLHttpRequest ����
    var xhr = null;
    if (window.XMLHttpRequest) {
        xhr = new XMLHttpRequest();
    } else {
        xhr = new ActiveXObject('Microsoft.XMLHTTP');
    }
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                doResult(xhr.responseText);
            } else {
                console.log('�����������˴������Ӧ״̬��');
                $('#file').val("");
            }
        }
    }
    function doResult(data) {
        var data=JSON.parse(data);
        whir.loading.remove();
        if(data.code=="0"){
            alert('����ɹ�');
            window.location.reload();
        }else if(data.code=="-1"){
            alert("����ʧ��"+data.message);
        }
        $('#file').val("");
    }
    xhr.open("post", FileController, true);
    xhr.onload = function() {
        // alert("�ϴ����!");
    };
    xhr.send(form);
    $("#p").hide();
    $(".into_frame").hide();
}
//ɸѡ��ť
oc.postRequire("get","/list/filter_column?funcCode="+funcCode+"","0","",function(data){
    if(data.code=="0"){
        var message=JSON.parse(data.message);
        var filter=message.filter;
        $("#sxk .inputs ul").empty();
        var li="";
        for(var i=0;i<filter.length;i++){
            if(filter[i].type=="text"){
                li+="<li><label>"+filter[i].show_name+"</label><input type='text' id='"+filter[i].col_name+"'></li>";
            }else if(filter[i].type=="select"){
                var msg=filter[i].value;
                var ul="<ul class='isActive_select_down'>";
                for(var j=0;j<msg.length;j++){
                    ul+="<li data-code='"+msg[j].value+"'>"+msg[j].key+"</li>"
                }
                ul+="</ul>";
                li+="<li class='isActive_select'><label>"+filter[i].show_name+"</label><input type='text' id='"+filter[i].col_name+"' data-code='' readonly>"+ul+"</li>"
            }
        }
        $("#sxk .inputs ul").html(li);
        if(filtrate!==""){
            $(".sxk").slideDown();
            for(var i=0;i<list.length;i++){
                if($("#"+list[i].screen_key).parent("li").attr("class")!=="isActive_select"){
                    $("#"+list[i].screen_key).val(list[i].screen_value);
                }else if($("#"+list[i].screen_key).parent("li").attr("class")=="isActive_select"){
                    var svalue=$("#"+list[i].screen_key).next(".isActive_select_down").find("li[data-code='"+list[i].screen_value+"']").html();
                    $("#"+list[i].screen_key).val(svalue);
                }
            }
        }
        filtrateDown();
        //ɸѡ��keydow�¼�
        $('#sxk .inputs input').keydown(function(){
            var event=window.event||arguments[0];
            if(event.keyCode == 13){
                getInputValue();
            }
        })
    }
});
function filtrateDown(){
    //ɸѡselect��
    $(".isActive_select input").click(function (){
        var ul=$(this).next(".isActive_select_down");
        if(ul.css("display")=="none"){
            ul.show();
        }else{
            ul.hide();
        }
    })
    $(".isActive_select input").blur(function(){
        var ul=$(this).next(".isActive_select_down");
        setTimeout(function(){
            ul.hide();
        },200);
    })
    $(".isActive_select_down li").click(function () {
        var html=$(this).text();
        var code=$(this).attr("data-code");
        $(this).parents("li").find("input").val(html);
        $(this).parents("li").find("input").attr("data-code",code);
        $(".isActive_select_down").hide();
    })
}
//ɸѡ����
$("#find").click(function(){
    getInputValue();
})
function getInputValue(){
    var input=$('#sxk .inputs input');
    inx=1;
    _param["pageNumber"]=inx;
    _param["pageSize"]=pageSize;
    _param["funcCode"]=funcCode;
    var num=0;
    list=[];//����һ��list
    for(var i=0;i<input.length;i++){
        var screen_key=$(input[i]).attr("id");
        var screen_value="";
        if($(input[i]).parent("li").attr("class")=="isActive_select"){
            screen_value=$(input[i]).attr("data-code");
        }else{
            screen_value=$(input[i]).val().trim();
        }
        if(screen_value!=""){
            num++;
        }
        var param1={"screen_key":screen_key,"screen_value":screen_value};
        list.push(param1);
    }
    _param["list"]=list;
    value="";//�������Ϳ�
    $("#search").val("");
    filtrates(inx,pageSize)
    if(num>0){
        filtrate="sucess";
    }else if(num<=0){
        filtrate="";
    }
}
//ɸѡ��������
function filtrates(a,b){
    whir.loading.add("",0.5);//���صȴ���
    oc.postRequire("post","/corp/screen","0",_param,function(data){
        if(data.code=="0"){
            var message=JSON.parse(data.message);
            var list=JSON.parse(message.list);
            cout=list.pages;
            var pageNum = list.pageNum;
            var list=list.list;
            var actions=message.actions;
            $(".table tbody").empty();
            if(list.length<=0){
                $(".table p").remove();
                $(".table").append("<p>û���ҵ���Ϣ,����������</p>");
                whir.loading.remove();//�Ƴ����ؿ�
            }else if(list.length>0){
                $(".table p").remove();
                superaddition(list,pageNum);
                jumpBianse();
            }
            setPage($("#foot-num")[0],cout,pageNum,b,funcCode);
        }else if(data.code=="-1"){
            alert(data.message);
        }
    });
}
//��תҳ��ļ��̰����¼�
$("#input-txt").keydown(function() {
    var event=window.event||arguments[0];
    var inx= this.value.replace(/[^0-9]/g, '');
    var inx=parseInt(inx);
    if (inx > cout) {
        inx = cout
    };
    if (inx > 0) {
        if (event.keyCode == 13) {
            if (value == "" && filtrate == "") {
                param["pageNumber"]=inx;
                param["pageSize"]=pageSize;
                param["searchValue"]="";
                GET(inx, pageSize);
            } else if (value !== "") {
                param["pageSize"] = pageSize;
                param["pageNumber"]=inx;
                POST(inx, pageSize);
            } else if (filtrate !== "") {
                _param["pageSize"] = pageSize;
                _param["pageNumber"]=inx;
                filtrates(inx, pageSize);
            }
        };
    }
})