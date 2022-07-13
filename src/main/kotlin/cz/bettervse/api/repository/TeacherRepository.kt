package cz.bettervse.api.repository

import cz.bettervse.api.domain.Teacher
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface TeacherRepository : ReactiveCrudRepository<Teacher, Int> {

    fun findTeacherByInsis(insis: Int): Mono<Teacher>

}