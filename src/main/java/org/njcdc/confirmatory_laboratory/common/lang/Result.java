package org.njcdc.confirmatory_laboratory.common.lang;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result implements Serializable {

    private int code; // 200是正常，非200表示异常
    private String msg;
    private Object data;

    /*一般情况下，返回成功可以直接确定code和msg内容，因此不需要可以直接传入data，其余自动设置即可*/
    public static Result success(Object data) {
        return success(200, "操作成功", data);
    }

    public static Result success(String msg,Object data) {
        return success(200, msg, data);

    }

    public static Result success(int code, String msg, Object data) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /*失败的时候不需要返回数据，返回null即可*/
    public static Result fail(String msg) {
        return fail(400, msg, null);
    }

    /*不常用*/
    public static Result fail(String msg, Object data) {
        return fail(400, msg, data);
    }

    public static Result fail(int code, String msg, Object data) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
