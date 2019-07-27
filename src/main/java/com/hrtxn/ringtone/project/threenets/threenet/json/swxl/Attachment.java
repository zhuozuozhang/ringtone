package com.hrtxn.ringtone.project.threenets.threenet.json.swxl;

import lombok.Data;

/**
 * Author:lile
 * Date:2019/7/26 15:01
 * Description:
 */
@Data
public class Attachment {
    private String attId;
    private String attName;
    private String attPath;
    private String attaType;
    private String tnId;
    private String tnCheckId;
    private String orderId;//协议id(审核通过意向单)
    private String anthName;

    private String xh;//判断新增成果的时候，删除行的标识。
}
