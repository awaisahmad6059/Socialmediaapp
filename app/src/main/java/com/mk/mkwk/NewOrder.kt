package com.mk.mkwk

data class NewOrder(
    val orderId: String = "",
    val userId: String = "",
    val category: String = "",
    val description: String = "",
    val link: String = "",
    val serviceBy: String = "",
    val amount: Double = 0.0,
    val charge: Double = 0.0,
    val status: String = "",
    var username: String = ""
) {
    // Secondary constructor that accepts string values for amount and charge
    constructor(
        orderId: String = "",
        userId: String = "",
        category: String = "",
        description: String = "",
        link: String = "",
        serviceBy: String = "",
        status: String = "",
        amount: String,
        charge: String,
        username: String = ""
    ) : this(
        orderId = orderId,
        userId = userId,
        category = category,
        description = description,
        link = link,
        serviceBy = serviceBy,
        status = status,
        amount = amount.toDoubleOrNull() ?: 0.0,
        charge = charge.toDoubleOrNull() ?: 0.0,
        username = username
    )
}