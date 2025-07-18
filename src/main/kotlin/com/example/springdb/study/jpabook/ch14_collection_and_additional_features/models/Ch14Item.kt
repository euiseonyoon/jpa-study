package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models

import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.listener.Ch14ItemListener
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

/**
 * 엔티티에 리스너를 직접 적용 할 수도 있고
 * 아래 처럼 리스너들에 추가를 할 수 도 있다.
 * */
@EntityListeners(Ch14ItemListener::class)
@Entity
class Ch14Item {
    @Id @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    var price: Int = 0
    var stockQuantity: Int = 0

    constructor()
    constructor(name: String, price: Int, stockQuantity: Int) {
        this.name = name
        this.price = price
        this.stockQuantity = stockQuantity
    }
}