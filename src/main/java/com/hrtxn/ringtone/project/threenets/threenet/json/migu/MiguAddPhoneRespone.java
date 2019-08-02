package com.hrtxn.ringtone.project.threenets.threenet.json.migu;


import lombok.Data;

import java.util.List;

/**
 *
 *
 *
 * {"ret":"000000","list":[],"successCount":1,"successfulMsisdns":"15062107713","failedCount":0,"leftMemberAddNum":498}
 */
@Data
public class MiguAddPhoneRespone {
    private String ret;
    private String successCount;
    private String successfulMsisdns;
    private String failedCount;
    private String leftMemberAddNum;
    private List list;
}
