package cz.bettervse.api.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("subjects")
data class Subject(
    @Id
    val id: Int = 0,

    @Column("insis_id")
    val insis: Int,

    @Column("code")
    val code: String,

    @Column("name")
    val name: String
)