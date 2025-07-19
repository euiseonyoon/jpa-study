package com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.hibernate.Hibernate
import org.hibernate.proxy.HibernateProxy

@Entity
class Ch15Member {
    @Id
    @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    constructor()
    constructor(name: String) {
        this.name = name
    }

    override fun hashCode(): Int {
        return name?.hashCode() ?: 0
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true

        // 아래 처럼 하면 안된다. Ch15Member가 lazy Loading 되는 경우, other가 proxy일 수 있다.
        // if (other.javaClass != this.javaClass) return false

        return if (other !is HibernateProxy) {
            // 1. 프록시가 아닐때
            compareMember((other as Ch15Member))
        } else {
            // 2. 프록시 일때
            checkTargetIfProxy(other)
        }
    }

    private fun checkTargetIfProxy(other: HibernateProxy): Boolean {
        // 1. target(엔티티)가 초기화 되어있는지 확인
        if (!Hibernate.isInitialized(other)) return false

        val target = other.hibernateLazyInitializer.implementation
        // 2. target(엔티티)가 Ch15Member 인지 확인
        if (target !is Ch15Member) return false

        // 3. 엔티티가 있다면, id 비교
        return compareMember(target)
    }

    private fun compareMember(other: Ch15Member): Boolean {
        return if (other.id == null) {
            false
        } else {
            this.id == other.id
        }
    }
}
