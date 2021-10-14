package com.kunangkunang.app.api

interface AppRepositoryCallback<T> {
    fun onDataLoaded(data: T)
    fun onDataError()
}