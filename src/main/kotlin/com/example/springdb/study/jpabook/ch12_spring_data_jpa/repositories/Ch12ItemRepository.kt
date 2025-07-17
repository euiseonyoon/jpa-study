package com.example.springdb.study.jpabook.ch12_spring_data_jpa.repositories

import com.example.springdb.study.jpabook.ch12_spring_data_jpa.models.Ch12Item
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections.Ch12ItemNameOnlyDto
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections.Ch12ItemStockQuantityOnly
import org.springframework.data.jpa.repository.JpaRepository

interface Ch12ItemRepository : JpaRepository<Ch12Item, Long> {

    // @Query, @NativeQuery가 아닌 Derived queries -> Interface 예시
    // https://docs.spring.io/spring-data/jpa/reference/repositories/projections.html#_derived_queries
    fun findByName(name: String): List<Ch12ItemStockQuantityOnly>

    // @Query, @NativeQuery가 아닌 Derived queries -> Class Dto 예시
    fun findByPrice(price: Int): List<Ch12ItemNameOnlyDto>
}