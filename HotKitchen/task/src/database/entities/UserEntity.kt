package hotkitchen.database.entities

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object UserEntity : IntIdTable() {
    val email: Column<String> = varchar("email", 50)
    val userType: Column<String> = varchar("userType", 50)
    val password: Column<String> = varchar("password", 500)
}