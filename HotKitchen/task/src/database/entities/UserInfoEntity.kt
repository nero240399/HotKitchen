package hotkitchen.database.entities

import org.h2.table.Table
import org.jetbrains.exposed.dao.id.IntIdTable

object UserInfoEntity : IntIdTable() {
    val name = varchar("name", 50)
    val userType = varchar("userType", 50)
    val phone = varchar("phone", 50)
    val email = varchar("email", 50)
    val address = varchar("address", 50)
}