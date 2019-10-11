package com.hrtxn.ringtone.common.utils;

import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

/**
 * Author:lile
 * Date:2019-08-21 9:17
 * Description:
 */
public final class Const implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 公告类型
     *
     */
    public final static String MODUL_THREENETS = "0";
    public final static String MODUL_KEDA = "8";
    /**
     * 运营商类型
     */
    public final static Integer OPERATORS_MOBILE = 1;  //移动
    public final static Integer OPERATORS_TELECOM = 2; //电信
    public final static Integer OPERATORS_UNICOM = 3;  //联通

    /**
     * 运营商审核
     */
    public final static Integer UNREVIEWED = 0;
    public final static Integer REVIEWED = 1;

    /**
     * 审核状态
     */
    public final static String PENDING_REVIEW = "待审核";
    public final static String SUCCESSFUL_REVIEW = "审核通过";
    public final static String FAILURE_REVIEW = "审核失败";
    public final static String UNDER_REVIEW = "正在审核";
    public final static String KEDA_UNDER_REVIEW = "审核中";


    /**
     * 电信新增失败状态码
     */
    public final static String ILLEFAL_AREA = "illegal_area";
    public final static String NUMBER_REPEAT = "number_repeat";
    public final static String TIME_OUT = "time_out";

    /**
     * 电信资费
     */
    public final static Integer TELECOM_10_YUAN_TRAIFF = 2;
    public final static Integer TELECOM_20_YUAN_TRAIFF =11;

    /**
     * 电信子渠道商ID
     */
    //public static String child_Distributor_ID_177 = "594095"; //子渠道商（四川）
    public static String child_Distributor_ID_181 = "296577"; //子渠道商（18159093112）
    public static String child_Distributor_ID_188 = "61204"; //子渠道商（18888666361）
    public static String child_Distributor_ID_177 = "1352214"; //子渠道商（18888666361）

    /**
     * 电信主渠道商ID
     */
    public static String parent_Distributor_ID_188 = "61203"; //主渠道商（18859093112）
    public static String parent_Distributor_ID_177 = "1352066"; //主渠道商（17751937950）

    //子渠道商（17712033392）(四川)
    //private static String child_Distributor_ID_177 = "594095";
    //private static String test_id = "1673882";

    //充值记录类型--号码认证
    public static Integer SYS_RECHARGE_LOG_TYPE_TEL_CER = 1;
    //充值记录类型--三网
    public static Integer SYS_RECHARGE_LOG_TYPE_THREE_NET = 2;

    //消费记录类型--号码认证
    public static Integer SYS_CONSUME_LOG_TYPE_TEL_CER = 1;
    //消费记录类型--三网
    public static Integer SYS_CONSUME_LOG_TYPE_THREE_NET = 2;

    //号码认证消费记录类型（1.首次/2.续费）
    public static Integer TEL_CER_CONSUME_LOG_TYPE_FIRST = 1;
    public static Integer TEL_CER_CONSUME_LOG_TYPE_RENEW = 2;

    //号码认证状态
    //开通中
    public static Integer TEL_CER_STATUS_OPENING = 1;
    //开通成功
    public static Integer TEL_CER_STATUS_SUCCESS_OPENING = 2;
    //开通失败
    public static Integer TEL_CER_STATUS_DEFAULT_OPENING = 3;
    //续费中
    public static Integer TEL_CER_STATUS_RENEWAL = 4;
    //续费成功
    public static Integer TEL_CER_STATUS_SUCCESS_RENEWAL = 5;
    //续费失败
    public static Integer TEL_CER_STATUS_DEFAULT_RENEWAL= 6;

    //号码认证配置类型
    //泰迪熊
    public static Integer TEL_CER_CONFIG_TYPE_TEDDY = 1;
    public static String TEDDY = "泰迪熊";
    //电话邦
    public static Integer TEL_CER_CONFIG_TYPE_TELBOND = 2;
    public static String TELBOND = "电话邦";
    //彩印
    public static Integer TEL_CER_CONFIG_TYPE_COLORPRINT = 3;
    public static String COLORPRINT = "彩印";
    //挂机短信
    public static Integer TEL_CER_CONFIG_TYPE_HANGUPMESSAGE = 4;
    public static String HANGUPMESSAGE = "挂机短信";

    // 彩印永久有效
    public static String TEL_CER_COLORPRINT_ETERNAL = "永久有效";
    // 彩印免费
    public static Float TEL_CER_COLORPRINT_COST = Float.valueOf(0);


    /**
     * 中国电信号码格式验证 手机段： 133,153,180,181,189,177,1700,173,199
     **/
    public static final String CHINA_TELECOM_PATTERN = "(^1(33|53|77|73|99|8[019])\\d{8}$)|(^1700\\d{7}$)";

    /**
     * 中国联通号码格式验证 手机段：130,131,132,155,156,185,186,145,176,1709
     **/
    public static final String CHINA_UNICOM_PATTERN = "(^1(3[0-2]|4[5]|5[56]|7[6]|8[56])\\d{8}$)|(^1709\\d{7}$)";

    /**
     * 中国移动号码格式验证
     * 手机段：134,135,136,137,138,139,150,151,152,157,158,159,182,183,184,187,188,147,178,1705
     **/
    public static final String CHINA_MOBILE_PATTERN = "(^1(3[4-9]|4[7]|5[0-27-9]|7[28]|8[2-478])\\d{8}$)|(^1705\\d{7}$)";


    /**
     * 请求type
     */
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_FORM_DATA="multipart/form-data";

    /**
     * 400订单状态
     */
    /** 预占申请中 */
    public static final String FOUR_ORDER_PREOCCUPATION_NEW = "1";
    /** 预占成功 */
    public static final String FOUR_ORDER_PREOCCUPATION_SUCCESS = "2";
    /** 预占失败 */
    public static final String FOUR_ORDER_PREOCCUPATION_FAIL = "3";
    /** 资料模板申请中 */
    public static final String FOUR_ORDER_TEMPLATE_NEW = "4";
    /** 资料模板申请成功 */
    public static final String FOUR_ORDER_TEMPLATE_SUCCESS = "5";
    /** 资料模板申请失败 */
    public static final String FOUR_ORDER_TEMPLATE_FAIL = "6";
    /** 资料审核中 */
    public static final String FOUR_ORDER_SUBMIT_NEW = "7";
    /** 资料审核成功 */
    public static final String FOUR_ORDER_SUBMIT_SUCCESS = "8";
    /** 资料审核失败 */
    public static final String FOUR_ORDER_SUBMIT_FAIL = "9";

    /**
     * 接口编码对照
     */
    /**
     * 400预占结果通知
     */
    public static final String PREEMPTION_RESULT = "80001001";
    /**
     * 400资料模板生成完成通知
     */
    public static final String TEMPLATE_GENERATION_RESULT = "80001002";
    /**
     * 资料审核通知
     */
    public static final String EXAMINE_RESULT = "80001003";

    public static final String UPDATE_STATUS_RING = "ring";
    public static final String UPDATE_STATUS_VIDEO_RING = "videoRing";
    public static final String UPDATE_STATUS_ALL = "all";
    public static final String UPDATE_STATUS_MONTHLY = "monthly";
    public static final String UPDATE_STATUS_ALONE = "alone";

    public static final String SESSION_VERIFICATION_CODE = "session_verification_code";


    public static final Integer IS_EXEMPT_SMS_YES = 1;
    public static final Integer IS_EXEMPT_SMS_NO = 0;


}
