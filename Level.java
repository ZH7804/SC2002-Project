import java.util.ArrayList;
import java.util.List;

//this just contains the information for what each level will spawn and its backups 
// 1 - Easy:   3 Goblins (no backup) 
// 2 - Medium: 1 Goblin + 1 Wolf; backup: 2 Wolves 
// 3 - Hard:   2 Goblins; backup: 1 Goblin + 2 Wolves

public class Level {

    private int levelNumber;
    private String difficultyName;

    // Whether the backup wave has already been deployed, which we use in battlemanager to determine whether the round is over
    private boolean backupAlreadySpawned = false;

    public Level(int levelNumber) {
        this.levelNumber = levelNumber;
        this.difficultyName = switch (levelNumber) {
            case 1 -> "Easy";
            case 2 -> "Medium";
            case 3 -> "Hard";
            default -> "Unknown";
        };
    }

    //sends out intiial enemies
    public List<Enemy> spawnInitialEnemies() {
        List<Enemy> wave = new ArrayList<>();

        switch (levelNumber) {
            case 1 -> {
                wave.add(new Goblin());
                wave.add(new Goblin());
                wave.add(new Goblin());
            }
            case 2 -> {
                wave.add(new Goblin());
                wave.add(new Wolf());
            }
            case 3 -> {
                wave.add(new Goblin());
                wave.add(new Goblin());
            }
        }

        return wave;
    }

    //sends out backups if there is leftover backups, and an empty list if there isn't (which is validated in battlemanager)
    public List<Enemy> spawnBackupEnemies() {
        if (backupAlreadySpawned || !hasBackupWave()) {
            return new ArrayList<>();
        }

        backupAlreadySpawned = true;
        List<Enemy> backup = new ArrayList<>();

        switch (levelNumber) {
            case 2 -> {
                backup.add(new Wolf());
                backup.add(new Wolf());
            }
            case 3 -> {
                backup.add(new Goblin());
                backup.add(new Wolf());
                backup.add(new Wolf());
            }
        }

        return backup;
    }

    public boolean hasBackupWave() { //level 1 does not have
        return levelNumber == 2 || levelNumber == 3;
    }

    public boolean isBackupSpawned() {
        return backupAlreadySpawned;
    }

    public int getLevelNumber() { return levelNumber; }
    public String getDifficultyName() { return difficultyName; }

    @Override
    public String toString() {
        return "Level " + levelNumber + " (" + difficultyName + ")";
    }
}
