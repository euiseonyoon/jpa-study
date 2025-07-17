package com.example.springdb.study.jpabook.ch12_spring_data_jpa.querydsl_extension

import com.example.springdb.study.jpabook.ch12_spring_data_jpa.models.Ch12Item

interface Ch12ItemRepositoryExtension {
    fun searchItemWithMinMax(minPrice: Int, maxPrice: Int) : List<Ch12Item>
}
