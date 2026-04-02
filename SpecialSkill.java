import java.util.List;

//this implements action, but of course the exact special skill is within the warrior and wizard classes
public class SpecialSkill implements Action {

    @Override
    public void execute(Combatant attacker, List<Combatant> targets, BattleManager battle) {

        Player player = (Player) attacker;

        // Fire the skill and start the cooldown
        player.executeSpecialSkillEffect(battle);
        player.startSkillCooldown();
    }

    @Override
    public String getName() { return "Special Skill"; }
}
