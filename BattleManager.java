import java.util.ArrayList;
import java.util.List;

//battle manager handles the logic of the game itself

public class BattleManager {

    private Player player;
    private List<Enemy> activeEnemies;
    private Level currentLevel;
    private TurnOrderStrategy turnOrder;
    private GameUI ui;

    private int roundNumber = 0;
    private boolean gameOver = false;
    private boolean playerWon = false;

    public BattleManager(Player player, Level level, GameUI ui, TurnOrderStrategy strategy) {
        this.player = player;
        this.currentLevel = level;
        this.ui = ui;
        this.activeEnemies = new ArrayList<>();
        this.turnOrder = strategy;
    }


    public void runLevel() { //runs a level from start to finish
        // Spawn the first wave of enemies
        List<Enemy> firstWave = currentLevel.spawnInitialEnemies();
        activeEnemies.addAll(firstWave);

        ui.displayLevelStart(currentLevel, activeEnemies);

        // Keep going until someone wins or the player dies
        while (!gameOver) {
            runOneRound();

            // If everyone's dead but we have a backup wave pending, spawn it
            if (allEnemiesDefeated() && currentLevel.hasBackupWave() && !currentLevel.isBackupSpawned()) {
                List<Enemy> backup = currentLevel.spawnBackupEnemies();
                if (!backup.isEmpty()) {
                    activeEnemies.addAll(backup);
                    ui.displayBackupSpawn(backup);
                }
            }

            checkForGameEnd();
        }

        // End of level - clean up the Wizard's Arcane Blast stacks if needed - otherwise it bugs out and carries over
        if (player instanceof Wizard) {
            ((Wizard) player).resetArcaneBonus();
        }

        ui.displayBattleResult(playerWon, player, roundNumber, getAliveEnemies());
    }

    private void runOneRound() { //runs a single round, which is called within runLevel()
        roundNumber++;
        ui.displayRoundStart(roundNumber);

        List<Combatant> everyone = new ArrayList<>();
        everyone.add(player);
        everyone.addAll(activeEnemies.stream().filter(Enemy::isAlive).toList());

        List<Combatant> turnSequence = turnOrder.getTurnOrder(everyone); //uses the getTurnOrder method which is currently based off speed

        for (Combatant current : turnSequence) {
            // Skip if this combatant died earlier in this very round
            if (!current.isAlive()) continue;

            current.processStatusEffects(); //apply status effects

            if (current.hasEffect(StunEffect.class)) { //if stunned, skip turn and continue out of the loop
                System.out.println(current.getName() + " is stunned - skipping their turn.");
                continue;
            }

            ui.displayTurnStart(current); //if still in the loop take the turn
            current.takeTurn(this);

            checkForGameEnd(); //check if round has ended, which would exit the loop
            if (gameOver) return;
        }

        ui.displayRoundEnd(player, getAliveEnemies());
    }

    private void checkForGameEnd() { //helper function to check whether game is over
        if (!player.isAlive()) {
            gameOver = true;
            playerWon = false;
        } else if (allEnemiesDefeated()) { //ensures that victory only occurs when all backups are gone
            if (!currentLevel.hasBackupWave() || currentLevel.isBackupSpawned()) {
                gameOver = true;
                playerWon = true;
            }
        }
    }

    private boolean allEnemiesDefeated() {
        return activeEnemies.stream().noneMatch(Enemy::isAlive);
    }

    public Player getPlayer() {
        return player;
    }

    public List<Combatant> getAliveEnemies() {
        List<Combatant> alive = new ArrayList<>();
        for (Enemy e : activeEnemies) {
            if (e.isAlive()) alive.add(e);
        }
        return alive;
    }

    public GameUI getGameUI() {
        return ui;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public boolean isPlayerWon() {
        return playerWon;
    }
}
