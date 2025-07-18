package com.example.springdb.study.jpabook.ch7_advanced_mapping.practice

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Ch7Member : RegisterUpdateBaseEntity {
    @Id @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    @Embedded
    @Column(nullable = false)
    var address: Ch7Address? = null

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var orders: MutableSet<Ch7Order> = mutableSetOf()

    constructor() : super()
    constructor(name: String, address: Ch7Address) {
        this.name = name
        this.address = address
    }
}
