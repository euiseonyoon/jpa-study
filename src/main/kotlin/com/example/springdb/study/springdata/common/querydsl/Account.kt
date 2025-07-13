package com.example.springdb.study.springdata.common.querydsl

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id


/*
*
* */
@Entity
class Account(
    @Id @GeneratedValue
    val id: Long = 0,
    var firstName: String = "",
    var lastName: String = "",
) {

}