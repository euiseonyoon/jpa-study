package com.example.springdb.study.jpabook.ch6_relational_mapping

import jakarta.persistence.Embeddable

@Embeddable
class Ch6V1Address {
    var city: String? = null
    var street: String? = null
    var zipcode: String? = null
}
