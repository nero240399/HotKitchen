package hotkitchen.database.entities

import org.jetbrains.exposed.sql.Table

object CategoryEntity : Table() {
    val id = integer("id")
    val title = varchar("title", 50)
    val description = varchar("description", 500)
}