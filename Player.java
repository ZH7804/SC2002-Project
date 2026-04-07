import java.util.ArrayList;
import java.util.List;

//still another abstract class, but this categorises the combatants that players can play
public abstract class Player extends Combatant {

    private List<Item> inventory; //we have to have an inventory

    private int skillCooldown = 0; //we need a skill cooldown since we are using special skills for player character

    private static final int SKILL_COOLDOWN_TURNS = 3;

    public Player(String name, int maxHp, int attack, int defense, int speed) {
        super(name, maxHp, attack, defense, speed);
        this.inventory = new ArrayList<>();
    }

    //here are methods to retrieve and plant items in this character's inventory

    public void addItem(Item item) {
        inventory.add(item);
    }

    public void removeItem(Item item) {
        inventory.remove(item);
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public boolean hasItems() {
        return !inventory.isEmpty();
    }

    //here are methods to handle skill cooldowns and check whether it is ready for use, as well as lock it after use for a cooldown and tick it down 

    public boolean isSkillReady() {
        return skillCooldown == 0;
    }

    public int getSkillCooldown() {
        return skillCooldown;
    }

    public void startSkillCooldown() {
        skillCooldown = SKILL_COOLDOWN_TURNS;
    }

    public void tickCooldown() {
        if (skillCooldown > 0) {
            skillCooldown--;
        }
    }

    private boolean skipCooldownTick = false;

    public void setSkipCooldownTick(boolean skip) { //for powerstone shenanigans, since we are not ticking down cooldown during powerstone
        this.skipCooldownTick = skip;
    }

    public boolean shouldSkipCooldownTick() {
        return skipCooldownTick;
    }

    public abstract void executeSpecialSkillEffect(BattleManager battle); //this executes the interface for the special skill, which depends on exactly what the special skill does

    public abstract String getSkillDescription(); //each special skill will require a description
}
