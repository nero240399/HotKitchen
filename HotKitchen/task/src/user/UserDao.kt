package hotkitchen.user

import hotkitchen.model.User

interface UserDao {

    fun getUser(email: String): User?
}