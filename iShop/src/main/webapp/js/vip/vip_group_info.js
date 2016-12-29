var oc = new ObjectControl();
var vip_group_info={
    pageNumber:1,//删除默认第一页
    pageSize:10,//默认传的每页多少行
    count:"",
    titleArray:[],
    funcCode:"",
    inx:1,
    nowId:"",
    nowList:[],
    staff_num:1,
    staff_next:false,
    isscroll:false,
    list_show:false,
    charts:{},
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
        this.checkList();
        this.showVipSelect();
        this.goToVipInfo();
        this.addLabel();
        this.export();
        this.chooseUser();
        this.switchModel();
        this.fsendMessage();
        this.transAnalysis();
        this.transShowModel();
        this.addChart();
    },
    switchModel:function(){
        $("#show_chart").click(function(){
            $(this).addClass("active").siblings().removeClass("active");
            $("#list_show").hide();
            $("#action").find(".action_r").hide();
            $("#chart_analyze").parent().show();
            vip_group_info.list_show=false;
        });
        $("#show_list").click(function(){
           if( !vip_group_info.list_show && vip_group_info.nowId!=""){
               vip_group_info.getList();
           }
            if(!$(this).hasClass("active")){
                vip_group_info.getList();
            }
            $(this).addClass("active").siblings().removeClass("active");
            $("#list_show").show();
            $("#action").find(".action_r").show();
            $("#chart_analyze").parent().hide();
            vip_group_info.list_show=true;
        })
    }
    ,
    setPage:function (container, count, pageindex,pageSize){
        count==0?count=1:'';
        var container = container;
        var count = count;
        var pageindex = pageindex;
        //var pageSize=pageSize;
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
            vip_group_info.inx = pageindex; //初始的页码
            $("#input-txt").val(vip_group_info.inx);
            //$(".foot-sum .zy").html("共 "+count+"页");
            oAlink[0].onclick = function() { //点击上一页
                if (vip_group_info.inx == 1) {
                    return false;
                }
                vip_group_info.inx--;
                vip_group_info.dian(vip_group_info.inx,vip_group_info.pageSize);
                // setPage(container, count, inx,pageSize,funcCode,value);
                return false;
            };
            for (var i = 1; i < oAlink.length - 1; i++) { //点击页码
                oAlink[i].onclick = function() {
                    vip_group_info.inx = parseInt(this.innerHTML);
                    vip_group_info.dian(vip_group_info.inx,vip_group_info.pageSize);
                    // setPage(container, count, inx,pageSize,funcCode,value);
                    return false;
                }
            }
            oAlink[oAlink.length - 1].onclick = function() { //点击下一页
                if (vip_group_info.inx == count) {
                    return false;
                }
                vip_group_info.inx++;
                vip_group_info.dian(vip_group_info.inx,vip_group_info.pageSize);
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
            vip_group_info.inx=1;
            vip_group_info.pageSize=10;
            vip_group_info.nowId=$(this).attr("id");
            $("#group_list li ul li").removeClass("active");
            $(this).addClass("active");
            if(!vip_group_info.list_show){
            vip_group_info.getChartsData();
                return;
            }
            vip_group_info.getList(vip_group_info.nowId);
        })
    },
    //权限配置
    jurisdiction:function(actions){
        $('#jurisdiction').empty();
        for(var i=0;i<actions.length;i++){
            if(actions[i].act_name=="add"){
                $('#jurisdiction').append("<li id='add'><a href='javascript:void(0);'><span class='icon-ishop_6-01'></span>新增</a></li>");
            }else if(actions[i].act_name=="delete"){
                $('#jurisdiction').append("<li id='remove'><a href='javascript:void(0);'><span class='icon-ishop_6-02'></span>删除</a></li>");
            }else if(actions[i].act_name=="edit"){
                $('#jurisdiction').append("<li id='compile' class='bg'><a href='javascript:void(0);'><span class='icon-ishop_6-03'></span>编辑</a></li>");
            }else if(actions[i].act_name=="chooseUser"){
                $('.more_down').append("<div id='chooseUser' style='font-size: 10px'>设置所属导购</div>");
            }else if(actions[i].act_name=="output"){
                $("#filtrate").before("<li id='leading_out' title='因会员数据量大，请筛选后再导出'> <span class='icon-ishopwebicon_6-24'></span> 导出</li>")
            }else if(actions[i].act_name=="input"){
                $("#more_down").append("<div id='guide_into'>导入</div>");
            }else if(actions[i].act_name=="addLabel"){
                $("#more_down").append("<div style='font-size:10px;' id='batch_label'>批量贴标签</div>");
            }else if(actions[i].act_name=="fsendMessage"){
                $("#more_down").append("<div style='font-size:10px;' id='fsendMessage'>发送消息</div>");
            }
        }
    },
    qjia:function(){  //页面加载调权限接口
        var param={};
        var self=this;
        param["funcCode"]=this.funcCode;
        oc.postRequire("post","/list/action","0",param,function(data){
            var message=JSON.parse(data.message);
            var actions=message.actions;
            titleArray=message.columns;
            self.jurisdiction(actions);
            //jumpBianse();
            //InitialState();
            self.tableTh();
        })
    },
    tableTh:function(){ //table  的表头
        var TH="";
        for(var i=0;i<titleArray.length;i++){
            if(titleArray[i].show_name.trim()=='姓名'){
                TH+="<th>"+titleArray[i].show_name+"</th>"+'<th style="width: 20px"></th>'
            }else{
                TH+="<th>"+titleArray[i].show_name+"</th>"
            }
        }
        $("#tableOrder").after(TH);
        var len = $(".table thead tr th").length;
        var i;
        for(i=0;i<10;i++){
            $(".table tbody").append("<tr></tr>");
            for(var j=0;j<len;j++){
                $($(".table tbody tr")[i]).append("<td></td>");
            }
        }
        $(".table tbody tr:nth-child(5)").append("<span style='position:absolute;left:54%;font-size: 15px;color:#999'></span>");
        vip_group_info.jumpBianse();
    },
    superaddition:function(data,num){ //页面加载循环
        if(data.length==1&&num>1){
            pageNumber=num-1;
        }else{
            pageNumber=num;
        }
        vip_group_info.nowList=data;
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
            var wx='';
            if(data[i].sex=="F"){
                data[i].sex="女"
            }else if(data[i].sex=="M"){
                data[i].sex="男"
            }
            if(num>=2){
                var a=i+1+(num-1)*vip_group_info.pageSize;
            }else{
                var a=i+1;
            }
            if(data[i].open_id){
                wx="<td><span class='icon-ishop_6-22'style='color:#8ec750'></span></td>";
            }else{
                wx="<td><span class='icon-ishop_6-22'style='color:#cdcdcd'></span></td>";
            }
            for (var c=0;c<titleArray.length;c++){
                (function(j){
                    var code=titleArray[j].column_name;
                    if(code=='vip_name'){
                        TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>"+wx;
                }else {
                        TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
                    }
                })(c)
            }
            $(".table tbody").append("<tr id='"+data[i].vip_id+"' data-storeid='"+data[i].store_id+"'><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
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
    },
    getList:function(){
        whir.loading.add("",0.5);//加载等待框
        var getListParam={};
        getListParam["id"]=vip_group_info.nowId;
        getListParam["pageNumber"]=vip_group_info.inx;
        getListParam["pageSize"]=vip_group_info.pageSize;
        getListParam["type"]="list";
        getListParam["corp_code"]="C10000";
        oc.postRequire("post","/vipGroup/groupVips","",getListParam,function(data){
            if(data.code=="0"){
                $(".table tbody").empty();
                var messages=JSON.parse(data.message);
                var list=messages.all_vip_list;
                vip_group_info.count=messages.pages;
                var pageNum = messages.pageNum;
                //var list=list.list;
                vip_group_info.superaddition(list,pageNum);
                vip_group_info.jumpBianse();
                vip_group_info.setPage($("#foot-num")[0],vip_group_info.count,pageNum,vip_group_info.pageSize);
            }else if(data.code=="-1"){
                alert(data.message);
            }
        });
    },
    dian:function(inx,pageSize){
        vip_group_info.getList(inx,pageSize);
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
                       $("#group_list li[data-type='brand'] ul").append("<li id='"+list[i].id+"' data-code='"+list[i].corp_code+"' data-group-code='"+list[i].vip_group_code+"'>"+list[i].vip_group_name+"</li>");
                        break;
                    case "class":
                       $("#group_list li[data-type='class'] ul").append("<li id='"+list[i].id+"' data-code='"+list[i].corp_code+"' data-group-code='"+list[i].vip_group_code+"'>"+list[i].vip_group_name+"</li>");
                        break;
                    case "discount":
                       $("#group_list li[data-type='discount'] ul").append("<li id='"+list[i].id+"' data-code='"+list[i].corp_code+"' data-group-code='"+list[i].vip_group_code+"'>"+list[i].vip_group_name+"</li>");
                        break;
                    case "season":
                       $("#group_list li[data-type='season'] ul").append("<li id='"+list[i].id+"' data-code='"+list[i].corp_code+"' data-group-code='"+list[i].vip_group_code+"'>"+list[i].vip_group_name+"</li>");
                        break;
                    case "define":
                       $("#group_list li[data-type='define'] ul").append("<li id='"+list[i].id+"' data-code='"+list[i].corp_code+"' data-group-code='"+list[i].vip_group_code+"'>"+list[i].vip_group_name+"</li>");
                        break;
                }
                if(list.length>0){
                    $("#group_list li[data-type='"+list[0].group_type+"'] ul").show();
                    $("#group_list li[data-type='"+list[0].group_type+"'] ul").children().eq(0).addClass("active");
                    $("#group_list li[data-type='"+list[0].group_type+"']").find("div i").show();
                    vip_group_info.nowId=$("#group_list li[data-type='"+list[0].group_type+"'] ul").children().eq(0).attr("id")
                }
            }
            vip_group_info.getChartsData();
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
                vip_group_info.pageSize=$(this).attr('id');
                vip_group_info.inx=1;
                vip_group_info.getList();
                $("#page_row").val($(this).html());
                hideLi();
            })
        })
    },
    inputGo:function(){
        $("#input-txt").keydown(function() {
            var event=window.event||arguments[0];
            var index= this.value.replace(/[^0-9]/g, '');
            var index=parseInt(index);
            if (index > vip_group_info.count) {
                index = vip_group_info.count;
            };
            if (index > 0) {
                if (event.keyCode == 13) {
                    vip_group_info.inx=index;
                    vip_group_info.getList()
                }
            }
        });
    },
    checkList:function () {
        //全选
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
        }
        $("#checkboxTwoInput0").click(function(){
           if(this.checked==true) { checkAll('test'); } else { clearAll('test'); }
        })
    },
    showVipSelect:function(){
        //弹框关闭
        $("#screen_wrapper_close").click(function(){
            $("#screen_wrapper").hide();
            $("#p").hide();
        });
    },
    goToVipInfo:function(){
        $("#table").on("click","tr td:not(:first-child)",function(){
            if(vip_group_info.nowList.length==0){
                return
            }
            var ID=$(this).parent().attr("id");
            sessionStorage.setItem("id",ID);
            sessionStorage.setItem("corp_code","C10000");
            window.open("http://"+window.location.host+"/navigation_bar.html?url=/vip/vip_data.html&func_code=F0010");
        })
    },
    //批量贴标签
    addLabel:function(){
        $("#more_down").on("click","#batch_label",function () {
            var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
            if(tr.length == 0){
                frame();
                $(".frame").html("请选择会员");
            }else if(tr.length>0) {
                var arr=whir.loading.getPageSize();
                var left=(arr[0]-$("#batch_label_wrapper").width())/2;
                var tp=(arr[3]-$("#batch_label_wrapper").height())/2;
                $("#batch_label_wrapper").css({"left":+left+"px","top":+tp+"px","position":"fixrd"});
                $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
                $("#p").show();
                $("#batch_label_wrapper").show();
                $("#batch_label_hot").empty();
                var param={};
                var vip_id="";
                for(var j=0;j<tr.length;j++){
                    vip_id+=$(tr[j]).attr("id")+",";
                }
                param['vip_id']=vip_id;
                param["corp_code"]="C10000"
                oc.postRequire("post","/VIP/label/findHotViplabel","",param,function(data){
                    if(data.code=="0"){
                        var msg=JSON.parse(data.message);
                        msg=JSON.parse(msg.list);
                        var html="";
                        var classname="";
                        for(var i=0;i<msg.length;i++){
                            if(msg[i].label_type=="user"){
                                classname="label_u";
                            }else{
                                classname="label_g";
                            }
                            html+="<span data-id="+msg[i].label_id+" class="+classname+" id="+i+">"+msg[i].label_name+"</span>"
                        }
                        $("#batch_label_hot").append(html);
                    }
                })
            }
        });
        $("#close_label").click(function () {
            $("#batch_label_wrapper").hide();
            $("#p").hide();
        });
        //批量标签tabel页切换
        $("#label_title li").click(function () {
            $(this).children("a").addClass("liactive");
            $(this).siblings("li").children("a").removeClass("liactive");
            if($(this).children("a").html()=="热门"){
                $("#batch_label_hot").show();
                $("#batch_label_gov").hide();
            }else if($(this).children("a").html()=="官方"){
                $("#batch_label_gov").show();
                $("#batch_label_hot").hide();
                if($("#batch_label_gov span").length>0){
                    return ;
                }else {
                    $("#batch_label_gov").empty();
                    var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
                    var param={};
                    var vip_id="";
                    var page=1;
                    for(var j=0;j<tr.length;j++){
                        vip_id+=$(tr[j]).attr("id")+",";
                    }
                    param["type"]="2";
                    param['vip_id']=vip_id;
                    param["corp_code"]="C10000";
                    param['pageNumber']=page;
                    param['searchValue']="";
                    oc.postRequire("post","/VIP/label/findViplabelByType ","",param,function(data){
                        if(data.code=="0"){
                            var msg=JSON.parse(data.message);
                            var list=JSON.parse(msg.list)
                            var hasNextPage=list.hasNextPage;
                            list=list.list;
                            var html="";
                            var classname="";
                            for(var i=0;i<list.length;i++){
                                classname="label_g";
                                html+="<span  draggable='true' data-id="+list[i].id+" class="+classname+" id='"+i+"g'>"+list[i].label_name+"</span>"
                            }
                            $("#batch_label_gov").append(html);
                        }
                    })
                }
            }
        });
        //批量添加标签
        $("#batch_label_box").on("click","span",function () {
            var that=$(this);
            vip_group_info.addViplabel(that,"");
        });
//按钮添加
        $("#label_add").click(function () {
            var btn=$("#batch_search_label").val().trim();
            if(btn==""){
                return ;
            }
            vip_group_info.addViplabel("",btn);
        });
//搜索标签
        $('#batch_search_label').bind('input propertychange', function() {
            var thatFun=arguments.callee;
            var that=this;
            $(this).unbind("input propertychange",thatFun);
            $(".batch_search_label ul").empty();
            page=1;
            vip_group_info.searchHotlabel();
            setTimeout(function(){$(that).bind("input propertychange",thatFun)},0);
        });
    },
    searchHotlabel:function () {
        var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
        var param={};
        var vip_id="";
        for(var j=0;j<tr.length;j++){
            vip_id+=$(tr[j]).attr("id")+",";
        }
        param["corp_code"]="C10000";
        param['pageNumber']=page;
        param['vip_id']=vip_id;
        param['searchValue']=$('#batch_search_label').val().replace(/\s+/g,"");
        param['type']="1";
        oc.postRequire("post","/VIP/label/findViplabelByType","",param,function(data){
            if(data.code=="0"){
                var msg=JSON.parse(data.message);
                msg=JSON.parse(msg.list);
                var hasNextPage=msg.hasNextPage;
                var list=msg.list;
                var html="";
                if(list.length>0){
                    $(".batch_search_label ul").show();
                    for(var i=0;i<list.length;i++){
                        html+="<li data-id="+list[i].id+">"+list[i].label_name+"</li>"
                    }
                    $(".batch_search_label ul").append(html);
                }else {
                    $(".batch_search_label ul").hide();
                }
                if(hasNextPage==true){

                }
            }
            //搜索下拉点击事件
            $(".batch_search_label ul li").click(function () {
                $("#batch_search_label").val($(this).html());
            })
        })
    },
    addViplabel:function(obj,btn) {
        var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
        var classname=$(obj).attr("class");
        var param={};
        var list=[];
        for(var i=0;i<tr.length;i++){
            var code=$(tr[i]).attr("id");
            var store_id=$(tr[i]).attr("data-storeid");
            var params={
                "vip_code":code,
                "store_code":store_id
            }
            list.push(params);
        }
        if(btn!==""){
            param['label_name']=btn;
        }else{
            param['label_name']=$(obj).html();
        }
        param["corp_code"]="C10000";
        param['label_id']="";
        param['list']=list;
        oc.postRequire("post","/VIP/label/addBatchRelViplabel","",param,function(data){
            if(data.code=="0"){
                if(btn!==""){
                    var len_g = $("#batch_label_gov span").length;
                    var len_h = $("#batch_label_hot span").length;
                    for(var i=0;i<len_g;i++){
                        if(btn == $($("#batch_label_gov span")[i]).html()){
                            $($("#batch_label_gov span")[i]).removeClass("label_g").addClass("label_g_active");
                        }
                    }
                    for(var j=0;j<len_h;j++){
                        if(btn == $($("#batch_label_hot span")[j]).html()){
                            var name = $($("#batch_label_hot span")[j]).attr("class");
                            if(name=="label_u"){
                                $($("#batch_label_hot span")[j]).removeClass("label_u").addClass("label_u_active");
                            }else if(name=="label_g"){
                                $($("#batch_label_hot span")[j]).removeClass("label_g").addClass("label_g_active");
                            }
                        }
                    }
                    frame();
                    $(".frame").html("添加成功");
                }
                if(classname=="label_u"){
                    $(obj).removeClass(classname).addClass("label_u_active");
                }else if(classname=="label_g"){
                    $(obj).removeClass(classname).addClass("label_g_active");
                }
            }else if(data.code=="-1"){
                frame();
                $('.frame').html('添加失败');
            }
        })
    },
    export: function () {
        $(".action_r ul").on("click","#leading_out",function(){
            var l=$(window).width();
            var h=$(document.body).height();
            $("#p").show();
            $("#p").css({"width":+l+"px","height":+h+"px"});
            $('.file').show();
            $(".into_frame").hide();
            var param={};
            param["function_code"]=vip_group_info.funcCode;
            whir.loading.add("",0.5);//加载等待框
            oc.postRequire("post","/list/getCols","0",param,function(data){
                if(data.code=="0"){
                    var message=JSON.parse(data.message);
                    var message=JSON.parse(message.tableManagers);
                    $("#file_list_l ul").empty();
                    for(var i=0;i<message.length;i++){
                        $("#file_list_l ul").append("<li data-name='"+message[i].column_name+"'><div class='checkbox1'><input type='checkbox' value='' name='test'  class='check'  id='checkboxInput"
                            +i+1+"'/><label for='checkboxInput"+i+1+"'></label></div><span class='p15'>"+message[i].show_name+"</span></li>")
                    }
                    vip_group_info.bianse();
                    $("#file_list_r ul").empty();
                    whir.loading.remove();//移除加载框
                }else if(data.code=="-1"){
                    alert(data.message);
                    whir.loading.remove();//移除加载框
                }
            })
        });
        //导出关闭按钮
        $('#file_close').click(function(){
            $("#p").hide();
            $('.file').hide();
            $('#file_submit').show();
            $('#download').hide();
        });
        //导出提交的
        $("#file_submit").click(function(){
            var li=$("#file_list_r input[type='checkbox']").parents("li");
            //var value = $("#search").val().trim();
            var param={};
            var tablemanager=[];
            if(li.length=="0"){
                frame();
                $('.frame').html('请把要导出的列移到右边');
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
            param["type"]="group";
            param["id"]=vip_group_info.nowId;
            param["searchValue"]="";
            //if(filtrate==""){
                param["screen_message"]="";
            //}
            //else if(filtrate!==""){
            //    var list={};
            //    list["brand_code"]=message.cache.brand_codes;
            //    list["store_code"]=message.cache.store_codes;
            //    list["area_code"]=message.cache.area_codes;
            //    list["user_code"]=message.cache.user_codes;
            //    param["screen_message"]=list;
            //}
            if(vip_group_info.nowId==""){
                frame();
                $('.frame').html('当前不可操作');
                return;
            }
            whir.loading.add("",0.5);//加载等待框
            oc.postRequire("post","/vip/exportExecl","0",param,function(data){
                if(data.code=="0"){
                    var message=JSON.parse(data.message);
                    var path=message.path;
                    var path=path.substring(1,path.length-1);
                    $('#download').html("<a href='/"+path+"'>下载文件</a>");
                    $('#download').addClass("download");
                    $('#file_submit').hide();
                    $('#download').show();
                    //导出关闭按钮
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
                whir.loading.remove();//移除加载框
            })
        })
    },
    bianse:function (){
        $("#file_list_l li:odd").css("backgroundColor","#fff");
        $("#file_list_l li:even").css("backgroundColor","#ededed");
        $("#file_list_r li:odd").css("backgroundColor","#fff");
        $("#file_list_r li:even").css("backgroundColor","#ededed");
    },
    chooseUser:function(){
        //选择导购
        $(".action_r ul").on("click","#chooseUser",function(){
            var tr=$("#table tbody input[type='checkbox']:checked").parents("tr");
            var arr=whir.loading.getPageSize();
            var left=(arr[0]-$(".screen_area").width())/2;
            var tp=(arr[3]-$(".screen_area").height())/2+30;
            var staff_num=1;
            var mark="staff";
            $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
            $("#choose_staff").css({"left":+left+"px","top":+tp+"px","position":"fixed"});
            if(tr.length==0){
                frame();
                $('.frame').html('请先选择会员');
            }else if(tr.length>0){
                var storeid=$(tr[0]).attr("data-storeid");
                for(var i=0;i<tr.length-1;i++){
                    if($(tr[i]).attr("data-storeid")!==$(tr[i+1]).attr("data-storeid")){
                        frame();
                        $('.frame').html('请选择相同店铺下的会员');
                        return;
                    }
                }
                $("#p").show();
                $("#choose_staff").show();
                vip_group_info.isscroll=false;
                $("#choose_staff .screen_content_l").unbind("scroll");
                $("#choose_staff .screen_content_l ul").empty();
                $("#choose_staff .screen_content_r ul").empty();
                vip_group_info.getstafflist(staff_num,mark);
            }
        });
    },
    //获取员工列表
    getstafflist:function (a,b){
        var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
        var pageSize=20;
        var pageNumber=a;
        var _param={};
        if(b=="staff"){
            _param['store_code']=$(tr[0]).attr("data-storecode");//店铺
            _param['searchValue']=$("#search_staff").val().trim();//搜索值
        }else{
            _param['area_code']=message.cache.area_codes;
            _param['brand_code']=message.cache.brand_codes;
            _param['store_code']=message.cache.store_codes;
            _param['searchValue']=$('#staff_search').val();
        }
        _param["corp_code"]="C10000";
        _param['pageNumber']=pageNumber;
        _param['pageSize']=pageSize;
        whir.loading.add("",0.5);//加载等待框
        $("#mask").css("z-index","10002");
        oc.postRequire("post","/user/selectUsersByRole","", _param, function(data) {
            if (data.code == "0"){
                var message=JSON.parse(data.message);
                var list=JSON.parse(message.list);
                var hasNextPage=list.hasNextPage;
                var cout=list.pages;
                var list=list.list;
                var staff_html = '';
                if (list.length == 0){

                } else {
                    if(list.length>0){
                        for (var i = 0; i < list.length; i++) {
                            staff_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].user_code+"' data-phone='"+list[i].phone+"' data-storename='"+list[i].user_name+"' name='test'  class='check'  id='checkboxFourInput"
                                + i
                                + a
                                + 1
                                + "'/><label for='checkboxFourInput"
                                + i
                                + a
                                + 1
                                + "'></label></div><span class='p16'>"+list[i].user_name+"\("+list[i].user_code+"\)</span></li>"
                        }
                    }
                }
                if(hasNextPage==true){
                    vip_group_info.staff_num++;
                    vip_group_info.staff_next=false;
                }
                if(hasNextPage==false){
                    vip_group_info.staff_next=true;
                }
                $("#screen_staff .screen_content_l ul").append(staff_html);
                $("#choose_staff .screen_content_l ul").append(staff_html);
                if(!vip_group_info.isscroll){
                    $("#screen_staff .screen_content_l").bind("scroll",function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight=$(this).height();
                        if(nScrollTop + nDivHight >= nScrollHight){
                            if(vip_group_info.staff_next){
                                return;
                            }
                            getstafflist(vip_group_info.staff_num);
                        };
                    })
                }
                vip_group_info.isscroll=true;
                $("#screen_staff .screen_content_l li:odd").css("backgroundColor","#fff");
                $("#screen_staff .screen_content_l li:even").css("backgroundColor","#ededed");
                $("#screen_staff .screen_content_r li:odd").css("backgroundColor","#fff");
                $("#screen_staff .screen_content_r li:even").css("backgroundColor","#ededed");
                var li=$("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
                for(var k=0;k<li.length;k++){
                    $("#screen_staff .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true");
                }
                whir.loading.remove();//移除加载框
            } else if (data.code == "-1") {
                alert(data.message);
            }
        });
        //导购关闭
        $("#close_choose_staff").click(function(){
            $("#choose_staff").hide();
            $("#p").hide();
        });
        //分配导购确定
        $("#choose_staff .screen_que").click(function () {
            var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
            var li = $("#choose_staff .screen_content_r ul li");
            if(li.length>1){
                frame();
                $('.frame').html('只能分配一个导购');
            }else if(li.length==0){
                frame();
                $('.frame').html('没有选择导购');
            }else  if(li.length==1){
                $("#choose_staff").hide();
                $("#p").hide();
                var staff_name = $("#choose_staff .screen_content_r ul li span").html();
                var user_code = $("#choose_staff .screen_content_r ul li").attr("id");
                var store_code = $(tr[0]).attr("data-storeid");
                var vip_id = "";
                for(var i=0;i<tr.length;i++){
                    if(i<tr.length-1){
                        vip_id += $(tr[i]).children("td:nth-child(3)").text()+",";
                    }else{
                        vip_id += $(tr[i]).children("td:nth-child(3)").text();
                    }
                }
                var _param={};
                _param["corp_code"]="C10000";
                _param["store_code"]=store_code;
                _param["vip_id"]=vip_id;
                _param["user_code"]=user_code;
                whir.loading.add("",0.5);//加载等待框
                oc.postRequire("post","/vip/changeVipsUser","",_param,function (data) {
                    if(data.code=="0"){
                        for(var i=0;i<tr.length;i++){
                            $(tr[i]).children("td:nth-child(9)").html(staff_name);
                        }
                        whir.loading.remove();//移除加载框
                    }else if(data.code == "-1"){
                        console.log(data.message);
                    }
                })
            }
        })
    },
    fsendMessage:function(){
        $("#more").on("click","#fsendMessage",function(){
            if(vip_group_info.nowId==""){
                frame();
                $('.frame').html('当前不可操作');
                return;
            }
            sessionStorage.removeItem("id");
            var group_vip={};
            group_vip.corp_code=$("#"+vip_group_info.nowId).attr("data-code");
            group_vip.group_code=$("#"+vip_group_info.nowId).attr("data-group-code");
            sessionStorage.setItem("group_vip",JSON.stringify(group_vip));
            window.open("http://"+window.location.host+"/navigation_bar.html?url=/vip/message_add.html&func_code=F0041");
        });
    },
    transAnalysis:function(){
        $(".chart_analyze_condition i,.chart_analyze_condition span").click(function(){
            $(this).siblings("ul").toggle();
        });
        $(".chart_analyze_condition ul li").click(function(){
            var val=$(this).text();
            $(this).parent().siblings("span").html(val);
            $(this).parent().hide();
        })
    },
    transShowModel:function(){
        $(".chart_list_icon").click(function(){
            if($(this).parent().siblings("li").is(":hidden")){
                $(this).parent().siblings("li").show();
                $(this).parent().siblings(".data_table").hide();
            }else {
                $(this).parent().siblings("li").hide();
                $(this).parent().siblings(".data_table").show();
            }
        })
    },
    getChartsData: function () {
        var param={};
            param.type="vipGroup";
            param.corp_code=$("#"+vip_group_info.nowId).attr("data-code");
            param.vip_group_code=$("#"+vip_group_info.nowId).attr("data-group-code");
            oc.postRequire("post","/vipAnalysis/vipChart","0",param,function (data) {
               vip_group_info.allChartsData=JSON.parse(data.message);
               vip_group_info.getCharts()
            });
    },
    getCharts:function(order){
        var key_val=sessionStorage.getItem("key_val");//取页面的function_code
        key_val=JSON.parse(key_val);
        var funcCode=key_val.func_code;
        var param=null;
        if(order!==""&&order!==undefined){
            var id=$("#chart_analyze").attr("data-id");
            param={
                "corp_code":"C10000",
                "function_code":funcCode,
                "type":"order",
                "id":id,
                "order":order
            };
        }else {
           param={
                "corp_code":"C10000",
                "function_code":funcCode,
                "type":"show"
            };
        }
        oc.postRequire("post","/privilege/vip/chartOrder","0",param,function (data) {
            if(data.code == 0){
                if(data.message=="success"){
                    console.log("成功") ;
                }else{
                    var list = JSON.parse(data.message);
                    if(list.length>0){
                        var l = list.length-1;
                        var id = list[l].id;
                        if (id !== undefined) {
                            $("#chart_analyze").attr("data-id", id);
                            var column = JSON.parse(list[l].column_name);
                            for (var i = 0; i < column.length; i++) {
                                var a = column[i];
                                $(".chart_module").each(function () {
                                    if (a == $(this).attr("data-id")) {
                                        $(this).show();
                                        var ID = $(this).attr("data-id");
                                        vip_group_info.init_chart(ID);
                                        $("#add_chart").before($(this));
                                    }
                                });
                            }
                        }
                    }else{

                    }
                }
            }else if(data.code == -1){
                console.log(data.message);
            }
        });
    },
    init_chart:function(id) {
        var myChart = echarts.init(document.getElementById(id));
        var option_pie = {
            color:['#9AD8DB', '#8BC0C8', '#7BA8B5', '#6C8FA2','#5C778F','#4D5F7C','#444960','#2C3244'] ,
            tooltip : {
                textStyle : {
                    fontSize : '10'
                },
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            legend: {
                show:'true',
                orient : 'vertical',
                x : 'left',
                y:'center',
                data:[]
            },
            series : [
                {   name:'消费分类',
                    type:'pie',
                    radius : ['50%', '60%'],
                    avoidLabelOverlap: false,
                    label: {
                        normal: {
                            show: false,
                            position: 'center'
                        },
                        emphasis: {
                            show: true,
                            textStyle: {
                                fontSize: '20',
                                fontWeight: 'bold'
                            }
                        }
                    },
                    labelLine: {
                        normal: {
                            show: false
                        }
                    },
                    data:[]
                }
            ]
        };
        var option_radar_week = {
            color:['#A7DADE'],
            axis:{
                areaStyle:{
                    color:['red']
                }
            },
            tooltip : {
                trigger: 'axis'
            },
            radar : [
                {
                    indicator : [
                        {text : '周一', max  : 100},
                        {text : '周二', max  : 100},
                        {text : '周三', max  : 100},
                        {text : '周四', max  : 100},
                        {text : '周五', max  : 100},
                        {text : '周六', max  : 100},
                        {text : '周日', max  : 100}
                    ],
                    radius : 100,
                    splitNumber: 8,
                    startAngle: 45,
                    splitArea : {
                        show : true,
                        areaStyle : {
                            color: '#fff'
                        }
                    }
                }
            ],
            series : [
                {
                    symbol:'circle',
                    type: 'radar',
                    tooltip: {
                        trigger: 'item'
                    },
                    itemStyle: {normal: {areaStyle: {type: 'default'}}},
                    symbolSize:'0',
                    data : [
                        {
                            value : [97, 42, 88, 94, 90, 86,69,99],
                            name : '一周数据'
                        }
                    ]
                }
            ]
        };
        var option_radar_month = {
            color:['#A7DADE'],
            axis:{
                areaStyle:{
                    color:['red']
                }
            },
            tooltip : {
                trigger: 'axis'
            },
            radar : [
                {
                    indicator : [
                        {text : '一月', max  : 100},
                        {text : '二月', max  : 100},
                        {text : '三月', max  : 100},
                        {text : '四月', max  : 100},
                        {text : '五月', max  : 100},
                        {text : '六月', max  : 100},
                        {text : '七月', max  : 100},
                        {text : '八月', max  : 100},
                        {text : '九月', max  : 100},
                        {text : '十月', max  : 100},
                        {text : '十一月', max  : 100},
                        {text : '十二月', max  : 100}
                    ],
                    radius : 100,
                    splitNumber: 8,
                    startAngle: 45,
                    splitArea : {
                        show : true,
                        areaStyle : {
                            color: '#fff'
                        }
                    }
                }
            ],
            series : [
                {
                    symbol:'circle',
                    type: 'radar',
                    tooltip: {
                        trigger: 'item'
                    },
                    itemStyle: {normal: {areaStyle: {type: 'default'}}},
                    symbolSize:'0',
                    data : [
                        {
                            value : [97, 42, 88, 94, 90, 86,69,66,33,58,44,55,66],
                            name : '一月数据'
                        }
                    ]
                }
            ]
        };
        var option_map = {
            tooltip : {
                trigger: 'item'
            },
            dataRange: {
                itemWidth:7 ,
                itemGap:0.5,
                color:['#A7CFD5','#3C95A2'],
                splitNumber:'20',
                orient:'horizontal',
                min: 0,
                max: 2500,
                x: 'left',
                y: 'top',
                text:['  低','高  ']      // 文本，默认为数值文本
            },
            series : [
                {
                    name: 'iphone3',
                    type: 'map',
                    mapType: 'china',
                    roam: false,
                    itemStyle:{
                        normal:{label:{show:true, textStyle: {
                            color: "#434960",
                            fontSize:10
                        }}},
                        emphasis:{label:{show:true}}
                    },
                    data:[]
                }
            ]
        };
        var option_bar = {
            color:['#6CC1C8'],
            tooltip : {
                trigger: 'item',
                formatter : function (params) {
                    return params.seriesName + ' :'+params.value;
                }
            },
            grid:{
                borderWidth:0,
                x:'100',
                y:'20',
                x2:'0',
                y2:'20'
            },
            xAxis : [

                {	show:false,
                    type : 'value',
                    boundaryGap:[0,0.01]

                }
            ],
            yAxis : [
                {
                    axisLine:{
                         show:false
                    },
                    axisTick:{
                        show:false
                    },
                    splitLine:{
                        show:false
                    },
                    type : 'category',
                    data : []
                }
            ],
            series : [
                {	itemStyle: {
                    normal: {
                        barBorderRadius:0
                    }
                },
                    name:'价格偏好',
                    type:'bar',
                    barWidth:10,
                    data:[]
                }
            ]
        };
        var option_line= {
            color:['#6DADC8'],
            tooltip : {
                trigger: 'item'
            },
            grid:{
                borderWidth:0,
                x:'50',
                y:'20',
                x2:'20',
                y2:'50'
            },
            xAxis : [
                { axisLine:{
                    lineStyle:{color:'#58A0C0'}
                },
                    splitLine:{
                        show:false
                    },
                    axisLabel:{
                        rotate:45
                    },
                    axisTick:{
                        show:false
                    },
                    type : 'category',
                    boundaryGap : false,
                    data : []
                }
            ],
            yAxis : [
                {	axisLine:{
                    show:false
                },
                    splitArea:{
                        show:false
                    },
                    splitLine:{
                        lineStyle:{
                            color:'#999',
                            type: 'dashed'
                        }
                    },
                    type : 'value'
                }
            ],
            series : [
                {
                    itemStyle: {
                        symbolSize:'0',
                        normal: {
                            borderRadius:0,
                            nodeStyle:{
                                borderRadius:0
                            }
                        }
                    },
                    name:'购买时段',
                    type:'line',
                    stack: '总量',
                    symbolSize:0,
                    smooth:false,
                    data:[]
                }
            ]
        };
        if(id == "type"){
            var typeData=vip_group_info.allChartsData.Type.trade_amt;
            var typeLegendAll=vip_group_info.allChartsData.Type.trade_amt;
            for(var i=0;i<typeLegendAll.length;i++){
                typeLegendAll[i].icon="12"
            }
            option_pie.legend.data=typeLegendAll;
            option_pie.series[0].data=typeData;
            myChart.setOption(option_pie);
        }
        if(id == "series"){
            var typeData=vip_group_info.allChartsData.Series.trade_amt;
            var typeLegendAll=vip_group_info.allChartsData.Series.trade_amt;
            for(var i=0;i<typeLegendAll.length;i++){
                typeLegendAll[i].icon="12"
            }
            option_pie.legend.data=typeLegendAll;
            option_pie.series[0].data=typeData;
            myChart.setOption(option_pie);
        }
        if(id == "weeks"){
            var weekData=vip_group_info.allChartsData.Week.trade_amt;
            weekData.sort();
            var weekDataArray=[];
            for(var i=0;i<weekData.length;i++){
                weekDataArray.push(weekData[i].value);
            }
            for(var b=0;b<option_radar_week.radar[0].indicator.length;b++){
                option_radar_week.radar[0].indicator[b].max=weekData[weekData.length-1].value;
            }
            option_radar_week.series[0].data[0].value=weekDataArray;
            myChart.setOption(option_radar_week);
        }
        if(id == "price"){
            var priceData=vip_group_info.allChartsData.Price.trade_amt;
            var name=[];
            var value=[];
            for(var i=0;i<priceData.length;i++){
                name.push(priceData[i].name);
                value.push(priceData[i].value);
            }
            option_bar.yAxis[0].data=name;
            option_bar.series[0].data=value;
            myChart.setOption(option_bar);
        }
        if(id == "times"){
            var TimeData=vip_group_info.allChartsData.Time.trade_amt;
            var name=[];
            var value=[];
            for(var i=0;i<TimeData.length;i++){
                name.push(TimeData[i].name);
                value.push(TimeData[i].value);
            }
            option_line.xAxis[0].data=name;
            option_line.series[0].data=value;
            myChart.setOption(option_line);
        }
        if(id == "month"){
            var monthData=vip_group_info.allChartsData.Month.trade_amt;
            var monthDataArray=[];
            for(var i=0;i<monthData.length;i++){
                monthDataArray.push(monthData[i].value)
            }
            for(var b=0;b<option_radar_month.radar[0].indicator.length;b++){
                option_radar_month.radar[0].indicator[b].max=monthData[monthData.length-1].value;
            }
            option_radar_month.series[0].data[0].value=monthDataArray;
            myChart.setOption(option_radar_month);
        }
        if(id == "areas"){
            var areaData=vip_group_info.allChartsData.Area.trade_amt;
            option_map.series[0].data=areaData;
            myChart.setOption(option_map);
        }
    },
    addChart:function(){    //图标新增弹窗
        $(".chart_close").click(function () {
            $("#chart_add_wrap").hide();
            $("#p").hide();
        });
        $("#chart_add_enter").click(function () {
            $("#chart_add_details input[type='checkbox']:checked").each(function () {
                var val = $(this).nextAll("span").html();
                $("#chart_analyze .chart_module").each(function () {
                    if ($(this).css("display") == "none" && val == $(this).find(".drag_area span").html()) {
                        $(this).show();
                        var ID = $(this).attr("data-id");
                        vip_group_info.init_chart(ID);
                    } else {
                        return;
                    }
                });
            });
            var order = [];
            $(".chart_module").each(function () {
                if ($(this).attr("data-id") !== undefined && $(this).css("display") == "block") {
                    order.push($(this).attr("data-id"));
                }
            });
            vip_group_info.getCharts(order);
            $("#chart_add_wrap").hide();
            $("#p").hide();
        });
        $("#add_chart").click(function () {
            var arr = whir.loading.getPageSize();
            var w = arr[0];
            var h = arr[1];
            var left = (arr[0] - $("#chart_add_wrap").width()) / 2;
            var tp = (arr[3] - $("#chart_add_wrap").height()) / 2;
            $("#chart_add_wrap").css({"left": left, "top": tp, "position": "fixed"});
            $("#p").css({"width": w, "height": h});
            $("#chart_add_wrap").show();
            $("#p").show();
            $("#chart_analyze .chart_module").each(function (i, e) {
                var val = $(this).find(".drag_area").children("span").html();
                if ($(this).css("display") == "block") {
                    $("#chart_add_details li").each(function () {
                        if ($(this).find(".chart_checkbox span").html() == val) {
                            $(this).find(".check")[0].checked = true;
                        }
                    });
                } else {
                    $("#chart_add_details li").each(function () {
                        if ($(this).find(".chart_checkbox span").html() == val) {
                            $(this).find(".check")[0].checked = false;
                        }
                    });
                }
            });
        });
        //图表删除
        $(".chart_close_icon").click(function () {
            $(this).parents(".chart_module").hide();
            var order = [];
            $(".chart_module").each(function () {
                if ($(this).attr("data-id") !== undefined && $(this).css("display") == "block") {
                    order.push($(this).attr("data-id"));
                }
            });
            vip_group_info.getCharts(order);
        });
    }
};
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
$(function(){
    vip_group_info.init();
    $(".icon-ishop_6-07").parent().click(function () {
        window.location.reload();
    });
    setInterval(function(){
        $("#left").height($("#left").next().height()-10)
    },0);
});