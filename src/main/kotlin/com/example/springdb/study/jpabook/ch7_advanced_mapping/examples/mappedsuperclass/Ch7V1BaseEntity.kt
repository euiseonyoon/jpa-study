package com.example.springdb.study.jpabook.ch7_advanced_mapping.examples.mappedsuperclass

import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

/**
 *
 * 특징:
 *      @MappedSuperclass로 지정된건 실제 Entity가 아니라서 em.find(), JPQL등에서 사용안됨.
 *      실제 ORM에서 말하는 클래스 간의 `상속 매핑`이라고 보기 어려움
 * */
@MappedSuperclass
abstract class Ch7V1BaseEntity {
    @Id @GeneratedValue
    val id: Long? = null
    var name: String? = null
}