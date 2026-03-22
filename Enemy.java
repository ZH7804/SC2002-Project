import java.util.List;

//still another abstract class, but this categorises the enemies, which is a lot simpler since they only basic attack

public abstract class Enemy extends Combatant {

    public Enemy(String name, int maxHp, int attack, int defense, int speed) {
        super(name, maxHp, attack, defense, speed); //uses combatant constructor
    }

    // since enemies have to automake decisions, here is a simple decision tree. if it is stunned then it cant do anything, but otherwise, it will target the player with a basic attack
    @Override //this implements the taketurn from combatant
    public void takeTurn(BattleManager battle) {
        if (this.hasEffect(StunEffect.class)) {
            System.out.println(this.getName() + " is stunned and loses their turn!");
            return;
        }

        Combatant target = battle.getPlayer();
        BasicAttack attack = new BasicAttack(target);
        attack.execute(this, List.of(target), battle); //the list.of target will just be the player, we still have to get a list since the execute method requires a list
    }
}
