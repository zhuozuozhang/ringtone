package com.hrtxn.ringtone.project.threenets.threenet.json.migu;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 咪咕更新彩铃，返回对象
 * @author mutouyang
 * http://211.137.107.18:8888/cm/userpay!refreshCrbtStatus.action
 *{"msg":"更新彩铃状态成功","crbtStatus":1,"success":true}
 */
public class RefreshCrbtStatusRespone  implements Serializable {
	@Getter @Setter private String msg;
	@Getter @Setter	private String crbtStatus;
	@Getter @Setter private boolean success;
}
