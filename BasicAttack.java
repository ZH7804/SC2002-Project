import java.util.List;

//this is an implementation of an action for basic attack, which works for all other than those with a smoke bomb atm
public class BasicAttack implements Action {

    private Combatant chosenTarget;

    public BasicAttack(Combatant target) {
        this.chosenTarget = target; //yeah once again i realise i actually don't use the target list in execute, but i shall leave it in
    }

    @Override //implements execute, which works by attack-defence, with a minimum dmg of 0 (otherwise we would hael)
    public void execute(Combatant attacker, List<Combatant> targets, BattleManager battle) {
        // Check if the target is protected by a Smoke Bomb, if there's a smoke bomb then it does nothing
        if (chosenTarget.hasEffect(SmokeBombEffect.class)) {
            System.out.println(attacker.getName() + " attacks " + chosenTarget.getName()
                    + " but the smoke obscures them — 0 damage!");
            return;
        }

        int rawDamage = attacker.getAttack() - chosenTarget.getDefense();
        int actualDamage = Math.max(0, rawDamage); // can't deal negative damage

        chosenTarget.takeDamage(actualDamage);

        System.out.println(attacker.getName() + " attacks " + chosenTarget.getName()
                + " for " + actualDamage + " damage!"
                + " (" + chosenTarget.getName() + " HP: " + chosenTarget.getCurrentHp() + ")");
    }

    @Override
    public String getName() { return "Basic Attack"; } //this is the name for the action that will be displayed in the UI
}
