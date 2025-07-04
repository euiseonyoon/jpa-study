package com.example.JPA.study.orm.relation.onetomany.jointable

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Team(
    @Id @GeneratedValue
    val id: Long = 0,

    var name: String = ""
) {
    // 팀이 없으면 join table(Membership)에서 member도 없어져야함
    @OneToMany(mappedBy = "team", cascade = [CascadeType.ALL], orphanRemoval = true)
    val memberships: MutableSet<Membership> = mutableSetOf()
}
