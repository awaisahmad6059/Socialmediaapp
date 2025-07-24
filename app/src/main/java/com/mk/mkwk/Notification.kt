package com.mk.mkwk

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val read: Boolean
)