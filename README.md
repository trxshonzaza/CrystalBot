# CrystalPvP Bot Plugin

A fully customizable **Crystal PvP combat bot** for Minecraft **1.20.1+**, designed to simulate high-level PvP behavior including pearling, anchoring, digging, and more. Ideal for testing or training, or if you just want to have a fight with yourself.

---

## 📦 Requirements

- **Minecraft Version:** `1.20.1-1.21.5 (last tested)`
- **Required Dependencies:**
  - [Citizens](https://www.spigotmc.org/resources/citizens.13811/) Get the appropriate version of Citizens for your server.
  - [Sentinel](https://www.spigotmc.org/resources/sentinel.22017/)

---

## 💡 Features

- **Anchoring**
  - Full support for anchor PvP.
- **Digging**
  - Digs and uses crystals if it is stuck or is closed up in a corner.
- **Eating Mechanics**
  - Automatically eats enchanted golden apples at low health.
- **Durability Management**
  - Armor durability is automatically adjusted when it takes damage.
- **Pearling**
  - Can use pearls to go to you if you are running away.
- **Fully Configurable**
  - Almost all behavior is adjustable: crystal place and break delays, anchor place and break delays, when to place anchors, when to dig, and more.
  - You can also customize how much totems the bot has, how many stacks of golden apples it has, and more.

### 💬 Commands

```bash
/spawngui # opens the GUI to spawn a bot.
/removebot # removes the bot assigned to the player who ran the command.
