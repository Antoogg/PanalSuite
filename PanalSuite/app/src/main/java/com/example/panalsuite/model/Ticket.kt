package com.example.panalsuite.model

data class Ticket (
    val id: String,
    val workflowType:String,
    val category:String,
    val priority:String,
    val state:String,
    val creationDate:String,
    val updateDate:String,
    val location:String,
    val applicantRole:String,
    val resolverTeam:String,
    val responsible:String,
    )