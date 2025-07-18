package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToOne

@Entity
class Ch14Delivery {
    @Id @GeneratedValue
    val id: Long? = null

    @Enumerated(EnumType.STRING)
    var status: Ch14DeliveryStatus? = null

    @OneToOne(mappedBy = "delivery")
    var order: Ch14Order? = null
}
