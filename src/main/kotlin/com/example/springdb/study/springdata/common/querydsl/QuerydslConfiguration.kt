package com.example.springdb.study.springdata.common.querydsl

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.context.annotation.Configuration

// 1. 이거 입력 후, 우측 gradle / Tasks / build / bootJar 더블 클릭
// 2. 좌측 build / generated / source / kapt / main / .. / QAccount 생성 확인
@Configuration
class QuerydslConfiguration(
    @PersistenceContext
    private val entityManager: EntityManager
) {
//    @Bean
//    fun jpaQueryFactory(): JPAQueryFactory = JPAQueryFactory(entityManager)
}
