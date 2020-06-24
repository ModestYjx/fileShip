vm = new Vue({
    el : "#nav-right",
    data : page.data
});

vm2 = new Vue({
    el : "#file-upload-form",
    data : page.data,
});

window.page.init = function () {
    window.cp = new ClipboardJS('.copy-btn');

    // 当前任务队列需要上传的总大小
    window.page.totalSize = 0;
    // 当前已经上传的文件大小总计，不包括当前正在上传的
    window.page.currentUploadedSize = 0;
    // 当前正在上传的文件大小
    window.page.currentFileSize = 0;
    // 小船停止位置
    window.page.stopPosition = 88;
    if (window.innerWidth<768){
        window.page.stopPosition = 69;
    }
    $.ajax({
        url: '/user/',
        type: 'get',
        dataType:"json",
        async:false,
        success:function (r) {
            if (r.code!=window.FO.ResponseStatus.OK){
                Cookies.set("user",JSON.stringify(r.data),{expires:0});
            }else {
                page.data.user = r.data;
                Cookies.set("user",JSON.stringify(r.data));
            }
        },
        error: function (r) {
            window.FO.postAlert(r);
        }
    });
}
window.page.init();

Messenger.options = {
    extraClasses: 'messenger-fixed messenger-on-top',
    theme: 'air'
}



function removeFile (self){
    var ps = $(self).prev(); //获取上面的兄弟元素
    var  i = ps[0].innerHTML.split(" ")[0].split(".")[0]; //获取上一个span的值，就可以得知这是第几个文件
    $($("#file-progress-"+i)).remove();
    $($("#file-"+i)).remove();
    page.i--;
    if(page.i==0){
            $("#file-time-select").css("display","none");
    }
}
page.i = 0;//第几个文件

/* 1. 移除所有没有指向本地文件的input[type="file"]包括它的一些父元素
* */
page.removeEmptyFileProgress = function () {
    var fileInputs = $("input[type='file']");
    for(let i=0;i<fileInputs.length;i++){
        if (fileInputs[i].files.length==0){
            fileInputs[i].remove();
        }
    }
}

/**
 * 点击切换验证码
 * @param self
 */
page.changeVerificationCode = function (self) {
    var time = new Date().getTime();
     $(self).css('background-image','url(/common/VerificationCodeGenerator?d'+time+')' );

}

/* 1. 当#file-label被点击是新建一个input[type="file"]并隐藏它
*  2. 为这个input绑定上change事件
*  3. 虽然这样会导致有的input[type="file"]并没有指向任何本地文件
**/
$("#file-label").click(function () {
    //准备一个新的input[type="file"]并隐藏
    if(page.i>=5) {
        alert("最大上传文件个数为5!");
    }else {
        page.i++;
    var fileInput = "<input name='file' data-isUploaded='false' type=\"file\" id=\"file-"+page.i+"\">";
    $(this).after(fileInput);
    fileInput = $("#file-"+page.i);
    fileInput.hide().trigger("click");

    //为input绑定事件
    fileInput.change(fileInputChange);
    }
});

function fileInputChange() {
    var fileName = this.files[0].name;
        var progressBar = "<div class=\"fo-file-progress\" id=\"file-progress-"+page.i+"\">\n" +
            "                    <p><span>"+page.i+". </span>"+fileName+" &nbsp;<span class='glyphicon glyphicon-remove' onclick='removeFile(this)'></span> </p>\n" +
            "                    <div class=\"progress\">\n" +
            "                        <div class=\"progress-bar\" role=\"progressbar\" style=\"width: 0%;\">\n" +
            "                            0%\n" +
            "                        </div>\n" +
            "                    </div>\n" +
            "                </div>";
        $(this).after(progressBar);

        //移除所有没有指向本地文件的input[type="file"]包括它的一些父元素
        page.removeEmptyFileProgress();
        filetrue = 1;
        $("#file-time-select").css("display","inline");
};

/* 1. 点击上传按钮后清掉所有没有指向本地文件的input[type="file"]包括它的一些父元素
* */
$("#file-upload-form").submit(function () {
    page.removeEmptyFileProgress();
});


/* 1. 点击提交按钮后小船向红旗移动
   2. 校验验证码
   3. 完成上传任务
* */
$("#upLoadBTN").click(function () {
    // 检查验证码
    let checkVC = window.page.checkVerificationCode("verificationCode1");
    if(checkVC!=true) return;

    // 上传任务
    var form = document.getElementById("file-upload-form");
    var file_inputs = form.querySelectorAll("input[type='file']");
    let fileIndex = 0;
    // 检查字段
    let email = document.getElementById('input1').value;

    // 暂时确定总文件大小
    window.page.totalSize = 0;
    // 已上传大小，不包括正在上传的
    window.page.currentUploadedSize = 0;
    // 初始化小船位置
    document.getElementById("ship").style.left = "0vw";
    for (let i=0;i<file_inputs.length;i++){
        window.page.totalSize += file_inputs[i].files[0].size;
    }

    window.page.upload(fileIndex,file_inputs);

    //红旗出现
    $("#redFlag>svg").show();

});
page.checkVerificationCode = function (elementId) {
    var pass;
    $.ajax({
        url: '/common/VerificationCodeChecker',
        type: 'post',
        data: {'verificationCode':document.getElementById(elementId).value},
        contentType: 'application/x-www-form-urlencoded;charset=utf-8',
        dataType:"json",
        async:false,
        success:function (r) {
          if (r.code!=window.FO.ResponseStatus.OK){
              window.FO.postAlert(r);
              pass = false;
          }else {
              pass = true;
          }

        },
        error: function (r) {
            window.FO.postAlert(r)
            pass = false;
        }
    });
    return pass;
}
// 完成上传任务
page.upload = function (fileIndex,file_inputs){
    // 判断文件大小是否超限制
    if (file_inputs[fileIndex].files[0].size>window.FO.MAX_UPLOAD_SIZE){
        window.FO.postAlert("文件大小超限,大小上限为"+(window.FO.MAX_UPLOAD_SIZE/1024/1024)+"MB");
        let file_input_id = file_inputs[fileIndex].id.split('-')[1];
        var file_progress_wrapper = document.getElementById('file-progress-'+file_input_id);
        var file_progress = file_progress_wrapper.children[1].children[0];
        file_progress.style.width = "100%";
        file_progress.innerHTML = "文件大小约为："+(file_inputs[fileIndex].files[0].size/1024/1024)+"MB"
            +",大小上限为"+(window.FO.MAX_UPLOAD_SIZE/1024/1024)+"MB";
        file_progress.style.backgroundColor = "red";
        // 更新totalSize
        window.page.totalSize -= file_inputs[fileIndex].files[0].size;
        return;
    }
    // 已上传过的文件不再重复上传
    if(file_inputs[fileIndex].dataset.isuploaded==="true"){
        if(fileIndex<file_inputs.length-1){
            fileIndex++;
            window.page.upload(fileIndex,file_inputs);
        }
        // 更新totalSize
        window.page.totalSize -= file_inputs[fileIndex].files[0].size;
        return;
    }else{
        file_inputs[fileIndex].dataset.isuploaded="true"
    }
    // 更新currentFileSize
    window.page.currentFileSize =  file_inputs[fileIndex].files[0].size;

    let formData = new FormData();
    formData.append("file",file_inputs[fileIndex].files[0]); // 文件
    formData.append("email",document.getElementById('input1').value); // 邮箱
    formData.append("ttl",$('input[name="file-time-select"]:checked').val());//ttl:time to live,文件的有效期
    // 拼接url
    // let email = document.getElementById('input1').value;
    // let ttl = $('input[name="file-time-select"]:checked').val();
//    postUrl = postUrl +'?'+"email="+email+"&"+"ttl="+ttl;
    $.ajax({
        url: '/files/upload/',
        type: 'post',
        cache: false, //cache设置为false，上传文件不需要缓存。
        data: formData,
        processData: false, //processData设置为false。因为data值是FormData对象，不需要对数据做处理
        contentType: false, //contentType设置为false，不设置contentType值，因为是由<form>表单构造的FormData对象，且已经声明了属性
        // enctype="multipart/form-data"，所以这里设置为false。
        dataType:"json",
        xhr: function() {
            var xhr = new XMLHttpRequest();
            //使用XMLHttpRequest.upload监听上传过程，注册progress事件，打印回调函数中的event事件
            xhr.upload.addEventListener('progress', function (e) {
                // loaded代表上传了多少
                // total代表总数为多少
                var progressRate = (e.loaded / e.total) * 100 + '%';
                // 设置进度条
                let file_input_id = file_inputs[fileIndex].id.split('-')[1];
                var file_progress_wrapper = document.getElementById('file-progress-'+file_input_id);
                var file_progress = file_progress_wrapper.children[1].children[0];
                file_progress.style.width = progressRate;
                file_progress.innerHTML = progressRate;

                // 更新 currentUploadedSize
                let size = window.page.currentFileSize*(e.loaded / e.total)
                    + window.page.currentUploadedSize;
                let radio = size/window.page.totalSize;

                $("#ship").animate({
                    left: radio*window.page.stopPosition+"vw"
                },10);
            })
            return xhr;
        },
        success:function (r) {
            if (r.code === window.FO.ResponseStatus.OK){
                // 更新currentSize
                window.page.currentUploadedSize += window.page.currentFileSize;

                Messenger().post({
                    message:"<div>"+
                    "<span>上传成功! </span>"+"<br/>"+
                    "<span>"+"文件名："+r.data.filename+"</span>"+"<br/>"
                    // +"<span>"+" 32位文件码："+r.data.uuid+"</span>"+"<br/>"
                    +"<span>"+"中文文件码："+r.data.chineseToken+"</span>"
                        +"<button class='btn btn-default pull-right copy-btn' data-clipboard-text='"+r.data.chineseToken+"'>复制文件码</button>"
                    +"</div>",
                    hideAfter:1000,
                    showCloseButton: true
                });
            }else{
                if (r.code === window.FO.ResponseStatus.BAD_REQUEST){
                    window.FO.postAlert("请求参数错误，可能是文件大小超限");
                }

            }

            // 上传下一个文件
            if(fileIndex<file_inputs.length-1){
                fileIndex++;
                page.upload(fileIndex,file_inputs);
            }
        },
        error: function (r) {
            window.FO.postAlert(r)
        }
    });
}

/**
 * 用户点击下载按钮
 * 1. 检查验证码正确性
 * 2. 调用下载函数
 */
$("#downLoadBTN").click(function () {
    let pass = window.page.checkVerificationCode("verificationCode2");
    if (!pass)  return;
    window.page.download();
});
/**
 * 根据用户输入的文件码重定向URL
 */
// todo 按道理应该将文件码和验证码同时送往后台，此处偷懒
page.download = function () {
    window.location.href = '';
    window.location.href = "/files/download/"+$("#input2").val();
    // $.ajax({
    //     url: "/files/download/"+$("#input2").val(),
    //     type: 'get',
    //     dataType:"json",
    //     async:false,
    //     success:function (r) {
    //         if (r.code !== window.FO.ResponseStatus.OK){
    //             window.FO.postAlert(r);
    //         }
    //     },
    //     error: function (r) {
    //         console.log(r);
    //     }
    // });
}

/*点击切换表单面板*/
$("#form-header-left").click(function () {
    // 更新验证码
    $($("#yzmBTNWrapper>button")[0]).trigger('click');

    $("#form-header-left").removeClass("fo-inactive");
    $("#form-header-right").addClass("fo-inactive");
    $("#form-body-upload").show();
    $("#form-body-download").hide();

    // 默认过渡效果
    // $(".fo-form").css("filter","blur(90px)");
    //
    // setTimeout(function(){
    //     $(".fo-form").css("filter","blur(0px)")
    // },250*0.5);
});
$("#form-header-right").click(function () {
    // 更新验证码
    $($("#yzmBTNWrapper2>button")[0]).trigger('click');

    $("#form-header-right").removeClass("fo-inactive");
    $("#form-header-left").addClass("fo-inactive");
    $("#form-body-upload").hide();
    $("#form-body-download").show();

    // 默认过渡效果
    // $(".fo-form").css("filter","blur(90px)");
    //
    // setTimeout(function(){
    //     $(".fo-form").css("filter","blur(0px)")
    // },250*0.5);
});


