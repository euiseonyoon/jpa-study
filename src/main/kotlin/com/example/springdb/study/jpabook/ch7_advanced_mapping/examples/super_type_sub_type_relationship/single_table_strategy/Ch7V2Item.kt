package com.example.springdb.study.jpabook.ch7_advanced_mapping.examples.super_type_sub_type_relationship.single_table_strategy

import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType

/**
 * 생성 테이블 개수: 1개 (통합 단일 테이블)
 *
 * 장점:
 *      조인이 필요없음
 *      조회 쿼리가 단순
 *
 *  단점:
 *      자식들이 만든 컬럼든은 모두 nullable 해야한다 (e.g. Album이 만든 artist는 nullable 해야한다.)
 *      단일 테이블에 모든 부모와 자식들의 정보를 저장 -> 테이블이 커지고 쿼리 속도가 늦어질수 있음
 *
 *  특징:
 *      @DiscriminatorColumn을 꼭 사용해야 된다.
 * */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
abstract class Ch7V2Item {
    @Id @GeneratedValue
    val id: Long? = null
    var name: String? = null
    var price: Int? = null
}
