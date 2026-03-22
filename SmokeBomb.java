// Implements smoke bomb, which doesn't allow enemies to hit you with their basic attack for 2 turns
public class SmokeBomb implements Item {

    @Override
    public void use(Combatant user, BattleManager battle) {
        user.addStatusEffect(new SmokeBombEffect());
        System.out.println(user.getName() + " throws a Smoke Bomb! Enemy attacks will miss for 2 turns.");
    }

    @Override
    public String getName() { return "Smoke Bomb"; }

    @Override
    public String getDescription() { return "Enemy attacks deal 0 damage this turn and next"; }
}
