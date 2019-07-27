package com.hrtxn.ringtone.project.threenets.threenet.json.swxl;

import lombok.Getter;
import lombok.Setter;

public class SwxlBaseBackMessage<T> {
	@Getter @Setter private String  recode;   
	@Getter @Setter private String  message; 
	@Getter @Setter private T data;//返回对象
	@Getter @Setter private boolean success;
	
}
