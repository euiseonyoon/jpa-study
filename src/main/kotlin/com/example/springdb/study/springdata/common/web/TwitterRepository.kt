package com.example.springdb.study.springdata.common.web

import com.example.springdb.study.springdata.common.repositories.customgeneral.MyRepository

interface TwitterRepository : MyRepository<Twitter, Long>
