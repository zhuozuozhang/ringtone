package com.hrtxn.ringtone.project.system.toolboxs.ringContent.mapper;

import com.hrtxn.ringtone.common.domain.BaseRequest;
import com.hrtxn.ringtone.common.domain.Page;
import com.hrtxn.ringtone.project.system.toolboxs.ringContent.domain.RingContent;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author zcy
 * @Date 2019-09-04 13:27
 */
@Repository
public interface RingContentMapper {

    List<RingContent> selectRingContent(@Param("page") Page page, @Param("param") BaseRequest baseRequest);

    int getCount(@Param("param") BaseRequest baseRequest);

    int deleteRingContent(Integer id);

    int insertRingContent(RingContent ringContent);

    int updateRingContent(RingContent ringContent);
}
