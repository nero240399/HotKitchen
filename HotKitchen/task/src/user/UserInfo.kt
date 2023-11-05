package hotkitchen.user

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val name: String = "",
    val userType: String = "",
    val phone: String = "",
    val email: String,
    val address: String = ""
)