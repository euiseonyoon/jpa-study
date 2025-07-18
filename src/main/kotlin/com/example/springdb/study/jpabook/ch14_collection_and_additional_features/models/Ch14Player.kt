package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Ch14Player {
    @Id @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    @Column(nullable = false)
    var salary: Int? = null

    @ManyToOne
    @JoinColumn(name = "team_id")
    var team: Ch14Team? = null

    constructor()
    constructor(name: String, salary: Int){
        this.name = name
        this.salary = salary
    }

    fun addTeam(team: Ch14Team) {
        if (this.team != null) {
            this.team = team
        }
        team.players.add(this)
    }
}
