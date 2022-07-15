package cz.bettervse.api.repository

import cz.bettervse.api.domain.Subject
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SubjectRepository : CoroutineCrudRepository<Subject, Int> {

    suspend fun findSubjectByCode(code: String): Subject?

    suspend fun findSubjectByInsis(insis: Int): Subject?

}