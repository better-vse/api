package cz.bettervse.api.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("accounts")
data class Account(
    @Id
    val id: Int = 0,

    @Column("username")
    val username: String,

    @Column("verification_code")
    val code: String? = null
) {
    @Transient
    val email: String = "$username@vse.cz"
}