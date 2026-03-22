//this adds the +10 atk from arcaneblast for every enemy killed by it
public class ArcaneBlastBuff implements StatusEffect {

    private int attackBonus;
    private boolean active = true;

    public ArcaneBlastBuff(int bonusPerKill) {
        this.attackBonus = bonusPerKill;
    }

    @Override
    public void apply(Combatant target) {//the application is done within the wizard class itself
    }

    @Override
    public void tick() {
        // This effect does not tick down, and will stay for the entire level, which will be removed at battlemanager
    }

    @Override
    public boolean isExpired() {
        // Only expires when we manually deactivate it
        return !active;
    }

    @Override
    public void onRemove(Combatant target) {
        // Take back all the attack we added from kills, which is why we have to store attackBonus
        target.setAttack(target.getAttack() - attackBonus);
        System.out.println(target.getName() + "'s Arcane Blast power fades.");
    }

    public void expire() {
        active = false;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    @Override
    public String getName() {
        return "Arcane Empowered (+" + attackBonus + " ATK)";
    }
}
