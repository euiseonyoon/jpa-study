package com.example.JPA.study.repositories.custom

import com.example.JPA.study.repositories.customgeneral.MyRepository
import com.example.JPA.study.repositories.customgeneral.MyRepositoryImpl
import com.example.JPA.study.repositories.models.Post
import org.springframework.data.jpa.repository.JpaRepository

//interface PostRepository: JpaRepository<Post, Long>, PostCustomRepository {
//}
interface PostRepository: MyRepository<Post, Long>, PostCustomRepository {
}
