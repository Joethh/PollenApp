package com.example.pollenapp


data class Credentials(
    var login: String = "",
    var password: String = "",
) {
    fun isNotEmpty(): Boolean {
        return login.isNotEmpty() && password.isNotEmpty()
    }
}
