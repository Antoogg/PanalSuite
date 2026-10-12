package com.example.panalsuite.repository

import com.example.panalsuite.data.local.EvidenceDao
import com.example.panalsuite.data.local.EvidenceEntity
import kotlinx.coroutines.flow.Flow

class EvidenceRepository {

    fun getEvidencesFromDatabase(
        dao: EvidenceDao,
        ticketId: String
    ): Flow<List<EvidenceEntity>> {
        return dao.getEvidencesByTicket(ticketId)
    }

    suspend fun saveEvidenceToDatabase(
        dao: EvidenceDao,
        evidence: EvidenceEntity
    ) {
        dao.insertEvidence(evidence)
    }
}