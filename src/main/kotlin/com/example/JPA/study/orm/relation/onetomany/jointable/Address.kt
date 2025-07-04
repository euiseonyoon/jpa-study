package com.example.JPA.study.orm.relation.onetomany.jointable

import jakarta.persistence.Embeddable

@Embeddable
class Address(
    var city: String = "",
    val zipCode: String = "",
    val street: String = "",
) {
}
