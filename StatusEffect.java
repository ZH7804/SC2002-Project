//this is the interface for all status effects, which will require the application, countdown, expiry and the label presented when removed as well as the label shown in the ui
public interface StatusEffect {

    void apply(Combatant target); //called at start of turn
    void tick(); //counts the duration down by one at end of turn when called - duration is stored in each implementation as an interface can't hold instance var
    boolean isExpired(); //when duration left is zero, this is true, helps us find it easily for removal
    void onRemove(Combatant target); //called before the effect is removed, for example to remove the defence effect 
    String getName(); //label for ui purposes
}
