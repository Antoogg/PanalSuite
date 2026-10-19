
package com.example.panalsuite.repository

import com.example.panalsuite.data.local.dao.TicketDao
import com.example.panalsuite.data.local.entity.TicketEntity
import com.example.panalsuite.network.ApiClient
import com.example.panalsuite.network.ApiTestPost

class TicketRepository {

    suspend fun saveTicketToDatabase(
        dao: TicketDao,
        ticketEntity: TicketEntity
    ) {
        dao.insertTicket(ticketEntity)
    }

    suspend fun deleteTicketFromDatabase(
        dao: TicketDao,
        ticketId: String
    ) {
        dao.deleteTicket(ticketId)
    }

    suspend fun getPostsFromApi(): List<ApiTestPost> {
        return ApiClient.service.getPosts()
    }
}
