package com.example.springdb.study.jpabook.ch12_spring_data_jpa.models

import com.querydsl.core.annotations.QueryProjection

data class Ch12ItemDto @QueryProjection constructor(
    val name: String,
    val price: Int,
    val stock: Int
)