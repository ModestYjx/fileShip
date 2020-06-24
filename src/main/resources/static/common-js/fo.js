/**
 * 自定义的类, 由于对其他 js 库有依赖，该 js 文件 放在<head>的最后引入
 * 放一些全局配置，如上下文路径，后端返回状态码，再包括一些工具方法，都直接加到 FO 上
 * 如果写成 window.FO={...}，在其他地方写 FO 的方法idea 不会给代码提示
 * @type {{}}
 */
FO = {
    /**
     * 后端返回状态码
     * @type {{OK: number, NOT_ACCEPTABLE: number, UNAUTHORIZED: number, NOT_FOUND: number, BAD_REQUEST: number, FORBIDDEN: number, ERROR_VERIFICATION_CODE: number, NOT_FOUND_USER: number}}
     */
    ResponseStatus : {
        OK : 0,
        NOT_ACCEPTABLE : 1,
        UNAUTHORIZED : 2,
        NOT_FOUND : 3,
        BAD_REQUEST : 4,
        FORBIDDEN : 5,
        ERROR_VERIFICATION_CODE : 6,
        NOT_FOUND_USER : 14},
    /**
     * 上下文路径
     * @type {string}
     */
    CONTEXT_PATH : "localhost",
    MAX_UPLOAD_SIZE: 50*1024*1024,
    /**
     * 全局定义Alert提示
     * @param r
     * @param type 默认是 'error'，可选值还有 'info'
     * @param hideAfter
     */
    postAlert : function (r,type,hideAfter) {
        if (type==undefined){
            type = 'error';
        }
        if (hideAfter == undefined){
            hideAfter = 2.5; // 单位秒
        }

        if (typeof (r)==='string'){
            Messenger().post({
                message:r,
                hideAfter:hideAfter,
                type:type
            });
            return;
        }
        if (r.status==undefined){
            Messenger().post({
                message:r.msg,
                hideAfter:hideAfter,
                type:type
            });
            return;
        }else{
            Messenger().post({
                message:"异步请求出错了 "+r.status+":"+r.statusText,
                hideAfter:hideAfter,
                type:type
            });
            return;
        }
    },
    /**
     * collection 是一个数据集，需要支持[]运算符
     * key 是指定该 collection 中单个元素的某个属性
     * 找到主属性 key 的值为 keyValue 的元素，将属性 property 的值修改为 value
     * 如果没有找到范湖false，否则返回true
     * @param collection
     * @param key
     * @param keyValue
     * @param keyValue
     * @param value
     * @returns {boolean}
     */
    modifyTheOne : function(collection,key,keyValue,property,value){
        for (let i=0;i<collection;i++){
            if (collection[i][key] === keyValue){
                collection[i][property] = value;
                return true;
            }
        }
        return false;
    },
    /**
     * 字段检查
     * @param which
     * @param value
     * @returns {boolean}
     */
    fieldCheck : function (which,value) {
        if(which==='email'){
            let reg = new RegExp('.*@.*\\..*');
            return reg.test(value);
        }
        if (which==='nickname'){
            // fixme 暂时没有发现 xss，先暂定只做长度要求
            let l = value.length;
            if (l>14||l<4){
               return false;
            }else{
                return true;
            }
        }
        if (which==='password'){
            let reg = new RegExp("[1-9A-Za-z._*!?]{6,25}");
            return reg.test(value);
        }
    }
};
window.FO = FO;

/**
 * =========================================================
 */

/**
 * 弹窗组件配置
 * @type {{extraClasses: string, theme: string}}
 */
Messenger.options = {
    extraClasses: 'messenger-fixed messenger-on-top',
    theme: 'air'
}
