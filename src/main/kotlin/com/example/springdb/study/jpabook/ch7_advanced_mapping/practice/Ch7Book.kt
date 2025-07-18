package com.example.springdb.study.jpabook.ch7_advanced_mapping.practice

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("B")
class Ch7Book : Ch7Item() {
    var author: String? = null
    var isbn: String? = null
}
