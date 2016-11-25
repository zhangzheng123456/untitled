var oc = new ObjectControl();
var  message={
	init:function(){
		this.getCorplist();
		this.clickVip();
	},
	getCorplist:function(){//获取所属企业列表
	    var corp_command="/user/getCorpByUser";
		oc.postRequire("post", corp_command,"", "", function(data){
			if(data.code=="0"){
				var msg=JSON.parse(data.message);
				var corp_html='';
				for(var i=0;i<msg.corps.length;i++){
					corp_html+='<option value="'+msg.corps[i].corp_code+'">'+msg.corps[i].corp_name+'</option>';
				}
				$("#OWN_CORP").append(corp_html);
				$('.corp_select select').searchableSelect();
				$('.corp_select .searchable-select-input').keydown(function(event){
					var event=window.event||arguments[0];
					if(event.keyCode == 13){
					
					}
				})
				$('.searchable-select-item').click(function(){
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
	},
	clickVip:function(){//选择会员
		$("#add_sendee").bind("click",function(){
			$("#page-wrapper").hide();
			$(".content").show();
		})
		$("#turnoff").bind("click",function(){
			$("#page-wrapper").show();
			$(".content").hide();
		})
	},
};
$(function(){
	message.init();
})
