package com.example.springdb.study.jpabook.ch6_relational_mapping

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToOne

@Entity
class Ch6V1Delivery {
    @Id @GeneratedValue
    val id: Long? = null

    @Enumerated(EnumType.STRING)
    var status: Ch6V1DeliveryStatus? = null

    @Embedded
    var address: Ch6V1Address? = null

    @OneToOne(mappedBy = "delivery")
    var order: Ch6V1Order? = null
}

