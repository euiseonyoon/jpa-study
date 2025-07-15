package com.example.springdb.study.orm.relation.jpabook_example.ch7_advanced_mapping.examples.super_type_sub_type_relationship.join_strategy

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("M")
class Ch7V1Movie : Ch7V1Item() {
    var director: String? = null
}