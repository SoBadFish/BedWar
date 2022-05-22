package org.sobadfish.bedwar.item.team;

import lombok.Data;

/**
 * @author SoBadFish
 * 2022/1/6
 */

public class TeamEffectInfo {

    private BaseTeamEffect effect;

    private int level = 1;
    public TeamEffectInfo(BaseTeamEffect effect){
        this.effect = effect;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TeamEffectInfo){
            return ((TeamEffectInfo) obj).effect.equals(effect);
        }
        return false;
    }



    public BaseTeamEffect getEffect() {
        return effect;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
