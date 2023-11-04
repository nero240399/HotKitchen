package hotkitchen.database.daos

import hotkitchen.authentication.AuthenticationDao
import hotkitchen.authentication.UserAuthentication
import hotkitchen.database.DatabaseConnection
import hotkitchen.database.entities.UserEntity
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
}

object InvalidEmailOrPassword : Exception("Invalid email or password") {
    private fun readResolve(): Any = InvalidEmailOrPassword
}