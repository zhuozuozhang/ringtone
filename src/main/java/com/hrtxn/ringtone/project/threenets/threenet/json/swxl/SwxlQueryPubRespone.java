package com.hrtxn.ringtone.project.threenets.threenet.json.swxl;

import com.fasterxml.jackson.databind.ser.std.SerializableSerializer;
import lombok.Data;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-25 10:50
 * Description:中间级信息封装
 */
@Data
public class SwxlQueryPubRespone<T> extends SerializableSerializer {

    private String draw;
    private String recordsFiltered;
    private String recordsTotal;
    private List<T> data;
}
