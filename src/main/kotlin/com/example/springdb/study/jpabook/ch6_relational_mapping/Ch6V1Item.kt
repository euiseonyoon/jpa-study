package com.example.springdb.study.jpabook.ch6_relational_mapping

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany

@Entity
class Ch6V1Item {
    @Id @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    @Column(nullable = false)
    var price: Int? = null

    @Column(nullable = false)
    var stockQuantity: Int? = null

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    var orderItems: MutableSet<Ch6V1OrderItem> = mutableSetOf()

    @ManyToMany(mappedBy = "items") // 그냥 양방향 커넥션의 주인을 카테고리로 정했다.
    var categories: MutableSet<Ch6V1Category> = mutableSetOf()
}
