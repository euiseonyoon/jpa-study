package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.jpa_listener

import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models.Ch14Item
import com.example.springdb.study.logger
import jakarta.persistence.PostLoad
import jakarta.persistence.PostPersist
import jakarta.persistence.PostRemove
import jakarta.persistence.PostUpdate
import jakarta.persistence.PrePersist
import jakarta.persistence.PreRemove
import jakarta.persistence.PreUpdate


class Ch14ItemListener {
    private val log = logger()

    // 1. DB에서 조회되어 엔티티가 영속성 컨텍스트의 영역으로 들어올때 발생
    // 2. em.refresh(entity)때 발생
    @PostLoad
    fun postLoad(item: Ch14Item) {
        log.info("[PostLoad] item={}", item)
    }

    // persist()나 merge()를 통해 엔티티가 영속성 컨텍스트의 영역으로 들어올때 발생
    @PrePersist
    fun prePersist(item: Ch14Item) {
        log.info("[PrePersist] item={}", item)
    }

    // flush나 commit을 통해 엔티티가 수정되어 DB에 반영 되기 전에 발생
    @PreUpdate
    fun preUpdate(item: Ch14Item) {
        log.info("[PreUpdate] item={}", item)
    }

    // 엔티티가 영속성 컨텍스트에서 삭제되기전 발생, orphanRemoval은 flush, commit으로 인해 DB에 반영시 발생
    @PreRemove
    fun preRemove(item: Ch14Item) {
        log.info("[PreRemove] item={}", item)
    }

    // flush, commit을 통해 엔티티가 DB에 반영이 되고 난 후에 발생
    @PostPersist
    fun postPersist(item: Ch14Item) {
        log.info("[PostPersist] item={}", item)
    }

    // flush나 commit을 통해 엔티티가 수정되어 DB에 반영 된 후에 발생
    @PostUpdate
    fun postUpdate(item: Ch14Item) {
        log.info("[PostUpdate] item={}", item)
    }

    // flush나 commit을 통해 엔티티가 DB에서 삭제 된 후에 발생
    @PostRemove
    fun postRemove(item: Ch14Item) {
        log.info("[PostRemove] item={}", item)
    }
}
