package com.example.springdb.study.orm.relation.onetomany.jointable

import jakarta.persistence.Embeddable

@Embeddable
class Address {
    constructor()

    constructor(city: String, zipCode: String, street: String) {
        this.city = city
        this.street = street
        this.zipCode = zipCode
    }

    var city: String? = null
    var zipCode: String? = null
    var street: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Address
        return city == other.city &&
            zipCode == other.zipCode &&
            street == other.street
    }

    override fun hashCode(): Int {
        var result = city?.hashCode() ?: 0
        result = 31 * result + (zipCode?.hashCode() ?: 0)
        result = 31 * result + (street?.hashCode() ?: 0)
        return result
    }
}
