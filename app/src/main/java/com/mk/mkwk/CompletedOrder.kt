package com.mk.mkwk

data class CompletedOrder(
    val orderId: String = "",
    val userId: String = "",
    val category: String = "",
    val description: String = "",
    val serviceBy: String = "",
    val link: String = "",
    val amount: String = "",
    val charge: String = "",
    var username: String = ""
)