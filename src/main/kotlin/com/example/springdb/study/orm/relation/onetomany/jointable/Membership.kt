package com.example.springdb.study.orm.relation.onetomany.jointable

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
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
class Membership {
    // Composite pk
    @EmbeddedId
    var membershipId: MembershipId? = null

    var joined: LocalDateTime = LocalDateTime.now(Clock.systemUTC())

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("teamId") // MembershipId.teamId 필드와 이름 동일
    @JoinColumn(name = "team_id")
    var team: Team? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId") // MembershipId.memberId 필드와 이름 동일
    @JoinColumn(name = "member_id")
    var member: Member? = null
}
