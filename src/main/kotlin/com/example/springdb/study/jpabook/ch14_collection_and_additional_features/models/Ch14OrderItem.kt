package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["order", "item"])
    ]
)
class Ch14OrderItem {
    @Id @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var price: Int? = null

    @Column(nullable = false)
    var count: Int? = null

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    var order: Ch14Order? = null

    // 엔티티 그래프의 동작을 명확히 보기 위해, 의도적으로 FetchType.LAZY를 설정했다.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    var item: Ch14Item? = null
}
