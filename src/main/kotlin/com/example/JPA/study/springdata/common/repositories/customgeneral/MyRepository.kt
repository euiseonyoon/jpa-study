package com.example.JPA.study.springdata.common.repositories.customgeneral

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

// 모든 entity에 적용될 공통
// NOTE: 이거 중요!!
@NoRepositoryBean
interface MyRepository<T, ID : Serializable>: JpaRepository<T, ID> {

    // persistent context에 있는지 여부
    fun contains(entity: T): Boolean
}
