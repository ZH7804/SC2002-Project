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

## Combatant Tree
 
### Combatant (Abstract)
The base class for every character in the game. Holds the shared stats (hp, atk, def, spd) and the list of active status effects. Every player and enemy is a Combatant.
- `processStatusEffects()` — called at the start of each turn; loops through active effects, calls `apply()`, `tick()`, and removes any that have expired via `onRemove()`
- `addStatusEffect(effect)` — attaches a new status effect to this combatant
- `hasEffect(class)` — checks whether a specific effect type is currently active, e.g. `hasEffect(StunEffect.class)`
- `removeEffect(class)` — removes all effects of a given type, used for end-of-level cleanup
- `takeDamage(amount)` — reduces HP by the given amount, clamped to a minimum of 0
- `heal(amount)` — increases HP, capped at maxHp
- `isAlive()` — returns true if currentHp is above 0
- `takeTurn(battle)` — abstract; each subclass defines what happens on their turn
- Getters for all stats, setters for attack and defense (needed so effects can temporarily modify them)
- `toString()` — returns a formatted stat line, e.g. `Warrior [HP: 180/260 | ATK: 40 | DEF: 20 | SPD: 30]`
 
### Player (Abstract, extends Combatant)
Extends Combatant with everything specific to a human-controlled character. Still abstract — Warrior and Wizard are the real concrete players.
- Holds an inventory list of Items (max 2, chosen at game start)
- `addItem(item)` / `removeItem(item)` / `getInventory()` / `hasItems()` — inventory management, called by GameUI and UseItem
- `isSkillReady()` — returns true when skillCooldown is 0
- `getSkillCooldown()` — returns the number of turns remaining on the cooldown
- `startSkillCooldown()` — sets the cooldown to 3 after the skill is used
- `tickCooldown()` — counts the cooldown down by 1; called at the end of every `takeTurn()` so it only ticks on turns the player actually takes
- `executeSpecialSkillEffect(battle)` — abstract; Warrior and Wizard each implement this with their own skill logic
- `getSkillDescription()` — abstract; returns a short description shown in the action menu
 
### Warrior (extends Player)
HP: 260 | ATK: 40 | DEF: 20 | SPD: 30
- `executeSpecialSkillEffect(battle)` — Shield Bash: deals ATK minus DEF damage to one chosen enemy, then applies a StunEffect to them. Called by SpecialSkill action and PowerStone item
- `getSkillDescription()` — returns the Shield Bash description shown in the menu
- `takeTurn(battle)` — calls `GameUI.promptPlayerAction()` to get the player's chosen action, executes it, then calls `tickCooldown()`
 
### Wizard (extends Player)
HP: 200 | ATK: 50 | DEF: 10 | SPD: 20
- `executeSpecialSkillEffect(battle)` — Arcane Blast: deals ATK minus DEF damage to every living enemy simultaneously. For each kill, stacks +10 ATK onto the Wizard for the rest of the level
- `resetArcaneBonus()` — subtracts all stacked Arcane Blast attack from the Wizard's stats and resets the counter to 0. Called by BattleManager at the end of the level
- `getSkillDescription()` — returns the Arcane Blast description shown in the menu
- `takeTurn(battle)` — same as Warrior; calls `promptPlayerAction()`, executes the action, ticks cooldown
 
### Enemy (Abstract, extends Combatant)
Enemies have no decision-making — they always BasicAttack the player. Still abstract because a raw Enemy has no stats.
- `takeTurn(battle)` — checks for StunEffect first; if stunned, skips the turn. Otherwise creates a BasicAttack targeting the player and executes it
 
### Goblin (extends Enemy)
HP: 55 | ATK: 35 | DEF: 15 | SPD: 25. No methods beyond the constructor — inherits everything from Enemy.
 
### Wolf (extends Enemy)
HP: 40 | ATK: 45 | DEF: 5 | SPD: 35. Fast and hits hard but low HP. No methods beyond the constructor — inherits everything from Enemy.
 
---
 
## Action System
 
### Action (Interface)
The contract that every action must follow. Applying the Open/Closed Principle — adding a new action only requires a new class, nothing existing needs to change.
- `execute(attacker, targets, battle)` — carries out the action
- `getName()` — returns the display name used in menus
 
### BasicAttack (implements Action)
The standard attack used by all combatants. Checks for SmokeBombEffect on the target before dealing damage — if smoke is active, the attack deals 0 damage. Otherwise applies ATK minus DEF damage, minimum 0.
- Constructor takes the chosen target as a parameter since the target is selected before `execute()` is called
- `execute(attacker, targets, battle)` — runs the smoke check, calculates damage, calls `takeDamage()` on the target
- `getName()` — returns "Basic Attack"
 
### Defend (implements Action)
Applies a +10 defense boost to the attacker immediately, then attaches a DefendEffect to count down and remove the bonus after 2 turns.
- `execute(attacker, targets, battle)` — calls `setDefense()` to apply the bonus, then `addStatusEffect(new DefendEffect())`
- `getName()` — returns "Defend"
 
### UseItem (implements Action)
Bridges the gap between the player choosing to use an item and the item doing its thing. Calls `use()` on the item, then removes it from the player's inventory since items are single-use.
- Constructor takes both the chosen item and the player who owns it
- `execute(attacker, targets, battle)` — calls `itemToUse.use()` then `owner.removeItem()`
- `getName()` — returns "Use " followed by the item name
 
### SpecialSkill (implements Action)
Handles the cooldown gate for special skills. Checks whether the skill is ready, blocks it with a message if not, otherwise fires `executeSpecialSkillEffect()` on the player and starts the 3-turn cooldown.
- `execute(attacker, targets, battle)` — cooldown check, then calls `player.executeSpecialSkillEffect()` and `player.startSkillCooldown()`
- `getName()` — returns "Special Skill"
 
---
 
## Status Effects
 
### StatusEffect (Interface)
The contract all status effects follow. Each effect has a lifecycle: applied at the start of a turn, ticked down, then removed when expired.
- `apply(target)` — called at the start of the affected combatant's turn
- `tick()` — counts duration down by 1
- `isExpired()` — returns true when duration hits 0
- `onRemove(target)` — called once just before removal, used to undo any stat changes
- `getName()` — display label shown in the UI
 
### StunEffect (implements StatusEffect)
Applied by the Warrior's Shield Bash. Prevents the affected combatant from acting for 2 turns. The actual action-blocking logic lives in BattleManager and Enemy, which check `hasEffect(StunEffect.class)` before calling `takeTurn()`.
- `turnsRemaining` starts at 2 and counts down via `tick()`
- `apply()` — keeps the stun flag active; no stat changes needed
- `onRemove()` — nothing to undo, just deactivates the flag
 
### DefendEffect (implements StatusEffect)
Applied when a player uses the Defend action. Tracks the duration of the +10 DEF bonus. The bonus itself is applied immediately in the Defend action — DefendEffect just knows when to take it back.
- `turnsRemaining` starts at 2
- `apply()` — does nothing each turn; the bonus is already in the stats
- `onRemove(target)` — subtracts 10 from the combatant's defense and prints a message
- `getDefenseBonus()` — static method returning 10, used by the Defend action when applying the initial boost
 
### SmokeBombEffect (implements StatusEffect)
Applied when the player uses a Smoke Bomb item. Signals to BasicAttack that the target should take 0 damage. The damage negation logic lives in BasicAttack, which calls `hasEffect(SmokeBombEffect.class)` before calculating damage.
- `turnsRemaining` starts at 2
- `apply()` — does nothing; the check lives in BasicAttack
- `onRemove(target)` — prints "the smoke clears" message
 
### ArcaneBlastBuff (implements StatusEffect)
Tracks the Wizard's stacking attack bonus from Arcane Blast kills. Unlike other effects it never expires on its own — BattleManager removes it manually at the end of each level by calling `Wizard.resetArcaneBonus()`.
- `tick()` — does nothing, no countdown
- `isExpired()` — only returns true when `expire()` is called manually
- `onRemove(target)` — subtracts the stored attack bonus from the Wizard's stats
- `expire()` — manually flags the effect for removal
- `getAttackBonus()` — returns the total attack bonus currently stored
 
---
 
## Items
 
### Item (Interface)
The contract all items follow. Items are single-use and removed from inventory by UseItem after being used.
- `use(user, battle)` — the item does its thing; battle is passed in so items that need game state (like PowerStone) can access it
- `getName()` — display name shown in inventory
- `getDescription()` — short description shown next to the name in the menu
 
### Potion (implements Item)
Heals the user for 100 HP, capped at their max HP.
- `use(user, battle)` — calculates the actual heal amount (capped at missing HP), calls `user.heal(100)`, prints the result
 
### PowerStone (implements Item)
Triggers the player's special skill effect for free without starting or affecting the cooldown. Bypasses the cooldown check in SpecialSkill by calling `executeSpecialSkillEffect()` directly on the Player.
- `use(user, battle)` — casts user to Player and calls `executeSpecialSkillEffect(battle)` directly
 
### SmokeBomb (implements Item)
Applies a SmokeBombEffect to the user, causing enemy attacks to deal 0 damage for 2 turns.
- `use(user, battle)` — calls `user.addStatusEffect(new SmokeBombEffect())`
 
---
 
### TurnOrderStrategy (Interface)
Defines how combatants are sorted into turn order each round. Held as an interface in BattleManager so the algorithm can be swapped without touching any other logic.
- `getTurnOrder(everyone)` — takes the full list of living combatants and returns them sorted into the order they should act
 
### SpeedBasedOrder (implements TurnOrderStrategy)
The turn order implementation used in this game. Sorts all living combatants by speed descending — highest speed goes first. Java's stable sort guarantees that combatants with equal speed keep their original relative order.
- `getTurnOrder(everyone)` — copies the input list, sorts by `getSpeed()` descending, returns the sorted copy
 
---
 
### Level
Stores the difficulty number and handles all enemy spawning. Called by BattleManager at the start of a level and after each wave is cleared.
- `spawnInitialEnemies()` — returns the first wave of enemies based on level number: Easy = 3 Goblins, Medium = 1 Goblin + 1 Wolf, Hard = 2 Goblins
- `spawnBackupEnemies()` — returns the backup wave if one exists and hasn't been spawned yet, then flips `backupAlreadySpawned` to true so it can only fire once. Returns an empty list if there is no backup or it has already been used
- `hasBackupWave()` — returns true for Medium and Hard only
- `isBackupSpawned()` — lets BattleManager check whether the backup has already arrived, used in the win condition check
- `getLevelNumber()` / `getDifficultyName()` — getters used by GameUI for display
- `toString()` — returns e.g. "Level 2 (Medium)" used in display output
