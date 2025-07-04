package com.example.JPA.study.orm.relation.manytomany

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
class Worker(
    @Id @GeneratedValue
    val id: Long? = null,

    val name: String
) {
    @OneToMany(mappedBy = "worker", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val workerRoleMap: MutableSet<WorkerRole> = mutableSetOf()
    // hibernate가 getter 없으면 인식 못함
    val roles: List<Role>
        get() = workerRoleMap.map { it.role }

    fun addWorkerRole(workerRole: WorkerRole) {
        this.workerRoleMap.add(workerRole)
    }
}