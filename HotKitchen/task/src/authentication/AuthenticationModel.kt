package hotkitchen.authentication

import kotlinx.serialization.Serializable

@Serializable
data class UserAuthentication(
    val email: String,
    val password: String
)
