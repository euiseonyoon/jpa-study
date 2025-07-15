package com.example.springdb.study.orm.relation.jpabook_example.ch7_advanced_mapping.examples.mappedsuperclass

import jakarta.persistence.Entity

@Entity
class Ch7V1Seller : Ch7V1BaseEntity() {
    // id, name을 상속
    var shopName: String? = null
}