package hotkitchen.authentication

import hotkitchen.models.User

interface AuthenticationDao {

    fun signIn(userAuthentication: UserAuthentication): String

    fun signUp(user: User)
}