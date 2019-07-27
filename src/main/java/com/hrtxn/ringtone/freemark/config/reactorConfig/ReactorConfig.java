package com.hrtxn.ringtone.freemark.config.reactorConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.spring.context.config.EnableReactor;

/**
 * Author:zcy
 * Date:2019-06-25 9:17
 * Description:reactor 配置
 */
@Configuration
@EnableReactor
public class ReactorConfig {
    @Bean
    Environment env() {
        return new Environment();
    }

    @Bean(name = "createReactor")
    Reactor createReactor(Environment env) {
        return Reactors.reactor()
            .env(env)
            .dispatcher(Environment.RING_BUFFER)
            .get();
    }
}
