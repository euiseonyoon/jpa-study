package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.repositories

import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models.Ch14Order
import org.springframework.data.jpa.repository.JpaRepository

interface Ch14OrderRepository : JpaRepository<Ch14Order, Long> {
}
