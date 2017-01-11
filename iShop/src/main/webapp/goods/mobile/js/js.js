var photo = $('#photo');

function isCanvasSupported(){
    var elem = document.createElement('canvas');
    return !!(elem.getContext && elem.getContext('2d'));
}
$('#photo').on('change','', function(event){
    if(!isCanvasSupported()){
        return;
    }
    compress(event, function(base64Img){
        var oss=new OSS.Wrapper({
            region: 'oss-cn-hangzhou',
            accessKeyId: 'O2zXL39br8rSn1zC',
            accessKeySecret: 'XvHmCScXX9CiuMBRJ743yJdPoEiKTe',
            bucket: 'products-image'
        });
        var storeAs='ShowImage/';
        oss.multipartUpload(storeAs,base64Img).then(function (result) {
            console.log(arguments)
            var storeAs='http://products-image.oss-cn-hangzhou.aliyuncs.com/'+result.name;
        }).catch(function (err) {
            console.log(err);
        });
    });
});

function compress(event, callback){
    var file = event.currentTarget.files[0];
    var reader = new FileReader();
     console.log(file);
    reader.onload = function (e) {
        var image = $('<img/>');
        image.on('load', function () {
            var square = 100;
            var canvas = document.createElement('canvas');
             // document.body.appendChild(canvas)
            canvas.width = square;
            canvas.height = square;

            var context = canvas.getContext('2d');
            context.clearRect(0, 0, square, square);
            var imageWidth;
            var imageHeight;
            var offsetX = 0;
            var offsetY = 0;

            if (this.width > this.height) {
                imageWidth = Math.round(square * this.width / this.height);
                imageHeight = square;
                offsetX = - Math.round((imageWidth - square) / 2);
            } else {
                imageHeight = Math.round(square * this.height / this.width);
                imageWidth = square;
                offsetY = - Math.round((imageHeight - square) / 2);
            }
            context.drawImage(this, offsetX, offsetY, imageWidth, imageHeight);
            var data = canvas.toDataURL('image/jpeg');
            callback(data);
        });
        image.attr('src', e.target.result);
    };
    reader.readAsDataURL(file);
}