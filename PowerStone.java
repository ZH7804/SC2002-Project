//powerstone is essentially a free use of the player character's special skill so we shall execute it within here rather than the player class to avoid the cooldown
public class PowerStone implements Item {

    @Override
    public void use(Combatant user, BattleManager battle) {
        if (user instanceof Player) {
            System.out.println(user.getName() + " activates a Power Stone - free skill use!");
            ((Player) user).setSkipCooldownTick(true); //prevents special skill tickdown for this turn
            ((Player) user).executeSpecialSkillEffect(battle);
        } else {
            System.out.println("This item can only be used by players.");
        }
    }

    @Override
    public String getName() { return "Power Stone"; }

    @Override
    public String getDescription() { return "Use your special skill for free (no cooldown change)"; }
}
