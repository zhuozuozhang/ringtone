package com.hrtxn.ringtone.project.threenets.kedas.kedapublic.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author zcy
 * @Date 2019-8-26 14:06
 * @Description 微信公众号用户信息
 */
@Data
public class KedaPublicUser implements Serializable {
    /***/
    private Integer id;
    /**用户的唯一标识*/
    private String openid;
    /**用户昵称*/
    private String nickname;
    /**用户的性别，值为1时是男性，值为2时是女性，值为0时是未知*/
    private String sex;
    /**用户个人资料填写的省份*/
    private String province;
    /**普通用户个人资料填写的城市*/
    private String city;
    /**国家*/
    private String country;
    /**用户头像*/
    private String headimgurl;
    /**创建时间*/
    private Date createTime;

}