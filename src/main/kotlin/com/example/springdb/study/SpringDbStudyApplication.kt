package com.example.springdb.study

import com.example.springdb.study.springdata.common.repositories.customgeneral.MyRepositoryImpl
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
// NOTE: 이거 중요!!
@EnableJpaRepositories(repositoryBaseClass = MyRepositoryImpl::class)
class SpringDbStudyApplication

fun main(args: Array<String>) {
	runApplication<SpringDbStudyApplication>(*args)
}
