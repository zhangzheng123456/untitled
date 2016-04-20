//日期联动
function YYYYMMDDstart(){
	MonHead = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];//（1，3，5，7，8，10，12月是31天，4,6,9,11是30天，2月看是不是闰年，如果是就是29天，不是就是28天。）
	var YYYYvalue="";
	//先给年下拉框赋内容
	var y  = new Date().getFullYear();
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
	// getInfo(2013,12);
	for(var i=1;i<13;i++){
		getInfo(2016,i);
	}
}
if(document.attachEvent)
	window.attachEvent("onload", YYYYMMDDstart);
else
	window.addEventListener('load', YYYYMMDDstart, false);
function YYYYDD() //年发生变化时日期发生变化(主要是判断闰平年)
{
	var str= $('.Year .title')[0].innerHTML.replace(/[^0-9]/g, '');
	var MMvalue =$('.Month .title')[0].innerHTML.replace(/[^0-9]/g, '');
	var n = MonHead[MMvalue - 1];
	if (MMvalue ==2 && IsPinYear(str)) n++;
	$('.Day ul').empty();
	writeDay(n)
	$('.Day .title')[0].innerHTML=$('.Day ul li')[0].innerHTML;
}
function MMDD()//月发生变化时日期联动
{
	var str=$('.Month .title')[0].innerHTML.replace(/[^0-9]/g, '');
	var YYYYvalue =$('.Year .title')[0].innerHTML.replace(/[^0-9]/g, '');
	var n = MonHead[str - 1];
	if (str ==2 && IsPinYear(YYYYvalue)) n++;
	$('.Day ul').empty();
	writeDay(n)
	$('.Day .title')[0].innerHTML=$('.Day ul li')[0].innerHTML;
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
//取每年有多少周
// function getInfo(year, month) {
// 	var d = new Date();
// 	// what day is first day
// 	d.setFullYear(year, month-1, 1);
// 	var w1 = d.getDay();
// 	if (w1 == 0) w1 = 7;
// 	// total day of month
// 	d.setFullYear(year, month, 0);
// 	var dd = d.getDate();
// 	// first Monday
// 	if (w1 != 1) d1 = 7 - w1 + 2;
// 	else d1 = 1;
// 	week_count = Math.ceil((dd-d1+1)/7);
// 	// console.log(year + "年" + month + "月有" + week_count +"周<br/>");
// 	for (var i = 0; i < week_count; i++) {
// 		var monday = d1+i*7;
// 		if(monday.toString().length==1){
// 			var sunday = monday + 6;
// 			var from = month+"-"+"0"+monday;
// 			var to;
// 			if (sunday <= dd) {
// 				if(sunday.toString().length==1){
// 					to = month+"-"+"0"+sunday;
// 				}else{
// 					to = month+"-"+sunday;
// 				}
// 			} else {
// 				d.setFullYear(year, month-1, sunday);
// 				if((d.getMonth()+1).toString.length==1){
// 					if(d.getDate().toString().length==1){
// 						to ="0"+(d.getMonth()+1)+"-"+"0"+d.getDate();
// 					}else{
// 						to ="0"+(d.getMonth()+1)+"-"+d.getDate();
// 					}
// 				}else{
// 					if(d.getDate().toString().length==1){
// 						to =(d.getMonth()+1)+"-"+"0"+d.getDate();
// 					}else{
// 						to =(d.getMonth()+1)+"-"+d.getDate();
// 					}
// 				}
// 			}
// 		}else{
// 			var sunday = monday + 6;
// 			var from = month+"-"+monday;
// 			var to;
// 			if (sunday <= dd) {
// 				if(sunday.toString().length==1){
// 					to = month+"-"+"0"+sunday;
// 				}else{
// 					to = month+"-"+sunday;
// 				}
// 			} else {
// 				d.setFullYear(year, month-1, sunday);
// 				if((d.getMonth()+1).toString.length==1){
// 					if(d.getDate().toString().length==1){
// 						to ="0"+(d.getMonth()+1)+"-"+"0"+d.getDate();
// 					}else{
// 						to ="0"+(d.getMonth()+1)+"-"+d.getDate();
// 					}
// 				}else{
// 					if(d.getDate().toString().length==1){
// 						to =(d.getMonth()+1)+"-"+"0"+d.getDate();
// 					}
// 					else{
// 						to =(d.getMonth()+1)+"-"+d.getDate();
// 					}
// 				}
// 			}
// 		}
// 		console.log("第"+(i+1)+"周 " + from + " 至 " + to);
// 	}
// }
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
    	// console.log(year + "年" + month + "月有" + week_count +"周<br/>");
    	for (var i = 0; i < week_count; i++) {
    		var monday = d1+i*7;
    		var sunday = monday + 6;
    		var from =month+"-"+monday;
    		var to;
    		if (sunday <= dd) {
    			to =month+"-"+sunday;
    		} else {
    			d.setFullYear(year, month-1, sunday);
    			to =(d.getMonth()+1)+"-"+d.getDate();
    		}

    		console.log("第"+(i+1)+"周 " + from + " 至 " + to);
    	}
    }
//业绩看板曲线图

//转换成纵坐标
function trans(degree){
    return 60+(40-degree)*10;
}
//简单版绘制图表
function drawChart(canvasId,perArr, dateArr) {
    var inint_num=$('.customer_add_cart').width()/12;
    var mul_num=$('.customer_add_cart').width()/7;
    var init_height=$('.customer_add_cart').height();
    var end_num=$('.customer_add_cart').width()-20;
    var pi2 = Math.PI*2;
    //获取canva的dom对象
    var canvas =document.getElementById(canvasId);//画布
    //获取画布的上下文：使用画布的getContext方法获取
    var c =canvas.getContext("2d");//画笔
    //绘制横线
    c.moveTo(20,180); //起始点坐标
    c.lineTo(end_num,180);//终点坐标
    c.strokeStyle = '#a3adbc';
    c.stroke();
    //绘制文字
    c.font ="12px Times New Roman";
    c.fillStyle="rgba(255,255,255,0.5)";
    c.textAlign = "center";
    for(var i=0; i< 7; i++){
        c.fillText(dateArr[i], inint_num+mul_num*i,init_height*0.64 );
    }
    //绘制曲线图
    c.beginPath();
    c.moveTo(inint_num, trans(perArr[0]));
    for(var i=1; i< 7; i++){
        c.lineTo(inint_num+mul_num*i, trans(perArr[i]));
    }
    c.strokeStyle="#6cc1c8";
    c.stroke();
    //顶点的阴影部分
    c.fillStyle="rgba(108,193,200,0.3)";
    c.beginPath();
    for(var i=0; i< 7; i++){
        c.moveTo(inint_num+mul_num*i, trans(perArr[i]));
        c.arc(inint_num+mul_num*i,trans(perArr[i]),6,0,pi2);
    }
    c.fill();
    //绘制点
    c.fillStyle = "#6cc1c8";
    c.beginPath();
    for(var i=0; i< 7; i++){
        c.moveTo(inint_num+mul_num*i, trans(perArr[i]));
        c.arc(inint_num+mul_num*i, trans(perArr[i]),2, 0, pi2);
    }
    c.fill();
}
function init() {
        var myCanvas =document.getElementById('canvas_circle');
        var avail_width=$('.customer_add_cart').width();
        var avail_height=$('.customer_add_cart').height()*0.7;
        myCanvas.setAttribute("width", avail_width);
        myCanvas.setAttribute("height", avail_height);
        var perArr = [30,34,31,37,35,39,45];//顶点
        var dateArr = ["01-04", "01-05", "01-06", "01-07", "01-08", "01-09", "01-10"];//日期
        drawChart("canvas_circle", perArr, dateArr);
}
window.onresize=function(){
    init();
}
window.onload = init;
