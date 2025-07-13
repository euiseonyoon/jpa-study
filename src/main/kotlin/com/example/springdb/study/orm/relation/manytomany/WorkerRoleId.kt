package com.example.springdb.study.orm.relation.manytomany

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class WorkerRoleId(
    val workerId: Long,
    val roleId: Long,
) : Serializable