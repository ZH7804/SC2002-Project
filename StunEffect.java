//this is from warrior shield bash, and stuns for two turns
public class StunEffect implements StatusEffect {

    private int turnsRemaining = 2;
    private boolean isStunned = true; //this is what is checked before the battlemanager allows the person to move

    @Override
    public void apply(Combatant target) { //this is to fulfill the implementation of statuseffect, does not add anything new
        isStunned = true;
    }

    @Override
    public void tick() { //counts down the duration, called at end of round
        turnsRemaining--;
    }

    @Override
    public boolean isExpired() {
        return turnsRemaining <= 0;
    }

    @Override
    public void onRemove(Combatant target) {
        // Nothing to undo — stun doesn't permanently change any stats
        isStunned = false;
    }

    public boolean isStunActive() {
        return isStunned;
    }

    @Override
    public String getName() {
        return "Stunned"; //for ui purposes
    }
}
