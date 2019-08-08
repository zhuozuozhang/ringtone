package com.hrtxn.ringtone.project.system.user.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-08-08 11:58
 * Description:菜单
 */
@Data
public class Menu implements Serializable {
    private Integer id;
    private String menu;
    /************************VO***************************/
    // 判断是否选中
    private Boolean check;
}
