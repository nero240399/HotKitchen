package hotkitchen.models

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val status: String = "",
    val token: String = ""
)
