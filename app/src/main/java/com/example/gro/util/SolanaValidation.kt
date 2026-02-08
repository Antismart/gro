package com.example.gro.util

import org.sol4k.Base58

fun isValidSolanaAddress(address: String): Boolean {
    if (address.isBlank()) return false
    if (address.length !in 32..44) return false
    return try {
        val decoded = Base58.decode(address)
        decoded.size == 32
    } catch (_: Exception) {
        false
    }
}

fun isValidDepositAmount(amountSol: Double): Boolean {
    return amountSol > 0 && amountSol <= 1_000_000
}
