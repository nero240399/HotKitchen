package hotkitchen.database.daos

import hotkitchen.database.DatabaseConnection
import hotkitchen.database.entities.OrderEntity
import hotkitchen.features.order.Order
import hotkitchen.features.order.OrderDao
import io.ktor.server.plugins.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class DefaultOrderDao : OrderDao {

    override fun insertOrder(order: Order) {
        DatabaseConnection.execute {
            if (OrderEntity.select { OrderEntity.id eq order.id }.singleOrNull() != null) {
                throw OrderAlreadyExists
            }
            OrderEntity.insert {
                it[id] = order.id
                it[userAddress] = order.userAddress
                it[userEmail] = order.userEmail
                it[price] = order.price
                it[status] = order.status
                it[mealIds] = order.mealIds.joinToString(", ")
            }
        }
    }

    override fun markReady(id: Int) {
        DatabaseConnection.execute {
            if (OrderEntity.select { OrderEntity.id eq id }.singleOrNull() == null) {
                throw NotFoundException()
            }
            OrderEntity.update({ OrderEntity.id eq id }) {
                it[status] = "COMPLETE"
            }
        }
    }

    override fun getOrders(): List<Order> {
        return DatabaseConnection.execute {
            OrderEntity.selectAll().map {
                Order(
                    id = it[OrderEntity.id],
                    userAddress = it[OrderEntity.userAddress],
                    userEmail = it[OrderEntity.userEmail],
                    price = it[OrderEntity.price],
                    status = it[OrderEntity.status],
                    mealIds = it[OrderEntity.mealIds].split(", ").map { id -> id.toInt() },
                )
            }
        }
    }

    override fun getIncompleteOrders(): List<Order> {
        return DatabaseConnection.execute {
            OrderEntity.selectAll().map {
                Order(
                    id = it[OrderEntity.id],
                    userAddress = it[OrderEntity.userAddress],
                    userEmail = it[OrderEntity.userEmail],
                    price = it[OrderEntity.price],
                    status = "IN PROGRESS",
                    mealIds = it[OrderEntity.mealIds].split(", ").map { id -> id.toInt() },
                )
            }
        }
    }
}

object OrderAlreadyExists : BadRequestException("This order already exists") {
    private fun readResolve(): Any = OrderAlreadyExists
}