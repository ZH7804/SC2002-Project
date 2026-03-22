//the class responsible for running everything
public class Main {

    public static void main(String[] args) {
        GameUI ui = new GameUI();
        ui.displayLoadingScreen();

        Player lastPlayer = null;
        Level lastLevel = null;

        boolean keepPlaying = true;

        while (keepPlaying) {
            Player player;
            Level level;
            
            boolean replaySameSettings = (lastPlayer != null && lastLevel != null); // If the player wants to replay with the same settings, skip setup

            if (replaySameSettings) {
                player = resetPlayer(lastPlayer);
                level = new Level(lastLevel.getLevelNumber());
            } else {
                player = ui.pickPlayer(); //have to re-pick everything if they are either playing for first time or choose to not replay
                ui.pickItems(player);
                level = ui.pickDifficulty();
            }

            // Save these in case of replay
            lastPlayer = player;
            lastLevel = level;

            BattleManager battle = new BattleManager(player, level, ui); //using the battlemanager to start running the battle
            battle.runLevel();

            // Ask what to do after the game ends
            int postGameChoice = ui.promptPostGame();

            switch (postGameChoice) {
                case 1 -> {
                    //since we are replaying, we just rerun the loop
                }
                case 2 -> {
                    //new game, so we have to clear the memory
                    lastPlayer = null;
                    lastLevel = null;
                }
                case 3 -> { 
                    //exiting the loop since game is over
                    keepPlaying = false;
                    System.out.println("\n  Thanks for playing. See you in the arena!\n");
                }
            }
        }
    }

    private static Player resetPlayer(Player template) { //helper function to reset the player's stats and everything
        if (template instanceof Warrior) {
            return new Warrior();
        } else if (template instanceof Wizard) {
            return new Wizard();
        }
        return new Warrior(); // shouldn't reach this, but just in case
    }
}
