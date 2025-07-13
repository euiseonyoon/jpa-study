package com.example.springdb.study.orm.relation.onetomany.jointable

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Clock
import java.time.LocalDateTime

// NOTE: 1:N 의 연결이지만 join table을 쓰는경우도, ManyToOne으로 한다.
// 그리고 unique를 넣는다
@Entity
@Table(
    name = "membership",
    // 한 사람은 1팀에만 속할 수 있음
    uniqueConstraints = [UniqueConstraint(columnNames = ["member_id"])]
)
class Membership(
    // Composite pk
    @EmbeddedId
    var membershipId: MembershipId? = null,

    val joined: LocalDateTime = LocalDateTime.now(Clock.systemUTC()),

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamId")
    @JoinColumn(name = "team_id")
    val team: Team,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    var member: Member
) {
    companion object {
        fun addMembership(member: Member, team: Team): Membership  {
            if (member.id == 0L || team.id == 0L) {
                throw IllegalStateException("member 또는 team이 아직 영속화되지 않았습니다. 먼저 저장(save)하세요.")
            }

            val membership = Membership(
                membershipId = MembershipId(
                    teamId = team.id,
                    memberId = member.id
                ),
                team = team,
                member = member
            )
            member.memberships.add(membership)
            team.memberships.add(membership)
            return membership
        }
    }

    @PrePersist
    fun prePersist() {
        if (membershipId == null) {
            this.membershipId = MembershipId(
                teamId = team.id,
                memberId = member.id
            )
        }
    }
}
