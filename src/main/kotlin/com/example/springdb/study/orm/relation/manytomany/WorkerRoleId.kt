package com.example.springdb.study.orm.relation.manytomany

import jakarta.persistence.Embeddable
import java.io.Serializable

/**
 *
 * 복합키는 아래의 특징을 가져야함
 *
 * 1. 별도의 실별자 클래스를 만들어야 한다.
 * 2. Serializable을 구현해야한다.
 * 3. equals(), hashCode()를 구현해야한다.
 * 4. 기본 생성자가 있어야 한다.
 * 5. 식별자 클래스는 public이어야 한다.
 * 6. @IdClass를 사용하거나, @EmbeddedId를 사용한다.
 * */
@Embeddable
class WorkerRoleId : Serializable {

    private var workerId: Long? = null
    private var roleId: Long? = null

    constructor()

    constructor(workerId: Long, roleId: Long) {
        this.workerId = workerId
        this.roleId = roleId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as WorkerRoleId

        return workerId == other.workerId && roleId == other.roleId
    }

    override fun hashCode(): Int {
        var result = workerId?.hashCode() ?: 0
        result = 31 * result + (roleId?.hashCode() ?: 0)
        return result
    }
}


