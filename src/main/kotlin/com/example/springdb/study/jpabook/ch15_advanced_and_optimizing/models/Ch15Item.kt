package com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models

import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.interfaces.Ch15ItemProxyInterface
import com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.interfaces.Ch15Visitor
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.OneToMany

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
abstract class Ch15Item : Ch15ItemProxyInterface {
    @Id
    @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    @Column(nullable = false)
    var price: Int? = null

    @Column(nullable = false)
    var stockQuantity: Int? = null

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var orderItems: MutableSet<Ch15OrderItem> = mutableSetOf()

    abstract fun acceptVisitor(visitor: Ch15Visitor)
}
