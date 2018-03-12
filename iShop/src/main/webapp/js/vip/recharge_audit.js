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
        list:[],
        filtrate:"",//筛选的定义的值
        _param:{},
        param:{},
        count:"",
        allData:"",//所有的结果
        return_jump:"",
        init:function(){
            this.getReturnJump();
            this.getFunCode();
            this.qjia();
            this.bind();
        },
        bind:function(){
            this.auditRefuse();
            this.auditParse();
            this.slideScreen();
            this.inputTopage();
            this.setPageSize();
            this.find();
            this.clearFiltrates();
            this.edit();
            this.delete();
            this.output();
        },
        getReturnJump:function(){
           audit.return_jump=sessionStorage.getItem("return_jump");//获取本页面的状态
           audit.return_jump=JSON.parse(audit.return_jump);
        },
        //筛选查找
        find:function(){
            $("#find").click(function(){
                audit.getInputValue();
            });
        },
        InitialState:function (){
            this.getFileColumn();
            if(audit.return_jump!==null){
                audit.inx=audit.return_jump.inx;
                audit.pageSize=audit.return_jump.pageSize;
                audit.filtrate=audit.return_jump.filtrate;
                audit.list=audit.return_jump.list;
                audit.param=JSON.parse(audit.return_jump.param);
                audit._param=JSON.parse(audit.return_jump._param);
            }
            if(audit.return_jump==null){
                //$("#find").trigger("click");
                //if(audit.filtrate==""){
                    //audit.GET(audit.inx,audit.pageSize);
                    //audit._param["pageSize"] = audit.pageSize;
                    //audit._param["pageNumber"]=audit.inx;
                    //audit.filtrates(audit.inx,audit.pageSize)
                //}
            }else if(audit.return_jump!==null){
                if(audit.pageSize==10){
                    $("#page_row").val("10行/页");
                }
                if(audit.pageSize==30){
                    $("#page_row").val("30行/页");
                }
                if(audit.pageSize==50){
                    $("#page_row").val("50行/页");
                }
                if(audit.pageSize==100){
                    $("#page_row").val("100行/页");
                }
                if(audit.filtrate==""){
                    audit.param["funcCode"]=audit.funcCode;
                    audit.GET(audit.inx,audit.pageSize);
                }else if(filtrate!==""){
                    audit.filtrates(audit.inx,audit.pageSize);
                }
            }
        },
        getInputValue:function (){
            var input=$('#sxk .inputs input');
            audit.inx=1;
            audit._param["pageNumber"]=audit.inx;
            audit._param["pageSize"]=audit.pageSize;
            audit._param["funcCode"]=audit.funcCode;
            audit.list=[];//定义一个list
            for(var i=0;i<input.length;i++){
                var screen_key=$(input[i]).attr("id");
                var screen_value="";
                if($(input[i]).parent("li").attr("class")=="isActive_select"){
                    screen_value=$(input[i]).attr("data-code");
                }else if($(input[i]).parent("li").attr("class")=="created_date" && $(input[i]).attr("id")=="start"){
                    var start=$('#start').val();
                    var end=$('#end').val();
                    screen_key=$(input[i]).parent("li").attr("id");
                    screen_value={"start":start,"end":end};
                }else if($(input[i]).parent("li").attr("class")=="created_date" && $(input[i]).attr("id")!="start"){
                    continue
                }else{
                    screen_value=$(input[i]).val().trim();
                }
                var param1={"screen_key":screen_key,"screen_value":screen_value};
                audit.list.push(param1);
            }
            audit._param["list"]=audit.list;
            audit.filtrates(audit.inx,audit.pageSize);
            audit.filtrate="success";

    },
        clearFiltrates:function(){
            //点击清空  清空input的value值
            $("#empty").click(function(){
                var input=$(".inputs input");
                for(var i=0;i<input.length;i++){
                    input[i].value="";
                    $(input[i]).attr("data-code","");
                    if($(input[i]) && $(input[i]).attr("id")=="status"){
                        input[i].value="未审核";
                        $(input[i]).attr("data-code","0");
                    }
                    if($(input[i]) && $(input[i]).attr("id")=="isactive"){
                        input[i].value="可用";
                        $(input[i]).attr("data-code","Y");
                    }
                    if($(input[i]) && $(input[i]).attr("id")=="type"){
                        input[i].value="全部";
                        $(input[i]).attr("data-code","");
                    }
                }
                audit.filtrate="success";
                audit.inx=1;
                $('#search').val("");
                $(".table p").remove();
                //audit.param["funcCode"]= audit.funcCode;
                //audit.GET(audit.inx,audit.pageSize);
                //audit._param["pageSize"] = audit.pageSize;
                //audit._param["pageNumber"]=audit.inx;
                //audit.filtrates(audit.inx,audit.pageSize)
                $("#find").trigger("click")
            })
        },
        //筛选发送请求
    filtrates:function (a,b){
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post","/vipCheck/screen","0",audit._param,function(data){
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var list=message.list;
                audit.count=message.pages;
                var pageNum = message.page_number;
                var total = message.total;
                audit.allData=total;
                var actions=message.actions;
                $(".table tbody").empty();
                if(list.length<=0){
                    $(".table p").remove();
                    $(".table").append("<p>没有找到信息,请重新搜索</p>");
                    whir.loading.remove();//移除加载框
                }else if(list.length>0){
                    $(".table p").remove();
                    audit.superaddition(list,pageNum);
                    audit.jumpBianse();
                }
                audit.setPage($("#foot-num")[0],audit.count,pageNum,b,total);
            }else if(data.code=="-1"){
                alert(data.message);
            }
        });
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
                            if(filter[i].col_name=="status"){
                                li+="<li class='isActive_select'><label>"+filter[i].show_name+"</label><input type='text' id='"+filter[i].col_name+"' readonly value='未审核' data-code='0'>"+ul+"</li>"
                            }else if(filter[i].col_name=="isactive"){
                                li+="<li class='isActive_select'><label>"+filter[i].show_name+"</label><input type='text' id='"+filter[i].col_name+"' data-code='Y' readonly value='可用'>"+ul+"</li>"
                            }else if(filter[i].col_name=="type"){
                                li+="<li class='isActive_select'><label>"+filter[i].show_name+"</label><input type='text' id='"+filter[i].col_name+"' data-code='' readonly value='全部'>"+ul+"</li>"
                            }
                        }else if(filter[i].type=="date"){
                            li+="<li class='created_date' id='"
                                +filter[i].col_name
                                +"'><label>"
                                +filter[i].show_name
                                +"</label>"
                                +"<input type='text' id='start' class='time_data laydate-icon' onClick=\"laydate({elem: '#start',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkStart})\">"
                                +"<label class='tm20'>至</label>"
                                +"<input type='text' id='end' class='time_data laydate-icon' onClick=\"laydate({elem: '#end',min:'1900-01-01 00:00:00',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkEnd})\">"
                                +"</li>";
                        }
                    }
                    $("#sxk .inputs ul").html(li);
                    if(self.filtrate!==""){
                        //$(".sxk").slideDown();
                        for(var i=0;i<self.list.length;i++){
                             if($("#"+self.list[i].screen_key).parent("li").attr("class")=="isActive_select"){
                                var value=$("#"+self.list[i].screen_key).next(".isActive_select_down").find("li[data-code='"+self.list[i].screen_value+"']").html();
                                $("#"+self.list[i].screen_key).val(value);
                                $("#"+self.list[i].screen_key).attr("data-code",self.list[i].screen_value);
                                $("#"+self.list[i].screen_key).attr("value",value);
                            }else if($("#"+self.list[i].screen_key).attr("class")=="created_date"){
                                $("#"+self.list[i].screen_key).find("input#start").val(self.list[i].screen_value["start"]);
                                $("#"+self.list[i].screen_key).find("input#end").val(self.list[i].screen_value["end"]);
                            }else{
                                $("#"+self.list[i].screen_key).val(self.list[i].screen_value);
                            }
                        }
                    }else{
                        $("#find").trigger("click");
                    }
                    self.filtrateDown();
                    //筛选的keydow事件
                    $('#sxk .inputs input').keydown(function(){
                        var event=window.event||arguments[0];
                        if(event.keyCode == 13){
                            audit.getInputValue();
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
        setPage:function (container, count, pageindex,pageSize,funcCode,value,total) {
            var self=audit;
            var container = container;
            var count = count;
            var pageindex = pageindex;
            var pageSize=pageSize;
            var total=funcCode;
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
                $(".foot-sum .zy").html("共 "+count+"页,"+total+'条记录');
                oAlink[0].onclick = function() { //点击上一页
                    if (self.inx == 1) {
                        return false;
                    }
                    self.inx--;
                    self.dian(self.inx);
                    self.setPage(container, count, self.inx,pageSize,funcCode,value,total);
                    return false;
                };
                for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
                    oAlink[i].onclick = function() {
                        self.inx = parseInt(this.innerHTML);
                        self.dian(self.inx);
                        self.setPage(container, count, self.inx,pageSize,funcCode,value,total);
                        return false;
                    }
                }
                oAlink[oAlink.length - 1].onclick = function() { //点击下一页
                    if (self.inx == count) {
                        return false;
                    }
                    self.inx++;
                    self.dian(self.inx);
                    self.setPage(container, count, self.inx,pageSize,funcCode,value,total);
                    return false;
                }
            }();
        },
        inputTopage:function(){
            //跳转页面的键盘按下事件
            $("#input-txt").keydown(function() {
                var event=window.event||arguments[0];
                var inx= this.value.replace(/[^0-9]/g, '');
                audit.inx=parseInt(inx);
                if (inx > audit.count) {
                    audit.inx = audit.count
                }
                if (audit.inx > 0) {
                    if (event.keyCode == 13) {
                        if (audit.filtrate == "") {
                            audit.param["funcCode"]=audit.funcCode;
                            audit.GET(audit.inx, audit.pageSize);
                        }
                        else if (audit.filtrate !== "") {
                            audit._param["pageSize"] = audit.pageSize;
                            audit._param["pageNumber"]=audit.inx;
                            audit.filtrates(audit.inx, audit.pageSize);
                        }
                    }
                }
            });
        },
        setPageSize:function(){
            $("#page_row,.page_p .icon-ishop_8-02").click(function(){
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
                        audit.param["funcCode"]=audit.funcCode;
                        audit.GET(audit.inx,audit.pageSize);
                    }else if(audit.filtrate!=""){
                        audit.inx=1;
                        audit._param["pageNumber"]=audit.inx;
                        audit._param["pageSize"]=audit.pageSize;
                        audit.filtrates(audit.inx,audit.pageSize);
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
                audit.param["funcCode"]=audit.funcCode;
                audit.GET(audit.inx, audit.pageSize);
            }else if(audit.filtrate!=""){
                audit._param["pageSize"] = audit.pageSize;
                audit._param["pageNumber"]=audit.inx;
                audit.filtrates(audit.inx,audit.pageSize)
            }

        },
        qjia:function (){
            var self=this;
            var param={};
            param["funcCode"]=self.funcCode;
            oc.postRequire("post","/list/action","0",param,function(data){
                var message=JSON.parse(data.message);
                var actions=message.actions;
                self.jurisdiction(actions);
                self.titleArray=message.columns;
                self.InitialState();
                self.tableTh();
            })
        },
        jurisdiction:function(actions){
            for(var i=0;i<actions.length;i++){
                if(actions[i].act_name=="check"){
                    $("#auditParse").show();
                }
                if(actions[i].act_name=="edit"){
                    $("#compile").show();
                }
                if(actions[i].act_name=="delete"){
                    $("#remove").show();
                }
                if(actions[i].act_name=="output"){
                    $("#output").show();
                }
            }
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
                audit.pageNumber=num-1;
            }else{
                audit.pageNumber=num;
            }
            for (var i = 0; i < data.length; i++) {
                var TD="";
                if(num>=2){
                    var a=i+1+(num-1)*audit.pageSize;
                }else{
                    var a=i+1;
                }
                data[i].status=="0"?data[i].status="未审核":data[i].status=="1"?data[i].status="审核通过":data[i].status="失败";
                for (var c=0;c<audit.titleArray.length;c++){
                    (function(j){
                        var code=audit.titleArray[j].column_name;
                        if(code=="isactive"){
                            if(data[i][code]=="Y"){
                                TD+="<td><span title='"+data[i][code]+"'>是</span></td>";

                            }else if(data[i][code]=="N"){
                                TD+="<td><span title='"+data[i][code]+"'>否</span></td>";
                            }
                        }else{
                            TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
                        }
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
            sessionStorage.removeItem("return_jump");
        },
        GET:function (a,b){ //页面加载时list请求
            whir.loading.add("",0.5);//加载等待框
            audit.param["pageNumber"]=a;
            audit.param["pageSize"]=b;
            audit.param["searchValue"]="";
            oc.postRequire("post","/vipCheck/search","",audit.param,function(data){
                if(data.code=="0"){
                    $(".table tbody").empty();
                    var messages=JSON.parse(data.message);
                    var list=messages.list;
                    audit.count=messages.pages;
                    var pageNum = messages.pageNum;
                    var total = messages.total;
                    audit.superaddition(list,pageNum);
                    audit.jumpBianse();
                    audit.setPage($("#foot-num")[0],audit.count,pageNum,b,total);
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
        edit:function(){
            $("#compile").click(function(){
                var tr=$("tbody input[type='checkbox']:checked").parents("tr");
                if(tr.length==1){
                    var id=$(tr).attr("id");
                    var return_jump={};//定义一个对象
                    return_jump["inx"]=audit.inx;//跳转到第几页
                    return_jump["filtrate"]=audit.filtrate;//筛选的值
                    return_jump["param"]=JSON.stringify(audit.param);//搜索定义的值
                    return_jump["_param"]=JSON.stringify(audit._param);//筛选定义的值
                    return_jump["list"]=audit.list;//筛选的请求的list;
                    return_jump["pageSize"]=audit.pageSize;//每页多少行
                    sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
                    sessionStorage.setItem("id",id);
                    var status=$(tr).attr("data-status");
                    if(status=="审核通过"){
                        $(window.parent.document).find('#iframepage').attr("src","/vip/recharge_audit_info.html");
                    }else{
                        $(window.parent.document).find('#iframepage').attr("src","/vip/recharge_oparate.html");
                    }
                }else if(tr.length==0){
                    frame();
                    $('.frame').html("请先选择");
                }else if(tr.length>1){
                    frame();
                    $('.frame').html("不能选择多个");
                }
            });
        },
        checkDetail:function(){
            //双击跳转
            $(".table tbody tr").dblclick(function(){
                var id=$(this).attr("id");
                sessionStorage.setItem("id",id);
                var return_jump={};//定义一个对象
                return_jump["inx"]=audit.inx;//跳转到第几页
                return_jump["filtrate"]=audit.filtrate;//筛选的值
                return_jump["param"]=JSON.stringify(audit.param);//搜索定义的值
                return_jump["_param"]=JSON.stringify(audit._param);//筛选定义的值
                return_jump["list"]=audit.list;//筛选的请求的list;
                return_jump["pageSize"]=audit.pageSize;//每页多少行
                sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
                var status=$(this).attr("data-status");
                if(status=="审核通过"|| $("#compile").is(":hidden")==true){
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
            $("#addVipEnter,#addVipX").click(function(){
                $("#tk").hide();
                $("#msg").html("");
                whir.loading.remove("mask")
            });
            $("#auditParse").bind("click",function(){
                var tr=$("tbody input[type='checkbox']:checked").parents("tr");
                if(tr.length==0){
                    frame();
                    $('.frame').html("请先选择");
                    return;
                }
                if(tr.length>1){
                    whir.loading.add("mask");
                    $("#tk").show();
                    $("#msg").html("暂支持单个文件审核");
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
                whir.loading.add("",0.5);
                oc.postRequire("post","/vipCheck/changeStatus","",params,function(data){
                    whir.loading.remove("",0.5);
                    whir.loading.add("mask");
                    if(data.code=="0"){
                        $("#tk").show();
                        $("#msg").html("操作成功");
                        if(audit.filtrate==""){
                            audit.param["funcCode"]=audit.funcCode;
                            audit.GET(audit.inx, audit.pageSize);
                        }else if(audit.filtrate!=""){
                            audit._param["pageSize"] = audit.pageSize;
                            audit._param["pageNumber"]=audit.inx;
                            audit.filtrates(audit.inx,audit.pageSize)
                        }

                    }else{
                        $("#tk").show();
                        $("#msg").html(data.message);
                    }
                })
            })
        },
        delete:function(){
            //删除
            $("#cancel,#X").click(function(){
                $("#p").hide();
                $("#delete_tk").hide();
            });
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
                $("#delete_tk").show();
                $("#p").css({"width":+l+"px","height":+h+"px"});
            });
            //弹框删除关闭
            $("#delete").click(function(){
                $("#p").hide();
                $("#delete_tk").hide();
                var tr=$("tbody input[type='checkbox']:checked").parents("tr");
                for(var i=tr.length-1,ID="";i>=0;i--){
                    var r=$(tr[i]).attr("id");
                    if(i>0){
                        ID+=r+",";
                    }else{
                        ID+=r;
                    }
                }
                var param={};
                param["id"]=ID;
                whir.loading.add("",0.5);
                oc.postRequire("post","/vipCheck/deleteBill","0",param,function(data){
                    whir.loading.remove();
                    if(data.code=="0"){
                        if (filtrate == "") {
                            frame().then(function(){
                                GET(audit.pageNumber, audit.pageSize);
                            });
                            $('.frame').html('删除成功');
                            audit.param["pageNumber"]=inx;
                            audit.param["pageSize"]=pageSize;
                            audit.param["searchValue"]="";
                        } else if (filtrate !== "") {
                            frame().then(function(){
                                audit.filtrates(audit.pageNumber, audit.pageSize);
                            });
                            $('.frame').html('删除成功');
                            audit._param["pageNumber"]=audit.pageNumber;
                        }
                        var thinput=$("thead input")[0];
                        thinput.checked =false;
                    }else if(data.code=="-1"){
                        frame();
                        $('.frame').html(data.message);
                    }
                })
            });
        },
        output:function(){
            //导出拉出list
            $("#output").click(function(){
                var l=$(window).width();
                var h=$(document.body).height();
                $(".file").css("position","fixed");
                $("#p").show();
                $("#p").css({"width":+l+"px","height":+h+"px"});
                $('.file').show();
                $(".into_frame").hide();
                var param={};
                param["function_code"]=audit.funcCode;
                oc.postRequire("post","/list/getCols","0",param,function(data){
                    if(data.code=="0"){
                        var message=JSON.parse(data.message);
                        var message=JSON.parse(message.tableManagers);
                        console.log(message);
                        $("#file_list_l ul").empty();
                        for(var i=0;i<message.length;i++){
                            $("#file_list_l ul").append("<li data-name='"+message[i].column_name+"'><div class='checkbox1'><input type='checkbox' value='' name='test'  class='check'  id='checkboxInput"
                                +i+1+"'/><label for='checkboxInput"+i+1+"'></label></div><span class='p15'>"+message[i].show_name+"</span></li>")
                        }
                        bianse();
                        $("#file_list_r ul").empty();
                    }else if(data.code=="-1"){
                        alert(data.message);
                    }
                })
            });
            function bianse(){
                $("#file_list_l li:odd").css("backgroundColor","#fff");
                $("#file_list_l li:even").css("backgroundColor","#ededed");
                $("#file_list_r li:odd").css("backgroundColor","#fff");
                $("#file_list_r li:even").css("backgroundColor","#ededed");
            }
            $("#hide_export").click(function(){
                $("#export_list").hide();
                $("#p").hide();
            });
            //导出提交的
            $("#file_submit").click(function(){
                //$("#export_list").show();
                //whir.loading.add("",0.5);//加载等待框
                var li=$("#file_list_r input[type='checkbox']").parents("li");
                var list_html="";
                if(li.length=="0"){
                    frame();
                    $('.frame').html('请把要导出的列移到右边');
                    return;
                }
                var allPage=Math.ceil(audit.allData/5000);
                var param={};
                var tablemanager=[];
                for(var i=0;i<li.length;i++){
                    var r=$(li[i]).attr("data-name");
                    var z=$(li[i]).children("span").html();
                    var param1={"column_name":r,"show_name":z};
                    tablemanager.push(param1);
                }
                tablemanager.reverse();
                $(".file").hide();
                $("#export_list_all ul").html("");
                for(var a=1;a<allPage+1;a++){
                    var start_num=(a-1)*5000 + 1;
                    var end_num="";
                    if (audit.allData < a*5000 ){
                        end_num = audit.allData
                    }else{
                        end_num = a*5000
                    }
                    list_html+= '<li>'
                        +'<span style="float: left">审核管理('+start_num+'~'+end_num+')</span>'
                        +'<span class="export_list_btn" data-page="'+a+'">导出</span>'
                        +'<span style="margin-right:10px;" class="state"></span>'
                        +'</li>'
                }
                $("#export_list_all ul").html(list_html);
                $("#export_list").show();
                $("#export_list_all").scrollTop(0);
                $("#export_list_all .export_list_btn").click(function () {
                    if($(this).hasClass("btn_active")){
                        return
                    }
                    var self=$(this);
                    var page=$(this).attr("data-page");
                    $(this).next().text("导出中...");
                    $(this).addClass("btn_active");
                    param["pageNumber"]=page;
                    param["pageSize"]="5000";
                    param["tablemanager"]=tablemanager;
                    param["searchValue"]="";
                    if(audit.filtrate==""){
                        param["list"]="";
                    }else if(audit.filtrate!==""){
                        param["list"]=audit.list;
                    }
                    oc.postRequire("post","/vipCheck/exportExecl","0",param,function(data){
                        if(data.code=="0"){
                            var message=JSON.parse(data.message);
                            var path=message.path;
                            path=path.substring(1,path.length-1);
                            self.attr("data-path",path);
                            self.next().text("导出完成");
                            self.html("<a href='/"+path+"' style='display: inline-block;width: 100%;height: 100%;color: #FFFFFF'>下载</a>");
                            self.css("backgroundColor","#637ea4");
                        }else if(data.code=="-1"){
                            self.removeClass("btn_active");
                            self.next().text("导出失败");
                        }
                    })
                });
            });
            $("#to_zip").click(function(){
                var a=$("#export_list_all ul li a");
                var URL="";
                var param={};
                if(a.length==0){
                    frame();
                    $('.frame').html("请先导出文件");
                    return;
                }
                for(var i=a.length-1;i>=0;i--){
                    if(i>0){
                        URL+=$(a[i]).attr("href")+","
                    }else{
                        URL+=$(a[i]).attr("href");
                    }
                }
                param.url=URL;
                param.name="审核管理";
                whir.loading.add("","0.5");
                oc.postRequire("post","/vip/exportZip", "",param, function(data){
                    if(data.code=="0"){
                        var path=JSON.parse(data.message).path;
                        path=path.substring(1,path.length-1);
                        $("#download_all a").prop("href","/"+path);
                        $("#p").css("zIndex","789");
                        whir.loading.remove();
                        $("#download_all").show();
                    }
                    if(data.code=="-1"){
                        whir.loading.remove();
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content: "操作失败"
                        });
                    }
                })
            });
            $('#file_close').click(function(){
                $("#p").hide();
                $('.file').hide();
                $('#file_submit').show();
                $('#download').hide();
            });
            $("#cancel_download,#X_download").click(function(){
                $("#p").css("zIndex","787");
                $("#download_all").hide();
                $("#download_all a").removeProp("href");
            });
        },
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
}
function checkStart(data){
    $("#end").attr("onclick","laydate({elem:'#end',min:'"+data+"',max: '2099-12-31 23:59:59',istime: false, format: 'YYYY-MM-DD',choose:checkEnd})");
}
function checkEnd(data){
    $("#start").attr("onclick","laydate({elem:'#start',min:'1900-01-01 00:00:00',max: '"+data+"',istime: false, format: 'YYYY-MM-DD',choose:checkStart})");
}
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
}