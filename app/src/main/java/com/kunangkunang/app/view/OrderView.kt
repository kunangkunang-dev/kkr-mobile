package com.kunangkunang.app.view

interface OrderView<T> {
    fun addOrder(data: T)
    fun removeOrder(index: Int?)
}