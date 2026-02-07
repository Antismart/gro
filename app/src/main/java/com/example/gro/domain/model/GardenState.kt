package com.example.gro.domain.model

data class GardenState(
    val plants: List<Plant> = emptyList(),
    val totalPortfolioValueLamports: Long = 0,
    val solBalance: Long = 0,
    val gridColumns: Int = 4,
    val gridRows: Int = 3,
)
