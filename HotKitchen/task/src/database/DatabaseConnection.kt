package hotkitchen.database

import hotkitchen.database.entities.UserAuthenticationEntity
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseConnection {
    private val db = Database.connect(
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
        driver = "org.h2.Driver"
    )

    fun init() {
        transaction(db) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(UserAuthenticationEntity)
        }
    }

    fun <T> execute(blk: Transaction.() -> T): T =
        transaction(db, statement = blk)
}

fun Application.setupDb() {
    DatabaseConnection.init()
}