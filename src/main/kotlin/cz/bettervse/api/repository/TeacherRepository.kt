package cz.bettervse.api.repository

import cz.bettervse.api.domain.Teacher
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TeacherRepository : CoroutineCrudRepository<Teacher, Int> {

    suspend fun findTeacherByInsis(insis: Int): Teacher?

}