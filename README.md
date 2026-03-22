The 27 class files can be split in the below:
Combatant tree: Combatant (Abstract), Player (Abstract), Warrior, Wizard, Enemy (Abstract), Goblin, Wolf — 7 - Goblin and Wolf are actually files with just their stats, so not too much to handle there, while Warrior and Wizard need to include implementation of their special skills from Player’s abstract methods.
Action system: Action (Interface), BasicAttack, Defend, UseItem, SpecialSkill — 5
Status effects: StatusEffect (Interface), StunEffect, DefendEffect, SmokeBombEffect, ArcaneBlastBuff — 5
Items: Item (Interface), Potion, PowerStone, SmokeBomb — 4
Turn order: TurnOrderStrategy (Interface), SpeedBasedOrder — 2
Infrastructure: Level — 1
Higher level classes: Main, GameUI, BattleManager — 3
Main is the entry point of the program, it's the first thing Java runs, and its only job is setting up the game and managing the replay loop.
It does three things:
It creates the GameUI and BattleManager objects, then calls displayLoadingScreen().
It calls pickPlayer(), pickItems(), and pickDifficulty() on GameUI to get the player's choices, then hands them to a new BattleManager and calls runLevel()
After a game ends, promptPostGame() returns either replay same settings, new game, or exit. For same settings it re-creates the player and level from scratch so HP and state reset cleanly. For new game it clears the saved settings so the full setup flow runs again. For exit it breaks out of the loop.
The one private helper within the class is resetPlayer(). It takes the previous player as a template and returns a brand new Warrior or Wizard of the same type, ensuring the replay starts with full HP rather than the battered state from the last game.

GameUI is the class and file that contains everything that is output/input facing, i.e. the only section of the code that is responsible for displaying information and getting inputs from the user. The remaining classes will use GameUI and its methods for their input/output needs. It contains the following methods, which are all public outside of the two private helpers:
Setup screens (called from Main)
displayLoadingScreen() — the title screen on launch
pickPlayer() — shows Warrior vs Wizard stats, reads choice, returns a Player object
pickItems(player) — shows the 3 items, lets the player pick 2, adds them to inventory
pickDifficulty() — shows Easy/Medium/Hard with enemy previews, returns a Level object
During battle (called from BattleManager)
displayLevelStart() — shows the enemy lineup when a level begins
displayRoundStart() — prints the round number header
displayTurnStart() — shows whose turn it is + any active status effects
displayRoundEnd() — shows everyone's remaining HP after each round
displayBackupSpawn() — the "backup enemies have arrived!" message
Player decision-making (called from Warrior.takeTurn() and Wizard.takeTurn())
promptPlayerAction() — the main action menu: Basic Attack, Defend, Use Item, Special Skill. It also handles edge cases like greying out the item option when inventory is empty, and showing the cooldown timer on the skill
pickTarget() — shows alive enemies with HP bars, returns the chosen Combatant
End of game (called from BattleManager)
displayBattleResult() — Victory or Defeat screen with stats
promptPostGame() — the replay/new game/exit menu
The two private helpers
readInt(prompt, min, max) — the input safety net. Keeps asking until the player types a valid number in range, handles non-numeric input gracefully
clearScreen() — tries to clear the terminal between screens, falls back to blank lines if it fails

BattleManager is the engine of the game and focuses on the logic of running the game, it coordinates all other classes related to the game and calls them when needed. It contains the following methods, split into public and private:
The main entry point (called from Main)
runLevel() — runs an entire level from start to finish. It spawns the initial enemy wave, loops through rounds until the game ends, handles backup spawning between waves, cleans up the Wizard's Arcane Blast bonus at the end, and calls displayBattleResult() when done.
Private helpers
runOneRound() — runs a single round, called repeatedly inside runLevel(). It increments the round counter, builds the turn order using TurnOrderStrategy, then loops through each combatant. For each combatant it processes their status effects first, skips their turn if stunned, then calls takeTurn(). After every individual turn it checks if the game has ended mid-round.
checkForGameEnd() — a helper that sets gameOver = true when either the player's HP hits 0, or all enemies are defeated and there's no remaining backup wave to spawn.
allEnemiesDefeated() — a simple helper that returns true when every enemy in activeEnemies has 0 HP.
Public getters
getPlayer() — returns the player, used by enemies when targeting
getAliveEnemies() — returns only living enemies, used for targeting menus and Arcane Blast
getGameUI() — gives actions and players access to GameUI so they can prompt for target selection
getRoundNumber() and isPlayerWon() — used by GameUI when displaying end-of-game statistics
The key design decision is that BattleManager holds the TurnOrderStrategy as an interface rather than hardcoding SpeedBasedOrder directly, which allows us to edit the TurnOrderStrategy if we ever needed, which would in turn sort the array and order the character movements differently.
