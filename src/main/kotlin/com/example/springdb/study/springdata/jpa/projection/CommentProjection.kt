package com.example.springdb.study.springdata.jpa.projection

interface CommentProjection {
    val likes: Int
    val dislikes: Int
}

data class CommentDto(
    val likes: Int,
    val dislikes: Int
) {
    val score: Int = likes - dislikes
}
