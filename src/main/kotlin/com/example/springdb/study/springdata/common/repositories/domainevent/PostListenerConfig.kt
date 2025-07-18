package com.example.springdb.study.springdata.common.repositories.domainevent

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PostListenerConfig {

    @Bean
    fun postListener(): PostEventListener {
        return PostEventListener()
    }
}
