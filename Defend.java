import java.util.List;

//the assignment doesn't mention defence can't be stacked so i shall allow it, it basically increases the defence by 10 whenever used for the round
public class Defend implements Action {

    @Override
    public void execute(Combatant attacker, List<Combatant> targets, BattleManager battle) {
        // Apply the defense bonus right now, as this will be used within the fight itself, this class itself will handle the statuseffect, while the removal will be done by the effect itself
        attacker.setDefense(attacker.getDefense() + DefendEffect.getDefenseBonus());

        // Attach the effect so it knows when to remove the bonus - i will include the 2 turn duration within the effect class itself
        attacker.addStatusEffect(new DefendEffect());

        System.out.println(attacker.getName() + " takes a defensive stance! +"
                + DefendEffect.getDefenseBonus() + " DEF for 2 turns.");
    }

    @Override
    public String getName() { return "Defend"; } //the name of the action in the ui
}
