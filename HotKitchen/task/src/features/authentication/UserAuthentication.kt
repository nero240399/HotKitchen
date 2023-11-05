package hotkitchen.features.authentication

import kotlinx.serialization.Serializable

@Serializable
data class UserAuthentication(
    val email: String,
    val token: String,
    val userType: String
)
