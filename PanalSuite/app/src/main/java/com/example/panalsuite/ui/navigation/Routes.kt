package com.example.panalsuite.ui.navigation

object Routes {
    const val HOME = "home"
    const val TICKET_DETAIL = "ticketDetail/{ticketId}"

    fun ticketDetail(ticketId: String) = "ticketDetail/$ticketId"
}