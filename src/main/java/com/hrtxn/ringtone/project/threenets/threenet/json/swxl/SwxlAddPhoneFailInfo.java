package com.hrtxn.ringtone.project.threenets.threenet.json.swxl;

import lombok.Getter;
import lombok.Setter;

/***
 * 商务炫铃 增加用户失败解析实体
 * 
 * @date 2018-09-30
 * @author SJHTQYCL001
 *
 */
public class SwxlAddPhoneFailInfo implements java.io.Serializable {
	private static final long serialVersionUID = -839818860515629213L;
	@Getter @Setter
	private String msisdn;// 电话号码
	@Getter @Setter
	private String info;// 失败信息

}
