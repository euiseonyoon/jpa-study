package com.example.springdb.study.jpabook.ch12_spring_data_jpa.querydsl_extension

import com.example.springdb.study.jpabook.ch12_spring_data_jpa.models.Ch12Item
import com.example.springdb.study.jpabook.ch12_spring_data_jpa.models.QCh12Item
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class Ch12ItemRepositoryExtensionImpl(
    private val em: EntityManager
) : Ch12ItemRepositoryExtension {
    /**
     * NOTE:
     *
     *  여기서 QuerydslRepositorySupport를 상속받아 사용하지 못했다.
     *  이유는
     *  @EnableJpaRepositories(repositoryBaseClass = MyRepositoryImpl::class) 설정이 되어있다.
     *  모든 repository의 구현체는 MyRepositoryImpl을 따르게 되는데
     *  MyRepositoryImpl은 SimpleJpaRepository를 상속한다.
     *  SimpleJpaRepository는 interface가 아닌 객체이고
     *  QuerydslRepositorySupport 또한 abstract 객체이다.
     *
     *  한 클래스가 여러 부모로 부터 상속을 받지 못하기 때문에 부득이하게 QuerydslRepositorySupport를 사용하지 못했다.
     *
     *  그래서 대안으로 `Ch12ItemRepositoryExtensionImpl` 내부적으로 JPAQueryFactory를 직접 만들어서 사용하게 되었다.
     *
     * QuerydslRepositorySupport를 사용하면 편리한 점은 아래와 같다.
     *      1.도메인 클래스 자동 설정 → from(QCh12Item.ch12Item)에서 타입 추론 생략 가능
     *      2. getQuerydsl().applyPagination() 사용 가능 → 페이징 처리 코드가 간결
     *      3. 엔티티 매니저 자동 주입
     *      4. JPAQueryFactory 없이도 select(), from() 사용 가능 (내부적으로 처리)
     * */

    val query = JPAQueryFactory(em)
    val item: QCh12Item = QCh12Item.ch12Item

    override fun searchItemWithMinMax(
        minPrice: Int,
        maxPrice: Int
    ): List<Ch12Item> {
        val result = query.select(item)
            .from(item)
            .where(
                item.price.goe(minPrice)
                    .and(item.price.loe(maxPrice))
            ).fetch()
        return result
    }
}
