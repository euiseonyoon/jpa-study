package com.example.springdb.study.orm.relation.onetone

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToOne

@Entity
class Passport(
    @Id @GeneratedValue
    val id: Long = 0,
    val number: String
) {
    @OneToOne(mappedBy = "passport")
    var owner: Person? = null
}