package com.example.springdb.study.springdata.jpa.specification

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import kotlin.String

class Store2Specs {
    companion object {
        fun nameEqualTo(name: String): Specification<Store2> {
            return object : Specification<Store2> {
                /*
                * org.springframework.data.jpa.domain.Specification
                * @Nullable
	              Predicate toPredicate(Root<T> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder);
                * */
                override fun toPredicate(
                    root: Root<Store2>,
                    query: CriteriaQuery<*>?,
                    criteriaBuilder: CriteriaBuilder
                ): Predicate? {
                    return criteriaBuilder.equal(root.get<String>("name"), name)
                }
            }
        }

        fun nameContains(subName: String): Specification<Store2> {
            return Specification { root, query, cb ->
                val nameLower = cb.lower(root.get("name"))
                val subNameLower = subName.lowercase()
                cb.like(nameLower, "%$subNameLower%")
            }
        }
    }
}
