package com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import org.hibernate.Hibernate
import org.hibernate.annotations.BatchSize
import org.hibernate.proxy.HibernateProxy

@Entity
class Ch15Member {
    @Id
    @GeneratedValue
    val id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    // @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 50)
    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER)
    var orders: MutableSet<Ch15Order> = mutableSetOf()

    constructor()
    constructor(name: String) {
        this.name = name
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true

        // 아래 처럼 하면 안된다. Ch15Member가 lazy Loading 되는 경우, other가 proxy일 수 있다.
        // if (other.javaClass != this.javaClass) return false

        // Hibernate.getClass()를 사용하여 프록시를 벗겨내고 실제 엔티티 클래스를 비교. other가 proxy이던 아니던 상관없음
        if (Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as Ch15Member

        if(this.id == null) return false

        return this.id == other.id

        // 혹은 아래처럼
//        return if (other !is HibernateProxy) {
//            // 1. 프록시가 아닐때
//            if (other !is Ch15Member) return false
//            compareMember((other))
//        } else {
//            // 2. 프록시 일때
//            checkTargetIfProxy(other)
//        }
    }

    private fun checkTargetIfProxy(other: HibernateProxy): Boolean {
        /**
         * 기존 아래코드의 1번은 치명적인 오류가 있다.
         * lazy loading으로 인한 비교를 하려고 할때, 무조건 equals가 fail 된다.
         * */

//        // 1. target(엔티티)가 초기화 되어있는지 확인
//        if (!Hibernate.isInitialized(other)) return false
//        val target = other.hibernateLazyInitializer.implementation
//        // 2. target(엔티티)가 Ch15Member 인지 확인
//        if (target !is Ch15Member) return false
//        // 3. 엔티티가 있다면, id 비교
//        return compareMember(target)

        val target = other.hibernateLazyInitializer.implementation
        if (target !is Ch15Member) return false

        return if (Hibernate.isInitialized(other)) {
            compareMember(target)
        } else {
            val targetId = other.hibernateLazyInitializer.identifier as Long
            targetId == this.id
        }
    }

    private fun compareMember(other: Ch15Member): Boolean =
        if (other.id == null) {
            false
        } else {
            this.id == other.id
        }
}
