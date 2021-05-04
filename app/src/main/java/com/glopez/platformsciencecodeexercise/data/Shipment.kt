package com.glopez.platformsciencecodeexercise.data

class Shipment(
    val id: Int,
    val streetName: String) {
    var assignedDriverId: Int = 0
    private val scores: MutableList<DriverScore> = mutableListOf()

    fun updateScores(scores: List<DriverScore>) {
        this.scores.addAll(scores)
    }

    fun getScores() : List<DriverScore> {
        return this.scores
    }
}