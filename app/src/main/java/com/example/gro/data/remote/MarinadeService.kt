package com.example.gro.data.remote

import org.sol4k.AccountMeta
import org.sol4k.PublicKey
import org.sol4k.instruction.BaseInstruction
import org.sol4k.instruction.CreateAssociatedTokenAccountInstruction
import org.sol4k.instruction.Instruction
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarinadeService @Inject constructor() {

    fun buildDepositInstructions(
        userWallet: PublicKey,
        lamports: Long,
    ): List<Instruction> {
        val instructions = mutableListOf<Instruction>()

        // 1. Create user's mSOL Associated Token Account (idempotent — fails silently if exists)
        val userMsolAta = PublicKey.findProgramAddress(
            listOf(userWallet, TOKEN_PROGRAM, MSOL_MINT),
            ATA_PROGRAM,
        ).publicKey

        instructions.add(
            CreateAssociatedTokenAccountInstruction(
                userWallet,     // payer
                userMsolAta,    // associatedToken
                userWallet,     // owner
                MSOL_MINT,      // mint
            ),
        )

        // 2. Marinade deposit instruction
        // Anchor discriminator: SHA256("global:deposit")[0..8]
        val discriminator = MessageDigest.getInstance("SHA-256")
            .digest("global:deposit".toByteArray())
            .sliceArray(0 until 8)

        val data = ByteBuffer.allocate(16)
            .order(ByteOrder.LITTLE_ENDIAN)
            .put(discriminator)
            .putLong(lamports)
            .array()

        val accounts = listOf(
            AccountMeta(MARINADE_STATE, false, true),          // state
            AccountMeta(MSOL_MINT, false, true),               // msol_mint
            AccountMeta(LIQ_POOL_SOL_LEG, false, false),      // liq_pool_sol_leg_pda
            AccountMeta(LIQ_POOL_MSOL_LEG, false, true),      // liq_pool_msol_leg
            AccountMeta(LIQ_POOL_MSOL_AUTHORITY, false, false),// liq_pool_msol_leg_authority
            AccountMeta(RESERVE_PDA, false, true),             // reserve_pda
            AccountMeta(userWallet, true, true),               // transfer_from (signer)
            AccountMeta(userMsolAta, false, true),             // mint_to (user's mSOL ATA)
            AccountMeta(MSOL_MINT_AUTHORITY, false, false),    // msol_mint_authority
            AccountMeta(SYSTEM_PROGRAM, false, false),         // system_program
            AccountMeta(TOKEN_PROGRAM, false, false),          // token_program
        )

        instructions.add(BaseInstruction(data, accounts, MARINADE_PROGRAM))

        return instructions
    }

    companion object {
        // Marinade Finance mainnet addresses
        val MARINADE_PROGRAM = PublicKey("MarBmsSgKXdrN1egZf5sqe1TMai9K1rChYNDJgjq7aD")
        val MARINADE_STATE = PublicKey("8szGkuLTAux9XMgZ2vtY39jVSowEcpBfFfD8hXSEqdGC")
        val MSOL_MINT = PublicKey("mSoLzYCxHdYgdzU16g5QSh3i5K3z3KZK7ytfqcJm7So")

        // Marinade PDAs (deterministic from state + seeds, never change on mainnet)
        val LIQ_POOL_SOL_LEG = PublicKey("UGy1j3YkhKYghoLqRQNtE6MHEkBxFSkGT1DosUPo7pg")
        val LIQ_POOL_MSOL_LEG = PublicKey("7GgPYjS5Dza89wV6FpZ23kUJRG5vbQ1GM25ezspYFSoE")
        val LIQ_POOL_MSOL_AUTHORITY = PublicKey("EyaSjUtSgo9aRD1f26LRetCe2secHUH1rcFCGJNUiQTf")
        val RESERVE_PDA = PublicKey("Du3Ysj1wKbxPKkuPPnvzQLQh8oMSVifs3jGZjJWXFmHN")
        val MSOL_MINT_AUTHORITY = PublicKey("3JLPCS1qM2zRw3Dp6V4hZnYHd3SzUTuEqhCzs19duHN")

        // Common Solana programs
        val SYSTEM_PROGRAM = PublicKey("11111111111111111111111111111111")
        val TOKEN_PROGRAM = PublicKey("TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA")
        val ATA_PROGRAM = PublicKey("ATokenGPvbdGVxr1b2hvZbsiqW5xWH25efTNsLJA8knL")
    }
}
