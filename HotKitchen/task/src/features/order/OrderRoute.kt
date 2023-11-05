package hotkitchen.features.order

import hotkitchen.features.authentication.AuthenticationDao
import hotkitchen.features.meal.Meal
import hotkitchen.features.meal.NotStaffException
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

fun Route.orderRoute(authenticationDao: AuthenticationDao, orderDao: OrderDao) {
    authenticate {
        post("/order") {
            try {
                val order = call.receive<Order>()
                orderDao.insertOrder(order)
                call.respondText(
                    Json.encodeToString(order),
                    contentType = ContentType.Application.Json,
                    HttpStatusCode.OK
                )
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
        post("/order/{orderId}/markReady") {
            try {
                val email = call.principal<JWTPrincipal>()!!.payload.getClaim("email").asString()
                if (authenticationDao.getUser(email)?.userType != "staff") {
                    throw NotStaffException
                }
                orderDao.markReady(
                    call.receiveParameters()["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid order id")
                )
            } catch (e: Exception) {
                when (e) {
                    is BadRequestException -> call.response.status(HttpStatusCode.BadRequest)
                    is NotFoundException -> call.response.status(HttpStatusCode.NotFound)
                    is NotStaffException -> call.response.status(HttpStatusCode.Forbidden)
                    else -> call.response.status(HttpStatusCode.InternalServerError)
                }
                call.respondText(
                    Json.encodeToString(Response(e.message ?: "")),
                    contentType = ContentType.Application.Json,
                )
            }
        }
        get("/orderHistory") {
            try {
                val orders = orderDao.getOrders()
                call.respondText(
                    Json.encodeToString(orders),
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
        get("/orderIncomplete") {
            try {
                val orders = orderDao.getIncompleteOrders()
                call.respondText(
                    Json.encodeToString(orders),
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
    }
}
