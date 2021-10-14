package com.kunangkunang.app.model.order

data class Order(
    var roomId: Int? = null,
    var itemId: Int? = null,
    var categoryId: Int? = null,
    var orderCategory: String? = null,
    var orderDetail: String? = null,
    var orderQuantity: Int? = null,
    var orderPrice: Int? = null,
    var notes: String? = null,
    var spaDate: String? = null,
    var spaStart: String? = null,
    var spaEnd: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Order

        if (roomId != other.roomId) return false
        if (itemId != other.itemId) return false
        if (categoryId != other.categoryId) return false
        if (orderCategory != other.orderCategory) return false
        if (orderDetail != other.orderDetail) return false
        if (orderPrice != other.orderPrice) return false
        if (notes != other.notes) return false
        if (spaDate != other.spaDate) return false
        if (spaStart != other.spaStart) return false
        if (spaEnd != other.spaEnd) return false

        return true
    }

    override fun hashCode(): Int {
        var result = roomId ?: 0
        result = 31 * result + (itemId ?: 0)
        result = 31 * result + (categoryId ?: 0)
        result = 31 * result + (orderCategory?.hashCode() ?: 0)
        result = 31 * result + (orderDetail?.hashCode() ?: 0)
        result = 31 * result + (orderPrice ?: 0)
        result = 31 * result + (notes?.hashCode() ?: 0)
        result = 31 * result + (spaDate?.hashCode() ?: 0)
        result = 31 * result + (spaStart?.hashCode() ?: 0)
        result = 31 * result + (spaEnd?.hashCode() ?: 0)
        return result
    }
}