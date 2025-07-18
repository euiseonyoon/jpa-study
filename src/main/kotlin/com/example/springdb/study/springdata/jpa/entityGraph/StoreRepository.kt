package com.example.springdb.study.springdata.jpa.entityGraph

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface StoreRepository : JpaRepository<Store, Long> {

    // 여기서는 findAllWithProducts같은 커스텀 이름이 가능하다  이유는 @Query문을 사용했기 때문에 jpa가 어떤 쿼리를 사용해야 할 지 안다.
    @EntityGraph(value = "Store.withProducts", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT s FROM Store s")
    fun findAllWithProducts(): List<Store>
}
