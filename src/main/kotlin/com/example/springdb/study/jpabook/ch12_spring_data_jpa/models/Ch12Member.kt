package com.example.springdb.study.jpabook.ch12_spring_data_jpa.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.NamedQuery
import jakarta.persistence.OneToMany

@Entity
@NamedQuery(
    name = "Ch12Member.searchByUsername",
    query = "SELECT m FROM Ch12Member m WHERE m.name=:username"
)
class Ch12Member {
    @Id @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var orders: MutableSet<Ch12Order> = mutableSetOf()
}
