package hotkitchen.database.daos

import hotkitchen.database.DatabaseConnection
import hotkitchen.database.entities.CategoryEntity
import hotkitchen.database.entities.MealEntity
import hotkitchen.database.entities.UserAuthenticationEntity
import hotkitchen.features.meal.Category
import hotkitchen.features.meal.Meal
import hotkitchen.features.meal.MealDao
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

class DefaultMealDao : MealDao {

    override fun insertMeal(meal: Meal) {
        DatabaseConnection.execute {
            if (MealEntity.select {
                    MealEntity.id eq meal.id
                }.singleOrNull() != null) {
                throw MealAlreadyExists
            }
            MealEntity.insert {
                it[id] = meal.id
                it[title] = meal.title
                it[price] = meal.price
                it[imageUrl] = meal.imageUrl
                it[categoryIds] = meal.categoryIds.joinToString(", ")
            }
        }
    }

    override fun getMeals(): List<Meal> {
        var list = emptyList<Meal>()
        DatabaseConnection.execute {
            list = MealEntity.selectAll().map {
                Meal(
                    id = it[MealEntity.id],
                    title = it[MealEntity.title],
                    price = it[MealEntity.price],
                    imageUrl = it[MealEntity.imageUrl],
                    categoryIds = it[MealEntity.categoryIds].split(", ").map { category -> category.toInt() },
                )
            }
        }
        return list
    }

    override fun insertCategory(category: Category) {
        DatabaseConnection.execute {
            if (CategoryEntity.select {
                    CategoryEntity.id eq category.id
                }.singleOrNull() != null) {
                throw CategoryAlreadyExists
            }
            CategoryEntity.insert {
                it[id] = category.id
                it[description] = category.description
                it[title] = category.title
            }
        }
    }

    override fun getCategories(): List<Category> {
        var list = emptyList<Category>()
        DatabaseConnection.execute {
            list = CategoryEntity.selectAll().map {
                Category(
                    id = it[MealEntity.id],
                    title = it[MealEntity.title],
                    description = it[CategoryEntity.description]
                )
            }
        }
        return list
    }

    override fun getMeal(id: Int): Meal? {
        return DatabaseConnection.execute {
            val selected = MealEntity.select { MealEntity.id eq id }.singleOrNull() ?: return@execute null
            Meal(
                id = selected[MealEntity.id],
                title = selected[MealEntity.title],
                price = selected[MealEntity.price],
                imageUrl = selected[MealEntity.imageUrl],
                categoryIds = selected[MealEntity.categoryIds].split(", ").map { category -> category.toInt() },
            )
        }
    }

    override fun getCategory(id: Int): Category? {
        return DatabaseConnection.execute {
            val selected = CategoryEntity.select { MealEntity.id eq id }.singleOrNull() ?: return@execute null
            Category(
                id = selected[MealEntity.id],
                title = selected[MealEntity.title],
                description = selected[CategoryEntity.description]
            )
        }
    }
}

object MealAlreadyExists : Exception("Meal already exists") {
    private fun readResolve(): Any = UserAlreadyExists
}

object CategoryAlreadyExists : Exception("Category already exists") {
    private fun readResolve(): Any = UserAlreadyExists
}
