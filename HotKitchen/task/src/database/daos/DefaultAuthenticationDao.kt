package hotkitchen.database.daos

import hotkitchen.authentication.AuthenticationDao
import hotkitchen.authentication.UserAuthentication
import hotkitchen.database.DatabaseConnection
import hotkitchen.database.entities.UserEntity
import org.jetbrains.exposed.sql.select

class DefaultAuthenticationDao : AuthenticationDao {
    override fun signIn(userAuthentication: UserAuthentication) {
        DatabaseConnection.execute {
            UserEntity.select {
                UserEntity.email eq userAuthentication.email
                UserEntity.password eq userAuthentication.password
            }.singleOrNull() ?: throw Exception("User $id not found")
        }
    }
}