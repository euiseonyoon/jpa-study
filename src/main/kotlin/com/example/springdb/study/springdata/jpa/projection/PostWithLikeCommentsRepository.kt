package com.example.springdb.study.springdata.jpa.projection

import org.springframework.data.jpa.repository.JpaRepository

interface PostWithLikeCommentsRepository: JpaRepository<PostWithLikeComments, Long> {
}