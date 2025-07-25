package com.mk.mkwk

import java.io.Serializable

data class Order(
    val category: String = "",
    val description: String = "",
    val status: String = "",
    val userId: String = "",
    val username: String = "",
    val link: String = "",
    val serviceBy: String = "",
    val amount: String = "",
    val charge: String = ""
) : Serializable
