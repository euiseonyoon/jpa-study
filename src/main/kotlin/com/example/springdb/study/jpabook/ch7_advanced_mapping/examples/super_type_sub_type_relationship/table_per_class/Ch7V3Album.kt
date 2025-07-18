package com.example.springdb.study.jpabook.ch7_advanced_mapping.examples.super_type_sub_type_relationship.table_per_class

import jakarta.persistence.Entity

@Entity
class Ch7V3Album : Ch7V3Item() {
    var artist: String? = null
}
