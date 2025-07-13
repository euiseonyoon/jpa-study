package com.example.springdb.study.springdata.mystudy.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Rock(
    @Id @GeneratedValue
    val id: Long? = null,
    val name: String,
    val weightKgs: Int,
) {

}