package com.example.springdb.study.jpabook.ch7_advanced_mapping.examples.super_type_sub_type_relationship.table_per_class

import jakarta.persistence.Entity

@Entity
// @PrimaryKeyJoinColumn(name= "book_id")  InheritanceType.JOINED 에서만 사용가능
class Ch7V3Book : Ch7V3Item() {
    var author: String? = null
}