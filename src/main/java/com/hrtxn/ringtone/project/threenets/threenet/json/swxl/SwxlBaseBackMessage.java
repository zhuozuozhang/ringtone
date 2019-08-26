package com.hrtxn.ringtone.project.threenets.threenet.json.swxl;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SwxlBaseBackMessage<T> {
	private String  recode;
	private String  message;
	private T data;//返回对象
	private boolean success;
	
}
