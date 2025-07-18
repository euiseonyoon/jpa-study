package com.example.springdb.study.jpabook.ch7_advanced_mapping.examples.super_type_sub_type_relationship.join_strategy

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("A")
class Ch7V1Album : Ch7V1Item() {
    var artist: String? = null
}
