var oc = new ObjectControl();
(function(root,factory){
	root.coupon = factory();
}(this,function(){
	var couponjs={};
	couponjs.param={
		arr:"",
		coupon_innfo:""
	};
	couponjs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	couponjs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	couponjs.checkNumber=function(obj,hint){
		var isCode=/^[0-9]{1,13}$/;
		if(!this.isEmpty(obj)){
			if(isCode.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				console.log(obj.length);
				this.displayHint(hint,"请输入数字！");
				return false;
			}
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	couponjs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	couponjs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	couponjs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	couponjs.bindbutton=function(){
		var self=this;
		$("#add_save").click(function(){
			if(couponjs.firstStep()){
				var a = $('.xingming input');
				var send_coupon_user=[];
				for (var i = 0; i < a.length; i++) {
					var user_code= $(a[i]).attr("data-code");
					var user_phone= $(a[i]).attr("data-phone");
					var user_name=$(a[i]).attr("data-name");
					var param={"user_name":user_name,"user_code":user_code,"user_phone":user_phone};
					send_coupon_user.push(param);
				}
				var corp_code=$("#OWN_CORP").val();//企业编号
				var brand_code=$("#brand_code").attr("data-value");//品牌编号
				var app_id=$("#vipCode").attr("data-value");//公众号
				var app_name=$("#vipCode").val();//公众号名称
				var Accounts=$("#Accounts").val();//优惠券
				var send_coupon_title=$("#theme").val().trim();//主题
				var send_coupon_desc=$("#send_coupon_desc").val().trim();//促销说明
				var time_start=$("#target_start_time").val();//开始时间
				var end_time=$("#target_end_time").val();//截止时间
				var coupon_num_per=$("#coupon_num_per").val().trim();//人均张数
				var coupon_innfo=self.param.coupon_innfo;//券详情
				if(brand_code==""){
					art.dialog({
						time: 1,
						lock: true,
						cancel: false,
						content:"所属品牌不能为空"
					});
					return;
				}
				if(app_id==""){
					art.dialog({
						time: 1,
						lock: true,
						cancel: false,
						content:"公众号不能为空"
					});
					return;
				}
				if(Accounts==""){
					art.dialog({
						time: 1,
						lock: true,
						cancel: false,
						content:"优惠券不能为空"
					});
					return;
				}
				if(time_start==""){
					art.dialog({
						time: 1,
						lock: true,
						cancel: false,
						content:"开始时间不能为空"
					});
					return;
				}
				if(end_time==""){
					art.dialog({
						time: 1,
						lock: true,
						cancel: false,
						content:"截止时间不能为空"
					});
					return;
				}
				if(send_coupon_user.length==0){
					art.dialog({
						time: 1,
						lock: true,
						cancel: false,
						content:"员工不能为空"
					});
					return;
				}
				var isactive="";
				var input = $("#is_active")[0];
				if (input.checked == true) {
					isactive = "Y";
				} else if (input.checked == false) {
					isactive = "N";
				}
				var _command="/sendTicket/add";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={};
				_params["corp_code"]=corp_code;
				_params["brand_code"]=brand_code;
				_params["app_id"]=app_id;
				_params["app_name"]=app_name;
				_params["send_coupon_title"]=send_coupon_title;
				_params["send_coupon_desc"]=send_coupon_desc;
				_params["time_start"]=time_start;
				_params["end_time"]=end_time;
				_params["coupon_num_per"]=coupon_num_per;
				_params["send_coupon_user"]=send_coupon_user;
				_params["coupon_innfo"]=coupon_innfo;
				_params["isactive"]=isactive;
				couponjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$("#edit_save").click(function(){
			if(couponjs.firstStep()){
				var a = $('.xingming input');
				var send_coupon_user=[];
				for (var i = 0; i < a.length; i++) {
					var user_code= $(a[i]).attr("data-code");
					var user_phone= $(a[i]).attr("data-phone");
					var user_name=$(a[i]).attr("data-name");
					var param={"user_name":user_name,"user_code":user_code,"user_phone":user_phone};
					send_coupon_user.push(param);
				}
				var corp_code=$("#OWN_CORP").val();//企业编号
				var brand_code=$("#brand_code").attr("data-value");//品牌编号
				var app_id=$("#vipCode").attr("data-value");//公众号
				var app_name=$("#vipCode").val();//公众号名称
				var send_coupon_title=$("#theme").val().trim();//主题
				var send_coupon_desc=$("#send_coupon_desc").val().trim();//促销说明
				var time_start=$("#target_start_time").val();//开始时间
				var end_time=$("#target_end_time").val();//截止时间
				var coupon_num_per=$("#coupon_num_per").val().trim();//人均张数
				var coupon_innfo=self.param.coupon_innfo;//券详情
				var isactive="";
				var input = $("#is_active")[0];
				var id=sessionStorage.getItem("id");
				if (input.checked == true) {
					isactive = "Y";
				} else if (input.checked == false) {
					isactive = "N";
				}
				if(brand_code==""){
					art.dialog({
						time: 1,
						lock: true,
						cancel: false,
						content:"所属品牌不能为空"
					});
					return;
				}
				if(app_id==""){
					art.dialog({
						time: 1,
						lock: true,
						cancel: false,
						content:"公众号不能为空"
					});
					return;
				}
				if(Accounts==""){
					art.dialog({
						time: 1,
						lock: true,
						cancel: false,
						content:"优惠券不能为空"
					});
					return;
				}
				if(time_start==""){
					art.dialog({
						time: 1,
						lock: true,
						cancel: false,
						content:"开始时间不能为空"
					});
					return;
				}
				if(end_time==""){
					art.dialog({
						time: 1,
						lock: true,
						cancel: false,
						content:"截止时间不能为空"
					});
					return;
				}
				if(send_coupon_user.length==0){
					art.dialog({
						time: 1,
						lock: true,
						cancel: false,
						content:"员工不能为空"
					});
					return;
				}
				var _command="/sendTicket/edit";//接口名
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				var _params={};
				_params["corp_code"]=corp_code;
				_params["brand_code"]=brand_code;
				_params["app_id"]=app_id;
				_params["app_name"]=app_name;
				_params["send_coupon_title"]=send_coupon_title;
				_params["send_coupon_desc"]=send_coupon_desc;
				_params["time_start"]=time_start;
				_params["end_time"]=end_time;
				_params["coupon_num_per"]=coupon_num_per;
				_params["send_coupon_user"]=send_coupon_user;
				_params["coupon_innfo"]=coupon_innfo;
				_params["isactive"]=isactive;
				_params["id"]=id;
				var opt = {//返回成功后的操作
					success:function(){

					}
				};
				couponjs.ajaxSubmit(_command,_params,opt);
			}else{
				return;
			}
		});
		$("#brand_list").on("click","li",function(){
			$("#brand_code").val($(this).text());
			if($(this).attr("data-id")!==$("#brand_code").attr("data-value")){
				self.getAppIds($(this).attr("data-id"));
			}
			$("#brand_code").attr("data-value",$(this).attr("data-id"));
		});
		$("#vipPublic").on("click","li",function(){
			$("#vipCode").val($(this).text());
			if($(this).attr("data-id")!==$("#vipCode").attr("data-value")){
				self.getCouponsByAppId($(this).attr("data-id"));
			}
			$("#vipCode").attr("data-value",$(this).attr("data-id"));
		});
		$("#vipPublic").on("click","li",function(){
			$("#vipCode").val($(this).text());
			if($(this).attr("data-id")!==$("#vipCode").attr("data-value")){
				self.getCouponsByAppId($(this).attr("data-id"));
			}
			$("#vipCode").attr("data-value",$(this).attr("data-id"));
		});
		$("#Accounts").click(function () {
			var ul = $("#Acc_dropdown");
			if(ul.css("display")=="none"){
				$(ul).show();
			}else {
				$(ul).hide();
			}
		});
		$("#Acc_dropdown").on("click","li",function () {
			$("#Accounts").val($(this).text());
			$("#Accounts").attr("data-value",$(this).attr("data-id"));
			var index=$(this).index();
			$(this).find("input")[0].checked=true;
			$(".couponcode").html(self.param.arr[index].couponcode);//券类型型号
			$(".parvalue").html(self.param.arr[index].parvalue);//面额
			$(".name").html(self.param.arr[index].name);//名称
			$(".minimumcharge").html(self.param.arr[index].minimumcharge);//最低消费
			$(".start_time").html(self.param.arr[index].start_time);//开始时间
			$(".end_time").html(self.param.arr[index].end_time);//结束时间
			$(".effective_days").html(self.param.arr[index].effective_days);//有效天数
			$("#coupon_pop_detail_content").html(self.param.arr[index].quan_details);//券的富文本
			self.param.coupon_innfo=self.param.arr[index];
		})
		$("#Accounts").blur(function () {
			setTimeout(function(){
				$("#Acc_dropdown").hide();
			},200);
		});
		$("#add_close,#back_task").click(function(){
			$(window.parent.document).find('#iframepage').attr("src","/vip/coupon_list.html?t="+ $.now());
		});
		$("#look_more").click(function(){
			whir.loading.add("mask",0.5);//加载等待框
			$("#coupon_pop").show();
		});
		$("#close_coupon_pop").click(function () {
			whir.loading.remove("mask",0.5);//加载等待框
			$("#coupon_pop").hide();
		})
		$(".item_2 .input_select,.drop_down .input_select").click(function(){
			var ul = $(this).parent().find("ul li");
			if(ul.length=="0"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content:"暂无数据"
				});
			}
		});
	};
	couponjs.getcorplist=function(corp_code){//获取企业列表
		var self=this;
		whir.loading.add("",0.5);
		oc.postRequire("post","/user/getCorpByUser","", "", function(data){
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				var corp_html='';
				for(var i=0;i<msg.corps.length;i++){
					corp_html+='<option value="'+msg.corps[i].corp_code+'">'+msg.corps[i].corp_name+'</option>';
				}
				$("#OWN_CORP").append(corp_html);
				if(corp_code!==""){
					$("#OWN_CORP option[value='"+corp_code+"']").attr("selected","true");
				}
				$("#OWN_CORP").searchableSelect();
				var code=$("#OWN_CORP").val();
				self.getbrandlist(code);
				$('#corp_select .corp_select .searchable-select-input').keydown(function(event){
					var event=window.event||arguments[0];
					if(event.keyCode == 13){
						var corp_code1=$("#OWN_CORP").val();
						if(code!==corp_code1){
							message.cache.area_codes="";
							message.cache.area_names="";
							message.cache.brand_codes="";
							message.cache.brand_names="";
							message.cache.store_codes="";
							message.cache.store_names="";
							message.cache.user_codes="";
							message.cache.user_names="";
							code=corp_code1;
							self.getbrandlist(corp_code1);
							$(".xingming").empty();
						}
					}
				});
				$('#corp_select .searchable-select-item').click(function(){
					var corp_code1=$("#OWN_CORP").val();
					if(code!==corp_code1){
						message.cache.area_codes="";
						message.cache.area_names="";
						message.cache.brand_codes="";
						message.cache.brand_names="";
						message.cache.store_codes="";
						message.cache.store_names="";
						message.cache.user_codes="";
						message.cache.user_names="";
						code=corp_code1;
						self.getbrandlist(corp_code1);
						$(".xingming").empty();
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
	};
	couponjs.getbrandlist=function (corp_code) {
		var self=this;
		var param={};
		param["corp_code"]=corp_code;
		oc.postRequire("post","/shop/brand", "",param, function(data){
			var message=JSON.parse(data.message);
			var list=message.brands;
			var html="";
			for(var i=0;i<list.length;i++){
				html+="<li data-id='"+list[i].brand_code+"'>"+list[i].brand_name+"</li>"
			}
			$("#brand_list").html(html);
			if(list.length>0){
				var brand_code=list[0].brand_code;
				self.getAppIds(brand_code);
				$("#brand_code").val("");
				$("#brand_code").attr("data-value","");
			}else {
				self.getAppIds("");
				$("#brand_code").val("");
				$("#brand_code").attr("data-value","");
			}
		})
	};
	couponjs.getAppIds=function (brand_code) {
		var self=this;
		if(brand_code!=="") {
			var param = {};
			param["corp_code"] = $("#OWN_CORP").val();
			param["brand_code"] = brand_code;
			oc.postRequire("post", "/sendTicket/getAppIds", "", param, function (data) {
				var list = JSON.parse(data.message);
				var html = "";
				for (var i = 0; i < list.length; i++) {
					html += "<li data-id='" + list[i].app_id + "'>" + list[i].app_name + "</li>"
				}
				$("#vipPublic").html(html);
				if(list.length>0){
					var app_id=list[0].app_id;
					self.getCouponsByAppId(app_id);
					$("#vipCode").val("");
					$("#vipCode").attr("data-value","");
				}else {
					self.getCouponsByAppId("");
					$("#vipCode").val("");
					$("#vipCode").attr("data-value","");
				}
			})
		}else {
			$("#vipCode").val("");
			$("#vipPublic").empty();
			$("#vipCode").attr("data-value","");
			self.getCouponsByAppId("");
		}
	};
	couponjs.getCouponsByAppId=function (app_id) {
		var self=this;
		if(app_id!=="") {
			var param={};
			param["corp_code"]=$("#OWN_CORP").val();
			param["app_id"]=app_id;
			oc.postRequire("post","/sendTicket/getCouponsByAppId", "",param, function(data){
				var list=JSON.parse(data.message);
				self.param.arr=list;
				var html="";
				for(var i=0;i<list.length;i++){
					html+="<li style='height:30px!important;line-height: 30px!important;' data-id='"+list[i].couponcode+"'><p class='checkbox_isactive'><input type='radio' value='wx'   name='test' class='check'><label style='height: 16px;'></label></p><span class='p16'>"+list[i].name+"</span></li>"
				}
				$("#Acc_dropdown").html(html);
				if(list.length>0){
					$("#Accounts").val("");
					$("#Accounts").attr("data-value","");
					$("#Accounts").val("");
					$("#Accounts").attr("data-value","");
					$(".couponcode").html("");//券类型型号
					$(".parvalue").html("");//面额
					$(".name").html("");//名称
					$(".minimumcharge").html("");//最低消费
					$(".start_time").html("");//开始时间
					$(".end_time").html("");//结束时间
					$(".effective_days").html("");//有效天数
					$("#coupon_pop_detail_content").html("");//券的富文本
				}else {
					$("#Accounts").val("");
					$("#Accounts").attr("data-value","");
					$(".couponcode").html("");//券类型型号
					$(".parvalue").html("");//面额
					$(".name").html("");//名称
					$(".minimumcharge").html("");//最低消费
					$(".start_time").html("");//开始时间
					$(".end_time").html("");//结束时间
					$(".effective_days").html("");//有效天数
					$("#coupon_pop_detail_content").html("");//券的富文本
				}
			})
		}else {
			$("#Accounts").val("");
			$("#Acc_dropdown").empty();
			$("#Accounts").attr("data-value","");
			$(".couponcode").html("");//券类型型号
			$(".parvalue").html("");//面额
			$(".name").html("");//名称
			$(".minimumcharge").html("");//最低消费
			$(".start_time").html("");//开始时间
			$(".end_time").html("");//结束时间
			$(".effective_days").html("");//有效天数
			$("#coupon_pop_detail_content").html("");//券的富文本

		}
	};
	couponjs.getInputValue=function(){
		var self=this;
		var id=sessionStorage.getItem("id");
		var param={};
		param["id"]=id;
		oc.postRequire("post","/sendTicket/select", "",param, function(data){
			var message=JSON.parse(data.message);
			var couponInfo=JSON.parse(message.couponInfo);
			self.param.coupon_innfo=couponInfo;
			$("#corp_code").val(message.corp_name);
			$("#corp_code").attr("data-value",message.corp_code);
			$("#OWN_CORP").val(message.corp_code);
			$("#brand_code").val(message.brand_name);
			$("#brand_code").attr("data-value",message.brand_code);
			$("#vipCode").val(message.app_name);
			$("#vipCode").attr("data-value",message.app_id);
			$("#theme").val(message.send_coupon_title);
			$("#send_coupon_desc").val(message.send_coupon_desc);
			$("#Accounts").val(message.coupon_name);
			$("#Accounts").attr("data-value",message.coupon_code);
			$("#target_start_time").val(message.time_start);
			$("#target_end_time").val(message.time_end);
			$("#coupon_num_per").val(message.coupon_num_per);
			$(".couponcode").html(couponInfo.couponcode);//券类型型号
			$(".parvalue").html(couponInfo.parvalue);//面额
			$(".name").html(couponInfo.name);//名称
			$(".minimumcharge").html(couponInfo.minimumcharge);//最低消费
			$(".start_time").html(couponInfo.start_time);//开始时间
			$(".end_time").html(couponInfo.end_time);//结束时间
			$(".effective_days").html(couponInfo.effective_days);//有效天数
			$("#coupon_pop_detail_content").html(couponInfo.quan_details);//券的富文本
			var send_coupon_users=JSON.parse(message.send_coupon_users);
			var ul="";
			console.log(send_coupon_users);
			for (var i = 0; i < send_coupon_users.length; i++) {
				//ul+="<li data-code='"+list[i].user_code+"' data-phone='"+list[i].phone+"'>"+list[i].user_name+"<div class='delectxing' onclick='deleteName(this)'></div></li>"
				ul += "<p><input type='text'readonly='readonly'style='width: 348px;margin-right: 10px' data-name='"+send_coupon_users[i].user_name+"' data-code='" + send_coupon_users[i].user_code + "' data-phone='" + send_coupon_users[i].user_phone + "' value='" + send_coupon_users[i].user_name + "'><span class='power remove_app_id' onclick='deleteName(this)'>删除</span></p>";
			}
			$('.xingming').html(ul);
		})
	};
	couponjs.ajaxSubmit=function(_command,_params,opt){
		whir.loading.add("",0.5);//加载等待框
		oc.postRequire("post", _command,"",_params, function(data){
			if(data.code=="0"){
				if(_command=="/sendTicket/add"){
					sessionStorage.setItem("id",data.message);
					$(window.parent.document).find('#iframepage').attr("src", "/vip/coupon_edit.html");
				}
			if(_command=="/sendTicket/edit"){
				art.dialog({
					time: 1,
					lock: true,
					cancel: false,
					content:"保存成功"
				});
		}
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
};
var bindFun = function(obj1){//绑定函数，根据校验规则调用相应的校验函数
	var _this;
	if(obj1){
		_this = jQuery(obj1);
	}else{
		_this = jQuery(this);
	}
	var command = _this.attr("verify");
	var obj = _this.val();
	var hint = _this.nextAll(".hint").children();
	if(couponjs['check' + command]){
		if(!couponjs['check' + command].apply(couponjs,[obj,hint])){
			return false;
		}
	}
	return true;
};
jQuery(":text").focus(function() {
	var _this = this;
	interval = setInterval(function() {
		bindFun(_this);
	}, 500);
}).blur(function(event) {
	clearInterval(interval);
});
var init=function(){
	couponjs.bindbutton();
	if($(".pre_title label").text()=="新增优惠券配额"){
		couponjs.getcorplist();
	}
	if($(".pre_title label").text()=="编辑优惠券配额"){
		couponjs.getInputValue();
	}
}
var obj = {};
obj.couponjs = couponjs;
obj.init = init;
return obj;
}));
var start = {
	elem: '#target_start_time',
	format: 'YYYY-MM-DD',
	min: laydate.now(), //设定最小日期为当前日期
	max: '2099-06-16 23:59:59', //最大日期
	istime: false,
	istoday: false,
	choose: function (datas) {
		end.min = datas; //开始日选好后，重置结束日的最小日期
		end.start = datas //将结束日的初始值设定为开始日
	}
};
var end = {
	elem: '#target_end_time',
	format: 'YYYY-MM-DD',
	min: laydate.now(),
	max: '2099-06-16 23:59:59',
	istime: false,
	istoday: false,
	choose: function (datas) {
		start.max = datas; //结束日选好后，重置开始日的最大日期
	}
};
laydate(start);
laydate(end);
jQuery(document).ready(function(){
	window.coupon.init();//初始化;
	$(".xingming,.coupon_pop_info_content,.coupon_pop_detail_content").niceScroll({
		cursorborder: "0 none",
		cursorcolor: "rgba(0,0,0,0.3)",
		cursoropacitymin: "0",
		boxzoom: false,
		autohidemode:false
	});
})