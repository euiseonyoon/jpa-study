package com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models

import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.interfaces.Ch15Visitor
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("B")
class Ch15Book : Ch15Item() {
    var author: String? = null
    var isbn: String? = null

    override fun getTitle(): String = "book: author={$author}, isbn={$isbn}"

    override fun getTarget(): Any = this

    override fun acceptVisitor(visitor: Ch15Visitor) {
        visitor.visitBook(this)
    }
}
