package hotkitchen.user

interface UserDao {

    fun signUp(user: User)

    fun getUser(email: String): User?
}