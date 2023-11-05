import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hyperskill.hstest.dynamic.DynamicTest
import org.hyperskill.hstest.stage.StageTest
import org.hyperskill.hstest.testcase.CheckResult

class HotKitchenTest : StageTest<Any>() {

    @Serializable
    private data class Credentials(var email: String, var userType: String, var password: String)

    @Serializable
    private data class Token(val token: String)

    @Serializable
    data class Meal(
        val mealId: Int,
        val title: String,
        val price: Float,
        val imageUrl: String,
        val categoryIds: List<Int>
    )

    @Serializable
    data class Category(
        val categoryId: Int,
        val title: String,
        val description: String
    )

    private val time = System.currentTimeMillis()
    private val jwtRegex = """^[a-zA-Z0-9]+?\.[a-zA-Z0-9]+?\..+""".toRegex()
    private val accessDenied = """{"status":"Access denied"}"""
    private val currentCredentialsClient = Credentials("$time@client.com", "client", "password$time")
    private val currentCredentialsStaff = Credentials("$time@staff.com", "staff", "password$time")
    private val currentMeal = Meal(
        time.toInt(),
        "$time title",
        (time.toInt() % 100).toFloat(),
        "image $time url",
        listOf((0..10).random(), (0..10).random(), (0..10).random())
    )
    private val currentCategory = Category(
        time.toInt(),
        "$time TITLE",
        "Awesome $time description"
    )
    private lateinit var signInTokenClient: String
    private lateinit var signInTokenStaff: String


    @DynamicTest(order = 1)
    fun getSignInJWTToken(): CheckResult {
        var result = CheckResult.correct()
        try {
            testApplication {
                var response = client.post("/signup") {
                    setBody(Json.encodeToString(currentCredentialsClient))
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                    try {
                        val principal = Json.decodeFromString<Token>(response.bodyAsText() ?: "")
                        signInTokenClient = principal.token
                        if (!signInTokenClient.matches(jwtRegex) || signInTokenClient.contains(currentCredentialsClient.email)) {
                            result = CheckResult.wrong("Invalid JWT token")
                            return@testApplication
                        }
                    } catch (e: Exception) {
                        result = CheckResult.wrong("Cannot get token form /signup request")
                    }

                response = client.post("/signin") {
                    setBody(Json.encodeToString(currentCredentialsStaff))
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                    try {
                        val principal = Json.decodeFromString<Token>(response.bodyAsText() ?: "")
                        signInTokenStaff = principal.token
                        if (!signInTokenStaff.matches(jwtRegex) || signInTokenStaff.contains(currentCredentialsStaff.email))
                            result = CheckResult.wrong("Invalid JWT token")
                    } catch (e: Exception) {
                        result = CheckResult.wrong("Cannot get token form /signin request")
                    }
            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest(order = 2)
    fun correctValidation(): CheckResult {
        var result = CheckResult.correct()
        try {
            testApplication {
                var response = client.get("/features/validate") {
                    header(HttpHeaders.Authorization, "Bearer $signInTokenClient")
                }
                if (response.status != HttpStatusCode.OK
                    || response.bodyAsText() != "Hello, ${currentCredentialsClient.userType} ${currentCredentialsClient.email}") {
                    result = CheckResult.wrong(
                        "Token validation with signin token failed.\n" +
                                "Status code should be \"200 OK\"\n" +
                                "Message should be \"Hello, ${currentCredentialsClient.userType} ${currentCredentialsClient.email}\""
                    )
                    return@testApplication
                }

                response = client.get("/features/validate") {
                    header(HttpHeaders.Authorization, "Bearer $signInTokenStaff")
                }
                if (response.status != HttpStatusCode.OK
                    || response.bodyAsText() != "Hello, ${currentCredentialsStaff.userType} ${currentCredentialsStaff.email}")
                    result = CheckResult.wrong(
                    "Token validation with signin token failed.\n" +
                            "Status code should be \"200 OK\"\n" +
                            "Message should be \"Hello, ${currentCredentialsStaff.userType} ${currentCredentialsStaff.email}\""
                )
            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest(order = 3)
    fun accessDeniedAdditionMeal(): CheckResult {
        var result = CheckResult.correct()
        try {
            testApplication {
                val response = client.post("/meals") {
                    setBody(Json.encodeToString(currentMeal))
                    header(HttpHeaders.Authorization, "Bearer $signInTokenClient")
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                if (response.status != HttpStatusCode.Forbidden || response.bodyAsText() != accessDenied)
                    result = CheckResult.wrong("Only staff can add meal. Wrong response or status code")
            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest(order = 4)
    fun accessDeniedAdditionCategory(): CheckResult {
        var result = CheckResult.correct()
        try {
            testApplication {
                val response = client.post("/categories") {
                    setBody(Json.encodeToString(currentCategory))
                    header(HttpHeaders.Authorization, "Bearer $signInTokenClient")
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                if (response.status != HttpStatusCode.Forbidden || response.bodyAsText() != accessDenied)
                    result = CheckResult.wrong("Only staff can add category. Wrong response or status code")
            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest(order = 5)
    fun successAdditionMeal(): CheckResult {
        var result = CheckResult.correct()
        try {
            testApplication {
                val response = client.post("/meals") {
                    setBody(Json.encodeToString(currentMeal))
                    header(HttpHeaders.Authorization, "Bearer $signInTokenStaff")
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                if (response.status != HttpStatusCode.OK)
                    result = CheckResult.wrong("The meal was not added. Wrong status code.")
            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest(order = 6)
    fun failedAdditionMeal(): CheckResult {
        var result = CheckResult.correct()
        try {
            testApplication {
                val response = client.post("/meals") {
                    setBody(Json.encodeToString(currentMeal))
                    header(HttpHeaders.Authorization, "Bearer $signInTokenStaff")
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                if (response.status != HttpStatusCode.BadRequest)
                    result = CheckResult.wrong("The meal was added twice. Wrong status code.")
            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest(order = 7)
    fun successAdditionCategory(): CheckResult {
        var result = CheckResult.correct()
        try {
            testApplication {
                val response = client.post("/categories") {
                    setBody(Json.encodeToString(currentCategory))
                    header(HttpHeaders.Authorization, "Bearer $signInTokenStaff")
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                if (response.status != HttpStatusCode.OK)
                    result = CheckResult.wrong("The category was not added. Wrong status code.")
            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest(order = 8)
    fun failedAdditionCategory(): CheckResult {
        var result = CheckResult.correct()
        try {
            testApplication {
                val response = client.post("/categories") {
                    setBody(Json.encodeToString(currentCategory))
                    header(HttpHeaders.Authorization, "Bearer $signInTokenStaff")
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                if (response.status != HttpStatusCode.BadRequest)
                    result = CheckResult.wrong("The category was added twice. Wrong status code.")
            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest(order = 9)
    fun getMealById(): CheckResult {
        var result = CheckResult.correct()
        try {
            testApplication {
                val response = client.get("/meals?id=${currentMeal.mealId}") {
                    header(HttpHeaders.Authorization, "Bearer $signInTokenClient")
                }
                if (response.bodyAsText() != Json.encodeToString(currentMeal))
                    result = CheckResult.wrong("Wrong meal by id.")
            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest(order = 10)
    fun getCategoryById(): CheckResult {
        var result = CheckResult.correct()
        try {
            testApplication {
                val response = client.get("/categories?id=${currentCategory.categoryId}") {
                    header(HttpHeaders.Authorization, "Bearer $signInTokenClient")
                }
                if (response.bodyAsText() != Json.encodeToString(currentCategory))
                    result = CheckResult.wrong("Wrong category by id.")
            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }


    @DynamicTest(order = 11)
    fun getMeals(): CheckResult {
        var result = CheckResult.correct()
        try {
            testApplication {
                val response = client.get("/meals") {
                    header(HttpHeaders.Authorization, "Bearer $signInTokenClient")
                }
                val meals: List<Meal> = Json.decodeFromString(response.bodyAsText() ?: "")
                var flag = true
                for (meal in meals) {
                    if (meal.mealId == currentMeal.mealId) {
                        flag = false
                        break
                    }
                }
                if (flag) {
                    result = CheckResult.wrong("Wrong meals list. The newly added meal is missing.")
                    return@testApplication
                }
                if (response.status != HttpStatusCode.OK)
                    result = CheckResult.wrong("Wrong status code in /meals")
            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }

    @DynamicTest(order = 12)
    fun getCategories(): CheckResult {
        var result = CheckResult.correct()
        try {
            testApplication {
                val response = client.get("/categories") {
                    header(HttpHeaders.Authorization, "Bearer $signInTokenClient")
                }
                val categories: List<Category> = Json.decodeFromString(response.bodyAsText() ?: "")
                var flag = true
                for (category in categories) {
                    if (category.categoryId == currentCategory.categoryId) {
                        flag = false
                        break
                    }
                }
                if (flag) {
                    result = CheckResult.wrong("Wrong categories list. The newly added category is missing.")
                    return@testApplication
                }
                if (response.status != HttpStatusCode.OK)
                    result = CheckResult.wrong("Wrong status code in /categories")
            }
        } catch (e: Exception) {
            result = CheckResult.wrong(e.message)
        }
        return result
    }
}