package game.decorator;

public interface Ability {

    boolean useAbility();
    boolean isUsable();
//    String getAbilityName();
//    String getAbilityInfo();
    int abilityTimeInMilliseconds();
}
