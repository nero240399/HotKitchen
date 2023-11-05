package hotkitchen.user

import hotkitchen.models.Response
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.userRoute(userDao: UserDao) {
    authenticate {
        get("/me") {
            try {
                val email = call.principal<JWTPrincipal>()!!.payload.getClaim("email").asString()
                val user = userDao.getUser(email)
                call.respondText(
                    Json.encodeToString(user),
                    contentType = ContentType.Application.Json,
                    HttpStatusCode.OK
                )
            } catch (e: Exception) {
                call.respondText(
                    Json.encodeToString(Response(e.message ?: "")),
                    contentType = ContentType.Application.Json,
                    HttpStatusCode.BadRequest
                )
            }
        }
        put("/me") {
            try {
                val email = call.principal<JWTPrincipal>()!!.payload.getClaim("email").asString()
                val user = call.receive<UserInfo>()
                validateUserInfoUpdating(email, user)
                userDao.updateUser(user)
                call.respondText(
                    Json.encodeToString(user),
                    contentType = ContentType.Application.Json,
                    HttpStatusCode.OK
                )

            } catch (e: Exception) {
                call.respondText(
                    Json.encodeToString(Response(e.message ?: "")),
                    contentType = ContentType.Application.Json,
                    HttpStatusCode.BadRequest
                )
            }
        }
        delete("/me") {
            try {
                val email = call.principal<JWTPrincipal>()!!.payload.getClaim("email").asString()
                userDao.deleteUser(email)
                call.respondText(
                    Json.encodeToString(Response("User $email deleted successfully")),
                    contentType = ContentType.Application.Json,
                    HttpStatusCode.OK
                )
            } catch (e: Exception) {
                call.respondText(
                    Json.encodeToString(Response(e.message ?: "")),
                    contentType = ContentType.Application.Json,
                    HttpStatusCode.NotFound
                )
            }
        }
    }
}

private fun validateUserInfoUpdating(email: String, userInfo: UserInfo) {
    if (email != userInfo.email) {
        throw ChangeEmailException
    }
}

private object ChangeEmailException : Exception("You cannot change the email") {
    private fun readResolve(): Any = ChangeEmailException
}