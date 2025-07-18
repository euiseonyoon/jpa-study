package com.example.springdb.study.springdata.jpa.entityGraph

import com.example.springdb.study.springdata.common.repositories.customgeneral.MyRepository

interface ProductRepository : MyRepository<Product, Long>
