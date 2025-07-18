package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.repositories

import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models.Ch14Item
import org.springframework.data.jpa.repository.JpaRepository

interface Ch14ItemRepository : JpaRepository<Ch14Item, Long> {

    fun findByName(name: String): List<Ch14Item>
}
