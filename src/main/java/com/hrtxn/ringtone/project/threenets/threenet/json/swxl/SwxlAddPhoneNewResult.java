package com.hrtxn.ringtone.project.threenets.threenet.json.swxl;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class SwxlAddPhoneNewResult {
	@Getter @Setter private List<SwxlAddPhoneFailInfo> failedList;//号码返回信息'
	@Getter @Setter private List<String> successfulList;//成功返回信息
}
