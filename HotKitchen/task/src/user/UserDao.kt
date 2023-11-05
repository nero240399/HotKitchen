package hotkitchen.user

import hotkitchen.models.User

interface UserDao {

    fun getUser(email: String): User?
}