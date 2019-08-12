package com.hrtxn.ringtone.common.constant;

import java.util.HashMap;

/**
 * Author:zcy
 * Date:2019-07-05 16:17
 * Description:返回信息封装类
 */
public class AjaxResult extends HashMap<String, Object> {

    public AjaxResult() {
    }


    /**
     * 失败信息返回
     * @param msg
     * @return
     */
    public static AjaxResult error(String msg) {
        return error(500, msg);
    }

    public static AjaxResult success(String msg){
        return success(200,msg);
    }

    public static AjaxResult error(int code, String msg) {
        AjaxResult json = new AjaxResult();
        json.put("code", code);
        json.put("msg", msg);
        return json;
    }

    /**
     * 成功信息返回
     * @param data
     * @param msg
     * @return
     */
    public static AjaxResult success(Object data, String msg) {
        return success(200, data, msg);
    }

    public static AjaxResult success(int code, Object data, String msg) {
        AjaxResult json = new AjaxResult();
        json.put("code", code);
        json.put("data", data);
        json.put("msg", msg);
        return json;
    }

    /**
     * 含分页总数
     * @param data
     * @param msg
     * @param totalCount
     * @return
     */
    public static AjaxResult success(Object data,String msg,int totalCount){
        return success(200, data, msg,totalCount);
    }
    public static AjaxResult success(int code, Object data, String msg,int totalCount) {
        AjaxResult json = new AjaxResult();
        json.put("code", code);
        json.put("data", data);
        json.put("msg", msg);
        json.put("totalCount",totalCount);
        return json;
    }
}
