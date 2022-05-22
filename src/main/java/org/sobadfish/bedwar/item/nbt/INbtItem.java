package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.item.Item;

/**
 * @author SoBadFish
 * 2022/1/5
 */
public interface INbtItem {

    /**
     * 获取物品名称
     * @return  名称
     * */
    public String getName();
    /**
     * 玩家点击时触发
     * @param item 使用的哪个物品触发
     * @param player 玩家
     * */
    public boolean onClick(Item item,Player player);
}
