package org.sobadfish.bedwar.item.team;

import cn.nukkit.item.enchantment.Enchantment;
import lombok.Data;
import lombok.Getter;

/**
 * @author SoBadFish
 * 2022/1/6
 */

@Getter
public class TeamEnchant  extends BaseTeamEffect{

    private final Enchantment enchantment;

    public TeamEnchant(Enchantment enchantment,int maxLevel) {
        super(maxLevel);
        this.enchantment = enchantment;
    }



    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TeamEnchant){
            return ((TeamEnchant) obj).enchantment.getId() == enchantment.getId();
        }
        return false;
    }

    @Override
    public TeamEnchant clone() {
        return (TeamEnchant) super.clone();
    }
}
