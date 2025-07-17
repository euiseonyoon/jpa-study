package com.example.springdb.study.jpabook.ch7_advanced_mapping.examples.mappedsuperclass

import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Entity

/**
 * 부모로 부터 물려받은 매핑 정보를 재정의 하려면 @AttributeOverride(s) 사용
 *
 * 아래의 member는 `id` 대신 `member_id`사용
 *
 * 컬럼들: `member_id`, `name`, `email`
 * */
@Entity
@AttributeOverride(name="id", column = Column(name="member_id"))
class Ch7V1Member : Ch7V1BaseEntity() {
    // id, name을 상속
    var email: String? = null
}