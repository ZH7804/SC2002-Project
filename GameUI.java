import java.util.List;
import java.util.Scanner;


// this handles all the UI portion of the game, i.e. all input/output from scanner accesse this class
public class GameUI {

    private Scanner scanner;

    public GameUI() {
        this.scanner = new Scanner(System.in);
    }
    
    public void displayLoadingScreen() { //this is the title screen
        clearScreen();
        System.out.println("==========================================================");
        System.out.println("           TURN-BASED COMBAT ARENA");
        System.out.println("==========================================================");
        System.out.println();
        System.out.println("  A battle awaits. Choose your fighter and prove yourself.");
        System.out.println();
        System.out.println("  Press ENTER to begin...");
        scanner.nextLine();
    }

    public Player pickPlayer() { //this is the character selection screen
        clearScreen();
        System.out.println("==========================================================");
        System.out.println("  CHOOSE YOUR CHARACTER");
        System.out.println("==========================================================");
        System.out.println();

        System.out.println("  [1] WARRIOR");
        System.out.println("      HP: 260 | ATK: 40 | DEF: 20 | SPD: 30");
        System.out.println("      Skill: Shield Bash");
        System.out.println("        -> Attack one enemy and stun them for 2 turns");
        System.out.println();

        System.out.println("  [2] WIZARD");
        System.out.println("      HP: 200 | ATK: 50 | DEF: 10 | SPD: 20");
        System.out.println("      Skill: Arcane Blast");
        System.out.println("        -> Hits ALL enemies; gain +10 ATK per kill (this level)");
        System.out.println();

        int choice = readInt("  Your choice (1 or 2): ", 1, 2);

        return switch (choice) {
            case 1 -> new Warrior();
            case 2 -> new Wizard();
            default -> new Warrior(); // shouldn't happen given readInt bounds but just in case
        };
    }

    public void pickItems(Player player) { //this is the item picking screen
        clearScreen();
        System.out.println("==========================================================");
        System.out.println("  CHOOSE YOUR ITEMS  (pick 2 - duplicates allowed)");
        System.out.println("==========================================================");
        System.out.println();

        Item[] availableItems = { new Potion(), new PowerStone(), new SmokeBomb() };

        for (int i = 0; i < availableItems.length; i++) {
            System.out.println("  [" + (i + 1) + "] " + availableItems[i].getName());
            System.out.println("       " + availableItems[i].getDescription());
            System.out.println();
        }

        for (int slot = 1; slot <= 2; slot++) {
            int choice = readInt("  Item " + slot + " (1-3): ", 1, 3);
            // Re-instantiate so each item is a fresh object
            Item chosen = switch (choice) {
                case 1 -> new Potion();
                case 2 -> new PowerStone();
                case 3 -> new SmokeBomb();
                default -> new Potion();
            };
            player.addItem(chosen);
            System.out.println("  Added: " + chosen.getName());
        }

        System.out.println();
        System.out.println("  Items chosen! Press ENTER to continue...");
        scanner.nextLine();
    }

    public Level pickDifficulty() { //dificulty option selector screen
        clearScreen();
        System.out.println("==========================================================");
        System.out.println("  CHOOSE DIFFICULTY");
        System.out.println("==========================================================");
        System.out.println();

        System.out.println("  [1] EASY");
        System.out.println("      Enemies: 3 Goblins");
        System.out.println("      No backup wave");
        System.out.println();

        System.out.println("  [2] MEDIUM");
        System.out.println("      Enemies: 1 Goblin, 1 Wolf");
        System.out.println("      Backup wave: 2 Wolves");
        System.out.println();

        System.out.println("  [3] HARD");
        System.out.println("      Enemies: 2 Goblins");
        System.out.println("      Backup wave: 1 Goblin + 2 Wolves");
        System.out.println();

        System.out.println("  --- Enemy Stats for Reference ---");
        System.out.println("  Goblin: HP 55 | ATK 35 | DEF 15 | SPD 25");
        System.out.println("  Wolf:   HP 40 | ATK 45 | DEF  5 | SPD 35");
        System.out.println();

        int choice = readInt("  Your choice (1-3): ", 1, 3);
        return new Level(choice);
    }

    public void displayLevelStart(Level level, List<Enemy> enemies) { //battle screen - start of battle
        System.out.println();
        System.out.println("==========================================================");
        System.out.println("  " + level + " - FIGHT!");
        System.out.println("==========================================================");
        System.out.println("  Enemies:");
        for (Enemy e : enemies) {
            System.out.println("    - " + e);
        }
        System.out.println();
        pause();
    }

    public void displayRoundStart(int roundNumber) { //battle screen - displays round number
        System.out.println();
        System.out.println("----------------------------------------------------------");
        System.out.println("  ROUND " + roundNumber);
        System.out.println("----------------------------------------------------------");
    }

    public void displayTurnStart(Combatant combatant) { //battle screen - displays turn
        System.out.println();
        System.out.println("  >> " + combatant.getName() + "'s turn");

        if (!combatant.getActiveEffects().isEmpty()) { //battle screen - shows the active effects at the start of the turn
            System.out.print("     Active effects: ");
            for (StatusEffect e : combatant.getActiveEffects()) {
                System.out.print("[" + e.getName() + "] ");
            }
            System.out.println();
        }
    }

    public void displayRoundEnd(Player player, List<Combatant> aliveEnemies) {
        System.out.println();
        System.out.println("  --- End of Round ---");
        System.out.println("  " + player.getName() + ": " + player.getCurrentHp() + "/" + player.getMaxHp() + " HP");
        System.out.println("  Enemies remaining: " + aliveEnemies.size());
        for (Combatant e : aliveEnemies) {
            System.out.println("    - " + e.getName() + ": " + e.getCurrentHp() + "/" + e.getMaxHp() + " HP");
        }
    }

    public void displayBackupSpawn(List<Enemy> backup) {
        System.out.println();
        System.out.println("  !! BACKUP ENEMIES HAVE ARRIVED !!");
        for (Enemy e : backup) {
            System.out.println("    + " + e);
        }
        System.out.println();
        pause();
    }

    public Action promptPlayerAction(Player player, BattleManager battle) {
        while (true) { 
            System.out.println("\n-- " + player.getName() + "'s Turn --");
            System.out.println(" [1] Basic Attack");
            System.out.println(" [2] Defend");
        
            String skillStatus = player.isSkillReady() ? "" : " (Cooldown: " + player.getSkillCooldown() + ")";
            System.out.println(" [3] Special Skill: " + player.getSkillDescription() + skillStatus);
        
            System.out.println(" [4] Use Item");

            int choice = readInt("  Choose action: ", 1, 4);

            if (choice == 3 && !player.isSkillReady()) {
                System.out.println("\n  [!] That skill is on cooldown! Choose another action.");
                continue; // Restart the loop to pick another available skill, don't return an action yet
            }
        
            if (choice == 4 && !player.hasItems()) {
                System.out.println("\n  [!] Your inventory is empty!");
                continue;
            }

            return switch (choice) { //if validation passes
                case 1 -> new BasicAttack(pickTarget(battle.getAliveEnemies()));
                case 2 -> new Defend();
                case 3 -> new SpecialSkill();
                case 4 -> {Item item = pickItem(player);yield new UseItem(item, player);}
                default -> new BasicAttack(battle.getAliveEnemies().get(0));
            };
            }
        }

    public Combatant pickTarget(List<Combatant> aliveEnemies) { //function to pick a target amongst available enemy options
        System.out.println();
        System.out.println("  Pick a target:");
        for (int i = 0; i < aliveEnemies.size(); i++) {
            Combatant e = aliveEnemies.get(i);
            System.out.println("  [" + (i + 1) + "] " + e.getName()
                    + " (HP: " + e.getCurrentHp() + "/" + e.getMaxHp() + ")");
        }

        int choice = readInt("  Target (1-" + aliveEnemies.size() + "): ", 1, aliveEnemies.size());
        return aliveEnemies.get(choice - 1);
    }

    private Item pickItem(Player player) { //function to display player's inventory and pick an item to use from within
        List<Item> inventory = player.getInventory(); 
        System.out.println();
        System.out.println("  Your items:");
        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.get(i);
            System.out.println("  [" + (i + 1) + "] " + item.getName()
                    + " - " + item.getDescription());
        }

        int choice = readInt("  Use item (1-" + inventory.size() + "): ", 1, inventory.size());
        return inventory.get(choice - 1);
    }

    public void displayBattleResult(boolean playerWon, Player player, int rounds, List<Combatant> remainingEnemies) { //screen when game/round ends
        System.out.println();
        System.out.println("==========================================================");

        if (playerWon) {
            System.out.println("  VICTORY!");
            System.out.println("  Congratulations, you have defeated all your enemies.");
            System.out.println();
            System.out.println("  Remaining HP : " + player.getCurrentHp() + "/" + player.getMaxHp());
            System.out.println("  Total Rounds : " + rounds);
        } else {
            System.out.println("  DEFEATED.");
            System.out.println("  Don't give up, try again!");
            System.out.println();
            System.out.println("  Enemies remaining : " + remainingEnemies.size());
            System.out.println("  Rounds survived   : " + rounds);
        }

        System.out.println("==========================================================");
    }

    public int promptPostGame() { //postgame menu with options to replay, new game or to exit
        System.out.println();
        System.out.println("  What would you like to do?");
        System.out.println("  [1] Play again with the same settings");
        System.out.println("  [2] Start a new game");
        System.out.println("  [3] Exit");
        System.out.println();
        return readInt("  Choice (1-3): ", 1, 3);
    }

    private int readInt(String prompt, int min, int max) { //helper function to ensure that values entered are integers, else it will continually reprompt
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("  Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  That's not a valid number - try again.");
            }
        }
    }

    private void pause() {
        System.out.print("  Press ENTER to continue...");
        scanner.nextLine();
    }

    private void clearScreen() { //uh this is just something cool to help clear the terminal screen and demonstrate try/catch exception handling, but if it doesn't work then we will just use 3 blank lines to "clear"
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.println("\n\n\n");
        }
    }

    public TurnOrderStrategy pickTurnOrder() {
        System.out.println("\n==========================================================");
        System.out.println("  CHOOSE TURN ORDER");
        System.out.println("==========================================================");
        System.out.println("  [1] Speed Based (higher speed acts first)");
        System.out.println("  [2] Lowest HP First (wounded combatants act earlier)");
        int choice = readInt("  Your choice (1-2): ", 1, 2);
        if (choice==1){
            return new SpeedBasedOrder();
        }
        else{
            return new HPOrder();
        }
}
}
