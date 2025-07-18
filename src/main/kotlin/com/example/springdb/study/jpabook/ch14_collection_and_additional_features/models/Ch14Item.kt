package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models

import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.jpa_listener.Ch14ItemListener
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

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

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var orderItems: MutableSet<Ch14OrderItem> = mutableSetOf()

    constructor()
    constructor(name: String, price: Int, stockQuantity: Int) {
        this.name = name
        this.price = price
        this.stockQuantity = stockQuantity
    }
}
