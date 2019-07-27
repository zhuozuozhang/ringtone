package com.hrtxn.ringtone.project.system.File.mapper;

import com.hrtxn.ringtone.project.system.File.domain.Uploadfile;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:zcy
 * Date:2019-07-23 16:52
 * Description:文件上传数据访问
 */
@Repository
public interface UploadfileMapper {

    Uploadfile selectByPrimaryKey(Integer id);

    /**
     * 删除
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新增
     * @param Uploadfile
     * @return
     */
    int insertSelective(Uploadfile Uploadfile);

    /**
     * 修改
     * @param Uploadfile
     * @return
     */
    int updateByPrimaryKeySelective(Uploadfile Uploadfile);

    /**
     * 根据路径进行修改
     * @param uploadfile
     * @return
     */
    int updateByPath(Uploadfile uploadfile);
}
