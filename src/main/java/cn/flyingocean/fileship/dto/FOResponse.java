package cn.flyingocean.fileship.dto;

import cn.flyingocean.fileship.constance.ReturnValue;

/**
 * 自定义的统一返回数据结构
 * FO :FlyingOcean的缩写，以此为避免和其他Response重名，便于快速准确导包
 */
public class FOResponse<T> {
    // 状态码
    private int code;
    // 消息
    private String msg;
    // 主体数据对象s
    private T data;

    public FOResponse(int code,String msg,T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public FOResponse(ReturnValue rv,T data){
        this.code = rv.getCode();
        this.msg = rv.getMsg();
        this.data = data;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
