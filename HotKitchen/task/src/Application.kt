package hotkitchen

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hotkitchen.authentication.authenticationRoute
import hotkitchen.database.daos.DefaultAuthenticationDao
import hotkitchen.database.daos.DefaultUserDao
import hotkitchen.database.setupDb
import hotkitchen.user.UserDao
import hotkitchen.validate.validateRoute
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val userDao = DefaultUserDao()
    this.authenticationModule(userDao)
    setupDb()
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    routing {
        authenticationRoute(DefaultAuthenticationDao())
        validateRoute(userDao)
    }
}

fun Application.authenticationModule(userDao: UserDao) {
    val secret = environment.config.property("jwt.secret").getString()
    install(Authentication) {
        jwt {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .build()
            )
            validate { credential ->
                val email = credential.payload.getClaim("email").asString()
                if (userDao.getUser(email) != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    "Token is not valid or has expired"
                )
            }
        }
    }
}
