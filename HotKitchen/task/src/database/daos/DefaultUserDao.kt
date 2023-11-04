package hotkitchen.database.daos

import hotkitchen.database.DatabaseConnection
import hotkitchen.database.entities.UserEntity
import hotkitchen.user.User
import hotkitchen.user.UserDao
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class DefaultUserDao : UserDao {
    override fun signUp(user: User) {
        DatabaseConnection.execute {
            if (UserEntity.select {
                    UserEntity.email eq user.email
                }.firstOrNull() != null) {
                throw Exception("User $id existed")
            }
            UserEntity.insert {
                it[email] = user.email
                it[userType] = user.userType
                it[password] = user.password
            }
        }
    }
}