package hotkitchen.database.daos

import hotkitchen.authentication.UserAuthentication
import hotkitchen.database.DatabaseConnection
import hotkitchen.database.entities.UserAuthenticationEntity
import hotkitchen.database.entities.UserInfoEntity
import hotkitchen.user.UserDao
import hotkitchen.user.UserInfo
import org.h2.engine.User
import org.jetbrains.exposed.sql.Except
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

class DefaultUserDao : UserDao {

    override fun getUser(email: String): UserInfo? {
        var user: UserInfo? = null
        DatabaseConnection.execute {
            user = UserInfoEntity.select {
                UserInfoEntity.email eq email
            }.singleOrNull()?.let {
                UserInfo(
                    name = it[UserInfoEntity.name],
                    userType = it[UserInfoEntity.userType],
                    phone = it[UserInfoEntity.phone],
                    email = it[UserInfoEntity.email],
                    address = it[UserInfoEntity.address],
                )
            }
        }
        return user
    }

    override fun updateUser(userInfo: UserInfo) {
        DatabaseConnection.execute {
            UserInfoEntity.update({ UserInfoEntity.email eq userInfo.email }) {
                it[name] = userInfo.name
                it[userType] = userInfo.userType
                it[phone] = userInfo.phone
                it[address] = userInfo.address
            }
        }
    }

    override fun deleteUser(email: String) {
        DatabaseConnection.execute {
            if (UserInfoEntity.deleteWhere { UserInfoEntity.email eq email } == 0)
                throw UserNotFoundException
        }
    }
}

object UserAlreadyExists : Exception("User already exists") {
    private fun readResolve(): Any = UserAlreadyExists
}

object UserNotFoundException : Exception("User not found") {
    private fun readResolve(): Any = UserNotFoundException
}