package com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("M")
class Ch15Movie : Ch15Item() {
    var director: String? = null
    var actor: String? = null
}
