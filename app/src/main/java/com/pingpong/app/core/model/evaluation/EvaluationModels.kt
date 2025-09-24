package com.pingpong.app.core.model.evaluation

data class EvaluationItem(
    val id: Long,
    val appointmentId: Long?,
    val evaluatorId: Long?,
    val evaluatorType: String?,
    val content: String?,
    val createdAt: String?,
    val updatedAt: String?
)
