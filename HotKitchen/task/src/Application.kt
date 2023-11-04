package hotkitchen

import hotkitchen.authentication.authenticationRoute
import hotkitchen.database.daos.DefaultAuthenticationDao
import hotkitchen.database.daos.DefaultUserDao
import hotkitchen.database.setupDb
import hotkitchen.user.userRoute
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    setupDb()
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    routing {
        authenticationRoute(DefaultAuthenticationDao())
        userRoute(DefaultUserDao())
    }
}