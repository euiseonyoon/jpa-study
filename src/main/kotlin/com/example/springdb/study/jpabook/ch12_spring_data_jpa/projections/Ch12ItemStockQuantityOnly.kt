package com.example.springdb.study.jpabook.ch12_spring_data_jpa.projections

interface Ch12ItemStockQuantityOnly {

    // NOTE: Interface 에서 function 사용시 get(Entity의 필드명과 일치해야한다)
    // fun getStockQuantity(): Int

    // 그냥 val,var 사용할때에도 stockQuantity (Entity의 필드명과 일치)
    val stockQuantity: Int
}