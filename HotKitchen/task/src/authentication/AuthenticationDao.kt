package hotkitchen.authentication

import hotkitchen.model.User

interface AuthenticationDao {
    fun signIn(userAuthentication: UserAuthentication): String

    fun signUp(user: User)
}