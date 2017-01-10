// QQ表情插件
(function($){
	$.fn.qqFace = function(options){
		var defaults = {
			id : 'facebox',
			path:'image/face/',
			assign : 'content',
			tip : 'em_',
		};
		var option = $.extend(defaults, options);
		var assign = $('#'+option.assign);
		var id = option.id;
		var path = option.path;
		var tip = option.tip;
		if(assign.length<=0){
			alert('缺少表情赋值对象。');
			return false;
		}

		$(this).click(function(e){
			var strFace, labFace;
			if($('#'+id).length<=0){
				strFace = '<div id="'+id+'" class="qqFace" sroll="no">' +
					'<table border="0" cellspacing="0" cellpadding="0"><tr>';
				//for(var i=1; i<=75; i++){
				//	labFace = '['+tip+i+']';
				//	strFace += '<td><img src="'+path+i+'.gif" onclick="$(\'#'+option.assign+'\').setCaret();$(\'#'+option.assign+'\').insertAtCaret(\'' + labFace + '\');" /></td>';
				//	if( i % 15 == 0 ) strFace += '</tr><tr>';
				//}
				//竖排
				//var all='<td><img src="'+path+'expression_[微笑]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[撇嘴]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[色]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[发呆]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[得意]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[流泪]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[害羞]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[闭嘴]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[睡]@2x.png" onclick="$(this).inImage(this);" /></td></tr><tr>'
				//	+'<td><img src="'+path+'expression_[大哭]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[尴尬]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[发怒]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[调皮]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[呲牙]@2x.png" onclick="$(this).inImage(this);" /></td>';
				//all+='<td><img src="'+path+'expression_[惊讶]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[难过]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[酷]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[冷汗]@2x.png" onclick="$(this).inImage(this);" /></td></tr><tr>'
				//	+'<td><img src="'+path+'expression_[抓狂]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[吐]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[偷笑]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[愉快]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[白眼]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[傲慢]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[饥饿]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[困]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[惊恐]@2x.png" onclick="$(this).inImage(this);" /></td></tr><tr>'
				//	+'<td><img src="'+path+'expression_[流汗]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[憨笑]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[悠闲]@2x.png" onclick="$(this).inImage(this);" /></td>';
				//all+='<td><img src="'+path+'expression_[奋斗]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[咒骂]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[疑问]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[嘘]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[晕]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[疯了]@2x.png" onclick="$(this).inImage(this);" /></td></tr><tr>'
				//	+'<td><img src="'+path+'expression_[衰]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[骷髅]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[敲打]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[再见]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[擦汗]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[抠鼻]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[鼓掌]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[糗大了]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[坏笑]@2x.png" onclick="$(this).inImage(this);" /></td></tr><tr>'
				//	+'<td><img src="'+path+'expression_[左哼哼]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[右哼哼]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[哈欠]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[鄙视]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[委屈]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[快哭了]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[阴险]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[亲亲]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[吓]@2x.png" onclick="$(this).inImage(this);" /></td></tr><tr>'
				//	+'<td><img src="'+path+'expression_[可怜]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[菜刀]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[西瓜]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[啤酒]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[篮球]@2x.png" onclick="$(this).inImage(this);" /></td>';
				//all+='<td><img src="'+path+'expression_[乒乓]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[咖啡]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[饭]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[猪头]@2x.png" onclick="$(this).inImage(this);" /></td></tr><tr>'
				//	+'<td><img src="'+path+'expression_[玫瑰]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[凋谢]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[嘴唇]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[爱心]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[心碎]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[蛋糕]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[闪电]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[炸弹]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[刀]@2x.png" onclick="$(this).inImage(this);" /></td></tr><tr>'
				//	+'<td><img src="'+path+'expression_[足球]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[瓢虫]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[便便]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[月亮]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[太阳]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[礼物]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[拥抱]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[强]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[弱]@2x.png" onclick="$(this).inImage(this);" /></td></tr><tr>'
				//	+'<td><img src="'+path+'expression_[握手]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[胜利]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[抱拳]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[勾引]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[拳头]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[差劲]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[爱你]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[NO]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[OK]@2x.png" onclick="$(this).inImage(this);" /></td></tr><tr>'
				//	+'<td><img src="'+path+'expression_[爱情]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[飞吻]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[跳跳]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[发抖]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[怄火]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[转圈]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[磕头]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[回头]@2x.png" onclick="$(this).inImage(this);" /></td>'
				//	+'<td><img src="'+path+'expression_[跳绳]@2x.png" onclick="$(this).inImage(this);" /></td></tr><tr>'
				//	+'<td><img src="'+path+'expression_[投降]@2x.png" onclick="$(this).inImage(this);" /></td>'
				var all='<td><img src="'+path+'expression_[微笑]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[撇嘴]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[色]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[发呆]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[得意]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[流泪]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[害羞]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[闭嘴]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[睡]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[大哭]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[尴尬]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[发怒]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[调皮]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[呲牙]@2x.png" onclick="$(this).inImage(this);" /></td>';
				all+='<td><img src="'+path+'expression_[惊讶]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[难过]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[酷]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[冷汗]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[抓狂]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[吐]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[偷笑]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[愉快]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[白眼]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[傲慢]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[饥饿]@2x.png" onclick="$(this).inImage(this);" /></td></tr><tr>'
					+'<td><img src="'+path+'expression_[困]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[惊恐]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[流汗]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[憨笑]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[悠闲]@2x.png" onclick="$(this).inImage(this);" /></td>';
				all+='<td><img src="'+path+'expression_[奋斗]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[咒骂]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[疑问]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[嘘]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[晕]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[疯了]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[衰]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[骷髅]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[敲打]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[再见]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[擦汗]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[抠鼻]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[鼓掌]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[糗大了]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[坏笑]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[左哼哼]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[右哼哼]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[哈欠]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[鄙视]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[委屈]@2x.png" onclick="$(this).inImage(this);" /></td></tr><tr>'
					+'<td><img src="'+path+'expression_[快哭了]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[阴险]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[亲亲]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[吓]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[可怜]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[菜刀]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[西瓜]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[啤酒]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[篮球]@2x.png" onclick="$(this).inImage(this);" /></td>';
				all+='<td><img src="'+path+'expression_[乒乓]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[咖啡]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[饭]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[猪头]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[玫瑰]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[凋谢]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[嘴唇]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[爱心]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[心碎]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[蛋糕]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[闪电]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[炸弹]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[刀]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[足球]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[瓢虫]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[便便]@2x.png" onclick="$(this).inImage(this);" /></td></tr><tr>'
					+'<td><img src="'+path+'expression_[月亮]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[太阳]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[礼物]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[拥抱]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[强]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[弱]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[握手]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[胜利]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[抱拳]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[勾引]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[拳头]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[差劲]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[爱你]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[NO]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[OK]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[爱情]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[飞吻]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[跳跳]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[发抖]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[怄火]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[转圈]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[磕头]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[回头]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[跳绳]@2x.png" onclick="$(this).inImage(this);" /></td>'
					+'<td><img src="'+path+'expression_[投降]@2x.png" onclick="$(this).inImage(this);" /></td>'
				strFace += all+'</tr></table></div>';
			}
			$(this).parent().append(strFace);
			//var offset = $(this).position();
			//var top = offset.top + $(this).outerHeight();
			//$('#'+id).css('top',top);
			//$('#'+id).css('left',offset.left);
			$('#'+id).show();
			e.stopPropagation();
		});

		//$(document).click(function(){
		$('.content').click(function(){
			$('#'+id).hide();
			$('#'+id).remove();
			//alert('隐藏');
		});
	};

})(jQuery);

jQuery.extend({
	unselectContents: function(){
		if(window.getSelection)
			window.getSelection().removeAllRanges();
		else if(document.selection)
			document.selection.empty();
	}
});
jQuery.fn.extend({
	selectContents: function(){
		$(this).each(function(i){
			var node = this;
			var selection, range, doc, win;
			if ((doc = node.ownerDocument) && (win = doc.defaultView) && typeof win.getSelection != 'undefined' && typeof doc.createRange != 'undefined' && (selection = window.getSelection()) && typeof selection.removeAllRanges != 'undefined'){
				range = doc.createRange();
				range.selectNode(node);
				if(i == 0){
					selection.removeAllRanges();
				}
				selection.addRange(range);
			} else if (document.body && typeof document.body.createTextRange != 'undefined' && (range = document.body.createTextRange())){
				range.moveToElementText(node);
				range.select();
			}
		});
	},
	inImage: function(_this){
		$('.activity').css('bottom','330px');
		$('.content').css('bottom','440px');
		var text=$(_this);
		text= "<img class='inImage' src="+text.attr('src')+"> ";
		text=text.replace(/<img.*?face\/expression_(\[.+?\])@2x.png.*?>/g,"$1");
		text=text.substring(0,text.length-1);
		var val = $('#input').val();
		if(val =='') {
			$("#input").val(text);
		}else{
			var a=$("#input").val();
			a +=text;
			text =a;
			$("#input").val(text);
		}
	},
	setCaret: function(){
		if(! /msie/.test(navigator.userAgent.toLowerCase())) return;
		var initSetCaret = function(){
			var textObj = $(this).get(0);
			textObj.caretPos = document.selection.createRange().duplicate();
		};
		$(this).click(initSetCaret).select(initSetCaret).keyup(initSetCaret);
	},

	insertAtCaret: function(textFeildValue){
		var textObj = $(this).get(0);
		if(document.all && textObj.createTextRange && textObj.caretPos){
			var caretPos=textObj.caretPos;
			caretPos.text = caretPos.text.charAt(caretPos.text.length-1) == '' ?
			textFeildValue+'' : textFeildValue;
		} else if(textObj.setSelectionRange){
			var rangeStart=textObj.selectionStart;
			var rangeEnd=textObj.selectionEnd;
			var tempStr1=textObj.value.substring(0,rangeStart);
			var tempStr2=textObj.value.substring(rangeEnd);
			textObj.value=tempStr1+textFeildValue+tempStr2;
			textObj.focus();
			var len=textFeildValue.length;
			textObj.setSelectionRange(rangeStart+len,rangeStart+len);
			textObj.blur();
		}else{
			textObj.value+=textFeildValue;
		}
	}
});
function inimage(text){
	var obj= $(".im-message-area")[0];
	var range, node;
	if(!obj.hasfocus) {
		obj.focus();
	}
	if (window.getSelection && window.getSelection().getRangeAt) {
		range = window.getSelection().getRangeAt(0);
		range.collapse(false);
		var html=obj.innerHTML;
		obj.innerHTML="";
		node = range.createContextualFragment(html+text);
		var c = node.lastChild;
		range.insertNode(node);
		if(c){
			range.setEndAfter(c);
			range.setStartAfter(c)
		}
		var j = window.getSelection();
		j.removeAllRanges();
		j.addRange(range);

	} else if (document.selection && document.selection.createRange) {
		document.selection.createRange().pasteHTML(text);
	}
}
