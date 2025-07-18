package com.example.springdb.study.jpabook.ch10_oop_languge.querydsl.examples.models

import com.querydsl.core.annotations.QueryProjection

data class Ch10ItemDto @QueryProjection constructor(
    val name: String,
    val price: Int,
    val stock: Int
)
