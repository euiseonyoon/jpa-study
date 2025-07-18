package com.example.springdb.study.jpabook.ch7_advanced_mapping.examples.super_type_sub_type_relationship.single_table_strategy

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("M")
class Ch7V2Movie : Ch7V2Item() {
    var director: String? = null
}
