var oc = new ObjectControl();
(function(root,factory){
	root.regime= factory();
}(this,function(){
	var regimejs={
		"vip_type_code":"",
		"vip_type_high_code":"",
		"vip_type_down_code":""
	};
	regimejs.param={//定义参数类型
    	"area_codes":"",
    	"area_names":"",
    	"brand_codes":"",
    	"brand_names":"",
    	"store_codes":"",
    	"store_names":"",
    	"area_num":1,
		"area_next":false,
		"shop_num":1,
		"shop_next":false,
		"staff_num":1,
		"staff_next":false,
		"isscroll":false,
		"vip_type":"",
		edit_first_high:false,
		edit_first_down:false
	};
	regimejs.isEmpty=function(obj){
		if(obj.trim() == "" || obj.trim() == undefined){
			return true;
		}else{
			return false;
		}
	};
	regimejs.checkEmpty = function(obj,hint){
		if(!this.isEmpty(obj)){
			this.hiddenHint(hint);
			return true;
		}else{
			this.displayHint(hint);
			return false;
		}
	};
	regimejs.checkNumber=function(obj,hint){//检查必须是数字但是可以为空的状态
		var isMonth=$(hint).parent().siblings("input").attr("id")=="effective_time";
		if(isMonth){
			var isCode=/^(0|[1-9][0-9]*)$/;
		}else{
			var isCode=/^[0-9]+([.]{1}[0-9]+){0,1}$/;
		}
		if(!this.isEmpty(obj)){
			if(isCode.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				if(isMonth){
					this.displayHint(hint,"此处仅支持输入正整数！");
				}else{
					this.displayHint(hint,"此处仅支持输入数字！");
				}
				return false;
			}
		}else{
			this.displayHint(hint);
			return true;
		}
	};
	regimejs.checkNumber1=function(obj,hint){//检查必须是数字还要非空的状态
		var isCode=/^[0-9]+([.]{1}[0-9]+){0,1}$/;
		var reg = /^(0|[1-9][0-9]*)$/;
		if(!this.isEmpty(obj)){
			if(isCode.test(obj)){
				this.hiddenHint(hint);
				return true;
			}else{
				this.displayHint(hint,"此处仅支持输入数字！");
				return false;
			}
		}else{
			this.displayHint(hint);
			return true;
		}
	};
	regimejs.hiddenHint = function(hint){
		hint.removeClass('error_tips');
		hint.html("");//关闭，如果有友情提示则显示
	};
	regimejs.displayHint = function(hint,content){
		hint.addClass("error_tips");
		if(!content)hint.html(hint.attr("hintInfo"));//错误提示
		else hint.html(content);
	};
	regimejs.firstStep = function(){
		var inputText = jQuery(".conpany_msg").find(":text");
		for(var i=0,length=inputText.length;i<length;i++){
			if(!bindFun(inputText[i]))return false;
		}
		return true;
	};
	regimejs.bindbutton=function(){
		var self=this;
		$("#edit_save").click(function(){
			if(regimejs.firstStep()){
				var param={};
				var corp_code=$("#OWN_CORP").val().trim();//公司编号
				var discount=$("#discount").val().trim();//会员折扣
				var valid_date=$("#effective_time").val().trim(); //有效期限
				var join_threshold=$("#join_threshold").val().trim();//招募门槛
				var upgrade_time=$("#upgrade_time").attr("data-value");//升级门槛时间
				var upgrade_amount=$("#upgrade_amount").val().trim();//升级门槛金额
				var points_value=$("#points_value").val().trim();//积分比例
				var present_point=$("#present_point").val().trim();//送积分
				var rating_present_point=$("#rating_present_point").val().trim();// 保留等级下送积分
				var present_coupon=[];//券
				var rating_present_coupon=[];//券
				var quanList=$("#recruitment_threshold .coupon_list .coupon_line input");
				var ratingQuanList=$("#rating .coupon_list .coupon_line input");
				var isactive="";
				var input=$("#is_active")[0];//是否可用
				var is_present_point=$("#is_present_point")[0];//是否赠送积分
				var rating_is_present_point=$("#rating_is_present_point")[0];//是否赠送积分
				var is_present_coupon=$("#is_present_coupon")[0];//是否赠送券
				var rating_is_present_coupon=$("#rating_is_present_coupon")[0];//是否赠送券
				var is_shop=$("#is_shop")[0];//是否选择店铺
				var high_vip_type="";//上级会员类型名称
				var high_vip_card_type_code="";//上级会员类型编号
				var high_degree="";//上级会员类型等级

				var degrade_vip_name="";
				var degrade_vip_code="";
				var degrade_degree="";

				var vip_card_type_code="";//会员类型编号
				var vip_type=""; //会员类型名称
				var degree=""; //会员类型等级
				if($("#VIP_TYPE").val().trim()==""){
					vip_card_type_code=""; //会员类型编号
					vip_type="";  //会员类型名称
					degree="";//会员类型等级
				}else{
					vip_card_type_code=$("#VIP_TYPE").val().split("&")[0];//会员类型编号
					vip_type=$("#VIP_TYPE option:selected").attr("data-name");//会员类型名称
					degree=$("#VIP_TYPE").val().split("&")[1];//会员类型等级
				}
				if($("#VIP_TYPE_HIGH").val()==""){
					high_vip_type="";
					high_vip_card_type_code="";
					high_degree="";
				}else{
					high_vip_type=$("#VIP_TYPE_HIGH option:selected").attr("data-name");
					high_vip_card_type_code=$("#VIP_TYPE_HIGH").val().split("&")[0];
					high_degree=$("#VIP_TYPE_HIGH").val().split("&")[1]
				}
				if($("#VIP_TYPE_DOWN").val()==""){
					degrade_vip_name="";
					degrade_vip_code="";
					degrade_degree="";
				}else{
					degrade_vip_code=$("#VIP_TYPE_DOWN").val().split("&")[0];  //降级会员等级编号
					degrade_degree=$("#VIP_TYPE_DOWN").val().split("&")[1];  //降级会员等级
					degrade_vip_name=$("#VIP_TYPE_DOWN option:selected").attr("data-name");  //降级会员等级名字
				}
				if(input.checked==true){//是否可用
					isactive="Y";
				}else if(input.checked==false){
					isactive="N";
				}
				for(var i=0;i<quanList.length;i++){//券的list
					var _param={};
					if($(quanList[i]).attr("data-couponcode")!==""){
						_param["coupon_name"]=$(quanList[i]).val().trim();
						_param["coupon_code"]=$(quanList[i]).attr("data-couponcode");
						present_coupon.push(_param);
					}
				}
				for(var i=0;i<ratingQuanList.length;i++){//券的list
					var _param={};
					if($(ratingQuanList[i]).attr("data-couponcode")!==""){
						_param["coupon_name"]=$(ratingQuanList[i]).val().trim();
						_param["coupon_code"]=$(ratingQuanList[i]).attr("data-couponcode");
						rating_present_coupon.push(_param);
					}
				}
				if(is_present_point.checked==true){
					param["present_point"]=present_point;//送积分
				}else if(is_present_point.checked==false){
					param["present_point"]="";//送积分
				}
				if(rating_is_present_point.checked==true){  //保留等级条件下的积分
					param["keep_present_point"]=rating_present_point;//送积分
				}else if(rating_is_present_point.checked==false){
					param["keep_present_point"]="";//送积分
				}
				if(is_present_coupon.checked==true && present_coupon.length>0){
					param["present_coupon"]=present_coupon;//送券
				}else {
					param["present_coupon"]="";//送券
				}
				if(rating_is_present_coupon.checked==true && rating_present_coupon.length>0){  //保留等级条件下的送券
					param["keep_present_coupon"]=rating_present_coupon;//送券
				}else{
					param["keep_present_coupon"]="";//送券
				}
				if(is_shop.checked==true){
					param["store_code"]=self.param.store_codes;//店铺编号
				}else if(is_shop.checked==false){
					param["store_code"]="";//店铺编号
				}
				if(vip_type==""){
					art.dialog({
	                    time: 1,
	                    lock: true,
	                    cancel: false,
	                    content:"请先定义会员类型"
                	});
					return;
				}
				if(join_threshold == ''){
                    art.dialog({
                        time: 1,
                        lock: true,
                        cancel: false,
                        content:"消费条件不能为空"
                    });
                    return;
				}
				param["keep_grade_condition"]={
					price:{

					}
				};
				param["keep_grade_condition"].time=$("#reserved_rating").attr("data-value");  //保留等级条件    时间
				param["keep_grade_condition"]["price"]["start"]=$("#amount_start").val().trim(); //保留等级条件    开始时间
				param["keep_grade_condition"]["price"]["end"]="";    //保留等级条件    结束时间
				var appid =$('#vipCode').attr('data-app_id');
				param["corp_code"]=corp_code;//公司编号
				param["vip_type"]=vip_type;//会员类型
				param["valid_date"]=valid_date;//有效期限
				param["vip_card_type_code"]=vip_card_type_code;//会员类型编号
				param["degree"]=degree;//会员类型等级
				param["high_vip_type"]=high_vip_type;//上级会员类型
				param["high_vip_card_type_code"]=high_vip_card_type_code;//上级会员类型编号
				param["high_degree"]=high_degree;//上级会员等级
				param["discount"]=discount;//会员折扣
				param["join_threshold"]=join_threshold;//招募门槛
				param["upgrade_time"]=upgrade_time;//升级门槛时间
				param["upgrade_amount"]=upgrade_amount;//升级门槛金额
				param["points_value"]=points_value;//积分比例
				param["degrade_vip_code"]=degrade_vip_code;  //降级会员等级编号
				param["degrade_degree"]=degrade_degree;  //降级会员等级
				param["degrade_vip_name"]=degrade_vip_name;  //降级会员等级名字


				param["isactive"]=isactive;//是否可用
				//param["persent_coupon"]=persent_coupon;
				param["app_id"]=appid;
				var id=sessionStorage.getItem("id");//获取保存的id
				if(id==null){
					var command="/vipRules/add";
					regimejs.ajaxSubmit(command,param);
				}else if(id!==null){
					param["id"]=id;
					var command="/vipRules/edit";
					regimejs.ajaxSubmit(command,param);
				}
			}else{
				return;
			}
		});
		//关闭
		$("#edit_close").click(function(){
			$(window.parent.document).find('#iframepage').attr("src","/vip/vip_regime.html");
		});
		//回到列表
		$("#back_regime").click(function(){
			$(window.parent.document).find('#iframepage').attr("src","/vip/vip_regime.html");
		});
		//添加券
		$("#add_quan").click(function(){
			var corp_code=$("#OWN_CORP").val();
			var is_present_coupon=$("#is_present_coupon")[0];
			if(is_present_coupon.checked==false){
				art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content:"请先启用此参数"
                });
				return;
			}
			if($('#vipCode').val().trim() == '请选择公众号' ){
				art.dialog({
					time: 1,
					lock: true,
					cancel: false,
					content:"请选择公众号"
				});
				return;
			}
			$("#rating_add_quan").removeClass("active");
			$(this).addClass("active");
			self.getQuanList(corp_code);
		});
		//添加券
		$("#rating_add_quan").click(function(){
			var corp_code=$("#OWN_CORP").val();
			var rating_is_present_coupon=$("#rating_is_present_coupon")[0];
			if(rating_is_present_coupon.checked==false){
				art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content:"请先启用此参数"
                });
				return;
			}
			if($('#vipCode').val().trim() == '请选择公众号' ){
				art.dialog({
					time: 1,
					lock: true,
					cancel: false,
					content:"请选择公众号"
				});
				return;
			}
			$("#add_quan").removeClass("active");
			$(this).addClass("active");
			self.getQuanList(corp_code);
		});
		$("#present_point").click(function(){
			var is_present_point=$("#is_present_point")[0];
			if(is_present_point.checked==false){
				art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content:"请先启用此参数"
                });
				return;
			}
		});
		$("#rating_present_point").click(function(){
			var rating_is_present_point=$("#rating_is_present_point")[0];
			if(rating_is_present_point.checked==false){
				art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content:"请先启用此参数"
                });
				return;
			}
		});
		//删除券
		$("#quan_select,#rating_quan_select").on("click",".q_remove",function(){
			$(this).parent().remove();
		});
		//券点击li赋值
		$(".item_1").on("click","ul li",function(){
			var txt = $(this).text();
			var degree=$(this).attr("data-degree");
			var value=$(this).attr("data-value");
			var corp_code=$("#OWN_CORP").val();
			$(this).parents(".item_1").find(".input_select").val(txt);
			$(this).parents(".item_1").find(".input_select").attr("data-value",value);
			$(this).parents(".item_1").find(".input_select").attr("data-degree",degree);
			$(".item_1 ul").hide();
			if($(this).parents(".item_1").find(".input_select").attr("id")=="vip_type"){
				if(txt=="无会员类型"){
					return;
				}
				$("#high_vip_type").val("");
				$("#high_vip_type").attr("data-value","");
				$("#high_vip_type").attr("data-degree","");
				$("#down_vip_type").val("");
				$("#down_vip_type").attr("data-value","");
				$("#down_vip_type").attr("data-degree","");
				self.getHighVipCardTypes(corp_code,degree,"high");
				self.getHighVipCardTypes(corp_code,degree,"down");
			}
		});
		//券input框点击显示和隐藏
		$("#quan_select,#rating_quan_select").on("click",".item_2 .input_select",function(){
			var ul = $(this).parent().find("ul");
			if(ul.css("display")=="none"){
				ul.show();
			}else{
				ul.hide();
			}
		});
		//点击li给input赋值
		$("#quan_select,#rating_quan_select").on("click",".item_2 ul li",function(){
			var txt = $(this).text();
			var couponcode=$(this).attr("data-couponcode");
			//var appid=$(this).attr("data-appid");
			$(this).parent().siblings('.input_select').val(txt);
			$(this).parent().siblings('.input_select').attr("data-couponcode",couponcode);
			//$(this).parent().siblings('.input_select').attr("data-appid",appid);
			$(this).parent().siblings('.input_select').attr("title",txt);
			$(".item_2 ul").hide();
		});
		//券的input框失去焦点的时候隐藏
		$("#quan_select,#rating_quan_select").on("blur",".item_2 .input_select",function(){
			var ul = $(this).parent().find("ul");
			setTimeout(function(){
				ul.hide();
			},200);
		});
		//点击input框显示出店铺列表
		$("#get_store").click(function(){
			var shop_num=1;
			self.param.isscroll=false;
			$("#screen_shop .screen_content_l").unbind("scroll");
			$("#screen_shop .screen_content_l ul").empty();
			$("#screen_shop .screen_content_r ul").empty();
			$(".input_search input").val("");
			if(self.param.store_codes!==""){
				var store_codes=self.param.store_codes.split(',');
				var store_names=self.param.store_names.split(',');
				var store_html_right="";
			    for(var h=0;h<store_codes.length;h++){
					store_html_right+="<li id='"+store_codes[h]+"'>\
					<div class='checkbox1'><input type='checkbox' value='"+store_codes[h]+"'  data-storename='"+store_names[h]+"' name='test' class='check'>\
					<label></div><span class='p16'>"+store_names[h]+"</span>\
					\</li>"
				}
				$("#screen_shop .s_pitch span").html(h);
				$("#screen_shop .screen_content_r ul").html(store_html_right);
			}else{
				$("#screen_shop .s_pitch span").html("0");
				$("#screen_shop .screen_content_r ul").empty();
			}
			var is_shop=$("#is_shop")[0];
			if(is_shop.checked==false){
				art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content:"请先启用此参数"
                });
				return;
			}
			$("#loading").remove();
			$("#screen_shop").show();
			self.getStoreList(shop_num);
		});
		//点击x号关闭
		$(".screen_close").click(function(){
			$(this).parents(".screen_area").hide();
		});
		//点击区域确定按钮
		$("#screen_que_area").click(function(){
			var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
		    var area_codes="";
		    var area_names="";
		    for(var i=li.length-1;i>=0;i--){
		        var r=$(li[i]).attr("id");
		        var p=$(li[i]).find(".p16").html();
		        if(i>0){
		            area_codes+=r+",";
		            area_names+=p+",";
		        }else{
		            area_codes+=r;
		            area_names+=p;
		        }
		    };
		    self.param.area_codes=area_codes;
		    self.param.area_names=area_names;
		    $("#screen_area").hide();
		    $("#screen_shop").show();
		    $("#screen_area_num").val("已选"+li.length+"个");
		    $(".area_num").val("已选"+li.length+"个");
		    var shop_num=1;
			self.param.isscroll=false;
			$("#screen_shop .screen_content_l").unbind("scroll");
			$("#screen_shop .screen_content_l ul").empty();
			self.getStoreList(shop_num);
		});
		//点击品牌确定按钮
		$("#screen_que_brand").click(function(){
			var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
			var brand_codes="";
			var brand_names="";
			for(var i=li.length-1;i>=0;i--){
		        var r=$(li[i]).attr("id");
		        var p=$(li[i]).find(".p16").html();
		        if(i>0){
		            brand_codes+=r+",";
		            brand_names+=p+",";
		        }else{
		            brand_codes+=r;
		            brand_names+=p;
		        }
		    };
		    self.param.brand_codes=brand_codes;
		    self.param.brand_names=brand_names;
		    $("#screen_brand").hide();
		    $("#screen_shop").show();
		    $("#screen_brand_num").val("已选"+li.length+"个");
		    $(".brand_num").val("已选"+li.length+"个");
			var shop_num=1;
			self.param.isscroll=false;
			$("#screen_shop .screen_content_l").unbind("scroll");
			$("#screen_shop .screen_content_l ul").empty();
			self.getStoreList(shop_num);
		});
		//点击店铺确定按钮
		$("#screen_que_shop").click(function(){
			var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
			var store_codes="";
			var store_names="";
			for(var i=li.length-1;i>=0;i--){
		        var r=$(li[i]).attr("id");
		        var p=$(li[i]).find(".p16").html();
		        if(i>0){
		            store_codes+=r+",";
		            store_names+=p+",";
		        }else{
		            store_codes+=r;
		            store_names+=p;
		        }
		    };
		    self.param.store_codes=store_codes;
		    self.param.store_names=store_names;
		    $("#screen_shop").hide();
		    //$("#shop_list").val("已选"+li.length+"个");
		    $("#shop_list").html(li.length);
		});
		//店铺里面的区域点击
		$("#shop_area").click(function(){
			$(".input_search input").val("");
			if(self.param.area_codes!==""){
				var area_codes=self.param.area_codes.split(',');
				var area_names=self.param.area_names.split(',');
				var area_html_right="";
			    for(var h=0;h<area_codes.length;h++){
					area_html_right+="<li id='"+area_codes[h]+"'>\
					<div class='checkbox1'><input type='checkbox' value='"+area_codes[h]+"'  data-storename='"+area_names[h]+"' name='test' class='check'>\
					<label></div><span class='p16'>"+area_names[h]+"</span>\
					\</li>"
				}
				$("#screen_area .s_pitch span").html(h);
				$("#screen_area .screen_content_r ul").html(area_html_right);
			}else{
				$("#screen_area .s_pitch span").html("0");
				$("#screen_area .screen_content_r ul").empty();
			}
			self.param.isscroll=false;
			self.param.area_num=1;
			$("#screen_area .screen_content_l").unbind("scroll");
			$("#screen_area .screen_content_l ul").empty();
			$("#screen_area").show();
			$("#screen_shop").hide();
			self.getAreaList(self.param.area_num);
		})
		//店铺里面的品牌点击
		$("#shop_brand").click(function(){
			$(".input_search input").val("");
			if(self.param.brand_codes!==""){
				var brand_codes=self.param.brand_codes.split(',');
				var brand_names=self.param.brand_names.split(',');
				var brand_html_right="";
			    for(var h=0;h<brand_codes.length;h++){
					brand_html_right+="<li id='"+brand_codes[h]+"'>\
					<div class='checkbox1'><input type='checkbox' value='"+brand_codes[h]+"'  data-storename='"+brand_names[h]+"' name='test' class='check'>\
					<label></div><span class='p16'>"+brand_names[h]+"</span>\
					\</li>"
				}
				$("#screen_brand .s_pitch span").html(h);
				$("#screen_brand .screen_content_r ul").html(brand_html_right);
			}else{
				$("#screen_brand .s_pitch span").html("0");
				$("#screen_brand .screen_content_r ul").empty();
			}
			$("#screen_brand .screen_content_l ul").empty();
			$("#screen_brand").show();
			$("#screen_shop").hide();
			self.getBrandList();
		})
		//点击列表显示选中状态
		$(".screen_content").on("click", "li", function () {
		    var input = $(this).find("input")[0];
		    if (input.type == "checkbox" && input.checked == false) {
		        input.checked = true;
		    } else if (input.type == "checkbox" && input.checked == true) {
		        input.checked = false;
		    }
		});
		//搜索
		$("#area_search").keydown(function(){
		    var event=window.event||arguments[0];
		    self.param.area_num=1;
		    if(event.keyCode == 13){
		    	self.param.isscroll=false;
			    $("#screen_area .screen_content_l").unbind("scroll");
		    	$("#screen_area .screen_content_l ul").empty();
		        self.getAreaList(self.param.area_num);
		    }
		});
		//区域放大镜收索
		$("#area_search_f").click(function(){
			self.param.area_num=1;
			self.param.isscroll=false;
			$("#screen_area .screen_content_l").unbind("scroll");
		    $("#screen_area .screen_content_l ul").empty();
		    self.getAreaList(self.param.area_num);
		})
		//店铺搜索
		$("#store_search").keydown(function(){
			var event=window.event||arguments[0];
			self.param.shop_num=1;
			if(event.keyCode==13){
				self.param.isscroll=false;
				$("#screen_shop .screen_content_l ul").unbind("scroll");
				$("#screen_shop .screen_content_l ul").empty();
				self.getStoreList(self.param.shop_num);
			}
		})
		//店铺放大镜搜索
		$("#store_search_f").click(function(){
			self.param.shop_num=1;
			self.param.isscroll=false;
			$("#screen_shop .screen_content_l").unbind("scroll");
			$("#screen_shop .screen_content_l ul").empty();
			self.getStoreList(self.param.shop_num);
		})
		//品牌搜索
		$("#brand_search").keydown(function(){
			var event=window.event||arguments[0];
			if(event.keyCode==13){
				$("#screen_brand .screen_content_l ul").empty();
				self.getBrandList();
			}
		})
		//品牌放大镜收索
		$("#brand_search_f").click(function(){
			$("#screen_brand .screen_content_l ul").empty();
			self.getBrandList();
		})
		//移到右边
		function removeRight(a,b){
			var li="";
			if(a=="only"){
				li=$(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']:checked").parents("li");
			}
			if(a=="all"){
				li=$(b).parents(".screen_content").find(".screen_content_l input[type='checkbox']").parents("li");
			}
			if(li.length=="0"){
				art.dialog({
					zIndex:10003,
					time: 1,
					lock: true,
					cancel: false,
					content: "请先选择"
				});
				return;
			}
			if(li.length>0){
				for(var i=0;i<li.length;i++){
					var html=$(li[i]).html();
					var id=$(li[i]).find("input[type='checkbox']").val();
					$(li[i]).find("input[type='checkbox']")[0].checked=true;
					var input=$(b).parents(".screen_content").find(".screen_content_r li");
					for(var j=0;j<input.length;j++){
						if($(input[j]).attr("id")==id){
							$(input[j]).remove();
						}
					}
					$(b).parents(".screen_content").find(".screen_content_r ul").prepend("<li id='"+id+"'>"+html+"</li>");
					$(b).parents(".screen_content").find(".screen_content_r input[value='"+id+"']").removeAttr("checked");
				}
			}
			var num=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
			$(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
		}
		//移到左边
		function removeLeft(a,b){
			var li="";
			if(a=="only"){
				li=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']:checked").parents("li");
			}
			if(a=="all"){
				li=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li");
			}
			if(li.length=="0"){
				art.dialog({
					zIndex:10003,
					time: 1,
					lock: true,
					cancel: false,
					content: "请先选择"
				});
				return;
			}
			if(li.length>0){
				for(var i=li.length-1;i>=0;i--){
					$(li[i]).remove();
					$(b).parents(".screen_content").find(".screen_content_l input[value='"+$(li[i]).attr("id")+"']").removeAttr("checked");
				}
			}
			var num=$(b).parents(".screen_content").find(".screen_content_r input[type='checkbox']").parents("li").length;
			$(b).parents(".screen_content").siblings(".input_s").find(".s_pitch span").html(num);
		}
		//点击右移
		$(".shift_right").click(function(){
			var right="only";
			var div=$(this);
			removeRight(right,div);
		})
		//点击右移全部
		$(".shift_right_all").click(function(){
			var right="all";
			var div=$(this);
			removeRight(right,div);
		})
		//点击左移
		$(".shift_left").click(function(){
			var left="only";
			var div=$(this);
			removeLeft(left,div);
		})
		//点击左移全部
		$(".shift_left_all").click(function(){
			var left="all";
			var div=$(this);
			removeLeft(left,div);
		})
		$("#is_present_point").change(function(){
			if($(this)[0].checked==true){
				$("#present_point").removeAttr("readonly");
			}else if($(this)[0].checked==false){
				$("#present_point").attr("readonly","true");
			}
		})
		$("#rating_is_present_point").change(function(){
			if($(this)[0].checked==true){
				$("#rating_present_point").removeAttr("readonly");
			}else if($(this)[0].checked==false){
				$("#rating_present_point").attr("readonly","true");
			}
		})
	};
	regimejs.getQuanList=function(corp_code,points_value,div){//获取券的list
		var param={};
		param["corp_code"]=corp_code;
		param["app_id"]=$('#vipCode').attr('data-app_id');
		var appid = $('#vipCode').attr('data-app_id');
		var li="";
		oc.postRequire("post","/vipRules/getCoupon","",param, function(data){
			if(data.code=="0"){
				var list=JSON.parse(data.message);
				if(list.length=="0"){
					li+="<li data-couponcode=''>暂无优惠券</li>";
				}else{
					for(var i=0;i<list.length;i++){
						li+="<li data-couponcode='"+list[i].couponcode+"' title='"+list[i].name+"' data-name='"+list[i].name+"'>"+list[i].name+"</li>";
					}
				}
				if(div!==undefined){
					$(div).find(".coupon_list").html("");
					if(points_value!=""){
						for(var a=0;a<points_value.length;a++){
							var html='<span class="coupon_line" style=" position: relative;display: inline-block">'
								+'<input class="input_select w" data-couponcode="'+points_value[a].coupon_code+'" data-name="'+points_value[a].coupon_name+'" placeholder="请选择优惠券" value="'+points_value[a].coupon_name+'">'
								//+'<span class="icon-ishop_8-02" ></span>'
								+'<ul>';
							html+=li;
							html+='</ul>'
								+'<span class="icon-ishop_6-01" style="display: none" ></span>'
								+'<span class="icon-ishop_6-02" style="display: inline-block;"></span>'
								+'</span>';
							$(div).find(".coupon_list").append(html);
						}
						if($(div).find(".coupon_list .coupon_line").length==1){
							$(div).find(".coupon_list .coupon_line .icon-ishop_6-01").css("display","inline-block")
							$(div).find(".coupon_list .coupon_line .icon-ishop_6-02").hide()
						}
					}else{
						var html='<span class="coupon_line" style=" position: relative;display: inline-block">'
							+'<input class="input_select w" data-value="" value="" type="text" data-couponcode="" placeholder="请选择优惠券">'
							//+'<span class="icon-ishop_8-02" style=""></span>'
							+'<ul>';
						html+=li;
						html+='</ul>'
							+'<span class="icon-ishop_6-01" ></span>'
							+'<span class="icon-ishop_6-02" ></span>'
							+'</span>';
						$(div).find(".coupon_list").html(html);
					}
				}else{
					var html='<span class="coupon_line" style=" position: relative;display: inline-block">'
								+'<input class="input_select w" data-value="" value="" type="text" data-couponcode="" placeholder="请选择优惠券">'
								//+'<span class="icon-ishop_8-02" style=""></span>'
								+'<ul>';
						html+=li;
						html+='</ul>'
							 +'<span class="icon-ishop_6-01" ></span>'
							 +'<span class="icon-ishop_6-02" ></span>'
							 +'</span>';
					$(".coupon_list").html(html);
				}
				if(list.length=="0"){
					$(".quan_select ul").css({height:"24px",overflow:"hidden"})
				}
			}else if(data.code=="-1"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: data.message
				});
			}
		})
	};
	regimejs.getInputValue=function(id){//编辑时给input赋值
		var param={};
		var self=this;
		param["id"]=id;
		oc.postRequire("post","/vipRules/select","",param,function(data){
			if(data.code=="0"){
				var message=JSON.parse(data.message);
				var corp_code=message.corp_code;
				var app_id = message.app_id;
				var keep_grade_condition=message.keep_grade_condition==""?"":JSON.parse(message.keep_grade_condition);
				regimejs.param["edit_first_high"]=true;
				regimejs.param["edit_first_down"]=true;
				$("#vipCode").attr("data-app_id",app_id);
				$("#vipCode").val(message.app_name);
				$("#discount").val(message.discount);//会员折扣
				$("#effective_time").val(message.valid_date);
				$("#join_threshold").val(message.join_threshold);//招募门槛
				$("#upgrade_amount").val(message.upgrade_amount);//升级门槛金额
				$("#points_value").val(message.points_value);//积分比例
				$("#created_time").val(message.created_date);//创建时间
				$("#creator").val(message.creater);//创建人
				$("#modify_time").val(message.modified_date);//修改人
				$("#modifier").val(message.modifier);//修改时间
				$("#upgrade_time").attr("data-value",message.upgrade_time);//升级门槛时间
				$("#reserved_rating").attr("data-value",keep_grade_condition==""?"":keep_grade_condition.time);  //保留等级条件时间
				$("#amount_start").val(keep_grade_condition==""?"":keep_grade_condition.price["start"]);  //保留等级条件 价格区间
				//$("#amount_end").val(keep_grade_condition==""?"":keep_grade_condition.price["end"]);  //保留等级条件 价格区间
				var input=$("#is_active")[0];//是否可用
				var is_present_point=$("#is_present_point")[0];//是否赠送积分
				var is_present_coupon=$("#is_present_coupon")[0];//是否赠送券
				var rating_is_present_point=$("#rating_is_present_point")[0];// 保留等级下的  是否赠送积分
				var rating_is_present_coupon=$("#rating_is_present_coupon")[0];//保留登记下的  是否赠送券
				var is_shop=$("#is_shop")[0];//是否选择开卡店铺
				var store_codes="";
				var store_names="";
				for(var i=0;i<message.stores.length;i++){
					if(i<message.stores.length-1){
						store_codes+=message.stores[i].store_code+",";
						store_names+=message.stores[i].store_name+","
					}else{
						store_codes+=message.stores[i].store_code;
						store_names+=message.stores[i].store_name;
					}
				}
				//$("#shop_list").val("已选"+message.stores.length+"个");
				$("#shop_list").html(message.stores.length);
				self.param.store_codes=store_codes;
				self.param.store_names=store_names;
				if(message.upgrade_time!==""){
					var upgrade_time=$("#upgrade_time").siblings("ul").find("li[data-value='"+message.upgrade_time+"']").text();
					$("#upgrade_time").val(upgrade_time);//升级门槛时间
				}
				if(keep_grade_condition.time!==""){
					var reserved_rating=$("#upgrade_time").siblings("ul").find("li[data-value='"+keep_grade_condition.time+"']").text();
					$("#reserved_rating").val(reserved_rating);//升级门槛时间
				}
				//是否可用
				if(message.isactive=="Y"){
					input.checked=true;
				}else if(message.isactive=="N"){
					input.checked=false;
				}
				if(message.stores.length=="0"){
					is_shop.checked=false;
				}else if(message.stores.length>0){
					is_shop.checked=true;
				}
				//是否赠送积分
				if(message.present_point!==""){
					is_present_point.checked=true;
					$("#present_point").val(message.present_point);//送积分
				}else if(message.present_point==""){
					is_present_point.checked=false;
					$("#recruitment_threshold .coupon_list").prev().hide();
				}
				// 保留等级下是否赠送积分
				if(message.keep_present_point!==""){
					rating_is_present_point.checked=true;
					$("#rating_present_point").val(message.keep_present_point);//送积分
				}else if(message.keep_present_point==""){
					rating_is_present_point.checked=false;
					$("#rating .coupon_list").prev().hide();
				}

				//是否赠送券
				if(message.present_coupon!==""){
					var present_coupon=JSON.parse(message.present_coupon);//券的list
					if(present_coupon.length>0){
						is_present_coupon.checked=true;
					}else{
						is_present_coupon.checked=false;
					}
					$("#add_quan").addClass("active");
					$("#rating_add_quan").removeClass("active");
					self.getQuanList(corp_code,present_coupon,"#recruitment_threshold");
				}else if(message.present_coupon==""){
					is_present_coupon.checked=false;
					$("#recruitment_threshold .coupon_list").hide();
					self.getQuanList(corp_code,message.present_coupon,"#recruitment_threshold");
				}
				//保留登记下 是否赠送券
				if(message.keep_present_coupon!=""){
					var keep_present_coupon=JSON.parse(message.keep_present_coupon);//券的list
					if(keep_present_coupon.length>0){
						rating_is_present_coupon.checked=true;
					}else{
						rating_is_present_coupon.checked=false;
					}
					$("#rating_add_quan").addClass("active");
					$("#add_quan").removeClass("active");
					self.getQuanList(corp_code,keep_present_coupon,"#rating");
				}else if(message.keep_present_coupon==""){
					rating_is_present_coupon.checked=false;
					$("#rating .coupon_list").hide();
					self.getQuanList(corp_code,message.keep_present_coupon,"#rating");
				}
				self.vip_type_code=message.vip_card_type_code+"&"+message.degree;
				self.vip_type_high_code=message.high_vip_card_type_code+"&"+message.high_degree;
				self.vip_type_down_code=message.degrade_vip_code+"&"+message.degrade_degree;
				//$("#vip_type").val(message.vip_type);//会员类型
				//$("#vip_type").attr("data-value",message.vip_card_type_code);
				//$("#vip_type").attr("data-degree",message.degree);
				//if(message.high_vip_type==""){
				//	$("#high_vip_type").val("");
				//}else{
				//	$("#high_vip_type").val(message.high_vip_type);//上级会员类型
				//	$("#high_vip_type").attr("data-value",message.high_vip_card_type_code);
				//	$("#high_vip_type").attr("data-degree",message.high_degree);
				//}
				//if(message.degrade_vip_name==""){
				//	$("#down_vip_type").val("");
				//}else{
				//	$("#down_vip_type").val(message.degrade_vip_name);//上级会员类型
				//	$("#down_vip_type").attr("data-value",message.degrade_vip_code);
				//	$("#down_vip_type").attr("data-degree",message.degrade_degree);
				//}
				self.getcorplist(corp_code,message.degree);
			}else if(data.code=="-1"){
				alert(data.message);
			}
		})
	};
	regimejs.getcorplist=function(corp_code,degree){//获取企业列表
		var self=this;
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
				$('#OWN_CORP').searchableSelect();
				var code=$("#OWN_CORP").val();
				self.getVipCardTypes(code,degree);
				$('#corp_select .searchable-select-item').each(function(){
					if($(this).text() == $('.searchable-select-holder').text()){
						corp_code = $(this).attr('data-value');
					}
				});
				getWxList(corp_code);
				$('.corp_select .searchable-select-input').keydown(function(event){
					var event=window.event||arguments[0];
					var val = $('#corp_select .searchable-select-holder').text();
					if(event.keyCode == 13){
						var corp_code1=$("#OWN_CORP").val();
						if(code!==corp_code1){
							self.param.area_codes="";
    						self.param.area_names="";
    						self.param.brand_codes="";
    						self.param.brand_names="";
    						self.param.store_codes="";
    						self.param.store_names="";
    						self.param.vip_type="";
    						code=corp_code1;
    						//$("#shop_list").val("已选0个");
    						$("#shop_list").html("0");
    						$("#vip_type").val("");
							$("#high_vip_type").val("");
							$("#high_vip_type").attr("data-value","");
							$("#high_vip_type").attr("data-degree","");
							$("#down_vip_type").val("");
							$("#down_vip_type").attr("data-value","");
							$("#down_vip_type").attr("data-degree","");
							$("#vipCode").val("请选择公众号");
							$("#vipCode").attr("data-app_id","");
    						$(".coupon_list").empty();
    						self.getVipCardTypes(code);
							self.vip_type_code="";
							self.vip_type_high_code="";
							whir.loading.add("","0.5");
						}
						$('.searchable-select-item').each(function () {
							if($(this).text()==val){
								var corp_code =  $(this).attr('data-value');
								getWxList(corp_code);
								$('#quan_select .q_remove').each(function (){
									$(this).click();
								});
								$(".coupon_list").empty();
								regimejs.getQuanList(corp_code);
							}
						});
					}
				});
				$('.searchable-select-item').click(function(){
					var corp_code1=$("#OWN_CORP").val();
					if(code!==corp_code1){
						self.param.area_codes="";
    					self.param.area_names="";
    					self.param.brand_codes="";
    					self.param.brand_names="";
    					self.param.store_codes="";
    					self.param.store_names="";
    					self.param.vip_type="";
    					code=corp_code1;
    					//$("#shop_list").val("已选0个");
    					$("#shop_list").html("0");
    					$("#vip_type").val("");
    					$("#high_vip_type").val("");
    					$("#high_vip_type").attr("data-value","");
    					$("#high_vip_type").attr("data-degree","");
						$("#down_vip_type").val("");
    					$("#down_vip_type").attr("data-value","");
    					$("#down_vip_type").attr("data-degree","");
						$("#vipCode").val("请选择公众号");
						$("#vipCode").attr("data-app_id","");
						$("#rating_quan_select").empty();
						whir.loading.add("","0.5");
						self.getVipCardTypes(code);
						self.vip_type_code="";
						self.vip_type_high_code="";
					}
					var corp_code = $(this).attr('data-value');
					getWxList(corp_code);
					$('#quan_select .q_remove').each(function (){
						$(this).click();
					});
					$(".coupon_list").empty();
					regimejs.getQuanList(corp_code);
				})
			}else if(data.code=="-1"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: data.message
				});
			}
		});
	};
	regimejs.getVipCardTypes=function(corp_code,degree){
		var self=this;
		var param={};
		var degree=degree;
		param["corp_code"]=corp_code;
		$('#VIP_TYPE').empty();
		$('#vip_type_select .searchable-select').remove();
		oc.postRequire("post","/vipCardType/getVipCardTypes","", param, function(data) {
			if(data.code=="0"){
				var list=JSON.parse(data.message).list;
				var list=JSON.parse(list);
				var html="";
				if(list.length>0){
					for(var i=0;i<list.length;i++){
						html+="<option data-name='"+list[i].vip_card_type_name+"' value='"+list[i].vip_card_type_code+"&"+list[i].degree+"'>"+list[i].vip_card_type_name+"("+list[i].vip_card_type_code+")</option>"
					}
					//if(degree==undefined){
					//	degree=list[0].degree;
					//	$("#vip_type").val(list[0].vip_card_type_name);
					//	$("#vip_type").attr("data-value",list[0].vip_card_type_code);
					//	$("#vip_type").attr("data-degree",list[0].degree);
					//}
				}else{
					html+="<option value=''>无会员类型</option>";
					$("#recruitment_threshold .tipContent").html("用于会员类型招募")
				}
				$("#VIP_TYPE").append(html);
				if(self.vip_type_code!==""){
					$("#VIP_TYPE option[value='"+self.vip_type_code+"']").attr("selected","true");
					var firstVal=$("#VIP_TYPE option:selected").html();
					$("#recruitment_threshold .tipContent").html("用于"+firstVal+"招募")
				}
				$('#VIP_TYPE').searchableSelect();
				var vip_type_code=$("#VIP_TYPE").val();
				if(degree==undefined && list.length>0){
					degree=list[0].degree;
					var firstVal=$("#VIP_TYPE option:selected").html();
					$("#recruitment_threshold .tipContent").html("用于"+firstVal+"招募")
				}else if(degree==undefined &&list.length==0){
					degree="";
				}
				self.getHighVipCardTypes(corp_code,degree,"high");
				self.getHighVipCardTypes(corp_code,degree,"down");
				$('#vip_type_select .searchable-select-input').keydown(function(event){
					var event=window.event||arguments[0];
					var val = $('#vip_type_select .searchable-select-holder').text();
					if(event.keyCode == 13){
						var vip_type_code1=$("#VIP_TYPE").val();
						if(vip_type_code!=vip_type_code1){
							$('#vip_type_select .searchable-select-item').each(function () {
								if($(this).text()==val){
									$("#recruitment_threshold .tipContent").html("用于"+val+"招募");
									$("#upgrade .tipContent").html("用于会员类型升级为上级会员类型");
									var degree =  $(this).attr('data-value').split("&")[1];
									whir.loading.add("","0.5");
									self.getHighVipCardTypes(corp_code,degree,"high");
									self.getHighVipCardTypes(corp_code,degree,"down");
									regimejs.vip_type_high_code="";
									vip_type_code=vip_type_code1
								}
							});
						}
					}
				});
				$('#vip_type_select .searchable-select-item').click(function(){
					var vip_type_code1=$("#VIP_TYPE").val();
					var val = $('#vip_type_select .searchable-select-holder').text();
					if(vip_type_code!=vip_type_code1){
						$("#recruitment_threshold .tipContent").html("用于"+val+"招募");
						$("#upgrade .tipContent").html("用于会员类型升级为上级会员类型");
						var degree=$(this).attr("data-value").split("&")[1];
						whir.loading.add("","0.5");
						self.getHighVipCardTypes(corp_code,degree,"high");
						self.getHighVipCardTypes(corp_code,degree,"down");
						regimejs.vip_type_high_code="";
						vip_type_code=vip_type_code1
					}
				})
			}else if(data.code=="-1"){
				art.dialog({
					time: 1,
					lock: true,
					cancel: false,
					content: data.message
				});
			}
		})
	};
	regimejs.getHighVipCardTypes=function(corp_code,degree,type){//获取vip上级会员类型
		var param={};
		param["corp_code"]=corp_code;
		param["degree"]=degree;
		param["type"]=type;
		oc.postRequire("post","/vipCardType/getHighVipCardTypes","",param, function(data){
			var list=JSON.parse(data.message).list;
			var list=JSON.parse(list);
			//var html=type=="high"?"<li data-value=''>无上级会员类型</li>":"<li data-value=''>无降级会员类型</li>";
			var html="";
			if(list.length>0){
				html="<option value=''>无</option>";
				//if(type=="high"){
				//	html="<option value=''>无</option>"
				//}else if(type="down"){
				//	html=""
				//}
				for(var i=0;i<list.length;i++){
					html+="<option data-name='"+list[i].vip_card_type_name+"' value='"+list[i].vip_card_type_code+"&"+list[i].degree+"'>"+list[i].vip_card_type_name+"("+list[i].vip_card_type_code+")</option>"
				}
			}else{
				if(type=="high"){
					html+="<option value='' selected>无上级会员类型</option>";
				}else if(type="down"){
					html+="<option value='' selected >无降级会员类型</option>"
				}
			}
			var id=sessionStorage.getItem("id");
			if(type=="high"){
				$('#VIP_TYPE_HIGH').empty();
				$('#vip_type_high_select .searchable-select').remove();
				$("#VIP_TYPE_HIGH").append(html);
				var vip_type_high_code=$("#VIP_TYPE_HIGH").val();
				var vipType=$("#VIP_TYPE option:selected").html();
				var VIP_TYP=$("#VIP_TYPE").val();
				if(VIP_TYP!=""){
					$("#upgrade .tipContent").html("用于"+vipType+"升级为上级会员类型");
				}else{
					$("#upgrade .tipContent").html("用于会员类型升级为上级会员类型")
				}
				if(regimejs.vip_type_high_code!==""&& regimejs.vip_type_high_code!="&"){
					$("#VIP_TYPE_HIGH option[value='"+regimejs.vip_type_high_code+"']").attr("selected","true");
					var vipTypeHigh=$("#VIP_TYPE_HIGH option:selected").html();
					$("#upgrade .tipContent").html("用于"+vipType+"升级为"+vipTypeHigh)
				}
				$("#VIP_TYPE_HIGH").searchableSelect();
			}else {
				$('#VIP_TYPE_DOWN').empty();
				$('#vip_type_down_select .searchable-select').remove();
				$("#VIP_TYPE_DOWN").append(html);
				if(regimejs.vip_type_down_code!=="" && regimejs.vip_type_down_code!="&"){
					$("#VIP_TYPE_DOWN option[value='"+regimejs.vip_type_down_code+"']").attr("selected","true");
				}
				$("#VIP_TYPE_DOWN").searchableSelect();
				whir.loading.remove();
			}
			$('#vip_type_high_select .searchable-select-input').keydown(function(event){
				var event=window.event||arguments[0];
				var val = $('#vip_type_high_select .searchable-select-holder').text();
				if(event.keyCode == 13){
					var vip_type_high_code1=$("#VIP_TYPE_HIGH").val();
					var vipType=$("#VIP_TYPE option:selected").html();
					var vipTypeHigh=$("#VIP_TYPE_HIGH option:selected").html();
					if(vip_type_high_code!=vip_type_high_code1){
						$('#vip_type_high_select .searchable-select-item').each(function () {
							if($(this).text()==val){
								$("#upgrade .tipContent").html("用于"+vipType+"升级为"+vipTypeHigh);
							}
						});
					}
				}
			});
			$('#vip_type_high_select .searchable-select-item').click(function(){
				var vip_type_high_code1=$("#VIP_TYPE_HIGH").val();
				var vipType=$("#VIP_TYPE option:selected").html();
				var vipTypeHigh=$("#VIP_TYPE_HIGH option:selected").html();
				if(vip_type_high_code!=vip_type_high_code1){
					$("#upgrade .tipContent").html("用于"+vipType+"升级为"+vipTypeHigh);
				}
			})
		})
	};
	regimejs.getStoreList=function(pageNumber){
		var self=this;
		var corp_code=$("#OWN_CORP").val();
		var corp_code = $('#OWN_CORP').val();
		var searchValue=$("#store_search").val();
		var pageSize=20;
		var pageNumber=pageNumber;
		var param={};
		param['corp_code']=corp_code;
		param['area_code']=self.param.area_codes;
		param['brand_code']=self.param.brand_codes;
		param['searchValue']=searchValue;
		param['pageNumber']=pageNumber;
		param['pageSize']=pageSize;
		$("#mask").css("z-index","10002");
		oc.postRequire("post","/shop/selectByAreaCode","", param, function(data) {
		if (data.code == "0") {
				var message=JSON.parse(data.message);
	            var list=JSON.parse(message.list);
	            var hasNextPage=list.hasNextPage;
	            var cout=list.pages;
	            var list=list.list;
				var store_html = '';
				if (list.length == 0){
					
				} else {
					if(list.length>0){
						for (var i = 0; i < list.length; i++) {
					    store_html+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].store_code+"' data-storename='"+list[i].store_name+"' name='test'  class='check'  id='checkboxTowInput"
	                        + i
	                        + pageNumber
	                        + 1
	                        + "'/><label for='checkboxTowInput"
	                        + i
	                        + pageNumber
	                        + 1
	                        + "'></label></div><span class='p16'>"+list[i].store_name+"</span></li>"
						}
					}
				}
				if(hasNextPage==true){
					self.param.shop_num++;
					self.param.shop_next=false;
				}
				if(hasNextPage==false){
					self.param.shop_next=true;
				}
				$("#screen_shop .screen_content_l ul").append(store_html);
				if(!self.param.isscroll){
					$("#screen_shop .screen_content_l").bind("scroll",function () {
						var nScrollHight = $(this)[0].scrollHeight;
					    var nScrollTop = $(this)[0].scrollTop;
					    var nDivHight=$(this).height();
					    if(nScrollTop + nDivHight >= nScrollHight){
					    	if(self.param.shop_next){
					    		return;
					    	}
					    	self.getStoreList(self.param.shop_num);
					    };
					})
			    }
			    self.param.isscroll=true;
				var li=$("#screen_shop .screen_content_r input[type='checkbox']").parents("li");
				for(var k=0;k<li.length;k++){
					$("#screen_shop .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
				}
			} else if (data.code == "-1") {
				art.dialog({
					time: 1,
					lock: true,
					cancel: false,
					content: data.message
				});
			}
		})
	};
	regimejs.getBrandList=function(){//获取品牌列表
		var corp_code = $('#OWN_CORP').val();
		var searchValue=$("#brand_search").val();
		var param={};
		param["corp_code"]=corp_code;
		param["searchValue"]=searchValue;
		$("#mask").css("z-index","10002");
		oc.postRequire("post","/shop/brand", "",param, function(data){
			if (data.code == "0") {
				var message=JSON.parse(data.message);
	            var list=message.brands;
				var brand_html_left = '';
				var brand_html_right='';
				if (list.length == 0){
				} else {
					if(list.length>0){
						for (var i = 0; i < list.length; i++) {
						    brand_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].brand_code+"' data-areaname='"+list[i].brand_name+"' name='test'  class='check'  id='checkboxThreeInput"
		                        + i
		                        + 1
		                        + "'/><label for='checkboxThreeInput"
		                        + i
		                        + 1
		                        + "'></label></div><span class='p16'>"+list[i].brand_name+"</span></li>"
						}
					}
				}
				$("#screen_brand .screen_content_l ul").append(brand_html_left);
				var li=$("#screen_brand .screen_content_r input[type='checkbox']").parents("li");
				for(var k=0;k<li.length;k++){
					$("#screen_brand .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
				}
			} else if (data.code == "-1") {
				art.dialog({
					time: 1,
					lock: true,
					cancel: false,
					content: data.message
				});
			}
		})
	};
	regimejs.getAreaList=function(pageNumber){
		var self=this;
		var corp_code = $('#OWN_CORP').val();
		var searchValue=$("#area_search").val().trim();
		var pageSize=20;
		var pageNumber=pageNumber;
		var param={};
		param["corp_code"] = corp_code;
		param["searchValue"]=searchValue;
		param["pageSize"]=pageSize;
		param["pageNumber"]=pageNumber;
		$("#mask").css("z-index","10002");
		oc.postRequire("post", "/area/selAreaByCorpCode", "",param, function(data) {
			if (data.code == "0") {
				var message=JSON.parse(data.message);
	            var list=JSON.parse(message.list);
	            var hasNextPage=list.hasNextPage;
	            var cout=list.pages;
	            var list=list.list;
				var area_html_left ='';
				var area_html_right='';
				if (list.length == 0) {

				} else {
					if(list.length>0){
						for (var i = 0; i < list.length; i++) {
						    area_html_left+="<li><div class='checkbox1'><input  type='checkbox' value='"+list[i].area_code+"' data-areaname='"+list[i].area_name+"' name='test'  class='check'  id='checkboxOneInput"
		                        + i
		                        + pageNumber
		                        + 1
		                        + "'/><label for='checkboxOneInput"
		                        + i
		                        + pageNumber
		                        + 1
		                        + "'></label></div><span class='p16'>"+list[i].area_name+"</span></li>"
						}
					}
				}
				if(hasNextPage==true){
					self.param.area_num++;
					self.param.area_next=false;
				}
				if(hasNextPage==false){
					self.param.area_next=true;
				}
				$("#screen_area .screen_content_l ul").append(area_html_left);
				if(!self.param.isscroll){
					$("#screen_area .screen_content_l").bind("scroll",function () {
						var nScrollHight = $(this)[0].scrollHeight;
					    var nScrollTop = $(this)[0].scrollTop;
					    var nDivHight=$(this).height();
					    if(nScrollTop + nDivHight >= nScrollHight){
					    	if(self.param.area_next){
					    		return;
					    	}
					    	self.getAreaList(self.param.area_num);
					    };
					})
				}
				self.param.isscroll=true;
				var li=$("#screen_area .screen_content_r input[type='checkbox']").parents("li");
				for(var k=0;k<li.length;k++){
					$("#screen_area .screen_content_l input[value='"+$(li[k]).attr("id")+"']").attr("checked","true"); 
				}
			} else if (data.code == "-1") {
				art.dialog({
					time: 1,
					lock: true,
					cancel: false,
					content: data.message
				});
			}
		})
	};
	regimejs.ajaxSubmit=function(command,param){//提交接口
		oc.postRequire("post", command,"",param, function(data){
			if(data.code=="0"){
				if(command=="/vipRules/add"){
                    sessionStorage.setItem("id",data.message);
                    $(window.parent.document).find('#iframepage').attr("src","/vip/vip_regime_edit.html");
                }
                if(command=="/vipRules/edit"){
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
		if(regimejs['check' + command]){
			if(!regimejs['check' + command].apply(regimejs,[obj,hint])){
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
	//获取公众号
	function getWxList(corp_code){
		var param={};
		param["corp_code"]=corp_code;
		oc.postRequire("post","/corp/selectWx","0",param, function(data){
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				var list = msg.list;
				var tempHTML = ' <li data-id="${id}" data-username="${usermane}" data-value="${msg}">${msg}</li>';
				var html = '';
				for(i=0;i<list.length;i++){
					var nowHTML = tempHTML;
					nowHTML = nowHTML.replace('${id}',list[i].app_id);
					nowHTML = nowHTML.replace('${usermane}',list[i].app_user_name);
					nowHTML = nowHTML.replace('${msg}',list[i].app_name);
					nowHTML = nowHTML.replace('${msg}',list[i].app_name);
					html+=nowHTML;
				}
				$('#vipPublic').html(html);
				cache.vipCode = $('#vipCode').val();
				$('#vipPublic li').click(function () {
					$('#vipCode').val($(this).text());
					$('#vipCode').attr('data-app_id',$(this).attr('data-id'));
					$('#vipCode').attr('app_user_name',$(this).attr('data-username'));
					if(cache.vipCode == $(this).text()){
						console.log('公众号名称未改变!')
					}else{
						$('#quan_select .q_remove').each(function (){
							$(this).click();
						});
						cache.vipCode = $(this).text();
						$(".coupon_list").empty();
						regimejs.getQuanList(corp_code);
					}
				});

			}else if(data.code=="-1"){
				art.dialog({
					time: 1,
					lock:true,
					cancel: false,
					content: data.message
				});
			}
		});
	}
	var init=function(){
		var id=sessionStorage.getItem("id");
		regimejs.bindbutton();
		if(id==null){
			var corp_code="";
			regimejs.getcorplist(corp_code);
		}else if(id!==null){
			regimejs.getInputValue(id);
		}
	};
	var obj = {};
	obj.regimejs = regimejs;
	obj.init = init;
	return obj;
}));
$(function(){
	window.regime.init();//初始化
	whir.loading.add("",0.5)
});
cache = {
	"vipCode" :""
};
$("#amount_start,#amount_end").blur(function () {
	var start=$("#amount_start").val().trim();
	var end=$("#amount_end").val().trim();
	if(start!="" && end!=""){
		if(Number(start)>Number(end)){
			art.dialog({
				time: 1,
				lock:true,
				cancel: false,
				content: "后面的值不能小于前面的值"
			});
			$(this).val("")
		}
	}
});
$("#discount").blur(function () {
	var val=$(this).val().trim();
	if(val!="" && !(/^1$|^0\.[0-9]+$/g.test(val))){
		art.dialog({
			time: 1,
			lock:true,
			cancel: false,
			content: "折扣范围为0~1"
		});
		$(this).val("")
	}
});

$(".inputNum").blur(function () {
	var reg = /^\d+$|^\d+\.\d+$/g;
	var val = $(this).val();
	if(!reg.test(val)&&val!==""){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content: "请输入数字"
		});
		$(this).val("");
	}
});
$(".give .tabs>label").click(function(){
	var index=$(this).index();
	var checkState=$(this).find("input").prop("checked");
	if(checkState){
		$(this).parent().next().children().eq(index).show();
	}else{
		$(this).parent().next().children().eq(index).hide();
	}
});
$(".coupon_list").on("click","input",function(){
	if($('#vipCode').val().trim() == '请选择公众号' ){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content:"请选择公众号"
		});
		return;
	}
	if($(this).nextAll("ul").children("li").length<1){
		art.dialog({
			time: 1,
			lock: true,
			cancel: false,
			content:"暂无优惠券"
		});
		return
	}
	var ul=$(this).nextAll("ul");
	ul.find("li").show();
	if(ul.css("display")=="none"){
		ul.show();
	}else{
		ul.hide();
	}
});
$(".coupon_list").on("click",".icon-ishop_6-01",function(){
	var html=$(this).parents(".coupon_line").clone();
	$(html).find(".icon-ishop_6-02").css("display","inline-block");
	$(html).find("input").eq(0).attr("data-couponcode","");
	$(html).find("input").eq(0).attr("data-name","");
	$(html).find("input").eq(0).val("");
	$(this).parents(".coupon_list").append(html);
	$(this).hide();
	if(!$(this).next().is(":visible")){
		$(this).next().css("display","inline-block");
	}
});
$(".coupon_list").on("click",".icon-ishop_6-02",function(){
	var len=$(this).parents(".coupon_list").children().length;
	if(len==2){
		$(this).parents(".coupon_list").children().find(".icon-ishop_6-02").hide();
	}
	if(len>1){
		$(this).parents(".coupon_line").remove();
	}
});
$("#reserved_rating").click(function(){
	var ul=$(this).parent().nextAll("ul");
	if(ul.css("display")=="none"){
		ul.show();
	}else{
		ul.hide();
	}
});
$("#reserved_rating").parent().nextAll("ul").children("li").click(function () {
	var val=$(this).attr("data-value");
	var txt=$(this).html();
	$("#reserved_rating").attr("data-value",val);
	$("#reserved_rating").val(txt);
	$(this).parent().hide()
});
$("#reserved_rating").blur(function(){
	var ul=$(this).parent().nextAll("ul");
	setTimeout(function(){
		ul.hide();
	},200);
});
$(".coupon_list").on("mousedown",".coupon_line ul li",function(){
	var val=$(this).attr("data-couponcode");
	var txt=$(this).html();
	$(this).parents(".coupon_line").find("input").attr("data-couponcode",val);
	$(this).parents(".coupon_line").find("input").attr("data-name",$(this).attr("data-name"));
	$(this).parents(".coupon_line").find("input").val(txt);
	$(this).parent().hide()
});
$(".coupon_list").on("blur",".input_select",function(){
	var ul=$(this).nextAll("ul");
    $(this).val().trim() != $(this).attr("data-name") ? $(this).val($(this).attr("data-name")) : "";
	setTimeout(function(){
		ul.hide();
	},200);
});
$(".coupon_list").on("keyup",".input_select",function(){
    var search = $(this).val().trim();
    $(this).next("ul").show();
    if(search != ''){
        $(this).next("ul").find("li").hide();
        $(this).next("ul").find("li:contains("+search+")").show();
    }else {
        $(this).next("ul").find("li").show();
    }
});
