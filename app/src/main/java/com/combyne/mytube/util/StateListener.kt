package com.combyne.mytube.util

interface StateListener {
    fun onComplete(s: State)
    enum class State {
        SUCCESS, LOADING, ERROR
    }
}