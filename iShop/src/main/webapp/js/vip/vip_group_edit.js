var oc = new ObjectControl();
function getcorplist(){
    //获取所属企业列表
    var corp_command="/user/getCorpByUser";
    oc.postRequire("post", corp_command,"", "", function(data){
        console.log(data);
        if(data.code=="0"){
            var msg=JSON.parse(data.message);
            console.log(msg);
            var index=0;
            var corp_html='';
            var c=null;
            for(index in msg.corps){
                c=msg.corps[index];
                corp_html+='<option value="'+c.corp_code+'">'+c.corp_name+'</option>';
            }
            $("#OWN_CORP").append(corp_html);
            $('.corp_select select').searchableSelect();
            $('.searchable-select-item').click(function(){
                $("#AREA_ID").val("");
                $("#AREA_NAME").val("");
                $("#AREA_ID").attr("data-mark","");
                $("#AREA_NAME").attr("data-mark","");
            })
        }else if(data.code=="-1"){
            art.dialog({
                time: 1,
                lock:true,
                cancel: false,
                content: data.message
            });
        }
    });
}
getcorplist();

