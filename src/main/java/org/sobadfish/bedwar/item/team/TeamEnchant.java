package org.sobadfish.bedwar.item.team;

import cn.nukkit.item.enchantment.Enchantment;

/**
 * @author SoBadFish
 * 2022/1/6
 */
public class TeamEnchant  extends BaseTeamEffect{

    private Enchantment enchantment;

    public TeamEnchant(Enchantment enchantment,int maxLevel) {
        super(maxLevel);
        this.enchantment = enchantment;
    }

    public Enchantment getEnchantment() {
        return enchantment;
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
        try {
            return (TeamEnchant) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
