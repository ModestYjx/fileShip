
// 刷新验证码
$('#yzmBTN').click(function(){
    $(this).css("background-image","url(/common/VerificationCodeGenerator?d="+new Date().getTime() +")")
})

$("#submitBTN").click(
    function(){
        // 参数校验
        let email = $("input[name='email']").val();
        if (window.FO.fieldCheck("email",email)===false){
            window.FO.postAlert("email格式不合法");
            window.FO
            return false;
        }
        let nickname = $("input[name='nickname']").val();
        if (window.FO.fieldCheck("nickname",nickname)===false){
            window.FO.postAlert("昵称格式不合法");
            return false;
        }
        let password1 = $("#password1").val();
        let password2 = $("#passowrd2").val();
        if (password1 !== password2){
            window.FO.postAlert("两次密码不一致");
            return false;
        }
        if (window.FO.fieldCheck("password",password1)===false){
            window.FO.postAlert("密码格式不合法");
            return false;
        }
        let verificationCode = $("input[name='verificationCode']").val();
        if (verificationCode.length === 0){
            window.FO.postAlert("验证码不能为空");
            return false;
        }

        $.ajax({
            type:"post",
            async:false,
            url:"/user/doSignUp?verificationCode="+verificationCode,
            contentType: "application/json;charset=utf-8",//必须加
            data: JSON.stringify({
                'nickname': nickname,
                'password': password1,
                'email': email
            }),
            success:function(r){
                if(r.code==FO.ResponseStatus.OK){
                    window.location.href = "/signup-wait-info.html";
                }else{
                    window.FO.postAlert(r);
                   $('#yzmBTN').click();
                }
            },
            error:function (r) {
                window.FO.postAlert(r);
            }
        });
    }
);
