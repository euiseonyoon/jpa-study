package com.example.JPA.study.springdata.jpa.specification

import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query

interface Store2Repository : JpaRepository<Store2, Long>, JpaSpecificationExecutor<Store2> {

    // 여기서는 findWithSpecAndEntityGraph 같이 custom한 이름이 안된다.
    // 왜냐면 specification을 써서 findAll으로 생성된 (SELECT * FROM store2)에 기본으로 만들어진 where 절을 추가해야되서.
    @EntityGraph(value = "Store2.withProducts", type = EntityGraph.EntityGraphType.FETCH)
    override fun findAll(spec: Specification<Store2>?): List<Store2>

    @EntityGraph(value = "Store2.withProducts", type = EntityGraph.EntityGraphType.FETCH)
    override fun findAll(): List<Store2>


    /**
     *
     *     @EntityGraph(value = "Store2.withProducts", type = EntityGraph.EntityGraphType.LOAD)
     *     @Query("SELECT s FROM Store s")
     *     fun finWithSpecification(spec: Specification<Store2>?): List<Store2>
     *     는 안된다. @Query가 붙은데는 specification 지원 안됨
     *
     */
}
