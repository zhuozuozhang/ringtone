package com.hrtxn.ringtone.project.system.user.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Author:zcy
 * Date:2019-07-05 10:08
 * Description:User 扩展类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserVo extends User  {
    // 父级名称
    private String parentUserName;
    // 开通数量
    private Integer openNum;

}
