package com.example.springdb.study.orm.relation.jpabook_example.ch10_oop_query_langauge.querydsl.examples.models

import com.querydsl.core.annotations.QueryProjection

data class Ch10ItemDto @QueryProjection constructor(
    val name: String,
    val price: Int,
    val stock: Int
)