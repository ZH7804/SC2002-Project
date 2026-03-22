import java.util.ArrayList;
import java.util.List;

//The base abstract class for all characters created, it mainly holds the stats every combatant has and handles the common mechanics
public abstract class Combatant {

    protected String name;
    protected int maxHp;
    protected int currentHp;
    protected int attack;
    protected int defense;
    protected int speed;

    // List of status effects currently on this combatant (stun, defend boost, etc.)
    protected List<StatusEffect> activeEffects;

    public Combatant(String name, int maxHp, int attack, int defense, int speed) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.activeEffects = new ArrayList<>();
    }

    public void processStatusEffects() { //called at the start of the turn to apply all active effects and remove those that have run out
        List<StatusEffect> wornOff = new ArrayList<>();

        for (StatusEffect effect : activeEffects) {
            effect.apply(this);
            effect.tick();
            if (effect.isExpired()) {
                effect.onRemove(this);
                wornOff.add(effect);
            }
        }

        activeEffects.removeAll(wornOff);
    }

    public void addStatusEffect(StatusEffect effect) { //helper to add a new status effect
        activeEffects.add(effect);
    }

    public boolean hasEffect(Class<? extends StatusEffect> effectType) { //helper to check whether a particular and specific effect is current applied
        for (StatusEffect e : activeEffects) {
            if (effectType.isInstance(e)) return true;
        }
        return false;
    }

    public void removeEffect(Class<? extends StatusEffect> effectType) {//helper to remove and cleanup effects of a certain type, used at end of round
        List<StatusEffect> toRemove = new ArrayList<>();
        for (StatusEffect e : activeEffects) {
            if (effectType.isInstance(e)) {
                e.onRemove(this);
                toRemove.add(e);
            }
        }
        activeEffects.removeAll(toRemove);
    }

    public void takeDamage(int amount) { //deals damage and includes it within HP, can't get below 0 as a general rule of thumb
        currentHp = Math.max(0, currentHp - amount);
    }

    public void heal(int amount) { //inverse of takeDamage as it heals, but not more than max HP
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    public boolean isAlive() { //helper to check if this char is dead
        return currentHp > 0;
    }

    public abstract void takeTurn(BattleManager battle); //so since this is an abstract class, each implementation will implement this and does its own thing when its their turn

    // --- Getters ---
    public String getName()   { return name; }
    public int getCurrentHp() { return currentHp; }
    public int getMaxHp()     { return maxHp; }
    public int getAttack()    { return attack; }
    public int getDefense()   { return defense; }
    public int getSpeed()     { return speed; }
    public List<StatusEffect> getActiveEffects() { return activeEffects; }

    // --- Setters (needed so effects can temporarily modify stats) ---
    public void setAttack(int newAttack)   { this.attack = newAttack; }
    public void setDefense(int newDefense) { this.defense = newDefense; }

    @Override
    public String toString() { //this is a shortcut, as when I directly print(combatant), Java will now return this format at the end of the string, which is used often in GameUI
        return String.format("%s [HP: %d/%d | ATK: %d | DEF: %d | SPD: %d]",
                name, currentHp, maxHp, attack, defense, speed);
    }
}
