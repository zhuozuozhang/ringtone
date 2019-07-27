package com.hrtxn.ringtone.common.domain;

import lombok.Data;

/**
 * Author:lile
 * Date:2019/7/17 17:41
 * Description:
 */
@Data
public class Page {

    public Page() {
    }

    public Page(Integer page, Integer pagesize) {
        this.page = page;
        this.pagesize = pagesize;
    }

    private Integer page;

    private Integer pagesize;
}
