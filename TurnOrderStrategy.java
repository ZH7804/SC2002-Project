import java.util.List;

//this is the interface for the strategy that allows for the speedbasedorder to be interchangeable, as long as it takes in a list and orders it. 
public interface TurnOrderStrategy {
    List<Combatant> getTurnOrder(List<Combatant> everyone);
}
