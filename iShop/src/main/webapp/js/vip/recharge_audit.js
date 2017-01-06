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
        count:"",
        init:function(){
            this.getFunCode();
            this.qjia();
            this.bind();
        },
        bind:function(){
            this.auditRefuse();
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
        dian:function(inx){//
            whir.loading.add("",0.5);//加载等待框
            //if(audit.value=="") {
                //oc.postRequire("get","/interfacers/list?pageNumber="+inx+"&pageSize="+audit.pageSize
                //    +"&funcCode="+audit.funcCode+"","","",function(data){
                audit.param["pageNumber"] = audit.inx;
                audit.param["pageSize"] = audit.pageSize;
                audit.param["funcCode"] = audit.funcCode;
                audit.param["searchValue"] = "";
                oc.postRequire("post", "/vipCheck/search", "0", audit.param, function (data) {
                    if (data.code == "0") {
                        $(".table tbody").empty();
                        var message = JSON.parse(data.message);
                        var list = JSON.parse(message.list);
                        audit.count = list.pages;
                        var list = list.list;
                        audit.superaddition(list, inx);
                        audit.jumpBianse();
                    } else if (data.code == "-1") {
                        // alert(data.message);
                    }
                });
            //}
            //}else if(audit.value!==""){
            //    audit.param["pageNumber"]=audit.inx;
            //    audit.param["pageSize"]=audit.pageSize;
            //    $("#search").val(audit.value);
            //    oc.postRequire("post","/interfacers/search","0",audit.param,function(data){
            //        if(data.code=="0"){
            //            var message=JSON.parse(data.message);
            //            var list=JSON.parse(message.list);
            //            audit.count=list.pages;
            //            var list=list.list;
            //            $(".table tbody").empty();
            //            if(list.length<=0){
            //                $(".table p").remove();
            //                $(".table").append("<p>没有找到与<span class='color'>“"+value+"”</span>相关的信息请重新搜索</p>");
            //            }else if(list.length>0){
            //                $(".table p").remove();
            //                audit.superaddition(list,inx);
            //                audit.jumpBianse();
            //            }
            //        }else if(data.code=="-1"){
            //            alert(data.message);
            //        }
            //    })
            //}
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

                for (var c=0;c<audit.titleArray.length;c++){
                    (function(j){
                        var code=audit.titleArray[j].column_name;
                            TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
                    })(c)
                }
                $(".table tbody").append("<tr id='"+data[i].billNO+"'><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
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
                    var messages=JSON.parse(data.message);
                    var list=JSON.parse(messages.list);
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
                $(window.parent.document).find('#iframepage').attr("src","/vip/recharge_audit_info.html");
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