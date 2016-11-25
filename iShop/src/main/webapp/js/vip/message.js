var oc = new ObjectControl();
var vipMessage={
	cache:{//页面的全局变量
		pageNumber:1,
		pageSize:10,
		value:'',
		param:{},
		_param:{},
		list:'',
		filtrate:''
	},
	init:function(){
		this.pageSize();
	},
	getSessionStorage:function(){//获取SessionStorage的字段
		var key_val = sessionStorage.getItem("key_val");//取页面的function_code
        key_val = JSON.parse(key_val);
        return key_val;
	},
	pageSize:function(){
		var _self=this;
		$("#page_row").click(function(){
            if("block" == $("#liebiao").css("display")){  
               $("#liebiao").hide();
            }else{  
               $("#liebiao").show();
            }  
        });
        $("#liebiao li").each(function(i,v){  
            $(this).click(function(){
                pageSize=$(this).attr('id');  
                if(_self.cache.value==""&&_self.cache.filtrate==""){
                    // inx=1;
                    // GET(inx,pageSize);
                }else if(_self.cache.value!==""){
                    // inx=1;
                    // param["pageNumber"]=inx;
                    // param["pageSize"]=pageSize;
                    // POST(inx,pageSize); 
                }else if(_self.cache.filtrate!==""){
                    // inx=1;
                    // _param["pageNumber"]=inx;
                    // _param["pageSize"]=pageSize;
                    // filtrates(inx,pageSize); 
                }
                $("#page_row").val($(this).html());  
                $("#liebiao").hide();
            });    
        });      
        $("#page_row").blur(function(){  
            setTimeout(function(){
            	$("#liebiao").hide();
            },200);  
        });     
	}
}
$(function(){
	vipMessage.init();
})