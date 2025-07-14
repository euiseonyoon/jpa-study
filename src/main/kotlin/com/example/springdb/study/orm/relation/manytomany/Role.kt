package com.example.springdb.study.orm.relation.manytomany

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Transient

@Entity
class Role {
    @Id @GeneratedValue
    val id: Long? = null
    var name: String? = null

    constructor()
    constructor(name: String) {
        this.name = name
    }

    @OneToMany(mappedBy = "role", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val workerRoleMap: MutableSet<WorkerRole> = mutableSetOf()

    // 반드시 @Transient해줘야 함. 안그러면 JPA가 해당 컬렉션을 매핑 대상으로 생각합.
    @Transient
    val workers: List<Worker> = workerRoleMap.map { it.worker!! }

    fun addWorkerRole(workerRole: WorkerRole) {
        this.workerRoleMap.add(workerRole)
    }
}