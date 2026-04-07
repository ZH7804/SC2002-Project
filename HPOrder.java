import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HPOrder implements TurnOrderStrategy {
    @Override
    public List<Combatant> getTurnOrder(List<Combatant> everyone) {
        List<Combatant> ordered = new ArrayList<>(everyone);
        // Sort by current HP ascending (lowest HP goes first)
        ordered.sort(Comparator.comparingInt(Combatant::getCurrentHp));
        return ordered;
    }
}