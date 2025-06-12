# 🎰 Scratch Game

A CLI-based scratch card game built in Java where players place bets and win based on symbol combinations generated in a 2D matrix.
The game supports standard and bonus symbols, custom configuration via JSON, and flexible win conditions.

---

## 📋 Features

- Configurable matrix size (default: 3x3)
- Standard & Bonus symbols with weighted probabilities
- Configurable winning combinations (horizontal, vertical, diagonal, count-based)
- Bonus symbols that modify final reward (e.g., +1000, 10x)
- JSON-based configuration
- CLI executable JAR

---

## 🚀 Getting Started

### ✅ Requirements

- Java 8+ (JDK 1.8 or higher)
- Maven (for build)

---

## 🔧 Configuration

Create or use the provided `config.json` to define the game settings:

--config config.json

## 🏃 Run

```bash
 java -jar target/scratchGame-1.0.jar --config config.json --betting-amount 100
```