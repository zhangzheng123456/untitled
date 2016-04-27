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
		                        <a href="企业管理.html"><h1 class="menu_t"><i class="icon-ishop_2-07"></i>企业管理<span></span></h1></a>\
		                    </div>\
		                    <div>\
		                        <a href="店铺管理.html"><h1 class="menu_t"><i class="icon-ishop_2-01"></i>店铺管理<span></span></h1></a>\
		                    </div>\
		                    <ul>\
		                        <li>\
		                            <h1 class="menu_t"><i class="icon-ishop_2-02"></i>员工管理<span></span></h1>\
		                            <dl>\
		                                <dd><a href="员工列表.html"><span></span>员工列表</a></dd>\
		                                <dd><a href="签到管理.html"><span></span>签到管理</a></dd>\
		                            </dl>\
		                        </li>\
		                        <li>\
		                            <h1 class="menu_t"><i class="icon-ishop_2-08"></i>业绩管理<span></span></h1>\
		                            <dl>\
		                                <dd><a href="店铺业绩目标.html"><span></span>店铺业绩目标</a></dd>\
		                                <dd><a href="员工业绩目标.html"><span></span>员工业绩目标</a></dd>\
		                            </dl>\
		                        </li>\
		                        <li>\
		                            <h1 class="menu_t"><i class="icon-ishop_2-03"></i>会员管理<span></span></h1>\
		                            <dl>\
		                                <dd><a href="会员列表.html"><span></span>会员列表</a></dd>\
		                                <dd><a href="会员标签管理.html"><span></span>会员标签管理</a></dd>\
		                                <dd><a href="回访记录管理.html"><span></span>回访记录管理</a></dd>\
		                            </dl>\
		                        </li>\
		                        <li>\
		                            <h1 class="menu_t"><i class="icon-ishop_2-04"></i>商品管理<span></span></h1>\
		                            <dl>\
		                                <dd><a href="商品培训.html"><span></span>商品培训(FAB)</a></dd>\
		                                <dd><a href="秀搭管理.html"><span></span>秀搭管理</a></dd>\
		                            </dl>\
		                        </li>\
		                        <li>\
		                            <h1 class="menu_t"><i class="icon-ishop_2-05"></i>消息管理<span></span></h1>\
		                            <dl>\
		                                <dd><a href="爱秀消息.html"><span></span>爱秀消息</a></dd>\
		                                <dd><a href="手机短信.html"><span></span>手机短信</a></dd>\
		                            </dl>\
		                        </li>\
		                        <li>\
		                            <h1 class="menu_t"><i class="icon-ishop_2-10"></i>用户及权限<span></span></h1>\
		                            <dl>\
		                                <dd><a href="角色定义.html"><span></span>角色定义</a></dd>\
		                                <dd><a href="群组管理.html"><span></span>群组管理</a></dd>\
		                                <dd><a href="用户管理.html"><span></span>用户管理</a></dd>\
		                            </dl>\
		                        </li>\
		                        <li>\
		                            <h1 class="menu_t"><i class="icon-ishop_2-09"></i>系统管理<span></span></h1>\
		                            <dl>\
		                                <dd><a href="用户反馈.html"><span></span>用户反馈</a></dd>\
		                                <dd><a href="APP.html"><span></span>APP版本控制</a></dd>\
		                                <dd><a href="缓存管理.html"><span></span>缓存管理</a></dd>\
		                                <dd><a href="接口管理.html"><span></span>接口管理</a></dd>\
		                                <dd class="log"><b></b>日志管理<i></i></dd>\
		                                <ul>\
		                                    <li><a href="#"><span></span>错误日志</a></dd>\
		                                    <li><a href="#"><span></span>登录日志</a></dd>\
		                                    <li><a href="#"><span></span>验证码管理</a></dd>\
		                                </ul>\
		                            </dl>\
		                        </li>\
		                    </ul>\
        				</div>'
    )
})