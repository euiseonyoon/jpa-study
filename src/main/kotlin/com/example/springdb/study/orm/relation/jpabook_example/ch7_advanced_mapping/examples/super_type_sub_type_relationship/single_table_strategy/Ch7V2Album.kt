package com.example.springdb.study.orm.relation.jpabook_example.ch7_advanced_mapping.examples.super_type_sub_type_relationship.single_table_strategy

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("A")
class Ch7V2Album : Ch7V2Item() {
    var artist: String? = null
}