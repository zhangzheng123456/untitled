/**
 * JavaScript CheckboxSelect v0.1
 * Copyright (c) 2012 fly2004jun
 * Blog: http://blog.csdn.net/fly2004jun
 * Date: 2012-07-02
 * Licensed under the MIT：http://www.opensource.org/licenses/mit-license.php
 * 转载请保留原始版权信息
 *
 * 描述：模仿select，对下拉选项添加Checkbox框。
 * new CheckboxSelect({
 *    input         HTMLInputElement 必选,需要绑定的input
 *    hiddeninput   HTMLInputElement 可选,需要接收所勾选项id的input，为通过AJAX获取键值对准备 v0.2实现
 *    data          Array ['北京','上海','安徽','吉林'] 必选
 *    containerCls  容器className 可选,默认值checkboxselect-container
 *    itemCls       容器子项className 可选,默认值checkboxselect-item
 *    activeCls     高亮子项className 可选,默认值checkboxselect-active
 *    width         宽度设置 此项将覆盖containerCls的width 可选
 *    opacity       透明度设置 此项将覆盖containerCls的opacity 可选
 *    arrayUrl      通过Url获取数组格式数据,v0.2实现
 *    jsonUrl       通过Url获取JSON格式数据,v0.2实现
 * });
 */

Array.prototype.indexOf = function(val) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] == val) return i;
    }
    return -1;
};
Array.prototype.remove = function(val) {
    var index = this.indexOf(val);
    if (index > -1) {
        this.splice(index, 1);
    }
};

 /**
 *
 * CheckboxSelect构造类
 */
function CheckboxSelect(opt){
    this.win = null;
    this.doc = null;
    this.container = null;
    this.items = null;
    this.input = opt.input || null;
    this.hiddeninput = opt.hiddeninput || null;
    this.containerCls = opt.containerCls || 'checkboxselect-container';
    this.itemCls = opt.itemCls || 'checkboxselect-item';
    this.activeCls = opt.activeCls || 'checkboxselect-active';
    this.width = opt.width;
    this.opacity = opt.opacity;
    this.data = opt.data || [];
    this.code=opt.code || [];
    this.checkboxdata = [];  //数组保存选中项的值
    this.arrayUrl = opt.arrayUrl || null;
    this.jsonUrl = opt.jsonUrl || null;
    this.active = null;
    this.visible = false;
    this.init();
}

CheckboxSelect.prototype = {
    init: function(){
        this.win = window;
        this.doc = window.document;
        //构造一个DIV元素
        this.container = this.$C('div');

        //设置div的样式
        this.attr(this.container, 'class', this.containerCls);
        //将div附在DOM文档中
        this.doc.body.appendChild(this.container);
        //设置div属性
        this.setPos();
        var _this = this, input = this.input,container = this.container;
        this.on(input,'click',function(e){
            // _this.onClick(e);
        });
        $("#OWN_BRAND").parent().append(this.container);
        // this.onMouseover();
        // this.onMousedown();
        // blur会在click前发生，元素失去焦点
        this.on(container,'blur',function(e){
            _this.hide();
        });
    },
    $C: function(tag){
        return this.doc.createElement(tag);
    },
    getPos: function (el){
        var pos=[0,0], a=el;
        //获取页面元素的位置
        if(el.getBoundingClientRect){
            //左、上、右、下
            var box = el.getBoundingClientRect();
            pos=[box.left,box.top];
        }else{
            //遍历根元素
            while(a && a.offsetParent){
                pos[0] += a.offsetLeft;
                pos[1] += a.offsetTop;
                a = a.offsetParent
            }
        }
        return pos;
    },
    setPos: function(){
        var input = this.input,
            //获得当前input元素所在位置坐标。
            pos = this.getPos(input),
            //获得浏览器类型
            brow = this.brow,
            //获得宽度
            width = this.width,
            //获得透明度
            opacity = this.opacity,
            //获得新增的div元素
            container = this.container;
            //直接付给他CSS样式省去了定义CSS的步骤
            container.id="brand_data";
            container.style.cssText =
                'display:none;position: absolute;margin-left:78px;overflow:hidden;border: 1px solid #a9c9e2;margin-top:2px;background:#e8f5fe;width:'
                // IE6/7/8/9/Chrome/Safari input[type=text] border默认为2，Firefox为1，因此取offsetWidth-2保证与FF一致
                + (brow.firefox ? input.clientWidth : input.offsetWidth-2) + 'px;height:146px;overflow-y: scroll;';
        if(width){
            container.style.width = width + 'px';
        }
        if(opacity){
            if(this.brow.ie){
                container.style.filter = 'Alpha(Opacity=' + opacity * 100 + ');';
            }else{
                container.style.opacity = (opacity == 1 ? '' : '' + opacity);
            }
        }
    },
    show: function(){
        this.container.style.visibility = 'visible';
        this.visible = true;
    },
    hide: function(){
        this.container.style.visibility = 'hidden';
        this.visible = false;
    },
    attr: function(el, name, val){
        if(val === undefined){
            return el.getAttribute(name);
        }else{
            el.setAttribute(name,val);
            name=='class' && (el.className = val);
        }
    },
    on: function(el, type, fn){
        el.addEventListener ? el.addEventListener(type, fn, false) : el.attachEvent('on' + type, fn);
    },
    un: function(el, type, fn){
        el.removeEventListener ? el.removeEventListener(type, fn, false) : el.detachEvent('on' + type, fn);
    },
    brow: function(ua){
        return {
            ie: /msie/.test(ua) && !/opera/.test(ua),
            opera: /opera/.test(ua),
            firefox: /firefox/.test(ua)
        };
    }(navigator.userAgent.toLowerCase()),
    createXMLHttpRequest: function(){
        if (window.ActiveXObject) {
            return  new ActiveXObject("Microsoft.XMLHTTP");
        }
        else if (window.XMLHttpRequest) {
            return new XMLHttpRequest();
        }
    }
    // onClick:function(e){
    //     var container = this.container, input = this.input, iCls = this.itemCls, aCls = this.activeCls,checkboxdata = this.checkboxdata ,hiddeninput = this.hiddeninput;
    //     this.win = window;
    //     this.doc = window.document;
    //     this.items = [];
    //     if(this.data.length>0){
    //         this.container.innerHTML = '';
    //         //循环增加子项并设置样式
    //         for(var i=0,len=this.data.length;i<len;i++){
    //             var item = this.$C('div');
    //             this.attr(item, 'class', this.itemCls);
    //             var tempinput = this.$C('input');
    //             tempinput.type = 'checkbox';
    //             tempinput.value = this.data[i];
    //             tempinput.style='appearance:checkbox;-webkit-appearance:checkbox;width:14px;height:14px;-moz-appearance:checkbox;-ms-appearance:checkbox;';
    //             this.on(tempinput,'click',function(e){
    //                 var target = e.target || e.srcElement;
    //                 if(target.checked){
    //                     checkboxdata.push(target.value);
    //                     input.value = checkboxdata.toString();
    //                     if(hiddeninput)
    //                         hiddeninput.value = checkboxdata.toString();
    //                 }else{
    //                     if(checkboxdata.length>0)
    //                         checkboxdata.remove(target.value)
    //                     input.value = checkboxdata.toString();
    //                     if(hiddeninput)
    //                         hiddeninput.value = checkboxdata.toString();
    //                 }
    //             });
    //             //判断是否有初始选中
    //             if(checkboxdata.length>0){
    //                 for(var t=0;t<checkboxdata.length;t++){
    //                     if(checkboxdata[t] === this.data[i])
    //                         tempinput.checked = true;
    //                 }
    //             }
    //             item.appendChild(tempinput);
    //             item.appendChild(this.doc.createTextNode(this.data[i]))
    //             this.items[i] = item;
    //             this.container.appendChild(item);
    //         }
    //         this.show();
    //     }
    // },
    // onMouseover: function(){
    //     var _this = this, icls = this.itemCls, acls = this.activeCls;
    //     this.on(this.container,'mouseover',function(e){
    //         var target = e.target || e.srcElement;
    //         if(target.className == icls){
    //             if(_this.active){
    //                 _this.active.className = icls;
    //             }
    //             target.className = acls;
    //             _this.active = target;

    //         }
    //     });
    // },
    // onMousedown: function(){
    //     var _this = this ;
    //     var container = this.container, input = this.input, iCls = this.itemCls, aCls = this.activeCls,checkboxdata = this.checkboxdata ,hiddeninput = this.hiddeninput;
    //     this.on(this.container,'mousedown',function(e){
    //         var target = e.target || e.srcElement;
    //         //首先判断target是何种类型
    //         if(target.type === 'checkbox'){
    //         }else{
    //             if(container.childNodes.length>0){
    //                 if(target.childNodes[0].checked){
    //                     //如果第一个节点Checkbox是选中状态，则取消选中
    //                     target.childNodes[0].checked = false;
    //                     if(checkboxdata.length>0)
    //                     checkboxdata.remove(target.childNodes[1].nodeValue)
    //                     input.value = checkboxdata.toString();
    //                     if(hiddeninput)
    //                         hiddeninput.value = checkboxdata.toString();
    //                 }
    //                 else{
    //                     //如果第一个节点Checkbox是未选中状态，则选中
    //                     target.childNodes[0].checked = true;
    //                     checkboxdata.push(target.childNodes[1].nodeValue);
    //                     input.value = checkboxdata.toString();
    //                     if(hiddeninput)
    //                         hiddeninput.value = checkboxdata.toString();
    //                 }
    //             }
    //         }
    //     });
    // }
}
