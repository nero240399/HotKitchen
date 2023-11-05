package hotkitchen

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import hotkitchen.authentication.authenticationRoute
import hotkitchen.database.daos.DefaultAuthenticationDao
import hotkitchen.database.daos.DefaultUserDao
import hotkitchen.database.setupDb
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

const val Secret = "nero240399"

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val userDao = DefaultUserDao()
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

fun Application.authenticationModule() {
    install(Authentication) {
        jwt {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(Secret))
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("email").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    "All authorization must be done using a Bearer token in headers.\n"
                )
            }
        }
    }
}
