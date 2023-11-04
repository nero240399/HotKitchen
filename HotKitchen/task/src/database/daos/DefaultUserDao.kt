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
                throw UserAlreadyExists
            }
            UserEntity.insert {
                it[email] = user.email
                it[userType] = user.userType
                it[password] = user.password
            }
        }
    }

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