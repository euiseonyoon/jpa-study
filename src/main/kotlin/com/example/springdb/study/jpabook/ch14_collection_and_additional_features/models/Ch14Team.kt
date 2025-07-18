package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.NamedAttributeNode
import jakarta.persistence.NamedEntityGraph
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy

@NamedEntityGraph(
    name = "Ch14Team.withPlayers",
    attributeNodes = [
        NamedAttributeNode("players")
    ]
)
@Entity
class Ch14Team {
    @Id @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    // Ch14Player의 필드명 일치하게
    @OrderBy("salary desc, id asc")
    @OneToMany(mappedBy = "team", cascade = [CascadeType.ALL], orphanRemoval = true)
    var players: MutableSet<Ch14Player> = mutableSetOf()

    constructor()
    constructor(name: String) {
        this.name = name
    }
}
