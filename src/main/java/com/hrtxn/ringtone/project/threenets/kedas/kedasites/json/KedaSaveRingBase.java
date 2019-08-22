package com.hrtxn.ringtone.project.threenets.kedas.kedasites.json;

import lombok.Data;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-08-21 10:44
 * Description:暂存铃音外部
 */
@Data
public class KedaSaveRingBase implements Serializable {

    private String ringContent;
    private String phoneList;


}
