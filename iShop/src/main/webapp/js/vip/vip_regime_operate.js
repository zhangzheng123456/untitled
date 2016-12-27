var oc = new ObjectControl();
(function(root,factory){
	root.regime= factory();
}(this,function(){
	var regimejs={};
	regimejs.param={//定义参数类型
		"price":"",
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
		"isscroll":false
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
		var isCode=/^[0-9]+([.]{1}[0-9]+){0,1}$/;
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
	regimejs.checkNumber1=function(obj,hint){//检查必须是数字还要非空的状态
		var isCode=/^[0-9]+([.]{1}[0-9]+){0,1}$/;
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
			return false;
		}
	}
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
				if(!self.param.price){
					return;
				}
				var param={};
				var corp_code=$("#OWN_CORP").val().trim();//公司编号
				var vip_type=$("#vip_type").val().trim();//会员类型
				var discount=$("#discount").val().trim();//会员折扣
				var join_threshold=$("#join_threshold").val().trim();//招募门槛
				var upgrade_time=$("#upgrade_time").attr("data-value");//升级门槛时间
				var upgrade_amount=$("#upgrade_amount").val().trim();//升级门槛金额
				var points_value=$("#points_value").val().trim();//积分比例
				var present_point=$("#present_point").val().trim();//送积分
				var present_coupon=[];//券
				var quanList=$("#quan_select .input_select");
				var isactive="";
				var input=$("#is_active")[0];//是否可用
				var is_present_point=$("#is_present_point")[0];//是否赠送积分
				var is_present_coupon=$("#is_present_coupon")[0];//是否赠送券
				var is_shop=$("#is_shop")[0];//是否选择店铺
				var high_vip_type="";
				if($("#high_vip_type").val().trim()=="无上级会员类型"){
					high_vip_type="";
				}else{
					high_vip_type=$("#high_vip_type").val().trim();
				}
				if(input.checked==true){//是否可用
					isactive="Y";
				}else if(input.checked==false){
					isactive="N";
				};
				for(var i=0;i<quanList.length;i++){//券的list
					var _param={};
					if($(quanList[i]).attr("data-appid")!==""){
						_param["appid"]=$(quanList[i]).attr("data-appid");
						_param["couponcode"]=$(quanList[i]).attr("data-couponcode");
						present_coupon.push(_param);
					}
				};
				if(is_present_point.checked==true){
					param["present_point"]=present_point;//送积分
				}else if(is_present_point.checked==false){
					param["present_point"]="";//送积分
				}
				if(is_present_coupon.checked==true){
					param["present_coupon"]=present_coupon;//送券
				}else if(is_present_coupon.checked==false){
					param["present_coupon"]="";//送券
				}
				if(is_shop.checked==true){
					param["store_code"]=self.param.store_codes;//店铺编号
				}else if(is_shop.checked==false){
					param["store_code"]="";//店铺编号
				}
				param["corp_code"]=corp_code;//公司编号
				param["vip_type"]=vip_type;//会员类型
				param["high_vip_type"]=high_vip_type;//上级会员类型
				param["discount"]=discount;//会员折扣
				param["join_threshold"]=join_threshold;//招募门槛
				param["upgrade_time"]=upgrade_time;//升级门槛时间
				param["upgrade_amount"]=upgrade_amount;//升级门槛金额
				param["points_value"]=points_value;//积分比例
				param["isactive"]=isactive;//是否可用
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
                    content:"请先选中左侧按钮"
                });
				return;
			}
			self.getQuanList(corp_code);
		})
		$("#present_point").click(function(){
			var is_present_point=$("#is_present_point")[0];
			if(is_present_point.checked==false){
				art.dialog({
                    time: 1,
                    lock: true,
                    cancel: false,
                    content:"请先选中左侧按钮"
                });
				return;
			}
		})
		//删除券
		$("#quan_select").on("click",".q_remove",function(){
			$(this).parent().remove();
		})
		//券点击li赋值
		$(".item_1").on("click","ul li",function(){
			var txt = $(this).text();
			$(".item_1 .input_select").val(txt);
			$(".item_1 ul").hide();
		});
		//券input框点击显示和隐藏
		$("#quan_select").on("click",".item_2 .input_select",function(){
			var ul = $(this).parent().find("ul");
			if(ul.css("display")=="none"){
				ul.show();
			}else{
				ul.hide();
			}
		});
		//点击li给input赋值
		$("#quan_select").on("click",".item_2 ul li",function(){
			var txt = $(this).text();
			var couponcode=$(this).attr("data-couponcode");
			var appid=$(this).attr("data-appid");
			$(this).parent().siblings('.input_select').val(txt);
			$(this).parent().siblings('.input_select').attr("data-couponcode",couponcode);
			$(this).parent().siblings('.input_select').attr("data-appid",appid);
			$(".item_2 ul").hide();
		});
		//券的input框失去焦点的时候隐藏
		$("#quan_select").on("blur",".item_2 .input_select",function(){
			var ul = $(this).parent().find("ul");
			setTimeout(function(){
				ul.hide();
			},200);
		});
		//获取vip类型
		$("#vip_type").blur(function(){
			var param={};
			param["corp_code"]=$("#OWN_CORP").val();//企业编号
			param["vip_type"]=$("#vip_type").val();//会员类型
			var div=$(this).next('.hint').children();
			oc.postRequire("post","/vipRules/vipTypeExist","",param, function(data){
				if(data.code=="0"){
					div.html("");
					self.param.price=true;
				}else if(data.code=="-1"){
					div.addClass("error_tips");
					div.html(data.message);
					self.param.price=false;
				}
			})
		});
		//点击input框显示出店铺列表
		$("#shop_list").click(function(){
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
                    content:"请先选中左侧按钮"
                });
				return;
			}
			whir.loading.add("",0.5);
			$("#loading").remove();
			$("#screen_shop").show();
			self.getStoreList(shop_num);
		});
		//点击x号关闭
		$(".screen_close").click(function(){
			$(this).parents(".screen_area").hide();
			whir.loading.remove();//移除加载框
		})
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
		    $("#shop_list").val("已选"+li.length+"个");
		   	whir.loading.remove();//移除加载框
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
	};
	regimejs.getQuanList=function(corp_code,points_value){//获取券的list
		var param={};
		param["corp_code"]=corp_code;
		var li="";
		whir.loading.add("",0.5);
		oc.postRequire("post","/vipRules/getCoupon","",param, function(data){
			var list=JSON.parse(data.message);
			for(var i=0;i<list.length;i++){
				li+="<li data-appid='"+list[i].appid+"' data-couponcode='"+list[i].couponcode+"'>"+list[i].name+"\("+list[i].appname+"\)</li>";
			}
			if(points_value!==undefined){
			for(var i=0;i<points_value.length;i++){
				var html="<div class='quan_select item_2'><input type='text' data-appid='"
				+points_value[i].appid+"' data-couponcode='"
				+points_value[i].couponcode+"'value='"
				+points_value[i].name+"\("+points_value[i].appname
				+"\)' class='input_select quan' class='present_coupon' maxlength='50'/><ul style='display:none'>"
				html+=li;
				html+="</ul><span class='icon-ishop_6-12 q_remove'></span></div>"
				$("#quan_select").append(html);
			}
			}else{
				var html="<div class='quan_select item_2'><input type='text' data-appid='' data-couponcode='' class='input_select quan' class='present_coupon' maxlength='50'/><ul style='display:none'>"
				html+=li;
				html+="</ul><span class='icon-ishop_6-12 q_remove'></span></div>"
				$("#quan_select").append(html);
			}
			whir.loading.remove();//移除加载框
		})
	};
	regimejs.getInputValue=function(id){//编辑时给input赋值
		var param={};
		var self=this;
		param["id"]=id;
		whir.loading.add("",0.5);
		oc.postRequire("post","/vipRules/select","",param,function(data){
			if(data.code=="0"){
				var message=JSON.parse(data.message);
				var corp_code=message.corp_code;
				$("#vip_type").val(message.vip_type);//会员类型
				$("#discount").val(message.discount);//会员折扣
				$("#join_threshold").val(message.join_threshold);//招募门槛
				$("#upgrade_amount").val(message.upgrade_amount);//升级门槛金额
				$("#points_value").val(message.points_value);//积分比例
				$("#present_point").val(message.present_point);//送积分
				$("#created_time").val(message.created_date);//创建时间
				$("#creator").val(message.creater);//创建人
				$("#modify_time").val(message.modified_date);//修改人
				$("#modifier").val(message.modifier);//修改时间
				$("#upgrade_time").attr("data-value",message.upgrade_time);//升级门槛时间
				var input=$("#is_active")[0];//是否可用
				var is_present_point=$("#is_present_point")[0];//是否赠送积分
				var is_present_coupon=$("#is_present_coupon")[0];//是否赠送券
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
				$("#shop_list").val("已选"+message.stores.length+"个");
				self.param.store_codes=store_codes;
				self.param.store_names=store_names;
				if(message.upgrade_time!==""){
					var upgrade_time=$("#upgrade_time").siblings("ul").find("li[data-value='"+message.upgrade_time+"']").text();
					$("#upgrade_time").val(upgrade_time);//升级门槛时间
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
				}else if(message.present_point==""){
					is_present_point.checked=false;
				}
				//是否赠送券
				if(message.present_coupon!==""){
					var present_coupon=JSON.parse(message.present_coupon);//券的list
					if(present_coupon.length>0){
						is_present_coupon.checked=true;
						$("#present_point").removeAttr("readonly");
					}else{
						is_present_coupon.checked=false;
					}
					self.getQuanList(corp_code,present_coupon);
				}else if(present_coupon==""){
					is_present_coupon=false;
				}
				if(message.high_vip_type==""){
					$("#high_vip_type").val("无上级会员类型");
				}else{
					$("#high_vip_type").val(message.high_vip_type);//上级会员类型
				}
				
				self.getcorplist(corp_code);
			}else if(data.code=="-1"){
				alert(data.message);
			}
			whir.loading.remove();//移除加载框
		})
	};
	regimejs.getcorplist=function(corp_code){//获取企业列表
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
				$('.corp_select select').searchableSelect();
				var code=$("#OWN_CORP").val();
				self.getVipType(code);
				$('.corp_select .searchable-select-input').keydown(function(event){
					var event=window.event||arguments[0];
					if(event.keyCode == 13){
						var corp_code1=$("#OWN_CORP").val();
						if(code!==corp_code1){
							self.param.area_codes="";
    						self.param.area_names="";
    						self.param.brand_codes="";
    						self.param.brand_names="";
    						self.param.store_codes="";
    						self.param.store_names="";
    						code=corp_code1;
    						$("#shop_list").val("已选0个");
    						$("#vip_type").val("");
    						$("#high_vip_type").val("");
    						$("#quan_select").empty();
    						self.getVipType(code);
						}
					}
				})
				$('.searchable-select-item').click(function(){
					var corp_code1=$("#OWN_CORP").val();
					if(code!==corp_code1){
						self.param.area_codes="";
    					self.param.area_names="";
    					self.param.brand_codes="";
    					self.param.brand_names="";
    					self.param.store_codes="";
    					self.param.store_names="";
    					code=corp_code1;
    					$("#shop_list").val("已选0个");
    					$("#vip_type").val("");
    					$("#high_vip_type").val("");
    					$("#quan_select").empty();
    					self.getVipType(code);
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
	regimejs.getVipType=function(corp_code){//获取vip上级会员类型
		var param={};
		param["corp_code"]=corp_code;
		whir.loading.add("",0.5);
		oc.postRequire("post","/vipRules/getVipTypes","",param, function(data){
			var list=JSON.parse(data.message).list;
			var list=JSON.parse(list);
			var html="<li data-value=''>无上级会员类型</li>";
			for(var i=0;i<list.length;i++){
				html+="<li data-value='"+list[i].vip_type+"'>"+list[i].vip_type+"</li>";
			}
			$("#high_vip_type").val('无上级会员类型');
			$("#high_vip_type").siblings("ul").html(html);
			whir.loading.remove();//移除加载框
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
		whir.loading.add("",0.5);//加载等待框
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
				whir.loading.remove();//移除加载框
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
		whir.loading.add("",0.5);//加载等待框
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
				whir.loading.remove();//移除加载框
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
		whir.loading.add("",0.5);//加载等待框
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
				whir.loading.remove();//移除加载框
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
		whir.loading.add("",0.5);
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
	var init=function(){
		var id=sessionStorage.getItem("id");
		regimejs.bindbutton();
		if(id==null){
			var corp_code="";
			regimejs.getcorplist(corp_code);
		}else if(id!==null){
			regimejs.getInputValue(id);
		}
	}
	var obj = {};
	obj.regimejs = regimejs;
	obj.init = init;
	return obj;
}));
$(function(){
	window.regime.init();//初始化
});
