package com.example.springdb.study.springdata.mystudy

import jakarta.persistence.EntityManagerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class MyTransactionManagerConfig {

    @Bean(name = ["myTransactionManager"])
    fun myTransactionManager(): PlatformTransactionManager = MyTransactionManager()

    @Bean
    @Primary
    fun transactionManager(emf: EntityManagerFactory): PlatformTransactionManager {
        return JpaTransactionManager(emf)
    }
}
