package com.hrtxn.ringtone.project.threenets.threenet.json.swxl;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-08-01 13:56
 * Description:商务炫铃 查询铃音分发情况  --放音平台下发到各省
 * {"recode":"000000","message":"成功","data":{"data":[{"successTime":"2019-03-05 21:07:14","ringId":"9456600020190305475833","ctime":"2019-03-05 20:43:17","id":"bddf4ce2b98f4c339eb7e125d260a72a","provinceName":"集中","provinceId":"00","deployStatus":0},{"successTime":"2019-03-05 21:15:18","ringId":"9456600020190305475833","ctime":"2019-03-05 20:43:17","id":"c768987688424d4fba74d7cedf322124","provinceName":"上海","provinceId":"31","deployStatus":0}],"recordsTotal":2},"success":true}
 */
public class SwxlRingAreaFenFaInfo implements Serializable {

	//获取铃音返回内容:
	@Setter @Getter private String provinceId;//省份ID
	@Setter @Getter private String ctime;//记录时间
	@Setter @Getter private String id;//主键 
	@Setter @Getter private String ringId;//铃音ID
	@Setter @Getter private String successTime;//分发成功时间
	@Setter @Getter private int deployStatus;//分发状态 0激活成功 1 激活中 3重新激活中
	@Setter @Getter private String provinceName;//省份名称
	@Setter @Getter private String responseCode;
	@Setter @Getter private String responseDesc;
}
