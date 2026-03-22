# SC2002 Turn-Based Combat Arena

## Project Structure

The 27 class files are split into the following groups:

| Group | Files | Count |
|---|---|---|
| Combatant tree | Combatant, Player, Warrior, Wizard, Enemy, Goblin, Wolf | 7 |
| Action system | Action, BasicAttack, Defend, UseItem, SpecialSkill | 5 |
| Status effects | StatusEffect, StunEffect, DefendEffect, SmokeBombEffect, ArcaneBlastBuff | 5 |
| Items | Item, Potion, PowerStone, SmokeBomb | 4 |
| Turn order | TurnOrderStrategy, SpeedBasedOrder | 2 |
| Infrastructure | Level | 1 |
| Higher level | Main, GameUI, BattleManager | 3 |

---

## How to Run
```powershell
javac *.java
java Main
```

---

## Class Summaries

### Main
Entry point of the program. Handles setup and the replay loop.
- Creates GameUI and BattleManager, then calls `displayLoadingScreen()`
- Calls `pickPlayer()`, `pickItems()`, and `pickDifficulty()` on GameUI, then hands the choices to a new BattleManager and calls `runLevel()`
- After a game ends, `promptPostGame()` returns replay same settings, new game, or exit
- Private helper `resetPlayer()` re-creates a fresh Warrior or Wizard of the same type so replays start with full HP

### GameUI
The only class responsible for all input and output. Every other class calls GameUI for their display and input needs.

**Setup screens** (called from Main)
- `displayLoadingScreen()` — title screen on launch
- `pickPlayer()` — shows Warrior vs Wizard stats, reads choice, returns a Player object
- `pickItems(player)` — shows the 3 items, lets the player pick 2, adds them to inventory
- `pickDifficulty()` — shows Easy/Medium/Hard with enemy previews, returns a Level object

**During battle** (called from BattleManager)
- `displayLevelStart()` — shows the enemy lineup when a level begins
- `displayRoundStart()` — prints the round number header
- `displayTurnStart()` — shows whose turn it is and any active status effects
- `displayRoundEnd()` — shows everyone's remaining HP after each round
- `displayBackupSpawn()` — the backup enemies have arrived message

**Player decision-making** (called from Warrior.takeTurn() and Wizard.takeTurn())
- `promptPlayerAction()` — the main action menu. Handles edge cases like greying out items when inventory is empty and showing the cooldown timer on the skill
- `pickTarget()` — shows alive enemies with HP bars, returns the chosen Combatant

**End of game** (called from BattleManager)
- `displayBattleResult()` — Victory or Defeat screen with stats
- `promptPostGame()` — the replay/new game/exit menu

**Private helpers**
- `readInt(prompt, min, max)` — keeps asking until the player types a valid number in range, handles non-numeric input gracefully
- `clearScreen()` — tries to clear the terminal between screens, falls back to blank lines if it fails

### BattleManager
The engine of the game. Coordinates all other classes and handles the logic of running the fight.

**Main entry point** (called from Main)
- `runLevel()` — runs an entire level from start to finish. Spawns the initial enemy wave, loops through rounds, handles backup spawning between waves, cleans up the Wizard's Arcane Blast bonus at the end, and calls `displayBattleResult()` when done

**Private helpers**
- `runOneRound()` — runs a single round, called repeatedly inside `runLevel()`. Builds the turn order using TurnOrderStrategy, processes status effects for each combatant, skips stunned combatants, then calls `takeTurn()`. Checks for game end after every individual turn
- `checkForGameEnd()` — sets gameOver to true when the player's HP hits 0, or all enemies are defeated with no remaining backup wave
- `allEnemiesDefeated()` — returns true when every enemy in activeEnemies has 0 HP

**Public getters**
- `getPlayer()` — returns the player, used by enemies when targeting
- `getAliveEnemies()` — returns only living enemies, used for targeting menus and Arcane Blast
- `getGameUI()` — gives actions and players access to GameUI for target selection
- `getRoundNumber()` and `isPlayerWon()` — used by GameUI when displaying end-of-game statistics

> Key design decision: BattleManager holds TurnOrderStrategy as an interface rather than hardcoding SpeedBasedOrder directly, meaning the turn order algorithm can be swapped out without changing any other logic in this class.

---

## Class Types

| Type | Classes |
|---|---|
| Abstract | Combatant, Player, Enemy |
| Interface | Action, StatusEffect, Item, TurnOrderStrategy |
| Concrete (interface implementations) | BasicAttack, Defend, UseItem, SpecialSkill, StunEffect, DefendEffect, SmokeBombEffect, ArcaneBlastBuff, Potion, PowerStone, SmokeBomb, SpeedBasedOrder |
| Concrete (subclasses) | Warrior, Wizard, Goblin, Wolf |
| Plain concrete | Main, GameUI, BattleManager, Level |
