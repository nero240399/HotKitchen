package hotkitchen.features.order

interface OrderDao {

    fun insertOrder(order: Order)

    fun markReady(id: Int)

    fun getOrders(): List<Order>

    fun getIncompleteOrders(): List<Order>
}