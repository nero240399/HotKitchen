package hotkitchen.authentication

interface AuthenticationDao {
    fun signIn(userAuthentication: UserAuthentication): String
}