//this is the implementation of the defend effect when defend option is used by player
public class DefendEffect implements StatusEffect {

    private int turnsRemaining = 2;
    private static final int DEFENSE_BONUS = 10; //the 10 here is set as a variable, in case we ever want to change it 

    @Override
    public void apply(Combatant target) {
        //since we apply the defence stat boost directly in defend class, we don't need to do anything here other than as implementation
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
        // when over, we will remove the defence bonus and print an output, one of the rare outputs not handled by the GameUI class
        target.setDefense(target.getDefense() - DEFENSE_BONUS);
        System.out.println(target.getName() + "'s defense boost has worn off.");
    }

    public static int getDefenseBonus() {
        return DEFENSE_BONUS;
    }

    @Override
    public String getName() {
        return "Defending (+" + DEFENSE_BONUS + " DEF)";
    }
}
