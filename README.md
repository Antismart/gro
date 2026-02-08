# Gro

**Your money is alive.**

Gro is a Solana Mobile savings and staking app that visualizes your crypto holdings as a living garden. Deposits plant seeds. Staking yields make them grow. Consistency creates beauty. Neglect causes cosmetic wilting — but your funds are always safe.

Built for the **Solana Mobile Hackathon (Monolith II)**.

---

## Features

### Living Garden
- Interactive Canvas-rendered garden with parallax sky, clouds, hills, grass, and soil layers
- 6 plant species mapped to Solana tokens (SOL, USDC, BONK, JUP, RAY, ORCA)
- 5 growth stages: Seed, Sprout, Sapling, Mature, Blooming
- Health system (0-100) with visual feedback — vibrant, muted, wilting, dormant
- Dynamic weather system reflecting user activity (Sunny, Partly Cloudy, Cloudy, Rainy, Golden Hour)
- Particle effects (sparkles, weather overlays)

### Wallet & Staking
- Mobile Wallet Adapter (MWA) integration for Phantom, Solflare, and Seed Vault
- Deposit SOL to plant or water your garden
- Marinade Finance liquid staking on mainnet (SOL to mSOL)
- Self-transfer on devnet for safe demo mode
- SPL Memo instructions on every deposit (`gro:deposit:v1:<species>:<lamports>`)
- Real-time token prices via Jupiter Price API v2
- Full USD portfolio value across SOL and all SPL token holdings
- Marinade APY display on plant detail screen

### Engagement
- Daily streak tracking with growth multiplier
- Streak badge with animated flame icon
- Garden journal with timeline and weekly summary
- On-chain transaction history sync via memo parsing
- Notification system (morning growth updates, streak reminders)
- Social garden visits — view a friend's garden by wallet address
- "Leave a sunflower" on-chain interaction (0.000001 SOL + memo)

### Design
- Custom design system — earthy warm palette, not Material 3 defaults
- Serif display font (Lora) + sans-serif body (DM Sans) + monospace (JetBrains Mono)
- Organic rounded shapes (12dp/20dp/28dp)
- Animated splash screen with growing vine Canvas animation
- 3-screen onboarding with Canvas-drawn seed-to-garden illustrations
- Custom bottom navigation with Canvas-drawn icons (leaf, droplet, binoculars, gear)
- Settings screen with wallet info, notification toggle, about section

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.0.21 (100%, no Java) |
| UI | Jetpack Compose (no XML layouts) |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt 2.51.1 |
| Database | Room 2.6.1 |
| Preferences | DataStore + EncryptedSharedPreferences |
| Networking | Ktor 2.3.12 + kotlinx.serialization |
| Solana SDK | Sol4k 0.4.2 |
| Wallet | Mobile Wallet Adapter 2.0.8 |
| Background | WorkManager |
| Animation | Compose Canvas + Lottie |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 36 |

---

## Project Structure

```
com.example.gro/
├── di/                     Hilt modules (App, Database, Network, Solana)
├── data/
│   ├── local/
│   │   ├── db/             Room database, DAOs, entities
│   │   ├── datastore/      DataStore preferences
│   │   └── secure/         EncryptedSharedPreferences token storage
│   ├── remote/             Solana RPC client, price feed, Marinade service
│   ├── repository/         Repository implementations
│   └── mapper/             Entity-to-domain mappers
├── domain/
│   ├── model/              Plant, PlantSpecies, GrowthStage, Streak, etc.
│   ├── repository/         Repository interfaces
│   └── usecase/            Business logic (Deposit, Sync, Visit, Weather, etc.)
├── ui/
│   ├── theme/              Custom colors, typography, shapes, spacing, gradients
│   ├── component/          Reusable composables (GroButton, garden components)
│   ├── screen/             Screens (splash, onboarding, garden, deposit, etc.)
│   └── navigation/         NavGraph, Screen routes, bottom nav bar
├── worker/                 WorkManager workers (garden sync, notifications)
├── notification/           Notification channel setup
└── util/                   Retry, validation, extensions
```

---

## Build

### Prerequisites

- Android Studio Ladybug or newer
- JDK 17 (bundled with Android Studio)
- Android SDK 36
- A Solana wallet app installed on the device (Phantom recommended)

### Commands

```bash
# Debug build (devnet)
./gradlew assembleDebug

# Release build (mainnet)
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Lint check
./gradlew lint

# Clean
./gradlew clean
```

### Build Variants

| Variant | RPC Endpoint | Cluster | Staking |
|---------|-------------|---------|---------|
| Debug | `api.devnet.solana.com` | devnet | Self-transfer (safe demo) |
| Release | `api.mainnet-beta.solana.com` | mainnet-beta | Marinade Finance (SOL to mSOL) |

---

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Install Phantom wallet on your Android device
5. Build and run the debug variant
6. Fund your devnet wallet at [faucet.solana.com](https://faucet.solana.com)
7. Connect your wallet through the onboarding flow
8. Make a deposit to plant your first seed

---

## Architecture

The app follows **Clean Architecture** with three layers:

- **Domain** — Pure Kotlin models, repository interfaces, and use cases. No Android dependencies.
- **Data** — Repository implementations, Room database, Solana RPC client, Marinade service, price feeds.
- **UI** — Jetpack Compose screens, ViewModels, custom theme and components.

All Solana interactions go through the Mobile Wallet Adapter for transaction signing. The app never holds private keys.

### Security

- Auth tokens stored in `EncryptedSharedPreferences` (AES-256-GCM)
- Solana address validation (Base58 decode, 32-byte check)
- Deposit amount validation
- Runtime Marinade account verification before staking
- All RPC calls wrapped with exponential backoff retry (500ms to 5s, 3 attempts)
- No private keys stored or transmitted

---

## Solana Integration

### On-Chain Interactions

| Action | Description | Memo Format |
|--------|-------------|-------------|
| Deposit | SOL transfer (devnet) or Marinade stake (mainnet) | `gro:deposit:v1:<SPECIES>:<LAMPORTS>` |
| Sunflower | 0.000001 SOL social "like" | `gro:sunflower:v1` |

### Token-to-Plant Mapping

| Token | Mint | Plant Species | Rarity |
|-------|------|--------------|--------|
| SOL | `So111...` | Solana Fern | Common |
| USDC | `EPjFW...` | Stableleaf | Common |
| BONK | `DezXA...` | Bonk Cactus | Uncommon |
| JUP | `JUPyi...` | Jupiter Vine | Uncommon |
| RAY | `4k3Dy...` | Radiant Sunflower | Rare |
| ORCA | `orcaE...` | Ocean Lily | Rare |

---

## License

MIT
