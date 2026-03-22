import java.util.List;

//this is the interface for all actions occuring, and i have to incorporate targets as a list due to wizard's arcane blast hitting everybody and not just one target. that being said i implemented the arcane blast differently so target here is quite redundant.
public interface Action { 

    void execute(Combatant attacker, List<Combatant> targets, BattleManager battle);//attacker is the action-performer, target is a list of targetted individuals, and the battle is referencing the battle manager

    String getName(); //need a display name for the action in the ui
}
