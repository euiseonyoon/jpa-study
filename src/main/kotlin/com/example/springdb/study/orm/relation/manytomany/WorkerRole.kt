package com.example.springdb.study.orm.relation.manytomany

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId

@Entity
class WorkerRole{
    @EmbeddedId
    var workerRoleId: WorkerRoleId = WorkerRoleId()

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("workerId") // WorkerRoleId.workerId 필드와 이름이 동일해야함
    @JoinColumn(name = "worker_id") // 양방향 관계의 주인
    var worker: Worker? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId") // WorkerRoleId.roleId 필드와 이름이 동일해야함
    @JoinColumn(name = "role_id") // 양방향 관계의 주인
    var role: Role? = null

    constructor(worker: Worker, role: Role) {
        requireNotNull(worker.id) { "Worker must have an ID (persisted entity)" }
        requireNotNull(role.id) { "Role must have an ID (persisted entity)" }

        this.workerRoleId = WorkerRoleId(worker.id!!, role.id!!)
        if(this.worker != worker) {
            this.worker = worker
        }
        if(this.role != role) {
            this.role = role
        }

        // 양방향 관계 설정
        worker.addWorkerRole(this)
        role.addWorkerRole(this)
    }

    companion object {
        fun addWorker(worker: Worker, roles: List<Role>): List<WorkerRole> {
            return roles.map { role ->
                WorkerRole(worker, role)
            }
        }
    }
}