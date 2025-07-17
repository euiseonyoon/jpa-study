package com.example.springdb.study.jpabook.ch7_advanced_mapping.practice

import jakarta.persistence.Embeddable

@Embeddable
class Ch7Address {
    var city: String? = null
    var street: String? = null
    var zipcode: String? = null

    constructor(city: String, street: String, zipcode: String) {
        this.city = city
        this.street = street
        this.zipcode = zipcode
    }
}