package com.hrtxn.ringtone.common.utils;

import java.io.Serializable;

/**
 * Author:lile
 * Date:2019-08-21 9:17
 * Description:
 */
public final class Const implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * 电信新增失败状态码
     */
    public final static String ILLEFAL_AREA = "illegal_area";

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
    public static String parent_Distributor_ID_188 = "61203"; //主渠道商（18159093112）
    public static String parent_Distributor_ID_177 = "1352066"; //主渠道商（17751937950）

    //子渠道商（17712033392）(四川)
    //private static String child_Distributor_ID_177 = "594095";
    //private static String test_id = "1673882";
}
