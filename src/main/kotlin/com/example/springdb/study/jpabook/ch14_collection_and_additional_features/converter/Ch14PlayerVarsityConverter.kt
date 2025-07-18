package com.example.springdb.study.jpabook.ch14_collection_and_additional_features.converter

import com.example.springdb.study.jpabook.ch14_collection_and_additional_features.enums.Ch14PlayerVarsity
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

/**
 * Converter 적용은:
 *      1. Entity의 특정 필드(들)에 적용할 수 있다. (현재 코드)
 *
 *      2. @Entity 자체에 적용할 수 있다.

 *          @Entity
 *          @Convert(converter = Ch14PlayerVarsityConverter::class, attributeName = "varsityStatus")
 *          class Ch14Player {...}
 *
 *      3. 글로벌 하게 지정할수 있다. (하지만 이렇게 하면 엔티티의 모든 Boolean 타입에 적용된다.. 이건 별로인듯)
 *
 *          @Converter(autoApply = true)
 *          class Ch14PlayerVarsityConverter : AttributeConverter<Boolean, String> {..}
 *
 * */
@Converter
class Ch14PlayerVarsityConverter : AttributeConverter<Boolean, String> {
    override fun convertToDatabaseColumn(attribute: Boolean?): String? {
        // Hibernate가 EntityManagerFactory를 생성 중, Ch14PlayerVarsityConverter.convertToDatabaseColumn(null)을 호출해본다.
        // 이때 attribute = null인 상황을 핸들링 하지 않으면 NullPointerException이 발생한다.
        // attribute는 nullable하게 하고, 그리고 null일때 핸들링이 필요하다.
        if (attribute == null) {
            return null
        }

        return if (attribute) {
            Ch14PlayerVarsity.VARSITY.name
        } else {
            Ch14PlayerVarsity.JUNIOR_VARSITY.name
        }
    }

    override fun convertToEntityAttribute(dbData: String?): Boolean? {
        // 여기는 Hibernate가 호출해 보지 않는것 같다. 하지만 db의 값이 nullable 할 수도 있으니
        // 꼭 nullable하게 만들고, null핸들링을 하도록 하자.
        if (dbData == null) {
            return null
        }

        return if (dbData == Ch14PlayerVarsity.VARSITY.name) {
            true
        } else {
            false
        }
    }
}
