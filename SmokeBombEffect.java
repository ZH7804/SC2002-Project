//this is an implementation of the statuseffect from smokebomb, the implementation is relatively similar to the rest
public class SmokeBombEffect implements StatusEffect {

    private int turnsRemaining = 2;

    @Override
    public void apply(Combatant target) {
        // Like stun, the actual logic lives in BattleManager, so this is empty just for the implementation
    }

    @Override
    public void tick() {
        turnsRemaining--;
    }

    @Override
    public boolean isExpired() {
        return turnsRemaining <= 0;
    }

    @Override
    public void onRemove(Combatant target) {
        System.out.println("The smoke around " + target.getName() + " clears."); //after it has been removed, it will automatically not have an effect as the effect only comes when we check at the start of the round
    }

    @Override
    public String getName() {
        return "Smoke Cover";
    }
}
