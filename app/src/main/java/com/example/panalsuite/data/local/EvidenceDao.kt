package com.example.panalsuite.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenceDao {

    @Insert
    suspend fun insertEvidence(evidence: EvidenceEntity)

    @Query(
        "SELECT * FROM evidences WHERE ticketId = :ticketId ORDER BY createdAt DESC"
    )
    fun getEvidencesByTicket(ticketId: String): Flow<List<EvidenceEntity>>
}