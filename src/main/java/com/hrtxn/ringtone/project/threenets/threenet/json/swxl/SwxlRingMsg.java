package com.hrtxn.ringtone.project.threenets.threenet.json.swxl;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class SwxlRingMsg implements java.io.Serializable {
	
	private static final long serialVersionUID = 3359446463172760720L;
	
	@JsonProperty("groupId")
	@Getter @Setter private String groupId;//集团ID
	@JsonProperty("ringName")
	@Getter @Setter private String ringName;//铃音名称
	@JsonProperty("ctime")
	@Getter @Setter private String ctime;//创建时间
	@JsonProperty("remark")
	@Getter @Setter private String remark;//备注
	@JsonProperty("id")
	@Getter @Setter private String id;//铃音ID
	@JsonProperty("ringFilePath")
	@Getter @Setter private String ringFilePath;//商务炫铃铃音存放地址
	@JsonProperty("status")
	@Getter @Setter private String status;// 1: 审核中 2：审核通过 3：审核不通过  4：分发失败 9：激活成功
	
	//查询铃音，取得的内容：{"recode":"000000","message":"成功","data":{"data":[{"id":"9456600020190305475833","ringFilePath":"/ring/2019/03/05/c05631ce85b14b339e2f66b231a7b6a7.mp3","groupId":"b9454884419746f79d6b1ff9319fd4fd","ringName":"上海汉霆激光科技有限公司","ctime":"2019-03-05 19:32:45","status":"2","remark":"审核通过"}],"recordsTotal":1},"success":true}

	

}
