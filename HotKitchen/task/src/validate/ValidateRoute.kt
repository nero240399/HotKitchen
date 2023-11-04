package hotkitchen.validate

import hotkitchen.user.UserDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.validateRoute(userDao: UserDao) {

    authenticate {
        get("/validate") {
            try {
                val principal = call.principal<JWTPrincipal>()
                val email = principal!!.payload.getClaim("email").asString()
                val user = userDao.getUser(email)
                call.respond(HttpStatusCode.OK, "Hello, ${user?.userType} $email")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, e.message!!)
            }
        }
    }
}