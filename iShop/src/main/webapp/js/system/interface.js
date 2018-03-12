var oc = new ObjectControl();
var interFace={
    titleArray:[],
    value:"", //收索的关键词
    param:{}, //定义的对象
    _param:{}, //筛选定义的内容
    list:"",
    filtrate:"", //筛选的定义的值
    funcCode:"", //
    inx:1,
    cunt:"",
    pageSize:10,
    return_jump:JSON.parse(sessionStorage.getItem("return_jump")),  //获取本页面的状态
    init:function(){
        this.getFunctionCode();
        //this.InitialState();
        this.slideFilter();
        this.clearFilterValue();
        this.qjia();
        this.keyDownSearch();
        this.btnSearch();
        this.bombBox();
        this.refresh();
        this.toPage();
    },
    getFunctionCode:function(){
        var self=this;
        var key_val=sessionStorage.getItem("key_val");//取页面的function_code
        key_val=JSON.parse(key_val);
        self.funcCode=key_val.func_code;
    },
    InitialState:function(){
        var self=interFace;
        if(self.return_jump!==null){
            self.inx=self.return_jump.inx;
            self.pageSize=self.return_jump.pageSize;
            self.value=self.return_jump.value;
            self.filtrate=self.return_jump.filtrate;
            self.list=self.return_jump.list;
            self.param=JSON.parse(self.return_jump.param);
            self._param=JSON.parse(self.return_jump._param);

            if(self.pageSize==10){
                $("#page_row").val("10行/页");
            }
            if(self.pageSize==30){
                $("#page_row").val("30行/页");
            }
            if(self.pageSize==50){
                $("#page_row").val("50行/页");
            }
            if(self.pageSize==100){
                $("#page_row").val("100行/页");
            }
            if(self.value=="" && self.filtrate==""){
                self.param["pageNumber"]=self.inx;
                self.param["pageSize"]=self.pageSize;
                self.param["funcCode"]=self.funcCode;
                self.param["searchValue"]="";
                self.GET(self.inx,self.pageSize);
            }else if(self.value!==""){
                $("#search").val(self.value);
                self.POST(self.inx,self.pageSize);
            }else if(self.filtrate!==""){
                self.filtrates(self.inx,self.pageSize);
            }
        }else if (self.return_jump==null){
            if(self.value=="" && self.filtrate==""){
                self.param["pageNumber"]=self.inx;
                self.param["pageSize"]=self.pageSize;
                self.param["funcCode"]=self.funcCode;
                self.param["searchValue"]="";
                self.GET(self.inx,self.pageSize);
            }
        }
    },
    showLi:function(){
        $("#liebiao").show();
    },
    hideLi:function(){
    $("#liebiao").hide();
    },
    slideFilter:function(){ // 筛选框的展开与折叠
        $("#filtrate").click(function(){//点击筛选框弹出下拉框
            $(".sxk").slideToggle();
        });
        $("#pack_up").click(function(){//点击收回 取消下拉框
            $(".sxk").slideUp();
        });
    },
    clearFilterValue:function(){   //点击清空  清空input的value值
        $("#empty").click(function(){
            var input=$(".inputs input");
            for(var i=0;i<input.length;i++){
                input[i].value="";
            }
        });
    },
    setPage:function (container, count, pageindex,pageSize,funcCode,value) {
    var self=interFace;
    count==0?count=1:'';
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
    dian:function(inx){//
        whir.loading.add("",0.5);//加载等待框
        if(interFace.value==""){
            //oc.postRequire("get","/interfacers/list?pageNumber="+inx+"&pageSize="+interFace.pageSize
            //    +"&funcCode="+interFace.funcCode+"","","",function(data){
            interFace.param["pageNumber"]=interFace.inx;
            interFace.param["pageSize"]=interFace.pageSize;
            interFace.param["funcCode"]=interFace.funcCode;
            interFace.param["searchValue"]="";
            oc.postRequire("post","/interfacers/search","0",interFace.param,function(data){
                if(data.code=="0"){
                    $(".table tbody").empty();
                    var message=JSON.parse(data.message);
                    var list=JSON.parse(message.list);
                   interFace.cout=list.pages;
                    var list=list.list;
                    interFace.superaddition(list,inx);
                    interFace.jumpBianse();
                }else if(data.code=="-1"){
                    // alert(data.message);
                }
            });
        }else if(interFace.value!==""){
            interFace.param["pageNumber"]=interFace.inx;
            interFace.param["pageSize"]=interFace.pageSize;
            $("#search").val(interFace.value);
            oc.postRequire("post","/interfacers/search","0",interFace.param,function(data){
                if(data.code=="0"){
                    var message=JSON.parse(data.message);
                    var list=JSON.parse(message.list);
                   interFace.cout=list.pages;
                    var list=list.list;
                    $(".table tbody").empty();
                    if(list.length<=0){
                        $(".table p").remove();
                        $(".table").append("<p>没有找到与<span class='color'>“"+value+"”</span>相关的信息请重新搜索</p>");
                    }else if(list.length>0){
                        $(".table p").remove();
                        interFace.superaddition(list,inx);
                        interFace.jumpBianse();
                    }
                }else if(data.code=="-1"){
                    alert(data.message);
                }
            })
        }
    },
    superaddition:function(data,num){//页面加载循环
        if(data.length == 0){
            var len = $(".table thead tr th").length;
            var i;
            for(i=0;i<10;i++){
                $(".table tbody").append("<tr></tr>");
                for(var j=0;j<len;j++){
                    $($(".table tbody tr")[i]).append("<td></td>")
                }
            }
            $(".table tbody tr:nth-child(5)").append("<span style='position:absolute;left:50%;font-size: 15px;color:#999'>暂无内容</span>");
        }
        for (var i = 0; i < data.length; i++) {
            var TD="";
            if(num>=2){
                var a=i+1+(num-1)*interFace.pageSize;
            }else{
                var a=i+1;
            }
            for (var c=0;c<interFace.titleArray.length;c++){
                (function(j){
                    var code=interFace.titleArray[j].column_name;
                    TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
                })(c)
            }
            $(".table tbody").append("<tr id='"+data[i].id+"''><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
                + i
                + 1
                + "'/><label for='checkboxTwoInput"
                + i
                + 1
                + "'></label></div>"
                + "</td><td style='text-align:left;'>"
                + a
                + "</td>"
                +TD
                +"</tr>");
        }
        $(".th th:first-child input").removeAttr("checked");
        sessionStorage.removeItem("return_jump");
        whir.loading.remove();
    },
    jurisdiction:function (actions){  //权限配置
        $('#jurisdiction').empty();
        for(var i=0;i<actions.length;i++){
            if(actions[i].act_name=="add"){
                $('#jurisdiction').append("<li id='add'><a href='javascript:void(0);'><span class='icon-ishop_6-01'></span>新增</a></li>");
            }else if(actions[i].act_name=="delete"){
                $('#jurisdiction').append("<li id='remove'><a href='javascript:void(0);'><span class='icon-ishop_6-02'></span>删除</a></li>");
            }else if(actions[i].act_name=="edit"){
                $('#jurisdiction').append("<li id='compile' class='bg'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>编辑</a></li>");
            }
        }
    },
    qjia:function (){ //页面加载调权限接口
        var self=this;
        var param={};
        param["funcCode"]=self.funcCode;
        //whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post","/list/action","0",param,function(data){
            var message=JSON.parse(data.message);
            var actions=message.actions;
            self.titleArray=message.columns;
            self.jurisdiction(actions);
            self.jumpBianse();
            self.tableTh();
            self.InitialState();
        })
    },
    tableTh:function (){ //table  的表头
        var TH="";
        for(var i=0;i<interFace.titleArray.length;i++){
            TH+="<th>"+interFace.titleArray[i].show_name+"</th>"
        }
        $("#tableOrder").after(TH);
    },
    GET:function(){   //页面加载时list请求
        whir.loading.add("",0.5);//加载等待框
            //oc.postRequire("get","/interfacers/list?pageNumber="+interFace.inx+"&pageSize="+interFace.pageSize
            //    +"&funcCode="+interFace.funcCode+"","","",function(data){
        console.log(interFace.param);
        oc.postRequire("post","/interfacers/search","0",interFace.param,function(data){
                if(data.code=="0"){
                    $(".table tbody").empty();
                    var message=JSON.parse(data.message);
                    var list=JSON.parse(message.list);
                   interFace.cout=list.pages;
                   total = list.total;
                    var list=list.list;
                    interFace.superaddition(list,interFace.inx);
                    interFace.jumpBianse();
                    interFace.setPage($("#foot-num")[0],interFace.cout,interFace.inx,interFace.pageSize,interFace.funcCode,interFace.value,total);
                }else if(data.code=="-1"){
                    // alert(data.message);
                }
            });
    },
    //加载完成以后页面进行的操作
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
        //点击新增时页面进行的跳转
        $('#add').click(function(){
            $(window.parent.document).find('#iframepage').attr("src","/system/interface_add.html");
        });
        //点击编辑时页面进行的跳转
        $('#compile').click(function(){
            var tr=$("tbody input[type='checkbox']:checked").parents("tr");
            if(tr.length==1){
                id=$(tr).attr("id");
                sessionStorage.setItem("id",id);
                var return_jump={};//定义一个对象
                return_jump["inx"]=interFace.inx;//跳转到第几页
                return_jump["value"]=interFace.value;//搜索的值;
                return_jump["filtrate"]=interFace.filtrate;//筛选的值
                return_jump["param"]=JSON.stringify(interFace.param);//搜索定义的值
                return_jump["_param"]=JSON.stringify(interFace._param);//筛选定义的值
                return_jump["list"]=interFace.list;//筛选的请求的list;
                return_jump["pageSize"]=interFace.pageSize;//每页多少行
                sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
                $(window.parent.document).find('#iframepage').attr("src","/system/interface_edit.html");
            }else if(tr.length==0){
                interFace.frame();
                $('.frame').html("请先选择");
            }else if(tr.length>1){
                interFace.frame();
                $('.frame').html("不能选择多个");
            }
        });
        //双击跳转
        $(".table tbody tr").dblclick(function(){
            var id=$(this).attr("id");
            sessionStorage.setItem("id",id);
            var return_jump={};//定义一个对象
            return_jump["inx"]=interFace.inx;//跳转到第几页
            return_jump["value"]=interFace.value;//搜索的值;
            return_jump["filtrate"]=interFace.filtrate;//筛选的值
            return_jump["param"]=JSON.stringify(interFace.param);//搜索定义的值
            return_jump["_param"]=JSON.stringify(interFace._param);//筛选定义的值
            return_jump["list"]=interFace.list;//筛选的请求的list;
            return_jump["pageSize"]=interFace.pageSize;//每页多少行
            sessionStorage.setItem("return_jump",JSON.stringify(return_jump));
            if(id == "" || id == undefined){
                return ;
            }else{
                $(window.parent.document).find('#iframepage').attr("src","/system/interface_edit.html");
            }
        });
        //删除
        $("#remove").click(function(){
            var l=$(window).width();
            var h=$(document.body).height();
            var tr=$("tbody input[type='checkbox']:checked").parents("tr");
            if(tr.length==0){
                interFace.frame();
                $('.frame').html("请先选择");
                return;
            }
            $("#p").show();
            $("#tk").show();
            $("#p").css({"width":+l+"px","height":+h+"px"});
        })
    },
    //删除弹框
    frame:function (){
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
        return def
    },
    toPage:function(){  ////跳转页面的键盘按下事件
        var self=this;
        $("#input-txt").keydown(function() {
            var event=window.event||arguments[0];
            self.inx= this.value.replace(/[^0-9]/g, '');
            self.inx=parseInt(self.inx);
            if (self.inx > self.cout) {
                self.inx = self.cout
            };
            if (self.inx > 0) {
                if (event.keyCode == 13) {
                    if ( self.value == "" && self.filtrate == "") {
                        self.param["pageNumber"]=self.inx;
                        self.param["pageSize"]=self.pageSize;
                        self.param["funcCode"]=self.funcCode;
                        self.param["searchValue"]="";
                        self.GET(self.inx, self.pageSize);
                    } else if (self.value !== "") {
                        self.param["pageSize"] = self.pageSize;
                        self.param["pageNumber"]=self.inx;
                        self.POST(self.inx, self.pageSize);
                    } else if (self.filtrate !== "") {
                        self._param["pageSize"] = self.pageSize;
                        self._param["pageNumber"]=self.inx;
                        self.filtrates(self.inx, self.pageSize);
                    }
                };
            }
        })
    },
    keyDownSearch:function(){//鼠标按下时触发的收索
        var self=this;
        $("#search").keydown(function() {
            var event=window.event||arguments[0];
            self.inx=1;
            self.param["pageNumber"]=self.inx;
            self.param["pageSize"]=self.pageSize;
            self.param["funcCode"]=self.funcCode;
            if(event.keyCode == 13){
                self.value=this.value.trim();
                self.param["searchValue"]=self.value;
                self.POST();
            }
        });
    },
    btnSearch:function(){   //点击放大镜触发搜索
        var self=this;
        $("#d_search").click(function(){
            self.value=$("#search").val().replace(/\s+/g,"");
            self.inx=1;
            self.param["searchValue"]=self.value;
            self.param["pageNumber"]=self.inx;
            self.param["pageSize"]=self.pageSize;
            self.param["funcCode"]=self.funcCode;
            self.POST();
        });
    },
    //搜索的请求函数
    POST:function (){
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post","/interfacers/search","0",interFace.param,function(data){
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                interFace.cout=list.pages;
                total =list.total;
                var list=list.list;
                var actions=message.actions;
                $(".table tbody").empty();
                if(list.length<=0){
                    $(".table p").remove();
                    $(".table").append("<p>没有找到与<span class='color'>“"+interFace.value+"”</span>相关的信息请重新搜索</p>");
                }else if(list.length>0){
                    $(".table p").remove();
                    interFace.superaddition(list,interFace.inx);
                    interFace.jumpBianse();
                }
                interFace.setPage($("#foot-num")[0],interFace.cout,interFace.inx,interFace.pageSize,interFace.funcCode,interFace.value,total);
            }else if(data.code=="-1"){
                alert(data.message);
            }
        })
    },
    bombBox:function (){
        var self=this;
        //弹框关闭
        $("#X").click(function(){
            $("#p").hide();
            $("#tk").hide();
        });
        //取消关闭
        $("#cancel").click(function(){
            $("#p").hide();
            $("#tk").hide();
        });
        //弹框删除关闭
        $("#delete").click(function(){
            $("#p").hide();
            $("#tk").hide();
            var tr=$("tbody input[type='checkbox']:checked").parents("tr");
            for(var i=0,ID="";i<tr.length;i++){
                var r=$(tr[i]).attr("id");
                if(i<tr.length-1){
                    ID+=r+",";
                }else{
                    ID+=r;
                }
            }
            var param={};
            param["id"]=ID;
            oc.postRequire("post","/interfacers/delete","0",param,function(data){
                if(data.code=="0"){
                    if(self.value==""){
                       self.frame().then(function(){
                            self.GET();
                        });
                        $('.frame').html('删除成功');
                        self.param["pageNumber"]=self.inx;
                        self.param["pageSize"]=self.pageSize;
                        self.param["funcCode"]=self.funcCode;
                        self.param["searchValue"]="";
                    }else if(self.value!==""){
                        interFace.frame().then(function(){
                            self.POST();
                        });
                        $('.frame').html('删除成功');
                    }else if(data.code=="-1"){
                        self.frame();
                        $('.frame').html(data.message);
                    }
                }
            })
        });
    },
    refresh:function(){
        //刷新列表
        $(".icon-ishop_6-07").parent().click(function () {
            window.location.reload();
        });
    }
};
$(function(){
         interFace.init();
        $("#page_row").click(function(){
            if("block" == $("#liebiao").css("display")){
                interFace.hideLi();
            }else{
                interFace.showLi();
            }
        });
        $("#liebiao li").each(function(i,v){
            $(this).click(function(){
                interFace.pageSize=$(this).attr('id');
                if(interFace.value=="" && interFace.filtrate==""){
                    interFace.inx=1;
                    interFace.param["pageNumber"]=interFace.inx;
                    interFace.param["pageSize"]=interFace.pageSize;
                    interFace.param["funcCode"]=interFace.funcCode;
                    interFace.param["searchValue"]="";
                    interFace.GET(interFace.inx,interFace.pageSize);
                }else if(interFace.value!==""){
                    interFace.inx=1;
                    interFace.param["pageNumber"]=interFace.inx;
                    interFace.param["pageSize"]=interFace.pageSize;
                    interFace.POST(interFace.inx,interFace.pageSize);
                }else if(interFace.filtrate!==""){
                    interFace.inx=1;
                    interFace._param["pageNumber"]=interFace.inx;
                    interFace._param["pageSize"]=interFace.pageSize;
                    interFace.filtrates(interFace.inx,interFace.pageSize);
                }
                $("#page_row").val($(this).html());
                interFace.hideLi();
            });
        });
        $("#page_row").blur(function(){
            setTimeout(interFace.hideLi,200);
        });
    }
);
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