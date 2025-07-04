package com.example.JPA.study.springdata.common.repositories.custom

import com.example.JPA.study.springdata.common.repositories.customgeneral.MyRepository
import com.example.JPA.study.springdata.common.repositories.models.Post

//interface PostRepository: JpaRepository<Post, Long>, PostCustomRepository {
//}
interface PostRepository: MyRepository<Post, Long>, PostCustomRepository {
}
