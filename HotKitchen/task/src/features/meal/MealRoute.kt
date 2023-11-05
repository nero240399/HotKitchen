package hotkitchen.features.meal

import hotkitchen.features.authentication.AuthenticationDao
import hotkitchen.models.Response
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.mealRoute(authenticationDao: AuthenticationDao, mealDao: MealDao) {
    authenticate {
        post("/meals") {
            try {
                val meal = call.receive<Meal>()
                val email = call.principal<JWTPrincipal>()!!.payload.getClaim("email").asString()
                if (authenticationDao.getUser(email)?.userType != "staff") {
                    throw NotStaffException
                }
                mealDao.insertMeal(meal)
            } catch (e: Exception) {
                when (e) {
                    is NotStaffException -> call.response.status(HttpStatusCode.Forbidden)
                    is BadRequestException -> call.response.status(HttpStatusCode.BadRequest)
                    else -> call.response.status(HttpStatusCode.InternalServerError)
                }
                call.respondText(
                    Json.encodeToString(Response(e.message ?: "")),
                    contentType = ContentType.Application.Json,
                )
            }
        }
        post("/categories") {
            try {
                val category = call.receive<Category>()
                val email = call.principal<JWTPrincipal>()!!.payload.getClaim("email").asString()
                if (authenticationDao.getUser(email)?.userType != "staff") {
                    throw NotStaffException
                }
                mealDao.insertCategory(category)
            } catch (e: Exception) {
                when (e) {
                    is NotStaffException -> call.response.status(HttpStatusCode.Forbidden)
                    is BadRequestException -> call.response.status(HttpStatusCode.BadRequest)
                    else -> call.response.status(HttpStatusCode.InternalServerError)
                }
                call.respondText(
                    Json.encodeToString(Response(e.message ?: "")),
                    contentType = ContentType.Application.Json,
                )
            }
        }
        get("/meals") {
            try {
                val meals = mealDao.getMeals()
                call.respondText(
                    Json.encodeToString(meals),
                    contentType = ContentType.Application.Json,
                    HttpStatusCode.OK
                )
            } catch (e: Exception) {
                call.respondText(
                    Json.encodeToString(Response(e.message ?: "")),
                    contentType = ContentType.Application.Json,
                    HttpStatusCode.InternalServerError
                )
            }
        }
        get("/meals") {
            try {
                val categories = mealDao.getCategories()
                call.respondText(
                    Json.encodeToString(categories),
                    contentType = ContentType.Application.Json,
                    HttpStatusCode.OK
                )
            } catch (e: Exception) {
                call.respondText(
                    Json.encodeToString(Response(e.message ?: "")),
                    contentType = ContentType.Application.Json,
                    HttpStatusCode.InternalServerError
                )
            }
        }
        get("/meals?id={id}") {
            try {
                val id = call.receiveParameters()["id"]?.toIntOrNull()
                id?.let {
                    val meal = mealDao.getMeal(id) ?: throw NotFoundException("Meal $id not found")
                    call.respondText(
                        Json.encodeToString(meal),
                        contentType = ContentType.Application.Json,
                        HttpStatusCode.OK
                    )
                } ?: throw BadRequestException("Invalid id parameter")
            } catch (e: Exception) {
                when (e) {
                    is BadRequestException -> call.response.status(HttpStatusCode.BadRequest)
                    is NotFoundException -> call.response.status(HttpStatusCode.NotFound)
                    else -> call.response.status(HttpStatusCode.InternalServerError)
                }
                call.respondText(
                    Json.encodeToString(Response(e.message ?: "")),
                    contentType = ContentType.Application.Json,
                )
            }
        }
        get("/category?id={id}") {
            try {
                val id = call.receiveParameters()["id"]?.toIntOrNull()
                id?.let {
                    val category = mealDao.getCategory(id) ?: throw NotFoundException("Category $id not found")
                    call.respondText(
                        Json.encodeToString(category),
                        contentType = ContentType.Application.Json,
                        HttpStatusCode.OK
                    )
                } ?: throw BadRequestException("Invalid id parameter")
            } catch (e: Exception) {
                when (e) {
                    is BadRequestException -> call.response.status(HttpStatusCode.BadRequest)
                    else -> call.response.status(HttpStatusCode.InternalServerError)
                }
                call.respondText(
                    Json.encodeToString(Response(e.message ?: "")),
                    contentType = ContentType.Application.Json,
                )
            }
        }
    }
}

object NotStaffException : Exception("Access denied") {
    private fun readResolve(): Any = NotStaffException
}