package com.example.springdb.study.jpabook.ch7_advanced_mapping.practice

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("M")
class Ch7Movie : Ch7Item() {
    var director: String? = null
    var actor: String? = null
}