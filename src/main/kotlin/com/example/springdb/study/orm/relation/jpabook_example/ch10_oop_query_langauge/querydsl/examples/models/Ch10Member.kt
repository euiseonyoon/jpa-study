package com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Ch10Member {
    @Id @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var name: String? = null
}