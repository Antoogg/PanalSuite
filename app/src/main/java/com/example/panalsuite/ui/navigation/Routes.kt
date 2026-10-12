package com.example.panalsuite.ui.navigation

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val CREATE_TICKET = "createTicket"
    const val TICKET_DETAIL = "ticketDetail/{ticketId}"
    const val EVIDENCE = "evidence/{ticketId}"

    fun ticketDetail(ticketId: String) = "ticketDetail/$ticketId"
    fun evidence(ticketId: String) = "evidence/$ticketId"
}