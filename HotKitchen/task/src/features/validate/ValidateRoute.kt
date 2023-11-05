package hotkitchen.features.validate

import hotkitchen.features.authentication.AuthenticationDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.validateRoute(authenticationDao: AuthenticationDao) {

    authenticate {
        get("/features/validate") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val email = principal!!.payload.getClaim("email").asString()
                val user = authenticationDao.getUser(email)
                call.respond(HttpStatusCode.OK, "Hello, ${user?.userType} $email")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.message!!)
            }
        }
    }
}