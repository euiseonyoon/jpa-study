package com.example.springdb.study.orm.relation.jpabook_example.ch7_advanced_mapping.practice

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany

@Entity
class Ch7Item {
    @Id @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    @Column(nullable = false)
    var price: Int? = null

    @Column(nullable = false)
    var stockQuantity: Int? = null

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    var orderItems: MutableSet<Ch7OrderItem> = mutableSetOf()

    @ManyToMany(mappedBy = "items") // 그냥 양방향 커넥션의 주인을 카테고리로 정했다.
    var categories: MutableSet<Ch7Category> = mutableSetOf()
}

