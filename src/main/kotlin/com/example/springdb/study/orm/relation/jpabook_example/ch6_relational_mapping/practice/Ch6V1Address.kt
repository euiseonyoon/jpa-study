package com.example.springdb.study.orm.relation.jpabook_example.ch6_relational_mapping.practice

import jakarta.persistence.Embeddable

@Embeddable
class Ch6V1Address {
    var city: String? = null
    var street: String? = null
    var zipcode: String? = null
}