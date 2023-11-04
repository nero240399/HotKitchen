package hotkitchen.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hotkitchen.Secret
import hotkitchen.database.daos.InvalidEmailOrPassword
import hotkitchen.model.Response
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Except

fun Route.authenticationRoute(authenticationDao: AuthenticationDao) {
    post("/signin") {
        val userAuthentication = call.receive<UserAuthentication>()
        try {
            val tokenAuthentication = JWT.create()
                .withClaim("email", userAuthentication.email)
                .withClaim("password", userAuthentication.password)
                .sign(Algorithm.HMAC256(Secret))
            val token = authenticationDao.signIn(userAuthentication.copy(password = tokenAuthentication))
            call.response.status(HttpStatusCode.OK)
            call.respondText { Json.encodeToString(Response(token = token)) }
        } catch (e: Exception) {
            call.respondText(
                Json.encodeToString(Response(e.message!!)),
                ContentType.Application.Json,
                HttpStatusCode.Forbidden
            )
        }
    }
}