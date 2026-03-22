//it implements Item, and adds 100 HP, with a max of the character's max HP
public class Potion implements Item {

    private static final int HEAL_AMOUNT = 100;

    @Override
    public void use(Combatant user, BattleManager battle) {
        int healedFor = Math.min(HEAL_AMOUNT, user.getMaxHp() - user.getCurrentHp());
        user.heal(HEAL_AMOUNT);
        System.out.println(user.getName() + " drinks a Potion and recovers " + healedFor + " HP!");
    }

    @Override
    public String getName() { return "Potion"; }

    @Override
    public String getDescription() { return "Heal 100 HP (capped at max HP)"; }
}
