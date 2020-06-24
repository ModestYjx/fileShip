vm = new Vue({
    el : "#nav-right",
});

// 刷新验证码
$('#yzmBTN').click(function () {
    $(this).css("background-image", "url(/common/VerificationCodeGenerator?d=" + new Date().getTime() + ")")
})

// 提交按钮点击事件
$("#submitBTN").click(
    function () {
        // 参数校验
        let email = $("input[name='email']").val();
        if (window.FO.fieldCheck("email",email)===false){
            window.FO.postAlert("email格式不合法");
            return false;
        }
        let password = $("input[name='password']").val();
        if (window.FO.fieldCheck("password",password)===false){
            window.FO.postAlert("密码格式不合法");
            return false;
        }
        let verificationCode = $("input[name='verificationCode']").val();
        if (verificationCode.length === 0){
            window.FO.postAlert("验证码不能为空");
            return false;
        }


        let isRememberMe = document.getElementById('remembermeCheckBox').checked;
        $.ajax({
            type: "post",
            async: false,
            url: "/user/doSignIn?verificationCode=" + verificationCode +"&isRememberMe="+isRememberMe,
            contentType: "application/json;charset=utf-8",//必须加
            data: JSON.stringify({
                'email': $("input[name='email']").val(),
                'password': password,
            }),
            success: function (r) {
                if (r.code != window.FO.ResponseStatus.OK) {
                    window.FO.postAlert(r);
                    // 刷新验证码
                    $('#yzmBTN').click();
                } else {
                    window.location.href = "/user-center.html";
                }
            },
            error: function (r) {
                alert("服务器内部错误 "+r);

            }
        });
    }
);

