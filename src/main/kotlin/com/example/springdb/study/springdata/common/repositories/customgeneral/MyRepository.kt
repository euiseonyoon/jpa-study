package com.example.springdb.study.springdata.common.repositories.customgeneral

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

// 모든 entity에 적용될 공통
// NOTE: 이거 중요!!  다른 Repository에서 사용될 중간 Repository에는 @NoRepositoryBean를 꼭 넣는다
// e.g.). AccountRepository - MyRepository(중간) - JpaRepository
@NoRepositoryBean
interface MyRepository<T, ID : Serializable> : JpaRepository<T, ID> {

    // persistent context에 있는지 여부
    fun contains(entity: T): Boolean
}
