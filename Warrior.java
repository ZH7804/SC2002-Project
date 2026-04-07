import java.util.List;


// the warrior's special skill is shield bash which stuns them for this turn and next turn

public class Warrior extends Player { //implements a playable character under player

    public Warrior() {
        super("Warrior", 260, 40, 20, 30);
    }

    @Override //this is for the special skill for warrior
    public void executeSpecialSkillEffect(BattleManager battle) {
        List<Combatant> aliveEnemies = battle.getAliveEnemies();

        if (aliveEnemies.isEmpty()) {
            System.out.println("No enemies left to bash!");
            return;
        }

        // Let the player pick a target
        Combatant target = battle.getGameUI().pickTarget(aliveEnemies);

        // Deal basic attack damage, since for a special skill it still does basic damage
        int damage = Math.max(0, this.getAttack() - target.getDefense());
        target.takeDamage(damage);
        System.out.println(this.getName() + " Shield Bashes " + target.getName()
                + " for " + damage + " damage!");

        // Then stun them, adding the effect to the target
        target.addStatusEffect(new StunEffect());
        System.out.println(target.getName() + " is stunned and can't act!");

        if (!target.isAlive()) {
            System.out.println(target.getName() + " was defeated by the Shield Bash!");
        }
    }

    @Override //provides the skill descrption
    public String getSkillDescription() {
        return "Shield Bash - attack one enemy and stun them for 2 turns";
    }

    @Override //gives it what it actually does during turn, which includes prompting the playeraction from GameUI
    public void takeTurn(BattleManager battle) {
        Action chosenAction = battle.getGameUI().promptPlayerAction(this, battle);
        chosenAction.execute(this, battle.getAliveEnemies(), battle);
        if (!this.shouldSkipCooldownTick()) {
            this.tickCooldown();
        }
        this.setSkipCooldownTick(false);
    }
}
