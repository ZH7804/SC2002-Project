import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//this is the proper speedbasedorder, where we sort by speed.
public class SpeedBasedOrder implements TurnOrderStrategy {

    @Override
    public List<Combatant> getTurnOrder(List<Combatant> everyone) {
        // Copy the list so we don't shuffle the original
        List<Combatant> ordered = new ArrayList<>(everyone);

        // Sort by speed descending, such that highest speed goes first
        ordered.sort(Comparator.comparingInt(Combatant::getSpeed).reversed());

        return ordered;
    }
}
