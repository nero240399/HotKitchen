package hotkitchen.user

import hotkitchen.model.Response
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.userRoute(userDao: UserDao) {
    post("/signup") {
        val user = call.receive<User>()
        try {
            userDao.signUp(user)
            call.response.status(HttpStatusCode.OK)
            call.respondText { Json.encodeToString(Response("Signed Up")) }
        } catch (e: Exception) {
            call.response.status(HttpStatusCode.Forbidden)
            call.respondText { Json.encodeToString(Response("Registration failed")) }
        }
    }
}