package hotkitchen.features.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hotkitchen.models.Response
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.commons.validator.routines.EmailValidator
import java.util.*

fun Route.authenticationRoute(authenticationDao: AuthenticationDao) {
    post("/signin") {
        val user = call.receive<UserAuthentication>()
        try {
            val tokenAuthentication = generateToken(user.email, user.password, call)
            val token = authenticationDao.signIn(user.copy(password = tokenAuthentication))
            call.respondText(
                Json.encodeToString(Response(token = token)),
                ContentType.Application.Json,
                HttpStatusCode.OK
            )
        } catch (e: Exception) {
            call.respondText(
                Json.encodeToString(Response(e.message!!)),
                ContentType.Application.Json,
                HttpStatusCode.Forbidden
            )
        }
    }
    post("/signup") {
        val user = call.receive<UserAuthentication>()
        try {
            validateSignUpInfo(user)
            val token = generateToken(user.email, user.password, call)
            authenticationDao.signUp(user.copy(password = token))
            call.respondText(
                Json.encodeToString(Response(token = token)),
                ContentType.Application.Json,
                HttpStatusCode.OK
            )
        } catch (e: Exception) {
            call.respondText(
                Json.encodeToString(Response(e.message ?: "")),
                contentType = ContentType.Application.Json,
                HttpStatusCode.Forbidden
            )
        }
    }
}

private object InvalidEmail : Exception("Invalid email") {
    private fun readResolve(): Any = InvalidEmail
}

private object InvalidPassword : Exception("Invalid password") {
    private fun readResolve(): Any = InvalidPassword
}

private fun String.isValidEmail(): Boolean {
    return EmailValidator.getInstance().isValid(this) && this.split("@")[0].all { it.isLetterOrDigit() }
}

private fun String.isValidPassword(): Boolean {
    return this.length >= 6 && this.any { it.isLetter() } && this.any { it.isDigit() }
}

private fun generateToken(email: String, password: String, call: ApplicationCall): String {
    val config = call.application.environment.config
    val secret = config.property("jwt.secret").getString()

    return JWT.create()
        .withClaim("email", email)
        .withClaim("password", password)
        .sign(Algorithm.HMAC256(secret))
}

private fun validateSignUpInfo(user: UserAuthentication) {
    if (!user.email.isValidEmail()) {
        throw InvalidEmail
    }
    if (!user.password.isValidPassword()) {
        throw InvalidPassword
    }
}
