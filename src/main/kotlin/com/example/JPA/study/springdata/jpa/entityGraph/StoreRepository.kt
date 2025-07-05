package com.example.JPA.study.springdata.jpa.entityGraph

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface StoreRepository : JpaRepository<Store, Long> {

    @EntityGraph(value = "Store.withProducts", type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT s FROM Store s")
    fun findAllWithProducts(): List<Store>
}