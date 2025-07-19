package com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("B")
class Ch15Book : Ch15Item() {
    var author: String? = null
    var isbn: String? = null

    override fun getTitle(): String = "book: author={$author}, isbn={$isbn}"

    override fun getTarget(): Any = this
}
