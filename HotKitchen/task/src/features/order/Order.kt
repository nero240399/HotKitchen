package hotkitchen.features.order

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Int,
    val userEmail: String,
    val mealIds: List<Int>,
    val price: Float,
    val userAddress: String,
    val status: String = "IN PROGRESS"
)
