package hotkitchen.database.daos

import hotkitchen.features.authentication.AuthenticationDao
import hotkitchen.features.authentication.UserAuthentication
import hotkitchen.database.DatabaseConnection
import hotkitchen.database.entities.UserAuthenticationEntity
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class DefaultAuthenticationDao : AuthenticationDao {
    override fun signIn(user: UserAuthentication): String {
        var password = ""
        DatabaseConnection.execute {
            val authentication = UserAuthenticationEntity.select {
                UserAuthenticationEntity.email eq user.email
                UserAuthenticationEntity.token eq user.token
            }.singleOrNull() ?: throw InvalidEmailOrPassword
            password = authentication[UserAuthenticationEntity.token]
        }
        return password
    }

    override fun signUp(user: UserAuthentication) {
        DatabaseConnection.execute {
            if (UserAuthenticationEntity.select {
                    UserAuthenticationEntity.email eq user.email
                }.firstOrNull() != null) {
                throw UserAlreadyExists
            }
            UserAuthenticationEntity.insert {
                it[email] = user.email
                it[userType] = user.userType
                it[token] = user.token
            }
        }
    }

    override fun getUser(email: String): UserAuthentication? {
        var user: UserAuthentication? = null
        DatabaseConnection.execute {
            user = UserAuthenticationEntity.select {
                UserAuthenticationEntity.email eq email
            }.singleOrNull()?.let {
                UserAuthentication(
                    email = it[UserAuthenticationEntity.email],
                    token = it[UserAuthenticationEntity.token],
                    userType = it[UserAuthenticationEntity.userType],
                )
            }
        }
        return user
    }
}

object InvalidEmailOrPassword : Exception("Invalid email or password") {
    private fun readResolve(): Any = InvalidEmailOrPassword
}