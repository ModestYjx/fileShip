package cn.flyingocean.fileship.constance;

public enum ReturnValue {

    // 成功
    OK(0,"成功"),
    // 不常用，状态码表示客户端错误，指代服务器端无法提供与  Accept-Charset 以及 Accept-Language 消息头指定的值相匹配的响应
    NOT_ACCEPTABLE(1,"请求不合法"),
    // 缺乏目标资源要求的身份验证凭证，发送的请求未得到满足
    UNAUTHORIZED(2,"未认证"),
    // 资源没有找到
    NOT_FOUND(3,"资源未找到"),
    // 请求参数错误或请求语法错误
    BAD_REQUEST(4,"请求参数错误"),
    // 权限不足，禁止访问
    FORBIDDEN(5,"禁止访问"),
    // 验证码错误
    ERROR_VERIFICATION_CODE(6, "验证码错误"),
    // ;
    ERROR_ACC_OR_PAS(7, "用户名或密码错误"),
    // 账号被封禁
    ACCOUNT_BANNING(8, "账号被封禁"),
    // 邮箱已被注册
    ACCOUNT_EMAIL_REGSITED(9, "邮箱已被注册"),
    // 账号未激活
    ACCOUNT_NOT_ACTIVATED(10, "账号未激活"),
    ACTIVATE_FAILED(11, "激活失败"),
    ACTIVATE_SUCCESS(12, "激活成功"),
    // 文件找不到
    NOT_FOUND_FILE(13,"文件找不到"),
    // 认领凭证找不到
    NOT_FOUND_FILE_CLAIM(14,"认领凭证找不到，可能已被领取"),
    // 认领凭证错误
    ERROR_FILE_CLAIM(15,"认领凭证错误"),
    // 用户不存在
    ACCOUNT_NOT_FOUND(16,"用户不存在"),
    // 文件未被认领
    FILE_UNCLAIMED(17,"文件未被认领"),
    // 文件操作出错
    ERROR_FILE_JOIN(18,"文件JOIN操作出错"),
    ERROR_WAREHOUSE_MERGE(19,"文件仓库MERGE操作出错"),
    ERROR_FILE_RENAME(20,"文件RENAME错误"),
    ERROR_WAREHOUSE_DIRECTORY_CREATE(21,"文件仓库DIRECTORY_CREATE出错"),
    NOT_FOUND_WAREHOUSE(22,"文件仓库不存在"),
    ERROR_EXCEL_FETCH_HEADER(23,"解析EXCEL表头出错"),
    NOT_FOUND_NAMELIST(24,"名单不存在"),
    ERROR_COLLATIONANDANALYSIS1(25,"以方式一整理分析出错"),
    ERROR_WAREHOUSE_PACKAGE(26,"文件仓库打包出错"),
    ERROR_DELETE_FILE(27,"文件删除出错"),
    ERROR_DELETE_WAREHOUSE_DIR(28,"文件仓库目录删除出错"),
    ERROR_MAX_UPLOAD_SIZE(29,"超出文件大小限制"),
    NOT_SUPPORT_COLLABORATIVE_WAREHOUSE(30,"不支持协作型文件仓库"),
    NOT_SUPPORT_MERGE_ASSOCIATE_WAREHOUSE_SELF(31,"关联型仓库不支持源用户和目标用户相同");

    private int code;
    private String msg;

    private ReturnValue(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }









}
