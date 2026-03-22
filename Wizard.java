import java.util.List;

// the wizards special skill is arcane blast, which hits everyone in the round and adds 10 atk for everyone killed (which is why we need to reset stats when restarting game)
public class Wizard extends Player {

    // Tracks total bonus attack stacked from Arcane Blast kills this level
    private int arcaneAttackBonus = 0;

    public Wizard() {
        super("Wizard", 200, 50, 10, 20);
    }

    @Override // for arcande blast
    public void executeSpecialSkillEffect(BattleManager battle) {
        List<Combatant> aliveEnemies = battle.getAliveEnemies();

        if (aliveEnemies.isEmpty()) {
            System.out.println("No enemies to blast!");
            return;
        }

        System.out.println(this.getName() + " unleashes Arcane Blast on all enemies!");

        int killCount = 0;

        // Hit every enemy with the same damage roll
        for (Combatant enemy : aliveEnemies) {
            int damage = Math.max(0, this.getAttack() - enemy.getDefense());
            enemy.takeDamage(damage);
            System.out.println("  -> " + enemy.getName() + " takes " + damage
                    + " damage! (HP: " + enemy.getCurrentHp() + ")");

            if (!enemy.isAlive()) {
                System.out.println("     " + enemy.getName() + " was obliterated!");
                killCount++;
            }
        }

        // Stack +10 attack for every kill from this blast
        if (killCount > 0) {
            int bonusGained = killCount * 10;
            arcaneAttackBonus += bonusGained;
            this.setAttack(this.getAttack() + bonusGained);
            System.out.println(this.getName() + " gains +" + bonusGained
                    + " ATK from Arcane Blast kills! (Total bonus: +" + arcaneAttackBonus + ")");
        }
    }

    //helper that resets the atk boost at new round
    public void resetArcaneBonus() {
        this.setAttack(this.getAttack() - arcaneAttackBonus);
        arcaneAttackBonus = 0;
    }

    @Override //adds desciption of arcane
    public String getSkillDescription() {
        return "Arcane Blast — hit all enemies; +10 ATK per kill (until end of level)";
    }

    @Override
    public void takeTurn(BattleManager battle) {
        Action chosenAction = battle.getGameUI().promptPlayerAction(this, battle);
        chosenAction.execute(this, battle.getAliveEnemies(), battle);
        this.tickCooldown();
    }
}
