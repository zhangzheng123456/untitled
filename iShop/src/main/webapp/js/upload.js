
var myUpload = {
    accessid: "",
    accesskey: "",
    host: "",
    policyBase64: "",
    signature: "",
    callbackbody: "",
    filename: "",
    key: "",
    expire: 0,
    g_object_name: "",
    g_object_name_type: "random_name",
    dir: "test",
    new_multipart_params: {},
    callback: null,
    hasSigned:false,

    send_request: function () {
        var body={
            region: 'oss-cn-hangzhou',
            accessKeyId: 'O2zXL39br8rSn1zC',
            accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
            secure:true,
            bucket: 'products-image'
        };
        return body;
    },
    get_signature: function () {
        var body = myUpload.send_request();
        var obj = eval("(" + body + ")");
        myUpload.host = obj["host"]
        myUpload.policyBase64 = obj["policy"]
        myUpload.accessid = obj["accessid"]
        myUpload.signature = obj["signature"]
        myUpload.expire = parseInt(obj["expire"])
        myUpload.callbackbody = obj["callback"]
        myUpload.key = obj["dir"]
        myUpload.hasSigned = true;
        return true;
    },
    random_string: function (len) {
        len = len || 32;
        var chars = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678";
        var maxPos = chars.length;
        var pwd = "";
        for (i = 0; i < len; i++) {
            pwd += chars.charAt(Math.floor(Math.random() * maxPos));
        }
        return pwd;
    },
    get_suffix: function (filename) {
        var pos = filename.lastIndexOf(".");
        var suffix = "";
        if (pos != -1) {
            suffix = filename.substring(pos);
        }
        return suffix;
    },
    calculate_object_name: function (filename) {
        var suffix = myUpload.get_suffix(filename);
        myUpload.g_object_name = myUpload.key + myUpload.random_string(10) + suffix;

        return ""
    },
    get_uploaded_object_name: function (filename) {
        return myUpload.g_object_name;
    },
    set_upload_param: function (up, filename, ret) {
        if (myUpload.hasSigned == false) {
            myUpload.get_signature();            
        }

        myUpload.g_object_name = myUpload.key;
        if (filename != "") {
            suffix = myUpload.get_suffix(filename);
            myUpload.calculate_object_name(filename);
        }

        myUpload.new_multipart_params = {
            "key": myUpload.dir + "/" + myUpload.g_object_name,
            "policy": myUpload.policyBase64,
            "OSSAccessKeyId": myUpload.accessid,
            "success_action_status": "200", //让服务端返回200,不然，默认会返回204
            "callback": myUpload.callbackbody,
            "signature": myUpload.signature,
        };

        // up.setOption({
        //     "url": myUpload.host,
        //     "multipart_params": myUpload.new_multipart_params
        // });
        up.start();
    },
    init: function (subdir, callback) {
        myUpload.callback = callback;
        myUpload.dir = appimagepath+"/"+orgid+"/"+subdir;
        myUpload.get_signature();
        uploader.init();        
    }
};


var uploader = new plupload.Uploader({
	runtimes : "html5,flash,silverlight,html4",
	browse_button : "selectfiles",
	container: document.getElementById("container"),
	flash_swf_url : "js/plupload-2.1.2/js/Moxie.swf",
	silverlight_xap_url : "js/plupload-2.1.2/js/Moxie.xap",
	url: " https://products-image.oss-cn-hangzhou.aliyuncs.com",
    multipart_params:{
        region: 'oss-cn-hangzhou',
        accessKeyId: 'O2zXL39br8rSn1zC',
        accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
        secure:true,
        bucket: 'products-image'
    },
	filters: {        
        mime_types : [ 
        { title : "Image files", extensions : "jpg,gif,png,bmp,jpeg" }
        ],
        max_file_size : "10mb", 
        prevent_duplicates : false 
    },

	init: {
		PostInit: function() {		
		    
		},

		FilesAdded: function(up, files) {
            if(files.length>9){
                dialog.showToast('只能传9张靓图哟', 2);
                for(var i=0;i<files.length;i++){
                    uploader.removeFile(files[i].id);
                }
                return
            }
			dialog.showWaitting();
		    myUpload.set_upload_param(uploader, "", false);
		},

		BeforeUpload: function(up, file) {
		    myUpload.set_upload_param(up, file.name, true);
        },

		UploadProgress: function(up, file) {			
		},

		FileUploaded: function(up, file, info) {
			dialog.closeWaitting();
            if (info.status == 200)
            {
                var imgUrl = myUpload.host + "/" + myUpload.dir + "/" + myUpload.get_uploaded_object_name(file.name);
                
                var param = {"imgUrl":imgUrl};
                postData("user/show/retationOssImage", param, function (data) {
                    myUpload.callback(imgUrl);
                });
            }
            else
            {
                alert(info.response);
            } 
		},

		Error: function (up, err) {
			dialog.closeWaitting();
		    alert(err.code + err.response);            
		}
	}
});


//myUpload.init("ttt", function (imgUrl) {
//    alert(imgUrl);
//});