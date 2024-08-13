package org.sobadfish.bedwar.item.team;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author SoBadFish
 * 2022/1/6
 */
@Data
public class TeamEffectInfo {

    private final BaseTeamEffect effect;

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




}
