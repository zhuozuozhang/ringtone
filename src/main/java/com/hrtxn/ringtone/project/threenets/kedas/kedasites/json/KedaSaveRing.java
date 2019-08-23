package com.hrtxn.ringtone.project.threenets.kedas.kedasites.json;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Author:zcy
 * Date:2019-08-21 10:48
 * Description:铃音保存
 */
@Data
public class KedaSaveRing implements Serializable {

    private BigInteger id;
    private String name;
    private String url;

}
