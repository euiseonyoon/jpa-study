package com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections

data class Ch12NestedOrderItemDto(
    val count: Int,
    val order: Ch12OrderDto,
    val item: Ch12ItemDto,
) {
//    아래처럼 하면 top-level 의 생성자가 아니라서 JPA가 인식을 하지 못하여, projection에 실패한다.
//    data class Ch12OrderDto(
//        val id: Long,
//    )
//
//    data class Ch12ItemDto(
//        val id: Long,
//        val name: String,
//    )
}
