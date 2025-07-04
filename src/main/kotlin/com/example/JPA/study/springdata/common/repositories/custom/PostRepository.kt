package com.example.JPA.study.springdata.common.repositories.custom

import com.example.JPA.study.springdata.common.repositories.customgeneral.MyRepository
import com.example.JPA.study.springdata.common.repositories.models.Post

// custom repository 1
//interface PostRepository: JpaRepository<Post, Long>, PostCustomRepository {
//}

// custom repository 2 (general repository custom)
interface PostRepository: MyRepository<Post, Long>, PostCustomRepository {
}
