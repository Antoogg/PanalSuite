package com.example.panalsuite.model

enum class UserRole{
    APPLICANT,
    RESOLVER,
    SUPERVISOR,
    MANAGEMENT,
    ADMINISTRATOR
}

data class User(
    val id:String,
    val name:String,
    val email:String,
    val role: UserRole
)