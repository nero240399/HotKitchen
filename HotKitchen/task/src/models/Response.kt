package hotkitchen.model

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val status: String = "",
    val token: String = ""
)
