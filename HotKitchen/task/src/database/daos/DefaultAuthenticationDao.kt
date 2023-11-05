package hotkitchen.database.daos

import hotkitchen.authentication.AuthenticationDao
import hotkitchen.authentication.UserAuthentication
import hotkitchen.database.DatabaseConnection
import hotkitchen.database.entities.UserEntity
import hotkitchen.models.User
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class DefaultAuthenticationDao : AuthenticationDao {
    override fun signIn(userAuthentication: UserAuthentication): String {
        var password = ""
        DatabaseConnection.execute {
            val user = UserEntity.select {
                UserEntity.email eq userAuthentication.email
                UserEntity.password eq userAuthentication.password
            }.singleOrNull() ?: throw InvalidEmailOrPassword
            password = user[UserEntity.password]
        }
        return password
    }

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
}

object InvalidEmailOrPassword : Exception("Invalid email or password") {
    private fun readResolve(): Any = InvalidEmailOrPassword
}