var whir = window.whir || {};  
whir.loading ={  
    add: function (title, opacity,src) {
        opacity = opacity == undefined ? 0.4 : opacity;  
        var arr = this.getPageSize();
        var width = parseInt(arr[2]);  
        var height = parseInt(arr[3]);
        var R=this.remove1;
        var L=this.prev;
        var r=this.next;
        //var loadingImage = _basepath + "Admin/Scripts/jquery-easyui-1.4/themes/default/images/loading.gif";  
        var loadingImage = "http://img.lanrentuku.com/img/allimg/1212/5-121204193943.gif";  
  
        //背景遮罩  
        var mask = document.createElement("div");  
        mask.id = 'mask';  
        mask.style.position = 'fixed';  
        mask.style.left = '0';
        mask.style.top = '0';
        mask.style.width = '100%';  
        mask.style.height = parseInt(arr[1]) + "px";  
        mask.style.background = "gray";
        mask.style.opacity=opacity;
        mask.style.filter = "alpha(opacity=" + opacity * 100 + ")";  
        mask.style.zIndex = "10000";  
        mask.addEventListener('touchstart', function (e) { e.preventDefault(); }, false);   //触摸事件  
        mask.addEventListener('touchmove', function (e) { e.preventDefault(); }, false);    //滑动事件  
        mask.addEventListener('touchend', function (e) { e.preventDefault(); }, false);         //离开元素事件
        if(src!==undefined){
            mask.style.background = "#000";
        }
        document.body.appendChild(mask);
        if(src===undefined){
            //提示文本
            var loading = document.createElement("div");
            loading.id = 'loading';
            loading.style.position = 'fixed';
            loading.style.left = ((width / 2) - 75) + "px";
            loading.style.top = (height / 2-20) + "px";
            loading.style.width = '130px';
            loading.style.height = "50px";
            loading.style.lineHeight = "50px";
            loading.style.display = "inline-block";
            loading.style.padding = "0px 5px 0 5px";
            loading.style.fontSize = " 12px";
            loading.style.fontFamily = " initial";
            loading.style.zIndex = "100001";
            // loading.style.background = "rgba(0,0,0,0.5) url(" + loadingImage + ") no-repeat scroll 5px 5px";
            loading.style.backgroundColor="rgba(0,0,0,0.5)";
            loading.style.borderRadius="8px";
            loading.style.textAlign="center";
            loading.style.color = "#fff";
            if(title != undefined && title.length > 0){
                if(title == "mask"){
                    title = "" ;
                }else {
                    title =title ;
                    loading.innerHTML = title;
                    document.body.appendChild(loading);
                }
            }else {
                title = "加载中，请稍候...";
                loading.innerHTML = title;
                document.body.appendChild(loading);
            }
            // title = (title != undefined && title.length > 0) ? title : "加载中，请稍候...";
        }else {
            //显示图片
            var imgBox = document.createElement("div");
            var div = document.createElement("img");
            div.id = 'div';
            imgBox.id = "imgBox"
            div.style.maxWidth = "600px";
            div.style.maxHeight = "800px"
            imgBox.style.maxHeight="800px";
            imgBox.style.maxWidth="600px";
            imgBox.style.width = "auto";
            imgBox.style.position = 'absolute';
            imgBox.style.left = "55%";
            imgBox.style.top = "65%";
            imgBox.style.display = "inline-block";
            imgBox.style.zIndex = "100001";
            div.style.transform="translate(-50%,-40%)";
            div.style.msTransform="translate(-50%,-40%)";
            div.style.mozTransform="translate(-50%,-40%)";
            div.style.otransform="translate(-50%,-40%)";
            div.style.webkitTransform="translate(-50%,-40%)";
            div.setAttribute('src',src);
            document.body.appendChild(imgBox);
            imgBox.appendChild(div);
            mask.addEventListener('click', function () {R()}, false); //点击事件
            imgBox.addEventListener('click', function () {R()}, false); //点击事件
            var left = document.createElement("span");
            left.id = 'left';
            left.className = "icon-ishop_8-02";
            left.style.transform="rotate(90deg)";
            left.style.webkitTransform="rotate(90deg)";
            left.style.mozTransform="rotate(90deg)";
            left.style.width="50px";
            left.style.height="50px";
            left.style.color = "#fff";
            left.style.cursor = "pointer";
            left.style.lineHeight = "50px";
            left.style.textAlign = "center";
            left.style.display = "block";
            left.style.fontSize = "35px";
            left.style.borderRadius = "30px";
            left.style.position="absolute";
            left.style.left = "20%";
            left.style.top = "65%";
            left.style.background="rgba(255,255,255,0.15)";
            left.style.zIndex = "100001";
            left.onmouseover = function () {
                left.style.background = "rgba(255,255,255,0.8)";
                left.style.color = "#6dc1c8";
            };
            left.onmouseout = function () {
                left.style.background="rgba(255,255,255,0.15)";
                left.style.color = "#fff";
            };
            document.body.appendChild(left);
            left.addEventListener('click', function () {L()}, false); //点击事件
            var right = document.createElement("span");
            right.id = 'right';
            right.className = "icon-ishop_8-03 ";
            right.style.width="50px";
            right.style.height="50px";
            right.style.color = "#fff";
            right.style.cursor = "pointer";
            right.style.lineHeight = "50px";
            right.style.fontSize = "35px";
            right.style.textAlign = "center";
            right.style.borderRadius = "30px";
            right.style.display = "block";
            right.style.position="absolute";
            right.style.right = "10%";
            right.style.top = "65%";
            right.style.background="rgba(255,255,255,0.15)";
            right.style.zIndex = "100001";
            document.body.appendChild(right);
            right.onmouseover = function () {
                right.style.background = "rgba(255,255,255,0.8)";
                right.style.color = "#6dc1c8";
            };
            right.onmouseout = function () {
                right.style.background="rgba(255,255,255,0.15)";
                right.style.color = "#fff";
            };
            right.addEventListener('click', function () {r()}, false); //点击事件
        }

    },
    remove: function (e) {
        if(e == "mask"){
            var element = document.getElementById("mask");
            element.parentNode.removeChild(element);
        }else {
            var element = document.getElementById("mask");
            element.parentNode.removeChild(element);
            element = document.getElementById("loading");
            element.parentNode.removeChild(element);
        }
    },
    remove1: function () {
        var element = document.getElementById("mask");
        element.parentNode.removeChild(element);
        element = document.getElementById("div");
        element.parentNode.removeChild(element);
        element = document.getElementById("imgBox");
        element.parentNode.removeChild(element);
        element = document.getElementById("left");
        element.parentNode.removeChild(element);
        element = document.getElementById("right");
        element.parentNode.removeChild(element);
    },
    prev: function () {
        var element = document.getElementById("div");
        var src = element.getAttribute('src');
        var i = swip_image.indexOf(src);
        if(i>0){
            i = i-1;
            src = swip_image[i];
        }
        element.setAttribute('src',src);
    },
    next: function() {
        var element = document.getElementById("div");
        var src = element.getAttribute('src');
        var i = swip_image.indexOf(src);
        if(i<swip_image.length-1){
            i = i+1;
            src = swip_image[i];
        }
        element.setAttribute('src',src);
    },
    getPageSize: function () {  
        var xScroll, yScroll;  
        if (window.innerHeight && window.scrollMaxY) {  
            xScroll = window.innerWidth + window.scrollMaxX;  
            yScroll = window.innerHeight + window.scrollMaxY;  
        } else {  
            if (document.body.scrollHeight > document.body.offsetHeight) { // all but Explorer Mac      
                xScroll = document.body.scrollWidth;  
                yScroll = document.body.scrollHeight;  
            } else { // Explorer Mac...would also work in Explorer 6 Strict, Mozilla and Safari      
                xScroll = document.body.offsetWidth;  
                yScroll = document.body.offsetHeight;  
            }  
        }  
        var windowWidth = 0;  
        var windowHeight = 0;  
        var pageHeight = 0;  
        var pageWidth = 0;  
  
        if (self.innerHeight) { // all except Explorer      
            if (document.documentElement.clientWidth) {  
                windowWidth = document.documentElement.clientWidth;  
            } else {  
                windowWidth = self.innerWidth;  
            }  
            windowHeight = self.innerHeight;  
        } else {  
            if (document.documentElement && document.documentElement.clientHeight) { // Explorer 6 Strict Mode      
                windowWidth = document.documentElement.clientWidth;  
                windowHeight = document.documentElement.clientHeight;  
            } else {  
                if (document.body) { // other Explorers      
                    windowWidth = document.body.clientWidth;  
                    windowHeight = document.body.clientHeight;  
                }  
            }  
        }  
        // for small pages with total height less then height of the viewport      
  
        if (yScroll < windowHeight) {  
            pageHeight = windowHeight;  
        } else {  
            pageHeight = yScroll;
        }  
        // for small pages with total width less then width of the viewport      
        if (xScroll < windowWidth) {
            pageWidth = xScroll;
        } else {  
            pageWidth = windowWidth;  
        }  
        var arrayPageSize = new Array(pageWidth, pageHeight, windowWidth, windowHeight);  
        return arrayPageSize;
    }  
};