package hotkitchen.database.daos

import hotkitchen.database.DatabaseConnection
import hotkitchen.database.entities.UserEntity
import hotkitchen.model.User
import hotkitchen.user.UserDao
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class DefaultUserDao : UserDao {

    override fun getUser(email: String): User? {
        var user: User? = null
        DatabaseConnection.execute {
            user = UserEntity.select {
                UserEntity.email eq email
            }.singleOrNull()?.let {
                User(
                    email = it[UserEntity.email],
                    password = it[UserEntity.password],
                    userType = it[UserEntity.userType],
                )
            }
        }
        return user
    }
}

object UserAlreadyExists : Exception("User already exists") {
    private fun readResolve(): Any = UserAlreadyExists
}