package org.sobadfish.bedwar.item.team;

import cn.nukkit.potion.Effect;
import lombok.Getter;
import lombok.Setter;

/**
 * @author SoBadFish
 * 2022/1/6
 */
@Setter
@Getter
public class TeamEffect extends BaseTeamEffect
{

    private Effect effect;

    public TeamEffect(Effect effect,int maxLevel) {
        super(maxLevel);
        this.effect = effect;
    }



    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TeamEffect){
            return ((TeamEffect) obj).getEffect().getId() == effect.getId();
        }
        return false;
    }

    @Override
    public TeamEffect clone() {
        return (TeamEffect) super.clone();
    }
}
