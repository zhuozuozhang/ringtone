package com.hrtxn.ringtone.project.numcertification.json;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @param
 * @Author zcy
 * @Date 2019-08-29 13:58
 * @return
 */
@Data
public class NumBaseDataResult implements Serializable {



    private List<NumDataResult> dataList;

    private String dataCount;


}
