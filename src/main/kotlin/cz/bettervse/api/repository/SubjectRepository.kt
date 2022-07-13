package cz.bettervse.api.repository

import cz.bettervse.api.domain.Subject
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface SubjectRepository : ReactiveCrudRepository<Subject, Int> {

    fun findSubjectByCode(code: String): Mono<Subject>

    fun findSubjectByInsis(insis: Int): Mono<Subject>

}