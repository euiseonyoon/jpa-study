package com.example.springdb.study.orm.relation.manytomany

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId

@Entity
class WorkerRole(
    @EmbeddedId
    val workerRoleId: WorkerRoleId,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("workerId") // WorkerRoleId.workerId 필드와 이름이 동일해야함
    @JoinColumn(name = "worker_id") // 양방향 관계의 주인
    val worker: Worker,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId") // WorkerRoleId.roleId 필드와 이름이 동일해야함
    @JoinColumn(name = "role_id") // 양방향 관계의 주인
    val role: Role
) {
    companion object {
        fun addWorker(worker: Worker, roles: List<Role>): List<WorkerRole> {
            requireNotNull(worker.id) { "Worker must be persisted (have an id)" }

            return roles.map { role ->
                requireNotNull(role.id) { "Role must be persisted (have an id)" }

                val workerRole = WorkerRole(
                    workerRoleId = WorkerRoleId(
                        workerId = worker.id!!,
                        roleId = role.id!!
                    ),
                    worker = worker,
                    role = role
                )

                // 내부 컬렉션에 직접 추가
                worker.addWorkerRole(workerRole)
                role.addWorkerRole(workerRole)

                workerRole
            }
        }
    }
}