var oc = new ObjectControl();
var vip_ground_info={
    pageNumber:1,//删除默认第一页
    pageSize:10,//默认传的每页多少行
    count:"",
    titleArray:[],
    funcCode:"",
    inx:1,
    nowId:"",
    init:function(){
        var key_val=sessionStorage.getItem("key_val");//取页面的function_code
        key_val=JSON.parse(key_val);//取key_val的值
        this.funcCode=key_val.func_code;
        this.qjia();
        this.getGroup();
        this.slideLeft();
        this.selectShowType();
        this.setPageSize();
        this.inputGo();
    },
    setPage:function (container, count, pageindex,pageSize){
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
            inx = pageindex; //初始的页码
            $("#input-txt").val(inx);
            $(".foot-sum .zy").html("共 "+count+"页");
            oAlink[0].onclick = function() { //点击上一页
                if (inx == 1) {
                    return false;
                }
                inx--;
                dian(vip_ground_info.inx,vip_ground_info.pageSize);
                // setPage(container, count, inx,pageSize,funcCode,value);
                return false;
            };
            for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
                oAlink[i].onclick = function() {
                    inx = parseInt(this.innerHTML);
                    dian(vip_ground_info.inx,vip_ground_info.pageSize);
                    // setPage(container, count, inx,pageSize,funcCode,value);
                    return false;
                }
            }
            oAlink[oAlink.length - 1].onclick = function() { //点击下一页
                if (inx == count) {
                    return false;
                }
                inx++;
                dian(vip_ground_info.inx,vip_ground_info.pageSize);
                // setPage(container, count, inx,pageSize,funcCode,value);
                return false;
            }
        }()
    },
    selectShowType: function () {
       $("#select_show_type div").click(function(){
           $(this).addClass("active");
           $(this).siblings().removeClass("active");
       })
    },
    slideLeft:function(){
        $("#group_list li div").click(function(){
            if($(this).next().find("li").length>0){
                $(this).next().slideToggle();
                $(this).parent().find("i").toggle();
            }
        });
        $("#group_list li ul").on("click","li",function(){
            vip_ground_info.nowId=$(this).attr("id");
            $("#group_list li ul li").removeClass("active");
            $(this).addClass("active");
            vip_ground_info.getList(vip_ground_info.id)
        })
    },
    qjia:function(){  //页面加载调权限接口
        var param={};
        var self=this;
        param["funcCode"]=this.funcCode;
        oc.postRequire("post","/list/action","0",param,function(data){
            var message=JSON.parse(data.message);
            var actions=message.actions;
            titleArray=message.columns;
            //jurisdiction(actions);
            //jumpBianse();
            //InitialState();
            self.tableTh();
        })
    },
    tableTh:function(){ //table  的表头
        var TH="";
        for(var i=0;i<titleArray.length;i++){
            if(titleArray[i].show_name.trim()=='姓名'){
                TH+="<th>"+titleArray[i].show_name+"</th>"
            }else{
                TH+="<th>"+titleArray[i].show_name+"</th>"
            }
        }
        $("#tableOrder").after(TH);
    },
    superaddition:function(data,num){ //页面加载循环
        if(data.length==1&&num>1){
            pageNumber=num-1;
        }else{
            pageNumber=num;
        }
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
        for (var i = 0; i < data.length; i++) {
            var TD="";
            if(data[i].sex=="F"){
                data[i].sex="女"
            }else if(data[i].sex=="M"){
                data[i].sex="男"
            }
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
            $(".table tbody").append("<tr id='"+data[i].id+"''><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
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
    getList:function(){
        whir.loading.add("",0.5);//加载等待框
        var getListParam={};
        getListParam["id"]=vip_ground_info.nowId;
        getListParam["pageNumber"]=vip_ground_info.inx;
        getListParam["pageSize"]=vip_ground_info.pageSize;
        getListParam["type"]="list";
        getListParam["corp_code"]="C10000";
        oc.postRequire("post","/vipGroup/groupVips","",getListParam,function(data){
            if(data.code=="0"){
                $(".table tbody").empty();
                var messages=JSON.parse(data.message);
                var list=messages.all_vip_list;
                vip_ground_info.count=messages.pages;
                var pageNum = messages.pageNum;
                //var list=list.list;
                vip_ground_info.superaddition(list,pageNum);
                vip_ground_info.jumpBianse();
                vip_ground_info.setPage($("#foot-num")[0],vip_ground_info.count,pageNum,vip_ground_info.pageSize);
            }else if(data.code=="-1"){
                alert(data.message);
            }
        });
    },
    dian:function(inx,pageSize){
        vip_ground_info.getList(inx,pageSize);
    },
    jumpBianse:function (){
        $(document).ready(function(){//隔行变色
            $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
            $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
        })
    },
    getGroup:function(){
        var getGroupParam={};
        getGroupParam.corp_code="C10000";
        getGroupParam.search_value="";
        oc.postRequire("post","/vipGroup/getCorpGroups","",getGroupParam,function(data){
               var msg=JSON.parse(data.message);
               var list=JSON.parse(msg.list);
            for(var i= 0;i<list.length;i++){
                switch (list[i].group_type){
                    case "brand":
                       $("#group_list li[data-type='brand'] ul").append("<li id='"+list[i].id+"'>"+list[i].vip_group_name+"</li>");
                        break;
                    case "class":
                       $("#group_list li[data-type='class'] ul").append("<li id='"+list[i].id+"'>"+list[i].vip_group_name+"</li>");
                        break;
                    case "discount":
                       $("#group_list li[data-type='discount'] ul").append("<li id='"+list[i].id+"'>"+list[i].vip_group_name+"</li>");
                        break;
                    case "season":
                       $("#group_list li[data-type='season'] ul").append("<li id='"+list[i].id+"'>"+list[i].vip_group_name+"</li>");
                        break;
                    case "define":
                       $("#group_list li[data-type='define'] ul").append("<li id='"+list[i].id+"'>"+list[i].vip_group_name+"</li>");
                        break;
                }
            }
        })
    },
    setPageSize: function () {
        $("#page_row").click(function(){
            if("block" == $("#liebiao").css("display")){
                hideLi();
            }else{
                showLi();
            }
        });
        function showLi(){
            $("#liebiao").show();
        }
        function hideLi(){
            $("#liebiao").hide();
        }
        $("#liebiao li").each(function(i,v){
            $(this).click(function(){
                vip_ground_info.pageSize=$(this).attr('id');
                vip_ground_info.getList();
                $("#page_row").val($(this).html());
                hideLi();
            })
        })
    },
    inputGo:function(){
        $("#input-txt").keydown(function() {
            var event=window.event||arguments[0];
            var inx= this.value.replace(/[^0-9]/g, '');
            var inx=parseInt(inx);
            if (inx > vip_ground_info.count) {
                inx = vip_ground_info.count
            };
            if (inx > 0) {
                if (event.keyCode == 13) {
                    vip_ground_info.getList()
                }
            }
        });
    }
};
$(function(){
    vip_ground_info.init();
});