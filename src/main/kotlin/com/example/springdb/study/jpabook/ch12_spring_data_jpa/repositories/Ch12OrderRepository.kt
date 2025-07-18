package com.example.springdb.study.jpabook.ch12_spring_data_jpa.repositories

import com.example.springdb.study.jpabook.ch12_spring_data_jpa.models.Ch12Order
import org.springframework.data.jpa.repository.JpaRepository

interface Ch12OrderRepository : JpaRepository<Ch12Order, Long>
