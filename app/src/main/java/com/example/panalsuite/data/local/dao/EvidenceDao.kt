package com.example.panalsuite.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.panalsuite.data.local.entity.EvidenceEntity
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