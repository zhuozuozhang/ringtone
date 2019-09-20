package com.hrtxn.ringtone.project.system.dict.mapper;

import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.dict.domain.Dict;
import com.hrtxn.ringtone.project.system.user.domain.Menu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author:lxp
 * Date:2019-08-08 12:07
 * Description:字典访问层
 */
@Repository
public interface DictMapper {

    public int insert(Dict dict);

    public int update(Dict dict);

    public List<Dict> pageDictList(@Param("page") Page page,@Param("dict") Dict dict);

    public List<Dict> queryDictByType(String type);

    public Dict getDcitByTypeAndCode(String type,String code);

    public Dict getDictByTypeAndName(String type,String name);

    public int getDictCount(@Param("dict") Dict dict);

    public int delete(Integer id);

    public Dict getDictById(Integer id);

}
