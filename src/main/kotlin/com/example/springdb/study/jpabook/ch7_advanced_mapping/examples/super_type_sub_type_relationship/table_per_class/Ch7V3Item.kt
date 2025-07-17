package com.example.springdb.study.jpabook.ch7_advanced_mapping.examples.super_type_sub_type_relationship.table_per_class

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType


/**
 * 생성 테이블 개수: 자식 클래수의 수(N)
 *
 *  장점:
 *      서브 타입을 구분해서 처리할떄 효과적
 *      자식들의 column에 nullable=false 하게 할 수 있다
 *
 *  단점:
 *      여러 자식을 조회할때 성능이 느림(UNION이 사용됨..)
 *      자식 테이블을 통합해서 쿼리하기 힘듬
 *
 *  특징:
 *      @DiscriminatorColumn 미사용
 *      대부분 추천하지 않는 방식임!!!
 *
 * */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class Ch7V3Item {
    @Id @GeneratedValue
    val id: Long? = null
    var name: String? = null
    var price: Int? = null
}