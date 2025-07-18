package com.example.springdb.study.orm.relation.onetone

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne

@Entity
class Person {
    @Id @GeneratedValue
    val id: Long? = null
    val name: String? = null

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "passport_id") // 외래키는 person 테이블에 생김, 즉 양방향 연결의 주인은 Person -> Passport에 mapped_by 설정을 해줘야 한다.
    var passport: Passport? = null

    fun expirePassport() {
        passport?.owner = null
        this.passport = null
    }

    fun renewPassport(passport: Passport) {
        this.passport = passport
        if (passport.owner != this) {
            passport.owner = this
        }
    }
}
