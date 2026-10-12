package com.example.panalsuite.model

enum class UserRole(
    val canCreateTicket: Boolean = false,
    val canChangeStatus: Boolean = false,
    val canAddEvidence: Boolean = false
) {
    APPLICANT(
        canCreateTicket = true
    ),
    RESOLVER(
        canChangeStatus = true,
        canAddEvidence = true
    ),
    SUPERVISOR(
        canCreateTicket = true,
        canChangeStatus = true,
        canAddEvidence = true
    ),
    MANAGEMENT(
        canChangeStatus = true
    ),
    ADMINISTRATOR(
        canCreateTicket = true,
        canChangeStatus = true,
        canAddEvidence = true
    )
}