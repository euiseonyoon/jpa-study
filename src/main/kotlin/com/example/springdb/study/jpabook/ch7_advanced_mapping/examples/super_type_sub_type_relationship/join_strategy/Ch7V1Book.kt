package com.example.springdb.study.jpabook.ch7_advanced_mapping.examples.super_type_sub_type_relationship.join_strategy

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.PrimaryKeyJoinColumn

@Entity
@DiscriminatorValue("B")
@PrimaryKeyJoinColumn(name = "book_id") // ID를 재정의 할 수 있다.
class Ch7V1Book : Ch7V1Item() {
    var author: String? = null
}
