
/**
 * 获取用户信息
 */
$.ajax({
    type: "get",
    async: false,
    url: "/user/",
    success: function (r) {
        if (r.code != FO.ResponseStatus.OK) {
            alert(r.msg);
        } else {
            window.page.data.user.id = r.data.id;
            window.page.data.user.email = r.data.email;
            window.page.data.user.nickname = r.data.nickname;
            window.page.data.user.phoneNumber = r.data.phoneNumber;
        }
    },
    error: function (r) {
        // window.FO.postAlert(r);
    }
});
/**
 * 获取名单信息
 * @type {Vue}
 */
$.ajax({
    type: "get",
    async: false,
    url: "/namelists/list",
    success: function (r) {
        if (r.code != FO.ResponseStatus.OK) {
            alert(r.msg);
        } else {
            window.page.data.nameLists = r.data;
        }
    },
    error: function (r) {
        // window.FO.postAlert(r);
    }
});

// 用户信息面板
vm1 = new Vue({
    el : "#user-info-panel",
    data : window.page.data.user,
});

// 名单面板
vm2 = new Vue({
    el : "#namelist-panel",
    data : window.page.data ,
    methods : {
        // 上传名单
        doUpload : function (e) {
            let formData = new FormData();
            formData.append("file",$('input[type="file"]')[0].files[0]); // 文件
            $.ajax({
                url: '/namelists/upload',
                type: 'post',
                cache: false, //cache设置为false，上传文件不需要缓存。
                data: formData,
                processData: false, //processData设置为false。因为data值是FormData对象，不需要对数据做处理
                contentType: false, //contentType设置为false，不设置contentType值，因为是由<form>表单构造的FormData对象，且已经声明了属性
                // enctype="multipart/form-data"，所以这里设置为false。
                dataType:"json",
                success:function (r) {
                    Messenger().post({
                        message: "名单上传成功",
                        hideAfter: 1.5
                    });
                },
                error: function () {
                    alert("上传失败")
                }
            });
        },
        // 下载名单
        download : function (id) {
            return '/namelists/download/'+id;
        }
    }
});