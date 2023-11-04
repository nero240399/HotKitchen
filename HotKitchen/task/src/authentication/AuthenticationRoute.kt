package hotkitchen.authentication

import hotkitchen.model.Response
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.authenticationRoute(authenticationDao: AuthenticationDao) {
    post("/signin") {
        val userAuthentication = call.receive<UserAuthentication>()
        try {
            authenticationDao.signIn(userAuthentication)
            call.response.status(HttpStatusCode.OK)
            call.respondText { Json.encodeToString(Response("Signed In")) }
        } catch (e: Exception) {
            call.response.status(HttpStatusCode.Forbidden)
            call.respondText { Json.encodeToString(Response("Authorization failed")) }
        }
    }
}