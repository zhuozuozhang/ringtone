package com.hrtxn.ringtone.project.threenets.kedas.kedapublic.mapper;

import com.hrtxn.ringtone.project.threenets.kedas.kedapublic.domain.KedaPublicOnlineOrder;
import org.springframework.stereotype.Repository;

/**
 * @Author:zcy
 * @Date:2019-08-24 16:37
 * @Description:商务彩铃在线办理订单
 */
@Repository
public interface KedaPublicOnlineOrderMapper {
    
    /**
     * @Author zcy
     * @Date 2019-8-24 16:44
     * @Description 添加商务彩铃在线办理订单
     */
    int insertOnlieOrder(KedaPublicOnlineOrder kedaPublicOnlineOrder);
}
