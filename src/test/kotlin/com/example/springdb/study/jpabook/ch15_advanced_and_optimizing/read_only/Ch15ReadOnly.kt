package com.example.springdb.study.jpabook.ch15_advanced_and_optimizing.read_only

class Ch15ReadOnly {
    /**
     * ReadOnly를 사용하여 쿼리를 최적화 할 수 있다
     *
     * 1. Hint
     *   em.createQuery(..)
     *   .setHint("org.hibernate.readonly", true)
     *
     * 2. @Transactional(readOnly = true)
     *
     * */
}
