package org.sobadfish.bedwar.item.team;

import cn.nukkit.potion.Effect;

/**
 * @author SoBadFish
 * 2022/1/6
 */
public class TeamEffect extends BaseTeamEffect
{

    private Effect effect;

    public TeamEffect(Effect effect,int maxLevel) {
        super(maxLevel);
        this.effect = effect;
    }

    public Effect getEffect() {
        return effect;
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
        try {
            return (TeamEffect) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
