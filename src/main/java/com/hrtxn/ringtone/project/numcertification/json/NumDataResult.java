package com.hrtxn.ringtone.project.numcertification.json;

import lombok.Data;

import java.io.Serializable;

/**
 * @param
 * @Author zcy
 * @Date 2019-08-29 13:59
 * @return
 */
@Data
public class NumDataResult implements Serializable {

    private String platform;

    private String category;

    private String phoneNum;

    private String source;

    private String status;

    private String formatNumber;

    private String type;

    private String typeId;

    private Integer cost;

    private Double agentCost;
}
