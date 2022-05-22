package org.sobadfish.bedwar.item.team;

/**
 * @author SoBadFish
 * 2022/1/12
 */
public class TeamTrap extends BaseTeamEffect{


    public TeamTrap(int maxLevel) {
        super(maxLevel);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TeamTrap;
    }
}
