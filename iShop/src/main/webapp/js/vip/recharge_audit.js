/**
 * Created by Administrator on 2017/1/5.
 */
(function(){
    var oc= new ObjectControl();
    var audit={
        inx:1,
        pageSize:10,
        funcCode:"",
        titleArray:[],
        list:"",
        filtrate:"",//筛选的定义的值
        count:"",
        init:function(){
            this.getFunCode();
            this.qjia();
            this.bind();
        },
        bind:function(){
            this.auditRefuse();
            this.auditParse();
            this.slideScreen();
            this.getFileColumn();
            this.inputTopage();
            this.setPageSize();
        },
        getFileColumn:function(){
            var self=this;
            //筛选按钮
            oc.postRequire("get","/list/filter_column?funcCode="+self.funcCode+"","0","",function(data){
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
                    if(self.filtrate!==""){
                        $(".sxk").slideDown();
                        for(var i=0;i<self.list.length;i++){
                            if($("#"+self.list[i].screen_key).parent("li").attr("class")!=="isActive_select"){
                                $("#"+self.list[i].screen_key).val(self.list[i].screen_value);
                            }else if($("#"+self.list[i].screen_key).parent("li").attr("class")=="isActive_select"){
                                var value=$("#"+self.list[i].screen_key).next(".isActive_select_down").find("li[data-code='"+self.list[i].screen_value+"']").html();
                                $("#"+self.list[i].screen_key).val(value);
                            }
                        }
                    }
                    self.filtrateDown();
                    //筛选的keydow事件
                    $('#sxk .inputs input').keydown(function(){
                        var event=window.event||arguments[0];
                        if(event.keyCode == 13){
                            getInputValue();
                        }
                    })
                }
            });
        },
        filtrateDown:function (){
            //筛选select框
            $(".isActive_select input").click(function (){
                var ul=$(this).next(".isActive_select_down");
                if(ul.css("display")=="none"){
                    ul.show();
                }else{
                    ul.hide();
                }
            });
            $(".isActive_select input").blur(function(){
                var ul=$(this).next(".isActive_select_down");
                setTimeout(function(){
                    ul.hide();
                },200);
            });
            $(".isActive_select_down li").click(function () {
                var html=$(this).text();
                var code=$(this).attr("data-code");
                $(this).parents("li").find("input").val(html);
                $(this).parents("li").find("input").attr("data-code",code);
                $(".isActive_select_down").hide();
            })
        },
        slideScreen:function(){
            $("#filtrate").click(function(){//点击筛选框弹出下拉框
                $(".sxk").slideToggle();
            });
            $("#pack_up").click(function(){//点击收回 取消下拉框
                $(".sxk").slideUp();
            })
        },
        getFunCode:function(){
            var key_val=sessionStorage.getItem("key_val");//取页面的function_code
            key_val=JSON.parse(key_val);//取key_val的值
            this.funcCode=key_val.func_code;
        },
        setPage:function (container, count, pageindex,pageSize,funcCode,value) {
            var self=audit;
            var container = container;
            var count = count;
            var pageindex = pageindex;
            var pageSize=pageSize;
            var a = [];
            //总页数少于10 全部显示,大于10 显示前3 后3 中间3 其余....
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
            //总页数小于10
            if (count <= 10) {
                for (var i = 1; i <= count; i++) {
                    setPageList();
                }
            }
            //总页数大于10页
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
                else { //当前页在中间部分
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
                self.inx = pageindex; //初始的页码
                $("#input-txt").val(self.inx);
                $(".foot-sum .zy").html("共 "+count+"页");
                oAlink[0].onclick = function() { //点击上一页
                    if (self.inx == 1) {
                        return false;
                    }
                    self.inx--;
                    self.dian(self.inx);
                    self.setPage(container, count, self.inx,pageSize,funcCode,value);
                    return false;
                };
                for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
                    oAlink[i].onclick = function() {
                        self.inx = parseInt(this.innerHTML);
                        self.dian(self.inx);
                        self.setPage(container, count, self.inx,pageSize,funcCode,value);
                        return false;
                    }
                }
                oAlink[oAlink.length - 1].onclick = function() { //点击下一页
                    if (self.inx == count) {
                        return false;
                    }
                    self.inx++;
                    self.dian(self.inx);
                    self.setPage(container, count, self.inx,pageSize,funcCode,value);
                    return false;
                }
            }();
        },
        inputTopage:function(){
            //跳转页面的键盘按下事件
            $("#input-txt").keydown(function() {
                var event=window.event||arguments[0];
                var inx= this.value.replace(/[^0-9]/g, '');
                inx=parseInt(inx);
                if (inx > audit.count) {
                    inx = audit.count
                }
                if (inx > 0) {
                    if (event.keyCode == 13) {
                        if (audit.filtrate == "") {
                            audit.GET(audit.inx, audit.pageSize);
                        }
                        //else if (filtrate !== "") {
                        //    _param["pageSize"] = pageSize;
                        //    _param["pageNumber"]=inx;
                        //    filtrates(inx, pageSize);
                        //}
                    }
                }
            });
        },
        setPageSize:function(){
            $("#page_row").click(function(){
                if("block" == $("#liebiao").css("display")){
                    hideLi();
                }else{
                    showLi();
                }
            });
            $("#liebiao li").each(function(i,v){
                $(this).click(function(){
                    audit.pageSize=$(this).attr('id');
                    if(audit.filtrate==""){
                        audit.inx=1;
                        audit.GET(audit.inx,audit.pageSize);
                    }
                        // else if(value!==""){
                    //    inx=1;
                    //    param["pageSize"]=pageSize;
                    //    param["pageNumber"]=inx;
                    //    POST(inx,pageSize);
                    //}else if(filtrate!==""){
                    //    inx=1;
                    //    _param["pageNumber"]=inx;
                    //    _param["pageSize"]=pageSize;
                    //    filtrates(inx,pageSize);
                    //}
                    $("#page_row").val($(this).html());
                    hideLi();
                });
            });
            function showLi(){
                $("#liebiao").show();
            }
            function hideLi(){
                $("#liebiao").hide();
            }
            $("#page_row").blur(function(){
                setTimeout(hideLi,200);
            });
        },
        dian:function(inx){//
            if(audit.filtrate==""){
                audit.GET(audit.inx,audit.pageSize)
            }

        },
        qjia:function (){
            var self=this;
            var param={};
            param["funcCode"]=self.funcCode;
            oc.postRequire("post","/list/action","0",param,function(data){
                var message=JSON.parse(data.message);
                //var actions=message.actions;
                self.titleArray=message.columns;
                self.tableTh();
                self.GET(self.inx,self.pageSize)
            })
        },
        tableTh:function (){ //table  的表头
            var TH="";
            var titleArray=audit.titleArray;
            for(var i=0;i<titleArray.length;i++){
                if(titleArray[i].show_name.trim()=='姓名'){
                    TH+="<th>"+titleArray[i].show_name+"</th>"+'<th style="width: 20px"></th>'
                }else{
                    TH+="<th>"+titleArray[i].show_name+"</th>"
                }
            }
            $("#tableOrder").after(TH);
        },
        superaddition:function (data,num){//页面加载循环
            //$(".table p").remove();
            if(data.length == 0){
                var len = $(".table thead tr th").length;
                var i;
                for(i=0;i<10;i++){
                    $(".table tbody").append("<tr></tr>");
                    for(var j=0;j<len;j++){
                        $($(".table tbody tr")[i]).append("<td></td>");
                    }
                }
                $(".table tbody tr:nth-child(5)").append("<span style='position:absolute;left:54%;font-size: 15px;color:#999'>暂无内容</span>");
            }
            if(data.length==1&&num>1){
                pageNumber=num-1;
            }else{
                pageNumber=num;
            }
            for (var i = 0; i < data.length; i++) {
                var TD="";
                if(num>=2){
                    var a=i+1+(num-1)*pageSize;
                }else{
                    var a=i+1;
                }
                data[i].status=="0"?data[i].status="未审核":data[i].status=="1"?data[i].status="审核通过":data[i].status="失败";
                for (var c=0;c<audit.titleArray.length;c++){
                    (function(j){
                        var code=audit.titleArray[j].column_name;
                            TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
                    })(c)
                }
                $(".table tbody").append("<tr id='"+data[i]._id+"' data-type='"+data[i].type+"' data-status='"+data[i].status+"'><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
                    + i
                    + 1
                    + "'/><label for='checkboxTwoInput"
                    + i
                    + 1
                    + "'></label></div>"
                    + "</td><td style='text-align:left;'>"
                    + a
                    + "</td>" +
                    TD +
                    "</tr>");
            }
            whir.loading.remove();//移除加载框
            $(".th th:first-child input").removeAttr("checked");
            //sessionStorage.removeItem("return_jump");
        },
        GET:function (a,b){ //页面加载时list请求
            whir.loading.add("",0.5);//加载等待框
            var param={};
            param["pageNumber"]=a;
            param["pageSize"]=b;
            param["searchValue"]="";
            oc.postRequire("post","/vipCheck/search","",param,function(data){
                if(data.code=="0"){
                    $(".table tbody").empty();
                    var messages=JSON.parse(data.message);
                    var list=messages.list;
                    audit.count=messages.pages;
                    var pageNum = messages.pageNum;
                    audit.superaddition(list,pageNum);
                    audit.jumpBianse();
                    audit.setPage($("#foot-num")[0],audit.count,pageNum,b);
                }else if(data.code=="-1"){
                    alert(data.message);
                }
            });
        },
        jumpBianse:function (){
            $(document).ready(function(){//隔行变色
                $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
                $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
            });
            //点击tr input是选择状态  tr增加class属性
            $(".table tbody tr").click(function(){
                var input=$(this).find("input")[0];
                var thinput=$("thead input")[0];
                $(this).toggleClass("tr");
                //console.log(input);
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
            });
            audit.checkDetail();
        },
        checkDetail:function(){
            //双击跳转
            $(".table tbody tr").dblclick(function(){
                var id=$(this).attr("id");
                sessionStorage.setItem("id",id);
                var status=$(this).attr("data-status");
                if(status=="审核通过"){
                    $(window.parent.document).find('#iframepage').attr("src","/vip/recharge_audit_info.html");
                }else{
                    $(window.parent.document).find('#iframepage').attr("src","/vip/recharge_oparate.html");
                }

            })
        },
        auditRefuse:function(){
            $("#auditRefuse").bind("click",function(){
                var tr=$("tbody input[type='checkbox']:checked").parents("tr");
                if(tr.length==0){
                    frame();
                    $('.frame').html("请先选择");
                    return;
                }
                whir.loading.add("mask",0.5);
                $("#refuse_model").show();
            });
            $("#auditRefuseCancel,#auditRefuseOk").bind("click",function(){
                $("#refuse_model").hide();
                whir.loading.remove("mask");
                clearAll("test");
                //$("#checkboxTwoInput0").
            })
        },
        auditParse:function(){
            var self=this;
            $("#auditParse").bind("click",function(){
                var tr=$("tbody input[type='checkbox']:checked").parents("tr");
                if(tr.length==0){
                    frame();
                    $('.frame').html("请先选择");
                    return;
                }
                if(tr.length>1){
                    frame();
                    $('.frame').html("暂支持单个文件审核");
                    return;
                }
                for(var i=tr.length-1,ID="";i>=0;i--){
                    var r=$(tr[i]).attr("id");
                    var type=$(tr[i]).attr("data-type")=="充值"?"pay":"refund";
                    if(i>0){
                        ID+=r+",";
                    }else{
                        ID+=r;
                    }
                }
                var params={};
                params["id"]=ID;
                params["type"]=type;
                oc.postRequire("post","/vipCheck/changeStatus","",params,function(data){
                    if(data.code=="0"){
                        frame();
                        $('.frame').html("操作成功");
                        self.GET(self.inx,self.pageSize);
                    }else{
                        frame();
                        $('.frame').html(data.message);
                    }
                })
            })
        }
    };
    $(function(){
        audit.init();
    })
})();
//删除弹框
function frame(){
    var def= $.Deferred();
    var left=($(window).width()-$("#frame").width())/2;//弹框定位的left值
    var tp=($(window).height()-$("#frame").height())/2;//弹框定位的top值
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
//取消全选
function clearAll(name){
    var el=$(".table input");
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