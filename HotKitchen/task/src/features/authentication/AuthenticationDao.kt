package hotkitchen.features.authentication

interface AuthenticationDao {

    fun signIn(user: UserAuthentication): String

    fun signUp(user: UserAuthentication)

    fun getUser(email: String): UserAuthentication?
}