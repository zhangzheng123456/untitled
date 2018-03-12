var oc = new ObjectControl();
var vip_group_info={
    pageNumber:1,//删除默认第一页
    pageSize:10,//默认传的每页多少行
    filtrate:"",
    _param:{},
    count:"",
    titleArray:[],
    funcCode:"",
    inx:1,
    nowId:"",
    nowList:[],
    staff_num:1,
    staff_next:false,
    choose_staff_next:false,
    isscroll:false,
    list_show:false,
    charts:{},
    clickMark:"",
    exportAllvip_num:"",
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
        this.staffSearch();
        this.staff_bind();
        this.screenOk();
        this.documentClick();
        this.exportCahrtsData();
    },
    documentClick:function(){  //点击文档空白处做的事情
        $(document).on('click', function (e) {
                $(".chart_analyze_condition ul").hide();
        })
    },
    switchModel:function(){
        $("#show_chart").click(function(){
            if(vip_group_info.list_show && vip_group_info.nowId!=""){
                //vip_group_info.getList("view");
                vip_group_info.getCharts().then(function(){
                    $(".chart_module").each(function () {
                        if ($(this).is(":visible")) {
                            var ID = $(this).attr("data-id");
                            vip_group_info.getList("view",ID);
                        }
                    });
                });
            }
            $(this).addClass("active").siblings().removeClass("active");
            $("#list_show").hide();
            $("#action").find(".action_r").hide();
            $("#chart_parent").show();
            vip_group_info.list_show=false;
            $("#left").getNiceScroll().resize()
        });
        $("#show_list").click(function(){
            //if(!$(this).hasClass("active")){
            //    vip_group_info.getList("list");
            //}
            $(this).addClass("active").siblings().removeClass("active");
            $("#list_show").show();
            $("#action").find(".action_r").show();
            $("#chart_parent").hide();
            if( !vip_group_info.list_show && vip_group_info.nowId!=""){
                if(vip_group_info._param["screen"] && vip_group_info._param["screen"].length>0){
                    vip_group_info.filtrates(vip_group_info.inx, vip_group_info.pageSize);
                }else{
                    vip_group_info.getList("list");
                }
            }
            vip_group_info.list_show=true;
            $("#left").getNiceScroll().resize()
        })
    },
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
                $(this).next().slideToggle(function(){
                    $("#left").getNiceScroll().resize()
                });
                $(this).parent().find("i").toggle();
            }else{
                $(this).parent().find("i").toggle();
            }
            $(this).parent().siblings().find("ul").slideUp();
            $(this).parent().siblings().find("i.icon-ishop_8-02").hide();
        });
        $("#group_list li ul").on("click","li",function(){
            vip_group_info.inx=1;
            vip_group_info.pageSize=10;
            $("#page_row").val("10行/页");
            vip_group_info.nowId=$(this).attr("id");
            $("#group_list li ul li").removeClass("active");
            $(this).addClass("active");
            $(".chart_analyze_condition").find("span").html("按件数分析");
            $(".chart_analyze_condition").find("span").attr("data-type","piece");
            if(!vip_group_info.list_show){
            //vip_group_info.getChartsData();
                vip_group_info.getCharts().then(function(){
                    $(".chart_module").each(function () {
                        if ($(this).is(":visible")) {
                            var ID = $(this).attr("data-id");
                            vip_group_info.getList("view",ID);
                        }
                    });
                });
            //    vip_group_info.getList("view");
            }else {
                $(".table p").remove();
                vip_group_info.filtrate="";
                vip_group_info._param={};
                $("#select_clear").trigger("click");
                //vip_group_info.expend_data();
                select_corp_code=$("#"+vip_group_info.nowId).attr("data-code");
                expend_data_1();
                vip_group_info.getList("list");
            }
            //var reg=/活动|会员任务|券/;
            //if(reg.test($(".condition_active").eq(1).html())){
            //    $(".condition_active").eq(1).parent().trigger("click");
            //}
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
                $("#filtrate_group").before("<li id='leading_out' title='因会员数据量大，请筛选后再导出'> <span class='icon-ishopwebicon_6-24'></span> 导出</li>")
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
                if(i==4){
                    $($(".table tbody tr")[i]).append("<td colspan='"+len+"'>暂无内容</td>");
                }else {
                    $($(".table tbody tr")[i]).append("<td colspan='"+len+"'></td>");
                }
            }
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
                    }else  if(code=="user_name"){
                        TD+="<td data-value='user_name'><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
                    }else {
                            TD+="<td><span title='"+data[i][code]+"'>"+data[i][code]+"</span></td>";
                        }
                })(c)
            }
            $(".table tbody").append("<tr id='"+data[i].vip_id+"' data-corp_code='"+data[i].corp_code+"' data-storeid='"+data[i].store_id+"' data-storecode='"+data[i].store_code+"'><td width='50px;' style='text-align: left;'><div class='checkbox'><input  type='checkbox' value='' name='test' title='全选/取消' class='check'  id='checkboxTwoInput"
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
        //whir.loading.remove();//移除加载框
        $("#chart_mask").hide();
        $("#chart_mask_text").hide();
        $(".th th:first-child input").removeAttr("checked");
    },
    getList:function(type,type_view){
        var getListParam={};
        getListParam["id"]=vip_group_info.nowId;
        getListParam["pageNumber"]=vip_group_info.inx;
        getListParam["pageSize"]=vip_group_info.pageSize;
        getListParam["type"]=type;
        getListParam["corp_code"]=$("#"+vip_group_info.nowId).attr("data-code");
       if($("#"+vip_group_info.nowId).attr("data-group-code")==undefined){
           delete getListParam.id;
           getListParam["fixed_code"]=vip_group_info.nowId;
       }
        if(type=="view"){
            getListParam["type_view"]=type_view;
            $("#chart_mask").css("top","220px");
            $("#chart_mask").css("minHeight",$("#chart_parent").height()-30+"px");
        }else {
            if($("#list_show").height()<$("#left").height()){
                $("#chart_mask").css("minHeight",$("#left").height()+10+"px");
            }else{
                $("#chart_mask").css("minHeight",$("#list_show").height()+"px");
            }
            $("#chart_mask").css("top","181px");
            $("#chart_mask").show();
            $("#chart_mask_text").show();
        }
        $(".table p").remove();

        oc.postRequire("post","/vipGroup/groupVips","",getListParam,function(data){
            if(data.code=="0"){
                if(type=="view"){
                    vip_group_info.allChartsData=JSON.parse(data.message);
                    vip_group_info.allChartsData=vip_group_info.allChartsData.message;
                    //vip_group_info.getCharts();
                    $("#chart_mask").hide();
                    $("#chart_mask_text").hide();
                    $(".chart_module[data-id="+type_view+"]").attr("data-value",JSON.stringify(vip_group_info.allChartsData));
                    vip_group_info.init_chart(type_view);
                }else{
                    $(".table tbody").empty();
                    var messages=JSON.parse(data.message);
                    var list=messages.all_vip_list;
                    vip_group_info.count=messages.pages;
                    var pageNum = messages.pageNum;
                    vip_group_info.exportAllvip_num=messages.count;
                    //var list=list.list;
                    vip_group_info.superaddition(list,pageNum);
                    vip_group_info.jumpBianse();
                    vip_group_info.setPage($("#foot-num")[0],vip_group_info.count,pageNum,vip_group_info.pageSize);
                }
            }else if(data.code=="-1"){
                $("#chart_mask").hide();
                $("#chart_mask_text").hide();
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content: data.message
                });
            }
        });
    },
    expend_data:function () {    //获取拓展资料
        var param={"corp_code":$("#"+vip_group_info.nowId).attr("data-code")};
        oc.postRequire("post","/vipparam/corpVipParams","0",param,function (data) {
            if(data.code == 0){
                $("#expend_attribute").empty();
                $("#memorial_day").empty();
                var msg = JSON.parse(data.message);
                var list = JSON.parse(msg.list);
                var html="";
                var simple_html="";
                if(list.length>0){
                    for(var i=0;i<list.length;i++){
                        var param_type = list[i].param_type;
                        if(param_type=="date"){
                            simple_html+='<div class="contion_input"><label>'+list[i].param_desc+'</label>'
                                + '<input data-expend="date" data-kye="'+list[i].param_name+'" readonly="true" id="start'+i+'s" class="short_input_date laydate-icon" onclick="laydate({elem:\'#start'+i+'s\', min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformat})"><label class="jian">~</label>'
                                + '<input readonly="true" id="end'+i+'s" class="short_input_date laydate-icon" onclick="laydate({elem:\'#end'+i+'s\',min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformat})"></div>'
                            html+='<div class="contion_input"><label>'+list[i].param_desc+'</label>'
                                + '<input data-expend="date" data-kye="'+list[i].param_name+'" readonly="true" id="start'+i+'" class="short_input_date laydate-icon" onclick="laydate({elem:\'#start'+i+'\', min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformat})"><label class="jian">~</label>'
                                + '<input readonly="true" id="end'+i+'" class="short_input_date laydate-icon" onclick="laydate({elem:\'#end'+i+'\',min:\'1900-01-0\', max:\'2099-12-31 23:59:59\' ,istime: false,istoday:false, format: \'YYYY-MM-DD\',choose:Ownformat})"></div>'
                        }
                        if(param_type=="select"){
                            var param_values = "";
                            param_values = list[i].param_values.split(",");
                            if(param_values.length>0){
                                var li="<li>空</li>";
                                for(var j=0;j<param_values.length;j++){
                                    li+='<li>'+param_values[j]+'</li>'
                                }
                                html+='<div class="contion_input"><label>'+list[i].param_desc+'</label>'
                                    + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select">'
                                    + li
                                    + '</ul></div>'
                            }else {
                                html+='<div class="contion_input"><label>'+list[i].param_desc+'</label>'
                                    + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select"></ul></div>'
                            }
                        }
                        if(param_type=="text"){
                            html+='<div class="contion_input"><label>'+list[i].param_desc+'</label>'
                                + '<input data-expend="text" data-kye="'+list[i].param_name+'" class="input"><ul class="sex_select"></ul></div>'
                        }
                        if(param_type=="longtext"){
                            html+='<div class="textarea"><label>'+list[i].param_desc+'</label>'
                                + '<textarea data-kye="'+list[i].param_name+'" rows="0" cols="0"></textarea><ul class="sex_select"></ul></div>'
                        }
                        if(param_type=="check"){
                            var param_values = "";
                            param_values = list[i].param_values.split(",");
                            if(param_values.length>0){
                                var li="";
                                for(var j=0;j<param_values.length;j++){
                                    li+='<li class="type_check">'
                                        +'<input type="checkbox" style="vertical-align:middle;width: 15px;height: 15px;margin: 0"/>'
                                        +'<span style="position: inherit;vertical-align: middle;display: inline-block;width: 90%;text-overflow: ellipsis;overflow: hidden;white-space: nowrap" title="'+param_values[j]+'">'+param_values[j]+'</span>'
                                        +'</li>'
                                }
                                html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                    + '<input data-expend="text" data-type="check" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select">'
                                    + li
                                    + '</ul></div>'
                            }else {
                                html+='<div class="contion_input"><label title="'+list[i].param_desc+'">'+list[i].param_desc+'</label>'
                                    + '<input data-expend="text" data-type="check" data-kye="'+list[i].param_name+'" class="select" readonly><ul class="sex_select"></ul></div>'
                            }
                        }
                    }
                }
                $("#expend_attribute").append(html);
                $("#memorial_day").append(simple_html);
            }else if(data.code == -1){
                //console.log(data.message);
            }
        });
    },
    dian:function(inx,pageSize){
        if(vip_group_info.filtrate==""){
            vip_group_info.getList("list");
        }else{
            vip_group_info._param["pageNumber"] =  vip_group_info.inx;
            vip_group_info._param["pageSize"] = vip_group_info.pageSize;
            vip_group_info.filtrates(vip_group_info.inx,vip_group_info.pageSize);
        }
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
                if(list[i].group_type=="define" || list[i].group_type=="define_v2"){
                    $("#group_list li[data-type='define'] ul").append("<li id='"+list[i].id+"' data-code='"+list[i].corp_code+"' data-group-code='"+list[i].vip_group_code+"' title='"+list[i].vip_group_name+"'>"+list[i].vip_group_name+"</li>");
                }
                switch (list[i].group_type){
                    case "brand":
                       $("#group_list li[data-type='brand'] ul").append("<li id='"+list[i].id+"' data-code='"+list[i].corp_code+"' data-group-code='"+list[i].vip_group_code+"' title='"+list[i].vip_group_name+"'>"+list[i].vip_group_name+"</li>");
                        break;
                    case "class":
                       $("#group_list li[data-type='class'] ul").append("<li id='"+list[i].id+"' data-code='"+list[i].corp_code+"' data-group-code='"+list[i].vip_group_code+"' title='"+list[i].vip_group_name+"'>"+list[i].vip_group_name+"</li>");
                        break;
                    case "discount":
                       $("#group_list li[data-type='discount'] ul").append("<li id='"+list[i].id+"' data-code='"+list[i].corp_code+"' data-group-code='"+list[i].vip_group_code+"' title='"+list[i].vip_group_name+"'>"+list[i].vip_group_name+"</li>");
                        break;
                    case "season":
                       $("#group_list li[data-type='season'] ul").append("<li id='"+list[i].id+"' data-code='"+list[i].corp_code+"' data-group-code='"+list[i].vip_group_code+"' title='"+list[i].vip_group_name+"'>"+list[i].vip_group_name+"</li>");
                        break;
                }
            }
                $("#group_list li ul li").eq(0).parent().show();
                $("#group_list li ul li").eq(0).addClass("active");
                $("#group_list li ul li").eq(0).parent().prev().find("i").show();
                vip_group_info.nowId=$("#group_list li ul li").eq(0).attr("id");
                //vip_group_info.expend_data();//扩展资料
                select_corp_code=$("#"+vip_group_info.nowId).attr("data-code");
                expend_data_1();
                //vip_group_info.getList("view");
                vip_group_info.getCharts().then(function(){
                    $(".chart_module").each(function () {
                        if ($(this).is(":visible")) {
                            var ID = $(this).attr("data-id");
                            vip_group_info.getList("view",ID);
                        }
                    });
                });
        });
        $("#left").niceScroll({
            cursorborder: "0 none", cursoropacitymin: "0", boxzoom: false,
            cursorcolor: " rgba(0,0,0,0.1)",
            cursoropacitymax: 1,
            touchbehavior: false,
            cursorminheight: 30,
            autohidemode: false
        });
    },
    setPageSize: function () {
        $("#page_row,.page_p .icon-ishop_8-02").click(function(){
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
                if(vip_group_info.filtrate==""){
                    vip_group_info.getList("list");
                }else{
                    vip_group_info._param["pageNumber"] =  vip_group_info.inx;
                    vip_group_info._param["pageSize"] = vip_group_info.pageSize;
                    vip_group_info.filtrates(vip_group_info.inx,vip_group_info.pageSize);
                }
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
                    vip_group_info.getList("list")
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
        $("#table").on("dblclick","tr td:not(:first-child)",function(){
            if(vip_group_info.nowList.length==0){
                return
            }
            var ID=$(this).parent().attr("id");
            var corpCode=$(this).parent().attr("data-corp_code");
            sessionStorage.setItem("id",ID);
            sessionStorage.setItem("corp_code",corpCode);
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
                param["corp_code"]=$("#"+vip_group_info.nowId).attr("data-code");
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
                    param["corp_code"]=$("#"+vip_group_info.nowId).attr("data-code");
                    param['pageNumber']=page;
                    param['searchValue']="";
                    oc.postRequire("post","/VIP/label/findViplabelByType ","",param,function(data){
                        if(data.code=="0"){
                            var msg=JSON.parse(data.message);
                            var list=JSON.parse(msg.list);
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
        param["corp_code"]=$("#"+vip_group_info.nowId).attr("data-code");
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
        param["corp_code"]=$("#"+vip_group_info.nowId).attr("data-code");
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
        $("#hide_export").click(function(){
            $("#export_list").hide();
            $("#p").hide();
        });
        //导出提交的
        $("#file_submit").click(function(){
            var allPage=Math.ceil(vip_group_info.exportAllvip_num/2000);
            var li=$("#file_list_r input[type='checkbox']").parents("li");
            //var value = $("#search").val().trim();
            var _param={};
            var tablemanager=[];
            var screen = [];
            var list_html=""
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
            _param["tablemanager"]=tablemanager;
            _param["type"]="group";
            _param["id"]=vip_group_info.nowId;
            if($("#"+vip_group_info.nowId).attr("data-group-code")==undefined){
                delete _param.id;
                _param["fixed_code"]=vip_group_info.nowId;
            }
            _param["searchValue"]="";
            if(vip_group_info.filtrate==""){
                _param["screen_message"]="";
            }else if(vip_group_info.filtrate!==""){
                _param["screen_message"]=vip_group_info._param['screen'];
            }
            if(vip_group_info.nowId==""){
                frame();
                $('.frame').html('当前不可操作');
                return;
            }
            $(".file").hide();
            $("#export_list_all ul").html("");
            for(var a=1;a<allPage+1;a++){
                var start_num=(a-1)*2000 + 1;
                var end_num="";
                if (vip_group_info.exportAllvip_num < a*2000 ){
                    end_num = vip_group_info.exportAllvip_num
                }else{
                    end_num = a*2000
                }
                list_html+= '<li>'
                    +'<span style="float: left">分组会员('+start_num+'~'+end_num+')</span>'
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
                _param["page_num"]=page;
                _param["page_size"]="2000";
                oc.postRequire("post","/vipGroup/groupVip/exportExcel","0",_param,function(data){
                    if(data.code=="0"){
                        var message=JSON.parse(data.message);
                        var path=message.path;
                        if(message.path_type && message.path_type=="oss"){
                            self.attr("data-path",path);
                            self.html("<a href='"+path+"' style='display: inline-block;width: 100%;height: 100%;color: #FFFFFF'>下载</a>");
                        }else {
                            path=path.substring(1,path.length-1);
                            self.attr("data-path",path);
                            self.html("<a href='/"+path+"' style='display: inline-block;width: 100%;height: 100%;color: #FFFFFF'>下载</a>");
                        }

                        //path=path.substring(1,path.length-1);
                        //self.attr("data-path",path);
                        self.next().text("导出完成");
                        //self.html("<a href='/"+path+"' style='display: inline-block;width: 100%;height: 100%;color: #FFFFFF'>下载</a>");
                        self.css("backgroundColor","#637ea4");
                    }else if(data.code=="-1"){
                        self.removeClass("btn_active");
                        self.next().text("导出失败");
                    }
                },function(){
                    self.removeClass("btn_active");
                    self.next().text("导出超时");
                })
            });
            $("#to_zip").unbind("click").bind("click",function(){
                var a=$("#export_list_all ul li a");
                var param={};
                if(a.length==0){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content: "请先导出文件"
                    });
                    return;
                }
                var URL= a.map(function(){
                    return $(this).attr("href")
                }).get().join(",");
                param.url=URL;
                param.name="分组会员";
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
            $("#cancel_download,#X_download").click(function(){
                $("#p").css("zIndex","787");
                $("#download_all").hide();
                $("#download_all a").removeProp("href");
            });
            //whir.loading.add("",0.5);//加载等待框
            //oc.postRequire("post","/vipGroup/groupVip/exportExcel","0",_param,function(data){
            //    if(data.code=="0"){
            //        var message=JSON.parse(data.message);
            //        var path=message.path;
            //        var path=path.substring(1,path.length-1);
            //        $('#download').html("<a href='/"+path+"'>下载文件</a>");
            //        $('#download').addClass("download");
            //        $('#file_submit').hide();
            //        $('#download').show();
            //        //导出关闭按钮
            //        $('#file_close').click(function(){
            //            $('.file').hide();
            //        })
            //        $('#download').click(function(){
            //            $("#p").hide();
            //            $('.file').hide();
            //            $('#file_submit').show();
            //            $('#download').hide();
            //        })
            //    }else if(data.code=="-1"){
            //        alert(data.message);
            //    }
            //    whir.loading.remove();//移除加载框
            //})
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
            $("#choose_staff").css({"position":"fixed"});
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
                $("#choose_staff .select_list").unbind("scroll");
                $("#choose_staff .select_list ul").empty();
               getstafflist(staff_num,mark);
            }
        });

    },
    staff_bind:function(){
        $("#addVipEnter,#addVipX").click(function(){
            $("#msg").html("");
            $("#p").hide();
            $("#addVipTk").hide();
        });
        //导购关闭
        $(".select_cancel").click(function(){
            $(".select_modal").hide();
            $("#p").hide();
            //$(document.body).css("overflow","auto");
        });
        $("#set_store_group").on("click",".select_this",function(){
            if($(this).parents("ul").find("li").hasClass("active")){
                frame();
                $(".frame").html("只能分配一个店铺");
                return;
            }
            $(this).parent().addClass("active");
        });
        $("#choose_staff").on("click",".select_this",function(){
            if($(this).parents("ul").find("li").hasClass("active")){
                frame();
                $(".frame").html("只能分配一个导购");
                return;
            }
            $(this).parent().addClass("active");
        });
        $(".select_modal").on("click",".cancel_this",function(){
            $(this).parent().removeClass("active");
        });
        //分配导购确定
        $("#choose_staff #select_staff_ok").click(function () {
            var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
            var li = $("#choose_staff .select_list li.active");
            if(li.length>1){
                frame();
                $('.frame').html('只能分配一个导购');
            }else if(li.length==0){
                frame();
                $('.frame').html('请先选择导购');
            }else  if(li.length==1){
                $("#choose_staff").hide();
                $("#p").hide();
                var staff_name = $("#choose_staff .select_list ul li.active").attr("data-user_name");
                var user_code = $("#choose_staff  .select_list ul li.active").attr("data-user_code");
                var store_code = $(tr[0]).attr("data-storecode");
                var vip_id ="";
                for(var i=0;i<tr.length;i++){
                    if(i<tr.length-1){
                        vip_id+=$(tr[i]).attr("id")+",";
                    }else{
                        vip_id+=$(tr[i]).attr("id")
                    }
                }
                var _param={};
                _param["corp_code"]="C10000";
                _param["store_code"]=store_code;
                _param["vip_id"]=vip_id;
                _param["user_code"]=user_code;
                _param["user_name"]=staff_name;
                whir.loading.add("",0.5);//加载等待框
                oc.postRequire("post","/vip/changeVipsUser","",_param,function (data) {
                    if(data.code=="0"){
                        for(var i=0;i<tr.length;i++){
                            $(tr[i]).children("td[data-value=user_name]").html(staff_name);
                        }
                        whir.loading.remove();//移除加载框
                        $("#p").show();
                        $("#addVipTk").show();
                        $("#msg").html("分配成功");
                    }else if(data.code == "-1"){
                        whir.loading.remove();//移除加载框
                        $("#p").show();
                        $("#addVipTk").show();
                        $("#msg").html(data.message==""?"分配失败":data.message);
                    }
                })
            }
        });
    },
    staffSearch:function(){
        //导购搜索
        $("#search_staff").keydown(function(){
            var event=window.event||arguments[0];
            staff_num=1;
            if(event.keyCode==13){
                var mark="staff";
                isscroll=false;
                $("#choose_staff .select_list").unbind("scroll");
                $("#choose_staff .select_list ul").empty();
                getstafflist(staff_num,mark);
            }
        });
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
        $(".chart_analyze_condition i,.chart_analyze_condition span").click(function(e){
            $(this).parents(".chart_module").siblings().find(".chart_analyze_condition ul").hide();
            $(this).siblings("ul").toggle();
            e.stopPropagation();
        });
        $(".chart_analyze_condition ul li").click(function(e){
            var val=$(this).text();
            $(this).parent().siblings("span").html(val);
            $(this).parent().hide();
            $(this).parent().siblings("span").attr("data-type",$(this).attr("data-type"));
            var type= $(this).parent().siblings("span").attr("data-type");
            switch (type){
                case "piece":
                    type="trade_num";
                    break;
                case "money":
                    type="trade_amt";
                    break;
                case "discount":
                    type="discount";
                    break;
                case "trade_price":
                    type="trade_price";
                    break;
                case "relate_rate":
                    type="relate_rate";
                    break;
                case "stroke_num":
                    type="stroke_num";
                    break;
                case "people":
                    type="size_array";
                    break;
            }
            //var type= $(this).parent().siblings("span").attr("data-type")=="piece"?"trade_num":$(this).parent().siblings("span").attr("data-type")=="money"?"trade_amt":"discount";
            var chartType=$(this).parents(".chart_module").attr("data-id");
            vip_group_info.init_chart(chartType,type);
            e.stopPropagation();
        });
        $(".chart_head").dblclick(function(e){
            var e=e || window.event;
            if(!($(e.target).is(".chart_head"))){
                return ;
            }
            if($(this).parents(".chart_module").hasClass("chart_lg")) {
                $(this).find(".chart_close_icon").trigger("click");
                return ;
            }
            whir.loading.add("mask",0.8);
            $(this).parents(".chart_module").addClass("chart_lg");
            $("#chart_analyze .chart_module.chart_lg>ul>li").height($(".chart_lg").height()-30);
            var chartShow=$(this).parents(".chart_module").attr("data-id");
            $(this).parents(".chart_module").find(".data_table ul li").css("width",($(this).parents(".chart_module").width()-20)/ $(this).parents(".chart_module").find(".data_table ul:first-child li").length+"px");
           switch (chartShow){
               case "type":
                   vip_group_info.myChart_type.resize();
                   break;
               case "weeks":
                   vip_group_info.myChart_weeks.resize();
                   break;
               case "price":
                   vip_group_info.myChart_price.resize();
                   break;
               case "times":
                   vip_group_info.myChart_times.resize();
                   break;
               case "month":
                   vip_group_info.myChart_month.resize();
                   break;
               case "series":
                   vip_group_info.myChart_series.resize();
                   break;
               case "areas":
                   vip_group_info.myChart_areas.resize();
                   break;
               case "season":
                   vip_group_info.myChart_season.resize();
                   break;
               case "typeMid":
                   vip_group_info.myChart_typeMid.resize();
                   break;
               case "typeSm":
                   vip_group_info.myChart_typeSm.resize();
                   break;
               case "typeSeries":
                   vip_group_info.myChart_typeSeries.resize();
                   break;
               case "age":
                   vip_group_info.myChart_age.resize();
                   break;
               case "cardType":
                   vip_group_info.myChart_card_type.resize();
                   break;
               case "sex_vip_num":
                   vip_group_info.myChart_type_sex.resize();
                   break;
               case "birth_vip_num":
                   vip_group_info.myChart_birth_vip_num.resize();
                   break;
               case "source":
                   vip_group_info.myChart_source.resize();
                   break;
           }

        });
    },
    exportCahrtsData:function(){
        $(".chart_head .icon-ishopwebicon_6-24").click(function(){
            var arr=whir.loading.getPageSize();
            var self=$(this);
            var id=self.parents(".chart_module").attr("data-id");
            vip_group_info.allChartsData=JSON.parse(self.parents(".chart_module").attr("data-value"));
            var data="";
            switch (id){
                case "type":
                    data=vip_group_info.allChartsData.Type;
                    break;
                case "weeks":
                    data=vip_group_info.allChartsData.Week;
                    break;
                case "price":
                    data=vip_group_info.allChartsData.Price;
                    break;
                case "month":
                    data=vip_group_info.allChartsData.Month;
                    break;
                case "season":
                    data=vip_group_info.allChartsData.Season;
                    break;
                case "series":
                    data=vip_group_info.allChartsData.Series;
                    break;
                case "typeMid":
                    data=vip_group_info.allChartsData.TypeMid;
                    break;
                case "typeSm":
                    data=vip_group_info.allChartsData.TypeSm;
                    break;
	            case "typeSeries":
                    data=vip_group_info.allChartsData.TypeSeries;
                    break;
                case "age":
                    data=vip_group_info.allChartsData.age;
                    break;
                case "cardType":
                    data=vip_group_info.allChartsData.cardType;
                    break;
                case "sex_vip_num":
                    data=vip_group_info.allChartsData;
                    break;
                case "birth_vip_num":
                    data=vip_group_info.allChartsData;
                    break;
                case "source":
                    data=vip_group_info.allChartsData.source;
                    break;
            }

            if(id!="sex_vip_num" && id!="birth_vip_num" && data.trade_amt["length"]=="0" && data.trade_num["length"]=="0" &&data.discount["length"]=="0"){
                art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    zIndex:10011,
                    content: "暂无数据"
                });
                return
            }
            $("#p").css({"width":+arr[0]+"px","height":+arr[1]+"px"});
            $("#p").show();
            $("#tk_export").show();
            $("#enter a").attr("data-src","");
            $("#enter a").removeAttr("href");
            $("#enter a").html("确认");
            $("#X_export p").html("是否导出该图表数据?");
            $("#enter").unbind("click").bind("click",function(){
                if($("#enter a").attr("data-src")!=""){
                    $("#tk_export").hide();
                    $("#p").hide();
                }else{
                    whir.loading.add("","0.5");
                    oc.postRequire("post","/vipAnalysis/exportExecl2View","0",data,function (reg) {
                        whir.loading.remove();
                        if(reg.code=="0"){
                            var src=JSON.parse(reg.message);
                            var path=src.path;
                            path=path.substring(1,path.length-1);
                            $("#X_export p").html("是否下载文件?");
                            $("#enter a").attr("href","/"+path);
                            $("#enter a").attr("data-src",src.path);
                            $("#enter a").html("下载文件");
                        }else if(reg.code=="-1"){
                            art.dialog({
                                time: 1,
                                lock: true,
                                cancel: false,
                                zIndex:10011,
                                content: reg.message
                            });
                        }
                    })
                }
            });
            $("#X_export,#cancel_export").click(function(){
                $("#tk_export").hide();
                $("#enter a").attr("data-src","");
                $("#enter a").removeAttr("href");
                $("#enter a").html("确认");
                $("#X_export p").html("是否导出该图表数据?");
                $("#p").hide();
            })

        })
    },
    transShowModel:function(){
        $(".chart_list_icon").click(function(){
            if($(this).parent().siblings("li").is(":hidden")){
                $(this).parent().siblings("li").show();
                $(this).parent().siblings(".data_table").hide();
                var chartShow=$(this).parents(".chart_module").attr("data-id");
                switch (chartShow){
                    case "type":
                        vip_group_info.myChart_type.resize();
                        break;
                    case "weeks":
                        vip_group_info.myChart_weeks.resize();
                        break;
                    case "price":
                        vip_group_info.myChart_price.resize();
                        break;
                    case "times":
                        vip_group_info.myChart_times.resize();
                        break;
                    case "month":
                        vip_group_info.myChart_month.resize();
                        break;
                    case "series":
                        vip_group_info.myChart_series.resize();
                        break;
                    case "areas":
                        vip_group_info.myChart_areas.resize();
                        break;
                    case "season":
                        vip_group_info.myChart_season.resize();
                        break;
                    case "typeMid":
                        vip_group_info.myChart_typeMid.resize();
                        break;
                    case "typeSm":
                        vip_group_info.myChart_typeSm.resize();
                        break;
                    case "age":
                        vip_group_info.myChart_age.resize();
                        break;
                    case "cardType":
                        vip_group_info.myChart_card_type.resize();
                        break;
                    case "typeSeries":
                        vip_group_info.myChart_typeSeries.resize();
                        break;
                    case "birth_vip_num":
                        vip_group_info.myChart_birth_vip_num.resize();
                        break;
                    case "source":
                        vip_group_info.myChart_source.resize();
                        break;

                }
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
            if(param.vip_group_code==undefined){
                param.vip_group_code="";
                param.fixed_code=$("#"+vip_group_info.nowId).attr("id");
            }
            oc.postRequire("post","/vipAnalysis/vipChart","0",param,function (data) {
               vip_group_info.allChartsData=JSON.parse(data.message);
               vip_group_info.getCharts()
            });
    },
    getCharts:function(order){
        var key_val=sessionStorage.getItem("key_val");//取页面的function_code
        var def= $.Deferred();
        key_val=JSON.parse(key_val);
        var funcCode=key_val.func_code;
        var param=null;
        if(order!==""&&order!==undefined){
            var id=$("#chart_analyze").attr("data-id");
            param={
                "corp_code":$("#"+vip_group_info.nowId).attr("data-code"),
                "function_code":funcCode,
                "type":"order",
                "id":id,
                "order":order
            };
        }else {
           param={
                "corp_code":$("#"+vip_group_info.nowId).attr("data-code"),
                "function_code":funcCode,
                "type":"show"
            };
        }
        oc.postRequire("post","/privilege/vip/chartOrder","0",param,function (data) {
            if(data.code == 0){
                if(data.message=="success"){
                }else{
                    var message = JSON.parse(data.message);
                    var can_add=message.can_add;
                    var list=message.list;
                    if(can_add=="N"){
                        $("#add_chart").hide();
                    }else if(can_add=="Y"){
                        $("#add_chart").show();
                    }
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
                                        $("#chart_analyze").append($(this));
                                        $("#add_chart").show();
                                        $(this).find(".chart_loading").show();
                                        //$("#add_chart").before($(this));
                                    }
                                });
                            }
                        }
                    }else{
                    }
                    def.resolve();
                }
            }else if(data.code == -1){
                console.log(data.message);
            }
        });
        return def;
    },
    init_chart:function(id,showType) {
        var dataZoom=[];
        var option_pie = {
            color: ['#44556c', '#4a5f7c', '#5475a2', '#5f8bc8', '#7c92ca', '#9999cc', '#6db0d3','#93d7e2', '#c1e7ed','#e5e8ea', '#41c7db'],
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
                {   name:'',
                    type:'pie',
                    radius : ['50%', '60%'],
                    avoidLabelOverlap:false,
                    label:{
                        normal:{
                            show:false,
                            position:"center"
                        },
                        emphasis:{
                            show:true,
                            textStyle:{
                                fontSize:"20",
                                fontWeight:"bold"
                            }
                        }
                    },
                    labelLine:{
                        normal:{
                            show:false
                        }
                    },
                    data:[]
                }
            ]
        };
        var option_pie_sex = {
            color: ['#2f4553','#c33531'],
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
                {   name:'',
                    type:'pie',
                    radius : ["50%","60%"],
                    avoidLabelOverlap:false,
                    label:{
                        //normal:{
                        //    show:false,
                        //    position:"center"
                        //},
                        //emphasis:{
                        //    show:true,
                        //    textStyle:{
                        //        fontSize:"20",
                        //        fontWeight:"bold"
                        //    }
                        //}
                    },
                    labelLine:{
                        //normal:{
                        //    show:false
                        //}
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
                trigger: 'axis',
                confine:true
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
                            value : [],
                            name : '星期偏好'
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
                trigger: 'axis',
                confine:true
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
                            value : [],
                            name : '月份偏好'
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
                itemWidth: 5,
                itemGap: 0.2,
                color: ['#3C95A2', '#A7CFD5'],
                splitNumber: '20',
                orient: 'horizontal',
                min: 0,
                max: 10,
                x: 'left',
                y: 'top',
                text: ['高','低']      // 文本，默认为数值文本
            },
            series : [
                {
                    name: '地域偏好',
                    type: 'map',
                    mapType: 'china',
                    roam: false,
                    itemStyle: {
                        normal: {
                            label: {
                                show: true, textStyle: {
                                    color: "#434960",
                                    fontSize: 10
                                }
                            }
                        },
                        emphasis: {label: {show: true}}
                    },
                    label: {
                        normal: {
                            show: false
                        },
                        emphasis: {
                            show: true
                        }
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
                    return params.seriesName+"<br/>"+params.name + ' : '+params.value;
                }
            },
            grid:{
                borderWidth:0,
                x:'100',
                y:'20',
                x2:'40',
                y2:'30'
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
            dataZoom: dataZoom,
            series : [
                {
                    itemStyle: {
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
        $(".chart_module[data-id="+id+"]").find(".chart_loading").hide();
        vip_group_info.allChartsData=JSON.parse($(".chart_module[data-id="+id+"]").attr("data-value"));
        if(id == "type"){
            vip_group_info.myChart_type=echarts.init(document.getElementById(id));
            var typeData=showType==undefined ? vip_group_info.allChartsData.Type["trade_num"]:vip_group_info.allChartsData.Type[showType];
            var typeLegendAll=showType==undefined ? vip_group_info.allChartsData.Type["trade_num"]:vip_group_info.allChartsData.Type[showType];
            for(var i=0;i<typeLegendAll.length;i++){
                typeLegendAll[i].icon="12"
            }
            option_pie.legend.data=typeLegendAll;
            option_pie.series[0].data=typeData;
            option_pie.series[0].name="品类偏好";
            if(typeData.length==0){
                $("#type").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
                $("#type").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
                return
            }
            vip_group_info.myChart_type.setOption(option_pie);
            vip_group_info.resize(vip_group_info.myChart_type);
            var head="";
            var value="";
            for(var a=0;a<typeData.length;a++){
                head+='<li>'+typeData[a].name+'</li>';
                value+='<li style="word-break: break-all" title="'+typeData[a].value+'">'+typeData[a].value+'</li>';
            }
            head="<ul><li></li>"+head+"</ul>";
            value=showType=="trade_amt"?"<ul><li>金额</li>"+value+"</ul>":showType=="discount"?"<ul><li>折扣</li>"+value+"</ul>":"<ul><li>件数</li>"+value+"</ul>";
            $("#type").next().html(head+value);
            $("#type").next().find("li").css("width",($("#type").next().width()-10)/$(head).find("li").length+"px")
        }
        if(id == "sex_vip_num"){
            vip_group_info.myChart_type_sex=echarts.init(document.getElementById(id));
            var typeData=vip_group_info.allChartsData.sex_vip_num;
            var typeLegendAll=vip_group_info.allChartsData.sex_vip_num
            for(var i=0;i<typeLegendAll.length;i++){
                typeLegendAll[i].icon="12"
            }
            option_pie_sex.legend.data=typeLegendAll;
            option_pie_sex.series[0].data=typeData;
            option_pie_sex.series[0].name="性别比例";
            if(typeData.length==0){
                $("#sex_vip_num").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
                $("#sex_vip_num").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
                return
            }
            vip_group_info.myChart_type_sex.setOption(option_pie_sex);
            vip_group_info.resize(vip_group_info.myChart_type_sex);
            var head="";
            var value="";
            for(var a=0;a<typeData.length;a++){
                head+='<li>'+typeData[a].name+'</li>';
                value+='<li style="word-break: break-all" title="'+typeData[a].value+'">'+typeData[a].value+'</li>';
            }
            head="<ul><li></li>"+head+"</ul>";
            value=showType=="trade_amt"?"<ul><li>金额</li>"+value+"</ul>":showType=="discount"?"<ul><li>折扣</li>"+value+"</ul>":"<ul><li>人数</li>"+value+"</ul>";
            $("#sex_vip_num").next().html(head+value);
            $("#sex_vip_num").next().find("li").css("width",($("#sex_vip_num").next().width()-10)/$(head).find("li").length+"px")
        }
        if(id == "series"){
           vip_group_info.myChart_series= echarts.init(document.getElementById(id));
            var seriesData= showType==undefined ? vip_group_info.allChartsData.Series["trade_num"]:vip_group_info.allChartsData.Series[showType];
            var seriesDataLegendAll= showType==undefined ? vip_group_info.allChartsData.Series["trade_num"]:vip_group_info.allChartsData.Series[showType];
            for(var i=0;i<seriesDataLegendAll.length;i++){
                seriesDataLegendAll[i].icon="12"
            }
            option_pie.legend.data=seriesDataLegendAll;
            option_pie.series[0].data=seriesData;
            option_pie.series[0].name="品牌偏好";
            if(seriesData.length==0){
                $("#series").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
                $("#series").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
                return
            }
            vip_group_info.myChart_series.setOption(option_pie);
            vip_group_info.resize(vip_group_info.myChart_series);
            var head="";
            var value="";
            for(var a=0;a<seriesData.length;a++){
                head+='<li>'+seriesData[a].name+'</li>';
                value+='<li style="word-break: break-all" title="'+seriesData[a].value+'">'+seriesData[a].value+'</li>';
            }
            head="<ul><li></li>"+head+"</ul>";
            value=showType=="trade_amt"?"<ul><li>金额</li>"+value+"</ul>":showType=="discount"?"<ul><li>折扣</li>"+value+"</ul>":showType=="size_array"?"<ul><li>人数</li>"+value+"</ul>":"<ul><li>件数</li>"+value+"</ul>";
            $("#series").next().html(head+value);
            $("#series").next().find("li").css("width",($("#series").next().width()-10)/$(head).find("li").length+"px")
        }
        if(id == "weeks"){
            vip_group_info.myChart_weeks= echarts.init(document.getElementById(id));
            var weekData=showType==undefined ? vip_group_info.allChartsData.Week["trade_num"]:vip_group_info.allChartsData.Week[showType];
            var weekDataArray=[];
            var newWeekDataArray=[];
            for(var i=0;i<weekData.length;i++){
                weekDataArray.push(weekData[i].value);
                newWeekDataArray.push(weekData[i].value);
            }
            option_radar_week.series[0].data[0].value=weekDataArray;
            newWeekDataArray.sort(vip_group_info.sortNumber);
            for(var b=0;b<option_radar_week.radar[0].indicator.length;b++){
                option_radar_week.radar[0].indicator[b].max=newWeekDataArray[newWeekDataArray.length-1]*1.1==0?"10":newWeekDataArray[newWeekDataArray.length-1]*1.1;
            }
            if(weekData.length==0){
                $("#weeks").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
                $("#weeks").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
                return
            }
            vip_group_info.myChart_weeks.setOption(option_radar_week);
            vip_group_info.resize(vip_group_info.myChart_weeks);
            var head="";
            var value="";
            for(var a=0;a<weekData.length;a++){
                head+='<li>'+weekData[a].name+'</li>';
                value+='<li style="word-break: break-all;" title="'+weekData[a].value+'">'+weekData[a].value+'</li>';
            }
            head="<ul><li></li>"+head+"</ul>";
            value=showType=="trade_amt"?"<ul><li>金额</li>"+value+"</ul>":showType=="discount"?"<ul><li>折扣</li>"+value+"</ul>":"<ul><li>件数</li>"+value+"</ul>";
            $("#weeks").next().html(head+value);
            $("#weeks").next().find("li").css("width",($("#weeks").next().width()-10)/$(head).find("li").length+"px")
        }
        if(id == "birth_vip_num"){
            vip_group_info.myChart_birth_vip_num= echarts.init(document.getElementById(id));
            var weekData=vip_group_info.allChartsData.birth_vip_num;
            var weekDataArray=[];
            var newWeekDataArray=[];
            for(var i=0;i<weekData.length;i++){
                weekDataArray.push(weekData[i].value);
                newWeekDataArray.push(weekData[i].value);
            }
            option_radar_month.series[0].data[0].value=weekDataArray;
            newWeekDataArray.sort(vip_group_info.sortNumber);
            for(var b=0;b<option_radar_month.radar[0].indicator.length;b++){
                option_radar_month.radar[0].indicator[b].max=newWeekDataArray[newWeekDataArray.length-1]*1.1==0?"10":newWeekDataArray[newWeekDataArray.length-1]*1.1;
            }
            if(weekData.length==0){
                $("#birth_vip_num").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
                $("#birth_vip_num").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
                return
            }
            option_radar_month.series[0].data[0].name="生日分布";
            vip_group_info.myChart_birth_vip_num.setOption(option_radar_month);
            vip_group_info.resize(vip_group_info.myChart_birth_vip_num);
            var head="";
            var value="";
            for(var a=0;a<weekData.length;a++){
                head+='<li>'+weekData[a].name+'</li>';
                value+='<li style="word-break: break-all;" title="'+weekData[a].value+'">'+weekData[a].value+'</li>';
            }
            head="<ul><li></li>"+head+"</ul>";
            value=showType=="trade_amt"?"<ul><li>金额</li>"+value+"</ul>":showType=="discount"?"<ul><li>折扣</li>"+value+"</ul>":"<ul><li>人数</li>"+value+"</ul>";
            $("#birth_vip_num").next().html(head+value);
            $("#birth_vip_num").next().find("li").css("width",($("#birth_vip_num").next().width()-10)/$(head).find("li").length+"px")
        }
        if(id == "price"){
            vip_group_info.myChart_price= echarts.init(document.getElementById(id));
            var priceData=showType==undefined ? vip_group_info.allChartsData.Price["trade_num"]:vip_group_info.allChartsData.Price[showType];
            var name=[];
            var value=[];
            for(var i=0;i<priceData.length;i++){
                name.push(priceData[i].name);
                value.push(priceData[i].value);
            }
            if(name.length>8){
                option_bar.dataZoom=[
                    {
                        type: 'slider',
                        show: true,
                        handleSize: 8,
                        width:15,
                        yAxisIndex: [0],
                        right:"20",
                        start:100,
                        end: 50
                    },
                    {
                        type: 'inside',
                        yAxisIndex: [0],
                        start: 0,
                        end: 100
                    }
                ]
            }
            option_bar.yAxis[0].data=name;
            option_bar.series[0].data=value;
            if(priceData.length==0){
                $("#price").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
                $("#price").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
                return
            }
            vip_group_info.myChart_price.setOption(option_bar);
            vip_group_info.resize(vip_group_info.myChart_price);
            var head="";
            var value="";
            if(priceData.length==0){
                $("#price").next().html("<div style='font-size: 18px;color: #666;text-align: center'>暂无数据</div>");
                return
            }
            for(var a=0;a<priceData.length;a++){
                head+='<li>'+priceData[a].name+'</li>';
                value+='<li style="word-break: break-all" title="'+priceData[a].value+'">'+priceData[a].value+'</li>';
            }
            head="<ul><li></li>"+head+"</ul>";
            value=showType=="trade_amt"?"<ul><li>金额</li>"+value+"</ul>":showType=="discount"?"<ul><li>折扣</li>"+value+"</ul>":"<ul><li>件数</li>"+value+"</ul>";
            $("#price").next().html(head+value);
            $("#price").next().find("li").css("width",($("#price").next().width()-10)/$(head).find("li").length+"px")
        }
        if(id == "age"){
            vip_group_info.myChart_age= echarts.init(document.getElementById(id));
            var ageData=showType==undefined ? vip_group_info.allChartsData.age["trade_num"]:vip_group_info.allChartsData.age[showType];
            var name=[];
            var value=[];
            for(var i=0;i<ageData.length;i++){
                name.push(ageData[i].name);
                value.push(ageData[i].value);
            }
            if(name.length>8){
                option_bar.dataZoom=[
                    {
                        type: 'slider',
                        show: true,
                        handleSize: 8,
                        width:15,
                        yAxisIndex: [0],
                        right:"20",
                        start:100,
                        end: 50
                    },
                    {
                        type: 'inside',
                        yAxisIndex: [0],
                        start: 0,
                        end: 100
                    }
                ]
            }
            option_bar.yAxis[0].data=name;
            option_bar.series[0].data=value;
            option_bar.series[0].name="年龄偏好";
            if(ageData.length==0){
                $("#age").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
                $("#age").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
                return
            }
            vip_group_info.myChart_age.setOption(option_bar);
            vip_group_info.resize(vip_group_info.myChart_age);
            var head="";
            var value="";
            if(ageData.length==0){
                $("#age").next().html("<div style='font-size: 18px;color: #666;text-align: center'>暂无数据</div>");
                return
            }
            for(var a=0;a<ageData.length;a++){
                head+='<li>'+ageData[a].name+'</li>';
                value+='<li style="word-break: break-all" title="'+ageData[a].value+'">'+ageData[a].value+'</li>';
            }
            head="<ul><li></li>"+head+"</ul>";
            value=showType=="trade_amt"?"<ul><li>金额</li>"+value+"</ul>":showType=="discount"?"<ul><li>折扣</li>"+value+"</ul>":"<ul><li>件数</li>"+value+"</ul>";
            $("#age").next().html(head+value);
            $("#age").next().find("li").css("width",($("#age").next().width()-10)/$(head).find("li").length+"px")
        }
        if(id == "cardType"){
            vip_group_info.myChart_card_type= echarts.init(document.getElementById(id));
            var cardTypeData=showType==undefined ? vip_group_info.allChartsData.cardType["trade_num"]:vip_group_info.allChartsData.cardType[showType];
            var name=[];
            var value=[];
            for(var i=0;i<cardTypeData.length;i++){
                name.push(cardTypeData[i].name);
                value.push(cardTypeData[i].value);
            }
            if(name.length>8){
                option_bar.dataZoom=[
                    {
                        type: 'slider',
                        show: true,
                        handleSize: 8,
                        width:15,
                        yAxisIndex: [0],
                        right:"20",
                        start:100,
                        end: 50
                    },
                    {
                        type: 'inside',
                        yAxisIndex: [0],
                        start: 0,
                        end: 100
                    }
                ]
            }
            option_bar.yAxis[0].data=name;
            option_bar.series[0].data=value;
            option_bar.series[0].name="会员类型";
            if(cardTypeData.length==0){
                $("#cardType").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
                $("#cardType").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
                return
            }

            vip_group_info.myChart_card_type.setOption(option_bar);
            vip_group_info.resize(vip_group_info.myChart_card_type);
            var head="";
            var value="";
            if(cardTypeData.length==0){
                $("#cardType").next().html("<div style='font-size: 18px;color: #666;text-align: center'>暂无数据</div>");
                return
            }
            for(var a=0;a<cardTypeData.length;a++){
                head+='<li>'+cardTypeData[a].name+'</li>';
                value+='<li style="word-break: break-all" title="'+cardTypeData[a].value+'">'+cardTypeData[a].value+'</li>';
            }
            head="<ul><li></li>"+head+"</ul>";
            value=showType=="trade_amt"?"<ul><li>金额</li>"+value+"</ul>":showType=="discount"?"<ul><li>折扣</li>"+value+"</ul>":showType=="size_array"?"<ul><li>人数</li>"+value+"</ul>":"<ul><li>件数</li>"+value+"</ul>";
            $("#cardType").next().html(head+value);
            $("#cardType").next().find("li").css("width",($("#cardType").next().width()-10)/$(head).find("li").length+"px")
        }
        if(id == "times"){
            vip_group_info.myChart_times= echarts.init(document.getElementById(id));
            var TimeData=showType==undefined ? vip_group_info.allChartsData.times["trade_num"]:vip_group_info.allChartsData.times[showType];
            var name=[];
            var value=[];
            for(var i=0;i<TimeData.length;i++){
                name.push(TimeData[i].name+":00");
                value.push(TimeData[i].value);
            }
            option_line.xAxis[0].data=name;
            option_line.series[0].data=value;
            if(TimeData.length==0){
                $("#times").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
                $("#times").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
                return
            }
            vip_group_info.myChart_times.setOption(option_line);
            vip_group_info.resize(vip_group_info.myChart_times);
            var head="";
            var value="";
            for(var a=0;a<TimeData.length;a++){
                head+='<li>'+TimeData[a].name+'</li>';
                value+='<li style="word-break: break-all" title="'+TimeData[a].value+'">'+TimeData[a].value+'</li>';
            }
            head="<ul><li></li>"+head+"</ul>";
            value=showType=="trade_amt"?"<ul><li>金额</li>"+value+"</ul>":showType=="discount"?"<ul><li>折扣</li>"+value+"</ul>":"<ul><li>件数</li>"+value+"</ul>";
            $("#times").next().html(head+value);
            $("#times").next().find("li").css("width",($("#times").next().width()-10)/$(head).find("li").length+"px")
        }
        if(id == "season" && vip_group_info.allChartsData.Season!=undefined){
            vip_group_info.myChart_season=echarts.init(document.getElementById(id));
            var seasonData=showType==undefined ? vip_group_info.allChartsData.Season["trade_num"]:vip_group_info.allChartsData.Season[showType];
            var seasonLegendAll=showType==undefined ? vip_group_info.allChartsData.Season["trade_num"]:vip_group_info.allChartsData.Season[showType];
            for(var i=0;i<seasonLegendAll.length;i++){
                seasonLegendAll[i].icon="12"
            }
            option_pie.legend.data=seasonLegendAll;
            option_pie.series[0].data=seasonData;
            option_pie.series[0].name="季节偏好";
            if(seasonData.length==0){
                $("#season").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
                $("#season").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
                return
            }
            vip_group_info.myChart_season.setOption(option_pie);
            vip_group_info.resize(vip_group_info.myChart_season);
            var head="";
            var value="";
            for(var a=0;a<seasonData.length;a++){
                head+='<li>'+seasonData[a].name+'</li>';
                value+='<li style="word-break: break-all" title="'+seasonData[a].value+'">'+seasonData[a].value+'</li>';
            }
            head="<ul><li></li>"+head+"</ul>";
            value=showType=="trade_amt"?"<ul><li>金额</li>"+value+"</ul>":showType=="discount"?"<ul><li>折扣</li>"+value+"</ul>":"<ul><li>件数</li>"+value+"</ul>";
            $("#season").next().html(head+value);
            $("#season").next().find("li").css("width",($("#season").next().width()-10)/$(head).find("li").length+"px")
        }
        if(id == "month"){
            vip_group_info.myChart_month= echarts.init(document.getElementById(id));
            var monthData=showType==undefined ? vip_group_info.allChartsData.Month["trade_num"]:vip_group_info.allChartsData.Month[showType];
            var monthDataArray=[];
            var newMonthDataArray=[];
            for(var i=0;i<monthData.length;i++){
                monthDataArray.push(monthData[i].value);
                newMonthDataArray.push(monthData[i].value)
            }
            option_radar_month.series[0].data[0].value=monthDataArray;
            newMonthDataArray.sort(vip_group_info.sortNumber);
            for(var b=0;b<option_radar_month.radar[0].indicator.length;b++){
                option_radar_month.radar[0].indicator[b].max=newMonthDataArray[newMonthDataArray.length-1]*1.1==0?"10":newMonthDataArray[newMonthDataArray.length-1]*1.1
            }
            if(monthData.length==0){
                $("#month").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
                $("#month").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
                return
            }
            option_radar_month.series[0].data[0].name="月份偏好";
            vip_group_info.myChart_month.setOption(option_radar_month);
            vip_group_info.resize(vip_group_info.myChart_month);
            var head="";
            var value="";
            for(var a=0;a<monthData.length;a++){
                head+='<li>'+monthData[a].name+'</li>';
                value+='<li style="word-break: break-all" title="'+monthData[a].value+'">'+monthData[a].value+'</li>';
            }
            head="<ul><li></li>"+head+"</ul>";
            value=showType=="trade_amt"?"<ul><li>金额</li>"+value+"</ul>":showType=="discount"?"<ul><li>折扣</li>"+value+"</ul>":"<ul><li>件数</li>"+value+"</ul>";
            $("#month").next().html(head+value);
            $("#month").next().find("li").css("width",($("#month").next().width()-10)/$(head).find("li").length+"px")
        }
        if(id == "areas"){
            vip_group_info.myChart_areas= echarts.init(document.getElementById(id));
            var areaData=showType==undefined ? vip_group_info.allChartsData.Area["trade_num"]:vip_group_info.allChartsData.Area[showType];
            var valArr=[];
            for(var i=0;i<areaData.length;i++){
                areaData[i].name=areaData[i].name.replace(/省|市/,"");
                if(areaData[i].name=="宁夏回族自治区"){
                    areaData[i].name="宁夏"
                }
                if(areaData[i].name=="内蒙古自治区"){
                    areaData[i].name="内蒙古"
                }
                if(areaData[i].name=="新疆维吾尔自治区"){
                    areaData[i].name="新疆"
                }
                if(areaData[i].name=="西藏自治区"){
                    areaData[i].name="西藏"
                }
                if(areaData[i].name=="广西壮族自治区"){
                    areaData[i].name="广西"
                }
                valArr.push(areaData[i].value);
            }
            valArr.sort(function(a,b){return a-b});
            option_map.series[0].data=areaData;
            option_map.dataRange.max=Number(valArr[valArr.length-1]);
            if(areaData.length==0){
                $("#areas").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
                $("#areas").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
                return
            }
            vip_group_info.myChart_areas.setOption(option_map);
            vip_group_info.resize(vip_group_info.myChart_areas);
        }
        if(id == "typeMid"){
            vip_group_info.myChart_typeMid=echarts.init(document.getElementById(id));
            var typeData=showType==undefined ? vip_group_info.allChartsData.TypeMid["trade_num"]:vip_group_info.allChartsData.TypeMid[showType];
            var typeLegendAll=showType==undefined ? vip_group_info.allChartsData.TypeMid["trade_num"]:vip_group_info.allChartsData.TypeMid[showType];
            for(var i=0;i<typeLegendAll.length;i++){
                typeLegendAll[i].icon="12"
            }
            option_pie.legend.data=typeLegendAll;
            option_pie.series[0].data=typeData;
            option_pie.series[0].name="中类偏好";
            if(typeData.length==0){
                $("#typeMid").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
                $("#typeMid").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
                return
            }
            vip_group_info.myChart_typeMid.setOption(option_pie);
            vip_group_info.resize(vip_group_info.myChart_typeMid);
            var head="";
            var value="";
            for(var a=0;a<typeData.length;a++){
                head+='<li>'+typeData[a].name+'</li>';
                value+='<li style="word-break: break-all" title="'+typeData[a].value+'">'+typeData[a].value+'</li>';
            }
            head="<ul><li></li>"+head+"</ul>";
            value=showType=="trade_amt"?"<ul><li>金额</li>"+value+"</ul>":showType=="discount"?"<ul><li>折扣</li>"+value+"</ul>":"<ul><li>件数</li>"+value+"</ul>";
            $("#typeMid").next().html(head+value);
            $("#typeMid").next().find("li").css("width",($("#typeMid").next().width()-10)/$(head).find("li").length+"px")
        }
        if(id == "typeSm"){
            vip_group_info.myChart_typeSm=echarts.init(document.getElementById(id));
            var typeData=showType==undefined ? vip_group_info.allChartsData.TypeSm["trade_num"]:vip_group_info.allChartsData.TypeSm[showType];
            var typeLegendAll=showType==undefined ? vip_group_info.allChartsData.TypeSm["trade_num"]:vip_group_info.allChartsData.TypeSm[showType];
            for(var i=0;i<typeLegendAll.length;i++){
                typeLegendAll[i].icon="12"
            }
            option_pie.legend.data=typeLegendAll;
            option_pie.series[0].data=typeData;
            option_pie.series[0].name="小类偏好";
            if(typeData.length==0){
                $("#typeSm").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
                $("#typeSm").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
                return
            }
            vip_group_info.myChart_typeSm.setOption(option_pie);
            vip_group_info.resize(vip_group_info.myChart_typeSm);
            var head="";
            var value="";
            for(var a=0;a<typeData.length;a++){
                head+='<li>'+typeData[a].name+'</li>';
                value+='<li style="word-break: break-all" title="'+typeData[a].value+'">'+typeData[a].value+'</li>';
            }
            head="<ul><li></li>"+head+"</ul>";
            value=showType=="trade_amt"?"<ul><li>金额</li>"+value+"</ul>":showType=="discount"?"<ul><li>折扣</li>"+value+"</ul>":"<ul><li>件数</li>"+value+"</ul>";
            $("#typeSm").next().html(head+value);
            $("#typeSm").next().find("li").css("width",($("#typeSm").next().width()-10)/$(head).find("li").length+"px")
        }
        if(id == "typeSeries"){
            $("#typeSeries").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
            $("#typeSeries").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
            vip_group_info.myChart_typeSeries=echarts.init(document.getElementById(id));
            var typeData=showType==undefined ? vip_group_info.allChartsData.TypeSeries["trade_num"]:vip_group_info.allChartsData.TypeSeries[showType];
            var typeLegendAll=showType==undefined ? vip_group_info.allChartsData.TypeSeries["trade_num"]:vip_group_info.allChartsData.TypeSeries[showType];
            for(var i=0;i<typeLegendAll.length;i++){
                typeLegendAll[i].icon="12"
            }
            option_pie.legend.data=typeLegendAll;
            option_pie.series[0].data=typeData;
            option_pie.series[0].name="系列偏好";
            if(typeData.length==0){
                $("#typeSeries").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
                $("#typeSeries").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
                return
            }
            vip_group_info.myChart_typeSeries.setOption(option_pie);
            vip_group_info.resize(vip_group_info.myChart_typeSeries);
            var head="";
            var value="";
            for(var a=0;a<typeData.length;a++){
                head+='<li>'+typeData[a].name+'</li>';
                value+='<li style="word-break: break-all" title="'+typeData[a].value+'">'+typeData[a].value+'</li>';
            }
            head="<ul><li></li>"+head+"</ul>";
            value=showType=="trade_amt"?"<ul><li>金额</li>"+value+"</ul>":showType=="discount"?"<ul><li>折扣</li>"+value+"</ul>":"<ul><li>件数</li>"+value+"</ul>";
            $("#typeSeries").next().html(head+value);
            $("#typeSeries").next().find("li").css("width",($("#typeSeries").next().width()-10)/$(head).find("li").length+"px")
        }
        if(id == "source"){
            vip_group_info.myChart_source=echarts.init(document.getElementById(id));
            var SourceData=showType==undefined ? vip_group_info.allChartsData.source["trade_num"]:vip_group_info.allChartsData.source[showType];
            var SourceLegendAll=showType==undefined ? vip_group_info.allChartsData.source["trade_num"]:vip_group_info.allChartsData.source[showType];
            for(var i=0;i<SourceLegendAll.length;i++){
                SourceLegendAll[i].icon="12"
            }
            option_pie.legend.data=SourceLegendAll;
            option_pie.series[0].data=SourceData;
            option_pie.series[0].name="渠道偏好";
            if(SourceData.length==0){
                $("#source").html("<div style='font-size: 18px;color: #666;text-align: center;display: block' class='data_table'>暂无数据</div>");
                $("#source").next().html("<div style='font-size: 18px;color: #666;text-align: center;'>暂无数据</div>");
                return
            }
            vip_group_info.myChart_source.setOption(option_pie);
            vip_group_info.resize(vip_group_info.myChart_source);
            var head="";
            var value="";
            for(var a=0;a<SourceData.length;a++){
                head+='<li>'+SourceData[a].name+'</li>';
                value+='<li style="word-break: break-all" title="'+SourceData[a].value+'">'+SourceData[a].value+'</li>';
            }
            head="<ul><li></li>"+head+"</ul>";
            value=showType=="trade_amt"?"<ul><li>金额</li>"+value+"</ul>":showType=="discount"?"<ul><li>折扣</li>"+value+"</ul>":showType=="size_array"?"<ul><li>人数</li>"+value+"</ul>":"<ul><li>件数</li>"+value+"</ul>";
            $("#source").next().html(head+value);
            $("#source").next().find("li").css("width",($("#source").next().width()-10)/$(head).find("li").length+"px")
        }
    },
    resize:function(chart){
        window.addEventListener("resize",function(){
            chart.resize();
        });
    },
    addChart:function(){    //图标新增弹窗
        //$(".chart_close").click(function () {
        //    $("#chart_add_wrap").hide();
        //    $("#p").hide();
        //});
        //$("#chart_add_enter").click(function () {
        //    $("#chart_add_details input[type='checkbox']:checked").each(function () {
        //        var val = $(this).nextAll("span").html();
        //        $("#chart_analyze .chart_module").each(function () {
        //            if ($(this).css("display") == "none" && val == $(this).find(".drag_area span").html()) {
        //                $(this).show();
        //                var ID = $(this).attr("data-id");
        //                vip_group_info.init_chart(ID);
        //            } else {
        //                return;
        //            }
        //        });
        //    });
        //    var order = [];
        //    $(".chart_module").each(function () {
        //        if ($(this).attr("data-id") !== undefined && $(this).css("display") == "block") {
        //            order.push($(this).attr("data-id"));
        //        }
        //    });
        //    vip_group_info.getCharts(order);
        //    $("#chart_add_wrap").hide();
        //    $("#p").hide();
        //});
        //$("#add_chart").click(function () {
        //    var arr = whir.loading.getPageSize();
        //    var w = arr[0];
        //    var h = arr[1];
        //    var left = (arr[0] - $("#chart_add_wrap").width()) / 2;
        //    var tp = (arr[3] - $("#chart_add_wrap").height()) / 2;
        //    $("#chart_add_wrap").css({"left": left, "top": tp, "position": "fixed"});
        //    $("#p").css({"width": w, "height": h});
        //    $("#chart_add_wrap").show();
        //    $("#p").show();
        //    $("#chart_analyze .chart_module").each(function (i, e) {
        //        var val = $(this).find(".drag_area").children("span").html();
        //        if ($(this).css("display") == "block") {
        //            $("#chart_add_details li").each(function () {
        //                if ($(this).find(".chart_checkbox span").html() == val) {
        //                    $(this).find(".check")[0].checked = true;
        //                }
        //            });
        //        } else {
        //            $("#chart_add_details li").each(function () {
        //                if ($(this).find(".chart_checkbox span").html() == val) {
        //                    $(this).find(".check")[0].checked = false;
        //                }
        //            });
        //        }
        //    });
        //});
        //图标新增弹窗
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
            $("#chart_analyze .chart_module").each(function () {
                var val = $(this).find(".drag_area").children("span").html();
                if ($(this).css("display") == "block") {
                    $("#chart_add_details li").each(function () {
                        if ($(this).find(".chart_name").html() == val) {
                            $(this).find(".chart_icon").html("-");
                            $(this).find(".chart_icon").removeClass("chart_icon").addClass("chart_icon_remove");
                        }
                    });
                }else if($(this).css("display") == "none"){
                    $("#chart_add_details li").each(function () {
                        if ($(this).find(".chart_name").html() == val) {
                            $(this).find(".chart_icon_remove").html("+");
                            $(this).find(".chart_icon_remove").removeClass("chart_icon_remove").addClass("chart_icon");
                        }
                    });
                }
            });
        });
        $(".chart_close").click(function () {
            $("#chart_add_wrap").hide();
            $("#p").hide();
        });
        $("#chart_add_enter").click(function () {
            $("#chart_add_details .chart_icon_remove").each(function () {
                var val = $(this).prev().find(".chart_name").html();
                $("#chart_analyze .chart_module").each(function (i,e) {
                    if ($(e).css("display") == "none" && val == $(e).find(".drag_area span").html()) {
                        $("#chart_analyze").append($(e));
                        $(e).show();
                        $(e).find(".chart_loading").show();
                        var ID = $(e).attr("data-id");
                        vip_group_info.getList("view",ID);
                        //vip_group_info.init_chart(ID);
                    } else {
                        return;
                    }
                });
            });
            $("#chart_add_details .chart_icon").each(function () {
                var val = $(this).prev().find(".chart_name").html();
                $("#chart_analyze .chart_module").each(function (i,e) {
                    if ($(e).css("display") !== "none" && val == $(e).find(".drag_area span").html()) {
                        $(e).find(".chart_head").next("li").html("");
                        $(e).find(".data_table").html("");
                        $(e).hide();
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
            $("#chart_add_wrap").hide();
            $("#p").hide();
            vip_group_info.getCharts(order);
        });
        $("#chart_add_details").on("click",".chart_icon",function () {
            $(this).removeClass().addClass("chart_icon_remove");
            $(this).html("-");
        });
        $("#chart_add_details").on("click",".chart_icon_remove",function () {
            $(this).removeClass().addClass("chart_icon");
            $(this).html("+");
        });
        //图表删除
        $(".chart_close_icon").click(function () {
            if($(this).parents(".chart_module").hasClass("chart_lg")){
                whir.loading.remove("mask");
                $("#chart_analyze .chart_module.chart_lg>ul>li").height($(".chart_module").height()-30);
                $(this).parents(".chart_module").removeClass("chart_lg");
                var chartShow=$(this).parents(".chart_module").attr("data-id");
                $(this).parents(".chart_module").find(".data_table ul li").css("width",($(this).parents(".chart_module").width()-20)/ $(this).parents(".chart_module").find(".data_table ul:first-child li").length+"px");
                switch (chartShow){
                    case "type":
                        vip_group_info.myChart_type.resize();
                        break;
                    case "weeks":
                        vip_group_info.myChart_weeks.resize();
                        break;
                    case "price":
                        vip_group_info.myChart_price.resize();
                        break;
                    case "times":
                        vip_group_info.myChart_times.resize();
                        break;
                    case "month":
                        vip_group_info.myChart_month.resize();
                        break;
                    case "series":
                        vip_group_info.myChart_series.resize();
                        break;
                    case "areas":
                        vip_group_info.myChart_areas.resize();
                        break;
                    case "season":
                        vip_group_info.myChart_season.resize();
                        break;
                    case "typeMid":
                        vip_group_info.myChart_typeMid.resize();
                        break;
                    case "typeSm":
                        vip_group_info.myChart_typeSm.resize();
                        break;
	                case "typeSeries":
                        vip_group_info.myChart_typeSeries.resize();
                        break;
                    case "age":
                        vip_group_info.myChart_age.resize();
                        break;
                    case "cardType":
                        vip_group_info.myChart_card_type.resize();
                        break;
                    case "sex_vip_num":
                        vip_group_info.myChart_type_sex.resize();
                        break;
                    case "birth_vip_num":
                        vip_group_info.myChart_birth_vip_num.resize();
                        break;
                    case "source":
                        vip_group_info.myChart_source.resize();
                        break;
                }
            }else if(!$(this).parents(".chart_module").hasClass("chart_lg")){
                var chartShow=$(this).parents(".chart_module").attr("data-id");
                $(this).find("#"+chartShow).html("");
                $(this).find(".data_table").html("");
                switch (chartShow){
                    case "type":
                        vip_group_info.myChart_type.dispose();
                        break;
                    case "weeks":
                        vip_group_info.myChart_weeks.dispose();
                        break;
                    case "price":
                        vip_group_info.myChart_price.dispose();
                        break;
                    case "times":
                        vip_group_info.myChart_times.dispose();
                        break;
                    case "month":
                        vip_group_info.myChart_month.dispose();
                        break;
                    case "series":
                        vip_group_info.myChart_series.dispose();
                        break;
                    case "areas":
                        vip_group_info.myChart_areas.dispose();
                        break;
                    case "season":
                        vip_group_info.myChart_season.dispose();
                        break;
                    case "typeMid":
                        vip_group_info.myChart_typeMid.dispose();
                        break;
                    case "typeSm":
                        vip_group_info.myChart_typeSm.dispose();
                        break;
                    case "typeSeries":
                        vip_group_info.myChart_typeSeries.dispose();
                        break;
                    case "age":
                        vip_group_info.myChart_age.dispose();
                        break;
                    case "cardType":
                        vip_group_info.myChart_card_type.dispose();
                        break;
                    case "sex_vip_num":
                        vip_group_info.myChart_type_sex.dispose();
                        break;
                    case "birth_vip_num":
                        vip_group_info.myChart_birth_vip_num.dispose();
                        break;
                    case "source":
                        vip_group_info.myChart_source.resize();
                        break;
                }
                $(this).parents(".chart_module").hide();
                var order = [];
                $(".chart_module").each(function () {
                    if ($(this).attr("data-id") !== undefined && $(this).css("display") == "block") {
                        order.push($(this).attr("data-id"));
                    }
                });
                $(this).prev(".chart_analyze_condition").children().eq(0).html("按件数分析");
                vip_group_info.getCharts(order);
            }
        });
    },
    sortNumber:function(a,b)
        {
            return a - b
        },
    screenOk:function(){
        $("#screen_vip_que_info").click(function () {
            vip_group_info.inx = 1;
            vip_group_info._param["id"]=vip_group_info.nowId;
            vip_group_info._param["corp_code"] = $("#"+vip_group_info.nowId).attr("data-code");
            vip_group_info._param["pageNumber"] =  vip_group_info.inx;
            vip_group_info._param["pageSize"] = vip_group_info.pageSize;
            vip_group_info._param["type"] ="list";

            if($("#"+vip_group_info.nowId).attr("data-group-code")==undefined){
                delete vip_group_info._param["id"];
                vip_group_info._param["fixed_code"]=vip_group_info.nowId;
            }else {
                delete  vip_group_info._param["fixed_code"];
            }
            var screen = [];
            getSelectData().then(function(screen){
                vip_group_info._param['screen']=screen;
                if (screen.length == 0) {
                    vip_group_info.filtrate = "";
                    vip_group_info.getList("list")
                }else {
                    vip_group_info.filtrate = "success";
                    vip_group_info.filtrates(vip_group_info.inx, vip_group_info.pageSize);
                }
                $(".select_box_wrap").hide();
            })
        });
    },
    filtrates:function(a,b){
        if($("#list_show").height()<$("#left").height()){
            $("#chart_mask").css("minHeight",$("#left").height()+10+"px");
        }else{
            $("#chart_mask").css("minHeight",$("#list_show").height()+"px");
        }
        $("#chart_mask").css("top","181px");
        $("#chart_mask").show();
        $("#chart_mask_text").show();
        oc.postRequire("post","/vipGroup/groupVipsScreen","", vip_group_info._param, function(data) {
            if(data.code=="0"){
                var message=JSON.parse(data.message);
                var list=message.all_vip_list;
                vip_group_info.count=message.pages;
                vip_group_info.exportAllvip_num=message.count;
                $(".table tbody").empty();
                if(list.length<=0){
                    $(".table p").remove();
                    $(".table").append("<p>没有找到信息,请重新搜索</p>");
                    $("#chart_mask").hide();
                    $("#chart_mask_text").hide();
                }else if(list.length>0){
                    $(".table p").remove();
                    vip_group_info.superaddition(list,a);
                    vip_group_info.jumpBianse();
                }
               vip_group_info.setPage($("#foot-num")[0],vip_group_info.count,a,b,vip_group_info.funcCode);
            }else if(data.code=="-1"){
                $("#chart_mask").hide();
                $("#chart_mask_text").hide();
                art.dialog({
                    time: 3,
                    lock: true,
                    cancel: false,
                    content: "筛选失败"
                });
            }
        })
    }
};
function fuzhiSelect(){
    var list=vip_group_info._param["screen"];
    for(var i=0;i<list.length;i++){
        var key=list[i].key;
        var type=list[i].type;
        var value=list[i].value;
        if((key=="brand_code" || key=="area_code" || key=="store_code" || key=="user_code") && $("#content_high").is(":visible")){
            var wrapHtml=$("#content_high .select_box_content_center .select_box_content_center_box .select_affiliation");
        }else{
            var wrapHtml=$("#"+select_type+" .select_box_content_center .select_box_content_center_box .center_item_group[data-key="+key+"]");
        }
        wrapHtml.show();
        if((key=="brand_code" || key=="area_code" || key=="store_code" || key=="user_code") && $("#content_high").is(":visible")){
            cloneHtml=wrapHtml.find(".item_list").nextAll("[data-key="+key+"]").clone();
            var name=list[i].name;
            var typeChild="";
            switch (key){
                case "brand_code":
                    typeChild="brand";
                    break;
                case "area_code":
                    typeChild="area";
                    break;
                case "store_code":
                    typeChild="shop";
                    break;
                case "user_code":
                    typeChild="staff";
                    break;
            }
            select_affiliation[typeChild].code=value;
            select_affiliation[typeChild].name=name;
            cloneHtml.find("input").attr("data-code",value);
            cloneHtml.find("input").attr("data-name",name);
            cloneHtml.find("input").val("已选"+value.split(",").length+"个");
            wrapHtml.find(".item_list").append(cloneHtml.show());
        }else if(key=="user_code"){
            var name=list[i].name;
            var cloneHtml=wrapHtml.find(".item_list").next().clone();
            cloneHtml.find("input").attr("data-code",value);
            cloneHtml.find("input").attr("data-name",name);
            select_affiliation.staff.code=value;
            select_affiliation.staff.name=name;
            cloneHtml.find("input").val("已选"+value.split(",").length+"个");
            wrapHtml.find(".item_list").append(cloneHtml.show());
        }else if(key=="VIP_GROUP_CODE"){
            var name=list[i].name;
            var cloneHtml=wrapHtml.find(".item_list").next().clone();
            cloneHtml.find("input").attr("data-code",value);
            cloneHtml.find("input").attr("data-name",name);
            select_affiliation.group.code=value;
            select_affiliation.group.name=name;
            cloneHtml.find("input").val("已选"+value.split(",").length+"个");
            wrapHtml.find(".item_list").append(cloneHtml.show());
        }else{
            switch (type){
                case "radio":
                    for(var d=0;d<value.length;d++){
                        var cloneHtml=wrapHtml.find(".item_list").next().clone();
                        var logic=value[d].logic;
                        var logic_data="";
                        var opera=value[d].opera;
                        var input_radio = cloneHtml.find("input[type='radio']");
                        if (input_radio.length > 0) {
                            input_radio.map(function (index, ele) {
                                var index = wrapHtml.find(".item_list").children().length;
                                var id = $(ele).attr("id");
                                var name = $(ele).attr("name");
                                $(ele).attr("id", id + index);
                                $(ele).attr("name", name + index);
                                $(ele).next().attr("for", id + index);
                                if($(ele).val()==value[d].value){
                                    $(ele).parents(".select_radio_group").find("input[type='radio']").prop("checked",false);
                                    $(ele).prop("checked",true);
                                }
                            })
                        }
                        if (key == "SEX_VIP") {
                            logic_data=getLogicDefault(logic);
                            cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select span").html(logic_data)
                        }else if(key=="ACTIVITY" || key=="TASK"){
                            var child_key=value[d].key.replace(key+"_","");
                            var selectType=wrapHtml.attr("class").replace(/center_item_group select_/,"");
                            logic_data=getLogicDefault(logic);
                            if(d>0){
                                opera=opera=="AND"?"并且":opera=="OR"?"或者":"";
                                cloneHtml.find(".imitate_select_wrap").eq(0).find(".imitate_select span").html(opera)
                            }
                            var child_key_name=cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select").next("ul").find("li[data-type="+child_key+"]").html();
                            cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select input").val(child_key_name);
                            cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select input").attr("data-type",child_key);
                            cloneHtml.find(".select_"+selectType+"_right_line+div").show();
                        }
                        wrapHtml.find(".item_list").append(cloneHtml.show());
                    }
                    break;
                case "text":
                    for(var t=0;t<value.length;t++){
                        var cloneHtml=wrapHtml.find(".item_list").next().clone();
                        var logic=value[t].logic;
                        var logic_data="";
                        var opera=value[t].opera;
                        if(t>0){
                            opera=opera=="AND"?"并且":opera=="OR"?"或者":"";
                            cloneHtml.find(".imitate_select_wrap").eq(0).find(".imitate_select span").html(opera)
                        }
                        logic_data=getLogicDefault(logic);
                        cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select span").html(logic_data);
                        if(logic_data=="为空" || logic_data=="不为空"){
                            cloneHtml.find(".imitate_select_wrap").eq(1).next().hide();
                        }else{
                            if(key=="ZONE"){
                                (function(t){
                                    var zone=value[t].value.split("-");
                                    var provinceSelect=cloneHtml.find(".select_city_group .imitate_select").eq(0);
                                    var citySelect=cloneHtml.find(".select_city_group .imitate_select").eq(1);
                                    var areaSelect=cloneHtml.find(".select_city_group .imitate_select").eq(2);
                                    provinceSelect.find("span").html(zone[0]);
                                    citySelect.find("span").html(zone[1]);
                                    areaSelect.find("span").html(zone[2]);
                                })(t)
                            }else if(key=="APP_ID"){
                                if(value[t].value==""){
                                    cloneHtml.find("input:text").val("");
                                    cloneHtml.find("input:text").attr("data-code",value[t].value);
                                    cloneHtml.find("input:text").attr("data-name",value[t].name);
                                }else{
                                    select_affiliation["public_signal"].code=value[t].value;
                                    select_affiliation["public_signal"].name=value[t].name;
                                    cloneHtml.find("input:text").val("已选"+value[t].value.split(",").length+"个");
                                    cloneHtml.find("input:text").attr("data-code",value[t].value);
                                    cloneHtml.find("input:text").attr("data-name",value[t].name);
                                }

                            }else if(key=="act_store"){
                                if(value[t].value==""){
                                    cloneHtml.find("input:text").val("");
                                    cloneHtml.find("input:text").attr("data-code",value[t].value);
                                    cloneHtml.find("input:text").attr("data-name",value[t].name);
                                }else{
                                    select_affiliation["act_store"].code=value[t].value;
                                    select_affiliation["act_store"].name=value[t].name;
                                    cloneHtml.find("input:text").val("已选"+value[t].value.split(",").length+"个");
                                    cloneHtml.find("input:text").attr("data-code",value[t].value);
                                    cloneHtml.find("input:text").attr("data-name",value[t].name);
                                }

                            }else if(key=="CARD_TYPE_ID"){
                                if(value[t].value==""){
                                    cloneHtml.find("input:text").val("");
                                    cloneHtml.find("input:text").attr("data-code",value[t].code);
                                    cloneHtml.find("input:text").attr("data-name",value[t].value);
                                }else {
                                    select_affiliation["vip_type"].code=value[t].code;
                                    select_affiliation["vip_type"].name=value[t].value;
                                    cloneHtml.find("input:text").val("已选"+value[t].value.split(",").length+"个");
                                    cloneHtml.find("input:text").attr("data-code",value[t].code);
                                    cloneHtml.find("input:text").attr("data-name",value[t].value);
                                }
                            }else{
                                if(cloneHtml.find("textarea").length>0){
                                    cloneHtml.find("textarea").val(value[t].value);
                                }else{
                                    cloneHtml.find("input:text").val(value[t].value);
                                }
                            }
                            if(wrapHtml.attr("data-expend")!==undefined && cloneHtml.has(".expend_select_wrap")){   //拓展资料，给多选赋值
                                var valArray=value[t].value.split(",");
                                var expendLi=cloneHtml.find(".expend_select_wrap ul li");
                                expendLi.map(function(dex,ele){
                                    for(var c=0;c<valArray.length;c++){
                                        if($(ele).find("span").html()==valArray[c]){
                                            $(ele).find("input").attr("checked",true)
                                        }
                                    }
                                })
                            }
                        }
                        wrapHtml.find(".item_list").append(cloneHtml.show());
                    }
                    break;
                case "interval":
                    for(var l=0;l<value.length;l++){
                        var cloneHtml=wrapHtml.find(".item_list").next().clone();
                        var logic_1=value[l].logic1;
                        var logic_2=value[l].logic2;
                        var opera=value[l].opera;
                        logic_1=getLogicDefault(logic_1);
                        logic_2=getLogicDefault(logic_2);
                        if(key=="TRADE"){
                            var child_key=value[l].key;
                            var index=value[l].key.lastIndexOf("_");
                            var month="";
                            month=child_key.slice(index+1);
                            if(month!="all" && isNaN(month)){
                                cloneHtml=wrapHtml.find(".item_list").nextAll("[data-key="+child_key+"]").clone();
                            }else{
                                child_key=child_key.slice(0,index);
                                cloneHtml=wrapHtml.find(".item_list").nextAll("[data-key="+child_key+"]").clone();
                                if(month=="all"){
                                    cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select span").html("累计");
                                }else{
                                    cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select span").html("最近"+month+"个月");
                                }
                                cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select span").attr("data-type",month);
                            }
                            cloneHtml.find(".imitate_select_wrap").eq(2).find(".imitate_select span").html(logic_1);
                            cloneHtml.find(".imitate_select_wrap").eq(3).find(".imitate_select span").html(logic_2);
                            cloneHtml.find("input:text").eq(0).val(value[l].start);
                            cloneHtml.find("input:text").eq(1).val(value[l].end);
                        }else if(key=="COUPON"){
                            var child_key=value[l].key;
                            var index=value[l].key.lastIndexOf("_");
                            var index_prev=value[l].key.indexOf("_");
                            var li="";
                            var type_code="";
                            var type_name="";
                            type_code=child_key.slice(index_prev+1,index);
                            child_key=child_key.slice(index+1);
                            li=cloneHtml.find(".select_coupon_right_label_name+span").find("ul li");
                            li.map(function(){
                                if($(this).attr("data-type")==type_code){
                                    type_name=$(this).html();
                                }
                            });
                            if(type_name==""){
                                type_name=="请选择";
                                type_code==""
                            }
                            cloneHtml=wrapHtml.find(".item_list").nextAll("[data-key="+child_key+"]").clone();
                            cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select input").val(type_name);
                            cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select input").attr("data-type",type_code);
                            cloneHtml.find(".imitate_select_wrap").eq(2).find(".imitate_select span").html(logic_1);
                            cloneHtml.find(".imitate_select_wrap").eq(3).find(".imitate_select span").html(logic_2);
                            cloneHtml.find("input:text").eq(1).val(value[l].start);
                            cloneHtml.find("input:text").eq(2).val(value[l].end);
                            cloneHtml.find(".select_coupon_right_line+div").show();
                        }else if(key=="CATA1_PRD" || key=="CATA2_PRD" || key=="CATA3_PRD"|| key=="CATA4_PRD" || key=="SEASON_PRD" || key=="T_BL_M" || key=="DAYOFWEEK" || key=="BRAND_ID" || key=="CATA3_PRD" || key=="STORE_AREA" || key=="PRICE_CODE"){
                            var child_key=value[l].key;
                            var type_name=value[l].name;
                            cloneHtml.find(".select_dimension").find(".imitate_select span").html(type_name);
                            cloneHtml.find(".select_dimension").find(".imitate_select span").attr("data-value",child_key);
                            cloneHtml.find(".imitate_select_wrap").eq(2).find(".imitate_select span").html(logic_1);
                            cloneHtml.find(".imitate_select_wrap").eq(3).find(".imitate_select span").html(logic_2);
                            cloneHtml.find("input:text").eq(0).val(value[l].start);
                            cloneHtml.find("input:text").eq(1).val(value[l].end);
                            cloneHtml.find(".select_dimension+div").show();
                        }else{
                            cloneHtml.find(".imitate_select_wrap").eq(1).find(".imitate_select span").html(logic_1);
                            cloneHtml.find(".imitate_select_wrap").eq(2).find(".imitate_select span").html(logic_2);
                            cloneHtml.find("input:text").eq(0).val(value[l].start);
                            cloneHtml.find("input:text").eq(1).val(value[l].end);
                            if(cloneHtml.has("input.laydate-icon").length>0){
                                var dateInput=cloneHtml.find("input.laydate-icon");
                                dateInput.map(function(num){
                                    var index = wrapHtml.find(".item_list").children().length;
                                    var id = $(this).attr("id");
                                    $(this).attr("id", id + index);
                                    if($(this).attr("onclick").indexOf("setDateSelectionDate")!=-1){
                                        if(num==0){
                                            $(this).attr("onclick","laydate({elem:'#"+id + index+"',max:'"+value[l].end+"',format: 'YYYY-MM-DD',choose:setDateSelectionDate})")
                                        }else {
                                            $(this).attr("onclick","laydate({elem:'#"+id + index+"',min:'"+value[l].start+"',format: 'YYYY-MM-DD',choose:setDateSelectionDate})")
                                        }
                                    }else if($(this).attr("onclick").indexOf("setDateSelection")!=-1){
                                        if(num==0){
                                            $(this).attr("onclick","laydate({elem:'#"+id + index+"',max:'"+value[l].end+"',format: 'YYYY-MM-DD hh:mm:ss',choose:setDateSelection})")
                                        }else {
                                            $(this).attr("onclick","laydate({elem:'#"+id + index+"',min:'"+value[l].start+"',format: 'YYYY-MM-DD hh:mm:ss',choose:setDateSelection})")
                                        }
                                    }else if($(this).attr("onclick").indexOf("Ownformat")!=-1){
                                        if(num==0){
                                            $(this).attr("onclick","laydate({elem:'#"+id + index+"',max:'2017-"+value[l].end+"',format: 'YYYY-MM-DD', istoday:false,choose:Ownformat})")
                                        }else {
                                            $(this).attr("onclick","laydate({elem:'#"+id + index+"',min:'2017-"+value[l].start+"',format: 'YYYY-MM-DD',istoday:false,choose:Ownformat})")
                                        }
                                    }
                                })
                            }
                        }
                        if(l>0){
                            opera=opera=="AND"?"并且":opera=="OR"?"或者":"";
                            cloneHtml.find(".imitate_select_wrap").eq(0).find(".imitate_select span").html(opera)
                        }
                        wrapHtml.find(".item_list").append(cloneHtml.show());
                    }
                    break
            }
        }
        var type=wrapHtml.attr("class").replace(/center_item_group select_/,"");
        runtimeData(type);
    }
}
//点击筛选
$("#filtrate_group").click(function () {
    //var arr = whir.loading.getPageSize();
    //var left = (arr[0] - $("#screen_wrapper").width()) / 2;
    //var tp = (arr[3] - $("#screen_wrapper").height()) / 2;
    //$("#p").css({"width": +arr[0] + "px", "height": +arr[1] + "px"});
    //$("#p").show();
    //$("#screen_wrapper").css("position","fixed");
    $(".select_box").css({
        height: $(window).height() - 130 + "px"
    });
    $(".select_box_wrap").show();
    $(".select_box_wrap .select_box").css({"top":"100px","left":"50%"});
    $(".select_box .center_item_group .item_list").html("");
    $(".select_box .center_item_group").hide();
    $(".select_box .selected_list").html("");
    if(vip_group_info._param["screen"]!==undefined && vip_group_info._param["screen"].length>0){
        $(".notSelect").hide();
        if(select_type=="content_basic"){
            $("#content_high .notSelect").show();
            $(".tabs_head>label:first-child").trigger("click");
        }else{
            $("#content_basic .notSelect").show();
            $(".tabs_head>label:last-child").trigger("click");
        }
        fuzhiSelect();
    }else{
        if($(".select_box .tabs_head").css("visibility")!="hidden"){
            $(".tabs_head>label:first-child").trigger("click");
        } else{
            //if(showAdvance && !showBasic){
            //    $(".tabs_head>label:last-child").trigger("click");
            //}
            //if(!showAdvance && showBasic){
            //    $(".tabs_head>label:first-child").trigger("click");
            //}
        }
        $(".notSelect").show();
    }
    $("#content_high .item_group").find(".group_arrow").css({
        animation: "none"
    });
    $(".select_box_wrap").css("bottom",$(window).height()-$(document).height());
});
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


var area_num=1;
var area_next=false;
var shop_num=1;
var shop_next=false;
var staff_num=1;
var staff_next=false;
var isscroll=false;
var  message={
    cache:{//缓存变量
        "vip_id":"",
        "area_codes":"",
        "area_names":"",
        "brand_codes":"",
        "brand_names":"",
        "store_codes":"",
        "store_names":"",
        "user_codes":"",
        "user_names":"",
        "group_codes":"",
        "group_name":"",
        "type":"",
        "count":"",
        "corp_code":""
    }
};

$(function(){
    $("#chart_analyze .chart_module>ul>li").height($(".chart_module").height()-30);
    vip_group_info.init();
    $(".icon-ishop_6-07").parent().click(function () {
        window.location.reload();
    });
});
$("#chart_parent").css("minHeight",parseInt(window.innerHeight||document.documentElement.clientHeight)-195+"px");
$("#chart_parent>div").css("minHeight",parseInt(window.innerHeight||document.documentElement.clientHeight)-195+"px");
//$("#chart_mask").css("minHeight",parseInt(window.innerHeight||document.documentElement.clientHeight)-195+"px");
function Ownformat(datas){
    var date=datas.split("-");
    date=date[1]+"-"+date[2];
    $(this.elem).val(date);
}
window.addEventListener("resize",function(){
    $("#chart_analyze .chart_module.chart_lg>ul>li").height($(".chart_lg").height()-30);
});

$(document).click(function (e) {
    var selector=$(".select_parent");
    if(!selector.is(e.target) && selector.has(e.target).length === 0){
        selector.hide();
    }
});
//防止ajax重复请求
var pendingRequests = {};
$.ajaxPrefilter(function( options, originalOptions, jqXHR ) {
    var key = options.url;
    var queryName = JSON.parse(originalOptions.data.param).message.type_view;
    var type = JSON.parse(originalOptions.data.param).message.type;
    if(key == "/vipGroup/groupVips" && !pendingRequests[queryName] && type=="view"){
        pendingRequests[queryName] = jqXHR;
    }else if((!pendingRequests[key] && key != "/vipGroup/groupVips") || (!pendingRequests[key] && type=="list" && key == "/vipGroup/groupVips")){
        pendingRequests[key] = jqXHR;
    }else {
        // jqXHR.abort();    //放弃后触发的提交
       if(pendingRequests[queryName] && key == "/vipGroup/groupVips" && type=="view" ){
            pendingRequests[queryName].abort();
            pendingRequests[queryName]=jqXHR
        }
        if((pendingRequests[key] && key != "/vipGroup/groupVips") || (pendingRequests[key] && type=="list" && key == "/vipGroup/groupVips")){
            pendingRequests[key].abort();
            pendingRequests[key]=jqXHR
        }
        //pendingRequests[name]?pendingRequests[name].abort(): pendingRequests[queryName]?pendingRequests[queryName].abort():pendingRequests[key].abort();// 放弃先触发的提交
        //pendingRequests[name]=jqXHR;
        //pendingRequests[queryName]=jqXHR
    }
    var complete = options.complete;
    options.complete = function(jqXHR, textStatus) {
        //pendingRequests[key] = null;
        //pendingRequests[name] = null;
        if ($.isFunction(complete)) {
            complete.apply(this, arguments);
        }
    };
});