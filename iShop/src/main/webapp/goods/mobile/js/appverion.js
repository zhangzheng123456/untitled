    var oc = new ObjectControl();
	cache = {
		'pageNumber':'1',
		'pageSize':'10',
		next:false
	}
	function getList(){
		var param={};
		param["pageNumber"]=cache.pageNumber;
		param["pageSize"]=cache.pageSize;
		var osType = getWebOSType();
		if(osType=="iOS"){
			param["platform"]="iOS";
		}else if(osType == "Android"){
			param["platform"]="Android";
		}
		oc.postRequire("post","/api/appverion/list","0",param,function(data){
			if(data.code=="0"){
				var message=JSON.parse(data.message);
				var list=JSON.parse(message.list).list;
				var html="";
				for(var i=0;i<list.length;i++){
					var str="";
					str=list[i].version_describe.replace(new RegExp('<n>','gm'),'<br/>');
					html+="<div class='appverion'><h3 class='appverion_title'>"
						+list[i].version_id+"</h3><div class='data_time'>"
						+list[i].created_date+"</div><div class='verion_content'>"+str+"</div><div class='xian'></div></div>"
				}
				$("#appverion_list").append(html);
				if(JSON.parse(message.list).hasNextPage==false){
					cache.next=false;
				}
				if(JSON.parse(message.list).hasNextPage==true){
					cache.next=true;
				}
			}else if(data.code=="-1"){
				console.log(123);
			}
		})
	}
	//获取手机系统
	function getWebOSType(){
		var browser = navigator.userAgent;
		var isAndroid = browser.indexOf('Android') > -1 || browser.indexOf('Adr') > -1; //android终端
		var isiOS = !!browser.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
		if(isAndroid){
			return "Android";
		}else if (isiOS) {
			return "iOS";
		}else{
			return "Unknown"
		}
	}
	//获取iShop用户信息
	function getAppUserInfo(){
		var osType = getWebOSType();
		var userInfo = null;
		if(osType=="iOS"){
			userInfo = NSReturnUserInfo();
		}else if(osType == "Android"){
			userInfo = iShop.ReturnUserInfo();
		}
		return userInfo;
	}
getList();
	$(window).scroll( function() {
		var bot = 50; //bot是底部距离的高度
		if ((bot + $(window).scrollTop()) >= ($(document).height() - $(window).height())) {
			if(!cache.next){
				return;
			}
			cache.next=false;
			cache.pageNumber++;
			getList();
		}
	})

