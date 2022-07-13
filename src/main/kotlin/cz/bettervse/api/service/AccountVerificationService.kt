package cz.bettervse.api.service

import org.springframework.stereotype.Service
import java.nio.ByteBuffer
import java.security.SecureRandom
import kotlin.random.Random

@Service
class AccountVerificationService {

    fun generateVerificationCode(): String {
        val charset = ('A'..'Z').toSet()

        // Generate a cryptographically secure random seed that can be used in PRNG
        val seed = SecureRandom.getSeed(Long.SIZE_BYTES)
        val source = Random(ByteBuffer.wrap(seed).long)

        return generateSequence { charset.random(source) }.take(8).joinToString("")
    }

}