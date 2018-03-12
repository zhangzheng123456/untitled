//单个上传头像
function previewImage(file) {
    var MAXWIDTH = 200;
    var MAXHEIGHT = 200;
    var div = document.getElementById('preview');
    if (file.files && file.files[0]) {
        div.innerHTML = '<img id=imghead>';
        var img = document.getElementById('imghead');
        img.onload = function() { //图片预加载
            var rect = clacImgZoomParam(MAXWIDTH, MAXHEIGHT, img.offsetWidth, img.offsetHeight);
            img.width = rect.width;
            img.height = rect.height;
        };
        var reader = new FileReader();
        reader.onload = function(evt) {
            img.src = evt.target.result;
        };
        reader.readAsDataURL(file.files[0]);
    } else //兼容IE
    {
        var sFilter = 'filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale,src="';//过滤适应对象的尺寸边界
        file.select();
        var src = document.selection.createRange().text;//运用IE滤镜获取路径数据
        div.innerHTML = '<img id=imghead>';
        var img = document.getElementById('imghead');
        img.filters.item('DXImageTransform.Microsoft.AlphaImageLoader').src = src;
        var rect = clacImgZoomParam(MAXWIDTH, MAXHEIGHT, img.offsetWidth, img.offsetHeight);
        status = ('rect:' + rect.top + ',' + rect.left + ',' + rect.width + ',' + rect.height);
        div.innerHTML = "<div id=divhead style='width:" + rect.width + "px;height:" + rect.height + "px;margin-top:" + rect.top + "px;" + sFilter + src + "\"'></div>";
    }
}
function clacImgZoomParam(maxWidth, maxHeight, width, height) {
    var param = {
        top: 0,
        left: 0,
        width: width,
        height: height
    };
    if (width > maxWidth || height > maxHeight) {
        rateWidth = width / maxWidth;
        rateHeight = height / maxHeight;
        if (rateWidth > rateHeight) {
            param.width = maxWidth;
            param.height = Math.round(height / rateWidth);
        } else {
            param.width = Math.round(width / rateHeight);
            param.height = maxHeight;
        }
    }
    param.left = Math.round((maxWidth - param.width) / 2);
    param.top = Math.round((maxHeight - param.height) / 2);
    return param;
}
//上传头像至oss存储
$(function(){
    var client = new OSS.Wrapper({
        region: 'oss-cn-hangzhou',
        accessKeyId: 'O2zXL39br8rSn1zC',
        accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
        secure:true,   
        bucket: 'products-image'
    });
    document.getElementById('file').addEventListener('change', function (e) {
        var file = e.target.files[0];
        var corp_code=$("#OWN_CORP").val()//公司编号
        var user_code=$("#USERID").val()//员工编号
        // console.log(corp_code);
        // console.log(user_code);
        var storeAs="";
        if(user_code==""||user_code==undefined){
            storeAs = '/Corp_logo/ishow/'+corp_code.trim()+'.jpg';
        }
        if(user_code!==""&&user_code!==undefined){
            storeAs = '/Avatar/User/iShow/'+corp_code.trim()+user_code.trim()+'.jpg';
        }
        client.multipartUpload(storeAs, file).then(function (result) {
            var storeAs='https://products-image.oss-cn-hangzhou.aliyuncs.com/'+result.name;
            // $("#imghead").attr(src",result.url);
            console.log(result.name)
            $("#imghead").attr("data-src",storeAs);
            console.log($("#imghead").attr("data-src"))
        }).catch(function (err) {
            // console.log(err);
        });
    });
});