package com.hrtxn.ringtone.project.system.user.mapper;

import com.hrtxn.ringtone.project.system.user.domain.Menu;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-08 12:07
 * Description:菜单数据访问层
 */
@Repository
public interface MenuMapper {

    List<Menu> findAllMenu();

}
