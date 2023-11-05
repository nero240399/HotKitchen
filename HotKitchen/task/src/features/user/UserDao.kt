package hotkitchen.features.user

interface UserDao {

    fun getUser(email: String): UserInfo?

    fun updateUser(userInfo: UserInfo)

    fun deleteUser(email: String)
}