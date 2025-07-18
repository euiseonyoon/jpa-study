package com.example.springdb.study.jpabook.ch6_relational_mapping

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

@Entity
class Ch6V1Category {
    @Id @GeneratedValue
    val id: Long? = null

    var name: String? = null

    @ManyToOne
    @JoinColumn(name = "parent_id")
    var parent: Ch6V1Category? = null

    @OneToMany(mappedBy = "parent")
    var child: MutableSet<Ch6V1Category> = mutableSetOf()

    @ManyToMany
    @JoinTable(
        name = "category_item",
        joinColumns = [JoinColumn(name = "category_id", nullable = false)],
        inverseJoinColumns = [JoinColumn(name = "item_id", nullable = false)]
    )
    var items: MutableSet<Ch6V1Item> = mutableSetOf()
}
