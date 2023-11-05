package hotkitchen.features.meal

import kotlinx.serialization.Serializable

@Serializable
data class Meal(
    val id: Int,
    val title: String,
    val price: Int,
    val imageUrl: String,
    val categoryIds: List<Int>
)

@Serializable
data class Category(
    val id: Int,
    val title: String,
    val description: String
)