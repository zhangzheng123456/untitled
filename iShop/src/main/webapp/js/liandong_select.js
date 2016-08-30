//日期联动
var num=0;
function YYYYMMDDstart(){
	MonHead = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];//（1，3，5，7，8，10，12月是31天，4,6,9,11是30天，2月看是不是闰年，如果是就是29天，不是就是28天。）
	var YYYYvalue="";
	//先给年下拉框赋内容
	var y  = new Date().getFullYear();
	for(var i=1;i<13;i++){
		getInfo(new Date().getFullYear(),i);
	}
	for (var i = (y-5); i < (y+1); i++){//以今年为准，前5年至今
		$('.Year ul').append('<li>'+" "+ i +" 年"+'</li>');
	}
	//赋月份的下拉框
	for (var i = 1; i < 13; i++){
		var num=i;
		if(num.toString().length==1){
			$('.Month ul').append('<li>'+" 0"+ i +" 月"+'</li>');
		}else{
			$('.Month ul').append('<li>'+" "+ i +" 月"+'</li>');
		}
	}
	// 初始化选择框
	$('.select_1 .Year .title')[0].innerHTML= y +" 年";
	$('.select_2 .Year .title')[0].innerHTML= y +" 年";
	$('.select_3 .Year .title')[0].innerHTML= y +" 年";
	$('.select_4 .Year .title')[0].innerHTML= y +" 年";
	$('.select_1 .Week .title')[0].innerHTML= $('.select_1 .Week ul li')[0].innerHTML;
	$('.select_2 .Week .title')[0].innerHTML= $('.select_2 .Week ul li')[0].innerHTML;
	$('.select_3 .Week .title')[0].innerHTML= $('.select_3 .Week ul li')[0].innerHTML;
	$('.select_4 .Week .title')[0].innerHTML= $('.select_4 .Week ul li')[0].innerHTML;
	var yue=new Date().getMonth();
	if(yue.toString().length==1&&yue!==9){
		var m=new Date().getMonth() + 1 ;
		$('.select_1 .Month .title')[0].innerHTML= "0"+ m +" 月";
		$('.select_2 .Month .title')[0].innerHTML= "0"+ m +" 月";
		$('.select_3 .Month .title')[0].innerHTML= "0"+ m +" 月";
		$('.select_4 .Month .title')[0].innerHTML= "0"+ m +" 月";
	}else{
		var m=new Date().getMonth() + 1 ;
		$('.select_1 .Month .title')[0].innerHTML= m +" 月";
		$('.select_2 .Month .title')[0].innerHTML= m +" 月";
		$('.select_3 .Month .title')[0].innerHTML= m +" 月";
		$('.select_4 .Month .title')[0].innerHTML= m +" 月";
	}
	var d=new Date().getDate();
	var weekDay = ["星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"];//0~6(0代表星期天)
	var dt = new Date(new Date().getFullYear(), new Date().getMonth(),new Date().getDate());
	var week_day=weekDay[dt.getDay()];
	if(d.toString().length==1){
		$('.select_1 .Day .title')[0].innerHTML= "0"+ new Date().getDate()+"日"+" "+"<label>"+week_day+"</label>";
		$('.select_4 .Day .title')[0].innerHTML= "0"+ new Date().getDate()+"日"+" "+"<label>"+week_day+"</label>";
	}else{
		$('.select_1 .Day .title')[0].innerHTML= new Date().getDate()+"日"+" "+"<label>"+week_day+"</label>";
		$('.select_4 .Day .title')[0].innerHTML= new Date().getDate()+"日"+" "+"<label>"+week_day+"</label>";
	}
	//赋日期下拉框
	var n = MonHead[new Date().getMonth()];//判断这个月有多少天
	if (new Date().getMonth() ==1 && IsPinYear(YYYYvalue)) n++;
	writeDay(n);
}
if(document.attachEvent)
	window.attachEvent("onload", YYYYMMDDstart);
else
	window.addEventListener('load', YYYYMMDDstart, false);
function YYYYDD(obj) //年发生变化时日期发生变化(主要是判断闰平年)
{
	// var str= $('.Year .title')[0].innerHTML.replace(/[^0-9]/g, '');
	var str= $(obj).parent().children('.title')[0].innerHTML.replace(/[^0-9]/g, '');
	var MMvalue =$(obj).parent().parent().children('.Month').children('.title')[0].innerHTML.replace(/[^0-9]/g, '');
	var n = MonHead[MMvalue - 1];
	if (MMvalue ==2 && IsPinYear(str)) n++;
	$(obj).parent().parent().children('.Day').children('ul').empty();
	$(obj).parent().parent().children('.Week').children('ul').empty();
	writeDay(n)
	$(obj).parent().parent().children('.Day').children('.title')[0].innerHTML=$(obj).parent().parent().children('.Day').children('ul').children('li')[0].innerHTML;
	count=0;
	for(var i=1;i<13;i++){
		getInfo(str,i);
	}
	$(obj).parent().parent().children('.Week').children('.title')[0].innerHTML=$(obj).parent().parent().children('.Week').children('ul').children('li')[0].innerHTML;
}
function MMDD(obj)//月发生变化时日期联动
{
	var str=$(obj).parent().children('.title')[0].innerHTML.replace(/[^0-9]/g, '');
	var YYYYvalue =$(obj).parent().parent().children('.Year').children('.title')[0].innerHTML.replace(/[^0-9]/g, '');
	var n = MonHead[str - 1];
	if (str ==2 && IsPinYear(YYYYvalue)) n++;
	$(obj).parent().parent().children('.Day').children('ul').empty();
	writeDay(n)
	$(obj).parent().parent().children('.Day').children('.title')[0].innerHTML=$(obj).parent().parent().children('.Day').children('ul').children('li')[0].innerHTML;
}
function writeDay(n)//据条件写日期的下拉框
{
	var year = $('.Year .title')[0].innerHTML.replace(/[^0-9]/g, ''), month =$('.Month .title')[0].innerHTML.replace(/[^0-9]/g, '')-1;
	var weekDay = ["星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"];//0~6(0代表星期天)
	for (var i=0; i<n; i++){
		var date = i+1;
		var dt = new Date(year, month, date);
		var week_day=weekDay[dt.getDay()];
		var _i=i+1;
		if(_i.toString().length==1){
			$('.Day ul').append('<li>'+" 0"+ _i +"日"+" "+'<label>'+week_day+'</label></li>');
		}else{
			$('.Day ul').append('<li>'+" "+ _i +"日"+" "+'<label>'+week_day+'</label></li>');
		}
	}
}
function IsPinYear(year)//判断是否闰平年
{
	return(0 == year%4 && (year%100 !=0 || year%400 == 0));
}
function CDT(obj)//选择删选条件（按日、按周、按月、按年）
{
	var select_div=$(obj).children('span');
	for(var i=0;i<select_div.length;i++){
		select_div[i].onclick=function(){
			$(this).addClass('select');
			$(this).siblings().removeClass('select');
			$(this).siblings().children('ul').css('display','none');
			$(this).siblings().removeClass('cur')
			$(obj).children('.select').children('ul').slideToggle();
			$(this).toggleClass('cur');
			var select_li=$(obj).children('.select').children('ul').children('li');
			select_li.on('click',function(){
				$(this).addClass('selected');
				$(this).siblings().removeClass('selected');
				var value=$(this).html();
				$(obj).children('.select').children('.title').html(value);
			});
		}
	}
	var CDvalue=$(obj).children('.choose').children('.title')[0].innerText;
	if(CDvalue=="按日查看"){
		$(obj).children('.Month').show();
		$(obj).children('.Day').show();
		$(obj).children('.Week').hide();
	}else if(CDvalue=="按周查看"){
		$(obj).children('.Day').hide();
		$(obj).children('.Month').hide();
		$(obj).children('.Week').show();
	}else if(CDvalue=="按月查看"){
		$(obj).children('.Day').hide();
	    $(obj).children('.Month').show();
	    $(obj).children('.Week').hide();
	}else if(CDvalue=="按年查看"){
		$(obj).children('.Day').hide();
		$(obj).children('.Month').hide();
		$(obj).children('.Week').hide();
	}
	$(obj).children('span').on('click',function(){
		
		var _index=$(this).children('div').html();
		var li_index=$(this).children('ul').children('li');
		for(var i=0;i<li_index.length;i++){
			if(li_index[i].innerText==_index){
				$(li_index[i]).addClass('selected');
			}
		}
	});
}
//获取一年有多少周
var count=0;
function first_month(year,month){
	var d = new Date();
	// what day is first day
	d.setFullYear(year, month-1, 1);
	var w1 = d.getDay();
	if (w1 == 0) w1 = 7;
	// total day of month
	d.setFullYear(year, month, 0);
	var dd = d.getDate();
	// first Monday
	if (w1 != 1) d1 = 7 - w1 + 2;
	else d1 = 1;
	week_count = Math.ceil((dd-d1+1)/7);
	return week_count;
}
function getInfo(year, month) {
	var d = new Date();
	// what day is first day
	d.setFullYear(year, month-1, 1);
	var w1 = d.getDay();
	if (w1 == 0) w1 = 7;
	// total day of month
	d.setFullYear(year, month, 0);
	var dd = d.getDate();
	// first Monday
	if (w1 != 1) d1 = 7 - w1 + 2;
	else d1 = 1;
	week_count = Math.ceil((dd-d1+1)/7);
	count+=week_count;
	month = month < 10 ? "0" + month : month;
	// console.log(year + "年" + month + "月有" + week_count +"周<br/>");
	var num=first_month(year, 1);
	for (var i = 0,j=count-num; i < week_count; i++) {
		var monday = d1+i*7;
		var sunday = monday + 6;
		monday = monday < 10 ? "0" + monday : monday;
		var from =month+"-"+monday;
		var to;
		if (sunday <= dd) {
			sunday = sunday < 10 ? "0" + sunday : sunday;
			to =month+"-"+sunday;
		} else {
			d.setFullYear(year, month-1, sunday);
			var m=d.getMonth()+1;
			var day=d.getDate();
			m = m < 10 ? "0" + m : m;
			day = day < 10 ? "0" + day : day;
			to =m+"-"+day;
		}
		// console.log( '第'+(i+1+j)+ '周 ' + from + "至" + to);
		$('.Week ul').append('<li>'+ '第'+ (i+1+j) + '周' + '<label>' + from + "至" + to + '</label>' + '</li>');
	}
}
//业绩看板曲线图
//var circles = [];
//var selectedCircle;//选中的圆
//var hoveredCircle;//滑过的圆
//var inint_num=$('.customer_add_cart').width()/10;
//var mul_num=$('.customer_add_cart').width()/7;
//var canvas =document.getElementById('canvas_circle');//画布
//var b =canvas.getContext("2d");//画笔
////转换成纵坐标
//function trans(degree){
//    return 60+(40-degree)*10;
//}
////圆对象
//function Circle(x, y, radius){
//    this.x = x;
//    this.y = y;
//    this.radius = radius;
//}
////画提示框
//function drawScene(perArr) {
//    for (var i=0; i<circles.length; i++) {
//        if(hoveredCircle == i){
//            $('#tip_note').css({'position':'absolute','left':inint_num+mul_num*i-35,'top':trans(perArr[i]+5)+80,'display':'block'});
//            $('.border').css({'left':26,'top':0});
//            $('#tip_note label').text(perArr[i]);
//        }
//
//    }
//}
//// 简单版绘制图表
//function drawChart(canvasId,perArr, dateArr) {
//    var inint_num=$('.customer_add_cart').width()/12;
//    var mul_num=$('.customer_add_cart').width()/7;
//    var init_height=$('.customer_add_cart').height();
//    var end_num=$('.customer_add_cart').width()-20;
//    var pi2 = Math.PI*2;
//    //获取canva的dom对象
//    var canvas =document.getElementById(canvasId);//画布
//    //获取画布的上下文：使用画布的getContext方法获取
//    var c =canvas.getContext("2d");//画笔
//    //绘制横线
//    c.moveTo(20,180); //起始点坐标
//    c.lineTo(end_num,180);//终点坐标
//    c.strokeStyle = '#a3adbc';
//    c.stroke();
//    //绘制文字
//    c.font ="12px Times New Roman";
//    c.fillStyle="rgba(255,255,255,0.5)";
//    c.textAlign = "center";
//    for(var i=0; i< 7; i++){
//        c.fillText(dateArr[i], inint_num+mul_num*i,init_height*0.64 );
//    }
//    //绘制曲线图
//    c.beginPath();
//    c.moveTo(inint_num, trans(perArr[0]));
//    for(var i=1; i< 7; i++){
//        c.lineTo(inint_num+mul_num*i, trans(perArr[i]));
//    }
//    c.strokeStyle="#6cc1c8";
//    c.stroke();
//    //顶点的阴影部分
//    c.fillStyle="rgba(108,193,200,0.3)";
//    c.beginPath();
//    for(var i=0; i< 7; i++){
//        c.moveTo(inint_num+mul_num*i, trans(perArr[i]));
//        c.arc(inint_num+mul_num*i,trans(perArr[i]),6,0,pi2);
//        circles.push(new Circle(inint_num+mul_num*i, trans(perArr[i]),5));
//    }
//    c.fill();
//    //绘制点
//    c.fillStyle = "#6cc1c8";
//    c.beginPath();
//    for(var i=0; i< 7; i++){
//        c.moveTo(inint_num+mul_num*i, trans(perArr[i]));
//        c.arc(inint_num+mul_num*i, trans(perArr[i]),2, 0, pi2);
//    }
//    c.fill();
//}
//function init(perArr) {
//        var myCanvas =document.getElementById('canvas_circle');
//        var avail_width=$('.customer_add_cart').width();
//        var avail_height=$('.customer_add_cart').height()*0.7;
//        myCanvas.setAttribute("width", avail_width);
//        myCanvas.setAttribute("height", avail_height);
//
//        var dateArr = ["01-04", "01-05", "01-06", "01-07", "01-08", "01-09", "01-10"];//日期
//        drawChart("canvas_circle", perArr, dateArr);
//
//        //鼠标移动
//        myCanvas.onmousemove=function(e) {
//
//           var e = window.event || e
//
//           var rect = this.getBoundingClientRect();
//
//           var mouseX =e.clientX - rect.left;//获取鼠标在canvsa中的坐标
//
//           var mouseY =e.clientY - rect.top;
//
//            if (selectedCircle != undefined) {
//
//                var radius = circles[selectedCircle].radius;
//
//                circles[selectedCircle] = new Circle(mouseX, mouseY,radius); //改变选中圆的位置
//
//            }
//
//            hoveredCircle = undefined;
//
//            for (var i=0; i<circles.length; i++) { // 检查每一个圆，看鼠标是否滑过
//
//                var circleX = circles[i].x;
//
//                var circleY = circles[i].y;
//
//                var radius = circles[i].radius;
//
//                if (Math.pow(mouseX-circleX,2) + Math.pow(mouseY-circleY,2) < Math.pow(radius,2)) {
//
//                    hoveredCircle = i;
//                    drawScene(perArr);
//                    break;
//
//                }else{
//                	$('#tip_note').css('display','none');
//                }
//
//            }
//
//        }
//}

//转换成纵坐标
var circles = [];
var selectedCircle;//选中的圆
var hoveredCircle;//滑过的圆
var inint_num=$('.customer_add_cart').width()/15;
var mul_num=$('.customer_add_cart').width()/7;
var canvas =document.getElementById('canvas_circle');//画布
var b =canvas.getContext("2d");//画笔
//定义关机位置开始
var origin={
	x:20,
	y:180
};
var axisY={
	x:20,
	y:0
};
var  popY=null;
var  max=null;
//定义关机位置结束
function trans(degree){
	return origin.y- (origin.y- (axisY.y + popY))*degree/max;
	//return 60+(40-degree)*10;
	//console.log(origin.y- (origin.y- (axisY.y + popY))*degree/max);
}
//圆对象
function Circle(x, y, radius){
	this.x = x;
	this.y = y;
	this.radius = radius;
}
//画提示框
function drawScene(perArr) {
	for (var i=0; i<circles.length; i++) {
		if(hoveredCircle == i){
			$('#tip_note').css({'position':'absolute','left':inint_num+mul_num*i-21,'top':trans(perArr[i])+35,'display':'block','z-index':'100'});
			$('.border').css({'left':26,'top':0});
			$('#tip_note label').text(perArr[i]);
		}

	}
}
//简单版绘制图表
function drawChart(canvasId,perArr, dateArr) {
	var init_height=$('.customer_add_cart').height();
	var end_num=$('.customer_add_cart').width()-20;
	var pi2 = Math.PI*2;
	var dataLen=dateArr.length;
	max = Math.max.apply(Math,perArr);
	popY = (origin.y - axisY.y)/(max/(max/10)+1);

	//获取canva的dom对象
	var canvas =document.getElementById(canvasId);//画布
	//获取画布的上下文：使用画布的getContext方法获取
	var c =canvas.getContext("2d");//画笔
	//绘制横线
	//c.moveTo(20,0); //起始点坐标
	c.moveTo(20,180); //起始点坐标
	c.lineTo(end_num,180);//终点坐标
	c.strokeStyle = '#a3adbc';
	c.stroke();
	//绘制文字
	c.font ="12px Times New Roman";
	c.fillStyle="rgba(255,255,255,0.5)";
	c.textAlign = "center";
	for(var i=0; i< dataLen; i++){
		if(dataLen>7){
			mul_num=$('.customer_add_cart').width()/(dataLen+1);
		}else {
			mul_num=$('.customer_add_cart').width()/dataLen;
		}

		c.fillText(dateArr[i], inint_num+mul_num*i,init_height*0.64);
	}
	//绘制曲线图
	c.beginPath();
	c.moveTo(inint_num, trans(perArr[0]));
	for(var i=1; i< dataLen; i++){
		c.lineTo(inint_num+mul_num*i, trans(perArr[i]));
	}
	c.strokeStyle="#6cc1c8";
	c.stroke();
	//顶点的阴影部分
	c.fillStyle="rgba(108,193,200,0.3)";
	c.beginPath();
	for(var i=0; i< dataLen; i++){
		c.moveTo(inint_num+mul_num*i, trans(perArr[i]));
		c.arc(inint_num+mul_num*i,trans(perArr[i]),6,0,pi2);
		circles.push(new Circle(inint_num+mul_num*i, trans(perArr[i]),5));
	}
	c.fill();
	//绘制点
	c.fillStyle = "#6cc1c8";
	c.beginPath();
	for(var i=0; i< dataLen; i++){
		//if(dataLen>12){
		//	inint_num=$('.customer_add_cart').width()/15;
		//	mul_num=$('.customer_add_cart').width()/7;
		//}
		c.moveTo(inint_num+mul_num*i, trans(perArr[i]));
		c.arc(inint_num+mul_num*i, trans(perArr[i]),2, 0, pi2);
	}
	c.fill();
}
function init(perArr,dateArr) {
	if(dateArr.length>12){
		inint_num=$('.customer_add_cart').width()/(dateArr.length);
		mul_num=$('.customer_add_cart').width()/(dateArr.length);
	}else {
		inint_num=$('.customer_add_cart').width()/15;
		mul_num=$('.customer_add_cart').width()/7;
	}
	var myCanvas =document.getElementById('canvas_circle');
	var avail_width=$('.customer_add_cart').width();
	var avail_height=$('.customer_add_cart').height()*0.8;
	circles = [];
	myCanvas.setAttribute("width", avail_width);
	myCanvas.setAttribute("height", avail_height);
	//var dateArr = ["01-04", "01-05", "01-06", "01-07", "01-08", "01-09", "01-10"];//日期
	drawChart("canvas_circle", perArr, dateArr);

	//鼠标移动
	myCanvas.onmousemove=function(e) {

		var e = window.event || e;

		var rect = this.getBoundingClientRect();

		var mouseX =e.clientX - rect.left;//获取鼠标在canvsa中的坐标

		var mouseY =e.clientY - rect.top;

		if (selectedCircle != undefined) {

			var radius = circles[selectedCircle].radius;

			circles[selectedCircle] = new Circle(mouseX, mouseY,radius); //改变选中圆的位置

		}

		hoveredCircle = undefined;

		for (var i=0; i<circles.length; i++) { // 检查每一个圆，看鼠标是否滑过

			var circleX = circles[i].x;

			var circleY = circles[i].y;

			var radius = circles[i].radius;

			if (Math.pow(mouseX-circleX,2) + Math.pow(mouseY-circleY,2) < Math.pow(radius,2)) {

				hoveredCircle = i;
				drawScene(perArr);
				break;

			}else{
				$('#tip_note').css('display','none');
			}

		}

	}

}


// 业绩看板业绩趋势和能力分析切换
$('.cart_tab ul li label').bind('click',function(){
	$(this).parent().addClass('select').siblings().removeClass('select');
	if($(this).text()==$('.cart_tab ul li:nth-of-type(2) label').text()){
		$(this).parent().parent().parent('.cart_tab').parent().children('.charts_holder').hide();
		$(this).parent().parent().parent('.cart_tab').parent().children('.radar_holder').show();
	}else if($(this).text()==$('.cart_tab ul li:nth-of-type(1) label').text()){
		$(this).parent().parent().parent('.cart_tab').parent().children('.charts_holder').show();
		$(this).parent().parent().parent('.cart_tab').parent().children('.radar_holder').hide();
	}
});
//雷达图
var radarChartData = {
	labels : ["成交笔数","客单价","新增会员","平均折扣","连带率","销售件数"],
	labels2 : ["4","￥5279","5","0.9","1.75","7"],
	title:["销售能力"],
	datasets : [
		{
			fillColor : "rgba(255,255,220,0.5)",
			strokeColor : "rgba(255,255,255,0.1)",
			pointColor : "rgba(220,220,220,1)",
			pointStrokeColor : "#fff",
			data : [70,55,58,61,81,55]
		}
	]
}
var radarChartData2 = {
	labels : ["新入会员","首单均价","销售额","销售占比"],
	labels2 : ["3","2712","6516","0.25"],
	title:["发展会员"],
	datasets : [
		{
			fillColor : "rgba(255,255,220,0.5)",
			strokeColor : "rgba(255,255,255,0.1)",
			pointColor : "rgba(220,220,220,1)",
			pointStrokeColor : "#fff",
			data : [70,50,80,60]
		}
	]
}
var radarChartData3 = {
	labels : ["金额占比","件数占比","回头率","销售金额","销售金额","消费件数"],
	labels2 : ["4","￥5279","0","0.9","1.75","7"],
	title:['维护会员'],
	datasets : [
		{
			fillColor : "rgba(255,255,220,0.5)",
			strokeColor : "rgba(255,255,255,0.1)",
			pointColor : "rgba(220,220,220,1)",
			pointStrokeColor : "#fff",
			data :  [68,68,78,68,81,55]
		}
	]
}
var myRadar = new Chart(document.getElementById("canvas").getContext("2d")).Radar(radarChartData,{scaleShowLabels : false, pointLabelFontSize : 10});
var myRadar2 = new Chart(document.getElementById("canvas2").getContext("2d")).Radar(radarChartData2,{scaleShowLabels : false, pointLabelFontSize : 10});
var myRadar3 = new Chart(document.getElementById("canvas3").getContext("2d")).Radar(radarChartData3,{scaleShowLabels : false, pointLabelFontSize : 10});
