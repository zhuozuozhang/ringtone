package com.hrtxn.ringtone.project.system.user.mapper;

import com.hrtxn.ringtone.project.system.user.domain.RoleRelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-08-08 12:13
 * Description:用户--菜单关系
 */
@Repository
public interface RoleRelationMapper {
    /**
     * 获取当前用户拥有的菜单
     *
     * @param id
     * @return
     */
    List<RoleRelation> findRoleRelationByUserId(@Param("id") Integer id);

    /**
     *  删除用户对应的菜单
     *
     * @param userId
     * @return
     */
    int deleteRoleRelationByUserId(@Param("userId") Integer userId);

    int insertRoleRelation(@Param("userId") Integer userId, @Param("menuArr") int[] menuArr);
}
