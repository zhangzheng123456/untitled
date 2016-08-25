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
             y:220
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
                    $('#tip_note').css({'position':'absolute','left':inint_num+mul_num*i-21,'top':trans(perArr[i])+5,'display':'block'});
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
            c.moveTo(20,0); //起始点坐标
            c.lineTo(20,220); //起始点坐标
            c.lineTo(end_num,220);//终点坐标
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

                c.fillText(dateArr[i], inint_num+mul_num*i,init_height*0.8);
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

