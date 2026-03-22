import java.util.List;

//this implements action, but of course the exact special skill is within the warrior and wizard classes
public class SpecialSkill implements Action {

    @Override
    public void execute(Combatant attacker, List<Combatant> targets, BattleManager battle) {

        Player player = (Player) attacker;

        if (!player.isSkillReady()) {
            System.out.println(player.getName() + "'s skill is on cooldown! (" //this checks for cooldown and whether it is available
                    + player.getSkillCooldown() + " turns left)");
            return;
        }

        // Fire the skill and start the cooldown
        player.executeSpecialSkillEffect(battle);
        player.startSkillCooldown();
    }

    @Override
    public String getName() { return "Special Skill"; }
}
