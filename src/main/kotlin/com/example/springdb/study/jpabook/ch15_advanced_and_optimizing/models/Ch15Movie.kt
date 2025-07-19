package com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models

import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.interfaces.Ch15Visitor
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("M")
class Ch15Movie : Ch15Item() {
    var director: String? = null
    var actor: String? = null

    override fun getTitle(): String = "movie: director={$director}, actor={$actor}"

    override fun getTarget(): Any = this

    override fun acceptVisitor(visitor: Ch15Visitor) {
        visitor.visitMovie(this)
    }
}
