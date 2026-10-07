package com.example.panalsuite.repository

import android.provider.ContactsContract
import com.example.panalsuite.model.User
import com.example.panalsuite.model.UserRole

class UserRepository {

    private val user = listOf(
        User(
            id = "USR-001",
            name = "Usuario Solicitante",
            email = "solicitante@panalsuite.cl",
            role = UserRole.APPLICANT
        ),
        User(
            id  = "USR-002",
            name = "Usuario Resolutor",
            email = "resolutor@panalsuite.cl",
            role = UserRole.RESOLVER
        ),
        User(
            id  = "USR-003",
            name = "Usuario Supervisor",
            email = "supervisor@panalsuite.cl",
            role = UserRole.SUPERVISOR
        ),
        User(
            id  = "USR-004",
            name = "Usuario Gerencia",
            email = "gerencia@panalsuite.cl",
            role = UserRole.MANAGEMENT
        ),
        User(
            id  = "USR-005",
            name = "Usuario Administrador",
            email = "admin@panalsuite.cl",
            role = UserRole.RESOLVER
        )
    )
    fun login(email: String, password: String): User? {
        if (password != "123456") {
            return null
        }

        return user.find { user ->
            user.email.equals(email.trim(), ignoreCase = true)
        }
    }
}