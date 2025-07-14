package com.example.springdb.study.orm.relation.onetomany.jointable

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
class MembershipId : Serializable {
    private var teamId: Long? = null
    private var memberId: Long? = null

    constructor()

    constructor(teamId: Long, memberId: Long) {
        this.teamId = teamId
        this.memberId = memberId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        other as MembershipId

        return teamId == other.teamId && memberId == other.memberId
    }

    override fun hashCode(): Int {
        var result = teamId?.hashCode() ?: 0
        result = 31 * result + (memberId?.hashCode() ?: 0)
        return result
    }
}
