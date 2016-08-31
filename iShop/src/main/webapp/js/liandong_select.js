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
	var a=null;
	if(max==0){
		a=origin.y;
	}else{
		a= origin.y- (origin.y- (axisY.y + popY))*degree/max;
	}
	return a;
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
		if(7<dataLen&&dataLen<13){
			mul_num=$('.customer_add_cart').width()/(dataLen);
			inint_num=$('.customer_add_cart').width()/23;
			c.fillText(dateArr[i], inint_num+mul_num*i,init_height*0.64);
		}else if(12<dataLen){
			mul_num=$('.customer_add_cart').width()/(dataLen+1);
			inint_num=$('.customer_add_cart').width()/dataLen;
			if(i%2==0){
				c.fillText(dateArr[i], inint_num+mul_num*i,init_height*0.64);
			}
		}else {
			mul_num=$('.customer_add_cart').width()/7;
			inint_num=$('.customer_add_cart').width()/15;
			c.fillText(dateArr[i], inint_num+mul_num*i,init_height*0.64);
		}
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
		c.moveTo(inint_num+mul_num*i, trans(perArr[i]));
		c.arc(inint_num+mul_num*i, trans(perArr[i]),2, 0, pi2);
	}
	c.fill();
}
function init(perArr,dateArr) {
	var myCanvas =document.getElementById('canvas_circle');
	var avail_width=$('.customer_add_cart').width();
	var avail_height=$('.customer_add_cart').height()*0.8;
	circles = [];
	myCanvas.setAttribute("width", avail_width);
	myCanvas.setAttribute("height", avail_height);
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
