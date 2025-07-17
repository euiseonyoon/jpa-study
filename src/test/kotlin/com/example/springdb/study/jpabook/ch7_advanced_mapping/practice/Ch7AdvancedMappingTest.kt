package com.example.springdb.study.jpabook.ch7_advanced_mapping.practice

import com.example.springdb.study.logger
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
class Ch7AdvancedMappingTest {

    private val log = logger()

    @Autowired
    @PersistenceContext
    lateinit var em: EntityManager

    private fun createMember(): Ch7Member {
        return Ch7Member("john", Ch7Address(city = "chicago", zipcode = "111", street = "Saratoka22"))
    }

    private fun createCategory(categoryName: String, parentCategory: Ch7Category? = null): Ch7Category {
        val category = Ch7Category()
        category.name = categoryName
        parentCategory?.addChild(category)
        return category
    }

    private fun createBook(category: Ch7Category): Ch7Book {
        val book = Ch7Book()
        book.name = "PetitePrince"
        book.price = 10000
        book.stockQuantity = 20
        book.author = "Saint"
        book.isbn = "12345.abcde"

        category.addItem(book)
        return book
    }

    private fun createMovie(category: Ch7Category): Ch7Movie {
        val movie = Ch7Movie()
        movie.name = "LordOfTheRings"
        movie.price = 20000
        movie.stockQuantity = 40
        movie.actor = "Gandalf"
        movie.director = "Frodo"

        category.addItem(movie)
        return movie
    }

    private fun createAlbum(category: Ch7Category): Ch7Album {
        val album = Ch7Album()
        album.name = "BrandNewAlbum"
        album.price = 7000
        album.stockQuantity = 5
        album.artist = "Nirvana"
        album.genre = Ch7AlbumGenre.ROCK

        category.addItem(album)
        return album
    }

    private fun createOrder(member: Ch7Member, item: Ch7Item, count: Int, delivery: Ch7Delivery? = null): Ch7Order {
        val order = Ch7Order()
        order.assignMember(member)

        order.assignItem(item, count)
        if (delivery != null) {
            order.assignDelivery(delivery)
        }

        return order
    }

    private fun createDelivery(member: Ch7Member, order: Ch7Order) {
        val delivery = Ch7Delivery()
        // 멤버의 주소가 변한다면 이것도 변해야 하기 떄문에 copy하지 않고 그대로 넣는다.
        delivery.address = member.address
        delivery.status = Ch7DeliveryStatus.STARTED
        order.assignDelivery(delivery)
    }

    @Transactional
    @Test
    fun test() {
        val member = createMember()
        em.persist(member)
        em.flush()

        val artCategory = createCategory("ART")
        val musicCategory = createCategory("music", artCategory)
        val movieCategory = createCategory("movie", artCategory)
        val bookCategory = createCategory("book", artCategory)
        em.persist(artCategory)
        em.persist(musicCategory)
        em.persist(movieCategory)
        em.persist(bookCategory)

        val music = createAlbum(musicCategory)
        val movie = createMovie(movieCategory)
        val book = createBook(bookCategory)
        em.persist(music)
        em.persist(movie)
        em.persist(book)

        val musicOrder = createOrder(member, music, 2)
        createDelivery(member, musicOrder)
        em.persist(musicOrder)

        em.flush()
        em.clear()

        log.info("--------------------------------------------------")
        log.info("--------------------------------------------------")
        val madeMusicOrder = em.find(Ch7Order::class.java, musicOrder.id)
        /**
         * select
         *         co1_0.id,
         *         co1_0.created_date,
         *         co1_0.last_modified_date,
         *         co1_0.member_id,
         *         co1_0.order_date,
         *         co1_0.status
         *
         *         d1_0.id,
         *         d1_0.city,
         *         d1_0.street,
         *         d1_0.zipcode,
         *         d1_0.created_date,
         *         d1_0.last_modified_date,
         *         d1_0.status,

         *         m1_0.id,
         *         m1_0.city,
         *         m1_0.street,
         *         m1_0.zipcode,
         *         m1_0.created_date,
         *         m1_0.last_modified_date,
         *         m1_0.name,

         *     from
         *         ch7order co1_0
         *     left join
         *         ch7delivery d1_0
         *             on d1_0.id=co1_0.delivery_id
         *     join
         *         ch7member m1_0
         *             on m1_0.id=co1_0.member_id
         *     where
         *         co1_0.id=?
         *
         * */
        log.info("--------------------------------------------------")
        /**
         * 아래에서 문제가 발생했다.
         *      madeMusicOrder.orderItem이 빈 값이었다. (DB에 저장이 안되었다)
         *
         * 이유:
         *      Order - OrderItem 의 양방향 관계의 주인은 OrderItem 이다.
         *      하지만 OrderItem 객체를 persist 하는곳이 없어서, 실제로 orderItem이 DB에 저장 되지 않았다.
         *
         * 해결:
         *      Ch7Order.orderItems 에 Cascade 옵션을 주었다.
         *
         * 추가로:
         *      Ch7Item.orderItems, Ch7Item.categories 에도 비슷한 조치를 했다.
         *      Ch7Member.orders 에도 비슷한 조치를 했다.
         *
         * 결과:
         *      아래의 쿼리가 수행되었다. (item, album, music, movie 을 전부 JOIN 하는걸 볼 수 있다.)
         *
         * select
         *         oi1_0.order_id,
         *         oi1_0.id,
         *         oi1_0.count,
         *         oi1_0.created_date,
         *         oi1_0.item_id,
         *         i1_0.id,
         *         i1_0.dtype,
         *         i1_0.created_date,
         *         i1_0.last_modified_date,
         *         i1_0.name,
         *         i1_0.price,
         *         i1_0.stock_quantity,
         *         i1_1.artist,
         *         i1_1.genre,
         *         i1_2.author,
         *         i1_2.isbn,
         *         i1_3.actor,
         *         i1_3.director,
         *         oi1_0.last_modified_date,
         *         oi1_0.price
         *     from
         *         ch7order_item oi1_0
         *     left join
         *         ch7item i1_0
         *             on i1_0.id=oi1_0.item_id
         *     left join
         *         ch7album i1_1
         *             on i1_0.id=i1_1.id
         *     left join
         *         ch7book i1_2
         *             on i1_0.id=i1_2.id
         *     left join
         *         ch7movie i1_3
         *             on i1_0.id=i1_3.id
         *     where
         *         oi1_0.order_id=?
         * */
        val firstOrderItem = madeMusicOrder.orderItems.first()
        log.info("--------------------------------------------------")
        // 아래는 아무런 쿼리 발생하지 않음.
        val orderedItem = firstOrderItem.item

        log.info("--------------------------------------------------")
        // 아래 두줄에서 쿼리 발생하지 않았다.
        val orderedAlbum = orderedItem as Ch7Album
        val artistName = orderedAlbum.artist

        log.info("--------------------------------------------------")
        // 아래에서 쿼리 발생하지 않았다.
        val madeDelivery = firstOrderItem.order?.delivery
    }

}