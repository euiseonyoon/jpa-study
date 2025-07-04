package com.example.JPA.study.springdata.common.web

import com.example.JPA.study.springdata.common.repositories.customgeneral.MyRepository

interface TwitterRepository : MyRepository<Twitter, Long> {
}