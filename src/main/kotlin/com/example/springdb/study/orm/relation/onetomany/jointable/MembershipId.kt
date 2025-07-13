package com.example.springdb.study.orm.relation.onetomany.jointable

import jakarta.persistence.Embeddable
import java.io.Serializable

// data class로 만드는 이유는
// equals(), hashCode() 자동으로 작성해줌
// JPA에서 복합키(Composite Key)를 식별자로 사용하려면,
// equals()와 hashCode()가 정확하게 구현되어 있어야 함.
// Composite type
// 복합키에서는 String, long 같은것만 넣는다.
@Embeddable
data class MembershipId(
    var teamId: Long,
    var memberId: Long
) : Serializable
