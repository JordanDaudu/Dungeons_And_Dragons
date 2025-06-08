package game.decorator;

public interface Ability {

    boolean useAbility();
    String getAbilityName();
    String getAbilityInfo();
    int abilityTimeInMilliseconds();
}
