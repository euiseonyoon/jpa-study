package com.example.JPA.study.springdata.jpa.entityGraph

import com.example.JPA.study.springdata.common.repositories.customgeneral.MyRepository

interface ProductRepository : MyRepository<Product, Long> {
}