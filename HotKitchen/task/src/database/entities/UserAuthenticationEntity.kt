package hotkitchen.database.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object UserAuthenticationEntity : IntIdTable() {
    val email: Column<String> = varchar("email", 50)
    val userType: Column<String> = varchar("userType", 50)
    val token: Column<String> = varchar("password", 500)
}