package com.example.springdb.study.orm.relation.jpabook_example.ch6_relational_mapping.practice

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Ch6V1Member {
    @Id @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    @Embedded
    @Column(nullable = false)
    var address: Ch6V1Address? = null

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    var orders: MutableSet<Ch6V1Order> = mutableSetOf()
}

