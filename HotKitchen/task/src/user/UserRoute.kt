package hotkitchen.user

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hotkitchen.model.Response
import hotkitchen.Secret
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.commons.validator.routines.EmailValidator

fun Route.userRoute(userDao: UserDao) {
    post("/signup") {
        val user = call.receive<User>()
        try {
            if (!user.email.isValidEmail()) {
                throw InvalidEmail
            }
            if (!user.password.isValidPassword()) {
                throw InvalidPassword
            }
            val token = JWT.create()
                .withClaim("email", user.email)
                .withClaim("password", user.password)
                .sign(Algorithm.HMAC256(Secret))
            userDao.signUp(user.copy(password = token))
            call.response.status(HttpStatusCode.OK)
            call.respondText { Json.encodeToString(Response(token = token)) }
        } catch (e: Exception) {
            call.respondText(
                Json.encodeToString(Response(e.message ?: "")),
                contentType = ContentType.Application.Json,
                HttpStatusCode.Forbidden
            )
        }
    }

}

object InvalidEmail : Exception("Invalid email") {
    private fun readResolve(): Any = InvalidEmail
}

object InvalidPassword : Exception("Invalid password") {
    private fun readResolve(): Any = InvalidPassword
}

private fun String.isValidEmail(): Boolean {
    return EmailValidator.getInstance().isValid(this) && this.split("@")[0].all { it.isLetterOrDigit() }
}

private fun String.isValidPassword(): Boolean {
    return this.length >= 6 && this.any { it.isLetter() } && this.any { it.isDigit() }
}