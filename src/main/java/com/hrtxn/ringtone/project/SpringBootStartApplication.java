package com.hrtxn.ringtone.project;

import com.hrtxn.ringtone.RingtoneApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Author:lile
 * Date:2019-09-30 14:57
 * Description:
 */

public class SpringBootStartApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder){
        return builder.sources(RingtoneApplication.class);
    }
}
