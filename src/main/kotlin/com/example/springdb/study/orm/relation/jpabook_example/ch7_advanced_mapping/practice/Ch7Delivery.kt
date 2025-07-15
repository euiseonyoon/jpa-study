package com.example.springdb.study.orm.relation.jpabook_example.ch7_advanced_mapping.practice

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToOne

@Entity
class Ch7Delivery : RegisterUpdateBaseEntity() {
    @Id @GeneratedValue
    val id: Long? = null

    @Enumerated(EnumType.STRING)
    var status: Ch7DeliveryStatus? = null

    @Embedded
    var address: Ch7Address? = null

    @OneToOne(mappedBy = "delivery")
    var order: Ch7Order? = null
}

