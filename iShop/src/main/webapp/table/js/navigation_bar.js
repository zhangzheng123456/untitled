$(function(){
	$('#navbar').append('<div class="navbar">\
		                    <div class="logo"></div>\
		                    <ul class="floot-r">\
		                        <li>\
    				           		<a href="#"><span class="icon-ishop_3-01"></span></a>\
    			                </li>\
    			                <li>\
				    				<a href="#">\
				    				<span class="icon-ishop_3-02"></span>\
				    				</a>\
				    				<p>2</p>\
				    			</li>\
				    			<li>\
				    				<a href="#"><span class="icon-ishop_3-03"></span></a>\
				    			</li>\
				    			<li>\
				    			    <a href="#"><span class="icon-ishop_3-04"></span></a>\
				    			</li>\
				    		    <li class="search">\
				    			    <input type="text" placeholder="输入搜索的内容">\
				    				<i class="icon-ishop_3-05"></i>\
				    			</li>\
    			 				<li id="img" onmouseover="show(\'.person_menu\',true)" onmouseout="show(\'.person_menu\',false)"><img src="../table/images/user_01.jpg" alt="" width="40px" height="40px"></li>\
		                    </ul>\
		                    <div class="person_menu" onmouseover="show(\'.person_menu\',true)" onmouseout="show(\'.person_menu\',false)">\
                            	<div><a href="#"><span class="icon-ishop_3-06"></span>我的账号</a><i></i></div>\
                            	<div><a href="#"><span class="icon-ishop_3-07"></span>退出登录</a></div>\
                            	<div><a href="#"><span class="icon-ishop_3-08"></span>帮助手册</a></div>\
                            </div>\
		                </div>\
		                <div class="sidebar" id="sidebar">\
		                    <div>\
		                        <a href="#"><h1 id="corp" class="menu_t" data="/corp/corp.html"><i class="icon-ishop_2-07"></i>企业管理<span></span></h1></a>\
		                    </div>\
		                    <div>\
		                        <a href="#"><h1 id="shop" class="menu_t" data="/shop/shop.html"><i class="icon-ishop_2-01"></i>店铺管理<span></span></h1></a>\
		                    </div>\
		                    <ul>\
		                        <li>\
		                            <h1 class="menu_t"><i class="icon-ishop_2-02"></i>员工管理<span></span></h1>\
		                            <dl>\
		                                <dd data="/staff/staff.html"><a href="javascript:void(0);"><span></span>员工列表</a></dd>\
		                                <dd data="/staff/checkin.html"><a href="javascript:void(0);"><span></span>签到管理</a></dd>\
		                            </dl>\
		                        </li>\
		                        <li>\
		                            <h1 class="menu_t"><i class="icon-ishop_2-08"></i>业绩管理<span></span></h1>\
		                            <dl>\
		                                <dd data="/achv/shopgoal.html"><a href="javascript:void(0);"><span></span>店铺业绩目标</a></dd>\
		                                <dd data="/achv/staffgoal.html"><a href="javascript:void(0);"><span></span>员工业绩目标</a></dd>\
		                            </dl>\
		                        </li>\
		                        <li>\
		                            <h1 class="menu_t"><i class="icon-ishop_2-03"></i>会员管理<span></span></h1>\
		                            <dl>\
		                                <dd data="/vip/vip.html"><a href="javascript:void(0);"><span></span>会员列表</a></dd>\
		                                <dd data="/vip/viplabel.html"><a href="javascript:void(0);"><span></span>会员标签管理</a></dd>\
		                                <dd data="/vip/callback.html"><a href="javascript:void(0);"><span></span>回访记录管理</a></dd>\
		                            </dl>\
		                        </li>\
		                        <li>\
		                            <h1 class="menu_t"><i class="icon-ishop_2-04"></i>商品管理<span></span></h1>\
		                            <dl>\
		                                <dd data="/goods/fab.html"><a href="javascript:void(0);"><span></span>商品培训(FAB)</a></dd>\
		                                <dd data="/goods/xiuda.html"><a href="javascript:void(0);"><span></span>秀搭管理</a></dd>\
		                            </dl>\
		                        </li>\
		                        <li>\
		                            <h1 class="menu_t"><i class="icon-ishop_2-05"></i>消息管理<span></span></h1>\
		                            <dl>\
		                                <dd data="/message/iShop.html"><a href="javascript:void(0);"><span></span>爱秀消息</a></dd>\
		                                <dd data="/message/mobile.html"><a href="javascript:void(0);"><span></span>手机短信</a></dd>\
		                            </dl>\
		                        </li>\
		                        <li>\
		                            <h1 class="menu_t"><i class="icon-ishop_2-10"></i>用户及权限<span></span></h1>\
		                            <dl>\
		                                <dd data="/user/role.html"><a href="javascript:void(0);"><span></span>角色定义</a></dd>\
		                                <dd data="/user/group.html"><a href="javascript:void(0);"><span></span>群组管理</a></dd>\
		                                <dd data="/user/user.html"><a href="javascript:void(0);"><span></span>用户管理</a></dd>\
		                            </dl>\
		                        </li>\
		                        <li>\
		                            <h1 class="menu_t"><i class="icon-ishop_2-09"></i>系统管理<span></span></h1>\
		                            <dl>\
		                                <dd data="/system/feedback.html"><a href="javascript:void(0);"><span></span>用户反馈</a></dd>\
		                                <dd data="/system/appversion.html"><a href="javascript:void(0);"><span></span>APP版本控制</a></dd>\
		                                <dd data="/system/cache.html"><a href="javascript:void(0);"><span></span>缓存管理</a></dd>\
		                                <dd data="/system/interface.html"><a href="javascript:void(0);"><span></span>接口管理</a></dd>\
		                                <dd class="log"><b></b>日志管理<i></i></dd>\
		                                <ul>\
		                                    <li data="/system/errorlog.html"><a href="javascript:void(0);"><span></span>错误日志</a></dd>\
		                                    <li data="/system/logging.html"><a href="javascript:void(0);"><span></span>登录日志</a></dd>\
		                                    <li data="/system/authcode.html"><a href="javascript:void(0);"><span></span>验证码管理</a></dd>\
		                                </ul>\
		                            </dl>\
		                        </li>\
		                    </ul>\
        				</div>'
    )
     //左侧导航栏
    $("#corp").click(function(){
    	var src=$(this).attr("data");
    	$('#iframepage').attr("src",src);
    })
    $("#shop").click(function(){
    	var src=$(this).attr("data");
    	$('#iframepage').attr("src",src);
    })
    $(".sidebar ul li dl dd").click(function(e){
    	e.stopPropagation();
    	var src=$(this).attr("data");
    	$('#iframepage').attr("src",src);
        $(this).find("span").addClass("icon-ishop_8-01");
        $(this).find("a").css({color:"#6cc1c8"});
        $(this).siblings("dd").find("a").css({color:"#fff"});
        $(this).siblings("dd").find("span").removeClass("icon-ishop_8-01");
    });
    $(".sidebar .log").click(function(e){
    	console.log(this);
        $(this).next("ul").slideToggle(300);
        $(this).toggleClass("h1");
        $(this).find("i").toggleClass("icon-ishop_8-02");
    });
    $(".sidebar ul li").click(function(e){
        e.stopPropagation();
        var src=$(this).attr("data");
    	$('#iframepage').attr("src",src);
        $(this).find("h1").next("dl").slideToggle(300).parents().siblings("li").find("dl").slideUp(300);
        $(this).find("h1").toggleClass("h1").parents().siblings("li").find("h1").removeClass("h1");
        $(this).find("h1 span").toggleClass("icon-ishop_8-02").parents().siblings("li").find("h1 span").removeClass("icon-ishop_8-02");   
    });
    $(".sidebar ul li dl ul li").click(function(e){
        e.stopPropagation();
        var src=$(this).attr("data");
    	$('#iframepage').attr("src",src);
        $(this).find("span").addClass("icon-ishop_8-01");
        $(this).find("a").css({color:"#6cc1c8"});
        $(this).siblings("li").find("a").css({color:"#fff"});
        $(this).siblings("li").find("span").removeClass("icon-ishop_8-01");
    });
})