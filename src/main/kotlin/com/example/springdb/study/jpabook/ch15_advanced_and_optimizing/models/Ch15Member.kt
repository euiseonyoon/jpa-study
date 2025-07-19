package com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Ch15Member {
    @Id
    @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    constructor()
    constructor(name: String) {
        this.name = name
    }
}