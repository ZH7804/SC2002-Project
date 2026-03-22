import java.util.List;

//this is another implementation of the action interface, but the exact way the item is used will be written in the item classes
public class UseItem implements Action {

    private Item itemToUse;
    private Player owner;

    public UseItem(Item item, Player player) {
        this.itemToUse = item;
        this.owner = player;
    }

    @Override
    public void execute(Combatant attacker, List<Combatant> targets, BattleManager battle) {
        itemToUse.use(attacker, battle);
        owner.removeItem(itemToUse); // single-use items are what i will interpret them to be in this game
    }

    @Override
    public String getName() { return "Use " + itemToUse.getName(); }
}
