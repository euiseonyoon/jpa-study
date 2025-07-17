package com.example.springdb.study.jpabook.ch7_advanced_mapping.examples.super_type_sub_type_relationship.join_strategy

import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType

/**
 * 생성 테이블 개수: 부모1개 + 자식마다 테이블(N개)
 *
 * 장점:
 *      테이블이 정규화 된다.
 *      자식 테이블이 부모 테이블의 ID를 외래 키로 참조 -> 부모가 없는 자식은 없을 수 없음 -> 데이터 무결성 제약조건을 활용
 *      저장 공간을 효율적으로 사용
 *
 * 단점:
 *      조회시 조인이 많이 사용됨 -> 성능 저하 가능성
 *      조회 쿼리가 복잡
 *      데이터를 등록할때, INSERT 문이 부모테이블(Ch6V1Item)에 한번 자식테이블에 한번 , 총 2번 발생
 *
 * */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name= "DTYPE")
abstract class Ch7V1Item {
    @Id @GeneratedValue
    val id: Long? = null

    var name: String? = null
    var price: Int? = null
}