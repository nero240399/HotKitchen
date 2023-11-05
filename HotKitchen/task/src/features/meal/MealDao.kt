package hotkitchen.features.meal

interface MealDao {

    fun insertMeal(meal: Meal)

    fun getMeals(): List<Meal>

    fun insertCategory(category: Category)

    fun getCategories(): List<Category>

    fun getMeal(id: Int): Meal?

    fun getCategory(id: Int): Category?
}