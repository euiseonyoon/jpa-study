package com.example.springdb.study.springtx.propagation

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Log(
    val message: String
) {
    @Id @GeneratedValue
    val id: Long? = null
}