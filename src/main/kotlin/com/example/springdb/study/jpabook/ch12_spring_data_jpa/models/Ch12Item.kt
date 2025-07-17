package com.example.springdb.study.jpabook.ch12_spring_data_jpa.models

import com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections.Ch12ItemNativeDto
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.ColumnResult
import jakarta.persistence.ConstructorResult
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.SqlResultSetMapping

@SqlResultSetMapping(
    name = "Ch12ItemNativeQueryDto",
    classes = [
        ConstructorResult(
            targetClass = Ch12ItemNativeDto::class,
            columns = [
                // 위의 Dto의 생성자의 param 순서와 동일 해야한다.
                // name은 db의 컬럼명과 동일해야 함.
                ColumnResult(name = "price", type = Int::class),
                ColumnResult(name = "name", type = String::class),
                ColumnResult(name = "stock_quantity", type = Int::class),
            ]
        )
    ]
)
@Entity
class Ch12Item {
    @Id @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    var price: Int = 0
    var stockQuantity: Int = 0

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    var orderItems: MutableSet<Ch12OrderItem> = mutableSetOf()

    constructor()
    constructor(name: String, price: Int, stockQuantity: Int) {
        this.name = name
        this.price = price
        this.stockQuantity = stockQuantity
    }
}