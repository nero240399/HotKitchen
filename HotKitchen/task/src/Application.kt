package hotkitchen

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hotkitchen.features.authentication.authenticationRoute
import hotkitchen.database.daos.DefaultAuthenticationDao
import hotkitchen.database.daos.DefaultUserDao
import hotkitchen.database.setupDb
import hotkitchen.features.user.UserDao
import hotkitchen.features.user.userRoute
import hotkitchen.features.validate.validateRoute
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
    val authenticationDao = DefaultAuthenticationDao()
    this.authenticationModule(authenticationDao)
    setupDb()
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    routing {
        authenticationRoute(authenticationDao)
        validateRoute(authenticationDao)
        userRoute(DefaultUserDao())
    }
}

fun Application.authenticationModule(authenticationDao: DefaultAuthenticationDao) {
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
                if (authenticationDao.getUser(email) != null) {
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
