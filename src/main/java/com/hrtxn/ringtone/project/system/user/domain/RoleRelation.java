package com.hrtxn.ringtone.project.system.user.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Author:zcy
 * Date:2019-08-08 11:58
 * Description:用户--菜单关系
 */
@Data
public class RoleRelation implements Serializable {
    private Integer id;

    private Integer userId;

    private Integer menuId;

}