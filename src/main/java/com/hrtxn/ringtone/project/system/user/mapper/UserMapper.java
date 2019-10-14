package com.hrtxn.ringtone.project.system.user.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.user.domain.User;
import com.hrtxn.ringtone.project.system.user.domain.UserVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-01 15:52
 * Description:用户数据处理层
 */
@Repository
public interface UserMapper {

    /**
     * 根据用户名查询用户信息
     *
     * @param username
     * @return
     */
    User findUserByUserName(String username) throws Exception;

    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    int updateUserById(User user);

    /**
     * 获取用户列表
     *
     * @return
     * @throws Exception
     */
    List<UserVo> getUserList(@Param("page") Page page, @Param("baseRequest") BaseRequest baseRequest) throws Exception;

    /**
     * 根据ID获取用户信息
     *
     * @param id
     * @return
     * @throws Exception
     */
    User findUserById(Integer id) throws Exception;

    /**
     * 根据电话号码获取用户信息
     *
     * @param phone
     * @return
     * @throws Exception
     */
    User findUserByUserTel(String phone) throws Exception;

    /**
     * 根据父级ID获取用户信息
     *
     * @param id
     * @return
     */
    List<UserVo> findUserByparentId(
            @Param("page") Integer page,
            @Param("pagesize") Integer pagesize,
            @Param("id") Integer id, @Param("userName") String userName, @Param("userTel") String userTel
    ) throws Exception;

    List<User> findChildUser(@Param("id") Integer id, @Param("name") String name) throws Exception;

    /**
     * 获取子账号总数
     *
     * @param parentId
     * @return
     */
    int getUserCount(@Param("parentId") Integer parentId, @Param("userName") String userName, @Param("userTel") String userTel) throws Exception;

    /**
     * 添加账号
     *
     * @param user
     * @return
     */
    int insertUser(User user);

    int getUserCountByCon(@Param("baseRequest") BaseRequest baseRequest);

    List<User> findAllUser();

    List<User> isBelongsTo022();
}
