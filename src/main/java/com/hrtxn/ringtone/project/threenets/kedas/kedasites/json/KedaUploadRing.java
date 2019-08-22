package com.hrtxn.ringtone.project.threenets.kedas.kedasites.json;

import lombok.Data;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-08-21 9:30
 * Description:上传铃音
 *{
 *     "retCode":"000000",
 *     "retMsg":"成功",
 *     "exDesc":null,
 *     "data":[
 *         {
 *             "fileName":"rBBGdV1WG0qARs_4AAsY34HPgZY597.mp3",
 *             "fileSize":"710 Kb",
 *             "fileType":"mp3",
 *             "realName":"慧科001.mp3",
 *             "fileUrl":"http://file.kuyinyun.com/group1/M00/60/72/rBBGdV1WG0qARs_4AAsY34HPgZY597.mp3"
 *         }
 *     ],
 *     "data2":null
 * }
 */
@Data
public class KedaUploadRing implements Serializable {

    private String fileName;
    private String fileSize;
    private String fileType;
    private String realName;
    private String fileUrl;


}
