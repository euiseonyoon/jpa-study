package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models

import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.converter.Ch14PlayerDto
import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.converter.Ch14PlayerVarsityConverter
import jakarta.persistence.Column
import jakarta.persistence.ColumnResult
import jakarta.persistence.ConstructorResult
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SqlResultSetMapping

@SqlResultSetMapping(
    name = "Ch14PlayerNativeQueryDto",
    classes = [
        ConstructorResult(
            targetClass = Ch14PlayerDto::class,
            columns = [
                // 위의 Dto의 생성자의 param 순서와 동일 해야한다.
                // name은 db의 컬럼명과 동일해야 함.
                ColumnResult(name = "id", type = Long::class),
                ColumnResult(name = "varsity_status", type = String::class),
            ]
        )
    ]
)
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

    @Convert(converter = Ch14PlayerVarsityConverter::class)
    var varsityStatus: Boolean = false

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
