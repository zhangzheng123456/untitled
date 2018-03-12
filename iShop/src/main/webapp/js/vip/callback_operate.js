var oc = new ObjectControl();
var id = sessionStorage.getItem("id");
var _params = {
	"id": id,
	"vip_id":"",
	"user_id_app":"",
	"corp_code":"",
	"open_id":""
};
jQuery(document).ready(function() {
	var _command = "/VIP/callback/select";
	oc.postRequire("post", _command, "", _params, function(data) {
		if (data.code == "0") {
			var msg = JSON.parse(data.message)[0];
			var vip_id=msg.vip_id;
			var user_id_app=msg.user_id;
			var corp_code=msg.corp_code;
			var open_id=msg.open_id;
			var data=msg.created_date.substring(0,10);
			_params.vip_id=msg.vip_id;
			_params.user_id_app=msg.user_id;
			_params.corp_code=msg.corp_code;
			_params.open_id=msg.open_id;
			$("#OWN_CORP").val(msg.company_name);//赋值给所属企业
			$("#CALLBACK_DATE").val(msg.created_date);//回访日期
			$("#CALLBACK_TYPE").val(msg.action);//回访类型
			$("#VIP").val(msg.vip_name)//会员名称
			$("#VIP_CODE").val(msg.vip_card_no);
			$("#task_dec").val(msg.message_content)//会员名称
			$("#CALLBACK_STUFF").val(msg.user_name)//回访员工
			$("#wx_content .task_dec").val(msg.message_content);
			$("#STUFF_CODE").val(msg.user_id);//员工编号
			$("#created_time").val(msg.created_date);
			$("#creator").val(msg.creater);
			$("#modify_time").val(msg.modified_date);
			$("#modifier").val(msg.modifier);
            if(msg.action=='电话'){
				$("#task_dec_content").hide();
			}else{
				$("#task_dec_content").show();
			}
			if(msg.action=="微信"&&msg.type=="爱秀回访"){
				$("#wx_content").show();
				$("#task_dec_content").hide();
			}
			var input = $(".checkbox_isactive").find("input")[0];
			if (msg.isactive == "Y") {
				input.checked = true;
			} else if (msg.isactive == "N") {
				input.checked = false;
			}
			$("#look_more").unbind().bind("click",function(){
				whir.loading.add("mask",0.5);
				$("#wx_dialog").show();
				$("#data").val(data);
				var param={};
				param["vip_id"]=vip_id;
				param["user_id_app"]=user_id_app;
				param["corp_code"]=corp_code;
				param["query_time"]=data;
				param["open_id"]=open_id;
				getWxList(param);
			})
		} else if (data.code == "-1") {
			art.dialog({
				time: 1,
				lock: true,
				cancel: false,
				content: data.message
			});
		}
	});
	$(".operadd_btn ul li:nth-of-type(2)").click(function() {
		$(window.parent.document).find('#iframepage').attr("src", "/vip/callback.html");
	});
	$(".operedit_btn ul li").click(function() {
		$(window.parent.document).find('#iframepage').attr("src", "/vip/callback.html");
	});
	$("#close").click(function(){
		$("#wx_dialog").hide();
		whir.loading.remove();//移除遮罩层
	})
});
function checkStart(data){
	var param={};
	param["vip_id"]=_params.vip_id;
	param["user_id_app"]=_params.user_id_app;
	param["corp_code"]=_params.corp_code;
	param["query_time"]=data;
	param["open_id"]=_params.open_id;
	getWxList(param);
};
function getNowFormatDate() {//获取当前日期
	var date = new Date();
	var seperator1 = "-";
	var year = date.getFullYear();
	var month = date.getMonth() + 1;
	var strDate = date.getDate();
	if (month >= 1 && month <= 9) {
		month = "0" + month;
	}
	if (strDate >= 0 && strDate <= 9) {
		strDate = "0" + strDate;
	}
	var currentdate = year + seperator1 + month + seperator1 + strDate;
	return currentdate
}
function getWxList(param) {
	var param=param;
	oc.postRequire("post", "/VIP/callback/find/message", "", param, function(data) {
		var message=JSON.parse(data.message).content;
		var user_avatar="";
		// var user_avatar=JSON.parse(data.message).user_avatar;
		// var vip_avatar=JSON.parse(data.message).vip_avatar;
		var message=JSON.parse(message);
		var html="";
		var html2="";
		for(var i=0;i<message.length;i++){
		// for(var i=message.length-1;i>=0;i--){
			var  type="";
			var  name="";
			var color="";
			if(message[i].message_target=="1"){
				type="right";
				name=$("#CALLBACK_STUFF").val();
				color="data black";
				user_avatar=JSON.parse(data.message).user_avatar;
				if(user_avatar==""){
					user_avatar="../img/head.png";
				}
			}
			if(message[i].message_target=="0"){
				type="left";
				name=$("#VIP").val();
				color="data";
				user_avatar=JSON.parse(data.message).vip_avatar;
				if(user_avatar==""){
					user_avatar="../img/head.png";
				}
			}
				if(message[i].message_type=="text"){
					var sms=emojito(message[i].message_content);
					var content=eplace_em(sms);
					html+='<li class="'+type+'">\
						<div class="img">\
							<img src="'+user_avatar+'" alt="">\
						</div>\
						<div class="right">\
							<div class="data">'+name+" "+message[i].message_date+'</div>\
							<div class="text">'+content+'<div class="angle_r"></div></div>\
						</div>\
					</li>'
					html2+='<li class="left">\
						<div class="img">\
							<img src="'+user_avatar+'" alt="">\
						</div>\
						<div class="right">\
							<div class="'+color+'">'+name+" "+message[i].message_date+'</div>\
							<div class="text">'+content+'<div class="angle_r"></div></div>\
						</div>\
					</li>'
				}
				if (message[i].message_type == "image") {
					html += '<li class="' + type + '">\
					<div class="img">\
						<img src="../img/head.png" alt="">\
					</div>\
					<div class="right">\
						<div class="data">' + name + " " + message[i].message_date + '</div>\
						<div class="text"><div class="img_text"><img src="' + message[i].message_content + '@300h_300w_1e_1c"></div><div class="angle_r"></div></div>\
					</div>\
				</li>'
					html2 += '<li class="' + type + '">\
					<div class="img">\
						<img src="../img/head.png" alt="">\
					</div>\
					<div class="right">\
						<div class="'+color+'">' + name + " " + message[i].message_date + '</div>\
						<div class="text"><div class="img_text"><img src="' + message[i].message_content + '@300h_300w_1e_1c"></div><div class="angle_r"></div></div>\
					</div>\
				</li>'
				}
		}
		if(message.length<=0){
			html+='<li style="text-align: center;line-height: 300px;height: 300px;">暂无内容</li>';
			html2+='<li style="text-align: center;line-height: 300px;height: 300px;">暂无内容</li>';
		}
		$("#chat_record ul").html(html);
		$("#chat_record2 ul").html(html2);
		search();
	})
};
function eplace_em(str){
	str=decodeURI(str);
	str = str.replace(/(\[.+?\])/g,'<img src="../img/face/expression_$1@2x.png" border="0" style="margin-bottom: -5px;width: 22px" />');
	return str;
};
function emojito(content){  //把微信发过来的表情转换成[ 表情 ] 格式
	content=content.replace(/\/::\)/g,'[微笑]');
	content=content.replace(/\/::~/g,'[撇嘴]');
	content=content.replace(/\/::B/g,'[色]');
	content=content.replace(/\/::\|/g,'[发呆]');
	content=content.replace(/\/:8-\)/g,'[得意]');
	content=content.replace(/\/::</g,'[流泪]');
	content=content.replace(/\/::\$/g,'[害羞]');
	content=content.replace(/\/::X/g,'[闭嘴]');
	content=content.replace(/\/::Z/g,'[睡]');
	content=content.replace(/\/::'\(/g,'[大哭]');
	content=content.replace(/\/::-\|/g,'[尴尬]');
	content=content.replace(/\/::@/g,'[发怒]');
	content=content.replace(/\/::P/g,'[调皮]');
	content=content.replace(/\/::D/g,'[呲牙]');
	content=content.replace(/\/::O/g,'[惊讶]');
	//content=content.replace(/\/::/g,'[难过]');
	content=content.replace(/\/::\+/g,'[酷]');
	content=content.replace(/\/:--b/g,'[冷汗]');
	content=content.replace(/\/::Q/g,'[抓狂]');
	content=content.replace(/\/::T/g,'[吐]');
	content=content.replace(/\/:,@P/g,'[偷笑]');
	content=content.replace(/\/:,@-D/g,'[愉快]');
	content=content.replace(/\/::d/g,'[白眼]');
	content=content.replace(/\/:,@o/g,'[傲慢]');
	content=content.replace(/\/::g/g,'[饥饿]');
	content=content.replace(/\/:\|-\)/g,'[困]');
	content=content.replace(/\/::!/g,'[惊恐]');
	content=content.replace(/\/::L/g,'[流汗]');
	content=content.replace(/\/::>/g,'[憨笑]');
	content=content.replace(/\/::,@/g,'[悠闲]');
	content=content.replace(/\/:,@f/g,'[奋斗]');
	content=content.replace(/\/::-S/g,'[咒骂]');
	content=content.replace(/\/:\?/g,'[疑问]');
	content=content.replace(/\/:,@x/g,'[嘘]');
	content=content.replace(/\/:,@@/g,'[晕]');
	content=content.replace(/\/::8/g,'[疯了]');
	content=content.replace(/\/:,@!/g,'[衰]');
	content=content.replace(/\/:!!!/g,'[骷髅]');
	content=content.replace(/\/:xx/g,'[敲打]');
	content=content.replace(/\/:bye/g,'[再见]');
	content=content.replace(/\/:wipe/g,'[擦汗]');
	content=content.replace(/\/:dig/g,'[抠鼻]');
	content=content.replace(/\/:handclap/g,'[鼓掌]');
	content=content.replace(/\/:&-\(/g,'[糗大了]');
	content=content.replace(/\/:B-\)/g,'[坏笑]');
	content=content.replace(/\/:<@/g,'[左哼哼]');
	content=content.replace(/\/:@>/g,'[右哼哼]');
	content=content.replace(/\/::-O/g,'[哈欠]');
	content=content.replace(/\/:>-\|/g,'[鄙视]');
	content=content.replace(/\/:P-\(/g,'[委屈]');
	content=content.replace(/\/::'\|/g,'[快哭了]');
	content=content.replace(/\/:X-\)/g,'[阴险]');
	content=content.replace(/\/::\*/g,'[亲亲]');
	content=content.replace(/\/:@x/g,'[吓]');
	content=content.replace(/\/:8\*/g,'[可怜]');
	content=content.replace(/\/:pd/g,'[菜刀]');
	content=content.replace(/\/:<W>/g,'[西瓜]');
	content=content.replace(/\/:beer/g,'[啤酒]');
	content=content.replace(/\/:basketb/g,'[篮球]');
	content=content.replace(/\/:oo/g,'[乒乓]');
	content=content.replace(/\/:coffee/g,'[咖啡]');
	content=content.replace(/\/:eat/g,'[饭]');
	content=content.replace(/\/:pig/g,'[猪头]');
	content=content.replace(/\/:rose/g,'[玫瑰]');
	content=content.replace(/\/:fade/g,'[凋谢]');
	content=content.replace(/\/:showlove/g,'[嘴唇]');
	content=content.replace(/\/:heart/g,'[爱心]');
	content=content.replace(/\/:break/g,'[心碎]');
	content=content.replace(/\/:cake/g,'[蛋糕]');
	content=content.replace(/\/:li/g,'[闪电]');
	content=content.replace(/\/:bome/g,'[炸弹]');
	content=content.replace(/\/:kn/g,'[刀]');
	content=content.replace(/\/:footb/g,'[足球]');
	content=content.replace(/\/:ladybug/g,'[瓢虫]');
	content=content.replace(/\/:shit/g,'[便便]');
	content=content.replace(/\/:moon/g,'[月亮]');
	content=content.replace(/\/:sun/g,'[太阳]');
	content=content.replace(/\/:gift/g,'[礼物]');
	content=content.replace(/\/:hug/g,'[拥抱]');
	content=content.replace(/\/:strong/g,'[强]');
	content=content.replace(/\/:weak/g,'[弱]');
	content=content.replace(/\/:share/g,'[握手]');
	content=content.replace(/\/:v/g,'[胜利]');
	content=content.replace(/\/:@\)/g,'[抱拳]');
	content=content.replace(/\/:jj/g,'[勾引]');
	content=content.replace(/\/:@@/g,'[拳头]');
	content=content.replace(/\/:bad/g,'[差劲]');
	content=content.replace(/\/:lvu/g,'[爱你]');
	content=content.replace(/\/:no/g,'[NO]');
	content=content.replace(/\/:ok/g,'[OK]');
	content=content.replace(/\/:love/g,'[爱情]');
	content=content.replace(/\/:<L>/g,'[飞吻]');
	content=content.replace(/\/:jump/g,'[跳跳]');
	content=content.replace(/\/:shake/g,'[发抖]');
	content=content.replace(/\/:<O>/g,'[怄火]');
	content=content.replace(/\/:circle/g,'[转圈]');
	content=content.replace(/\/:kotow/g,'[磕头]');
	content=content.replace(/\/:turn/g,'[回头]');
	content=content.replace(/\/:skip/g,'[跳绳]');
	content=content.replace(/\/:oY/g,'[投降]');
	content=content.replace(/\/::\(/g,'[难过]');
	return content
};
$("#corp_search").keydown(function(){
	var event=window.event||arguments[0];
	if(event.keyCode == 13){
		search();
	}
});
function search() {
	var value=$("#corp_search").val();
	$("#chat_record2 ul li").hide();
	$("#chat_record").hide();
	$("#chat_record2").show();
	$("#chat_record2 ul li .text:contains('"+value+"')").parents("li").show();
	if($("#chat_record2 ul li .text:contains('"+value+"')").parents("li").length<=0){
		$("#chat_record").hide();
		$("#chat_record2 ul").append('<li id="li" style="text-align: center;line-height: 300px;height: 300px;">暂无内容</li>');
	}else  if($("#chat_record2 ul li .text:contains('"+value+"')").parents("li").length>0){
		$("#li").hide();
		$("#chat_record").hide();
	}
	if(value==""){
		$("#chat_record").show();
		$("#chat_record ul li").show();
		$("#chat_record2").hide();
	}
}
$("#corp_search_f").click(function(){
	search();
});
$("#chat_record2 ul").on("dblclick ","li",function () {
	var content=$(this).find(".right .data").text();
	$("#chat_record").show();
	$("#chat_record ul li .data:contains('"+content+"')").parents("li").prevAll().hide();
	$("#chat_record").animate({
		'scrollTop': 0
	},500);
})
