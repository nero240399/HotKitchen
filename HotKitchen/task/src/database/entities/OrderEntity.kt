package hotkitchen.database.entities

import org.jetbrains.exposed.sql.Table

object OrderEntity : Table() {
    val id = integer("id")
    val userEmail = varchar("userEmail", 50)
    val mealIds = varchar("mealIds", 50)
    val price = float("price")
    val userAddress = varchar("userAddress", 50)
    val status = varchar("status", 50)
}

