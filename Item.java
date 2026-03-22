//this is the interface for an item, it just contains use, it's name (for the ui) and a short description (also for the ui)
public interface Item {
    void use(Combatant user, BattleManager battle);
    String getName();
    String getDescription();
}
