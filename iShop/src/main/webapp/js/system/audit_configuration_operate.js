var oc = new ObjectControl();
var audit={
    audit_user:[],
    id:sessionStorage.getItem("id"),
    staff_page_num:1,
    staff_next:false,
    isscroll:false,
    staff_page_size:20,
    bindDocument:function(){
        $(document).bind("click",function(){
            var _con = $('#auditor_group .selectInputWrap ul');   // 设置目标区域
            if(!_con.is(event.target) && _con.has(event.target).length === 0){ // Mark 1
                //$('#divTop').slideUp('slow');   //滑动消失
                _con.hide();          //淡出消失
            }
        })
    },
    resetGroupName:function(){
        var line=$("#auditor_group .auditor_line");
        line.map(function(){
            var index=$(this).index();
            $(this).find(".group_name").html("组"+(index+1))
        })
    },
    getstafflist:function () {
        var self=audit;
        var _this=this;
        var current_code=JSON.parse($(_this).attr("data-selected_code"));
        var tr = $("#table tbody input[type='checkbox']:checked").parents("tr");
        var pageSize = self.staff_page_size;
        var pageNumber = self.staff_page_num;
        var _param = {};
        var searchValue = $('#staff_search').val().trim();
        _param['store_code'] ="";
        _param['searchValue'] = $("#staff_search").val().trim();//搜索值
        _param["corp_code"] = $("#OWN_CORP").val();
        _param['pageNumber'] = pageNumber;
        _param['pageSize'] = pageSize;
        whir.loading.add("", 0.5);//加载等待框
        $("#mask").css("z-index", "10002");
        oc.postRequire("post", "/user/selectUsersByRole", "", _param, function (data) {
            if (data.code == "0") {
                var message = JSON.parse(data.message);
                var list = JSON.parse(message.list);
                var hasNextPage = list.hasNextPage;
                var cout = list.total;
                list = list.list;
                var staff_html = '';
                $("#screen_staff .s_pitch span").eq(1).text(cout);
                if (list.length>0) {
                    for (var i = 0; i < list.length; i++) {
                        var isAllowed="pointer";
                        for(var n=0;n<audit.audit_user.length;n++){
                            if(current_code.indexOf(list[i].user_code) == -1 && current_code.indexOf(audit.audit_user[n])==-1 && audit.audit_user[n].indexOf(list[i].user_code)!=-1){
                                isAllowed="not-allowed"
                            }
                        }
                        if(isAllowed=="pointer"){
                            staff_html += "<li style='cursor:"+isAllowed+"' ><div class='checkbox1'><input  type='checkbox' value='" + list[i].user_code + "' data-phone='" + list[i].phone + "' data-storename='" + list[i].user_name + "' name='test'  class='check'/><label></label></div><span class='p16'>" + list[i].user_name + "\(" + list[i].user_code + "\)</span></li>"
                        }else{
                            staff_html += "<li style='cursor:"+isAllowed+"' class='isNotAllowed'><div class='checkbox1'><input  type='checkbox' value='" + list[i].user_code + "' data-phone='" + list[i].phone + "' data-storename='" + list[i].user_name + "' name='test'  class='check'/><label></label></div><span class='p16'>" + list[i].user_name + "\(" + list[i].user_code + "\)</span></li>"
                        }
                    }
                }
                if (hasNextPage == true) {
                    self .staff_page_num++;
                    self.staff_next = false;
                }
                if (hasNextPage == false) {
                    self.staff_next = true;
                }
                //if (self.staff_page_num == 1 && searchValue == "") {
                //    $("#screen_staff .screen_content_l ul").append('<li><div class="checkbox1"><input type="checkbox" value="无" data-areaname="" name="test" class="check" id="checkboxThreeInput0"><label for="checkboxThreeInput0"></label></div><span class="p16">无</span></li>');
                //}
                $("#screen_staff .screen_content_l ul").append(staff_html);
                if (!self.isscroll) {
                    $("#screen_staff .screen_content_l").unbind("scroll").bind("scroll", function () {
                        var nScrollHight = $(this)[0].scrollHeight;
                        var nScrollTop = $(this)[0].scrollTop;
                        var nDivHight = $(this).height();
                        if (nScrollTop + nDivHight +5>= nScrollHight && nScrollTop != 0) {
                            if (self.staff_next) {
                                return;
                            }
                            self.staff_next=true;
                            self.getstafflist.apply(_this);
                        }
                    });
                }
                self.isscroll = true;
                var li = $("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
                for (var k = 0; k < li.length; k++) {
                    $("#screen_staff .screen_content_l input[value='" + $(li[k]).attr("id") + "']").attr("checked", "true");
                }
                whir.loading.remove();//移除加载框
            } else if (data.code == "-1") {
                whir.loading.remove();
                art.dialog({
                    time: 3,
                    lock: true,
                    cancel: false,
                    zIndex: 10003,
                    content: data.message
                });
            }
        });
    },
    bind:function(){
        var self=this;
        self.bindDocument();
        self.save();
        $(".select_sendType .checkbox+span").click(function(){
            $(this).prev().find("input").prop("checked")?$(this).prev().find("input").prop("checked",false):$(this).prev().find("input").prop("checked",true)
        });
        $("#back_audit_list,#send_close").click(function(){
            $(window.parent.document).find('#iframepage').attr("src","/system/audit_configuration.html?t="+ $.now());
        });
        $("#functional_documents").next("ul").find("li").bind("mousedown",function(){
            var functional_documents=$(this).html();
            var corp_code=$("#OWN_CORP").val();
            //audit.audit_user=[];
            //self.getAudit(corp_code,functional_documents);
            //$("#auditor_group .auditor_line:first-child").find(".selectInput").attr("data-selected","[]").attr("data-selected_code","[]").val("").attr("title","");
            //$("#auditor_group .auditor_line:first-child").nextAll().remove();
            self.resetChecked()
        });
        $("#auditor_group").on("click",".icon-ishop_6-01",function(){
            var cloneHtml=$(this).parent(".auditor_line").clone();
            var len=$("#auditor_group .auditor_line").length;
            cloneHtml.find(".selectInput").attr("data-selected","[]").attr("data-selected_code","[]").val("").attr("title","");
            cloneHtml.find(".icon-ishop_6-02").css("display","inline-block");
            cloneHtml.find(".icon-ishop_6-01").css("display","none");
            //var li=cloneHtml.find(".selectInputWrap li");
            //if(li && li.length>0){
            //    for(var n=0;n<li.length;n++){
            //        if($(li[n]).find("input").prop("checked")){
            //            $(li[n]).find("input").prop("checked",false);
            //            $(li[n]).css("cursor","not-allowed");
            //        }
            //    }
            //}
            $(this).parents("#auditor_group").append(cloneHtml);
            self.resetGroupName();
            if($("#auditor_group").children().length>1){
                $("#auditor_group").children().eq(0).find(".icon-ishop_6-02").css("display","inline-block");
                $("#auditor_group").children().eq(0).find(".icon-ishop_6-01").hide();
            }else{
                $("#auditor_group").children().eq(0).find(".icon-ishop_6-01").css("display","inline-block");
                $("#auditor_group").children().eq(0).find(".icon-ishop_6-02").hide();
            }
            $("#auditor_group").children(":last-child").find(".icon-ishop_6-01").css("display","inline-block");
            $("#auditor_group").children().not(":last-child").find(".icon-ishop_6-01").css("display","none");
        });
        $("#auditor_group").on("click",".icon-ishop_6-02",function(){
            var code=JSON.parse($(this).parent(".auditor_line").find("input.selectInput").attr("data-selected_code"));
            for(var c=0;c<code.length;c++){
                if(audit.audit_user.indexOf(code[c])!=-1){
                    var index=audit.audit_user.indexOf(code[c]);
                    audit.audit_user.splice(index,1)
                }
            }
            $(this).parent(".auditor_line").remove();
            self.resetGroupName();
            if($("#auditor_group").children().length>1){
                $("#auditor_group").children().eq(0).find(".icon-ishop_6-02").css("display","inline-block")
            }else{
                $("#auditor_group").children().eq(0).find(".icon-ishop_6-01").css("display","inline-block");
                $("#auditor_group").children().eq(0).find(".icon-ishop_6-02").hide();
            }
            $("#auditor_group").children(":last-child").find(".icon-ishop_6-01").css("display","inline-block");
            $("#auditor_group").children().not(":last-child").find(".icon-ishop_6-01").css("display","none");
        });
        jQuery(":text").focus(function() {
            var _this = this;
            interval = setInterval(function() {
                self.bindFun(_this);
            }, 500);
        }).blur(function() {
            clearInterval(interval);
        });
        $(".screen_area").on("click",".screen_content li",function(){
            if($(this).css("cursor")=="not-allowed") return;
            $(this).find("input.check").prop("checked")?$(this).find("input.check").prop("checked",false):$(this).find("input.check").prop("checked",true);
        });
        $("#auditor_group").on("click",".selectInputWrap .selectInput",function(e){
            e.stopPropagation();
            audit.staff_page_num = 1;
            $("#staff_search").val("")
            var name=JSON.parse($(this).attr("data-selected"));
            var code=JSON.parse($(this).attr("data-selected_code"));
            if (code.length>0) {
                var html_right = "";
                for (var h = 0; h < code.length; h++) {
                    html_right += "<li id='" + code[h] + "'>\
                        <div class='checkbox1'><input type='checkbox' value='" + code[h] + "' name='test' class='check'>\
                        <label></div><span class='p16'>" + name[h] + "</span>\
                        \</li>"
                }
                $("#screen_staff .s_pitch span").eq(0).html(h);
                $("#screen_staff .screen_content_r ul").html(html_right);
            } else {
                $("#screen_staff .s_pitch span").eq(0).html("0");
                $("#screen_staff .screen_content_r ul").empty();
            }
            $("#screen_staff .screen_content_l ul").unbind("scroll");
            $("#screen_staff .screen_content_l ul").empty();
            $("#screen_staff").show();
            $("#p").show();
            self.sureSelect.apply(this);

            //$(this).next("ul").toggle();
            //$(".auditor_group ul").not($(this).next("ul")).hide();
        });
        $("#screen_close_staff").click(function(){
            $("#screen_staff").hide();
            $("#p").hide();
            audit.isscroll=false
        });
        //$("#auditor_group").on("mousedown",".selectInputWrap ul li",function(){
        //    var cursor=$(this).css("cursor");
        //    if(cursor && cursor=="not-allowed") return;
        //    $(this).find("input").prop("checked")==true?$(this).find("input").prop("checked",false):$(this).find("input").prop("checked",true);
        //    var selected=JSON.parse($(this).parent().prev("input").attr("data-selected"));
        //    var selected_code=JSON.parse($(this).parent().prev("input").attr("data-selected_code"));
        //    var ul=$(".auditor_group ul").not($(this).parent());
        //    var code= $(this).find("span").attr("data-code");
        //    if($(this).find("input").prop("checked")){
        //        selected.push($(this).find("span").html());
        //        selected_code.push($(this).find("span").attr("data-code"));
        //        ul.map(function(){
        //            $(this).find("li").children("span[data-code='"+code+"']").parent().css("cursor","not-allowed")
        //        })
        //    }else{
        //        for(var s=0;s<selected.length;s++){
        //            if(selected[s]==$(this).find("span").html()){
        //                selected.splice(s,1);
        //                selected_code.splice(s,1);
        //            }
        //        }
        //        ul.map(function(){
        //            console.log($(this).find("li span[data-code='"+code+"']").parent().find("input").prop("checked"));
        //            if($(this).find("li span[data-code='"+code+"']").parent().find("input").prop("checked")){
        //                $(this).find("li").children("span[data-code='"+code+"']").parent().css("cursor","not-allowed")
        //            }else{
        //                $(this).find("li").children("span[data-code='"+code+"']").parent().css("cursor","pointer")
        //            }
        //        })
        //    }
        //    $(this).parent().prev("input").attr("data-selected",JSON.stringify(selected)).attr("data-selected_code",JSON.stringify(selected_code));
        //    $(this).parent().prev("input").val(selected.join(",")).attr("title",selected.join(","));
        //});
    },
    sureSelect:function(){
        audit.getstafflist.apply(this);
        var _this=this;
        //员工放大镜搜索
        $("#staff_search_f").unbind("click").bind("click",function () {
            audit.staff_page_num = 1;
            audit.isscroll = false;
            $("#screen_staff .screen_content_l").unbind("scroll");
            $("#screen_staff .screen_content_l ul").empty();
            audit.getstafflist.apply(_this);
        });
        //员工搜索
        $("#staff_search").unbind("keydown").bind("keydown",function () {
            var event = window.event || arguments[0];
            audit.staff_page_num = 1;
            if (event.keyCode == 13) {
                audit.isscroll = false;
                $("#screen_staff .screen_content_l").unbind("scroll");
                $("#screen_staff .screen_content_l ul").empty();
                audit.getstafflist.apply(_this);
            }
        });
        //点击员工确定按钮
        $("#screen_que_staff").unbind("click").bind("click",function () {
            var li = $("#screen_staff .screen_content_r input[type='checkbox']").parents("li");
            var user_codes= li.map(function(){
                return $(this).attr("id");
            }).get().reverse();
            var user_names= li.map(function(){
                return $(this).find(".p16").html();
            }).get().reverse();
            var code=JSON.parse($(_this).attr("data-selected_code"));
            $("#screen_staff").hide();
            $("#p").hide();
            for(var u=0;u<code.length;u++){
                if(user_codes.indexOf(code[u])==-1 && audit.audit_user.indexOf(code[u])!=-1){
                    var index=audit.audit_user.indexOf(code[u]);
                    audit.audit_user.splice(index,1);
                }
            }
            for(var b=0;b<user_codes.length;b++){
                if( audit.audit_user.indexOf(user_codes[b])==-1){
                    audit.audit_user.push(user_codes[b]);
                }
            }

            $(_this).attr("data-selected",JSON.stringify(user_names));
            $(_this).attr("data-selected_code",JSON.stringify(user_codes));
            $(_this).val(user_names.join(","));
            $(_this).attr("title",user_names.join(","));
            audit.isscroll=false;
        });
    },
    isEmpty:function(obj){
        if(obj.trim() == "" || obj.trim() == undefined){
            return true;
        }else{
            return false;
        }
    },
    checkEmpty:function(obj,hint){
        if(!this.isEmpty(obj)){
            this.hiddenHint(hint);
            return true;
        }else{
            this.displayHint(hint);
            return false;
        }
    },
    hiddenHint:function(hint){
        hint.removeClass('error_tips');
        hint.html("");//关闭，如果有友情提示则显示
    },
    displayHint:function(hint,content){
        hint.addClass("error_tips");
        if(!content)hint.html(hint.attr("hintInfo"));//错误提示
        else hint.html(content);
    },
    bindFun:function(obj1){//绑定函数，根据校验规则调用相应的校验函数
        var _this;
        if(obj1){
            _this = jQuery(obj1);
        }else{
            _this = jQuery(this);
        }
        var command = _this.attr("verify");
        var obj = _this.val();
        var hint = _this.nextAll(".hint").children();
        if(audit['check' + command]){
            if(!audit['check' + command].apply(audit,[obj,hint])){
                return false;
            }
        }
        return true;
    },
    resetChecked:function(){
        var li=$("#auditor_group .auditor_line:first-child .selectInputWrap li");
        for(var c=0;c<li.length;c++){
            $(li[c]).find("input").prop("checked",false)
        }
    },
    getCorplist:function(corp){//获取所属企业列表
        var self=this;
        var corp_command="/user/getCorpByUser";
        whir.loading.add("",0.5);//加载等待框
        oc.postRequire("post", corp_command,"", "", function(data){
            if(data.code=="0"){
                var msg=JSON.parse(data.message);
                var corp_html='';
                for(var i=0;i<msg.corps.length;i++){
                    corp_html+='<option value="'+msg.corps[i].corp_code+'">'+msg.corps[i].corp_name+'</option>';
                }
                $("#OWN_CORP").append(corp_html);
                if(corp!==""){
                    $("#OWN_CORP option[value='"+corp+"']").attr("selected","true");
                }
                var corp_code=$("#OWN_CORP").val();
                $('#corp_select select').searchableSelect();
                var functional_documents=$("#functional_documents").attr("data-value");
                //self.getAudit(corp_code,functional_documents);
                $('#corp_select .searchable-select-input').keydown(function(event){
                    var event=window.event||arguments[0];
                    if(event.keyCode == 13){
                        var corp_code1=$("#OWN_CORP").val();
                        if(corp_code!==corp_code1){
                            corp_code=corp_code1;
                            var functional_documents=$("#functional_documents").attr("data-value");
                            //self.getAudit(corp_code,functional_documents);
                            $("#auditor_group .auditor_line:first-child").find(".selectInput").attr("data-selected","[]").attr("data-selected_code","[]").attr("title","").attr("value","").val("");
                            $("#auditor_group .auditor_line:first-child").nextAll().remove();
                            self.resetChecked()
                            self.audit_user=[];
                        }
                    }
                });
                $('#corp_select .searchable-select-item').click(function(){
                    var corp_code1=$("#OWN_CORP").val();
                    if(corp_code!==corp_code1){
                        corp_code=corp_code1;
                        var functional_documents=$("#functional_documents").attr("data-value");
                        //self.getAudit(corp_code,functional_documents);
                        $("#auditor_group .auditor_line:first-child").find(".selectInput").attr("data-selected","[]").attr("data-selected_code","[]").attr("title","").attr("value","").val("");
                        $("#auditor_group .auditor_line:first-child").nextAll().remove();
                        self.resetChecked();
                        self.audit_user=[];
                    }
                })
            }else if(data.code=="-1"){
                art.dialog({
                    time: 1,
                    lock:true,
                    cancel: false,
                    content: data.message
                });
            }
            whir.loading.remove();//移除加载框
        });
    },
    getAudit:function(corp_code,function_name){
        var param={
            corp_code:corp_code,
            function_name:function_name
        };
        oc.postRequire("post", "/examine/examineUsers","", param, function(data){
            var msg=JSON.parse(data.message);
            var list=JSON.parse(msg.list);
            var audit_html="";
            if(list.length>0){
                for(var i=0;i<list.length;i++){
                    audit.audit_user.push(
                        {
                            "user_name":list[i].user_name,
                            "user_code":list[i].user_code,
                            "checked":false
                        }
                    );
                    audit_html+='<li data-value="">'+
                        '<div class="checkbox"><input type="checkbox" value="" name="test" title="全选/取消" class="check"><label></label></div>'+
                        '<span data-code="'+list[i].user_code+'">'+list[i].user_name+'</span>'+
                        '</li>'
                }
                $(".selectInputWrap ul").html(audit_html);
            }else{
                $(".selectInputWrap ul").html("<p class='noAudit'>暂无数据</p>")
            }
            var ul=$(".selectInputWrap ul");
            ul.map(function(){
                var code=JSON.parse($(this).prev().attr("data-selected_code"));
                var name=[];
                for(var c=0;c<code.length;c++){
                    $(this).find("li span[data-code='"+code[c]+"']").prev().find("input").prop("checked",true);
                    $(".selectInputWrap ul").not(this).find("li span[data-code='"+code[c]+"']").parent().css("cursor","not-allowed");
                    name.push($(this).find("li span[data-code='"+code[c]+"']").html());
                    $(this).prev().attr("data-selected", JSON.stringify(name)).val(name.join(",")).attr("title",name.join(","))
                }
            })
        })
    },
    firstStep:function(){
        var self=this;
        var inputText = jQuery(".conpany_msg").find(":text");
        for(var i=0,length=inputText.length;i<length;i++){
            if(!self.bindFun(inputText[i]))return false;
        }
        return true;
    },
    save:function(){
        $("#edit_save").click(function(){
            var url="";
            var examine_group=[];
            var examine_group_info=[];
            var is_active="";
            if($("#is_active").prop("checked")){
                is_active="Y"
            }else{
                is_active="N"
            }
            var param={
                "corp_code":$("#OWN_CORP").val(),
                "function_bill_name":$("#functional_documents").val(),
                "isactive":is_active
            };
            var auditor_line=$("#auditor_group .auditor_line");
            auditor_line.map(function(){
                var code=JSON.parse($(this).find(".selectInputWrap .selectInput").attr("data-selected_code"));
                var name=JSON.parse($(this).find(".selectInputWrap .selectInput").attr("data-selected"));
                if(code.length>0){
                    examine_group.push(code)
                }
                if(name.length>0){
                    examine_group_info.push(name)
                }
            });

            if(param.corp_code==""){
                art.dialog({
                    time: 2,
                    lock: true,
                    cancel: false,
                    content: "所属企业不能为空"
                });
                return
            }
            if(param.function_bill_name==""){
                art.dialog({
                    time: 2,
                    lock: true,
                    cancel: false,
                    content: "功能单据不能为空"
                });
                return
            }
            if(examine_group.length==0){
                art.dialog({
                    time: 2,
                    lock: true,
                    cancel: false,
                    content: "审核人员不能为空"
                });
                return
            }
            param.examine_group=JSON.stringify(examine_group);
            param.examine_group_info=JSON.stringify(examine_group_info);
            param.sms_examine=$("#sms_examine").prop("checked")?"Y":"N";
            param.sms_progress=$("#sms_progress").prop("checked")?"Y":"N";
            param.sms_result=$("#sms_result").prop("checked")?"Y":"N";
            if(audit.id && audit.id!=""){
                url="/examine/update";
                param.id=audit.id;
            }else{
                url="/examine/insert"
            }
            whir.loading.add("",0.5);//加载等待框
            oc.postRequire("post", url,"", param, function(data){
                whir.loading.remove();
                if(data.code==0){
                    if(url=="/examine/insert"){
                        var id=JSON.parse(data.message).id;
                        sessionStorage.setItem("id",id);
                        $(window.parent.document).find('#iframepage').attr("src", "/system/audit_configuration_edit.html");
                    }
                    if(url=="/examine/update"){
                        art.dialog({
                            time: 1,
                            lock: true,
                            cancel: false,
                            content:"保存成功"
                        });
                    }
                }else {
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:data.message
                    });
                }
            })
        })
    },
    getSelect:function(){
        var param={
            id:sessionStorage.getItem("id")
        };
        oc.postRequire("post","/examine/select","", param, function(data){
            if(data.code=="0"){
                var msg=JSON.parse(data.message);
                var corp_code=msg.corp_code;//公司编号
                var input=$("#is_active");
                var examine_group=JSON.parse(msg.examine_group);
                var examine_group_info=JSON.parse(msg.examine_group_info);
                var function_name=msg.function_bill_name;
                $("#functional_documents").attr("data-value",function_name).val(function_name);
                $("#creator").val(msg.creater);
                $("#created_time").val(msg.created_date);
                $("#modify_time").val(msg.modified_date);
                $("#modifier").val(msg.modifier);
                $("#sms_examine").prop("checked",msg.sms_examine=="Y"?true:false);
                $("#sms_progress").prop("checked",msg.sms_progress=="Y"?true:false);
                $("#sms_result").prop("checked",msg.sms_result=="Y"?true:false);
                var html='';
                for(var e=0; e<examine_group.length;e++){
                    for(var a=0;a<examine_group[e].length;a++){
                        audit.audit_user.push(examine_group[e][a])
                    }
                    html+='<div class="auditor_line">'+
                        '<span class="group_name">组'+(e+1)+'</span>'+
                        '<span class="selectInputWrap">'+
                        '<input type="text" class="input250px selectInput notEmpty" placeholder="选择审核员工" readonly="" title="'+examine_group_info[e].join(",")+'" value="'+examine_group_info[e].join(",")+'" data-selected=\''+JSON.stringify(examine_group_info[e])+'\' data-selected_code=\''+JSON.stringify(examine_group[e])+'\'>'+
                        '<ul>' +
                        '</ul>'+
                        '</span>'+
                        '<span class="add_btn icon-ishop_6-01"></span>'+
                        '<span class="add_btn icon-ishop_6-02"></span>'+
                        '</div>'
                }
                $("#auditor_group").html(html);
                if($("#auditor_group").children().length>1){
                    $("#auditor_group").children().eq(0).find(".icon-ishop_6-02").css("display","inline-block")
                }else{
                    $("#auditor_group").children().eq(0).find(".icon-ishop_6-01").css("display","inline-block");
                    $("#auditor_group").children().eq(0).find(".icon-ishop_6-02").hide();
                }
                $("#auditor_group").children(":last-child").find(".icon-ishop_6-01").css("display","inline-block");
                $("#auditor_group").children().not(":last-child").find(".icon-ishop_6-01").css("display","none");
                if(msg.isactive=="Y"){
                    input.prop("checked",true);
                }else if(msg.isactive=="N"){
                    input.prop("checked",false);
                }
                audit.getCorplist(corp_code);
            }
        })
    },
    init:function(){
        this.bind();
        var id=this.id;
        if(id!==null){
            this.getSelect(id);
        }else if(id==null){
            this.getCorplist()
        }
    }
};
//移到右边
function removeRight(a, b) {
    var li = "";
    if (a == "only") {
        li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li:visible");
    }
    if (a == "all") {
        li = $(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']").parents("li:visible").not(".isNotAllowed");
    }
    if(li.length>100){
        art.dialog({
            zIndex: 10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请选择少于100个"
        });
        return;
    }
    if (li.length == "0") {
        art.dialog({
            zIndex: 10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请先选择"
        });
        return;
    }
    if (li.length > 0) {
        for (var i = 0; i < li.length; i++) {
            var html = $(li[i]).html();
            var id = $(li[i]).find("input[type='checkbox']").val();
            $(li[i]).find("input[type='checkbox']")[0].checked = true;
            var input = $(b).parents(".screen_content").find(".screen_content_r li");
            for (var j = 0; j < input.length; j++) {
                if ($(input[j]).attr("id") == id) {
                    $(input[j]).remove();
                }
            }
            $(b).parents(".screen_content").find(".screen_content_r ul").prepend("<li id='" + id + "'>" + html + "</li>");
            $(b).parents(".screen_content").find(".screen_content_r input[value='" + id + "']").removeAttr("checked");
        }
    }
    var num = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").eq(0).html(num);
}
//移到左边
function removeLeft(a, b) {
    var li = "";
    if (a == "only") {
        li = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']:checked").parents("li");
    }
    if (a == "all") {
        li = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li");
    }
    if (li.length == "0") {
        art.dialog({
            zIndex: 10003,
            time: 1,
            lock: true,
            cancel: false,
            content: "请先选择"
        });
        return;
    }
    if (li.length > 0) {
        for (var i = li.length - 1; i >= 0; i--) {
            $(li[i]).remove();
            $(b).parents(".screen_content").find(".screen_content_l input[value='" + $(li[i]).attr("id") + "']").removeAttr("checked");
        }
    }
    var num = $(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
    $(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").eq(0).html(num);
}
//点击右移
$(".shift_right").click(function () {
    var right = "only";
    var div = $(this);
    removeRight(right, div);
});
//点击右移全部
$(".shift_right_all").click(function () {
    var right = "all";
    var div = $(this);
    removeRight(right, div);
});
//点击左移
$(".shift_left").click(function () {
    var left = "only";
    var div = $(this);
    removeLeft(left, div);
});
//点击左移全部
$(".shift_left_all").click(function () {
    var left = "all";
    var div = $(this);
    removeLeft(left, div);
});
audit.init();
