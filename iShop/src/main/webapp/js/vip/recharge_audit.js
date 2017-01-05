/**
 * Created by Administrator on 2017/1/5.
 */
(function(){
    var oc= new ObjectControl();
    var audit={
        funcCode:"",
        titleArray:[],
        init:function(){
            this.getFunCode();
            this.qjia();
            this.jumpBianse();
        },
        getFunCode:function(){
            var key_val=sessionStorage.getItem("key_val");//取页面的function_code
            key_val=JSON.parse(key_val);//取key_val的值
            this.funcCode=key_val.func_code;
        },
        qjia:function (){
            var self=this;
            var param={};
            param["funcCode"]=self.funcCode;
            oc.postRequire("post","/list/action","0",param,function(data){
                var message=JSON.parse(data.message);
                var actions=message.actions;
                self.titleArray=message.columns;
                self.tableTh();
            })
        },
        tableTh:function (){ //table  的表头
            var TH="";
            var titleArray=audit.titleArray;
            for(var i=0;i<titleArray.length;i++){
                if(titleArray[i].show_name.trim()=='姓名'){
                    TH+="<th>"+titleArray[i].show_name+"</th>"+'<th style="width: 20px"></th>'
                }else{
                    TH+="<th>"+titleArray[i].show_name+"</th>"
                }
            }
            $("#tableOrder").after(TH);
        },
        jumpBianse:function (){
            $(document).ready(function(){//隔行变色
                $(".table tbody tr:odd").css("backgroundColor","#e8e8e8");
                $(".table tbody tr:even").css("backgroundColor","#f4f4f4");
            })
        }
    };

    $(function(){
        audit.init();
    })
})();
