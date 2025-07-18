package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.additional_features

import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models.Ch14Player
import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.models.Ch14Team
import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.repositories.Ch14PlayerRepository
import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.repositories.Ch14TeamRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class Ch14AdditionalFeaturesTest {

    @Autowired
    @PersistenceContext
    lateinit var em: EntityManager
    @Autowired
    lateinit var playerRepository: Ch14PlayerRepository
    @Autowired
    lateinit var teamRepository: Ch14TeamRepository

    var team: Ch14Team? = null
    var players: List<Ch14Player> = listOf()
    val FIXED_SALARY = 1000
    enum class OrderingFlow {
        ASC,
        DESC
    }

    @BeforeEach
    fun init() {
        // GIVEN
        val team = makeTeam("Basketball Team").also { this.team = it }
        em.flush()

        players = listOf("Jordan" to true, "Kawaii" to true, "LeBron" to false, "Rose" to false, "Novich" to false).map {
            val name = it.first
            val isRandomSalary = it.second
            makePlayer(name, isRandomSalary).also { it.addTeam(team) }
        }

        em.flush()
        em.clear()
    }

    private fun makePlayer(name: String, isRandomSalary: Boolean): Ch14Player {
        val salary = if (isRandomSalary) {
            (100..10000).random()
        } else {
            FIXED_SALARY
        }
        return playerRepository.save(Ch14Player(name, salary))
    }

    private fun makeTeam(name: String): Ch14Team {
        return teamRepository.save(Ch14Team(name))
    }

    private fun checkOrdering(numbers: List<Int>, ordering: OrderingFlow): Boolean {
        if (numbers.size <= 1) return true

        for (index in 0 until numbers.lastIndex) {
            val current = numbers[index]
            val next = numbers[index + 1]

            if (ordering == OrderingFlow.ASC) {
                if (current > next) {
                    return false
                }
            } else {
                if (current < next) {
                    return false
                }
            }
        }
        return true
    }

    @Test
    fun test_order_by() {
        // WHEN
        // salary desc, id asc
        val result = teamRepository.searchByTeamIdAndAllPlayers(team!!.id!!)
        /**
         *    select
         *         ct1_0.id,
         *         ct1_0.name,
         *         p1_0.team_id,
         *         p1_0.id,
         *         p1_0.name,
         *         p1_0.salary
         *     from
         *         ch14team ct1_0
         *     left join
         *         ch14player p1_0
         *             on ct1_0.id=p1_0.team_id
         *     where
         *         ct1_0.id=?
         *     order by
         *         p1_0.salary desc,
         *         p1_0.id
         * */

        // THEN
        val salariesInOrder = result.players.map { it.salary!! }
        checkOrdering(salariesInOrder, OrderingFlow.DESC)

        val salaryKeyPlayersValuesMap = result.players.groupBy { it.salary!! }

        salaryKeyPlayersValuesMap.values.map { playerList ->
            checkOrdering(playerList.map { it.id!!.toInt() }, OrderingFlow.ASC)
        }
    }

}
