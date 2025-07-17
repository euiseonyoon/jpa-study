package com.example.springdb.study.jpabook.ch7_advanced_mapping.examples.super_type_sub_type_relationship.single_table_strategy

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("B")
// @PrimaryKeyJoinColumn(name= "book_id")  InheritanceType.JOINED 에서만 사용가능
class Ch7V2Book : Ch7V2Item() {
    var author: String? = null
}