package hotkitchen.database.entities

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object MealEntity : Table() {
    val id = integer("id").uniqueIndex()
    val title = varchar("title", 50)
    val price = integer("price")
    val imageUrl = varchar("imageUrl", 50)
    val categoryIds = varchar("categoryIds", 50)
}