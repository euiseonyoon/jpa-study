package com.example.JPA.study.orm.relation.onetomany.jointable

import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

// Composite type
@Entity
class Member(
    @Id @GeneratedValue
    val id: Long = 0,

    val name: String,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(
            name = "street",
            column = Column(name = "home_street")
        ),
        AttributeOverride(
            name = "city",
            column = Column(name = "home_city")
        ),
        AttributeOverride(
            name = "zipCode",
            column = Column(name = "home_zipcode")
        )
    )
    val homeAddress: Address,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(
            name = "street",
            column = Column(name = "office_street")
        ),
        AttributeOverride(
            name = "city",
            column = Column(name = "office_city")
        ),
        AttributeOverride(
            name = "zipCode",
            column = Column(name = "office_zipcode")
        )
    )
    val officeAddress: Address,

) {
    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val memberships: MutableSet<Membership> = mutableSetOf()
}

// +----+--------+--------------+-----------+----------------+----------------+--------------+----------------+
// | id | name   | home_street  | home_city | home_zipcode   | office_street | office_city  | office_zipcode  |
// +----+--------+--------------+-----------+----------------+----------------+--------------+----------------+
// | 1  | Alice  | 123 Main St  | Seoul     | 12345          | 456 Gangnam    | Seoul        | 67890          |
// | 2  | Bob    | 88 Elm Road  | Busan     | 45678          | 77 Tech Ave    | Daejeon      | 11111          |
// +----+--------+--------------+-----------+----------------+----------------+--------------+----------------+
